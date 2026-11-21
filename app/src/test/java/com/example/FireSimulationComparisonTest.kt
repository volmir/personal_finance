package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.domain.FireSimulationComparisonCalculator
import com.example.data.model.FireStatus
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FireSimulationComparisonTest {
    private val today = LocalDate.of(2026, 10, 31)

    @Test
    fun unreachableDetection() {
        assertTrue(FireSimulationComparisonCalculator.isUnreachable(FireStatus.UNREACHABLE))
        assertTrue(FireSimulationComparisonCalculator.isUnreachable(FireStatus.HORIZON_EXHAUSTED))
        assertFalse(FireSimulationComparisonCalculator.isUnreachable(FireStatus.REACHABLE))
        assertFalse(FireSimulationComparisonCalculator.isUnreachable(FireStatus.REACHED_NOW))
    }

    @Test
    fun comparisonPositiveWhenSimFaster() {
        val input = FinancialInputData(
            monthlyIncome = 6_000.0, monthlyExpenses = 4_800.0, liquidAssets = 15_000.0, financialAssets = 40_000.0,
            propertyAssets = 280_000.0, liabilities = 180_000.0, birthDate = "1988-10-31", lifeExpectancy = 82,
            nominalReturnPercent = 7.5, inflationPercent = 2.5, capitalGainsTaxPercent = 20.0, currencySymbol = "$"
        )
        val base = FinanceCalculator.calculate(input, today = today)
        // simulated with better savings (lower expenses) -> should be faster or equal
        val simInput = input.copy(monthlyExpenses = 4_000.0)
        val sim = FinanceCalculator.calculate(simInput, today = today)
        val cmp = FireSimulationComparisonCalculator.compare(base, sim)
        assertEquals(base.yearsToRetirement - sim.yearsToRetirement, cmp.yearsDiff, 0.0001)
        // sim unreachable? both reachable expected
        assertFalse(cmp.isSimUnreachable)
        assertFalse(cmp.isBaseUnreachable)
    }

    @Test
    fun roundRealReturnForStatus() {
        assertEquals(7.5, FireSimulationComparisonCalculator.roundRealReturnForStatus(7.5), 0.0001)
        assertEquals(0.0, FireSimulationComparisonCalculator.roundRealReturnForStatus(Double.NaN), 0.0001)
    }
}
