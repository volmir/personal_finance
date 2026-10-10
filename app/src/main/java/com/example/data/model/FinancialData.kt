package com.example.data.model

data class FinancialInputData(
    // Среднестатистические данные по РФ (Росстат / ЦБ РФ)
    val monthlyIncome: Double = 85_000.0,
    val monthlyExpenses: Double = 68_000.0,
    val liquidAssets: Double = 200_000.0,
    val financialAssets: Double = 450_000.0,
    val propertyAssets: Double = 3_800_000.0,
    val liabilities: Double = 480_000.0,
    val birthDate: String = "1985-06-15",
    val lifeExpectancy: Int = 78,
    val nominalReturnPercent: Double = 4.0,
    val inflationPercent: Double = 2.0,
    val currencySymbol: String = "₽",
    val includeFinancialInEmergencyFund: Boolean = true
) {
    companion object {
        // Средние показатели гражданина по РФ
        fun russiaAverageData(): FinancialInputData = FinancialInputData()

        fun sampleData(): FinancialInputData = russiaAverageData()
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
    val scores: FinancialScores
)

data class TrajectoryPoint(
    val age: Double,
    val yearOffset: Double,
    val accumulatedCapital: Double,
    val requiredCapital: Double,
    val isRetired: Boolean
)
