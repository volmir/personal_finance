package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.ProjectionParams
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.theme.ScoreColorEvaluator
import com.example.ui.theme.SuccessGreen
import com.example.util.localization.AppLanguage
import java.time.LocalDate
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CapitalAndAuditConsistencyTest {
    private val today = LocalDate.of(2026, 11, 6)

    @Test
    fun `FIRE start capital equals nonnegative shared Fidelity capital for 1000 seeded inputs`() {
        val random = Random(20261106)
        repeat(1_000) { index ->
            val liquid = random.nextDouble(0.0, 1e12)
            val financial = random.nextDouble(0.0, 1e12)
            val property = random.nextDouble(0.0, 1e12)
            val liabilities = when (index % 4) {
                0 -> property
                1 -> property + liquid + financial
                else -> random.nextDouble(0.0, 1e12)
            }
            val input = FinancialInputData.usAverageData().copy(
                liquidAssets = liquid, financialAssets = financial, propertyAssets = property, liabilities = liabilities
            )
            val expected = maxOf(0.0, FinancialMetricEvaluator.fidelityCapital(liquid, financial, property, liabilities))
            assertEquals("input $index", expected, ProjectionParams.fireStartCapital(input), 0.0)
            assertEquals("params $index", expected, ProjectionParams.from(input, today = today).startCapital, 0.0)
        }
    }

    @Test
    fun `unavailable Fidelity target has neutral fallback and zero ratio regardless of capital`() {
        for (target in listOf(-100.0, -0.0, 0.0)) {
            for (capital in listOf(-100.0, 0.0, 100.0)) {
                assertEquals("capital=$capital target=$target", AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getFidelityStatus(capital, target))
                assertEquals(0.0, FinancialMetricEvaluator.calculateFidelityRatio(capital, target), 0.0)
                assertEquals(0.0, FinancialMetricEvaluator.calculateFidelityPercent(capital, target), 0.0)
            }
        }
        for (input in listOf(
            FinancialInputData.usAverageData().copy(birthDate = "invalid-date"),
            FinancialInputData.usAverageData().copy(monthlyIncome = 0.0)
        )) {
            val metrics = FinanceCalculator.calculate(input, today = today)
            assertTrue(!metrics.isFidelityAvailable)
            assertEquals(0.0, metrics.fidelityRatio, 0.0)
            for (lang in AppLanguage.values()) {
                val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
                assertTrue((opinion.positive + opinion.neutral + opinion.negative).none { it.id == "fidelity" })
            }
        }
    }

    @Test
    fun `audit icons and groups match evaluator statuses for 100 seeded profiles in all languages`() {
        val random = Random(20261107)
        val seenGroups = mutableSetOf<AuditIndicatorStatus>()
        repeat(100) { index ->
            val inflation = random.nextDouble(0.0, 10.0)
            val input = FinancialInputData.usAverageData().copy(
                birthDate = if (index % 17 == 0) "invalid-date" else today.minusYears(random.nextLong(18, 100)).toString(),
                monthlyIncome = if (index % 7 == 0) 0.0 else random.nextDouble(0.0, 20_000.0),
                monthlyExpenses = if (index % 11 == 0) 0.0 else random.nextDouble(0.0, 20_000.0),
                liquidAssets = random.nextDouble(0.0, 50_000.0),
                financialAssets = random.nextDouble(0.0, 2_000_000.0),
                propertyAssets = random.nextDouble(0.0, 2_000_000.0),
                liabilities = if (index % 9 == 0) 0.0 else random.nextDouble(0.0, 3_000_000.0),
                nominalReturnPercent = if (index % 10 == 0) inflation else random.nextDouble(-20.0, 20.0),
                inflationPercent = inflation,
                includeFinancialInEmergencyFund = index % 2 == 0,
                currencySymbol = if (index % 5 == 0) "¥" else "€"
            )
            val metrics = FinanceCalculator.calculate(input, today = today)
            val statuses = mapOf(
                "cash_flow" to FinancialMetricEvaluator.getCashFlowStatus(metrics.cashFlow),
                "savings_rate" to FinancialMetricEvaluator.getSavingsRateStatus(metrics.savingsRate),
                "emergency_fund" to FinancialMetricEvaluator.getSafetyMonthsStatus(metrics.safetyMonths),
                "safety_months" to FinancialMetricEvaluator.getSafetyMonthsStatus(metrics.safetyMonths),
                "debt_ratio" to FinancialMetricEvaluator.getDebtRatioStatus(metrics.debtRatio),
                "net_worth" to FinancialMetricEvaluator.getNetWorthStatus(metrics.netWorth, metrics.totalAssets),
                "fidelity" to if (metrics.isFidelityAvailable) FinancialMetricEvaluator.getFidelityStatus(metrics.fidelityCapital, metrics.fidelityTarget) else null,
                "cfs" to FinancialMetricEvaluator.getCfsStatus(metrics.financialFreedomRatio),
                "real_return" to FinancialMetricEvaluator.getRealReturnStatus(metrics.realReturnPercent)
            ).filterValues { it != null }
            for (lang in AppLanguage.values()) {
                val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
                val groups = listOf(
                    Triple(AuditIndicatorStatus.POSITIVE, opinion.positive, listOf("✅", "🎉")),
                    Triple(AuditIndicatorStatus.NEUTRAL, opinion.neutral, listOf("ℹ️")),
                    Triple(AuditIndicatorStatus.NEGATIVE, opinion.negative, listOf("⚠️", "🚨"))
                )
                assertEquals(statuses.keys, (opinion.positive + opinion.neutral + opinion.negative).map { it.id }.toSet())
                for ((status, items, icons) in groups) {
                    for (item in items) {
                        val label = "profile=$index language=$lang id=${item.id}: ${item.text}"
                        seenGroups += status
                        assertEquals(label, status, item.status)
                        assertEquals(label, statuses[item.id], item.status)
                        assertTrue(label, icons.any { item.text.startsWith(it) })
                    }
                }
            }
        }
        assertEquals(AuditIndicatorStatus.values().toSet(), seenGroups)
    }

    @Test
    fun `calculated total remains green with neutral CFS at both ends of the neutral interval`() {
        for (cfs in listOf(2.0, 3.0, 4.99)) {
            val input = FinancialInputData.usAverageData().copy(
                monthlyIncome = 10_000.0, monthlyExpenses = 5_000.0,
                liquidAssets = 60_000.0, financialAssets = cfs * 60_000.0 - 60_000.0,
                propertyAssets = 0.0, liabilities = 0.0
            )
            val metrics = FinanceCalculator.calculate(input, today = today)
            assertEquals(cfs, metrics.financialFreedomRatio, 0.0)
            assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getCfsStatus(metrics.financialFreedomRatio))
            assertEquals(6, metrics.scores.cfsScore)
            assertEquals(8.2, metrics.scores.totalScore, 0.0)
            assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getTotalScoreStatus(metrics.scores.totalScore))
            assertEquals(SuccessGreen, ScoreColorEvaluator.getTotalScoreColor(metrics.scores.totalScore))
        }
    }
}
