package com.example

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.ui.screens.InputDataScreen
import com.example.ui.screens.NumberInputField
import com.example.util.localization.*
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h800dp-mdpi")
class InputKeyboardRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    private class Keyboard : SoftwareKeyboardController {
        var hides = 0
        override fun show() {}
        override fun hide() { hides++ }
    }

    private fun showScreen(keyboard: Keyboard, onNavigate: () -> Unit = {}) {
        val input = mutableStateOf(FinancialInputData.usAverageData())
        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.EN,
                    LocalAppStrings provides StringsEn,
                    LocalSoftwareKeyboardController provides keyboard
                ) {
                    InputDataScreen(
                        input.value, FinanceCalculator.calculate(input.value, today = LocalDate.of(2026, 11, 6)),
                        onUpdate = { input.value = it(input.value) }, onNavigateToMetrics = onNavigate,
                        modifier = Modifier.testTag("list")
                    )
                }
            }
        }
    }

    @Test
    fun `Next visits every editable field in visual order and Done clears focus and hides IME`() {
        val keyboard = Keyboard()
        showScreen(keyboard)
        val fields = listOf(
            "monthly_income", "monthly_expenses", "liquid_assets", "financial_assets",
            "property_assets", "liabilities", "nominal_return", "inflation",
            "capital_gains_tax", "birth_date", "life_expectancy"
        )
        composeRule.onNodeWithTag("input_${fields.first()}").performClick()
        for ((index, field) in fields.withIndex()) {
            val node = composeRule.onNodeWithTag("input_$field")
            node.assertIsFocused()
            assertEquals(if (index == fields.lastIndex) ImeAction.Done else ImeAction.Next,
                node.fetchSemanticsNode().config[SemanticsProperties.ImeAction])
            node.performImeAction()
            composeRule.waitForIdle()
        }
        composeRule.onNodeWithTag("input_life_expectancy").assertIsNotFocused()
        assertTrue(keyboard.hides > 0)
    }

    @Test
    fun `blank tap dismisses but field taps currency chips scroll switches and buttons still work`() {
        val keyboard = Keyboard()
        var navigated = false
        showScreen(keyboard) { navigated = true }
        val income = composeRule.onNodeWithTag("input_monthly_income")
        income.performTouchInput { click() }.assertIsFocused()
        val before = keyboard.hides
        composeRule.onNodeWithTag("currency_chip_€").performTouchInput { click() }
        income.assertIsFocused()
        assertEquals(before, keyboard.hides)
        // Внешний промежуток между валютами и первой карточкой.
        composeRule.onNodeWithTag("list").performTouchInput { click(Offset(width / 2f, 1f)) }
        income.assertIsNotFocused()
        assertTrue(keyboard.hides > before)

        income.performTouchInput { click() }
        val beforeScroll = keyboard.hides
        composeRule.onNodeWithTag("list").performTouchInput { swipeUp() }
        assertEquals(beforeScroll, keyboard.hides)
        composeRule.onNodeWithTag("list").performScrollToNode(hasTestTag("toggle_emergency_fund_mode"))
        val toggle = composeRule.onNodeWithTag("toggle_emergency_fund_mode")
        val previous = toggle.fetchSemanticsNode().config[SemanticsProperties.ToggleableState]
        val beforeSwitch = keyboard.hides
        toggle.performTouchInput { click() }
        assertTrue(previous != toggle.fetchSemanticsNode().config[SemanticsProperties.ToggleableState])
        assertEquals(beforeSwitch, keyboard.hides)
        composeRule.onNodeWithTag("list").performScrollToNode(hasTestTag("go_to_metrics_button"))
        composeRule.onNodeWithTag("go_to_metrics_button").performTouchInput { click() }
        assertTrue(navigated)
    }

    @Test
    fun `money and percent keyboards allow decimals while yen stays integral`() {
        lateinit var view: View
        composeRule.setContent {
            view = LocalView.current
            Column {
                NumberInputField("Money", 0.0, "$", {}, "money")
                NumberInputField("Percent", 0.0, "%", {}, "percent", isMoney = false)
                NumberInputField("Yen", 0.0, "¥", {}, "yen")
            }
        }
        for ((tag, decimal) in listOf("money" to true, "percent" to true, "yen" to false)) {
            composeRule.onNodeWithTag(tag).performClick()
            composeRule.runOnIdle {
                val info = EditorInfo()
                assertTrue(view.onCreateInputConnection(info) != null)
                assertEquals(InputType.TYPE_CLASS_NUMBER, info.inputType and InputType.TYPE_MASK_CLASS)
                assertEquals(decimal, info.inputType and InputType.TYPE_NUMBER_FLAG_DECIMAL != 0)
            }
        }
    }

    @Test
    fun `both decimal separators preserve intermediate text cursor negatives and rounding in ru de en`() {
        val value = mutableStateOf(0.0)
        val lang = mutableStateOf(AppLanguage.RU)
        val currency = mutableStateOf("$")
        val money = mutableStateOf(true)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides lang.value) {
                Column(Modifier.focusable()) {
                    NumberInputField("Value", value.value, currency.value, { value.value = it }, "number",
                        allowNegative = true, isMoney = money.value)
                }
            }
        }
        val node = composeRule.onNodeWithTag("number")
        for (language in listOf(AppLanguage.RU, AppLanguage.DE, AppLanguage.EN)) {
            composeRule.runOnIdle { lang.value = language }
            for (isMoney in listOf(true, false)) {
                composeRule.runOnIdle { money.value = isMoney }
                for (separator in listOf(",", ".")) {
                    node.performTextClearance()
                    node.performTextInput("15")
                    node.performTextInput(separator)
                    assertEquals("15$separator", node.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
                    assertEquals(15.0, value.value, 0.0)
                    assertEquals(TextRange(3), node.fetchSemanticsNode().config[SemanticsProperties.TextSelectionRange])
                    node.performTextInput("5")
                    assertEquals(15.5, value.value, 0.0)
                    // Вставка в середину сохраняет позицию курсора и сам разделитель.
                    node.performSemanticsAction(SemanticsActions.SetSelection) { it(1, 1, false) }
                    node.performTextInput("2")
                    assertEquals("125${separator}5", node.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
                    assertEquals(TextRange(2), node.fetchSemanticsNode().config[SemanticsProperties.TextSelectionRange])
                    node.performTextClearance()
                    node.performTextInput("-15${separator}555")
                    assertEquals(-15.56, value.value, 0.0)
                    assertEquals("-15${separator}555", node.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
                    node.performImeAction()
                    node.assertIsNotFocused()
                    assertEquals(if (language == AppLanguage.EN) "-15.56" else "-15,56",
                        node.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
                }
            }
        }
    }
}
