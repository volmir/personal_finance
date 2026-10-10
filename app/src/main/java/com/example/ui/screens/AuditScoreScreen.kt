package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.ui.components.ScoreGauge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WarningYellowBg
import com.example.util.Formatters

@Composable
fun AuditScoreScreen(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scores = metrics.scores

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Big Score Header Card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audit_score_header_card")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Набранные баллы",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Итоги и суммирование результатов",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                val reportText = buildString {
                                    appendLine("📊 Финансовый отчет и аудит капитала:")
                                    appendLine("• Итоговый результат: ${Formatters.formatScore(scores.totalScore)} из 10")
                                    appendLine("• Норма сбережений: ${scores.savingsRateScore}/10 (${Formatters.formatPercent(metrics.savingsRate)})")
                                    appendLine("• Запас расходов: ${scores.safetyMonthsScore}/10 (${Formatters.formatRatio(metrics.safetyMonths)} мес.)")
                                    appendLine("• Коэффициент задолженности: ${scores.debtRatioScore}/10 (${Formatters.formatRatio(metrics.debtRatio)})")
                                    appendLine("• Коэффициент фин. свободы: ${scores.cfsScore}/10 (${Formatters.formatRatio(metrics.financialFreedomRatio)})")
                                    appendLine("• До пенсии (FIRE): ${Formatters.formatRatio(metrics.yearsToRetirement)} лет (в ${Formatters.formatRatio(metrics.retirementAge)} лет)")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Поделиться отчетом"))
                            },
                            modifier = Modifier.testTag("share_audit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Поделиться отчетом",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Radial Gauge
                    ScoreGauge(
                        score = scores.totalScore,
                        maxScore = 10.0,
                        size = 170.dp,
                        strokeWidth = 16.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val totalScoreColor = when {
                        scores.totalScore >= 8.0 -> SuccessGreen
                        scores.totalScore >= 5.0 -> WarningYellow
                        else -> AlertRed
                    }

                    Text(
                        text = "Итоговый результат: ${Formatters.formatScore(scores.totalScore)} из 10",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = totalScoreColor
                        )
                    )

                    val statusDesc = when {
                        scores.totalScore >= 8.0 -> "Отличная финансовая устойчивость и готовность к долгосрочным инвестициям."
                        scores.totalScore >= 6.0 -> "Хорошая финансовая форма с потенциалом роста нормы накопления."
                        scores.totalScore >= 4.0 -> "Умеренная стабильность. Рекомендуется нарастить подушку безопасности."
                        else -> "Требуется оптимизация расходов и погашение долговых обязательств."
                    }

                    Text(
                        text = statusDesc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Section Breakdown Items
        item {
            Text(
                text = "Детализация баллов по компонентам:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        // 1. Норма сбережений Score
        item {
            ScoreItemCard(
                title = "Норма сбережений",
                score = scores.savingsRateScore,
                currentValue = Formatters.formatPercent(metrics.savingsRate),
                formula = "(Денежный поток / Доходы) * 100%",
                comment = "Рекомендуется не менее 15-20%. У вас отличное значение (${Formatters.formatPercent(metrics.savingsRate)}).",
                tag = "score_savings_rate"
            )
        }

        // 2. Запас месячных расходов Score
        item {
            ScoreItemCard(
                title = "Запас месячных расходов",
                score = scores.safetyMonthsScore,
                currentValue = "${Formatters.formatRatio(metrics.safetyMonths)} мес.",
                formula = "(Подушка безопасности / Ежемесячные расходы)",
                comment = "Минимум 3-6 месяцев, идеал 12+ мес. Ваш запас превышает 12 месяцев (максимальный балл).",
                tag = "score_safety_months"
            )
        }

        // 3. Коэффициент задолженности Score
        item {
            val debtComment = when {
                metrics.debtRatio <= 0.3 -> "0,0 - 0,3: Отличная устойчивость, активы финансируются собственными средствами."
                metrics.debtRatio <= 0.6 -> "0,3 - 0,6: Сбалансированное кредитное плечо, умеренная нагрузка."
                metrics.debtRatio <= 1.0 -> "0,6 - 1,0: Высокий риск, значительная зависимость от заемных средств."
                else -> "Более 1,0: Критический риск, долги превышают стоимость активов."
            }
            ScoreItemCard(
                title = "Коэффициент задолженности",
                score = scores.debtRatioScore,
                currentValue = Formatters.formatRatio(metrics.debtRatio),
                formula = "(Пассивы / Активы)",
                comment = debtComment,
                tag = "score_debt_ratio"
            )
        }

        // 4. Коэффициент финансовой свободы Score
        item {
            ScoreItemCard(
                title = "Коэффициент финансовой свободы",
                score = scores.cfsScore,
                currentValue = Formatters.formatRatio(metrics.financialFreedomRatio),
                formula = "(Активы / Годовые расходы)",
                comment = metrics.cfsStageDescription,
                tag = "score_cfs"
            )
        }

        // Recommendations Card (Аудиторское заключение)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auditor_opinion_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 1. Подушка безопасности (проверенные пороги: >=12 мес, 6..12 мес, 3..6 мес, <3 мес)
                    val recSafety = when {
                        metrics.safetyMonths >= 12.0 ->
                            "✅ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Полностью сформирована с большим запасом. Риски потери дохода нивелированы. Избыточную ликвидность сверх годового резерва целесообразно направить в доходные инвестиционные инструменты."
                        metrics.safetyMonths >= 6.0 ->
                            "✅ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Целевой норматив в 6 месяцев расходов успешно достигнут. Базовая финансовая безопасность семьи обеспечена."
                        metrics.safetyMonths >= 3.0 ->
                            "ℹ️ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Сформирован минимальный базовый резерв, однако рекомендуется постепенно нарастить его до комфортных 6 месяцев расходов для защиты от непредвиденных кризисов."
                        else ->
                            "⚠️ Запас прочности (Подушка безопасности: ${Formatters.formatRatio(metrics.safetyMonths)} мес.): Критически недостаточный резерв (менее 3 месяцев). Любой кассовый разрыв или непредвиденные траты создадут угрозу долговой ямы. Приоритетная задача — экстренно довести ликвидный резерв минимум до 6 месяцев расходов."
                    }

                    // 2. Долговая нагрузка и коэффициент задолженности (уточнение кредитного плеча + шкала тональности)
                    val debtPct = metrics.debtRatio * 100.0
                    val recDebt = when {
                        metrics.debtRatio == 0.0 ->
                            "✅ Долговая нагрузка (Коэффициент задолженности: 0,00): Абсолютная финансовая автономность — долги и кредиты отсутствуют, все активы принадлежат вам на 100%."
                        metrics.debtRatio <= 0.30 ->
                            "✅ Долговая нагрузка (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долг ${Formatters.formatPercent(debtPct)} от активов): Кредитное плечо консервативное и безопасное. Обязательства полностью обеспечены ликвидным имуществом, кредитный риск минимален."
                        metrics.debtRatio <= 0.60 ->
                            "ℹ️ Долговая нагрузка (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долг ${Formatters.formatPercent(debtPct)} от активов): Умеренный уровень заемного финансирования. Долговая нагрузка контролируема, но требует внимания при планировании крупных покупок и регулярных платежей."
                        metrics.debtRatio <= 1.0 ->
                            "⚠️ Внимание: Высокая закредитованность (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долг ${Formatters.formatPercent(debtPct)} от активов)! Значительная зависимость от внешних кредиторов. Рекомендуется рефинансировать займы, сократить долги и воздержаться от новых обязательств."
                        else ->
                            "🚨 КРИТИЧЕСКИЙ РИСК: Дефицит капитала (Коэффициент задолженности: ${Formatters.formatRatio(metrics.debtRatio)}, долги превышают активы на ${Formatters.formatPercent(debtPct - 100.0)})! Объем финансовых обязательств превышает стоимость всего вашего имущества. Необходима экстренная реструктуризация долгов и финансовое оздоровление."
                    }

                    // 3. Норма сбережений и денежный поток (дополнительный ключевой финансовый показатель)
                    val recSavings = when {
                        metrics.savingsRate >= 35.0 ->
                            "✅ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Превосходный уровень! Высокая норма инвестирования денежного потока позволяет экспоненциально наращивать собственный капитал."
                        metrics.savingsRate >= 15.0 ->
                            "ℹ️ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Здоровый рабочий темп (в рамках стандарта 15–20%). Обеспечивает стабильное регулярное пополнение активов."
                        metrics.savingsRate > 0.0 ->
                            "ℹ️ Способность к накоплению (Норма сбережений: ${Formatters.formatPercent(metrics.savingsRate)}): Низкий уровень сбережений (менее 15%). Формирование капитала идет медленно, рекомендуется оптимизировать структуру расходов."
                        metrics.savingsRate == 0.0 ->
                            "⚠️ Способность к накоплению: Нулевой свободный денежный поток (доходы равны расходам). Капитал не прирастает, резервы не пополняются."
                        else ->
                            "🚨 Способность к накоплению: Отрицательный денежный поток (дефицит бюджета)! Расходы превышают доходы, что ведет к быстрому истощению активов и росту долгов."
                    }

                    // 4. Достаточность капитала по возрасту (Бенчмарк Fidelity)
                    val recFidelity = if (metrics.fidelityTarget > 0.0) {
                        val fidelityRatio = metrics.totalAssets / metrics.fidelityTarget
                        when {
                            fidelityRatio >= 1.2 ->
                                "✅ Возрастной ориентир капитала (Бенчмарк Fidelity): Капитал опережает целевой норматив для вашего возраста (${Formatters.formatPercent(fidelityRatio * 100.0)} от цели). Отличный задел для долгосрочного благосостояния."
                            fidelityRatio >= 0.8 ->
                                "ℹ️ Возрастной ориентир капитала (Бенчмарк Fidelity): Капитал соответствует рекомендуемым возрастным ориентирам накоплений (${Formatters.formatPercent(fidelityRatio * 100.0)} от целевого значения)."
                            else ->
                                "⚠️ Возрастной ориентир капитала (Бенчмарк Fidelity): Отставание от рекомендуемой возрастной планки капитала (${Formatters.formatPercent(fidelityRatio * 100.0)} от цели в ${Formatters.formatMoney(metrics.fidelityTarget, inputData.currencySymbol)}). Желательно ускорить темп капитализации."
                        }
                    } else null

                    // 5. Финансовая свобода и горизонт FIRE (КФС)
                    val recCfs = when {
                        metrics.financialFreedomRatio >= 30.0 ->
                            "🎉 Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Вы достигли полной финансовой независимости (FIRE)! Накопленные активы способны пожизненно обеспечивать текущий уровень жизни."
                        metrics.financialFreedomRatio >= 15.0 ->
                            "✅ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Высокая степень автономии. Капитал генерирует существенный пассивный доход, закрывающий большую часть обязательных расходов."
                        metrics.financialFreedomRatio >= 5.0 ->
                            "ℹ️ Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Уверенная фаза роста капитала (${metrics.cfsStageDescription}). Следующая стратегическая цель — рубеж КФС > 15."
                        else ->
                            "📈 Финансовая свобода (КФС: ${Formatters.formatRatio(metrics.financialFreedomRatio)}): Начальный этап формирования инвестиционного капитала (${metrics.cfsStageDescription}). Основной упор на дисциплинированные регулярные сбережения."
                    }

                    val fullOpinion = buildList {
                        add(recSafety)
                        add(recDebt)
                        add(recSavings)
                        recFidelity?.let { add(it) }
                        add(recCfs)
                    }.joinToString(separator = "\n\n")

                    val shareFullOpinion: () -> Unit = {
                        val shareText = buildString {
                            appendLine("📋 АУДИТОРСКОЕ ЗАКЛЮЧЕНИЕ И ФИНАНСОВЫЙ АУДИТ")
                            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                            appendLine("⭐️ Итоговая оценка: ${Formatters.formatScore(scores.totalScore)} из 10 баллов")
                            appendLine()
                            appendLine(fullOpinion)
                            appendLine()
                            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                            appendLine("💡 Сформировано в приложении «Personal finance»")
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Поделиться аудиторским заключением"))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Аудиторское заключение",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        IconButton(
                            onClick = shareFullOpinion,
                            modifier = Modifier.testTag("share_auditor_opinion_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Поделиться аудиторским заключением",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = fullOpinion,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ScoreItemCard(
    title: String,
    score: Int,
    currentValue: String,
    formula: String,
    comment: String,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val scoreColor = when {
                    score >= 8 -> SuccessGreen
                    score >= 5 -> WarningYellow
                    else -> AlertRed
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Значение: $currentValue",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = scoreColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = scoreColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$score из 10",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { (score / 10f).coerceIn(0f, 1f) },
                color = when {
                    score >= 8 -> SuccessGreen
                    score >= 5 -> WarningYellow
                    else -> AlertRed
                },
                trackColor = MaterialTheme.colorScheme.surface,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
