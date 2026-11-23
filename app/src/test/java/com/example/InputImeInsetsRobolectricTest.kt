package com.example

import android.app.Application
import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ApplicationProvider
import com.example.ui.FinanceApp
import com.example.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h800dp-mdpi")
class InputImeInsetsRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `focused last field stays above IME without duplicate navigation inset`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app).also { it.dismissSampleDataNotice() }
        lateinit var composeView: View
        composeRule.activity.enableEdgeToEdge()
        composeRule.setContent {
            val view = LocalView.current
            SideEffect { composeView = view }
            FinanceApp(viewModel)
        }

        fun dispatchInsets(imeBottom: Int) {
            composeRule.runOnIdle {
                ViewCompat.dispatchApplyWindowInsets(
                    composeView,
                    WindowInsetsCompat.Builder()
                        .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, 24, 0, 0))
                        .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, 24))
                        .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, imeBottom))
                        .setVisible(WindowInsetsCompat.Type.ime(), imeBottom > 0)
                        .build()
                )
            }
            composeRule.waitForIdle()
        }

        dispatchInsets(0)
        val navClosed = composeRule.onNodeWithTag("bottom_nav_bar").fetchSemanticsNode().boundsInRoot
        val list = composeRule.onNode(hasScrollToIndexAction())
        list.performScrollToNode(hasTestTag("input_life_expectancy"))
        // Place the field near the bottom before focus so that IME really would cover it.
        val fieldBefore = composeRule.onNodeWithTag("input_life_expectancy").getUnclippedBoundsInRoot()
        list.performSemanticsAction(SemanticsActions.ScrollBy) {
            it(0f, fieldBefore.bottom.value - (navClosed.top - 8f))
        }
        composeRule.onNodeWithTag("input_life_expectancy").performClick().assertIsFocused()
        val focusedBefore = composeRule.onNodeWithTag("input_life_expectancy").getUnclippedBoundsInRoot()
        assertTrue("IME must cover the original field position", focusedBefore.bottom.value > navClosed.bottom - 300f)

        dispatchInsets(300)
        val navOpen = composeRule.onNodeWithTag("bottom_nav_bar").fetchSemanticsNode().boundsInRoot
        // IME replaces the 24 px system navigation inset; it must not be added twice.
        assertEquals(300f, navClosed.bottom - navOpen.bottom, 1f)
        assertEquals(navClosed.height - 24f, navOpen.height, 1f)
        val field = composeRule.onNodeWithTag("input_life_expectancy")
        field.assertIsFocused().assertIsDisplayed()
        val fieldBounds = field.getUnclippedBoundsInRoot()
        assertTrue("last field must fit above the navigation bar and IME", fieldBounds.bottom.value <= navOpen.top)
        assertTrue("last field must fit below the top bar", fieldBounds.top.value >= list.fetchSemanticsNode().boundsInRoot.top)

        // Scrolling and navigation remain available while the keyboard is open.
        list.performScrollToNode(hasTestTag("go_to_metrics_button"))
        composeRule.onNodeWithTag("go_to_metrics_button").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("metrics_header_card").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_item_input").performClick()
        dispatchInsets(0)
        val navRestored = composeRule.onNodeWithTag("bottom_nav_bar").fetchSemanticsNode().boundsInRoot
        assertEquals(navClosed, navRestored)
        composeRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasTestTag("input_monthly_income"))
        composeRule.onNodeWithTag("input_monthly_income").assertIsDisplayed()
    }
}
