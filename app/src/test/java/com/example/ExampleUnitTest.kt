package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.AuditIndicatorStatus
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testUSAverageDefaults() {
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

        // 6. Рост инфляции (+5% к инфляции)
        val inflationInput = baseInput.copy(inflationPercent = baseInput.inflationPercent + 5.0)
        val inflationMetrics = FinanceCalculator.calculate(inflationInput)
        assertTrue(inflationMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)
        assertTrue(inflationMetrics.realReturnPercent < baseMetrics.realReturnPercent)

        // 7. Рождение ребенка (+25% на 18 лет)
        val childAnnual = baseInput.monthlyExpenses * 0.25 * 12.0
        val childInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 1.25)
        val childMetrics = FinanceCalculator.calculate(
            childInput,
            listOf(com.example.domain.TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0))
        )
        assertTrue(childMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)

        // Кумулятивное совместное влияние: Рост инфляции (+5%) + Ребёнок (+25% на 18 лет)
        val combinedExpensesInput = baseInput.copy(
            monthlyExpenses = baseInput.monthlyExpenses * 1.25,
            inflationPercent = baseInput.inflationPercent + 5.0
        )
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

    @Test
    fun testAuditorAnalysisAllMetricsGrouping() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        val opinion = AuditorAnalysisGenerator.generate(input, metrics)

        // Должны быть проанализированы все 9 показателей из экрана "Показатели"
        assertEquals(9, opinion.totalCount)
        assertEquals(9, opinion.positive.size + opinion.neutral.size + opinion.negative.size)

        // Проверяем наличие всех 9 метрик по id
        val allIds = (opinion.positive + opinion.neutral + opinion.negative).map { it.id }.toSet()
        val expectedIds = setOf(
            "cash_flow",
            "savings_rate",
            "emergency_fund",
            "safety_months",
            "debt_ratio",
            "net_worth",
            "fidelity",
            "cfs",
            "real_return"
        )
        assertEquals(expectedIds, allIds)

        // Проверяем динамическое реагирование при смене вводных данных:
        // Если расходы резко превышают доходы, денежный поток и норма сбережений становятся негативными
        val stressedInput = input.copy(
            monthlyIncome = 50_000.0,
            monthlyExpenses = 100_000.0,
            liabilities = 10_000_000.0 // огромный долг
        )
        val stressedMetrics = FinanceCalculator.calculate(stressedInput)
        val stressedOpinion = AuditorAnalysisGenerator.generate(stressedInput, stressedMetrics)

        assertEquals(9, stressedOpinion.totalCount)
        assertTrue(stressedOpinion.negative.any { it.id == "cash_flow" })
        assertTrue(stressedOpinion.negative.any { it.id == "savings_rate" })
        assertTrue(stressedOpinion.negative.any { it.id == "debt_ratio" })
    }

    @Test
    fun testExportSummaryContents() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics)

        // 4 обязательных блока
        assertTrue(export.contains("1. Исходные данные:"))
        assertTrue(export.contains("2. Рассчитанные показатели:"))
        assertTrue(export.contains("3. Набранные баллы:"))
        assertTrue(export.contains("4. Финансовая свобода, FIRE:"))

        // Аудиторское заключение и симуляцию в экспорт НЕ включаем
        assertFalse(export.contains("Аудиторское заключение"))
        assertFalse(export.contains("Симуляция"))
        assertFalse(export.contains("Стресс-тест"))
        assertFalse(export.contains("чекбокс"))
    }

    @Test
    fun testSavingsRateScoreCommentsLocalization() {
        val ruStrings = com.example.util.localization.StringsRu
        val enStrings = com.example.util.localization.StringsEn

        // При норме сбережений 8% (низкое значение)
        val commentRu8 = ruStrings.savingsRateScoreComment(8.0, "8,0%")
        assertTrue(commentRu8.contains("низкое значение (8,0%)"))

        val commentEn8 = enStrings.savingsRateScoreComment(8.0, "8.0%")
        assertTrue(commentEn8.contains("low rate (8.0%)"))

        // При норме сбережений 25% (отличное значение)
        val commentRu25 = ruStrings.savingsRateScoreComment(25.0, "25,0%")
        assertTrue(commentRu25.contains("отличное значение (25,0%)"))

        val commentEn25 = enStrings.savingsRateScoreComment(25.0, "25.0%")
        assertTrue(commentEn25.contains("excellent rate (25.0%)"))

        // При отрицательной норме сбережений (-10%)
        val commentRuNeg = ruStrings.savingsRateScoreComment(-10.0, "-10,0%")
        assertTrue(commentRuNeg.contains("отрицательное значение"))

        val commentEnNeg = enStrings.savingsRateScoreComment(-10.0, "-10.0%")
        assertTrue(commentEnNeg.contains("negative rate"))
    }

    @Test
    fun testSafetyMonthsScoreCommentsLocalization() {
        val ruStrings = com.example.util.localization.StringsRu
        val enStrings = com.example.util.localization.StringsEn

        // Запас 1.5 мес (недостаточный)
        val commentRuLow = ruStrings.safetyMonthsScoreComment(1.5, "1,5 мес.")
        assertTrue(commentRuLow.contains("недостаточный запас"))

        val commentEnLow = enStrings.safetyMonthsScoreComment(1.5, "1.5 mo.")
        assertTrue(commentEnLow.contains("insufficient buffer"))

        // Запас 15 мес (отличный)
        val commentRuHigh = ruStrings.safetyMonthsScoreComment(15.0, "15,0 мес.")
        assertTrue(commentRuHigh.contains("запас превышает 12 месяцев"))

        val commentEnHigh = enStrings.safetyMonthsScoreComment(15.0, "15.0 mo.")
        assertTrue(commentEnHigh.contains("buffer exceeds 12 months"))
    }

    @Test
    fun testCfsCalculatedWithNetAssets() {
        // Проверка: КФС рассчитывается исходя из Чистых активов (netWorth), а не просто суммарных активов
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0, // годовые расходы = 600 000
            liquidAssets = 200_000.0,
            financialAssets = 300_000.0,
            propertyAssets = 1_500_000.0, // сумма активов = 2 000 000
            liabilities = 800_000.0 // чистые активы = 1 200 000
        )
        val metrics = FinanceCalculator.calculate(input)

        // Чистые активы = 1 200 000. Годовые расходы = 600 000. КФС = 1 200 000 / 600 000 = 2.0
        assertEquals(1_200_000.0, metrics.netWorth, 0.01)
        assertEquals(2.0, metrics.financialFreedomRatio, 0.01)

        // При отрицательном чистом капитале КФС становится отрицательным
        val insolventInput = input.copy(
            liabilities = 3_000_000.0 // чистые активы = 2 000 000 - 3 000 000 = -1 000 000
        )
        val insolventMetrics = FinanceCalculator.calculate(insolventInput)
        assertTrue(insolventMetrics.netWorth < 0)
        assertTrue(insolventMetrics.financialFreedomRatio < 0)
        assertEquals(0, insolventMetrics.scores.cfsScore)
        assertEquals("КФС < 0 — Вы в долгах", FinanceCalculator.getCfsStageDescription(insolventMetrics.financialFreedomRatio, com.example.util.localization.AppLanguage.RU))
        assertEquals("CFS < 0 — You are in debt", FinanceCalculator.getCfsStageDescription(insolventMetrics.financialFreedomRatio, com.example.util.localization.AppLanguage.EN))
    }

    @Test
    fun testNegativeRealReturnCalculation() {
        val input = FinancialInputData(
            nominalReturnPercent = 5.0,
            inflationPercent = 12.0
        )
        val metrics = FinanceCalculator.calculate(input)

        // (1 + 0.05) / (1 + 0.12) - 1 = 1.05 / 1.12 - 1 = -0.0625 (-6.25%)
        assertTrue(metrics.realReturnPercent < 0.0)
        assertEquals(-6.25, metrics.realReturnPercent, 0.01)

        val opinion = AuditorAnalysisGenerator.generate(input, metrics)
        val realReturnOpinion = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "real_return" }
        assertEquals(AuditIndicatorStatus.NEGATIVE, realReturnOpinion.status)
    }

    @Test
    fun testSavingsRateThresholds() {
        // 1. Меньше 0: негативный показатель
        val negInput = FinancialInputData(monthlyIncome = 50_000.0, monthlyExpenses = 60_000.0)
        val negMetrics = FinanceCalculator.calculate(negInput)
        assertTrue(negMetrics.savingsRate < 0.0)
        assertEquals(0, negMetrics.scores.savingsRateScore)
        val negOpinion = AuditorAnalysisGenerator.generate(negInput, negMetrics)
        val negSr = (negOpinion.positive + negOpinion.neutral + negOpinion.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEGATIVE, negSr.status)

        // 2. От 0 до 20: нейтральный показатель (например 8% и 20%)
        val neutralInput8 = FinancialInputData(monthlyIncome = 100_000.0, monthlyExpenses = 92_000.0)
        val neutralMetrics8 = FinanceCalculator.calculate(neutralInput8)
        assertEquals(8.0, neutralMetrics8.savingsRate, 0.01)
        assertEquals(6, neutralMetrics8.scores.savingsRateScore) // 5..7 диапазон (желтый)
        val opinion8 = AuditorAnalysisGenerator.generate(neutralInput8, neutralMetrics8)
        val sr8 = (opinion8.positive + opinion8.neutral + opinion8.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEUTRAL, sr8.status)

        val neutralInput20 = FinancialInputData(monthlyIncome = 100_000.0, monthlyExpenses = 80_000.0)
        val neutralMetrics20 = FinanceCalculator.calculate(neutralInput20)
        assertEquals(20.0, neutralMetrics20.savingsRate, 0.01)
        assertEquals(7, neutralMetrics20.scores.savingsRateScore)
        val opinion20 = AuditorAnalysisGenerator.generate(neutralInput20, neutralMetrics20)
        val sr20 = (opinion20.positive + opinion20.neutral + opinion20.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEUTRAL, sr20.status)

        // 3. Больше 20: позитивный показатель (например 25%)
        val posInput25 = FinancialInputData(monthlyIncome = 100_000.0, monthlyExpenses = 75_000.0)
        val posMetrics25 = FinanceCalculator.calculate(posInput25)
        assertEquals(25.0, posMetrics25.savingsRate, 0.01)
        assertEquals(8, posMetrics25.scores.savingsRateScore) // 8..10 диапазон (зеленый)
        val opinion25 = AuditorAnalysisGenerator.generate(posInput25, posMetrics25)
        val sr25 = (opinion25.positive + opinion25.neutral + opinion25.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.POSITIVE, sr25.status)
    }

    @Test
    fun testTotalScoreColorGradation() {
        // >= 7.0: SuccessGreen
        assertEquals(com.example.ui.theme.SuccessGreen, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(7.0))
        assertEquals(com.example.ui.theme.SuccessGreen, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(8.5))
        assertEquals(com.example.ui.theme.SuccessGreen, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(10.0))

        // 3.5 .. < 7.0: WarningYellow
        assertEquals(com.example.ui.theme.WarningYellow, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(3.5))
        assertEquals(com.example.ui.theme.WarningYellow, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(5.0))
        assertEquals(com.example.ui.theme.WarningYellow, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(6.9))

        // < 3.5: AlertRed
        assertEquals(com.example.ui.theme.AlertRed, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(3.4))
        assertEquals(com.example.ui.theme.AlertRed, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(2.0))
        assertEquals(com.example.ui.theme.AlertRed, com.example.ui.theme.ScoreColorEvaluator.getTotalScoreColor(0.0))
    }

    @Test
    fun testSimulatorSliderLabelsNoDuplicateParentheses() {
        val ru = com.example.util.localization.StringsRu
        val en = com.example.util.localization.StringsEn

        // Проверяем русские строки: чистые слова без скобок и пробелов
        assertEquals("рост", ru.fireSliderInc)
        assertEquals("сниж.", ru.fireSliderDec)
        assertFalse(ru.fireSliderInc.contains("("))
        assertFalse(ru.fireSliderInc.contains(")"))
        assertFalse(ru.fireSliderDec.contains("("))
        assertFalse(ru.fireSliderDec.contains(")"))
        assertEquals(ru.fireSliderInc.trim(), ru.fireSliderInc)
        assertEquals(ru.fireSliderDec.trim(), ru.fireSliderDec)

        // Проверяем английские строки: чистые слова без скобок и пробелов
        assertEquals("growth", en.fireSliderInc)
        assertEquals("red.", en.fireSliderDec)
        assertFalse(en.fireSliderInc.contains("("))
        assertFalse(en.fireSliderInc.contains(")"))
        assertFalse(en.fireSliderDec.contains("("))
        assertFalse(en.fireSliderDec.contains(")"))
        assertEquals(en.fireSliderInc.trim(), en.fireSliderInc)
        assertEquals(en.fireSliderDec.trim(), en.fireSliderDec)

        // Проверяем форматирование "до +"
        assertEquals("до +10,50%", ru.fireSliderTo("10,50%"))
        assertEquals("до +10,50%", ru.fireSliderTo("+10,50%"))
        assertEquals("up to +10.50%", en.fireSliderTo("10.50%"))
        assertEquals("up to +10.50%", en.fireSliderTo("+10.50%"))
    }

    @Test
    fun testInflationScenario5PercentLogic() {
        val ru = com.example.util.localization.StringsRu
        val en = com.example.util.localization.StringsEn

        assertEquals("+5% к инфляции", ru.scenarioInflationParam)
        assertEquals("+5% to inflation", en.scenarioInflationParam)
        assertTrue(ru.scenarioInflationDesc("13,0%").contains("13,0%"))
        assertTrue(en.scenarioInflationDesc("13.0%").contains("13.0%"))

        // Базовые параметры: 12% доходность, 8% инфляция
        val baseInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            nominalReturnPercent = 12.0,
            inflationPercent = 8.0
        )
        val baseMetrics = FinanceCalculator.calculate(baseInput)
        // (1 + 0.12) / (1 + 0.08) - 1 = 1.12 / 1.08 - 1 = +3.7037%
        assertEquals(3.70, baseMetrics.realReturnPercent, 0.02)

        // Стресс-тест инфляции (+5%): инфляция становится 13%
        val stressInput = baseInput.copy(inflationPercent = baseInput.inflationPercent + 5.0)
        val stressMetrics = FinanceCalculator.calculate(stressInput)
        // Расходы не должны увеличиваться (+20% удалено)
        assertEquals(baseInput.monthlyExpenses, stressInput.monthlyExpenses, 0.01)
        assertEquals(baseMetrics.cashFlow, stressMetrics.cashFlow, 0.01)

        // (1 + 0.12) / (1 + 0.13) - 1 = 1.12 / 1.13 - 1 = -0.885%
        assertEquals(-0.88, stressMetrics.realReturnPercent, 0.02)
        assertTrue(stressMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)
    }

    @Test
    fun testInvestmentsCategoriesDevelopedAndEmergingBenchmarks() {
        val ru = com.example.util.localization.StringsRu
        val en = com.example.util.localization.StringsEn

        val ruAllText = ru.investmentsCategories.flatMap { it.items }.joinToString(" ")
        val enAllText = en.investmentsCategories.flatMap { it.items }.joinToString(" ")

        // Проверяем наличие ориентиров для развитых и развивающихся стран
        assertTrue(ruAllText.contains("развитых стран"))
        assertTrue(ruAllText.contains("развивающихся рынков"))
        assertTrue(enAllText.contains("Developed markets"))
        assertTrue(enAllText.contains("Emerging markets"))

        // Проверяем отсутствие специфичной привязки к рублям / РФ
        assertFalse(ruAllText.contains("рублях"))
        assertFalse(enAllText.contains("rubles"))
        assertFalse(ruAllText.contains("ЦБ"))
    }

    @Test
    fun testFidelityCalculationAndColorGradation() {
        // 1. Проверка расчета % и коэффициента (может быть больше 100%)
        val target = 2_000_000.0

        // 150% от цели (капитал 3 000 000)
        val ratio150 = com.example.domain.FinancialMetricEvaluator.calculateFidelityRatio(3_000_000.0, target)
        val percent150 = com.example.domain.FinancialMetricEvaluator.calculateFidelityPercent(3_000_000.0, target)
        assertEquals(1.5, ratio150, 0.001)
        assertEquals(150.0, percent150, 0.001)
        assertEquals(com.example.domain.AuditIndicatorStatus.POSITIVE, com.example.domain.FinancialMetricEvaluator.getFidelityStatus(3_000_000.0, target))
        assertEquals(com.example.ui.theme.SuccessGreen, com.example.domain.FinancialMetricEvaluator.getFidelityColor(3_000_000.0, target))

        // 80% от цели (капитал 1 600 000) - нейтральный (желтый)
        val ratio80 = com.example.domain.FinancialMetricEvaluator.calculateFidelityRatio(1_600_000.0, target)
        val percent80 = com.example.domain.FinancialMetricEvaluator.calculateFidelityPercent(1_600_000.0, target)
        assertEquals(0.8, ratio80, 0.001)
        assertEquals(80.0, percent80, 0.001)
        assertEquals(com.example.domain.AuditIndicatorStatus.NEUTRAL, com.example.domain.FinancialMetricEvaluator.getFidelityStatus(1_600_000.0, target))
        assertEquals(com.example.ui.theme.WarningYellow, com.example.domain.FinancialMetricEvaluator.getFidelityColor(1_600_000.0, target))

        // 40% от цели (капитал 800 000) - негативный (красный)
        val ratio40 = com.example.domain.FinancialMetricEvaluator.calculateFidelityRatio(800_000.0, target)
        val percent40 = com.example.domain.FinancialMetricEvaluator.calculateFidelityPercent(800_000.0, target)
        assertEquals(0.4, ratio40, 0.001)
        assertEquals(40.0, percent40, 0.001)
        assertEquals(com.example.domain.AuditIndicatorStatus.NEGATIVE, com.example.domain.FinancialMetricEvaluator.getFidelityStatus(800_000.0, target))
        assertEquals(com.example.ui.theme.AlertRed, com.example.domain.FinancialMetricEvaluator.getFidelityColor(800_000.0, target))

        // Отрицательный капитал (-200 000)
        val ratioNeg = com.example.domain.FinancialMetricEvaluator.calculateFidelityRatio(-200_000.0, target)
        val percentNeg = com.example.domain.FinancialMetricEvaluator.calculateFidelityPercent(-200_000.0, target)
        assertEquals(-0.1, ratioNeg, 0.001)
        assertEquals(-10.0, percentNeg, 0.001)
        assertEquals(com.example.domain.AuditIndicatorStatus.NEGATIVE, com.example.domain.FinancialMetricEvaluator.getFidelityStatus(-200_000.0, target))
        assertEquals(com.example.ui.theme.AlertRed, com.example.domain.FinancialMetricEvaluator.getFidelityColor(-200_000.0, target))

        // 2. Интеграция в модель и аудит: капитал выше 100% отображается в отчете и аудите
        val input = FinancialInputData(
            birthDate = "1990-01-01", // возраст ~36 лет, множитель Fidelity = 1.0 (или 2.0 в зависимости от сетки)
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 2_000_000.0,
            financialAssets = 3_000_000.0 // netWorth = 5 000 000
        )
        val metrics = com.example.domain.FinanceCalculator.calculate(input)
        assertTrue(metrics.fidelityTarget > 0.0)
        // Капитал 5 млн превышает цель (1.2 млн или 2.4 млн)
        assertTrue(metrics.fidelityPercent > 100.0)
        val opinion = com.example.domain.AuditorAnalysisGenerator.generate(input, metrics, com.example.util.localization.AppLanguage.RU)
        val fidelityItem = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "fidelity" }
        assertEquals(com.example.domain.AuditIndicatorStatus.POSITIVE, fidelityItem.status)
        assertTrue(fidelityItem.text.contains("% от цели"))
    }

    @Test
    fun testFidelityDescriptionFootnote() {
        val ruStrings = com.example.util.localization.getAppStrings(com.example.util.localization.AppLanguage.RU)
        assertTrue(ruStrings.metricFidelityDesc.contains("не связано с компанией Fidelity Investments"))
        assertTrue(ruStrings.metricFidelityDesc.contains("открытые исследовательские ориентиры"))

        val enStrings = com.example.util.localization.getAppStrings(com.example.util.localization.AppLanguage.EN)
        assertTrue(enStrings.metricFidelityDesc.contains("not affiliated with or endorsed by Fidelity Investments"))
        assertTrue(enStrings.metricFidelityDesc.contains("publicly available research milestones"))
    }
}
