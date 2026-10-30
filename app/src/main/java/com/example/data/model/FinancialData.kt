package com.example.data.model

data class FinancialInputData(
    // Среднестатистические данные жителя США (US Federal Reserve / BLS)
    val monthlyIncome: Double = 6_000.0,
    val monthlyExpenses: Double = 4_800.0,
    val liquidAssets: Double = 15_000.0,
    val financialAssets: Double = 40_000.0,
    val propertyAssets: Double = 280_000.0,
    val liabilities: Double = 180_000.0,
    val birthDate: String = "1988-06-15",
    val lifeExpectancy: Int = 82,
    val nominalReturnPercent: Double = 7.5,
    val inflationPercent: Double = 2.5,
    val capitalGainsTaxPercent: Double = 20.0,
    val currencySymbol: String = "$",
    val includeFinancialInEmergencyFund: Boolean = true,
    val includePropertyInFireCapital: Boolean = false
) {
    companion object {
        // Средние показатели жителя США (US Average Data)
        fun usAverageData(): FinancialInputData = FinancialInputData()

        fun defaultData(): FinancialInputData = usAverageData()
        fun sampleData(): FinancialInputData = usAverageData()
        fun russiaAverageData(): FinancialInputData = usAverageData()
    }
}

enum class MetricStatus {
    EXCELLENT,
    GOOD,
    WARNING,
    CRITICAL
}

data class FinancialScores(
    val savingsRateScore: Int,
    val safetyMonthsScore: Int,
    val debtRatioScore: Int,
    val cfsScore: Int,
    val totalScore: Double
)

data class CalculatedMetrics(
    val cashFlow: Double,
    val savingsRate: Double,
    val totalAssets: Double,
    val emergencyFund: Double,
    val safetyMonths: Double,
    val debtRatio: Double,
    val netWorth: Double,
    val annualIncome: Double,
    val annualExpenses: Double,
    val currentAge: Int,
    val lifeExpectancy: Int,
    val remainingYearsHorizon: Int,
    val fidelityMultiplier: Double,
    val fidelityTarget: Double,
    val financialFreedomRatio: Double,
    val cfsStageDescription: String,
    val realReturnPercent: Double,
    val yearsToRetirement: Double,
    val retirementAge: Double,
    val retirementTargetCapital: Double,
    val scores: FinancialScores,
    val capitalGainsTaxPercent: Double = 20.0,
    val effectiveCapitalGainsTaxRate: Double = 12.0,
    val capitalGainsGainSharePercent: Double = 60.0,
    val isAgeValid: Boolean = true
) {
    val fidelityRatio: Double get() = com.example.domain.FinancialMetricEvaluator.calculateFidelityRatio(netWorth, fidelityTarget)
    val fidelityPercent: Double get() = com.example.domain.FinancialMetricEvaluator.calculateFidelityPercent(netWorth, fidelityTarget)
}

data class TrajectoryPoint(
    val age: Double,
    val yearOffset: Double,
    val accumulatedCapital: Double,
    val requiredCapital: Double,
    val isRetired: Boolean
)
