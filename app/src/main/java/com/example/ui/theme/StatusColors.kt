package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.domain.model.AuditIndicatorStatus

/**
 * Единый источник маппинга статусов аудита и показателей в цвета темы Material 3:
 * - POSITIVE -> SuccessGreen (зеленый)
 * - NEUTRAL  -> WarningYellow (желтый)
 * - NEGATIVE -> AlertRed (красный)
 */
object StatusColors {
    fun getColor(status: AuditIndicatorStatus): Color = when (status) {
        AuditIndicatorStatus.POSITIVE -> SuccessGreen
        AuditIndicatorStatus.NEUTRAL -> WarningYellow
        AuditIndicatorStatus.NEGATIVE -> AlertRed
    }

    fun getComponentScoreColor(score: Int): Color = when {
        score >= 8 -> SuccessGreen
        score >= 5 -> WarningYellow
        else -> AlertRed
    }
}

fun AuditIndicatorStatus.toColor(): Color = StatusColors.getColor(this)
