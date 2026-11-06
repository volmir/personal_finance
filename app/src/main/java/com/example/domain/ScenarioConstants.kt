package com.example.domain

/** Scenario values are expressed in their displayed units so UI copy and calculations share one source. */
object ScenarioConstants {
    const val SALARY_INCREASE_PERCENT = 20.0
    const val MARKET_GROWTH_PERCENT = 25.0
    const val EXPENSE_OPTIMIZATION_PERCENT = 15.0
    const val RELOCATION_EXPENSE_REDUCTION_PERCENT = 30.0
    const val INHERITANCE_PERCENT_OF_MONTHLY_INCOME = 1200.0
    const val PROPERTY_SALE_PERCENT_OF_MONTHLY_INCOME = 2000.0
    const val CHILDREN_EXPENSE_REDUCTION_PERCENT = 25.0
    const val CHILDREN_SELF_SUFFICIENT_YEARS = 18.0
    const val JOB_LOSS_MONTHS = 12.0
    const val MARKET_CRASH_PERCENT = 30.0
    const val MORTGAGE_INCOME_PERCENT = 35.0
    const val MORTGAGE_DURATION_YEARS = 20.0
    const val CAR_LOAN_INCOME_PERCENT = 20.0
    const val CAR_LOAN_DURATION_YEARS = 5.0
    const val LARGE_ONE_OFF_EXPENSE_PERCENT = 500.0
    const val EXPENSE_INFLATION_PERCENT = 25.0
    const val CHILD_DEPENDENT_EXPENSE_PERCENT = 25.0
    const val CHILD_DEPENDENT_DURATION_YEARS = 18.0
}

data class ScenarioAmounts(
    val monthlyIncome: Double,
    val salaryIncrease: Double,
    val inheritance: Double,
    val propertySale: Double,
    val jobLoss: Double,
    val mortgageMonthly: Double,
    val carLoanMonthly: Double,
    val largeOneOffExpense: Double
)

/** Job loss and loan payments use current income; windfalls and one-off spending use base income. */
fun calculateScenarioAmounts(
    monthlyIncome: Double,
    propertyAssets: Double,
    salaryIncrease20: Boolean
): ScenarioAmounts {
    val currentIncome = monthlyIncome *
        (1.0 + if (salaryIncrease20) ScenarioConstants.SALARY_INCREASE_PERCENT / 100.0 else 0.0)
    return ScenarioAmounts(
        monthlyIncome = currentIncome,
        salaryIncrease = monthlyIncome * ScenarioConstants.SALARY_INCREASE_PERCENT / 100.0,
        inheritance = ScenarioConstants.INHERITANCE_PERCENT_OF_MONTHLY_INCOME / 100.0 * monthlyIncome,
        propertySale = minOf(ScenarioConstants.PROPERTY_SALE_PERCENT_OF_MONTHLY_INCOME / 100.0 * monthlyIncome, propertyAssets),
        jobLoss = ScenarioConstants.JOB_LOSS_MONTHS * currentIncome,
        mortgageMonthly = ScenarioConstants.MORTGAGE_INCOME_PERCENT / 100.0 * currentIncome,
        carLoanMonthly = ScenarioConstants.CAR_LOAN_INCOME_PERCENT / 100.0 * currentIncome,
        largeOneOffExpense = ScenarioConstants.LARGE_ONE_OFF_EXPENSE_PERCENT / 100.0 * monthlyIncome
    )
}

data class SimulationOngoingExpenses(
    val beforeReduction: Double,
    val effective: Double
)

/** Permanent expenses, excluding mortgage, car loan and child payments. */
fun calculateSimulationOngoingExpenses(
    monthlyExpenses: Double,
    expenseReduction: Double,
    expenseOptimization15: Boolean,
    relocationLowerCost30: Boolean,
    expensesPlus25: Boolean
): SimulationOngoingExpenses {
    val multiplier =
        (if (expenseOptimization15) 1.0 - ScenarioConstants.EXPENSE_OPTIMIZATION_PERCENT / 100.0 else 1.0) *
            (if (relocationLowerCost30) 1.0 - ScenarioConstants.RELOCATION_EXPENSE_REDUCTION_PERCENT / 100.0 else 1.0) *
            (if (expensesPlus25) 1.0 + ScenarioConstants.EXPENSE_INFLATION_PERCENT / 100.0 else 1.0)
    val beforeReduction = monthlyExpenses * multiplier.coerceAtLeast(0.10)
    return SimulationOngoingExpenses(
        beforeReduction = beforeReduction,
        effective = (beforeReduction - expenseReduction).coerceAtLeast(monthlyExpenses * 0.10)
    )
}
