package com.example

import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialAssessmentCalculator
import com.example.domain.model.AuditIndicatorStatus
import com.example.domain.model.FinancialAssessmentCapReason
import com.example.domain.model.FinancialAssessmentGroup
import com.example.domain.model.FinancialAssessmentId
import com.example.domain.model.FinancialAssessmentUnavailableReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Профильная проверка утверждённой методики комплексной оценки (FA-01, реализация FA-03).
 *
 * Все входные данные вымышленные, валюта одна (EUR, символ €), дата фиксирована.
 * Ожидаемые значения рассчитаны независимо от тестируемого калькулятора (отдельный
 * ручной/скриптовый расчёт по спецификации) и зафиксированы здесь как baseline.
 * Исторические значения старого итога 1.0.3 и качественные пояснения находятся в
 * docs/financial-assessment-validation.md; тесты намеренно не зависят от старого
 * калькулятора, чтобы продолжать работать после переключения итогового балла.
 */
class FinancialAssessmentProfilesTest {

    private val fixedToday = LocalDate.of(2026, 11, 25)

    private data class ExpectedBase(
        val cashFlow: Double,
        val savingsRate: Double,
        val totalAssets: Double,
        val emergencyFund: Double,
        val safetyMonths: Double,
        val debtRatio: Double,
        val netWorth: Double,
        val fidelityCapital: Double,
        val fidelityTarget: Double,
        val cfs: Double,
        val realReturnPercent: Double
    )

    private data class ExpectedAssessment(
        /** Девять баллов в порядке [com.example.domain.model.FinancialAssessmentId]. */
        val scores: List<Int?>,
        /** Четыре группы в порядке [FinancialAssessmentGroup]. */
        val groups: List<Double?>,
        val rawScore: Double?,
        val totalScore: Double?,
        val coverage: Int,
        val capReasons: Set<FinancialAssessmentCapReason> = emptySet(),
        val unavailableReason: FinancialAssessmentUnavailableReason? = null,
        val status: AuditIndicatorStatus?
    )

    private data class Profile(
        val id: String,
        val input: FinancialInputData,
        val expectedBase: ExpectedBase,
        val expected: ExpectedAssessment,
        val fireStatus: FireStatus
    )

    private fun input(
        age: Int? = null,
        birthDate: String? = null,
        income: Double,
        expenses: Double,
        liquid: Double,
        financial: Double,
        property: Double,
        liabilities: Double,
        nominalReturnPercent: Double = 7.5,
        includeFinancialInEmergencyFund: Boolean = true
    ): FinancialInputData {
        val resolvedBirthDate = birthDate
            ?: fixedToday.minusYears(requireNotNull(age) { "age or birthDate is required" }.toLong()).toString()
        return FinancialInputData(
            monthlyIncome = income,
            monthlyExpenses = expenses,
            liquidAssets = liquid,
            financialAssets = financial,
            propertyAssets = property,
            liabilities = liabilities,
            birthDate = resolvedBirthDate,
            nominalReturnPercent = nominalReturnPercent,
            inflationPercent = 2.5,
            currencySymbol = "€",
            includeFinancialInEmergencyFund = includeFinancialInEmergencyFund
        )
    }

    private fun expectedBase(
        cashFlow: Double,
        savingsRate: Double,
        totalAssets: Double,
        emergencyFund: Double,
        safetyMonths: Double,
        debtRatio: Double,
        netWorth: Double,
        fidelityCapital: Double,
        fidelityTarget: Double,
        cfs: Double,
        realReturnPercent: Double = 4.878048780487809
    ) = ExpectedBase(
        cashFlow = cashFlow,
        savingsRate = savingsRate,
        totalAssets = totalAssets,
        emergencyFund = emergencyFund,
        safetyMonths = safetyMonths,
        debtRatio = debtRatio,
        netWorth = netWorth,
        fidelityCapital = fidelityCapital,
        fidelityTarget = fidelityTarget,
        cfs = cfs,
        realReturnPercent = realReturnPercent
    )

