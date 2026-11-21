package com.example

import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.calculateScenarioAmounts
import com.example.domain.calculateSimulationOngoingExpenses
import com.example.domain.model.AuditIndicatorStatus
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialBaselineTest {
    private val today = LocalDate.of(2026, 10, 31)

    @Test
    fun baselineCashFlowAndSavingsRate() {
        val input = FinancialInputData(monthlyIncome = 6_000.0, monthlyExpenses = 4_800.0, currencySymbol = "$")
        val metrics = FinanceCalculator.calculate(input, today = today)
        assertEquals(1_200.0, metrics.cashFlow, 0.01)
        assertEquals(20.0, metrics.savingsRate, 0.01)
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getCashFlowStatus(metrics.cashFlow))
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getSavingsRateStatus(metrics.savingsRate))

        val zeroIncome = FinanceCalculator.calculate(FinancialInputData(monthlyIncome = 0.0, monthlyExpenses = 4_800.0), today = today)
        assertEquals(-4_800.0, zeroIncome.cashFlow, 0.01)
        assertEquals(-100.0, zeroIncome.savingsRate, 0.01)
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getCashFlowStatus(zeroIncome.cashFlow))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getSavingsRateStatus(zeroIncome.savingsRate))
    }

    @Test
    fun baselineCapitalAndRiskMetrics() {
        val input = FinancialInputData(
            monthlyIncome = 6_000.0, monthlyExpenses = 4_800.0, liquidAssets = 15_000.0, financialAssets = 40_000.0,
            propertyAssets = 280_000.0, liabilities = 180_000.0, birthDate = "1988-10-31", lifeExpectancy = 82,
            nominalReturnPercent = 7.5, inflationPercent = 2.5, capitalGainsTaxPercent = 20.0, currencySymbol = "$"
        )
        val metrics = FinanceCalculator.calculate(input, today = today)
        assertEquals(55_000.0, metrics.fidelityCapital, 0.01)
        assertEquals(187_200.0, metrics.fidelityTarget, 0.01)
        assertEquals(155_000.0, metrics.netWorth, 0.01)
        assertEquals(2.6909722222222223, metrics.financialFreedomRatio, 0.000001)
        assertEquals(6.7, metrics.scores.totalScore, 0.001)
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getTotalScoreStatus(metrics.scores.totalScore))
        assertEquals(55_000.0, FinancialMetricEvaluator.fidelityCapital(15_000.0, 40_000.0, 280_000.0, 180_000.0), 0.0)
        assertEquals(35_000.0, FinancialMetricEvaluator.fidelityCapital(15_000.0, 40_000.0, 280_000.0, 300_000.0), 0.0)
    }

    @Test
    fun baselineThresholdStatuses() {
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getCashFlowStatus(0.0))
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getCashFlowStatus(0.01))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getSavingsRateStatus(0.0))
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getSavingsRateStatus(20.0))
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getSavingsRateStatus(20.01))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getTotalScoreStatus(3.4))
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getTotalScoreStatus(3.5))
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getTotalScoreStatus(7.0))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getCfsStatus(1.99))
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getCfsStatus(2.0))
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getCfsStatus(5.0))
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getNetWorthStatus(50.0, 100.0))
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getNetWorthStatus(49.99, 100.0))
    }

    @Test
    fun baselineFireFuture() {
        val input = FinancialInputData(
            monthlyIncome = 6_000.0, monthlyExpenses = 4_800.0, liquidAssets = 15_000.0, financialAssets = 40_000.0,
            propertyAssets = 280_000.0, liabilities = 180_000.0, birthDate = "1988-10-31", lifeExpectancy = 82,
            nominalReturnPercent = 7.5, inflationPercent = 2.5, capitalGainsTaxPercent = 20.0, currencySymbol = "$"
        )
        val metrics = FinanceCalculator.calculate(input, today = today)
        assertEquals(FireStatus.REACHABLE, metrics.fireStatus)
        assertEquals(24.0, metrics.yearsToRetirement, 0.01)
        assertEquals(62.0, metrics.retirementAge, 0.01)
        assertEquals(803009.5816400539, metrics.retirementTargetCapital, 0.01)
    }

    @Test
    fun baselineFireAchieved() {
        val input = FinancialInputData(
            monthlyIncome = 100_000.0, monthlyExpenses = 50_000.0, liquidAssets = 100_000_000.0,
            financialAssets = 100_000_000.0, propertyAssets = 0.0, liabilities = 0.0, birthDate = "1990-01-01",
            lifeExpectancy = 80, nominalReturnPercent = 7.0, inflationPercent = 2.0
        )
        val metrics = FinanceCalculator.calculate(input, today = today)
        assertEquals(FireStatus.REACHED_NOW, metrics.fireStatus)
        assertEquals(0.0, metrics.yearsToRetirement, 0.001)
        assertEquals(1.1427014472415913E7, metrics.retirementTargetCapital, 1.0)
    }

    @Test
    fun baselineFireUnreachable() {
        val input = FinancialInputData(
            monthlyIncome = 30_000.0, monthlyExpenses = 100_000.0, liquidAssets = 10_000.0, financialAssets = 0.0,
            propertyAssets = 0.0, liabilities = 200_000.0, birthDate = "1990-01-01", lifeExpectancy = 80,
            nominalReturnPercent = 2.0, inflationPercent = 10.0
        )
        val metrics = FinanceCalculator.calculate(input, today = today)
        assertEquals(FireStatus.UNREACHABLE, metrics.fireStatus)
        assertEquals(44.0, metrics.yearsToRetirement, 0.01)
    }

    @Test
    fun baselineFireInvalidAge() {
        val input = FinancialInputData(birthDate = "invalid-date")
        val metrics = FinanceCalculator.calculate(input, today = today)
        assertEquals(FireStatus.INVALID_AGE, metrics.fireStatus)
        assertEquals(0.0, metrics.yearsToRetirement, 0.001)
        assertEquals(0.0, metrics.retirementAge, 0.001)
        assertEquals(0.0, metrics.retirementTargetCapital, 0.001)
        assertTrue(FinanceCalculator.generateTrajectory(metrics, input).isEmpty())
    }

    @Test
    fun baselineScenarioConstants() {
        val amounts = calculateScenarioAmounts(6_000.0, 120_000.0, false)
        assertEquals(6_000.0, amounts.monthlyIncome, 0.0)
        assertEquals(1_200.0, amounts.salaryIncrease, 0.0)
        assertEquals(72_000.0, amounts.inheritance, 0.0)
        assertEquals(120_000.0, amounts.propertySale, 0.0)
        assertEquals(72_000.0, amounts.jobLoss, 0.0)
        assertEquals(2_100.0, amounts.mortgageMonthly, 0.0)
        assertEquals(1_200.0, amounts.carLoanMonthly, 0.0)
        assertEquals(30_000.0, amounts.largeOneOffExpense, 0.0)

        val expenses = calculateSimulationOngoingExpenses(60_000.0, 10_000.0, true, false, false)
        assertEquals(51_000.0, expenses.beforeReduction, 0.000001)
        assertEquals(41_000.0, expenses.effective, 0.000001)
    }

    @Test
    fun baselineTotalScoreCap() {
        assertEquals(6.0, FinanceCalculator.calculateTotalScore(10, 10, 10, 4), 0.001)
        assertTrue(FinanceCalculator.calculateTotalScore(10, 10, 10, 5) > 6.0)
    }
}
