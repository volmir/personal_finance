package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.DeferredAsset
import com.example.domain.FinanceCalculator
import com.example.domain.SimulatorScaleCalculator
import com.example.domain.TemporaryExpense
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.util.Formatters
import com.example.util.localization.StringsEn
import com.example.util.localization.StringsRu
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioSimulationTest {

    @Test
    fun testSimulatorDynamicScalesRecalculation() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        val savingsConfig = SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = metrics.cashFlow,
            monthlyIncome = input.monthlyIncome,
            monthlyExpenses = input.monthlyExpenses
        )
        assertTrue(savingsConfig.minValue < 0f)
        assertTrue(savingsConfig.maxValue > 0f)
        assertTrue(savingsConfig.stepSize > 0.0)
        assertTrue(savingsConfig.steps > 0)
        assertEquals(0.0, (savingsConfig.minValue % savingsConfig.stepSize.toFloat()).toDouble(), 0.001)
        assertEquals(0.0, (savingsConfig.maxValue % savingsConfig.stepSize.toFloat()).toDouble(), 0.001)

        val expensesConfig = SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = input.monthlyExpenses,
            monthlyIncome = input.monthlyIncome
        )
        assertTrue(expensesConfig.minValue < 0f)
        assertTrue(expensesConfig.maxValue > 0f)
        assertTrue(expensesConfig.stepSize > 0.0)
        assertTrue(expensesConfig.steps > 0)
        assertEquals(0.0, (expensesConfig.minValue % expensesConfig.stepSize.toFloat()).toDouble(), 0.001)
        assertEquals(0.0, (expensesConfig.maxValue % expensesConfig.stepSize.toFloat()).toDouble(), 0.001)

        val returnConfig = SimulatorScaleCalculator.calculateReturnConfig(
            realReturnPercent = metrics.realReturnPercent
        )
        assertTrue(returnConfig.minValue < 0f)
        assertTrue(returnConfig.maxValue > 0f)
        assertTrue(returnConfig.stepSize in 0.25..1.0)
        assertTrue(returnConfig.steps > 0)

        val dollarSavingsConfig = SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = 1500.0,
            monthlyIncome = 5000.0,
            monthlyExpenses = 3500.0
        )
        assertTrue(dollarSavingsConfig.stepSize in 50.0..250.0)
        assertTrue(dollarSavingsConfig.minValue <= -1500f)

        val dollarExpensesConfig = SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = 3500.0,
            monthlyIncome = 5000.0
        )
        assertTrue(dollarExpensesConfig.stepSize in 100.0..500.0)
        assertTrue(dollarExpensesConfig.maxValue >= 3500f)
    }

    @Test
    fun testSimulatorSliderLabelsNoDuplicateParentheses() {
        val ru = StringsRu
        val en = StringsEn

        assertEquals("рост", ru.fireSliderInc)
        assertEquals("сниж.", ru.fireSliderDec)
        assertFalse(ru.fireSliderInc.contains("("))
        assertFalse(ru.fireSliderInc.contains(")"))
        assertFalse(ru.fireSliderDec.contains("("))
        assertFalse(ru.fireSliderDec.contains(")"))
        assertEquals(ru.fireSliderInc.trim(), ru.fireSliderInc)
        assertEquals(ru.fireSliderDec.trim(), ru.fireSliderDec)

        assertEquals("growth", en.fireSliderInc)
        assertEquals("red.", en.fireSliderDec)
        assertFalse(en.fireSliderInc.contains("("))
        assertFalse(en.fireSliderInc.contains(")"))
        assertFalse(en.fireSliderDec.contains("("))
        assertFalse(en.fireSliderDec.contains(")"))
        assertEquals(en.fireSliderInc.trim(), en.fireSliderInc)
        assertEquals(en.fireSliderDec.trim(), en.fireSliderDec)

        assertEquals("до +10,50%", ru.fireSliderTo("10,50%"))
        assertEquals("до +10,50%", ru.fireSliderTo("+10,50%"))
        assertEquals("up to +10.50%", en.fireSliderTo("10.50%"))
        assertEquals("up to +10.50%", en.fireSliderTo("+10.50%"))
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

        // Потеря работы в симуляторе списывает 12 доходов из накоплений; базовый расчет не меняется.

        // 2. Обвал рынка на 30% (ETF -30%)
        val marketCrashInput = baseInput.copy(financialAssets = baseInput.financialAssets * 0.70)
        val marketCrashMetrics = FinanceCalculator.calculate(marketCrashInput)
        assertEquals(700_000.0, marketCrashInput.financialAssets, 0.01)
        assertTrue(marketCrashMetrics.yearsToRetirement >= baseMetrics.yearsToRetirement)

        // 3. Ипотека: временный платеж 35% дохода на 20 лет, без отложенного актива.
        val mortgageMonthly = 0.35 * baseInput.monthlyIncome
        val mortgageAnnual = mortgageMonthly * 12.0
        val mortgageAsset = 84.0 * baseInput.monthlyIncome
        val mortgageInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses + mortgageMonthly)
        val mortgageMetrics = FinanceCalculator.calculate(
            mortgageInput,
            temporaryExpenses = listOf(TemporaryExpense(annualAmount = mortgageAnnual, durationYears = 20.0))
        )
        assertEquals(85_000.0, mortgageInput.monthlyExpenses, 0.01)
        assertEquals(7.0, mortgageMetrics.yearsToRetirement - baseMetrics.yearsToRetirement, 0.3)

        // 4. Автомобиль в кредит: временный платеж 20% дохода на 5 лет, без отложенного актива.
        val carLoanMonthly = 0.20 * baseInput.monthlyIncome
        val carLoanAnnual = carLoanMonthly * 12.0
        val carLoanInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses + carLoanMonthly)
        val carLoanMetrics = FinanceCalculator.calculate(
            carLoanInput,
            temporaryExpenses = listOf(TemporaryExpense(annualAmount = carLoanAnnual, durationYears = 5.0))
        )
        assertEquals(70_000.0, carLoanInput.monthlyExpenses, 0.01)

        val carTrajectory = FinanceCalculator.generateTrajectory(
            carLoanMetrics,
            carLoanInput,
            temporaryExpenses = listOf(TemporaryExpense(annualAmount = carLoanAnnual, durationYears = 5.0))
        )
        assertTrue(carTrajectory.any { it.yearOffset >= 5.0 && it.accumulatedCapital > 0 })
        assertEquals(1.2, carLoanMetrics.yearsToRetirement - baseMetrics.yearsToRetirement, 0.2)

        // 5. Крупная разовая трата (500% дохода = 500 000, покрытие из ликвидных/фин. активов)
        val largeExpenseInput = baseInput.copy(liquidAssets = 0.0)
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
            listOf(TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0))
        )
        assertTrue(childMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)

        // Кумулятивное совместное влияние: Рост инфляции (+5%) + Ребёнок (+25% на 18 лет)
        val combinedExpensesInput = baseInput.copy(
            monthlyExpenses = baseInput.monthlyExpenses * 1.25,
            inflationPercent = baseInput.inflationPercent + 5.0
        )
        val combinedExpensesMetrics = FinanceCalculator.calculate(
            combinedExpensesInput,
            listOf(TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0))
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
                TemporaryExpense(annualAmount = mortgageAnnual, durationYears = 20.0),
                TemporaryExpense(annualAmount = childAnnual, durationYears = 18.0)
            ),
            deferredAssets = listOf(
                DeferredAsset(amount = mortgageAsset, afterYears = 20.0)
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

        // Потеря работы моделируется списанием накоплений, а не обнулением дохода.
        val jobLossOnly = baseInput.copy(monthlyIncome = 0.0)
        val jobLossMetrics = FinanceCalculator.calculate(jobLossOnly)

        val jobLossWithSavingsSlider = baseInput.copy(monthlyIncome = 15_000.0)
        val jobLossWithSavingsMetrics = FinanceCalculator.calculate(jobLossWithSavingsSlider)

        assertTrue(jobLossWithSavingsMetrics.cashFlow > jobLossMetrics.cashFlow)
        assertEquals(-35_000.0, jobLossWithSavingsMetrics.cashFlow, 0.01)

        val extraSavingsInput = baseInput.copy(monthlyIncome = baseInput.monthlyIncome + 20_000.0)
        val extraSavingsMetrics = FinanceCalculator.calculate(extraSavingsInput)
        assertTrue(extraSavingsMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)

        // Тест 2: Чекбоксы Инфляция (+20%) и Ребёнок (+25%) + Ползунок расходов (сокращение на 10 000)
        val checkboxesExpenses = baseInput.monthlyExpenses * 1.45
        val checkboxesAndSliderExpenses = checkboxesExpenses - 10_000.0
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
                TemporaryExpense(annualAmount = mortgageMonthlySync * 12.0, durationYears = 20.0),
                TemporaryExpense(annualAmount = 0.25 * baseInput.monthlyExpenses * 12.0, durationYears = 18.0)
            )
        )

        assertEquals(baseMetrics.realReturnPercent + 2.0, fullSyncMetrics.realReturnPercent, 0.01)
        val trajectory = FinanceCalculator.generateTrajectory(
            fullSyncMetrics,
            fullSyncInput,
            listOf(
                TemporaryExpense(annualAmount = mortgageMonthlySync * 12.0, durationYears = 20.0),
                TemporaryExpense(annualAmount = 0.25 * baseInput.monthlyExpenses * 12.0, durationYears = 18.0)
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
        assertEquals(0.6, baseMetrics.yearsToRetirement - childSelfSufficientMetrics.yearsToRetirement, 0.15)
        assertEquals("Дети станут самостоятельными", StringsRu.scenarioChildrenSelfSufficientTitle)
        assertEquals("расходы −25% через 18 лет (расходы на детей уже в ваших расходах)", StringsRu.scenarioChildrenSelfSufficientParam)
        assertEquals("Снижение семейных расходов: -AMOUNT/мес. (через 18 лет)", StringsRu.scenarioChildrenSelfSufficientDesc("AMOUNT"))
        assertEquals("Children Become Independent", StringsEn.scenarioChildrenSelfSufficientTitle)
        assertEquals("expenses −25% in 18 yrs (child costs already in your expenses)", StringsEn.scenarioChildrenSelfSufficientParam)
        assertEquals("Family expense reduction: -AMOUNT/mo. (in 18 yrs)", StringsEn.scenarioChildrenSelfSufficientDesc("AMOUNT"))

        // Совместное кумулятивное влияние: Зарплата +20% + Оптимизация расходов -15% + Рост рынка +25%
        val combinedPositiveInput = baseInput.copy(
            monthlyIncome = baseInput.monthlyIncome * 1.20,
            monthlyExpenses = baseInput.monthlyExpenses * 0.85,
            financialAssets = baseInput.financialAssets * 1.25
        )
        val combinedPositiveMetrics = FinanceCalculator.calculate(combinedPositiveInput)
        assertTrue(combinedPositiveMetrics.yearsToRetirement < salaryMetrics.yearsToRetirement)

        // Баланс позитивных и негативных событий
        val balancedInput = baseInput.copy(
            monthlyIncome = baseInput.monthlyIncome * 1.20,
            monthlyExpenses = baseInput.monthlyExpenses * (1.0 + 0.20 - 0.15)
        )
        val balancedMetrics = FinanceCalculator.calculate(balancedInput)
        assertTrue(balancedMetrics.cashFlow > baseMetrics.cashFlow)
        assertTrue(balancedMetrics.yearsToRetirement < baseMetrics.yearsToRetirement)
    }

    @Test
    fun testInflationScenarioRaisesExpensesWithoutChangingRealReturn() {
        val ru = StringsRu
        val en = StringsEn

        assertEquals("+25% к ежемесячным расходам", ru.scenarioInflationParam)
        assertEquals("+25% to monthly expenses", en.scenarioInflationParam)
        assertEquals("Расходы растут: +13,0/мес.", ru.scenarioInflationDesc("13,0"))
        assertEquals("Expenses increase: +13.0/mo.", en.scenarioInflationDesc("13.0"))

        val baseInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            nominalReturnPercent = 12.0,
            inflationPercent = 8.0
        )
        val baseMetrics = FinanceCalculator.calculate(baseInput)
        assertEquals(3.70, baseMetrics.realReturnPercent, 0.02)

        val stressInput = baseInput.copy(monthlyExpenses = baseInput.monthlyExpenses * 1.25)
        val stressMetrics = FinanceCalculator.calculate(stressInput)
        assertEquals(62_500.0, stressInput.monthlyExpenses, 0.01)
        assertEquals(baseMetrics.realReturnPercent, stressMetrics.realReturnPercent, 0.01)
        assertTrue(stressMetrics.yearsToRetirement > baseMetrics.yearsToRetirement)
        assertEquals(2.5, stressMetrics.yearsToRetirement - baseMetrics.yearsToRetirement, 0.2)

        assertEquals("Рост расходов (инфляция)", ru.scenarioInflationTitle)
        assertEquals("Expense Increase (Inflation)", en.scenarioInflationTitle)
        val optimizedRelocationExpenses = 60_000.0 * 0.85 * 0.70
        assertEquals(35_700.0, optimizedRelocationExpenses, 0.01)
    }

    @Test
    fun testFormatCheckboxesBadge() {
        assertEquals("1\u00A0чекбокс", Formatters.formatCheckboxesCount(1))
        assertEquals("2\u00A0чекбокса", Formatters.formatCheckboxesCount(2))
        assertEquals("3\u00A0чекбокса", Formatters.formatCheckboxesCount(3))
        assertEquals("4\u00A0чекбокса", Formatters.formatCheckboxesCount(4))
        assertEquals("5\u00A0чекбоксов", Formatters.formatCheckboxesCount(5))
        assertEquals("11\u00A0чекбоксов", Formatters.formatCheckboxesCount(11))
        assertEquals("21\u00A0чекбокс", Formatters.formatCheckboxesCount(21))

        assertEquals("1\u00A0чекбокс", Formatters.formatSimulationBadge(1, false))
        assertEquals("1\u00A0чекбокс\u00A0+\u00A0ползунки", Formatters.formatSimulationBadge(1, true))
        assertEquals("2\u00A0чекбокса\u00A0+\u00A0ползунки", Formatters.formatSimulationBadge(2, true))
        assertEquals("Ползунки настройки", Formatters.formatSimulationBadge(0, true))
    }

    @Test
    fun testAllFourteenScenariosSimultaneouslyActive() {
        // Создаем состояние со всеми 14 активными сценариями
        val allScenarios = FireScenarioToggles(
            // 7 позитивных
            salaryIncrease20 = true,
            marketGrowth25 = true,
            expenseOptimization15 = true,
            relocationLowerCost30 = true,
            inheritance1200 = true,
            propertySale2000 = true,
            childrenSelfSufficient18 = true,
            // 7 негативных
            jobLoss = true,
            marketCrash30 = true,
            mortgage20Years35Income = true,
            carLoan5Years = true,
            largeOneOffExpense500 = true,
            inflation5Percent = true,
            childOrDependent18Years = true
        )

        val baseInput = FinancialInputData(
            monthlyIncome = 120_000.0,
            monthlyExpenses = 60_000.0,
            liquidAssets = 300_000.0,
            financialAssets = 3_414_389.0,
            propertyAssets = 2_000_000.0,
            liabilities = 200_000.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 82,
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0,
            capitalGainsTaxPercent = 20.0
        )

        // Воспроизводим вычисление симуляции для всех 14 сценариев:
        val incomeMultiplier = 1.0 + (if (allScenarios.salaryIncrease20) 0.20 else 0.0)
        val baseIncome = baseInput.monthlyIncome * incomeMultiplier
        val effectiveIncome = maxOf(0.0, baseIncome)

        val marketMultiplier = (if (allScenarios.marketCrash30) 0.70 else 1.0) *
            (if (allScenarios.marketGrowth25) 1.25 else 1.0)
        val afterMarketFinAssets = baseInput.financialAssets * marketMultiplier.coerceAtLeast(0.0)
        assertEquals(3_414_389.0 * 0.70 * 1.25, afterMarketFinAssets, 1.0)

        val inheritanceAmount = if (allScenarios.inheritance1200) 12.0 * baseInput.monthlyIncome else 0.0
        val propertySaleAmount = if (allScenarios.propertySale2000) minOf(20.0 * baseInput.monthlyIncome, baseInput.propertyAssets) else 0.0
        val effectivePropertyAssets = baseInput.propertyAssets - propertySaleAmount
        val totalPositiveLumpSum = inheritanceAmount + propertySaleAmount

        val ongoingExpenseMultiplier = (if (allScenarios.expenseOptimization15) 0.85 else 1.0) *
            (if (allScenarios.relocationLowerCost30) 0.70 else 1.0) *
            (if (allScenarios.inflation5Percent) 1.25 else 1.0)
        val baseOngoingExpenses = baseInput.monthlyExpenses * ongoingExpenseMultiplier.coerceAtLeast(0.10)

        val mortgageMonthlyExpense = if (allScenarios.mortgage20Years35Income) 0.35 * baseInput.monthlyIncome else 0.0
        val carLoanMonthlyExpense = if (allScenarios.carLoan5Years) 0.20 * baseInput.monthlyIncome else 0.0
        val childMonthlyExpense = if (allScenarios.childOrDependent18Years) 0.25 * baseInput.monthlyExpenses else 0.0

        val currentEffectiveExpenses = baseOngoingExpenses + mortgageMonthlyExpense + carLoanMonthlyExpense + childMonthlyExpense
        val deferredReductionAnnual = if (allScenarios.childrenSelfSufficient18) {
            0.25 * baseInput.monthlyExpenses * 12.0
        } else 0.0

        val temporaryExpenses = mutableListOf<TemporaryExpense>()
        if (allScenarios.mortgage20Years35Income) {
            temporaryExpenses.add(TemporaryExpense(annualAmount = mortgageMonthlyExpense * 12.0, durationYears = 20.0))
        }
        if (allScenarios.carLoan5Years) {
            temporaryExpenses.add(TemporaryExpense(annualAmount = carLoanMonthlyExpense * 12.0, durationYears = 5.0))
        }
        if (allScenarios.childOrDependent18Years) {
            temporaryExpenses.add(TemporaryExpense(annualAmount = childMonthlyExpense * 12.0, durationYears = 18.0))
        }

        val deferredAssets = mutableListOf<DeferredAsset>()
        if (allScenarios.carLoan5Years) {
            deferredAssets.add(DeferredAsset(amount = 6.0 * baseInput.monthlyIncome, afterYears = 5.0))
        }
        if (allScenarios.mortgage20Years35Income) {
            deferredAssets.add(DeferredAsset(amount = 84.0 * baseInput.monthlyIncome, afterYears = 20.0))
        }

        var effLiquidAssets = baseInput.liquidAssets + totalPositiveLumpSum
        var effFinAssets = afterMarketFinAssets
        var uncoveredExpenseDebt = 0.0

        if (allScenarios.largeOneOffExpense500 || allScenarios.jobLoss) {
            val expenseTarget =
                (if (allScenarios.largeOneOffExpense500) 5.0 else 0.0) * baseInput.monthlyIncome +
                    (if (allScenarios.jobLoss) 12.0 else 0.0) * baseInput.monthlyIncome
            var remainingToCover = expenseTarget

            val coveredFromLiquid = minOf(effLiquidAssets, remainingToCover)
            effLiquidAssets -= coveredFromLiquid
            remainingToCover -= coveredFromLiquid

            val coveredFromFin = minOf(effFinAssets, remainingToCover)
            effFinAssets -= coveredFromFin
            remainingToCover -= coveredFromFin

            uncoveredExpenseDebt = remainingToCover
        }

        val effectiveLiabilities = baseInput.liabilities + uncoveredExpenseDebt
        val effectiveInflationPercent = baseInput.inflationPercent

        val simulatedInput = baseInput.copy(
            monthlyIncome = effectiveIncome,
            monthlyExpenses = currentEffectiveExpenses,
            liquidAssets = effLiquidAssets,
            financialAssets = effFinAssets,
            propertyAssets = effectivePropertyAssets,
            liabilities = effectiveLiabilities,
            inflationPercent = effectiveInflationPercent
        )

        val simCalc = FinanceCalculator.calculate(
            input = simulatedInput,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredReductionAnnual,
            deferredStartYears = 18.0,
            deferredAssets = deferredAssets
        )

        // Проверяем, что нет NaN и бесконечностей ни в одной из ключевых метрик
        assertFalse("cashFlow must not be NaN", simCalc.cashFlow.isNaN())
        assertFalse("cashFlow must not be Infinite", simCalc.cashFlow.isInfinite())

        assertFalse("savingsRate must not be NaN", simCalc.savingsRate.isNaN())
        assertFalse("savingsRate must not be Infinite", simCalc.savingsRate.isInfinite())

        assertFalse("totalAssets must not be NaN", simCalc.totalAssets.isNaN())
        assertFalse("totalAssets must not be Infinite", simCalc.totalAssets.isInfinite())

        assertFalse("netWorth must not be NaN", simCalc.netWorth.isNaN())
        assertFalse("netWorth must not be Infinite", simCalc.netWorth.isInfinite())

        assertFalse("realReturnPercent must not be NaN", simCalc.realReturnPercent.isNaN())
        assertFalse("realReturnPercent must not be Infinite", simCalc.realReturnPercent.isInfinite())

        assertFalse("yearsToRetirement must not be NaN", simCalc.yearsToRetirement.isNaN())
        assertFalse("yearsToRetirement must not be Infinite", simCalc.yearsToRetirement.isInfinite())

        assertFalse("retirementTargetCapital must not be NaN", simCalc.retirementTargetCapital.isNaN())
        assertFalse("retirementTargetCapital must not be Infinite", simCalc.retirementTargetCapital.isInfinite())

        assertFalse("retirementAge must not be NaN", simCalc.retirementAge.isNaN())
        assertFalse("retirementAge must not be Infinite", simCalc.retirementAge.isInfinite())

        val trajectory = FinanceCalculator.generateTrajectory(
            metrics = simCalc,
            input = simulatedInput,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredReductionAnnual,
            deferredStartYears = 18.0,
            deferredAssets = deferredAssets
        )

        // Проверяем каждую точку на графике
        assertTrue("Trajectory must not be empty", trajectory.isNotEmpty())
        for (point in trajectory) {
            assertFalse("point.accumulatedCapital must not be NaN", point.accumulatedCapital.isNaN())
            assertFalse("point.accumulatedCapital must not be Infinite", point.accumulatedCapital.isInfinite())
            assertFalse("point.requiredCapital must not be NaN", point.requiredCapital.isNaN())
            assertFalse("point.requiredCapital must not be Infinite", point.requiredCapital.isInfinite())
            assertFalse("point.age must not be NaN", point.age.isNaN())
            assertFalse("point.age must not be Infinite", point.age.isInfinite())
        }
    }
}
