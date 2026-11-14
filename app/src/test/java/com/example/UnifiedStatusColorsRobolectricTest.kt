package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.ProjectionEngine
import com.example.domain.ProjectionParams
import com.example.domain.ScenarioConstants
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.ui.components.ScenarioTogglesPanel
import com.example.ui.screens.AuditScoreScreen
import com.example.ui.screens.FireRetirementScreen
import com.example.ui.screens.InputDataScreen
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
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Регрессия UX-F07: единый статусный красный цвет внутри темы во всех четырёх вкладках
 * и совпадение цвета прогресс-бара Fidelity с числовым значением и процентом.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class UnifiedStatusColorsRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val today = LocalDate.of(2026, 11, 6)
    private val strings = getAppStrings(AppLanguage.RU)

    @Test
    fun `fidelity value percent and progress bar share exact status color across statuses and themes`() {
        val base = FinancialInputData(
            monthlyIncome = 4_000.0,
            monthlyExpenses = 5_000.0,
            liquidAssets = 64_800.0, // annualIncome = 48_000; age 40 -> 3.0x -> target = 144_000 -> ratio = 0.45 (< 0.7 NEGATIVE)
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            nominalReturnPercent = 2.0,
            inflationPercent = 5.0,
            birthDate = "1986-01-01",
            lifeExpectancy = 85
        )
        val negativeInput = base
        val neutralInput = base.copy(liquidAssets = 122_400.0) // 0.85 (NEUTRAL)
        val positiveInput = base.copy(liquidAssets = 144_000.0) // 1.00 (POSITIVE)

        val cases = listOf(
            AuditIndicatorStatus.NEGATIVE to negativeInput,
            AuditIndicatorStatus.NEUTRAL to neutralInput,
            AuditIndicatorStatus.POSITIVE to positiveInput
        )

        val activeInput = mutableStateOf(negativeInput)
        val activeMetrics = mutableStateOf(FinanceCalculator.calculate(negativeInput, today = today))
        val darkTheme = mutableStateOf(false)

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    MetricsScreen(
                        inputData = activeInput.value,
                        metrics = activeMetrics.value,
                        modifier = Modifier.testTag("metrics_unified_list")
                    )
                }
            }
        }

        val fidelityCardTag = "metric_card_${strings.metricFidelityTitle.replace(" ", "_")}"

        for ((status, input) in cases) {
            val metrics = FinanceCalculator.calculate(input, today = today)
            assertEquals(status, FinancialMetricEvaluator.getFidelityStatus(metrics.fidelityRatio))

            for (isDark in listOf(false, true)) {
                composeRule.runOnIdle {
                    darkTheme.value = isDark
                    activeInput.value = input
                    activeMetrics.value = metrics
                }
                composeRule.waitForIdle()
                composeRule.onNodeWithTag("metrics_unified_list").performScrollToNode(hasTestTag(fidelityCardTag))
                composeRule.waitForIdle()

                val expectedColor = StatusColors.getTextColor(status, darkTheme = isDark)
                val targetText = Formatters.formatMoney(metrics.fidelityTarget, input.currencySymbol, AppLanguage.RU)
                val percentText = Formatters.formatPercent(metrics.fidelityPercent, AppLanguage.RU)

                val valueColor = composeRule.onNode(
                    hasText(targetText) and hasAnyAncestor(hasTestTag(fidelityCardTag)),
                    useUnmergedTree = true
                ).textColor()
                val percentColor = composeRule.onNode(
                    hasText(percentText) and hasAnyAncestor(hasTestTag(fidelityCardTag)),
                    useUnmergedTree = true
                ).textColor()

                assertEquals("Fidelity value mismatch ($status, dark=$isDark)", expectedColor, valueColor)
                assertEquals("Fidelity percent mismatch ($status, dark=$isDark)", expectedColor, percentColor)

                val barNode = composeRule.onNodeWithTag("fidelity_progress_indicator", useUnmergedTree = true)
                    .fetchSemanticsNode()
                val sampledFill = sampleProgressIndicatorFillPixel(barNode, fraction = 0.15f)
                assertColorMatches("Fidelity progress bar mismatch ($status, dark=$isDark)", expectedColor, sampledFill)
            }
        }
    }

    @Test
    fun `negative status uses single unified red color across all four main tabs in both themes`() {
        // Отрицательные cash flow, savings rate, net worth, real return, fidelity, total score и недостижимый FIRE
        val negativeInput = FinancialInputData(
            monthlyIncome = 4_000.0,
            monthlyExpenses = 5_000.0,
            liquidAssets = 1_000.0,
            financialAssets = 5_000.0,
            propertyAssets = 0.0,
            liabilities = 10_000.0,
            nominalReturnPercent = 2.0,
            inflationPercent = 5.0,
            birthDate = "1986-01-01",
            lifeExpectancy = 85
        )
        val negativeMetrics = FinanceCalculator.calculate(negativeInput, today = today)
        assertTrue(negativeMetrics.cashFlow < 0)
        assertTrue(negativeMetrics.savingsRate <= 0)
        assertTrue(negativeMetrics.netWorth < 0)
        assertTrue(negativeMetrics.realReturnPercent < 0)
        assertTrue(negativeMetrics.scores.totalScore < 3.5)
        assertEquals(FireStatus.UNREACHABLE, negativeMetrics.fireStatus)

        val trajectory = ProjectionEngine(ProjectionParams.from(negativeInput, today = today)).buildTrajectory()
        assertTrue(trajectory.isNotEmpty())

        val tabIndex = mutableStateOf(0)
        val darkTheme = mutableStateOf(false)
        val simulationState = mutableStateOf(FireSimulationState())

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    when (tabIndex.value) {
                        0 -> InputDataScreen(
                            inputData = negativeInput,
                            metrics = negativeMetrics,
                            onUpdate = {},
                            onNavigateToMetrics = {},
                            modifier = Modifier.testTag("tab_input_list")
                        )
                        1 -> MetricsScreen(
                            inputData = negativeInput,
                            metrics = negativeMetrics,
                            modifier = Modifier.testTag("tab_metrics_list")
                        )
                        2 -> AuditScoreScreen(
                            inputData = negativeInput,
                            metrics = negativeMetrics,
                            modifier = Modifier.testTag("tab_audit_list")
                        )
                        else -> FireRetirementScreen(
                            inputData = negativeInput,
                            metrics = negativeMetrics,
                            simulatedMetrics = negativeMetrics,
                            simulationState = simulationState.value,
                            trajectory = trajectory,
                            onUpdateSimulation = { _, _, _ -> },
                            onToggleScenario = {},
                            onResetSimulation = {},
                            modifier = Modifier.testTag("tab_fire_list")
                        )
                    }
                }
            }
        }

        for (isDark in listOf(false, true)) {
            val expectedRed = if (isDark) AlertRedTextDark else AlertRedTextLight

            // 1. Вкладка «Данные»
            composeRule.runOnIdle {
                darkTheme.value = isDark
                tabIndex.value = 0
            }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("tab_input_list").performScrollToNode(hasTestTag("input_metric_cash_flow"))
            assertEquals(expectedRed, childTextColor("input_metric_cash_flow", Formatters.formatMoney(negativeMetrics.cashFlow, negativeInput.currencySymbol, AppLanguage.RU)))
            assertEquals(expectedRed, childTextColor("input_metric_savings_rate", Formatters.formatPercent(negativeMetrics.savingsRate, AppLanguage.RU)))
            composeRule.onNodeWithTag("tab_input_list").performScrollToNode(hasTestTag("input_metric_net_worth"))
            assertEquals(expectedRed, childTextColor("input_metric_net_worth", Formatters.formatMoney(negativeMetrics.netWorth, negativeInput.currencySymbol, AppLanguage.RU)))
            composeRule.onNodeWithTag("tab_input_list").performScrollToNode(hasTestTag("input_metric_real_return"))
            assertEquals(expectedRed, childTextColor("input_metric_real_return", Formatters.formatPercent(negativeMetrics.realReturnPercent, AppLanguage.RU)))

            // 2. Вкладка «Показатели»
            composeRule.runOnIdle { tabIndex.value = 1 }
            composeRule.waitForIdle()
            val netWorthCardTag = "metric_card_${strings.metricNetWorthTitle.replace(" ", "_")}"
            composeRule.onNodeWithTag("tab_metrics_list").performScrollToNode(hasTestTag(netWorthCardTag))
            assertEquals(expectedRed, childTextColor(netWorthCardTag, Formatters.formatMoney(negativeMetrics.netWorth, negativeInput.currencySymbol, AppLanguage.RU)))
            val fidelityCardTag = "metric_card_${strings.metricFidelityTitle.replace(" ", "_")}"
            composeRule.onNodeWithTag("tab_metrics_list").performScrollToNode(hasTestTag(fidelityCardTag))
            assertEquals(expectedRed, childTextColor(fidelityCardTag, Formatters.formatPercent(negativeMetrics.fidelityPercent, AppLanguage.RU)))

            // 3. Вкладка «Итоги и Баллы» (текст + шкала ScoreItemCard)
            composeRule.runOnIdle { tabIndex.value = 2 }
            composeRule.waitForIdle()
            assertEquals(expectedRed, textColor(strings.scoresTotalResult(Formatters.formatScore(negativeMetrics.scores.totalScore, lang = AppLanguage.RU))))
            composeRule.onNodeWithTag("tab_audit_list").performScrollToNode(hasTestTag("score_savings_rate"))
            assertEquals(expectedRed, childTextColor("score_savings_rate", strings.scoreOutOfTen(negativeMetrics.scores.savingsRateScore)))
            // В ScoreItemCard с score=0 полоса не заполнена (0f), поэтому для пиксельной проверки прогресс-бара используем score=3 (<5 NEGATIVE) в отдельном блоке ниже.

            // 4. Вкладка «FIRE» (Hero + подпись графика при UNREACHABLE + ползунок доходности)
            composeRule.runOnIdle {
                tabIndex.value = 3
                simulationState.value = FireSimulationState()
            }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("tab_fire_list").performScrollToNode(hasTestTag("fire_unreachable_title"))
            assertEquals(expectedRed, textColorByTag("fire_unreachable_title"))
            composeRule.onNodeWithTag("tab_fire_list").performScrollToNode(hasTestTag("fire_chart_status_label"))
            assertEquals(expectedRed, textColorByTag("fire_chart_status_label"))

            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            composeRule.onNodeWithTag("tab_fire_list").performScrollToNode(hasTestTag("fire_slider_return_value"))
            assertEquals(expectedRed, textColorByTag("fire_slider_return_value"))
        }
    }

    @Test
    fun `negative component progress bar in audit tab and checked scenario switch match unified red`() {
        val inputWithNonZeroLowSavingsScore = FinancialInputData.usAverageData().copy(
            monthlyIncome = 5_000.0,
            monthlyExpenses = 5_200.0,
            liquidAssets = 4_000.0, // safetyMonths = 0.77 -> score = 2 (негативный, полоса 20% ширины)
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 0.0
        )
        val metrics = FinanceCalculator.calculate(inputWithNonZeroLowSavingsScore, today = today)
        assertEquals(2, metrics.scores.safetyMonthsScore)

        val darkTheme = mutableStateOf(false)
        val showAudit = mutableStateOf(true)

        composeRule.setContent {
            TestTheme(darkTheme.value) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    if (showAudit.value) {
                        AuditScoreScreen(
                            inputData = inputWithNonZeroLowSavingsScore,
                            metrics = metrics,
                            modifier = Modifier.testTag("audit_bar_list")
                        )
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            ScenarioTogglesPanel(
                                scenarios = FireScenarioToggles(jobLoss = true),
                                inputData = inputWithNonZeroLowSavingsScore,
                                onToggleScenario = {}
                            )
                        }
                    }
                }
            }
        }

        for (isDark in listOf(false, true)) {
            val expectedRed = if (isDark) AlertRedTextDark else AlertRedTextLight

            composeRule.runOnIdle {
                darkTheme.value = isDark
                showAudit.value = true
            }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("audit_bar_list").performScrollToNode(hasTestTag("score_safety_months_progress"))
            composeRule.waitForIdle()
            val barNode = composeRule.onNodeWithTag("score_safety_months_progress", useUnmergedTree = true)
                .fetchSemanticsNode()
            val sampledFill = sampleProgressIndicatorFillPixel(barNode, fraction = 0.10f)
            assertColorMatches("Audit SafetyMonths progress bar mismatch (dark=$isDark)", expectedRed, sampledFill)

            composeRule.onNodeWithTag("audit_bar_list").performScrollToNode(hasTestTag("score_gauge"))
            composeRule.waitForIdle()
            val gaugeNode = composeRule.onNodeWithTag("score_gauge", useUnmergedTree = true).fetchSemanticsNode()
            assertNodeContainsColor("ScoreGauge arc mismatch (dark=$isDark)", gaugeNode, expectedRed)

            composeRule.runOnIdle { showAudit.value = false }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("switch_job_loss", useUnmergedTree = true).performScrollTo()
            composeRule.waitForIdle()
            val negativeParam = "${strings.scenarioParamsPrefix} " +
                strings.scenarioJobLossParam(strings.formatThreshold(ScenarioConstants.JOB_LOSS_MONTHS))
            assertEquals(expectedRed, textColor(negativeParam))

            // Дорожка активного Switch негативного сценария также использует единый красный оттенок темы
            val switchNode = composeRule.onNodeWithTag("switch_job_loss", useUnmergedTree = true).fetchSemanticsNode()
            assertNodeContainsColor("Checked negative scenario switch track mismatch (dark=$isDark)", switchNode, expectedRed)
        }
    }

    @Test
    fun `input validation error text preserves material error color separate from financial negative status`() {
        val invalidDateInput = FinancialInputData.usAverageData().copy(
            birthDate = "invalid-date",
            monthlyIncome = 2_000.0,
            monthlyExpenses = 3_000.0
        )
        val metrics = FinanceCalculator.calculate(invalidDateInput, today = today)
        val expectedMaterialLightError = Color(0xFFB3261E)

        composeRule.setContent {
            TestTheme(darkTheme = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    InputDataScreen(
                        inputData = invalidDateInput,
                        metrics = metrics,
                        onUpdate = {},
                        onNavigateToMetrics = {},
                        modifier = Modifier.testTag("validation_input_list")
                    )
                }
            }
        }

        // Финансовый показатель — единый красный статус
        composeRule.onNodeWithTag("validation_input_list").performScrollToNode(hasTestTag("input_metric_cash_flow"))
        assertEquals(
            AlertRedTextLight,
            childTextColor("input_metric_cash_flow", Formatters.formatMoney(metrics.cashFlow, invalidDateInput.currencySymbol, AppLanguage.RU))
        )

        // Ошибка валидации поля ввода — системный colorScheme.error
        composeRule.onNodeWithTag("validation_input_list").performScrollToNode(hasTestTag("input_birth_date"))
        val errorColor = composeRule.onAllNodesWithText(strings.invalidBirthDateError, useUnmergedTree = true)
            .onFirst()
            .textColor()
        assertEquals(expectedMaterialLightError, errorColor)
    }

    private fun sampleProgressIndicatorFillPixel(node: SemanticsNode, fraction: Float): Color {
        val (bitmap, origin) = captureContentBitmap()
        val rect = node.boundsInWindow
        val x = (rect.left - origin[0] + rect.width * fraction).toInt().coerceIn(0, bitmap.width - 1)
        val y = ((rect.top + rect.bottom) / 2f - origin[1]).toInt().coerceIn(0, bitmap.height - 1)
        return Color(bitmap.getPixel(x, y))
    }

    private fun assertNodeContainsColor(message: String, node: SemanticsNode, expected: Color) {
        val (bitmap, origin) = captureContentBitmap()
        val rect = node.boundsInWindow
        val left = (rect.left - origin[0]).toInt().coerceIn(0, bitmap.width - 1)
        val right = (rect.right - origin[0]).toInt().coerceIn(0, bitmap.width - 1)
        val top = (rect.top - origin[1]).toInt().coerceIn(0, bitmap.height - 1)
        val bottom = (rect.bottom - origin[1]).toInt().coerceIn(0, bitmap.height - 1)
        val expectedArgb = expected.toArgb()
        var found = false
        for (y in top..bottom) {
            for (x in left..right) {
                val px = bitmap.getPixel(x, y)
                if (abs(android.graphics.Color.red(expectedArgb) - android.graphics.Color.red(px)) <= 4 &&
                    abs(android.graphics.Color.green(expectedArgb) - android.graphics.Color.green(px)) <= 4 &&
                    abs(android.graphics.Color.blue(expectedArgb) - android.graphics.Color.blue(px)) <= 4
                ) {
                    found = true
                    break
                }
            }
            if (found) break
        }
        assertTrue("$message: color ${Integer.toHexString(expectedArgb)} not found in node bounds $rect", found)
    }

    private fun captureContentBitmap(): Pair<Bitmap, IntArray> {
        return composeRule.runOnIdle {
            val view = composeRule.activity.findViewById<View>(android.R.id.content)
            val origin = IntArray(2)
            view.getLocationInWindow(origin)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also {
                view.draw(Canvas(it))
            }
            bitmap to origin
        }
    }

    private fun assertColorMatches(message: String, expected: Color, actual: Color) {
        val expectedArgb = expected.toArgb()
        val actualArgb = actual.toArgb()
        val dr = abs(android.graphics.Color.red(expectedArgb) - android.graphics.Color.red(actualArgb))
        val dg = abs(android.graphics.Color.green(expectedArgb) - android.graphics.Color.green(actualArgb))
        val db = abs(android.graphics.Color.blue(expectedArgb) - android.graphics.Color.blue(actualArgb))
        assertTrue(
            "$message: expected=${Integer.toHexString(expectedArgb)}, actual=${Integer.toHexString(actualArgb)}",
            dr <= 4 && dg <= 4 && db <= 4
        )
    }

    private fun childTextColor(parentTag: String, text: String): Color =
        composeRule.onNode(
            hasText(text) and hasAnyAncestor(hasTestTag(parentTag)),
            useUnmergedTree = true
        ).textColor()

    private fun textColor(text: String): Color =
        composeRule.onNode(hasText(text), useUnmergedTree = true).textColor()

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
