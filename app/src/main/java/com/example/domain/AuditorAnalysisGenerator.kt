package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.util.Formatters
import com.example.util.localization.AppLanguage

enum class AuditIndicatorStatus {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
}

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
        val isEn = lang == AppLanguage.EN
        return buildString {
            if (isEn) {
                appendLine("📋 AUDITOR OPINION ACROSS ALL INDICATORS")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("⭐️ Overall score: ${Formatters.formatScore(metrics.scores.totalScore, lang = lang)} of 10 points")
                appendLine("Total indicators analyzed: $totalCount")
                appendLine()

                appendLine("🟢 POSITIVE INDICATORS (${positive.size}):")
                if (positive.isEmpty()) {
                    appendLine("• No positive indicators identified")
                } else {
                    positive.forEach { appendLine(it.text) }
                }
                appendLine()

                appendLine("🟡 NEUTRAL INDICATORS (${neutral.size}):")
                if (neutral.isEmpty()) {
                    appendLine("• No neutral indicators")
                } else {
                    neutral.forEach { appendLine(it.text) }
                }
                appendLine()

                appendLine("🔴 NEGATIVE INDICATORS (${negative.size}):")
                if (negative.isEmpty()) {
                    appendLine("• No critical or negative factors detected 🎉")
                } else {
                    negative.forEach { appendLine(it.text) }
                }
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("💡 Generated in \"Personal Finance\" app")
            } else {
                appendLine("📋 АУДИТОРСКОЕ ЗАКЛЮЧЕНИЕ ПО ВСЕМ ПОКАЗАТЕЛЯМ")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("⭐️ Итоговая оценка: ${Formatters.formatScore(metrics.scores.totalScore, lang = lang)} из 10 баллов")
                appendLine("Всего показателей проанализировано: $totalCount")
                appendLine()

                appendLine("🟢 ПОЗИТИВНЫЕ ПОКАЗАТЕЛИ (${positive.size}):")
                if (positive.isEmpty()) {
                    appendLine("• Нет позитивных показателей")
                } else {
                    positive.forEach { appendLine(it.text) }
                }
                appendLine()

                appendLine("🟡 НЕЙТРАЛЬНЫЕ ПОКАЗАТЕЛИ (${neutral.size}):")
                if (neutral.isEmpty()) {
                    appendLine("• Нет нейтральных показателей")
                } else {
                    neutral.forEach { appendLine(it.text) }
                }
                appendLine()

                appendLine("🔴 НЕГАТИВНЫЕ ПОКАЗАТЕЛИ (${negative.size}):")
                if (negative.isEmpty()) {
                    appendLine("• Критических и негативных факторов не выявлено 🎉")
                } else {
                    negative.forEach { appendLine(it.text) }
                }
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("💡 Сформировано в приложении «Личные финансы»")
            }
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
        val isEn = lang == AppLanguage.EN

        // 1. Денежный поток (Cash Flow)
        val cashFlowAnalysis = if (isEn) {
            when {
                metrics.cashFlow > 0.0 -> AuditIndicatorAnalysis(
                    id = "cash_flow",
                    title = "Cash Flow",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Cash Flow (+${Formatters.formatMoney(metrics.cashFlow, currency, lang)}/mo.): Budget surplus. Your income exceeds expenses, generating a regular investment resource for wealth accumulation."
                )
                metrics.cashFlow == 0.0 -> AuditIndicatorAnalysis(
                    id = "cash_flow",
                    title = "Cash Flow",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Cash Flow (0 $currency/mo.): Balanced budget (income equals expenses). You do not accumulate debt, but lack a surplus to build capital."
                )
                else -> AuditIndicatorAnalysis(
                    id = "cash_flow",
                    title = "Cash Flow",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 Cash Flow (${Formatters.formatMoney(metrics.cashFlow, currency, lang)}/mo.): Budget deficit! Current expenses exceed income, depleting reserves or raising debt."
                )
            }
        } else {
            when {
                metrics.cashFlow > 0.0 -> AuditIndicatorAnalysis(
                    id = "cash_flow",
                    title = "Денежный поток",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Денежный поток (Cash Flow: +${Formatters.formatMoney(metrics.cashFlow, currency, lang)}/мес.): Профицит бюджета. Ваши доходы превышают расходы, формируя регулярный инвестиционный ресурс для приумножения капитала."
                )
                metrics.cashFlow == 0.0 -> AuditIndicatorAnalysis(
                    id = "cash_flow",
                    title = "Денежный поток",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Денежный поток (Cash Flow: 0 $currency/мес.): Нулевой баланс бюджета (доходы равны расходам). Вы не накапливаете долги, однако свободный ресурс для создания и приумножения капитала отсутствует."
                )
                else -> AuditIndicatorAnalysis(
                    id = "cash_flow",
                    title = "Денежный поток",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 Денежный поток (Cash Flow: ${Formatters.formatMoney(metrics.cashFlow, currency, lang)}/мес.): Дефицит бюджета! Текущие расходы превышают доходы, что ведет к быстрому истощению резервов или росту долговых обязательств."
                )
            }
        }

        // 2. Норма сбережений
        val savingsRateAnalysis = if (isEn) {
            when {
                metrics.savingsRate >= 35.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Savings Rate",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Savings Capacity (Savings Rate: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Outstanding level! High savings rate allows exponential wealth compounding."
                )
                metrics.savingsRate >= 15.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Savings Rate",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Savings Capacity (Savings Rate: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Solid working pace (within 15–20% standard). Provides steady asset accumulation."
                )
                metrics.savingsRate > 0.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Savings Rate",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Savings Capacity (Savings Rate: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Low savings level (under 15%). Wealth formation progresses slowly; expense optimization recommended."
                )
                metrics.savingsRate == 0.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Savings Rate",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Savings Capacity: Zero free cash flow (income equals expenses). Capital is stagnant and reserves are not replenished."
                )
                else -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Savings Rate",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 Savings Capacity (Savings Rate: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Negative cash flow (budget deficit)! Expenses exceed income, depleting assets and increasing debt."
                )
            }
        } else {
            when {
                metrics.savingsRate >= 35.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Норма сбережений",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Превосходный уровень! Высокая норма инвестирования денежного потока позволяет экспоненциально наращивать собственный капитал."
                )
                metrics.savingsRate >= 15.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Норма сбережений",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Здоровый рабочий темп (в рамках стандарта 15–20%). Обеспечивает стабильное регулярное пополнение активов."
                )
                metrics.savingsRate > 0.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Норма сбережений",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Низкий уровень сбережений (менее 15%). Формирование капитала идет медленно, рекомендуется оптимизировать структуру расходов."
                )
                metrics.savingsRate == 0.0 -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Норма сбережений",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Способность к накоплению: Нулевой свободный денежный поток (доходы равны расходам). Капитал не прирастает, резервы не пополняются."
                )
                else -> AuditIndicatorAnalysis(
                    id = "savings_rate",
                    title = "Норма сбережений",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate, lang)}): Отрицательный денежный поток (дефицит бюджета)! Расходы превышают доходы, что ведет к быстрому истощению активов и росту долгов."
                )
            }
        }

        // 3. Подушка безопасности (сумма)
        val emergencyFundAnalysis = if (isEn) {
            when {
                metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                    id = "emergency_fund",
                    title = "Emergency Fund",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Emergency Fund (Reserve Volume: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)}): Liquid reserve is fully sufficient to cover unexpected emergencies and crises without borrowing."
                )
                metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                    id = "emergency_fund",
                    title = "Emergency Fund",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Emergency Fund (Reserve Volume: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)}): Baseline minimal reserve established. Recommended to gradually expand to 6 months of living expenses."
                )
                else -> AuditIndicatorAnalysis(
                    id = "emergency_fund",
                    title = "Emergency Fund",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Emergency Fund (Reserve Volume: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)}): Critically low reserve. High vulnerability to unforeseen costs and forced borrowing."
                )
            }
        } else {
            when {
                metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                    id = "emergency_fund",
                    title = "Подушка безопасности",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Подушка безопасности (Объем резерва: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)}): Ликвидный резерв полностью достаточен для покрытия непредвиденных кризисов и экстренных ситуаций без обращения к кредитам."
                )
                metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                    id = "emergency_fund",
                    title = "Подушка безопасности",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Подушка безопасности (Объем резерва: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)}): Сформирован базовый минимальный резерв. Рекомендуется постепенно довести его до комфортных 6 месяцев расходов."
                )
                else -> AuditIndicatorAnalysis(
                    id = "emergency_fund",
                    title = "Подушка безопасности",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Подушка безопасности (Объем резерва: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)}): Критически низкий объем резерва. При непредвиденных тратах высок риск кассового разрыва и необходимости брать кредиты."
                )
            }
        }

        // 4. Запас месячных расходов
        val safetyMonthsAnalysis = if (isEn) {
            when {
                metrics.safetyMonths >= 12.0 -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Emergency Buffer (Months)",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Financial Cushion (Emergency Buffer: ${Formatters.formatRatio(metrics.safetyMonths, lang)} mo.): Fully formed with ample margin. Income loss risks are mitigated. Excess liquidity above 12 months should be deployed into productive investments."
                )
                metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Emergency Buffer (Months)",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Financial Cushion (Emergency Buffer: ${Formatters.formatRatio(metrics.safetyMonths, lang)} mo.): Target 6-month norm achieved. Baseline family financial security is assured."
                )
                metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Emergency Buffer (Months)",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Financial Cushion (Emergency Buffer: ${Formatters.formatRatio(metrics.safetyMonths, lang)} mo.): Minimal baseline reserve created; recommended to expand to 6 months for better crisis resilience."
                )
                else -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Emergency Buffer (Months)",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Financial Cushion (Emergency Buffer: ${Formatters.formatRatio(metrics.safetyMonths, lang)} mo.): Critically insufficient reserve (under 3 months). Cash crunch risks creating debt traps. Priority: build a 6-month reserve urgently."
                )
            }
        } else {
            when {
                metrics.safetyMonths >= 12.0 -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Запас месячных расходов",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths, lang)} мес.): Полностью сформирована с большим запасом. Риски потери дохода нивелированы. Избыточную ликвидность сверх годового резерва целесообразно направить в доходные инвестиционные инструменты."
                )
                metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Запас месячных расходов",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths, lang)} мес.): Целевой норматив в 6 месяцев расходов успешно достигнут. Базовая финансовая безопасность семьи обеспечена."
                )
                metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Запас месячных расходов",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths, lang)} мес.): Сформирован минимальный базовый резерв, однако рекомендуется постепенно нарастить его до комфортных 6 месяцев расходов для защиты от непредвиденных кризисов."
                )
                else -> AuditIndicatorAnalysis(
                    id = "safety_months",
                    title = "Запас месячных расходов",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths, lang)} мес.): Критически недостаточный резерв (менее 3 месяцев). Любой кассовый разрыв или непредвиденные траты создадут угрозу долговой ямы. Приоритетная задача — экстренно довести ликвидный резерв минимум до 6 месяцев расходов."
                )
            }
        }

        // 5. Коэффициент задолженности
        val debtPct = metrics.debtRatio * 100.0
        val debtRatioAnalysis = if (isEn) {
            when {
                metrics.debtRatio == 0.0 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Debt Ratio",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Debt Load (Debt Ratio: 0.00): Absolute financial autonomy — no debts or loans, 100% of assets are owned outright."
                )
                metrics.debtRatio <= 0.30 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Debt Ratio",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Debt Load (Debt Ratio: ${Formatters.formatRatio(metrics.debtRatio, lang)}, debt ${Formatters.formatPercent(debtPct, lang)} of assets): Conservative and safe leverage. Obligations are fully backed by assets; credit risk is minimal."
                )
                metrics.debtRatio <= 0.60 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Debt Ratio",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Debt Load (Debt Ratio: ${Formatters.formatRatio(metrics.debtRatio, lang)}, debt ${Formatters.formatPercent(debtPct, lang)} of assets): Moderate borrowing level. Debt is controlled but requires discipline during major purchases."
                )
                metrics.debtRatio <= 1.0 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Debt Ratio",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Warning: High Debt Load (Debt Ratio: ${Formatters.formatRatio(metrics.debtRatio, lang)}, debt ${Formatters.formatPercent(debtPct, lang)} of assets)! Heavy reliance on creditors. Refinancing and debt repayment advised."
                )
                else -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Debt Ratio",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 CRITICAL RISK: Negative Equity Margin (Debt Ratio: ${Formatters.formatRatio(metrics.debtRatio, lang)}, debts exceed assets by ${Formatters.formatPercent(debtPct - 100.0, lang)})! Obligations exceed total asset value. Emergency restructuring required."
                )
            }
        } else {
            when {
                metrics.debtRatio == 0.0 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Коэффициент задолженности",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Долговая нагрузка (Коэффициент задолженности: 0,00): Абсолютная финансовая автономность — долги и кредиты отсутствуют, все активы принадлежат вам на 100%."
                )
                metrics.debtRatio <= 0.30 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Коэффициент задолженности",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Долговая нагрузка (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio, lang)}, долг ${Formatters.formatPercent(debtPct, lang)} от активов): Кредитное плечо консервативное и безопасное. Обязательства полностью обеспечены ликвидным имуществом, кредитный риск минимален."
                )
                metrics.debtRatio <= 0.60 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Коэффициент задолженности",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Долговая нагрузка (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio, lang)}, долг ${Formatters.formatPercent(debtPct, lang)} от активов): Умеренный уровень заемного финансирования. Долговая нагрузка контролируема, но требует внимания при планировании крупных покупок и регулярных платежей."
                )
                metrics.debtRatio <= 1.0 -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Коэффициент задолженности",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Внимание: Высокая закредитованность (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio, lang)}, долг ${Formatters.formatPercent(debtPct, lang)} от активов)! Значительная зависимость от внешних кредиторов. Рекомендуется рефинансировать займы, сократить долги и воздержаться от новых обязательств."
                )
                else -> AuditIndicatorAnalysis(
                    id = "debt_ratio",
                    title = "Коэффициент задолженности",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 КРИТИЧЕСКИЙ РИСК: Дефицит капитала (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio, lang)}, долги превышают активы на ${Formatters.formatPercent(debtPct - 100.0, lang)})! Объем финансовых обязательств превышает стоимость всего вашего имущества. Необходима экстренная реструктуризация долгов и финансовое оздоровление."
                )
            }
        }

        // 6. Чистый капитал
        val netWorthAnalysis = if (isEn) {
            when {
                metrics.netWorth > 0.0 && (metrics.totalAssets == 0.0 || metrics.netWorth / metrics.totalAssets >= 0.5) -> AuditIndicatorAnalysis(
                    id = "net_worth",
                    title = "Net Worth",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Net Worth (Own Capital: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}): Strong positive balance. Aggregate assets comfortably exceed financial liabilities, providing a solid foundation."
                )
                metrics.netWorth >= 0.0 -> AuditIndicatorAnalysis(
                    id = "net_worth",
                    title = "Net Worth",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Net Worth (Own Capital: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}): Assets and liabilities are closely matched. Value is positive, but significant assets are encumbered by debts."
                )
                else -> AuditIndicatorAnalysis(
                    id = "net_worth",
                    title = "Net Worth",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 Net Worth (Negative Balance: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}): Negative net worth (technical insolvency). Total liabilities exceed all possessed assets."
                )
            }
        } else {
            when {
                metrics.netWorth > 0.0 && (metrics.totalAssets == 0.0 || metrics.netWorth / metrics.totalAssets >= 0.5) -> AuditIndicatorAnalysis(
                    id = "net_worth",
                    title = "Чистый капитал",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Чистый капитал (Собственные средства: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}): Положительный чистый баланс. Ваши совокупные активы уверенно превышают финансовые обязательства, формируя прочный финансовый фундамент."
                )
                metrics.netWorth >= 0.0 -> AuditIndicatorAnalysis(
                    id = "net_worth",
                    title = "Чистый капитал",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Чистый капитал (Собственные средства: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}): Активы и обязательства находятся в близком балансе. Ваша финансовая стоимость положительна, но существенная часть активов обременена долгами."
                )
                else -> AuditIndicatorAnalysis(
                    id = "net_worth",
                    title = "Чистый капитал",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "🚨 Чистый капитал (Отрицательный баланс: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}): Отрицательный чистый капитал (техническая неплатежеспособность). Обязательства превышают стоимость всего имеющегося имущества."
                )
            }
        }

        // 7. Бенчмарк Fidelity
        val fidelityAnalysis = if (metrics.fidelityTarget > 0.0) {
            val fidelityRatio = metrics.totalAssets / metrics.fidelityTarget
            if (isEn) {
                when {
                    fidelityRatio >= 1.0 -> AuditIndicatorAnalysis(
                        id = "fidelity",
                        title = "Fidelity Benchmark",
                        status = AuditIndicatorStatus.POSITIVE,
                        text = "✅ Age Capital Milestone (Fidelity Benchmark: ${Formatters.formatPercent(fidelityRatio * 100.0, lang)} of goal): Capital exceeds target milestone for your age (${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)}). Excellent long-term trajectory."
                    )
                    fidelityRatio >= 0.7 -> AuditIndicatorAnalysis(
                        id = "fidelity",
                        title = "Fidelity Benchmark",
                        status = AuditIndicatorStatus.NEUTRAL,
                        text = "ℹ️ Age Capital Milestone (Fidelity Benchmark: ${Formatters.formatPercent(fidelityRatio * 100.0, lang)} of goal): Capital matches recommended age savings guidelines (${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)})."
                    )
                    else -> AuditIndicatorAnalysis(
                        id = "fidelity",
                        title = "Fidelity Benchmark",
                        status = AuditIndicatorStatus.NEGATIVE,
                        text = "⚠️ Age Capital Milestone (Fidelity Benchmark: ${Formatters.formatPercent(fidelityRatio * 100.0, lang)} of goal): Lagging behind recommended age benchmark (${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)}). Accelerating savings pace advised."
                    )
                }
            } else {
                when {
                    fidelityRatio >= 1.0 -> AuditIndicatorAnalysis(
                        id = "fidelity",
                        title = "Бенчмарк Fidelity",
                        status = AuditIndicatorStatus.POSITIVE,
                        text = "✅ Возрастной ориентир капитала (Бенчмарк Fidelity: ${Formatters.formatPercent(fidelityRatio * 100.0, lang)} от цели): Капитал опережает целевой норматив для вашего возраста (${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)}). Отличный задел для долгосрочного благосостояния."
                    )
                    fidelityRatio >= 0.7 -> AuditIndicatorAnalysis(
                        id = "fidelity",
                        title = "Бенчмарк Fidelity",
                        status = AuditIndicatorStatus.NEUTRAL,
                        text = "ℹ️ Возрастной ориентир капитала (Бенчмарк Fidelity: ${Formatters.formatPercent(fidelityRatio * 100.0, lang)} от цели): Капитал соответствует рекомендуемым возрастным ориентирам накоплений (${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)})."
                    )
                    else -> AuditIndicatorAnalysis(
                        id = "fidelity",
                        title = "Бенчмарк Fidelity",
                        status = AuditIndicatorStatus.NEGATIVE,
                        text = "⚠️ Возрастной ориентир капитала (Бенчмарк Fidelity: ${Formatters.formatPercent(fidelityRatio * 100.0, lang)} от цели): Отставание от рекомендуемой возрастной планки капитала (${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)}). Желательно ускорить темп капитализации."
                    )
                }
            }
        } else {
            if (isEn) {
                AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = "Fidelity Benchmark",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Age Capital Milestone (Fidelity Benchmark): Under age 30, the primary focus is laying down early savings foundations. Fidelity multiplier milestones begin at age 30 (1x annual salary)."
                )
            } else {
                AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = "Бенчмарк Fidelity",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Возрастной ориентир капитала (Бенчмарк Fidelity): В возрасте до 30 лет основной задачей является закладка фундамента накоплений. Целевые множители Fidelity начинаются с 30 лет (1x годовой доход)."
                )
            }
        }

        // 8. Коэффициент финансовой свободы (КФС)
        val cfsDesc = FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, lang)
        val cfsAnalysis = if (isEn) {
            when {
                metrics.financialFreedomRatio >= 30.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Financial Freedom Ratio",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "🎉 Financial Freedom (CFS: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): You have achieved complete financial independence (FIRE)! Accumulated wealth can support your current living standards indefinitely."
                )
                metrics.financialFreedomRatio >= 15.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Financial Freedom Ratio",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Financial Freedom (CFS: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): High degree of autonomy. Capital yields substantial passive income covering most necessary living expenses."
                )
                metrics.financialFreedomRatio >= 5.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Financial Freedom Ratio",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Financial Freedom (CFS: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Solid capital compounding phase ($cfsDesc). Investment returns yield noticeable impact. Next target: CFS > 15."
                )
                metrics.financialFreedomRatio >= 2.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Financial Freedom Ratio",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Financial Freedom (CFS: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Initial stage of investment capital ($cfsDesc). Primary focus on disciplined regular savings."
                )
                else -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Financial Freedom Ratio",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "📈 Financial Freedom (CFS: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Early stage of capital accumulation ($cfsDesc). Current volume is insufficient for financial autonomy; disciplined saving required."
                )
            }
        } else {
            when {
                metrics.financialFreedomRatio >= 30.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Коэффициент финансовой свободы",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "🎉 Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Вы достигли полной финансовой независимости (FIRE)! Накопленные активы способны пожизненно обеспечивать текущий уровень жизни."
                )
                metrics.financialFreedomRatio >= 15.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Коэффициент финансовой свободы",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Высокая степень автономии. Капитал генерирует существенный пассивный доход, закрывающий большую часть обязательных расходов."
                )
                metrics.financialFreedomRatio >= 5.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Коэффициент финансовой свободы",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Уверенная фаза роста капитала ($cfsDesc). Доходы от капитала начинают приносить ощутимый результат. Следующая стратегическая цель — рубеж КФС > 15."
                )
                metrics.financialFreedomRatio >= 2.0 -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Коэффициент финансовой свободы",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Начальный этап формирования инвестиционного капитала ($cfsDesc). Основной упор на дисциплинированные регулярные сбережения."
                )
                else -> AuditIndicatorAnalysis(
                    id = "cfs",
                    title = "Коэффициент финансовой свободы",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "📈 Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)}): Начальный этап формирования капитала ($cfsDesc). Накопленного объема недостаточно для финансовой автономии, требуется дисциплинированное сбережение."
                )
            }
        }

        // 9. Реальная доходность капитала
        val realReturnAnalysis = if (isEn) {
            when {
                metrics.realReturnPercent > 0.0 -> AuditIndicatorAnalysis(
                    id = "real_return",
                    title = "Real Return on Capital",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Real Return on Capital (+${Formatters.formatPercent(metrics.realReturnPercent, lang)}): Asset yield outpaces inflation (${Formatters.formatPercent(inputData.nominalReturnPercent, lang)} vs ${Formatters.formatPercent(inputData.inflationPercent, lang)}). Capital is protected against erosion and expands purchasing power."
                )
                metrics.realReturnPercent == 0.0 -> AuditIndicatorAnalysis(
                    id = "real_return",
                    title = "Real Return on Capital",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Real Return on Capital (0.0%): Yield precisely offsets inflation. Capital preserves nominal purchasing power, but produces no real wealth expansion."
                )
                else -> AuditIndicatorAnalysis(
                    id = "real_return",
                    title = "Real Return on Capital",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Real Return on Capital (${Formatters.formatPercent(metrics.realReturnPercent, lang)}): Inflation (${Formatters.formatPercent(inputData.inflationPercent, lang)}) exceeds capital returns (${Formatters.formatPercent(inputData.nominalReturnPercent, lang)}). Hidden depreciation of savings and loss of purchasing power occurring."
                )
            }
        } else {
            when {
                metrics.realReturnPercent > 0.0 -> AuditIndicatorAnalysis(
                    id = "real_return",
                    title = "Реальная доходность капитала",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Реальная доходность капитала (+${Formatters.formatPercent(metrics.realReturnPercent, lang)}): Доходность активов опережает инфляцию (${Formatters.formatPercent(inputData.nominalReturnPercent, lang)} против ${Formatters.formatPercent(inputData.inflationPercent, lang)}). Капитал защищен от обесценивания и наращивает реальную покупательную способность."
                )
                metrics.realReturnPercent == 0.0 -> AuditIndicatorAnalysis(
                    id = "real_return",
                    title = "Реальная доходность капитала",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Реальная доходность капитала (0,0%): Доходность в точности компенсирует инфляцию. Капитал сохраняет номинальную покупательную способность, но не создает реального прироста богатства."
                )
                else -> AuditIndicatorAnalysis(
                    id = "real_return",
                    title = "Реальная доходность капитала",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Реальная доходность капитала (${Formatters.formatPercent(metrics.realReturnPercent, lang)}): Инфляция (${Formatters.formatPercent(inputData.inflationPercent, lang)}) превышает доходность капитала (${Formatters.formatPercent(inputData.nominalReturnPercent, lang)}). Происходит скрытое обесценивание накоплений и потеря покупательной способности."
                )
            }
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
        val isEn = lang == AppLanguage.EN
        val cfsDesc = FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, lang)

        return buildString {
            if (isEn) {
                appendLine("📊 FINANCIAL REPORT — PERSONAL FINANCE")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("1. Input Data:")
                appendLine("• Income: ${Formatters.formatMoney(inputData.monthlyIncome, currency, lang)}/mo.")
                appendLine("• Expenses: ${Formatters.formatMoney(inputData.monthlyExpenses, currency, lang)}/mo.")
                appendLine("• Liquid assets: ${Formatters.formatMoney(inputData.liquidAssets, currency, lang)}")
                appendLine("• Financial assets: ${Formatters.formatMoney(inputData.financialAssets, currency, lang)}")
                appendLine("• Real estate / property: ${Formatters.formatMoney(inputData.propertyAssets, currency, lang)}")
                appendLine("• Liabilities (debts): ${Formatters.formatMoney(inputData.liabilities, currency, lang)}")
                appendLine("• Age: ${metrics.currentAge} yrs (life expectancy: ${metrics.lifeExpectancy} yrs, horizon: ${metrics.remainingYearsHorizon} yrs)")
                appendLine("• Return: ${Formatters.formatPercent(inputData.nominalReturnPercent, lang)} | Inflation: ${Formatters.formatPercent(inputData.inflationPercent, lang)}")
                appendLine()
                appendLine("2. Calculated Metrics:")
                appendLine("• Cash flow: ${Formatters.formatMoney(metrics.cashFlow, currency, lang)}/mo.")
                appendLine("• Savings rate: ${Formatters.formatPercent(metrics.savingsRate, lang)}")
                appendLine("• Emergency fund: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)} (${Formatters.formatRatio(metrics.safetyMonths, lang)} mo.)")
                appendLine("• Debt ratio: ${Formatters.formatRatio(metrics.debtRatio, lang)}")
                appendLine("• Net worth: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}")
                appendLine("• Real return: ${Formatters.formatPercent(metrics.realReturnPercent, lang)}")
                appendLine()
                appendLine("3. Score Summary:")
                appendLine("• Overall score: ${Formatters.formatScore(metrics.scores.totalScore, lang)} of 10")
                appendLine("• Savings rate: ${metrics.scores.savingsRateScore} / 10")
                appendLine("• Emergency buffer: ${metrics.scores.safetyMonthsScore} / 10")
                appendLine("• Debt load: ${metrics.scores.debtRatioScore} / 10")
                appendLine("• Financial freedom: ${metrics.scores.cfsScore} / 10")
                appendLine()
                appendLine("4. Financial Independence, FIRE:")
                appendLine("• CFS: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)} ($cfsDesc)")
                appendLine("• Target FIRE capital: ${Formatters.formatMoney(metrics.retirementTargetCapital, currency, lang)}")
                appendLine("• Time to retirement: ${Formatters.formatRatio(metrics.yearsToRetirement, lang)} yrs (at age ${Formatters.formatRatio(metrics.retirementAge, lang)} yrs)")
                appendLine("• Fidelity benchmark: ${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)} (${Formatters.formatRatio(metrics.fidelityMultiplier, lang)}x annual income)")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("💡 Generated in \"Personal Finance\" app")
            } else {
                appendLine("📊 ФИНАНСОВЫЙ ОТЧЕТ — ЛИЧНЫЕ ФИНАНСЫ")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("1. Исходные данные:")
                appendLine("• Доход: ${Formatters.formatMoney(inputData.monthlyIncome, currency, lang)}/мес.")
                appendLine("• Расходы: ${Formatters.formatMoney(inputData.monthlyExpenses, currency, lang)}/мес.")
                appendLine("• Ликвидные активы: ${Formatters.formatMoney(inputData.liquidAssets, currency, lang)}")
                appendLine("• Финансовые активы: ${Formatters.formatMoney(inputData.financialAssets, currency, lang)}")
                appendLine("• Недвижимость / имущество: ${Formatters.formatMoney(inputData.propertyAssets, currency, lang)}")
                appendLine("• Пассивы (долги): ${Formatters.formatMoney(inputData.liabilities, currency, lang)}")
                appendLine("• Возраст: ${metrics.currentAge} лет (дожитие: ${metrics.lifeExpectancy} лет, горизонт: ${metrics.remainingYearsHorizon} г.)")
                appendLine("• Доходность: ${Formatters.formatPercent(inputData.nominalReturnPercent, lang)} | Инфляция: ${Formatters.formatPercent(inputData.inflationPercent, lang)}")
                appendLine()
                appendLine("2. Рассчитанные показатели:")
                appendLine("• Денежный поток: ${Formatters.formatMoney(metrics.cashFlow, currency, lang)}/мес.")
                appendLine("• Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate, lang)}")
                appendLine("• Подушка безопасности: ${Formatters.formatMoney(metrics.emergencyFund, currency, lang)} (${Formatters.formatRatio(metrics.safetyMonths, lang)} мес.)")
                appendLine("• Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio, lang)}")
                appendLine("• Чистый капитал: ${Formatters.formatMoney(metrics.netWorth, currency, lang)}")
                appendLine("• Реальная доходность: ${Formatters.formatPercent(metrics.realReturnPercent, lang)}")
                appendLine()
                appendLine("3. Набранные баллы:")
                appendLine("• Итоговая оценка: ${Formatters.formatScore(metrics.scores.totalScore, lang)} из 10")
                appendLine("• Норма сбережений: ${metrics.scores.savingsRateScore} / 10")
                appendLine("• Подушка безопасности: ${metrics.scores.safetyMonthsScore} / 10")
                appendLine("• Долговая нагрузка: ${metrics.scores.debtRatioScore} / 10")
                appendLine("• Финансовая свобода: ${metrics.scores.cfsScore} / 10")
                appendLine()
                appendLine("4. Финансовая свобода, FIRE:")
                appendLine("• КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio, lang)} ($cfsDesc)")
                appendLine("• Капитал для FIRE: ${Formatters.formatMoney(metrics.retirementTargetCapital, currency, lang)}")
                appendLine("• Срок до пенсии FIRE: ${Formatters.formatRatio(metrics.yearsToRetirement, lang)} лет (в возрасте ${Formatters.formatRatio(metrics.retirementAge, lang)} лет)")
                appendLine("• Бенчмарк Fidelity: ${Formatters.formatMoney(metrics.fidelityTarget, currency, lang)} (${Formatters.formatRatio(metrics.fidelityMultiplier, lang)}x годового дохода)")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("💡 Сформировано в приложении «Личные финансы»")
            }
        }
    }
}
