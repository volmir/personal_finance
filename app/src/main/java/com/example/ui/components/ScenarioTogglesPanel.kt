package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialInputData
import com.example.ui.FireScenarioToggles
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellowBg
import com.example.util.Formatters
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings

private data class ScenarioItemData(
    val id: String,
    val number: Int,
    val title: String,
    val parametersText: String,
    val description: String,
    val icon: ImageVector,
    val isChecked: Boolean,
    val isPositive: Boolean
)

@Composable
fun ScenarioTogglesPanel(
    scenarios: FireScenarioToggles,
    inputData: FinancialInputData,
    onToggleScenario: (scenarioId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val strings = LocalAppStrings.current

    val positiveItems = listOf(
        ScenarioItemData(
            id = "pos_salary_increase",
            number = 1,
            title = strings.scenarioSalaryIncreaseTitle,
            parametersText = strings.scenarioSalaryIncreaseParam,
            description = strings.scenarioSalaryIncreaseDesc(
                Formatters.formatMoney(inputData.monthlyIncome * 0.20, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.TrendingUp,
            isChecked = scenarios.salaryIncrease20,
            isPositive = true
        ),
        ScenarioItemData(
            id = "pos_market_growth",
            number = 2,
            title = strings.scenarioMarketGrowthTitle,
            parametersText = strings.scenarioMarketGrowthParam,
            description = strings.scenarioMarketGrowthDesc(
                Formatters.formatMoney(inputData.financialAssets * 0.25, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.ShowChart,
            isChecked = scenarios.marketGrowth25,
            isPositive = true
        ),
        ScenarioItemData(
            id = "pos_expense_optimization",
            number = 3,
            title = strings.scenarioExpenseOptTitle,
            parametersText = strings.scenarioExpenseOptParam,
            description = strings.scenarioExpenseOptDesc(
                Formatters.formatMoney(inputData.monthlyExpenses * 0.15, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.Savings,
            isChecked = scenarios.expenseOptimization15,
            isPositive = true
        ),
        ScenarioItemData(
            id = "pos_relocation",
            number = 4,
            title = strings.scenarioRelocationTitle,
            parametersText = strings.scenarioRelocationParam,
            description = strings.scenarioRelocationDesc(
                Formatters.formatMoney(inputData.monthlyExpenses * 0.30, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.LocationCity,
            isChecked = scenarios.relocationLowerCost30,
            isPositive = true
        ),
        ScenarioItemData(
            id = "pos_inheritance",
            number = 5,
            title = strings.scenarioInheritanceTitle,
            parametersText = strings.scenarioInheritanceParam,
            description = strings.scenarioInheritanceDesc(
                Formatters.formatMoney(12.0 * inputData.monthlyIncome, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.Redeem,
            isChecked = scenarios.inheritance1200,
            isPositive = true
        ),
        ScenarioItemData(
            id = "pos_property_sale",
            number = 6,
            title = strings.scenarioPropertySaleTitle,
            parametersText = strings.scenarioPropertySaleParam,
            description = strings.scenarioPropertySaleDesc(
                Formatters.formatMoney(20.0 * inputData.monthlyIncome, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.AccountBalance,
            isChecked = scenarios.propertySale2000,
            isPositive = true
        ),
        ScenarioItemData(
            id = "pos_children_self_sufficient",
            number = 7,
            title = strings.scenarioChildrenSelfSufficientTitle,
            parametersText = strings.scenarioChildrenSelfSufficientParam,
            description = strings.scenarioChildrenSelfSufficientDesc(
                Formatters.formatMoney(inputData.monthlyExpenses * 0.25, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.School,
            isChecked = scenarios.childrenSelfSufficient18,
            isPositive = true
        )
    )

    val negativeItems = listOf(
        ScenarioItemData(
            id = "job_loss",
            number = 1,
            title = strings.scenarioJobLossTitle,
            parametersText = strings.scenarioJobLossParam,
            description = strings.scenarioJobLossDesc,
            icon = Icons.Default.WorkOff,
            isChecked = scenarios.jobLoss,
            isPositive = false
        ),
        ScenarioItemData(
            id = "market_crash",
            number = 2,
            title = strings.scenarioMarketCrashTitle,
            parametersText = strings.scenarioMarketCrashParam,
            description = strings.scenarioMarketCrashDesc(
                Formatters.formatMoney(inputData.financialAssets * 0.3, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.TrendingDown,
            isChecked = scenarios.marketCrash30,
            isPositive = false
        ),
        ScenarioItemData(
            id = "mortgage",
            number = 3,
            title = strings.scenarioMortgageTitle,
            parametersText = strings.scenarioMortgageParam,
            description = strings.scenarioMortgageDesc(
                Formatters.formatMoney(inputData.monthlyIncome * 0.35, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.Home,
            isChecked = scenarios.mortgage20Years35Income,
            isPositive = false
        ),
        ScenarioItemData(
            id = "car_loan",
            number = 4,
            title = strings.scenarioCarLoanTitle,
            parametersText = strings.scenarioCarLoanParam,
            description = strings.scenarioCarLoanDesc(
                Formatters.formatMoney(inputData.monthlyIncome * 0.20, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.DirectionsCar,
            isChecked = scenarios.carLoan5Years,
            isPositive = false
        ),
        ScenarioItemData(
            id = "large_expense",
            number = 5,
            title = strings.scenarioLargeExpenseTitle,
            parametersText = strings.scenarioLargeExpenseParam,
            description = strings.scenarioLargeExpenseDesc(
                Formatters.formatMoney(5.0 * inputData.monthlyIncome, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.Payments,
            isChecked = scenarios.largeOneOffExpense500,
            isPositive = false
        ),
        ScenarioItemData(
            id = "inflation",
            number = 6,
            title = strings.scenarioInflationTitle,
            parametersText = strings.scenarioInflationParam,
            description = strings.scenarioInflationDesc(
                Formatters.formatMoney(inputData.monthlyExpenses * 0.25, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.TrendingUp,
            isChecked = scenarios.inflation5Percent,
            isPositive = false
        ),
        ScenarioItemData(
            id = "child_dependent",
            number = 7,
            title = strings.scenarioChildTitle,
            parametersText = strings.scenarioChildParam,
            description = strings.scenarioChildDesc(
                Formatters.formatMoney(inputData.monthlyExpenses * 0.25, inputData.currencySymbol, lang)
            ),
            icon = Icons.Default.ChildCare,
            isChecked = scenarios.childOrDependent18Years,
            isPositive = false
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Общий заголовок панели сценариев
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.scenarioHeaderTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = strings.scenarioHeaderSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            if (scenarios.activeCount > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = strings.scenarioTotalBadge(scenarios.activeCount),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2.1. Позитивные события
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SuccessGreen.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = strings.scenarioPositiveSectionNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = strings.scenarioPositiveTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (scenarios.positiveActiveCount > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = strings.scenarioPositiveActive(scenarios.positiveActiveCount),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Text(
            text = strings.scenarioPositiveSubtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            positiveItems.forEach { item ->
                ScenarioToggleCard(
                    item = item,
                    paramPrefix = strings.scenarioParamsPrefix,
                    onToggle = { onToggleScenario(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2.2. Негативные события
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AlertRed.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = strings.scenarioNegativeSectionNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AlertRed,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = strings.scenarioNegativeTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (scenarios.negativeActiveCount > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AlertRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = strings.scenarioNegativeActive(scenarios.negativeActiveCount),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AlertRed,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Text(
            text = strings.scenarioNegativeSubtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            negativeItems.forEach { item ->
                ScenarioToggleCard(
                    item = item,
                    paramPrefix = strings.scenarioParamsPrefix,
                    onToggle = { onToggleScenario(item.id) }
                )
            }
        }
    }
}

@Composable
private fun ScenarioToggleCard(
    item: ScenarioItemData,
    paramPrefix: String,
    onToggle: () -> Unit
) {
    val accentColor = if (item.isPositive) SuccessGreen else AlertRed

    val containerColor by animateColorAsState(
        targetValue = if (item.isChecked) {
            if (item.isPositive) {
                SuccessGreen.copy(alpha = 0.12f)
            } else {
                AlertRed.copy(alpha = 0.10f)
            }
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "scenario_card_color"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .testTag("scenario_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (item.isChecked) accentColor.copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (item.isChecked) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.number}. ${item.title}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (item.isChecked) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                val badgeBgColor = if (item.isChecked) {
                    accentColor.copy(alpha = 0.12f)
                } else if (item.isPositive) {
                    SuccessGreen.copy(alpha = 0.10f)
                } else {
                    WarningYellowBg
                }

                val badgeTextColor = if (item.isChecked) {
                    accentColor
                } else if (item.isPositive) {
                    SuccessGreen
                } else {
                    AccentGold
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBgColor,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "$paramPrefix ${item.parametersText}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = badgeTextColor,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = item.isChecked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("switch_${item.id}")
            )
        }
    }
}
