package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import com.example.domain.AuditIndicatorStatus
import com.example.domain.AuditorAnalysisGenerator
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

        // Recommendations Card (Аудиторское заключение по всем показателям)
        item {
            val opinion = AuditorAnalysisGenerator.generate(inputData, metrics)

            val shareFullOpinion: () -> Unit = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, opinion.toFormattedShareText(inputData, metrics))
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Поделиться аудиторским заключением"))
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auditor_opinion_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                            Column {
                                Text(
                                    text = "Аудиторское заключение",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Анализ по всем ${opinion.totalCount} показателям экрана «Показатели»",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Быстрые плашки-счетчики по трем группам
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🟢 ${opinion.positive.size} позит.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WarningYellowBg,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🟡 ${opinion.neutral.size} нейтр.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AlertRed.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🔴 ${opinion.negative.size} негатив.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Позитивные показатели
                    Text(
                        text = "Позитивные показатели (${opinion.positive.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (opinion.positive.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Позитивных показателей пока не выявлено. Рекомендуется оптимизировать доходы и расходы.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            opinion.positive.forEach { item ->
                                AuditOpinionIndicatorItem(text = item.text, status = item.status)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Нейтральные показатели
                    Text(
                        text = "Нейтральные показатели (${opinion.neutral.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (opinion.neutral.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Нейтральных показателей нет — все метрики в позитивной или негативной зоне.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            opinion.neutral.forEach { item ->
                                AuditOpinionIndicatorItem(text = item.text, status = item.status)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Негативные показатели
                    Text(
                        text = "Негативные показатели (${opinion.negative.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (opinion.negative.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.20f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎉 Критических и негативных факторов не выявлено — все показатели в безопасной зоне!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            opinion.negative.forEach { item ->
                                AuditOpinionIndicatorItem(text = item.text, status = item.status)
                            }
                        }
                    }
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

@Composable
private fun AuditOpinionIndicatorItem(
    text: String,
    status: AuditIndicatorStatus
) {
    val (bgColor, borderColor) = when (status) {
        AuditIndicatorStatus.POSITIVE -> SuccessGreen.copy(alpha = 0.08f) to SuccessGreen.copy(alpha = 0.22f)
        AuditIndicatorStatus.NEUTRAL -> WarningYellowBg to AccentGold.copy(alpha = 0.28f)
        AuditIndicatorStatus.NEGATIVE -> AlertRed.copy(alpha = 0.08f) to AlertRed.copy(alpha = 0.25f)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}
