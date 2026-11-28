package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.model.FinancialAssessmentCapReason
import com.example.domain.model.FinancialAssessmentComponents
import com.example.domain.model.FinancialAssessmentGroup
import com.example.domain.model.FinancialAssessmentGroupScore
import com.example.domain.model.FinancialAssessmentId
import com.example.domain.model.FinancialAssessmentResult
import com.example.domain.model.FinancialAssessmentUnavailableReason
import com.example.util.Formatters
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Уже вычисленные значения для компонентных оценок. Расчётные денежные величины
 * нормализованы к точности валюты вызывающим кодом (FinanceCalculator):
 * калькулятор не пересчитывает raw-метрики. Исходные [monthlyIncome],
 * [monthlyExpenses] и [liabilities] нужны агрегации и cap; сравнение их с нулём
 * выполняется по [currencyFractionDigits], а не сырым равенством Double.
 */
data class FinancialAssessmentInput(
    val cashFlow: Double,
    val savingsRate: Double,
    val safetyMonths: Double,
    val debtRatio: Double,
    val netWorth: Double,
    val totalAssets: Double,
    val fidelityCapital: Double,
    val fidelityTarget: Double,
    val isAgeValid: Boolean,
    val cfs: Double,
    val realReturnPercent: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val liabilities: Double,
    /** Точность валюты нужна только для сравнения сумм с нулём при агрегации. */
    val currencyFractionDigits: Int = 2
) {
    companion object {
        fun from(source: FinancialInputData, metrics: CalculatedMetrics): FinancialAssessmentInput =
            FinancialAssessmentInput(
                cashFlow = metrics.cashFlow,
                savingsRate = metrics.savingsRate,
                safetyMonths = metrics.safetyMonths,
                debtRatio = metrics.debtRatio,
                netWorth = metrics.netWorth,
                totalAssets = metrics.totalAssets,
                fidelityCapital = metrics.fidelityCapital,
                fidelityTarget = metrics.fidelityTarget,
                isAgeValid = metrics.isAgeValid,
                cfs = metrics.financialFreedomRatio,
                realReturnPercent = metrics.realReturnPercent,
                monthlyIncome = source.monthlyIncome,
                monthlyExpenses = source.monthlyExpenses,
                liabilities = source.liabilities,
                currencyFractionDigits = Formatters.currencyFractionDigits(source.currencySymbol)
            )
    }
}

/**
 * Данные для агрегации и cap, не входящие в индивидуальные баллы.
 * [isAllZero] соответствует согласованному правилу: нулевые доход, расходы,
 * активы и долги одновременно. Нули и знак денежных сумм определяются
 * в точности валюты ([currencyFractionDigits]), без сырого равенства Double.
 */
data class FinancialAssessmentAggregationInput(
    val netWorth: Double,
    val totalAssets: Double,
    val liabilities: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val currencyFractionDigits: Int = 2
) {
    init {
        require(currencyFractionDigits in 0..3) {
            "Currency fraction digits must be between 0 and 3"
        }
    }

    /**
     * Сравнение денежной суммы с нулём в точности валюты, а не сырым равенством
     * Double. Для ¥ (0 знаков) 0.4 уже ноль, для обычной валюты — нет.
     */
    fun isZeroAmount(value: Double): Boolean =
        value.isFinite() && roundedAmount(value).signum() == 0

    fun isNegativeAmount(value: Double): Boolean =
        value.isFinite() && roundedAmount(value).signum() < 0

    fun isPositiveAmount(value: Double): Boolean =
        value.isFinite() && roundedAmount(value).signum() > 0

    val isAllZero: Boolean
        get() = isZeroAmount(monthlyIncome) &&
            isZeroAmount(monthlyExpenses) &&
            isZeroAmount(totalAssets) &&
            isZeroAmount(liabilities)

    private fun roundedAmount(value: Double): BigDecimal =
        BigDecimal.valueOf(value).setScale(currencyFractionDigits, RoundingMode.HALF_UP)

    companion object {
        fun from(input: FinancialAssessmentInput): FinancialAssessmentAggregationInput =
            FinancialAssessmentAggregationInput(
                netWorth = input.netWorth,
                totalAssets = input.totalAssets,
                liabilities = input.liabilities,
                monthlyIncome = input.monthlyIncome,
                monthlyExpenses = input.monthlyExpenses,
                currencyFractionDigits = input.currencyFractionDigits
            )
    }
}

