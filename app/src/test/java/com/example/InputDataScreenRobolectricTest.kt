package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.FinanceCalculator
import com.example.ui.screens.InputDataScreen
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}
