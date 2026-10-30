package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.model.AuditIndicatorStatus
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.getAppStrings

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
            appendLine(String.format(strings.auditShareScoreLine, Formatters.formatScore(metrics.scores.totalScore, lang = lang)))
            appendLine(String.format(strings.auditShareTotalCountLine, totalCount))
            appendLine()

            appendLine(String.format(strings.auditSharePositiveHeader, positive.size))
            if (positive.isEmpty()) {
                appendLine(strings.auditSharePositiveEmpty)
            } else {
                positive.forEach { appendLine(it.text) }
            }
            appendLine()

            appendLine(String.format(strings.auditShareNeutralHeader, neutral.size))
            if (neutral.isEmpty()) {
                appendLine(strings.auditShareNeutralEmpty)
            } else {
                neutral.forEach { appendLine(it.text) }
            }
            appendLine()

            appendLine(String.format(strings.auditShareNegativeHeader, negative.size))
            if (negative.isEmpty()) {
                appendLine(strings.auditShareNegativeEmpty)
            } else {
                negative.forEach { appendLine(it.text) }
            }
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(strings.auditShareGeneratedBy)
            appendLine(strings.auditShareDisclaimer)
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
                text = String.format(strings.auditCashFlowPositive, Formatters.formatMoney(metrics.cashFlow, currency, lang))
            )
            metrics.cashFlow == 0.0 -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = strings.auditCashFlowTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(strings.auditCashFlowNeutral, currency)
            )
            else -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = strings.auditCashFlowTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditCashFlowNegative, Formatters.formatMoney(metrics.cashFlow, currency, lang))
            )
        }

        // 2. Норма сбережений
        val savingsRateAnalysis = when {
            metrics.savingsRate > 20.0 -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = strings.auditSavingsRateTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditSavingsRatePositive, Formatters.formatPercent(metrics.savingsRate, lang))
            )
            metrics.savingsRate >= 0.0 -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = strings.auditSavingsRateTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(strings.auditSavingsRateNeutral, Formatters.formatPercent(metrics.savingsRate, lang))
            )
            else -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = strings.auditSavingsRateTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditSavingsRateNegative, Formatters.formatPercent(metrics.savingsRate, lang))
            )
        }

        // 3. Подушка безопасности (сумма)
        val emergencyFundAnalysis = when {
            metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = strings.auditEmergencyFundTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditEmergencyFundPositive, Formatters.formatMoney(metrics.emergencyFund, currency, lang))
            )
            metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = strings.auditEmergencyFundTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(strings.auditEmergencyFundNeutral, Formatters.formatMoney(metrics.emergencyFund, currency, lang))
            )
            else -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = strings.auditEmergencyFundTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditEmergencyFundNegative, Formatters.formatMoney(metrics.emergencyFund, currency, lang))
            )
        }

        // 4. Запас месячных расходов
        val safetyMonthsAnalysis = when {
            metrics.safetyMonths >= 12.0 -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditSafetyMonthsHigh, Formatters.formatRatio(metrics.safetyMonths, lang))
            )
            metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditSafetyMonthsTarget, Formatters.formatRatio(metrics.safetyMonths, lang))
            )
            metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(strings.auditSafetyMonthsNeutral, Formatters.formatRatio(metrics.safetyMonths, lang))
            )
            else -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = strings.auditSafetyMonthsTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditSafetyMonthsNegative, Formatters.formatRatio(metrics.safetyMonths, lang))
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
            metrics.debtRatio <= 0.30 -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(
                    strings.auditDebtRatioConservative,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct, lang)
                )
            )
            metrics.debtRatio <= 0.60 -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(
                    strings.auditDebtRatioModerate,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct, lang)
                )
            )
            metrics.debtRatio <= 1.0 -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(
                    strings.auditDebtRatioHigh,
                    Formatters.formatRatio(metrics.debtRatio, lang),
                    Formatters.formatPercent(debtPct, lang)
                )
            )
            else -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = strings.auditDebtRatioTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(
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
                text = String.format(strings.auditNetWorthStrong, Formatters.formatMoney(metrics.netWorth, currency, lang))
            )
            metrics.netWorth >= 0.0 -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = strings.auditNetWorthTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(strings.auditNetWorthBalanced, Formatters.formatMoney(metrics.netWorth, currency, lang))
            )
            else -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = strings.auditNetWorthTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditNetWorthNegative, Formatters.formatMoney(metrics.netWorth, currency, lang))
            )
        }

        // 7. Бенчмарк Fidelity
        val fidelityAnalysis = if (metrics.fidelityTarget > 0.0) {
            val fidelityPercent = FinancialMetricEvaluator.calculateFidelityPercent(metrics.netWorth, metrics.fidelityTarget)
            val fidelityStatus = FinancialMetricEvaluator.getFidelityStatus(metrics.netWorth, metrics.fidelityTarget)
            val formattedPercent = Formatters.formatPercent(fidelityPercent, lang)
            val formattedTarget = Formatters.formatMoney(metrics.fidelityTarget, currency, lang)
            val formattedNetWorth = Formatters.formatMoney(metrics.netWorth, currency, lang)
            when (fidelityStatus) {
                AuditIndicatorStatus.POSITIVE -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = strings.auditFidelityTitle,
                    status = AuditIndicatorStatus.POSITIVE,
                    text = String.format(strings.auditFidelityPositive, formattedPercent, formattedTarget)
                )
                AuditIndicatorStatus.NEUTRAL -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = strings.auditFidelityTitle,
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = String.format(strings.auditFidelityNeutral, formattedPercent, formattedTarget)
                )
                AuditIndicatorStatus.NEGATIVE -> {
                    if (metrics.netWorth < 0.0) {
                        AuditIndicatorAnalysis(
                            id = "fidelity",
                            title = strings.auditFidelityTitle,
                            status = AuditIndicatorStatus.NEGATIVE,
                            text = String.format(strings.auditFidelityNegativeInsolvent, formattedNetWorth, formattedTarget)
                        )
                    } else {
                        AuditIndicatorAnalysis(
                            id = "fidelity",
                            title = strings.auditFidelityTitle,
                            status = AuditIndicatorStatus.NEGATIVE,
                            text = String.format(strings.auditFidelityNegativeLagging, formattedPercent, formattedTarget)
                        )
                    }
                }
            }
        } else {
            AuditIndicatorAnalysis(
                id = "fidelity",
                title = strings.auditFidelityTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = strings.auditFidelityUnder30
            )
        }

        // 8. Коэффициент финансовой свободы (КФС)
        val cfsDesc = FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, lang)
        val cfsAnalysis = when {
            metrics.financialFreedomRatio >= FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditCfsIndependence, Formatters.formatRatio(metrics.financialFreedomRatio, lang))
            )
            metrics.financialFreedomRatio >= FinancialMetricEvaluator.CFS_THRESHOLD_STABILITY -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditCfsStability, Formatters.formatRatio(metrics.financialFreedomRatio, lang))
            )
            metrics.financialFreedomRatio >= FinancialMetricEvaluator.CFS_POSITIVE_THRESHOLD -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(strings.auditCfsGrowth, Formatters.formatRatio(metrics.financialFreedomRatio, lang), cfsDesc)
            )
            metrics.financialFreedomRatio >= FinancialMetricEvaluator.CFS_NEUTRAL_THRESHOLD -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.NEUTRAL,
                text = String.format(strings.auditCfsInitial, Formatters.formatRatio(metrics.financialFreedomRatio, lang), cfsDesc)
            )
            metrics.financialFreedomRatio < 0.0 -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditCfsNegative, Formatters.formatRatio(metrics.financialFreedomRatio, lang), cfsDesc)
            )
            else -> AuditIndicatorAnalysis(
                id = "cfs",
                title = strings.auditCfsTitle,
                status = AuditIndicatorStatus.NEGATIVE,
                text = String.format(strings.auditCfsBuffer, Formatters.formatRatio(metrics.financialFreedomRatio, lang), cfsDesc)
            )
        }

        // 9. Реальная доходность капитала
        val realReturnAnalysis = when {
            metrics.realReturnPercent > 0.0 -> AuditIndicatorAnalysis(
                id = "real_return",
                title = strings.auditRealReturnTitle,
                status = AuditIndicatorStatus.POSITIVE,
                text = String.format(
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
                text = String.format(
                    strings.auditRealReturnNegative,
                    Formatters.formatPercent(metrics.realReturnPercent, lang),
                    Formatters.formatPercent(inputData.inflationPercent, lang),
                    Formatters.formatPercent(inputData.nominalReturnPercent, lang)
                )
            )
        }

        val allIndicators = listOf(
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
            appendLine(String.format(strings.exportIncomeLine, Formatters.formatMoney(inputData.monthlyIncome, currency, lang)))
            appendLine(String.format(strings.exportExpensesLine, Formatters.formatMoney(inputData.monthlyExpenses, currency, lang)))
            appendLine(String.format(strings.exportLiquidAssetsLine, Formatters.formatMoney(inputData.liquidAssets, currency, lang)))
            appendLine(String.format(strings.exportFinancialAssetsLine, Formatters.formatMoney(inputData.financialAssets, currency, lang)))
            appendLine(String.format(strings.exportPropertyAssetsLine, Formatters.formatMoney(inputData.propertyAssets, currency, lang)))
            appendLine(String.format(strings.exportLiabilitiesLine, Formatters.formatMoney(inputData.liabilities, currency, lang)))
            appendLine(String.format(strings.exportAgeHorizonLine, metrics.currentAge, metrics.lifeExpectancy, metrics.remainingYearsHorizon))
            appendLine(String.format(strings.exportReturnsInflationLine, Formatters.formatPercent(inputData.nominalReturnPercent, lang), Formatters.formatPercent(inputData.inflationPercent, lang)))
            appendLine(String.format(strings.exportTaxLine, Formatters.formatPercent(inputData.capitalGainsTaxPercent, lang)))
            appendLine()
            appendLine(strings.exportSection2Title)
            appendLine(String.format(strings.exportCashFlowLine, Formatters.formatMoney(metrics.cashFlow, currency, lang)))
            appendLine(String.format(strings.exportSavingsRateLine, Formatters.formatPercent(metrics.savingsRate, lang)))
            appendLine(String.format(strings.exportEmergencyFundLine, Formatters.formatMoney(metrics.emergencyFund, currency, lang), Formatters.formatRatio(metrics.safetyMonths, lang)))
            appendLine(String.format(strings.exportDebtRatioLine, Formatters.formatRatio(metrics.debtRatio, lang)))
            appendLine(String.format(strings.exportNetWorthLine, Formatters.formatMoney(metrics.netWorth, currency, lang)))
            appendLine(String.format(strings.exportRealReturnLine, Formatters.formatPercent(metrics.realReturnPercent, lang)))
            appendLine(String.format(strings.exportEffectiveTaxLine, Formatters.formatPercent(metrics.effectiveCapitalGainsTaxRate, lang)))
            appendLine()
            appendLine(strings.exportSection3Title)
            appendLine(String.format(strings.exportScoreTotalLine, Formatters.formatScore(metrics.scores.totalScore, lang)))
            appendLine(String.format(strings.exportScoreSavingsRateLine, metrics.scores.savingsRateScore))
            appendLine(String.format(strings.exportScoreSafetyMonthsLine, metrics.scores.safetyMonthsScore))
            appendLine(String.format(strings.exportScoreDebtRatioLine, metrics.scores.debtRatioScore))
            appendLine(String.format(strings.exportScoreCfsLine, metrics.scores.cfsScore))
            appendLine()
            appendLine(strings.exportSection4Title)
            appendLine(String.format(strings.exportCfsLine, Formatters.formatRatio(metrics.financialFreedomRatio, lang), cfsDesc))
            appendLine(String.format(strings.exportFireTargetCapitalLine, Formatters.formatMoney(metrics.retirementTargetCapital, currency, lang)))
            appendLine(String.format(strings.exportFireTimeToRetirementLine, Formatters.formatRatio(metrics.yearsToRetirement, lang), Formatters.formatRatio(metrics.retirementAge, lang)))
            appendLine(String.format(strings.exportFidelityLine, Formatters.formatMoney(metrics.fidelityTarget, currency, lang), Formatters.formatRatio(metrics.fidelityMultiplier, lang), Formatters.formatPercent(metrics.fidelityPercent, lang)))
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(strings.auditShareGeneratedBy)
            appendLine(strings.auditShareDisclaimer)
        }
    }
}