/**
 * Рассчитывает девять индивидуальных баллов и агрегирует их без изменения
 * raw-метрик. Не вызывает FinanceCalculator.calculate рекурсивно.
 */
object FinancialAssessmentCalculator {

    fun calculate(source: FinancialInputData, metrics: CalculatedMetrics): FinancialAssessmentComponents =
        calculate(FinancialAssessmentInput.from(source, metrics))

    fun calculate(input: FinancialAssessmentInput): FinancialAssessmentComponents {
        val hasIncome = input.monthlyIncome.isFinite() && input.monthlyIncome > 0.0
        val hasExpenses = input.monthlyExpenses.isFinite() && input.monthlyExpenses > 0.0
        val noBudgetData = !hasIncome && !hasExpenses

        val cashFlowScore = if (noBudgetData) {
            null
        } else {
            FinancialMetricEvaluator.getCashFlowScore(
                cashFlow = input.cashFlow,
                savingsRate = input.savingsRate,
                monthlyIncome = input.monthlyIncome
            )
        }

        val savingsRateScore = when {
            noBudgetData -> null
            !input.monthlyIncome.isFinite() -> null
            !input.savingsRate.isFinite() -> null
            else -> FinancialMetricEvaluator.getSavingsRateScore(input.savingsRate)
        }

        val safetyMonthsScore = if (hasExpenses && input.safetyMonths.isFinite()) {
            FinancialMetricEvaluator.getSafetyMonthsScore(input.safetyMonths)
        } else {
            null
        }

        val debtRatioScore = when {
            input.debtRatio.isNaN() -> null
            input.debtRatio == Double.NEGATIVE_INFINITY -> null
            input.debtRatio < 0.0 -> null
            else -> FinancialMetricEvaluator.getDebtRatioScore(input.debtRatio)
        }

        val netWorthScore = FinancialMetricEvaluator.getNetWorthScore(
            netWorth = input.netWorth,
            totalAssets = input.totalAssets
        )

        val fidelityScore = FinancialMetricEvaluator.getFidelityScore(
            capital = input.fidelityCapital,
            target = input.fidelityTarget,
            isAgeValid = input.isAgeValid
        )

        val cfsScore = if (hasExpenses && input.cfs.isFinite()) {
            FinancialMetricEvaluator.getCfsScore(input.cfs)
        } else {
            null
        }

        val realReturnScore = FinancialMetricEvaluator.getRealReturnScore(input.realReturnPercent)

        return FinancialAssessmentComponents.of(
            mapOf(
                FinancialAssessmentId.CASH_FLOW to cashFlowScore,
                FinancialAssessmentId.SAVINGS_RATE to savingsRateScore,
                FinancialAssessmentId.EMERGENCY_FUND to safetyMonthsScore,
                FinancialAssessmentId.SAFETY_MONTHS to safetyMonthsScore,
                FinancialAssessmentId.DEBT_RATIO to debtRatioScore,
                FinancialAssessmentId.NET_WORTH to netWorthScore,
                FinancialAssessmentId.FIDELITY to fidelityScore,
                FinancialAssessmentId.CFS to cfsScore,
                FinancialAssessmentId.REAL_RETURN to realReturnScore
            )
        )
    }

    /**
     * Полный результат: компоненты, группы, покрытие, cap и итог.
     * Старый [calculate] остаётся доступным и не меняет публичный итог приложения.
     */
    fun assess(source: FinancialInputData, metrics: CalculatedMetrics): FinancialAssessmentResult =
        assess(FinancialAssessmentInput.from(source, metrics))

    fun assess(input: FinancialAssessmentInput): FinancialAssessmentResult =
        aggregate(
            components = calculate(input),
            input = FinancialAssessmentAggregationInput.from(input)
        )

