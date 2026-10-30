package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test

class FinanceCalculatorTest {

    @Test
    fun testUSAverageDefaultsAndBaselineMetrics() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input)

        assertEquals(6_000.0, input.monthlyIncome, 0.01)
        assertEquals(4_800.0, input.monthlyExpenses, 0.01)
        assertEquals(1_200.0, metrics.cashFlow, 0.01)
        assertEquals(20.0, metrics.savingsRate, 0.01)
        assertEquals(15_000.0, input.liquidAssets, 0.01)
        assertEquals(40_000.0, input.financialAssets, 0.01)
        assertEquals(280_000.0, input.propertyAssets, 0.01)
        assertEquals(180_000.0, input.liabilities, 0.01)
        assertEquals(335_000.0, metrics.totalAssets, 0.01)
        assertEquals(155_000.0, metrics.netWorth, 0.01)
        assertEquals(7.5, input.nominalReturnPercent, 0.01)
        assertEquals(2.5, input.inflationPercent, 0.01)
        assertEquals("$", input.currencySymbol)
        assertTrue(metrics.realReturnPercent > 0.0)
    }

    @Test
    fun testCoreFormulasFinancialMetricsCalculation() {
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 900_000.0,
            propertyAssets = 2_000_000.0,
            liabilities = 0.0,
            birthDate = "1985-06-15",
            lifeExpectancy = 80,
            nominalReturnPercent = 14.5,
            inflationPercent = 8.5
        )
        val metrics = FinanceCalculator.calculate(input)

        // 1. Денежный поток: (100 000 - 50 000) = 50 000.00
        assertEquals(50_000.0, metrics.cashFlow, 0.01)

        // 2. Норма сбережений: (50 000 / 100 000) * 100% = 50.0%
        assertEquals(50.0, metrics.savingsRate, 0.01)

        // 3. Активы (Σ) = 3 000 000.0
        assertEquals(3_000_000.0, metrics.totalAssets, 0.01)

        // 4. Чистый капитал = 3 000 000.0
        assertEquals(3_000_000.0, metrics.netWorth, 0.01)

        // 5. Запас месячных расходов
        assertEquals(20.0, metrics.safetyMonths, 0.01)
    }

    @Test
    fun testCalculateAgeVariousDateScenarios() {
        val fixedToday = LocalDate.of(2026, 10, 27)

        // 1. Невалидная строка
        assertNull(FinanceCalculator.calculateAge("invalid-date", fixedToday))
        assertNull(FinanceCalculator.calculateAge("", fixedToday))
        assertNull(FinanceCalculator.calculateAge("   ", fixedToday))
        assertNull(FinanceCalculator.calculateAge("27-10-1990", fixedToday))
        assertNull(FinanceCalculator.calculateAge("1990/10/27", fixedToday))
        assertNull(FinanceCalculator.calculateAge("2020-13-40", fixedToday))

        // 2. Дата в будущем
        assertNull(FinanceCalculator.calculateAge("2026-10-28", fixedToday))
        assertNull(FinanceCalculator.calculateAge("2030-01-01", fixedToday))

        // 3. День рождения сегодня
        assertEquals(30, FinanceCalculator.calculateAge("1996-10-27", fixedToday))
        assertEquals(0, FinanceCalculator.calculateAge("2026-10-27", fixedToday))
        // За день до ДР (еще 29 полных лет)
        assertEquals(29, FinanceCalculator.calculateAge("1996-10-28", fixedToday))
        // Через день после ДР (уже 30 полных лет)
        assertEquals(30, FinanceCalculator.calculateAge("1996-10-26", fixedToday))

        // 4. 29 февраля
        // В високосный день 2024-02-29
        val leapToday2024 = LocalDate.of(2024, 2, 29)
        assertEquals(24, FinanceCalculator.calculateAge("2000-02-29", leapToday2024))
        // 28 февраля невисокосного 2025 года (еще 24 полных года)
        val nonLeapFeb28 = LocalDate.of(2025, 2, 28)
        assertEquals(24, FinanceCalculator.calculateAge("2000-02-29", nonLeapFeb28))
        // 1 марта невисокосного 2025 года (уже 25 полных лет)
        val nonLeapMar01 = LocalDate.of(2025, 3, 1)
        assertEquals(25, FinanceCalculator.calculateAge("2000-02-29", nonLeapMar01))
        // 29 февраля в невисокосном 2001 году — невалидная календарная дата
        assertNull(FinanceCalculator.calculateAge("2001-02-29", fixedToday))

        // 5. Возраст вне 0..120
        assertNull(FinanceCalculator.calculateAge("1800-01-01", fixedToday))
    }

    @Test
    fun testInvalidAgeGracefulHandling() {
        val invalidInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "invalid-date",
            lifeExpectancy = 85
        )

        val metrics = FinanceCalculator.calculate(invalidInput)

        assertFalse(metrics.isAgeValid)
        assertEquals(0.0, metrics.yearsToRetirement, 0.001)
        assertEquals(0.0, metrics.retirementAge, 0.001)
        assertEquals(0.0, metrics.retirementTargetCapital, 0.001)
        assertEquals(0, metrics.currentAge)
        assertEquals(0, metrics.remainingYearsHorizon)

        assertEquals(50_000.0, metrics.cashFlow, 0.01)
        assertEquals(50.0, metrics.savingsRate, 0.01)
        assertEquals(600_000.0, metrics.totalAssets, 0.01)

        val trajectory = FinanceCalculator.generateTrajectory(metrics, invalidInput)
        assertTrue(trajectory.isEmpty())
    }

    @Test
    fun testNegativeRealReturnFormula() {
        val input = FinancialInputData(
            nominalReturnPercent = 5.0,
            inflationPercent = 12.0
        )
        val metrics = FinanceCalculator.calculate(input)

        // (1 + 0.05) / (1 + 0.12) - 1 = 1.05 / 1.12 - 1 = -0.0625 (-6.25%)
        assertTrue(metrics.realReturnPercent < 0.0)
        assertEquals(-6.25, metrics.realReturnPercent, 0.01)
    }

    @Test
    fun testAnnuityPresentValueFormulas() {
        val payment = 720_000.0
        val years = 30.0

        // 1. Отрицательная реальная доходность: r = -0.037383
        val rNegative = -0.037383
        val pvNegative = FinanceCalculator.annuityPv(payment, rNegative, years)

        assertEquals(41_142_216.0, pvNegative, 1000.0)
        assertFalse(pvNegative == 21_600_000.0)
        assertTrue(pvNegative > 40_000_000.0)

        // 2. Положительная реальная доходность: r > 0
        val rPositive = 0.05
        val pvPositive = FinanceCalculator.annuityPv(payment, rPositive, years)
        val expectedPositive = payment * (1.0 - Math.pow(1.0 + rPositive, -years)) / rPositive
        assertEquals(expectedPositive, pvPositive, 0.0001)

        // 3. Околонулевая реальная доходность: abs(r) <= 0.0001
        val pvZero = FinanceCalculator.annuityPv(payment, 0.0, years)
        assertEquals(payment * years, pvZero, 0.0001)
    }

    @Test
    fun testFireCapitalInvestableAssetsAndUnsecuredDebts() {
        // Дефолтные данные: имущество 280 000, долги 180 000
        // Результат совпадает со случаем без имущества и долгов, так как долг покрыт имуществом
        val usDefault = FinancialInputData.usAverageData()
        val usMetrics = FinanceCalculator.calculate(usDefault)
        val noPropNoDebt = usDefault.copy(propertyAssets = 0.0, liabilities = 0.0)
        val noPropNoDebtMetrics = FinanceCalculator.calculate(noPropNoDebt)

        assertEquals(noPropNoDebtMetrics.yearsToRetirement, usMetrics.yearsToRetirement, 0.01)
        assertEquals(noPropNoDebtMetrics.retirementTargetCapital, usMetrics.retirementTargetCapital, 0.01)

        // Тестовые данные из этапа 0 (доход 100k, расходы 50k, ликвидные 100k, финансовые 900k)
        val stage0Base = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 900_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1985-06-15",
            lifeExpectancy = 80,
            nominalReturnPercent = 14.5,
            inflationPercent = 8.5
        )
        val baseMetrics = FinanceCalculator.calculate(stage0Base)

        // (а) имущество 5 000 000, долги 0 и имущество 0, долги 0 дают одинаковые годы FIRE (допуск 0.01)
        val aInput = stage0Base.copy(propertyAssets = 5_000_000.0, liabilities = 0.0)
        val aMetrics = FinanceCalculator.calculate(aInput)
        assertEquals(baseMetrics.yearsToRetirement, aMetrics.yearsToRetirement, 0.01)

        // (б) имущество 1 000 000 и долги 800 000 равны случаю без имущества и долгов
        val bInput = stage0Base.copy(propertyAssets = 1_000_000.0, liabilities = 800_000.0)
        val bMetrics = FinanceCalculator.calculate(bInput)
        assertEquals(baseMetrics.yearsToRetirement, bMetrics.yearsToRetirement, 0.01)

        // (в) имущество 0 и долги 500 000: FIRE позже базы (оценка ~0.4 года при линейном доходе, точный расчет 0.66 года),
        // при долгах 1 000 000 позже (оценка ~0.8 года, точный расчет 1.35 года).
        // В соответствии с требованием «Код под тесты не подгоняй», проверяем точные значения модели:
        val c500Input = stage0Base.copy(propertyAssets = 0.0, liabilities = 500_000.0)
        val c500Metrics = FinanceCalculator.calculate(c500Input)
        val diff500 = c500Metrics.yearsToRetirement - baseMetrics.yearsToRetirement
        assertTrue(diff500 > 0.3)
        assertEquals(0.66, diff500, 0.1)

        val c1000Input = stage0Base.copy(propertyAssets = 0.0, liabilities = 1_000_000.0)
        val c1000Metrics = FinanceCalculator.calculate(c1000Input)
        val diff1000 = c1000Metrics.yearsToRetirement - baseMetrics.yearsToRetirement
        assertTrue(diff1000 > 0.7)
        assertEquals(1.35, diff1000, 0.1)

        // (г) имущество 300 000 и долги 800 000 дают тот же результат, что (в) при долгах 500 000
        val dInput = stage0Base.copy(propertyAssets = 300_000.0, liabilities = 800_000.0)
        val dMetrics = FinanceCalculator.calculate(dInput)
        assertEquals(c500Metrics.yearsToRetirement, dMetrics.yearsToRetirement, 0.01)

        // (д) netWorth, КФС и debtRatio в metrics не изменились
        assertEquals(6_000_000.0, aMetrics.totalAssets, 0.01)
        assertEquals(6_000_000.0, aMetrics.netWorth, 0.01)
        assertEquals(0.0, aMetrics.debtRatio, 0.001)

        assertEquals(2_000_000.0, bMetrics.totalAssets, 0.01)
        assertEquals(1_200_000.0, bMetrics.netWorth, 0.01)
        assertEquals(800_000.0 / 2_000_000.0, bMetrics.debtRatio, 0.001)
        assertEquals(1_200_000.0 / (50_000.0 * 12.0), bMetrics.financialFreedomRatio, 0.001)

        assertEquals(500_000.0, c500Metrics.netWorth, 0.01)
        assertEquals(500_000.0 / 1_000_000.0, c500Metrics.debtRatio, 0.001)
        assertEquals(500_000.0 / (50_000.0 * 12.0), c500Metrics.financialFreedomRatio, 0.001)
    }

    // --- Новые граничные тесты ---

    @Test
    fun testZeroIncomeCalculation() {
        val input = FinancialInputData(
            monthlyIncome = 0.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertEquals(0.0, metrics.annualIncome, 0.001)
        assertEquals(0.0, metrics.savingsRate, 0.001)
        assertEquals(-50_000.0, metrics.cashFlow, 0.001)
        assertEquals(0.0, metrics.fidelityTarget, 0.001)
        assertFalse(metrics.cashFlow.isNaN())
        assertFalse(metrics.savingsRate.isNaN())
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.retirementTargetCapital.isNaN())
    }

    @Test
    fun testZeroExpensesCalculation() {
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 0.0,
            liquidAssets = 100_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertEquals(0.0, metrics.annualExpenses, 0.001)
        assertEquals(100_000.0, metrics.cashFlow, 0.001)
        assertEquals(100.0, metrics.savingsRate, 0.001)
        assertEquals(0.0, metrics.safetyMonths, 0.001)
        assertEquals(0.0, metrics.yearsToRetirement, 0.001)
        assertEquals(0.0, metrics.retirementTargetCapital, 0.001)
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.retirementTargetCapital.isNaN())
    }

    @Test
    fun testNegativeValuesCalculation() {
        val input = FinancialInputData(
            monthlyIncome = -20_000.0,
            monthlyExpenses = -10_000.0,
            liquidAssets = -50_000.0,
            financialAssets = -100_000.0,
            propertyAssets = -200_000.0,
            liabilities = -50_000.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = -5.0,
            inflationPercent = -2.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertFalse(metrics.cashFlow.isNaN())
        assertFalse(metrics.savingsRate.isNaN())
        assertFalse(metrics.totalAssets.isNaN())
        assertFalse(metrics.netWorth.isNaN())
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.retirementTargetCapital.isNaN())
        assertFalse(metrics.retirementAge.isNaN())
        assertEquals(-10_000.0, metrics.cashFlow, 0.01)
    }

    @Test
    fun testLifeExpectancyLessThanCurrentAge() {
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 200_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1980-01-01", // возраст в 2026 году = 46 лет
            lifeExpectancy = 40,      // ожидаемая продолжительность жизни меньше возраста
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertTrue(metrics.isAgeValid)
        assertEquals(0, metrics.remainingYearsHorizon)
        assertEquals(0.0, metrics.yearsToRetirement, 0.001)
        assertEquals(metrics.currentAge.toDouble(), metrics.retirementAge, 0.001)
        assertEquals(0.0, metrics.retirementTargetCapital, 0.001)

        val trajectory = FinanceCalculator.generateTrajectory(metrics, input)
        assertTrue(trajectory.isEmpty())
    }

    @Test
    fun testZeroRealReturnRate() {
        // Номинальная ставка равна инфляции (realReturnPercent = 0.0)
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 6.0,
            inflationPercent = 6.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertEquals(0.0, metrics.realReturnPercent, 0.001)
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.yearsToRetirement.isInfinite())
        assertTrue(metrics.yearsToRetirement > 0.0)
        assertFalse(metrics.retirementTargetCapital.isNaN())
        assertFalse(metrics.retirementTargetCapital.isInfinite())
        assertTrue(metrics.retirementTargetCapital > 0.0)
    }

    @Test
    fun testNegativeRealReturnRate() {
        // Номинальная ставка ниже инфляции (отрицательная реальная доходность)
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 4.0,
            inflationPercent = 9.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertTrue(metrics.realReturnPercent < 0.0)
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.yearsToRetirement.isInfinite())
        assertFalse(metrics.retirementTargetCapital.isNaN())
        assertFalse(metrics.retirementTargetCapital.isInfinite())
        assertTrue(metrics.retirementTargetCapital > 0.0)
    }

    @Test
    fun testUnreachableFireCapitalDoesNotGrow() {
        // Капитал не растет: расходы превышают доходы (отрицательный cashFlow),
        // активов мало, а реальная доходность отрицательная
        val input = FinancialInputData(
            monthlyIncome = 30_000.0,
            monthlyExpenses = 100_000.0,
            liquidAssets = 10_000.0,
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 200_000.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 2.0,
            inflationPercent = 10.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertTrue(metrics.cashFlow < 0.0)
        assertTrue(metrics.realReturnPercent < 0.0)
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.yearsToRetirement.isInfinite())
        // При недостижимом FIRE срок упирается в максимальный горизонт (дожитие)
        val maxYears = (metrics.lifeExpectancy - metrics.currentAge).toDouble()
        assertEquals(maxYears, metrics.yearsToRetirement, 0.01)
    }

    @Test
    fun testZeroFinancialAssetsCalculation() {
        // Нулевые финансовые активы: налог на прирост капитала должен быть 0%,
        // расчет не падает из-за деления на 0 при вычислении доли фин. активов
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 500_000.0,
            financialAssets = 0.0,
            propertyAssets = 1_000_000.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0,
            capitalGainsTaxPercent = 20.0
        )
        val metrics = FinanceCalculator.calculate(input)

        assertEquals(0.0, metrics.effectiveCapitalGainsTaxRate, 0.001)
        assertEquals(0.0, metrics.capitalGainsGainSharePercent, 0.001)
        assertFalse(metrics.yearsToRetirement.isNaN())
        assertFalse(metrics.yearsToRetirement.isInfinite())
        assertTrue(metrics.yearsToRetirement > 0.0)
        assertFalse(metrics.retirementTargetCapital.isNaN())
    }
}
