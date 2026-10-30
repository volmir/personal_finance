package com.example.domain

import com.example.domain.model.AuditIndicatorStatus
import com.example.util.localization.AppLanguage

/**
 * Единый источник истины (Single Source of Truth) для расчета показателей
 * и определения их статуса (ПОЗИТИВНЫЙ / НЕЙТРАЛЬНЫЙ / НЕГАТИВНЫЙ).
 * Не содержит зависимостей от UI и Compose Color.
 */
object FinancialMetricEvaluator {

    // =========================================================================
    // 1. Денежный поток (Cash Flow)
    // =========================================================================

    fun getCashFlowStatus(cashFlow: Double): AuditIndicatorStatus = when {
        cashFlow > 0.0 -> AuditIndicatorStatus.POSITIVE
        cashFlow == 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    // =========================================================================
    // 2. Норма сбережений (Savings Rate)
    // < 0%: негативный (0 баллов)
    // 0..20%: нейтральный (5..7 баллов)
    // > 20%: позитивный (8..10 баллов)
    // =========================================================================

    const val SAVINGS_RATE_POSITIVE_THRESHOLD = 20.0
    const val SAVINGS_RATE_NEUTRAL_THRESHOLD = 0.0

    const val SAVINGS_RATE_TIER_LOW = 8.0
    const val SAVINGS_RATE_TIER_MEDIUM = 15.0
    const val SAVINGS_RATE_TIER_HIGH = 35.0
    const val SAVINGS_RATE_TIER_EXCELLENT = 50.0

    fun getSavingsRateStatus(savingsRate: Double): AuditIndicatorStatus = when {
        savingsRate > SAVINGS_RATE_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        savingsRate >= SAVINGS_RATE_NEUTRAL_THRESHOLD -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getSavingsRateScore(savingsRate: Double): Int = when {
        savingsRate < SAVINGS_RATE_NEUTRAL_THRESHOLD -> 0
        savingsRate < SAVINGS_RATE_TIER_LOW -> 5
        savingsRate < SAVINGS_RATE_TIER_MEDIUM -> 6
        savingsRate <= SAVINGS_RATE_POSITIVE_THRESHOLD -> 7
        savingsRate < SAVINGS_RATE_TIER_HIGH -> 8
        savingsRate < SAVINGS_RATE_TIER_EXCELLENT -> 9
        else -> 10
    }

    // =========================================================================
    // 3 & 4. Запас месячных расходов / Подушка безопасности (Safety Months)
    // >= 6.0 мес.: позитивный (баллы 9..10)
    // 3.0 .. < 6.0 мес.: нейтральный (балл 7)
    // < 3.0 мес.: негативный (баллы 0..4)
    // =========================================================================

    const val SAFETY_MONTHS_POSITIVE_THRESHOLD = 6.0
    const val SAFETY_MONTHS_NEUTRAL_THRESHOLD = 3.0

    const val SAFETY_MONTHS_TIER_CRITICAL = 0.5
    const val SAFETY_MONTHS_TIER_LOW = 1.0
    const val SAFETY_MONTHS_TIER_EXCELLENT = 12.0

    fun getSafetyMonthsStatus(safetyMonths: Double): AuditIndicatorStatus = when {
        safetyMonths >= SAFETY_MONTHS_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        safetyMonths >= SAFETY_MONTHS_NEUTRAL_THRESHOLD -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getSafetyMonthsScore(safetyMonths: Double): Int = when {
        safetyMonths < SAFETY_MONTHS_TIER_CRITICAL -> 0
        safetyMonths < SAFETY_MONTHS_TIER_LOW -> 2
        safetyMonths < SAFETY_MONTHS_NEUTRAL_THRESHOLD -> 4
        safetyMonths < SAFETY_MONTHS_POSITIVE_THRESHOLD -> 7
        safetyMonths < SAFETY_MONTHS_TIER_EXCELLENT -> 9
        else -> 10
    }

    // =========================================================================
    // 5. Коэффициент задолженности (Debt Ratio)
    // <= 0.30: позитивный (баллы 8..10)
    // 0.30 .. 0.60: нейтральный (баллы 6..7)
    // > 0.60: негативный (баллы 0..4)
    // =========================================================================

    const val DEBT_RATIO_POSITIVE_THRESHOLD = 0.30
    const val DEBT_RATIO_NEUTRAL_THRESHOLD = 0.60

    const val DEBT_RATIO_TIER_EXCELLENT = 0.05
    const val DEBT_RATIO_TIER_VERY_GOOD = 0.15
    const val DEBT_RATIO_TIER_MODERATE = 0.45
    const val DEBT_RATIO_TIER_HIGH = 0.80
    const val DEBT_RATIO_TIER_CRITICAL = 1.0

    fun getDebtRatioStatus(debtRatio: Double): AuditIndicatorStatus = when {
        debtRatio <= DEBT_RATIO_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        debtRatio <= DEBT_RATIO_NEUTRAL_THRESHOLD -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getDebtRatioScore(debtRatio: Double): Int = when {
        debtRatio <= DEBT_RATIO_TIER_EXCELLENT -> 10
        debtRatio <= DEBT_RATIO_TIER_VERY_GOOD -> 9
        debtRatio <= DEBT_RATIO_POSITIVE_THRESHOLD -> 8
        debtRatio <= DEBT_RATIO_TIER_MODERATE -> 7
        debtRatio <= DEBT_RATIO_NEUTRAL_THRESHOLD -> 6
        debtRatio <= DEBT_RATIO_TIER_HIGH -> 4
        debtRatio <= DEBT_RATIO_TIER_CRITICAL -> 2
        else -> 0
    }

    // =========================================================================
    // 6. Чистый капитал (Net Worth)
    // > 0: позитивный
    // == 0: нейтральный
    // < 0: негативный
    // =========================================================================

    fun getNetWorthStatus(netWorth: Double): AuditIndicatorStatus = when {
        netWorth > 0.0 -> AuditIndicatorStatus.POSITIVE
        netWorth == 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    // =========================================================================
    // 7. Бенчмарк Fidelity (Fidelity Ratio)
    // >= 1.0 (>= 100% от цели): позитивный
    // 0.7 .. < 1.0 (70%..99% от цели): нейтральный
    // < 0.7 (< 70% от цели): негативный
    // =========================================================================

    const val FIDELITY_POSITIVE_THRESHOLD = 1.0
    const val FIDELITY_NEUTRAL_THRESHOLD = 0.7

    fun calculateFidelityRatio(netWorth: Double, target: Double): Double {
        return if (target > 0.0) {
            netWorth / target
        } else {
            if (netWorth >= 0.0) 1.0 else 0.0
        }
    }

    fun calculateFidelityPercent(netWorth: Double, target: Double): Double {
        return calculateFidelityRatio(netWorth, target) * 100.0
    }

    fun getFidelityStatus(ratio: Double): AuditIndicatorStatus = when {
        ratio >= FIDELITY_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        ratio >= FIDELITY_NEUTRAL_THRESHOLD -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    fun getFidelityStatus(netWorth: Double, target: Double): AuditIndicatorStatus {
        if (target <= 0.0) {
            return if (netWorth >= 0.0) AuditIndicatorStatus.POSITIVE else AuditIndicatorStatus.NEGATIVE
        }
        val ratio = netWorth / target
        return getFidelityStatus(ratio)
    }

    // =========================================================================
    // 8. Коэффициент финансовой свободы (КФС / CFS)
    // Расчет: Чистые активы / Годовые расходы
    // Единые интервалы (нижняя граница включительно, верхняя нет):
    // < 0: долги (балл 0, негативный)
    // [0, 0.5): минимальная подушка (балл 2, негативный)
    // [0.5, 2): начало формирования капитала (балл 4, негативный)
    // [2, 5): долгосрочные инвестиции (балл 6, нейтральный)
    // [5, 15): доходы от капитала (балл 8, позитивный)
    // [15, 30): финансовая стабильность (балл 9, позитивный)
    // 30+: финансовая независимость (балл 10, позитивный)
    // =========================================================================

    const val CFS_THRESHOLD_BUFFER = 0.5
    const val CFS_NEUTRAL_THRESHOLD = 2.0
    const val CFS_POSITIVE_THRESHOLD = 5.0
    const val CFS_THRESHOLD_STABILITY = 15.0
    const val CFS_THRESHOLD_INDEPENDENCE = 30.0

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

    fun getCfsScore(cfs: Double): Int = when {
        cfs < 0.0 -> 0
        cfs < CFS_THRESHOLD_BUFFER -> 2
        cfs < CFS_NEUTRAL_THRESHOLD -> 4
        cfs < CFS_POSITIVE_THRESHOLD -> 6
        cfs < CFS_THRESHOLD_STABILITY -> 8
        cfs < CFS_THRESHOLD_INDEPENDENCE -> 9
        else -> 10
    }

    fun getCfsStageDescription(cfs: Double, lang: AppLanguage = AppLanguage.RU): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return when {
            cfs < 0.0 -> strings.cfsStageDebt
            cfs < CFS_THRESHOLD_BUFFER -> strings.cfsStageBuffer
            cfs < CFS_NEUTRAL_THRESHOLD -> strings.cfsStageCapital
            cfs < CFS_POSITIVE_THRESHOLD -> strings.cfsStageInvesting
            cfs < CFS_THRESHOLD_STABILITY -> strings.cfsStageReturns
            cfs < CFS_THRESHOLD_INDEPENDENCE -> strings.cfsStageStability
            else -> strings.cfsStageIndependence
        }
    }

    // =========================================================================
    // 9. Реальная доходность капитала (Real Return Percent)
    // > 0%: позитивный
    // == 0%: нейтральный
    // < 0%: негативный
    // =========================================================================

    fun getRealReturnStatus(realReturnPercent: Double): AuditIndicatorStatus = when {
        realReturnPercent > 0.0 -> AuditIndicatorStatus.POSITIVE
        realReturnPercent == 0.0 -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    // =========================================================================
    // 10. Общий суммирующий балл (Total Score)
    // >= 7.0: позитивный
    // 3.5 .. < 7.0: нейтральный
    // < 3.5: негативный
    // =========================================================================

    const val TOTAL_SCORE_GREEN_MIN = 7.0
    const val TOTAL_SCORE_YELLOW_MIN = 3.5

    fun getTotalScoreStatus(totalScore: Double): AuditIndicatorStatus = when {
        totalScore >= TOTAL_SCORE_GREEN_MIN -> AuditIndicatorStatus.POSITIVE
        totalScore >= TOTAL_SCORE_YELLOW_MIN -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }

    const val COMPONENT_SCORE_POSITIVE_THRESHOLD = 8
    const val COMPONENT_SCORE_NEUTRAL_THRESHOLD = 5

    fun getComponentScoreStatus(score: Int): AuditIndicatorStatus = when {
        score >= COMPONENT_SCORE_POSITIVE_THRESHOLD -> AuditIndicatorStatus.POSITIVE
        score >= COMPONENT_SCORE_NEUTRAL_THRESHOLD -> AuditIndicatorStatus.NEUTRAL
        else -> AuditIndicatorStatus.NEGATIVE
    }
}
