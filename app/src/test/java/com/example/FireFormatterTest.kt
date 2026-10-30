package com.example

import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FireFormatterTest {

    @Test
    fun testIsFireUnreachableThreshold() {
        val lifeExpectancy = 80

        // Граница: retirementAge >= lifeExpectancy - 0.01 (>= 79.99)
        assertTrue(Formatters.isFireUnreachable(80.0, lifeExpectancy))
        assertTrue(Formatters.isFireUnreachable(79.99, lifeExpectancy))
        assertTrue(Formatters.isFireUnreachable(85.0, lifeExpectancy))

        // Ниже границы -> достижимо
        assertFalse(Formatters.isFireUnreachable(79.989, lifeExpectancy))
        assertFalse(Formatters.isFireUnreachable(60.0, lifeExpectancy))
        assertFalse(Formatters.isFireUnreachable(45.5, lifeExpectancy))
    }

    @Test
    fun testFormatFireYearsToRetirementRuAndEn() {
        val lifeExpectancy = 80

        // 1. Достижимый FIRE (RU и EN)
        assertEquals(
            "15,50",
            Formatters.formatFireYearsToRetirement(
                yearsToRetirement = 15.5,
                retirementAge = 50.5,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.RU
            )
        )
        assertEquals(
            "15.50",
            Formatters.formatFireYearsToRetirement(
                yearsToRetirement = 15.5,
                retirementAge = 50.5,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.EN
            )
        )

        // 2. Недостижимый FIRE: retirementAge >= lifeExpectancy - 0.01
        assertEquals(
            "Недостижимо при текущих параметрах",
            Formatters.formatFireYearsToRetirement(
                yearsToRetirement = 45.0,
                retirementAge = 80.0,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.RU
            )
        )
        assertEquals(
            "Unachievable with current parameters",
            Formatters.formatFireYearsToRetirement(
                yearsToRetirement = 45.0,
                retirementAge = 80.0,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.EN
            )
        )

        // 3. Пограничный случай: retirementAge ровно 79.99 при дожитии 80
        assertEquals(
            "Недостижимо при текущих параметрах",
            Formatters.formatFireYearsToRetirement(
                yearsToRetirement = 44.99,
                retirementAge = 79.99,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.RU
            )
        )
        assertEquals(
            "Unachievable with current parameters",
            Formatters.formatFireYearsToRetirement(
                yearsToRetirement = 44.99,
                retirementAge = 79.99,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.EN
            )
        )
    }

    @Test
    fun testFormatFireRetirementAgeRuAndEn() {
        val lifeExpectancy = 80

        // 1. Достижимый FIRE
        assertEquals(
            "55,00 лет",
            Formatters.formatFireRetirementAge(
                retirementAge = 55.0,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.RU
            )
        )
        assertEquals(
            "55.00 yrs",
            Formatters.formatFireRetirementAge(
                retirementAge = 55.0,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.EN
            )
        )

        // 2. Недостижимый FIRE
        assertEquals(
            "Недостижимо при текущих параметрах",
            Formatters.formatFireRetirementAge(
                retirementAge = 80.0,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.RU
            )
        )
        assertEquals(
            "Unachievable with current parameters",
            Formatters.formatFireRetirementAge(
                retirementAge = 80.0,
                lifeExpectancy = lifeExpectancy,
                lang = AppLanguage.EN
            )
        )
    }

    @Test
    fun testFormatFireSimulationSummaryUnreachableSimulation() {
        val lifeExpectancy = 80

        // База достижима (возраст 55), но в симуляции пенсионный возраст уперся в 80 (недостижимо)
        val summaryRu = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 20.0,
            baseRetirementAge = 55.0,
            simulatedYearsToRetirement = 45.0,
            simulatedRetirementAge = 80.0,
            lifeExpectancy = lifeExpectancy,
            lang = AppLanguage.RU
        )
        assertEquals("Недостижимо при текущих параметрах", summaryRu)
        assertFalse(summaryRu.contains("раньше"))
        assertFalse(summaryRu.contains("позже"))

        val summaryEn = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 20.0,
            baseRetirementAge = 55.0,
            simulatedYearsToRetirement = 45.0,
            simulatedRetirementAge = 80.0,
            lifeExpectancy = lifeExpectancy,
            lang = AppLanguage.EN
        )
        assertEquals("Unachievable with current parameters", summaryEn)
        assertFalse(summaryEn.contains("earlier"))
        assertFalse(summaryEn.contains("later"))
    }

    @Test
    fun testFormatFireSimulationSummaryUnreachableBase() {
        val lifeExpectancy = 80

        // База недостижима (возраст 80), но в симуляции FIRE стал достижим (возраст 55, срок 20)
        // Разница в годах с базой должна быть скрыта
        val summaryRu = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 45.0,
            baseRetirementAge = 80.0,
            simulatedYearsToRetirement = 20.0,
            simulatedRetirementAge = 55.0,
            lifeExpectancy = lifeExpectancy,
            lang = AppLanguage.RU
        )
        assertEquals("Пенсия достижима через 20,00 лет (в возрасте 55,00 лет).", summaryRu)
        assertFalse(summaryRu.contains("раньше"))
        assertFalse(summaryRu.contains("25,00")) // разница 45 - 20 = 25 скрыта!

        val summaryEn = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 45.0,
            baseRetirementAge = 80.0,
            simulatedYearsToRetirement = 20.0,
            simulatedRetirementAge = 55.0,
            lifeExpectancy = lifeExpectancy,
            lang = AppLanguage.EN
        )
        assertEquals("Retirement reachable in 20.00 yrs (at age 55.00 yrs).", summaryEn)
        assertFalse(summaryEn.contains("earlier"))
        assertFalse(summaryEn.contains("25.00"))
    }

    @Test
    fun testFormatFireSimulationSummaryBothReachable() {
        val lifeExpectancy = 80

        // Оба сценария достижимы: разница отображается нормально
        val earlierRu = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 20.0,
            baseRetirementAge = 55.0,
            simulatedYearsToRetirement = 15.0,
            simulatedRetirementAge = 50.0,
            lifeExpectancy = lifeExpectancy,
            lang = AppLanguage.RU
        )
        assertTrue(earlierRu.contains("раньше"))
        assertTrue(earlierRu.contains("5,00"))

        val laterEn = Formatters.formatFireSimulationSummary(
            baseYearsToRetirement = 20.0,
            baseRetirementAge = 55.0,
            simulatedYearsToRetirement = 25.0,
            simulatedRetirementAge = 60.0,
            lifeExpectancy = lifeExpectancy,
            lang = AppLanguage.EN
        )
        assertTrue(laterEn.contains("increased"))
        assertTrue(laterEn.contains("5.00"))
    }
}
