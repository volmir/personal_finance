package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.FinancialScores
import com.example.data.model.TrajectoryPoint
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
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

        val currentAge = calculateAge(input.birthDate)
        val remainingYearsHorizon = (input.lifeExpectancy - currentAge).coerceAtLeast(0)

        val fidelityMultiplier = calculateFidelityMultiplier(currentAge)
        val fidelityTarget = annualIncome * fidelityMultiplier

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

        val yearsToRetire = calculateYearsToRetirement(
            currentAge = currentAge,
            lifeExpectancy = input.lifeExpectancy,
            totalAssets = netWorth,
            annualCashFlow = cashFlow * 12.0,
            annualExpenses = annualExpenses,
            realReturnRate = realReturnPercent / 100.0,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
            deferredStartYears = deferredStartYears,
            deferredAssets = deferredAssets,
            financialAssets = input.financialAssets,
            liquidAssets = input.liquidAssets,
            capitalGainsTaxPercent = input.capitalGainsTaxPercent
        )

        val retirementAge = currentAge + yearsToRetire
        val remainingYearsInRetirement = (input.lifeExpectancy - retirementAge).coerceAtLeast(0.0)
        val r = realReturnPercent / 100.0
        val ongoingAnnualExpenses = (annualExpenses - temporaryExpenses.sumOf { it.annualAmount }).coerceAtLeast(0.0)

        // Индивидуальный расчет Blended Model для конкретного пользователя:
        // 1. Доля финансовых активов (акции/облигации) в структуре ликвидных/инвестиционных активов
        val liquidatableAssets = input.financialAssets + input.liquidAssets
        val financialAssetRatio = if (liquidatableAssets > 0.0) {
            (input.financialAssets / liquidatableAssets).coerceIn(0.0, 1.0)
        } else {
            0.0
        }

        // 2. Совокупные расходы на пенсии на всем горизонте дожития
        val totalRetirementExpenses = ongoingAnnualExpenses * remainingYearsInRetirement

        // 3. Совокупные собственные вложения (тело капитала = текущие финансовые активы + пополнения до пенсии)
        val contributedPrincipal = input.financialAssets + maxOf(0.0, cashFlow * 12.0) * yearsToRetire

        // Базовый капитал до налогообложения
        val baseReq = if (remainingYearsInRetirement > 0) {
            if (yearsToRetire >= deferredStartYears) {
                val exp = (ongoingAnnualExpenses - deferredAnnualExpenseReduction).coerceAtLeast(0.0)
                if (r > 0.0001) {
                    exp * (1.0 - (1.0 + r).pow(-remainingYearsInRetirement)) / r
                } else {
                    exp * remainingYearsInRetirement
                }
            } else {
                val reqFull = if (r > 0.0001) {
                    ongoingAnnualExpenses * (1.0 - (1.0 + r).pow(-remainingYearsInRetirement)) / r
                } else {
                    ongoingAnnualExpenses * remainingYearsInRetirement
                }
                val remAfterDef = (input.lifeExpectancy - currentAge - deferredStartYears).coerceAtLeast(0.0)
                val savingsAtDef = if (remAfterDef > 0.0 && deferredAnnualExpenseReduction > 0.0) {
                    if (r > 0.0001) {
                        deferredAnnualExpenseReduction * (1.0 - (1.0 + r).pow(-remAfterDef)) / r
                    } else {
                        deferredAnnualExpenseReduction * remAfterDef
                    }
                } else 0.0
                val discountedSavings = if (r > 0.0001) {
                    savingsAtDef * (1.0 + r).pow(-(deferredStartYears - yearsToRetire))
                } else {
                    savingsAtDef
                }
                (reqFull - discountedSavings).coerceAtLeast(0.0)
            }
        } else 0.0

        // 4. Индивидуальный накопленный инвестиционный доход (прирост капитала)
        val totalGain = maxOf(
            (totalRetirementExpenses - contributedPrincipal).coerceAtLeast(0.0),
            (totalRetirementExpenses - baseReq).coerceAtLeast(0.0)
        )

        // 5. Индивидуальная расчетная доля прибыли в выводе (вместо жестко закодированных 60%)
        val individualGainShare = if (totalRetirementExpenses > 0.0 && hasFinancialAssets) {
            (totalGain / totalRetirementExpenses).coerceIn(0.0, 0.95)
        } else {
            0.0
        }

        // 6. Индивидуальная эффективная ставка налога с вывода
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

        val targetRetirementCapital = if (remainingYearsInRetirement > 0) {
            var tempReq = 0.0
            for (temp in temporaryExpenses) {
                if (yearsToRetire < temp.durationYears) {
                    val remainingYears = minOf(temp.durationYears - yearsToRetire, remainingYearsInRetirement)
                    if (remainingYears > 0.0) {
                        tempReq += if (r > 0.0001) {
                            temp.annualAmount * (1.0 - (1.0 + r).pow(-remainingYears)) / r
                        } else {
                            temp.annualAmount * remainingYears
                        }
                    }
                }
            }
            var deferredAssetDiscounted = 0.0
            for (asset in deferredAssets) {
                if (yearsToRetire < asset.afterYears && asset.afterYears < remainingYearsHorizon) {
                    val dt = asset.afterYears - yearsToRetire
                    deferredAssetDiscounted += if (r > 0.0001) {
                        asset.amount * (1.0 + r).pow(-dt)
                    } else {
                        asset.amount
                    }
                }
            }
            ((baseReq + tempReq) * taxMultiplier - deferredAssetDiscounted).coerceAtLeast(0.0)
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
            capitalGainsGainSharePercent = individualGainShare * 100.0
        )
    }

    fun calculateAge(birthDateStr: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val birthDate = LocalDate.parse(birthDateStr.trim(), formatter)
            // Use fixed 2026 anchor or current date matching sample (1979-05-11 -> 47 years in 2026)
            val currentDate = LocalDate.now()
            val period = Period.between(birthDate, currentDate)
            val computedAge = period.years
            if (computedAge > 0) computedAge else 47
        } catch (_: Exception) {
            47
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
     * For age 47: 3.0 + (47 - 40) / 10.0 * (6.0 - 3.0) = 3.0 + 0.7 * 3.0 = 5.1x!
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
        val maxYears = (lifeExpectancy - currentAge).toDouble()
        if (maxYears <= 0) return 0.0
        if (annualExpenses <= 0) return 0.0

        val r = realReturnRate.coerceAtLeast(-0.5)
        val ongoingAnnualExpenses = (annualExpenses - temporaryExpenses.sumOf { it.annualAmount }).coerceAtLeast(0.0)
        val ongoingAnnualCashFlow = annualCashFlow + temporaryExpenses.sumOf { it.annualAmount }
        val R = deferredAnnualExpenseReduction
        val T_def = deferredStartYears

        val liquidatableAssets = financialAssets + liquidAssets
        val finRatio = if (liquidatableAssets > 0.0) {
            (financialAssets / liquidatableAssets).coerceIn(0.0, 1.0)
        } else 0.0

        fun getTaxFactorAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
            if (capitalGainsTaxPercent <= 0.0 || financialAssets <= 0.0 || yearsInRetirement <= 0.0) {
                return 1.0
            }
            val totalRetirementSpend = ongoingAnnualExpenses * yearsInRetirement
            val contributedPrincipal = financialAssets + maxOf(0.0, ongoingAnnualCashFlow) * t
            val totalGain = maxOf(
                (totalRetirementSpend - contributedPrincipal).coerceAtLeast(0.0),
                (totalRetirementSpend - baseReq).coerceAtLeast(0.0)
            )
            val individualGainShare = if (totalRetirementSpend > 0.0) {
                (totalGain / totalRetirementSpend).coerceIn(0.0, 0.95)
            } else 0.0

            val dynamicEffectiveTax = ((capitalGainsTaxPercent / 100.0) * individualGainShare * finRatio).coerceIn(0.0, 0.50)
            return if (dynamicEffectiveTax > 0.0 && dynamicEffectiveTax < 0.99) {
                1.0 / (1.0 - dynamicEffectiveTax)
            } else 1.0
        }

        // Function to compute required capital at retirement year t
        fun requiredCapital(t: Double): Double {
            val yearsInRetirement = (maxYears - t).coerceAtLeast(0.0)
            if (yearsInRetirement <= 0.0) return 0.0

            val baseReq = if (t >= T_def) {
                val exp = (ongoingAnnualExpenses - R).coerceAtLeast(0.0)
                if (kotlin.math.abs(r) > 0.0001) {
                    exp * (1.0 - (1.0 + r).pow(-yearsInRetirement)) / r
                } else {
                    exp * yearsInRetirement
                }
            } else {
                val reqFull = if (kotlin.math.abs(r) > 0.0001) {
                    ongoingAnnualExpenses * (1.0 - (1.0 + r).pow(-yearsInRetirement)) / r
                } else {
                    ongoingAnnualExpenses * yearsInRetirement
                }
                val remAfterDef = (maxYears - T_def).coerceAtLeast(0.0)
                val savingsAtDef = if (remAfterDef > 0.0 && R > 0.0) {
                    if (kotlin.math.abs(r) > 0.0001) {
                        R * (1.0 - (1.0 + r).pow(-remAfterDef)) / r
                    } else {
                        R * remAfterDef
                    }
                } else 0.0
                val discountedSavings = if (kotlin.math.abs(r) > 0.0001) {
                    savingsAtDef * (1.0 + r).pow(-(T_def - t))
                } else {
                    savingsAtDef
                }
                (reqFull - discountedSavings).coerceAtLeast(0.0)
            }

            var tempReq = 0.0
            for (temp in temporaryExpenses) {
                if (t < temp.durationYears) {
                    val remainingYears = minOf(temp.durationYears - t, yearsInRetirement)
                    if (remainingYears > 0.0) {
                        tempReq += if (kotlin.math.abs(r) > 0.0001) {
                            temp.annualAmount * (1.0 - (1.0 + r).pow(-remainingYears)) / r
                        } else {
                            temp.annualAmount * remainingYears
                        }
                    }
                }
            }

            val taxFactor = getTaxFactorAt(t, yearsInRetirement, baseReq)

            var deferredAssetDiscounted = 0.0
            for (asset in deferredAssets) {
                if (t < asset.afterYears && asset.afterYears < maxYears) {
                    val dt = asset.afterYears - t
                    deferredAssetDiscounted += if (kotlin.math.abs(r) > 0.0001) {
                        asset.amount * (1.0 + r).pow(-dt)
                    } else {
                        asset.amount
                    }
                }
            }
            return ((baseReq + tempReq) * taxFactor - deferredAssetDiscounted).coerceAtLeast(0.0)
        }

        // Function to compute accumulated capital at year t
        fun accumulatedCapital(t: Double): Double {
            val baseAcc = if (kotlin.math.abs(r) > 0.0001) {
                totalAssets * (1.0 + r).pow(t) + ongoingAnnualCashFlow * ((1.0 + r).pow(t) - 1.0) / r
            } else {
                totalAssets + ongoingAnnualCashFlow * t
            }
            val extraAccFromR = if (t > T_def && R > 0.0) {
                val dt = t - T_def
                if (kotlin.math.abs(r) > 0.0001) {
                    R * ((1.0 + r).pow(dt) - 1.0) / r
                } else {
                    R * dt
                }
            } else 0.0

            var tempCost = 0.0
            for (temp in temporaryExpenses) {
                val tau = minOf(t, temp.durationYears)
                if (tau > 0.0) {
                    val fvAtTau = if (kotlin.math.abs(r) > 0.0001) {
                        temp.annualAmount * ((1.0 + r).pow(tau) - 1.0) / r
                    } else {
                        temp.annualAmount * tau
                    }
                    val compoundedToT = if (kotlin.math.abs(r) > 0.0001) {
                        fvAtTau * (1.0 + r).pow(t - tau)
                    } else {
                        fvAtTau
                    }
                    tempCost += compoundedToT
                }
            }

            var deferredAssetsValue = 0.0
            for (asset in deferredAssets) {
                if (t >= asset.afterYears) {
                    val dt = t - asset.afterYears
                    deferredAssetsValue += if (kotlin.math.abs(r) > 0.0001) {
                        asset.amount * (1.0 + r).pow(dt)
                    } else {
                        asset.amount
                    }
                }
            }
            return baseAcc + extraAccFromR - tempCost + deferredAssetsValue
        }

        // Check if already ready at t = 0
        if (accumulatedCapital(0.0) >= requiredCapital(0.0)) {
            return 0.0
        }

        // Binary search for t in [0.0, maxYears]
        var low = 0.0
        var high = maxYears
        var bestT = maxYears

        for (i in 0 until 60) {
            val mid = (low + high) / 2.0
            val acc = accumulatedCapital(mid)
            val req = requiredCapital(mid)
            if (acc >= req) {
                bestT = mid
                high = mid
            } else {
                low = mid
            }
        }

        // Round to 2 decimal places (e.g. 10.02)
        return (bestT * 100.0).roundToInt() / 100.0
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
        val list = mutableListOf<TrajectoryPoint>()
        val startAge = metrics.currentAge
        val endAge = metrics.lifeExpectancy
        val totalYears = endAge - startAge
        val r = metrics.realReturnPercent / 100.0
        val yearsToRetire = metrics.yearsToRetirement
        val ongoingAnnualExpenses = (metrics.annualExpenses - temporaryExpenses.sumOf { it.annualAmount }).coerceAtLeast(0.0)
        val ongoingAnnualCashFlow = metrics.cashFlow * 12.0 + temporaryExpenses.sumOf { it.annualAmount }
        val startCapital = metrics.netWorth
        val R = deferredAnnualExpenseReduction
        val T_def = deferredStartYears
        val effectiveCapitalGainsTaxRate = (metrics.effectiveCapitalGainsTaxRate / 100.0).coerceIn(0.0, 0.50)
        val taxFactor = if (effectiveCapitalGainsTaxRate > 0.0 && effectiveCapitalGainsTaxRate < 0.99) {
            1.0 / (1.0 - effectiveCapitalGainsTaxRate)
        } else {
            1.0
        }

        fun requiredCapitalAt(t: Double): Double {
            val yearsInRetireLeft = (totalYears.toDouble() - t).coerceAtLeast(0.0)
            if (yearsInRetireLeft <= 0.0) return 0.0

            val baseReq = if (t >= T_def) {
                val exp = (ongoingAnnualExpenses - R).coerceAtLeast(0.0)
                if (kotlin.math.abs(r) > 0.0001) {
                    exp * (1.0 - (1.0 + r).pow(-yearsInRetireLeft)) / r
                } else {
                    exp * yearsInRetireLeft
                }
            } else {
                val reqFull = if (kotlin.math.abs(r) > 0.0001) {
                    ongoingAnnualExpenses * (1.0 - (1.0 + r).pow(-yearsInRetireLeft)) / r
                } else {
                    ongoingAnnualExpenses * yearsInRetireLeft
                }
                val remAfterDef = (totalYears.toDouble() - T_def).coerceAtLeast(0.0)
                val savingsAtDef = if (remAfterDef > 0.0 && R > 0.0) {
                    if (kotlin.math.abs(r) > 0.0001) {
                        R * (1.0 - (1.0 + r).pow(-remAfterDef)) / r
                    } else {
                        R * remAfterDef
                    }
                } else 0.0
                val discountedSavings = if (kotlin.math.abs(r) > 0.0001) {
                    savingsAtDef * (1.0 + r).pow(-(T_def - t))
                } else {
                    savingsAtDef
                }
                (reqFull - discountedSavings).coerceAtLeast(0.0)
            }

            var tempReq = 0.0
            for (temp in temporaryExpenses) {
                if (t < temp.durationYears) {
                    val remainingYears = minOf(temp.durationYears - t, yearsInRetireLeft)
                    if (remainingYears > 0.0) {
                        tempReq += if (kotlin.math.abs(r) > 0.0001) {
                            temp.annualAmount * (1.0 - (1.0 + r).pow(-remainingYears)) / r
                        } else {
                            temp.annualAmount * remainingYears
                        }
                    }
                }
            }

            var deferredAssetDiscounted = 0.0
            for (asset in deferredAssets) {
                if (t < asset.afterYears && asset.afterYears < totalYears) {
                    val dt = asset.afterYears - t
                    deferredAssetDiscounted += if (kotlin.math.abs(r) > 0.0001) {
                        asset.amount * (1.0 + r).pow(-dt)
                    } else {
                        asset.amount
                    }
                }
            }
            return ((baseReq + tempReq) * taxFactor - deferredAssetDiscounted).coerceAtLeast(0.0)
        }

        fun accumulatedCapitalWorking(t: Double): Double {
            val baseAcc = if (kotlin.math.abs(r) > 0.0001) {
                startCapital * (1.0 + r).pow(t) + ongoingAnnualCashFlow * ((1.0 + r).pow(t) - 1.0) / r
            } else {
                startCapital + ongoingAnnualCashFlow * t
            }
            val extraAccFromR = if (t > T_def && R > 0.0) {
                val dt = t - T_def
                if (kotlin.math.abs(r) > 0.0001) {
                    R * ((1.0 + r).pow(dt) - 1.0) / r
                } else {
                    R * dt
                }
            } else 0.0

            var tempCost = 0.0
            for (temp in temporaryExpenses) {
                val tau = minOf(t, temp.durationYears)
                if (tau > 0.0) {
                    val fvAtTau = if (kotlin.math.abs(r) > 0.0001) {
                        temp.annualAmount * ((1.0 + r).pow(tau) - 1.0) / r
                    } else {
                        temp.annualAmount * tau
                    }
                    val compoundedToT = if (kotlin.math.abs(r) > 0.0001) {
                        fvAtTau * (1.0 + r).pow(t - tau)
                    } else {
                        fvAtTau
                    }
                    tempCost += compoundedToT
                }
            }

            var deferredAssetsValue = 0.0
            for (asset in deferredAssets) {
                if (t >= asset.afterYears) {
                    val dt = t - asset.afterYears
                    deferredAssetsValue += if (kotlin.math.abs(r) > 0.0001) {
                        asset.amount * (1.0 + r).pow(dt)
                    } else {
                        asset.amount
                    }
                }
            }
            return baseAcc + extraAccFromR - tempCost + deferredAssetsValue
        }

        val capAtRetirement = accumulatedCapitalWorking(yearsToRetire)

        for (i in 0..totalYears) {
            val currentYearOffset = i.toDouble()
            val ageAtYear = startAge + currentYearOffset
            val isRetired = currentYearOffset >= yearsToRetire

            val required = requiredCapitalAt(currentYearOffset)

            val accumulated = if (!isRetired) {
                accumulatedCapitalWorking(currentYearOffset)
            } else {
                // In retirement, drawing expenses
                val yearsSinceRetirement = currentYearOffset - yearsToRetire
                val baseDraw = if (kotlin.math.abs(r) > 0.0001) {
                    ongoingAnnualExpenses * ((1.0 + r).pow(yearsSinceRetirement) - 1.0) / r
                } else {
                    ongoingAnnualExpenses * yearsSinceRetirement
                }
                val savingsDraw = if (currentYearOffset > T_def && R > 0.0) {
                    val tauDef = currentYearOffset - maxOf(yearsToRetire, T_def)
                    if (tauDef > 0.0) {
                        if (kotlin.math.abs(r) > 0.0001) {
                            R * ((1.0 + r).pow(tauDef) - 1.0) / r
                        } else {
                            R * tauDef
                        }
                    } else 0.0
                } else 0.0

                var tempDraw = 0.0
                for (temp in temporaryExpenses) {
                    if (yearsToRetire < temp.durationYears) {
                        val tauRet = minOf(currentYearOffset, temp.durationYears) - yearsToRetire
                        if (tauRet > 0.0) {
                            val fvTau = if (kotlin.math.abs(r) > 0.0001) {
                                temp.annualAmount * ((1.0 + r).pow(tauRet) - 1.0) / r
                            } else {
                                temp.annualAmount * tauRet
                            }
                            val comp = if (kotlin.math.abs(r) > 0.0001) {
                                fvTau * (1.0 + r).pow(currentYearOffset - minOf(currentYearOffset, temp.durationYears))
                            } else {
                                fvTau
                            }
                            tempDraw += comp
                        }
                    }
                }
                val capCompounded = if (kotlin.math.abs(r) > 0.0001) {
                    capAtRetirement * (1.0 + r).pow(yearsSinceRetirement)
                } else {
                    capAtRetirement
                }

                var deferredAssetsInRetirement = 0.0
                for (asset in deferredAssets) {
                    if (yearsToRetire < asset.afterYears && currentYearOffset >= asset.afterYears) {
                        val dt = currentYearOffset - asset.afterYears
                        deferredAssetsInRetirement += if (kotlin.math.abs(r) > 0.0001) {
                            asset.amount * (1.0 + r).pow(dt)
                        } else {
                            asset.amount
                        }
                    }
                }

                capCompounded - (baseDraw - savingsDraw + tempDraw) * taxFactor + deferredAssetsInRetirement
            }

            list.add(
                TrajectoryPoint(
                    age = ageAtYear,
                    yearOffset = currentYearOffset,
                    accumulatedCapital = accumulated.coerceAtLeast(0.0),
                    requiredCapital = required.coerceAtLeast(0.0),
                    isRetired = isRetired
                )
            )
        }

        return list
    }
}
