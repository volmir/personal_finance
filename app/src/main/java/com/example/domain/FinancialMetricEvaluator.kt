package com.example.domain

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.util.localization.AppLanguage

/**
 * Единый источник истины (Single Source of Truth) для расчета показателей,
 * определения их статуса (ПОЗИТИВНЫЙ / НЕЙТРАЛЬНЫЙ / НЕГАТИВНЫЙ) и цвета (ЗЕЛЁНЫЙ / ЖЁЛТЫЙ / КРАСНЫЙ).
 */
object FinancialMetricEvaluator {

    // =========================================================================
    // Общие вспомогательные методы
    // =========================================================================

    fun getColorForStatus(status: AuditIndicatorStatus): Color = when (status) {
        AuditIndicatorStatus.POSITIVE -> SuccessGreen
        AuditIndicatorStatus.NEUTRAL -> WarningYellow
        AuditIndicatorStatus.NEGATIVE -> AlertRed
    }

    // =========================================================================
    // 1. Денежный поток (Cash Flow)
    // =========================================================================

    fun getCashFlowStatus(cashFlow: Double): AuditIndicatorStatus = when {
        cashFlow > 0.0 -> AuditIndicatorStatus.POSITIVE
        cashFlow == 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getCashFlowColor(cashFlow: Double): Color = getColorForStatus(getCashFlowStatus(cashFlow))

    // =========================================================================
    // 2. Норма сбережений (Savings Rate)
    // < 0%: негативный (красный, 0 баллов)
    // 0..20%: нейтральный (жёлтый, 5..7 баллов)
    // > 20%: позитивный (зелёный, 8..10 баллов)
    // =========================================================================

    const val SAVINGS_RATE_POSITIVE_THRESHOLD = 20.0

    fun getSavingsRateStatus(savingsRate: Double): AuditIndicatorStatus = when {
        savingsRate > SAVINGS_RATE_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        savingsRate >= 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getSavingsRateColor(savingsRate: Double): Color = getColorForStatus(getSavingsRateStatus(savingsRate))

    fun getSavingsRateScore(savingsRate: Double): Int = when {
        savingsRate < 0 -> 0
        savingsRate < 8.0 -> 5
        savingsRate < 15.0 -> 6
        savingsRate <= 20.0 -> 7
        savingsRate < 35.0 -> 8
        savingsRate < 50.0 -> 9
        else -> 10
    }

    // =========================================================================
    // 3 & 4. Запас месячных расходов / Подушка безопасности (Safety Months)
    // >= 6.0 мес.: позитивный (зелёный)
    // 3.0 .. < 6.0 мес.: нейтральный (жёлтый)
    // < 3.0 мес.: негативный (красный)
    // =========================================================================

    fun getSafetyMonthsStatus(safetyMonths: Double): AuditIndicatorStatus = when {
        safetyMonths >= 6.0 -> AuditIndicatorStatus.POSITIVE
        safetyMonths >= 3.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getSafetyMonthsColor(safetyMonths: Double): Color = getColorForStatus(getSafetyMonthsStatus(safetyMonths))

    fun getSafetyMonthsScore(safetyMonths: Double): Int = when {
        safetyMonths < 0.5 -> 0
        safetyMonths < 1.0 -> 2
        safetyMonths < 3.0 -> 4
        safetyMonths < 6.0 -> 7
        safetyMonths < 12.0 -> 9
        else -> 10
    }

    // =========================================================================
    // 5. Коэффициент задолженности (Debt Ratio)
    // <= 0.30: позитивный (зелёный)
    // 0.30 .. 0.60: нейтральный (жёлтый)
    // > 0.60: негативный (красный)
    // =========================================================================

    fun getDebtRatioStatus(debtRatio: Double): AuditIndicatorStatus = when {
        debtRatio <= 0.30 -> AuditIndicatorStatus.POSITIVE
        debtRatio <= 0.60 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getDebtRatioColor(debtRatio: Double): Color = getColorForStatus(getDebtRatioStatus(debtRatio))

    fun getDebtRatioScore(debtRatio: Double): Int = when {
        debtRatio <= 0.05 -> 10
        debtRatio <= 0.15 -> 9
        debtRatio <= 0.30 -> 8
        debtRatio <= 0.45 -> 7
        debtRatio <= 0.60 -> 6
        debtRatio <= 0.80 -> 4
        debtRatio <= 1.0 -> 2
        else -> 0
    }

    // =========================================================================
    // 6. Чистый капитал (Net Worth)
    // > 0: позитивный (зелёный)
    // == 0: нейтральный (жёлтый)
    // < 0: негативный (красный)
    // =========================================================================

    fun getNetWorthStatus(netWorth: Double): AuditIndicatorStatus = when {
        netWorth > 0.0 -> AuditIndicatorStatus.POSITIVE
        netWorth == 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getNetWorthColor(netWorth: Double): Color = getColorForStatus(getNetWorthStatus(netWorth))

    // =========================================================================
    // 7. Бенчмарк Fidelity (Fidelity Ratio)
    // >= 1.0: позитивный (зелёный)
    // 0.7 .. < 1.0: нейтральный (жёлтый)
    // < 0.7: негативный (красный)
    // =========================================================================

    fun getFidelityStatus(netWorth: Double, target: Double): AuditIndicatorStatus {
        if (target <= 0.0) return AuditIndicatorStatus.NEUTRAL
        val ratio = netWorth / target
        return when {
            ratio >= 1.0 -> AuditIndicatorStatus.POSITIVE
            ratio >= 0.7 -> AuditIndicatorStatus.NEUTRAL
            else -> AuditIndicatorStatus.NEGATIVE
        }
    }

    fun getFidelityColor(netWorth: Double, target: Double): Color =
        getColorForStatus(getFidelityStatus(netWorth, target))

    // =========================================================================
    // 8. Коэффициент финансовой свободы (КФС / CFS)
    // Расчет: Чистые активы / Годовые расходы
    // Пороги:
    // >= 5.0: позитивный (зелёный, баллы 8..10)
    // 2.0 .. < 5.0: нейтральный (жёлтый, балл 6)
    // < 2.0: негативный (красный, баллы 0..4)
    // =========================================================================

    const val CFS_POSITIVE_THRESHOLD = 5.0
    const val CFS_NEUTRAL_THRESHOLD = 2.0

    fun calculateCfs(netWorth: Double, annualExpenses: Double): Double {
        return if (annualExpenses > 0.0) {
            netWorth / annualExpenses
        } else 0.0
    }

    fun getCfsStatus(cfs: Double): AuditIndicatorStatus = when {
        cfs >= CFS_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        cfs >= CFS_NEUTRAL_THRESHOLD -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getCfsColor(cfs: Double): Color = getColorForStatus(getCfsStatus(cfs))

    fun getCfsScore(cfs: Double): Int = when {
        cfs < 0 -> 0
        cfs <= 0.5 -> 2
        cfs <= 2.0 -> 4
        cfs <= 5.0 -> 6
        cfs <= 15.0 -> 8
        cfs <= 30.0 -> 9
        else -> 10
    }

    fun getCfsStageDescription(cfs: Double, lang: AppLanguage = AppLanguage.RU): String {
        return if (lang == AppLanguage.EN) {
            when {
                cfs < 0 -> "CFS < 0 — You are in debt"
                cfs <= 0.5 -> "CFS 0..0.5 — You have a minimal emergency buffer"
                cfs <= 2.0 -> "CFS 0.5..2 — You have started building financial capital"
                cfs <= 5.0 -> "CFS 2..5 — You can begin engaging in long-term investing"
                cfs <= 15.0 -> "CFS 5..15 — Investment returns provide significant income. Inflation affects capital more"
                cfs <= 30.0 -> "CFS 15..30 — Solid financial stability. How do you spend your irreplaceable time?"
                else -> "CFS > 30 — You have achieved financial independence"
            }
        } else {
            when {
                cfs < 0 -> "КФС < 0 — Вы в долгах"
                cfs <= 0.5 -> "КФС 0..0,5 — У вас есть минимальная «подушка безопасности»"
                cfs <= 2.0 -> "КФС 0,5..2 — Вы начали формировать свой финансовый капитал"
                cfs <= 5.0 -> "КФС 2..5 — Вы можете начать заниматься долгосрочными инвестициями"
                cfs <= 15.0 -> "КФС 5..15 — Доходы от капитала могут приносить вам существенную прибавку. Инфляция все более значительно влияет на капитал"
                cfs <= 30.0 -> "КФС 15..30 — Крепкая финансовая стабильность. На что вы тратите свой невосполнимый капитал времени?"
                else -> "КФС > 30 — Вы достигли финансовой независимости"
            }
        }
    }

    // =========================================================================
    // 9. Реальная доходность капитала (Real Return Percent)
    // > 0%: позитивный (зелёный)
    // == 0%: нейтральный (жёлтый)
    // < 0%: негативный (красный)
    // =========================================================================

    fun getRealReturnStatus(realReturnPercent: Double): AuditIndicatorStatus = when {
        realReturnPercent > 0.0 -> AuditIndicatorStatus.POSITIVE
        realReturnPercent == 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getRealReturnColor(realReturnPercent: Double): Color =
        getColorForStatus(getRealReturnStatus(realReturnPercent))

    // =========================================================================
    // 10. Общий суммирующий балл (Total Score)
    // >= 7.0: позитивный (зелёный)
    // 3.5 .. < 7.0: нейтральный (жёлтый)
    // < 3.5: негативный (красный)
    // =========================================================================

    const val TOTAL_SCORE_GREEN_MIN = 7.0
    const val TOTAL_SCORE_YELLOW_MIN = 3.5

    fun getTotalScoreStatus(totalScore: Double): AuditIndicatorStatus = when {
        totalScore >= TOTAL_SCORE_GREEN_MIN -> AuditIndicatorStatus.POSITIVE
        totalScore >= TOTAL_SCORE_YELLOW_MIN -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getTotalScoreColor(totalScore: Double): Color =
        getColorForStatus(getTotalScoreStatus(totalScore))

    fun getComponentScoreColor(score: Int): Color = when {
        score >= 8 -> SuccessGreen
        score >= 5 -> WarningYellow
        else -> AlertRed
    }
}
