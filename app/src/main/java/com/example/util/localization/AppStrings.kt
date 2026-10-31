package com.example.util.localization

import androidx.compose.runtime.compositionLocalOf
import android.icu.text.PluralRules
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

data class PluralForms(
    val zero: String? = null,
    val one: String? = null,
    val two: String? = null,
    val few: String? = null,
    val many: String? = null,
    val other: String
) {
    fun forCategory(category: String): String = when (category) {
        "zero" -> zero ?: other
        "one" -> one ?: other
        "two" -> two ?: other
        "few" -> few ?: other
        "many" -> many ?: other
        else -> other
    }
}

data class BreakdownCategory(
    val title: String,
    val items: List<String>
)

interface AppStrings {
    val language: AppLanguage

    // App & Navigation
    val appName: String
    val tabInputTitle: String
    val tabInputShort: String
    val tabMetricsTitle: String
    val tabMetricsShort: String
    val tabScoresTitle: String
    val tabScoresShort: String
    val tabFireTitle: String
    val tabFireShort: String
    val quickScorePrefix: String

    // Input Screen
    val block1Title: String
    val block1Subtitle: String
    val monthlyIncomeLabel: String
    val monthlyExpensesLabel: String
    val incomeBreakdownName: String
    val incomeHintCollapsed: String
    val incomeHintExpanded: String
    val expensesBreakdownName: String
    val expensesHintCollapsed: String
    val expensesHintExpanded: String
    val breakdownInstruction: String
    val breakdownInstructionIcon: String
    val breakdownCategoryTitleFormat: (title: String) -> String
    val breakdownCategoryItemFormat: (item: String) -> String
    val incomeCategories: List<BreakdownCategory>
    val expensesCategories: List<BreakdownCategory>
    val cashFlowTitle: String
    val cashFlowFormula: String
    val savingsRateTitle: String
    val savingsRateFormula: String

    val block2Title: String
    val block2Subtitle: String
    val liquidAssetsLabel: String
    val financialAssetsLabel: String
    val propertyAssetsLabel: String
    val assetsBreakdownName: String
    val assetsHintCollapsed: String
    val assetsHintExpanded: String
    val assetsCategories: List<BreakdownCategory>
    val totalAssetsTitle: String
    val totalAssetsFormula: String
    val toggleEmergencyFundTitle: String
    val toggleEmergencyFundSubOn: String
    val toggleEmergencyFundSubOff: String

    val block3Title: String
    val block3Subtitle: String
    val liabilitiesLabel: String
    val liabilitiesBreakdownName: String
    val liabilitiesHintCollapsed: String
    val liabilitiesHintExpanded: String
    val liabilitiesCategories: List<BreakdownCategory>
    val netWorthTitle: String
    val netWorthFormula: String

    val block4Title: String
    val block4Subtitle: String
    val nominalReturnLabel: String
    val inflationLabel: String
    val capitalGainsTaxLabel: String
    val capitalGainsTaxBreakdownName: String
    val capitalGainsTaxHintCollapsed: String
    val capitalGainsTaxHintExpanded: String
    val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String
    val capitalGainsTaxFormula: String
    val capitalGainsTaxCategories: List<BreakdownCategory>
    val investmentsBreakdownName: String
    val investmentsHintCollapsed: String
    val investmentsHintExpanded: String
    val investmentsCategories: List<BreakdownCategory>
    val realReturnTitle: String
    val realReturnFormula: String

    val block5Title: String
    val block5Subtitle: String
    val birthDateLabel: String
    val invalidBirthDateError: String
    val lifeExpectancyLabel: String
    val fireHorizonBreakdownName: String
    val fireHorizonHintCollapsed: String
    val fireHorizonHintExpanded: String
    val fireHorizonCategories: List<BreakdownCategory>
    val horizonTitle: String
    val horizonFormula: String
    val horizonValueFormat: (horizon: Int, age: Int) -> String
    val includePropertyInFireLabel: String
    val includePropertyInFireSubOn: String
    val includePropertyInFireSubOff: String

