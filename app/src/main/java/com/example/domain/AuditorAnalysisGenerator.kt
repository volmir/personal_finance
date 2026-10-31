package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.model.AuditIndicatorStatus
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.getAppStrings
import java.util.Locale
import kotlin.math.roundToInt

typealias AuditIndicatorStatus = com.example.domain.model.AuditIndicatorStatus

data class AuditIndicatorAnalysis(
    val id: String,
    val title: String,
    val status: AuditIndicatorStatus,
    val text: String
)

data class GroupedAuditOpinion(
    val positive: List<AuditIndicatorAnalysis>,
    val neutral: List<AuditIndicatorAnalysis>,
    val negative: List<AuditIndicatorAnalysis>
) {
    val totalCount: Int get() = positive.size + neutral.size + negative.size

    fun toFormattedShareText(
        inputData: FinancialInputData,
        metrics: CalculatedMetrics,
        lang: AppLanguage = AppLanguage.RU
    ): String {
        val strings = getAppStrings(lang)
        return buildString {
            appendLine(strings.auditShareHeaderTitle)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(String.format(Locale.ROOT, strings.auditShareScoreLine, Formatters.formatScore(metrics.scores.totalScore, lang = lang)))
            appendLine(String.format(Locale.ROOT, strings.auditShareTotalCountLine, totalCount))
            appendLine()

            appendLine(String.format(Locale.ROOT, strings.auditSharePositiveHeader, positive.size))
            if (positive.isEmpty()) {
                appendLine(strings.auditSharePositiveEmpty)
            } else {
                positive.forEach { appendLine(it.text) }
            }
            appendLine()

            appendLine(String.format(Locale.ROOT, strings.auditShareNeutralHeader, neutral.size))
            if (neutral.isEmpty()) {
                appendLine(strings.auditShareNeutralEmpty)
            } else {
                neutral.forEach { appendLine(it.text) }
            }
            appendLine()

            appendLine(String.format(Locale.ROOT, strings.auditShareNegativeHeader, negative.size))
            if (negative.isEmpty()) {
                appendLine(strings.auditShareNegativeEmpty)
            } else {
                negative.forEach { appendLine(it.text) }
            }
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(strings.auditShareGeneratedBy)
            appendLine(strings.auditShareDisclaimer)
            appendLine()
            appendLine(strings.fireAssumptionsTitle)
            appendLine(strings.fireAssumptionsBody(
                Formatters.formatPercent(inputData.nominalReturnPercent, lang),
                Formatters.formatPercent(inputData.inflationPercent, lang)
            ))
        }
    }
}

object AuditorAnalysisGenerator {

