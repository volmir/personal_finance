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
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.util.Formatters

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
    // Доллар ($) на 1-м месте, Евро (€) на 2-м месте, Гривна (₴) на 4-м месте
    val currencies = listOf("$", "€", "₽", "₴", "₸", "Br")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Компактная панель выбора валюты
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Валюты: $, €, ₽, ₴ (№4), ₸, Br
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currencies.forEach { cur ->
                        FilterChip(
                            selected = inputData.currencySymbol == cur,
                            onClick = { onUpdate { it.copy(currencySymbol = cur) } },
                            label = { Text(cur, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("currency_chip_$cur")
                        )
                    }
                }
            }
        }

        // Блок №1: Доходы и Расходы
        item {
            InputSectionCard(
                title = "1. Доходы и Расходы",
                icon = Icons.Default.AttachMoney,
                subtitle = "Ежемесячный приток и отток денежных средств"
            ) {
                NumberInputField(
                    label = "Доходы (в месяц)",
                    value = inputData.monthlyIncome,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(monthlyIncome = newVal) } },
                    testTag = "input_monthly_income"
                )

                CategoryBreakdownHint(
                    metricName = "Доходы",
                    categories = listOf(
                        "Заработная плата" to listOf(
                            "Основная работа (оклад, премия, бонусы)",
                            "Вторая работа, совместительство"
                        ),
                        "Доход от бизнеса и самозанятости" to listOf(
                            "Прибыль от собственного бизнеса / ИП",
                            "Фриланс, подработки, консалтинг, разовые услуги"
                        ),
                        "Пассивный и инвестиционный доход" to listOf(
                            "Проценты по банковским вкладам и накопительным счетам",
                            "Дивиденды по акциям и купоны по облигациям",
                            "Доход от сдачи недвижимости или транспорта в аренду",
                            "Роялти, авторские права"
                        ),
                        "Социальные и прочие выплаты" to listOf(
                            "Пенсии, государственные пособия, субсидии",
                            "Алименты, регулярная помощь родственников",
                            "Налоговые вычеты, кэшбэк"
                        )
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = "Расходы (в месяц)",
                    value = inputData.monthlyExpenses,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(monthlyExpenses = newVal) } },
                    testTag = "input_monthly_expenses"
                )

                CategoryBreakdownHint(
                    metricName = "Расходы",
                    categories = listOf(
                        "Базовые обязательные расходы" to listOf(
                            "Продукты питания и бытовая химия",
                            "Аренда жилья или ипотечные платежи",
                            "Коммунальные услуги (ЖКХ, электричество, вода)",
                            "Связь, интернет, мобильные подписки"
                        ),
                        "Транспорт и логистика" to listOf(
                            "Общественный транспорт, проездные",
                            "Топливо, парковка, мойка",
                            "ТО, автостраховка (ОСАГО/КАСКО), ремонт",
                            "Такси, каршеринг"
                        ),
                        "Здоровье и личные нужды" to listOf(
                            "Медикаменты, врачи, стоматология, ДМС",
                            "Одежда, обувь, средства ухода",
                            "Спорт, фитнес-абонементы, секции"
                        ),
                        "Досуг, образование и семья" to listOf(
                            "Кафе, рестораны, доставка еды",
                            "Развлечения, кино, путешествия, хобби",
                            "Обучение, курсы, книги, кружки детей",
                            "Подарки, благотворительность, непредвиденные траты"
                        ),
                        "Обслуживание долгов" to listOf(
                            "Ежемесячные платежи по потребительским кредитам и кредитным картам"
                        )
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Строка 1: Денежный поток по шаблону "Реальная доходность капитала"
                MetricIndicatorRow(
                    title = "Денежный поток:",
                    formula = "(Доходы - Расходы)",
                    value = Formatters.formatMoney(metrics.cashFlow, inputData.currencySymbol),
                    valueColor = if (metrics.cashFlow >= 0) SuccessGreen else AlertRed
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Строка 2: Норма сбережений перенесена на отдельную строку по шаблону
                MetricIndicatorRow(
                    title = "Норма сбережений:",
                    formula = "(Денежный поток / Доходы) * 100%",
                    value = Formatters.formatPercent(metrics.savingsRate),
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Блок №2: Активы (Σ)
        item {
            InputSectionCard(
                title = "2. Активы (Σ)",
                icon = Icons.Default.AccountBalance,
                subtitle = "Ликвидные, финансовые и имущественные накопления"
            ) {
                NumberInputField(
                    label = "Ликвидные (наличные, вклады до востребования)",
                    value = inputData.liquidAssets,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(liquidAssets = newVal) } },
                    testTag = "input_liquid_assets"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = "Финансовые (акции, облигации, фонды, брокерские счета)",
                    value = inputData.financialAssets,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(financialAssets = newVal) } },
                    testTag = "input_financial_assets"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NumberInputField(
                    label = "Имущественные (недвижимость, автомобили, ценности)",
                    value = inputData.propertyAssets,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(propertyAssets = newVal) } },
                    testTag = "input_property_assets"
                )

                CategoryBreakdownHint(
                    metricName = "Активы",
                    categories = listOf(
                        "Ликвидные активы" to listOf(
                            "Наличные деньги в любой валюте",
                            "Остатки на текущих и дебетовых картах",
                            "Накопительные счета и вклады до востребования",
                            "Краткосрочные депозиты (до 1-3 месяцев)"
                        ),
                        "Финансовые (инвестиционные) активы" to listOf(
                            "Акции, ETF, паевые инвестиционные фонды (ПИФ)",
                            "Облигации (ОФЗ, корпоративные, еврооблигации)",
                            "Средства на брокерских счетах и ИИС",
                            "Срочные долгосрочные банковские вклады",
                            "Драгоценные металлы (ОМС, слитки, монеты)",
                            "Криптовалюты и цифровые активы",
                            "Пенсионные накопления в НПФ"
                        ),
                        "Имущественные (материальные) активы" to listOf(
                            "Жилая недвижимость (квартира, дом, дача)",
                            "Коммерческая недвижимость, земельные участки, гаражи",
                            "Транспортные средства (автомобиль, мотоцикл и др.)",
                            "Оборудование, антиквариат, ювелирные ценности",
                            "Доля в реальном бизнесе (оценочная стоимость)"
                        )
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Расчет Активы (Σ) по единому шаблону
                MetricIndicatorRow(
                    title = "Активы (Σ):",
                    formula = "(Ликвидные + Финансовые + Имущественные)",
                    value = Formatters.formatMoney(metrics.totalAssets, inputData.currencySymbol),
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
                            text = "Включать финансовые активы в подушку",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = if (inputData.includeFinancialInEmergencyFund) "Подушка = Ликвидные + Финансовые" else "Подушка = Только Ликвидные",
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
                title = "3. Пассивы",
                icon = Icons.Default.TrendingUp,
                subtitle = "Обязательства, кредиты, долги и ипотека"
            ) {
                NumberInputField(
                    label = "Пассивы (все долги и обязательства)",
                    value = inputData.liabilities,
                    currency = inputData.currencySymbol,
                    onValueChange = { newVal -> onUpdate { it.copy(liabilities = newVal) } },
                    testTag = "input_liabilities"
                )

                CategoryBreakdownHint(
                    metricName = "Пассивы",
                    categories = listOf(
                        "Залоговые и долгосрочные обязательства" to listOf(
                            "Остаток основного долга по ипотеке (квартира, дом)",
                            "Автокредит (остаток задолженности)",
                            "Кредиты под залог имущества"
                        ),
                        "Потребительские и краткосрочные кредиты" to listOf(
                            "Потребительские кредиты наличными",
                            "Задолженность по кредитным картам и картам рассрочки",
                            "Рассрочки, BNPL-сервисы, займы в МФО"
                        ),
                        "Личные и частные долги" to listOf(
                            "Долги перед родственниками, друзьями или коллегами",
                            "Частные займы под расписку"
                        ),
                        "Прочие финансовые обязательства" to listOf(
                            "Задолженность по налогам, штрафам, судебным искам",
                            "Долги по коммунальным платежам (ЖКХ)"
                        )
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Расчет формулы для "Чистый капитал" (Активы - Пассивы) перенесен на новую строку по примеру блока №4
                MetricIndicatorRow(
                    title = "Чистый капитал:",
                    formula = "(Активы - Пассивы)",
                    value = Formatters.formatMoney(metrics.netWorth, inputData.currencySymbol),
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Блок №4: Инвестиции & Инфляция
        item {
            InputSectionCard(
                title = "4. Инвестиции & Инфляция",
                icon = Icons.Default.Percent,
                subtitle = "Параметры доходности капитала и покупательской способности"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        NumberInputField(
                            label = "Номинальная %",
                            value = inputData.nominalReturnPercent,
                            currency = "%",
                            onValueChange = { newVal -> onUpdate { it.copy(nominalReturnPercent = newVal) } },
                            testTag = "input_nominal_return"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NumberInputField(
                            label = "Инфляция %",
                            value = inputData.inflationPercent,
                            currency = "%",
                            onValueChange = { newVal -> onUpdate { it.copy(inflationPercent = newVal) } },
                            testTag = "input_inflation"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Шаблон отображения "Реальная доходность капитала"
                MetricIndicatorRow(
                    title = "Реальная доходность капитала:",
                    formula = "(1 + Номинальная доходность) / (1 + Инфляция) - 1",
                    value = Formatters.formatPercent(metrics.realReturnPercent),
                    valueColor = SuccessGreen
                )
            }
        }

        // Блок №5: Возраст & Горизонт FIRE
        item {
            InputSectionCard(
                title = "5. Возраст & Горизонт FIRE",
                icon = Icons.Default.CalendarMonth,
                subtitle = "Параметры для расчета выхода на пенсию"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = inputData.birthDate,
                        onValueChange = { newDate -> onUpdate { it.copy(birthDate = newDate) } },
                        label = { Text("Дата рождения (ГГГГ-ММ-ДД)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
                        label = { Text("Дожитие (лет)") },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Отображение горизонта дожития по единому шаблону
                MetricIndicatorRow(
                    title = "Горизонт дожития:",
                    formula = "(Возраст дожития - Текущий возраст)",
                    value = "${metrics.remainingYearsHorizon} года (возраст ${metrics.currentAge} лет)",
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Кнопка перехода к расчету показателей и кнопка экспорта (в одну строку без переносов)
        item {
            val context = LocalContext.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateToMetrics,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("go_to_metrics_button")
                ) {
                    Text(
                        text = "Рассчитать показатели",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                OutlinedButton(
                    onClick = {
                        val exportText = AuditorAnalysisGenerator.generateExportSummary(inputData, metrics)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, exportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Экспорт финансовых данных"))
                    },
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .testTag("export_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Экспорт",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Экспорт",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }
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
    categories: List<Pair<String, List<String>>>,
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
                        text = if (isExpanded) "Скрыть структуру показателя «$metricName»" else "Что входит в «$metricName»? (подсказка)",
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
                            text = "💡 ",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Посчитайте и сложите ваши статьи из списка ниже самостоятельно, а полученную итоговую сумму укажите в поле выше.",
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
                categories.forEachIndexed { index, (catTitle, items) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "• $catTitle",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                        )
                        items.forEach { itemText ->
                            Text(
                                text = "    – $itemText",
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
