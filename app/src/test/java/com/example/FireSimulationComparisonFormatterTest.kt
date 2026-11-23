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
            Triple(27.0, "↑ +3 года", AuditIndicatorStatus.NEGATIVE),
            Triple(24.0, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(23.96, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(23.95, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(24.04, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(24.05, "0 лет", AuditIndicatorStatus.NEUTRAL),
            Triple(23.94, "↓ −0,1 года", AuditIndicatorStatus.POSITIVE),
            Triple(24.06, "↑ +0,1 года", AuditIndicatorStatus.NEGATIVE)
        )
        for ((years, delta, status) in cases) {
            val display = Formatters.formatFireSimulationComparison(base, base.copy(yearsToRetirement = years), 1, AppLanguage.RU)
            assertEquals("$years", delta, display.delta)
            assertEquals("$years", status, display.status)
        }
    }

    @Test
    fun `displayed delta always agrees with rounded comparison columns and avoids negative zero`() {
        // Одинаковые после округления до 0,1 сроки должны давать 0 лет и NEUTRAL даже при сырой разнице > 0,05
        val sameDisplayed = Formatters.formatFireSimulationComparison(
            base.copy(yearsToRetirement = 24.04, retirementAge = 62.04),
            base.copy(yearsToRetirement = 23.96, retirementAge = 61.96),
            1,
            AppLanguage.RU
        )
        assertEquals("24 → 24 года", sameDisplayed.years)
        assertEquals("62 → 62", sameDisplayed.age)
        assertEquals("0 лет", sameDisplayed.delta)
        assertFalse(sameDisplayed.delta.contains("−0"))
        assertEquals(AuditIndicatorStatus.NEUTRAL, sameDisplayed.status)

        // Округлённые 24 и 21,5 -> разница должна быть -2,5 (а не -2,6 от сырого 21.46 - 24.04 = -2.58)
        val earlierDisplayed = Formatters.formatFireSimulationComparison(
            base.copy(yearsToRetirement = 24.04, retirementAge = 62.04),
            base.copy(yearsToRetirement = 21.46, retirementAge = 59.46),
            1,
            AppLanguage.RU
        )
        assertEquals("24 → 21,5 года", earlierDisplayed.years)
        assertEquals("62 → 59,5", earlierDisplayed.age)
        assertEquals("↓ −2,5 года", earlierDisplayed.delta)
        assertEquals(AuditIndicatorStatus.POSITIVE, earlierDisplayed.status)

        // Округлённые 23,9 и 24 -> разница +0,1 (согласована с колонкой срока)
        val laterDisplayed = Formatters.formatFireSimulationComparison(
            base.copy(yearsToRetirement = 23.94, retirementAge = 61.94),
            base.copy(yearsToRetirement = 23.96, retirementAge = 61.96),
            1,
            AppLanguage.RU
        )
        assertEquals("23,9 → 24 года", laterDisplayed.years)
        assertEquals("61,9 → 62", laterDisplayed.age)
        assertEquals("↑ +0,1 года", laterDisplayed.delta)
        assertEquals(AuditIndicatorStatus.NEGATIVE, laterDisplayed.status)

        // Малый положительный срок (< 0,1) отображается как локализованное сообщение, а не «0 лет»
        val fromSubTenth = Formatters.formatFireSimulationComparison(
            base.copy(yearsToRetirement = 0.04, retirementAge = 38.04),
            base.copy(yearsToRetirement = 2.0, retirementAge = 40.0),
            1,
            AppLanguage.RU
        )
        assertEquals("Менее 0,1 года → 2 года", fromSubTenth.years)
        assertEquals("↑ +2 года", fromSubTenth.delta)
        assertEquals(AuditIndicatorStatus.NEGATIVE, fromSubTenth.status)

        // Сценарий сократил срок до 0.05 (HALF_EVEN -> 0.0), но FIRE ещё не достигнут (REACHABLE)
        val toSubTenth = Formatters.formatFireSimulationComparison(
            base.copy(yearsToRetirement = 2.0, retirementAge = 40.0),
            base.copy(yearsToRetirement = 0.05, retirementAge = 38.05),
            1,
            AppLanguage.RU
        )
        assertEquals("2 → Менее 0,1 года", toSubTenth.years)
        assertEquals("↓ −2 года", toSubTenth.delta)
        assertEquals(AuditIndicatorStatus.POSITIVE, toSubTenth.status)

        // Оба срока < 0.05 (незначимое изменение): дельта нейтральна, нет похвалы/критики и ложного REACHED_NOW
        val bothSubTenth = Formatters.formatFireSimulationComparison(
            base.copy(yearsToRetirement = 0.049, retirementAge = 38.049),
            base.copy(yearsToRetirement = 0.01, retirementAge = 38.01),
            1,
            AppLanguage.RU
        )
        assertEquals("Менее 0,1 года → Менее 0,1 года", bothSubTenth.years)
        assertEquals("0 лет", bothSubTenth.delta)
        assertEquals(AuditIndicatorStatus.NEUTRAL, bothSubTenth.status)
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
