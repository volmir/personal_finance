package com.example

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.data.model.TrajectoryPoint
import com.example.domain.FinanceCalculator
import com.example.domain.SimulatorScaleCalculator
import com.example.ui.FireSimulationState
import com.example.ui.FireScenarioToggles
import com.example.ui.components.ScenarioTogglesPanel
import com.example.ui.screens.FireRetirementScreen
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FireAccessibilityRobolectricTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `chart and sliders expose localized data and preserve adjustment actions`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val language = mutableStateOf(AppLanguage.EN)
        val simulation = mutableStateOf(FireSimulationState())
        val status = mutableStateOf(FireStatus.REACHABLE)
        val points = listOf(
            TrajectoryPoint(38.0, 0.0, 12000.0, 400000.0, false),
            TrajectoryPoint(65.0, 27.0, 500000.0, 450000.0, true)
        )
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(
                        input, metrics.copy(fireStatus = status.value), metrics,
                        simulation.value, points,
                        onUpdateSimulation = { savings, returns, expenses ->
                            simulation.value = simulation.value.copy(
                                extraMonthlySavings = savings,
                                realReturnAdjustment = returns,
                                expenseReduction = expenses
                            )
                        },
                        onToggleScenario = {
                            simulation.value = simulation.value.copy(scenarios = simulation.value.scenarios.copy(
                                salaryIncrease20 = !simulation.value.scenarios.salaryIncrease20
                            ))
                        }, onResetSimulation = {},
                        modifier = Modifier.testTag("a11y_list")
                    )
                }
            }
        }
        val configs = listOf(
            SimulatorScaleCalculator.calculateSavingsConfig(metrics.cashFlow, input.monthlyIncome, input.monthlyExpenses, input.currencySymbol),
            SimulatorScaleCalculator.calculateExpensesConfig(input.monthlyExpenses, input.monthlyIncome, currency = input.currencySymbol),
            SimulatorScaleCalculator.calculateReturnConfig(metrics.realReturnPercent, input.inflationPercent)
        )
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            composeRule.onNodeWithTag("a11y_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            for (fireStatus in FireStatus.values()) {
                composeRule.runOnIdle { status.value = fireStatus }
                composeRule.onNodeWithTag("a11y_list").performScrollToNode(hasTestTag("fire_trajectory_chart"))
                val description = composeRule.onNodeWithTag("fire_trajectory_chart")
                    .fetchSemanticsNode().config[SemanticsProperties.ContentDescription].single()
                assertTrue(description.startsWith(strings.fireChartBaseForecast))
                assertTrue(description.contains(strings.fireChartAxes(
                    Formatters.formatRatio(38.0, lang), Formatters.formatRatio(65.0, lang),
                    Formatters.formatMoney(500000.0, input.currencySymbol, lang)
                )))
                assertTrue(description.contains(Formatters.formatMoney(12000.0, input.currencySymbol, lang)))
                assertTrue(description.contains(Formatters.formatMoney(450000.0, input.currencySymbol, lang)))
                assertTrue(description.contains(Formatters.formatFireRetirementAge(metrics.currentAge + metrics.yearsToRetirement, fireStatus, lang)))
                assertFalse(description.contains("NaN"))
                assertFalse(description.contains("Infinity"))
            }
            composeRule.onNodeWithTag("a11y_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            val names = listOf(strings.fireSliderSavings, strings.fireSliderExpenses, strings.fireSliderReturn)
            for ((index, tag) in listOf("fire_slider_savings", "fire_slider_expenses", "fire_slider_return").withIndex()) {
                composeRule.onNodeWithTag("a11y_list").performScrollToNode(hasTestTag(tag))
                val node = composeRule.onNodeWithTag(tag)
                val config = node.fetchSemanticsNode().config
                assertEquals(listOf(names[index]), config[SemanticsProperties.ContentDescription])
                assertTrue(config[SemanticsProperties.StateDescription].isNotBlank())
                val range = config[SemanticsProperties.ProgressBarRangeInfo]
                assertEquals(configs[index].minValue..configs[index].maxValue, range.range)
                assertEquals(configs[index].steps, range.steps)
                fun formatted(value: Double): String = if (index == 2) {
                    strings.firePercentagePoints(Formatters.formatRatio(value, lang))
                } else Formatters.formatMoney(value, input.currencySymbol, lang)
                val description = config[SemanticsProperties.StateDescription]
                assertTrue(description.contains(formatted(configs[index].minValue.toDouble())))
                assertTrue(description.contains(formatted(configs[index].maxValue.toDouble())))
                assertTrue(description.contains(formatted(configs[index].stepSize)))
                val currentValue = when (index) {
                    0 -> Formatters.formatMoney(metrics.cashFlow, input.currencySymbol, lang)
                    1 -> Formatters.formatMoney(metrics.annualExpenses / 12.0, input.currencySymbol, lang)
                    else -> Formatters.formatPercent(metrics.realReturnPercent, lang)
                }
                assertTrue(description.contains(currentValue))
                if (index == 1) assertTrue(description.contains(strings.fireSliderReductionAccessibility))
                for (next in listOf(configs[index].minValue, configs[index].maxValue, configs[index].stepSize.toFloat())) {
                    node.performSemanticsAction(SemanticsActions.SetProgress) { it(next) }
                    composeRule.runOnIdle {
                        val actual = listOf(simulation.value.extraMonthlySavings, simulation.value.expenseReduction, simulation.value.realReturnAdjustment)[index]
                        assertEquals(next.toDouble(), actual, 0.0001)
                    }
                    assertTrue(node.fetchSemanticsNode().config[SemanticsProperties.StateDescription].contains(formatted(next.toDouble())))
                }
            }
            composeRule.onNodeWithTag("a11y_list").performScrollToNode(hasTestTag("switch_pos_salary_increase"))
            val toggle = composeRule.onNodeWithTag("switch_pos_salary_increase").fetchSemanticsNode().config
            assertEquals(Role.Switch, toggle[SemanticsProperties.Role])
            assertTrue(toggle[SemanticsProperties.ContentDescription].single().contains(strings.scenarioSalaryIncreaseTitle))
            val card = composeRule.onNodeWithTag("scenario_card_pos_salary_increase")
            assertEquals(Role.Switch, card.fetchSemanticsNode().config[SemanticsProperties.Role])
            card.performSemanticsAction(SemanticsActions.OnClick) { it() }
            assertEquals(ToggleableState.On, composeRule.onNodeWithTag("switch_pos_salary_increase")
                .fetchSemanticsNode().config[SemanticsProperties.ToggleableState])
            composeRule.onNodeWithTag("switch_pos_salary_increase").performSemanticsAction(SemanticsActions.OnClick) { it() }
            assertEquals(ToggleableState.Off, card.fetchSemanticsNode().config[SemanticsProperties.ToggleableState])
        }
    }

    @Test
    fun `all scenario controls expose switch roles and localized labels`() {
        val language = mutableStateOf(AppLanguage.EN)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    ScenarioTogglesPanel(
                        FireScenarioToggles(), FinancialInputData.usAverageData(), {},
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val switches = composeRule.onAllNodes(SemanticsMatcher("scenario switch") {
                it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith("switch_") == true
            })
            switches.assertCountEquals(14)
            for (node in switches.fetchSemanticsNodes()) {
                assertEquals(Role.Switch, node.config[SemanticsProperties.Role])
                assertEquals(ToggleableState.Off, node.config[SemanticsProperties.ToggleableState])
                assertTrue(node.config[SemanticsProperties.ContentDescription].single().isNotBlank())
                assertTrue(node.config.contains(SemanticsActions.OnClick))
            }
        }
    }
}
