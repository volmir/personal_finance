package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.LocalPreferencesManager
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.domain.SimulatorScaleCalculator
import com.example.domain.calculateSimulationOngoingExpenses
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.ui.MainViewModel
import com.example.util.Formatters
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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
        // Reduction is bounded to the last scale step: 35,000 instead of the 38,625 model limit.
        assertEquals(90_625.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        viewModel.flushPendingSave()
    }

    @Test
    fun `relocation bounds expense reduction before calculating and saving simulation`() {
        val viewModel = MainViewModel(app) { today }
        viewModel.updateInputData { it.copy(monthlyIncome = 100_000.0, monthlyExpenses = 60_000.0) }
        val input = viewModel.inputData.value
        val originalConfig = SimulatorScaleCalculator.calculateExpensesConfig(input.monthlyExpenses, input.monthlyIncome)
        viewModel.updateSimulation(0.0, 0.0, originalConfig.maxValue.toDouble())

        viewModel.toggleScenario("pos_relocation")

        val sim = viewModel.fireSimulation.value
        val ongoing = calculateSimulationOngoingExpenses(input.monthlyExpenses, sim.expenseReduction, false, true, false)
        val config = SimulatorScaleCalculator.calculateExpensesConfig(input.monthlyExpenses, input.monthlyIncome, ongoing.beforeReduction)
        assertEquals(35_000.0, config.maxValue.toDouble(), 0.0)
        assertEquals(config.maxValue.toDouble(), sim.expenseReduction, 0.0)
        assertEquals(7_000.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        assertEquals(ongoing.beforeReduction - sim.expenseReduction, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        assertEquals(sim, LocalPreferencesManager(app).loadSimulationState())
        assertEquals(input, viewModel.inputData.value)
        assertEquals(FinanceCalculator.calculate(input, today = today), viewModel.metrics.value)
        viewModel.flushPendingSave()
    }

    @Test
    fun `restored simulation is bounded and saved before initial calculation`() {
        val prefs = LocalPreferencesManager(app)
        val input = prefs.loadData().copy(monthlyIncome = 100_000.0, monthlyExpenses = 60_000.0)
        prefs.saveData(input)
        prefs.saveSimulationState(FireSimulationState(
            extraMonthlySavings = 1e12,
            realReturnAdjustment = -1e12,
            expenseReduction = 1e12,
            scenarios = FireScenarioToggles(relocationLowerCost30 = true)
        ))

        val viewModel = MainViewModel(app) { today }

        val sim = viewModel.fireSimulation.value
        val savings = SimulatorScaleCalculator.calculateSavingsConfig(viewModel.metrics.value.cashFlow, input.monthlyIncome, input.monthlyExpenses)
        val expenses = SimulatorScaleCalculator.calculateExpensesConfig(input.monthlyExpenses, input.monthlyIncome, 42_000.0)
        val returns = SimulatorScaleCalculator.calculateReturnConfig(viewModel.metrics.value.realReturnPercent, input.inflationPercent)
        assertEquals(savings.maxValue.toDouble(), sim.extraMonthlySavings, 0.0)
        assertEquals(expenses.maxValue.toDouble(), sim.expenseReduction, 0.0)
        assertEquals(returns.minValue.toDouble(), sim.realReturnAdjustment, 0.0)
        assertTrue(sim.scenarios.relocationLowerCost30)
        assertEquals(sim, prefs.loadSimulationState())
        assertEquals(7_000.0, viewModel.simulatedMetrics.value.annualExpenses / 12.0, 0.000001)
        assertEquals(input, prefs.loadData())
    }

    @Test
    fun `corrupted preferences restore finite metrics and simulation within all scales`() {
        val prefs = app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        val manager = LocalPreferencesManager(app)
        val numericKeys = listOf(
            "monthly_income", "monthly_expenses", "liquid_assets", "financial_assets", "property_assets", "liabilities",
            "nominal_return", "inflation", "capital_gains_tax",
            "sim_extra_savings", "sim_return_adjustment", "sim_expense_reduction"
        )
        val metricFields = CalculatedMetrics::class.java.declaredFields.filter { it.type == Double::class.javaPrimitiveType }
        for (raw in listOf("NaN", "Infinity", "-Infinity", "-5", "abc", (Formatters.MAX_INPUT_VALUE + 1.0).toString())) {
            manager.saveData(FinancialInputData.usAverageData())
            prefs.edit().apply {
                numericKeys.forEach { putString(it, raw) }
                putInt("life_expectancy", 500)
                putBoolean("sc_relocation", true)
            }.commit()

            val viewModel = MainViewModel(app) { today }
            val input = viewModel.inputData.value
            val metrics = viewModel.metrics.value
            val sim = viewModel.fireSimulation.value
            assertEquals(raw, 82, input.lifeExpectancy)
            for (calculated in listOf(metrics, viewModel.simulatedMetrics.value)) {
                for (field in metricFields) {
                    field.isAccessible = true
                    assertTrue("$raw ${field.name}", field.getDouble(calculated).isFinite())
                }
                assertTrue("$raw fidelityRatio", calculated.fidelityRatio.isFinite())
                assertTrue("$raw fidelityPercent", calculated.fidelityPercent.isFinite())
            }

            val savings = SimulatorScaleCalculator.calculateSavingsConfig(metrics.cashFlow, input.monthlyIncome, input.monthlyExpenses, input.currencySymbol)
            val ongoing = calculateSimulationOngoingExpenses(input.monthlyExpenses, sim.expenseReduction, false, true, false)
            val expenses = SimulatorScaleCalculator.calculateExpensesConfig(input.monthlyExpenses, input.monthlyIncome, ongoing.beforeReduction, input.currencySymbol)
            val returns = SimulatorScaleCalculator.calculateReturnConfig(metrics.realReturnPercent, input.inflationPercent)
            assertTrue("$raw savings", sim.extraMonthlySavings in savings.minValue.toDouble()..savings.maxValue.toDouble())
            assertTrue("$raw expenses", sim.expenseReduction in expenses.minValue.toDouble()..expenses.maxValue.toDouble())
            assertTrue("$raw return", sim.realReturnAdjustment in returns.minValue.toDouble()..returns.maxValue.toDouble())
            assertTrue(sim.scenarios.relocationLowerCost30)
            assertEquals(sim, manager.loadSimulationState())
        }
    }

    @Test
    fun `every return slider step changes metrics at maximum nominal return`() {
        val viewModel = MainViewModel(app) { today }
        viewModel.updateInputData { it.copy(nominalReturnPercent = 100.0, inflationPercent = 2.5) }
        val baseMetrics = viewModel.metrics.value
        val input = viewModel.inputData.value
        val config = SimulatorScaleCalculator.calculateReturnConfig(baseMetrics.realReturnPercent, input.inflationPercent)
        assertEquals(0f, config.maxValue, 0f)
        var previous = Double.NEGATIVE_INFINITY

        for (index in 0..config.steps + 1) {
            val delta = config.minValue + index * config.stepSize
            viewModel.updateSimulation(0.0, delta, 0.0)

            val realReturn = viewModel.simulatedMetrics.value.realReturnPercent
            assertEquals(delta, viewModel.fireSimulation.value.realReturnAdjustment, 0.0)
            assertEquals(baseMetrics.realReturnPercent + delta, realReturn, 0.000001)
            assertTrue("step $index must change the projected rate", realReturn > previous)
            previous = realReturn
        }
        assertEquals(baseMetrics, viewModel.metrics.value)
        assertEquals(input, viewModel.inputData.value)
        viewModel.flushPendingSave()
    }

    @Test
    fun `simulation updates and input changes bound all sliders and reset saves baseline`() {
        val viewModel = MainViewModel(app) { today }
        val prefs = LocalPreferencesManager(app)
        viewModel.updateInputData { it.copy(monthlyIncome = 100_000.0, monthlyExpenses = 60_000.0) }
        for (requested in listOf(-1e12, 1e12)) {
            viewModel.updateSimulation(requested, requested, requested)
            val input = viewModel.inputData.value
            val metrics = viewModel.metrics.value
            val savings = SimulatorScaleCalculator.calculateSavingsConfig(metrics.cashFlow, input.monthlyIncome, input.monthlyExpenses)
            val expenses = SimulatorScaleCalculator.calculateExpensesConfig(input.monthlyExpenses, input.monthlyIncome)
            val returns = SimulatorScaleCalculator.calculateReturnConfig(metrics.realReturnPercent, input.inflationPercent)
            val sim = viewModel.fireSimulation.value
            assertEquals(if (requested < 0) savings.minValue.toDouble() else savings.maxValue.toDouble(), sim.extraMonthlySavings, 0.0)
            assertEquals(if (requested < 0) expenses.minValue.toDouble() else expenses.maxValue.toDouble(), sim.expenseReduction, 0.0)
            assertEquals(if (requested < 0) returns.minValue.toDouble() else returns.maxValue.toDouble(), sim.realReturnAdjustment, 0.0)
            assertEquals(sim, prefs.loadSimulationState())
        }

        viewModel.updateInputData {
            it.copy(monthlyIncome = 2_000.0, monthlyExpenses = 1_000.0, nominalReturnPercent = 100.0)
        }

        val bounded = viewModel.fireSimulation.value
        assertEquals(2_000.0, bounded.extraMonthlySavings, 0.0)
        assertEquals(900.0, bounded.expenseReduction, 0.0)
        assertEquals(0.0, bounded.realReturnAdjustment, 0.0)
        assertEquals(bounded, prefs.loadSimulationState())

        viewModel.toggleScenario("pos_relocation")
        viewModel.resetSimulation()

        assertEquals(FireSimulationState(), viewModel.fireSimulation.value)
        assertEquals(FireSimulationState(), prefs.loadSimulationState())
        assertEquals(viewModel.metrics.value, viewModel.simulatedMetrics.value)
        assertEquals(viewModel.trajectory.value, viewModel.simulatedTrajectory.value)
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
