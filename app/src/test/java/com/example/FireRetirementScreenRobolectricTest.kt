package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.ui.screens.FireRetirementScreen
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import kotlin.math.abs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FireRetirementScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `base parameters live only in the FIRE subtab and show the base real return in every language`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0)
        val simulation = mutableStateOf(FireSimulationState())
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(input, metrics, simulated, simulation.value, emptyList(),
                        onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {}, onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }
        // Карточка базовых параметров принадлежит только внутреннему FIRE и всегда
        // показывает базовую доходность, даже при активной симуляции.
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            for (delta in listOf(1.0, 0.0)) {
                composeRule.runOnIdle { simulation.value = FireSimulationState(realReturnAdjustment = delta) }
                composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_parameters_card"))
                val baseRate = Formatters.formatPercent(metrics.realReturnPercent, lang)
                composeRule.onAllNodes(hasText(baseRate) and hasAnyAncestor(hasTestTag("fire_parameters_card")), useUnmergedTree = true)
                    .assertCountEquals(1)
            }
        }

        // В «Симуляции» карточки базовых параметров нет ни при активной, ни при нулевой настройке.
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onAllNodes(hasTestTag("fire_parameters_card"), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.runOnIdle { simulation.value = FireSimulationState(realReturnAdjustment = 1.0) }
        composeRule.onAllNodes(hasTestTag("fire_parameters_card"), useUnmergedTree = true)
            .assertCountEquals(0)
    }

    @Test
    fun `subtab selector switches selection and keeps simulation state intact`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0)
        val simulation = mutableStateOf(
            FireSimulationState(
                extraMonthlySavings = 100.0,
                realReturnAdjustment = 1.0,
                scenarios = FireScenarioToggles(salaryIncrease20 = true)
            )
        )
        val language = mutableStateOf(AppLanguage.RU)
        val screenVisible = mutableStateOf(true)
        var updateCalls = 0
        var toggleCalls = 0
        var resetCalls = 0
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    if (screenVisible.value) {
                        FireRetirementScreen(
                            input, metrics, simulated, simulation.value, emptyList(),
                            onUpdateSimulation = { _, _, _ -> updateCalls++ },
                            onToggleScenario = { toggleCalls++ },
                            onResetSimulation = { resetCalls++ },
                            modifier = Modifier.testTag("fire_test_list"))
                    }
                }
            }
        }

        // По умолчанию выбран внутренний таб FIRE
        composeRule.onNodeWithTag("fire_subtab_base").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_simulation").assertIsNotSelected()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertDoesNotExist()

        // Подписи двух пунктов корректны на всех шести языках
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            composeRule.onNodeWithTag("fire_subtab_base")
                .assert(hasText(getAppStrings(lang).fireSubtabBase))
            composeRule.onNodeWithTag("fire_subtab_simulation")
                .assert(hasText(getAppStrings(lang).fireSubtabSimulation))
        }

        // Переключение выделения туда-обратно
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_subtab_simulation").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_base").assertIsNotSelected()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertDoesNotExist()
        composeRule.onNodeWithTag("fire_subtab_base").performClick()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()

        // Переключение не трогает сценарии и ползунки: колбэки состояния не вызывались
        composeRule.runOnIdle {
            check(updateCalls == 0) { "onUpdateSimulation must not be called by subtab switching" }
            check(toggleCalls == 0) { "onToggleScenario must not be called by subtab switching" }
            check(resetCalls == 0) { "onResetSimulation must not be called by subtab switching" }
        }

        // Повторный вход на вкладку №4 (экран уходит из композиции и создаётся заново) открывает FIRE
        composeRule.runOnIdle { screenVisible.value = false }
        composeRule.runOnIdle { screenVisible.value = true }
        composeRule.onNodeWithTag("fire_subtab_base").assertIsSelected()
        composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_subtab_content_simulation").assertDoesNotExist()

        // FIRE после повторного входа показывает базовую доходность, а не смоделированную
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_parameters_card"))
        val baseRate = Formatters.formatPercent(metrics.realReturnPercent, AppLanguage.RU)
        composeRule.onAllNodes(hasText(baseRate) and hasAnyAncestor(hasTestTag("fire_parameters_card")), useUnmergedTree = true)
            .assertCountEquals(1)

        // Активная симуляция при этом сохранена: в «Симуляции» есть резюме сравнения,
        // но карточка базовых параметров туда не дублируется (UX-F06).
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_simulation_summary"))
        composeRule.onNodeWithTag("fire_simulation_summary").assertIsDisplayed()
        composeRule.onAllNodes(hasTestTag("fire_parameters_card"), useUnmergedTree = true)
            .assertCountEquals(0)

        // Значения сценариев пережили переключение подвкладок: переключатель показывает
        // сохраненное включенное состояние.
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("switch_pos_salary_increase"))
        composeRule.onNodeWithTag("switch_pos_salary_increase").assertIsOn()
    }

    @Test
    fun `simulation summary exists once in the simulation subtab only while simulating`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0)
        val simulation = mutableStateOf(FireSimulationState())
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(input, metrics, simulated, simulation.value, emptyList(),
                        onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {}, onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }

        // Без активной симуляции резюме отсутствует
        composeRule.onNodeWithTag("fire_simulation_summary").assertDoesNotExist()

        // При активной симуляции базовый FIRE по-прежнему показывает график, но не резюме
        composeRule.runOnIdle { simulation.value = FireSimulationState(extraMonthlySavings = 100.0) }
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_trajectory_card"))
        composeRule.onNodeWithTag("fire_trajectory_card").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_simulation_summary").assertDoesNotExist()

        // Резюме ровно в одном экземпляре и вне карточки графика — только в «Симуляции» (UX-F03/F04)
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_simulation_summary"))
        composeRule.onAllNodes(hasTestTag("fire_simulation_summary"), useUnmergedTree = true)
            .assertCountEquals(1)
        composeRule.onAllNodes(
            hasTestTag("fire_simulation_summary") and hasAnyAncestor(hasTestTag("fire_trajectory_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)
        composeRule.onNodeWithTag("fire_trajectory_card").assertDoesNotExist()
        composeRule.onNodeWithTag("fire_simulation_summary").assertIsDisplayed()

        // Возврат в FIRE: резюме скрыто, базовый график снова на месте
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_base").performClick()
        composeRule.onNodeWithTag("fire_simulation_summary").assertDoesNotExist()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_trajectory_card"))
        composeRule.onNodeWithTag("fire_trajectory_card").assertIsDisplayed()
    }

    @Test
    fun `simulator controls live only in the simulation subtab and keep their values`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0)
        val simulation = mutableStateOf(FireSimulationState(extraMonthlySavings = 200.0))
        var toggleCalls = 0
        var updateCalls = 0
        val strings = getAppStrings(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(input, metrics, simulated, simulation.value, emptyList(),
                        onUpdateSimulation = { _, _, _ -> updateCalls++ }, onToggleScenario = { toggleCalls++ },
                        onResetSimulation = {}, modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }
        val list = composeRule.onNodeWithTag("fire_test_list")

        fun assertNoSimulatorUi() {
            composeRule.onAllNodes(hasTestTag("fire_simulator_card"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasTestTag("fire_slider_savings"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasTestTag("fire_slider_expenses"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasTestTag("fire_slider_return"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasTestTag("scenario_card_pos_salary_increase"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(strings.fireSimReset), useUnmergedTree = true)
                .assertCountEquals(0)
        }

        // FIRE: ползунков, сценариев и сброса нет ни на одном участке списка
        repeat(8) {
            assertNoSimulatorUi()
            list.performTouchInput { swipeUp() }
        }
        assertNoSimulatorUi()
        // Дошли до конца списка — проверка отсутствия выше покрывает весь контент FIRE
        list.performScrollToNode(hasText(strings.disclaimerTitle))
        composeRule.onAllNodes(hasText(strings.disclaimerTitle), useUnmergedTree = true)
            .assertCountEquals(1)

        // «Симуляция»: три ползунка, сценарии и сброс на месте, значения из состояния сохранены
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        list.performScrollToNode(hasTestTag("fire_simulator_card"))
        composeRule.onNodeWithTag("fire_simulator_card").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_slider_savings").assertExists()
        composeRule.onNodeWithTag("fire_slider_expenses").assertExists()
        composeRule.onNodeWithTag("fire_slider_return").assertExists()
        composeRule.onAllNodes(hasText(strings.fireSimReset), useUnmergedTree = true)
            .assertCountEquals(1)
        val savingsRange = composeRule.onNodeWithTag("fire_slider_savings")
            .fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
        check(abs(savingsRange.current - 200f) < 1f) {
            "savings slider must show the stored 200, was ${savingsRange.current}"
        }
        composeRule.onNodeWithTag("switch_pos_salary_increase").performScrollTo()
        composeRule.onNodeWithTag("switch_pos_salary_increase").assertIsDisplayed()
        composeRule.onNodeWithTag("switch_pos_salary_increase").performClick()
        composeRule.runOnIdle {
            check(toggleCalls == 1) { "scenario switch must call onToggleScenario" }
            check(updateCalls == 0) { "showing or switching the subtabs must not call onUpdateSimulation" }
        }

        // Возврат в FIRE и снова в «Симуляцию»: состояние симуляции не сброшено
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_base").performClick()
        assertNoSimulatorUi()
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        list.performScrollToNode(hasTestTag("fire_simulator_card"))
        val savingsRangeAfterSwitch = composeRule.onNodeWithTag("fire_slider_savings")
            .fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
        check(abs(savingsRangeAfterSwitch.current - 200f) < 1f) {
            "subtab switching must not reset sliders, was ${savingsRangeAfterSwitch.current}"
        }
    }

    @Test
    fun `each internal subtab keeps only its own cards and FIRE explanations`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0)
        val strings = getAppStrings(AppLanguage.RU)
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(input, metrics, simulated, FireSimulationState(), emptyList(),
                        onUpdateSimulation = { _, _, _ -> }, onToggleScenario = {}, onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list"))
                }
            }
        }
        val list = composeRule.onNodeWithTag("fire_test_list")

        // FIRE: базовый результат, параметры, базовый график, предпосылки и дисклеймер.
        list.performScrollToNode(hasTestTag("fire_hero_card"))
        composeRule.onNodeWithTag("fire_hero_card").assertIsDisplayed()
        list.performScrollToNode(hasTestTag("fire_parameters_card"))
        composeRule.onNodeWithTag("fire_parameters_card").assertIsDisplayed()
        list.performScrollToNode(hasTestTag("fire_trajectory_card"))
        composeRule.onNodeWithTag("fire_trajectory_card").assertIsDisplayed()
        list.performScrollToNode(hasText(strings.fireAssumptionsTitle))
        composeRule.onAllNodes(hasText(strings.fireAssumptionsTitle), useUnmergedTree = true)
            .assertCountEquals(1)
        list.performScrollToNode(hasText(strings.disclaimerTitle))
        composeRule.onAllNodes(hasText(strings.disclaimerTitle), useUnmergedTree = true)
            .assertCountEquals(1)

        // В FIRE нет резюме и управляющих элементов «Симуляции».
        composeRule.onAllNodes(hasTestTag("fire_simulation_summary"), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(hasTestTag("fire_simulator_card"), useUnmergedTree = true)
            .assertCountEquals(0)

        // «Симуляция» без активных изменений: базовый результат и настройка сценариев,
        // без карточки параметров, графика и пояснений FIRE (UX-F06).
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        list.performScrollToNode(hasTestTag("fire_hero_card"))
        composeRule.onNodeWithTag("fire_hero_card").assertIsDisplayed()
        list.performScrollToNode(hasTestTag("fire_simulator_card"))
        composeRule.onNodeWithTag("fire_simulator_card").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_slider_savings").assertExists()
        composeRule.onAllNodes(hasTestTag("fire_parameters_card"), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(hasTestTag("fire_trajectory_card"), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(hasText(strings.fireAssumptionsTitle), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(hasText(strings.disclaimerTitle), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(hasTestTag("fire_simulation_summary"), useUnmergedTree = true)
            .assertCountEquals(0)
    }
}
