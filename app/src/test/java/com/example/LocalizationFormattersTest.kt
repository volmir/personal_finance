package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.model.AuditIndicatorStatus
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.PluralForms
import com.example.util.localization.getAppStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LocalizationFormattersTest {
    @Test
    fun yenUsesWholeUnitsWithLocalizedGroupingAndExistingSignPlacement() {
        val numbers = mapOf(
            "en" to "1,235", "de" to "1.235", "fr" to "1 235",
            "it" to "1.235", "es" to "1.235", "ru" to "1 235", "be" to "1 235"
        )
        for ((code, number) in numbers) {
            val lang = AppLanguage.fromLocaleCode(code)
            val positive = when (lang) {
                AppLanguage.EN -> "¥$number"
                AppLanguage.RU -> "$number ¥"
                else -> "$number\u00A0¥"
            }
            val negative = if (lang == AppLanguage.RU) "-$number ¥" else "-¥$number"
            val zeroYen = when (lang) {
                AppLanguage.EN -> "¥0"
                AppLanguage.RU -> "0 ¥"
                else -> "0\u00A0¥"
            }
            assertEquals(code, positive, Formatters.formatMoney(1234.5, "¥", lang))
            assertEquals(code, negative, Formatters.formatMoney(-1234.5, "¥", lang))
            assertEquals(code, zeroYen, Formatters.formatMoney(-0.0, "¥", lang))
        }
    }

    @Test
    fun currencyPrecisionAndHalfUpRoundingApplyOnlyToMoney() {
        assertEquals(0, Formatters.currencyFractionDigits("¥"))
        for (currency in listOf("$", "€", "CHF", "₽", "unknown")) {
            assertEquals(currency, 2, Formatters.currencyFractionDigits(currency))
        }
        assertEquals("¥3", Formatters.formatMoney(2.5, "¥", AppLanguage.EN))
        assertEquals("-¥3", Formatters.formatMoney(-2.5, "¥", AppLanguage.EN))
        for (currency in listOf("$", "€")) {
            assertEquals("${currency}1,234.50", Formatters.formatMoney(1234.5, currency, AppLanguage.EN))
            assertEquals("${currency}1.13", Formatters.formatMoney(1.125, currency, AppLanguage.EN))
            assertEquals("-${currency}1.13", Formatters.formatMoney(-1.125, currency, AppLanguage.EN))
        }
        assertEquals("1.12%", Formatters.formatPercent(1.125, AppLanguage.EN))
        assertEquals("1.12", Formatters.formatRatio(1.125, AppLanguage.EN))
        assertEquals("1.2", Formatters.formatScore(1.25, AppLanguage.EN))
        assertEquals("1.12", Formatters.formatNumber(1.125, AppLanguage.EN))
    }

    @Test
    fun negativeMoneyPlacesSignBeforeCurrencyExceptRussianAndPreservesPositiveFormat() {
        val numbers = mapOf(
            AppLanguage.RU to "1 000,00",
            AppLanguage.EN to "1,000.00",
            AppLanguage.DE to "1.000,00",
            AppLanguage.FR to "1 000,00",
            AppLanguage.IT to "1.000,00",
            AppLanguage.ES to "1.000,00"
        )
        for ((lang, number) in numbers) {
            for (currency in listOf("$", "€", "CHF")) {
                val negative = if (lang == AppLanguage.RU) "-$number $currency" else "-$currency$number"
                assertEquals("$lang $currency negative", negative, Formatters.formatMoney(-1000.0, currency, lang))
                val positive = when (lang) {
                    AppLanguage.RU -> "$number $currency"
                    AppLanguage.EN -> "$currency$number"
                    else -> "$number\u00A0$currency"
                }
                assertEquals("$lang $currency positive", positive, Formatters.formatMoney(1000.0, currency, lang))
                val zero = if (lang == AppLanguage.EN) "0.00" else "0,00"
                val zeroMoney = when (lang) {
                    AppLanguage.RU -> "$zero $currency"
                    AppLanguage.EN -> "$currency$zero"
                    else -> "$zero\u00A0$currency"
                }
                assertEquals("$lang $currency negative zero without minus", zeroMoney, Formatters.formatMoney(-0.0, currency, lang))
            }
        }
    }

    @Test
    fun formatMoneyAndPercentWithoutMinusForValuesRoundingToZeroAcrossAllLanguages() {
        for (lang in AppLanguage.values()) {
            val zeroPercent = if (lang == AppLanguage.EN) "0.00%" else "0,00%"
            assertEquals("$lang percent -0.001", zeroPercent, Formatters.formatPercent(-0.001, lang))
            assertEquals("$lang percent -0.0", zeroPercent, Formatters.formatPercent(-0.0, lang))

            for (currency in listOf("$", "€", "CHF", "₽")) {
                val zero = if (lang == AppLanguage.EN) "0.00" else "0,00"
                val expected = when (lang) {
                    AppLanguage.RU -> "$zero $currency"
                    AppLanguage.EN -> "$currency$zero"
                    else -> "$zero\u00A0$currency"
                }
                assertEquals("$lang $currency money -0.001", expected, Formatters.formatMoney(-0.001, currency, lang))
                assertEquals("$lang $currency money -0.0", expected, Formatters.formatMoney(-0.0, currency, lang))
            }

            val expectedYen = when (lang) {
                AppLanguage.RU -> "0 ¥"
                AppLanguage.EN -> "¥0"
                else -> "0\u00A0¥"
            }
            assertEquals("$lang yen -0.001", expectedYen, Formatters.formatMoney(-0.001, "¥", lang))
            assertEquals("$lang yen -0.0", expectedYen, Formatters.formatMoney(-0.0, "¥", lang))
        }
    }

    @Test
    fun nonFiniteMoneyNumbersAndPercentagesAreUnavailableInEveryLanguage() {
        for (lang in AppLanguage.values()) {
            for (value in listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
                assertEquals("$lang $value money", "—", Formatters.formatMoney(value, "€", lang))
                assertEquals("$lang $value yen", "—", Formatters.formatMoney(value, "¥", lang))
                assertEquals("$lang $value number", "—", Formatters.formatNumber(value, lang))
                assertEquals("$lang $value percentage", "—", Formatters.formatPercent(value, lang))
            }
        }
    }

    @Test
    fun infiniteDebtRatioUsesUnavailableExcessAndFiniteDebtKeepsPercentage() {
        for (assets in listOf(0.0, 100.0)) {
            val input = FinancialInputData(liquidAssets = assets, financialAssets = 0.0, propertyAssets = 0.0, liabilities = 150.0)
            val metrics = FinanceCalculator.calculate(input, today = java.time.LocalDate.of(2026, 10, 31))
            assertEquals(assets == 0.0, metrics.debtRatio.isInfinite())
            for (lang in AppLanguage.values()) {
                val strings = getAppStrings(lang)
                val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
                val debt = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "debt_ratio" }
                val excess = if (assets == 0.0) strings.metricUnavailableValue else Formatters.formatPercent(50.0, lang)
                assertEquals(AuditIndicatorStatus.NEGATIVE, debt.status)
                assertEquals(String.format(java.util.Locale.ROOT, strings.auditDebtRatioCritical,
                    Formatters.formatRatio(metrics.debtRatio, lang), excess), debt.text)
                assertFalse(debt.text.contains("NaN"))
                assertFalse(debt.text.contains("Infinity"))
                assertFalse(debt.text.contains("∞"))
                assertTrue(opinion.toFormattedShareText(input, metrics, lang).contains(debt.text))
            }
        }
    }

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

    @Test
    fun newLanguagesAreProperlyLoadedAndFormatted() {
        val languages = listOf(AppLanguage.DE, AppLanguage.FR, AppLanguage.IT, AppLanguage.ES)
        for (lang in languages) {
            val strings = getAppStrings(lang)
            assertEquals(lang, strings.language)
            org.junit.Assert.assertNotNull(strings.appName)
            org.junit.Assert.assertTrue(strings.appName.isNotEmpty())
            org.junit.Assert.assertTrue(strings.block1Title.isNotEmpty())
            org.junit.Assert.assertTrue(strings.disclaimerText.isNotEmpty())
            org.junit.Assert.assertTrue(strings.fireAssumptionsTitle.isNotEmpty())
            val formatted = Formatters.formatMoney(1000.0, "€", lang)
            org.junit.Assert.assertTrue(formatted.contains("1") && formatted.contains("000") && formatted.contains("€"))
            val capitalLine = strings.metricFidelityCapitalFormat("100 000 €")
            org.junit.Assert.assertTrue(capitalLine.contains("100 000 €"))
        }

        val ruFidelityLine = getAppStrings(AppLanguage.RU).metricFidelityCapitalFormat("55 000 $")
        org.junit.Assert.assertEquals("Учитываемый капитал: 55 000 $", ruFidelityLine)

        val enFidelityLine = getAppStrings(AppLanguage.EN).metricFidelityCapitalFormat("$55,000")
        org.junit.Assert.assertEquals("Eligible capital: $55,000", enFidelityLine)

        val ruRatio = getAppStrings(AppLanguage.RU).fidelityProgressRatioFormat("55 000 $", "187 200 $")
        org.junit.Assert.assertEquals("(55 000 $ из 187 200 $)", ruRatio)

        val enRatio = getAppStrings(AppLanguage.EN).fidelityProgressRatioFormat("$55,000", "$187,200")
        org.junit.Assert.assertEquals("($55,000 of $187,200)", enRatio)
    }

    @Test
    fun testFireYearsSuffixPluralization() {
        val ru = getAppStrings(AppLanguage.RU)
        val en = getAppStrings(AppLanguage.EN)
        val de = getAppStrings(AppLanguage.DE)
        val fr = getAppStrings(AppLanguage.FR)
        val es = getAppStrings(AppLanguage.ES)
        val it = getAppStrings(AppLanguage.IT)

        // Fractional numbers in Russian take genitive singular: "года"
        org.junit.Assert.assertEquals("года", ru.fireYearsSuffix(27.18))
        org.junit.Assert.assertEquals("года", ru.fireYearsSuffix(15.5))

        // Russian integer numbers
        org.junit.Assert.assertEquals("год", ru.fireYearsSuffix(1.0))
        org.junit.Assert.assertEquals("года", ru.fireYearsSuffix(2.0))
        org.junit.Assert.assertEquals("лет", ru.fireYearsSuffix(5.0))
        org.junit.Assert.assertEquals("лет", ru.fireYearsSuffix(11.0))
        org.junit.Assert.assertEquals("год", ru.fireYearsSuffix(21.0))
        org.junit.Assert.assertEquals("года", ru.fireYearsSuffix(23.0))
        org.junit.Assert.assertEquals("лет", ru.fireYearsSuffix(25.0))

        // English: 1.0 year, others years
        org.junit.Assert.assertEquals("year", en.fireYearsSuffix(1.0))
        org.junit.Assert.assertEquals("years", en.fireYearsSuffix(2.0))
        org.junit.Assert.assertEquals("years", en.fireYearsSuffix(27.18))

        // German: 1.0 Jahr, others Jahre
        org.junit.Assert.assertEquals("Jahr", de.fireYearsSuffix(1.0))
        org.junit.Assert.assertEquals("Jahre", de.fireYearsSuffix(2.0))
        org.junit.Assert.assertEquals("Jahre", de.fireYearsSuffix(27.18))

        // French: 1.0 an, 2.0+ ans
        org.junit.Assert.assertEquals("an", fr.fireYearsSuffix(1.0))
        org.junit.Assert.assertEquals("ans", fr.fireYearsSuffix(2.0))
        org.junit.Assert.assertEquals("ans", fr.fireYearsSuffix(27.18))

        // Spanish: 1.0 año, others años
        org.junit.Assert.assertEquals("año", es.fireYearsSuffix(1.0))
        org.junit.Assert.assertEquals("años", es.fireYearsSuffix(2.0))
        org.junit.Assert.assertEquals("años", es.fireYearsSuffix(27.18))

        // Italian: 1.0 anno, others anni
        org.junit.Assert.assertEquals("anno", it.fireYearsSuffix(1.0))
        org.junit.Assert.assertEquals("anni", it.fireYearsSuffix(2.0))
        org.junit.Assert.assertEquals("anni", it.fireYearsSuffix(27.18))

        // Verify no language returns any parenthesis or placeholder in fireYearsSuffix
        listOf(ru, en, de, fr, es, it).forEach { strings ->
            org.junit.Assert.assertFalse(strings.fireYearsSuffix(27.18).contains("("))
            org.junit.Assert.assertFalse(strings.fireYearsSuffix(1.0).contains("("))
            org.junit.Assert.assertFalse(strings.fireYearsSuffix.contains("("))
        }
    }
}
