package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.TrajectoryPoint
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

data class ProjectionParams(
    val currentAge: Int,
    val lifeExpectancy: Int,
    val startCapital: Double,
    val annualCashFlow: Double,
    val annualExpenses: Double,
    val realReturnRate: Double,
    val temporaryExpenses: List<TemporaryExpense> = emptyList(),
    val deferredAnnualExpenseReduction: Double = 0.0,
    val deferredStartYears: Double = 18.0,
    val deferredAssets: List<DeferredAsset> = emptyList(),
    val financialAssets: Double = 0.0,
    val liquidAssets: Double = 0.0,
    val capitalGainsTaxPercent: Double = 0.0,
    val isAgeValid: Boolean = true
) {
    companion object {
        // Капитал FIRE = ликвидные + финансовые; имущество не учитывается; долги вычитаются только в части, не покрытой имуществом (покрытые имуществом долги обслуживаются через расходы)
        fun fireStartCapital(input: FinancialInputData): Double {
            return maxOf(0.0, input.liquidAssets + input.financialAssets + minOf(0.0, input.propertyAssets - input.liabilities))
        }

        fun from(
            input: FinancialInputData,
            temporaryExpenses: List<TemporaryExpense> = emptyList(),
            deferredAnnualExpenseReduction: Double = 0.0,
            deferredStartYears: Double = 18.0,
            deferredAssets: List<DeferredAsset> = emptyList()
        ): ProjectionParams {
            val computedAge = FinanceCalculator.calculateAge(input.birthDate)
            val isAgeValid = computedAge != null
            val currentAge = computedAge ?: 0
            val startCapital = fireStartCapital(input)
            val cashFlow = input.monthlyIncome - input.monthlyExpenses
            val nominalRate = input.nominalReturnPercent / 100.0
            val inflRate = input.inflationPercent / 100.0
            val realReturnPercent = if (inflRate != -1.0) {
                ((1.0 + nominalRate) / (1.0 + inflRate) - 1.0) * 100.0
            } else 0.0

            return ProjectionParams(
                currentAge = currentAge,
                lifeExpectancy = input.lifeExpectancy,
                startCapital = startCapital,
                annualCashFlow = cashFlow * 12.0,
                annualExpenses = input.monthlyExpenses * 12.0,
                realReturnRate = realReturnPercent / 100.0,
                temporaryExpenses = temporaryExpenses,
                deferredAnnualExpenseReduction = deferredAnnualExpenseReduction,
                deferredStartYears = deferredStartYears,
                deferredAssets = deferredAssets,
                financialAssets = input.financialAssets,
                liquidAssets = input.liquidAssets,
                capitalGainsTaxPercent = input.capitalGainsTaxPercent,
                isAgeValid = isAgeValid
            )
        }
    }
}

class ProjectionEngine(val params: ProjectionParams) {

    val maxYears: Double = (params.lifeExpectancy - params.currentAge).toDouble().coerceAtLeast(0.0)
    val r: Double = params.realReturnRate.coerceAtLeast(-0.5)
    val ongoingAnnualExpenses: Double = (params.annualExpenses - params.temporaryExpenses.sumOf { it.annualAmount }).coerceAtLeast(0.0)
    val ongoingAnnualCashFlow: Double = params.annualCashFlow + params.temporaryExpenses.sumOf { it.annualAmount }
    val deferredReduction: Double = params.deferredAnnualExpenseReduction
    val deferredStartYears: Double = params.deferredStartYears

    private val liquidatableAssets: Double = params.financialAssets + params.liquidAssets
    val finRatio: Double = if (liquidatableAssets > 0.0) {
        (params.financialAssets / liquidatableAssets).coerceIn(0.0, 1.0)
    } else 0.0

    private fun annuityPv(payment: Double, rate: Double, years: Double): Double {
        return if (abs(rate) > 0.0001) {
            payment * (1.0 - (1.0 + rate).pow(-years)) / rate
        } else {
            payment * years
        }
    }

    fun getTaxFactorAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
        if (params.capitalGainsTaxPercent <= 0.0 || params.financialAssets <= 0.0 || yearsInRetirement <= 0.0) {
            return 1.0
        }
        val totalRetirementSpend = ongoingAnnualExpenses * yearsInRetirement
        val contributedPrincipal = params.financialAssets + maxOf(0.0, ongoingAnnualCashFlow) * t
        val totalGain = maxOf(
            (totalRetirementSpend - contributedPrincipal).coerceAtLeast(0.0),
            (totalRetirementSpend - baseReq).coerceAtLeast(0.0)
        )
        val individualGainShare = if (totalRetirementSpend > 0.0) {
            (totalGain / totalRetirementSpend).coerceIn(0.0, 0.95)
        } else 0.0

