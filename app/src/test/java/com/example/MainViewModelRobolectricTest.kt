package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.LocalPreferencesManager
import com.example.ui.MainViewModel
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import com.example.domain.SimulatorScaleCalculator
import com.example.domain.calculateSimulationOngoingExpenses
import com.example.ui.FireSimulationState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MainViewModelRobolectricTest {
    private val dispatcher = StandardTestDispatcher()
    private val today = LocalDate.of(2026, 10, 31)
    private lateinit var app: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `FIRE simulation return adjustment starts from displayed projection rate`() {
        val viewModel = MainViewModel(app) { today }
        viewModel.updateInputData { it.copy(nominalReturnPercent = -50.0, inflationPercent = 100.0) }
        assertEquals(-50.0, viewModel.metrics.value.realReturnPercent, 0.000001)
        assertEquals(viewModel.metrics.value, viewModel.simulatedMetrics.value)

        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 2.0, expReduction = 0.0)

        assertEquals(-48.0, viewModel.simulatedMetrics.value.realReturnPercent, 0.000001)
        viewModel.flushPendingSave()
    }

    @Test
    fun `simulation expenses combine shared ongoing costs with temporary payments`() {
        val viewModel = MainViewModel(app) { today }
        viewModel.updateInputData { it.copy(monthlyIncome = 100_000.0, monthlyExpenses = 60_000.0) }
        listOf("pos_salary_increase", "pos_expense_optimization", "pos_relocation", "inflation")
            .forEach(viewModel::toggleScenario)
        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 0.0, expReduction = 10_000.0)
        assertEquals(34_625.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)

        viewModel.toggleScenario("mortgage")
        assertEquals(76_625.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        viewModel.toggleScenario("car_loan")
        assertEquals(100_625.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        viewModel.toggleScenario("child_dependent")
        assertEquals(115_625.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)

        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 0.0, expReduction = 100_000.0)
        assertEquals(87_000.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        viewModel.flushPendingSave()
    }

    @Test
    fun `flush saves latest input immediately and cancels delayed save`() {
        val prefs = LocalPreferencesManager(app)
        val viewModel = MainViewModel(app) { today }
        val original = viewModel.inputData.value
        viewModel.updateInputData { it.copy(monthlyIncome = 12_000.0) }
        viewModel.updateInputData { it.copy(monthlyIncome = 13_000.0, monthlyExpenses = 7_000.0) }
        dispatcher.scheduler.runCurrent()
        assertEquals(original, prefs.loadData())

        viewModel.flushPendingSave()

        val expected = viewModel.inputData.value
        assertEquals(expected, prefs.loadData())
        // A pending save must not overwrite a subsequent write after flush.
        val subsequent = expected.copy(monthlyIncome = 14_000.0)
        prefs.saveData(subsequent)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(subsequent, prefs.loadData())

        viewModel.updateInputData { it.copy(monthlyIncome = 15_000.0) }
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(viewModel.inputData.value, prefs.loadData())
    }

    @Test
    fun `clearing view model saves pending input immediately`() {
        val viewModel = MainViewModel(app) { today }
        val store = ViewModelStore()
        store.put("main", viewModel)
        viewModel.updateInputData { it.copy(monthlyIncome = 12_000.0) }

        store.clear()

        assertEquals(viewModel.inputData.value, LocalPreferencesManager(app).loadData())
    }

    @Test
    fun `recalculation uses updated date for metrics and both trajectories`() {
        var currentDate = today
        val viewModel = MainViewModel(app) { currentDate }
        viewModel.updateInputData { it.copy(birthDate = "1988-11-01") }
        assertEquals(37, viewModel.metrics.value.currentAge)

        currentDate = today.plusDays(1)
        viewModel.updateInputData { it }

        assertEquals(38, viewModel.metrics.value.currentAge)
        assertEquals(38, viewModel.simulatedMetrics.value.currentAge)
        assertEquals(38.0, viewModel.trajectory.value.first().age, 0.0)
        assertEquals(viewModel.trajectory.value, viewModel.simulatedTrajectory.value)
        viewModel.flushPendingSave()
    }

    @Test
    fun `expenseReduction at max scale re-clamps below new max when relocation scenario is toggled`() {
        val viewModel = MainViewModel(app) { today }
        viewModel.updateInputData { it.copy(monthlyIncome = 100_000.0, monthlyExpenses = 60_000.0) }
        val input = viewModel.inputData.value
        val ongoingBefore = calculateSimulationOngoingExpenses(
            input.monthlyExpenses, 0.0, false, false, false
        ).beforeReduction
        val initialScale = SimulatorScaleCalculator.calculateExpensesConfig(
            input.monthlyExpenses, input.monthlyIncome, ongoingBefore
        )
        val maxReduction = initialScale.maxValue.toDouble()

        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 0.0, expReduction = maxReduction)
        assertEquals(maxReduction, viewModel.fireSimulation.value.expenseReduction, 0.001)

        // Включаем переезд (relocationLowerCost30: -30% к расходам)
        viewModel.toggleScenario("pos_relocation")

        val currentSim = viewModel.fireSimulation.value
        val ongoingWithRelocation = calculateSimulationOngoingExpenses(
            input.monthlyExpenses,
            currentSim.expenseReduction,
            currentSim.scenarios.expenseOptimization15,
            currentSim.scenarios.relocationLowerCost30,
            currentSim.scenarios.expensesPlus25
        )
        val newScale = SimulatorScaleCalculator.calculateExpensesConfig(
            input.monthlyExpenses, input.monthlyIncome, ongoingWithRelocation.beforeReduction
        )

        // Значение <= нового максимума
        assertTrue(currentSim.expenseReduction <= newScale.maxValue.toDouble())
        assertEquals(newScale.maxValue.toDouble(), currentSim.expenseReduction, 0.001)

        // Расчет совпадает с ползунком
        assertEquals(ongoingWithRelocation.effective, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.001)
        viewModel.flushPendingSave()
    }

    @Test
    fun `nominal return at 100 and inflation at 2_5 percent ensures all slider positions change metrics`() {
        val viewModel = MainViewModel(app) { today }
        viewModel.updateInputData {
            it.copy(nominalReturnPercent = 100.0, inflationPercent = 2.5)
        }
        val input = viewModel.inputData.value
        val metrics = viewModel.metrics.value
        val returnConfig = SimulatorScaleCalculator.calculateReturnConfig(
            realReturnPercent = metrics.realReturnPercent,
            inflationPercent = input.inflationPercent
        )

        // При nominal = 100% шкала не должна позволять положительные дельты, создающие мёртвую зону
        assertTrue(returnConfig.maxValue <= 0.0f)
        assertTrue(returnConfig.steps > 0)

        var previousRealReturn = Double.NEGATIVE_INFINITY
        for (i in 0..returnConfig.steps + 1) {
            val delta = (returnConfig.minValue + i * returnConfig.stepSize.toFloat()).toDouble()
            viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = delta, expReduction = 0.0)
            val simMetrics = viewModel.simulatedMetrics.value
            // Метрики строго монотонно меняются при каждом положении ползунка
            assertTrue("Step $i ($delta%) must change real return: ${simMetrics.realReturnPercent} > $previousRealReturn",
                simMetrics.realReturnPercent > previousRealReturn)
            previousRealReturn = simMetrics.realReturnPercent
        }
        viewModel.flushPendingSave()
    }

    @Test
    fun `saved simulation state out of scale is bounded on launch`() {
        val prefs = LocalPreferencesManager(app)
        val outOfBoundsSim = FireSimulationState(
            extraMonthlySavings = 100_000_000.0,
            realReturnAdjustment = 500.0,
            expenseReduction = 100_000_000.0
        )
        prefs.saveSimulationState(outOfBoundsSim)

        val restartedViewModel = MainViewModel(app) { today }
        val bounded = restartedViewModel.fireSimulation.value

        val input = restartedViewModel.inputData.value
        val metrics = restartedViewModel.metrics.value
        val savingsConfig = SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = metrics.cashFlow,
            monthlyIncome = input.monthlyIncome,
            monthlyExpenses = input.monthlyExpenses,
            currency = input.currencySymbol
        )
        val expensesConfig = SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = input.monthlyExpenses,
            monthlyIncome = input.monthlyIncome
        )
        val returnConfig = SimulatorScaleCalculator.calculateReturnConfig(
            realReturnPercent = metrics.realReturnPercent,
            inflationPercent = input.inflationPercent
        )

        assertEquals(savingsConfig.maxValue.toDouble(), bounded.extraMonthlySavings, 0.001)
        assertEquals(expensesConfig.maxValue.toDouble(), bounded.expenseReduction, 0.001)
        assertEquals(returnConfig.maxValue.toDouble(), bounded.realReturnAdjustment, 0.001)
        assertEquals(bounded, prefs.loadSimulationState())
    }
}
