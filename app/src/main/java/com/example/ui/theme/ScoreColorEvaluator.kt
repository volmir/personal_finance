package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.util.localization.AppStrings

/**
 * Единый источник истины для цветовой индикации и градации оценок.
 * Итоговый суммирующий показатель рассчитывается в одном месте и окрашивается единообразно:
 * - Зелёный (SuccessGreen) — при балле от 7,0 и выше;
 * - Жёлтый (WarningYellow) — при балле от 3,5 до 7;
 * - Красный (AlertRed) — при балле ниже 3,5.
 */
object ScoreColorEvaluator {
    const val TOTAL_SCORE_GREEN_MIN = 7.0
    const val TOTAL_SCORE_YELLOW_MIN = 3.5

    fun getTotalScoreColor(score: Double): Color =
        StatusColors.getColor(com.example.domain.FinancialMetricEvaluator.getTotalScoreStatus(score))

    /**
     * Возвращает полупрозрачный цвет фона для плашки итогового суммирующего показателя.
     */
    fun getTotalScoreContainerColor(score: Double, alpha: Float = 0.15f): Color =
        getTotalScoreColor(score).copy(alpha = alpha)

    /**
     * Возвращает описание статуса для итогового суммирующего показателя.
     */
    fun getTotalScoreStatusDescription(score: Double, strings: AppStrings): String = when {
        score >= 8.5 -> strings.scoresStatusExcellent
        score >= TOTAL_SCORE_GREEN_MIN -> strings.scoresStatusGood
        score >= TOTAL_SCORE_YELLOW_MIN -> strings.scoresStatusModerate
        else -> strings.scoresStatusAttention
    }
}