    private val average45ExpectedAssessment = ExpectedAssessment(
        scores = listOf(9, 8, 10, 10, 10, 10, 6, 8, 10),
        groups = listOf(8.2, 10.0, 10.0, 260.0 / 35.0),
        rawScore = 8.65,
        totalScore = 8.7,
        coverage = 100,
        status = AuditIndicatorStatus.POSITIVE
    )

    private val average45ExpectedBase = expectedBase(
        cashFlow = 1_000.0,
        savingsRate = 33.33333333333333,
        totalAssets = 140_000.0,
        emergencyFund = 140_000.0,
        safetyMonths = 70.0,
        debtRatio = 0.0,
        netWorth = 140_000.0,
        fidelityCapital = 140_000.0,
        fidelityTarget = 162_000.0,
        cfs = 5.833333333333333
    )

    private fun average45(
        id: String = "average45",
        nominalReturnPercent: Double = 7.5,
        birthDate: String? = null,
        fireStatus: FireStatus = FireStatus.REACHABLE
    ): Profile = Profile(
        id = id,
        input = input(
            age = 45,
            birthDate = birthDate,
            income = 3_000.0,
            expenses = 2_000.0,
            liquid = 20_000.0,
            financial = 120_000.0,
            property = 0.0,
            liabilities = 0.0,
            nominalReturnPercent = nominalReturnPercent
        ),
        expectedBase = average45ExpectedBase.copy(
            fidelityTarget = if (birthDate == null) 162_000.0 else 0.0,
            realReturnPercent = when (nominalReturnPercent) {
                20.0 -> 17.073170731707332
                80.0 -> 75.609756097561
                else -> 4.878048780487809
            }
        ),
        expected = average45ExpectedAssessment,
        fireStatus = fireStatus
    )

