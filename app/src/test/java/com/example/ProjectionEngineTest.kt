package com.example

import com.example.domain.ProjectionEngine
import com.example.domain.ProjectionParams
import com.example.domain.TemporaryExpense
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
        val years = engine.findYearsToRetirement()

        // 1. Точное значение срока до пенсии из текущей версии
        assertEquals(11.29, years, 0.01)

        val reqAtRetire = engine.requiredCapitalAt(years)
        val accAtRetire = engine.accumulatedCapitalAt(years)

        // Накопленный капитал в точке FIRE покрывает требуемый целевой капитал
        assertEquals(9_683_192.37, reqAtRetire, 10.0)
        assertEquals(9_683_890.20, accAtRetire, 10.0)

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
        val years = engine.findYearsToRetirement()

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
        val years = engine.findYearsToRetirement()

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
        val years = engine.findYearsToRetirement()

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
        val years = engine.findYearsToRetirement()

        // 5. Налог на прирост капитала 20% с учетом доли прибыли Blended Model
        assertEquals(12.16, years, 0.01)

        // Налог 20% отодвигает срок FIRE по сравнению с базой (12.16 против 11.29)
        assertTrue(years > 11.29)
        val taxFactor = engine.getTaxFactorAt(years, 80.0 - (35 + years), engine.requiredCapitalAt(years))
        assertTrue(taxFactor > 1.0)
    }
}
