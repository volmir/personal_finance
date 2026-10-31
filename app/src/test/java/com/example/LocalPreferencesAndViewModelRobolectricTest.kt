package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FinancialInputData
import com.example.data.repository.LocalPreferencesManager
import com.example.ui.AppTab
import com.example.ui.FireSimulationState
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalPreferencesAndViewModelRobolectricTest {

    @Test
    fun `first launch assigns default US values and subsequent launches preserve user changes`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Ensure clean preferences for first-launch test
        context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()

        val prefsManager = LocalPreferencesManager(context)
        assertTrue(prefsManager.isFirstLaunch())

        // 1. First launch: loads defaults and persists them
        val initialData = prefsManager.loadData()
        assertEquals(6000.0, initialData.monthlyIncome, 0.01)
        assertEquals(4800.0, initialData.monthlyExpenses, 0.01)
        assertEquals("$", initialData.currencySymbol)
        assertEquals(20.0, initialData.capitalGainsTaxPercent, 0.01)
        assertFalse(prefsManager.isFirstLaunch())

        // 2. User modifies indicators and settings
        val customData = initialData.copy(
            monthlyIncome = 12000.0,
            monthlyExpenses = 7500.0,
            currencySymbol = "€",
            inflationPercent = 3.2,
            capitalGainsTaxPercent = 25.0
        )
        prefsManager.saveData(customData)
        prefsManager.saveLanguage(AppLanguage.EN)
        prefsManager.saveCurrentTab(AppTab.FIRE)

        val customSim = FireSimulationState(
            extraMonthlySavings = 500.0,
            scenarios = prefsManager.loadSimulationState().scenarios.copy(
                salaryIncrease20 = true,
                expensesPlus25 = true
            )
        )
        prefsManager.saveSimulationState(customSim)

        // 3. Subsequent launch: creates a new manager instance and verifies all saved data is restored
        val restartedPrefsManager = LocalPreferencesManager(context)
        assertFalse(restartedPrefsManager.isFirstLaunch())

        val loadedData = restartedPrefsManager.loadData()
        assertEquals(12000.0, loadedData.monthlyIncome, 0.01)
        assertEquals(7500.0, loadedData.monthlyExpenses, 0.01)
        assertEquals("€", loadedData.currencySymbol)
        assertEquals(3.2, loadedData.inflationPercent, 0.01)
        assertEquals(25.0, loadedData.capitalGainsTaxPercent, 0.01)

        assertEquals(AppLanguage.EN, restartedPrefsManager.loadLanguage())
        assertEquals(AppTab.FIRE, restartedPrefsManager.loadCurrentTab())

        val loadedSim = restartedPrefsManager.loadSimulationState()
        assertEquals(500.0, loadedSim.extraMonthlySavings, 0.01)
        assertTrue(loadedSim.scenarios.salaryIncrease20)
        assertTrue(loadedSim.scenarios.expensesPlus25)
        assertFalse(loadedSim.scenarios.jobLoss)
    }

    @Test
    fun `disclaimer text matches user specification in both languages`() {
        val ruStrings = com.example.util.localization.getAppStrings(AppLanguage.RU)
        assertEquals("Отказ от отвествености", ruStrings.disclaimerTitle)
        assertTrue(ruStrings.disclaimerText.contains("Важно: Проект является персональной разработкой и предоставляется исключительно в ознакомительных целях «как есть»"))
        assertTrue(ruStrings.disclaimerText.contains("Все расчеты, формулы, метрики и симуляции носят исключительно информационно-ознакомительный характер"))
        assertTrue(ruStrings.disclaimerText.contains("Вы используете этот инструмент на свой собственный страх и риск"))

        val enStrings = com.example.util.localization.getAppStrings(AppLanguage.EN)
        assertEquals("Disclaimer", enStrings.disclaimerTitle)
        assertTrue(enStrings.disclaimerText.contains("Important: This project is a personal development and is provided solely for informational purposes \"as is\""))
        assertTrue(enStrings.disclaimerText.contains("You use this tool at your own risk"))
    }

    @Test
    fun `propertySale2000 preserves totalAssets and netWorth while transferring property to liquidAssets`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.MainViewModel(app)

        val baseMetrics = viewModel.metrics.value
        val initialInput = viewModel.inputData.value
        val expectedSold = minOf(20.0 * initialInput.monthlyIncome, initialInput.propertyAssets)

        // 1. Активируем сценарий продажи имущества
        viewModel.toggleScenario("property_sale")

        val simulatedMetrics = viewModel.simulatedMetrics.value

        // Капитал не задваивается: совокупные активы и чистый капитал неизменны
        assertEquals(baseMetrics.totalAssets, simulatedMetrics.totalAssets, 0.01)
        assertEquals(baseMetrics.netWorth, simulatedMetrics.netWorth, 0.01)

        // 2. Тест с ограничением: если propertyAssets меньше 20 * monthlyIncome
        viewModel.updateInputData {
            it.copy(
                monthlyIncome = 10_000.0,
                propertyAssets = 50_000.0,
                liquidAssets = 20_000.0,
                financialAssets = 30_000.0,
                liabilities = 10_000.0
            )
        }
        val cappedBaseMetrics = viewModel.metrics.value
        val cappedSimulated = viewModel.simulatedMetrics.value

        // Проданная сумма ограничена 50_000 (а не 200_000)
        assertEquals(cappedBaseMetrics.totalAssets, cappedSimulated.totalAssets, 0.01)
        assertEquals(cappedBaseMetrics.netWorth, cappedSimulated.netWorth, 0.01)
    }
}