    private fun profiles(): List<Profile> = listOf(
        Profile(
            id = "young25",
            input = input(age = 25, income = 3_000.0, expenses = 1_500.0, liquid = 9_000.0, financial = 3_000.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = 1_500.0,
                savingsRate = 50.0,
                totalAssets = 12_000.0,
                emergencyFund = 12_000.0,
                safetyMonths = 8.0,
                debtRatio = 0.0,
                netWorth = 12_000.0,
                fidelityCapital = 12_000.0,
                fidelityTarget = 18_000.0,
                cfs = 0.6666666666666666
            ),
            expected = ExpectedAssessment(
                scores = listOf(10, 10, 9, 9, 10, 10, 4, 4, 10),
                groups = listOf(10.0, 9.0, 10.0, 4.0),
                rawScore = 7.65,
                totalScore = 7.7,
                coverage = 100,
                status = AuditIndicatorStatus.POSITIVE
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        Profile(
            id = "pensioner70",
            input = input(age = 70, income = 0.0, expenses = 2_000.0, liquid = 60_000.0, financial = 600_000.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = -2_000.0,
                savingsRate = -100.0,
                totalAssets = 660_000.0,
                emergencyFund = 660_000.0,
                safetyMonths = 330.0,
                debtRatio = 0.0,
                netWorth = 660_000.0,
                fidelityCapital = 660_000.0,
                fidelityTarget = 0.0,
                cfs = 27.5
            ),
            expected = ExpectedAssessment(
                scores = listOf(0, 0, 10, 10, 10, 10, null, 9, 10),
                groups = listOf(0.0, 10.0, 10.0, 9.0),
                rawScore = 7.15,
                totalScore = 7.2,
                coverage = 90,
                status = AuditIndicatorStatus.POSITIVE
            ),
            fireStatus = FireStatus.REACHED_NOW
        ),
        Profile(
            id = "mortgage40",
            input = input(age = 40, income = 5_000.0, expenses = 3_500.0, liquid = 15_000.0, financial = 30_000.0, property = 250_000.0, liabilities = 200_000.0),
            expectedBase = expectedBase(
                cashFlow = 1_500.0,
                savingsRate = 30.0,
                totalAssets = 295_000.0,
                emergencyFund = 45_000.0,
                safetyMonths = 12.857142857142858,
                debtRatio = 0.6779661016949152,
                netWorth = 95_000.0,
                fidelityCapital = 45_000.0,
                fidelityTarget = 180_000.0,
                cfs = 2.261904761904762
            ),
            expected = ExpectedAssessment(
                scores = listOf(9, 8, 10, 10, 4, 7, 3, 6, 10),
                groups = listOf(8.2, 10.0, 5.0, 180.0 / 35.0),
                rawScore = 7.1,
                totalScore = 7.1,
                coverage = 100,
                status = AuditIndicatorStatus.POSITIVE
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        Profile(
            id = "property50",
            input = input(age = 50, income = 3_000.0, expenses = 2_900.0, liquid = 0.0, financial = 0.0, property = 700_000.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = 100.0,
                savingsRate = 3.3333333333333335,
                totalAssets = 700_000.0,
                emergencyFund = 0.0,
                safetyMonths = 0.0,
                debtRatio = 0.0,
                netWorth = 700_000.0,
                fidelityCapital = 0.0,
                fidelityTarget = 216_000.0,
                cfs = 20.114942528735632
            ),
            expected = ExpectedAssessment(
                scores = listOf(8, 5, 0, 0, 10, 10, 0, 9, 10),
                groups = listOf(5.6, 0.0, 10.0, 225.0 / 35.0),
                rawScore = 5.15,
                totalScore = 5.2,
                coverage = 100,
                status = AuditIndicatorStatus.NEUTRAL
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        Profile(
            id = "highIncome40",
            input = input(age = 40, income = 15_000.0, expenses = 14_900.0, liquid = 5_000.0, financial = 10_000.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = 100.0,
                savingsRate = 0.6666666666666667,
                totalAssets = 15_000.0,
                emergencyFund = 15_000.0,
                safetyMonths = 1.0067114093959733,
                debtRatio = 0.0,
                netWorth = 15_000.0,
                fidelityCapital = 15_000.0,
                fidelityTarget = 540_000.0,
                cfs = 0.08389261744966443
            ),
            expected = ExpectedAssessment(
                scores = listOf(8, 5, 4, 4, 10, 10, 2, 2, 10),
                groups = listOf(5.6, 4.0, 10.0, 2.0),
                rawScore = 4.6,
                totalScore = 4.6,
                coverage = 100,
                status = AuditIndicatorStatus.NEUTRAL
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        average45(),
        Profile(
            id = "noAssets30",
            input = input(age = 30, income = 2_000.0, expenses = 1_800.0, liquid = 0.0, financial = 0.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = 200.0,
                savingsRate = 10.0,
                totalAssets = 0.0,
                emergencyFund = 0.0,
                safetyMonths = 0.0,
                debtRatio = 0.0,
                netWorth = 0.0,
                fidelityCapital = 0.0,
                fidelityTarget = 24_000.0,
                cfs = 0.0
            ),
            expected = ExpectedAssessment(
                scores = listOf(8, 6, 0, 0, 10, 5, 0, 2, 10),
                groups = listOf(6.4, 0.0, 125.0 / 15.0, 50.0 / 35.0),
                rawScore = 3.35,
                totalScore = 3.4,
                coverage = 100,
                status = AuditIndicatorStatus.NEGATIVE
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        Profile(
            id = "negativeNetWorth40",
            input = input(age = 40, income = 3_000.0, expenses = 2_000.0, liquid = 2_000.0, financial = 0.0, property = 10_000.0, liabilities = 30_000.0),
            expectedBase = expectedBase(
                cashFlow = 1_000.0,
                savingsRate = 33.33333333333333,
                totalAssets = 12_000.0,
                emergencyFund = 2_000.0,
                safetyMonths = 1.0,
                debtRatio = 2.5,
                netWorth = -18_000.0,
                fidelityCapital = -18_000.0,
                fidelityTarget = 108_000.0,
                cfs = -0.75
            ),
            expected = ExpectedAssessment(
                scores = listOf(9, 8, 4, 4, 0, 0, 0, 0, 10),
                groups = listOf(8.2, 4.0, 0.0, 0.0),
                rawScore = 3.05,
                totalScore = 3.1,
                coverage = 100,
                status = AuditIndicatorStatus.NEGATIVE
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        Profile(
            id = "debtsWithoutAssets40",
            input = input(age = 40, income = 3_000.0, expenses = 2_000.0, liquid = 0.0, financial = 0.0, property = 0.0, liabilities = 30_000.0),
            expectedBase = expectedBase(
                cashFlow = 1_000.0,
                savingsRate = 33.33333333333333,
                totalAssets = 0.0,
                emergencyFund = 0.0,
                safetyMonths = 0.0,
                debtRatio = Double.POSITIVE_INFINITY,
                netWorth = -30_000.0,
                fidelityCapital = -30_000.0,
                fidelityTarget = 108_000.0,
                cfs = -1.25
            ),
            expected = ExpectedAssessment(
                scores = listOf(9, 8, 0, 0, 0, 0, 0, 0, 10),
                groups = listOf(8.2, 0.0, 0.0, 0.0),
                rawScore = 2.05,
                totalScore = 2.1,
                coverage = 100,
                status = AuditIndicatorStatus.NEGATIVE
            ),
            fireStatus = FireStatus.REACHABLE
        ),
        Profile(
            id = "fire55",
            input = input(age = 55, income = 1_000.0, expenses = 2_000.0, liquid = 100_000.0, financial = 900_000.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = -1_000.0,
                savingsRate = -100.0,
                totalAssets = 1_000_000.0,
                emergencyFund = 1_000_000.0,
                safetyMonths = 500.0,
                debtRatio = 0.0,
                netWorth = 1_000_000.0,
                fidelityCapital = 1_000_000.0,
                fidelityTarget = 84_000.0,
                cfs = 41.666666666666664
            ),
            expected = ExpectedAssessment(
                scores = listOf(0, 0, 10, 10, 10, 10, 10, 10, 10),
                groups = listOf(0.0, 10.0, 10.0, 10.0),
                rawScore = 7.5,
                totalScore = 7.5,
                coverage = 100,
                status = AuditIndicatorStatus.POSITIVE
            ),
            fireStatus = FireStatus.REACHED_NOW
        ),
        Profile(
            id = "zeroExpenses35",
            input = input(age = 35, income = 3_000.0, expenses = 0.0, liquid = 10_000.0, financial = 20_000.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = 3_000.0,
                savingsRate = 100.0,
                totalAssets = 30_000.0,
                emergencyFund = 30_000.0,
                safetyMonths = 12.0,
                debtRatio = 0.0,
                netWorth = 30_000.0,
                fidelityCapital = 30_000.0,
                fidelityTarget = 72_000.0,
                cfs = 30.0
            ),
            expected = ExpectedAssessment(
                scores = listOf(10, 10, null, null, 10, 10, 4, null, 10),
                groups = listOf(10.0, null, 10.0, 4.0),
                rawScore = 7.2,
                totalScore = 7.2,
                coverage = 50,
                status = AuditIndicatorStatus.POSITIVE
            ),
            fireStatus = FireStatus.REACHED_NOW
        ),
        Profile(
            id = "allZero35",
            input = input(age = 35, income = 0.0, expenses = 0.0, liquid = 0.0, financial = 0.0, property = 0.0, liabilities = 0.0),
            expectedBase = expectedBase(
                cashFlow = 0.0,
                savingsRate = 0.0,
                totalAssets = 0.0,
                emergencyFund = 0.0,
                safetyMonths = 0.0,
                debtRatio = 0.0,
                netWorth = 0.0,
                fidelityCapital = 0.0,
                fidelityTarget = 0.0,
                cfs = 0.0
            ),
            expected = ExpectedAssessment(
                scores = listOf(null, null, null, null, 10, 5, null, null, 10),
                groups = listOf(null, null, 125.0 / 15.0, null),
                rawScore = null,
                totalScore = null,
                coverage = 15,
                unavailableReason = FinancialAssessmentUnavailableReason.ALL_ZERO_INPUT,
                status = null
            ),
            fireStatus = FireStatus.REACHED_NOW
        ),
        average45(
            id = "invalidBirthDate45",
            birthDate = "1981-02-30",
            fireStatus = FireStatus.INVALID_AGE
        ).let { profile ->
            profile.copy(
                expected = profile.expected.copy(
                    scores = listOf(9, 8, 10, 10, 10, 10, null, 8, 10),
                    groups = listOf(8.2, 10.0, 10.0, 8.0),
                    rawScore = 8.85,
                    totalScore = 8.9,
                    coverage = 90
                )
            )
        },
        average45(id = "average45_return20", nominalReturnPercent = 20.0),
        average45(
            id = "average45_return80",
            nominalReturnPercent = 80.0,
            fireStatus = FireStatus.REACHED_NOW
        ),
        Profile(
            id = "mortgage40_liquidOnlyReserve",
            input = input(
                age = 40,
                income = 5_000.0,
                expenses = 3_500.0,
                liquid = 15_000.0,
                financial = 30_000.0,
                property = 250_000.0,
                liabilities = 200_000.0,
                includeFinancialInEmergencyFund = false
            ),
            expectedBase = expectedBase(
                cashFlow = 1_500.0,
                savingsRate = 30.0,
                totalAssets = 295_000.0,
                emergencyFund = 15_000.0,
                safetyMonths = 4.285714285714286,
                debtRatio = 0.6779661016949152,
                netWorth = 95_000.0,
                fidelityCapital = 45_000.0,
                fidelityTarget = 180_000.0,
                cfs = 2.261904761904762
            ),
            expected = ExpectedAssessment(
                scores = listOf(9, 8, 7, 7, 4, 7, 3, 6, 10),
                groups = listOf(8.2, 7.0, 5.0, 180.0 / 35.0),
                rawScore = 6.35,
                totalScore = 6.4,
                coverage = 100,
                status = AuditIndicatorStatus.NEUTRAL
            ),
            fireStatus = FireStatus.REACHABLE
        )
    )

    private fun assertProfile(profile: Profile) {
        val label = profile.id
        val metrics = FinanceCalculator.calculate(profile.input, today = fixedToday)
        val base = profile.expectedBase

        assertEquals("$label cashFlow", base.cashFlow, metrics.cashFlow, 1e-9)
        assertEquals("$label savingsRate", base.savingsRate, metrics.savingsRate, 1e-9)
        assertEquals("$label totalAssets", base.totalAssets, metrics.totalAssets, 1e-9)
        assertEquals("$label emergencyFund", base.emergencyFund, metrics.emergencyFund, 1e-9)
        assertEquals("$label safetyMonths", base.safetyMonths, metrics.safetyMonths, 1e-9)
        assertEquals("$label debtRatio", base.debtRatio, metrics.debtRatio, 1e-9)
        assertEquals("$label netWorth", base.netWorth, metrics.netWorth, 1e-9)
        assertEquals("$label fidelityCapital", base.fidelityCapital, metrics.fidelityCapital, 1e-9)
        assertEquals("$label fidelityTarget", base.fidelityTarget, metrics.fidelityTarget, 1e-9)
        assertEquals("$label cfs", base.cfs, metrics.financialFreedomRatio, 1e-9)
        assertEquals("$label realReturnPercent", base.realReturnPercent, metrics.realReturnPercent, 1e-9)
        assertEquals("$label fireStatus", profile.fireStatus, metrics.fireStatus)

        val result = FinancialAssessmentCalculator.assess(profile.input, metrics)
        val expected = profile.expected

        assertEquals("$label component scores", expected.scores, result.components.components.map { it.score })
        for ((index, group) in FinancialAssessmentGroup.values().withIndex()) {
            val expectedGroup = expected.groups[index]
            val actualGroup = result[group].score
            if (expectedGroup == null) {
                assertNull("$label $group", actualGroup)
            } else {
                assertEquals("$label $group", expectedGroup, requireNotNull(actualGroup), 1e-12)
            }
        }

        val expectedRaw = expected.rawScore
        val expectedTotal = expected.totalScore
        if (expectedRaw == null) {
            assertNull("$label rawScore", result.rawScore)
        } else {
            assertEquals("$label rawScore", expectedRaw, requireNotNull(result.rawScore), 1e-12)
        }
        if (expectedTotal == null) {
            assertNull("$label totalScore", result.totalScore)
        } else {
            assertEquals("$label totalScore", expectedTotal, requireNotNull(result.totalScore), 0.0)
        }
        assertEquals("$label coverage", expected.coverage, result.coveragePercent)
        assertEquals("$label capReasons", expected.capReasons, result.capReasons)
        assertEquals("$label unavailableReason", expected.unavailableReason, result.unavailableReason)
        assertEquals("$label overallStatus", expected.status, result.overallStatus)
        assertEquals("$label isAvailable", expected.totalScore != null, result.isAvailable)
        assertEquals("$label isComplete", expected.coverage == 100, result.isComplete)
        assertEquals("$label isCapped", expected.capReasons.isNotEmpty(), result.isCapped)
    }

    @Test
    fun `financial profiles match the independently calculated assessment baseline`() {
        val profiles = profiles()
        assertEquals(16, profiles.size)
        for (profile in profiles) {
            assertProfile(profile)
        }
    }

    @Test
    fun `all profiles with available total have full component catalog in order`() {
        for (profile in profiles()) {
            val metrics = FinanceCalculator.calculate(profile.input, today = fixedToday)
            val result = FinancialAssessmentCalculator.assess(profile.input, metrics)
            assertEquals(
                "${profile.id} catalog",
                FinancialAssessmentId.values().toList(),
                result.components.components.map { it.id }
            )
            assertEquals(
                "${profile.id} group catalog",
                FinancialAssessmentGroup.values().toList(),
                result.groups.map { it.group }
            )
            for (component in result.components.components) {
                val score = component.score
                assertTrue(
                    "${profile.id} ${component.id.stableId} score in range",
                    score == null || score in 0..10
                )
            }
        }
    }

    @Test
    fun `invalid birth date keeps fidelity unavailable and reduces coverage`() {
        val profile = profiles().single { it.id == "invalidBirthDate45" }
        val metrics = FinanceCalculator.calculate(profile.input, today = fixedToday)
        val result = FinancialAssessmentCalculator.assess(profile.input, metrics)

        assertNull("Fidelity is unavailable without a valid age", result.components.components
            .single { it.id.stableId == "fidelity" }.score)
        assertEquals(90, result.coveragePercent)
        assertTrue(result.isIncomplete)
        assertEquals(8.9, requireNotNull(result.totalScore), 0.0)
        assertEquals(FireStatus.INVALID_AGE, metrics.fireStatus)
    }

    @Test
    fun `all zero input has no total instead of a substituted zero`() {
        val profile = profiles().single { it.id == "allZero35" }
        val metrics = FinanceCalculator.calculate(profile.input, today = fixedToday)
        val result = FinancialAssessmentCalculator.assess(profile.input, metrics)

        assertNull(result.totalScore)
        assertNull(result.rawScore)
        assertNull(result.overallStatus)
        assertFalse(result.isAvailable)
        assertEquals(FinancialAssessmentUnavailableReason.ALL_ZERO_INPUT, result.unavailableReason)
        assertEquals(15, result.coveragePercent)
        assertEquals(10, result.components.components.single { it.id.stableId == "debt_ratio" }.score)
        assertEquals(5, result.components.components.single { it.id.stableId == "net_worth" }.score)
    }

    @Test
    fun `forecast return leaves the total unchanged while FIRE may change`() {
        val base = profiles().single { it.id == "average45" }
        val high = profiles().single { it.id == "average45_return20" }
        val extreme = profiles().single { it.id == "average45_return80" }

        val baseResult = FinancialAssessmentCalculator.assess(
            base.input,
            FinanceCalculator.calculate(base.input, today = fixedToday)
        )
        val highResult = FinancialAssessmentCalculator.assess(
            high.input,
            FinanceCalculator.calculate(high.input, today = fixedToday)
        )
        val extremeResult = FinancialAssessmentCalculator.assess(
            extreme.input,
            FinanceCalculator.calculate(extreme.input, today = fixedToday)
        )

        assertEquals(8.7, requireNotNull(baseResult.totalScore), 0.0)
        assertEquals(baseResult.totalScore, highResult.totalScore)
        assertEquals(baseResult.totalScore, extremeResult.totalScore)
        assertEquals(baseResult.coveragePercent, highResult.coveragePercent)
        assertEquals(baseResult.coveragePercent, extremeResult.coveragePercent)
        assertEquals(baseResult.groups, highResult.groups)
        assertEquals(baseResult.groups, extremeResult.groups)

        assertEquals(10, baseResult.components.components.single { it.id.stableId == "real_return" }.score)
        assertEquals(10, highResult.components.components.single { it.id.stableId == "real_return" }.score)
        assertEquals(10, extremeResult.components.components.single { it.id.stableId == "real_return" }.score)

        assertEquals(FireStatus.REACHABLE, FinanceCalculator.calculate(high.input, today = fixedToday).fireStatus)
        assertEquals(FireStatus.REACHED_NOW, FinanceCalculator.calculate(extreme.input, today = fixedToday).fireStatus)
    }

    @Test
    fun `excluding financial assets from the emergency fund changes only the reserve group`() {
        val included = profiles().single { it.id == "mortgage40" }
        val liquidOnly = profiles().single { it.id == "mortgage40_liquidOnlyReserve" }

        val includedResult = FinancialAssessmentCalculator.assess(
            included.input,
            FinanceCalculator.calculate(included.input, today = fixedToday)
        )
        val liquidOnlyResult = FinancialAssessmentCalculator.assess(
            liquidOnly.input,
            FinanceCalculator.calculate(liquidOnly.input, today = fixedToday)
        )

        assertEquals(10, includedResult.components.components.single { it.id.stableId == "safety_months" }.score)
        assertEquals(7, liquidOnlyResult.components.components.single { it.id.stableId == "safety_months" }.score)
        assertEquals(10.0, requireNotNull(includedResult[FinancialAssessmentGroup.RESERVE].score), 1e-12)
        assertEquals(7.0, requireNotNull(liquidOnlyResult[FinancialAssessmentGroup.RESERVE].score), 1e-12)
        assertEquals(7.1, requireNotNull(includedResult.totalScore), 0.0)
        assertEquals(6.4, requireNotNull(liquidOnlyResult.totalScore), 0.0)
        assertEquals(
            includedResult[FinancialAssessmentGroup.BUDGET],
            liquidOnlyResult[FinancialAssessmentGroup.BUDGET]
        )
        assertEquals(
            includedResult[FinancialAssessmentGroup.DEBT],
            liquidOnlyResult[FinancialAssessmentGroup.DEBT]
        )
        assertEquals(
            includedResult[FinancialAssessmentGroup.CAPITAL],
            liquidOnlyResult[FinancialAssessmentGroup.CAPITAL]
        )
    }

    @Test
    fun `fire profile is reached by the projection model and keeps a moderate assessment`() {
        val profile = profiles().single { it.id == "fire55" }
        val metrics = FinanceCalculator.calculate(profile.input, today = fixedToday)
        val result = FinancialAssessmentCalculator.assess(profile.input, metrics)

        assertEquals(FireStatus.REACHED_NOW, metrics.fireStatus)
        assertEquals(0.0, metrics.yearsToRetirement, 0.0)
        assertEquals(7.5, requireNotNull(result.totalScore), 0.0)
        assertEquals(0, result.components.components.single { it.id.stableId == "cash_flow" }.score)
        assertEquals(10, result.components.components.single { it.id.stableId == "cfs" }.score)
    }

    @Test
    fun `negative net worth and debts without assets meet cap conditions but stay below the cap`() {
        for (id in listOf("negativeNetWorth40", "debtsWithoutAssets40")) {
            val profile = profiles().single { it.id == id }
            val metrics = FinanceCalculator.calculate(profile.input, today = fixedToday)
            val result = FinancialAssessmentCalculator.assess(profile.input, metrics)

            assertTrue("$id raw is below the cap", requireNotNull(result.rawScore) < 6.0)
            assertTrue("$id cap does not change the score", result.capReasons.isEmpty())
            assertFalse("$id is not capped", result.isCapped)
        }
    }
}
