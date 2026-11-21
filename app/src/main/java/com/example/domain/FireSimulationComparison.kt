package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FireStatus

/**
 * Чистая (без Compose/Android) модель сравнения базового и сценарного FIRE-результата.
 * Содержит только вычисления/правила; UI отвечает за цвета и форматирование.
 */
data class FireSimulationComparison(
    val isBaseUnreachable: Boolean,
    val isSimUnreachable: Boolean,
    val yearsDiff: Double,
    val isPositive: Boolean,
    val isNegative: Boolean
)

object FireSimulationComparisonCalculator {
    fun isUnreachable(status: FireStatus): Boolean =
        status == FireStatus.UNREACHABLE || status == FireStatus.HORIZON_EXHAUSTED

    fun compare(base: CalculatedMetrics, simulated: CalculatedMetrics): FireSimulationComparison {
        val isBaseUnreachable = isUnreachable(base.fireStatus)
        val isSimUnreachable = isUnreachable(simulated.fireStatus)
        val yearsDiff = base.yearsToRetirement - simulated.yearsToRetirement
        val isPositive = !isSimUnreachable && (isBaseUnreachable || yearsDiff > 0)
        val isNegative = !isBaseUnreachable && (isSimUnreachable || yearsDiff < 0)
        return FireSimulationComparison(
            isBaseUnreachable = isBaseUnreachable,
            isSimUnreachable = isSimUnreachable,
            yearsDiff = yearsDiff,
            isPositive = isPositive,
            isNegative = isNegative
        )
    }

    /**
     * Округление реальной доходности для определения статуса (как было в UI, BigDecimal HALF_EVEN).
     */
    fun roundRealReturnForStatus(value: Double): Double =
        if (value.isFinite()) java.math.BigDecimal.valueOf(value)
            .setScale(2, java.math.RoundingMode.HALF_EVEN)
            .toDouble() else 0.0
}
