package com.example

import androidx.compose.ui.graphics.Color
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedTextDark
import com.example.ui.theme.AlertRedTextLight
import com.example.ui.theme.ScoreColorEvaluator
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateLightCard
import com.example.ui.theme.StatusColors
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenTextDark
import com.example.ui.theme.SuccessGreenTextLight
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WarningYellowTextDark
import com.example.ui.theme.WarningYellowTextLight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Регрессия UX-C01: текстовые статусные цвета должны читаться на карточках
 * (surfaceVariant) и полупрозрачных статусных плашках в обеих темах.
 */
class StatusTextContrastTest {

    private data class StatusPalette(
        val status: AuditIndicatorStatus,
        val accent: Color,
        val lightText: Color,
        val darkText: Color
    )

    private val palettes = listOf(
        StatusPalette(
            AuditIndicatorStatus.POSITIVE,
            SuccessGreen,
            SuccessGreenTextLight,
            SuccessGreenTextDark
        ),
        StatusPalette(
            AuditIndicatorStatus.NEUTRAL,
            WarningYellow,
            WarningYellowTextLight,
            WarningYellowTextDark
        ),
        StatusPalette(
            AuditIndicatorStatus.NEGATIVE,
            AlertRed,
            AlertRedTextLight,
            AlertRedTextDark
        )
    )

    // Заливки, используемые в MetricCard и AuditScoreScreen, с запасом до 20%.
    private val statusFillAlphas = listOf(0.08f, 0.10f, 0.12f, 0.125f, 0.15f, 0.20f)

    @Test
    fun `text status colors meet wcag aa on plain card surfaces`() {
        for (palette in palettes) {
            assertTrue(
                "${palette.status} light text on light card",
                contrast(palette.lightText, SlateLightCard) >= WCAG_AA_NORMAL_TEXT
            )
            assertTrue(
                "${palette.status} dark text on dark card",
                contrast(palette.darkText, SlateDarkCard) >= WCAG_AA_NORMAL_TEXT
            )
        }
    }

    @Test
    fun `text status colors meet wcag aa on translucent status fills`() {
        for (palette in palettes) {
            for (alpha in statusFillAlphas) {
                val lightFill = composite(palette.accent, alpha, SlateLightCard)
                val darkFill = composite(palette.accent, alpha, SlateDarkCard)
                assertTrue(
                    "${palette.status} light text on fill alpha=$alpha: " +
                        "${contrast(palette.lightText, lightFill)}",
                    contrast(palette.lightText, lightFill) >= WCAG_AA_NORMAL_TEXT
                )
                assertTrue(
                    "${palette.status} dark text on fill alpha=$alpha: " +
                        "${contrast(palette.darkText, darkFill)}",
                    contrast(palette.darkText, darkFill) >= WCAG_AA_NORMAL_TEXT
                )
            }
        }
    }

    @Test
    fun `score text colors follow evaluator thresholds without changing values`() {
        assertEquals(SuccessGreenTextLight, ScoreColorEvaluator.getTotalScoreTextColor(10.0, darkTheme = false))
        assertEquals(SuccessGreenTextLight, ScoreColorEvaluator.getTotalScoreTextColor(7.0, darkTheme = false))
        assertEquals(WarningYellowTextLight, ScoreColorEvaluator.getTotalScoreTextColor(6.99, darkTheme = false))
        assertEquals(WarningYellowTextLight, ScoreColorEvaluator.getTotalScoreTextColor(3.5, darkTheme = false))
        assertEquals(AlertRedTextLight, ScoreColorEvaluator.getTotalScoreTextColor(3.49, darkTheme = false))
        assertEquals(AlertRedTextDark, ScoreColorEvaluator.getTotalScoreTextColor(3.49, darkTheme = true))

        assertEquals(SuccessGreenTextDark, StatusColors.getComponentScoreTextColor(10, darkTheme = true))
        assertEquals(SuccessGreenTextLight, StatusColors.getComponentScoreTextColor(8, darkTheme = false))
        assertEquals(WarningYellowTextLight, StatusColors.getComponentScoreTextColor(7, darkTheme = false))
        assertEquals(WarningYellowTextLight, StatusColors.getComponentScoreTextColor(5, darkTheme = false))
        assertEquals(AlertRedTextLight, StatusColors.getComponentScoreTextColor(4, darkTheme = false))
        assertEquals(AlertRedTextDark, StatusColors.getComponentScoreTextColor(4, darkTheme = true))
    }

