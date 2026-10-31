package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.data.model.TrajectoryPoint
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow

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
            deferredAssets: List<DeferredAsset> = emptyList(),
            extraMonthlyCashFlow: Double = 0.0,
            today: LocalDate = LocalDate.now()
        ): ProjectionParams {
            val computedAge = FinanceCalculator.calculateAge(input.birthDate, today)
            val isAgeValid = computedAge != null
            val currentAge = computedAge ?: 0
            val startCapital = fireStartCapital(input)
            val cashFlow = input.monthlyIncome - input.monthlyExpenses + extraMonthlyCashFlow
            val realReturnPercent = FinanceCalculator.calculateRealReturnPercent(
                input.nominalReturnPercent,
                input.inflationPercent
            )

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

internal data class TaxInfo(
    val gainShare: Double,
    val effectiveRate: Double,
    val factor: Double
)

class ProjectionEngine(val params: ProjectionParams) {

    data class RetirementResult(val years: Double, val status: FireStatus)

    val maxYears: Double = (params.lifeExpectancy - params.currentAge).toDouble().coerceAtLeast(0.0)
    val r: Double = params.realReturnRate.coerceAtLeast(-0.5)
    private val effectiveTemp: Double = minOf(params.temporaryExpenses.sumOf { it.annualAmount }, params.annualExpenses)
    val ongoingAnnualExpenses: Double = (params.annualExpenses - effectiveTemp).coerceAtLeast(0.0)
    val ongoingAnnualCashFlow: Double = params.annualCashFlow + effectiveTemp
    val deferredReduction: Double = params.deferredAnnualExpenseReduction.coerceAtMost(ongoingAnnualExpenses)
    val deferredStartYears: Double = params.deferredStartYears

    private val liquidatableAssets: Double = params.financialAssets + params.liquidAssets
    /** Если активов нет, считаем, что накопления будут вложены. */
    val finRatio: Double = if (liquidatableAssets > 0.0) {
        (params.financialAssets / liquidatableAssets).coerceIn(0.0, 1.0)
    } else 1.0

    internal fun taxInfo(t: Double, yearsInRetirement: Double, baseReq: Double): TaxInfo {
        val gainShare = if (yearsInRetirement <= 0.0 || finRatio == 0.0) {
            0.0
        } else {
            val totalRetirementSpend = ongoingAnnualExpenses * yearsInRetirement
            val contributedPrincipal = params.financialAssets + maxOf(0.0, ongoingAnnualCashFlow) * t
            val totalGain = maxOf(
                (totalRetirementSpend - contributedPrincipal).coerceAtLeast(0.0),
                (totalRetirementSpend - baseReq).coerceAtLeast(0.0)
            )
            if (totalRetirementSpend > 0.0) {
                (totalGain / totalRetirementSpend).coerceIn(0.0, 0.95)
            } else 0.0
        }

        val effectiveRate = if (
            params.capitalGainsTaxPercent > 0.0 &&
            yearsInRetirement > 0.0
        ) {
            ((params.capitalGainsTaxPercent / 100.0) * gainShare * finRatio).coerceIn(0.0, 0.50)
        } else 0.0
        val factor = if (effectiveRate > 0.0 && effectiveRate < 0.99) {
            1.0 / (1.0 - effectiveRate)
        } else 1.0
        return TaxInfo(gainShare, effectiveRate, factor)
    }

    fun getTaxFactorAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
        return taxInfo(t, yearsInRetirement, baseReq).factor
    }

    fun getEffectiveTaxRateAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
        return taxInfo(t, yearsInRetirement, baseReq).effectiveRate * 100.0
    }

    fun getGainShareAt(t: Double, yearsInRetirement: Double, baseReq: Double): Double {
        return taxInfo(t, yearsInRetirement, baseReq).gainShare * 100.0
    }

    fun computeBaseRequirement(t: Double, yearsInRetirement: Double): Double {
        if (yearsInRetirement <= 0.0) return 0.0
        return if (t >= deferredStartYears) {
            val exp = (ongoingAnnualExpenses - deferredReduction).coerceAtLeast(0.0)
            FinanceCalculator.annuityPv(exp, r, yearsInRetirement)
        } else {
            val reqFull = FinanceCalculator.annuityPv(ongoingAnnualExpenses, r, yearsInRetirement)
            val remAfterDef = (maxYears - deferredStartYears).coerceAtLeast(0.0)
            val savingsAtDef = if (remAfterDef > 0.0 && deferredReduction > 0.0) {
                FinanceCalculator.annuityPv(deferredReduction, r, remAfterDef)
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
                    tempReq += FinanceCalculator.annuityPv(temp.annualAmount, r, remainingYears)
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

    fun findYearsToRetirement(): RetirementResult {
        if (!params.isAgeValid) return RetirementResult(0.0, FireStatus.INVALID_AGE)
        if (params.lifeExpectancy <= params.currentAge) {
            return RetirementResult(0.0, FireStatus.HORIZON_EXHAUSTED)
        }
        if (accumulatedCapitalAt(0.0) >= requiredCapitalAt(0.0)) {
            return RetirementResult(0.0, FireStatus.REACHED_NOW)
        }

        // At maxYears no retirement years remain and requiredCapitalAt() becomes 0,
        // so check the last centiyear that still has a positive retirement horizon.
        val lastCheckYear = (maxYears - 0.01).coerceAtLeast(0.0)
        if (accumulatedCapitalAt(lastCheckYear) < requiredCapitalAt(lastCheckYear)) {
            return RetirementResult(maxYears, FireStatus.UNREACHABLE)
        }

        var low = 0.0
        var high = lastCheckYear

        for (i in 0 until 60) {
            val mid = (low + high) / 2.0
            val acc = accumulatedCapitalAt(mid)
            val req = requiredCapitalAt(mid)
            if (acc >= req) {
                high = mid
            } else {
                low = mid
            }
        }

        val roundedYears = (ceil(high * 100.0 - 1e-10) / 100.0).coerceAtMost(maxYears)
        return RetirementResult(roundedYears, FireStatus.REACHABLE)
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

        val yearsToRetire = precomputedYearsToRetire ?: findYearsToRetirement().years
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
            val required = if (isRetired) {
                requiredCapitalAt(currentYearOffset, fixedTaxFactor = taxFactorAtRetirement)
            } else {
                requiredCapitalAt(currentYearOffset)
            }

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