    val calculateButtonText: String
    val exportButtonText: String
    val exportChooserTitle: String

    // Metrics Screen
    val metricsHeaderTitle: String
    val metricsHeaderSubtitle: String
    val categoryAll: String
    val categoryCashFlow: String
    val categoryCapitalRisks: String
    val categoryBenchmarks: String
    val metricCardInfo: String
    val metricCardHide: String
    val metricCardFormulaPrefix: String
    val metricCardExpand: String
    val metricCardCollapse: String

    val metricCashFlowTitle: String
    val metricCashFlowFormula: String
    val metricCashFlowDesc: String
    val metricCashFlowPositiveSub: String
    val metricCashFlowNeutralSub: String
    val metricCashFlowNegativeSub: String

    val metricSavingsRateTitle: String
    val metricSavingsRateFormula: String
    val metricSavingsRateDesc: (neutralThreshold: String, positiveThreshold: String) -> String
    val metricSavingsRateSub: (neutralThreshold: String, positiveThreshold: String) -> String

    val metricEmergencyFundTitle: String
    val metricEmergencyFundFormula: String
    val metricEmergencyFundDesc: String
    val metricEmergencyFundSubLiquidPlusFin: String
    val metricEmergencyFundSubOnlyLiquid: String

    val metricSafetyMonthsTitle: String
    val metricSafetyMonthsFormula: String
    val metricSafetyMonthsDesc: (neutralThreshold: String, positiveThreshold: String, excellentThreshold: String) -> String
    val metricSafetyMonthsSub: (neutralThreshold: String, positiveThreshold: String, excellentThreshold: String) -> String
    val monthsSuffix: String

    val metricDebtRatioTitle: String
    val metricDebtRatioFormula: String
    val metricDebtRatioDesc: (zero: String, positiveThreshold: String, neutralThreshold: String, criticalThreshold: String) -> String
    val metricDebtRatioLowSub: String
    val metricDebtRatioHighSub: String

    val metricNetWorthTitle: String
    val metricNetWorthFormula: String
    val metricNetWorthDesc: String
    val metricNetWorthSubPrefix: String
    val metricNetWorthNegativeSub: String
    val metricNetWorthAssetsFormat: (prefix: String, amount: String) -> String
    val metricNetWorthNegativeAssetsFormat: (prefix: String, amount: String, negative: String) -> String

    val metricFidelityTitle: String
    val metricFidelityFormula: String
    val metricFidelityDesc: String
    val metricFidelitySubFormat: (age: Int, mult: String) -> String
    val metricFidelityProgress: String

    val metricCfsTitle: String
    val metricCfsFormula: String
    val metricCfsDesc: (debt: String, buffer: String, neutral: String, positive: String, stability: String, independence: String) -> String

    val metricRealReturnTitle: String
    val metricRealReturnFormula: String
    val metricRealReturnDesc: String
    val metricRealReturnSubFormat: (nom: String, infl: String) -> String
    val metricUnavailableValue: String

    // Scores Screen
    val scoresHeaderTitle: String
    val scoresHeaderSubtitle: String
    val scoresShareReportChooser: String
    val scoresGaugeOutOf: (max: Int) -> String
    val scoresTotalResult: (score: String) -> String
    val scoresStatusExcellent: String
    val scoresStatusGood: String
    val scoresStatusModerate: String
    val scoresStatusAttention: String
    val scoresBreakdownTitle: String
    val scoreValuePrefix: String
    val scoreOutOfTen: (score: Int) -> String
    val scoreSlashOutOfTen: (score: String) -> String
    val savingsRateScoreComment: (rate: Double, sr: String, positiveThreshold: String) -> String
    val safetyMonthsScoreComment: (months: Double, monthsText: String, neutralThreshold: String, positiveThreshold: String, excellentThreshold: String) -> String
    val debtComment0: (zero: String, positiveThreshold: String) -> String
    val debtComment1: (positiveThreshold: String, neutralThreshold: String) -> String
    val debtComment2: (neutralThreshold: String, criticalThreshold: String) -> String
    val debtComment3: (criticalThreshold: String) -> String

