package com.example.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.ui.components.MetricCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.util.Formatters

@Composable
fun MetricsScreen(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Все") }
    val categories = listOf("Все", "Денежный поток", "Капитал & Риски", "Бенчмарки")

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
                                text = "Расчет показателей",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Все ключевые метрики с формулами и оценкой",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Фильтры по категориям: первая строка ("Все", "Денежный поток", "Капитал & Риски")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Все", "Денежный поток", "Капитал & Риски").forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Вторая строка: "Бенчмарки" перенесены на следующую строку
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == "Бенчмарки",
                            onClick = { selectedCategory = "Бенчмарки" },
                            label = { Text("Бенчмарки", fontSize = 11.sp) },
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
        if (selectedCategory == "Все" || selectedCategory == "Денежный поток") {
            item {
                MetricCard(
                    title = "Денежный поток",
                    value = Formatters.formatMoney(metrics.cashFlow, inputData.currencySymbol),
                    formula = "(Доходы - Расходы)",
                    description = "Cash Flow. Движение денег (приток и отток) за период.\n\n" +
                            "• Положительное значение: Вы зарабатываете больше, чем тратите.\n" +
                            "• Отрицательное значение: Вы тратите больше, чем зарабатываете.",
                    icon = Icons.Default.MonetizationOn,
                    highlightColor = if (metrics.cashFlow >= 0) SuccessGreen else AlertRed,
                    subValue = if (metrics.cashFlow >= 0) "Положительный приток денег" else "Дефицит бюджета"
                )
            }
        }

        // 2. Норма сбережений
        if (selectedCategory == "Все" || selectedCategory == "Денежный поток") {
            item {
                MetricCard(
                    title = "Норма сбережений",
                    value = Formatters.formatPercent(metrics.savingsRate),
                    formula = "(Денежный поток / Доходы) * 100%",
                    description = "Рекомендуется стремиться к норме сбережения не менее чем 15-20% от общего дохода.",
                    icon = Icons.Default.Savings,
                    score = metrics.scores.savingsRateScore,
                    highlightColor = when {
                        metrics.savingsRate >= 30.0 -> SuccessGreen
                        metrics.savingsRate >= 15.0 -> WarningYellow
                        else -> AlertRed
                    },
                    subValue = "Целевой ориентир: ≥ 15-20%"
                )
            }
        }

        // 3. Подушка безопасности
        if (selectedCategory == "Все" || selectedCategory == "Капитал & Риски") {
            item {
                MetricCard(
                    title = "Подушка безопасности",
                    value = Formatters.formatMoney(metrics.emergencyFund, inputData.currencySymbol),
                    formula = "(Ликвидные активы)",
                    description = "Личная финансовая «подушка безопасности» или запас денег, созданный для покрытия непредвиденных расходов (болезни, потеря работы и т.п.) и обеспечения финансовой стабильности в экстренных ситуациях, чтобы не влезать в долги.",
                    icon = Icons.Default.Shield,
                    highlightColor = MaterialTheme.colorScheme.primary,
                    subValue = if (inputData.includeFinancialInEmergencyFund) "Ликвидные + финансовые" else "Только ликвидные"
                )
            }
        }

        // 4. Запас месячных расходов
        if (selectedCategory == "Все" || selectedCategory == "Капитал & Риски") {
            item {
                MetricCard(
                    title = "Запас месячных расходов",
                    value = "${Formatters.formatRatio(metrics.safetyMonths)} мес.",
                    formula = "(Подушка безопасности / Ежемесячные расходы)",
                    description = "Количество месяцев, которые вы можете прожить на средства из \"подушки безопасности\".\n\n" +
                            "• Минимальный уровень: 3-6 месяцев основных расходов (аренда/ипотека, еда, транспорт, коммунальные услуги, связь). Это «спокойное» существование при нулевых доходах.\n" +
                            "• Идеальный уровень: Может включать больше, например, до 12 месяцев, для более уверенного прохождения длительных кризисов, особенно при нестабильной работе или наличии иждивенцев.",
                    icon = Icons.Default.AvTimer,
                    score = metrics.scores.safetyMonthsScore,
                    highlightColor = when {
                        metrics.safetyMonths >= 12.0 -> SuccessGreen
                        metrics.safetyMonths >= 3.0 -> WarningYellow
                        else -> AlertRed
                    },
                    subValue = "Норма: 3-6 мес, Идеал: 12+ мес"
                )
            }
        }

        // 5. Коэффициент задолженности
        if (selectedCategory == "Все" || selectedCategory == "Капитал & Риски") {
            item {
                MetricCard(
                    title = "Коэффициент задолженности",
                    value = Formatters.formatRatio(metrics.debtRatio),
                    formula = "(Пассивы / Активы)",
                    description = "Показывает, какая доля активов финансируется за счет долгов (обязательств), отражая степень финансового риска и зависимости от кредиторов.\n\n" +
                            "• 0,0 - 0,3: Хорошая финансовая устойчивость, большая часть активов финансируется собственными средствами\n" +
                            "• 0,3 - 0,6: Идеальный баланс для большинства, сбалансированное кредитное плечо\n" +
                            "• Больше 0,6: Высокий риск, сильная зависимость от долгов\n" +
                            "• Больше 1,0: Долгов больше, чем активов; очень высокий риск",
                    icon = Icons.Default.AccountBalanceWallet,
                    score = metrics.scores.debtRatioScore,
                    highlightColor = when {
                        metrics.debtRatio <= 0.3 -> SuccessGreen
                        metrics.debtRatio <= 0.6 -> WarningYellow
                        else -> AlertRed
                    },
                    subValue = if (metrics.debtRatio <= 0.3) "Низкий риск (отлично)" else "Высокий риск долгов"
                )
            }
        }

        // 6. Чистый капитал
        if (selectedCategory == "Все" || selectedCategory == "Капитал & Риски") {
            item {
                MetricCard(
                    title = "Чистый капитал",
                    value = Formatters.formatMoney(metrics.netWorth, inputData.currencySymbol),
                    formula = "(Активы - Пассивы)",
                    description = "Общая стоимость активов за вычетом всех обязательств. Это те средства, что останутся у собственника после продажи всех активов и погашения всех долгов.",
                    icon = Icons.Default.AccountBalance,
                    highlightColor = MaterialTheme.colorScheme.primary,
                    subValue = "Активы: ${Formatters.formatMoney(metrics.totalAssets, inputData.currencySymbol)}"
                )
            }
        }

        // 7. Бенчмарк Fidelity
        if (selectedCategory == "Все" || selectedCategory == "Бенчмарки") {
            item {
                val fidelityRatio = if (metrics.fidelityTarget > 0) {
                    (metrics.netWorth / metrics.fidelityTarget).coerceIn(0.0, 1.0).toFloat()
                } else 0f

                MetricCard(
                    title = "Бенчмарк: целевые показатели по Fidelity",
                    value = Formatters.formatMoney(metrics.fidelityTarget, inputData.currencySymbol),
                    formula = "Годовой доход * Множитель",
                    description = "Методика Fidelity показывает, во сколько раз ваши текущие пенсионные накопления должны превышать ваш годовой заработок на определенном жизненном этапе.\n\n" +
                            "Шкала Fidelity дискретна (задана конкретными точками для 30, 40, 50, 60 и 67 лет).\n" +
                            "Пример: Если вам 40 лет и вы зарабатываете ($100,000) в год, то, согласно Fidelity, на ваших инвестиционных и пенсионных счетах должно быть накоплено не менее ($300,000).",
                    icon = Icons.Default.Flag,
                    highlightColor = AccentGold,
                    subValue = "Возраст: ${metrics.currentAge} лет | Множитель: ${Formatters.formatRatio(metrics.fidelityMultiplier)}x",
                    extraContent = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Прогресс к ориентиру Fidelity:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "${(fidelityRatio * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { fidelityRatio },
                                color = AccentGold,
                                trackColor = MaterialTheme.colorScheme.surface,
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                            )
                        }
                    }
                )
            }
        }

        // 8. Коэффициент финансовой свободы (КФС)
        if (selectedCategory == "Все" || selectedCategory == "Бенчмарки") {
            item {
                MetricCard(
                    title = "Коэффициент финансовой свободы",
                    value = Formatters.formatRatio(metrics.financialFreedomRatio),
                    formula = "(Активы / Годовые расходы)",
                    description = "Показывает, сколько годовых расходов покрывает ваш капитал.\n\n" +
                            "• КФС < 0 — Вы в долгах\n" +
                            "• КФС > 0 и КФС <= 0,5 — У вас есть минимальная «подушка безопасности»\n" +
                            "• КФС > 0,5 и КФС <= 2 — Вы начали формировать свой финансовый капитала\n" +
                            "• КФС > 2 и КФС <= 5 — Вы можете начать заниматься долгосрочными инвестициями\n" +
                            "• КФС > 5 и КФС <= 15 — Доходы от капитала могут приносить вам существенную прибавку. Инфляция все более значительно влияет на капитал\n" +
                            "• КФС > 15 и КФС <= 30 — Крепкая финансовая стабильность. На что вы тратите свой невосполнимый капитал времени?\n" +
                            "• КФС > 30 — Вы достигли финансовой независимости",
                    icon = Icons.Default.CompassCalibration,
                    score = metrics.scores.cfsScore,
                    highlightColor = AccentTeal,
                    subValue = metrics.cfsStageDescription
                )
            }
        }

        // 9. Реальная доходность капитала
        if (selectedCategory == "Все" || selectedCategory == "Бенчмарки") {
            item {
                MetricCard(
                    title = "Реальная доходность капитала, %",
                    value = Formatters.formatPercent(metrics.realReturnPercent),
                    formula = "(1 + Номинальная доходность) / (1 + Инфляция) - 1",
                    description = "Фактический прирост ваших сбережений после того, как из начисленной прибыли вычли уровень инфляции. Показывает, на сколько увеличилась ваша реальная покупательная способность.",
                    icon = Icons.Default.Percent,
                    highlightColor = SuccessGreen,
                    subValue = "Номинал: ${Formatters.formatPercent(inputData.nominalReturnPercent)} | Инфляция: ${Formatters.formatPercent(inputData.inflationPercent)}"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
