package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.components.ScoreGauge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ScoreColorEvaluator
import com.example.ui.theme.StatusColors
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.WarningYellowBg
import com.example.util.Formatters
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings

@Composable
fun AuditScoreScreen(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val strings = LocalAppStrings.current
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
                                text = strings.scoresHeaderTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = strings.scoresHeaderSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                val reportText = String.format(
                                    strings.shareScoreReportTemplate,
                                    Formatters.formatScore(scores.totalScore, lang = lang),
                                    scores.savingsRateScore,
                                    Formatters.formatPercent(metrics.savingsRate, lang),
                                    scores.safetyMonthsScore,
                                    Formatters.formatRatio(metrics.safetyMonths, lang),
                                    scores.debtRatioScore,
                                    Formatters.formatRatio(metrics.debtRatio, lang),
                                    scores.cfsScore,
                                    Formatters.formatRatio(metrics.financialFreedomRatio, lang),
                                    Formatters.formatRatio(metrics.yearsToRetirement, lang),
                                    Formatters.formatRatio(metrics.retirementAge, lang)
                                )
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, strings.scoresShareReportChooser))
                            },
                            modifier = Modifier.testTag("share_audit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = strings.scoresShareReportChooser,
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

                    val totalScoreColor = ScoreColorEvaluator.getTotalScoreColor(scores.totalScore)

                    Text(
                        text = strings.scoresTotalResult(Formatters.formatScore(scores.totalScore, lang = lang)),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = totalScoreColor
                        )
                    )

                    val statusDesc = ScoreColorEvaluator.getTotalScoreStatusDescription(scores.totalScore, strings)

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
                text = strings.scoresBreakdownTitle,
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
                title = strings.savingsRateTitle,
                score = scores.savingsRateScore,
                currentValue = Formatters.formatPercent(metrics.savingsRate, lang),
                formula = strings.savingsRateFormula,
                comment = strings.savingsRateScoreComment(metrics.savingsRate, Formatters.formatPercent(metrics.savingsRate, lang)),
                valuePrefix = strings.scoreValuePrefix,
                scoreOutOfTen = strings.scoreOutOfTen(scores.savingsRateScore),
                tag = "score_savings_rate"
            )
        }

        // 2. Запас месячных расходов Score
        item {
            val monthsText = String.format(strings.safetyMonthsValueFormat, Formatters.formatRatio(metrics.safetyMonths, lang))
            ScoreItemCard(
                title = strings.metricSafetyMonthsTitle,
                score = scores.safetyMonthsScore,
                currentValue = monthsText,
                formula = strings.metricSafetyMonthsFormula,
                comment = strings.safetyMonthsScoreComment(metrics.safetyMonths, monthsText),
                valuePrefix = strings.scoreValuePrefix,
                scoreOutOfTen = strings.scoreOutOfTen(scores.safetyMonthsScore),
                tag = "score_safety_months"
            )
        }

        // 3. Коэффициент задолженности Score
        item {
            val debtComment = when {
                metrics.debtRatio <= 0.3 -> strings.debtComment0
                metrics.debtRatio <= 0.6 -> strings.debtComment1
                metrics.debtRatio <= 1.0 -> strings.debtComment2
                else -> strings.debtComment3
            }
            ScoreItemCard(
                title = strings.metricDebtRatioTitle,
                score = scores.debtRatioScore,
                currentValue = Formatters.formatRatio(metrics.debtRatio, lang),
                formula = strings.metricDebtRatioFormula,
                comment = debtComment,
                valuePrefix = strings.scoreValuePrefix,
                scoreOutOfTen = strings.scoreOutOfTen(scores.debtRatioScore),
                tag = "score_debt_ratio"
            )
        }

        // 4. Коэффициент финансовой свободы Score
        item {
            ScoreItemCard(
                title = strings.metricCfsTitle,
                score = scores.cfsScore,
                currentValue = Formatters.formatRatio(metrics.financialFreedomRatio, lang),
                formula = strings.metricCfsFormula,
                comment = com.example.domain.FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, lang),
                valuePrefix = strings.scoreValuePrefix,
                scoreOutOfTen = strings.scoreOutOfTen(scores.cfsScore),
                tag = "score_cfs"
            )
        }

        // Recommendations Card (Аудиторское заключение по всем показателям)
        item {
            val opinion = AuditorAnalysisGenerator.generate(inputData, metrics, lang)

            val shareFullOpinion: () -> Unit = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, opinion.toFormattedShareText(inputData, metrics, lang))
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, strings.shareAuditorOpinionChooser))
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
                                    text = strings.auditorOpinionTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = strings.auditorOpinionSubtitle(opinion.totalCount),
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
                                contentDescription = strings.shareAuditorOpinionChooser,
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
                                    text = strings.positiveBadgeText(opinion.positive.size),
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
                                    text = strings.neutralBadgeText(opinion.neutral.size),
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
                                    text = strings.negativeBadgeText(opinion.negative.size),
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
                        text = strings.positiveSectionTitle(opinion.positive.size),
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
                                text = strings.positiveEmptyText,
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
                        text = strings.neutralSectionTitle(opinion.neutral.size),
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
                                text = strings.neutralEmptyText,
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
                        text = strings.negativeSectionTitle(opinion.negative.size),
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
                                text = strings.negativeEmptyText,
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

        // Кнопка экспорта краткого финансового отчета
        item {
            val context = LocalContext.current
            Button(
                onClick = {
                    val exportText = AuditorAnalysisGenerator.generateExportSummary(inputData, metrics, lang)
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, exportText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, strings.exportChooserTitle))
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("export_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = strings.exportButtonText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.exportButtonText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Карточка отказа разработчика от ответственности (Developer Disclaimer)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("developer_disclaimer_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.disclaimerTitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.disclaimerText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.assumptionsDisclaimerTitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.assumptionsDisclaimerBody(
                                Formatters.formatPercent(inputData.nominalReturnPercent, lang),
                                Formatters.formatPercent(inputData.inflationPercent, lang)
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
    valuePrefix: String,
    scoreOutOfTen: String,
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
            val scoreColor = StatusColors.getColor(FinancialMetricEvaluator.getComponentScoreStatus(score))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "$valuePrefix $currentValue",
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
                        text = scoreOutOfTen,
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
                color = scoreColor,
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
