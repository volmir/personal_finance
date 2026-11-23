package com.example

import com.example.data.model.FireStatus
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.domain.model.AuditIndicatorStatus
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FireSimulationComparisonFormatterTest {
    private val base = FinanceCalculator.calculate(FinancialInputData.usAverageData(), today = LocalDate.of(2026, 11, 6)).copy(
        fireStatus = FireStatus.REACHABLE, yearsToRetirement = 24.0, retirementAge = 62.0
    )

    @Test
    fun `difference sign and status follow time to FIRE including rounded zero boundaries`() {
        val cases = listOf(
            Triple(21.5, "↓ −2,5 года", AuditIndicatorStatus.POSITIVE),
            Triple(26.5, "↑ +2,5 года", AuditIndicatorStatus.NEGATIVE),
            Triple(24.0, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(23.996, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(24.004, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(23.994, "↓ −0,01 года", AuditIndicatorStatus.POSITIVE),
            Triple(24.006, "↑ +0,01 года", AuditIndicatorStatus.NEGATIVE)
        )
        for ((years, delta, status) in cases) {
            val display = Formatters.formatFireSimulationComparison(base, base.copy(yearsToRetirement = years), 1, AppLanguage.RU)
            assertEquals("$years", delta, display.delta)
            assertEquals("$years", status, display.status)
        }
    }

    @Test
    fun `all locales format compact comparison and plural units from displayed numbers`() {
        val expected = mapOf(
            AppLanguage.RU to ("24 → 21,5 года" to "↓ −2,5 года"),
            AppLanguage.EN to ("24 → 21.5 years" to "↓ −2.5 years"),
            AppLanguage.DE to ("24 → 21,5 Jahre" to "↓ −2,5 Jahre"),
            AppLanguage.FR to ("24 → 21,5 ans" to "↓ −2,5 ans"),
            AppLanguage.IT to ("24 → 21,5 anni" to "↓ −2,5 anni"),
            AppLanguage.ES to ("24 → 21,5 años" to "↓ −2,5 años")
        )
        for (lang in AppLanguage.values()) {
            val display = Formatters.formatFireSimulationComparison(base, base.copy(yearsToRetirement = 21.5, retirementAge = 59.5), 1, lang)
            assertEquals(expected.getValue(lang).first, display.years)
            assertEquals(expected.getValue(lang).second, display.delta)
            assertEquals(if (lang == AppLanguage.EN) "62 → 59.5" else "62 → 59,5", display.age)
            val zero = Formatters.formatFireSimulationComparison(base, base, 1, lang)
            assertEquals("0 ${getAppStrings(lang).fireYearsSuffix(0.0)}", zero.delta)
            assertEquals(AuditIndicatorStatus.NEUTRAL, zero.status)
        }
        val oneYear = Formatters.formatFireSimulationComparison(base, base.copy(yearsToRetirement = 23.0), 1, AppLanguage.RU)
        assertEquals("↓ −1 год", oneYear.delta)
    }

    @Test
    fun `zero changes ignores simulated metrics and omits comparison arrows in all locales`() {
        for (lang in AppLanguage.values()) {
            val display = Formatters.formatFireSimulationComparison(base,
                base.copy(yearsToRetirement = 1.0, retirementAge = 39.0), 0, lang)
            assertFalse(display.years.contains("→"))
            assertEquals("62", display.age)
            assertEquals(getAppStrings(lang).fireSimNoChanges, display.delta)
            assertEquals(AuditIndicatorStatus.NEUTRAL, display.status)
        }
    }

    @Test
    fun `every nonnumeric status pair suppresses unavailable differences in all locales`() {
        for (lang in AppLanguage.values()) for (baseStatus in FireStatus.values()) for (simStatus in FireStatus.values()) {
            if (baseStatus == FireStatus.REACHABLE && simStatus == FireStatus.REACHABLE) continue
            val display = Formatters.formatFireSimulationComparison(base.copy(fireStatus = baseStatus),
                base.copy(fireStatus = simStatus), 2, lang)
            assertEquals("$lang $baseStatus $simStatus", getAppStrings(lang).metricUnavailableValue, display.delta)
            assertEquals(AuditIndicatorStatus.NEUTRAL, display.status)
            val statusText = when (simStatus) {
                FireStatus.REACHED_NOW -> getAppStrings(lang).fireReachedSimulationText
                FireStatus.UNREACHABLE -> getAppStrings(lang).fireUnreachableSimulationText
                FireStatus.HORIZON_EXHAUSTED -> getAppStrings(lang).fireHorizonExhaustedText
                FireStatus.INVALID_AGE -> getAppStrings(lang).invalidBirthDateError
                FireStatus.REACHABLE -> null
            }
            if (statusText != null) assertTrue(display.years.contains(statusText))
            assertFalse(display.toString().contains("NaN"))
            assertFalse(display.toString().contains("Infinity"))
        }
    }

    @Test
    fun `nonfinite years and ages never leak into the panel or produce a difference`() {
        for (lang in AppLanguage.values()) for (value in listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
            for ((original, simulated) in listOf(
                base to base.copy(yearsToRetirement = value, retirementAge = value),
                base.copy(yearsToRetirement = value, retirementAge = value) to base
            )) {
                val display = Formatters.formatFireSimulationComparison(original, simulated, 1, lang)
                assertEquals(getAppStrings(lang).metricUnavailableValue, display.delta)
                assertEquals(AuditIndicatorStatus.NEUTRAL, display.status)
                assertFalse(display.toString().contains("NaN"))
                assertFalse(display.toString().contains("Infinity"))
            }
        }
    }
}
