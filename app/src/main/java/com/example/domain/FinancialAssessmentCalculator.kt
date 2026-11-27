package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.model.FinancialAssessmentComponents
import com.example.domain.model.FinancialAssessmentId

/**
 * Уже вычисленные значения для компонентных оценок. Денежные величины должны
 * быть нормализованы к точности валюты вызывающим кодом (FinanceCalculator):
 * калькулятор не повторяет валютную нормализацию и не пересчитывает raw-метрики.
 * [liabilities] сохранены для последующей агрегации и cap, но в компонентных
 * баллах не участвуют.
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
    val liabilities: Double
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
                liabilities = source.liabilities
            )
    }
}

/**
 * Рассчитывает девять индивидуальных баллов без агрегации, покрытия и cap.
 * Не меняет raw-метрики и не вызывает FinanceCalculator.calculate рекурсивно.
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
}
