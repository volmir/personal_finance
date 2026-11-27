package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialAssessmentCalculator
import com.example.domain.FinancialAssessmentInput
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.model.FinancialAssessmentComponent
import com.example.domain.model.FinancialAssessmentComponents
import com.example.domain.model.FinancialAssessmentGroup
import com.example.domain.model.FinancialAssessmentId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FinancialAssessmentCalculatorTest {

    private val fixedToday = LocalDate.of(2026, 11, 1)

    private fun input(
        cashFlow: Double = 3_000.0,
        savingsRate: Double = 50.0,
        safetyMonths: Double = 6.0,
        debtRatio: Double = 0.1,
        netWorth: Double = 180_000.0,
        totalAssets: Double = 200_000.0,
        fidelityCapital: Double = 100_000.0,
        fidelityTarget: Double = 200_000.0,
        isAgeValid: Boolean = true,
        cfs: Double = 5.0,
        realReturnPercent: Double = 5.0,
        monthlyIncome: Double = 6_000.0,
        monthlyExpenses: Double = 3_000.0,
        liabilities: Double = 20_000.0
    ): FinancialAssessmentInput = FinancialAssessmentInput(
        cashFlow = cashFlow,
        savingsRate = savingsRate,
        safetyMonths = safetyMonths,
        debtRatio = debtRatio,
        netWorth = netWorth,
        totalAssets = totalAssets,
        fidelityCapital = fidelityCapital,
        fidelityTarget = fidelityTarget,
        isAgeValid = isAgeValid,
        cfs = cfs,
        realReturnPercent = realReturnPercent,
        monthlyIncome = monthlyIncome,
        monthlyExpenses = monthlyExpenses,
        liabilities = liabilities
    )

    private fun assess(input: FinancialAssessmentInput): FinancialAssessmentComponents =
        FinancialAssessmentCalculator.calculate(input)

    @Test
    fun `catalog defines nine unique stable ids in deterministic order`() {
        val ids = FinancialAssessmentId.values()
        assertEquals(9, ids.size)
        assertEquals(
            listOf(
                "cash_flow",
                "savings_rate",
                "emergency_fund",
                "safety_months",
                "debt_ratio",
                "net_worth",
                "fidelity",
                "cfs",
                "real_return"
            ),
            ids.map { it.stableId }
        )
        assertEquals(9, ids.map { it.stableId }.toSet().size)
    }

    @Test
    fun `group and indicator weights sum to one hundred`() {
        assertEquals(100, FinancialAssessmentGroup.values().sumOf { it.weightPercent })
        assertEquals(25, FinancialAssessmentGroup.BUDGET.weightPercent)
        assertEquals(25, FinancialAssessmentGroup.RESERVE.weightPercent)
        assertEquals(15, FinancialAssessmentGroup.DEBT.weightPercent)
        assertEquals(35, FinancialAssessmentGroup.CAPITAL.weightPercent)

        val weightedIds = FinancialAssessmentId.values().filter { !it.isForecastOnly }
        assertEquals(100, weightedIds.sumOf { it.weightPercent })
        assertEquals(100, FinancialAssessmentId.values().sumOf { it.weightPercent })

        for (group in FinancialAssessmentGroup.values()) {
            assertEquals(
                "Group ${group.name} weight must match its indicators",
                group.weightPercent,
                weightedIds.filter { it.group == group }.sumOf { it.weightPercent }
            )
        }

        for (id in weightedIds) {
            assertTrue("${id.stableId} must have a group", id.group != null)
            assertTrue("${id.stableId} must have a positive weight", id.weightPercent > 0)
            assertFalse("${id.stableId} is not a forecast parameter", id.isForecastOnly)
        }

        assertEquals(0, FinancialAssessmentId.REAL_RETURN.weightPercent)
        assertTrue(FinancialAssessmentId.REAL_RETURN.isForecastOnly)
        assertNull(FinancialAssessmentId.REAL_RETURN.group)
    }

    @Test
    fun `component model distinguishes unavailable zero and forecast only`() {
        val result = FinancialAssessmentComponents.of(
            mapOf(
                FinancialAssessmentId.CASH_FLOW to 0,
                FinancialAssessmentId.SAVINGS_RATE to null,
                FinancialAssessmentId.EMERGENCY_FUND to 7,
                FinancialAssessmentId.SAFETY_MONTHS to 7,
                FinancialAssessmentId.DEBT_RATIO to 10,
                FinancialAssessmentId.NET_WORTH to 5,
                FinancialAssessmentId.FIDELITY to null,
                FinancialAssessmentId.CFS to 8,
                FinancialAssessmentId.REAL_RETURN to 9
            )
        )

        assertEquals(FinancialAssessmentId.values().toList(), result.components.map { it.id })
        assertEquals(0, result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertTrue(result[FinancialAssessmentId.CASH_FLOW].isAvailable)
        assertNull(result.scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertFalse(result[FinancialAssessmentId.SAVINGS_RATE].isAvailable)
        assertEquals(5, result[FinancialAssessmentId.CASH_FLOW].weightPercent)
        assertEquals(FinancialAssessmentGroup.BUDGET, result[FinancialAssessmentId.CASH_FLOW].group)
        assertTrue(result[FinancialAssessmentId.REAL_RETURN].isForecastOnly)
        assertEquals(0, result[FinancialAssessmentId.REAL_RETURN].weightPercent)
        assertNull(result[FinancialAssessmentId.REAL_RETURN].group)
        assertEquals(9, result.scoreOf(FinancialAssessmentId.REAL_RETURN))
    }

    @Test
    fun `component catalog rejects duplicate missing unordered or out of range entries`() {
        val ordered = FinancialAssessmentId.values().map { FinancialAssessmentComponent(it, 0) }

        assertThrows(IllegalArgumentException::class.java) {
            FinancialAssessmentComponents(ordered.dropLast(1))
        }
        assertThrows(IllegalArgumentException::class.java) {
            FinancialAssessmentComponents(ordered.reversed())
        }
        val duplicate = ordered.toMutableList().also { it[1] = it[0] }
        assertThrows(IllegalArgumentException::class.java) {
            FinancialAssessmentComponents(duplicate)
        }
        assertThrows(IllegalArgumentException::class.java) {
            FinancialAssessmentComponent(FinancialAssessmentId.CASH_FLOW, -1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            FinancialAssessmentComponent(FinancialAssessmentId.CASH_FLOW, 11)
        }
    }

    @Test
    fun `cash flow score covers boundaries and unavailable cases`() {
        assertEquals(0, FinancialMetricEvaluator.getCashFlowScore(0.0, 50.0, 6_000.0))
        assertEquals(0, FinancialMetricEvaluator.getCashFlowScore(-0.01, 50.0, 6_000.0))
        assertEquals(8, FinancialMetricEvaluator.getCashFlowScore(1.0, 0.0, 6_000.0))
        assertEquals(8, FinancialMetricEvaluator.getCashFlowScore(1.0, 20.0, 6_000.0))
        assertEquals(9, FinancialMetricEvaluator.getCashFlowScore(1.0, Math.nextUp(20.0), 6_000.0))
        assertEquals(9, FinancialMetricEvaluator.getCashFlowScore(1.0, Math.nextDown(50.0), 6_000.0))
        assertEquals(10, FinancialMetricEvaluator.getCashFlowScore(1.0, 50.0, 6_000.0))
        assertEquals(10, FinancialMetricEvaluator.getCashFlowScore(Double.MAX_VALUE, 50.0, 6_000.0))

        assertNull(FinancialMetricEvaluator.getCashFlowScore(1.0, 50.0, 0.0))
        assertNull(FinancialMetricEvaluator.getCashFlowScore(1.0, 50.0, -1.0))
        assertNull(FinancialMetricEvaluator.getCashFlowScore(Double.NaN, 50.0, 6_000.0))
        assertNull(FinancialMetricEvaluator.getCashFlowScore(1.0, Double.NaN, 6_000.0))
        assertNull(FinancialMetricEvaluator.getCashFlowScore(1.0, 50.0, Double.NaN))
        assertNull(FinancialMetricEvaluator.getCashFlowScore(Double.POSITIVE_INFINITY, 50.0, 6_000.0))

        // Отрицательный поток остаётся валидным критическим случаем.
        assertEquals(0, FinancialMetricEvaluator.getCashFlowScore(-1.0, Double.NaN, Double.NaN))
    }

    @Test
    fun `net worth score covers share boundaries and unavailable cases`() {
        assertEquals(0, FinancialMetricEvaluator.getNetWorthScore(-1.0, 100.0))
        assertEquals(0, FinancialMetricEvaluator.getNetWorthScore(-0.01, 100.0))
        assertEquals(5, FinancialMetricEvaluator.getNetWorthScore(0.0, 100.0))
        assertEquals(5, FinancialMetricEvaluator.getNetWorthScore(-0.0, 100.0))
        assertEquals(5, FinancialMetricEvaluator.getNetWorthScore(0.0, 0.0))
        assertEquals(0, FinancialMetricEvaluator.getNetWorthScore(-1.0, 0.0))

        assertEquals(6, FinancialMetricEvaluator.getNetWorthScore(24.99, 100.0))
        assertEquals(7, FinancialMetricEvaluator.getNetWorthScore(25.0, 100.0))
        assertEquals(7, FinancialMetricEvaluator.getNetWorthScore(49.99, 100.0))
        assertEquals(8, FinancialMetricEvaluator.getNetWorthScore(50.0, 100.0))
        assertEquals(8, FinancialMetricEvaluator.getNetWorthScore(69.99, 100.0))
        assertEquals(9, FinancialMetricEvaluator.getNetWorthScore(70.0, 100.0))
        assertEquals(9, FinancialMetricEvaluator.getNetWorthScore(89.99, 100.0))
        assertEquals(10, FinancialMetricEvaluator.getNetWorthScore(90.0, 100.0))
        assertEquals(10, FinancialMetricEvaluator.getNetWorthScore(100.0, 100.0))
        assertEquals(10, FinancialMetricEvaluator.getNetWorthScore(Double.MAX_VALUE, Double.MAX_VALUE))

        // Точные соседние значения доли активов.
        assertEquals(6, FinancialMetricEvaluator.getNetWorthScore(Math.nextDown(0.25), 1.0))
        assertEquals(7, FinancialMetricEvaluator.getNetWorthScore(0.25, 1.0))
        assertEquals(7, FinancialMetricEvaluator.getNetWorthScore(Math.nextUp(0.25), 1.0))
        assertEquals(7, FinancialMetricEvaluator.getNetWorthScore(Math.nextDown(0.5), 1.0))
        assertEquals(8, FinancialMetricEvaluator.getNetWorthScore(0.5, 1.0))
        assertEquals(8, FinancialMetricEvaluator.getNetWorthScore(Math.nextUp(0.5), 1.0))
        assertEquals(8, FinancialMetricEvaluator.getNetWorthScore(Math.nextDown(0.7), 1.0))
        assertEquals(9, FinancialMetricEvaluator.getNetWorthScore(0.7, 1.0))
        assertEquals(9, FinancialMetricEvaluator.getNetWorthScore(Math.nextUp(0.7), 1.0))
        assertEquals(9, FinancialMetricEvaluator.getNetWorthScore(Math.nextDown(0.9), 1.0))
        assertEquals(10, FinancialMetricEvaluator.getNetWorthScore(0.9, 1.0))
        assertEquals(10, FinancialMetricEvaluator.getNetWorthScore(Math.nextUp(0.9), 1.0))

        assertNull(FinancialMetricEvaluator.getNetWorthScore(1.0, 0.0))
        assertNull(FinancialMetricEvaluator.getNetWorthScore(1.0, -1.0))
        assertNull(FinancialMetricEvaluator.getNetWorthScore(1.0, Double.NaN))
        assertNull(FinancialMetricEvaluator.getNetWorthScore(Double.NaN, 100.0))
        assertNull(FinancialMetricEvaluator.getNetWorthScore(Double.POSITIVE_INFINITY, 100.0))

        // Для отрицательного и нулевого капитала достаточно знака.
        assertEquals(0, FinancialMetricEvaluator.getNetWorthScore(-1.0, Double.NaN))
        assertEquals(5, FinancialMetricEvaluator.getNetWorthScore(0.0, Double.NaN))
    }

    @Test
    fun `fidelity score covers exact ratio boundaries and availability`() {
        assertEquals(0, FinancialMetricEvaluator.getFidelityScore(-1.0))
        assertEquals(0, FinancialMetricEvaluator.getFidelityScore(0.0))
        assertEquals(2, FinancialMetricEvaluator.getFidelityScore(Math.nextUp(0.0)))
        assertEquals(2, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(0.2)))
        assertEquals(3, FinancialMetricEvaluator.getFidelityScore(0.2))
        assertEquals(3, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(0.4)))
        assertEquals(4, FinancialMetricEvaluator.getFidelityScore(0.4))
        assertEquals(4, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(0.7)))
        assertEquals(5, FinancialMetricEvaluator.getFidelityScore(0.7))
        assertEquals(5, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(0.8)))
        assertEquals(6, FinancialMetricEvaluator.getFidelityScore(0.8))
        assertEquals(6, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(0.9)))
        assertEquals(7, FinancialMetricEvaluator.getFidelityScore(0.9))
        assertEquals(7, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(1.0)))
        assertEquals(8, FinancialMetricEvaluator.getFidelityScore(1.0))
        assertEquals(8, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(1.2)))
        assertEquals(9, FinancialMetricEvaluator.getFidelityScore(1.2))
        assertEquals(9, FinancialMetricEvaluator.getFidelityScore(Math.nextDown(1.5)))
        assertEquals(10, FinancialMetricEvaluator.getFidelityScore(1.5))
        assertEquals(10, FinancialMetricEvaluator.getFidelityScore(Double.MAX_VALUE))

        assertNull(FinancialMetricEvaluator.getFidelityScore(Double.NaN))
        assertNull(FinancialMetricEvaluator.getFidelityScore(Double.POSITIVE_INFINITY))
        assertNull(FinancialMetricEvaluator.getFidelityScore(Double.NEGATIVE_INFINITY))

        assertEquals(8, FinancialMetricEvaluator.getFidelityScore(100.0, 100.0, true))
        assertEquals(0, FinancialMetricEvaluator.getFidelityScore(0.0, 100.0, true))
        assertEquals(0, FinancialMetricEvaluator.getFidelityScore(-100.0, 100.0, true))
        assertEquals(8, FinancialMetricEvaluator.getFidelityScore(Double.MAX_VALUE, Double.MAX_VALUE, true))

        assertNull(FinancialMetricEvaluator.getFidelityScore(100.0, 0.0, true))
        assertNull(FinancialMetricEvaluator.getFidelityScore(100.0, -1.0, true))
        assertNull(FinancialMetricEvaluator.getFidelityScore(100.0, 100.0, false))
        assertNull(FinancialMetricEvaluator.getFidelityScore(Double.NaN, 100.0, true))
        assertNull(FinancialMetricEvaluator.getFidelityScore(100.0, Double.NaN, true))
        assertNull(FinancialMetricEvaluator.getFidelityScore(Double.POSITIVE_INFINITY, 100.0, true))
        assertNull(FinancialMetricEvaluator.getFidelityScore(100.0, Double.POSITIVE_INFINITY, true))
    }

    @Test
    fun `real return score covers exact percentage boundaries and invalid values`() {
        assertEquals(0, FinancialMetricEvaluator.getRealReturnScore(-100.0))
        assertEquals(0, FinancialMetricEvaluator.getRealReturnScore(-5.0001))
        assertEquals(0, FinancialMetricEvaluator.getRealReturnScore(-Double.MAX_VALUE))
        assertEquals(2, FinancialMetricEvaluator.getRealReturnScore(-5.0))
        assertEquals(2, FinancialMetricEvaluator.getRealReturnScore(Math.nextDown(-2.0)))
        assertEquals(4, FinancialMetricEvaluator.getRealReturnScore(-2.0))
        assertEquals(4, FinancialMetricEvaluator.getRealReturnScore(-0.0001))
        assertEquals(4, FinancialMetricEvaluator.getRealReturnScore(Math.nextDown(0.0)))
        assertEquals(6, FinancialMetricEvaluator.getRealReturnScore(0.0))
        assertEquals(6, FinancialMetricEvaluator.getRealReturnScore(-0.0))
        assertEquals(8, FinancialMetricEvaluator.getRealReturnScore(Math.nextUp(0.0)))
        assertEquals(8, FinancialMetricEvaluator.getRealReturnScore(1.9999))
        assertEquals(9, FinancialMetricEvaluator.getRealReturnScore(2.0))
        assertEquals(9, FinancialMetricEvaluator.getRealReturnScore(Math.nextDown(4.0)))
        assertEquals(10, FinancialMetricEvaluator.getRealReturnScore(4.0))
        assertEquals(10, FinancialMetricEvaluator.getRealReturnScore(Double.MAX_VALUE))

        assertNull(FinancialMetricEvaluator.getRealReturnScore(Double.NaN))
        assertNull(FinancialMetricEvaluator.getRealReturnScore(Double.POSITIVE_INFINITY))
        assertNull(FinancialMetricEvaluator.getRealReturnScore(Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `zero expenses make reserve and cfs scores unavailable despite legacy special values`() {
        val result = assess(
            input(
                cashFlow = 6_000.0,
                savingsRate = 100.0,
                safetyMonths = 12.0,
                cfs = 30.0,
                monthlyExpenses = 0.0
            )
        )

        assertNull(result.scoreOf(FinancialAssessmentId.EMERGENCY_FUND))
        assertNull(result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertNull(result.scoreOf(FinancialAssessmentId.CFS))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.NET_WORTH))
    }

    @Test
    fun `zero income and zero expenses make budget scores unavailable`() {
        val result = assess(
            input(
                cashFlow = 0.0,
                savingsRate = 0.0,
                safetyMonths = 0.0,
                debtRatio = 0.0,
                netWorth = 0.0,
                totalAssets = 0.0,
                fidelityCapital = 0.0,
                fidelityTarget = 0.0,
                cfs = 0.0,
                monthlyIncome = 0.0,
                monthlyExpenses = 0.0,
                liabilities = 0.0
            )
        )

        assertNull(result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertNull(result.scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertNull(result.scoreOf(FinancialAssessmentId.EMERGENCY_FUND))
        assertNull(result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertNull(result.scoreOf(FinancialAssessmentId.CFS))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertEquals(5, result.scoreOf(FinancialAssessmentId.NET_WORTH))
        assertNull(result.scoreOf(FinancialAssessmentId.FIDELITY))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.REAL_RETURN))
    }

    @Test
    fun `zero income with expenses keeps deficit scores and makes fidelity unavailable`() {
        val result = assess(
            input(
                cashFlow = -3_000.0,
                savingsRate = -100.0,
                safetyMonths = 6.0,
                debtRatio = 0.1,
                netWorth = 180_000.0,
                totalAssets = 200_000.0,
                fidelityCapital = 100_000.0,
                fidelityTarget = 0.0,
                cfs = 5.0,
                monthlyIncome = 0.0,
                monthlyExpenses = 3_000.0
            )
        )

        assertEquals(0, result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertEquals(0, result.scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertEquals(9, result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertEquals(8, result.scoreOf(FinancialAssessmentId.CFS))
        assertNull(result.scoreOf(FinancialAssessmentId.FIDELITY))
    }

    @Test
    fun `emergency fund score mirrors safety months at every boundary`() {
        for (months in listOf(-1.0, 0.0, 0.49, 0.5, 0.99, 1.0, 2.99, 3.0, 5.99, 6.0, 11.99, 12.0, 100.0)) {
            val result = assess(input(safetyMonths = months, monthlyExpenses = 1_000.0))
            assertEquals(
                "months=$months",
                result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS),
                result.scoreOf(FinancialAssessmentId.EMERGENCY_FUND)
            )
        }

        val unavailable = assess(input(safetyMonths = 12.0, monthlyExpenses = 0.0))
        assertNull(unavailable.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertNull(unavailable.scoreOf(FinancialAssessmentId.EMERGENCY_FUND))
    }

    @Test
    fun `zero assets without debt keeps debt score ten and net worth five`() {
        val result = assess(
            input(
                cashFlow = 500.0,
                savingsRate = 50.0,
                safetyMonths = 0.0,
                debtRatio = 0.0,
                netWorth = 0.0,
                totalAssets = 0.0,
                fidelityCapital = 0.0,
                fidelityTarget = 100_000.0,
                cfs = 0.0,
                monthlyIncome = 1_000.0,
                monthlyExpenses = 500.0,
                liabilities = 0.0
            )
        )

        assertEquals(10, result.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertEquals(5, result.scoreOf(FinancialAssessmentId.NET_WORTH))
    }

    @Test
    fun `zero assets with debt gives available critical debt and net worth scores`() {
        val result = assess(
            input(
                cashFlow = 500.0,
                savingsRate = 50.0,
                safetyMonths = 0.0,
                debtRatio = Double.POSITIVE_INFINITY,
                netWorth = -100.0,
                totalAssets = 0.0,
                fidelityCapital = -100.0,
                fidelityTarget = 100_000.0,
                cfs = -0.01,
                monthlyIncome = 1_000.0,
                monthlyExpenses = 500.0,
                liabilities = 100.0
            )
        )

        assertEquals(0, result.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertTrue(result[FinancialAssessmentId.DEBT_RATIO].isAvailable)
        assertEquals(0, result.scoreOf(FinancialAssessmentId.NET_WORTH))
        assertEquals(0, result.scoreOf(FinancialAssessmentId.FIDELITY))
    }

    @Test
    fun `negative debt ratio is unavailable instead of excellent`() {
        assertNull(assess(input(debtRatio = -0.1)).scoreOf(FinancialAssessmentId.DEBT_RATIO))
    }

    @Test
    fun `NaN inputs become unavailable instead of high legacy scores`() {
        val result = assess(
            input(
                cashFlow = 1.0,
                savingsRate = Double.NaN,
                safetyMonths = Double.NaN,
                debtRatio = Double.NaN,
                netWorth = Double.NaN,
                totalAssets = 100.0,
                fidelityCapital = Double.NaN,
                fidelityTarget = 100.0,
                cfs = Double.NaN,
                realReturnPercent = Double.NaN
            )
        )

        assertNull(result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertNull(result.scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertNull(result.scoreOf(FinancialAssessmentId.EMERGENCY_FUND))
        assertNull(result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertNull(result.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertNull(result.scoreOf(FinancialAssessmentId.NET_WORTH))
        assertNull(result.scoreOf(FinancialAssessmentId.FIDELITY))
        assertNull(result.scoreOf(FinancialAssessmentId.CFS))
        assertNull(result.scoreOf(FinancialAssessmentId.REAL_RETURN))
    }

    @Test
    fun `positive infinity debt is scored while other non finite ratios are unavailable`() {
        val debt = assess(input(debtRatio = Double.POSITIVE_INFINITY, netWorth = -1.0, totalAssets = 0.0))
        assertEquals(0, debt.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertTrue(debt[FinancialAssessmentId.DEBT_RATIO].isAvailable)

        assertNull(assess(input(debtRatio = Double.NEGATIVE_INFINITY)).scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertNull(assess(input(savingsRate = Double.POSITIVE_INFINITY)).scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertNull(assess(input(safetyMonths = Double.POSITIVE_INFINITY)).scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertNull(assess(input(cfs = Double.POSITIVE_INFINITY)).scoreOf(FinancialAssessmentId.CFS))
        assertNull(assess(input(realReturnPercent = Double.POSITIVE_INFINITY)).scoreOf(FinancialAssessmentId.REAL_RETURN))
    }

    @Test
    fun `fidelity availability follows age and positive target`() {
        assertNull(assess(input(isAgeValid = false)).scoreOf(FinancialAssessmentId.FIDELITY))
        assertNull(assess(input(fidelityTarget = 0.0)).scoreOf(FinancialAssessmentId.FIDELITY))
        assertNull(assess(input(fidelityTarget = -1.0)).scoreOf(FinancialAssessmentId.FIDELITY))
        assertEquals(0, assess(input(fidelityCapital = 0.0, fidelityTarget = 100.0)).scoreOf(FinancialAssessmentId.FIDELITY))
        assertEquals(8, assess(input(fidelityCapital = 100.0, fidelityTarget = 100.0)).scoreOf(FinancialAssessmentId.FIDELITY))
    }

    @Test
    fun `finite maximum values are scored without overflow`() {
        val result = assess(
            input(
                cashFlow = Double.MAX_VALUE,
                savingsRate = 50.0,
                safetyMonths = Double.MAX_VALUE,
                debtRatio = 0.0,
                netWorth = Double.MAX_VALUE,
                totalAssets = Double.MAX_VALUE,
                fidelityCapital = Double.MAX_VALUE,
                fidelityTarget = Double.MAX_VALUE,
                cfs = Double.MAX_VALUE,
                realReturnPercent = Double.MAX_VALUE,
                monthlyIncome = Double.MAX_VALUE,
                monthlyExpenses = Double.MAX_VALUE,
                liabilities = 0.0
            )
        )

        assertEquals(10, result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.SAVINGS_RATE))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.EMERGENCY_FUND))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.NET_WORTH))
        assertEquals(8, result.scoreOf(FinancialAssessmentId.FIDELITY))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.CFS))
        assertEquals(10, result.scoreOf(FinancialAssessmentId.REAL_RETURN))
    }

    @Test
    fun `input factory copies computed metrics and source amounts`() {
        val source = FinancialInputData(
            birthDate = "1990-01-01",
            monthlyIncome = 6_000.0,
            monthlyExpenses = 3_000.0,
            liquidAssets = 10_000.0,
            financialAssets = 20_000.0,
            propertyAssets = 170_000.0,
            liabilities = 20_000.0
        )
        val metrics = FinanceCalculator.calculate(source, today = fixedToday)
        val assessmentInput = FinancialAssessmentInput.from(source, metrics)

        assertEquals(metrics.cashFlow, assessmentInput.cashFlow, 0.0)
        assertEquals(metrics.savingsRate, assessmentInput.savingsRate, 0.0)
        assertEquals(metrics.safetyMonths, assessmentInput.safetyMonths, 0.0)
        assertEquals(metrics.debtRatio, assessmentInput.debtRatio, 0.0)
        assertEquals(metrics.netWorth, assessmentInput.netWorth, 0.0)
        assertEquals(metrics.totalAssets, assessmentInput.totalAssets, 0.0)
        assertEquals(metrics.fidelityCapital, assessmentInput.fidelityCapital, 0.0)
        assertEquals(metrics.fidelityTarget, assessmentInput.fidelityTarget, 0.0)
        assertEquals(metrics.isAgeValid, assessmentInput.isAgeValid)
        assertEquals(metrics.financialFreedomRatio, assessmentInput.cfs, 0.0)
        assertEquals(metrics.realReturnPercent, assessmentInput.realReturnPercent, 0.0)
        assertEquals(source.monthlyIncome, assessmentInput.monthlyIncome, 0.0)
        assertEquals(source.monthlyExpenses, assessmentInput.monthlyExpenses, 0.0)
        assertEquals(source.liabilities, assessmentInput.liabilities, 0.0)
    }

    @Test
    fun `legacy special safety and cfs values are not scored when expenses are zero`() {
        val source = FinancialInputData(
            birthDate = "1990-01-01",
            monthlyIncome = 6_000.0,
            monthlyExpenses = 0.0,
            liquidAssets = 10_000.0,
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 0.0
        )
        val metrics = FinanceCalculator.calculate(source, today = fixedToday)
        assertEquals(12.0, metrics.safetyMonths, 0.0)
        assertEquals(30.0, metrics.financialFreedomRatio, 0.0)

        val oldScores = metrics.scores
        val result = FinancialAssessmentCalculator.calculate(source, metrics)

        assertNull(result.scoreOf(FinancialAssessmentId.EMERGENCY_FUND))
        assertNull(result.scoreOf(FinancialAssessmentId.SAFETY_MONTHS))
        assertNull(result.scoreOf(FinancialAssessmentId.CFS))
        assertEquals(oldScores, metrics.scores)
    }

    @Test
    fun `currency normalized zero amounts score as zero`() {
        val source = FinancialInputData(
            birthDate = "1990-01-01",
            monthlyIncome = 6_000.0,
            monthlyExpenses = 6_000.004,
            liquidAssets = 0.004,
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 0.0
        )
        val metrics = FinanceCalculator.calculate(source, today = fixedToday)
        assertEquals(0.0, metrics.cashFlow, 0.0)
        assertEquals(0.0, metrics.totalAssets, 0.0)
        assertEquals(0.0, metrics.netWorth, 0.0)

        val result = FinancialAssessmentCalculator.calculate(source, metrics)

        assertEquals(0, result.scoreOf(FinancialAssessmentId.CASH_FLOW))
        assertEquals(5, result.scoreOf(FinancialAssessmentId.NET_WORTH))
    }
}