    @Test
    fun `readable text color maps only status accents`() {
        assertEquals(SuccessGreenTextLight, StatusColors.getReadableTextColor(SuccessGreen, darkTheme = false))
        assertEquals(SuccessGreenTextDark, StatusColors.getReadableTextColor(SuccessGreen, darkTheme = true))
        assertEquals(WarningYellowTextLight, StatusColors.getReadableTextColor(WarningYellow, darkTheme = false))
        assertEquals(WarningYellowTextDark, StatusColors.getReadableTextColor(WarningYellow, darkTheme = true))
        assertEquals(WarningYellowTextLight, StatusColors.getReadableTextColor(AccentGold, darkTheme = false))
        assertEquals(WarningYellowTextDark, StatusColors.getReadableTextColor(AccentGold, darkTheme = true))
        assertEquals(AlertRedTextLight, StatusColors.getReadableTextColor(AlertRed, darkTheme = false))
        assertEquals(AlertRedTextDark, StatusColors.getReadableTextColor(AlertRed, darkTheme = true))

        val nonStatusAccent = Color(0xFF006C4A)
        assertEquals(nonStatusAccent, StatusColors.getReadableTextColor(nonStatusAccent, darkTheme = false))
        assertEquals(nonStatusAccent, StatusColors.getReadableTextColor(nonStatusAccent, darkTheme = true))
    }

    @Test
    fun `accent palette stays unchanged for charts icons and containers`() {
        assertEquals(Color(0xFF10B981), SuccessGreen)
        assertEquals(Color(0xFFF59E0B), WarningYellow)
        assertEquals(Color(0xFFEF4444), AlertRed)

        assertEquals(SuccessGreen, StatusColors.getColor(AuditIndicatorStatus.POSITIVE))
        assertEquals(WarningYellow, StatusColors.getColor(AuditIndicatorStatus.NEUTRAL))
        assertEquals(AlertRed, StatusColors.getColor(AuditIndicatorStatus.NEGATIVE))
        assertEquals(SuccessGreen, ScoreColorEvaluator.getTotalScoreColor(7.0))
        assertEquals(WarningYellow, ScoreColorEvaluator.getTotalScoreColor(3.5))
        assertEquals(AlertRed, ScoreColorEvaluator.getTotalScoreColor(3.49))
    }

    private fun relativeLuminance(color: Color): Double =
        0.2126 * linearChannel(color.red) +
            0.7152 * linearChannel(color.green) +
            0.0722 * linearChannel(color.blue)

    private fun linearChannel(channel: Float): Double {
        val value = channel.toDouble()
        return if (value <= 0.04045) value / 12.92
        else ((value + 0.055) / 1.055).pow(2.4)
    }

    private fun contrast(first: Color, second: Color): Double {
        val firstLuminance = relativeLuminance(first)
        val secondLuminance = relativeLuminance(second)
        return (max(firstLuminance, secondLuminance) + 0.05) /
            (min(firstLuminance, secondLuminance) + 0.05)
    }

    private fun composite(foreground: Color, alpha: Float, background: Color): Color = Color(
        red = foreground.red * alpha + background.red * (1f - alpha),
        green = foreground.green * alpha + background.green * (1f - alpha),
        blue = foreground.blue * alpha + background.blue * (1f - alpha)
    )

    private companion object {
        const val WCAG_AA_NORMAL_TEXT = 4.5
    }
}