    val auditorOpinionTitle: String
    val auditorOpinionSubtitle: (count: Int) -> String
    val shareAuditorOpinionChooser: String
    val positiveBadgeText: (count: Int) -> String
    val neutralBadgeText: (count: Int) -> String
    val negativeBadgeText: (count: Int) -> String
    val positiveSectionTitle: (count: Int) -> String
    val neutralSectionTitle: (count: Int) -> String
    val negativeSectionTitle: (count: Int) -> String
    val positiveEmptyText: String
    val neutralEmptyText: String
    val negativeEmptyText: String

    // FIRE Screen
    val fireHeroTitle: String
    val fireHeroSubtitle: String
    val fireTimeToRetire: String
    val fireYearsSuffix: String
    val fireHeroAssumption: String
    val fireRetirementAgeLabel: String
    val fireRetirementAgeValue: (age: String) -> String
    val fireTargetCapitalLabel: String
    val fireParamsTitle: String
    val fireBirthDateLabel: String
    val fireCurrentAgeLabel: String
    val fireCurrentAgeValue: (age: Int) -> String
    val fireLifeExpectancyLabel: String
    val fireLifeExpectancyValue: (exp: Int) -> String
    val fireHorizonLabel: String
    val fireHorizonValue: (h: Int) -> String
    val fireRealReturnLabel: String
    val fireTrajectoryTitle: String
    val fireTrajectorySubtitle: String
    val fireChartAccumulated: String
    val fireChartRequired: String
    val fireChartYearsSuffix: String
    val fireChartFirePoint: (age: String) -> String
    val fireSimSummaryTitle: String
    val fireSimEarlier: (diff: String, target: String, age: String) -> String
    val fireSimLater: (diff: String, target: String, age: String) -> String
    val fireSimSame: (target: String) -> String
    val fireSimWithoutBase: (target: String, age: String) -> String
    val fireUnreachableText: String
    val fireHorizonExhaustedText: String
    val fireSimCashFlowRow: String
    val fireSimExpensesRow: String
    val fireSimNetWorthRow: String
    val fireSimPropertyRow: String
    val fireSimSliderTitle: String
    val fireSimReset: String
    val fireSimFineTuning: String
    val fireSliderSavings: String
    val fireSliderExpenses: String
    val fireSliderReturn: String
    val fireSliderBaseSuffix: String
    val fireSliderBaseReturnSuffix: String
    val fireSliderFrom: (v: String) -> String
    val fireSliderStep: (v: String) -> String
    val fireSliderTo: (v: String) -> String
    val fireSliderInc: String
    val fireSliderDec: String

    // Scenario Panel
    val scenarioHeaderTitle: String
    val scenarioHeaderSubtitle: String
    val scenarioTotalBadge: (count: Int) -> String
    val scenarioPositiveTitle: String
    val scenarioPositiveSectionNumber: String
    val scenarioPositiveActive: (count: Int) -> String
    val scenarioPositiveSubtitle: String
    val scenarioNegativeTitle: String
    val scenarioNegativeSectionNumber: String
    val scenarioNegativeActive: (count: Int) -> String
    val scenarioNegativeSubtitle: String
    val scenarioParamsPrefix: String

    // Scenarios Items (titles, parameters, descriptions)
    val scenarioSalaryIncreaseTitle: String
    val scenarioSalaryIncreaseParam: (percent: String) -> String
    val scenarioSalaryIncreaseDesc: (amt: String) -> String

    val scenarioMarketGrowthTitle: (percent: String) -> String
    val scenarioMarketGrowthParam: (percent: String) -> String
    val scenarioMarketGrowthDesc: (amt: String) -> String

    val scenarioExpenseOptTitle: String
    val scenarioExpenseOptParam: (percent: String) -> String
    val scenarioExpenseOptDesc: (amt: String) -> String

    val scenarioRelocationTitle: String
    val scenarioRelocationParam: (percent: String) -> String
    val scenarioRelocationDesc: (amt: String) -> String

    val scenarioInheritanceTitle: String
    val scenarioInheritanceParam: (percent: String) -> String
    val scenarioInheritanceDesc: (amt: String) -> String

