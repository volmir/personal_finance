package com.example

import android.app.Application
import android.content.Context
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MainViewModel
import com.example.ui.components.ScenarioTogglesPanel
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScenarioTogglesPanelRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `scenario descriptions match raised income payments and capped property sale in every language`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app) { LocalDate.of(2026, 11, 6) }
        viewModel.updateInputData { it.copy(monthlyIncome = 6_000.0, monthlyExpenses = 4_800.0, propertyAssets = 1_000.0, liabilities = 0.0) }
        viewModel.toggleScenario("pos_salary_increase")
        val baseExpenses = viewModel.simulatedMetrics.value.annualExpenses / 12.0
        viewModel.toggleScenario("mortgage")
        val mortgage = viewModel.simulatedMetrics.value.annualExpenses / 12.0 - baseExpenses
        viewModel.toggleScenario("car_loan")
        val carLoan = viewModel.simulatedMetrics.value.annualExpenses / 12.0 - baseExpenses - mortgage
        val investable = viewModel.simulatedMetrics.value.fidelityCapital
        viewModel.toggleScenario("pos_property_sale")
        val propertySale = viewModel.simulatedMetrics.value.fidelityCapital - investable
        assertEquals(2_520.0, mortgage, 0.0)
        assertEquals(1_440.0, carLoan, 0.0)
        assertEquals(1_000.0, propertySale, 0.0)
        viewModel.flushPendingSave()
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            val scenarios by viewModel.fireSimulation.collectAsState()
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    ScenarioTogglesPanel(scenarios.scenarios, viewModel.inputData.value, viewModel::toggleScenario,
                        modifier = Modifier.verticalScroll(rememberScrollState()))
                }
            }
        }
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            fun money(value: Double) = Formatters.formatMoney(value, "$", lang)
            composeRule.onNodeWithText(strings.scenarioMortgageDesc(money(mortgage), "20")).assertExists()
            composeRule.onNodeWithText(strings.scenarioCarLoanDesc(money(carLoan), "5")).assertExists()
            composeRule.onNodeWithText(strings.scenarioPropertySaleDesc(money(propertySale))).assertExists()
            composeRule.runOnIdle { viewModel.toggleScenario("pos_salary_increase") }
            composeRule.onNodeWithText(strings.scenarioMortgageDesc(money(2_100.0), "20")).assertExists()
            composeRule.onNodeWithText(strings.scenarioCarLoanDesc(money(1_200.0), "5")).assertExists()
            composeRule.onNodeWithText(strings.scenarioPropertySaleDesc(money(propertySale))).assertExists()
            composeRule.runOnIdle { viewModel.toggleScenario("pos_salary_increase") }
        }
    }
}
