package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialAssessmentAggregationInput
import com.example.domain.FinancialAssessmentCalculator
import com.example.domain.FinancialAssessmentInput
import com.example.domain.model.AuditIndicatorStatus
import com.example.domain.model.FinancialAssessmentCapReason
import com.example.domain.model.FinancialAssessmentComponents
import com.example.domain.model.FinancialAssessmentGroup
import com.example.domain.model.FinancialAssessmentId
import com.example.domain.model.FinancialAssessmentResult
import com.example.domain.model.FinancialAssessmentUnavailableReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FinancialAssessmentAggregationTest {

    private fun components(
        cashFlow: Int? = null,
        savingsRate: Int? = null,
        emergencyFund: Int? = null,
        safetyMonths: Int? = null,
        debtRatio: Int? = null,
        netWorth: Int? = null,
        fidelity: Int? = null,
        cfs: Int? = null,
        realReturn: Int? = 6
    ): FinancialAssessmentComponents = FinancialAssessmentComponents.of(
        mapOf(
            FinancialAssessmentId.CASH_FLOW to cashFlow,
            FinancialAssessmentId.SAVINGS_RATE to savingsRate,
            FinancialAssessmentId.EMERGENCY_FUND to emergencyFund,
            FinancialAssessmentId.SAFETY_MONTHS to safetyMonths,
            FinancialAssessmentId.DEBT_RATIO to debtRatio,
            FinancialAssessmentId.NET_WORTH to netWorth,
            FinancialAssessmentId.FIDELITY to fidelity,
            FinancialAssessmentId.CFS to cfs,
            FinancialAssessmentId.REAL_RETURN to realReturn
        )
    )

    /** Синтетические баллы: обе компоненты группы получают одинаковый балл. */
    private fun groups(
        budget: Int? = null,
        reserve: Int? = null,
        debt: Int? = null,
        capital: Int? = null,
        realReturn: Int? = 6
    ): FinancialAssessmentComponents = components(
        cashFlow = budget,
        savingsRate = budget,
        emergencyFund = reserve,
        safetyMonths = reserve,
        debtRatio = debt,
        netWorth = debt,
        fidelity = capital,
        cfs = capital,
        realReturn = realReturn
    )

    private fun context(
        netWorth: Double = 1_000.0,
        totalAssets: Double = 2_000.0,
        liabilities: Double = 1_000.0,
        monthlyIncome: Double = 5_000.0,
        monthlyExpenses: Double = 4_000.0
    ): FinancialAssessmentAggregationInput = FinancialAssessmentAggregationInput(
        netWorth = netWorth,
        totalAssets = totalAssets,
        liabilities = liabilities,
        monthlyIncome = monthlyIncome,
        monthlyExpenses = monthlyExpenses
    )

    private fun aggregate(
        components: FinancialAssessmentComponents,
        input: FinancialAssessmentAggregationInput = context()
    ): FinancialAssessmentResult = FinancialAssessmentCalculator.aggregate(components, input)

    private fun total(result: FinancialAssessmentResult): Double =
        requireNotNull(result.totalScore) { "Expected an available total score" }

    @Test
    fun `minimum and maximum component scores are valid available totals`() {
        val minimum = aggregate(groups(budget = 0, reserve = 0, debt = 0, capital = 0))
        assertEquals(0.0, total(minimum), 0.0)
        assertEquals(0.0, minimum.rawScore!!, 0.0)
        assertEquals(100, minimum.coveragePercent)
        assertTrue(minimum.isComplete)
        assertFalse(minimum.isIncomplete)
        assertTrue(minimum.isAvailable)
        assertEquals(AuditIndicatorStatus.NEGATIVE, minimum.overallStatus)
        assertFalse(minimum.isExcellent)
        assertTrue(minimum.capReasons.isEmpty())
        for (group in FinancialAssessmentGroup.values()) {
            assertEquals(0.0, minimum[group].score!!, 0.0)
        }

        val maximum = aggregate(groups(budget = 10, reserve = 10, debt = 10, capital = 10))
        assertEquals(10.0, total(maximum), 0.0)
        assertEquals(10.0, maximum.rawScore!!, 0.0)
        assertEquals(100, maximum.coveragePercent)
        assertEquals(AuditIndicatorStatus.POSITIVE, maximum.overallStatus)
        assertTrue(maximum.isExcellent)
        assertTrue(maximum.capReasons.isEmpty())
        for (group in FinancialAssessmentGroup.values()) {
            assertEquals(10.0, maximum[group].score!!, 0.0)
        }
    }

    @Test
    fun `equal group scores ignore group weight differences`() {
        val result = aggregate(groups(budget = 8, reserve = 8, debt = 8, capital = 8))
        assertEquals(8.0, total(result), 0.0)
        for (group in FinancialAssessmentGroup.values()) {
            assertEquals(8.0, result[group].score!!, 0.0)
        }
        assertEquals(AuditIndicatorStatus.POSITIVE, result.overallStatus)
        assertFalse(result.isExcellent)
    }

    @Test
    fun `group weights determine total when scores differ`() {
        val budgetAndDebt = aggregate(groups(budget = 10, reserve = 0, debt = 10, capital = 0))
        assertEquals(4.0, total(budgetAndDebt), 0.0)

        val reserveAndCapital = aggregate(groups(budget = 0, reserve = 10, debt = 0, capital = 10))
        assertEquals(6.0, total(reserveAndCapital), 0.0)
    }

    @Test
    fun `specification example with all groups yields six point four`() {
        val result = aggregate(groups(budget = 8, reserve = 6, debt = 10, capital = 4))
        assertEquals(6.4, total(result), 0.0)
        assertEquals(6.4, result.rawScore!!, 0.0)
        assertEquals(100, result.coveragePercent)
        assertTrue(result.isComplete)
        assertEquals(AuditIndicatorStatus.NEUTRAL, result.overallStatus)
    }

    @Test
    fun `missing capital group renormalizes available group weights and coverage`() {
        val result = aggregate(groups(budget = 8, reserve = 6, debt = 10, capital = null))
        assertEquals(7.7, total(result), 0.0)
        assertEquals(500.0 / 65.0, result.rawScore!!, 1e-12)
        assertEquals(65, result.coveragePercent)
        assertFalse(result.isComplete)
        assertTrue(result.isIncomplete)
        assertNull(result[FinancialAssessmentGroup.CAPITAL].score)
        assertFalse(result[FinancialAssessmentGroup.CAPITAL].isAvailable)
        assertEquals(AuditIndicatorStatus.POSITIVE, result.overallStatus)
    }

    @Test
    fun `missing fidelity keeps capital group weight and coverage at ninety`() {
        val result = aggregate(
            components(
                cashFlow = 8,
                savingsRate = 8,
                emergencyFund = 6,
                safetyMonths = 6,
                debtRatio = 10,
                netWorth = 10,
                fidelity = null,
                cfs = 8
            )
        )
        assertEquals(7.8, total(result), 0.0)
        assertEquals(90, result.coveragePercent)
        assertFalse(result.isComplete)
        assertEquals(8.0, result[FinancialAssessmentGroup.CAPITAL].score!!, 0.0)
        assertTrue(result[FinancialAssessmentGroup.CAPITAL].isAvailable)
        assertEquals(100, result.groups.filter { it.isAvailable }.sumOf { it.group.weightPercent })
    }

    @Test
    fun `partial group keeps full group weight in aggregation`() {
        val result = aggregate(
            components(
                cashFlow = 8,
                savingsRate = null,
                emergencyFund = 6,
                safetyMonths = 6,
                debtRatio = 10,
                netWorth = 10,
                fidelity = 4,
                cfs = 4
            )
        )
        assertEquals(8.0, result[FinancialAssessmentGroup.BUDGET].score!!, 0.0)
        assertEquals(80, result.coveragePercent)
        assertEquals(6.4, total(result), 0.0)
        assertFalse(result.isComplete)
    }

    @Test
    fun `within group weights are normalized over available components`() {
        val result = aggregate(
            components(
                cashFlow = 5,
                savingsRate = 3,
                emergencyFund = 2,
                safetyMonths = 4,
                debtRatio = null,
                netWorth = null,
                fidelity = null,
                cfs = null
            )
        )
        assertEquals(3.4, result[FinancialAssessmentGroup.BUDGET].score!!, 1e-12)
        assertEquals(3.6, result[FinancialAssessmentGroup.RESERVE].score!!, 1e-12)
        assertEquals(3.5, total(result), 0.0)
        assertEquals(50, result.coveragePercent)
        assertEquals(AuditIndicatorStatus.NEUTRAL, result.overallStatus)
    }

    @Test
    fun `no available groups makes total unavailable without substituting zero`() {
        val result = aggregate(components(realReturn = 10))
        assertNull(result.totalScore)
        assertNull(result.rawScore)
        assertEquals(0, result.coveragePercent)
        assertFalse(result.isAvailable)
        assertFalse(result.isComplete)
        assertNull(result.overallStatus)
        assertTrue(result.capReasons.isEmpty())
        assertEquals(FinancialAssessmentUnavailableReason.NO_AVAILABLE_GROUPS, result.unavailableReason)
        for (group in FinancialAssessmentGroup.values()) {
            assertNull(result[group].score)
        }
    }

    @Test
    fun `all zero input makes total unavailable while components stay visible`() {
        val result = FinancialAssessmentCalculator.assess(
            FinancialAssessmentInput(
                cashFlow = 0.0,
                savingsRate = 0.0,
                safetyMonths = 0.0,
                debtRatio = 0.0,
                netWorth = 0.0,
                totalAssets = 0.0,
                fidelityCapital = 0.0,
                fidelityTarget = 0.0,
                isAgeValid = true,
                cfs = 0.0,
                realReturnPercent = 0.0,
                monthlyIncome = 0.0,
                monthlyExpenses = 0.0,
                liabilities = 0.0
            )
        )
        assertNull(result.totalScore)
        assertNull(result.rawScore)
        assertNull(result.overallStatus)
        assertFalse(result.isAvailable)
        assertFalse(result.isComplete)
        assertEquals(FinancialAssessmentUnavailableReason.ALL_ZERO_INPUT, result.unavailableReason)
        assertEquals(15, result.coveragePercent)
        assertEquals(10, result.components.scoreOf(FinancialAssessmentId.DEBT_RATIO))
        assertEquals(5, result.components.scoreOf(FinancialAssessmentId.NET_WORTH))
        assertEquals(125.0 / 15.0, result[FinancialAssessmentGroup.DEBT].score!!, 1e-12)
        assertTrue(result[FinancialAssessmentGroup.DEBT].isAvailable)
        assertTrue(result.capReasons.isEmpty())
    }

    @Test
    fun `all zero check uses currency precision instead of raw double equality`() {
        val twoDigits = FinancialAssessmentAggregationInput(
            netWorth = 0.0,
            totalAssets = 0.4,
            liabilities = 0.4,
            monthlyIncome = 0.4,
            monthlyExpenses = 0.4,
            currencyFractionDigits = 2
        )
        assertFalse(twoDigits.isAllZero)
        assertFalse(twoDigits.isZeroAmount(0.4))
        assertTrue(twoDigits.isPositiveAmount(0.4))

        val zeroDigits = twoDigits.copy(currencyFractionDigits = 0)
        assertTrue(zeroDigits.isAllZero)
        assertTrue(zeroDigits.isZeroAmount(0.4))
        assertFalse(zeroDigits.isPositiveAmount(0.4))
    }

    @Test
    fun `all zero amounts at currency precision make total unavailable`() {
        val result = aggregate(
            groups(budget = 8, reserve = 8, debt = 8, capital = 8),
            FinancialAssessmentAggregationInput(
                netWorth = 0.0,
                totalAssets = 0.0,
                liabilities = 0.004,
                monthlyIncome = 0.004,
                monthlyExpenses = 0.004,
                currencyFractionDigits = 2
            )
        )
        assertNull(result.totalScore)
        assertNull(result.rawScore)
        assertNull(result.overallStatus)
        assertFalse(result.isAvailable)
        assertEquals(FinancialAssessmentUnavailableReason.ALL_ZERO_INPUT, result.unavailableReason)
    }

    @Test
    fun `assess carries currency precision from the source input`() {
        val source = FinancialInputData(
            birthDate = "1988-06-15",
            currencySymbol = "¥",
            monthlyIncome = 0.4,
            monthlyExpenses = 0.4,
            liquidAssets = 0.4,
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 0.4
        )
        val metrics = FinanceCalculator.calculate(source, today = LocalDate.of(2026, 11, 1))

        val aggregationInput = FinancialAssessmentAggregationInput.from(
            FinancialAssessmentInput.from(source, metrics)
        )
        assertEquals(0, aggregationInput.currencyFractionDigits)
        assertTrue(aggregationInput.isAllZero)

        val result = FinancialAssessmentCalculator.assess(source, metrics)
        assertNull(result.totalScore)
        assertEquals(FinancialAssessmentUnavailableReason.ALL_ZERO_INPUT, result.unavailableReason)
    }

    @Test
    fun `real return never changes total coverage cap or status`() {
        val reference = aggregate(groups(budget = 8, reserve = 6, debt = 10, capital = 4, realReturn = 0))
        for (realReturn in listOf(0, 2, 6, 10, null)) {
            val result = aggregate(
                groups(budget = 8, reserve = 6, debt = 10, capital = 4, realReturn = realReturn)
            )
            assertEquals(6.4, total(result), 0.0)
            assertEquals(reference.rawScore!!, result.rawScore!!, 0.0)
            assertEquals(reference.coveragePercent, result.coveragePercent)
            assertEquals(reference.capReasons, result.capReasons)
            assertEquals(reference.overallStatus, result.overallStatus)
            assertEquals(reference.isComplete, result.isComplete)
        }

        val cappedReference = aggregate(
            groups(budget = 8, reserve = 6, debt = 10, capital = 4, realReturn = 0),
            context(netWorth = -100.0, totalAssets = 1_000.0, liabilities = 1_100.0)
        )
        for (realReturn in listOf(0, 10, null)) {
            val result = aggregate(
                groups(budget = 8, reserve = 6, debt = 10, capital = 4, realReturn = realReturn),
                context(netWorth = -100.0, totalAssets = 1_000.0, liabilities = 1_100.0)
            )
            assertEquals(6.0, total(result), 0.0)
            assertEquals(cappedReference.capReasons, result.capReasons)
            assertEquals(cappedReference.coveragePercent, result.coveragePercent)
        }
    }

    @Test
    fun `negative net worth caps total once at six`() {
        val result = aggregate(
            groups(budget = 8, reserve = 8, debt = 8, capital = 8),
            context(netWorth = -100.0, totalAssets = 1_000.0, liabilities = 1_100.0)
        )
        assertEquals(8.0, result.rawScore!!, 0.0)
        assertEquals(6.0, total(result), 0.0)
        assertEquals(setOf(FinancialAssessmentCapReason.NEGATIVE_NET_WORTH), result.capReasons)
        assertTrue(result.isCapped)
        assertEquals(AuditIndicatorStatus.NEUTRAL, result.overallStatus)
        assertEquals(100, result.coveragePercent)
    }

    @Test
    fun `liabilities without assets caps total and records its reason`() {
        val consistent = aggregate(
            groups(budget = 8, reserve = 8, debt = 8, capital = 8),
            context(netWorth = -100.0, totalAssets = 0.0, liabilities = 100.0)
        )
        assertEquals(6.0, total(consistent), 0.0)
        assertEquals(
            setOf(
                FinancialAssessmentCapReason.NEGATIVE_NET_WORTH,
                FinancialAssessmentCapReason.LIABILITIES_WITHOUT_ASSETS
            ),
            consistent.capReasons
        )

        // Синтетическая комбинация изолирует второе условие cap.
        val liabilitiesOnly = aggregate(
            groups(budget = 8, reserve = 8, debt = 8, capital = 8),
            context(netWorth = 0.0, totalAssets = 0.0, liabilities = 100.0)
        )
        assertEquals(6.0, total(liabilitiesOnly), 0.0)
        assertEquals(
            setOf(FinancialAssessmentCapReason.LIABILITIES_WITHOUT_ASSETS),
            liabilitiesOnly.capReasons
        )
    }

    @Test
    fun `cap conditions use currency precision for debts without assets`() {
        val subUnitDebt = aggregate(
            groups(budget = 8, reserve = 8, debt = 8, capital = 8),
            context(
                netWorth = 0.0,
                totalAssets = 0.0,
                liabilities = 0.004,
                monthlyIncome = 1_000.0,
                monthlyExpenses = 500.0
            )
        )
        assertEquals(8.0, total(subUnitDebt), 0.0)
        assertTrue(subUnitDebt.capReasons.isEmpty())

        val halfUnitDebt = aggregate(
            groups(budget = 8, reserve = 8, debt = 8, capital = 8),
            context(
                netWorth = 0.0,
                totalAssets = 0.0,
                liabilities = 0.005,
                monthlyIncome = 1_000.0,
                monthlyExpenses = 500.0
            )
        )
        assertEquals(6.0, total(halfUnitDebt), 0.0)
        assertEquals(
            setOf(FinancialAssessmentCapReason.LIABILITIES_WITHOUT_ASSETS),
            halfUnitDebt.capReasons
        )
    }

    @Test
    fun `cap is not applied when raw is at or below six`() {
        val exactlySix = aggregate(
            groups(budget = 6, reserve = 6, debt = 6, capital = 6),
            context(netWorth = -100.0, totalAssets = 1_000.0, liabilities = 1_100.0)
        )
        assertEquals(6.0, exactlySix.rawScore!!, 0.0)
        assertEquals(6.0, total(exactlySix), 0.0)
        assertTrue(exactlySix.capReasons.isEmpty())
        assertFalse(exactlySix.isCapped)

        val belowSix = aggregate(
            groups(budget = 8, reserve = 7, debt = 3, capital = 5),
            context(netWorth = -100.0, totalAssets = 1_000.0, liabilities = 1_100.0)
        )
        assertEquals(5.95, belowSix.rawScore!!, 1e-12)
        assertEquals(6.0, total(belowSix), 0.0)
        assertTrue(belowSix.capReasons.isEmpty())
    }

    @Test
    fun `cap is applied before half up rounding`() {
        val cases = listOf(
            groups(budget = 8, reserve = 7, debt = 4, capital = 6) to 6.45,
            groups(budget = 10, reserve = 8, debt = 8, capital = 1) to 6.05
        )
        for ((components, expectedRaw) in cases) {
            val result = aggregate(
                components,
                context(netWorth = -100.0, totalAssets = 1_000.0, liabilities = 1_100.0)
            )
            assertEquals(expectedRaw, result.rawScore!!, 1e-12)
            assertEquals(6.0, total(result), 0.0)
            assertEquals(setOf(FinancialAssessmentCapReason.NEGATIVE_NET_WORTH), result.capReasons)
        }
    }

    @Test
    fun `low cfs alone does not cap the total`() {
        val result = aggregate(
            components(
                cashFlow = 10,
                savingsRate = 10,
                emergencyFund = 10,
                safetyMonths = 10,
                debtRatio = 10,
                netWorth = 10,
                fidelity = 10,
                cfs = 0
            )
        )
        assertEquals(0, result.components.scoreOf(FinancialAssessmentId.CFS))
        assertEquals(7.5, total(result), 0.0)
        assertTrue(result.capReasons.isEmpty())
        assertFalse(result.isCapped)
    }

    @Test
    fun `half up rounding uses exact decimal boundaries`() {
        val cases = listOf(
            Triple(groups(budget = 4, reserve = 4, debt = 5, capital = 2), 3.45, 3.5),
            Triple(groups(budget = 7, reserve = 7, debt = 2, capital = 9), 6.95, 7.0),
            Triple(groups(budget = 8, reserve = 7, debt = 8, capital = 10), 8.45, 8.5),
            Triple(groups(budget = 10, reserve = 8, debt = 8, capital = 1), 6.05, 6.1),
            Triple(groups(budget = 8, reserve = 7, debt = 4, capital = 6), 6.45, 6.5)
        )
        for ((components, expectedRaw, expectedTotal) in cases) {
            val result = aggregate(components)
            assertEquals(expectedRaw, result.rawScore!!, 1e-12)
            assertEquals(expectedTotal, total(result), 0.0)
            assertTrue(result.capReasons.isEmpty())
        }
    }

    @Test
    fun `overall status uses rounded score at 3_5 7_0 and 8_5`() {
        val belowYellow = aggregate(groups(budget = 4, reserve = 5, debt = 3, capital = 2))
        assertEquals(3.4, total(belowYellow), 0.0)
        assertEquals(AuditIndicatorStatus.NEGATIVE, belowYellow.overallStatus)
        assertFalse(belowYellow.isExcellent)

        val roundedYellow = aggregate(groups(budget = 4, reserve = 4, debt = 5, capital = 2))
        assertEquals(3.5, total(roundedYellow), 0.0)
        assertEquals(AuditIndicatorStatus.NEUTRAL, roundedYellow.overallStatus)

        val exactlyGreen = aggregate(groups(budget = 7, reserve = 7, debt = 7, capital = 7))
        assertEquals(7.0, total(exactlyGreen), 0.0)
        assertEquals(AuditIndicatorStatus.POSITIVE, exactlyGreen.overallStatus)
        assertFalse(exactlyGreen.isExcellent)

        val roundedGreen = aggregate(groups(budget = 7, reserve = 7, debt = 2, capital = 9))
        assertEquals(7.0, total(roundedGreen), 0.0)
        assertEquals(AuditIndicatorStatus.POSITIVE, roundedGreen.overallStatus)

        val belowExcellent = aggregate(groups(budget = 8, reserve = 8, debt = 6, capital = 10))
        assertEquals(8.4, total(belowExcellent), 0.0)
        assertEquals(AuditIndicatorStatus.POSITIVE, belowExcellent.overallStatus)
        assertFalse(belowExcellent.isExcellent)

        val roundedExcellent = aggregate(groups(budget = 8, reserve = 7, debt = 8, capital = 10))
        assertEquals(8.5, total(roundedExcellent), 0.0)
        assertEquals(AuditIndicatorStatus.POSITIVE, roundedExcellent.overallStatus)
        assertTrue(roundedExcellent.isExcellent)
    }

    @Test
    fun `group scores are not rounded before aggregation`() {
        val result = aggregate(
            components(
                cashFlow = 8,
                savingsRate = 8,
                emergencyFund = 6,
                safetyMonths = 6,
                debtRatio = 8,
                netWorth = 7,
                fidelity = 4,
                cfs = 4
            )
        )
        assertEquals(23.0 / 3.0, result[FinancialAssessmentGroup.DEBT].score!!, 1e-12)
        assertNotEquals(7.7, result[FinancialAssessmentGroup.DEBT].score!!, 1e-12)
        assertEquals(6.05, result.rawScore!!, 1e-12)
        assertEquals(6.1, total(result), 0.0)
    }

    @Test
    fun `assess uses calculated components and leaves legacy total untouched`() {
        val source = FinancialInputData(
            birthDate = "1988-06-15",
            monthlyIncome = 6_000.0,
            monthlyExpenses = 3_000.0,
            liquidAssets = 15_000.0,
            financialAssets = 40_000.0,
            propertyAssets = 280_000.0,
            liabilities = 180_000.0
        )
        val metrics = FinanceCalculator.calculate(source, today = LocalDate.of(2026, 11, 1))
        val legacyTotal = metrics.scores.totalScore

        val result = FinancialAssessmentCalculator.assess(source, metrics)

        assertEquals(FinancialAssessmentCalculator.calculate(source, metrics), result.components)
        assertEquals(legacyTotal, metrics.scores.totalScore, 0.0)
        assertTrue(result.isAvailable)
        assertEquals(100, result.coveragePercent)
        assertTrue(result.isComplete)
    }
}