    val scenarioPropertySaleTitle: String
    val scenarioPropertySaleParam: (percent: String) -> String
    val scenarioPropertySaleDesc: (amt: String) -> String

    val scenarioChildrenSelfSufficientTitle: String
    val scenarioChildrenSelfSufficientParam: (percent: String, years: String) -> String
    val scenarioChildrenSelfSufficientDesc: (amt: String, years: String) -> String

    val scenarioJobLossTitle: String
    val scenarioJobLossParam: (months: String) -> String
    val scenarioJobLossDesc: (months: String) -> String

    val scenarioMarketCrashTitle: (percent: String) -> String
    val scenarioMarketCrashParam: (percent: String) -> String
    val scenarioMarketCrashDesc: (percent: String, amt: String) -> String

    val scenarioMortgageTitle: String
    val scenarioMortgageParam: (percent: String, years: String) -> String
    val scenarioMortgageDesc: (pay: String, years: String) -> String

    val scenarioCarLoanTitle: String
    val scenarioCarLoanParam: (percent: String, years: String) -> String
    val scenarioCarLoanDesc: (pay: String, years: String) -> String

    val scenarioLargeExpenseTitle: String
    val scenarioLargeExpenseParam: (percent: String) -> String
    val scenarioLargeExpenseDesc: (amt: String) -> String

    val scenarioInflationTitle: String
    val scenarioInflationParam: (percent: String) -> String
    val scenarioInflationDesc: (amt: String) -> String

    val scenarioChildTitle: String
    val scenarioChildParam: (percent: String, years: String) -> String
    val scenarioChildDesc: (amt: String, years: String) -> String

    val disclaimerTitle: String
    val disclaimerText: String
    val assumptionsDisclaimerTitle: String
    val assumptionsDisclaimerBody: (returnRate: String, inflation: String) -> String
    val fireAssumptionsTitle: String
    val fireAssumptionsBody: (returnRate: String, inflation: String) -> String

    // Scenario feedback messages

    // Pluralization
    val countFormat: String
    val checkboxPluralForms: PluralForms
    val yearPluralForms: PluralForms
    val monthPluralForms: PluralForms
    val pointPluralForms: PluralForms
    val simulationBadgeSlidersSuffix: String
    val simulationBadgeOnlySliders: String

    fun plural(n: Number, forms: PluralForms): String {
        val category = PluralRules.forLocale(language.locale).select(n.toDouble())
        return forms.forCategory(category)
    }

    fun formatCount(n: Number, forms: PluralForms): String {
        val localizedNumber = NumberFormat.getNumberInstance(language.locale).format(n)
        return String.format(Locale.ROOT, countFormat, localizedNumber, plural(n, forms))
    }

    fun formatThreshold(value: Double): String = DecimalFormat(
        "#,##0.#",
        DecimalFormatSymbols.getInstance(language.locale)
    ).format(value)

    fun formatDebtRatioThreshold(value: Double): String = DecimalFormat(
        "#,##0.0",
        DecimalFormatSymbols.getInstance(language.locale)
    ).format(value)

    // Currency first, then the formatted amount. The template controls symbol placement.
    val moneyFormat: String

    // Score & Safety Months formats
    val safetyMonthsValueFormat: String
    val shareScoreReportTemplate: String

    // CFS Stage descriptions
    val cfsStageDebt: (debt: String) -> String
    val cfsStageBuffer: (debt: String, buffer: String) -> String
    val cfsStageCapital: (buffer: String, neutral: String) -> String
    val cfsStageInvesting: (neutral: String, positive: String) -> String
    val cfsStageReturns: (positive: String, stability: String) -> String
    val cfsStageStability: (stability: String, independence: String) -> String
    val cfsStageIndependence: (independence: String) -> String

    // Auditor Opinion Share Text
    val auditShareHeaderTitle: String
    val auditShareScoreLine: String
    val auditShareTotalCountLine: String
    val auditSharePositiveHeader: String
    val auditSharePositiveEmpty: String
    val auditShareNeutralHeader: String
    val auditShareNeutralEmpty: String
    val auditShareNegativeHeader: String
    val auditShareNegativeEmpty: String
    val auditShareGeneratedBy: String
    val auditShareDisclaimer: String

