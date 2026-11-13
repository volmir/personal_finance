package com.example

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FinancialInputData
import com.example.data.repository.LocalPreferencesManager
import com.example.ui.AppTab
import com.example.ui.FinanceApp
import com.example.ui.MainViewModel
import com.example.util.Formatters
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class FinanceAppRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createNewInstallViewModel(today: LocalDate = LocalDate.of(2026, 11, 6)): MainViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        return MainViewModel(app) { today }
    }

    private fun createViewModel(today: LocalDate = LocalDate.of(2026, 11, 6)): MainViewModel =
        createNewInstallViewModel(today).also {
            it.dismissSampleDataNotice()
        }

    @Test
    @Config(qualifiers = "ru-rRU")
    fun `new install shows the sample data notice once over input`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = createNewInstallViewModel()
        val initialInput = viewModel.inputData.value
        val rawPrefs = app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        assertEquals(FinancialInputData.usAverageData(), initialInput)
        assertFalse(rawPrefs.getBoolean("sample_data_notice_shown", false))
        composeRule.setContent { FinanceApp(viewModel) }

        composeRule.onNodeWithTag("sample_data_notice_dialog").assertIsDisplayed()
        composeRule.onNodeWithText("При первом запуске указаны примерные значения — проверьте и замените их своими")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Понятно").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_item_input").assertIsSelected()
        composeRule.runOnIdle {
            assertEquals(initialInput, viewModel.inputData.value)
            assertTrue(rawPrefs.getBoolean("sample_data_notice_shown", false))
            assertFalse(LocalPreferencesManager(app).shouldShowSampleDataNotice())
        }

        composeRule.onNodeWithTag("sample_data_notice_confirm").performClick()
        composeRule.onNodeWithTag("sample_data_notice_dialog").assertDoesNotExist()

        composeRule.onNodeWithTag("nav_item_metrics").performClick()
        composeRule.onNodeWithTag("nav_item_input").performClick()
        composeRule.onNodeWithTag("sample_data_notice_dialog").assertDoesNotExist()

        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithTag("sample_data_notice_dialog").assertDoesNotExist()
        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithTag("sample_data_notice_dialog").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(initialInput, viewModel.inputData.value) }
    }

    @Test
    fun `existing install has no sample data notice`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val rawPrefs = app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)
        rawPrefs.edit().clear().commit()
        val input = FinancialInputData.usAverageData().copy(monthlyIncome = 12_345.0)
        LocalPreferencesManager(app).saveData(input)
        // Update from a version that saved data but had no one-time notice marker.
        assertTrue(rawPrefs.getBoolean("initialized", false))
        assertFalse(rawPrefs.getBoolean("sample_data_notice_shown", false))
        val viewModel = MainViewModel(app) { LocalDate.of(2026, 11, 6) }

        composeRule.setContent { FinanceApp(viewModel) }

        composeRule.onNodeWithTag("sample_data_notice_dialog").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(input, viewModel.inputData.value) }

        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithTag("sample_data_notice_dialog").assertDoesNotExist()
        composeRule.runOnIdle {
            assertEquals(input, viewModel.inputData.value)
            assertEquals(input, LocalPreferencesManager(app).loadData())
        }
    }

    @Test
    fun `resuming FinanceApp recalculates age after birthday without editing input`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        var currentDate = LocalDate.of(2026, 10, 31)
        val viewModel = MainViewModel(app) { currentDate }
        viewModel.dismissSampleDataNotice()
        viewModel.updateInputData { it.copy(birthDate = "1988-11-01") }
        viewModel.flushPendingSave()
        val input = viewModel.inputData.value
        composeRule.setContent { FinanceApp(viewModel) }
        composeRule.runOnIdle { assertEquals(37, viewModel.metrics.value.currentAge) }

        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        currentDate = currentDate.plusDays(1)
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)

        composeRule.runOnIdle {
            assertEquals(38, viewModel.metrics.value.currentAge)
            assertEquals(38, viewModel.simulatedMetrics.value.currentAge)
            assertEquals(input, viewModel.inputData.value)
        }
    }

    @Test
    fun `bottom navigation exposes exactly four tabs and opens FIRE on its base subtab`() {
        val viewModel = createViewModel()
        composeRule.setContent { FinanceApp(viewModel) }

        val strings = getAppStrings(viewModel.appLanguage.value)
        assertEquals(4, AppTab.values().size)
        composeRule.onAllNodes(
            hasAnyAncestor(hasTestTag("bottom_nav_bar")) and hasClickAction(),
            useUnmergedTree = true
        ).assertCountEquals(4)
        for (tab in AppTab.values()) {
            composeRule.onNodeWithTag("nav_item_${tab.name.lowercase()}")
                .assertIsDisplayed()
                .assert(hasText(tab.localizedShortTitle(strings)))
        }
        composeRule.runOnIdle { assertEquals(AppTab.INPUT, viewModel.currentTab.value) }
        composeRule.onNodeWithTag("nav_item_input").assertIsSelected()

        composeRule.onNodeWithTag("nav_item_fire").performClick()
        composeRule.runOnIdle { assertEquals(AppTab.FIRE, viewModel.currentTab.value) }
        composeRule.onNodeWithTag("nav_item_fire").assertIsSelected()
        composeRule.onNodeWithTag("nav_item_input").assertIsNotSelected()
        composeRule.onNodeWithTag("fire_subtab_selector").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_base").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_simulation").assertIsNotSelected()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertDoesNotExist()
    }

    @Test
    fun `active scenario keeps base values and chart in FIRE and moves the comparison to Simulation`() {
        val viewModel = createViewModel()
        viewModel.toggleScenario("pos_expense_optimization")
        composeRule.setContent { FinanceApp(viewModel) }

        val base = viewModel.metrics.value
        val simulated = viewModel.simulatedMetrics.value
        val lang = viewModel.appLanguage.value
        val currency = viewModel.inputData.value.currencySymbol
        val baseYears = Formatters.formatFireYearsToRetirement(base.yearsToRetirement, base.fireStatus, lang)
        val simulatedYears = Formatters.formatFireYearsToRetirement(simulated.yearsToRetirement, simulated.fireStatus, lang)
        val baseTarget = Formatters.formatFireTargetCapital(base.retirementTargetCapital, currency, base.fireStatus, lang)
        val simulatedTarget = Formatters.formatFireTargetCapital(simulated.retirementTargetCapital, currency, simulated.fireStatus, lang)
        assertNotEquals("scenario must change the displayed years", baseYears, simulatedYears)
        assertNotEquals("scenario must change the displayed target capital", baseTarget, simulatedTarget)

        composeRule.onNodeWithTag("nav_item_fire").performClick()

        // FIRE: только базовые значения и базовый график, без резюме и управления.
        composeRule.onNodeWithTag("fire_hero_card").assertIsDisplayed()
        composeRule.onAllNodes(
            hasText(baseYears) and hasAnyAncestor(hasTestTag("fire_hero_card")),
            useUnmergedTree = true
        ).assertCountEquals(1)
        composeRule.onAllNodes(
            hasText(simulatedYears) and hasAnyAncestor(hasTestTag("fire_hero_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)
        composeRule.onAllNodes(
            hasText(baseTarget) and hasAnyAncestor(hasTestTag("fire_hero_card")),
            useUnmergedTree = true
        ).assertCountEquals(1)
        composeRule.onAllNodes(
            hasText(simulatedTarget) and hasAnyAncestor(hasTestTag("fire_hero_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)

        val list = composeRule.onNode(hasScrollAction())
        list.performScrollToNode(hasTestTag("fire_trajectory_card"))
        composeRule.onNodeWithTag("fire_trajectory_card").assertIsDisplayed()
        composeRule.onAllNodes(hasTestTag("fire_simulation_summary"), useUnmergedTree = true)
            .assertCountEquals(0)

        // «Симуляция»: графика нет, есть резюме сравнения и управление.
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        list.performScrollToNode(hasTestTag("fire_simulation_summary"))
        composeRule.onNodeWithTag("fire_simulation_summary").assertIsDisplayed()
        // Резюме — сосед графика в списке, поэтому проверка отсутствия графика
        // не зависит от того, какие элементы LazyColumn успела скомпоновать.
        composeRule.onAllNodes(hasTestTag("fire_trajectory_card"), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(
            hasText(Formatters.formatFireSimulationSummary(base, simulated, lang)) and
                hasAnyAncestor(hasTestTag("fire_simulation_summary")),
            useUnmergedTree = true
        ).assertCountEquals(1)
        list.performScrollToNode(hasTestTag("fire_simulator_card"))
        composeRule.onNodeWithTag("fire_simulator_card").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_slider_savings").assertExists()
        composeRule.onNodeWithTag("fire_slider_expenses").assertExists()
        composeRule.onNodeWithTag("fire_slider_return").assertExists()
        composeRule.onNodeWithTag("switch_pos_expense_optimization").assertIsOn()

        // Герой-карточка в «Симуляции» показывает смоделированный результат.
        list.performScrollToNode(hasTestTag("fire_hero_card"))
        composeRule.onAllNodes(
            hasText(simulatedYears) and hasAnyAncestor(hasTestTag("fire_hero_card")),
            useUnmergedTree = true
        ).assertCountEquals(1)
        composeRule.onAllNodes(
            hasText(baseYears) and hasAnyAncestor(hasTestTag("fire_hero_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)
    }

    @Test
    fun `subtab switching and re-entering FIRE keep scenario state and base input`() {
        val viewModel = createViewModel()
        viewModel.toggleScenario("pos_salary_increase")
        viewModel.toggleScenario("job_loss")
        composeRule.setContent { FinanceApp(viewModel) }
        composeRule.onNodeWithTag("nav_item_fire").performClick()

        val inputBefore = viewModel.inputData.value
        val baseMetricsBefore = viewModel.metrics.value
        val list = composeRule.onNode(hasScrollAction())

        // «Симуляция» рисует сохраненное состояние сценариев.
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        list.performScrollToNode(hasTestTag("switch_pos_salary_increase"))
        composeRule.onNodeWithTag("switch_pos_salary_increase").assertIsOn()
        list.performScrollToNode(hasTestTag("switch_job_loss"))
        composeRule.onNodeWithTag("switch_job_loss").assertIsOn()

        // Включаем еще один сценарий через реальный UI и запоминаем состояние.
        list.performScrollToNode(hasTestTag("switch_pos_expense_optimization"))
        composeRule.onNodeWithTag("switch_pos_expense_optimization").performScrollTo()
        composeRule.onNodeWithTag("switch_pos_expense_optimization").assertIsDisplayed()
        composeRule.onNodeWithTag("switch_pos_expense_optimization").performClick()
        composeRule.onNodeWithTag("switch_pos_expense_optimization").assertIsOn()
        composeRule.runOnIdle {
            assertTrue(viewModel.fireSimulation.value.scenarios.expenseOptimization15)
        }
        val simulationBefore = viewModel.fireSimulation.value
        val simulatedBefore = viewModel.simulatedMetrics.value

        // Переключение туда-обратно не сбрасывает сценарии.
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_base").performClick()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()

        composeRule.runOnIdle {
            assertEquals(simulationBefore, viewModel.fireSimulation.value)
            assertEquals(simulatedBefore, viewModel.simulatedMetrics.value)
            assertEquals(inputBefore, viewModel.inputData.value)
            assertEquals(baseMetricsBefore, viewModel.metrics.value)
        }
        list.performScrollToNode(hasTestTag("switch_pos_expense_optimization"))
        composeRule.onNodeWithTag("switch_pos_expense_optimization").assertIsOn()

        // Повторный вход в №4 после другой вкладки снова открывает FIRE.
        composeRule.onNodeWithTag("nav_item_metrics").performClick()
        composeRule.onNodeWithTag("metrics_header_card").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_item_fire").performClick()
        composeRule.onNodeWithTag("fire_subtab_base").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertDoesNotExist()
        composeRule.onNodeWithTag("fire_hero_card").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(simulationBefore, viewModel.fireSimulation.value)
            assertEquals(inputBefore, viewModel.inputData.value)
        }
    }
}
