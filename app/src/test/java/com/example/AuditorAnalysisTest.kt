package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.model.AuditIndicatorStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuditorAnalysisTest {

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
        assertTrue(export.contains("Дисклеймер"))
        assertTrue(export.contains("Расчёты основаны на постоянной доходности"))
        assertTrue(export.contains("2,50%"))
    }

    @Test
    fun testCfsFireReadinessMessagesUseFourPercentTarget() {
        fun cfsOpinion(cfs: Double): String {
            val input = FinancialInputData(
                monthlyIncome = 1_000.0,
                monthlyExpenses = 1_000.0,
                liquidAssets = cfs * 12_000.0,
                financialAssets = 0.0,
                propertyAssets = 0.0,
                liabilities = 0.0
            )
            val metrics = FinanceCalculator.calculate(input)
            val opinion = AuditorAnalysisGenerator.generate(input, metrics)
            return (opinion.positive + opinion.neutral + opinion.negative)
                .first { it.id == "cfs" }.text
        }

        assertEquals("Вы на пути к независимости, но пока далеко от цели.", cfsOpinion(14.0))
        assertEquals("Вы близки: капитал покрывает 60% от целевого.", cfsOpinion(15.0))
        assertEquals("Вы близки: капитал покрывает 96% от целевого.", cfsOpinion(24.0))
        assertEquals("Вы финансово независимы по правилу 4%.", cfsOpinion(25.0))
    }
}
