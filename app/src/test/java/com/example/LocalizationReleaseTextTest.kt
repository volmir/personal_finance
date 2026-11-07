package com.example

import com.example.data.model.FinancialInputData
import com.example.data.model.FireStatus
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.FinancialMetricEvaluator
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LocalizationReleaseTextTest {
    private val today = LocalDate.of(2026, 11, 6)

    @Test
    fun netWorthCaptionsUseExactlyOneColonInEveryLanguage() {
        for (lang in AppLanguage.values()) {
            val strings = getAppStrings(lang)
            for (currency in listOf("€", "¥")) {
                val amount = Formatters.formatMoney(1_234.5, currency, lang)
                val captions = listOf(
                    strings.metricNetWorthAssetsFormat(strings.metricNetWorthSubPrefix, amount),
                    strings.metricNetWorthNegativeAssetsFormat(strings.metricNetWorthSubPrefix, amount, strings.metricNetWorthNegativeSub)
                )
                assertEquals(strings.metricNetWorthSubPrefix + amount, captions.first())
                for (caption in captions) {
                    assertFalse("$lang: $caption", caption.contains(": :"))
                    assertFalse("$lang: $caption", caption.contains(" : :"))
                    assertEquals("$lang: $caption", 1, caption.count { it == ':' })
                }
                if (lang == AppLanguage.FR) assertTrue(captions.first().startsWith("Patrimoine total : "))
            }
        }
    }

    @Test
    fun assumptionsDescribeSimplifiedCapitalGainsTaxInScreensAndBothExports() {
        val phrases = mapOf(
            AppLanguage.RU to ("Налог на прирост капитала учитывается упрощённо" to "без учёта налогов"),
            AppLanguage.EN to ("Capital-gains tax is estimated in a simplified way" to "without accounting for taxes"),
            AppLanguage.DE to ("Die Steuer auf Kapitalerträge wird vereinfacht" to "ohne Steuern"),
            AppLanguage.FR to ("L'impôt sur les plus-values est estimé de manière simplifiée" to "sans tenir compte des impôts"),
            AppLanguage.IT to ("L'imposta sulle plusvalenze è stimata in modo semplificato" to "senza considerare imposte"),
            AppLanguage.ES to ("El impuesto sobre las plusvalías se estima de forma simplificada" to "sin contemplar impuestos")
        )
        val input = FinancialInputData.usAverageData()
        val metrics = FinanceCalculator.calculate(input, today = today)
        for ((lang, phrasesForLanguage) in phrases) {
            val strings = getAppStrings(lang)
            val body = strings.fireAssumptionsBody(
                Formatters.formatPercent(input.nominalReturnPercent, lang),
                Formatters.formatPercent(input.inflationPercent, lang)
            )
            assertTrue("$lang: $body", body.contains(phrasesForLanguage.first))
            assertFalse("$lang: $body", body.contains(phrasesForLanguage.second, ignoreCase = true))
            val disclaimer = strings.assumptionsDisclaimerBody(
                Formatters.formatPercent(input.nominalReturnPercent, lang),
                Formatters.formatPercent(input.inflationPercent, lang)
            )
            assertTrue("$lang: $disclaimer", disclaimer.contains(phrasesForLanguage.first))
            assertFalse("$lang: $disclaimer", disclaimer.contains(phrasesForLanguage.second, ignoreCase = true))
            assertTrue(body.contains(Formatters.formatPercent(input.nominalReturnPercent, lang)))
            assertTrue(body.contains(Formatters.formatPercent(input.inflationPercent, lang)))
            assertTrue(AuditorAnalysisGenerator.generateExportSummary(input, metrics, lang).contains(body))
            assertTrue(AuditorAnalysisGenerator.generate(input, metrics, lang).toFormattedShareText(input, metrics, lang).contains(body))
        }
    }

    @Test
    fun cfsDescriptionsAndStagesUseInclusiveLowerAndExclusiveUpperBounds() {
        val conjunctions = mapOf(
            AppLanguage.RU to "и", AppLanguage.EN to "and", AppLanguage.DE to "und",
            AppLanguage.FR to "et", AppLanguage.IT to "e", AppLanguage.ES to "y"
        )
        val limits = listOf(
            FinancialMetricEvaluator.CFS_THRESHOLD_DEBT, FinancialMetricEvaluator.CFS_THRESHOLD_BUFFER,
            FinancialMetricEvaluator.CFS_NEUTRAL_THRESHOLD, FinancialMetricEvaluator.CFS_POSITIVE_THRESHOLD,
            FinancialMetricEvaluator.CFS_THRESHOLD_STABILITY, FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE
        )
        for ((lang, conjunction) in conjunctions) {
            val strings = getAppStrings(lang)
            val thresholds = limits.map(strings::formatThreshold)
            val description = strings.metricCfsDesc(thresholds[0], thresholds[1], thresholds[2], thresholds[3], thresholds[4], thresholds[5])
            val stages = listOf(strings.cfsStageBuffer, strings.cfsStageCapital, strings.cfsStageInvesting, strings.cfsStageReturns, strings.cfsStageStability)
            val prefix = if (lang == AppLanguage.RU) "КФС" else "CFS"
            assertTrue(description.contains("$prefix < ${thresholds.first()}"))
            assertTrue(description.contains("$prefix >= ${thresholds.last()}"))
            for (index in stages.indices) {
                val interval = "$prefix >= ${thresholds[index]} $conjunction < ${thresholds[index + 1]}"
                assertTrue("$lang: $interval", description.contains(interval))
                assertTrue("$lang stage $index", stages[index](thresholds[index], thresholds[index + 1]).startsWith(interval))
            }
            assertFalse(description.contains("<="))
            assertFalse(description.contains(".."))
        }
    }

    @Test
    fun cfsAuditMentionsPropertyAndFireForBothUpperStages() {
        val propertyWords = mapOf(
            AppLanguage.RU to "недвижимость", AppLanguage.EN to "property", AppLanguage.DE to "Immobilien",
            AppLanguage.FR to "immobilier", AppLanguage.IT to "immobili", AppLanguage.ES to "inmuebles"
        )
        for (cfs in listOf(FinancialMetricEvaluator.CFS_THRESHOLD_STABILITY, FinancialMetricEvaluator.CFS_THRESHOLD_INDEPENDENCE)) {
            val input = FinancialInputData.usAverageData().copy(
                monthlyExpenses = 1_000.0, liquidAssets = 0.0, financialAssets = 0.0,
                propertyAssets = cfs * 12_000.0, liabilities = 0.0
            )
            val metrics = FinanceCalculator.calculate(input, today = today)
            assertEquals(cfs, metrics.financialFreedomRatio, 0.0)
            for ((lang, property) in propertyWords) {
                val text = AuditorAnalysisGenerator.generate(input, metrics, lang).positive.single { it.id == "cfs" }.text
                assertTrue("$lang: $text", text.contains(property, ignoreCase = true))
                assertTrue("$lang: $text", text.contains("FIRE"))
            }
        }
    }

    @Test
    fun balancedNetWorthTextDescribesBalanceEvenAtZero() {
        val balances = mapOf(
            AppLanguage.RU to "Чистый капитал неотрицателен, но обязательства составляют более половины активов.",
            AppLanguage.EN to "Net worth is not negative, but liabilities exceed half of total assets.",
            AppLanguage.DE to "Das Reinvermögen ist nicht negativ, aber die Verbindlichkeiten machen mehr als die Hälfte des Gesamtvermögens aus.",
            AppLanguage.FR to "La valeur nette n'est pas négative, mais les passifs représentent plus de la moitié du total des actifs.",
            AppLanguage.IT to "Il patrimonio netto non è negativo, ma le passività superano la metà delle attività totali.",
            AppLanguage.ES to "El patrimonio neto no es negativo, pero las deudas superan la mitad del total de activos."
        )
        for ((lang, balance) in balances) {
            // netWorth == 0 (assets = liabilities = 1_000.0)
            val inputZero = FinancialInputData.usAverageData().copy(liquidAssets = 1_000.0, financialAssets = 0.0, propertyAssets = 0.0, liabilities = 1_000.0)
            val metricsZero = FinanceCalculator.calculate(inputZero, today = today)
            val itemZero = AuditorAnalysisGenerator.generate(inputZero, metricsZero, lang).neutral.single { it.id == "net_worth" }
            assertEquals(String.format(Locale.ROOT, getAppStrings(lang).auditNetWorthBalanced, Formatters.formatMoney(0.0, inputZero.currencySymbol, lang)), itemZero.text)
            assertTrue("$lang: ${itemZero.text}", itemZero.text.endsWith(balance))

            // netWorth = 10% of assets (assets = 1_000.0, liabilities = 900.0 -> netWorth = 100.0)
            val inputTenPercent = FinancialInputData.usAverageData().copy(liquidAssets = 1_000.0, financialAssets = 0.0, propertyAssets = 0.0, liabilities = 900.0)
            val metricsTenPercent = FinanceCalculator.calculate(inputTenPercent, today = today)
            val itemTenPercent = AuditorAnalysisGenerator.generate(inputTenPercent, metricsTenPercent, lang).neutral.single { it.id == "net_worth" }
            assertEquals(String.format(Locale.ROOT, getAppStrings(lang).auditNetWorthBalanced, Formatters.formatMoney(100.0, inputTenPercent.currencySymbol, lang)), itemTenPercent.text)
            assertTrue("$lang: ${itemTenPercent.text}", itemTenPercent.text.endsWith(balance))
        }
    }

    @Test
    fun russianAgeAndHorizonUseYearFormsInScreensAndExport() {
        val strings = getAppStrings(AppLanguage.RU)
        for ((age, word) in listOf(21 to "год", 22 to "года", 25 to "лет", 11 to "лет", 101 to "год")) {
            assertEquals("$age $word", strings.fireCurrentAgeValue(age))
            assertEquals("$age $word", strings.fireLifeExpectancyValue(age))
            assertEquals("$age $word (возраст $age $word)", strings.horizonValueFormat(age, age))
            assertEquals("Возраст: $age $word | Множитель: 0,50x", strings.metricFidelitySubFormat(age, "0,50"))
            val input = FinancialInputData.usAverageData().copy(birthDate = today.minusYears(age.toLong()).toString(), lifeExpectancy = age + 1)
            val metrics = FinanceCalculator.calculate(input, today = today)
            val export = AuditorAnalysisGenerator.generateExportSummary(input, metrics, AppLanguage.RU)
            assertTrue(export.contains("• Возраст: $age $word (дожитие: ${age + 1} ${strings.plural(age + 1, strings.yearPluralForms)}, горизонт: 1 год)"))
        }
    }

    @Test
    fun yearFormsFollowDisplayedTwoDecimalValue() {
        val strings = getAppStrings(AppLanguage.RU)
        for ((number, word) in listOf(20.999 to "год", 21.004 to "год", 21.006 to "года", 21.5 to "года", 21.996 to "года", 24.999 to "лет")) {
            val display = Formatters.formatRatio(number, AppLanguage.RU)
            assertEquals("$display $word", "$display ${strings.fireYearsSuffix(number)}")
            assertEquals("$display $word", Formatters.formatFireRetirementAge(number, FireStatus.REACHABLE, AppLanguage.RU))
        }
        assertEquals("21,50 года", strings.fireRetirementAgeValue("21,50"))
        for (lang in AppLanguage.values()) {
            val localized = getAppStrings(lang)
            assertEquals("$lang rounded 1", localized.plural(1, localized.yearPluralForms), localized.fireYearsSuffix(1.004))
        }
    }

    @Test
    fun fireHorizonInstructionIsUniqueAndDefinedInAllLanguages() {
        val expected = mapOf(
            AppLanguage.RU to "Укажите дату рождения и ожидаемый возраст дожития, опираясь на ориентиры и рекомендации из списка ниже.",
            AppLanguage.EN to "Enter your date of birth and expected life expectancy, using the benchmarks and recommendations below.",
            AppLanguage.DE to "Geben Sie Ihr Geburtsdatum und die erwartete Lebenserwartung anhand der folgenden Richtwerte und Empfehlungen ein.",
            AppLanguage.FR to "Indiquez votre date de naissance et votre espérance de vie estimée en vous appuyant sur les repères et recommandations ci-dessous.",
            AppLanguage.IT to "Inserisci la data di nascita e l'aspettativa di vita prevista basandoti sui punti di riferimento e sui consigli sottostanti.",
            AppLanguage.ES to "Introduce tu fecha de nacimiento y la esperanza de vida estimada basándote en las referencias y recomendaciones a continuación."
        )
        for (lang in AppLanguage.values()) {
            val strings = getAppStrings(lang)
            assertEquals("$lang instruction", expected[lang], strings.fireHorizonInstruction)
            // Must be distinct from the standard breakdown instruction for other blocks
            org.junit.Assert.assertNotEquals("$lang distinct from breakdownInstruction", strings.breakdownInstruction, strings.fireHorizonInstruction)
        }
    }

    @Test
    fun scenarioInflationTitleHasNoInflationSuffixInAllLanguages() {
        val expected = mapOf(
            AppLanguage.RU to "Рост расходов",
            AppLanguage.EN to "Expense Increase",
            AppLanguage.DE to "Kostensteigerung",
            AppLanguage.FR to "Hausse des coûts",
            AppLanguage.IT to "Aumento dei costi",
            AppLanguage.ES to "Subida de costes"
        )
        for (lang in AppLanguage.values()) {
            val strings = getAppStrings(lang)
            assertEquals("$lang scenarioInflationTitle", expected[lang], strings.scenarioInflationTitle)
            assertFalse("$lang title contains parenthesis", strings.scenarioInflationTitle.contains("("))
        }
    }
}
