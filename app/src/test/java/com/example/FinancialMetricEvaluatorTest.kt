package com.example

import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ScoreColorEvaluator
import com.example.ui.theme.StatusColors
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.util.localization.AppLanguage
import com.example.util.localization.StringsEn
import com.example.util.localization.StringsRu
import com.example.util.localization.getAppStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialMetricEvaluatorTest {

    @Test
    fun testCfsWithNoExpensesUsesTopTierForPositiveNetWorth() {
        assertTrue(FinancialMetricEvaluator.calculateCfs(netWorth = 1.0, annualExpenses = 0.0) >= 30.0)
        assertEquals(0.0, FinancialMetricEvaluator.calculateCfs(netWorth = 0.0, annualExpenses = 0.0), 0.0)
        assertEquals(0.0, FinancialMetricEvaluator.calculateCfs(netWorth = -1.0, annualExpenses = 0.0), 0.0)
    }

    @Test
    fun testCfsCalculatedWithNetAssets() {
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0, // годовые расходы = 600 000
            liquidAssets = 200_000.0,
            financialAssets = 300_000.0,
            propertyAssets = 1_500_000.0, // сумма активов = 2 000 000
            liabilities = 800_000.0 // чистые активы = 1 200 000
        )
        val metrics = FinanceCalculator.calculate(input)

        // Чистые активы = 1 200 000. Годовые расходы = 600 000. КФС = 1 200 000 / 600 000 = 2.0
        assertEquals(1_200_000.0, metrics.netWorth, 0.01)
        assertEquals(2.0, metrics.financialFreedomRatio, 0.01)
        // При КФС = 2.0 попадает в полуинтервал [2, 5): балл 6, статус нейтральный, этап 2..5
        assertEquals(6, metrics.scores.cfsScore)
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getCfsStatus(metrics.financialFreedomRatio))
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getComponentScoreStatus(metrics.scores.cfsScore))
        assertEquals(
            "КФС 2..5 — Вы можете начать заниматься долгосрочными инвестициями",
            FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, AppLanguage.RU)
        )
        assertEquals(
            "CFS 2..5 — You can begin engaging in long-term investing",
            FinanceCalculator.getCfsStageDescription(metrics.financialFreedomRatio, AppLanguage.EN)
        )

        // При отрицательном чистом капитале КФС становится отрицательным
        val insolventInput = input.copy(
            liabilities = 3_000_000.0 // чистые активы = 2 000 000 - 3 000 000 = -1 000 000
        )
        val insolventMetrics = FinanceCalculator.calculate(insolventInput)
        assertTrue(insolventMetrics.netWorth < 0)
        assertTrue(insolventMetrics.financialFreedomRatio < 0)
        assertEquals(0, insolventMetrics.scores.cfsScore)
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getCfsStatus(insolventMetrics.financialFreedomRatio))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getComponentScoreStatus(insolventMetrics.scores.cfsScore))
        assertEquals(
            "КФС < 0 — Вы в долгах",
            FinanceCalculator.getCfsStageDescription(insolventMetrics.financialFreedomRatio, AppLanguage.RU)
        )
        assertEquals(
            "CFS < 0 — You are in debt",
            FinanceCalculator.getCfsStageDescription(insolventMetrics.financialFreedomRatio, AppLanguage.EN)
        )
    }

    @Test
    fun testCfsBoundariesStatusScoreAndDescriptionAlignment() {
        // [нижняя граница включительно, верхняя нет: [0.5, 2), [2, 5), [5, 15), [15, 30), 30+)
        data class CfsTestCase(
            val cfs: Double,
            val expectedScore: Int,
            val expectedStatus: AuditIndicatorStatus,
            val expectedRuSnippet: String,
            val expectedEnSnippet: String
        )

        val cases = listOf(
            CfsTestCase(-1.0, 0, AuditIndicatorStatus.NEGATIVE, "КФС < 0 — Вы в долгах", "CFS < 0 — You are in debt"),
            CfsTestCase(0.0, 2, AuditIndicatorStatus.NEGATIVE, "0..0,5", "0..0.5"),
            CfsTestCase(0.49, 2, AuditIndicatorStatus.NEGATIVE, "0..0,5", "0..0.5"),
            CfsTestCase(0.5, 4, AuditIndicatorStatus.NEGATIVE, "0,5..2", "0.5..2"),
            CfsTestCase(1.0, 4, AuditIndicatorStatus.NEGATIVE, "0,5..2", "0.5..2"),
            CfsTestCase(1.99, 4, AuditIndicatorStatus.NEGATIVE, "0,5..2", "0.5..2"),
            CfsTestCase(2.0, 6, AuditIndicatorStatus.NEUTRAL, "2..5", "2..5"),
            CfsTestCase(3.5, 6, AuditIndicatorStatus.NEUTRAL, "2..5", "2..5"),
            CfsTestCase(4.99, 6, AuditIndicatorStatus.NEUTRAL, "2..5", "2..5"),
            CfsTestCase(5.0, 8, AuditIndicatorStatus.POSITIVE, "5..15", "5..15"),
            CfsTestCase(10.0, 8, AuditIndicatorStatus.POSITIVE, "5..15", "5..15"),
            CfsTestCase(14.99, 8, AuditIndicatorStatus.POSITIVE, "5..15", "5..15"),
            CfsTestCase(15.0, 9, AuditIndicatorStatus.POSITIVE, "КФС >= 15 и КФС < 30", "CFS >= 15 and < 30"),
            CfsTestCase(25.0, 9, AuditIndicatorStatus.POSITIVE, "КФС >= 15 и КФС < 30", "CFS >= 15 and < 30"),
            CfsTestCase(29.99, 9, AuditIndicatorStatus.POSITIVE, "КФС >= 15 и КФС < 30", "CFS >= 15 and < 30"),
            CfsTestCase(30.0, 10, AuditIndicatorStatus.POSITIVE, "КФС >= 30", "CFS >= 30"),
            CfsTestCase(50.0, 10, AuditIndicatorStatus.POSITIVE, "КФС >= 30", "CFS >= 30")
        )

        for (tc in cases) {
            val score = FinancialMetricEvaluator.getCfsScore(tc.cfs)
            val status = FinancialMetricEvaluator.getCfsStatus(tc.cfs)
            val componentStatus = FinancialMetricEvaluator.getComponentScoreStatus(score)
            val ruDesc = FinancialMetricEvaluator.getCfsStageDescription(tc.cfs, AppLanguage.RU)
            val enDesc = FinancialMetricEvaluator.getCfsStageDescription(tc.cfs, AppLanguage.EN)

            assertEquals("Score mismatch for cfs=${tc.cfs}", tc.expectedScore, score)
            assertEquals("Status mismatch for cfs=${tc.cfs}", tc.expectedStatus, status)
            assertEquals("Component status should match indicator status for cfs=${tc.cfs}", tc.expectedStatus, componentStatus)
            assertTrue("RU desc should contain '${tc.expectedRuSnippet}' for cfs=${tc.cfs}", ruDesc.contains(tc.expectedRuSnippet))
            assertTrue("EN desc should contain '${tc.expectedEnSnippet}' for cfs=${tc.cfs}", enDesc.contains(tc.expectedEnSnippet))
        }
    }

    @Test
    fun testSafetyMonthsBoundariesAndStatusAlignment() {
        val cases = listOf(
            0.0 to (0 to AuditIndicatorStatus.NEGATIVE),
            0.4 to (0 to AuditIndicatorStatus.NEGATIVE),
            0.5 to (2 to AuditIndicatorStatus.NEGATIVE),
            0.9 to (2 to AuditIndicatorStatus.NEGATIVE),
            1.0 to (4 to AuditIndicatorStatus.NEGATIVE),
            2.9 to (4 to AuditIndicatorStatus.NEGATIVE),
            3.0 to (7 to AuditIndicatorStatus.NEUTRAL),
            5.9 to (7 to AuditIndicatorStatus.NEUTRAL),
            6.0 to (9 to AuditIndicatorStatus.POSITIVE),
            11.9 to (9 to AuditIndicatorStatus.POSITIVE),
            12.0 to (10 to AuditIndicatorStatus.POSITIVE),
            24.0 to (10 to AuditIndicatorStatus.POSITIVE)
        )

        for ((months, expected) in cases) {
            val (expectedScore, expectedStatus) = expected
            val score = FinancialMetricEvaluator.getSafetyMonthsScore(months)
            val status = FinancialMetricEvaluator.getSafetyMonthsStatus(months)
            val componentStatus = FinancialMetricEvaluator.getComponentScoreStatus(score)

            assertEquals("Score mismatch for months=$months", expectedScore, score)
            assertEquals("Status mismatch for months=$months", expectedStatus, status)
            assertEquals("Component score status mismatch for months=$months", expectedStatus, componentStatus)
        }
    }

    @Test
    fun testDebtRatioBoundariesAndStatusAlignment() {
        val cases = listOf(
            0.00 to (10 to AuditIndicatorStatus.POSITIVE),
            0.05 to (10 to AuditIndicatorStatus.POSITIVE),
            0.10 to (9 to AuditIndicatorStatus.POSITIVE),
            0.15 to (9 to AuditIndicatorStatus.POSITIVE),
            0.20 to (8 to AuditIndicatorStatus.POSITIVE),
            0.30 to (8 to AuditIndicatorStatus.POSITIVE),
            0.35 to (7 to AuditIndicatorStatus.NEUTRAL),
            0.45 to (7 to AuditIndicatorStatus.NEUTRAL),
            0.50 to (6 to AuditIndicatorStatus.NEUTRAL),
            0.60 to (6 to AuditIndicatorStatus.NEUTRAL),
            0.70 to (4 to AuditIndicatorStatus.NEGATIVE),
            0.80 to (4 to AuditIndicatorStatus.NEGATIVE),
            0.90 to (2 to AuditIndicatorStatus.NEGATIVE),
            1.00 to (2 to AuditIndicatorStatus.NEGATIVE),
            1.20 to (0 to AuditIndicatorStatus.NEGATIVE)
        )

        for ((ratio, expected) in cases) {
            val (expectedScore, expectedStatus) = expected
            val score = FinancialMetricEvaluator.getDebtRatioScore(ratio)
            val status = FinancialMetricEvaluator.getDebtRatioStatus(ratio)
            val componentStatus = FinancialMetricEvaluator.getComponentScoreStatus(score)

            assertEquals("Score mismatch for debtRatio=$ratio", expectedScore, score)
            assertEquals("Status mismatch for debtRatio=$ratio", expectedStatus, status)
            assertEquals("Component score status mismatch for debtRatio=$ratio", expectedStatus, componentStatus)
        }

        val noAssetsWithDebt = FinanceCalculator.calculate(
            FinancialInputData(
                liquidAssets = 0.0,
                financialAssets = 0.0,
                propertyAssets = 0.0,
                liabilities = 100_000.0
            )
        )
        assertEquals(Double.POSITIVE_INFINITY, noAssetsWithDebt.debtRatio, 0.0)
        assertEquals(0, noAssetsWithDebt.scores.debtRatioScore)
        assertEquals(
            AuditIndicatorStatus.NEGATIVE,
            FinancialMetricEvaluator.getDebtRatioStatus(noAssetsWithDebt.debtRatio)
        )
    }

    @Test
    fun testSavingsRateThresholdsAndScores() {
        // Нулевое и отрицательное значения: негативный показатель без баллов.
        val negInput = FinancialInputData(monthlyIncome = 50_000.0, monthlyExpenses = 60_000.0)
        val negMetrics = FinanceCalculator.calculate(negInput)
        assertTrue(negMetrics.savingsRate < 0.0)
        assertEquals(0, negMetrics.scores.savingsRateScore)
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getSavingsRateStatus(-5.0))
        assertEquals(0, FinancialMetricEvaluator.getSavingsRateScore(-5.0))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getSavingsRateStatus(0.0))
        assertEquals(0, FinancialMetricEvaluator.getSavingsRateScore(0.0))
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getCashFlowStatus(0.0))
        val negOpinion = AuditorAnalysisGenerator.generate(negInput, negMetrics)
        val negSr = (negOpinion.positive + negOpinion.neutral + negOpinion.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEGATIVE, negSr.status)

        val noIncomeInput = FinancialInputData(monthlyIncome = 0.0, monthlyExpenses = 50_000.0)
        val noIncomeMetrics = FinanceCalculator.calculate(noIncomeInput)
        assertEquals(-100.0, noIncomeMetrics.savingsRate, 0.0)
        assertEquals(0, noIncomeMetrics.scores.savingsRateScore)
        val noIncomeOpinion = AuditorAnalysisGenerator.generate(noIncomeInput, noIncomeMetrics)
        val noIncomeSr = (noIncomeOpinion.positive + noIncomeOpinion.neutral + noIncomeOpinion.negative)
            .first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEGATIVE, noIncomeSr.status)

        // Положительные значения до 20% включительно нейтральны.
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getSavingsRateStatus(0.01))
        assertEquals(5, FinancialMetricEvaluator.getSavingsRateScore(0.01))
        val neutralInput8 = FinancialInputData(monthlyIncome = 100_000.0, monthlyExpenses = 92_000.0)
        val neutralMetrics8 = FinanceCalculator.calculate(neutralInput8)
        assertEquals(8.0, neutralMetrics8.savingsRate, 0.01)
        assertEquals(6, neutralMetrics8.scores.savingsRateScore) // 5..7 диапазон (желтый)
        val opinion8 = AuditorAnalysisGenerator.generate(neutralInput8, neutralMetrics8)
        val sr8 = (opinion8.positive + opinion8.neutral + opinion8.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEUTRAL, sr8.status)

        val neutralInput20 = FinancialInputData(monthlyIncome = 100_000.0, monthlyExpenses = 80_000.0)
        val neutralMetrics20 = FinanceCalculator.calculate(neutralInput20)
        assertEquals(20.0, neutralMetrics20.savingsRate, 0.01)
        assertEquals(7, neutralMetrics20.scores.savingsRateScore)
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getSavingsRateStatus(20.0))
        val opinion20 = AuditorAnalysisGenerator.generate(neutralInput20, neutralMetrics20)
        val sr20 = (opinion20.positive + opinion20.neutral + opinion20.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.NEUTRAL, sr20.status)

        // 3. Больше 20: позитивный показатель (например 25%)
        val posInput25 = FinancialInputData(monthlyIncome = 100_000.0, monthlyExpenses = 75_000.0)
        val posMetrics25 = FinanceCalculator.calculate(posInput25)
        assertEquals(25.0, posMetrics25.savingsRate, 0.01)
        assertEquals(8, posMetrics25.scores.savingsRateScore) // 8..10 диапазон (зеленый)
        val opinion25 = AuditorAnalysisGenerator.generate(posInput25, posMetrics25)
        val sr25 = (opinion25.positive + opinion25.neutral + opinion25.negative).first { it.id == "savings_rate" }
        assertEquals(AuditIndicatorStatus.POSITIVE, sr25.status)

        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getSavingsRateStatus(20.01))
        assertEquals(8, FinancialMetricEvaluator.getSavingsRateScore(20.01))

        // Ноль теперь теряет прежние 5 баллов; ограничение при CFS < 5 сохраняется.
        val zeroRateScores = FinanceCalculator.calculateScores(0.0, 7.0, 0.3, 5.0)
        val positiveRateScores = FinanceCalculator.calculateScores(0.01, 7.0, 0.3, 5.0)
        assertEquals(0, zeroRateScores.savingsRateScore)
        assertEquals(5, positiveRateScores.savingsRateScore)
        assertTrue(zeroRateScores.totalScore < positiveRateScores.totalScore)
        assertEquals(6.0, FinanceCalculator.calculateTotalScore(10, 10, 10, 4), 0.0)
        assertTrue(FinanceCalculator.calculateTotalScore(10, 10, 10, 5) > 6.0)
    }

    @Test
    fun testTotalScoreColorGradation() {
        // >= 7.0: SuccessGreen
        assertEquals(SuccessGreen, ScoreColorEvaluator.getTotalScoreColor(7.0))
        assertEquals(SuccessGreen, ScoreColorEvaluator.getTotalScoreColor(8.5))
        assertEquals(SuccessGreen, ScoreColorEvaluator.getTotalScoreColor(10.0))

        // 3.5 .. < 7.0: WarningYellow
        assertEquals(WarningYellow, ScoreColorEvaluator.getTotalScoreColor(3.5))
        assertEquals(WarningYellow, ScoreColorEvaluator.getTotalScoreColor(5.0))
        assertEquals(WarningYellow, ScoreColorEvaluator.getTotalScoreColor(6.9))

        // < 3.5: AlertRed
        assertEquals(AlertRed, ScoreColorEvaluator.getTotalScoreColor(3.4))
        assertEquals(AlertRed, ScoreColorEvaluator.getTotalScoreColor(2.0))
        assertEquals(AlertRed, ScoreColorEvaluator.getTotalScoreColor(0.0))
    }

    @Test
    fun testFidelityCalculationAndColorGradation() {
        // 1. Проверка расчета % и коэффициента (может быть больше 100%)
        val target = 2_000_000.0

        // 150% от цели (капитал 3 000 000)
        val ratio150 = FinancialMetricEvaluator.calculateFidelityRatio(3_000_000.0, target)
        val percent150 = FinancialMetricEvaluator.calculateFidelityPercent(3_000_000.0, target)
        assertEquals(1.5, ratio150, 0.001)
        assertEquals(150.0, percent150, 0.001)
        assertEquals(AuditIndicatorStatus.POSITIVE, FinancialMetricEvaluator.getFidelityStatus(3_000_000.0, target))
        assertEquals(SuccessGreen, StatusColors.getColor(FinancialMetricEvaluator.getFidelityStatus(3_000_000.0, target)))

        // 80% от цели (капитал 1 600 000) - нейтральный (желтый)
        val ratio80 = FinancialMetricEvaluator.calculateFidelityRatio(1_600_000.0, target)
        val percent80 = FinancialMetricEvaluator.calculateFidelityPercent(1_600_000.0, target)
        assertEquals(0.8, ratio80, 0.001)
        assertEquals(80.0, percent80, 0.001)
        assertEquals(AuditIndicatorStatus.NEUTRAL, FinancialMetricEvaluator.getFidelityStatus(1_600_000.0, target))
        assertEquals(WarningYellow, StatusColors.getColor(FinancialMetricEvaluator.getFidelityStatus(1_600_000.0, target)))

        // 40% от цели (капитал 800 000) - негативный (красный)
        val ratio40 = FinancialMetricEvaluator.calculateFidelityRatio(800_000.0, target)
        val percent40 = FinancialMetricEvaluator.calculateFidelityPercent(800_000.0, target)
        assertEquals(0.4, ratio40, 0.001)
        assertEquals(40.0, percent40, 0.001)
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getFidelityStatus(800_000.0, target))
        assertEquals(AlertRed, StatusColors.getColor(FinancialMetricEvaluator.getFidelityStatus(800_000.0, target)))

        // Отрицательный капитал (-200 000)
        val ratioNeg = FinancialMetricEvaluator.calculateFidelityRatio(-200_000.0, target)
        val percentNeg = FinancialMetricEvaluator.calculateFidelityPercent(-200_000.0, target)
        assertEquals(-0.1, ratioNeg, 0.001)
        assertEquals(-10.0, percentNeg, 0.001)
        assertEquals(AuditIndicatorStatus.NEGATIVE, FinancialMetricEvaluator.getFidelityStatus(-200_000.0, target))
        assertEquals(AlertRed, StatusColors.getColor(FinancialMetricEvaluator.getFidelityStatus(-200_000.0, target)))

        // 2. Интеграция в модель и аудит: капитал выше 100% отображается в отчете и аудите
        val input = FinancialInputData(
            birthDate = "1990-01-01",
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 2_000_000.0,
            financialAssets = 3_000_000.0 // netWorth = 5 000 000
        )
        val metrics = FinanceCalculator.calculate(input)
        assertTrue(metrics.fidelityTarget > 0.0)
        assertTrue(metrics.fidelityPercent > 100.0)
        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)
        val fidelityItem = (opinion.positive + opinion.neutral + opinion.negative).first { it.id == "fidelity" }
        assertEquals(AuditIndicatorStatus.POSITIVE, fidelityItem.status)
        assertTrue(fidelityItem.text.contains("% от цели"))
    }

    @Test
    fun `Fidelity capital subtracts only debt not covered by property`() {
        assertEquals(55_000.0, FinancialMetricEvaluator.fidelityCapital(15_000.0, 40_000.0, 280_000.0, 180_000.0), 0.0)
        assertEquals(55_000.0, FinancialMetricEvaluator.fidelityCapital(15_000.0, 40_000.0, 0.0, 0.0), 0.0)
        assertEquals(35_000.0, FinancialMetricEvaluator.fidelityCapital(15_000.0, 40_000.0, 280_000.0, 300_000.0), 0.0)
        assertEquals(-65_000.0, FinancialMetricEvaluator.fidelityCapital(15_000.0, 40_000.0, 280_000.0, 400_000.0), 0.0)
    }

    @Test
    fun testFidelityDescriptionFootnoteLocalization() {
        val ruStrings = getAppStrings(AppLanguage.RU)
        assertTrue(ruStrings.metricFidelityDesc.contains("не связано с компанией Fidelity Investments"))
        assertTrue(ruStrings.metricFidelityDesc.contains("открытые исследовательские ориентиры"))

        val enStrings = getAppStrings(AppLanguage.EN)
        assertTrue(enStrings.metricFidelityDesc.contains("not affiliated with or endorsed by Fidelity Investments"))
        assertTrue(enStrings.metricFidelityDesc.contains("publicly available research milestones"))
    }

    @Test
    fun testSavingsRateScoreCommentsLocalization() {
        val ruStrings = StringsRu
        val enStrings = StringsEn

        // При норме сбережений 8% (низкое значение)
        val commentRu8 = ruStrings.savingsRateScoreComment(8.0, "8,0%")
        assertTrue(commentRu8.contains("низкое значение (8,0%)"))

        val commentEn8 = enStrings.savingsRateScoreComment(8.0, "8.0%")
        assertTrue(commentEn8.contains("low rate (8.0%)"))

        // При норме сбережений 25% (отличное значение)
        val commentRu25 = ruStrings.savingsRateScoreComment(25.0, "25,0%")
        assertTrue(commentRu25.contains("отличное значение (25,0%)"))

        val commentEn25 = enStrings.savingsRateScoreComment(25.0, "25.0%")
        assertTrue(commentEn25.contains("excellent rate (25.0%)"))

        // При отрицательной норме сбережений (-10%)
        val commentRuNeg = ruStrings.savingsRateScoreComment(-10.0, "-10,0%")
        assertTrue(commentRuNeg.contains("отрицательное значение"))

        val commentEnNeg = enStrings.savingsRateScoreComment(-10.0, "-10.0%")
        assertTrue(commentEnNeg.contains("negative rate"))
    }

    @Test
    fun testSafetyMonthsScoreCommentsLocalization() {
        val ruStrings = StringsRu
        val enStrings = StringsEn

        // Запас 1.5 мес (недостаточный)
        val commentRuLow = ruStrings.safetyMonthsScoreComment(1.5, "1,5 мес.")
        assertTrue(commentRuLow.contains("недостаточный запас"))

        val commentEnLow = enStrings.safetyMonthsScoreComment(1.5, "1.5 mo.")
        assertTrue(commentEnLow.contains("insufficient buffer"))

        // Запас 15 мес (отличный)
        val commentRuHigh = ruStrings.safetyMonthsScoreComment(15.0, "15,0 мес.")
        assertTrue(commentRuHigh.contains("запас превышает 12 месяцев"))

        val commentEnHigh = enStrings.safetyMonthsScoreComment(15.0, "15.0 mo.")
        assertTrue(commentEnHigh.contains("buffer exceeds 12 months"))
    }

    @Test
    fun testInvestmentsCategoriesDevelopedAndEmergingBenchmarks() {
        val ru = StringsRu
        val en = StringsEn

        val ruAllText = ru.investmentsCategories.flatMap { it.items }.joinToString(" ")
        val enAllText = en.investmentsCategories.flatMap { it.items }.joinToString(" ")

        assertTrue(ruAllText.contains("развитых стран"))
        assertTrue(ruAllText.contains("развивающихся рынков"))
        assertTrue(enAllText.contains("Developed markets"))
        assertTrue(enAllText.contains("Emerging markets"))

        assertFalse(ruAllText.contains("рублях"))
        assertFalse(enAllText.contains("rubles"))
        assertFalse(ruAllText.contains("ЦБ"))
    }
}
