package com.example

import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.model.AuditIndicatorStatus
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuditorAnalysisTest {

    @Test
    fun `net worth audit matches shared screen status and preserves text for all asset shares`() {
        val defaults = FinancialInputData.usAverageData()
        val base = defaults.copy(liquidAssets = 100.0, financialAssets = 0.0, propertyAssets = 0.0)
        val cases = listOf(
            base.copy(liabilities = 40.0) to AuditIndicatorStatus.POSITIVE,
            base.copy(liabilities = 50.0) to AuditIndicatorStatus.POSITIVE,
            defaults to AuditIndicatorStatus.NEUTRAL,
            base.copy(liabilities = 100.0) to AuditIndicatorStatus.NEUTRAL,
            base.copy(liabilities = 101.0) to AuditIndicatorStatus.NEGATIVE
        )

        for ((input, expectedStatus) in cases) {
            val metrics = FinanceCalculator.calculate(input, today = java.time.LocalDate.of(2026, 11, 1))
            val screenStatus = FinancialMetricEvaluator.getNetWorthStatus(metrics.netWorth, metrics.totalAssets)
            assertEquals(expectedStatus, screenStatus)
            if (input == defaults) {
                assertEquals(335_000.0, metrics.totalAssets, 0.0)
                assertEquals(155_000.0, metrics.netWorth, 0.0)
            }

            for (lang in AppLanguage.values()) {
                val strings = com.example.util.localization.getAppStrings(lang)
                val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
                val item = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "net_worth" }
                assertEquals("$input $lang", screenStatus, item.status)
                val template = when (expectedStatus) {
                    AuditIndicatorStatus.POSITIVE -> strings.auditNetWorthStrong
                    AuditIndicatorStatus.NEUTRAL -> strings.auditNetWorthBalanced
                    AuditIndicatorStatus.NEGATIVE -> strings.auditNetWorthNegative
                }
                assertEquals(
                    String.format(java.util.Locale.ROOT, template, Formatters.formatMoney(metrics.netWorth, input.currencySymbol, lang)),
                    item.text
                )
            }
        }
    }

    @Test
    fun `export target capital availability follows FIRE status without changing metrics`() {
        val input = FinancialInputData.usAverageData()
        val baseMetrics = FinanceCalculator.calculate(input, today = java.time.LocalDate.of(2026, 11, 1))
        val targetCapital = 1_234_567.89

        for (lang in AppLanguage.values()) {
            val strings = com.example.util.localization.getAppStrings(lang)
            for (status in FireStatus.values()) {
                val metrics = baseMetrics.copy(fireStatus = status, retirementTargetCapital = targetCapital)
                val expectedValue = when (status) {
                    FireStatus.REACHED_NOW, FireStatus.REACHABLE ->
                        Formatters.formatMoney(targetCapital, input.currencySymbol, lang)
                    else -> strings.metricUnavailableValue
                }

                val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, lang)

                assertTrue(
                    "$lang $status",
                    export.lineSequence().any {
                        it == String.format(java.util.Locale.ROOT, strings.exportFireTargetCapitalLine, expectedValue)
                    }
                )
                assertEquals(targetCapital, metrics.retirementTargetCapital, 0.0)
            }
        }
    }

    @Test
    fun testSavingsRateAuditBoundaries() {
        fun savingsRateStatus(rate: Double): AuditIndicatorStatus {
            val input = FinancialInputData(
                monthlyIncome = 100_000.0,
                monthlyExpenses = 100_000.0 * (1.0 - rate / 100.0)
            )
            val metrics = FinanceCalculator.calculate(input)
            val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
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

        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)

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
        val stressedOpinion = AuditorAnalysisGenerator.generate(stressedInput, stressedMetrics, AppLanguage.RU)

        assertEquals(9, stressedOpinion.totalCount)
        assertTrue(stressedOpinion.negative.any { it.id == "cash_flow" })
        assertTrue(stressedOpinion.negative.any { it.id == "savings_rate" })
        assertTrue(stressedOpinion.negative.any { it.id == "debt_ratio" })
    }

    @Test
    fun testExportSummaryContents() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)

        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.RU)

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
        assertTrue(export.contains("2,50%"))
        assertTrue(export.indexOf("Допущения модели") < export.indexOf("⚠️ Важно"))
        assertFalse(export.contains("лет лет"))
    }

    @Test
    fun `unavailable Fidelity is omitted from audit and export`() {
        val input = FinancialInputData(birthDate = "invalid-date", monthlyIncome = 100_000.0)
        val metrics = FinanceCalculator.calculate(input)

        assertFalse(metrics.isFidelityAvailable)
        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        assertFalse((opinion.positive + opinion.neutral + opinion.negative).any { it.id == "fidelity" })

        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.RU)
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
        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        val fidelity = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "fidelity" }

        assertTrue(metrics.fidelityCapital < 0.0)
        assertEquals(AuditIndicatorStatus.NEGATIVE, fidelity.status)
        assertTrue(fidelity.text.contains("Ликвидный и финансовый капитал за вычетом долгов отрицателен"))
    }

    @Test
    fun testAnalyticsShareIncludesModelAssumptionsBeforeLegalDisclaimer() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input)
        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        val export = opinion.toFormattedShareText(input, metrics, AppLanguage.RU)

        assertTrue(export.contains("Допущения модели"))
        assertTrue(export.contains("ориентир, а не прогноз и не гарантия"))
        assertTrue(export.indexOf("Допущения модели") < export.indexOf("⚠️ Важно"))
    }

    @Test
    fun testExportSummaryUsesUnreachableFireText() {
        val input = FinancialInputData.russiaAverageData()
        val metrics = FinanceCalculator.calculate(input).copy(fireStatus = FireStatus.UNREACHABLE)
        val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.RU)

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

        assertEquals("✅ " + FinancialMetricEvaluator.getCfsStageDescription(14.0, AppLanguage.RU), cfsOpinion(14.0))
        assertEquals("✅ Вы близки: капитал покрывает 50% от целевого.", cfsOpinion(15.0))
        assertEquals("✅ Вы близки: капитал покрывает 80% от целевого.", cfsOpinion(24.0))
        assertEquals("✅ Вы близки: капитал покрывает 100% от целевого.", cfsOpinion(29.99))
        assertEquals("🎉 Вы достигли финансовой независимости: КФС не менее 30.", cfsOpinion(30.0))

        val independentEn = cfsOpinion(30.0, AppLanguage.EN)
        assertEquals("🎉 You have achieved financial independence: CFS is at least 30.", independentEn)
        assertFalse(independentEn.contains("4%"))
    }

    @Test
    fun `CFS audit below fifteen uses localized stage and preserves status`() {
        val cases = listOf(
            -1.0 to AuditIndicatorStatus.NEGATIVE,
            0.3 to AuditIndicatorStatus.NEGATIVE,
            1.0 to AuditIndicatorStatus.NEGATIVE,
            3.0 to AuditIndicatorStatus.NEUTRAL,
            8.0 to AuditIndicatorStatus.POSITIVE
        )

        for ((cfs, expectedStatus) in cases) {
            val input = FinancialInputData(
                monthlyIncome = 1_000.0,
                monthlyExpenses = 1_000.0,
                liquidAssets = maxOf(cfs * 12_000.0, 0.0),
                financialAssets = 0.0,
                propertyAssets = 0.0,
                liabilities = maxOf(-cfs * 12_000.0, 0.0)
            )
            val metrics = FinanceCalculator.calculate(input, today = java.time.LocalDate.of(2026, 11, 1))
            assertEquals(cfs, metrics.financialFreedomRatio, 0.000001)

            for (lang in AppLanguage.values()) {
                val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
                val item = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "cfs" }
                val expectedIcon = when (expectedStatus) {
                    AuditIndicatorStatus.POSITIVE -> if (cfs >= 30.0) "🎉" else "✅"
                    AuditIndicatorStatus.NEUTRAL -> "ℹ️"
                    AuditIndicatorStatus.NEGATIVE -> if (cfs < 0.0) "🚨" else "⚠️"
                }
                assertEquals("$cfs $lang", expectedStatus, item.status)
                assertEquals(FinancialMetricEvaluator.getCfsStatus(cfs), item.status)
                assertEquals("$cfs $lang", "$expectedIcon ${FinancialMetricEvaluator.getCfsStageDescription(cfs, lang)}", item.text)
            }
        }
    }

    @Test
    fun testAnalyticsShareExportsCfsWithIcon() {
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input)
        for (lang in AppLanguage.values()) {
            val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
            val shareText = opinion.toFormattedShareText(input, metrics, lang)
            val cfsItem = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "cfs" }
            assertTrue("Share text must contain CFS item with icon", shareText.contains(cfsItem.text))
            assertTrue(
                "CFS item in export must start with an icon",
                cfsItem.text.startsWith("✅") || cfsItem.text.startsWith("ℹ️") ||
                        cfsItem.text.startsWith("⚠️") || cfsItem.text.startsWith("🚨") ||
                        cfsItem.text.startsWith("🎉")
            )
        }
    }

    @Test
    fun `export and share texts have model assumptions before disclaimer and no duplicate year units`() {
        val input = FinancialInputData(
            birthDate = "1990-01-01",
            monthlyIncome = 150_000.0,
            monthlyExpenses = 70_000.0,
            liquidAssets = 200_000.0,
            financialAssets = 500_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0
        )
        val metrics = FinanceCalculator.calculate(input)

        for (lang in AppLanguage.values()) {
            val strings = com.example.util.localization.getAppStrings(lang)
            val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, lang)
            val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
            val share = opinion.toFormattedShareText(input, metrics, lang)

            // Block order: assumptions before disclaimer
            val exportAssumptionsIdx = export.indexOf(strings.fireAssumptionsTitle)
            val exportDisclaimerIdx = export.indexOf(strings.auditShareDisclaimer)
            assertTrue(exportAssumptionsIdx in 0 until exportDisclaimerIdx)

            val shareAssumptionsIdx = share.indexOf(strings.fireAssumptionsTitle)
            val shareDisclaimerIdx = share.indexOf(strings.auditShareDisclaimer)
            assertTrue(shareAssumptionsIdx in 0 until shareDisclaimerIdx)

            // No duplicate year words
            assertFalse(export.contains("лет лет"))
            assertFalse(export.contains("yrs yrs"))
            assertFalse(export.contains("J. J."))
            assertFalse(export.contains("ans ans"))
            assertFalse(export.contains("anni anni"))
            assertFalse(export.contains("años años"))

            val scores = metrics.scores
            val scoreReport = String.format(
                java.util.Locale.ROOT,
                strings.shareScoreReportTemplate,
                Formatters.formatScore(scores.totalScore, lang = lang),
                scores.savingsRateScore,
                Formatters.formatPercent(metrics.savingsRate, lang),
                scores.safetyMonthsScore,
                Formatters.formatRatio(metrics.safetyMonths, lang),
                scores.debtRatioScore,
                Formatters.formatRatio(metrics.debtRatio, lang),
                scores.cfsScore,
                Formatters.formatRatio(metrics.financialFreedomRatio, lang),
                Formatters.formatFireYearsToRetirement(metrics.yearsToRetirement, metrics.fireStatus, lang),
                Formatters.formatFireRetirementAge(metrics.retirementAge, metrics.fireStatus, lang)
            )
            assertFalse(scoreReport.contains("лет лет"))
            assertFalse(scoreReport.contains("yrs yrs"))
            assertFalse(scoreReport.contains("Jahren Jahren"))
            assertFalse(scoreReport.contains("ans ans"))
            assertFalse(scoreReport.contains("anni anni"))
            assertFalse(scoreReport.contains("años años"))
        }
    }

    @Test
    fun testFidelityAuditAndExportIncludesEligibleCapitalOfTarget() {
        val input = FinancialInputData(
            birthDate = "1988-06-15",
            monthlyIncome = 6_000.0,
            monthlyExpenses = 4_800.0,
            liquidAssets = 15_000.0,
            financialAssets = 40_000.0,
            propertyAssets = 280_000.0,
            liabilities = 180_000.0,
            currencySymbol = "$"
        )
        val metrics = FinanceCalculator.calculate(input, today = java.time.LocalDate.of(2026, 11, 1))

        // Russian
        val ruOpinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        val ruFidelity = (ruOpinion.positive + ruOpinion.neutral + ruOpinion.negative).first { it.id == "fidelity" }
        val ruCapital = Formatters.formatMoney(metrics.fidelityCapital, "$", AppLanguage.RU)
        val ruTarget = Formatters.formatMoney(metrics.fidelityTarget, "$", AppLanguage.RU)
        val ruExpected = "($ruCapital из $ruTarget)"
        assertTrue(ruFidelity.text.contains(ruExpected))

        val ruExport = AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.RU)
        assertTrue(ruExport.contains(ruExpected))

        // English
        val enOpinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.EN)
        val enFidelity = (enOpinion.positive + enOpinion.neutral + enOpinion.negative).first { it.id == "fidelity" }
        val enCapital = Formatters.formatMoney(metrics.fidelityCapital, "$", AppLanguage.EN)
        val enTarget = Formatters.formatMoney(metrics.fidelityTarget, "$", AppLanguage.EN)
        val enExpected = "($enCapital of $enTarget)"
        assertTrue(enFidelity.text.contains(enExpected))

        val enExport = AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.EN)
        assertTrue(enExport.contains(enExpected))
    }

    @Test
    fun testCashFlowZeroPlacedInNegativeGroupWithWarningIcon() {
        val input = FinancialInputData(
            monthlyIncome = 50_000.0,
            monthlyExpenses = 50_000.0
        )
        val metrics = FinanceCalculator.calculate(input)
        assertEquals(0.0, metrics.cashFlow, 0.0)

        for (lang in AppLanguage.values()) {
            val opinion = AuditorAnalysisGenerator.generate(input, metrics, lang)
            val negativeCashFlow = opinion.negative.firstOrNull { it.id == "cash_flow" }
            org.junit.Assert.assertNotNull("cash_flow must be present in negative group for $lang", negativeCashFlow)
            assertTrue("cash_flow must not be present in neutral group for $lang", opinion.neutral.none { it.id == "cash_flow" })
            assertEquals(AuditIndicatorStatus.NEGATIVE, negativeCashFlow!!.status)
            assertTrue("Text must start with ⚠️ for $lang: ${negativeCashFlow.text}", negativeCashFlow.text.startsWith("⚠️"))
        }

        val ruOpinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        val ruCashFlow = ruOpinion.negative.first { it.id == "cash_flow" }
        assertTrue(ruCashFlow.text.contains("доходы равны расходам", ignoreCase = true))
        assertTrue(ruCashFlow.text.contains("свободного ресурса для накоплений нет, поэтому показатель негативный", ignoreCase = true))
    }
}
