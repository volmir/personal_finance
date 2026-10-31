package com.example

import com.example.data.db.ScenarioEntity
import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.ProjectionEngine
import com.example.domain.ProjectionParams
import com.example.domain.TemporaryExpense
import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapitalGainsTaxTest {

    @Test
    fun testCapitalGainsTaxCalculationsAndImpact() {
        val baseInput = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 1_000_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0,
            capitalGainsTaxPercent = 20.0
        )

        // 1. Dynamic individual calculation: gain share and effective tax rate are computed from individual user data
        val metrics20 = FinanceCalculator.calculate(baseInput)
        assertEquals(20.0, metrics20.capitalGainsTaxPercent, 0.01)
        assertTrue(metrics20.capitalGainsGainSharePercent > 0.0)
        assertTrue(metrics20.effectiveCapitalGainsTaxRate > 0.0)

        // Без временных расходов calculate() и движок используют один и тот же расчет налога.
        val engine = ProjectionEngine(ProjectionParams.from(baseInput))
        val yearsInRetirement = (baseInput.lifeExpectancy - engine.params.currentAge - metrics20.yearsToRetirement)
            .coerceAtLeast(0.0)
        val baseReq = engine.computeBaseRequirement(metrics20.yearsToRetirement, yearsInRetirement)
        assertEquals(
            engine.getEffectiveTaxRateAt(metrics20.yearsToRetirement, yearsInRetirement, baseReq),
            metrics20.effectiveCapitalGainsTaxRate,
            0.000001
        )
        assertEquals(
            engine.getGainShareAt(metrics20.yearsToRetirement, yearsInRetirement, baseReq),
            metrics20.capitalGainsGainSharePercent,
            0.000001
        )

        // 2. Custom rate: 23% scales effective tax proportionally
        val input23 = baseInput.copy(capitalGainsTaxPercent = 23.0)
        val metrics23 = FinanceCalculator.calculate(input23)
        assertEquals(23.0, metrics23.capitalGainsTaxPercent, 0.01)
        assertTrue(metrics23.effectiveCapitalGainsTaxRate > metrics20.effectiveCapitalGainsTaxRate)

        // 3. Higher capital gains tax increases required FIRE capital and years to retirement
        val input0 = baseInput.copy(capitalGainsTaxPercent = 0.0)
        val metrics0 = FinanceCalculator.calculate(input0)
        assertEquals(0.0, metrics0.effectiveCapitalGainsTaxRate, 0.01)
        assertTrue(metrics23.retirementTargetCapital > metrics0.retirementTargetCapital)
        assertTrue(metrics23.yearsToRetirement >= metrics0.yearsToRetirement)

        // 4. If user has no financial assets (stocks/bonds), effective tax rate is 0
        val inputNoFin = baseInput.copy(financialAssets = 0.0, liquidAssets = 1_100_000.0)
        val metricsNoFin = FinanceCalculator.calculate(inputNoFin)
        assertEquals(0.0, metricsNoFin.effectiveCapitalGainsTaxRate, 0.01)

        // 5. Export summary includes capital gains tax in Block 1 and full effective rate in Block 2 (both RU and EN)
        val exportRu = AuditorAnalysisGenerator.generateExportSummary(baseInput, metrics20, AppLanguage.RU)
        assertTrue(exportRu.contains("Номинальная доходность:"))
        assertTrue(exportRu.contains("• Налог на прирост капитала: 20"))
        assertTrue(exportRu.contains("Эффективная ставка налога с вывода капитала:"))
        assertFalse(exportRu.contains("эфф."))

        val exportEn = AuditorAnalysisGenerator.generateExportSummary(baseInput, metrics20, AppLanguage.EN)
        assertTrue(exportEn.contains("Nominal return:"))
        assertTrue(exportEn.contains("• Capital gains tax: 20"))
        assertTrue(exportEn.contains("Effective capital gains tax on withdrawal:"))
        assertFalse(exportEn.contains("eff."))
    }

    @Test
    fun testTemporaryExpensesUseEngineTaxCalculation() {
        val input = FinancialInputData(
            monthlyIncome = 100_000.0,
            monthlyExpenses = 50_000.0,
            liquidAssets = 100_000.0,
            financialAssets = 1_000_000.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 80,
            nominalReturnPercent = 10.0,
            inflationPercent = 5.0,
            capitalGainsTaxPercent = 20.0
        )
        val temporaryExpenses = listOf(TemporaryExpense(annualAmount = 120_000.0, durationYears = 10.0))
        val metrics = FinanceCalculator.calculate(input, temporaryExpenses = temporaryExpenses)
        val engine = ProjectionEngine(ProjectionParams.from(input, temporaryExpenses = temporaryExpenses))
        val yearsInRetirement = (input.lifeExpectancy - engine.params.currentAge - metrics.yearsToRetirement)
            .coerceAtLeast(0.0)
        val baseReq = engine.computeBaseRequirement(metrics.yearsToRetirement, yearsInRetirement)

        assertTrue(metrics.effectiveCapitalGainsTaxRate > 0.0)
        assertEquals(
            engine.getEffectiveTaxRateAt(metrics.yearsToRetirement, yearsInRetirement, baseReq),
            metrics.effectiveCapitalGainsTaxRate,
            0.000001
        )
    }

    @Test
    fun testScenarioEntityRoundtripWithCapitalGainsTax() {
        val original = FinancialInputData(
            monthlyIncome = 120_000.0,
            monthlyExpenses = 60_000.0,
            liquidAssets = 150_000.0,
            financialAssets = 800_000.0,
            propertyAssets = 2_500_000.0,
            liabilities = 300_000.0,
            birthDate = "1992-04-18",
            lifeExpectancy = 84,
            nominalReturnPercent = 12.0,
            inflationPercent = 6.0,
            currencySymbol = "€",
            includeFinancialInEmergencyFund = true,
            capitalGainsTaxPercent = 15.5,
            includePropertyInFireCapital = true
        )

        // 1. Преобразование FinancialInputData -> ScenarioEntity
        val entity = ScenarioEntity.fromInputData(name = "Target Retirement Plan", data = original, id = 42L)
        assertEquals("Target Retirement Plan", entity.name)
        assertEquals(42L, entity.id)
        assertEquals(15.5, entity.capitalGainsTaxPercent, 0.001)
        assertTrue(entity.includePropertyInFireCapital)

        // 2. Преобразование ScenarioEntity -> FinancialInputData
        val restored = entity.toInputData()
        assertEquals(15.5, restored.capitalGainsTaxPercent, 0.001)
        assertTrue(restored.includePropertyInFireCapital)
        assertEquals(original.monthlyIncome, restored.monthlyIncome, 0.001)
        assertEquals(original.monthlyExpenses, restored.monthlyExpenses, 0.001)
        assertEquals(original.liquidAssets, restored.liquidAssets, 0.001)
        assertEquals(original.financialAssets, restored.financialAssets, 0.001)
        assertEquals(original.propertyAssets, restored.propertyAssets, 0.001)
        assertEquals(original.liabilities, restored.liabilities, 0.001)
        assertEquals(original.birthDate, restored.birthDate)
        assertEquals(original.lifeExpectancy, restored.lifeExpectancy)
        assertEquals(original.nominalReturnPercent, restored.nominalReturnPercent, 0.001)
        assertEquals(original.inflationPercent, restored.inflationPercent, 0.001)
        assertEquals(original.currencySymbol, restored.currencySymbol)
        assertEquals(original.includeFinancialInEmergencyFund, restored.includeFinancialInEmergencyFund)
    }
}
