package com.example

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
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
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NumberInputFieldRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun SemanticsNodeInteraction.assertEditableTextEquals(expected: String) {
        val actual = fetchSemanticsNode().config.getOrElse(SemanticsProperties.EditableText) { AnnotatedString("") }.text
        assertEquals(expected, actual)
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
            NumberInputField("Value", value.value, "%", { value.value = it }, "number_input", allowNegative = true)
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
}
