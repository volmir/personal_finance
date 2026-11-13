package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.util.localization.AppStrings

import com.example.domain.FinancialMetricEvaluator

/**
 * Единый источник истины для цветовой индикации и градации оценок.
 * Итоговый суммирующий показатель рассчитывается в одном месте и окрашивается единообразно:
 * - Зелёный (SuccessGreen) — при балле от 7,0 и выше;
 * - Жёлтый (WarningYellow) — при балле от 3,5 до 7;
 * - Красный (AlertRed) — при балле ниже 3,5.
 *
 * [getTotalScoreColor] задаёт цвет графики (дуга ScoreGauge), а
 * [getTotalScoreTextColor] — контрастный цвет текста для светлой/тёмной темы.
 */
object ScoreColorEvaluator {

    fun getTotalScoreColor(score: Double): Color =
        StatusColors.getColor(FinancialMetricEvaluator.getTotalScoreStatus(score))

    fun getTotalScoreTextColor(score: Double, darkTheme: Boolean): Color =
        StatusColors.getTextColor(FinancialMetricEvaluator.getTotalScoreStatus(score), darkTheme)

    @Composable
    fun getTotalScoreTextColor(score: Double): Color =
        StatusColors.getTextColor(FinancialMetricEvaluator.getTotalScoreStatus(score))

    /**
     * Возвращает полупрозрачный цвет фона для плашки итогового суммирующего показателя.
     */
    fun getTotalScoreContainerColor(score: Double, alpha: Float = 0.15f): Color =
        getTotalScoreColor(score).copy(alpha = alpha)

    /**
     * Возвращает описание статуса для итогового суммирующего показателя.
     */
    fun getTotalScoreStatusDescription(score: Double, strings: AppStrings): String = when {
        score >= FinancialMetricEvaluator.TOTAL_SCORE_EXCELLENT_MIN -> strings.scoresStatusExcellent
        score >= FinancialMetricEvaluator.TOTAL_SCORE_GREEN_MIN -> strings.scoresStatusGood
        score >= FinancialMetricEvaluator.TOTAL_SCORE_YELLOW_MIN -> strings.scoresStatusModerate
        else -> strings.scoresStatusAttention
    }
}
