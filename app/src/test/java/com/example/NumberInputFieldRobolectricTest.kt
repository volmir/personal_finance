package com.example

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import com.example.ui.screens.NumberInputField
import com.example.util.Formatters
import org.junit.Assert.assertEquals
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
}
