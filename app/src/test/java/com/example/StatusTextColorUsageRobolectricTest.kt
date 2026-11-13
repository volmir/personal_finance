package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.ScenarioConstants
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.ui.components.ScenarioTogglesPanel
import com.example.ui.screens.AuditScoreScreen
import com.example.ui.screens.FireRetirementScreen
import com.example.ui.screens.MetricsScreen
import com.example.ui.theme.AlertRedTextDark
import com.example.ui.theme.AlertRedTextLight
import com.example.ui.theme.EmeraldDarkPrimary
import com.example.ui.theme.EmeraldLightPrimary
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface
import com.example.ui.theme.SlateLightBackground
import com.example.ui.theme.SlateLightCard
import com.example.ui.theme.SlateLightSurface
import com.example.ui.theme.StatusColors
import com.example.ui.theme.SuccessGreenTextDark
import com.example.ui.theme.SuccessGreenTextLight
import com.example.ui.theme.WarningYellowTextDark
import com.example.ui.theme.WarningYellowTextLight
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Регрессия UX-C02: статусные подписи в реально отрисованных экранах берут
 * контрастный текстовый оттенок из [StatusColors], а не базовый акцент.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
class StatusTextColorUsageRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val today = LocalDate.of(2026, 11, 6)
    private val strings = getAppStrings(AppLanguage.RU)

    @Test
    fun `fire simulation comparison title uses readable status text in both themes`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)
        val faster = metrics.copy(
            yearsToRetirement = (metrics.yearsToRetirement - 2.0).coerceAtLeast(0.0),
            cashFlow = -500.0,
            netWorth = metrics.netWorth - 1_000.0
        )
        val unreachable = metrics.copy(fireStatus = FireStatus.UNREACHABLE)
        val simulated = mutableStateOf(faster)
        val darkTheme = mutableStateOf(false)
        val simulation = FireSimulationState(extraMonthlySavings = 100.0)

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = metrics,
                        simulatedMetrics = simulated.value,
                        simulationState = simulation,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_simulation_summary"))
        assertEquals(SuccessGreenTextLight, textColor(strings.fireSimSummaryTitle))

        val cashFlowRow = "${Formatters.formatMoney(metrics.cashFlow, input.currencySymbol, AppLanguage.RU)} ➔ " +
            Formatters.formatMoney(faster.cashFlow, input.currencySymbol, AppLanguage.RU)
        val netWorthRow = "${Formatters.formatMoney(metrics.netWorth, input.currencySymbol, AppLanguage.RU)} ➔ " +
            Formatters.formatMoney(faster.netWorth, input.currencySymbol, AppLanguage.RU)
        assertEquals(AlertRedTextLight, textColor(cashFlowRow))
        assertEquals(AlertRedTextLight, textColor(netWorthRow))

        val badgeText = Formatters.formatSimulationBadge(0, hasSliders = true, AppLanguage.RU)
        val summaryBadge = hasText(badgeText) and hasAnyAncestor(hasTestTag("fire_simulation_summary"))
        assertEquals(Color(0xFF131D18), textColor(summaryBadge))

        composeRule.runOnIdle { darkTheme.value = true }
        assertEquals(SuccessGreenTextDark, textColor(strings.fireSimSummaryTitle))
        assertEquals(AlertRedTextDark, textColor(cashFlowRow))
        assertEquals(AlertRedTextDark, textColor(netWorthRow))
        assertEquals(Color(0xFFE2EBE5), textColor(summaryBadge))

        composeRule.runOnIdle { simulated.value = unreachable }
        assertEquals(AlertRedTextDark, textColor(strings.fireSimSummaryTitle))

        composeRule.runOnIdle { darkTheme.value = false }
        assertEquals(AlertRedTextLight, textColor(strings.fireSimSummaryTitle))
    }

    @Test
    fun `fire hero values use readable status text in both themes`() {
        val input = FinancialInputData.usAverageData()
        val baseMetrics = FinanceCalculator.calculate(input, today = today)
        val metrics = mutableStateOf(baseMetrics)
        val darkTheme = mutableStateOf(false)

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = metrics.value,
                        simulatedMetrics = metrics.value,
                        simulationState = FireSimulationState(),
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        val yearsText = Formatters.formatFireYearsToRetirement(
            baseMetrics.yearsToRetirement,
            baseMetrics.fireStatus,
            AppLanguage.RU
        )
        assertEquals(SuccessGreenTextLight, textColor(yearsText))
        composeRule.runOnIdle { darkTheme.value = true }
        assertEquals(SuccessGreenTextDark, textColor(yearsText))

        composeRule.runOnIdle { metrics.value = baseMetrics.copy(fireStatus = FireStatus.UNREACHABLE) }
        assertEquals(AlertRedTextDark, textColorByTag("fire_unreachable_title"))
        composeRule.runOnIdle { darkTheme.value = false }
        assertEquals(AlertRedTextLight, textColorByTag("fire_unreachable_title"))

        composeRule.runOnIdle {
            metrics.value = baseMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0)
        }
        assertEquals(SuccessGreenTextLight, textColorByTag("fire_reached_title"))
        composeRule.runOnIdle { darkTheme.value = true }
        assertEquals(SuccessGreenTextDark, textColorByTag("fire_reached_title"))

        composeRule.runOnIdle {
            metrics.value = baseMetrics.copy(fireStatus = FireStatus.REACHABLE, yearsToRetirement = 0.004)
        }
        assertEquals(SuccessGreenTextDark, textColorByTag("fire_sub_hundredth_title"))
        composeRule.runOnIdle { darkTheme.value = false }
        assertEquals(SuccessGreenTextLight, textColorByTag("fire_sub_hundredth_title"))

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.runOnIdle {
            metrics.value = baseMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0)
        }
        assertEquals(SuccessGreenTextLight, textColorByTag("fire_reached_title"))
        composeRule.runOnIdle { darkTheme.value = true }
        assertEquals(SuccessGreenTextDark, textColorByTag("fire_reached_title"))

        composeRule.runOnIdle {
            metrics.value = baseMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
        }
        assertEquals(AlertRedTextDark, textColorByTag("fire_unreachable_title"))
        composeRule.runOnIdle { darkTheme.value = false }
        assertEquals(AlertRedTextLight, textColorByTag("fire_unreachable_title"))
    }

    @Test
    fun `real return slider value uses status color by sign in both themes`() {
        val baseInput = FinancialInputData.usAverageData().copy(
            nominalReturnPercent = 4.0,
            inflationPercent = 4.0
        )
        val inputState = mutableStateOf(baseInput)
        val baseMetricsState = mutableStateOf(FinanceCalculator.calculate(baseInput, today = today))
        val simulatedState = mutableStateOf(baseMetricsState.value)
        val simulationState = mutableStateOf(FireSimulationState())
        val darkTheme = mutableStateOf(false)

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(
                        inputData = inputState.value,
                        metrics = baseMetricsState.value,
                        simulatedMetrics = simulatedState.value,
                        simulationState = simulationState.value,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = { simulationState.value = FireSimulationState() },
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_slider_return_value"))

        fun setEffectiveReturn(
            effectiveReturn: Double,
            scenarios: FireScenarioToggles = FireScenarioToggles(),
            baseReturn: Double = 0.0
        ) {
            composeRule.runOnIdle {
                val updatedBase = baseInput.copy(nominalReturnPercent = baseReturn, inflationPercent = 0.0)
                inputState.value = updatedBase
                val baseCalc = FinanceCalculator.calculate(updatedBase, today = today)
                baseMetricsState.value = baseCalc
                val delta = effectiveReturn - baseCalc.realReturnPercent
                simulationState.value = FireSimulationState(
                    realReturnAdjustment = delta,
                    scenarios = scenarios
                )
                simulatedState.value = baseCalc.copy(realReturnPercent = effectiveReturn)
            }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_slider_return_value"))
        }

        // Значения из таблицы проверки + пограничные значения округления (0,00%)
        val cases = listOf(
            10.0 to (SuccessGreenTextLight to SuccessGreenTextDark),
            5.0 to (SuccessGreenTextLight to SuccessGreenTextDark),
            0.5 to (SuccessGreenTextLight to SuccessGreenTextDark),
            0.004 to (WarningYellowTextLight to WarningYellowTextDark),
            0.0 to (WarningYellowTextLight to WarningYellowTextDark),
            -0.004 to (WarningYellowTextLight to WarningYellowTextDark),
            -0.5 to (AlertRedTextLight to AlertRedTextDark),
            -3.0 to (AlertRedTextLight to AlertRedTextDark),
            -3.5 to (AlertRedTextLight to AlertRedTextDark),
            -10.0 to (AlertRedTextLight to AlertRedTextDark)
        )

        for ((value, expectedPair) in cases) {
            setEffectiveReturn(value)
            composeRule.runOnIdle { darkTheme.value = false }
            assertEquals("Light theme color mismatch for $value%", expectedPair.first, textColorByTag("fire_slider_return_value"))
            composeRule.runOnIdle { darkTheme.value = true }
            assertEquals("Dark theme color mismatch for $value%", expectedPair.second, textColorByTag("fire_slider_return_value"))
        }

        // Рост от -5% до -2%: изменение (+3%) зелёное, но сама доходность (-2%) остаётся красной
        setEffectiveReturn(effectiveReturn = -2.0, baseReturn = -5.0)
        composeRule.runOnIdle { darkTheme.value = false }
        assertEquals(AlertRedTextLight, textColorByTag("fire_slider_return_value"))
        composeRule.runOnIdle { darkTheme.value = true }
        assertEquals(AlertRedTextDark, textColorByTag("fire_slider_return_value"))

        // Активные сценарии не ломают индикацию при переходе через ноль в обе стороны
        val activeScenarios = FireScenarioToggles(salaryIncrease20 = true, expensesPlus25 = true)
        setEffectiveReturn(effectiveReturn = -1.5, scenarios = activeScenarios, baseReturn = 2.0)
        composeRule.runOnIdle { darkTheme.value = false }
        assertEquals(AlertRedTextLight, textColorByTag("fire_slider_return_value"))

        setEffectiveReturn(effectiveReturn = 0.0, scenarios = activeScenarios, baseReturn = 2.0)
        assertEquals(WarningYellowTextLight, textColorByTag("fire_slider_return_value"))

        setEffectiveReturn(effectiveReturn = 3.5, scenarios = activeScenarios, baseReturn = 2.0)
        assertEquals(SuccessGreenTextLight, textColorByTag("fire_slider_return_value"))

        // Сброс симуляции возвращает базовое значение и соответствующий ему цвет (для базы -5% — красный)
        setEffectiveReturn(effectiveReturn = 3.0, baseReturn = -5.0)
        assertEquals(SuccessGreenTextLight, textColorByTag("fire_slider_return_value"))
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_reset_button"))
        composeRule.onNodeWithTag("fire_sim_reset_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_slider_return_value"))
        assertEquals(AlertRedTextLight, textColorByTag("fire_slider_return_value"))
    }

    @Test
    fun `scenario section counters and parameter badges use readable status text`() {
        val input = FinancialInputData.usAverageData()
        val scenarios = mutableStateOf(FireScenarioToggles(jobLoss = false))

        composeRule.setContent {
            TestTheme(darkTheme = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    ScenarioTogglesPanel(
                        scenarios = scenarios.value,
                        inputData = input,
                        onToggleScenario = {}
                    )
                }
            }
        }

        val positiveParam = "${strings.scenarioParamsPrefix} " +
            strings.scenarioSalaryIncreaseParam(strings.formatThreshold(ScenarioConstants.SALARY_INCREASE_PERCENT))
        val negativeParam = "${strings.scenarioParamsPrefix} " +
            strings.scenarioJobLossParam(strings.formatThreshold(ScenarioConstants.JOB_LOSS_MONTHS))

        assertEquals(SuccessGreenTextLight, textColor(positiveParam))
        // Выключенный негативный сценарий показывает параметры золотым — текст берёт тёмный оттенок.
        assertEquals(WarningYellowTextLight, textColor(negativeParam))

        composeRule.runOnIdle {
            scenarios.value = FireScenarioToggles(
                salaryIncrease20 = true,
                marketGrowth25 = true,
                jobLoss = true
            )
        }
        assertEquals(AlertRedTextLight, textColor(negativeParam))
        assertEquals(
            SuccessGreenTextLight,
            textColor(strings.scenarioPositiveActive(2))
        )
        assertEquals(
            AlertRedTextLight,
            textColor(strings.scenarioNegativeActive(1))
        )
    }

    @Test
    fun `fidelity progress value uses the same readable text color as the metric`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)
        val fidelityStatus = FinancialMetricEvaluator.getFidelityStatus(metrics.fidelityRatio)

        composeRule.setContent {
            TestTheme(darkTheme = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    MetricsScreen(
                        inputData = input,
                        metrics = metrics,
                        modifier = Modifier.testTag("metrics_list")
                    )
                }
            }
        }

        val cardTag = "metric_card_${strings.metricFidelityTitle.replace(" ", "_")}"
        composeRule.onNodeWithTag("metrics_list").performScrollToNode(hasTestTag(cardTag))
        val percentText = Formatters.formatPercent(metrics.fidelityPercent, AppLanguage.RU)
        val node = composeRule.onNode(
            hasText(percentText) and hasAnyAncestor(hasTestTag(cardTag)),
            useUnmergedTree = true
        )
        assertEquals(
            StatusColors.getReadableTextColor(StatusColors.getColor(fidelityStatus), darkTheme = false),
            node.textColor()
        )
    }

    @Test
    fun `auditor opinion counters keep readable status text in both themes`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)
        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        val darkTheme = mutableStateOf(false)

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    AuditScoreScreen(
                        inputData = input,
                        metrics = metrics,
                        modifier = Modifier.testTag("audit_test_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("audit_test_list").performScrollToNode(hasTestTag("auditor_opinion_card"))
        assertEquals(SuccessGreenTextLight, textColor(strings.positiveBadgeText(opinion.positive.size)))
        assertEquals(WarningYellowTextLight, textColor(strings.neutralBadgeText(opinion.neutral.size)))
        assertEquals(AlertRedTextLight, textColor(strings.negativeBadgeText(opinion.negative.size)))

        composeRule.runOnIdle { darkTheme.value = true }
        assertEquals(SuccessGreenTextDark, textColor(strings.positiveBadgeText(opinion.positive.size)))
        assertEquals(WarningYellowTextDark, textColor(strings.neutralBadgeText(opinion.neutral.size)))
        assertEquals(AlertRedTextDark, textColor(strings.negativeBadgeText(opinion.negative.size)))
    }

    private fun textColor(text: String): Color =
        composeRule.onNode(hasText(text), useUnmergedTree = true).textColor()

    private fun textColor(matcher: SemanticsMatcher): Color =
        composeRule.onNode(matcher, useUnmergedTree = true).textColor()

    private fun textColorByTag(tag: String): Color =
        composeRule.onNodeWithTag(tag, useUnmergedTree = true).textColor()

    private fun androidx.compose.ui.test.SemanticsNodeInteraction.textColor(): Color {
        val results = mutableListOf<TextLayoutResult>()
        val config = fetchSemanticsNode().config
        check(config.contains(SemanticsActions.GetTextLayoutResult)) {
            "Text node has no GetTextLayoutResult action"
        }
        val action = config[SemanticsActions.GetTextLayoutResult].action
        checkNotNull(action) { "Text node has no GetTextLayoutResult action" }
        action(results)
        return results.first().layoutInput.style.color
    }

    @Composable
    private fun TestTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
        val colorScheme = if (darkTheme) {
            darkColorScheme(
                primary = EmeraldDarkPrimary,
                background = SlateDarkSurface,
                surface = SlateDarkSurface,
                surfaceVariant = SlateDarkCard,
                onSurface = Color(0xFFE2EBE5),
                onSurfaceVariant = Color(0xFFB9C8BF)
            )
        } else {
            lightColorScheme(
                primary = EmeraldLightPrimary,
                background = SlateLightBackground,
                surface = SlateLightSurface,
                surfaceVariant = SlateLightCard,
                onSurface = Color(0xFF131D18),
                onSurfaceVariant = Color(0xFF4B5E55)
            )
        }
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
