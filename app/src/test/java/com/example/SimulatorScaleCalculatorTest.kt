package com.example

import com.example.domain.SimulatorScaleCalculator
import com.example.domain.calculateSimulationOngoingExpenses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatorScaleCalculatorTest {
    @Test
    fun `expense reduction scale caps at ninety percent and keeps aligned steps`() {
        for (expenses in listOf(1.0, 13.37, 1_000.0, 3_500.0, 4_800.0, 60_000.0)) {
            val config = SimulatorScaleCalculator.calculateExpensesConfig(expenses, 100_000.0)
            assertTrue(config.minValue < 0f)
            assertTrue(config.maxValue > 0f)
            assertTrue(config.maxValue.toDouble() <= expenses * 0.9 + 0.000001)
            val intervals = (config.maxValue - config.minValue) / config.stepSize
            assertEquals(config.steps + 1.0, intervals, 0.00001)
            assertTrue(expenses * 0.9 - config.maxValue < config.stepSize + 0.000001)
        }
        assertEquals(900f, SimulatorScaleCalculator.calculateExpensesConfig(1_000.0, 2_000.0).maxValue, 0f)
    }

    @Test
    fun `scenario expense scales contain no steps without effect`() {
        val monthlyExpenses = 60_000.0
        for (optimization in listOf(false, true)) {
            for (relocation in listOf(false, true)) {
                for (inflation in listOf(false, true)) {
                    val before = calculateSimulationOngoingExpenses(
                        monthlyExpenses, 0.0, optimization, relocation, inflation
                    ).beforeReduction
                    val config = SimulatorScaleCalculator.calculateExpensesConfig(monthlyExpenses, 100_000.0, before)
                    assertTrue(config.maxValue <= monthlyExpenses * 0.9)
                    var previous = Double.POSITIVE_INFINITY
                    for (index in 0..config.steps + 1) {
                        val reduction = config.minValue + index * config.stepSize
                        val effective = calculateSimulationOngoingExpenses(
                            monthlyExpenses, reduction, optimization, relocation, inflation
                        ).effective
                        assertTrue(effective < previous)
                        assertTrue(effective >= monthlyExpenses * 0.1)
                        previous = effective
                    }
                }
            }
        }
    }

    @Test
    fun `zero expenses have no reduction range and retain growth range`() {
        for (income in listOf(0.0, 6_000.0)) {
            val config = SimulatorScaleCalculator.calculateExpensesConfig(0.0, income)
            assertEquals(0f, config.maxValue, 0f)
            assertTrue(config.minValue < 0f)
            assertTrue(config.stepSize > 0.0)
        }
    }
}
