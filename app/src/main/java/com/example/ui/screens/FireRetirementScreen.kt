package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.TrajectoryPoint
import com.example.domain.SimulatorScaleCalculator
import com.example.ui.FireSimulationState
import com.example.ui.components.FireTrajectoryChart
import com.example.ui.components.ScenarioTogglesPanel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.util.Formatters

@Composable
fun FireRetirementScreen(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    simulatedMetrics: CalculatedMetrics,
    simulationState: FireSimulationState,
    trajectory: List<TrajectoryPoint>,
    onUpdateSimulation: (extraSavings: Double, realReturnDelta: Double, expReduction: Double) -> Unit,
    onToggleScenario: (scenarioId: String) -> Unit,
    onResetSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSimulating = simulationState.isSimulating

    val displayMetrics = if (isSimulating) simulatedMetrics else metrics

    // Динамический расчет шкал и шагов симулятора исходя из текущих значений показателей
    val savingsConfig = remember(metrics.cashFlow, inputData.monthlyIncome, inputData.monthlyExpenses) {
        SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = metrics.cashFlow,
            monthlyIncome = inputData.monthlyIncome,
            monthlyExpenses = inputData.monthlyExpenses
        )
    }

    val expensesConfig = remember(inputData.monthlyExpenses, inputData.monthlyIncome) {
        SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = inputData.monthlyExpenses,
            monthlyIncome = inputData.monthlyIncome
        )
    }

    val returnConfig = remember(metrics.realReturnPercent) {
        SimulatorScaleCalculator.calculateReturnConfig(
            realReturnPercent = metrics.realReturnPercent
        )
    }

    // Текущие динамические значения для симулятора (синхронно учитывают накопленный результат чекбоксов и ползунков)
    val currentEffectiveSavings = if (isSimulating) simulatedMetrics.cashFlow else metrics.cashFlow
    val currentEffectiveExpenses = if (isSimulating) (simulatedMetrics.annualExpenses / 12.0) else inputData.monthlyExpenses
    val currentEffectiveReturn = if (isSimulating) simulatedMetrics.realReturnPercent else metrics.realReturnPercent

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Главный баннер FIRE
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fire_hero_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Финансовая свобода, пенсия, FIRE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Расчет срока до выхода на пенсию",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "До пенсии осталось:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = Formatters.formatRatio(displayMetrics.yearsToRetirement),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        )
                        Text(
                            text = " года (лет)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(bottom = 8.dp, start = 6.dp)
                        )
                    }

                    Text(
                        text = "(с учетом будущих сбережений, инфляции и доходности капитала)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Возраст выхода на пенсию:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = "${Formatters.formatRatio(displayMetrics.retirementAge)} лет",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Требуемый целевой капитал:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = Formatters.formatMoney(displayMetrics.retirementTargetCapital, inputData.currencySymbol),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Параметры возраста и горизонта
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fire_parameters_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Параметры возраста и горизонта",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Дата рождения:", style = MaterialTheme.typography.bodySmall)
                        Text(inputData.birthDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Текущий возраст:", style = MaterialTheme.typography.bodySmall)
                        Text("${metrics.currentAge} лет", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Возраст дожития:", style = MaterialTheme.typography.bodySmall)
                        Text("${metrics.lifeExpectancy} лет", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("(Возраст дожития - Текущий возраст):", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${metrics.remainingYearsHorizon} года",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Реальная доходность капитала:", style = MaterialTheme.typography.bodySmall)
                        Text(
                            Formatters.formatPercent(metrics.realReturnPercent),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }

        // График траектории капитала
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Траектория накопления капитала",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Рост капитала до точки FIRE и обеспечение жизни на пенсии",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FireTrajectoryChart(
                        trajectory = trajectory,
                        yearsToRetire = displayMetrics.yearsToRetirement,
                        currentAge = metrics.currentAge,
                        currencySymbol = inputData.currencySymbol
                    )

                    // Блок "Итог симуляции (совместное влияние)" непосредственно под сам график
                    if (isSimulating) {
                        val yearsDiff = metrics.yearsToRetirement - simulatedMetrics.yearsToRetirement
                        val isPositive = yearsDiff > 0
                        val isNegative = yearsDiff < 0
                        val alertColor = if (isPositive) SuccessGreen else if (isNegative) AlertRed else MaterialTheme.colorScheme.primary

                        val activeScenariosCount = simulationState.scenarios.activeCount
                        val hasSliders = simulationState.extraMonthlySavings != 0.0 ||
                                simulationState.expenseReduction != 0.0 ||
                                simulationState.realReturnAdjustment != 0.0

                        val badgeText = Formatters.formatSimulationBadge(activeScenariosCount, hasSliders)

                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = alertColor.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fire_simulation_summary")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Итог симуляции (совместное влияние)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = alertColor
                                        ),
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = alertColor.copy(alpha = 0.20f)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = alertColor,
                                                fontSize = 10.sp
                                            ),
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (isPositive) {
                                        "⚡ Пенсия наступит на ${Formatters.formatRatio(yearsDiff)} года раньше (через ${Formatters.formatRatio(simulatedMetrics.yearsToRetirement)} лет, в возрасте ${Formatters.formatRatio(simulatedMetrics.retirementAge)} лет)!"
                                    } else if (isNegative) {
                                        "⏳ Срок до пенсии увеличится на ${Formatters.formatRatio(-yearsDiff)} года (через ${Formatters.formatRatio(simulatedMetrics.yearsToRetirement)} лет, в возрасте ${Formatters.formatRatio(simulatedMetrics.retirementAge)} лет)."
                                    } else {
                                        "Срок выхода на пенсию остается прежним (через ${Formatters.formatRatio(simulatedMetrics.yearsToRetirement)} лет)."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = alertColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Сводные строки кумулятивного пересчета
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Денежный поток (сбережения):", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                    Text(
                                        "${Formatters.formatMoney(metrics.cashFlow, inputData.currencySymbol)} ➔ ${Formatters.formatMoney(simulatedMetrics.cashFlow, inputData.currencySymbol)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = if (simulatedMetrics.cashFlow >= 0) MaterialTheme.colorScheme.onSurface else AlertRed
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Ежемесячные расходы:", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                    Text(
                                        "${Formatters.formatMoney(inputData.monthlyExpenses, inputData.currencySymbol)} ➔ ${Formatters.formatMoney(simulatedMetrics.annualExpenses / 12.0, inputData.currencySymbol)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Чистый капитал (активы − долги):", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                    Text(
                                        "${Formatters.formatMoney(metrics.netWorth, inputData.currencySymbol)} ➔ ${Formatters.formatMoney(simulatedMetrics.netWorth, inputData.currencySymbol)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = if (simulatedMetrics.netWorth >= metrics.netWorth) SuccessGreen else AlertRed
                                        )
                                    )
                                }
                                val futurePropertySum = (if (simulationState.scenarios.carLoan5Years) 12.0 * inputData.monthlyIncome else 0.0) +
                                        (if (simulationState.scenarios.mortgage20Years35Income) 84.0 * inputData.monthlyIncome else 0.0)
                                if (futurePropertySum > 0.0) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Прирост имущества (по окончании выплат):", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                        Text(
                                            "+${Formatters.formatMoney(futurePropertySum, inputData.currencySymbol)}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp,
                                                color = SuccessGreen
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Интерактивный симулятор "Что если?"
        // Во всех трех пунктах справа отображаются текущие значения, изменяемые ползунками!
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fire_simulator_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Симулятор «Что если?»",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (isSimulating) {
                            OutlinedButton(
                                onClick = onResetSimulation,
                                contentPadding = ButtonDefaults.ContentPadding,
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Сброс", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Тонкая настройка шкал (ползунки):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Пункт 1: Ежемесячные сбережения (позволяет как повышать, так и понижать)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ежемесячные сбережения:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                                if (isSimulating) {
                                    val netSavingsDiff = currentEffectiveSavings - metrics.cashFlow
                                    if (kotlin.math.abs(netSavingsDiff) > 0.01) {
                                        val isSavingsPositive = netSavingsDiff > 0
                                        Text(
                                            text = buildString {
                                                append(if (isSavingsPositive) "+" else "")
                                                append(Formatters.formatMoney(netSavingsDiff, inputData.currencySymbol))
                                                append(" к базовым")
                                                val incFactors = mutableListOf<String>()
                                                if (simulationState.scenarios.jobLoss) incFactors.add("потеря работы")
                                                if (simulationState.scenarios.salaryIncrease20) incFactors.add("зарплата +20%")
                                                if (simulationState.extraMonthlySavings != 0.0) {
                                                    incFactors.add("ползунок: ${if (simulationState.extraMonthlySavings > 0) "+" else ""}${Formatters.formatMoney(simulationState.extraMonthlySavings, inputData.currencySymbol)}")
                                                }
                                                if (incFactors.isNotEmpty()) {
                                                    append(" (")
                                                    append(incFactors.joinToString(", "))
                                                    append(")")
                                                }
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSavingsPositive) SuccessGreen else AlertRed,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                            Text(
                                text = Formatters.formatMoney(currentEffectiveSavings, inputData.currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentEffectiveSavings >= 0) MaterialTheme.colorScheme.primary else AlertRed
                                )
                            )
                        }
                        Slider(
                            value = simulationState.extraMonthlySavings.toFloat()
                                .coerceIn(savingsConfig.minValue, savingsConfig.maxValue),
                            onValueChange = { rawValue ->
                                val snapped = kotlin.math.round(rawValue / savingsConfig.stepSize) * savingsConfig.stepSize
                                onUpdateSimulation(
                                    snapped,
                                    simulationState.realReturnAdjustment,
                                    simulationState.expenseReduction
                                )
                            },
                            valueRange = savingsConfig.minValue..savingsConfig.maxValue,
                            steps = savingsConfig.steps,
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("fire_slider_savings")
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "от ${Formatters.formatMoney(savingsConfig.minValue.toDouble(), inputData.currencySymbol)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "шаг ${Formatters.formatMoney(savingsConfig.stepSize, inputData.currencySymbol)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "до +${Formatters.formatMoney(savingsConfig.maxValue.toDouble(), inputData.currencySymbol)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Пункт 2: Ежемесячные расходы (позволяет как понижать, так и повышать)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ежемесячные расходы:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                                if (isSimulating) {
                                    val netExpChange = currentEffectiveExpenses - inputData.monthlyExpenses
                                    if (kotlin.math.abs(netExpChange) > 0.01) {
                                        val isReduced = netExpChange < 0
                                        Text(
                                            text = buildString {
                                                append(if (netExpChange > 0) "+" else "")
                                                append(Formatters.formatMoney(netExpChange, inputData.currencySymbol))
                                                append(if (isReduced) " (снижение)" else " (рост)")
                                                val factors = mutableListOf<String>()
                                                if (simulationState.scenarios.expenseOptimization15) factors.add("оптимизация -15%")
                                                if (simulationState.scenarios.relocationLowerCost30) factors.add("переезд -30%")
                                                if (simulationState.scenarios.mortgage20Years35Income) factors.add("ипотека 35% дохода (20 л.)")
                                                if (simulationState.scenarios.carLoan5Years) factors.add("автокредит 20% дохода (5 л.)")
                                                if (simulationState.scenarios.inflation20Expenses) factors.add("инфляция +20%")
                                                if (simulationState.scenarios.childOrDependent18Years) factors.add("ребёнок +25% (18 л.)")
                                                if (simulationState.expenseReduction != 0.0) {
                                                    val red = simulationState.expenseReduction
                                                    factors.add("ползунок ${if (red > 0) "-" else "+"}${Formatters.formatMoney(kotlin.math.abs(red), inputData.currencySymbol)}")
                                                }
                                                if (factors.isNotEmpty()) {
                                                    append(" • ")
                                                    append(factors.joinToString(", "))
                                                }
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isReduced) SuccessGreen else AlertRed,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                            Text(
                                text = Formatters.formatMoney(currentEffectiveExpenses, inputData.currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentEffectiveExpenses < inputData.monthlyExpenses) SuccessGreen
                                    else if (currentEffectiveExpenses > inputData.monthlyExpenses) AlertRed
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Slider(
                            value = simulationState.expenseReduction.toFloat()
                                .coerceIn(expensesConfig.minValue, expensesConfig.maxValue),
                            onValueChange = { rawValue ->
                                val snapped = kotlin.math.round(rawValue / expensesConfig.stepSize) * expensesConfig.stepSize
                                onUpdateSimulation(
                                    simulationState.extraMonthlySavings,
                                    simulationState.realReturnAdjustment,
                                    snapped
                                )
                            },
                            valueRange = expensesConfig.minValue..expensesConfig.maxValue,
                            steps = expensesConfig.steps,
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("fire_slider_expenses")
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${Formatters.formatMoney(expensesConfig.minValue.toDouble(), inputData.currencySymbol)} (рост)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "шаг ${Formatters.formatMoney(expensesConfig.stepSize, inputData.currencySymbol)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "+${Formatters.formatMoney(expensesConfig.maxValue.toDouble(), inputData.currencySymbol)} (сниж.)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Пункт 3: Реальная доходность (отображает текущее значение доходности)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Реальная доходность капитала:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                                if (simulationState.realReturnAdjustment != 0.0) {
                                    Text(
                                        text = "${if (simulationState.realReturnAdjustment > 0) "+" else ""}${Formatters.formatRatio(simulationState.realReturnAdjustment)}% к базовой",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = Formatters.formatPercent(currentEffectiveReturn),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Slider(
                            value = simulationState.realReturnAdjustment.toFloat()
                                .coerceIn(returnConfig.minValue, returnConfig.maxValue),
                            onValueChange = { rawValue ->
                                val snapped = kotlin.math.round(rawValue / returnConfig.stepSize) * returnConfig.stepSize
                                onUpdateSimulation(
                                    simulationState.extraMonthlySavings,
                                    snapped,
                                    simulationState.expenseReduction
                                )
                            },
                            valueRange = returnConfig.minValue..returnConfig.maxValue,
                            steps = returnConfig.steps,
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("fire_slider_return")
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "от ${Formatters.formatRatio(returnConfig.minValue.toDouble())}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "шаг ${Formatters.formatRatio(returnConfig.stepSize)}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "до +${Formatters.formatRatio(returnConfig.maxValue.toDouble())}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Панель с переключателями сценариев (стресс-тесты)
                    ScenarioTogglesPanel(
                        scenarios = simulationState.scenarios,
                        inputData = inputData,
                        onToggleScenario = onToggleScenario
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
