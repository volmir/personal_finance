package com.example

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.FireSimulationState
import com.example.ui.screens.FireRetirementScreen
import com.example.ui.screens.FireSubtab
import com.example.ui.theme.MyApplicationTheme
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(qualifiers = "ru-w360dp-h800dp")
class FireScrollStateRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `FIRE and Simulation have independent saveable list positions`() {
        val selectedSubtab = mutableStateOf(FireSubtab.BASE)
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val restoration = StateRestorationTester(composeRule)
        restoration.setContent {
            MyApplicationTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.EN,
                    LocalAppStrings provides getAppStrings(AppLanguage.EN)
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = metrics,
                        simulatedMetrics = metrics,
                        simulationState = FireSimulationState(),
                        trajectory = emptyList(),
                        selectedSubtab = selectedSubtab.value,
                        onSelectSubtab = { selectedSubtab.value = it },
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {}
                    )
                }
            }
        }

        // At the standard 800dp height the parameters already fit without scrolling.
        scrollTo("fire_trajectory_card")
        composeRule.onNodeWithTag("fire_parameters_card").assertIsDisplayed()
        val basePosition = position()
        assertTrue(basePosition > 0f)
        // Управляем тем же selectedSubtab, который FinanceApp передаёт экрану;
        // прокрутка к селектору перед переключением исказила бы проверяемую позицию.
        composeRule.runOnIdle { selectedSubtab.value = FireSubtab.SIMULATION }
        assertEquals(0f, position(), 0f)
        scrollTo("switch_job_loss")
        val simulationPosition = position()
        assertTrue(simulationPosition > 0f)

        composeRule.runOnIdle { selectedSubtab.value = FireSubtab.BASE }
        assertEquals(basePosition, position(), 0f)
        composeRule.onNodeWithTag("fire_parameters_card").assertIsDisplayed()
        restoration.emulateSavedInstanceStateRestore()
        assertEquals(basePosition, position(), 0f)
        composeRule.runOnIdle { selectedSubtab.value = FireSubtab.SIMULATION }
        assertEquals(simulationPosition, position(), 0f)
        composeRule.onNodeWithTag("switch_job_loss").assertIsDisplayed()
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
