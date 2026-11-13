package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.screens.MetricsScreen
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import com.example.util.localization.getAppStrings
import org.junit.Rule
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
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
}
