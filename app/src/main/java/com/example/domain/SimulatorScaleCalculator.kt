package com.example.domain

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

data class SimulatorScaleConfig(
    val minValue: Float,
    val maxValue: Float,
    val stepSize: Double,
    val steps: Int
)

object SimulatorScaleCalculator {

    /**
     * Находит удобный, человекочитаемый шаг (1, 2, 2.5, 5, 10, 20, 25, 50, 100 и т.д.)
     */
    fun calculateNiceStep(targetStep: Double): Double {
        if (targetStep <= 0.0 || targetStep.isNaN() || targetStep.isInfinite()) return 1.0
        val exponent = floor(log10(targetStep))
        val magnitude = 10.0.pow(exponent)
        val normalized = targetStep / magnitude
        val niceNormalized = when {
            normalized <= 1.0 -> 1.0
            normalized <= 2.0 -> 2.0
            normalized <= 2.5 -> 2.5
            normalized <= 5.0 -> 5.0
            else -> 10.0
        }
        return niceNormalized * magnitude
    }

    /**
     * Шкала 1: Ежемесячные сбережения (extraMonthlySavings).
     * Динамически рассчитывает диапазон и шаг исходя из текущих значений показателей:
     * текущих сбережений (денежного потока), доходов и расходов.
     */
    fun calculateSavingsConfig(
        currentSavings: Double,
        monthlyIncome: Double,
        monthlyExpenses: Double
    ): SimulatorScaleConfig {
        val refSavings = when {
            currentSavings > 0.0 -> currentSavings
            monthlyIncome > 0.0 -> monthlyIncome * 0.25
            monthlyExpenses > 0.0 -> monthlyExpenses * 0.25
            else -> 10_000.0
        }

        // Целимся в ~25 интервалов по всей шкале
        val targetStep = (refSavings * 3.0) / 25.0
        val stepSize = calculateNiceStep(targetStep)

        // Нижняя граница: позволяет снизить сбережения до нуля (или небольшого дефицита)
        val minDelta = if (currentSavings > 0.0) {
            -ceil(currentSavings / stepSize) * stepSize
        } else {
            -ceil(refSavings / stepSize) * stepSize
        }

        // Верхняя граница: позволяет увеличить сбережения существенно выше базовых
        val maxTarget = maxOf(refSavings * 2.0, monthlyIncome * 0.5, stepSize * 10.0)
        val maxDelta = ceil(maxTarget / stepSize) * stepSize

        val span = maxDelta - minDelta
        val intervals = (span / stepSize).roundToInt().coerceAtLeast(1)
        val steps = (intervals - 1).coerceAtLeast(0)

        return SimulatorScaleConfig(
            minValue = minDelta.toFloat(),
            maxValue = maxDelta.toFloat(),
            stepSize = stepSize,
            steps = steps
        )
    }

    /**
     * Шкала 2: Ежемесячные расходы (expenseReduction).
     * Динамически рассчитывает диапазон и шаг исходя из текущих ежемесячных расходов:
     * положительное смещение = сокращение расходов (до 100%, т.е. до 0 расходов),
     * отрицательное смещение = рост расходов (до 100%, т.е. удвоение расходов).
     */
    fun calculateExpensesConfig(
        monthlyExpenses: Double,
        monthlyIncome: Double
    ): SimulatorScaleConfig {
        val refExpenses = when {
            monthlyExpenses > 0.0 -> monthlyExpenses
            monthlyIncome > 0.0 -> monthlyIncome * 0.5
            else -> 10_000.0
        }

        // Размах шкалы: от -refExpenses (рост) до +refExpenses (сокращение)
        val targetStep = (2.0 * refExpenses) / 25.0
        val stepSize = calculateNiceStep(targetStep)

        val maxReduction = ceil(refExpenses / stepSize) * stepSize
        val minReduction = -ceil(refExpenses / stepSize) * stepSize

        val span = maxReduction - minReduction
        val intervals = (span / stepSize).roundToInt().coerceAtLeast(1)
        val steps = (intervals - 1).coerceAtLeast(0)

        return SimulatorScaleConfig(
            minValue = minReduction.toFloat(),
            maxValue = maxReduction.toFloat(),
            stepSize = stepSize,
            steps = steps
        )
    }

    /**
     * Шкала 3: Реальная доходность капитала (realReturnAdjustment).
     * Динамически рассчитывает диапазон и шаг исходя из текущей реальной доходности капитала.
     */
    fun calculateReturnConfig(
        realReturnPercent: Double
    ): SimulatorScaleConfig {
        val absReturn = abs(realReturnPercent)

        // Шаг: 0.25% для низкой доходности (до 3%), 0.5% для умеренной (до 7%), 1.0% для высокой (>7%)
        val stepSize = when {
            absReturn <= 3.0 -> 0.25
            absReturn <= 7.0 -> 0.5
            else -> 1.0
        }

        val targetMin = if (realReturnPercent > 0.0) {
            -(realReturnPercent + maxOf(absReturn * 0.5, 1.5))
        } else {
            -(maxOf(absReturn * 1.5, 3.0))
        }
        val minAdjustment = -(ceil(abs(targetMin) / stepSize) * stepSize)

        val targetMax = maxOf(absReturn * 1.5, 5.0)
        val maxAdjustment = ceil(targetMax / stepSize) * stepSize

        val span = maxAdjustment - minAdjustment
        val intervals = (span / stepSize).roundToInt().coerceAtLeast(1)
        val steps = (intervals - 1).coerceAtLeast(0)

        return SimulatorScaleConfig(
            minValue = minAdjustment.toFloat(),
            maxValue = maxAdjustment.toFloat(),
            stepSize = stepSize,
            steps = steps
        )
    }
}
