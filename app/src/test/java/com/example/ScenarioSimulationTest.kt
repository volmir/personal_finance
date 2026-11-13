package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
import com.example.domain.SimulatorScaleCalculator
import com.example.domain.calculateSimulationOngoingExpenses
import com.example.domain.calculateScenarioAmounts
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.StringsEn
import com.example.util.localization.StringsRu
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ScenarioSimulationTest {
    private val dispatcher = StandardTestDispatcher()
    private val today = LocalDate.of(2026, 10, 31)
    private val viewModelStore = ViewModelStore()
    private lateinit var app: Application
    private val baseInput = FinancialInputData(
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
    // ID совпадают с ScenarioTogglesPanel: проверяем публичный путь включения сценариев.
    private val positiveScenarioIds = listOf(
        "pos_salary_increase", "pos_market_growth", "pos_expense_optimization",
        "pos_relocation", "pos_inheritance", "pos_property_sale", "pos_children_self_sufficient"
    )
    private val negativeScenarioIds = listOf(
        "job_loss", "market_crash", "mortgage", "car_loan", "large_expense", "inflation", "child_dependent"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
        Dispatchers.resetMain()
    }

    private fun createViewModel(input: FinancialInputData = baseInput): MainViewModel {
        val viewModel = MainViewModel(app) { today }
        viewModelStore.put("main", viewModel)
        viewModel.updateInputData { input }
        return viewModel
    }

    private fun simulate(viewModel: MainViewModel, vararg scenarioIds: String): CalculatedMetrics {
        viewModel.resetSimulation()
        scenarioIds.forEach(viewModel::toggleScenario)
        assertEquals(scenarioIds.size, viewModel.fireSimulation.value.scenarios.activeCount)
        assertFiniteSimulation(viewModel, scenarioIds.joinToString())
        return viewModel.simulatedMetrics.value
    }

    private fun assertFiniteSimulation(viewModel: MainViewModel, label: String) {
        val metrics = viewModel.simulatedMetrics.value
        val values = mapOf(
            "cashFlow" to metrics.cashFlow,
            "savingsRate" to metrics.savingsRate,
            "totalAssets" to metrics.totalAssets,
            "emergencyFund" to metrics.emergencyFund,
            "safetyMonths" to metrics.safetyMonths,
            "debtRatio" to metrics.debtRatio,
            "netWorth" to metrics.netWorth,
            "fidelityCapital" to metrics.fidelityCapital,
            "annualIncome" to metrics.annualIncome,
            "annualExpenses" to metrics.annualExpenses,
            "fidelityMultiplier" to metrics.fidelityMultiplier,
            "fidelityTarget" to metrics.fidelityTarget,
            "fidelityRatio" to metrics.fidelityRatio,
            "fidelityPercent" to metrics.fidelityPercent,
            "financialFreedomRatio" to metrics.financialFreedomRatio,
            "realReturnPercent" to metrics.realReturnPercent,
            "yearsToRetirement" to metrics.yearsToRetirement,
            "retirementAge" to metrics.retirementAge,
            "retirementTargetCapital" to metrics.retirementTargetCapital,
            "totalScore" to metrics.scores.totalScore,
            "capitalGainsTaxPercent" to metrics.capitalGainsTaxPercent,
            "effectiveCapitalGainsTaxRate" to metrics.effectiveCapitalGainsTaxRate,
            "capitalGainsGainSharePercent" to metrics.capitalGainsGainSharePercent
        )
        values.forEach { (name, value) ->
            assertTrue("$label: $name must be finite, got $value", value.isFinite())
        }
        val trajectory = viewModel.simulatedTrajectory.value
        assertTrue("$label: trajectory must not be empty", trajectory.isNotEmpty())
        trajectory.forEachIndexed { index, point ->
            assertTrue("$label: point[$index].age must be finite", point.age.isFinite())
            assertTrue("$label: point[$index].yearOffset must be finite", point.yearOffset.isFinite())
            assertTrue("$label: point[$index].accumulatedCapital must be finite", point.accumulatedCapital.isFinite())
            assertTrue("$label: point[$index].requiredCapital must be finite", point.requiredCapital.isFinite())
        }
    }

    @Test
    fun `shared scenario amounts use the correct income and property sale bounds`() {
        for (raise in listOf(false, true)) {
            for ((property, expectedSale) in listOf(0.0 to 0.0, 1_000.0 to 1_000.0, 120_000.0 to 120_000.0, 120_001.0 to 120_000.0)) {
                val amounts = calculateScenarioAmounts(6_000.0, property, raise)
                assertEquals(if (raise) 7_200.0 else 6_000.0, amounts.monthlyIncome, 0.0)
                assertEquals(1_200.0, amounts.salaryIncrease, 0.0)
                assertEquals(if (raise) 86_400.0 else 72_000.0, amounts.jobLoss, 0.0)
                assertEquals(if (raise) 2_520.0 else 2_100.0, amounts.mortgageMonthly, 0.0)
                assertEquals(if (raise) 1_440.0 else 1_200.0, amounts.carLoanMonthly, 0.0)
                assertEquals(expectedSale, amounts.propertySale, 0.0)
                assertEquals(72_000.0, amounts.inheritance, 0.0)
                assertEquals(30_000.0, amounts.largeOneOffExpense, 0.0)
            }
            val zero = calculateScenarioAmounts(0.0, 1_000.0, raise)
            assertEquals(0.0, zero.monthlyIncome, 0.0)
            assertEquals(0.0, zero.jobLoss, 0.0)
            assertEquals(0.0, zero.mortgageMonthly, 0.0)
            assertEquals(0.0, zero.carLoanMonthly, 0.0)
            assertEquals(0.0, zero.propertySale, 0.0)
        }
    }

    @Test
    fun `shared ongoing expenses preserve scenario multipliers slider and ten percent floor`() {
        data class Case(val optimization: Boolean, val relocation: Boolean, val inflation: Boolean, val before: Double)
        val cases = listOf(
            Case(false, false, false, 60_000.0),
            Case(true, false, false, 51_000.0),
            Case(false, true, false, 42_000.0),
            Case(false, false, true, 75_000.0),
            Case(true, true, false, 35_700.0),
            Case(true, false, true, 63_750.0),
            Case(false, true, true, 52_500.0),
            Case(true, true, true, 44_625.0)
        )
        for (case in cases) {
            for (reduction in listOf(-10_000.0, 0.0, 10_000.0, 54_000.0, 100_000.0)) {
                val expenses = calculateSimulationOngoingExpenses(
                    60_000.0, reduction, case.optimization, case.relocation, case.inflation
                )
                assertEquals(case.before, expenses.beforeReduction, 0.000001)
                assertEquals(maxOf(case.before - reduction, 6_000.0), expenses.effective, 0.000001)
            }
        }
    }

    @Test
    fun `shared ongoing expenses keep zero expenses and allow expense growth`() {
        assertEquals(0.0, calculateSimulationOngoingExpenses(0.0, 500.0, true, true, true).effective, 0.0)
        assertEquals(500.0, calculateSimulationOngoingExpenses(0.0, -500.0, true, true, true).effective, 0.0)
    }

    @Test
    fun testSimulatorDynamicScalesRecalculation() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)

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
        assertTrue(dollarExpensesConfig.maxValue > 0f)
        assertTrue(dollarExpensesConfig.maxValue <= 3500f * 0.9f)
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
    fun testEachScenarioProducesFiniteMetricsAndTrajectoryWithExpectedDirection() {
        val viewModel = createViewModel()
        val baseMetrics = viewModel.metrics.value
        assertEquals(FireStatus.REACHABLE, baseMetrics.fireStatus)
        for (scenarioId in positiveScenarioIds + negativeScenarioIds) {
            val simulated = simulate(viewModel, scenarioId)
            if (scenarioId in positiveScenarioIds) {
                assertTrue("$scenarioId must remain reachable", simulated.fireStatus != FireStatus.UNREACHABLE)
                assertTrue("$scenarioId must not delay retirement",
                    simulated.yearsToRetirement <= baseMetrics.yearsToRetirement)
            } else if (simulated.fireStatus != FireStatus.UNREACHABLE) {
                assertTrue("$scenarioId must not accelerate retirement",
                    simulated.yearsToRetirement >= baseMetrics.yearsToRetirement)
            }
            assertEquals(baseInput, viewModel.inputData.value)
            assertEquals(baseMetrics, viewModel.metrics.value)
        }
    }

    @Test
    fun testStressTestScenariosAccumulation() {
        val viewModel = createViewModel()
        val baseMetrics = viewModel.metrics.value

        val jobLoss = simulate(viewModel, "job_loss")
        assertEquals(1_200_000.0, baseMetrics.netWorth - jobLoss.netWorth, 0.01)
        assertEquals(baseMetrics.annualIncome, jobLoss.annualIncome, 0.01)

        val marketCrash = simulate(viewModel, "market_crash")
        assertEquals(1_200_000.0, marketCrash.fidelityCapital, 0.01)
        assertTrue(marketCrash.yearsToRetirement >= baseMetrics.yearsToRetirement)

        val mortgage = simulate(viewModel, "mortgage")
        assertEquals(85_000.0, mortgage.annualExpenses / 12.0, 0.01)
        assertTrue(mortgage.yearsToRetirement > baseMetrics.yearsToRetirement)

        val carLoan = simulate(viewModel, "car_loan")
        assertEquals(70_000.0, carLoan.annualExpenses / 12.0, 0.01)
        assertTrue(viewModel.simulatedTrajectory.value.any { it.yearOffset >= 5.0 && it.accumulatedCapital > 0 })
        assertEquals(1.2, carLoan.yearsToRetirement - baseMetrics.yearsToRetirement, 0.2)

        val largeExpense = simulate(viewModel, "large_expense")
        assertTrue(largeExpense.yearsToRetirement > baseMetrics.yearsToRetirement)

        // Сценарий inflation повышает расходы на 25%, а не инфляцию на 5 п.п.
        val inflation = simulate(viewModel, "inflation")
        assertEquals(62_500.0, inflation.annualExpenses / 12.0, 0.01)
        assertTrue(inflation.yearsToRetirement > baseMetrics.yearsToRetirement)
        assertEquals(baseMetrics.realReturnPercent, inflation.realReturnPercent, 0.000001)

        val child = simulate(viewModel, "child_dependent")
        assertTrue(child.yearsToRetirement > baseMetrics.yearsToRetirement)

        val combinedExpenses = simulate(viewModel, "inflation", "child_dependent")
        assertTrue(combinedExpenses.yearsToRetirement > inflation.yearsToRetirement)

        val complex = simulate(viewModel, "mortgage", "market_crash", "child_dependent")
        assertTrue(complex.yearsToRetirement > baseMetrics.yearsToRetirement)
    }

    @Test
    fun testScenariosAndSlidersSynchronousCumulativeEffect() {
        val viewModel = createViewModel()
        val baseMetrics = viewModel.metrics.value
        val jobLoss = simulate(viewModel, "job_loss")
        viewModel.updateSimulation(extraSavings = 15_000.0, realReturnDelta = 0.0, expReduction = 0.0)
        val jobLossWithSavings = viewModel.simulatedMetrics.value
        assertTrue(jobLossWithSavings.cashFlow > jobLoss.cashFlow)
        // Потеря работы списывает капитал, сохраняя доход; ползунок добавляет 15 000 к потоку.
        assertEquals(65_000.0, jobLossWithSavings.cashFlow, 0.01)
        assertEquals(jobLoss.annualIncome, jobLossWithSavings.annualIncome, 0.01)
        assertEquals(jobLoss.netWorth, jobLossWithSavings.netWorth, 0.01)
        assertTrue(jobLossWithSavings.yearsToRetirement < jobLoss.yearsToRetirement)
        assertFiniteSimulation(viewModel, "job_loss + savings slider")

        viewModel.resetSimulation()
        viewModel.updateSimulation(extraSavings = 20_000.0, realReturnDelta = 0.0, expReduction = 0.0)
        assertTrue(viewModel.simulatedMetrics.value.yearsToRetirement < baseMetrics.yearsToRetirement)
        assertEquals(baseMetrics.annualIncome, viewModel.simulatedMetrics.value.annualIncome, 0.01)
        assertFiniteSimulation(viewModel, "savings slider")

        simulate(viewModel, "inflation", "child_dependent")
        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 0.0, expReduction = 10_000.0)
        val combinedExpenses = viewModel.simulatedMetrics.value
        // Постоянные расходы растут на 25%; расходы на ребенка добавляются отдельно.
        assertEquals(65_000.0, combinedExpenses.annualExpenses / 12.0, 0.01)
        assertEquals(35_000.0, combinedExpenses.cashFlow, 0.01)
        assertFiniteSimulation(viewModel, "inflation + child_dependent + expense slider")

        viewModel.toggleScenario("mortgage")
        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 2.0, expReduction = 10_000.0)
        val fullSync = viewModel.simulatedMetrics.value
        assertEquals(100_000.0, fullSync.annualExpenses / 12.0, 0.01)
        assertEquals(baseMetrics.realReturnPercent + 2.0, fullSync.realReturnPercent, 0.01)
        assertFiniteSimulation(viewModel, "inflation + child_dependent + mortgage + sliders")
        assertEquals(baseMetrics.currentAge.toDouble(), viewModel.simulatedTrajectory.value.first().age, 0.01)
        assertEquals(baseInput, viewModel.inputData.value)
        assertEquals(baseMetrics, viewModel.metrics.value)
    }

    @Test
    fun `tab selection keeps scenario state input and base metrics unchanged`() {
        val viewModel = createViewModel()
        viewModel.toggleScenario("pos_salary_increase")
        viewModel.toggleScenario("job_loss")
        viewModel.updateSimulation(extraSavings = 500.0, realReturnDelta = 0.5, expReduction = 0.0)

        val input = viewModel.inputData.value
        val baseMetrics = viewModel.metrics.value
        val simulation = viewModel.fireSimulation.value
        val simulated = viewModel.simulatedMetrics.value
        assertTrue(simulation.isSimulating)
        assertTrue(simulation.scenarios.salaryIncrease20)
        assertTrue(simulation.scenarios.jobLoss)

        // Нижняя навигация (включая повторный вход в №4) не трогает сценарии,
        // симуляцию и базовый ввод.
        for (tab in AppTab.values() + listOf(AppTab.FIRE, AppTab.INPUT, AppTab.FIRE)) {
            viewModel.selectTab(tab)
            assertEquals(tab, viewModel.currentTab.value)
            assertEquals(input, viewModel.inputData.value)
            assertEquals(baseMetrics, viewModel.metrics.value)
            assertEquals(simulation, viewModel.fireSimulation.value)
            assertEquals(simulated, viewModel.simulatedMetrics.value)
        }
    }

    @Test
    fun testPositiveEventsAndCombinedScenarios() {
        val viewModel = createViewModel()
        val baseMetrics = viewModel.metrics.value

        val salary = simulate(viewModel, "pos_salary_increase")
        assertEquals(120_000.0, salary.annualIncome / 12.0, 0.01)
        assertTrue(salary.yearsToRetirement < baseMetrics.yearsToRetirement)

        val marketGrowth = simulate(viewModel, "pos_market_growth")
        assertEquals(1_750_000.0, marketGrowth.fidelityCapital, 0.01)
        assertTrue(marketGrowth.yearsToRetirement <= baseMetrics.yearsToRetirement)

        val optimization = simulate(viewModel, "pos_expense_optimization")
        assertEquals(42_500.0, optimization.annualExpenses / 12.0, 0.01)
        assertTrue(optimization.yearsToRetirement < baseMetrics.yearsToRetirement)

        val relocation = simulate(viewModel, "pos_relocation")
        assertEquals(35_000.0, relocation.annualExpenses / 12.0, 0.01)
        assertTrue(relocation.yearsToRetirement < optimization.yearsToRetirement)

        val inheritance = simulate(viewModel, "pos_inheritance")
        assertEquals(2_700_000.0, inheritance.fidelityCapital, 0.01)
        assertTrue(inheritance.yearsToRetirement < baseMetrics.yearsToRetirement)

        val propertySale = simulate(viewModel, "pos_property_sale")
        assertEquals(3_500_000.0, propertySale.fidelityCapital, 0.01)
        assertEquals(baseMetrics.netWorth, propertySale.netWorth, 0.01)
        assertTrue(propertySale.yearsToRetirement < inheritance.yearsToRetirement)

        val childrenSelfSufficient = simulate(viewModel, "pos_children_self_sufficient")
        assertTrue(childrenSelfSufficient.yearsToRetirement < baseMetrics.yearsToRetirement)
        assertEquals("Дети станут самостоятельными", StringsRu.scenarioChildrenSelfSufficientTitle)
        assertEquals("расходы −25% через 18 лет (расходы на детей уже в ваших расходах)", StringsRu.scenarioChildrenSelfSufficientParam("25", "18"))
        assertEquals("Снижение семейных расходов: -AMOUNT/мес. (через 18 лет)", StringsRu.scenarioChildrenSelfSufficientDesc("AMOUNT", "18"))
        assertEquals("Children Become Independent", StringsEn.scenarioChildrenSelfSufficientTitle)
        assertEquals("expenses −25% in 18 yrs (child costs already in your expenses)", StringsEn.scenarioChildrenSelfSufficientParam("25", "18"))
        assertEquals("Family expense reduction: -AMOUNT/mo. (in 18 yrs)", StringsEn.scenarioChildrenSelfSufficientDesc("AMOUNT", "18"))

        val combinedPositive = simulate(viewModel, "pos_salary_increase", "pos_expense_optimization", "pos_market_growth")
        assertTrue(combinedPositive.yearsToRetirement < salary.yearsToRetirement)

        val balanced = simulate(viewModel, "pos_salary_increase", "pos_expense_optimization", "inflation")
        // В старой ручной сумме было 52 500; production последовательно применяет множители.
        assertEquals(53_125.0, balanced.annualExpenses / 12.0, 0.01)
        assertTrue(balanced.cashFlow > baseMetrics.cashFlow)
        assertTrue(balanced.yearsToRetirement < baseMetrics.yearsToRetirement)
    }

    @Test
    fun testInflationScenarioRaisesExpensesWithoutChangingRealReturn() {
        val ru = StringsRu
        val en = StringsEn

        assertEquals("+25% к ежемесячным расходам", ru.scenarioInflationParam("25"))
        assertEquals("+25% to monthly expenses", en.scenarioInflationParam("25"))
        assertEquals("Расходы растут: +13,0/мес.", ru.scenarioInflationDesc("13,0"))
        assertEquals("Expenses increase: +13.0/mo.", en.scenarioInflationDesc("13.0"))

        val viewModel = createViewModel(FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            nominalReturnPercent = 12.0,
            inflationPercent = 8.0
        ))
        val baseMetrics = viewModel.metrics.value
        assertEquals(3.70, baseMetrics.realReturnPercent, 0.02)

        val stress = simulate(viewModel, "inflation")
        assertEquals(62_500.0, stress.annualExpenses / 12.0, 0.01)
        assertEquals(baseMetrics.realReturnPercent, stress.realReturnPercent, 0.01)
        assertTrue(stress.yearsToRetirement > baseMetrics.yearsToRetirement)

        assertEquals("Рост расходов", ru.scenarioInflationTitle)
        assertEquals("Expense Increase", en.scenarioInflationTitle)
        viewModel.updateInputData { it.copy(monthlyExpenses = 60_000.0) }
        val optimizedRelocation = simulate(viewModel, "pos_expense_optimization", "pos_relocation")
        assertEquals(35_700.0, optimizedRelocation.annualExpenses / 12.0, 0.01)
    }

    @Test
    fun testFormatCheckboxesBadge() {
        assertEquals("1\u00A0чекбокс", Formatters.formatCheckboxesCount(1, AppLanguage.RU))
        assertEquals("2\u00A0чекбокса", Formatters.formatCheckboxesCount(2, AppLanguage.RU))
        assertEquals("3\u00A0чекбокса", Formatters.formatCheckboxesCount(3, AppLanguage.RU))
        assertEquals("4\u00A0чекбокса", Formatters.formatCheckboxesCount(4, AppLanguage.RU))
        assertEquals("5\u00A0чекбоксов", Formatters.formatCheckboxesCount(5, AppLanguage.RU))
        assertEquals("11\u00A0чекбоксов", Formatters.formatCheckboxesCount(11, AppLanguage.RU))
        assertEquals("21\u00A0чекбокс", Formatters.formatCheckboxesCount(21, AppLanguage.RU))

        assertEquals("1\u00A0чекбокс", Formatters.formatSimulationBadge(1, false, AppLanguage.RU))
        assertEquals("1\u00A0чекбокс\u00A0+\u00A0ползунки", Formatters.formatSimulationBadge(1, true, AppLanguage.RU))
        assertEquals("2\u00A0чекбокса\u00A0+\u00A0ползунки", Formatters.formatSimulationBadge(2, true, AppLanguage.RU))
        assertEquals("Ползунки", Formatters.formatSimulationBadge(0, true, AppLanguage.RU))
    }

    @Test
    fun testAllFourteenScenariosSimultaneouslyActive() {
        val viewModel = createViewModel(FinancialInputData(
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
        ))
        val input = viewModel.inputData.value
        val baseMetrics = viewModel.metrics.value
        simulate(viewModel, *(positiveScenarioIds + negativeScenarioIds).toTypedArray())
        assertEquals(7, viewModel.fireSimulation.value.scenarios.positiveActiveCount)
        assertEquals(7, viewModel.fireSimulation.value.scenarios.negativeActiveCount)

        // Сравниваем результат списания в том же наборе сценариев с повышенным доходом.
        val amounts = calculateScenarioAmounts(input.monthlyIncome, input.propertyAssets, true)
        val withJobLoss = viewModel.simulatedMetrics.value
        val capitalWithJobLoss = viewModel.simulatedTrajectory.value.first().accumulatedCapital
        assertEquals(amounts.monthlyIncome * 12.0, withJobLoss.annualIncome, 0.01)
        assertEquals(12.0 * amounts.monthlyIncome, amounts.jobLoss, 0.01)
        viewModel.toggleScenario("job_loss")
        assertEquals(amounts.jobLoss, viewModel.simulatedMetrics.value.netWorth - withJobLoss.netWorth, 0.01)
        assertEquals(amounts.jobLoss,
            viewModel.simulatedTrajectory.value.first().accumulatedCapital - capitalWithJobLoss, 0.01)
        assertEquals(input, viewModel.inputData.value)
        assertEquals(baseMetrics, viewModel.metrics.value)
    }

    @Test
    fun testJobLossUsesRaisedIncomeAcrossLiquidFinancialAndUncoveredDebtBoundaries() {
        val viewModel = createViewModel()
        for ((liquid, financial) in listOf(
            1_440_001.0 to 0.0, 1_440_000.0 to 0.0,
            500_000.0 to 940_000.0, 500_000.0 to 939_999.0, 0.0 to 0.0
        )) {
            viewModel.resetSimulation()
            // Без имущества весь непокрытый долг виден и в стартовом FIRE-капитале.
            viewModel.updateInputData {
                baseInput.copy(liquidAssets = liquid, financialAssets = financial, propertyAssets = 0.0)
            }
            val input = viewModel.inputData.value
            val baseMetrics = viewModel.metrics.value
            val amounts = calculateScenarioAmounts(input.monthlyIncome, input.propertyAssets, true)
            val raised = simulate(viewModel, "pos_salary_increase")
            val raisedCapital = viewModel.simulatedTrajectory.value.first().accumulatedCapital
            viewModel.toggleScenario("job_loss")
            val jobLoss = viewModel.simulatedMetrics.value

            assertEquals(1_440_000.0, amounts.jobLoss, 0.01)
            assertEquals(12.0 * amounts.monthlyIncome, amounts.jobLoss, 0.01)
            assertEquals(amounts.jobLoss, raised.netWorth - jobLoss.netWorth, 0.01)
            assertEquals(amounts.jobLoss, raised.fidelityCapital - jobLoss.fidelityCapital, 0.01)
            assertEquals(maxOf(0.0, raisedCapital - amounts.jobLoss),
                viewModel.simulatedTrajectory.value.first().accumulatedCapital, 0.01)
            assertEquals(raised.annualIncome, jobLoss.annualIncome, 0.01)
            assertEquals(input, viewModel.inputData.value)
            assertEquals(baseMetrics, viewModel.metrics.value)
        }
    }
}
