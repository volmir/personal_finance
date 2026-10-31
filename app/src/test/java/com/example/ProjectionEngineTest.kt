package com.example

import com.example.domain.ProjectionEngine
import com.example.domain.ProjectionParams
import com.example.domain.TemporaryExpense
import com.example.data.model.FireStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectionEngineTest {

    @Test
    fun testBaseScenario() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = 0.05,
            financialAssets = 300_000.0,
            liquidAssets = 200_000.0,
            capitalGainsTaxPercent = 0.0
        )
        val engine = ProjectionEngine(params)
        val years = engine.findYearsToRetirement().years

        // 1. Точное значение срока до пенсии из текущей версии
        assertEquals(11.29, years, 0.01)

        val reqAtRetire = engine.requiredCapitalAt(years)
        val accAtRetire = engine.accumulatedCapitalAt(years)

        // Накопленный капитал в точке FIRE покрывает требуемый целевой капитал
        assertEquals(9_683_192.37, reqAtRetire, 10.0)
        assertEquals(9_683_890.20, accAtRetire, 10.0)
        assertTrue(accAtRetire >= reqAtRetire)

        val trajectory = engine.buildTrajectory(years, effectiveTaxRate = 0.0)
        assertEquals(46, trajectory.size) // возраст от 35 до 80 включительно
        assertEquals(35.0, trajectory.first().age, 0.001)
        assertEquals(80.0, trajectory.last().age, 0.001)
    }

    @Test
    fun testNegativeRealReturn() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = -0.04,
            financialAssets = 300_000.0,
            liquidAssets = 200_000.0,
            capitalGainsTaxPercent = 0.0
        )
        val engine = ProjectionEngine(params)
        val years = engine.findYearsToRetirement().years

        // 2. Отрицательная реальная доходность (инфляция опережает доходность капитала)
        assertEquals(31.53, years, 0.01)

        val reqAtRetire = engine.requiredCapitalAt(years)
        val accAtRetire = engine.accumulatedCapitalAt(years)
        assertTrue(reqAtRetire > 0.0)
        assertTrue(accAtRetire >= reqAtRetire)
    }

    @Test
    fun testTemporaryExpense() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = 0.05,
            temporaryExpenses = listOf(
                TemporaryExpense(annualAmount = 120_000.0, durationYears = 10.0)
            ),
            financialAssets = 300_000.0,
            liquidAssets = 200_000.0,
            capitalGainsTaxPercent = 0.0
        )
        val engine = ProjectionEngine(params)
        val years = engine.findYearsToRetirement().years

        // 3. Временные расходы (например, ипотека/автокредит на 10 лет с завершением выплат)
        assertEquals(9.58, years, 0.01)

        val reqAtRetire = engine.requiredCapitalAt(years)
        val accAtRetire = engine.accumulatedCapitalAt(years)
        assertTrue(accAtRetire >= reqAtRetire)
    }

    @Test
    fun testDeferredExpenseReduction() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = 0.05,
            deferredAnnualExpenseReduction = 150_000.0,
            deferredStartYears = 15.0,
            financialAssets = 300_000.0,
            liquidAssets = 200_000.0,
            capitalGainsTaxPercent = 0.0
        )
        val engine = ProjectionEngine(params)
        val years = engine.findYearsToRetirement().years

        // 4. Отложенное снижение расходов (выход детей на самообеспечение)
        assertEquals(9.71, years, 0.01)

        val reqAtRetire = engine.requiredCapitalAt(years)
        val accAtRetire = engine.accumulatedCapitalAt(years)
        assertTrue(accAtRetire >= reqAtRetire)
    }

    @Test
    fun testCapitalGainsTax20Percent() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = 0.05,
            financialAssets = 400_000.0,
            liquidAssets = 100_000.0,
            capitalGainsTaxPercent = 20.0
        )
        val engine = ProjectionEngine(params)
        val years = engine.findYearsToRetirement().years

        // 5. Налог на прирост капитала 20% с учетом доли прибыли Blended Model
        assertEquals(12.16, years, 0.01)

        // Налог 20% отодвигает срок FIRE по сравнению с базой (12.16 против 11.29)
        assertTrue(years > 11.29)
        val taxFactor = engine.getTaxFactorAt(years, 80.0 - (35 + years), engine.requiredCapitalAt(years))
        assertTrue(taxFactor > 1.0)
    }

    @Test
    fun testTaxInfoWrappersDelegateToSharedCalculation() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = 0.05,
            temporaryExpenses = listOf(TemporaryExpense(120_000.0, 10.0)),
            financialAssets = 400_000.0,
            liquidAssets = 100_000.0,
            capitalGainsTaxPercent = 20.0
        )
        val engine = ProjectionEngine(params)
        val t = 12.0
        val yearsInRetirement = 30.0
        val baseReq = 8_000_000.0
        val taxInfo = engine.taxInfo(t, yearsInRetirement, baseReq)

        assertEquals(480_000.0, engine.ongoingAnnualExpenses, 0.001)
        assertEquals(720_000.0, engine.ongoingAnnualCashFlow, 0.001)
        assertEquals(taxInfo.factor, engine.getTaxFactorAt(t, yearsInRetirement, baseReq), 0.000001)
        assertEquals(taxInfo.effectiveRate * 100.0, engine.getEffectiveTaxRateAt(t, yearsInRetirement, baseReq), 0.000001)
        assertEquals(taxInfo.gainShare * 100.0, engine.getGainShareAt(t, yearsInRetirement, baseReq), 0.000001)
    }

    @Test
    fun testNoAssetsAssumesFutureSavingsAreInvested() {
        val common = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 100_000.0,
            annualCashFlow = 20_000.0,
            annualExpenses = 10_000.0,
            realReturnRate = 0.05,
            capitalGainsTaxPercent = 20.0
        )
        val noAssets = ProjectionEngine(common.copy(financialAssets = 0.0, liquidAssets = 0.0))
        val financialAsset = ProjectionEngine(common.copy(financialAssets = 1.0, liquidAssets = 0.0))

        assertEquals(1.0, noAssets.finRatio, 0.0)
        assertEquals(
            financialAsset.getEffectiveTaxRateAt(0.0, 30.0, 0.0),
            noAssets.getEffectiveTaxRateAt(0.0, 30.0, 0.0),
            0.000001
        )
        assertEquals(
            financialAsset.findYearsToRetirement().years,
            noAssets.findYearsToRetirement().years,
            0.01
        )
    }

    @Test
    fun testLiquidAssetsWithoutFinancialAssetsKeepZeroCapitalGainsTax() {
        val common = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 100_000.0,
            annualCashFlow = 20_000.0,
            annualExpenses = 10_000.0,
            realReturnRate = 0.05,
            capitalGainsTaxPercent = 20.0
        )
        val withLiquidAssets = ProjectionEngine(
            common.copy(financialAssets = 0.0, liquidAssets = 50_000.0)
        )
        val withoutTax = ProjectionEngine(
            common.copy(financialAssets = 0.0, liquidAssets = 50_000.0, capitalGainsTaxPercent = 0.0)
        )

        assertEquals(0.0, withLiquidAssets.getEffectiveTaxRateAt(0.0, 30.0, 0.0), 0.0)
        assertEquals(1.0, withLiquidAssets.getTaxFactorAt(0.0, 30.0, 0.0), 0.0)
        assertEquals(withoutTax.findYearsToRetirement().years, withLiquidAssets.findYearsToRetirement().years, 0.01)
    }

    @Test
    fun testZeroCapitalGainsTaxKeepsTaxFreeProjectionResults() {
        val params = ProjectionParams(
            currentAge = 35,
            lifeExpectancy = 80,
            startCapital = 500_000.0,
            annualCashFlow = 600_000.0,
            annualExpenses = 600_000.0,
            realReturnRate = 0.05,
            financialAssets = 300_000.0,
            liquidAssets = 200_000.0,
            capitalGainsTaxPercent = 0.0
        )
        val engine = ProjectionEngine(params)

        assertEquals(11.29, engine.findYearsToRetirement().years, 0.01)
        assertEquals(0.0, engine.getEffectiveTaxRateAt(10.0, 20.0, 1_000_000.0), 0.0)
        assertEquals(1.0, engine.getTaxFactorAt(10.0, 20.0, 1_000_000.0), 0.0)
    }

    @Test
    fun testTemporaryExpensesAreCappedToAnnualExpenses() {
        val engine = ProjectionEngine(
            ProjectionParams(
                currentAge = 35,
                lifeExpectancy = 80,
                startCapital = 0.0,
                annualCashFlow = 100.0,
                annualExpenses = 600.0,
                realReturnRate = 0.0,
                temporaryExpenses = listOf(
                    TemporaryExpense(annualAmount = 500.0, durationYears = 5.0),
                    TemporaryExpense(annualAmount = 500.0, durationYears = 10.0)
                )
            )
        )

        assertEquals(0.0, engine.ongoingAnnualExpenses, 0.001)
        assertEquals(700.0, engine.ongoingAnnualCashFlow, 0.001)
    }

    @Test
    fun testDeferredReductionIsCappedBeforeDeferredStart() {
        val engine = ProjectionEngine(
            ProjectionParams(
                currentAge = 40,
                lifeExpectancy = 70,
                startCapital = 0.0,
                annualCashFlow = 0.0,
                annualExpenses = 600.0,
                realReturnRate = 0.0,
                deferredAnnualExpenseReduction = 1_000.0,
                deferredStartYears = 18.0
            )
        )

        assertEquals(4_800.0, engine.computeBaseRequirement(t = 0.0, yearsInRetirement = 20.0), 0.001)
    }

    @Test
    fun testTrajectoryUsesDynamicTaxBeforeRetirementAndFixedTaxAfter() {
        val engine = ProjectionEngine(
            ProjectionParams(
                currentAge = 35,
                lifeExpectancy = 80,
                startCapital = 100_000.0,
                annualCashFlow = 0.0,
                annualExpenses = 1_000_000.0,
                realReturnRate = 0.03,
                financialAssets = 100_000.0,
                liquidAssets = 0.0,
                capitalGainsTaxPercent = 50.0
            )
        )
        val points = engine.buildTrajectory(precomputedYearsToRetire = 5.0, effectiveTaxRate = 0.40)

        assertEquals(engine.requiredCapitalAt(0.0), points.first().requiredCapital, 0.001)
        assertEquals(engine.requiredCapitalAt(5.0, fixedTaxFactor = 1.0 / 0.60), points[5].requiredCapital, 0.001)
        assertTrue(points.first().requiredCapital != points[5].requiredCapital)
    }

    @Test
    fun testRetirementStatuses() {
        val common = ProjectionParams(
            currentAge = 40,
            lifeExpectancy = 80,
            startCapital = 100_000.0,
            annualCashFlow = -600_000.0,
            annualExpenses = 800_000.0,
            realReturnRate = -0.05,
            financialAssets = 0.0,
            liquidAssets = 100_000.0
        )
        val unreachable = ProjectionEngine(common).findYearsToRetirement()
        assertEquals(FireStatus.UNREACHABLE, unreachable.status)
        assertEquals(40.0, unreachable.years, 0.001)

        val exhausted = ProjectionEngine(common.copy(lifeExpectancy = 40)).findYearsToRetirement()
        assertEquals(FireStatus.HORIZON_EXHAUSTED, exhausted.status)
        assertEquals(0.0, exhausted.years, 0.001)

        val invalidAge = ProjectionEngine(common.copy(isAgeValid = false)).findYearsToRetirement()
        assertEquals(FireStatus.INVALID_AGE, invalidAge.status)
        assertEquals(0.0, invalidAge.years, 0.001)

        val reachedNow = ProjectionEngine(common.copy(startCapital = 100_000_000.0)).findYearsToRetirement()
        assertEquals(FireStatus.REACHED_NOW, reachedNow.status)
        assertEquals(0.0, reachedNow.years, 0.001)
    }
}
