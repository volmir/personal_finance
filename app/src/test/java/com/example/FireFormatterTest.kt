package com.example

import com.example.data.model.FireStatus
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FireFormatterTest {

    @Test
    fun `dates follow app language regardless of default locale and reject invalid dates`() {
        val original = java.util.Locale.getDefault()
        try {
            for (systemLocale in listOf(java.util.Locale.JAPAN, java.util.Locale.US)) {
                java.util.Locale.setDefault(systemLocale)
                for ((lang, expected) in mapOf(AppLanguage.RU to "2 янв. 2000 г.",
                    AppLanguage.EN to "Jan 2, 2000", AppLanguage.DE to "02.01.2000")) {
                    // CLDR в JDK 17/21 различается пробелом перед «г.».
                    assertEquals(expected, Formatters.formatDate("2000-01-02", lang).replace('\u202F', ' '))
                }
                for (lang in AppLanguage.values()) {
                    for (invalid in listOf("", "invalid", "2000-02-30", "2000-13-01")) {
                        assertEquals("—", Formatters.formatDate(invalid, lang))
                    }
                }
            }
        } finally {
            java.util.Locale.setDefault(original)
        }
    }

    @Test
    fun `special FIRE statuses never fall back to numeric years or infinity`() {
        for (lang in AppLanguage.values()) {
            val strings = com.example.util.localization.getAppStrings(lang)
            for ((status, expected) in mapOf(FireStatus.REACHED_NOW to strings.fireReachedBaseText,
                FireStatus.UNREACHABLE to strings.fireUnreachableText,
                FireStatus.HORIZON_EXHAUSTED to strings.fireHorizonExhaustedText,
                FireStatus.INVALID_AGE to strings.invalidBirthDateError)) {
                for (value in listOf(0.0, Double.POSITIVE_INFINITY, Double.NaN)) {
                    assertEquals("$lang $status $value", expected,
                        Formatters.formatFireYearsToRetirement(value, status, lang))
                    if (status != FireStatus.REACHED_NOW) {
                        assertEquals(expected, Formatters.formatFireRetirementAge(value, status, lang))
                    }
                    assertEquals(if (status == FireStatus.REACHED_NOW) strings.fireReachedSimulationText else expected,
                        Formatters.formatFireSimulationSummary(24.0, 60.0, FireStatus.REACHABLE,
                            value, value, status, lang))
                }
            }
            assertEquals(strings.fireLessThanHundredthYearText,
                Formatters.formatFireYearsToRetirement(0.004, FireStatus.REACHABLE, lang))
            assertEquals("—", Formatters.formatYears(Double.POSITIVE_INFINITY, lang))
            assertEquals("0", Formatters.formatYears(-0.0, lang))
            assertEquals("0", Formatters.formatYears(-0.001, lang))
            assertEquals(strings.fireSimWithoutBase(if (lang == AppLanguage.EN) "21.5" else "21,5", "60"),
                Formatters.formatFireSimulationSummary(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                    FireStatus.UNREACHABLE, 21.5, 60.0, FireStatus.REACHABLE, lang))
        }
    }

    @Test
    fun `FIRE years remove only insignificant fractional zeroes`() {
        for (lang in AppLanguage.values()) {
            for ((value, expected) in listOf(24.0 to "24", 21.5 to if (lang == AppLanguage.EN) "21.5" else "21,5",
                21.56 to if (lang == AppLanguage.EN) "21.56" else "21,56")) {
                assertEquals("$lang $value", expected,
                    Formatters.formatFireYearsToRetirement(value, FireStatus.REACHABLE, lang))
                val strings = com.example.util.localization.getAppStrings(lang)
                assertEquals(strings.fireRetirementAgeValue(expected),
                    Formatters.formatFireRetirementAge(value, FireStatus.REACHABLE, lang))
            }
        }
    }

    @Test
    fun `target capital is unavailable for invalid age unreachable and exhausted horizon`() {
        for (lang in AppLanguage.values()) {
            val unavailable = com.example.util.localization.getAppStrings(lang).metricUnavailableValue
            for (status in listOf(FireStatus.INVALID_AGE, FireStatus.UNREACHABLE, FireStatus.HORIZON_EXHAUSTED)) {
                for (amount in listOf(0.0, 1_234_567.89)) {
                    assertEquals(
                        "$lang $status $amount",
                        unavailable,
                        Formatters.formatFireTargetCapital(amount, "€", status, lang)
                    )
                }
            }
        }
    }

    @Test
    fun `target capital keeps money formatting when FIRE is reached or reachable`() {
        for (lang in AppLanguage.values()) {
            for (status in listOf(FireStatus.REACHED_NOW, FireStatus.REACHABLE)) {
                for (amount in listOf(0.0, 1_234_567.89)) {
                    assertEquals(
                        "$lang $status $amount",
                        Formatters.formatMoney(amount, "€", lang),
                        Formatters.formatFireTargetCapital(amount, "€", status, lang)
                    )
                }
            }
        }
    }

    @Test
    fun testFireUnreachableUsesStatus() {
        assertTrue(Formatters.isFireUnreachable(FireStatus.UNREACHABLE))
        assertFalse(Formatters.isFireUnreachable(FireStatus.REACHABLE))
        assertFalse(Formatters.isFireUnreachable(FireStatus.REACHED_NOW))
        assertFalse(Formatters.isFireUnreachable(FireStatus.HORIZON_EXHAUSTED))
        assertFalse(Formatters.isFireUnreachable(FireStatus.INVALID_AGE))
    }

    @Test
    fun testFormatFireYearsAndAgeByStatus() {
        assertEquals("15,5", Formatters.formatFireYearsToRetirement(15.5, FireStatus.REACHABLE, AppLanguage.RU))
        assertEquals("15.5", Formatters.formatFireYearsToRetirement(15.5, FireStatus.REACHABLE, AppLanguage.EN))
        assertEquals("Недостижимо при текущих параметрах", Formatters.formatFireYearsToRetirement(45.0, FireStatus.UNREACHABLE, AppLanguage.RU))
        assertEquals(com.example.util.localization.getAppStrings(AppLanguage.EN).fireHorizonExhaustedText, Formatters.formatFireRetirementAge(80.0, FireStatus.HORIZON_EXHAUSTED, AppLanguage.EN))
        assertEquals("Укажите корректную дату рождения", Formatters.formatFireYearsToRetirement(0.0, FireStatus.INVALID_AGE, AppLanguage.RU))
        assertEquals("Please specify a valid birth date", Formatters.formatFireRetirementAge(0.0, FireStatus.INVALID_AGE, AppLanguage.EN))

        val expectedShortUnreachable = mapOf(
            AppLanguage.RU to "Недостижимо",
            AppLanguage.EN to "Unreachable",
            AppLanguage.DE to "Nicht erreichbar",
            AppLanguage.FR to "Inatteignable",
            AppLanguage.ES to "Inalcanzable",
            AppLanguage.IT to "Non raggiungibile"
        )
        for (lang in AppLanguage.values()) {
            val strings = com.example.util.localization.getAppStrings(lang)
            assertEquals(
                "$lang compact UNREACHABLE",
                expectedShortUnreachable[lang],
                Formatters.formatFireRetirementAge(80.0, FireStatus.UNREACHABLE, lang, compactUnreachable = true)
            )
            assertEquals(
                "$lang compact equals fireUnreachableShortText",
                strings.fireUnreachableShortText,
                Formatters.formatFireRetirementAge(80.0, FireStatus.UNREACHABLE, lang, compactUnreachable = true)
            )
            assertEquals(
                "$lang full UNREACHABLE unchanged",
                strings.fireUnreachableText,
                Formatters.formatFireRetirementAge(80.0, FireStatus.UNREACHABLE, lang)
            )
            assertEquals(
                "$lang compact REACHABLE",
                strings.fireRetirementAgeValue(Formatters.formatYears(62.5, lang)),
                Formatters.formatFireRetirementAge(62.5, FireStatus.REACHABLE, lang, compactUnreachable = true)
            )
            assertEquals(
                "$lang compact REACHED_NOW",
                strings.fireRetirementAgeValue(Formatters.formatYears(38.0, lang)),
                Formatters.formatFireRetirementAge(38.0, FireStatus.REACHED_NOW, lang, compactUnreachable = true)
            )
        }
    }

    @Test
    fun testFormatFireSimulationSummaryUsesStatuses() {
        val unreachable = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 20.0,
            baseRetirementAge = 55.0,
            baseStatus = FireStatus.REACHABLE,
            simulatedYearsToRetirement = 45.0,
            simulatedRetirementAge = 80.0,
            simulatedStatus = FireStatus.UNREACHABLE,
            lang = AppLanguage.RU
        )
        assertEquals("Недостижимо при текущих параметрах", unreachable)

        val improved = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 45.0,
            baseRetirementAge = 80.0,
            baseStatus = FireStatus.UNREACHABLE,
            simulatedYearsToRetirement = 20.0,
            simulatedRetirementAge = 55.0,
            simulatedStatus = FireStatus.REACHABLE,
            lang = AppLanguage.RU
        )
        assertEquals("Пенсия достижима через 20 лет (в возрасте 55 лет).", improved)

        val invalid = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 20.0,
            baseRetirementAge = 55.0,
            baseStatus = FireStatus.REACHABLE,
            simulatedYearsToRetirement = 0.0,
            simulatedRetirementAge = 0.0,
            simulatedStatus = FireStatus.INVALID_AGE,
            lang = AppLanguage.RU
        )
        assertTrue(invalid.contains("корректную дату рождения"))
        assertFalse(invalid.contains("раньше"))
    }

    @Test
    fun `hero display shows status-specific messages and prevents 0,00 display across all languages`() {
        for (lang in AppLanguage.values()) {
            val strings = com.example.util.localization.getAppStrings(lang)

            val baseReached = Formatters.formatFireHeroDisplay(0.0, FireStatus.REACHED_NOW, isSimulationSubtab = false, lang = lang)
            assertEquals(
                com.example.util.FireHeroDisplay.Message(strings.fireReachedBaseText, isPositive = true, testTag = "fire_reached_title"),
                baseReached
            )
            assertTrue("$lang base reached must keep FIRE", strings.fireReachedBaseText.contains("FIRE"))

            val simReached = Formatters.formatFireHeroDisplay(0.0, FireStatus.REACHED_NOW, isSimulationSubtab = true, lang = lang)
            assertEquals(
                com.example.util.FireHeroDisplay.Message(strings.fireReachedSimulationText, isPositive = true, testTag = "fire_reached_title"),
                simReached
            )
            assertTrue("$lang sim reached must keep FIRE", strings.fireReachedSimulationText.contains("FIRE"))

            val baseUnreachable = Formatters.formatFireHeroDisplay(0.0, FireStatus.UNREACHABLE, isSimulationSubtab = false, lang = lang)
            assertEquals(
                com.example.util.FireHeroDisplay.Message(strings.fireUnreachableText, isPositive = false, testTag = "fire_unreachable_title"),
                baseUnreachable
            )

            val simUnreachable = Formatters.formatFireHeroDisplay(0.0, FireStatus.UNREACHABLE, isSimulationSubtab = true, lang = lang)
            assertEquals(
                com.example.util.FireHeroDisplay.Message(strings.fireUnreachableSimulationText, isPositive = false, testTag = "fire_unreachable_title"),
                simUnreachable
            )

            val exhausted = Formatters.formatFireHeroDisplay(0.0, FireStatus.HORIZON_EXHAUSTED, isSimulationSubtab = true, lang = lang)
            assertEquals(
                com.example.util.FireHeroDisplay.Message(strings.fireHorizonExhaustedText, isPositive = false, testTag = "fire_unreachable_title"),
                exhausted
            )

            val invalidAge = Formatters.formatFireHeroDisplay(0.0, FireStatus.INVALID_AGE, isSimulationSubtab = false, lang = lang)
            assertEquals(
                com.example.util.FireHeroDisplay.Message(strings.invalidBirthDateError, isPositive = false, testTag = "fire_unreachable_title"),
                invalidAge
            )

            // Положительные значения < 0.005 (HALF_EVEN -> 0.00) не должны показываться как "0,00" или "Цель достигнута"
            for (tiny in listOf(0.0001, 0.004, 0.005)) {
                val subHundredth = Formatters.formatFireHeroDisplay(tiny, FireStatus.REACHABLE, isSimulationSubtab = false, lang = lang)
                assertEquals(
                    com.example.util.FireHeroDisplay.Message(strings.fireLessThanHundredthYearText, isPositive = true, testTag = "fire_sub_hundredth_title"),
                    subHundredth
                )
            }

            // Граница округления вверх (>= 0.0051) -> числовой формат >= 0.01
            for (positive in listOf(0.0051, 0.01)) {
                val numeric = Formatters.formatFireHeroDisplay(positive, FireStatus.REACHABLE, isSimulationSubtab = false, lang = lang)
                assertTrue("$lang $positive should be Numeric", numeric is com.example.util.FireHeroDisplay.Numeric)
                val formatted = (numeric as com.example.util.FireHeroDisplay.Numeric).formattedYears
                assertEquals(Formatters.formatRatio(0.01, lang), formatted)
            }
        }
    }
}
