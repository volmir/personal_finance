package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
import com.example.ui.screens.CategoryBreakdownHint
import com.example.ui.screens.InputDataScreen
import com.example.ui.screens.MetricIndicatorRow
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(qualifiers = "ru-w360dp-h800dp")
class InputDataScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()
    private val today = LocalDate.of(2026, 11, 6)

    private fun showInput(input: MutableState<FinancialInputData>, language: MutableState<AppLanguage>) {
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    InputDataScreen(
                        input.value, FinanceCalculator.calculate(input.value, today = today),
                        onUpdate = { input.value = it(input.value) }, onNavigateToMetrics = {},
                        modifier = Modifier.testTag("input_test_list")
                    )
                }
            }
        }
        composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasTestTag("input_life_expectancy"))
    }

    @Test
    fun `assets and real return formulas use independent hints and restore after scrolling and recreation`() {
        val restoration = StateRestorationTester(composeRule)
        val input = FinancialInputData.usAverageData()
        restoration.setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalAppStrings provides StringsRu) {
                    InputDataScreen(
                        input, FinanceCalculator.calculate(input, today = today),
                        onUpdate = {}, onNavigateToMetrics = {},
                        modifier = Modifier.testTag("input_test_list")
                    )
                }
            }
        }
        fun scrollToTag(tag: String) {
            composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasTestTag(tag))
        }
        fun toggleHint(tag: String) {
            // Раскрытая подсказка может быть выше viewport: вызываем её доступное действие.
            composeRule.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.OnClick) { it() }
        }
        fun assertFormulaExpanded(tag: String, formula: String) {
            scrollToTag(tag)
            composeRule.onAllNodesWithText(formula, useUnmergedTree = true).assertCountEquals(1)
            composeRule.onNodeWithText(formula, useUnmergedTree = true)
                .assertTextEquals(formula).assert(hasAnyAncestor(hasTestTag(tag)))
            composeRule.onNode(
                hasText(StringsRu.metricCardFormulaPrefix.trimEnd()) and hasAnyAncestor(hasTestTag(tag)),
                useUnmergedTree = true
            ).assertExists()
        }
        for (tag in listOf("input_metric_total_assets", "input_metric_real_return")) {
            scrollToTag(tag)
            composeRule.onNode(hasText(StringsRu.metricCardInfo) and hasAnyAncestor(hasTestTag(tag)), useUnmergedTree = true)
                .assertDoesNotExist()
        }
        for ((tag, formula) in listOf(
            "input_assets_hint" to StringsRu.totalAssetsFormula,
            "input_investments_hint" to StringsRu.realReturnFormula
        )) {
            scrollToTag(tag)
            composeRule.onNodeWithText(formula, useUnmergedTree = true).assertDoesNotExist()
            toggleHint(tag)
            assertFormulaExpanded(tag, formula)
            toggleHint(tag)
            composeRule.onNodeWithText(formula, useUnmergedTree = true).assertDoesNotExist()
            toggleHint(tag)
        }
        assertFormulaExpanded("input_assets_hint", StringsRu.totalAssetsFormula)
        assertFormulaExpanded("input_investments_hint", StringsRu.realReturnFormula)
        // Налоговая подсказка раскрывается отдельно и не содержит формулы доходности.
        val taxHint = composeRule.onNode(hasText(StringsRu.capitalGainsTaxHintCollapsed) and hasClickAction())
        taxHint.performScrollTo().performClick()
        composeRule.onNodeWithText(StringsRu.capitalGainsTaxHintExpanded, useUnmergedTree = true).assertExists()
        scrollToTag("input_investments_hint")
        toggleHint("input_investments_hint")
        composeRule.onNodeWithText(StringsRu.capitalGainsTaxHintExpanded, useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText(StringsRu.realReturnFormula, useUnmergedTree = true).assertDoesNotExist()
        toggleHint("input_investments_hint")
        assertFormulaExpanded("input_investments_hint", StringsRu.realReturnFormula)
        scrollToTag("input_metric_horizon")
        restoration.emulateSavedInstanceStateRestore()
        assertFormulaExpanded("input_investments_hint", StringsRu.realReturnFormula)
        toggleHint("input_investments_hint")
        composeRule.onNodeWithText(StringsRu.realReturnFormula, useUnmergedTree = true).assertDoesNotExist()
        assertFormulaExpanded("input_assets_hint", StringsRu.totalAssetsFormula)
        toggleHint("input_assets_hint")
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula, useUnmergedTree = true).assertDoesNotExist()
        scrollToTag("input_investments_hint")
        composeRule.onNodeWithText(StringsRu.realReturnFormula, useUnmergedTree = true).assertDoesNotExist()
        for ((tag, formula) in listOf(
            "input_metric_cash_flow" to StringsRu.cashFlowFormula,
            "input_metric_savings_rate" to StringsRu.savingsRateFormula,
            "input_metric_net_worth" to StringsRu.netWorthFormula,
            "input_metric_horizon" to StringsRu.horizonFormula
        )) {
            scrollToTag(tag)
            composeRule.onNodeWithText(formula, useUnmergedTree = true).assertExists()
        }
    }

    @org.robolectric.annotation.Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    @Test
    fun `hint formulas preserve full text and style without value overlap at standard width with long text and theme risks`() {
        val language = mutableStateOf(AppLanguage.RU)
        val dark = mutableStateOf(false)
        val assetsExpanded = mutableStateOf(false)
        val investmentsExpanded = mutableStateOf(false)
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)
        var expectedStyle = androidx.compose.ui.text.TextStyle.Default
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides language.value) {
                com.example.ui.theme.MyApplicationTheme(darkTheme = dark.value, dynamicColor = false) {
                    val strings = getAppStrings(language.value)
                    expectedStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    CompositionLocalProvider(LocalAppStrings provides strings) {
                        // 360dp minus the screen's 16dp and section's 14dp padding on each side.
                        Column(Modifier.width(300.dp).verticalScroll(rememberScrollState())) {
                            CategoryBreakdownHint(
                                metricName = strings.assetsBreakdownName,
                                hintCollapsed = strings.assetsHintCollapsed,
                                hintExpanded = strings.assetsHintExpanded,
                                instruction = strings.breakdownInstruction,
                                categories = strings.assetsCategories,
                                isExpanded = assetsExpanded.value,
                                onExpandedChange = { assetsExpanded.value = it },
                                modifier = Modifier.testTag("input_assets_hint"),
                                formula = strings.totalAssetsFormula
                            )
                            MetricIndicatorRow(
                                title = strings.totalAssetsTitle,
                                formula = null,
                                value = com.example.util.Formatters.formatMoney(metrics.totalAssets, input.currencySymbol, language.value),
                                modifier = Modifier.testTag("assets")
                            )
                            CategoryBreakdownHint(
                                metricName = strings.investmentsBreakdownName,
                                hintCollapsed = strings.investmentsHintCollapsed,
                                hintExpanded = strings.investmentsHintExpanded,
                                instruction = strings.breakdownInstruction,
                                categories = strings.investmentsCategories,
                                isExpanded = investmentsExpanded.value,
                                onExpandedChange = { investmentsExpanded.value = it },
                                modifier = Modifier.testTag("input_investments_hint"),
                                formula = strings.realReturnFormula
                            )
                            MetricIndicatorRow(
                                title = strings.realReturnTitle,
                                formula = null,
                                value = com.example.util.Formatters.formatPercent(metrics.realReturnPercent, language.value),
                                modifier = Modifier.testTag("return")
                            )
                        }
                    }
                }
            }
        }
        for ((lang, isDark) in listOf(AppLanguage.RU to false, AppLanguage.DE to false, AppLanguage.RU to true)) {
            composeRule.runOnIdle {
                dark.value = isDark
                language.value = lang
            }
            val strings = getAppStrings(lang)
            for ((tag, hintTag, title, formula, value) in listOf(
                listOf("assets", "input_assets_hint", strings.totalAssetsTitle, strings.totalAssetsFormula,
                    com.example.util.Formatters.formatMoney(metrics.totalAssets, input.currencySymbol, lang)),
                listOf("return", "input_investments_hint", strings.realReturnTitle, strings.realReturnFormula,
                    com.example.util.Formatters.formatPercent(metrics.realReturnPercent, lang))
            )) {
                val valueNode = composeRule.onNodeWithText(value, useUnmergedTree = true).performScrollTo()
                valueNode.assertTextEquals(value)
                assertTextFits(valueNode)
                val titleNode = composeRule.onNodeWithText(title, useUnmergedTree = true)
                assertTextFits(titleNode)
                assertTrue("$lang title overlaps value",
                    titleNode.fetchSemanticsNode().boundsInWindow.right <= valueNode.fetchSemanticsNode().boundsInWindow.left)
                composeRule.onNode(hasText(strings.metricCardInfo) and hasAnyAncestor(hasTestTag(tag)), useUnmergedTree = true)
                    .assertDoesNotExist()
                val hint = composeRule.onNodeWithTag(hintTag).performScrollTo()
                hint.assert(hasClickAction())
                composeRule.onNodeWithText(formula, useUnmergedTree = true).assertDoesNotExist()
                composeRule.onNodeWithText(strings.metricCardFormulaPrefix.trimEnd(), useUnmergedTree = true).assertDoesNotExist()
                hint.performClick()
                val formulaNode = composeRule.onNodeWithText(formula, useUnmergedTree = true).performScrollTo()
                composeRule.onAllNodesWithText(formula, useUnmergedTree = true).assertCountEquals(1)
                formulaNode.assertTextEquals(formula).assert(hasAnyAncestor(hasTestTag(hintTag)))
                assertEquals(expectedStyle, formulaNode.textStyle())
                assertTextFits(formulaNode)
                val prefix = composeRule.onNodeWithText(strings.metricCardFormulaPrefix.trimEnd(), useUnmergedTree = true)
                prefix.assertExists()
                val formulaBounds = formulaNode.fetchSemanticsNode().boundsInWindow
                val hintBounds = hint.fetchSemanticsNode().boundsInWindow
                assertTrue(formulaBounds.left >= hintBounds.left)
                assertTrue(formulaBounds.right <= hintBounds.right)
                assertTrue(prefix.fetchSemanticsNode().boundsInWindow.bottom <= formulaBounds.top)
                // Формула завершает подсказку, итоговая строка расположена ниже неё.
                valueNode.performScrollTo()
                assertTrue(formulaNode.fetchSemanticsNode().boundsInWindow.bottom <= valueNode.fetchSemanticsNode().boundsInWindow.top)
                hint.performClick()
                composeRule.onNodeWithText(formula, useUnmergedTree = true).assertDoesNotExist()
                prefix.assertDoesNotExist()
            }
        }
    }

    @Test
    fun `expanded category hints without formula omit formula section`() {
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalAppStrings provides StringsRu) {
                    CategoryBreakdownHint(
                        metricName = StringsRu.assetsBreakdownName,
                        hintCollapsed = StringsRu.assetsHintCollapsed,
                        hintExpanded = StringsRu.assetsHintExpanded,
                        instruction = StringsRu.breakdownInstruction,
                        categories = StringsRu.assetsCategories,
                        isExpanded = true,
                        onExpandedChange = {}
                    )
                }
            }
        }
        composeRule.onNodeWithText(StringsRu.assetsHintExpanded, useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText(StringsRu.metricCardFormulaPrefix.trimEnd(), useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun `birth date is localized outside editing and ISO input remains unchanged`() {
        val input = mutableStateOf(FinancialInputData.usAverageData().copy(birthDate = "2000-01-02"))
        val language = mutableStateOf(AppLanguage.RU)
        showInput(input, language)
        for (lang in listOf(AppLanguage.RU, AppLanguage.EN, AppLanguage.DE)) {
            composeRule.runOnIdle { language.value = lang }
            composeRule.onNodeWithTag("input_birth_date")
                .assertTextEquals(getAppStrings(lang).fireBirthDateLabel, com.example.util.Formatters.formatDate("2000-01-02", lang))
        }
        composeRule.onNodeWithTag("input_birth_date").performClick()
        composeRule.onNodeWithTag("input_birth_date")
            .assertTextEquals(getAppStrings(AppLanguage.DE).birthDateLabel, "2000-01-02")
        composeRule.onNodeWithTag("input_birth_date").performTextClearance()
        composeRule.onNodeWithTag("input_birth_date").performTextInput("2001-02-03")
        composeRule.runOnIdle { assertEquals("2001-02-03", input.value.birthDate) }
        composeRule.onNodeWithTag("input_life_expectancy").performClick()
        composeRule.onNodeWithTag("input_birth_date")
            .assertTextEquals(getAppStrings(AppLanguage.DE).fireBirthDateLabel, com.example.util.Formatters.formatDate("2001-02-03", AppLanguage.DE))
    }

    @Test
    fun `invalid and empty life expectancy show the saved calculation value in every language`() {
        val input = mutableStateOf(FinancialInputData.usAverageData())
        val language = mutableStateOf(AppLanguage.RU)
        val usedValues = mapOf(
            AppLanguage.RU to "В расчётах: 82 года",
            AppLanguage.EN to "Calculation: 82 years",
            AppLanguage.DE to "Berechnung: 82 Jahre",
            AppLanguage.FR to "Calcul : 82 ans",
            AppLanguage.IT to "Calcolo: 82 anni",
            AppLanguage.ES to "Cálculo: 82 años"
        )
        showInput(input, language)
        for ((lang, usedValue) in usedValues) {
            composeRule.runOnIdle { language.value = lang }
            for (raw in listOf("", "38", "121", "999999999999")) {
                composeRule.onNodeWithTag("input_life_expectancy").performTextClearance()
                if (raw.isNotEmpty()) composeRule.onNodeWithTag("input_life_expectancy").performTextInput(raw)
                composeRule.onNodeWithText(usedValue).assertExists()
                assertEquals("$lang $raw", 82, input.value.lifeExpectancy)
            }
            composeRule.onNodeWithTag("input_life_expectancy").performTextClearance()
            composeRule.onNodeWithTag("input_life_expectancy").performTextInput("120")
            composeRule.onNodeWithText(usedValue).assertDoesNotExist()
            composeRule.onNodeWithText(getAppStrings(lang).lifeExpectancyError).assertDoesNotExist()
            composeRule.runOnIdle { input.value = input.value.copy(lifeExpectancy = 82) }
        }
    }

    @Test
    fun `changing birth date exhausts the saved horizon and marks life expectancy invalid`() {
        val input = mutableStateOf(FinancialInputData.usAverageData())
        showInput(input, mutableStateOf(AppLanguage.RU))
        for (birthDate in listOf("1944-11-06", "1943-11-06")) {
            composeRule.onNodeWithTag("input_birth_date").performTextClearance()
            composeRule.onNodeWithTag("input_birth_date").performTextInput(birthDate)
            composeRule.onNodeWithText(getAppStrings(AppLanguage.RU).lifeExpectancyError).assertExists()
            composeRule.onNodeWithText("В расчётах: 82 года").assertExists()
            assertEquals(82, input.value.lifeExpectancy)
            assertEquals(FireStatus.HORIZON_EXHAUSTED, FinanceCalculator.calculate(input.value, today = today).fireStatus)
        }
        composeRule.onNodeWithTag("input_birth_date").performTextClearance()
        composeRule.onNodeWithTag("input_birth_date").performTextInput("1945-11-06")
        composeRule.onNodeWithText(getAppStrings(AppLanguage.RU).lifeExpectancyError).assertDoesNotExist()
        composeRule.onNodeWithText("В расчётах: 82 года").assertDoesNotExist()
    }

    @Test
    fun `invalid birth date shows unavailable horizon in every language`() {
        val input = mutableStateOf(FinancialInputData.usAverageData().copy(birthDate = "invalid-date"))
        val language = mutableStateOf(AppLanguage.RU)
        showInput(input, language)
        assertFalse(FinanceCalculator.calculate(input.value, today = today).isAgeValid)
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            composeRule.onNodeWithText(strings.metricUnavailableValue).assertExists()
            composeRule.onNodeWithText(strings.horizonValueFormat(0, 0)).assertDoesNotExist()
        }
    }

    @Test
    fun `view metrics button keeps its action and a 48 dp secondary action target`() {
        var navigatedToMetrics = false
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu
                ) {
                    InputDataScreen(
                        inputData = FinancialInputData.defaultData(),
                        metrics = FinanceCalculator.calculate(FinancialInputData.defaultData(), today = today),
                        onUpdate = {},
                        onNavigateToMetrics = { navigatedToMetrics = true },
                        modifier = Modifier.testTag("input_test_list")
                    )
                }
            }
        }

        composeRule.onNodeWithTag("input_test_list")
            .performScrollToNode(hasTestTag("go_to_metrics_button"))
        composeRule.onNodeWithTag("go_to_metrics_button")
            .assertExists()
            .assert(hasClickAction())
            .assertHeightIsAtLeast(48.dp)
            .assertTextEquals(StringsRu.calculateButtonText)
            .performClick()

        assertTrue(navigatedToMetrics)
    }

    @org.robolectric.annotation.Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    @Test
    fun `horizon row displays formula with unified style, main result, and numeric substitution across languages and themes`() {
        val input = mutableStateOf(
            FinancialInputData.usAverageData().copy(
                birthDate = today.minusYears(38).toString(),
                lifeExpectancy = 82
            )
        )
        val language = mutableStateOf(AppLanguage.RU)
        val darkTheme = mutableStateOf(false)

        composeRule.setContent {
            com.example.ui.theme.MyApplicationTheme(darkTheme = darkTheme.value, dynamicColor = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    InputDataScreen(
                        input.value,
                        FinanceCalculator.calculate(input.value, today = today),
                        onUpdate = { input.value = it(input.value) },
                        onNavigateToMetrics = {},
                        modifier = Modifier.testTag("input_test_list")
                    )
                }
            }
        }

        // Сверяем стиль формулы горизонта с формулой чистого капитала на вкладке №1
        composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasTestTag("input_metric_net_worth"))
        val netWorthFormulaStyle = composeRule.onNodeWithText(StringsRu.netWorthFormula, useUnmergedTree = true)
            .textStyle()

        composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasTestTag("input_metric_horizon"))
        composeRule.onNodeWithText(StringsRu.horizonTitle).assertExists()
        composeRule.onNodeWithText(StringsRu.horizonFormula).assertExists()
        composeRule.onNodeWithText("44 года").assertExists()
        composeRule.onNodeWithText("82 \u2212 38").assertExists()

        val horizonFormulaNode = composeRule.onNodeWithText(StringsRu.horizonFormula, useUnmergedTree = true)
        val horizonFormulaStyle = horizonFormulaNode.textStyle()
        assertEquals(netWorthFormulaStyle.fontFamily, horizonFormulaStyle.fontFamily)
        assertEquals(netWorthFormulaStyle.fontSize, horizonFormulaStyle.fontSize)
        assertEquals(netWorthFormulaStyle.color, horizonFormulaStyle.color)

        // Формула не обрезана многоточием и имеет достаточную ширину слева без наложения справа
        val formulaLayout = horizonFormulaNode.textLayoutResult()
        assertFalse(formulaLayout.hasVisualOverflow)
        val formulaBounds = horizonFormulaNode.fetchSemanticsNode().boundsInWindow
        val resultBounds = composeRule.onNodeWithText("44 года", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow
        val subBounds = composeRule.onNodeWithText("82 \u2212 38", useUnmergedTree = true).fetchSemanticsNode().boundsInWindow
        assertTrue(formulaBounds.width > resultBounds.width)
        assertTrue(formulaBounds.right <= resultBounds.left)
        assertTrue(resultBounds.bottom <= subBounds.top)

        // Long formula text and the theme-dependent formula style.
        for ((lang, isDark) in listOf(AppLanguage.RU to false, AppLanguage.DE to false, AppLanguage.RU to true)) {
            composeRule.runOnIdle {
                darkTheme.value = isDark
                language.value = lang
            }
            composeRule.waitForIdle()
            val localized = getAppStrings(lang)
            composeRule.onNodeWithText(localized.horizonTitle).assertExists()
            composeRule.onNodeWithText(localized.horizonFormula).assertExists()
            composeRule.onNodeWithText(localized.horizonValueFormat(44, 38)).assertExists()
            composeRule.onNodeWithText("82 \u2212 38").assertExists()
            assertFalse(composeRule.onNodeWithText(localized.horizonFormula, useUnmergedTree = true).textLayoutResult().hasVisualOverflow)
        }

        // Динамическое обновление даты рождения и возраста дожития
        composeRule.runOnIdle {
            darkTheme.value = false
            language.value = AppLanguage.RU
            input.value = input.value.copy(
                birthDate = today.minusYears(41).toString(),
                lifeExpectancy = 90
            )
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("49 лет").assertExists()
        composeRule.onNodeWithText("90 \u2212 41").assertExists()

        // Нулевой горизонт (82 - 82 -> 0 лет) и исчерпанный/отрицательный (82 - 85 -> 0 лет)
        composeRule.runOnIdle {
            input.value = input.value.copy(
                birthDate = today.minusYears(82).toString(),
                lifeExpectancy = 82
            )
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("0 лет").assertExists()
        composeRule.onNodeWithText("82 \u2212 82").assertExists()

        composeRule.runOnIdle {
            input.value = input.value.copy(
                birthDate = today.minusYears(85).toString(),
                lifeExpectancy = 82
            )
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("0 лет").assertExists()
        composeRule.onNodeWithText("82 \u2212 85").assertExists()
    }

    @org.robolectric.annotation.Config(qualifiers = "w360dp-h800dp-xxhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    @Test
    fun `horizon row has no clipping or overlap in standard and long text locales`() {
        val input = mutableStateOf(
            FinancialInputData.usAverageData().copy(
                birthDate = today.minusYears(38).toString(),
                lifeExpectancy = 82
            )
        )
        val language = mutableStateOf(AppLanguage.RU)

        composeRule.setContent {
            com.example.ui.theme.MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                CompositionLocalProvider(
                    LocalAppLanguage provides language.value,
                    LocalAppStrings provides getAppStrings(language.value)
                ) {
                    InputDataScreen(
                        input.value,
                        FinanceCalculator.calculate(input.value, today = today),
                        onUpdate = {},
                        onNavigateToMetrics = {},
                        modifier = Modifier.testTag("input_test_list")
                    )
                }
            }
        }

        for (lang in listOf(AppLanguage.RU, AppLanguage.DE)) {
            composeRule.runOnIdle { language.value = lang }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasTestTag("input_metric_horizon"))
            composeRule.waitForIdle()
            val localized = getAppStrings(lang)
            val titleNode = composeRule.onNodeWithText(localized.horizonTitle, useUnmergedTree = true)
            val formulaNode = composeRule.onNodeWithText(localized.horizonFormula, useUnmergedTree = true)
            val valueNode = composeRule.onNodeWithText(localized.horizonValueFormat(44, 38), useUnmergedTree = true)
            val subNode = composeRule.onNodeWithText("82 \u2212 38", useUnmergedTree = true)

            val titleLayout = titleNode.textLayoutResult()
            val formulaLayout = formulaNode.textLayoutResult()
            val valueLayout = valueNode.textLayoutResult()
            val subLayout = subNode.textLayoutResult()
            assertFalse("$lang title height overflow", titleLayout.didOverflowHeight)
            assertFalse("$lang formula height overflow", formulaLayout.didOverflowHeight)
            assertFalse("$lang value height overflow", valueLayout.didOverflowHeight)
            assertFalse("$lang subValue height overflow", subLayout.didOverflowHeight)
            assertTrue("$lang title width fits constraints", titleLayout.size.width <= titleLayout.layoutInput.constraints.maxWidth)
            assertTrue("$lang formula width fits constraints", formulaLayout.size.width <= formulaLayout.layoutInput.constraints.maxWidth)
            assertTrue("$lang value width fits constraints", valueLayout.size.width <= valueLayout.layoutInput.constraints.maxWidth)
            assertTrue("$lang subValue width fits constraints", subLayout.size.width <= subLayout.layoutInput.constraints.maxWidth)

            val titleBounds = titleNode.fetchSemanticsNode().boundsInWindow
            val formulaBounds = formulaNode.fetchSemanticsNode().boundsInWindow
            val valueBounds = valueNode.fetchSemanticsNode().boundsInWindow
            val subBounds = subNode.fetchSemanticsNode().boundsInWindow
            assertTrue("$lang title/value overlap: $titleBounds vs $valueBounds", titleBounds.right <= valueBounds.left)
            assertTrue("$lang formula/value overlap: $formulaBounds vs $valueBounds", formulaBounds.right <= valueBounds.left)
            assertTrue("$lang title/formula vertical order: $titleBounds vs $formulaBounds", titleBounds.bottom <= formulaBounds.top)
            assertTrue("$lang value/sub overlap: $valueBounds vs $subBounds", valueBounds.bottom <= subBounds.top)
        }
    }

    private fun assertTextFits(node: androidx.compose.ui.test.SemanticsNodeInteraction) {
        val layout = node.textLayoutResult()
        // Like TypographyLayoutRobolectricTest: intrinsic-width semantics layouts can
        // report didOverflowWidth against the original maxWidth. Check the actual lines.
        assertFalse(layout.didOverflowHeight)
        for (line in 0 until layout.lineCount) {
            assertFalse(layout.isLineEllipsized(line))
            assertTrue(layout.getLineRight(line) - layout.getLineLeft(line) <= layout.size.width + 0.5f)
        }
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteraction.textLayoutResult(): androidx.compose.ui.text.TextLayoutResult {
        val results = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        val config = fetchSemanticsNode().config
        val action = checkNotNull(config[androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult].action)
        action(results)
        return results.first()
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteraction.textStyle(): androidx.compose.ui.text.TextStyle =
        textLayoutResult().layoutInput.style
}
