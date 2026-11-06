package com.example

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import com.example.ui.screens.NumberInputField
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NumberInputFieldRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun SemanticsNodeInteraction.assertEditableTextEquals(expected: String) {
        val actual = fetchSemanticsNode().config.getOrElse(SemanticsProperties.EditableText) { AnnotatedString("") }.text
        assertEquals(expected, actual)
    }

    @Test
    fun `yen pasted fractions round half up with dot and local separators`() {
        val value = mutableStateOf(0.0)
        val language = mutableStateOf(AppLanguage.EN)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides language.value) {
                NumberInputField("Money", value.value, "¥", { value.value = it }, "yen", allowNegative = true)
            }
        }

        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val separator = java.text.DecimalFormatSymbols.getInstance(lang.locale).decimalSeparator
            for ((input, expected) in listOf("1500.7" to "1501", "1500${separator}5" to "1501", "1500.4" to "1500", "-2.5" to "-3")) {
                composeRule.onNodeWithTag("yen").performTextClearance()
                composeRule.onNodeWithTag("yen").performTextInput(input)
                composeRule.onNodeWithTag("yen").assertEditableTextEquals(expected)
                assertEquals("$lang $input", expected.toDouble(), value.value, 0.0)
            }
        }
    }

    @Test
    fun `yen ignores typed decimal separators without changing saved fractions`() {
        val value = mutableStateOf(1500.7)
        var changes = 0
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU) {
                NumberInputField("Money", value.value, "¥", { value.value = it; changes++ }, "yen")
            }
        }
        composeRule.onNodeWithTag("yen").performClick()
        composeRule.onNodeWithTag("yen").assertEditableTextEquals("1501")
        for (separator in listOf(".", ",")) {
            composeRule.onNodeWithTag("yen").performTextInput(separator)
            composeRule.onNodeWithTag("yen").assertEditableTextEquals("1501")
            assertEquals(1500.7, value.value, 0.0)
            assertEquals(0, changes)
        }
        composeRule.onNodeWithTag("yen").performTextInput("2")
        composeRule.onNodeWithTag("yen").assertEditableTextEquals("15012")
        assertEquals(15012.0, value.value, 0.0)
    }

    @Test
    fun `currency changes and focus only reformat saved fractions`() {
        val value = mutableStateOf(1500.7)
        val currency = mutableStateOf("$")
        var changes = 0
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                Column {
                    NumberInputField("Money", value.value, currency.value, { value.value = it; changes++ }, "money")
                    NumberInputField("Other", 0.0, "%", {}, "other", isMoney = false)
                }
            }
        }
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1,500.70")
        composeRule.runOnIdle { currency.value = "¥" }
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1,501")
        composeRule.onNodeWithTag("money").performClick()
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1501")
        composeRule.runOnIdle { currency.value = "$" }
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1500.7")
        composeRule.runOnIdle { currency.value = "¥" }
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1501")
        composeRule.onNodeWithTag("other").performClick()
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1,501")
        composeRule.runOnIdle { currency.value = "$" }
        composeRule.onNodeWithTag("money").assertEditableTextEquals("1,500.70")
        assertEquals(1500.7, value.value, 0.0)
        assertEquals(0, changes)
    }

    @Test
    fun `percentage fields keep fractional input and half even display`() {
        val value = mutableStateOf(1.125)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Rate", value.value, "%", { value.value = it }, "rate", allowNegative = true, isMoney = false)
            }
        }
        composeRule.onNodeWithTag("rate").assertEditableTextEquals("1.12")
        composeRule.onNodeWithTag("rate").performTextClearance()
        composeRule.onNodeWithTag("rate").performTextInput("-1.5")
        composeRule.onNodeWithTag("rate").assertEditableTextEquals("-1.5")
        assertEquals(-1.5, value.value, 0.0)
    }

    @Test
    fun `empty and sign-only values parse as zero`() {
        assertEquals(0.0, Formatters.parseDouble(""), 0.0)
        assertEquals(0.0, Formatters.parseDouble("-"), 0.0)
        assertEquals(0.0, Formatters.parseDouble("-."), 0.0)
        assertEquals(0.0, Formatters.parseDouble("-,", AppLanguage.RU), 0.0)
    }

    @Test
    fun `decimal input keeps typed dot and parses 7 point 5`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            NumberInputField("Value", value.value, "$", { value.value = it }, "number_input")
        }

        composeRule.onNodeWithTag("number_input").performTextInput("7.5")

        composeRule.onNodeWithTag("number_input").assertEditableTextEquals("7.5")
        assertEquals(7.5, value.value, 0.0)
    }

    @Test
    fun `comma decimal input parses 0 comma 5`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            NumberInputField("Value", value.value, "$", { value.value = it }, "number_input")
        }

        composeRule.onNodeWithTag("number_input").performTextInput("0,5")

        composeRule.onNodeWithTag("number_input").assertEditableTextEquals("0,5")
        assertEquals(0.5, value.value, 0.0)
    }

    @Test
    fun `negative input is accepted for negative enabled fields`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            NumberInputField("Value", value.value, "%", { value.value = it }, "number_input", allowNegative = true, isMoney = false)
        }

        composeRule.onNodeWithTag("number_input").performTextInput("-1")

        composeRule.onNodeWithTag("number_input").assertEditableTextEquals("-1")
        assertEquals(-1.0, value.value, 0.0)
    }

    @Test
    fun `clearing the input keeps it empty and reports zero`() {
        val value = mutableStateOf(12.0)
        composeRule.setContent {
            NumberInputField("Value", value.value, "$", { value.value = it }, "number_input")
        }

        composeRule.onNodeWithTag("number_input").performTextClearance()

        composeRule.onNodeWithTag("number_input").assertEditableTextEquals("")
        assertEquals(0.0, value.value, 0.0)
    }

    @Test
    fun `typing 2 after 8 produces 82 without reformatting`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            NumberInputField("Value", value.value, "$", { value.value = it }, "number_input")
        }

        composeRule.onNodeWithTag("number_input").performTextInput("8")
        composeRule.onNodeWithTag("number_input").performTextInput("2")

        composeRule.onNodeWithTag("number_input").assertEditableTextEquals("82")
        assertEquals(82.0, value.value, 0.0)
    }

    // --- User Requirement 5 Tests ---

    @Test
    fun `EN input 1500 and 1500 point 5`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Value", value.value, "$", { value.value = it }, "input_en")
            }
        }

        composeRule.onNodeWithTag("input_en").performTextInput("1500")
        composeRule.onNodeWithTag("input_en").assertEditableTextEquals("1500")
        assertEquals(1500.0, value.value, 0.0)

        composeRule.onNodeWithTag("input_en").performTextInput(".5")
        composeRule.onNodeWithTag("input_en").assertEditableTextEquals("1500.5")
        assertEquals(1500.5, value.value, 0.0)
    }

    @Test
    fun `EN editing pre-filled 6000 shows raw 6000 on focus and typing 0 produces 60000`() {
        val value = mutableStateOf(6000.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Income", value.value, "$", { value.value = it }, "income_en")
            }
        }

        // Outside focus: formatted with grouping and two decimals
        composeRule.onNodeWithTag("income_en").assertEditableTextEquals("6,000.00")

        // Clicking gains focus: shows raw text without grouping and without .00
        composeRule.onNodeWithTag("income_en").performClick()
        composeRule.onNodeWithTag("income_en").assertEditableTextEquals("6000")

        // Appending '0' to raw 6000 yields 60000
        composeRule.onNodeWithTag("income_en").performTextInput("0")
        composeRule.onNodeWithTag("income_en").assertEditableTextEquals("60000")
        assertEquals(60000.0, value.value, 0.0)
    }

    @Test
    fun `DE input with comma 1500 comma 5 parses as 1500 point 5`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.DE) {
                NumberInputField("Value", value.value, "€", { value.value = it }, "input_de")
            }
        }
        composeRule.onNodeWithTag("input_de").performTextInput("1500,5")
        composeRule.onNodeWithTag("input_de").assertEditableTextEquals("1500,5")
        assertEquals(1500.5, value.value, 0.0)
    }

    @Test
    fun `ES input with comma 1500 comma 5 parses as 1500 point 5`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.ES) {
                NumberInputField("Value", value.value, "€", { value.value = it }, "input_es")
            }
        }
        composeRule.onNodeWithTag("input_es").performTextInput("1500,5")
        composeRule.onNodeWithTag("input_es").assertEditableTextEquals("1500,5")
        assertEquals(1500.5, value.value, 0.0)
    }

    @Test
    fun `IT input with comma 1500 comma 5 parses as 1500 point 5`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.IT) {
                NumberInputField("Value", value.value, "€", { value.value = it }, "input_it")
            }
        }
        composeRule.onNodeWithTag("input_it").performTextInput("1500,5")
        composeRule.onNodeWithTag("input_it").assertEditableTextEquals("1500,5")
        assertEquals(1500.5, value.value, 0.0)
    }

    @Test
    fun `RU input 1500 comma 5 and prefilled 6000 raw on focus`() {
        val value = mutableStateOf(6000.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU) {
                NumberInputField("Income", value.value, "₽", { value.value = it }, "income_ru")
            }
        }

        // Unfocused shows Russian grouping
        composeRule.onNodeWithTag("income_ru").assertEditableTextEquals("6 000,00")

        // On focus shows raw 6000 without tail
        composeRule.onNodeWithTag("income_ru").performClick()
        composeRule.onNodeWithTag("income_ru").assertEditableTextEquals("6000")

        // Clear and type 1500,5
        composeRule.onNodeWithTag("income_ru").performTextClearance()
        composeRule.onNodeWithTag("income_ru").performTextInput("1500,5")
        composeRule.onNodeWithTag("income_ru").assertEditableTextEquals("1500,5")
        assertEquals(1500.5, value.value, 0.0)
    }

    @Test
    fun `appending digit to prefilled 6000 gives 60000 without grouping interference`() {
        val value = mutableStateOf(6000.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.RU) {
                NumberInputField("Income", value.value, "₽", { value.value = it }, "income_field")
            }
        }

        // Clicking to enter focus mode converts text to raw 6000
        composeRule.onNodeWithTag("income_field").performClick()
        composeRule.onNodeWithTag("income_field").assertEditableTextEquals("6000")

        // Typing '0' appends to 6000 yielding 60000
        composeRule.onNodeWithTag("income_field").performTextInput("0")
        composeRule.onNodeWithTag("income_field").assertEditableTextEquals("60000")
        assertEquals(60000.0, value.value, 0.0)
    }

    @Test
    fun `pasted grouping comma in EN is ignored and parses as 1500`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Value", value.value, "$", { value.value = it }, "input_en_paste")
            }
        }

        composeRule.onNodeWithTag("input_en_paste").performTextInput("1,500")
        composeRule.onNodeWithTag("input_en_paste").assertEditableTextEquals("1500")
        assertEquals(1500.0, value.value, 0.0)
    }

    @Test
    fun `value is capped at 1e12 to prevent infinity`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Value", value.value, "$", { value.value = it }, "input_overflow")
            }
        }

        composeRule.onNodeWithTag("input_overflow").performTextInput("999999999999999999")
        assertEquals(Formatters.MAX_INPUT_VALUE, value.value, 0.0)
        assertTrue(value.value <= 1e12)
    }

    @Test
    fun `parseDouble correctly parses across locales without corrupting grouped numbers`() {
        // EN
        assertEquals(1500.0, Formatters.parseDouble("1500", AppLanguage.EN), 0.0)
        assertEquals(1500.5, Formatters.parseDouble("1500.5", AppLanguage.EN), 0.0)
        assertEquals(1500.0, Formatters.parseDouble("1,500", AppLanguage.EN), 0.0)
        assertEquals(6000.0, Formatters.parseDouble("6,000.00", AppLanguage.EN), 0.0)

        // DE, ES, IT
        assertEquals(1500.5, Formatters.parseDouble("1500,5", AppLanguage.DE), 0.0)
        assertEquals(1500.5, Formatters.parseDouble("1.500,5", AppLanguage.DE), 0.0)
        assertEquals(1500.5, Formatters.parseDouble("1500.5", AppLanguage.DE), 0.0) // fallback dot
        assertEquals(6000.0, Formatters.parseDouble("6.000,00", AppLanguage.DE), 0.0)

        assertEquals(1500.5, Formatters.parseDouble("1500,5", AppLanguage.ES), 0.0)
        assertEquals(1500.5, Formatters.parseDouble("1500,5", AppLanguage.IT), 0.0)

        // RU
        assertEquals(6000.0, Formatters.parseDouble("6 000,00", AppLanguage.RU), 0.0)
        assertEquals(1500.5, Formatters.parseDouble("1500,5", AppLanguage.RU), 0.0)
        assertEquals(1500.5, Formatters.parseDouble("1500.5", AppLanguage.RU), 0.0)

        // Overflow clamp
        assertEquals(1e12, Formatters.parseDouble("1000000000000000000", AppLanguage.EN), 0.0)
    }

    @Test
    fun `typing 7_555 in percent field saves 7_56 and typing 100_555 in money saves 100_56 without changing field text during focus`() {
        val percentValue = mutableStateOf(0.0)
        val moneyValue = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                Column {
                    NumberInputField("Percent", percentValue.value, "%", { percentValue.value = it }, "percent_field", isMoney = false)
                    NumberInputField("Money", moneyValue.value, "$", { moneyValue.value = it }, "money_field", isMoney = true)
                }
            }
        }

        composeRule.onNodeWithTag("percent_field").performTextInput("7.555")
        composeRule.onNodeWithTag("percent_field").assertEditableTextEquals("7.555")
        assertEquals(7.56, percentValue.value, 0.0)

        composeRule.onNodeWithTag("money_field").performTextInput("100.555")
        composeRule.onNodeWithTag("money_field").assertEditableTextEquals("100.555")
        assertEquals(100.56, moneyValue.value, 0.0)
    }

    @Test
    fun `percentage input saves half up ties while preserving focused text`() {
        val value = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Percent", value.value, "%", { value.value = it }, "percent", allowNegative = true, isMoney = false)
            }
        }
        composeRule.onNodeWithTag("percent").performClick()
        for ((input, expected) in listOf("7.545" to 7.55, "-7.545" to -7.55, "-0.001" to 0.0)) {
            composeRule.onNodeWithTag("percent").performTextClearance()
            composeRule.onNodeWithTag("percent").performTextInput(input)
            composeRule.onNodeWithTag("percent").assertEditableTextEquals(input)
            assertEquals(expected, value.value, 0.0)
        }
    }

    @Test
    fun `yen input and value are whole integers`() {
        val yenValue = mutableStateOf(0.0)
        composeRule.setContent {
            CompositionLocalProvider(LocalAppLanguage provides AppLanguage.EN) {
                NumberInputField("Yen", yenValue.value, "¥", { yenValue.value = it }, "yen_field", isMoney = true)
            }
        }
        composeRule.onNodeWithTag("yen_field").performTextInput("1234")
        composeRule.onNodeWithTag("yen_field").assertEditableTextEquals("1234")
        assertEquals(1234.0, yenValue.value, 0.0)

        // Typing dot or comma is ignored
        composeRule.onNodeWithTag("yen_field").performTextInput(".")
        composeRule.onNodeWithTag("yen_field").assertEditableTextEquals("1234")
        assertEquals(1234.0, yenValue.value, 0.0)

        composeRule.onNodeWithTag("yen_field").performTextInput(",")
        composeRule.onNodeWithTag("yen_field").assertEditableTextEquals("1234")
        assertEquals(1234.0, yenValue.value, 0.0)

        // Pasting decimal fraction rounds HALF_UP to whole integer
        composeRule.onNodeWithTag("yen_field").performTextClearance()
        composeRule.onNodeWithTag("yen_field").performTextInput("1234.6")
        composeRule.onNodeWithTag("yen_field").assertEditableTextEquals("1235")
        assertEquals(1235.0, yenValue.value, 0.0)
    }
}
