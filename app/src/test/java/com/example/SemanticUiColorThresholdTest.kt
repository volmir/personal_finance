package com.example

import com.example.ui.theme.ScoreColorEvaluator
import com.example.ui.theme.StatusColors
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WarningYellowTextDark
import com.example.ui.theme.WarningYellowTextLight
import com.example.domain.model.AuditIndicatorStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class SemanticUiColorThresholdTest {
    @Test
    fun `score 6_7 is neutral yellow not green`() {
        assertEquals(WarningYellow, ScoreColorEvaluator.getTotalScoreColor(6.7))
        assertEquals(WarningYellowTextLight, ScoreColorEvaluator.getTotalScoreTextColor(6.7, darkTheme = false))
        assertEquals(WarningYellowTextDark, ScoreColorEvaluator.getTotalScoreTextColor(6.7, darkTheme = true))
    }

    @Test
    fun `status colors mapping fixed`() {
        assertEquals(com.example.ui.theme.SuccessGreen, StatusColors.getColor(AuditIndicatorStatus.POSITIVE))
        assertEquals(WarningYellow, StatusColors.getColor(AuditIndicatorStatus.NEUTRAL))
        assertEquals(com.example.ui.theme.AlertRed, StatusColors.getColor(AuditIndicatorStatus.NEGATIVE))
    }
}
