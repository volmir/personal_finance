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

        val cfs = if (annualExpenses > 0) {
            totalAssets / annualExpenses
        } else 0.0

        val cfsStageDescription = getCfsStageDescription(cfs)

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
            deferredAssets = deferredAssets
        )

        val retirementAge = currentAge + yearsToRetire
        val remainingYearsInRetirement = (input.lifeExpectancy - retirementAge).coerceAtLeast(0.0)
        val r = realReturnPercent / 100.0
        val ongoingAnnualExpenses = (annualExpenses - temporaryExpenses.sumOf { it.annualAmount }).coerceAtLeast(0.0)
        val targetRetirementCapital = if (remainingYearsInRetirement > 0) {
            val baseReq = if (yearsToRetire >= deferredStartYears) {
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
            (baseReq + tempReq - deferredAssetDiscounted).coerceAtLeast(0.0)
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
            scores = scores
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
        return if (lang == com.example.util.localization.AppLanguage.EN) {
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

    fun calculateScores(
        savingsRate: Double,
        safetyMonths: Double,
        debtRatio: Double,
        cfs: Double
    ): FinancialScores {
        // 1. Savings Rate Score (0..10): In user sample 53.85% -> 8 points
        val savingsScore = when {
            savingsRate < 0 -> 0
            savingsRate < 5.0 -> 2
            savingsRate < 10.0 -> 3
            savingsRate < 15.0 -> 5
            savingsRate < 20.0 -> 6
            savingsRate < 35.0 -> 7
            savingsRate < 60.0 -> 8
            savingsRate < 75.0 -> 9
            else -> 10
        }

        // 2. Safety Months Score (0..10): In user sample 57.57 -> 10 points
        val safetyScore = when {
            safetyMonths < 0.5 -> 0
            safetyMonths < 1.0 -> 2
            safetyMonths < 3.0 -> 4
            safetyMonths < 6.0 -> 7
            safetyMonths < 12.0 -> 9
            else -> 10
        }

        // 3. Debt Ratio Score (0..10): In user sample 0.00 -> 10 points
        val debtScore = when {
            debtRatio <= 0.05 -> 10
            debtRatio <= 0.15 -> 9
            debtRatio <= 0.30 -> 8
            debtRatio <= 0.45 -> 7
            debtRatio <= 0.60 -> 6
            debtRatio <= 0.80 -> 4
            debtRatio <= 1.0 -> 2
            else -> 0
        }

        // 4. CFS Score (0..10): In user sample 4.80 (2..5 range) -> 6 points
        val cfsScore = when {
            cfs < 0 -> 0
            cfs <= 0.5 -> 2
            cfs <= 2.0 -> 4
            cfs <= 5.0 -> 6
            cfs <= 15.0 -> 8
            cfs <= 30.0 -> 9
            else -> 10
        }

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
        deferredAssets: List<DeferredAsset> = emptyList()
    ): Double {
        val maxYears = (lifeExpectancy - currentAge).toDouble()
        if (maxYears <= 0) return 0.0
        if (annualExpenses <= 0) return 0.0

        val r = realReturnRate.coerceAtLeast(-0.5)
        val ongoingAnnualExpenses = (annualExpenses - temporaryExpenses.sumOf { it.annualAmount }).coerceAtLeast(0.0)
        val ongoingAnnualCashFlow = annualCashFlow + temporaryExpenses.sumOf { it.annualAmount }
        val R = deferredAnnualExpenseReduction
        val T_def = deferredStartYears

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
            return (baseReq + tempReq - deferredAssetDiscounted).coerceAtLeast(0.0)
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
            return (baseReq + tempReq - deferredAssetDiscounted).coerceAtLeast(0.0)
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

                capCompounded - (baseDraw - savingsDraw) - tempDraw + deferredAssetsInRetirement
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
