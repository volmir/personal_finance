package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.FinancialScores
import com.example.data.model.TrajectoryPoint
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import com.example.util.Formatters

data class TemporaryExpense(
    val annualAmount: Double,
    val durationYears: Double
)

object FinancialInputLimits {
    const val MIN_RATE_PERCENT = -50.0
    const val MAX_RATE_PERCENT = 100.0
    const val MIN_TAX_PERCENT = 0.0
    const val MAX_TAX_PERCENT = 100.0

    // Existing projection limits also govern displayed values and trajectories.
    const val MIN_REAL_RETURN_RATE = -0.5
    const val MAX_EFFECTIVE_TAX_RATE = 0.50

    fun clampRatePercent(value: Double): Double = value.coerceIn(MIN_RATE_PERCENT, MAX_RATE_PERCENT)
    fun clampTaxPercent(value: Double): Double = value.coerceIn(MIN_TAX_PERCENT, MAX_TAX_PERCENT)
}

object FinanceCalculator {

    /**
     * Calculates the present value of an annuity with constant annual payment.
     * For abs(r) > 0.0001: payment * (1 - (1+r)^-years) / r
     * For abs(r) <= 0.0001: payment * years
     */
    internal fun annuityPv(payment: Double, r: Double, years: Double): Double {
        return if (abs(r) > 0.0001) {
            payment * (1.0 - (1.0 + r).pow(-years)) / r
        } else {
            payment * years
        }
    }

    internal fun calculateRealReturnPercent(nominalReturnPercent: Double, inflationPercent: Double): Double {
        val nominalRate = FinancialInputLimits.clampRatePercent(nominalReturnPercent) / 100.0
        val inflationRate = FinancialInputLimits.clampRatePercent(inflationPercent) / 100.0
        val realRate = (1.0 + nominalRate) / (1.0 + inflationRate) - 1.0
        return realRate.coerceAtLeast(FinancialInputLimits.MIN_REAL_RETURN_RATE) * 100.0
    }

    fun calculate(
        input: FinancialInputData,
        temporaryExpenses: List<TemporaryExpense> = emptyList(),
        deferredAnnualExpenseReduction: Double = 0.0,
        deferredStartYears: Double = 18.0,
        extraMonthlyCashFlow: Double = 0.0,
        today: LocalDate = LocalDate.now()
    ): CalculatedMetrics {
        val currencyDigits = Formatters.currencyFractionDigits(input.currencySymbol)
        fun roundCurrency(value: Double): Double {
            if (!value.isFinite()) return value
            val rounded = BigDecimal.valueOf(value)
                .setScale(currencyDigits, RoundingMode.HALF_UP)
                .toDouble()
            return if (rounded == 0.0) 0.0 else rounded
        }

        val totalAssets = roundCurrency(input.liquidAssets + input.financialAssets + input.propertyAssets)
        val cashFlow = roundCurrency(input.monthlyIncome - input.monthlyExpenses + extraMonthlyCashFlow)
        val savingsRate = if (input.monthlyIncome > 0) {
            (cashFlow / input.monthlyIncome) * 100.0
        } else if (cashFlow < 0.0) -100.0 else 0.0

        val emergencyFund = roundCurrency(
            if (input.includeFinancialInEmergencyFund) {
                input.liquidAssets + input.financialAssets
            } else {
                input.liquidAssets
            }
        )

        val safetyMonths = when {
            input.monthlyExpenses > 0 -> emergencyFund / input.monthlyExpenses
            input.monthlyExpenses == 0.0 && emergencyFund > 0.0 ->
                FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT
            else -> 0.0
        }

        val debtRatio = if (totalAssets > 0) {
            input.liabilities / totalAssets
        } else if (input.liabilities > 0.0) Double.POSITIVE_INFINITY else 0.0

        val netWorth = roundCurrency(totalAssets - input.liabilities)
        val fidelityCapital = roundCurrency(
            FinancialMetricEvaluator.fidelityCapital(
                input.liquidAssets, input.financialAssets, input.propertyAssets, input.liabilities
            )
        )
        val annualIncome = input.monthlyIncome * 12.0
        val annualExpenses = input.monthlyExpenses * 12.0

        val computedAge = calculateAge(input.birthDate, today)
        val isAgeValid = computedAge != null
        val currentAge = computedAge ?: 0
        val remainingYearsHorizon = if (isAgeValid) (input.lifeExpectancy - currentAge).coerceAtLeast(0) else 0

        val fidelityMultiplier = if (isAgeValid) calculateFidelityMultiplier(currentAge) else 0.0
        val fidelityTarget = if (isAgeValid) annualIncome * fidelityMultiplier else 0.0

        val cfs = FinancialMetricEvaluator.calculateCfs(netWorth, annualExpenses)
        val realReturnPercent = calculateRealReturnPercent(input.nominalReturnPercent, input.inflationPercent)

        val scores = calculateScores(
            savingsRate = savingsRate,
            safetyMonths = safetyMonths,
            debtRatio = debtRatio,
            cfs = cfs
        )

        val engine = ProjectionEngine(
            ProjectionParams.from(
                input = input,
                temporaryExpenses = temporaryExpenses,
                deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
                deferredStartYears = deferredStartYears,
                extraMonthlyCashFlow = extraMonthlyCashFlow,
                today = today
            )
        )

        val retirementResult = engine.findYearsToRetirement()
        val yearsToRetire = retirementResult.years

        val retirementAge = if (isAgeValid) currentAge + yearsToRetire else 0.0
        val remainingYearsInRetirement = if (isAgeValid) (input.lifeExpectancy - retirementAge).coerceAtLeast(0.0) else 0.0
        // Базовый капитал до налогообложения
        val baseReq = engine.computeBaseRequirement(yearsToRetire, remainingYearsInRetirement)
        val taxInfo = engine.taxInfo(yearsToRetire, remainingYearsInRetirement, baseReq)

        val targetRetirementCapital = if (isAgeValid && remainingYearsInRetirement > 0) {
            engine.requiredCapitalAt(yearsToRetire, fixedTaxFactor = taxInfo.factor)
        } else 0.0

        return CalculatedMetrics(
            cashFlow = cashFlow,
            savingsRate = savingsRate,
            totalAssets = totalAssets,
            emergencyFund = emergencyFund,
            safetyMonths = safetyMonths,
            debtRatio = debtRatio,
            netWorth = netWorth,
            fidelityCapital = fidelityCapital,
            annualIncome = annualIncome,
            annualExpenses = annualExpenses,
            currentAge = currentAge,
            lifeExpectancy = input.lifeExpectancy,
            remainingYearsHorizon = remainingYearsHorizon,
            fidelityMultiplier = fidelityMultiplier,
            fidelityTarget = fidelityTarget,
            financialFreedomRatio = cfs,
            realReturnPercent = realReturnPercent,
            yearsToRetirement = yearsToRetire,
            retirementAge = retirementAge,
            fireStatus = retirementResult.status,
            retirementTargetCapital = targetRetirementCapital,
            scores = scores,
            capitalGainsTaxPercent = FinancialInputLimits.clampTaxPercent(input.capitalGainsTaxPercent),
            effectiveCapitalGainsTaxRate = taxInfo.effectiveRate * 100.0,
            capitalGainsGainSharePercent = taxInfo.gainShare * 100.0,
            isAgeValid = isAgeValid
        )
    }

