package com.example

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.ViewModelProvider
import com.example.ui.MainViewModel
import com.example.util.localization.getAppStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "ru-w320dp-h800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class UiDisclosureStateRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun dismissNotice() {
        ReflectionHelpers.setStaticField(ViewModelProvider.AndroidViewModelFactory::class.java, "_instance", null)
        if (composeRule.onAllNodesWithTag("sample_data_notice_confirm").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("sample_data_notice_confirm").performClick()
        }
    }

    @Test
    fun `filter scroll and expanded formula survive tabs and Activity recreation`() {
        val strings = getAppStrings(viewModel().appLanguage.value)
        openTab("metrics")
        selectFilter(strings.categoryBenchmarks)
        val expectedScroll = filterPosition()
        assertTrue("Last filter must scroll the row", expectedScroll > 0f)
        val title = strings.metricFidelityTitle
        card(title).performClick()
        assertFormulaExpanded(title, true)
        openTab("input")
        openTab("metrics")

        // После восстановления Metrics вертикальная позиция LazyColumn
        // может находиться возле карточки Fidelity.
        // Возвращаем первый элемент списка в область композиции.
        list().performScrollToIndex(0)

        filter(strings.categoryBenchmarks).assertIsSelected()
        assertEquals(expectedScroll, filterPosition(), 0f)
        assertFormulaExpanded(title, true)
        openTab("scores")
        composeRule.activityRule.scenario.recreate()
        openTab("metrics")

        // После пересоздания Activity восстанавливается вертикальная
        // позиция LazyColumn, поэтому строка фильтров может быть скрыта.
        list().performScrollToIndex(0)

        filter(strings.categoryBenchmarks).assertIsSelected()
        assertEquals(expectedScroll, filterPosition(), 0f)
        assertFormulaExpanded(title, true)
    }

    @Test
    fun `card disclosure stays with metric when filters remove and reinsert items`() {
        val strings = getAppStrings(viewModel().appLanguage.value)
        openTab("metrics")
        card(strings.metricCashFlowTitle).performClick()
        selectFilter(strings.categoryCapitalRisks)
        assertFormulaExpanded(strings.metricEmergencyFundTitle, false)
        card(strings.metricEmergencyFundTitle).performClick()
        selectFilter(strings.categoryCashFlow)
        assertFormulaExpanded(strings.metricCashFlowTitle, true)
        assertFormulaExpanded(strings.metricSavingsRateTitle, false)
        selectFilter(strings.categoryCapitalRisks)
        assertFormulaExpanded(strings.metricEmergencyFundTitle, true)
        // Удаление заголовка из композиции не должно сбрасывать прокрутку фильтров.
        val expectedScroll = filterPosition()
        list().performScrollToNode(hasTestTag(cardTag(strings.metricNetWorthTitle)))
        list().performScrollToIndex(0)
        assertEquals(expectedScroll, filterPosition(), 0f)
        openTab("scores")
        composeRule.activityRule.scenario.recreate()
        openTab("metrics")
        selectFilter(strings.categoryCashFlow)
        assertFormulaExpanded(strings.metricCashFlowTitle, true)
        selectFilter(strings.categoryCapitalRisks)
        assertFormulaExpanded(strings.metricEmergencyFundTitle, true)
    }

    @Test
    fun `input hints restore independently after disposal tabs and Activity recreation`() {
        val vm = viewModel()
        val input = vm.inputData.value
        val simulation = vm.fireSimulation.value
        val strings = getAppStrings(vm.appLanguage.value)
        openTab("input")
        composeRule.onNodeWithText(strings.incomeHintCollapsed).performScrollTo().performClick()
        composeRule.onNodeWithText(strings.incomeHintExpanded).assertExists()
        list().performScrollToNode(hasTestTag("go_to_metrics_button"))
        list().performScrollToIndex(1)
        composeRule.onNodeWithText(strings.incomeHintExpanded).assertExists()
        composeRule.onNodeWithText(strings.expensesHintCollapsed).assertExists()
        openTab("metrics")
        openTab("input")
        composeRule.onNodeWithText(strings.incomeHintExpanded).assertExists()
        list().performScrollToNode(hasTestTag("go_to_metrics_button"))
        openTab("fire")
        composeRule.activityRule.scenario.recreate()
        openTab("input")
        list().performScrollToIndex(1)
        composeRule.onNodeWithText(strings.incomeHintExpanded).assertExists()
        composeRule.onNodeWithText(strings.expensesHintCollapsed).assertExists()
        assertEquals(input, viewModel().inputData.value)
        assertEquals(simulation, viewModel().fireSimulation.value)
        composeRule.onNodeWithText(strings.incomeHintExpanded).performScrollTo().performClick()
        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithText(strings.incomeHintCollapsed).assertExists()
    }

    private fun viewModel() = ViewModelProvider(composeRule.activity)[MainViewModel::class.java]

    private fun openTab(tab: String) {
        composeRule.onNodeWithTag("nav_item_$tab").performClick().assertIsSelected()
        composeRule.waitForIdle()
    }

    private fun list() = composeRule.onNode(
        hasScrollAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)
    )

    private fun filter(text: String) = composeRule.onNode(
        hasText(text) and hasAnyAncestor(hasTestTag("metrics_filter_row"))
    )

    private fun selectFilter(text: String) {
        list().performScrollToIndex(0)
        filter(text).performScrollTo().performClick()
    }

    private fun filterPosition() = composeRule.onNodeWithTag("metrics_filter_row").fetchSemanticsNode()
        .config[SemanticsProperties.HorizontalScrollAxisRange].value()

    private fun cardTag(title: String) = "metric_card_${title.replace(" ", "_")}"
    private fun card(title: String) = composeRule.onNodeWithTag(cardTag(title))

    private fun assertFormulaExpanded(title: String, expanded: Boolean) {
        list().performScrollToNode(hasTestTag(cardTag(title)))
        val layouts = mutableListOf<TextLayoutResult>()
        val node = composeRule.onNode(
            hasTestTag("metric_card_formula") and hasAnyAncestor(hasTestTag(cardTag(title))),
            useUnmergedTree = true
        ).fetchSemanticsNode()
        assertTrue(node.config[SemanticsActions.GetTextLayoutResult].action!!.invoke(layouts))
        assertEquals(if (expanded) Int.MAX_VALUE else 1, layouts.single().layoutInput.maxLines)
    }
}
