package com.example

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.TrajectoryPoint
import com.example.data.model.FireStatus
import com.example.ui.MainViewModel
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.ProjectionEngine
import com.example.domain.ProjectionParams
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.screens.InputDataScreen
import com.example.ui.screens.MetricsScreen
import com.example.ui.theme.StatusColors
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FinancialConsistencyIntegrationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val today = LocalDate.of(2026, 10, 31)

    private data class Profile(val name: String, val input: FinancialInputData, val fireStatus: FireStatus? = null)

    private fun profiles(): List<Profile> {
        val base = FinancialInputData.usAverageData()
        return listOf(
            Profile("defaults", base),
            Profile("zero income", base.copy(monthlyIncome = 0.0)),
            Profile("zero expenses", base.copy(monthlyExpenses = 0.0), FireStatus.REACHED_NOW),
            Profile("negative net worth", base.copy(liabilities = 400_000.0)),
            Profile("zero assets with debt", base.copy(liquidAssets = 0.0, financialAssets = 0.0, propertyAssets = 0.0, liabilities = 1_000.0)),
            Profile("unreachable", base.copy(monthlyIncome = 100.0, liquidAssets = 1_000.0, financialAssets = 1_000.0,
                propertyAssets = 0.0, liabilities = 0.0, nominalReturnPercent = 0.0, inflationPercent = 0.0), FireStatus.UNREACHABLE),
            Profile("reached now", base.copy(liquidAssets = 10_000_000.0, propertyAssets = 0.0, liabilities = 0.0), FireStatus.REACHED_NOW),
            Profile("invalid date", base.copy(birthDate = "invalid"), FireStatus.INVALID_AGE),
            Profile("zero cash flow", base.copy(monthlyIncome = base.monthlyExpenses)),
            Profile("zero income and expenses", base.copy(monthlyIncome = 0.0, monthlyExpenses = 0.0)),
            Profile("no financial assets", base.copy(financialAssets = 0.0)),
            Profile("lowest real return", base.copy(nominalReturnPercent = -50.0, inflationPercent = 100.0)),
            Profile("highest real return", base.copy(nominalReturnPercent = 100.0, inflationPercent = -50.0)),
            Profile("horizon at age", base.copy(lifeExpectancy = 38), FireStatus.HORIZON_EXHAUSTED),
            Profile("horizon below age", base.copy(lifeExpectancy = 37), FireStatus.HORIZON_EXHAUSTED)
        )
    }

    private fun expectedStatuses(metrics: CalculatedMetrics): Map<String, AuditIndicatorStatus?> = mapOf(
        "cash_flow" to FinancialMetricEvaluator.getCashFlowStatus(metrics.cashFlow),
        "savings_rate" to FinancialMetricEvaluator.getSavingsRateStatus(metrics.savingsRate),
        "emergency_fund" to FinancialMetricEvaluator.getSafetyMonthsStatus(metrics.safetyMonths),
        "safety_months" to FinancialMetricEvaluator.getSafetyMonthsStatus(metrics.safetyMonths),
        "debt_ratio" to FinancialMetricEvaluator.getDebtRatioStatus(metrics.debtRatio),
        "net_worth" to FinancialMetricEvaluator.getNetWorthStatus(metrics.netWorth, metrics.totalAssets),
        "fidelity" to if (metrics.isFidelityAvailable) FinancialMetricEvaluator.getFidelityStatus(metrics.fidelityRatio) else null,
        "cfs" to FinancialMetricEvaluator.getCfsStatus(metrics.financialFreedomRatio),
        "real_return" to FinancialMetricEvaluator.getRealReturnStatus(metrics.realReturnPercent)
    )

    @Test
    fun `input rate and tax fields enforce shared bounds`() {
        var input by mutableStateOf(FinancialInputData.usAverageData())
        val strings = getAppStrings(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU, LocalAppStrings provides strings) {
                    InputDataScreen(
                        input, FinanceCalculator.calculate(input, today = today),
                        onUpdate = { input = it(input) }, onNavigateToMetrics = {},
                        modifier = Modifier.testTag("bounds_input_list")
                    )
                }
            }
        }
        data class Field(val tag: String, val value: (FinancialInputData) -> Double, val cases: List<Pair<String, Double>>)
        val rateCases = listOf("-51" to -50.0, "-50" to -50.0, "100" to 100.0, "101" to 100.0)
        val fields = listOf(
            Field("input_nominal_return", { it.nominalReturnPercent }, rateCases),
            Field("input_inflation", { it.inflationPercent }, rateCases),
            Field("input_capital_gains_tax", { it.capitalGainsTaxPercent }, listOf("0" to 0.0, "100" to 100.0, "101" to 100.0))
        )
        for (field in fields) {
            composeRule.onNodeWithTag("bounds_input_list").performScrollToNode(hasTestTag(field.tag))
            for ((text, expected) in field.cases) {
                val node = composeRule.onNodeWithTag(field.tag)
                node.performClick()
                node.performTextClearance()
                node.performTextInput(text)
                composeRule.runOnIdle { assertEquals("${field.tag} $text", expected, field.value(input), 0.0) }
            }
        }
    }

    @Test
    fun `profile audit and share groups match evaluator and export uses calculated values`() {
        for (profile in profiles()) {
            val input = profile.input
            val metrics = FinanceCalculator.calculate(input, today = today)
            profile.fireStatus?.let { assertEquals(profile.name, it, metrics.fireStatus) }
            val expected = expectedStatuses(metrics)
            for (lang in AppLanguage.values()) {
                val strings = getAppStrings(lang)
                val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
                val items = opinion.positive + opinion.neutral + opinion.negative
                assertEquals(profile.name, expected.filterValues { it != null }.keys, items.map { it.id }.toSet())
                val share = opinion.toFormattedShareText(input, metrics, lang)
                val headers = listOf(
                    AuditIndicatorStatus.POSITIVE to String.format(Locale.ROOT, strings.auditSharePositiveHeader, opinion.positive.size),
                    AuditIndicatorStatus.NEUTRAL to String.format(Locale.ROOT, strings.auditShareNeutralHeader, opinion.neutral.size),
                    AuditIndicatorStatus.NEGATIVE to String.format(Locale.ROOT, strings.auditShareNegativeHeader, opinion.negative.size)
                )
                for (item in items) {
                    assertEquals("${profile.name} $lang ${item.id}", expected[item.id], item.status)
                    val groupIndex = headers.indexOfFirst { it.first == expected[item.id] }
                    val start = share.indexOf(headers[groupIndex].second)
                    val end = if (groupIndex < headers.lastIndex) share.indexOf(headers[groupIndex + 1].second) else share.length
                    assertTrue("${profile.name} $lang ${item.id} share group", start >= 0 && end > start)
                    assertTrue(share.substring(start, end).lineSequence().any { it == item.text })
                }

                val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, lang)
                val currency = input.currencySymbol
                fun money(value: Double) = Formatters.formatMoney(value, currency, lang)
                fun ratio(value: Double) = Formatters.formatRatio(value, lang)
                fun percent(value: Double) = Formatters.formatPercent(value, lang)
                fun line(template: String, vararg values: Any) {
                    assertTrue("${profile.name} $lang $template", export.lineSequence().any { it == String.format(Locale.ROOT, template, *values) })
                }
                line(strings.exportCashFlowLine, money(metrics.cashFlow))
                line(strings.exportSavingsRateLine, percent(metrics.savingsRate))
                line(strings.exportEmergencyFundLine, money(metrics.emergencyFund), ratio(metrics.safetyMonths))
                line(strings.exportDebtRatioLine, ratio(metrics.debtRatio))
                line(strings.exportNetWorthLine, money(metrics.netWorth))
                line(strings.exportRealReturnLine, percent(metrics.realReturnPercent))
                line(strings.exportEffectiveTaxLine, percent(metrics.effectiveCapitalGainsTaxRate))
                line(strings.exportCfsLine, ratio(metrics.financialFreedomRatio), FinancialMetricEvaluator.getCfsStageDescription(metrics.financialFreedomRatio, lang))
                val unavailable = strings.metricUnavailableValue
                line(strings.exportFidelityLine,
                    if (metrics.isFidelityAvailable) strings.fidelityProgressRatioFormat(money(metrics.fidelityCapital), money(metrics.fidelityTarget)) else unavailable,
                    if (metrics.isFidelityAvailable) ratio(metrics.fidelityMultiplier) else unavailable,
                    if (metrics.isFidelityAvailable) percent(metrics.fidelityPercent) else unavailable)
                line(strings.exportFireTargetCapitalLine, Formatters.formatFireTargetCapital(metrics.retirementTargetCapital, currency, metrics.fireStatus, lang))
            }
        }
    }

    @Test
    fun `input and metrics screen value colors match evaluator for every profile`() {
        val lang = AppLanguage.RU
        val strings = getAppStrings(lang)
        var input by mutableStateOf(FinancialInputData.usAverageData())
        var metrics by mutableStateOf(FinanceCalculator.calculate(input, today = today))
        var showInput by mutableStateOf(true)
        var unavailableColor = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                unavailableColor = MaterialTheme.colorScheme.onSurfaceVariant
                CompositionLocalProvider(LocalAppLanguage provides lang, LocalAppStrings provides strings) {
                    if (showInput) {
                        InputDataScreen(input, metrics, onUpdate = {}, onNavigateToMetrics = {}, modifier = Modifier.testTag("consistency_list"))
                    } else {
                        MetricsScreen(input, metrics, modifier = Modifier.testTag("consistency_list"))
                    }
                }
            }
        }

        data class Value(val id: String, val title: String, val text: String)
        for (profile in profiles()) {
            val calculated = FinanceCalculator.calculate(profile.input, today = today)
            val statuses = expectedStatuses(calculated)
            fun money(value: Double) = Formatters.formatMoney(value, profile.input.currencySymbol, lang)
            fun percent(value: Double) = Formatters.formatPercent(value, lang)
            val inputValues = listOf(
                Value("cash_flow", strings.cashFlowTitle, money(calculated.cashFlow)),
                Value("savings_rate", strings.savingsRateTitle, percent(calculated.savingsRate)),
                Value("net_worth", strings.netWorthTitle, money(calculated.netWorth)),
                Value("real_return", strings.realReturnTitle, percent(calculated.realReturnPercent))
            )
            val metricValues = listOf(
                Value("cash_flow", strings.metricCashFlowTitle, money(calculated.cashFlow)),
                Value("savings_rate", strings.metricSavingsRateTitle, percent(calculated.savingsRate)),
                Value("emergency_fund", strings.metricEmergencyFundTitle, money(calculated.emergencyFund)),
                Value("safety_months", strings.metricSafetyMonthsTitle, "${Formatters.formatRatio(calculated.safetyMonths, lang)} ${strings.monthsSuffix}"),
                Value("debt_ratio", strings.metricDebtRatioTitle, Formatters.formatRatio(calculated.debtRatio, lang)),
                Value("net_worth", strings.metricNetWorthTitle, money(calculated.netWorth)),
                Value("fidelity", strings.metricFidelityTitle, if (calculated.isFidelityAvailable) money(calculated.fidelityTarget) else strings.metricUnavailableValue),
                Value("cfs", strings.metricCfsTitle, Formatters.formatRatio(calculated.financialFreedomRatio, lang)),
                Value("real_return", strings.metricRealReturnTitle, percent(calculated.realReturnPercent))
            )
            for (inputScreen in listOf(true, false)) {
                composeRule.runOnIdle {
                    input = profile.input
                    metrics = calculated
                    showInput = inputScreen
                }
                for (value in if (inputScreen) inputValues else metricValues) {
                    val tag = if (inputScreen) "input_metric_${value.id}" else "metric_card_${value.title.replace(" ", "_")}"
                    composeRule.onNodeWithTag("consistency_list").performScrollToNode(hasTestTag(tag))
                    val layouts = mutableListOf<TextLayoutResult>()
                    composeRule.onAllNodes(hasText(value.text) and hasAnyAncestor(hasTestTag(tag)), useUnmergedTree = true).onFirst()
                        .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                    assertEquals("${profile.name} $inputScreen ${value.id}", 1, layouts.size)
                    val expectedColor = statuses[value.id]?.let(StatusColors::getColor) ?: unavailableColor
                    assertEquals("${profile.name} $inputScreen ${value.id}", expectedColor, layouts.single().layoutInput.style.color)
                }
            }
        }
    }

    @Test
    fun `tax fields come from projection and vanish when tax does not apply`() {
        var sawTaxableProfile = false
        for (profile in profiles()) {
            val input = profile.input
            val metrics = FinanceCalculator.calculate(input, today = today)
            val engine = ProjectionEngine(ProjectionParams.from(input, today = today))
            val years = (input.lifeExpectancy - metrics.retirementAge).coerceAtLeast(0.0)
            val baseRequirement = engine.computeBaseRequirement(metrics.yearsToRetirement, years)
            assertEquals(profile.name, engine.getEffectiveTaxRateAt(metrics.yearsToRetirement, years, baseRequirement), metrics.effectiveCapitalGainsTaxRate, 0.000001)
            assertEquals(profile.name, engine.getGainShareAt(metrics.yearsToRetirement, years, baseRequirement), metrics.capitalGainsGainSharePercent, 0.000001)
            if (input.financialAssets == 0.0 || input.lifeExpectancy <= metrics.currentAge ||
                metrics.fireStatus == FireStatus.UNREACHABLE || !metrics.isAgeValid) {
                assertEquals(profile.name, 0.0, metrics.effectiveCapitalGainsTaxRate, 0.0)
                assertEquals(profile.name, 0.0, metrics.capitalGainsGainSharePercent, 0.0)
            }
            sawTaxableProfile = sawTaxableProfile || metrics.effectiveCapitalGainsTaxRate > 0.0
        }
        assertTrue("profiles must also cover applicable tax", sawTaxableProfile)
    }

    private val scenarioIds = listOf(
        "pos_salary_increase",
        "pos_market_growth",
        "pos_expense_optimization",
        "pos_relocation",
        "pos_inheritance",
        "pos_property_sale",
        "pos_children_self_sufficient",
        "job_loss",
        "market_crash",
        "mortgage",
        "car_loan",
        "large_expense",
        "inflation",
        "child_dependent"
    )

    @Test
    fun `baseline and each scenario are finite and trajectory ends at life expectancy`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app, today)

        assertEquals(FinancialInputData.usAverageData(), viewModel.inputData.value)
        assertEquals(viewModel.metrics.value, viewModel.simulatedMetrics.value)
        assertFiniteAndCoherent(viewModel.metrics.value, viewModel.trajectory.value)
        assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value)

        scenarioIds.forEach { scenarioId ->
            viewModel.resetSimulation()
            viewModel.toggleScenario(scenarioId)
            assertEquals("scenario $scenarioId is enabled", 1, viewModel.fireSimulation.value.scenarios.activeCount)
            assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value)
        }

        viewModel.resetSimulation()
        scenarioIds.forEach(viewModel::toggleScenario)
        assertEquals(14, viewModel.fireSimulation.value.scenarios.activeCount)
        assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value)

        viewModel.resetSimulation()
        viewModel.updateSimulation(extraSavings = 0.0, realReturnDelta = 0.0, expReduction = 0.0)
        assertFalse(viewModel.fireSimulation.value.isSimulating)
        assertEquals(viewModel.metrics.value, viewModel.simulatedMetrics.value)
    }

    @Test
    fun `zero expenses negative cashflow exhausted horizon and invalid date stay finite`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app, today)

        val cases = listOf(
            "zero expenses" to FinancialInputData.usAverageData().copy(monthlyExpenses = 0.0),
            "negative cashflow" to FinancialInputData.usAverageData().copy(monthlyIncome = 1_000.0, monthlyExpenses = 2_000.0),
            "life expectancy at age" to FinancialInputData.usAverageData().copy(birthDate = "1988-06-15", lifeExpectancy = 38),
            "invalid date" to FinancialInputData.usAverageData().copy(birthDate = "not-a-date")
        )

        cases.forEach { (label, input) ->
            viewModel.resetSimulation()
            viewModel.updateInputData { input }
            assertFiniteAndCoherent(viewModel.metrics.value, viewModel.trajectory.value, label)
            assertFiniteAndCoherent(viewModel.simulatedMetrics.value, viewModel.simulatedTrajectory.value, label)
            assertEquals("$label baseline matches no-op simulation", viewModel.metrics.value, viewModel.simulatedMetrics.value)
        }
        assertTrue(viewModel.trajectory.value.isEmpty()) // invalid birth date

        viewModel.updateInputData {
            FinancialInputData.usAverageData().copy(birthDate = "1988-06-15", lifeExpectancy = 38)
        }
        assertTrue(viewModel.trajectory.value.isEmpty()) // no remaining horizon at age 38 on fixed today
    }

    private fun assertFiniteAndCoherent(
        metrics: CalculatedMetrics,
        trajectory: List<TrajectoryPoint>,
        label: String = "case"
    ) {
        val values = listOf(
            metrics.cashFlow,
            metrics.savingsRate,
            metrics.totalAssets,
            metrics.emergencyFund,
            metrics.safetyMonths,
            metrics.debtRatio,
            metrics.netWorth,
            metrics.annualIncome,
            metrics.annualExpenses,
            metrics.fidelityMultiplier,
            metrics.fidelityTarget,
            metrics.financialFreedomRatio,
            metrics.realReturnPercent,
            metrics.yearsToRetirement,
            metrics.retirementAge,
            metrics.retirementTargetCapital,
            metrics.capitalGainsTaxPercent,
            metrics.effectiveCapitalGainsTaxRate,
            metrics.capitalGainsGainSharePercent,
            metrics.scores.totalScore,
            metrics.fidelityRatio,
            metrics.fidelityPercent
        )
        assertTrue("$label CalculatedMetrics contains NaN/Infinity", values.all { it.isFinite() })
        assertTrue(
            "$label TrajectoryPoint contains NaN/Infinity",
            trajectory.all {
                it.age.isFinite() && it.yearOffset.isFinite() &&
                    it.accumulatedCapital.isFinite() && it.requiredCapital.isFinite()
            }
        )

        if (metrics.isAgeValid && metrics.lifeExpectancy > metrics.currentAge) {
            assertFalse("$label trajectory should not be empty", trajectory.isEmpty())
            assertTrue("$label ages should be monotonic", trajectory.zipWithNext().all { (a, b) -> b.age >= a.age })
            assertTrue("$label offsets should be monotonic", trajectory.zipWithNext().all { (a, b) -> b.yearOffset >= a.yearOffset })
            assertEquals(metrics.lifeExpectancy.toDouble(), trajectory.last().age, 0.0)
            if (metrics.fireStatus == FireStatus.REACHABLE || metrics.fireStatus == FireStatus.REACHED_NOW) {
                val exitPoint = trajectory.firstOrNull { it.isRetired }
                assertTrue("$label should expose a trajectory exit point", exitPoint != null)
                assertTrue(
                    "$label accumulated capital should cover required capital at exit",
                    exitPoint!!.accumulatedCapital >= exitPoint.requiredCapital
                )
            }
        } else {
            assertTrue("$label has no valid projection horizon", trajectory.isEmpty())
        }
    }
}
