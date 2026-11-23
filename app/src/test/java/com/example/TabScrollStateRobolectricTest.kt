package com.example

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.ViewModelProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class TabScrollStateRobolectricTest {
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
    fun `main tabs restore independent lower section positions including inactive tabs after recreation`() {
        val targets = linkedMapOf(
            "input" to "go_to_metrics_button",
            "metrics" to "fidelity_progress_indicator",
            "scores" to "developer_disclaimer_card"
        )
        val positions = targets.mapValues { (tab, target) ->
            openTab(tab)
            scrollTo(target)
            position().also { assertTrue("$tab must be scrolled", it > 0f) }
        }

        // Все три экрана удалены из Crossfade до восстановления, FIRE остаётся наверху.
        openTab("fire")
        assertEquals(0f, position(), 0f)
        for ((tab, target) in targets) {
            openTab(tab)
            assertEquals("$tab position after tab switch", positions.getValue(tab), position(), 0f)
            composeRule.onNodeWithTag(target).assertIsDisplayed()
        }
        openTab("fire")
        composeRule.activityRule.scenario.recreate()
        for ((tab, target) in targets) {
            openTab(tab)
            assertEquals("$tab position after recreation", positions.getValue(tab), position(), 0f)
            composeRule.onNodeWithTag(target).assertIsDisplayed()
        }
    }

    @Test
    fun `FIRE restores lower section after leaving tab and recreating Activity`() {
        assertFireScrollRestored(simulation = false, target = "fire_trajectory_card")
    }

    @Test
    fun `Simulation restores lower section after leaving tab and recreating Activity`() {
        assertFireScrollRestored(simulation = true, target = "switch_job_loss")
    }

    private fun assertFireScrollRestored(simulation: Boolean, target: String) {
        openTab("fire")
        if (simulation) {
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        }
        scrollTo(target)
        val expected = position()
        assertTrue(expected > 0f)
        openTab("metrics")
        openTab("fire")
        assertEquals(expected, position(), 0f)
        composeRule.onNodeWithTag(target).assertIsDisplayed()
        openTab("input")
        composeRule.activityRule.scenario.recreate()
        openTab("fire")
        assertEquals(expected, position(), 0f)
        composeRule.onNodeWithTag(target).assertIsDisplayed()
    }

    private fun openTab(tab: String) {
        composeRule.onNodeWithTag("nav_item_$tab").performClick().assertIsSelected()
        composeRule.waitForIdle()
    }

    private fun scrollTo(tag: String) {
        list().performScrollToNode(hasTestTag(tag))
        composeRule.onNodeWithTag(tag).performScrollTo().assertIsDisplayed()
    }

    private fun list() = composeRule.onNode(
        hasScrollAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)
    )

    private fun position(): Float = list().fetchSemanticsNode()
        .config[SemanticsProperties.VerticalScrollAxisRange].value()
}
