package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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

        // Переключаемся на другую основную вкладку, пересоздаём Activity и возвращаемся в FIRE
        composeRule.onNodeWithTag("nav_item_metrics").performClick()
        composeRule.onNodeWithTag("metrics_header_card").assertIsDisplayed()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithTag("nav_item_fire").performClick()
        composeRule.onNodeWithTag("fire_subtab_simulation").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()
        composeRule.onAllNodes(hasTestTag("fire_trajectory_card"), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .let { org.junit.Assert.assertTrue(it.isEmpty()) }
    }
}
