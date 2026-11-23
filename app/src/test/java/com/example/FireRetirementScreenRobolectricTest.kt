package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsAtLeast
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.data.model.FireStatus
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.ui.screens.FireRetirementScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusColors
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

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
    fun `expense increase scenario is not shown in real return slider subtitle`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulation = FireSimulationState(
            realReturnAdjustment = 1.0,
            scenarios = FireScenarioToggles(expensesPlus25 = true)
        )
        val language = AppLanguage.RU
        val strings = getAppStrings(language)

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = metrics,
                        simulatedMetrics = metrics.copy(realReturnPercent = metrics.realReturnPercent + 1.0),
                        simulationState = simulation,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_simulator_card"))

        val expectedSubtitle = "+${Formatters.formatRatio(1.0, language)}% ${strings.fireSliderBaseReturnSuffix}"
        composeRule.onAllNodes(hasText(expectedSubtitle), useUnmergedTree = true).assertCountEquals(1)
        composeRule.onAllNodes(
            hasText("${expectedSubtitle} • ${strings.scenarioInflationTitle.lowercase()}"),
            useUnmergedTree = true
        ).assertCountEquals(0)
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
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_sticky_summary"))
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()
        composeRule.onAllNodes(hasTestTag("fire_parameters_card"), useUnmergedTree = true)
            .assertCountEquals(0)

        // Значения сценариев пережили переключение подвкладок: переключатель показывает
        // сохраненное включенное состояние.
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("switch_pos_salary_increase"))
        composeRule.onNodeWithTag("switch_pos_salary_increase").assertIsOn()
    }

    @Test
    fun `simulation summary exists once in Simulation including zero changes`() {
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

        // В базовом FIRE резюме симуляции отсутствует
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertDoesNotExist()

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_sim_delta_value", useUnmergedTree = true).assertTextEquals("Без изменений")
        composeRule.onNodeWithTag("fire_sim_reset_button").assertIsNotEnabled()
        composeRule.onNodeWithTag("fire_subtab_base").performClick()

        // При активной симуляции базовый FIRE по-прежнему показывает график, но не резюме
        composeRule.runOnIdle { simulation.value = FireSimulationState(extraMonthlySavings = 100.0) }
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_trajectory_card"))
        composeRule.onNodeWithTag("fire_trajectory_card").assertIsDisplayed()
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertDoesNotExist()

        // Резюме ровно в одном экземпляре и вне карточки графика — только в «Симуляции» (UX-F03/F04)
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_sticky_summary"))
        composeRule.onAllNodes(hasTestTag("fire_sim_sticky_summary"), useUnmergedTree = true)
            .assertCountEquals(1)
        composeRule.onAllNodes(
            hasTestTag("fire_sim_sticky_summary") and hasAnyAncestor(hasTestTag("fire_trajectory_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)
        composeRule.onNodeWithTag("fire_trajectory_card").assertDoesNotExist()
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()

        // Возврат в FIRE: резюме скрыто, базовый график снова на месте
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_base").performClick()
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertDoesNotExist()
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
            composeRule.onAllNodes(hasTestTag("fire_hero_simulation_badge"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(strings.simulationBadgeOnlySliders), useUnmergedTree = true)
                .assertCountEquals(0)
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
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()
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
        composeRule.onAllNodes(hasTestTag("fire_sim_sticky_summary"), useUnmergedTree = true)
            .assertCountEquals(0)
        composeRule.onAllNodes(hasTestTag("fire_simulator_card"), useUnmergedTree = true)
            .assertCountEquals(0)

        // «Симуляция» без активных изменений: базовый результат и настройка сценариев,
        // без карточки параметров, графика и пояснений FIRE (UX-F06).
        list.performScrollToNode(hasTestTag("fire_subtab_selector"))
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_hero_card").assertDoesNotExist()
        composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()
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
        composeRule.onAllNodes(hasTestTag("fire_sim_sticky_summary"), useUnmergedTree = true)
            .assertCountEquals(1)
    }

    @Test
    fun `active change count and scenario info appear only in Simulation subtab across all languages`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(yearsToRetirement = (metrics.yearsToRetirement - 2.0).coerceAtLeast(0.0))
        val activeSim = FireSimulationState(
            extraMonthlySavings = 200.0,
            scenarios = FireScenarioToggles(salaryIncrease20 = true, expenseOptimization15 = true)
        )
        val language = mutableStateOf(AppLanguage.RU)

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = metrics,
                        simulatedMetrics = simulated,
                        simulationState = activeSim,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            val heroBadgeText = Formatters.formatSimulationBadge(
                activeCount = activeSim.scenarios.activeCount,
                hasSliders = true,
                lang = lang
            ).replace('\u00A0', ' ')

            // Подвкладка FIRE: нет бейджа чекбоксов/ползунков и нет текста управления симуляцией
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            composeRule.onNodeWithTag("fire_subtab_content_base").assertIsDisplayed()
            composeRule.onAllNodes(hasTestTag("fire_hero_simulation_badge"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(heroBadgeText), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(strings.fireSimSliderTitle), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(strings.fireSimFineTuning), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(strings.scenarioHeaderTitle), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onAllNodes(hasText(strings.scenarioHeaderSubtitle), useUnmergedTree = true)
                .assertCountEquals(0)

            // Вместо прежнего бейджа закреплённая панель считает сценарии и ползунки.
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()
            composeRule.onNodeWithTag("fire_sim_sticky_summary").assertIsDisplayed()
            composeRule.onAllNodes(
                hasText(strings.fireSimChangesCount(3)) and hasAnyAncestor(hasTestTag("fire_sim_sticky_summary")),
                useUnmergedTree = true
            ).assertCountEquals(1)
            composeRule.onAllNodes(hasTestTag("fire_trajectory_card"), useUnmergedTree = true)
                .assertCountEquals(0)
            composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_simulator_card"))
            composeRule.onNodeWithTag("fire_simulator_card").assertIsDisplayed()
            composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
        }
    }

    @Test
    fun `reset button has accessible touch target, decorative icon, and triggers reset across languages`() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val simulated = metrics.copy(yearsToRetirement = (metrics.yearsToRetirement - 1.0).coerceAtLeast(0.0))
        val simulation = mutableStateOf(FireSimulationState(extraMonthlySavings = 200.0))
        val language = mutableStateOf(AppLanguage.RU)
        var resetCalls = 0

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = metrics,
                        simulatedMetrics = simulated,
                        simulationState = simulation.value,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {
                            resetCalls++
                            simulation.value = FireSimulationState()
                        },
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()

        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle {
                language.value = lang
                simulation.value = FireSimulationState(extraMonthlySavings = 200.0)
            }
            val strings = getAppStrings(lang)
            composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_reset_button"))
            val resetButton = composeRule.onNodeWithTag("fire_sim_reset_button")
            resetButton.assertIsDisplayed()
            resetButton.assert(hasText(strings.fireSimReset))
            resetButton.assertWidthIsAtLeast(48.dp)
            resetButton.assertHeightIsAtLeast(48.dp)
            // Декоративная иконка не добавляет отдельный ContentDescription в TalkBack
            val semanticsNode = resetButton.fetchSemanticsNode()
            val contentDesc = semanticsNode.config.getOrElse(SemanticsProperties.ContentDescription) { emptyList() }
            check(contentDesc.isEmpty()) {
                "Reset button decorative icon must not add ContentDescription, found $contentDesc"
            }
        }

        composeRule.onNodeWithTag("fire_sim_reset_button").performClick()
        composeRule.runOnIdle {
            check(resetCalls == 1) { "Expected 1 reset call, got $resetCalls" }
        }
        composeRule.onNodeWithTag("fire_sim_reset_button").assertIsNotEnabled()
    }

    @Test
    fun `hero status matrix across base and simulation subtabs never renders 0,00 for reached or sub-hundredth`() {
        val input = FinancialInputData.usAverageData()
        val sampleMetrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val baseMetrics = mutableStateOf(sampleMetrics)
        val simMetrics = mutableStateOf(sampleMetrics)
        val simState = mutableStateOf(FireSimulationState())
        val strings = getAppStrings(AppLanguage.RU)

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides strings
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = baseMetrics.value,
                        simulatedMetrics = simMetrics.value,
                        simulationState = simState.value,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {
                            simState.value = FireSimulationState()
                            simMetrics.value = baseMetrics.value
                        },
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        fun metricsFor(status: FireStatus, years: Double = 12.5) = when (status) {
            FireStatus.REACHED_NOW -> sampleMetrics.copy(
                fireStatus = FireStatus.REACHED_NOW,
                yearsToRetirement = 0.0,
                retirementAge = sampleMetrics.currentAge.toDouble()
            )
            FireStatus.UNREACHABLE -> sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
            FireStatus.HORIZON_EXHAUSTED -> sampleMetrics.copy(fireStatus = FireStatus.HORIZON_EXHAUSTED)
            FireStatus.INVALID_AGE -> sampleMetrics.copy(fireStatus = FireStatus.INVALID_AGE)
            FireStatus.REACHABLE -> sampleMetrics.copy(
                fireStatus = FireStatus.REACHABLE,
                yearsToRetirement = years,
                retirementAge = sampleMetrics.currentAge + years
            )
        }

        fun assertBaseHeroMatches(status: FireStatus, years: Double = 12.5) {
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            when (status) {
                FireStatus.REACHED_NOW -> {
                    composeRule.onNodeWithTag("fire_reached_title").assertIsDisplayed()
                        .assert(hasText(strings.fireReachedBaseText))
                    composeRule.onAllNodes(hasText("0,00", substring = true) and hasAnyAncestor(hasTestTag("fire_hero_card")), useUnmergedTree = true)
                        .assertCountEquals(0)
                }
                FireStatus.UNREACHABLE -> {
                    composeRule.onNodeWithTag("fire_unreachable_title").assertIsDisplayed()
                        .assert(hasText(strings.fireUnreachableText))
                }
                FireStatus.HORIZON_EXHAUSTED -> {
                    composeRule.onNodeWithTag("fire_unreachable_title").assertIsDisplayed()
                        .assert(hasText(strings.fireHorizonExhaustedText))
                }
                FireStatus.INVALID_AGE -> {
                    composeRule.onNodeWithTag("fire_unreachable_title").assertIsDisplayed()
                        .assert(hasText(strings.invalidBirthDateError))
                }
                FireStatus.REACHABLE -> {
                    if (Formatters.isSubHundredthPositiveYears(years)) {
                        composeRule.onNodeWithTag("fire_sub_hundredth_title").assertIsDisplayed()
                            .assert(hasText(strings.fireLessThanHundredthYearText))
                        composeRule.onAllNodes(hasText("0,00", substring = true) and hasAnyAncestor(hasTestTag("fire_hero_card")), useUnmergedTree = true)
                            .assertCountEquals(0)
                    } else {
                        val expected = Formatters.formatRatio(years, AppLanguage.RU)
                        composeRule.onAllNodes(hasText(expected) and hasAnyAncestor(hasTestTag("fire_hero_card")), useUnmergedTree = true)
                            .assertCountEquals(1)
                    }
                }
            }
        }

        fun assertSimulationHeroMatches(status: FireStatus, years: Double = 9.25) {
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            val expected = when (status) {
                FireStatus.REACHED_NOW -> strings.fireReachedSimulationText
                FireStatus.UNREACHABLE -> strings.fireUnreachableSimulationText
                FireStatus.HORIZON_EXHAUSTED -> strings.fireHorizonExhaustedText
                FireStatus.INVALID_AGE -> strings.invalidBirthDateError
                FireStatus.REACHABLE -> if (Formatters.isSubHundredthPositiveYears(years)) {
                    strings.fireLessThanHundredthYearText
                } else null
            }
            val value = composeRule.onNodeWithTag("fire_sim_years_value", useUnmergedTree = true)
            value.assertIsDisplayed()
            if (expected != null) value.assert(hasText(expected, substring = true))
            else value.assertTextEquals(Formatters.formatFireSimulationComparison(
                baseMetrics.value, simMetrics.value, simState.value.activeChangesCount, AppLanguage.RU
            ).years)
            composeRule.onNodeWithTag("fire_hero_card").assertDoesNotExist()
        }

        val matrix = listOf(
            FireStatus.REACHED_NOW to FireStatus.REACHED_NOW,
            FireStatus.REACHED_NOW to FireStatus.UNREACHABLE,
            FireStatus.UNREACHABLE to FireStatus.REACHED_NOW,
            FireStatus.UNREACHABLE to FireStatus.REACHABLE,
            FireStatus.REACHABLE to FireStatus.UNREACHABLE,
            FireStatus.REACHABLE to FireStatus.REACHED_NOW,
            FireStatus.REACHED_NOW to FireStatus.REACHABLE,
            FireStatus.UNREACHABLE to FireStatus.UNREACHABLE,
            FireStatus.REACHABLE to FireStatus.REACHABLE
        )

        for ((baseStatus, simulatedStatus) in matrix) {
            composeRule.runOnIdle {
                baseMetrics.value = metricsFor(baseStatus, years = 14.25)
                simMetrics.value = metricsFor(simulatedStatus, years = 8.75)
                simState.value = FireSimulationState(extraMonthlySavings = 250.0)
            }
            assertBaseHeroMatches(baseStatus, years = 14.25)
            assertSimulationHeroMatches(simulatedStatus, years = 8.75)
        }

        // Граница < 0.01 года (0.004) и 0.01 года, а также HORIZON_EXHAUSTED и INVALID_AGE
        composeRule.runOnIdle {
            baseMetrics.value = metricsFor(FireStatus.REACHABLE, years = 0.004)
            simMetrics.value = metricsFor(FireStatus.REACHABLE, years = 0.01)
            simState.value = FireSimulationState(extraMonthlySavings = 100.0)
        }
        assertBaseHeroMatches(FireStatus.REACHABLE, years = 0.004)
        assertSimulationHeroMatches(FireStatus.REACHABLE, years = 0.01)

        composeRule.runOnIdle {
            baseMetrics.value = metricsFor(FireStatus.HORIZON_EXHAUSTED)
            simMetrics.value = metricsFor(FireStatus.INVALID_AGE)
        }
        assertBaseHeroMatches(FireStatus.HORIZON_EXHAUSTED)
        assertSimulationHeroMatches(FireStatus.INVALID_AGE)

        // Сброс симуляции возвращает результат к базовому прогнозу (здесь — REACHED_NOW)
        composeRule.runOnIdle {
            baseMetrics.value = metricsFor(FireStatus.REACHED_NOW)
            simMetrics.value = metricsFor(FireStatus.UNREACHABLE)
            simState.value = FireSimulationState(extraMonthlySavings = 250.0)
        }
        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
        composeRule.onNodeWithTag("fire_sim_years_value", useUnmergedTree = true).assertIsDisplayed()
            .assert(hasText(strings.fireUnreachableSimulationText, substring = true))

        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_reset_button"))
        composeRule.onNodeWithTag("fire_sim_reset_button").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_sticky_summary"))
        composeRule.onNodeWithTag("fire_sim_years_value", useUnmergedTree = true).assertIsDisplayed()
            .assert(hasText(strings.fireReachedSimulationText))
    }

    @Test
    @Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `base hero and simulation status texts wrap without overflow across all six languages`() {
        val input = FinancialInputData.usAverageData()
        val sampleMetrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val baseMetrics = mutableStateOf(sampleMetrics)
        val simMetrics = mutableStateOf(sampleMetrics)
        val simState = mutableStateOf(
            FireSimulationState(
                extraMonthlySavings = 200.0,
                scenarios = FireScenarioToggles(salaryIncrease20 = true, expenseOptimization15 = true)
            )
        )
        val language = mutableStateOf(AppLanguage.RU)

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = baseMetrics.value,
                        simulatedMetrics = simMetrics.value,
                        simulationState = simState.value,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        fun assertTextFits(tag: String, label: String, isSimulation: Boolean) {
            val node = composeRule.onNodeWithTag(tag, useUnmergedTree = true)
            node.assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            val semantics = node.fetchSemanticsNode()
            semantics.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
            val layout = layouts.single()
            val maxLineWidth = (0 until layout.lineCount)
                .maxOfOrNull { layout.getLineRight(it) - layout.getLineLeft(it) } ?: 0f
            val anyEllipsized = (0 until layout.lineCount).any { layout.isLineEllipsized(it) }
            check(!anyEllipsized && !layout.didOverflowHeight && maxLineWidth <= layout.size.width + 0.5f) {
                "$label ($tag) overflowed: ellipsized=$anyEllipsized, overflowH=${layout.didOverflowHeight}, " +
                    "maxW=$maxLineWidth, boxW=${layout.size.width}"
            }
            if (isSimulation) composeRule.onNodeWithTag("fire_hero_card").assertDoesNotExist()
        }

        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }

            // 1) REACHED_NOW in both subtabs
            composeRule.runOnIdle {
                baseMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0)
                simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0)
            }
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            assertTextFits("fire_reached_title", "$lang BASE REACHED", isSimulation = false)
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            assertTextFits("fire_sim_years_value", "$lang SIM REACHED", isSimulation = true)

            // 2) UNREACHABLE in both subtabs
            composeRule.runOnIdle {
                baseMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
                simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
            }
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            assertTextFits("fire_unreachable_title", "$lang BASE UNREACHABLE", isSimulation = false)
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            assertTextFits("fire_sim_years_value", "$lang SIM UNREACHABLE", isSimulation = true)

            // 3) REACHABLE < 0.01 in the simulation comparison cell
            composeRule.runOnIdle {
                simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHABLE, yearsToRetirement = 0.004)
            }
            assertTextFits("fire_sim_years_value", "$lang SIM SUB-HUNDREDTH", isSimulation = true)
        }
    }

    @Test
    @Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `retirement age row shows short unreachable label in both subtabs across all six languages and themes`() {
        val input = FinancialInputData.usAverageData()
        val sampleMetrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val baseMetrics = mutableStateOf(sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE))
        val simMetrics = mutableStateOf(sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE))
        val simState = mutableStateOf(FireSimulationState(extraMonthlySavings = 200.0))
        val language = mutableStateOf(AppLanguage.RU)
        val darkTheme = mutableStateOf(false)

        composeRule.setContent {
            MyApplicationTheme(darkTheme = darkTheme.value, dynamicColor = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = baseMetrics.value,
                        simulatedMetrics = simMetrics.value,
                        simulationState = simState.value,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        fun assertTwoColumnRow(lang: AppLanguage, subtabLabel: String, expectedValue: String, expectRed: Boolean, isDark: Boolean) {
            val simulation = subtabLabel.startsWith("SIM")
            val labelNode = composeRule.onNodeWithTag(if (simulation) "fire_sim_age_label" else "fire_retirement_age_label", useUnmergedTree = true)
            val valueNode = composeRule.onNodeWithTag(if (simulation) "fire_sim_age_value" else "fire_retirement_age_value", useUnmergedTree = true)
            labelNode.assertIsDisplayed()
            valueNode.assertIsDisplayed().assertTextEquals(expectedValue)

            val valueLayouts = mutableListOf<TextLayoutResult>()
            valueNode.fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action?.invoke(valueLayouts)
            val valueLayout = valueLayouts.single()
            assertFalse("$lang $subtabLabel value height overflow", valueLayout.didOverflowHeight)
            assertFalse("$lang $subtabLabel value ellipsized", (0 until valueLayout.lineCount).any { valueLayout.isLineEllipsized(it) })
            assertTrue("$lang $subtabLabel value width fits", valueLayout.size.width <= valueLayout.layoutInput.constraints.maxWidth)

            if (expectRed) {
                val expectedColor = if (isDark) com.example.ui.theme.AlertRedTextDark else com.example.ui.theme.AlertRedTextLight
                assertEquals("$lang $subtabLabel red color", expectedColor, valueLayout.layoutInput.style.color)
            }

            val labelBounds = labelNode.fetchSemanticsNode().boundsInWindow
            val valueBounds = valueNode.fetchSemanticsNode().boundsInWindow
            assertTrue(
                "$lang $subtabLabel must remain two columns with gap: $labelBounds vs $valueBounds",
                labelBounds.right < valueBounds.left
            )
        }

        for (isDark in listOf(false, true)) {
            composeRule.runOnIdle { darkTheme.value = isDark }
            for (lang in AppLanguage.values()) {
                val strings = getAppStrings(lang)
                composeRule.runOnIdle {
                    language.value = lang
                    baseMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
                    simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
                }
                composeRule.onNodeWithTag("fire_subtab_base").performClick()
                composeRule.onNodeWithTag("fire_unreachable_title").assertTextEquals(strings.fireUnreachableText)
                assertTwoColumnRow(lang, "BASE UNREACHABLE", strings.fireUnreachableShortText, expectRed = true, isDark = isDark)

                composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
                composeRule.onNodeWithTag("fire_sim_years_value", useUnmergedTree = true).assertTextEquals(strings.fireUnreachableSimulationText)
                assertTwoColumnRow(lang, "SIM UNREACHABLE", strings.fireUnreachableShortText, expectRed = false, isDark = isDark)

                composeRule.runOnIdle {
                    baseMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHABLE, yearsToRetirement = 14.5, retirementAge = 52.5)
                    simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0, retirementAge = 38.0)
                }
                composeRule.onNodeWithTag("fire_subtab_base").performClick()
                assertTwoColumnRow(
                    lang,
                    "BASE REACHABLE",
                    Formatters.formatFireRetirementAge(52.5, FireStatus.REACHABLE, lang, compactUnreachable = true),
                    expectRed = false,
                    isDark = isDark
                )
                composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
                assertTwoColumnRow(
                    lang,
                    "SIM REACHED_NOW",
                    Formatters.formatFireSimulationComparison(baseMetrics.value, simMetrics.value, 1, lang).age,
                    expectRed = false,
                    isDark = isDark
                )
            }
        }
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `retirement age row does not clip or overlap on 320dp screens and large font scale across all six languages`() {
        val input = FinancialInputData.usAverageData()
        val sampleMetrics = FinanceCalculator.calculate(input, today = LocalDate.of(2026, 11, 6))
        val unreachableMetrics = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
        val simState = FireSimulationState(extraMonthlySavings = 200.0)
        val language = mutableStateOf(AppLanguage.RU)
        val fontScaleState = mutableStateOf(1.0f)

        composeRule.setContent {
            val baseDensity = LocalDensity.current
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value),
                    LocalDensity provides Density(baseDensity.density, fontScale = fontScaleState.value)
                ) {
                    FireRetirementScreen(
                        inputData = input,
                        metrics = unreachableMetrics,
                        simulatedMetrics = unreachableMetrics,
                        simulationState = simState,
                        trajectory = emptyList(),
                        onUpdateSimulation = { _, _, _ -> },
                        onToggleScenario = {},
                        onResetSimulation = {},
                        modifier = Modifier.testTag("fire_test_list")
                    )
                }
            }
        }

        for (scale in listOf(1.0f, 1.6f)) {
            composeRule.runOnIdle { fontScaleState.value = scale }
            for (subtabTag in listOf("fire_subtab_base", "fire_subtab_simulation")) {
                composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_subtab_selector"))
                composeRule.onNodeWithTag(subtabTag).performClick()
                for (lang in AppLanguage.values()) {
                    composeRule.runOnIdle { language.value = lang }
                    composeRule.waitForIdle()
                    val simulation = subtabTag == "fire_subtab_simulation"
                    composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag(if (simulation) "fire_sim_sticky_summary" else "fire_hero_card"))
                    val strings = getAppStrings(lang)
                    val labelNode = composeRule.onNodeWithTag(if (simulation) "fire_sim_age_label" else "fire_retirement_age_label", useUnmergedTree = true)
                    val valueNode = composeRule.onNodeWithTag(if (simulation) "fire_sim_age_value" else "fire_retirement_age_value", useUnmergedTree = true)
                    if (!simulation) valueNode.performScrollTo()
                    composeRule.waitForIdle()
                    labelNode.assertIsDisplayed().assertTextEquals(if (simulation) strings.fireSimAgeLabel else strings.fireRetirementAgeLabel)
                    valueNode.assertIsDisplayed().assertTextEquals(strings.fireUnreachableShortText)

                    val labelLayouts = mutableListOf<TextLayoutResult>()
                    val valueLayouts = mutableListOf<TextLayoutResult>()
                    labelNode.fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action?.invoke(labelLayouts)
                    valueNode.fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action?.invoke(valueLayouts)
                    val labelLayout = labelLayouts.single()
                    val valueLayout = valueLayouts.single()
                    assertFalse("$lang scale=$scale $subtabTag label height overflow", labelLayout.didOverflowHeight)
                    assertFalse("$lang scale=$scale $subtabTag value height overflow", valueLayout.didOverflowHeight)
                    assertFalse("$lang scale=$scale $subtabTag value ellipsized", (0 until valueLayout.lineCount).any { valueLayout.isLineEllipsized(it) })
                    assertTrue("$lang scale=$scale $subtabTag label fits", labelLayout.size.width <= labelLayout.layoutInput.constraints.maxWidth)
                    assertTrue("$lang scale=$scale $subtabTag value fits", valueLayout.size.width <= valueLayout.layoutInput.constraints.maxWidth)

                    val labelBounds = labelNode.fetchSemanticsNode().boundsInWindow
                    val valueBounds = valueNode.fetchSemanticsNode().boundsInWindow
                    val sideBySide = labelBounds.right < valueBounds.left
                    val stacked = labelBounds.bottom <= valueBounds.top
                    assertTrue(
                        "$lang scale=$scale $subtabTag overlap: $labelBounds vs $valueBounds",
                        if (scale == 1.0f) sideBySide else (sideBySide || stacked)
                    )
                }
            }
        }
    }
}
