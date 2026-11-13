package com.example

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.ui.screens.MetricsScreen
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import com.example.util.localization.getAppStrings
import org.junit.Rule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MetricsScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    @Config(qualifiers = "w411dp-h891dp")
    fun `all category filters sit in one horizontally scrollable row and filter cards`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu
                ) {
                    MetricsScreen(inputData = input, metrics = metrics)
                }
            }
        }

        val filterRow = composeRule.onNodeWithTag("metrics_filter_row")
        filterRow.assertExists()
        filterRow.assert(hasScrollAction())

        val chipTexts = listOf(
            StringsRu.categoryAll,
            StringsRu.categoryCashFlow,
            StringsRu.categoryCapitalRisks,
            StringsRu.categoryBenchmarks
        )
        chipTexts.forEach { text ->
            composeRule.onNode(hasText(text) and hasAnyAncestor(hasTestTag("metrics_filter_row")))
                .assertExists()
        }

        val cashFlowCard = metricCardTag(StringsRu.metricCashFlowTitle)
        val emergencyFundCard = metricCardTag(StringsRu.metricEmergencyFundTitle)
        val fidelityCard = metricCardTag(StringsRu.metricFidelityTitle)

        fun selectCategory(text: String) {
            composeRule.onNode(hasText(text) and hasAnyAncestor(hasTestTag("metrics_filter_row")))
                .performScrollTo()
                .performClick()
        }

        selectCategory(StringsRu.categoryCashFlow)
        composeRule.onNodeWithTag(cashFlowCard).assertExists()
        composeRule.onNodeWithTag(emergencyFundCard).assertDoesNotExist()

        selectCategory(StringsRu.categoryCapitalRisks)
        composeRule.onNodeWithTag(emergencyFundCard).assertExists()
        composeRule.onNodeWithTag(cashFlowCard).assertDoesNotExist()

        selectCategory(StringsRu.categoryBenchmarks)
        composeRule.onNodeWithTag(fidelityCard).assertExists()
        composeRule.onNodeWithTag(cashFlowCard).assertDoesNotExist()

        selectCategory(StringsRu.categoryAll)
        composeRule.onNodeWithTag(cashFlowCard).assertExists()
        composeRule.onNodeWithTag(emergencyFundCard).assertExists()
    }

    private fun metricCardTag(title: String): String = "metric_card_${title.replace(" ", "_")}"

    @Test
    fun `invalid birth date shows unavailable Fidelity subtitle in every language`() {
        val input = FinancialInputData.usAverageData().copy(birthDate = "invalid-date")
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    MetricsScreen(inputData = input, metrics = metrics, modifier = Modifier.testTag("invalid_age_metrics_list"))
                }
            }
        }
        composeRule.onNodeWithText(getAppStrings(AppLanguage.RU).categoryBenchmarks)
            .performScrollTo()
            .performClick()
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            val cardTag = "metric_card_${strings.metricFidelityTitle.replace(" ", "_")}"
            composeRule.onNodeWithTag("invalid_age_metrics_list").performScrollToNode(hasTestTag(cardTag))
            composeRule.onNodeWithText(strings.metricFidelitySubFormat(0, if (lang == AppLanguage.EN) "0.00" else "0,00")).assertDoesNotExist()
            val texts = composeRule.onNodeWithTag(cardTag).fetchSemanticsNode().config[SemanticsProperties.Text]
            assertTrue("$lang subtitle, target and progress must be unavailable", texts.count { it.text == strings.metricUnavailableValue } >= 3)
        }
    }

    @Test
    fun `zero cash flow subtitle says income equals expenses in every language`() {
        val input = FinancialInputData.usAverageData().copy(monthlyIncome = 4_800.0, monthlyExpenses = 4_800.0)
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    MetricsScreen(inputData = input, metrics = metrics)
                }
            }
        }

        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            composeRule.onNodeWithText(strings.metricCashFlowNeutralSub).assertExists()
            composeRule.onNodeWithText(strings.metricCashFlowNegativeSub).assertDoesNotExist()
        }
    }

    @Test
    fun `MetricsScreen does not display score out of ten badges on metric cards`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu
                ) {
                    MetricsScreen(inputData = input, metrics = metrics)
                }
            }
        }

        // Header card should be present
        composeRule.onNodeWithTag("metrics_header_card").assertExists()

        // None of the individual score badges (e.g. "/ 10") should appear in MetricsScreen
        composeRule.onAllNodesWithText("/ 10", substring = true).assertCountEquals(0)
    }
    @Test
    fun `when cashFlow is zero MetricsScreen displays income equals expenses subtitle`() {
        val input = FinancialInputData(
            monthlyIncome = 50_000.0,
            monthlyExpenses = 50_000.0
        )
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu
                ) {
                    MetricsScreen(inputData = input, metrics = metrics)
                }
            }
        }

        composeRule.onNodeWithText(StringsRu.metricCashFlowNeutralSub).assertExists()
    }

    @Test
    @Config(qualifiers = "w411dp-h891dp")
    fun `results export button is the only one at the bottom and keeps the original summary and share action`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))
        val startedIntents = mutableListOf<Intent>()
        val recordingContext = object : ContextWrapper(ApplicationProvider.getApplicationContext<Context>()) {
            override fun startActivity(intent: Intent) {
                startedIntents += intent
            }
        }

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu,
                    LocalContext provides recordingContext
                ) {
                    MetricsScreen(
                        inputData = input,
                        metrics = metrics,
                        modifier = Modifier.testTag("metrics_export_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("metrics_export_list")
            .performScrollToNode(hasTestTag("export_button"))

        composeRule.onAllNodes(hasTestTag("export_button"), useUnmergedTree = true)
            .assertCountEquals(1)
        composeRule.onNodeWithTag("export_button")
            .assertExists()
            .assertIsDisplayed()
            .assert(hasClickAction())
            .assertHeightIsAtLeast(48.dp)
            .assertTextEquals(StringsRu.exportButtonText)

        // Кнопка экспорта — последний элемент списка, после последней карточки показателя.
        val exportBounds = composeRule.onNodeWithTag("export_button").getUnclippedBoundsInRoot()
        val lastMetricBounds = composeRule.onNodeWithTag(metricCardTag(StringsRu.metricRealReturnTitle))
            .assertExists()
            .getUnclippedBoundsInRoot()
        val exportGap = exportBounds.top - lastMetricBounds.bottom
        assertTrue(
            "Export button must be below the last metric card with compact spacing (actual=$exportGap)",
            exportGap >= 4.dp && exportGap <= 10.dp
        )

        composeRule.onNodeWithTag("export_button").performClick()

        val chooser = startedIntents.single()
        assertEquals(Intent.ACTION_CHOOSER, chooser.action)
        assertEquals(StringsRu.exportChooserTitle, chooser.getStringExtra(Intent.EXTRA_TITLE))
        val sendIntent = requireNotNull(chooser.getParcelableExtra<Intent>(Intent.EXTRA_INTENT))
        assertEquals(Intent.ACTION_SEND, sendIntent.action)
        assertEquals("text/plain", sendIntent.type)
        assertEquals(
            AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.RU),
            sendIntent.getStringExtra(Intent.EXTRA_TEXT)
        )
    }

    @Test
    @Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `metric card formulas are single-line when collapsed and wrap completely when expanded`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))
        val language = mutableStateOf(AppLanguage.RU)

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    MetricsScreen(
                        inputData = input,
                        metrics = metrics,
                        modifier = Modifier.testTag("metrics_formula_list")
                    )
                }
            }
        }

        fun SemanticsNodeInteraction.readTextLayout(): TextLayoutResult {
            val results = mutableListOf<TextLayoutResult>()
            val action = fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action
            assertTrue("GetTextLayoutResult action must exist on formula node", action?.invoke(results) == true)
            assertTrue("TextLayoutResult must be populated", results.isNotEmpty())
            return results.first()
        }

        for (lang in listOf(AppLanguage.RU, AppLanguage.EN)) {
            composeRule.runOnIdle { language.value = lang }
            composeRule.waitForIdle()
            val strings = getAppStrings(lang)

            val allMetricTitles = listOf(
                strings.metricCashFlowTitle,
                strings.metricSavingsRateTitle,
                strings.metricEmergencyFundTitle,
                strings.metricSafetyMonthsTitle,
                strings.metricDebtRatioTitle,
                strings.metricNetWorthTitle,
                strings.metricFidelityTitle,
                strings.metricCfsTitle,
                strings.metricRealReturnTitle
            )
            var wrappedCount = 0

            allMetricTitles.forEach { title ->
                val cardTag = metricCardTag(title)
                composeRule.onNodeWithTag("metrics_formula_list")
                    .performScrollToNode(hasTestTag(cardTag))
                composeRule.waitForIdle()

                val formulaNode = composeRule.onNode(
                    hasTestTag("metric_card_formula") and hasAnyAncestor(hasTestTag(cardTag)),
                    useUnmergedTree = true
                )

                // Collapsed: strictly 1 line
                val collapsedLayout = formulaNode.readTextLayout()
                assertEquals(
                    "[$lang] Collapsed formula for '$title' must be 1 line",
                    1,
                    collapsedLayout.lineCount
                )
                assertEquals(
                    "[$lang] Collapsed formula for '$title' must allow ellipsis",
                    TextOverflow.Ellipsis,
                    collapsedLayout.layoutInput.overflow
                )
                val collapsedHeight = composeRule.onNodeWithTag(cardTag)
                    .getUnclippedBoundsInRoot().height

                fun assertExpandedLayoutFits(layout: TextLayoutResult, phase: String) {
                    val maxLineWidth = (0 until layout.lineCount)
                        .maxOfOrNull { layout.getLineRight(it) - layout.getLineLeft(it) } ?: 0f
                    val anyEllipsized = (0 until layout.lineCount).any { layout.isLineEllipsized(it) }
                    assertEquals(
                        "[$lang] $phase formula for '$title' must allow unlimited lines",
                        Int.MAX_VALUE,
                        layout.layoutInput.maxLines
                    )
                    assertEquals(
                        "[$lang] $phase formula for '$title' must not use ellipsis",
                        TextOverflow.Clip,
                        layout.layoutInput.overflow
                    )
                    assertTrue(
                        "[$lang] $phase formula for '$title' must fit without clipping " +
                            "(lines=${layout.lineCount}, size=${layout.size}, " +
                            "mpWidth=${layout.multiParagraph.width}, maxLineWidth=$maxLineWidth, " +
                            "ellipsized=$anyEllipsized, didOverflowH=${layout.didOverflowHeight}, " +
                            "constraints=${layout.layoutInput.constraints})",
                        !anyEllipsized &&
                            !layout.didOverflowHeight &&
                            maxLineWidth <= layout.size.width + 0.5f &&
                            layout.size.width <= layout.layoutInput.constraints.maxWidth
                    )
                }

                // Expand
                composeRule.onNodeWithTag(cardTag).performClick()
                composeRule.waitForIdle()

                val expandedLayout = formulaNode.readTextLayout()
                assertExpandedLayoutFits(expandedLayout, "Expanded")
                if (collapsedLayout.isLineEllipsized(0)) {
                    wrappedCount++
                    assertTrue(
                        "[$lang] Expanded formula for '$title' was ellipsized when collapsed so it must wrap across multiple lines (lines=${expandedLayout.lineCount})",
                        expandedLayout.lineCount > 1
                    )
                }
                val expandedHeight = composeRule.onNodeWithTag(cardTag)
                    .getUnclippedBoundsInRoot().height
                assertTrue(
                    "[$lang] Expanded card '$title' must grow vertically (was $collapsedHeight, now $expandedHeight)",
                    expandedHeight > collapsedHeight
                )

                // Repeated collapse/expand cycle
                composeRule.onNodeWithTag(cardTag).performClick()
                composeRule.waitForIdle()

                val recollapsedLayout = formulaNode.readTextLayout()
                assertEquals(
                    "[$lang] Re-collapsed formula for '$title' must return to 1 line",
                    1,
                    recollapsedLayout.lineCount
                )
                val recollapsedHeight = composeRule.onNodeWithTag(cardTag)
                    .getUnclippedBoundsInRoot().height
                assertEquals(
                    "[$lang] Re-collapsed card '$title' height must match initial collapsed height",
                    collapsedHeight.value,
                    recollapsedHeight.value,
                    0.5f
                )

                composeRule.onNodeWithTag(cardTag).performClick()
                composeRule.waitForIdle()
                val reexpandedLayout = formulaNode.readTextLayout()
                assertExpandedLayoutFits(reexpandedLayout, "Re-expanded")
                assertEquals(
                    "[$lang] Re-expanded formula lineCount for '$title' must match initial expanded lineCount",
                    expandedLayout.lineCount,
                    reexpandedLayout.lineCount
                )

                // Leave collapsed before scrolling to next card
                composeRule.onNodeWithTag(cardTag).performClick()
                composeRule.waitForIdle()
            }

            assertTrue(
                "[$lang] Expected at least 3 formulas to wrap at 360dp phone width, but saw $wrappedCount",
                wrappedCount >= 3
            )
        }
    }
}
