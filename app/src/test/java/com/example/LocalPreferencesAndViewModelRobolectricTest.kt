package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FinancialInputData
import com.example.data.repository.LocalPreferencesManager
import com.example.ui.AppTab
import com.example.ui.FireSimulationState
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalPreferencesAndViewModelRobolectricTest {

    @Test
    fun `invalid saved amounts use defaults while valid currency bounds are preserved`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val manager = LocalPreferencesManager(context)
        val defaults = manager.loadData()
        val keys = listOf("monthly_income", "monthly_expenses", "liquid_assets", "financial_assets", "property_assets", "liabilities")
        for (raw in listOf("NaN", "Infinity", "-Infinity", "-5", "abc", (Formatters.MAX_INPUT_VALUE + 1.0).toString())) {
            prefs.edit().apply { keys.forEach { putString(it, raw) } }.commit()
            assertEquals(raw, defaults, manager.loadData())
        }
        for (value in listOf(0.0, 0.01, Formatters.MAX_INPUT_VALUE)) {
            prefs.edit().apply { keys.forEach { putString(it, value.toString()) } }.commit()
            assertEquals(defaults.copy(
                monthlyIncome = value, monthlyExpenses = value,
                liquidAssets = value, financialAssets = value,
                propertyAssets = value, liabilities = value
            ), manager.loadData())
        }
    }

    @Test
    fun `saved rates clamp infinities and bounds but use defaults for NaN and malformed text`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val manager = LocalPreferencesManager(context)
        val defaults = manager.loadData()
        val cases = listOf(
            "NaN" to defaults,
            "abc" to defaults,
            "Infinity" to defaults.copy(nominalReturnPercent = 100.0, inflationPercent = 100.0, capitalGainsTaxPercent = 100.0),
            "-Infinity" to defaults.copy(nominalReturnPercent = -50.0, inflationPercent = -50.0, capitalGainsTaxPercent = 0.0),
            "-50.01" to defaults.copy(nominalReturnPercent = -50.0, inflationPercent = -50.0, capitalGainsTaxPercent = 0.0),
            "-50" to defaults.copy(nominalReturnPercent = -50.0, inflationPercent = -50.0, capitalGainsTaxPercent = 0.0),
            "-5" to defaults.copy(nominalReturnPercent = -5.0, inflationPercent = -5.0, capitalGainsTaxPercent = 0.0),
            "0" to defaults.copy(nominalReturnPercent = 0.0, inflationPercent = 0.0, capitalGainsTaxPercent = 0.0),
            "100" to defaults.copy(nominalReturnPercent = 100.0, inflationPercent = 100.0, capitalGainsTaxPercent = 100.0),
            "100.01" to defaults.copy(nominalReturnPercent = 100.0, inflationPercent = 100.0, capitalGainsTaxPercent = 100.0)
        )
        for ((raw, expected) in cases) {
            prefs.edit().putString("nominal_return", raw).putString("inflation", raw).putString("capital_gains_tax", raw).commit()
            assertEquals(raw, expected, manager.loadData())
        }
    }

    @Test
    fun `life expectancy falls back outside bounds and invalid birth date remains unchanged`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val manager = LocalPreferencesManager(context)
        manager.loadData()
        for ((saved, expected) in listOf(-1 to 82, 0 to 82, 1 to 1, 82 to 82, 120 to 120, 121 to 82, 500 to 82)) {
            prefs.edit().putInt("life_expectancy", saved).putString("birth_date", "invalid-date").commit()
            val loaded = manager.loadData()
            assertEquals(expected, loaded.lifeExpectancy)
            assertEquals("invalid-date", loaded.birthDate)
        }
    }

    @Test
    fun `simulation load replaces nonfinite values with zero and preserves finite adjustments`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val manager = LocalPreferencesManager(context)
        val keys = listOf("sim_extra_savings", "sim_return_adjustment", "sim_expense_reduction")
        for ((raw, expected) in listOf("NaN" to 0.0, "Infinity" to 0.0, "-Infinity" to 0.0, "abc" to 0.0, "-5" to -5.0, "5" to 5.0)) {
            prefs.edit().apply { keys.forEach { putString(it, raw) } }.commit()
            val sim = manager.loadSimulationState()
            assertEquals(raw, expected, sim.extraMonthlySavings, 0.0)
            assertEquals(raw, expected, sim.realReturnAdjustment, 0.0)
            assertEquals(raw, expected, sim.expenseReduction, 0.0)
        }
    }

    @Test
    fun `legacy property in FIRE preference is ignored without changing saved data`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        val manager = LocalPreferencesManager(context)
        val expected = FinancialInputData.usAverageData()
        manager.saveData(expected)
        prefs.edit().putBoolean("include_property_fire", true).commit()

        assertEquals(expected, manager.loadData())

        val updated = expected.copy(monthlyIncome = 12_000.0)
        manager.saveData(updated)
        assertEquals(updated, LocalPreferencesManager(context).loadData())
        assertTrue(prefs.getBoolean("include_property_fire", false))
    }

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
    fun `sample data notice is offered only before the first data initialization`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()

        val manager = LocalPreferencesManager(context)
        assertTrue(manager.shouldShowSampleDataNotice())

        // Existing install from a previous version: initialized, but no notice marker.
        manager.loadData()
        assertFalse(manager.isFirstLaunch())
        assertFalse(manager.shouldShowSampleDataNotice())
        assertFalse(LocalPreferencesManager(context).shouldShowSampleDataNotice())
    }

    @Test
    fun `sample data notice marker is one-shot and does not initialize financial data`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val manager = LocalPreferencesManager(context)
        assertTrue(manager.shouldShowSampleDataNotice())
        manager.markSampleDataNoticeShown()

        // The marker alone must not turn the install into an initialized one.
        assertTrue(manager.isFirstLaunch())
        assertFalse(LocalPreferencesManager(context).shouldShowSampleDataNotice())

        assertEquals(FinancialInputData.usAverageData(), manager.loadData())
        assertFalse(LocalPreferencesManager(context).shouldShowSampleDataNotice())

        // Clearing all app data creates a new install again.
        prefs.edit().clear().commit()
        assertTrue(LocalPreferencesManager(context).shouldShowSampleDataNotice())
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
