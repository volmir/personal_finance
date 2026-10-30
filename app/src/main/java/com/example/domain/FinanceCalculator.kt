package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.FinancialScores
import com.example.data.model.TrajectoryPoint
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

data class TemporaryExpense(
    val annualAmount: Double,
    val durationYears: Double
)

data class DeferredAsset(
    val amount: Double,
    val afterYears: Double
)

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

    fun calculate(
        input: FinancialInputData,
        temporaryExpenses: List<TemporaryExpense> = emptyList(),
        deferredAnnualExpenseReduction: Double = 0.0,
        deferredStartYears: Double = 18.0,
        deferredAssets: List<DeferredAsset> = emptyList()
    ): CalculatedMetrics {
        val totalAssets = input.liquidAssets + input.financialAssets + input.propertyAssets
        val cashFlow = input.monthlyIncome - input.monthlyExpenses
        val savingsRate = if (input.monthlyIncome > 0) {
            (cashFlow / input.monthlyIncome) * 100.0
        } else 0.0

        val emergencyFund = if (input.includeFinancialInEmergencyFund) {
            input.liquidAssets + input.financialAssets
        } else {
            input.liquidAssets
        }

        val safetyMonths = if (input.monthlyExpenses > 0) {
            emergencyFund / input.monthlyExpenses
        } else 0.0

        val debtRatio = if (totalAssets > 0) {
            input.liabilities / totalAssets
        } else 0.0

        val netWorth = totalAssets - input.liabilities
        val annualIncome = input.monthlyIncome * 12.0
        val annualExpenses = input.monthlyExpenses * 12.0

        val computedAge = calculateAge(input.birthDate)
        val isAgeValid = computedAge != null
        val currentAge = computedAge ?: 0
        val remainingYearsHorizon = if (isAgeValid) (input.lifeExpectancy - currentAge).coerceAtLeast(0) else 0

        val fidelityMultiplier = if (isAgeValid) calculateFidelityMultiplier(currentAge) else 0.0
        val fidelityTarget = if (isAgeValid) annualIncome * fidelityMultiplier else 0.0

        val cfs = FinancialMetricEvaluator.calculateCfs(netWorth, annualExpenses)
        val cfsStageDescription = FinancialMetricEvaluator.getCfsStageDescription(cfs)

        val nominalRate = input.nominalReturnPercent / 100.0
        val inflRate = input.inflationPercent / 100.0
        val realReturnPercent = if (inflRate != -1.0) {
            ((1.0 + nominalRate) / (1.0 + inflRate) - 1.0) * 100.0
        } else 0.0

        val scores = calculateScores(
            savingsRate = savingsRate,
            safetyMonths = safetyMonths,
            debtRatio = debtRatio,
            cfs = cfs
        )

        // Динамический расчет эффективной ставки налога на прирост капитала с вывода
        // Учет в расчетах: применяется к выводу, если в структуре капитала есть финансовые активы (акции/облигации)
        val hasFinancialAssets = input.financialAssets > 0.0

        val engine = ProjectionEngine(
            ProjectionParams.from(
                input = input,
                temporaryExpenses = temporaryExpenses,
                deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
                deferredStartYears = deferredStartYears,
                deferredAssets = deferredAssets
            )
        )

        val yearsToRetire = if (isAgeValid) {
            engine.findYearsToRetirement()
        } else {
            0.0
        }

        val retirementAge = if (isAgeValid) currentAge + yearsToRetire else 0.0
        val remainingYearsInRetirement = if (isAgeValid) (input.lifeExpectancy - retirementAge).coerceAtLeast(0.0) else 0.0
        val ongoingAnnualExpenses = engine.ongoingAnnualExpenses

        // Индивидуальный расчет Blended Model для конкретного пользователя:
        val financialAssetRatio = engine.finRatio

        // Совокупные расходы на пенсии на всем горизонте дожития
        val totalRetirementExpenses = ongoingAnnualExpenses * remainingYearsInRetirement

        // Совокупные собственные вложения (тело капитала = текущие финансовые активы + пополнения до пенсии)
        val contributedPrincipal = input.financialAssets + maxOf(0.0, cashFlow * 12.0) * yearsToRetire

        // Базовый капитал до налогообложения
        val baseReq = engine.computeBaseRequirement(yearsToRetire, remainingYearsInRetirement)

        // Индивидуальный накопленный инвестиционный доход (прирост капитала)
        val totalGain = maxOf(
            (totalRetirementExpenses - contributedPrincipal).coerceAtLeast(0.0),
            (totalRetirementExpenses - baseReq).coerceAtLeast(0.0)
        )

        // Индивидуальная расчетная доля прибыли в выводе
        val individualGainShare = if (totalRetirementExpenses > 0.0 && hasFinancialAssets) {
            (totalGain / totalRetirementExpenses).coerceIn(0.0, 0.95)
        } else {
            0.0
        }

        // Индивидуальная эффективная ставка налога с вывода
        val effectiveCapitalGainsTaxRate = if (hasFinancialAssets && input.capitalGainsTaxPercent > 0.0) {
            ((input.capitalGainsTaxPercent / 100.0) * individualGainShare * financialAssetRatio).coerceIn(0.0, 0.50)
        } else {
            0.0
        }

        val taxMultiplier = if (effectiveCapitalGainsTaxRate > 0.0 && effectiveCapitalGainsTaxRate < 0.99) {
            1.0 / (1.0 - effectiveCapitalGainsTaxRate)
        } else {
            1.0
        }

        val targetRetirementCapital = if (isAgeValid && remainingYearsInRetirement > 0) {
            engine.requiredCapitalAt(yearsToRetire, fixedTaxFactor = taxMultiplier)
        } else 0.0

        return CalculatedMetrics(
            cashFlow = cashFlow,
            savingsRate = savingsRate,
            totalAssets = totalAssets,
            emergencyFund = emergencyFund,
            safetyMonths = safetyMonths,
            debtRatio = debtRatio,
            netWorth = netWorth,
            annualIncome = annualIncome,
            annualExpenses = annualExpenses,
            currentAge = currentAge,
            lifeExpectancy = input.lifeExpectancy,
            remainingYearsHorizon = remainingYearsHorizon,
            fidelityMultiplier = fidelityMultiplier,
            fidelityTarget = fidelityTarget,
            financialFreedomRatio = cfs,
            cfsStageDescription = cfsStageDescription,
            realReturnPercent = realReturnPercent,
            yearsToRetirement = yearsToRetire,
            retirementAge = retirementAge,
            retirementTargetCapital = targetRetirementCapital,
            scores = scores,
            capitalGainsTaxPercent = input.capitalGainsTaxPercent,
            effectiveCapitalGainsTaxRate = effectiveCapitalGainsTaxRate * 100.0,
            capitalGainsGainSharePercent = individualGainShare * 100.0,
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

    /**
     * Fidelity Benchmark Multiplier:
     * Discrete checkpoints:
     * 30: 1.0x
     * 40: 3.0x
     * 50: 6.0x
     * 60: 8.0x
     * 67+: 10.0x
     *
     * Linear interpolation between checkpoints:
     * For age 35: 1.0 + (35 - 30) / 10.0 * (3.0 - 1.0) = 1.0 + 0.5 * 2.0 = 2.0x
     */
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

    fun getCfsStageDescription(cfs: Double, lang: com.example.util.localization.AppLanguage = com.example.util.localization.AppLanguage.RU): String {
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

        val total = (savingsScore + safetyScore + debtScore + cfsScore) / 4.0
        return FinancialScores(
            savingsRateScore = savingsScore,
            safetyMonthsScore = safetyScore,
            debtRatioScore = debtScore,
            cfsScore = cfsScore,
            totalScore = total
        )
    }

    /**
     * Calculates years until financial independence (FIRE / retirement).
     * Solves: AccumulatedCapital(t) >= RequiredCapitalToCoverExpensesUntilLifeExpectancy(t).
     *
     * Initial capital: W0 = totalAssets
     * Annual addition: S = annualCashFlow
     * Annual expenses: Exp = annualExpenses
     * Real return: r
     * Horizon: currentAge to lifeExpectancy
     */
    fun calculateYearsToRetirement(
        currentAge: Int,
        lifeExpectancy: Int,
        totalAssets: Double,
        annualCashFlow: Double,
        annualExpenses: Double,
        realReturnRate: Double,
        temporaryExpenses: List<TemporaryExpense> = emptyList(),
        deferredAnnualExpenseReduction: Double = 0.0,
        deferredStartYears: Double = 18.0,
        deferredAssets: List<DeferredAsset> = emptyList(),
        financialAssets: Double = 0.0,
        liquidAssets: Double = 0.0,
        capitalGainsTaxPercent: Double = 0.0
    ): Double {
        val params = ProjectionParams(
            currentAge = currentAge,
            lifeExpectancy = lifeExpectancy,
            startCapital = totalAssets,
            annualCashFlow = annualCashFlow,
            annualExpenses = annualExpenses,
            realReturnRate = realReturnRate,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
            deferredStartYears = deferredStartYears,
            deferredAssets = deferredAssets,
            financialAssets = financialAssets,
            liquidAssets = liquidAssets,
            capitalGainsTaxPercent = capitalGainsTaxPercent,
            isAgeValid = true
        )
        return ProjectionEngine(params).findYearsToRetirement()
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
        deferredAssets: List<DeferredAsset> = emptyList()
    ): List<TrajectoryPoint> {
        val params = ProjectionParams.from(
            input = input,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
            deferredStartYears = deferredStartYears,
            deferredAssets = deferredAssets
        )
        val engine = ProjectionEngine(params)
        val effectiveTaxRate = (metrics.effectiveCapitalGainsTaxRate / 100.0).coerceIn(0.0, 0.50)
        return engine.buildTrajectory(
            precomputedYearsToRetire = metrics.yearsToRetirement,
            effectiveTaxRate = effectiveTaxRate
        )
    }
}
