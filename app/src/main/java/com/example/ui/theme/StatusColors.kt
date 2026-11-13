package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.model.AuditIndicatorStatus

/**
 * Единый источник маппинга статусов аудита и показателей в цвета темы Material 3:
 * - POSITIVE -> SuccessGreen (зеленый)
 * - NEUTRAL  -> WarningYellow (желтый)
 * - NEGATIVE -> AlertRed (красный)
 *
 * Базовые цвета используются для графиков, иконок, заливок и декоративных контейнеров.
 * Для ТЕКСТА применяйте [getTextColor]/[getComponentScoreTextColor]: они возвращают
 * более контрастные оттенки тех же цветов (WCAG AA >= 4.5:1 на surfaceVariant
 * и полупрозрачных статусных плашках) с поправкой на светлую/тёмную тему.
 */
object StatusColors {
    fun getColor(status: AuditIndicatorStatus): Color = when (status) {
        AuditIndicatorStatus.POSITIVE -> SuccessGreen
        AuditIndicatorStatus.NEUTRAL -> WarningYellow
        AuditIndicatorStatus.NEGATIVE -> AlertRed
    }

    fun getComponentScoreColor(score: Int): Color =
        getColor(FinancialMetricEvaluator.getComponentScoreStatus(score))

    /**
     * Текстовый цвет статуса для явно заданной темы.
     * Отделён от [getColor], чтобы не менять цвета графиков и контейнеров.
     */
    fun getTextColor(status: AuditIndicatorStatus, darkTheme: Boolean): Color = when (status) {
        AuditIndicatorStatus.POSITIVE -> if (darkTheme) SuccessGreenTextDark else SuccessGreenTextLight
        AuditIndicatorStatus.NEUTRAL -> if (darkTheme) WarningYellowTextDark else WarningYellowTextLight
        AuditIndicatorStatus.NEGATIVE -> if (darkTheme) AlertRedTextDark else AlertRedTextLight
    }

    /**
     * Текстовый цвет статуса, соответствующий фактически применённой теме.
     */
    @Composable
    fun getTextColor(status: AuditIndicatorStatus): Color =
        getTextColor(status, MaterialTheme.colorScheme.isDarkScheme())

    fun getComponentScoreTextColor(score: Int, darkTheme: Boolean): Color =
        getTextColor(FinancialMetricEvaluator.getComponentScoreStatus(score), darkTheme)

    /**
     * Текстовый цвет балла компонента. Пороговые значения берутся из
     * [FinancialMetricEvaluator], как и для [getComponentScoreColor].
     */
    @Composable
    fun getComponentScoreTextColor(score: Int): Color =
        getTextColor(FinancialMetricEvaluator.getComponentScoreStatus(score))

    /**
     * Контрастный текстовый вариант для базового акцента статуса.
     * Не-статусные акценты (primary, onSurfaceVariant) возвращаются без изменений.
     */
    fun getReadableTextColor(accentColor: Color, darkTheme: Boolean): Color = when (accentColor) {
        SuccessGreen -> getTextColor(AuditIndicatorStatus.POSITIVE, darkTheme)
        WarningYellow, AccentGold -> getTextColor(AuditIndicatorStatus.NEUTRAL, darkTheme)
        AlertRed -> getTextColor(AuditIndicatorStatus.NEGATIVE, darkTheme)
        else -> accentColor
    }

    @Composable
    fun getReadableTextColor(accentColor: Color): Color =
        getReadableTextColor(accentColor, MaterialTheme.colorScheme.isDarkScheme())
}

private fun ColorScheme.isDarkScheme(): Boolean =
    background.luminance() < 0.5f

fun AuditIndicatorStatus.toColor(): Color = StatusColors.getColor(this)