        val dynamicEffectiveTax = ((params.capitalGainsTaxPercent / 100.0) * individualGainShare * finRatio).coerceIn(0.0, 0.50)
        return if (dynamicEffectiveTax > 0.0 && dynamicEffectiveTax < 0.99) {
            1.0 / (1.0 - dynamicEffectiveTax)
        } else 1.0
    }

    fun getEffectiveTaxRateAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
        if (params.capitalGainsTaxPercent <= 0.0 || params.financialAssets <= 0.0 || yearsInRetirement <= 0.0) {
            return 0.0
        }
        val totalRetirementSpend = ongoingAnnualExpenses * yearsInRetirement
        val contributedPrincipal = params.financialAssets + maxOf(0.0, ongoingAnnualCashFlow) * t
        val totalGain = maxOf(
            (totalRetirementSpend - contributedPrincipal).coerceAtLeast(0.0),
            (totalRetirementSpend - baseReq).coerceAtLeast(0.0)
        )
        val individualGainShare = if (totalRetirementSpend > 0.0) {
            (totalGain / totalRetirementSpend).coerceIn(0.0, 0.95)
        } else 0.0

        return ((params.capitalGainsTaxPercent / 100.0) * individualGainShare * finRatio).coerceIn(0.0, 0.50) * 100.0
    }

    fun getGainShareAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
        if (params.financialAssets <= 0.0 || yearsInRetirement <= 0.0) {
            return 0.0
        }
        val totalRetirementSpend = ongoingAnnualExpenses * yearsInRetirement
        val contributedPrincipal = params.financialAssets + maxOf(0.0, ongoingAnnualCashFlow) * t
        val totalGain = maxOf(
            (totalRetirementSpend - contributedPrincipal).coerceAtLeast(0.0),
            (totalRetirementSpend - baseReq).coerceAtLeast(0.0)
        )
        return if (totalRetirementSpend > 0.0) {
            (totalGain / totalRetirementSpend).coerceIn(0.0, 0.95) * 100.0
        } else 0.0
    }

    fun computeBaseRequirement(t: Double, yearsInRetirement: Double): Double {
        if (yearsInRetirement <= 0.0) return 0.0
        return if (t >= deferredStartYears) {
            val exp = (ongoingAnnualExpenses - deferredReduction).coerceAtLeast(0.0)
            annuityPv(exp, r, yearsInRetirement)
        } else {
            val reqFull = annuityPv(ongoingAnnualExpenses, r, yearsInRetirement)
            val remAfterDef = (maxYears - deferredStartYears).coerceAtLeast(0.0)
            val savingsAtDef = if (remAfterDef > 0.0 && deferredReduction > 0.0) {
                annuityPv(deferredReduction, r, remAfterDef)
            } else 0.0
            val discountedSavings = if (abs(r) > 0.0001) {
                savingsAtDef * (1.0 + r).pow(-(deferredStartYears - t))
            } else {
                savingsAtDef
            }
            (reqFull - discountedSavings).coerceAtLeast(0.0)
        }
    }

    fun requiredCapitalAt(t: Double, fixedTaxFactor: Double? = null): Double {
        val yearsInRetirement = (maxYears - t).coerceAtLeast(0.0)
        if (yearsInRetirement <= 0.0) return 0.0

        val baseReq = computeBaseRequirement(t, yearsInRetirement)

        var tempReq = 0.0
        for (temp in params.temporaryExpenses) {
            if (t < temp.durationYears) {
                val remainingYears = minOf(temp.durationYears - t, yearsInRetirement)
                if (remainingYears > 0.0) {
                    tempReq += annuityPv(temp.annualAmount, r, remainingYears)
                }
            }
        }

        val taxFactor = fixedTaxFactor ?: getTaxFactorAt(t, yearsInRetirement, baseReq)

        var deferredAssetDiscounted = 0.0
        for (asset in params.deferredAssets) {
            if (t < asset.afterYears && asset.afterYears < maxYears) {
                val dt = asset.afterYears - t
                deferredAssetDiscounted += if (abs(r) > 0.0001) {
                    asset.amount * (1.0 + r).pow(-dt)
                } else {
                    asset.amount
                }
            }
        }
        return ((baseReq + tempReq) * taxFactor - deferredAssetDiscounted).coerceAtLeast(0.0)
    }

    fun accumulatedCapitalAt(t: Double): Double {
        val baseAcc = if (abs(r) > 0.0001) {
            params.startCapital * (1.0 + r).pow(t) + ongoingAnnualCashFlow * ((1.0 + r).pow(t) - 1.0) / r
        } else {
            params.startCapital + ongoingAnnualCashFlow * t
        }
        val extraAccFromR = if (t > deferredStartYears && deferredReduction > 0.0) {
            val dt = t - deferredStartYears
            if (abs(r) > 0.0001) {
                deferredReduction * ((1.0 + r).pow(dt) - 1.0) / r
            } else {
                deferredReduction * dt
            }
        } else 0.0

        var tempCost = 0.0
        for (temp in params.temporaryExpenses) {
            val tau = minOf(t, temp.durationYears)
            if (tau > 0.0) {
                val fvAtTau = if (abs(r) > 0.0001) {
                    temp.annualAmount * ((1.0 + r).pow(tau) - 1.0) / r
                } else {
                    temp.annualAmount * tau
                }
                val compoundedToT = if (abs(r) > 0.0001) {
                    fvAtTau * (1.0 + r).pow(t - tau)
                } else {
                    fvAtTau
                }
                tempCost += compoundedToT
            }
        }

        var deferredAssetsValue = 0.0
        for (asset in params.deferredAssets) {
            if (t >= asset.afterYears) {
                val dt = t - asset.afterYears
                deferredAssetsValue += if (abs(r) > 0.0001) {
                    asset.amount * (1.0 + r).pow(dt)
                } else {
                    asset.amount
                }
            }
        }
        return baseAcc + extraAccFromR - tempCost + deferredAssetsValue
    }

    fun findYearsToRetirement(): Double {
        if (maxYears <= 0) return 0.0
        if (params.annualExpenses <= 0) return 0.0

        if (accumulatedCapitalAt(0.0) >= requiredCapitalAt(0.0)) {
            return 0.0
        }

        var low = 0.0
        var high = maxYears
        var bestT = maxYears

        for (i in 0 until 60) {
            val mid = (low + high) / 2.0
            val acc = accumulatedCapitalAt(mid)
            val req = requiredCapitalAt(mid)
            if (acc >= req) {
                bestT = mid
                high = mid
            } else {
                low = mid
            }
        }

        return (bestT * 100.0).roundToInt() / 100.0
    }

    fun buildTrajectory(
        precomputedYearsToRetire: Double? = null,
        effectiveTaxRate: Double? = null
    ): List<TrajectoryPoint> {
        if (!params.isAgeValid || maxYears <= 0) {
            return emptyList()
        }
        val totalYears = (params.lifeExpectancy - params.currentAge)
        if (totalYears <= 0) {
            return emptyList()
        }

        val yearsToRetire = precomputedYearsToRetire ?: findYearsToRetirement()
        val capAtRetirement = accumulatedCapitalAt(yearsToRetire)
        val remYearsAtRetire = (maxYears - yearsToRetire).coerceAtLeast(0.0)
        val baseReqAtRetire = computeBaseRequirement(yearsToRetire, remYearsAtRetire)
        val taxFactorAtRetirement = if (effectiveTaxRate != null) {
            val clamped = effectiveTaxRate.coerceIn(0.0, 0.50)
            if (clamped > 0.0 && clamped < 0.99) {
                1.0 / (1.0 - clamped)
            } else {
                1.0
            }
        } else {
            getTaxFactorAt(yearsToRetire, remYearsAtRetire, baseReqAtRetire)
        }

        val list = mutableListOf<TrajectoryPoint>()
        for (i in 0..totalYears) {
            val currentYearOffset = i.toDouble()
            val ageAtYear = params.currentAge + currentYearOffset
            val isRetired = currentYearOffset >= yearsToRetire

            // Использование единого расчета требуемого капитала
            val required = requiredCapitalAt(currentYearOffset, fixedTaxFactor = taxFactorAtRetirement)

            val accumulated = if (!isRetired) {
                accumulatedCapitalAt(currentYearOffset)
            } else {
                val yearsSinceRetirement = currentYearOffset - yearsToRetire
                val baseDraw = if (abs(r) > 0.0001) {
                    ongoingAnnualExpenses * ((1.0 + r).pow(yearsSinceRetirement) - 1.0) / r
                } else {
                    ongoingAnnualExpenses * yearsSinceRetirement
                }
                val savingsDraw = if (currentYearOffset > deferredStartYears && deferredReduction > 0.0) {
                    val tauDef = currentYearOffset - maxOf(yearsToRetire, deferredStartYears)
                    if (tauDef > 0.0) {
                        if (abs(r) > 0.0001) {
                            deferredReduction * ((1.0 + r).pow(tauDef) - 1.0) / r
                        } else {
                            deferredReduction * tauDef
                        }
                    } else 0.0
                } else 0.0

                var tempDraw = 0.0
                for (temp in params.temporaryExpenses) {
                    if (yearsToRetire < temp.durationYears) {
                        val tauRet = minOf(currentYearOffset, temp.durationYears) - yearsToRetire
                        if (tauRet > 0.0) {
                            val fvTau = if (abs(r) > 0.0001) {
                                temp.annualAmount * ((1.0 + r).pow(tauRet) - 1.0) / r
                            } else {
                                temp.annualAmount * tauRet
                            }
                            val comp = if (abs(r) > 0.0001) {
                                fvTau * (1.0 + r).pow(currentYearOffset - minOf(currentYearOffset, temp.durationYears))
                            } else {
                                fvTau
                            }
                            tempDraw += comp
                        }
                    }
                }
                val capCompounded = if (abs(r) > 0.0001) {
                    capAtRetirement * (1.0 + r).pow(yearsSinceRetirement)
                } else {
                    capAtRetirement
                }

                var deferredAssetsInRetirement = 0.0
                for (asset in params.deferredAssets) {
                    if (yearsToRetire < asset.afterYears && currentYearOffset >= asset.afterYears) {
                        val dt = currentYearOffset - asset.afterYears
                        deferredAssetsInRetirement += if (abs(r) > 0.0001) {
                            asset.amount * (1.0 + r).pow(dt)
                        } else {
                            asset.amount
                        }
                    }
                }

                capCompounded - (baseDraw - savingsDraw + tempDraw) * taxFactorAtRetirement + deferredAssetsInRetirement
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
