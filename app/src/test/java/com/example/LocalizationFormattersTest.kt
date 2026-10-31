package com.example

import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.PluralForms
import com.example.util.localization.getAppStrings
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LocalizationFormattersTest {
    @Test
    fun numberAndMoneyFormattingKeepsRussianAndEnglishOutput() {
        assertEquals("12 345,67", Formatters.formatNumber(12345.67, AppLanguage.RU))
        assertEquals("12,345.67", Formatters.formatNumber(12345.67, AppLanguage.EN))
        assertEquals("12 345,67 $", Formatters.formatMoney(12345.67, "$", AppLanguage.RU))
        assertEquals("$12,345.67", Formatters.formatMoney(12345.67, "$", AppLanguage.EN))
        assertEquals("12 345,67 €", Formatters.formatMoney(12345.67, "€", AppLanguage.RU))
        assertEquals("€12,345.67", Formatters.formatMoney(12345.67, "€", AppLanguage.EN))
        assertEquals("12 345,67%", Formatters.formatPercent(12345.67, AppLanguage.RU))
        assertEquals("12,345.67%", Formatters.formatPercent(12345.67, AppLanguage.EN))
        assertEquals("12 345,67", Formatters.formatRatio(12345.67, AppLanguage.RU))
        assertEquals("12,345.67", Formatters.formatRatio(12345.67, AppLanguage.EN))
        assertEquals("12 345,7", Formatters.formatScore(12345.67, AppLanguage.RU))
        assertEquals("12,345.7", Formatters.formatScore(12345.67, AppLanguage.EN))
    }

    @Test
    fun pluralUsesCldrCategoriesForRussianAndEnglish() {
        val forms = PluralForms(
            zero = "zero",
            one = "one",
            two = "two",
            few = "few",
            many = "many",
            other = "other"
        )
        val russian = getAppStrings(AppLanguage.RU)
        val english = getAppStrings(AppLanguage.EN)

        val values = listOf<Number>(0, 1, 2, 5, 11, 21, 101, 1.5)
        val russianExpected = listOf("many", "one", "few", "many", "many", "one", "one", "other")
        val englishExpected = listOf("other", "one", "other", "other", "other", "other", "other", "other")

        values.forEachIndexed { index, value ->
            assertEquals("RU $value", russianExpected[index], russian.plural(value, forms))
            assertEquals("EN $value", englishExpected[index], english.plural(value, forms))
        }

        assertEquals("1,5\u00A0чекбокса", russian.formatCount(1.5, russian.checkboxPluralForms))
        assertEquals("1.5\u00A0checkboxes", english.formatCount(1.5, english.checkboxPluralForms))
    }
}
