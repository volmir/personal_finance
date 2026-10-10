package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testRussiaAverageDefaults() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        assertEquals(85_000.0, input.monthlyIncome, 0.01)
        assertEquals(68_000.0, input.monthlyExpenses, 0.01)
        assertEquals(17_000.0, metrics.cashFlow, 0.01)
        assertEquals(20.0, metrics.savingsRate, 0.01)
        assertEquals(4_450_000.0, metrics.totalAssets, 0.01)
        assertEquals(3_970_000.0, metrics.netWorth, 0.01)
        assertEquals(4.0, input.nominalReturnPercent, 0.01)
        assertEquals(2.0, input.inflationPercent, 0.01)
        assertTrue(metrics.realReturnPercent > 0.0)
    }

    @Test
    fun testFormulasCalculation() {
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
    fun testSimulatorDynamicScalesRecalculation() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        // Тестируем шкалу сбережений
        val savingsConfig = com.example.domain.SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = metrics.cashFlow,
            monthlyIncome = input.monthlyIncome,
            monthlyExpenses = input.monthlyExpenses
        )
        // Диапазон должен содержать 0, позволять снижение (отрицательный min) и рост (положительный max)
        assertTrue(savingsConfig.minValue < 0f)
        assertTrue(savingsConfig.maxValue > 0f)
        assertTrue(savingsConfig.stepSize > 0.0)
        assertTrue(savingsConfig.steps > 0)
        // Шаг должен нацело делить границы
        assertEquals(0.0, (savingsConfig.minValue % savingsConfig.stepSize.toFloat()).toDouble(), 0.001)
        assertEquals(0.0, (savingsConfig.maxValue % savingsConfig.stepSize.toFloat()).toDouble(), 0.001)

        // Тестируем шкалу расходов
        val expensesConfig = com.example.domain.SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = input.monthlyExpenses,
            monthlyIncome = input.monthlyIncome
        )
        assertTrue(expensesConfig.minValue < 0f)
        assertTrue(expensesConfig.maxValue > 0f)
        assertTrue(expensesConfig.stepSize > 0.0)
        assertTrue(expensesConfig.steps > 0)
        assertEquals(0.0, (expensesConfig.minValue % expensesConfig.stepSize.toFloat()).toDouble(), 0.001)
        assertEquals(0.0, (expensesConfig.maxValue % expensesConfig.stepSize.toFloat()).toDouble(), 0.001)

        // Тестируем шкалу реальной доходности
        val returnConfig = com.example.domain.SimulatorScaleCalculator.calculateReturnConfig(
            realReturnPercent = metrics.realReturnPercent
        )
        assertTrue(returnConfig.minValue < 0f)
        assertTrue(returnConfig.maxValue > 0f)
        assertTrue(returnConfig.stepSize in 0.25..1.0)
        assertTrue(returnConfig.steps > 0)

        // Проверяем масштабирование при долларовых/небольших значениях ($1500 сбережения)
        val dollarSavingsConfig = com.example.domain.SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = 1500.0,
            monthlyIncome = 5000.0,
            monthlyExpenses = 3500.0
        )
        // Шаг должен динамически адаптироваться под масштаб долларовых значений
        assertTrue(dollarSavingsConfig.stepSize in 50.0..250.0)
        assertTrue(dollarSavingsConfig.minValue <= -1500f)

        // Проверяем масштабирование расходов для долларовых значений ($3500)
        val dollarExpensesConfig = com.example.domain.SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = 3500.0,
            monthlyIncome = 5000.0
        )
        assertTrue(dollarExpensesConfig.stepSize in 100.0..500.0)
        assertTrue(dollarExpensesConfig.maxValue >= 3500f)
    }

    @Test
    fun testStressTestScenariosAccumulation() {
        val baseInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 500_000.0,
            financialAssets = 1_000_000.0,
            propertyAssets = 2_000_000.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 8.0,
            currencySymbol = "₽",
            includeFinancialInEmergencyFund = true
        )
        val baseMetrics = FinanceCalculator.calculate(baseInput)

        // 1. Потеря работы (доход = 0)
        val jobLossInput = baseInput.copy(monthlyIncome = 0.0)
        val jobLossMetrics = FinanceCalculator.calculate(jobLossInput)
        assertTrue(jobLossMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)
        assertEquals(0.0, jobLossMetrics.annualIncome, 0.01)

        // 2. Обвал рынка на 30% (ETF -30%)
        val marketCrashInput = baseInput.copy(financialAssets = baseInput.financialAssets * 0.70)
        val marketCrashMetrics = FinanceCalculator.calculate(marketCrashInput)
        assertEquals(700_000.0, marketCrashInput.financialAssets, 0.01)
        assertTrue(marketCrashMetrics.yearsToRetirement >= baseMetrics.yearsToRetirement)

        // 3. Ипотека на квартиру (35% от ежемесячного дохода на 20 лет, +84 дохода в имущество)
        val mortgageMonthly = 0.35 * baseInput.monthlyIncome // 35 000
        val mortgageAnnual = mortgageMonthly * 12.0
        val mortgageAsset = 84.0 * baseInput.monthlyIncome
        val mortgageInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses + mortgageMonthly)
        val mortgageMetrics = FinanceCalculator.calculate(
            mortgageInput,
            temporaryExpenses = listOf(com.example.domain.TemporaryExpense(annualAmount = mortgageAnnual, durationYears = 20.0)),
            deferredAssets = listOf(com.example.domain.DeferredAsset(amount = mortgageAsset, afterYears = 20.0))
        )
        assertEquals(85_000.0, mortgageInput.monthlyExpenses, 0.01)

        // Без учета накопленного имущества срок был бы значительно больше
        val mortgageWithoutAssetMetrics = FinanceCalculator.calculate(
            mortgageInput,
            temporaryExpenses = listOf(com.example.domain.TemporaryExpense(annualAmount = mortgageAnnual, durationYears = 20.0))
        )
        assertTrue(mortgageMetrics.yearsToRetirement <= mortgageWithoutAssetMetrics.yearsToRetirement)

        // 4. Автомобиль в кредит (20% от ежемесячного дохода на 5 лет, +6 доходов в имущество / 50% стоимости)
        val carLoanMonthly = 0.20 * baseInput.monthlyIncome // 20 000
        val carLoanAnnual = carLoanMonthly * 12.0
        val carLoanAsset = 6.0 * baseInput.monthlyIncome
        val carLoanInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses + carLoanMonthly)
        val carLoanMetrics = FinanceCalculator.calculate(
            carLoanInput,
            temporaryExpenses = listOf(com.example.domain.TemporaryExpense(annualAmount = carLoanAnnual, durationYears = 5.0)),
            deferredAssets = listOf(com.example.domain.DeferredAsset(amount = carLoanAsset, afterYears = 5.0))
        )
        assertEquals(70_000.0, carLoanInput.monthlyExpenses, 0.01)

        // Траектория учитывает появление автомобиля в имуществе через 5 лет
        val carTrajectory = FinanceCalculator.generateTrajectory(
            carLoanMetrics,
            carLoanInput,
            temporaryExpenses = listOf(com.example.domain.TemporaryExpense(annualAmount = carLoanAnnual, durationYears = 5.0)),
            deferredAssets = listOf(com.example.domain.DeferredAsset(amount = carLoanAsset, afterYears = 5.0))
        )
        assertTrue(carTrajectory.any { it.yearOffset >= 5.0 && it.accumulatedCapital > 0 })

        // 5. Крупная разовая трата (500% дохода = 500 000, покрытие из ликвидных/фин. активов)
        val largeExpense = 5.0 * baseInput.monthlyIncome
        val largeExpenseInput = baseInput.copy(liquidAssets = 0.0) // 500 000 полностью покрыто из ликвидных активов
        val largeExpenseMetrics = FinanceCalculator.calculate(largeExpenseInput)
        assertTrue(largeExpenseMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)

        // 6. Рост инфляции (+20% к расходам)
        val inflationInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 1.20)
        val inflationMetrics = FinanceCalculator.calculate(inflationInput)
        assertTrue(inflationMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)

        // 7. Рождение ребенка (+25% на 18 лет)
        val childAnnual = baseInput.monthlyExpenses * 0.25 * 12.0
        val childInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 1.25)
        val childMetrics = FinanceCalculator.calculate(
            childInput,
            listOf(com.example.domain.TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0))
        )
        assertTrue(childMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)

        // Кумулятивное совместное влияние: Рост инфляции (+20%) + Ребёнок (+25% на 18 лет)
        val combinedExpensesInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 1.45)
        val combinedExpensesMetrics = FinanceCalculator.calculate(
            combinedExpensesInput,
            listOf(com.example.domain.TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0))
        )
        assertTrue(combinedExpensesMetrics.yearsToRetirement > inflationMetrics.yearsToRetirement)

        // Комплексный кумулятивный стресс-тест: Ипотека + Обвал рынка + Рождение ребёнка
        val complexInput = baseInput.copy(
            financialAssets = baseInput.financialAssets * 0.70,
            monthlyExpenses = baseInput.monthlyExpenses * 1.25 + mortgageMonthly
        )
        val complexMetrics = FinanceCalculator.calculate(
            complexInput,
            temporaryExpenses = listOf(
                com.example.domain.TemporaryExpense(annualAmount = mortgageAnnual, durationYears = 20.0),
                com.example.domain.TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0)
            ),
            deferredAssets = listOf(
                com.example.domain.DeferredAsset(amount = mortgageAsset, afterYears = 20.0)
            )
        )
        assertTrue(complexMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)
    }

    @Test
    fun testScenariosAndSlidersSynchronousCumulativeEffect() {
        val baseInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 500_000.0,
            financialAssets = 1_000_000.0,
            propertyAssets = 2_000_000.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 8.0,
            currencySymbol = "₽",
            includeFinancialInEmergencyFund = true
        )
        val baseMetrics = FinanceCalculator.calculate(baseInput)

        // Тест 1: Чекбокс Потеря работы (доход = 0) + Ползунок сбережений (+15 000 подработка)
        val jobLossOnly = baseInput.copy(monthlyIncome = 0.0)
        val jobLossMetrics = FinanceCalculator.calculate(jobLossOnly)

        val jobLossWithSavingsSlider = baseInput.copy(monthlyIncome = 15_000.0) // 0 базовый доход + 15 000 сбережения
        val jobLossWithSavingsMetrics = FinanceCalculator.calculate(jobLossWithSavingsSlider)

        // Денежный поток при подработке улучшается на 15 000 по сравнению с чистой потерей работы
        assertTrue(jobLossWithSavingsMetrics.cashFlow > jobLossMetrics.cashFlow)
        assertEquals(-35_000.0, jobLossWithSavingsMetrics.cashFlow, 0.01)

        // При росте сбережений через ползунок срок до пенсии сокращается
        val extraSavingsInput = baseInput.copy(monthlyIncome = baseInput.monthlyIncome + 20_000.0)
        val extraSavingsMetrics = FinanceCalculator.calculate(extraSavingsInput)
        assertTrue(extraSavingsMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)

        // Тест 2: Чекбоксы Инфляция (+20%) и Ребёнок (+25%) + Ползунок расходов (сокращение на 10 000)
        val checkboxesExpenses = baseInput.monthlyExpenses * 1.45 // 72 500
        val checkboxesAndSliderExpenses = checkboxesExpenses - 10_000.0 // 62 500
        val expCombinedInput = baseInput.copy(monthlyExpenses = checkboxesAndSliderExpenses)
        val expCombinedMetrics = FinanceCalculator.calculate(expCombinedInput)

        assertEquals(62_500.0, expCombinedMetrics.annualExpenses / 12.0, 0.01)
        assertEquals(37_500.0, expCombinedMetrics.cashFlow, 0.01)

        // Тест 3: Синхронное накопление: Инфляция + Ребёнок + Ипотека (35% дохода) + Ползунок доходности (+2%)
        val baseNominal = baseInput.nominalReturnPercent / 100.0
        val baseInfl = baseInput.inflationPercent / 100.0
        val baseRealReturn = (1.0 + baseNominal) / (1.0 + baseInfl) - 1.0
        val targetRealReturn = baseRealReturn + 0.02
        val effectiveNominal = ((1.0 + targetRealReturn) * (1.0 + baseInfl) - 1.0) * 100.0

        val mortgageMonthlySync = 0.35 * baseInput.monthlyIncome
        val fullSyncInput = baseInput.copy(
            monthlyExpenses = checkboxesAndSliderExpenses + mortgageMonthlySync,
            nominalReturnPercent = effectiveNominal
        )
        val fullSyncMetrics = FinanceCalculator.calculate(
            fullSyncInput,
            listOf(
                com.example.domain.TemporaryExpense(annualAmount = mortgageMonthlySync * 12.0, durationYears = 20.0),
                com.example.domain.TemporaryExpense(annualAmount = 0.25 * baseInput.monthlyExpenses * 12.0, durationYears = 18.0)
            )
        )

        // Реальная доходность должна точно возрасти на 2.0%
        assertEquals(baseMetrics.realReturnPercent + 2.0, fullSyncMetrics.realReturnPercent, 0.01)
        // Траектория успешно генерируется и учитывает все накопленные факторы
        val trajectory = FinanceCalculator.generateTrajectory(
            fullSyncMetrics,
            fullSyncInput,
            listOf(
                com.example.domain.TemporaryExpense(annualAmount = mortgageMonthlySync * 12.0, durationYears = 20.0),
                com.example.domain.TemporaryExpense(annualAmount = 0.25 * baseInput.monthlyExpenses * 12.0, durationYears = 18.0)
            )
        )
        assertTrue(trajectory.isNotEmpty())
        assertEquals(baseMetrics.currentAge.toDouble(), trajectory.first().age, 0.01)
    }

    @Test
    fun testPositiveEventsAndCombinedScenarios() {
        val baseInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 500_000.0,
            financialAssets = 1_000_000.0,
            propertyAssets = 2_000_000.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 8.0,
            currencySymbol = "₽",
            includeFinancialInEmergencyFund = true
        )
        val baseMetrics = FinanceCalculator.calculate(baseInput)

        // 1. Позитивное 1: Повышение зарплаты (+20% доход)
        val salaryInput = baseInput.copy(monthlyIncome = baseInput.monthlyIncome * 1.20)
        val salaryMetrics = FinanceCalculator.calculate(salaryInput)
        assertEquals(120_000.0, salaryMetrics.annualIncome / 12.0, 0.01)
        assertTrue(salaryMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)

        // 2. Позитивное 2: Рост рынка на 25% (ETF +25%)
        val marketGrowthInput = baseInput.copy(financialAssets = baseInput.financialAssets * 1.25)
        val marketGrowthMetrics = FinanceCalculator.calculate(marketGrowthInput)
        assertEquals(1_250_000.0, marketGrowthInput.financialAssets, 0.01)
        assertTrue(marketGrowthMetrics.yearsToRetirement <= baseMetrics.yearsToRetirement)

        // 3. Позитивное 3: Оптимизация расходов (-15%)
        val optExpensesInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 0.85)
        val optExpensesMetrics = FinanceCalculator.calculate(optExpensesInput)
        assertEquals(42_500.0, optExpensesInput.monthlyExpenses, 0.01)
        assertTrue(optExpensesMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)

        // 4. Позитивное 4: Переезд в регион с более низкой стоимостью жизни (-30%)
        val relocationInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 0.70)
        val relocationMetrics = FinanceCalculator.calculate(relocationInput)
        assertEquals(35_000.0, relocationInput.monthlyExpenses, 0.01)
        assertTrue(relocationMetrics.yearsToRetirement < optExpensesMetrics.yearsToRetirement)

        // 5. Позитивное 5: Наследство (1200% месячного дохода = 1.2 млн)
        val inheritanceInput = baseInput.copy(liquidAssets = baseInput.liquidAssets + 12.0 * baseInput.monthlyIncome)
        val inheritanceMetrics = FinanceCalculator.calculate(inheritanceInput)
        assertEquals(1_700_000.0, inheritanceInput.liquidAssets, 0.01)
        assertTrue(inheritanceMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)

        // 6. Позитивное 6: Продажа имущества (2000% месячного дохода = 2.0 млн)
        val propertySaleInput = baseInput.copy(liquidAssets = baseInput.liquidAssets + 20.0 * baseInput.monthlyIncome)
        val propertySaleMetrics = FinanceCalculator.calculate(propertySaleInput)
        assertEquals(2_500_000.0, propertySaleInput.liquidAssets, 0.01)
        assertTrue(propertySaleMetrics.yearsToRetirement < inheritanceMetrics.yearsToRetirement)

        // 7. Позитивное 7: Выход детей на самообеспечение (-25% после 18 лет)
        val deferredReduction = baseInput.monthlyExpenses * 0.25 * 12.0
        val childSelfSufficientMetrics = FinanceCalculator.calculate(
            baseInput,
            deferredAnnualExpenseReduction = deferredReduction,
            deferredStartYears = 18.0
        )
        assertTrue(childSelfSufficientMetrics.yearsToRetirement <= baseMetrics.yearsToRetirement)

        // Совместное кумулятивное влияние: Зарплата +20% + Оптимизация расходов -15% + Рост рынка +25%
        val combinedPositiveInput = baseInput.copy(
            monthlyIncome = baseInput.monthlyIncome * 1.20,
            monthlyExpenses = baseInput.monthlyExpenses * 0.85,
            financialAssets = baseInput.financialAssets * 1.25
        )
        val combinedPositiveMetrics = FinanceCalculator.calculate(combinedPositiveInput)
        assertTrue(combinedPositiveMetrics.yearsToRetirement < salaryMetrics.yearsToRetirement)

        // Баланс позитивных и негативных событий: Инфляция (+20%) компенсируется оптимизацией (-15%) и повышением зарплаты (+20%)
        val balancedInput = baseInput.copy(
            monthlyIncome = baseInput.monthlyIncome * 1.20,
            monthlyExpenses = baseInput.monthlyExpenses * (1.0 + 0.20 - 0.15)
        )
        val balancedMetrics = FinanceCalculator.calculate(balancedInput)
        assertTrue(balancedMetrics.cashFlow > baseMetrics.cashFlow)
        assertTrue(balancedMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)
    }

    @Test
    fun testFormatCheckboxesBadge() {
        // Проверка корректности форм множественного числа в русском языке и неразрывного пробела
        assertEquals("1\u00A0чекбокс", Formatters.formatCheckboxesCount(1))
        assertEquals("2\u00A0чекбокса", Formatters.formatCheckboxesCount(2))
        assertEquals("3\u00A0чекбокса", Formatters.formatCheckboxesCount(3))
        assertEquals("4\u00A0чекбокса", Formatters.formatCheckboxesCount(4))
        assertEquals("5\u00A0чекбоксов", Formatters.formatCheckboxesCount(5))
        assertEquals("11\u00A0чекбоксов", Formatters.formatCheckboxesCount(11))
        assertEquals("21\u00A0чекбокс", Formatters.formatCheckboxesCount(21))

        // Проверка бейджей со слайдерами и без
        assertEquals("1\u00A0чекбокс", Formatters.formatSimulationBadge(1, false))
        assertEquals("1\u00A0чекбокс\u00A0+\u00A0ползунки", Formatters.formatSimulationBadge(1, true))
        assertEquals("2\u00A0чекбокса\u00A0+\u00A0ползунки", Formatters.formatSimulationBadge(2, true))
        assertEquals("Ползунки настройки", Formatters.formatSimulationBadge(0, true))
    }
}