    fun calculateAge(birthDateStr: String, today: LocalDate = LocalDate.now()): Int? {
        return try {
            val trimmed = birthDateStr.trim()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val birthDate = LocalDate.parse(trimmed, formatter)
            if (birthDate.format(formatter) != trimmed) {
                return null
            }
            if (birthDate.isAfter(today)) {
                return null
            }
            val period = Period.between(birthDate, today)
            val computedAge = period.years
            if (computedAge in 0..120) computedAge else null
        } catch (_: Exception) {
            null
        }
    }

    /** Адаптация приложения: до 25 лет 0.5x; 25–30 линейно 0.5→1.0; далее линейно между точками. */
    fun calculateFidelityMultiplier(age: Int): Double {
        return when {
            age <= 25 -> 0.5
            age <= 30 -> 0.5 + (age - 25) * (0.5 / 5.0)
            age <= 40 -> 1.0 + (age - 30) * (2.0 / 10.0)
            age <= 50 -> 3.0 + (age - 40) * (3.0 / 10.0)
            age <= 60 -> 6.0 + (age - 50) * (2.0 / 10.0)
            age <= 67 -> 8.0 + (age - 60) * (2.0 / 7.0)
            else -> 10.0
        }
    }

    fun getCfsStageDescription(cfs: Double, lang: com.example.util.localization.AppLanguage): String {
        return FinancialMetricEvaluator.getCfsStageDescription(cfs, lang)
    }

    fun calculateScores(
        savingsRate: Double,
        safetyMonths: Double,
        debtRatio: Double,
        cfs: Double
    ): FinancialScores {
        val savingsScore = FinancialMetricEvaluator.getSavingsRateScore(savingsRate)
        val safetyScore = FinancialMetricEvaluator.getSafetyMonthsScore(safetyMonths)
        val debtScore = FinancialMetricEvaluator.getDebtRatioScore(debtRatio)
        val cfsScore = FinancialMetricEvaluator.getCfsScore(cfs)

        val total = calculateTotalScore(savingsScore, safetyScore, debtScore, cfsScore)
        return FinancialScores(
            savingsRateScore = savingsScore,
            safetyMonthsScore = safetyScore,
            debtRatioScore = debtScore,
            cfsScore = cfsScore,
            totalScore = total
        )
    }

    internal fun calculateTotalScore(
        savingsScore: Int,
        safetyScore: Int,
        debtScore: Int,
        cfsScore: Int
    ): Double {
        var h = 25 * savingsScore + 15 * safetyScore + 15 * debtScore + 45 * cfsScore
        if (cfsScore < FinancialMetricEvaluator.TOTAL_SCORE_CFS_CAP_TRIGGER && h > 600) {
            h = 600
        }
        val tenths = (h + 5) / 10
        return tenths / 10.0
    }

    /**
     * Generates annual trajectory projection for visual charts
     */
    fun generateTrajectory(
        metrics: CalculatedMetrics,
        input: FinancialInputData,
        temporaryExpenses: List<TemporaryExpense> = emptyList(),
        deferredAnnualExpenseReduction: Double = 0.0,
        deferredStartYears: Double = 18.0,
        extraMonthlyCashFlow: Double = 0.0,
        today: LocalDate = LocalDate.now()
    ): List<TrajectoryPoint> {
        val params = ProjectionParams.from(
            input = input,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
            deferredStartYears = deferredStartYears,
            extraMonthlyCashFlow = extraMonthlyCashFlow,
            today = today
        )
        val engine = ProjectionEngine(params)
        val effectiveTaxRate = (metrics.effectiveCapitalGainsTaxRate / 100.0)
            .coerceIn(0.0, FinancialInputLimits.MAX_EFFECTIVE_TAX_RATE)
        return engine.buildTrajectory(
            precomputedYearsToRetire = metrics.yearsToRetirement,
            effectiveTaxRate = effectiveTaxRate
        )
    }
}
