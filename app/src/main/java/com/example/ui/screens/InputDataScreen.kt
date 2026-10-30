package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinancialMetricEvaluator
import com.example.ui.theme.AlertRed
import com.example.ui.theme.StatusColors
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.util.Formatters
import com.example.util.localization.BreakdownCategory
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings

private data class CurrencyOption(
    val symbol: String,
    val display: String,
    val aliases: List<String> = emptyList()
)

@Composable
fun InputDataScreen(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    onUpdate: ((FinancialInputData) -> FinancialInputData) -> Unit,
    onLoadRussiaAverage: () -> Unit = {},
    onClear: () -> Unit = {},
    onNavigateToMetrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val strings = LocalAppStrings.current

    // Список поддерживаемых валют: базовые ($ на 1-м месте, € на 2-м, ₴ на 4-м, ֏), а новые валюты (GBP, JPY, CHF, CAD, AUD, INR) в конце без трехбуквенных описаний
    val currencies = listOf(
        CurrencyOption("$", "$", listOf("USD")),
        CurrencyOption("€", "€", listOf("EUR")),
        CurrencyOption("₽", "₽", listOf("RUB")),
        CurrencyOption("₴", "₴", listOf("UAH")),
        CurrencyOption("₸", "₸", listOf("KZT")),
        CurrencyOption("Br", "Br", listOf("BYN")),
        CurrencyOption("֏", "֏", listOf("AMD")),
        CurrencyOption("£", "£", listOf("GBP")),
        CurrencyOption("¥", "¥", listOf("JPY")),
        CurrencyOption("CHF", "CHF", listOf("Fr")),
        CurrencyOption("C$", "C$", listOf("CAD")),
        CurrencyOption("A$", "A$", listOf("AUD")),
        CurrencyOption("₹", "₹", listOf("INR"))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Компактная горизонтальная панель выбора валюты
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currencies.forEach { cur ->
                        val isSelected = inputData.currencySymbol == cur.symbol ||
                            cur.aliases.any { it.equals(inputData.currencySymbol, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUpdate { it.copy(currencySymbol = cur.symbol) } },
                            label = { Text(cur.display, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("currency_chip_${cur.symbol}")
                        )
                    }
                }
            }
        }

        // Блок №1: Доходы и Расходы
        item {
            InputSectionCard(
                title = strings.block1Title,
                icon = Icons.Default.AttachMoney,
                subtitle = strings.block1Subtitle
            ) {
                NumberInputField(
                    label = strings.monthlyIncomeLabel,
                    value = inputData.monthlyIncome,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(monthlyIncome = newVal) } },
                    testTag = "input_monthly_income"
                )

                CategoryBreakdownHint(
                    metricName = strings.incomeBreakdownName,
                    hintCollapsed = strings.incomeHintCollapsed,
                    hintExpanded = strings.incomeHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.incomeCategories,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = strings.monthlyExpensesLabel,
                    value = inputData.monthlyExpenses,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(monthlyExpenses = newVal) } },
                    testTag = "input_monthly_expenses"
                )

                CategoryBreakdownHint(
                    metricName = strings.expensesBreakdownName,
                    hintCollapsed = strings.expensesHintCollapsed,
                    hintExpanded = strings.expensesHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.expensesCategories,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Строка 1: Денежный поток по шаблону "Реальная доходность капитала"
                MetricIndicatorRow(
                    title = strings.cashFlowTitle,
                    formula = strings.cashFlowFormula,
                    value = Formatters.formatMoney(metrics.cashFlow, inputData.currencySymbol, lang),
                    valueColor = StatusColors.getColor(FinancialMetricEvaluator.getCashFlowStatus(metrics.cashFlow))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Строка 2: Норма сбережений перенесена на отдельную строку по шаблону
                MetricIndicatorRow(
                    title = strings.savingsRateTitle,
                    formula = strings.savingsRateFormula,
                    value = Formatters.formatPercent(metrics.savingsRate, lang),
                    valueColor = StatusColors.getColor(FinancialMetricEvaluator.getSavingsRateStatus(metrics.savingsRate))
                )
            }
        }

        // Блок №2: Активы (Σ)
        item {
            InputSectionCard(
                title = strings.block2Title,
                icon = Icons.Default.AccountBalance,
                subtitle = strings.block2Subtitle
            ) {
                NumberInputField(
                    label = strings.liquidAssetsLabel,
                    value = inputData.liquidAssets,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(liquidAssets = newVal) } },
                    testTag = "input_liquid_assets"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = strings.financialAssetsLabel,
                    value = inputData.financialAssets,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(financialAssets = newVal) } },
                    testTag = "input_financial_assets"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = strings.propertyAssetsLabel,
                    value = inputData.propertyAssets,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(propertyAssets = newVal) } },
                    testTag = "input_property_assets"
                )

                CategoryBreakdownHint(
                    metricName = strings.assetsBreakdownName,
                    hintCollapsed = strings.assetsHintCollapsed,
                    hintExpanded = strings.assetsHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.assetsCategories,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Расчет Активы (Σ) по единому шаблону
                MetricIndicatorRow(
                    title = strings.totalAssetsTitle,
                    formula = strings.totalAssetsFormula,
                    value = Formatters.formatMoney(metrics.totalAssets, inputData.currencySymbol, lang),
                    valueColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Переключатель включения финансовых активов в подушку безопасности
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.toggleEmergencyFundTitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = if (inputData.includeFinancialInEmergencyFund) strings.toggleEmergencyFundSubOn else strings.toggleEmergencyFundSubOff,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = inputData.includeFinancialInEmergencyFund,
                        onCheckedChange = { checked -> onUpdate { it.copy(includeFinancialInEmergencyFund = checked) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("toggle_emergency_fund_mode")
                    )
                }
            }
        }

        // Блок №3: Пассивы
        item {
            InputSectionCard(
                title = strings.block3Title,
                icon = Icons.Default.TrendingUp,
                subtitle = strings.block3Subtitle
            ) {
                NumberInputField(
                    label = strings.liabilitiesLabel,
                    value = inputData.liabilities,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(liabilities = newVal) } },
                    testTag = "input_liabilities"
                )

                CategoryBreakdownHint(
                    metricName = strings.liabilitiesBreakdownName,
                    hintCollapsed = strings.liabilitiesHintCollapsed,
                    hintExpanded = strings.liabilitiesHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.liabilitiesCategories,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Расчет формулы для "Чистый капитал" (Активы - Пассивы)
                MetricIndicatorRow(
                    title = strings.netWorthTitle,
                    formula = strings.netWorthFormula,
                    value = Formatters.formatMoney(metrics.netWorth, inputData.currencySymbol, lang),
                    valueColor = StatusColors.getColor(FinancialMetricEvaluator.getNetWorthStatus(metrics.netWorth))
                )
            }
        }

        // Блок №4: Инвестиции & Инфляция
        item {
            InputSectionCard(
                title = strings.block4Title,
                icon = Icons.Default.Percent,
                subtitle = strings.block4Subtitle
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        NumberInputField(
                            label = strings.nominalReturnLabel,
                            value = inputData.nominalReturnPercent,
                            currency = "%",
                            onValueChange = { newVal -> onUpdate { it.copy(nominalReturnPercent = newVal) } },
                            testTag = "input_nominal_return"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NumberInputField(
                            label = strings.inflationLabel,
                            value = inputData.inflationPercent,
                            currency = "%",
                            onValueChange = { newVal -> onUpdate { it.copy(inflationPercent = newVal) } },
                            testTag = "input_inflation"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = strings.capitalGainsTaxLabel,
                    value = inputData.capitalGainsTaxPercent,
                    currency = "%",
                    onValueChange = { newVal -> onUpdate { it.copy(capitalGainsTaxPercent = newVal) } },
                    testTag = "input_capital_gains_tax"
                )

                CategoryBreakdownHint(
                    metricName = strings.investmentsBreakdownName,
                    hintCollapsed = strings.investmentsHintCollapsed,
                    hintExpanded = strings.investmentsHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.investmentsCategories,
                    modifier = Modifier.padding(top = 6.dp)
                )

                CategoryBreakdownHint(
                    metricName = strings.capitalGainsTaxBreakdownName,
                    hintCollapsed = strings.capitalGainsTaxHintCollapsed,
                    hintExpanded = strings.capitalGainsTaxHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.capitalGainsTaxCategories,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Шаблон отображения "Реальная доходность капитала"
                MetricIndicatorRow(
                    title = strings.realReturnTitle,
                    formula = strings.realReturnFormula,
                    value = Formatters.formatPercent(metrics.realReturnPercent, lang),
                    valueColor = StatusColors.getColor(FinancialMetricEvaluator.getRealReturnStatus(metrics.realReturnPercent))
                )
            }
        }

        // Блок №5: Возраст & Горизонт FIRE
        item {
            InputSectionCard(
                title = strings.block5Title,
                icon = Icons.Default.CalendarMonth,
                subtitle = strings.block5Subtitle
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    OutlinedTextField(
                        value = inputData.birthDate,
                        onValueChange = { newDate -> onUpdate { it.copy(birthDate = newDate) } },
                        label = { Text(strings.birthDateLabel) },
                        isError = !metrics.isAgeValid,
                        supportingText = if (!metrics.isAgeValid) {
                            { Text(strings.invalidBirthDateError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("input_birth_date")
                    )

                    OutlinedTextField(
                        value = inputData.lifeExpectancy.toString(),
                        onValueChange = { str ->
                            val v = str.toIntOrNull() ?: inputData.lifeExpectancy
                            onUpdate { it.copy(lifeExpectancy = v) }
                        },
                        label = { Text(strings.lifeExpectancyLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("input_life_expectancy")
                    )
                }

                CategoryBreakdownHint(
                    metricName = strings.fireHorizonBreakdownName,
                    hintCollapsed = strings.fireHorizonHintCollapsed,
                    hintExpanded = strings.fireHorizonHintExpanded,
                    instruction = strings.breakdownInstruction,
                    categories = strings.fireHorizonCategories,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Отображение горизонта дожития по единому шаблону
                MetricIndicatorRow(
                    title = strings.horizonTitle,
                    formula = strings.horizonFormula,
                    value = strings.horizonValueFormat(metrics.remainingYearsHorizon, metrics.currentAge),
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Кнопка перехода к расчету показателей
        item {
            Button(
                onClick = onNavigateToMetrics,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("go_to_metrics_button")
            ) {
                Text(
                    text = strings.calculateButtonText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Единый шаблон строки вывода показателей (как в блоке "Реальная доходность капитала"):
 * Слева: название показателя и формула.
 * Справа: форматированное значение.
 */
@Composable
fun MetricIndicatorRow(
    title: String,
    formula: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = formula,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            )
        }
    }
}

@Composable
fun InputSectionCard(
    title: String,
    icon: ImageVector,
    subtitle: String,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun NumberInputField(
    label: String,
    value: Double,
    currency: String,
    onValueChange: (Double) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) {
        mutableStateOf(if (value == 0.0) "" else Formatters.formatNumber(value))
    }

    OutlinedTextField(
        value = textValue,
        onValueChange = { inputStr ->
            textValue = inputStr
            val parsed = Formatters.parseDouble(inputStr)
            onValueChange(parsed)
        },
        label = { Text(label, fontSize = 12.sp) },
        trailingIcon = {
            Text(
                text = currency,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(end = 12.dp)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

/**
 * Раскрывающаяся подсказка со списком статей и категорий (из вкладок 2-5 файла).
 * Напоминает пользователю самостоятельно сложить и подсчитать свои статьи и ввести итоговую сумму.
 */
@Composable
fun CategoryBreakdownHint(
    metricName: String,
    hintCollapsed: String,
    hintExpanded: String,
    instruction: String,
    categories: List<BreakdownCategory>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExpanded) hintExpanded else hintCollapsed,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Важная плашка с напоминанием о самостоятельном подсчете
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = strings.breakdownInstructionIcon,
                            fontSize = 12.sp
                        )
                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Список статей по группам
                categories.forEachIndexed { index, cat ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = strings.breakdownCategoryTitleFormat(cat.title),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                        )
                        cat.items.forEach { itemText ->
                            Text(
                                text = strings.breakdownCategoryItemFormat(itemText),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                ),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                    if (index < categories.size - 1) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}
