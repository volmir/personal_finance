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
import org.junit.After
import org.junit.Assert.assertEquals
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
}
