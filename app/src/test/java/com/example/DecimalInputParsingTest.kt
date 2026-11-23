package com.example

import com.example.util.Formatters
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
}
