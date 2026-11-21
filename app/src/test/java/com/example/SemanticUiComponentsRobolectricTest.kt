package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.components.CompactResultCard
import com.example.ui.components.MetricSummaryRow
import com.example.ui.components.StatusChip
import com.example.ui.theme.EmeraldDarkPrimary
import com.example.ui.theme.EmeraldLightPrimary
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface
import com.example.ui.theme.SlateLightBackground
import com.example.ui.theme.SlateLightCard
import com.example.ui.theme.SlateLightSurface
import com.example.ui.theme.StatusColors
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SemanticUiComponentsRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun textColor(text: String): Color {
        val results = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        val node = composeRule.onNodeWithText(text).fetchSemanticsNode()
        val action = node.config[androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult].action!!
        action(results)
        return results.first().layoutInput.style.color
    }

    @Test
    fun `status chip text color matches status colors in both themes`() {
        var dark by mutableStateOf(false)
        var status by mutableStateOf(AuditIndicatorStatus.POSITIVE)
        composeRule.setContent {
            val scheme = if (dark) darkColorScheme(primary = EmeraldDarkPrimary, background = SlateDarkSurface, surface = SlateDarkSurface, surfaceVariant = SlateDarkCard)
            else lightColorScheme(primary = EmeraldLightPrimary, background = SlateLightBackground, surface = SlateLightSurface, surfaceVariant = SlateLightCard)
            MaterialTheme(colorScheme = scheme) {
                StatusChip(status = status, label = status.name, testTag = "chip")
            }
        }
        for (isDark in listOf(false, true)) {
            for (s in AuditIndicatorStatus.values()) {
                composeRule.runOnIdle { dark = isDark; status = s }
                composeRule.waitForIdle()
                assertEquals(StatusColors.getTextColor(s, isDark), textColor(s.name))
            }
        }
    }

    @Test
    fun `metric summary row value color matches status in both themes`() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            val scheme = if (dark) darkColorScheme(primary = EmeraldDarkPrimary, background = SlateDarkSurface, surface = SlateDarkSurface, surfaceVariant = SlateDarkCard)
            else lightColorScheme(primary = EmeraldLightPrimary, background = SlateLightBackground, surface = SlateLightSurface, surfaceVariant = SlateLightCard)
            MaterialTheme(colorScheme = scheme) {
                MetricSummaryRow(title = "T", value = "10", status = AuditIndicatorStatus.NEUTRAL, testTag = "row")
            }
        }
        for (isDark in listOf(false, true)) {
            composeRule.runOnIdle { dark = isDark }
            composeRule.waitForIdle()
            assertEquals(StatusColors.getTextColor(AuditIndicatorStatus.NEUTRAL, isDark), textColor("10"))
        }
    }

    @Test
    fun `compact result card value color matches status in both themes`() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            val scheme = if (dark) darkColorScheme(primary = EmeraldDarkPrimary, background = SlateDarkSurface, surface = SlateDarkSurface, surfaceVariant = SlateDarkCard)
            else lightColorScheme(primary = EmeraldLightPrimary, background = SlateLightBackground, surface = SlateLightSurface, surfaceVariant = SlateLightCard)
            MaterialTheme(colorScheme = scheme) {
                CompactResultCard(title = "Score", value = "6,7", status = AuditIndicatorStatus.NEUTRAL, testTag = "card")
            }
        }
        for (isDark in listOf(false, true)) {
            composeRule.runOnIdle { dark = isDark }
            composeRule.waitForIdle()
            assertEquals(StatusColors.getTextColor(AuditIndicatorStatus.NEUTRAL, isDark), textColor("6,7"))
        }
    }
}