    /**
     * Двухуровневая агрегация по спецификации: сначала доступные веса внутри
     * группы, затем доступные веса групп. Группы не округляются; real_return
     * не входит ни в балл, ни в покрытие, ни в cap.
     */
    fun aggregate(
        components: FinancialAssessmentComponents,
        input: FinancialAssessmentAggregationInput
    ): FinancialAssessmentResult {
        val groupFractions = FinancialAssessmentGroup.values().map { group ->
            group to groupFraction(components, group)
        }
        val groups = groupFractions.map { (group, fraction) ->
            FinancialAssessmentGroupScore(group, fraction?.toDouble())
        }
        val coveragePercent = components.components
            .filter { it.isAvailable && !it.isForecastOnly }
            .sumOf { it.weightPercent }
        val raw = rawFraction(groupFractions)
        val unavailableReason = when {
            input.isAllZero -> FinancialAssessmentUnavailableReason.ALL_ZERO_INPUT
            raw == null -> FinancialAssessmentUnavailableReason.NO_AVAILABLE_GROUPS
            else -> null
        }

        if (raw == null || unavailableReason != null) {
            return FinancialAssessmentResult(
                components = components,
                groups = groups,
                rawScore = null,
                totalScore = null,
                coveragePercent = coveragePercent,
                capReasons = emptySet(),
                unavailableReason = unavailableReason
            )
        }

        val capReasons = capReasons(input, raw)
        val totalScore = if (capReasons.isEmpty()) {
            raw.roundToOneDecimal()
        } else {
            FinancialMetricEvaluator.ASSESSMENT_SCORE_CAP
        }
        return FinancialAssessmentResult(
            components = components,
            groups = groups,
            rawScore = raw.toDouble(),
            totalScore = totalScore,
            coveragePercent = coveragePercent,
            capReasons = capReasons,
            unavailableReason = null
        )
    }

    private fun groupFraction(
        components: FinancialAssessmentComponents,
        group: FinancialAssessmentGroup
    ): Fraction? {
        var numerator = 0L
        var denominator = 0L
        for (component in components.components) {
            if (component.group != group || component.isForecastOnly) continue
            val score = component.score ?: continue
            numerator += component.weightPercent.toLong() * score
            denominator += component.weightPercent
        }
        return if (denominator == 0L) null else Fraction(numerator, denominator)
    }

    private fun rawFraction(
        groupFractions: List<Pair<FinancialAssessmentGroup, Fraction?>>
    ): Fraction? {
        val available = groupFractions.mapNotNull { (group, fraction) ->
            fraction?.let { group to it }
        }
        if (available.isEmpty()) return null

        var commonDenominator = 1L
        for ((_, fraction) in available) {
            commonDenominator = lcm(commonDenominator, fraction.denominator)
        }

        var numerator = 0L
        var totalGroupWeight = 0L
        for ((group, fraction) in available) {
            val weight = group.weightPercent.toLong()
            numerator += weight * fraction.numerator * (commonDenominator / fraction.denominator)
            totalGroupWeight += weight
        }
        return Fraction(numerator, totalGroupWeight * commonDenominator)
    }

    /**
     * Cap фиксируется только когда он реально меняет балл: условия выполнены
     * и raw строго больше предела. Иначе набор причин пуст.
     */
    private fun capReasons(
        input: FinancialAssessmentAggregationInput,
        raw: Fraction
    ): Set<FinancialAssessmentCapReason> {
        if (!raw.isGreaterThan(FinancialMetricEvaluator.ASSESSMENT_SCORE_CAP)) {
            return emptySet()
        }
        return buildSet {
            if (input.isNegativeAmount(input.netWorth)) {
                add(FinancialAssessmentCapReason.NEGATIVE_NET_WORTH)
            }
            if (input.isZeroAmount(input.totalAssets) && input.isPositiveAmount(input.liabilities)) {
                add(FinancialAssessmentCapReason.LIABILITIES_WITHOUT_ASSETS)
            }
        }
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = a
        var y = b
        while (y != 0L) {
            val remainder = x % y
            x = y
            y = remainder
        }
        return x
    }

    private fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

    /** Точная дробь: позволяет округлять HALF_UP без двоичной погрешности. */
    private data class Fraction(val numerator: Long, val denominator: Long) {
        init {
            require(numerator >= 0L) { "Numerator must be non-negative" }
            require(denominator > 0L) { "Denominator must be positive" }
        }

        fun toDouble(): Double =
            BigDecimal(numerator)
                .divide(BigDecimal(denominator), MathContext.DECIMAL128)
                .toDouble()

        fun isGreaterThan(value: Double): Boolean =
            BigDecimal(numerator) >
                BigDecimal(denominator).multiply(BigDecimal.valueOf(value))

        fun roundToOneDecimal(): Double =
            BigDecimal(numerator)
                .divide(BigDecimal(denominator), 1, RoundingMode.HALF_UP)
                .toDouble()
    }
}
