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
}
