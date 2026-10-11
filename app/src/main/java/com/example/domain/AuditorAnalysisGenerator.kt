package com.example.domain

import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.util.Formatters

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

    fun toFormattedShareText(inputData: FinancialInputData, metrics: CalculatedMetrics): String {
        return buildString {
            appendLine("📋 АУДИТОРСКОЕ ЗАКЛЮЧЕНИЕ ПО ВСЕМ ПОКАЗАТЕЛЯМ")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("⭐️ Итоговая оценка: ${Formatters.formatScore(metrics.scores.totalScore)} из 10 баллов")
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
            appendLine("💡 Сформировано в приложении «Personal finance»")
        }
    }
}

object AuditorAnalysisGenerator {

    fun generate(inputData: FinancialInputData, metrics: CalculatedMetrics): GroupedAuditOpinion {
        val currency = inputData.currencySymbol

        // 1. Денежный поток (Cash Flow)
        val cashFlowAnalysis = when {
            metrics.cashFlow > 0.0 -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = "Денежный поток",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Денежный поток (Cash Flow: +${Formatters.formatMoney(metrics.cashFlow, currency)}/мес.): Профицит бюджета. Ваши доходы превышают расходы, формируя регулярный инвестиционный ресурс для приумножения капитала."
            )
            metrics.cashFlow == 0.0 -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = "Денежный поток",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Денежный поток (Cash Flow: 0 ${currency}/мес.): Нулевой баланс бюджета (доходы равны расходам). Вы не накапливаете долги, однако свободный ресурс для создания и приумножения капитала отсутствует."
            )
            else -> AuditIndicatorAnalysis(
                id = "cash_flow",
                title = "Денежный поток",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "🚨 Денежный поток (Cash Flow: ${Formatters.formatMoney(metrics.cashFlow, currency)}/мес.): Дефицит бюджета! Текущие расходы превышают доходы, что ведет к быстрому истощению резервов или росту долговых обязательств."
            )
        }

        // 2. Норма сбережений
        val savingsRateAnalysis = when {
            metrics.savingsRate >= 35.0 -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = "Норма сбережений",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Превосходный уровень! Высокая норма инвестирования денежного потока позволяет экспоненциально наращивать собственный капитал."
            )
            metrics.savingsRate >= 15.0 -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = "Норма сбережений",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Здоровый рабочий темп (в рамках стандарта 15–20%). Обеспечивает стабильное регулярное пополнение активов."
            )
            metrics.savingsRate > 0.0 -> AuditIndicatorAnalysis(
                id = "savings_rate",
                title = "Норма сбережений",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Низкий уровень сбережений (менее 15%). Формирование капитала идет медленно, рекомендуется оптимизировать структуру расходов."
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
                text = "🚨 Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Отрицательный денежный поток (дефицит бюджета)! Расходы превышают доходы, что ведет к быстрому истощению активов и росту долгов."
            )
        }

        // 3. Подушка безопасности (сумма)
        val emergencyFundAnalysis = when {
            metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = "Подушка безопасности",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Подушка безопасности (Объем резерва: ${Formatters.formatMoney(metrics.emergencyFund, currency)}): Ликвидный резерв полностью достаточен для покрытия непредвиденных кризисов и экстренных ситуаций без обращения к кредитам."
            )
            metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = "Подушка безопасности",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Подушка безопасности (Объем резерва: ${Formatters.formatMoney(metrics.emergencyFund, currency)}): Сформирован базовый минимальный резерв. Рекомендуется постепенно довести его до комфортных 6 месяцев расходов."
            )
            else -> AuditIndicatorAnalysis(
                id = "emergency_fund",
                title = "Подушка безопасности",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "⚠️ Подушка безопасности (Объем резерва: ${Formatters.formatMoney(metrics.emergencyFund, currency)}): Критически низкий объем резерва. При непредвиденных тратах высок риск кассового разрыва и необходимости брать кредиты."
            )
        }

        // 4. Запас месячных расходов
        val safetyMonthsAnalysis = when {
            metrics.safetyMonths >= 12.0 -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = "Запас месячных расходов",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Полностью сформирована с большим запасом. Риски потери дохода нивелированы. Избыточную ликвидность сверх годового резерва целесообразно направить в доходные инвестиционные инструменты."
            )
            metrics.safetyMonths >= 6.0 -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = "Запас месячных расходов",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Целевой норматив в 6 месяцев расходов успешно достигнут. Базовая финансовая безопасность семьи обеспечена."
            )
            metrics.safetyMonths >= 3.0 -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = "Запас месячных расходов",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Сформирован минимальный базовый резерв, однако рекомендуется постепенно нарастить его до комфортных 6 месяцев расходов для защиты от непредвиденных кризисов."
            )
            else -> AuditIndicatorAnalysis(
                id = "safety_months",
                title = "Запас месячных расходов",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "⚠️ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Критически недостаточный резерв (менее 3 месяцев). Любой кассовый разрыв или непредвиденные траты создадут угрозу долговой ямы. Приоритетная задача — экстренно довести ликвидный резерв минимум до 6 месяцев расходов."
            )
        }

        // 5. Коэффициент задолженности
        val debtPct = metrics.debtRatio * 100.0
        val debtRatioAnalysis = when {
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
                text = "✅ Долговая нагрузка (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долг ${Formatters.formatPercent(debtPct)} от активов): Кредитное плечо консервативное и безопасное. Обязательства полностью обеспечены ликвидным имуществом, кредитный риск минимален."
            )
            metrics.debtRatio <= 0.60 -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = "Коэффициент задолженности",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Долговая нагрузка (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долг ${Formatters.formatPercent(debtPct)} от активов): Умеренный уровень заемного финансирования. Долговая нагрузка контролируема, но требует внимания при планировании крупных покупок и регулярных платежей."
            )
            metrics.debtRatio <= 1.0 -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = "Коэффициент задолженности",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "⚠️ Внимание: Высокая закредитованность (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долг ${Formatters.formatPercent(debtPct)} от активов)! Значительная зависимость от внешних кредиторов. Рекомендуется рефинансировать займы, сократить долги и воздержаться от новых обязательств."
            )
            else -> AuditIndicatorAnalysis(
                id = "debt_ratio",
                title = "Коэффициент задолженности",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "🚨 КРИТИЧЕСКИЙ РИСК: Дефицит капитала (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долги превышают активы на ${Formatters.formatPercent(debtPct - 100.0)})! Объем финансовых обязательств превышает стоимость всего вашего имущества. Необходима экстренная реструктуризация долгов и финансовое оздоровление."
            )
        }

        // 6. Чистый капитал
        val netWorthAnalysis = when {
            metrics.netWorth > 0.0 && (metrics.totalAssets == 0.0 || metrics.netWorth / metrics.totalAssets >= 0.5) -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = "Чистый капитал",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Чистый капитал (Собственные средства: ${Formatters.formatMoney(metrics.netWorth, currency)}): Положительный чистый баланс. Ваши совокупные активы уверенно превышают финансовые обязательства, формируя прочный финансовый фундамент."
            )
            metrics.netWorth >= 0.0 -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = "Чистый капитал",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Чистый капитал (Собственные средства: ${Formatters.formatMoney(metrics.netWorth, currency)}): Активы и обязательства находятся в близком балансе. Ваша финансовая стоимость положительна, но существенная часть активов обременена долгами."
            )
            else -> AuditIndicatorAnalysis(
                id = "net_worth",
                title = "Чистый капитал",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "🚨 Чистый капитал (Отрицательный баланс: ${Formatters.formatMoney(metrics.netWorth, currency)}): Отрицательный чистый капитал (техническая неплатежеспособность). Обязательства превышают стоимость всего имеющегося имущества."
            )
        }

        // 7. Бенчмарк Fidelity
        val fidelityAnalysis = if (metrics.fidelityTarget > 0.0) {
            val fidelityRatio = metrics.totalAssets / metrics.fidelityTarget
            when {
                fidelityRatio >= 1.0 -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = "Бенчмарк Fidelity",
                    status = AuditIndicatorStatus.POSITIVE,
                    text = "✅ Возрастной ориентир капитала (Бенчмарк Fidelity: ${Formatters.formatPercent(fidelityRatio * 100.0)} от цели): Капитал опережает целевой норматив для вашего возраста (${Formatters.formatMoney(metrics.fidelityTarget, currency)}). Отличный задел для долгосрочного благосостояния."
                )
                fidelityRatio >= 0.7 -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = "Бенчмарк Fidelity",
                    status = AuditIndicatorStatus.NEUTRAL,
                    text = "ℹ️ Возрастной ориентир капитала (Бенчмарк Fidelity: ${Formatters.formatPercent(fidelityRatio * 100.0)} от цели): Капитал соответствует рекомендуемым возрастным ориентирам накоплений (${Formatters.formatMoney(metrics.fidelityTarget, currency)})."
                )
                else -> AuditIndicatorAnalysis(
                    id = "fidelity",
                    title = "Бенчмарк Fidelity",
                    status = AuditIndicatorStatus.NEGATIVE,
                    text = "⚠️ Возрастной ориентир капитала (Бенчмарк Fidelity: ${Formatters.formatPercent(fidelityRatio * 100.0)} от цели): Отставание от рекомендуемой возрастной планки капитала (${Formatters.formatMoney(metrics.fidelityTarget, currency)}). Желательно ускорить темп капитализации."
                )
            }
        } else {
            AuditIndicatorAnalysis(
                id = "fidelity",
                title = "Бенчмарк Fidelity",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Возрастной ориентир капитала (Бенчмарк Fidelity): В возрасте до 30 лет основной задачей является закладка фундамента накоплений. Целевые множители Fidelity начинаются с 30 лет (1x годовой доход)."
            )
        }

        // 8. Коэффициент финансовой свободы (КФС)
        val cfsAnalysis = when {
            metrics.financialFreedomRatio >= 30.0 -> AuditIndicatorAnalysis(
                id = "cfs",
                title = "Коэффициент финансовой свободы",
                status = AuditIndicatorStatus.POSITIVE,
                text = "🎉 Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Вы достигли полной финансовой независимости (FIRE)! Накопленные активы способны пожизненно обеспечивать текущий уровень жизни."
            )
            metrics.financialFreedomRatio >= 15.0 -> AuditIndicatorAnalysis(
                id = "cfs",
                title = "Коэффициент финансовой свободы",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Высокая степень автономии. Капитал генерирует существенный пассивный доход, закрывающий большую часть обязательных расходов."
            )
            metrics.financialFreedomRatio >= 5.0 -> AuditIndicatorAnalysis(
                id = "cfs",
                title = "Коэффициент финансовой свободы",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Уверенная фаза роста капитала (${metrics.cfsStageDescription}). Доходы от капитала начинают приносить ощутимый результат. Следующая стратегическая цель — рубеж КФС > 15."
            )
            metrics.financialFreedomRatio >= 2.0 -> AuditIndicatorAnalysis(
                id = "cfs",
                title = "Коэффициент финансовой свободы",
                status = AuditIndicatorStatus.NEUTRAL,
                text = "ℹ️ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Начальный этап формирования инвестиционного капитала (${metrics.cfsStageDescription}). Основной упор на дисциплинированные регулярные сбережения."
            )
            else -> AuditIndicatorAnalysis(
                id = "cfs",
                title = "Коэффициент финансовой свободы",
                status = AuditIndicatorStatus.NEGATIVE,
                text = "📈 Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Начальный этап формирования капитала (${metrics.cfsStageDescription}). Накопленного объема недостаточно для финансовой автономии, требуется дисциплинированное сбережение."
            )
        }

        // 9. Реальная доходность капитала
        val realReturnAnalysis = when {
            metrics.realReturnPercent > 0.0 -> AuditIndicatorAnalysis(
                id = "real_return",
                title = "Реальная доходность капитала",
                status = AuditIndicatorStatus.POSITIVE,
                text = "✅ Реальная доходность капитала (+${Formatters.formatPercent(metrics.realReturnPercent)}): Доходность активов опережает инфляцию (${Formatters.formatPercent(inputData.nominalReturnPercent)} против ${Formatters.formatPercent(inputData.inflationPercent)}). Капитал защищен от обесценивания и наращивает реальную покупательную способность."
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
                text = "⚠️ Реальная доходность капитала (${Formatters.formatPercent(metrics.realReturnPercent)}): Инфляция (${Formatters.formatPercent(inputData.inflationPercent)}) превышает доходность капитала (${Formatters.formatPercent(inputData.nominalReturnPercent)}). Происходит скрытое обесценивание накоплений и потеря покупательной способности."
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
    fun generateExportSummary(inputData: FinancialInputData, metrics: CalculatedMetrics): String {
        val currency = inputData.currencySymbol
        return buildString {
            appendLine("📊 ФИНАНСОВЫЙ ОТЧЕТ — PERSONAL FINANCE")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("1. Исходные данные:")
            appendLine("• Доход: ${Formatters.formatMoney(inputData.monthlyIncome, currency)}/мес.")
            appendLine("• Расходы: ${Formatters.formatMoney(inputData.monthlyExpenses, currency)}/мес.")
            appendLine("• Ликвидные активы: ${Formatters.formatMoney(inputData.liquidAssets, currency)}")
            appendLine("• Финансовые активы: ${Formatters.formatMoney(inputData.financialAssets, currency)}")
            appendLine("• Недвижимость / имущество: ${Formatters.formatMoney(inputData.propertyAssets, currency)}")
            appendLine("• Пассивы (долги): ${Formatters.formatMoney(inputData.liabilities, currency)}")
            appendLine("• Возраст: ${metrics.currentAge} лет (дожитие: ${metrics.lifeExpectancy} лет, горизонт: ${metrics.remainingYearsHorizon} г.)")
            appendLine("• Доходность: ${Formatters.formatPercent(inputData.nominalReturnPercent)} | Инфляция: ${Formatters.formatPercent(inputData.inflationPercent)}")
            appendLine()
            appendLine("2. Рассчитанные показатели:")
            appendLine("• Денежный поток: ${Formatters.formatMoney(metrics.cashFlow, currency)}/мес.")
            appendLine("• Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}")
            appendLine("• Подушка безопасности: ${Formatters.formatMoney(metrics.emergencyFund, currency)} (${Formatters.formatRatio(metrics.safetyMonths)} мес.)")
            appendLine("• Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}")
            appendLine("• Чистый капитал: ${Formatters.formatMoney(metrics.netWorth, currency)}")
            appendLine("• Реальная доходность: ${Formatters.formatPercent(metrics.realReturnPercent)}")
            appendLine()
            appendLine("3. Набранные баллы:")
            appendLine("• Итоговая оценка: ${Formatters.formatScore(metrics.scores.totalScore)} из 10")
            appendLine("• Норма сбережений: ${metrics.scores.savingsRateScore} / 10")
            appendLine("• Подушка безопасности: ${metrics.scores.safetyMonthsScore} / 10")
            appendLine("• Долговая нагрузка: ${metrics.scores.debtRatioScore} / 10")
            appendLine("• Финансовая свобода: ${metrics.scores.cfsScore} / 10")
            appendLine()
            appendLine("4. Финансовая свобода, FIRE:")
            appendLine("• КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)} (${metrics.cfsStageDescription})")
            appendLine("• Капитал для FIRE: ${Formatters.formatMoney(metrics.retirementTargetCapital, currency)}")
            appendLine("• Срок до пенсии FIRE: ${Formatters.formatRatio(metrics.yearsToRetirement)} лет (в возрасте ${Formatters.formatRatio(metrics.retirementAge)} лет)")
            appendLine("• Бенчмарк Fidelity: ${Formatters.formatMoney(metrics.fidelityTarget, currency)} (${Formatters.formatRatio(metrics.fidelityMultiplier)}x годового дохода)")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("💡 Сформировано в приложении «Personal finance»")
        }
    }
}
