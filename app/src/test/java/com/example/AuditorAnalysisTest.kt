package com.example

import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.model.AuditIndicatorStatus
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuditorAnalysisTest {

    @Test
    fun testSavingsRateAuditBoundaries() {
        fun savingsRateStatus(rate: Double): AuditIndicatorStatus {
            val input = FinancialInputData(
                monthlyIncome = 100_000.0,
                monthlyExpenses = 100_000.0 * (1.0 - rate / 100.0)
            )
            val metrics = FinanceCalculator.calculate(input)
            val opinion = AuditorAnalysisGenerator.generate(input, metrics)
            return (opinion.positive + opinion.neutral + opinion.negative)
                .first { it.id == "savings_rate" }.status
        }

        assertEquals(AuditIndicatorStatus.NEGATIVE, savingsRateStatus(0.0))
        assertEquals(AuditIndicatorStatus.NEUTRAL, savingsRateStatus(0.01))
        assertEquals(AuditIndicatorStatus.NEUTRAL, savingsRateStatus(20.0))
        assertEquals(AuditIndicatorStatus.POSITIVE, savingsRateStatus(20.01))
        assertEquals(AuditIndicatorStatus.NEGATIVE, savingsRateStatus(-5.0))
    }

    @Test
    fun testAuditorAnalysisAllMetricsGrouping() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        val opinion = AuditorAnalysisGenerator.generate(input, metrics)

        // Должны быть проанализированы все 9 показателей из экрана "Показатели"
        assertEquals(9, opinion.totalCount)
        assertEquals(9, opinion.positive.size + opinion.neutral.size + opinion.negative.size)

        // Проверяем наличие всех 9 метрик по id
        val allIds = (opinion.positive + opinion.neutral + opinion.negative).map { it.id }.toSet()
        val expectedIds = setOf(
            "cash_flow",
            "savings_rate",
            "emergency_fund",
            "safety_months",
            "debt_ratio",
            "net_worth",
            "fidelity",
            "cfs",
            "real_return"
        )
        assertEquals(expectedIds, allIds)

        // Проверяем динамическое реагирование при смене вводных данных:
        // Если расходы резко превышают доходы, денежный поток и норма сбережений становятся негативными
        val stressedInput = input.copy(
            monthlyIncome = 50_000.0,
            monthlyExpenses = 100_000.0,
            liabilities = 10_000_000.0 // огромный долг
        )
        val stressedMetrics = FinanceCalculator.calculate(stressedInput)
        val stressedOpinion = AuditorAnalysisGenerator.generate(stressedInput, stressedMetrics)

        assertEquals(9, stressedOpinion.totalCount)
        assertTrue(stressedOpinion.negative.any { it.id == "cash_flow" })
        assertTrue(stressedOpinion.negative.any { it.id == "savings_rate" })
        assertTrue(stressedOpinion.negative.any { it.id == "debt_ratio" })
    }

    @Test
    fun testExportSummaryContents() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics)

        // 4 обязательных блока
        assertTrue(export.contains("1. Исходные данные:"))
        assertTrue(export.contains("2. Рассчитанные показатели:"))
        assertTrue(export.contains("3. Набранные баллы:"))
        assertTrue(export.contains("4. Финансовая свобода, FIRE:"))

        // Аудиторское заключение и симуляцию в экспорт НЕ включаем
        assertFalse(export.contains("Аудиторское заключение"))
        assertFalse(export.contains("Симуляция"))
        assertFalse(export.contains("Стресс-тест"))
        assertFalse(export.contains("чекбокс"))
        assertTrue(export.contains("Допущения модели"))
        assertTrue(export.contains("Расчёты основаны на постоянной доходности"))
        assertFalse(export.contains("\n\nМодель не учитывает"))
        assertTrue(export.contains("2,50%"))
        assertTrue(export.indexOf("⚠️ Важно") < export.indexOf("Допущения модели"))
    }

    @Test
    fun `unavailable Fidelity is omitted from audit and export`() {
        val input = FinancialInputData(birthDate = "invalid-date", monthlyIncome = 100_000.0)
        val metrics = FinanceCalculator.calculate(input)

        assertFalse(metrics.isFidelityAvailable)
        val opinion = AuditorAnalysisGenerator.generate(input, metrics)
        assertFalse((opinion.positive + opinion.neutral + opinion.negative).any { it.id == "fidelity" })

        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics)
        assertFalse(export.contains("100%"))
        assertTrue(export.contains(com.example.util.localization.getAppStrings(com.example.util.localization.AppLanguage.RU).metricUnavailableValue))
    }

    @Test
    fun `negative Fidelity capital uses insolvent audit message`() {
        val input = FinancialInputData(
            birthDate = "1988-10-31",
            monthlyIncome = 6_000.0,
            monthlyExpenses = 4_800.0,
            liquidAssets = 15_000.0,
            financialAssets = 40_000.0,
            propertyAssets = 280_000.0,
            liabilities = 400_000.0
        )
        val metrics = FinanceCalculator.calculate(input, today = java.time.LocalDate.of(2026, 10, 31))
        val opinion = AuditorAnalysisGenerator.generate(input, metrics)
        val fidelity = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "fidelity" }

        assertTrue(metrics.fidelityCapital < 0.0)
        assertEquals(AuditIndicatorStatus.NEGATIVE, fidelity.status)
        assertTrue(fidelity.text.contains("Ликвидный и финансовый капитал за вычетом долгов отрицателен"))
    }

    @Test
    fun testAnalyticsShareIncludesModelAssumptionsAfterLegalDisclaimer() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)
        val opinion = AuditorAnalysisGenerator.generate(input, metrics)
        val export = opinion.toFormattedShareText(input, metrics)

        assertTrue(export.contains("Допущения модели"))
        assertTrue(export.contains("ориентир, а не прогноз и не гарантия"))
        assertTrue(export.indexOf("⚠️ Важно") < export.indexOf("Допущения модели"))
    }

    @Test
    fun testExportSummaryUsesUnreachableFireText() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input).copy(fireStatus = FireStatus.UNREACHABLE)
        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics)

        assertTrue(export.contains("Недостижимо при текущих параметрах"))
    }

    @Test
    fun testCfsFireReadinessMessagesUseIndependenceThreshold() {
        fun cfsOpinion(cfs: Double, lang: AppLanguage = AppLanguage.RU): String {
            val input = FinancialInputData(
                monthlyIncome = 1_000.0,
                monthlyExpenses = 1_000.0,
                liquidAssets = cfs * 12_000.0,
                financialAssets = 0.0,
                propertyAssets = 0.0,
                liabilities = 0.0
            )
            val metrics = FinanceCalculator.calculate(input)
            val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
            return (opinion.positive + opinion.neutral + opinion.negative)
                .first { it.id == "cfs" }.text
        }

        assertEquals("Вы на пути к независимости, но пока далеко от цели.", cfsOpinion(14.0))
        assertEquals("Вы близки: капитал покрывает 50% от целевого.", cfsOpinion(15.0))
        assertEquals("Вы близки: капитал покрывает 80% от целевого.", cfsOpinion(24.0))
        assertEquals("Вы близки: капитал покрывает 100% от целевого.", cfsOpinion(29.99))
        assertEquals("Вы достигли финансовой независимости: КФС не менее 30.", cfsOpinion(30.0))

        val independentEn = cfsOpinion(30.0, AppLanguage.EN)
        assertEquals("You have achieved financial independence: CFS is at least 30.", independentEn)
        assertFalse(independentEn.contains("4%"))
    }
}
