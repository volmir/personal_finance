package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.click
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.model.FireStatus
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.FireSimulationState
import com.example.ui.FireScenarioToggles
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusColors
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.screens.FireRetirementScreen
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
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w320dp-h640dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FireSimulationStickySummaryRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `simulation starts with a sticky three row comparison instead of a duplicate hero`() {
        val input = FinancialInputData.usAverageData()
        val base = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6)).copy(
            fireStatus = FireStatus.REACHABLE, yearsToRetirement = 24.0, retirementAge = 62.0
        )
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides getAppStrings(AppLanguage.RU)
                ) {
                    FireRetirementScreen(input, base, base.copy(yearsToRetirement = 21.5, retirementAge = 59.5),
                        FireSimulationState(extraMonthlySavings = 100.0), emptyList(),
                        onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {}, onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_sim_years_value", useUnmergedTree = true).assertTextEquals("24 → 21,5 года")
        composeRule.onNodeWithTag("fire_sim_age_value", useUnmergedTree = true).assertTextEquals("62 → 59,5")
        composeRule.onNodeWithTag("fire_sim_delta_value", useUnmergedTree = true).assertTextEquals("↓ −2,5 года")
        composeRule.onNodeWithTag("fire_hero_card").assertDoesNotExist()
    }

    @Test
    fun `active count includes each slider and reset restores a neutral base without arrows`() {
        val input = FinancialInputData.usAverageData()
        val base = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6)).copy(
            fireStatus = FireStatus.REACHABLE, yearsToRetirement = 24.0, retirementAge = 62.0
        )
        val simulation = mutableStateOf(FireSimulationState())
        var resetCalls = 0
        composeRule.setContent {
            MyApplicationTheme(dynamicColor = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides getAppStrings(AppLanguage.RU)
                ) {
                    FireRetirementScreen(input, base, base.copy(yearsToRetirement = 21.5, retirementAge = 59.5),
                        simulation.value, emptyList(), onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {},
                        onResetSimulation = { resetCalls++; simulation.value = FireSimulationState() })
                }
            }
        }
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        val cases = listOf(
            FireSimulationState(),
            FireSimulationState(extraMonthlySavings = 100.0),
            FireSimulationState(realReturnAdjustment = -1.0),
            FireSimulationState(expenseReduction = 100.0),
            FireSimulationState(scenarios = FireScenarioToggles(salaryIncrease20 = true, jobLoss = true)),
            FireSimulationState(100.0, -1.0, 100.0, FireScenarioToggles(salaryIncrease20 = true, jobLoss = true))
        )
        for ((state, count) in cases.zip(listOf(0, 1, 1, 1, 2, 5))) {
            composeRule.runOnIdle { simulation.value = state }
            composeRule.onNodeWithTag("fire_sim_changes_count").assertTextEquals("Изменений: $count")
            if (count == 0) composeRule.onNodeWithTag("fire_sim_reset_button").assertIsNotEnabled()
            else composeRule.onNodeWithTag("fire_sim_reset_button").assertIsEnabled()
        }
        composeRule.onNodeWithTag("fire_sim_reset_button").performClick()
        assertEquals(1, resetCalls)
        composeRule.onNodeWithTag("fire_sim_years_value", true).assertTextEquals("24 года")
        composeRule.onNodeWithTag("fire_sim_age_value", true).assertTextEquals("62")
        composeRule.onNodeWithTag("fire_sim_delta_value", true).assertTextEquals("Без изменений")
        composeRule.onNodeWithTag("fire_sim_reset_button").assertIsNotEnabled()
        composeRule.onAllNodes(hasText("→", substring = true), useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `three rows align and wrap at 320dp in both themes all languages and large fonts`() {
        val input = FinancialInputData.usAverageData()
        val base = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6)).copy(
            fireStatus = FireStatus.REACHABLE, yearsToRetirement = 24.0, retirementAge = 62.0
        )
        val language = mutableStateOf(AppLanguage.RU)
        val dark = mutableStateOf(false)
        val scale = mutableStateOf(1f)
        val simulated = mutableStateOf(base.copy(yearsToRetirement = 21.5, retirementAge = 59.5))
        composeRule.setContent {
            val density = LocalDensity.current
            MyApplicationTheme(darkTheme = dark.value, dynamicColor = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value),
                    LocalDensity provides Density(density.density, scale.value)
                ) {
                    FireRetirementScreen(input, base, simulated.value, FireSimulationState(extraMonthlySavings = 100.0),
                        emptyList(), onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {}, onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        for (isDark in listOf(false, true)) for (font in listOf(1f, 1.6f)) for (lang in AppLanguage.values()) {
            for ((status, years) in listOf(FireStatus.REACHABLE to 21.5, FireStatus.REACHABLE to 26.5,
                FireStatus.UNREACHABLE to 21.5, FireStatus.INVALID_AGE to 21.5)) {
                composeRule.runOnIdle {
                    dark.value = isDark
                    scale.value = font
                    language.value = lang
                    simulated.value = base.copy(fireStatus = status, yearsToRetirement = years, retirementAge = 38.0 + years)
                }
                var previousBottom = 0f
                for (row in listOf("years", "age", "delta")) {
                    val label = composeRule.onNodeWithTag("fire_sim_${row}_label", true)
                    val value = composeRule.onNodeWithTag("fire_sim_${row}_value", true)
                    label.assertIsDisplayed()
                    value.assertIsDisplayed()
                    val left = label.fetchSemanticsNode().boundsInRoot
                    val right = value.fetchSemanticsNode().boundsInRoot
                    assertTrue("$lang $font $status two columns", left.right < right.left)
                    assertTrue("$lang $font $status row order", minOf(left.top, right.top) >= previousBottom)
                    previousBottom = maxOf(left.bottom, right.bottom)
                    val labelLayout = assertFits("fire_sim_${row}_label")
                    val valueLayout = assertFits("fire_sim_${row}_value")
                    assertEquals("$lang $font $status baseline", left.top + labelLayout.firstBaseline,
                        right.top + valueLayout.firstBaseline, 1f)
                }
                val controls = composeRule.onNodeWithTag("fire_sim_changes_count").fetchSemanticsNode().boundsInRoot
                assertTrue("controls below table", controls.top >= previousBottom)
                composeRule.onNodeWithTag("fire_sim_reset_button").assertIsDisplayed()
                val expected = when {
                    status != FireStatus.REACHABLE -> AuditIndicatorStatus.NEUTRAL
                    years < 24.0 -> AuditIndicatorStatus.POSITIVE
                    else -> AuditIndicatorStatus.NEGATIVE
                }
                assertEquals(StatusColors.getTextColor(expected, isDark), assertFits("fire_sim_delta_value").layoutInput.style.color)
            }
        }
    }

    @Test
    fun `only the opaque summary pins below the top bar while content scrolls down and back`() {
        val input = FinancialInputData.usAverageData()
        val base = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6)).copy(
            fireStatus = FireStatus.REACHABLE, yearsToRetirement = 24.0, retirementAge = 62.0
        )
        val dark = mutableStateOf(false)
        val scale = mutableStateOf(1f)
        var background = Color.Transparent
        var resetCalls = 0
        var leverCalls = 0
        composeRule.setContent {
            val density = LocalDensity.current
            MyApplicationTheme(darkTheme = dark.value, dynamicColor = false) {
                background = MaterialTheme.colorScheme.background
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides getAppStrings(AppLanguage.RU),
                    LocalDensity provides Density(density.density, scale.value)
                ) {
                    Column(Modifier.fillMaxSize().background(Color.Magenta)) {
                        Spacer(Modifier.fillMaxWidth().height(56.dp).testTag("top_bar"))
                        FireRetirementScreen(input, base, base.copy(yearsToRetirement = 21.5, retirementAge = 59.5),
                            FireSimulationState(extraMonthlySavings = 100.0), emptyList(),
                            onUpdateSimulation = { _, _, _ -> leverCalls++ }, onToggleScenario = { leverCalls++ },
                            onResetSimulation = { resetCalls++ },
                            modifier = Modifier.weight(1f).testTag("fire_test_list"))
                        Text("bottom tabs", modifier = Modifier.height(56.dp).testTag("bottom_tabs"))
                    }
                }
            }
        }
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        val list = composeRule.onNodeWithTag("fire_test_list")
        for (isDark in listOf(false, true)) for (font in listOf(1f, 1.6f)) {
            composeRule.runOnIdle {
                dark.value = isDark
                scale.value = font
            }
            list.performScrollToIndex(0)
            composeRule.onNodeWithTag("fire_subtab_selector").assertIsDisplayed()
            val initialTop = composeRule.onNodeWithTag("fire_sim_sticky_summary").fetchSemanticsNode().boundsInRoot.top
            list.performTouchInput { swipeUp() }
            composeRule.waitForIdle()
            val viewport = list.fetchSemanticsNode().boundsInRoot
            val pinned = composeRule.onNodeWithTag("fire_sim_sticky_summary").fetchSemanticsNode().boundsInRoot
            assertTrue(initialTop > pinned.top)
            assertEquals(viewport.top, pinned.top, 1f)
            assertEquals(viewport.left, pinned.left, 1f)
            assertEquals(viewport.right, pinned.right, 1f)
            assertEquals(composeRule.onNodeWithTag("top_bar").fetchSemanticsNode().boundsInRoot.bottom, pinned.top, 1f)
            assertTrue("content below panel", pinned.bottom < viewport.bottom)
            composeRule.onNodeWithTag("fire_subtab_selector").assertIsNotDisplayed()
            val before = capturePanel()
            assertOpaqueInsets(before, background)
            list.performTouchInput { swipeUp() }
            composeRule.waitForIdle()
            assertTrue("content must not bleed through the header", before.sameAs(capturePanel()))
            val beforeReset = resetCalls
            composeRule.onNodeWithTag("fire_sim_reset_button").performTouchInput { click() }
            composeRule.runOnIdle {
                assertEquals(beforeReset + 1, resetCalls)
                assertEquals(0, leverCalls)
            }
            composeRule.onNodeWithTag("bottom_tabs").assertIsDisplayed()
            list.performScrollToIndex(0)
            composeRule.onNodeWithTag("fire_subtab_selector").assertIsDisplayed()
            assertEquals(initialTop, composeRule.onNodeWithTag("fire_sim_sticky_summary").fetchSemanticsNode().boundsInRoot.top, 1f)
        }
    }

    private fun assertFits(tag: String): TextLayoutResult {
        val node = composeRule.onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode()
        val layouts = mutableListOf<TextLayoutResult>()
        node.config[SemanticsActions.GetTextLayoutResult].action!!.invoke(layouts)
        return layouts.single().also { layout ->
            assertFalse("$tag height", layout.didOverflowHeight)
            assertFalse("$tag ellipsis", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
            for (line in 0 until layout.lineCount) {
                assertTrue("$tag width", layout.getLineRight(line) - layout.getLineLeft(line) <= layout.size.width + 0.5f)
            }
        }
    }

    private fun capturePanel(): Bitmap {
        val bounds = composeRule.onNodeWithTag("fire_sim_sticky_summary").fetchSemanticsNode().boundsInWindow
        return composeRule.runOnIdle {
            val view = composeRule.activity.findViewById<View>(android.R.id.content)
            val location = IntArray(2)
            view.getLocationInWindow(location)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            Bitmap.createBitmap(bitmap, (bounds.left - location[0]).toInt(), (bounds.top - location[1]).toInt(),
                bounds.width.toInt(), bounds.height.toInt())
        }
    }

    private fun assertOpaqueInsets(bitmap: Bitmap, color: Color) {
        val density = composeRule.activity.resources.displayMetrics.density
        val inset = (16 * density).toInt()
        val gap = (6 * density).toInt()
        for (y in 0 until bitmap.height) for (x in 0 until bitmap.width) {
            if (x < inset || x >= bitmap.width - inset || y < gap || y >= bitmap.height - gap) {
                assertEquals("opaque inset $x,$y", color.toArgb(), bitmap.getPixel(x, y))
            }
        }
        assertEquals("rounded corner", color.toArgb(), bitmap.getPixel(inset, gap))
    }

}
