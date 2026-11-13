package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.components.MetricCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AlertRed
import com.example.ui.theme.StatusColors
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.util.Formatters
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings

private enum class MetricFilterCategory {
    ALL,
    CASH_FLOW,
    CAPITAL_RISKS,
    BENCHMARKS
}

@Composable
fun MetricsScreen(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val strings = LocalAppStrings.current
    var selectedCategory by remember { mutableStateOf(MetricFilterCategory.ALL) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Компактный заголовок экрана
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("metrics_header_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Column {
                            Text(
                                text = strings.metricsHeaderTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = strings.metricsHeaderSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Фильтры по категориям: одна горизонтально прокручиваемая строка
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .testTag("metrics_filter_row"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == MetricFilterCategory.ALL,
                            onClick = { selectedCategory = MetricFilterCategory.ALL },
                            label = { Text(strings.categoryAll, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedCategory == MetricFilterCategory.CASH_FLOW,
                            onClick = { selectedCategory = MetricFilterCategory.CASH_FLOW },
                            label = { Text(strings.categoryCashFlow, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedCategory == MetricFilterCategory.CAPITAL_RISKS,
                            onClick = { selectedCategory = MetricFilterCategory.CAPITAL_RISKS },
                            label = { Text(strings.categoryCapitalRisks, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedCategory == MetricFilterCategory.BENCHMARKS,
                            onClick = { selectedCategory = MetricFilterCategory.BENCHMARKS },
                            label = { Text(strings.categoryBenchmarks, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // 1. Денежный поток
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.CASH_FLOW) {
            item {
                MetricCard(
                    title = strings.metricCashFlowTitle,
                    value = Formatters.formatMoney(metrics.cashFlow, inputData.currencySymbol, lang),
                    formula = strings.metricCashFlowFormula,
                    description = strings.metricCashFlowDesc,
                    icon = Icons.Default.MonetizationOn,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getCashFlowStatus(metrics.cashFlow)),
                    subValue = when {
                        metrics.cashFlow > 0.0 -> strings.metricCashFlowPositiveSub
                        metrics.cashFlow == 0.0 -> strings.metricCashFlowNeutralSub
                        else -> strings.metricCashFlowNegativeSub
                    }
                )
            }
        }

        // 2. Норма сбережений
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.CASH_FLOW) {
            item {
                MetricCard(
                    title = strings.metricSavingsRateTitle,
                    value = Formatters.formatPercent(metrics.savingsRate, lang),
                    formula = strings.metricSavingsRateFormula,
                    description = strings.metricSavingsRateDesc(
                        strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD)
                    ),
                    icon = Icons.Default.Savings,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getSavingsRateStatus(metrics.savingsRate)),
                    subValue = strings.metricSavingsRateSub(
                        strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD)
                    )
                )
            }
        }

        // 3. Подушка безопасности
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.CAPITAL_RISKS) {
            item {
                MetricCard(
                    title = strings.metricEmergencyFundTitle,
                    value = Formatters.formatMoney(metrics.emergencyFund, inputData.currencySymbol, lang),
                    formula = strings.metricEmergencyFundFormula,
                    description = strings.metricEmergencyFundDesc,
                    icon = Icons.Default.Shield,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getSafetyMonthsStatus(metrics.safetyMonths)),
                    subValue = if (inputData.includeFinancialInEmergencyFund) strings.metricEmergencyFundSubLiquidPlusFin else strings.metricEmergencyFundSubOnlyLiquid
                )
            }
        }

        // 4. Запас месячных расходов
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.CAPITAL_RISKS) {
            item {
                MetricCard(
                    title = strings.metricSafetyMonthsTitle,
                    value = "${Formatters.formatRatio(metrics.safetyMonths, lang)} ${strings.monthsSuffix}",
                    formula = strings.metricSafetyMonthsFormula,
                    description = strings.metricSafetyMonthsDesc(
                        strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT)
                    ),
                    icon = Icons.Default.AvTimer,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getSafetyMonthsStatus(metrics.safetyMonths)),
                    subValue = strings.metricSafetyMonthsSub(
                        strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT)
                    )
                )
            }
        }

        // 5. Коэффициент задолженности
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.CAPITAL_RISKS) {
            item {
                MetricCard(
                    title = strings.metricDebtRatioTitle,
                    value = Formatters.formatRatio(metrics.debtRatio, lang),
                    formula = strings.metricDebtRatioFormula,
                    description = strings.metricDebtRatioDesc(
                        strings.formatDebtRatioThreshold(FinancialMetricEvaluator.DEBT_RATIO_ZERO_THRESHOLD),
                        strings.formatDebtRatioThreshold(FinancialMetricEvaluator.DEBT_RATIO_POSITIVE_THRESHOLD),
                        strings.formatDebtRatioThreshold(FinancialMetricEvaluator.DEBT_RATIO_NEUTRAL_THRESHOLD),
                        strings.formatDebtRatioThreshold(FinancialMetricEvaluator.DEBT_RATIO_TIER_CRITICAL)
                    ),
                    icon = Icons.Default.AccountBalanceWallet,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getDebtRatioStatus(metrics.debtRatio)),
                    subValue = when (FinancialMetricEvaluator.getDebtRatioStatus(metrics.debtRatio)) {
                        AuditIndicatorStatus.POSITIVE -> strings.metricDebtRatioLowSub
                        AuditIndicatorStatus.NEUTRAL -> strings.debtComment1(
                            strings.formatDebtRatioThreshold(FinancialMetricEvaluator.DEBT_RATIO_POSITIVE_THRESHOLD),
                            strings.formatDebtRatioThreshold(FinancialMetricEvaluator.DEBT_RATIO_NEUTRAL_THRESHOLD)
                        )
                        AuditIndicatorStatus.NEGATIVE -> strings.metricDebtRatioHighSub
                    }
                )
            }
        }

        // 6. Чистый капитал
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.CAPITAL_RISKS) {
            item {
                MetricCard(
                    title = strings.metricNetWorthTitle,
                    value = Formatters.formatMoney(metrics.netWorth, inputData.currencySymbol, lang),
                    formula = strings.metricNetWorthFormula,
                    description = strings.metricNetWorthDesc,
                    icon = Icons.Default.AccountBalance,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getNetWorthStatus(metrics.netWorth, metrics.totalAssets)),
                    subValue = if (metrics.netWorth >= 0) {
                        strings.metricNetWorthAssetsFormat(
                            strings.metricNetWorthSubPrefix,
                            Formatters.formatMoney(metrics.totalAssets, inputData.currencySymbol, lang)
                        )
                    } else {
                        strings.metricNetWorthNegativeAssetsFormat(
                            strings.metricNetWorthSubPrefix,
                            Formatters.formatMoney(metrics.totalAssets, inputData.currencySymbol, lang),
                            strings.metricNetWorthNegativeSub
                        )
                    }
                )
            }
        }

        // 7. Бенчмарк Fidelity
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.BENCHMARKS) {
            item {
                val fidelityRatio = metrics.fidelityRatio
                val fidelityPercent = metrics.fidelityPercent
                val fidelityColor = if (metrics.isFidelityAvailable) {
                    StatusColors.getColor(FinancialMetricEvaluator.getFidelityStatus(fidelityRatio))
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                MetricCard(
                    title = strings.metricFidelityTitle,
                    value = if (metrics.isFidelityAvailable) Formatters.formatMoney(metrics.fidelityTarget, inputData.currencySymbol, lang) else strings.metricUnavailableValue,
                    formula = strings.metricFidelityFormula,
                    description = strings.metricFidelityDesc,
                    icon = Icons.Default.Flag,
                    highlightColor = fidelityColor,
                    subValue = if (metrics.isAgeValid) {
                        strings.metricFidelitySubFormat(metrics.currentAge, Formatters.formatRatio(metrics.fidelityMultiplier, lang))
                    } else strings.metricUnavailableValue,
                    extraContent = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.metricFidelityProgress,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = if (metrics.isFidelityAvailable) {
                                        Formatters.formatPercent(fidelityPercent, lang)
                                    } else {
                                        strings.metricUnavailableValue
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = fidelityColor,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = {
                                    if (metrics.isFidelityAvailable) {
                                        (fidelityRatio.toFloat()).coerceIn(0f, 1f)
                                    } else 0f
                                },
                                color = fidelityColor,
                                trackColor = MaterialTheme.colorScheme.surface,
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.metricFidelityCapitalFormat(
                                    if (metrics.isFidelityAvailable) {
                                        Formatters.formatMoney(metrics.fidelityCapital, inputData.currencySymbol, lang)
                                    } else {
                                        strings.metricUnavailableValue
                                    }
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        }

        // 8. Коэффициент финансовой свободы (КФС)
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.BENCHMARKS) {
            item {
                MetricCard(
                    title = strings.metricCfsTitle,
                    value = Formatters.formatRatio(metrics.financialFreedomRatio, lang),
                    formula = strings.metricCfsFormula,
                    description = strings.metricCfsDesc(
                        strings.formatThreshold(FinancialMetricEvaluator.CFS_THRESHOLD_DEBT),
                        strings.formatThreshold(FinancialMetricEvaluator.CFS_THRESHOLD_BUFFER),
                        strings.formatThreshold(FinancialMetricEvaluator.CFS_NEUTRAL_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.CFS_POSITIVE_THRESHOLD),
                        strings.formatThreshold(FinancialMetricEvaluator.CFS_THRESHOLD_STABILITY),
                        strings.formatThreshold(FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE)
                    ),
                    icon = Icons.Default.CompassCalibration,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getCfsStatus(metrics.financialFreedomRatio)),
                    subValue = FinancialMetricEvaluator.getCfsStageDescription(metrics.financialFreedomRatio, lang)
                )
            }
        }

        // 9. Реальная доходность капитала
        if (selectedCategory == MetricFilterCategory.ALL || selectedCategory == MetricFilterCategory.BENCHMARKS) {
            item {
                MetricCard(
                    title = strings.metricRealReturnTitle,
                    value = Formatters.formatPercent(metrics.realReturnPercent, lang),
                    formula = strings.metricRealReturnFormula,
                    description = strings.metricRealReturnDesc,
                    icon = Icons.Default.Percent,
                    highlightColor = StatusColors.getColor(FinancialMetricEvaluator.getRealReturnStatus(metrics.realReturnPercent)),
                    subValue = strings.metricRealReturnSubFormat(
                        Formatters.formatPercent(inputData.nominalReturnPercent, lang),
                        Formatters.formatPercent(inputData.inflationPercent, lang)
                    )
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
