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
        assertEquals("15,50", Formatters.formatFireYearsToRetirement(15.5, FireStatus.REACHABLE, AppLanguage.RU))
        assertEquals("15.50", Formatters.formatFireYearsToRetirement(15.5, FireStatus.REACHABLE, AppLanguage.EN))
        assertEquals("Недостижимо при текущих параметрах", Formatters.formatFireYearsToRetirement(45.0, FireStatus.UNREACHABLE, AppLanguage.RU))
        assertEquals("Unachievable with current parameters", Formatters.formatFireRetirementAge(80.0, FireStatus.HORIZON_EXHAUSTED, AppLanguage.EN))
        assertEquals("Укажите корректную дату рождения", Formatters.formatFireYearsToRetirement(0.0, FireStatus.INVALID_AGE, AppLanguage.RU))
        assertEquals("Please specify a valid birth date", Formatters.formatFireRetirementAge(0.0, FireStatus.INVALID_AGE, AppLanguage.EN))
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
        assertEquals("Пенсия достижима через 20,00 лет (в возрасте 55,00 лет).", improved)

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
