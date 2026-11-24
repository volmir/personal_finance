package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
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
    fun `only assets and real return formulas collapse independently and restore after scrolling and recreation`() {
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
        fun scrollTo(text: String) {
            composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasText(text))
        }
        scrollTo(StringsRu.totalAssetsTitle)
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula).assertDoesNotExist()
        composeRule.onNodeWithText(StringsRu.metricCardInfo).performScrollTo().performClick()
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula).assertExists()
        scrollTo(StringsRu.realReturnTitle)
        composeRule.onNodeWithText(StringsRu.realReturnFormula).assertDoesNotExist()
        composeRule.onNodeWithText(StringsRu.metricCardInfo).performScrollTo().performClick()
        composeRule.onNodeWithText(StringsRu.realReturnFormula).assertExists()
        scrollTo(StringsRu.totalAssetsTitle)
        composeRule.onNodeWithText(StringsRu.metricCardInfo).performScrollTo()
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula).assertExists()
        scrollTo(StringsRu.realReturnTitle)
        restoration.emulateSavedInstanceStateRestore()
        scrollTo(StringsRu.realReturnTitle)
        composeRule.onNodeWithText(StringsRu.realReturnFormula).assertExists()
        composeRule.onNodeWithText(StringsRu.metricCardInfo).performScrollTo().performClick()
        composeRule.onNodeWithText(StringsRu.realReturnFormula).assertDoesNotExist()
        scrollTo(StringsRu.totalAssetsTitle)
        composeRule.onNodeWithText(StringsRu.metricCardInfo).performScrollTo()
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula).assertExists()
        composeRule.onNodeWithText(StringsRu.metricCardInfo).performScrollTo().performClick()
        composeRule.onNodeWithText(StringsRu.totalAssetsFormula).assertDoesNotExist()
        for ((tag, formula) in listOf(
            "input_metric_cash_flow" to StringsRu.cashFlowFormula,
            "input_metric_savings_rate" to StringsRu.savingsRateFormula,
            "input_metric_net_worth" to StringsRu.netWorthFormula,
            "input_metric_horizon" to StringsRu.horizonFormula
        )) {
            composeRule.onNodeWithTag("input_test_list").performScrollToNode(hasTestTag(tag))
            composeRule.onNodeWithText(formula).assertExists()
        }
    }

    @org.robolectric.annotation.Config(qualifiers = "w320dp-h800dp-xxhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    @Test
    fun `collapsible formulas preserve full text and style without value overlap at 320dp in all languages themes and font scales`() {
        val language = mutableStateOf(AppLanguage.RU)
        val dark = mutableStateOf(false)
        val scale = mutableStateOf(1f)
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)
        var expectedStyle = androidx.compose.ui.text.TextStyle.Default
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, scale.value)) {
                com.example.ui.theme.MyApplicationTheme(darkTheme = dark.value, dynamicColor = false) {
                    val strings = getAppStrings(language.value)
                    expectedStyle = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    CompositionLocalProvider(LocalAppStrings provides strings) {
                        // 320dp minus the screen's 16dp and section's 14dp padding on each side.
                        Column(Modifier.width(260.dp)) {
                            MetricIndicatorRow(
                                title = strings.totalAssetsTitle,
                                formula = strings.totalAssetsFormula,
                                value = com.example.util.Formatters.formatMoney(metrics.totalAssets, input.currencySymbol, language.value),
                                collapsibleFormula = true,
                                modifier = Modifier.testTag("assets")
                            )
                            MetricIndicatorRow(
                                title = strings.realReturnTitle,
                                formula = strings.realReturnFormula,
                                value = com.example.util.Formatters.formatPercent(metrics.realReturnPercent, language.value),
                                collapsibleFormula = true,
                                modifier = Modifier.testTag("return")
                            )
                        }
                    }
                }
            }
        }
        for (isDark in listOf(false, true)) {
            for (lang in AppLanguage.values()) {
                for (fontScale in listOf(1f, 1.6f)) {
                    composeRule.runOnIdle {
                        dark.value = isDark
                        language.value = lang
                        scale.value = fontScale
                    }
                    val strings = getAppStrings(lang)
                    for ((tag, formula, value) in listOf(
                        Triple("assets", strings.totalAssetsFormula, com.example.util.Formatters.formatMoney(metrics.totalAssets, input.currencySymbol, lang)),
                        Triple("return", strings.realReturnFormula, com.example.util.Formatters.formatPercent(metrics.realReturnPercent, lang))
                    )) {
                        val control = composeRule.onNode(hasText(strings.metricCardInfo) and hasAnyAncestor(hasTestTag(tag)))
                        val valueNode = composeRule.onNodeWithText(value, useUnmergedTree = true)
                        val valueBounds = valueNode.fetchSemanticsNode().boundsInWindow
                        val controlBounds = control.fetchSemanticsNode().boundsInWindow
                        valueNode.assertTextEquals(value)
                        assertTextFits(valueNode)
                        assertTrue("$lang $fontScale control overlaps value", valueBounds.bottom <= controlBounds.top)
                        control.assertHeightIsAtLeast(48.dp)
                        composeRule.onNodeWithText(formula).assertDoesNotExist()
                        control.performClick()
                        val formulaNode = composeRule.onNodeWithText(formula, useUnmergedTree = true)
                        formulaNode.assertTextEquals(formula)
                        assertEquals(expectedStyle, formulaNode.textStyle())
                        assertTextFits(formulaNode)
                        assertTrue(control.fetchSemanticsNode().boundsInWindow.bottom <= formulaNode.fetchSemanticsNode().boundsInWindow.top)
                        control.performClick()
                        composeRule.onNodeWithText(formula).assertDoesNotExist()
                    }
                }
            }
        }
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

        // Все 6 языков и тёмная тема
        for (isDark in listOf(false, true)) {
            composeRule.runOnIdle { darkTheme.value = isDark }
            for (lang in AppLanguage.values()) {
                composeRule.runOnIdle { language.value = lang }
                composeRule.waitForIdle()
                val localized = getAppStrings(lang)
                composeRule.onNodeWithText(localized.horizonTitle).assertExists()
                composeRule.onNodeWithText(localized.horizonFormula).assertExists()
                composeRule.onNodeWithText(localized.horizonValueFormat(44, 38)).assertExists()
                composeRule.onNodeWithText("82 \u2212 38").assertExists()
                assertFalse(composeRule.onNodeWithText(localized.horizonFormula, useUnmergedTree = true).textLayoutResult().hasVisualOverflow)
            }
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

    @org.robolectric.annotation.Config(qualifiers = "w320dp-h640dp-xxhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    @Test
    fun `horizon row has no clipping or overlap on narrow screens in all languages`() {
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

        for (lang in AppLanguage.values()) {
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