    fun generate(
        inputData: FinancialInputData,
        metrics: CalculatedMetrics,
        lang: AppLanguage = AppLanguage.RU
    ): GroupedAuditOpinion {
        val currency = inputData.currencySymbol
        val strings = getAppStrings(lang)

        // 1. Денежный поток (Cash Flow)
        val cashFlowAnalysis = when {
            metrics.cashFlow > 0.0 -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = strings.auditCashFlowTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(Locale.ROOT, strings.auditCashFlowPositive, Formatters.formatMoney(metrics.cashFlow, currency, lang))
            )
            metrics.cashFlow == 0.0 -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = strings.auditCashFlowTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(Locale.ROOT, strings.auditCashFlowNeutral, currency)
            )
            else -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = strings.auditCashFlowTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT, strings.auditCashFlowNegative, Formatters.formatMoney(metrics.cashFlow, currency, lang))
            )
        }

        // 2. Норма сбережений
        val savingsRateAnalysis = when {
            metrics.savingsRate > FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = strings.auditSavingsRateTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditSavingsRatePositive(
                    Formatters.formatPercent(metrics.savingsRate, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD)
                )
            )
            metrics.savingsRate > FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = strings.auditSavingsRateTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = strings.auditSavingsRateNeutral(
                    Formatters.formatPercent(metrics.savingsRate, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD),
                    strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD)
                )
            )
            else -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = strings.auditSavingsRateTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT, strings.auditSavingsRateNegative, Formatters.formatPercent(metrics.savingsRate, lang))
            )
        }

        // 3. Подушка безопасности (сумма)
        val emergencyFundAnalysis = when {
            metrics.safetyMonths >= FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = strings.auditEmergencyFundTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(Locale.ROOT, strings.auditEmergencyFundPositive, Formatters.formatMoney(metrics.emergencyFund, currency, lang))
            )
            metrics.safetyMonths >= FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = strings.auditEmergencyFundTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = strings.auditEmergencyFundNeutral(
                    Formatters.formatMoney(metrics.emergencyFund, currency, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD)
                )
            )
            else -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = strings.auditEmergencyFundTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT, strings.auditEmergencyFundNegative, Formatters.formatMoney(metrics.emergencyFund, currency, lang))
            )
        }

        // 4. Запас месячных расходов
        val safetyMonthsAnalysis = when {
            metrics.safetyMonths >= FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditSafetyMonthsHigh(
                    Formatters.formatRatio(metrics.safetyMonths, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT)
                )
            )
            metrics.safetyMonths >= FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditSafetyMonthsTarget(
                    Formatters.formatRatio(metrics.safetyMonths, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD)
                )
            )
            metrics.safetyMonths >= FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = strings.auditSafetyMonthsNeutral(
                    Formatters.formatRatio(metrics.safetyMonths, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD)
                )
            )
            else -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = strings.auditSafetyMonthsNegative(
                    Formatters.formatRatio(metrics.safetyMonths, lang),
                    strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD),
                    strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD)
                )
            )
        }

        // 5. Коэффициент задолженности
        val debtPct = metrics.debtRatio * 100.0
        val debtRatioAnalysis = when {
            metrics.debtRatio == 0.0 -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditDebtRatioZero
            )
            metrics.debtRatio <= FinancialMetricEvaluator.DEBT_RATIO_POSITIVE_THRESHOLD -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(Locale.ROOT,
                    strings.auditDebtRatioConservative,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct, lang)
                )
            )
            metrics.debtRatio <= FinancialMetricEvaluator.DEBT_RATIO_NEUTRAL_THRESHOLD -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(Locale.ROOT,
                    strings.auditDebtRatioModerate,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct, lang)
                )
            )
            metrics.debtRatio <= FinancialMetricEvaluator.DEBT_RATIO_TIER_CRITICAL -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT,
                    strings.auditDebtRatioHigh,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct, lang)
                )
            )
            else -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT,
                    strings.auditDebtRatioCritical,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct - 100.0, lang)
                )
            )
        }

        // 6. Чистый капитал
        val netWorthAnalysis = when {
            metrics.netWorth > 0.0 && (metrics.totalAssets == 0.0 || metrics.netWorth / metrics.totalAssets >= 0.5) -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = strings.auditNetWorthTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(Locale.ROOT, strings.auditNetWorthStrong, Formatters.formatMoney(metrics.netWorth, currency, lang))
            )
            metrics.netWorth >= 0.0 -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = strings.auditNetWorthTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(Locale.ROOT, strings.auditNetWorthBalanced, Formatters.formatMoney(metrics.netWorth, currency, lang))
            )
            else -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = strings.auditNetWorthTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT, strings.auditNetWorthNegative, Formatters.formatMoney(metrics.netWorth, currency, lang))
            )
        }

        // 7. Бенчмарк Fidelity
        val fidelityAnalysis = if (metrics.isFidelityAvailable) {
            val fidelityPercent = FinancialMetricEvaluator.calculateFidelityPercent(metrics.fidelityCapital, metrics.fidelityTarget)
            val fidelityStatus = FinancialMetricEvaluator.getFidelityStatus(metrics.fidelityCapital, metrics.fidelityTarget)
            val formattedPercent = Formatters.formatPercent(fidelityPercent, lang)
            val formattedTarget = Formatters.formatMoney(metrics.fidelityTarget, currency, lang)
            val formattedFidelityCapital = Formatters.formatMoney(metrics.fidelityCapital, currency, lang)
            when (fidelityStatus) {
                AuditIndicatorStatus.POSITIVE -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = strings.auditFidelityTitle,
                    status = AuditIndicatorStatus.POSITIVE,
                    text = String.format(Locale.ROOT, strings.auditFidelityPositive, formattedPercent, formattedTarget)
                )
                AuditIndicatorStatus.NEUTRAL -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = strings.auditFidelityTitle,
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = String.format(Locale.ROOT, strings.auditFidelityNeutral, formattedPercent, formattedTarget)
                )
                AuditIndicatorStatus.NEGATIVE -> {
                    if (metrics.fidelityCapital < 0.0) {
                        AuditIndicatorAnalysis(
                            id = "fidelity",
                            title = strings.auditFidelityTitle,
                            status = AuditIndicatorStatus.NEGATIVE,
                            text = String.format(Locale.ROOT, strings.auditFidelityNegativeInsolvent, formattedFidelityCapital, formattedTarget)
                        )
                    } else {
                        AuditIndicatorAnalysis(
                            id = "fidelity",
                            title = strings.auditFidelityTitle,
                            status = AuditIndicatorStatus.NEGATIVE,
                            text = String.format(Locale.ROOT, strings.auditFidelityNegativeLagging, formattedPercent, formattedTarget)
                        )
                    }
                }
            }
        } else null

        // 8. Коэффициент финансовой свободы (КФС)
        val cfs = metrics.financialFreedomRatio
        val cfsAnalysis = when {
            cfs >= FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditCfsFinanciallyIndependent
            )
            cfs >= FinancialMetricEvaluator.CFS_THRESHOLD_STABILITY &&
                    cfs < FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditCfsCloseToIndependence(
                    (cfs / FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE * 100.0).roundToInt()
                )
            )
            else -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = FinancialMetricEvaluator.getCfsStatus(cfs),
                text = strings.auditCfsPathToIndependence
            )
        }

        // 9. Реальная доходность капитала
        val realReturnAnalysis = when {
            metrics.realReturnPercent > 0.0 -> AuditIndicatorAnalysis(
                id = "real_return",
                title = strings.auditRealReturnTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(Locale.ROOT,
                    strings.auditRealReturnPositive,
                    Formatters.formatPercent(metrics.realReturnPercent, lang),
                    Formatters.formatPercent(inputData.nominalReturnPercent, lang),
                    Formatters.formatPercent(inputData.inflationPercent, lang)
                )
            )
            metrics.realReturnPercent == 0.0 -> AuditIndicatorAnalysis(
                id = "real_return",
                title = strings.auditRealReturnTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = strings.auditRealReturnNeutral
            )
            else -> AuditIndicatorAnalysis(
                id = "real_return",
                title = strings.auditRealReturnTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(Locale.ROOT,
                    strings.auditRealReturnNegative,
                    Formatters.formatPercent(metrics.realReturnPercent, lang),
                    Formatters.formatPercent(inputData.inflationPercent, lang),
                    Formatters.formatPercent(inputData.nominalReturnPercent, lang)
                )
            )
        }

        val allIndicators = listOfNotNull(
            cashFlowAnalysis,
            savingsRateAnalysis,
            emergencyFundAnalysis,
            safetyMonthsAnalysis,
            debtRatioAnalysis,
            netWorthAnalysis,
            fidelityAnalysis,
            cfsAnalysis,
            realReturnAnalysis
        )

        return GroupedAuditOpinion(
            positive = allIndicators.filter { it.status == AuditIndicatorStatus.POSITIVE },
            neutral = allIndicators.filter { it.status == AuditIndicatorStatus.NEUTRAL },
            negative = allIndicators.filter { it.status == AuditIndicatorStatus.NEGATIVE }
        )
    }

    /**
     * Экспорт в кратком текстовом виде 4 ключевых блоков:
     * 1. Исходные данные
     * 2. Рассчитанные показатели
     * 3. Набранные баллы
     * 4. Финансовая свобода, FIRE
     */
    fun generateExportSummary(
        inputData: FinancialInputData,
        metrics: CalculatedMetrics,
        lang: AppLanguage = AppLanguage.RU
    ): String {
        val currency = inputData.currencySymbol
        val strings = getAppStrings(lang)
        val cfsDesc = FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, lang)

        return buildString {
            appendLine(strings.exportHeaderTitle)
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(strings.exportSection1Title)
            appendLine(String.format(Locale.ROOT, strings.exportIncomeLine, Formatters.formatMoney(inputData.monthlyIncome, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportExpensesLine, Formatters.formatMoney(inputData.monthlyExpenses, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportLiquidAssetsLine, Formatters.formatMoney(inputData.liquidAssets, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportFinancialAssetsLine, Formatters.formatMoney(inputData.financialAssets, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportPropertyAssetsLine, Formatters.formatMoney(inputData.propertyAssets, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportLiabilitiesLine, Formatters.formatMoney(inputData.liabilities, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportAgeHorizonLine, metrics.currentAge, metrics.lifeExpectancy, metrics.remainingYearsHorizon))
            appendLine(String.format(Locale.ROOT, strings.exportReturnsInflationLine, Formatters.formatPercent(inputData.nominalReturnPercent, lang), Formatters.formatPercent(inputData.inflationPercent, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportTaxLine, Formatters.formatPercent(inputData.capitalGainsTaxPercent, lang)))
            appendLine()
            appendLine(strings.exportSection2Title)
            appendLine(String.format(Locale.ROOT, strings.exportCashFlowLine, Formatters.formatMoney(metrics.cashFlow, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportSavingsRateLine, Formatters.formatPercent(metrics.savingsRate, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportEmergencyFundLine, Formatters.formatMoney(metrics.emergencyFund, currency, lang), Formatters.formatRatio(metrics.safetyMonths, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportDebtRatioLine, Formatters.formatRatio(metrics.debtRatio, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportNetWorthLine, Formatters.formatMoney(metrics.netWorth, currency, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportRealReturnLine, Formatters.formatPercent(metrics.realReturnPercent, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportEffectiveTaxLine, Formatters.formatPercent(metrics.effectiveCapitalGainsTaxRate, lang)))
            appendLine()
            appendLine(strings.exportSection3Title)
            appendLine(String.format(Locale.ROOT, strings.exportScoreTotalLine, Formatters.formatScore(metrics.scores.totalScore, lang)))
            appendLine(String.format(Locale.ROOT, strings.exportScoreSavingsRateLine, metrics.scores.savingsRateScore))
            appendLine(String.format(Locale.ROOT, strings.exportScoreSafetyMonthsLine, metrics.scores.safetyMonthsScore))
            appendLine(String.format(Locale.ROOT, strings.exportScoreDebtRatioLine, metrics.scores.debtRatioScore))
            appendLine(String.format(Locale.ROOT, strings.exportScoreCfsLine, metrics.scores.cfsScore))
            appendLine()
            appendLine(strings.exportSection4Title)
            appendLine(String.format(Locale.ROOT, strings.exportCfsLine, Formatters.formatRatio(metrics.financialFreedomRatio, lang), cfsDesc))
            appendLine(String.format(Locale.ROOT, strings.exportFireTargetCapitalLine, Formatters.formatMoney(metrics.retirementTargetCapital, currency, lang)))
            appendLine(String.format(
                Locale.ROOT,
                strings.exportFireTimeToRetirementLine,
                Formatters.formatFireYearsToRetirement(metrics.yearsToRetirement, metrics.fireStatus, lang),
                Formatters.formatFireRetirementAge(metrics.retirementAge, metrics.fireStatus, lang)
            ))
            val fidelityValue = if (metrics.isFidelityAvailable) Formatters.formatPercent(metrics.fidelityPercent, lang) else strings.metricUnavailableValue
            val fidelityTarget = if (metrics.isFidelityAvailable) Formatters.formatMoney(metrics.fidelityTarget, currency, lang) else strings.metricUnavailableValue
            val fidelityMultiplier = if (metrics.isFidelityAvailable) Formatters.formatRatio(metrics.fidelityMultiplier, lang) else strings.metricUnavailableValue
            appendLine(String.format(Locale.ROOT, strings.exportFidelityLine, fidelityTarget, fidelityMultiplier, fidelityValue))
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(strings.auditShareGeneratedBy)
            appendLine(strings.auditShareDisclaimer)
            appendLine()
            appendLine(strings.fireAssumptionsTitle)
            appendLine(strings.fireAssumptionsBody(
                Formatters.formatPercent(inputData.nominalReturnPercent, lang),
                Formatters.formatPercent(inputData.inflationPercent, lang)
            ))
        }
    }
}
