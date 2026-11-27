package com.example

import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class DecimalInputParsingTest {
    @Test
    fun `ungrouped decimal input accepts both separators and intermediate states`() {
        for ((text, expected) in listOf(
            "15,5" to 15.5, "15.5" to 15.5,
            "15," to 15.0, "15." to 15.0,
            "-15,5" to -15.5, "-15.5" to -15.5,
            "" to 0.0, "-" to 0.0, "-," to 0.0, "-." to 0.0,
            "0,005" to 0.005, "0.005" to 0.005,
            "1000000000001" to Formatters.MAX_INPUT_VALUE
        )) {
            assertEquals(text, expected, Formatters.parseDecimalInput(text), 0.0)
        }
    }

    @Test
    fun `empty and sign-only values parse as zero`() {
        assertEquals(0.0, Formatters.parseDouble(""), 0.0)
        assertEquals(0.0, Formatters.parseDouble("-"), 0.0)
        assertEquals(0.0, Formatters.parseDouble("-."), 0.0)
        assertEquals(0.0, Formatters.parseDouble("-,", AppLanguage.RU), 0.0)
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
