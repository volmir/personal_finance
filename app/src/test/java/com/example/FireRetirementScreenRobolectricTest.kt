package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.FireSimulationState
import com.example.ui.screens.FireRetirementScreen
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FireRetirementScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `parameters show the displayed real return during simulation and after reset in every language`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0)
        val simulation = mutableStateOf(FireSimulationState())
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(input, metrics, simulated, simulation.value, emptyList(),
                        onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {}, onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            for (delta in listOf(1.0, 0.0)) {
                composeRule.runOnIdle { simulation.value = FireSimulationState(realReturnAdjustment = delta) }
                composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_parameters_card"))
                val rate = Formatters.formatPercent(metrics.realReturnPercent + delta, lang)
                composeRule.onAllNodes(hasText(rate) and hasAnyAncestor(hasTestTag("fire_parameters_card")), useUnmergedTree = true)
                    .assertCountEquals(1)
            }
        }
    }
}
