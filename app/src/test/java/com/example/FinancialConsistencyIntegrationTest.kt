package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.TrajectoryPoint
import com.example.data.model.FireStatus
import com.example.ui.MainViewModel
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FinancialConsistencyIntegrationTest {
    private val today = LocalDate.of(2026, 10, 31)

    private val scenarioIds = listOf(
        "pos_salary_increase",
        "pos_market_growth",
        "pos_expense_optimization",
        "pos_relocation",
        "pos_inheritance",
        "pos_property_sale",
        "pos_children_self_sufficient",
        "job_loss",
        "market_crash",
        "mortgage",
        "car_loan",
        "large_expense",
        "inflation",
        "child_dependent"
    )

    @Test
    fun `baseline and each scenario are finite and trajectory ends at life expectancy`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app, today)

        assertEquals(FinancialInputData.usAverageData(), viewModel.inputData.value)
        assertEquals(viewModel.metrics.value, viewModel.simulatedMetrics.value)
        assertFiniteAndCoherent(viewModel.metrics.value, viewModel.trajectory.value)
        assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value)

        scenarioIds.forEach { scenarioId ->
            viewModel.resetSimulation()
            viewModel.toggleScenario(scenarioId)
            assertEquals("scenario $scenarioId is enabled", 1, viewModel.fireSimulation.value.scenarios.activeCount)
            assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value)
        }

        viewModel.resetSimulation()
        scenarioIds.forEach(viewModel::toggleScenario)
        assertEquals(14, viewModel.fireSimulation.value.scenarios.activeCount)
        assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value)

        viewModel.resetSimulation()
        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 0.0, expReduction = 0.0)
        assertFalse(viewModel.fireSimulation.value.isSimulating)
        assertEquals(viewModel.metrics.value, viewModel.simulatedMetrics.value)
    }

    @Test
    fun `zero expenses negative cashflow exhausted horizon and invalid date stay finite`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app, today)

        val cases = listOf(
            "zero expenses" to FinancialInputData.usAverageData().copy(monthlyExpenses = 0.0),
            "negative cashflow" to FinancialInputData.usAverageData().copy(monthlyIncome = 1_000.0, monthlyExpenses = 2_000.0),
            "life expectancy at age" to FinancialInputData.usAverageData().copy(birthDate = "1988-06-15", lifeExpectancy = 38),
            "invalid date" to FinancialInputData.usAverageData().copy(birthDate = "not-a-date")
        )

        cases.forEach { (label, input) ->
            viewModel.resetSimulation()
            viewModel.updateInputData { input }
            assertFiniteAndCoherent(viewModel.metrics.value, viewModel.trajectory.value, label)
            assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value, label)
            assertEquals("$label baseline matches no-op simulation", viewModel.metrics.value, viewModel.simulatedMetrics.value)
        }
        assertTrue(viewModel.trajectory.value.isEmpty()) // invalid birth date

        viewModel.updateInputData {
            FinancialInputData.usAverageData().copy(birthDate = "1988-06-15", lifeExpectancy = 38)
        }
        assertTrue(viewModel.trajectory.value.isEmpty()) // no remaining horizon at age 38 on fixed today
    }

    private fun assertFiniteAndCoherent(
        metrics: CalculatedMetrics,
        trajectory: List<TrajectoryPoint>,
        label: String = "case"
    ) {
        val values = listOf(
            metrics.cashFlow,
            metrics.savingsRate,
            metrics.totalAssets,
            metrics.emergencyFund,
            metrics.safetyMonths,
            metrics.debtRatio,
            metrics.netWorth,
            metrics.annualIncome,
            metrics.annualExpenses,
            metrics.fidelityMultiplier,
            metrics.fidelityTarget,
            metrics.financialFreedomRatio,
            metrics.realReturnPercent,
            metrics.yearsToRetirement,
            metrics.retirementAge,
            metrics.retirementTargetCapital,
            metrics.capitalGainsTaxPercent,
            metrics.effectiveCapitalGainsTaxRate,
            metrics.capitalGainsGainSharePercent,
            metrics.scores.totalScore,
            metrics.fidelityRatio,
            metrics.fidelityPercent
        )
        assertTrue("$label CalculatedMetrics contains NaN/Infinity", values.all { it.isFinite() })
        assertTrue(
            "$label TrajectoryPoint contains NaN/Infinity",
            trajectory.all {
                it.age.isFinite() && it.yearOffset.isFinite() &&
                    it.accumulatedCapital.isFinite() && it.requiredCapital.isFinite()
            }
        )

        if (metrics.isAgeValid && metrics.lifeExpectancy > metrics.currentAge) {
            assertFalse("$label trajectory should not be empty", trajectory.isEmpty())
            assertTrue("$label ages should be monotonic", trajectory.zipWithNext().all { (a, b) -> b.age >= a.age })
            assertTrue("$label offsets should be monotonic", trajectory.zipWithNext().all { (a, b) -> b.yearOffset >= a.yearOffset })
            assertEquals(metrics.lifeExpectancy.toDouble(), trajectory.last().age, 0.0)
            if (metrics.fireStatus == FireStatus.REACHABLE || metrics.fireStatus == FireStatus.REACHED_NOW) {
                val exitPoint = trajectory.firstOrNull { it.isRetired }
                assertTrue("$label should expose a trajectory exit point", exitPoint != null)
                assertTrue(
                    "$label accumulated capital should cover required capital at exit",
                    exitPoint!!.accumulatedCapital >= exitPoint.requiredCapital
                )
            }
        } else {
            assertTrue("$label has no valid projection horizon", trajectory.isEmpty())
        }
    }
}
