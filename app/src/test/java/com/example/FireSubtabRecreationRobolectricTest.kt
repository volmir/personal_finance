package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.MainViewModel
import com.example.ui.FireSimulationState
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import androidx.lifecycle.ViewModelProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class FireSubtabRecreationRobolectricTest {
    @Before
    fun resetViewModelFactory() {
        ReflectionHelpers.setStaticField(ViewModelProvider.AndroidViewModelFactory::class.java, "_instance", null)
    }

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `selected Simulation subtab survives Activity recreation`() {
        val confirmNodes = composeRule.onAllNodesWithTag("sample_data_notice_confirm").fetchSemanticsNodes()
        if (confirmNodes.isNotEmpty()) {
            composeRule.onNodeWithTag("sample_data_notice_confirm").performClick()
        }

        composeRule.onNodeWithTag("nav_item_fire").performClick()
        composeRule.onNodeWithTag("fire_subtab_base").assertIsSelected()

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_subtab_simulation").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()

        val viewModel = ViewModelProvider(composeRule.activity)[MainViewModel::class.java]
        val baseInput = viewModel.inputData.value
        val baseMetrics = viewModel.metrics.value
        val list = composeRule.onNode(hasScrollAction())
        list.performScrollToNode(hasTestTag("switch_pos_salary_increase"))
        composeRule.onNodeWithTag("switch_pos_salary_increase").performScrollTo().assertIsDisplayed().performClick().assertIsOn()
        val activeState = viewModel.fireSimulation.value
        assertEquals(1, activeState.activeChangesCount)
        val expected = Formatters.formatFireSimulationComparison(baseMetrics,
            viewModel.simulatedMetrics.value, activeState.activeChangesCount, AppLanguage.EN)
        list.performScrollToIndex(0)
        composeRule.onNodeWithTag("fire_sim_years_value", true).assertTextEquals(expected.years)
        composeRule.onNodeWithTag("fire_sim_delta_value", true).assertTextEquals(expected.delta)
        for (tab in listOf("input", "metrics", "scores")) {
            composeRule.onNodeWithTag("nav_item_$tab").performClick()
            composeRule.onNodeWithTag("nav_item_fire").performClick()
            composeRule.onNodeWithTag("fire_subtab_simulation").assertIsSelected()
            composeRule.onNodeWithTag("fire_sim_years_value", true).assertTextEquals(expected.years)
            assertEquals(activeState, viewModel.fireSimulation.value)
        }

        // Переключаемся на другую основную вкладку, пересоздаём Activity и возвращаемся в FIRE
        composeRule.onNodeWithTag("nav_item_metrics").performClick()
        composeRule.onNodeWithTag("metrics_header_card").assertIsDisplayed()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithTag("nav_item_fire").performClick()
        composeRule.onNodeWithTag("fire_subtab_simulation").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_sim_years_value", true).assertTextEquals(expected.years)
        composeRule.onNodeWithTag("fire_sim_delta_value", true).assertTextEquals(expected.delta)
        composeRule.onNodeWithTag("fire_sim_reset_button").performClick().assertIsNotEnabled()
        assertEquals(FireSimulationState(), viewModel.fireSimulation.value)
        assertEquals(baseInput, viewModel.inputData.value)
        assertEquals(baseMetrics, viewModel.metrics.value)
        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithTag("fire_sim_reset_button").assertIsNotEnabled()
        val reset = Formatters.formatFireSimulationComparison(baseMetrics, baseMetrics, 0, AppLanguage.EN)
        composeRule.onNodeWithTag("fire_sim_years_value", true).assertTextEquals(reset.years)
        composeRule.onNodeWithTag("fire_sim_delta_value", true).assertTextEquals(reset.delta)
        composeRule.onAllNodes(hasTestTag("fire_trajectory_card"), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .let { org.junit.Assert.assertTrue(it.isEmpty()) }
    }
}
