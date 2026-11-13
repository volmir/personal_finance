package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
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
import androidx.compose.ui.unit.dp
import com.example.data.model.FireStatus
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
        composeRule.onNodeWithTag("fire_hero_simulation_badge").assertIsDisplayed()
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

    @Test
    fun `active simulation badge and scenario info appear only in Simulation subtab across all languages`() {
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

            // Подвкладка «Симуляция»: бейдж виден в hero-карточке, графика нет, элементы управления на месте
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            composeRule.onNodeWithTag("fire_subtab_content_simulation").assertIsDisplayed()
            composeRule.onNodeWithTag("fire_hero_simulation_badge").assertIsDisplayed()
            composeRule.onAllNodes(
                hasText(heroBadgeText) and hasAnyAncestor(hasTestTag("fire_hero_simulation_badge")),
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
        composeRule.onNodeWithTag("fire_sim_reset_button").assertDoesNotExist()
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
            when (status) {
                FireStatus.REACHED_NOW -> {
                    composeRule.onNodeWithTag("fire_reached_title").assertIsDisplayed()
                        .assert(hasText(strings.fireReachedSimulationText))
                    composeRule.onAllNodes(hasText("0,00", substring = true) and hasAnyAncestor(hasTestTag("fire_hero_card")), useUnmergedTree = true)
                        .assertCountEquals(0)
                }
                FireStatus.UNREACHABLE -> {
                    composeRule.onNodeWithTag("fire_unreachable_title").assertIsDisplayed()
                        .assert(hasText(strings.fireUnreachableSimulationText))
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

        val matrix = listOf(
            FireStatus.REACHED_NOW to FireStatus.REACHED_NOW,
            FireStatus.REACHED_NOW to FireStatus.UNREACHABLE,
            FireStatus.UNREACHABLE to FireStatus.REACHED_NOW,
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
        composeRule.onNodeWithTag("fire_unreachable_title").assertIsDisplayed()
            .assert(hasText(strings.fireUnreachableSimulationText))

        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_sim_reset_button"))
        composeRule.onNodeWithTag("fire_sim_reset_button").performClick()
        composeRule.onNodeWithTag("fire_test_list").performScrollToNode(hasTestTag("fire_hero_card"))
        composeRule.onNodeWithTag("fire_reached_title").assertIsDisplayed()
            .assert(hasText(strings.fireReachedSimulationText))
    }

    @Test
    @Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `hero status texts wrap without overflow or badge collision across all six languages`() {
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

        fun assertTextFits(tag: String, label: String, hasBadge: Boolean) {
            val node = composeRule.onNodeWithTag(tag)
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
            if (hasBadge) {
                val badgeBounds = composeRule.onNodeWithTag("fire_hero_simulation_badge").fetchSemanticsNode().boundsInRoot
                val textBounds = semantics.boundsInRoot
                check(textBounds.right <= badgeBounds.left + 0.5f) {
                    "$label ($tag) overlaps badge: textRight=${textBounds.right}, badgeLeft=${badgeBounds.left}"
                }
            }
        }

        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }

            // 1) REACHED_NOW in both subtabs
            composeRule.runOnIdle {
                baseMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0)
                simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHED_NOW, yearsToRetirement = 0.0)
            }
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            assertTextFits("fire_reached_title", "$lang BASE REACHED", hasBadge = false)
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            assertTextFits("fire_reached_title", "$lang SIM REACHED", hasBadge = true)

            // 2) UNREACHABLE in both subtabs
            composeRule.runOnIdle {
                baseMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
                simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.UNREACHABLE)
            }
            composeRule.onNodeWithTag("fire_subtab_base").performClick()
            assertTextFits("fire_unreachable_title", "$lang BASE UNREACHABLE", hasBadge = false)
            composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
            assertTextFits("fire_unreachable_title", "$lang SIM UNREACHABLE", hasBadge = true)

            // 3) REACHABLE < 0.01 in Simulation subtab with badge
            composeRule.runOnIdle {
                simMetrics.value = sampleMetrics.copy(fireStatus = FireStatus.REACHABLE, yearsToRetirement = 0.004)
            }
            assertTextFits("fire_sub_hundredth_title", "$lang SIM SUB-HUNDREDTH", hasBadge = true)
        }
    }
}