    // Auditor Indicators
    val auditCashFlowTitle: String
    val auditCashFlowPositive: String
    val auditCashFlowNeutral: String
    val auditCashFlowNegative: String

    val auditSavingsRateTitle: String
    val auditSavingsRatePositive: (value: String, positiveThreshold: String) -> String
    val auditSavingsRateNeutral: (value: String, neutralThreshold: String, positiveThreshold: String) -> String
    val auditSavingsRateNegative: String

    val auditEmergencyFundTitle: String
    val auditEmergencyFundPositive: String
    val auditEmergencyFundNeutral: (value: String, positiveMonths: String) -> String
    val auditEmergencyFundNegative: String

    val auditSafetyMonthsTitle: String
    val auditSafetyMonthsHigh: (value: String, excellentMonths: String) -> String
    val auditSafetyMonthsTarget: (value: String, positiveMonths: String) -> String
    val auditSafetyMonthsNeutral: (value: String, positiveMonths: String) -> String
    val auditSafetyMonthsNegative: (value: String, neutralMonths: String, positiveMonths: String) -> String

    val auditDebtRatioTitle: String
    val auditDebtRatioZero: String
    val auditDebtRatioConservative: String
    val auditDebtRatioModerate: String
    val auditDebtRatioHigh: String
    val auditDebtRatioCritical: String

    val auditNetWorthTitle: String
    val auditNetWorthStrong: String
    val auditNetWorthBalanced: String
    val auditNetWorthNegative: String

    val auditFidelityTitle: String
    val auditFidelityPositive: String
    val auditFidelityNeutral: String
    val auditFidelityNegativeInsolvent: String
    val auditFidelityNegativeLagging: String

    val auditCfsTitle: String
    val auditCfsIndependence: (cfs: String, independence: String) -> String
    val auditCfsStability: (cfs: String, stability: String, independence: String) -> String
    val auditCfsGrowth: (cfs: String, stability: String) -> String
    val auditCfsInitial: String
    val auditCfsNegative: String
    val auditCfsBuffer: (cfs: String, buffer: String) -> String
    val auditCfsPathToIndependence: String
    val auditCfsCloseToIndependence: (percent: Int) -> String
    val auditCfsFinanciallyIndependent: String

    val auditRealReturnTitle: String
    val auditRealReturnPositive: String
    val auditRealReturnNeutral: String
    val auditRealReturnNegative: String

    // Export Summary
    val exportHeaderTitle: String
    val exportSection1Title: String
    val exportIncomeLine: String
    val exportExpensesLine: String
    val exportLiquidAssetsLine: String
    val exportFinancialAssetsLine: String
    val exportPropertyAssetsLine: String
    val exportLiabilitiesLine: String
    val exportAgeHorizonLine: String
    val exportReturnsInflationLine: String
    val exportTaxLine: String

    val exportSection2Title: String
    val exportCashFlowLine: String
    val exportSavingsRateLine: String
    val exportEmergencyFundLine: String
    val exportDebtRatioLine: String
    val exportNetWorthLine: String
    val exportRealReturnLine: String
    val exportEffectiveTaxLine: String

    val exportSection3Title: String
    val exportScoreTotalLine: String
    val exportScoreSavingsRateLine: String
    val exportScoreSafetyMonthsLine: String
    val exportScoreDebtRatioLine: String
    val exportScoreCfsLine: String

    val exportSection4Title: String
    val exportCfsLine: String
    val exportFireTargetCapitalLine: String
    val exportFireTimeToRetirementLine: String
    val exportFidelityLine: String
}

fun getAppStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.RU -> StringsRu
        AppLanguage.EN -> StringsEn
    }
}

val LocalAppStrings = compositionLocalOf<AppStrings> { error("LocalAppStrings was not provided") }
val LocalAppLanguage = compositionLocalOf { AppLanguage.RU }
