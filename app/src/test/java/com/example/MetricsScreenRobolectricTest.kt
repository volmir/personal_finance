package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.screens.MetricsScreen
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MetricsScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

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
