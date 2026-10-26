package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.FinancialInputData
import com.example.ui.AppTab
import com.example.ui.FireScenarioToggles
import com.example.ui.FireSimulationState
import com.example.util.localization.AppLanguage

class LocalPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_INITIALIZED = "initialized"
        private const val KEY_MONTHLY_INCOME = "monthly_income"
        private const val KEY_MONTHLY_EXPENSES = "monthly_expenses"
        private const val KEY_LIQUID_ASSETS = "liquid_assets"
        private const val KEY_FINANCIAL_ASSETS = "financial_assets"
        private const val KEY_PROPERTY_ASSETS = "property_assets"
        private const val KEY_LIABILITIES = "liabilities"
        private const val KEY_BIRTH_DATE = "birth_date"
        private const val KEY_LIFE_EXPECTANCY = "life_expectancy"
        private const val KEY_NOMINAL_RETURN = "nominal_return"
        private const val KEY_INFLATION = "inflation"
        private const val KEY_CAPITAL_GAINS_TAX = "capital_gains_tax"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_INCLUDE_FINANCIAL_EMERGENCY = "include_financial_emergency"

        // Settings & Options
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_CURRENT_TAB = "current_tab"

        // Simulator sliders
        private const val KEY_SIM_EXTRA_SAVINGS = "sim_extra_savings"
        private const val KEY_SIM_RETURN_ADJUSTMENT = "sim_return_adjustment"
        private const val KEY_SIM_EXPENSE_REDUCTION = "sim_expense_reduction"

        // Simulator positive scenarios
        private const val KEY_SC_SALARY_INCREASE = "sc_salary_increase"
        private const val KEY_SC_MARKET_GROWTH = "sc_market_growth"
        private const val KEY_SC_EXPENSE_OPTIMIZATION = "sc_expense_optimization"
        private const val KEY_SC_RELOCATION = "sc_relocation"
        private const val KEY_SC_INHERITANCE = "sc_inheritance"
        private const val KEY_SC_PROPERTY_SALE = "sc_property_sale"
        private const val KEY_SC_CHILDREN_SELF_SUFFICIENT = "sc_children_self_sufficient"

        // Simulator negative scenarios
        private const val KEY_SC_JOB_LOSS = "sc_job_loss"
        private const val KEY_SC_MARKET_CRASH = "sc_market_crash"
        private const val KEY_SC_MORTGAGE = "sc_mortgage"
        private const val KEY_SC_CAR_LOAN = "sc_car_loan"
        private const val KEY_SC_LARGE_EXPENSE = "sc_large_expense"
        private const val KEY_SC_INFLATION = "sc_inflation"
        private const val KEY_SC_CHILD_DEPENDENT = "sc_child_dependent"
    }

    fun isFirstLaunch(): Boolean = !prefs.getBoolean(KEY_INITIALIZED, false)

    fun loadData(): FinancialInputData {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            // First run: assign and persist default US average data
            val defaultData = FinancialInputData.usAverageData()
            saveData(defaultData)
            return defaultData
        }

        return FinancialInputData(
            monthlyIncome = prefs.getString(KEY_MONTHLY_INCOME, "6000.0")?.toDoubleOrNull() ?: 6000.0,
            monthlyExpenses = prefs.getString(KEY_MONTHLY_EXPENSES, "4800.0")?.toDoubleOrNull() ?: 4800.0,
            liquidAssets = prefs.getString(KEY_LIQUID_ASSETS, "15000.0")?.toDoubleOrNull() ?: 15000.0,
            financialAssets = prefs.getString(KEY_FINANCIAL_ASSETS, "40000.0")?.toDoubleOrNull() ?: 40000.0,
            propertyAssets = prefs.getString(KEY_PROPERTY_ASSETS, "280000.0")?.toDoubleOrNull() ?: 280000.0,
            liabilities = prefs.getString(KEY_LIABILITIES, "180000.0")?.toDoubleOrNull() ?: 180000.0,
            birthDate = prefs.getString(KEY_BIRTH_DATE, "1988-06-15") ?: "1988-06-15",
            lifeExpectancy = prefs.getInt(KEY_LIFE_EXPECTANCY, 82),
            nominalReturnPercent = prefs.getString(KEY_NOMINAL_RETURN, "7.5")?.toDoubleOrNull() ?: 7.5,
            inflationPercent = prefs.getString(KEY_INFLATION, "2.5")?.toDoubleOrNull() ?: 2.5,
            capitalGainsTaxPercent = prefs.getString(KEY_CAPITAL_GAINS_TAX, "20.0")?.toDoubleOrNull() ?: 20.0,
            currencySymbol = prefs.getString(KEY_CURRENCY, "$") ?: "$",
            includeFinancialInEmergencyFund = prefs.getBoolean(KEY_INCLUDE_FINANCIAL_EMERGENCY, true)
        )
    }

    fun saveData(data: FinancialInputData) {
        prefs.edit().apply {
            putBoolean(KEY_INITIALIZED, true)
            putString(KEY_MONTHLY_INCOME, data.monthlyIncome.toString())
            putString(KEY_MONTHLY_EXPENSES, data.monthlyExpenses.toString())
            putString(KEY_LIQUID_ASSETS, data.liquidAssets.toString())
            putString(KEY_FINANCIAL_ASSETS, data.financialAssets.toString())
            putString(KEY_PROPERTY_ASSETS, data.propertyAssets.toString())
            putString(KEY_LIABILITIES, data.liabilities.toString())
            putString(KEY_BIRTH_DATE, data.birthDate)
            putInt(KEY_LIFE_EXPECTANCY, data.lifeExpectancy)
            putString(KEY_NOMINAL_RETURN, data.nominalReturnPercent.toString())
            putString(KEY_INFLATION, data.inflationPercent.toString())
            putString(KEY_CAPITAL_GAINS_TAX, data.capitalGainsTaxPercent.toString())
            putString(KEY_CURRENCY, data.currencySymbol)
            putBoolean(KEY_INCLUDE_FINANCIAL_EMERGENCY, data.includeFinancialInEmergencyFund)
            apply()
        }
    }

    fun loadLanguage(): AppLanguage? {
        val code = prefs.getString(KEY_LANGUAGE, null) ?: return null
        return try {
            AppLanguage.valueOf(code)
        } catch (_: Exception) {
            null
        }
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun loadCurrentTab(): AppTab? {
        val name = prefs.getString(KEY_CURRENT_TAB, null) ?: return null
        return try {
            AppTab.valueOf(name)
        } catch (_: Exception) {
            null
        }
    }

    fun saveCurrentTab(tab: AppTab) {
        prefs.edit().putString(KEY_CURRENT_TAB, tab.name).apply()
    }

    fun loadSimulationState(): FireSimulationState {
        val scenarios = FireScenarioToggles(
            salaryIncrease20 = prefs.getBoolean(KEY_SC_SALARY_INCREASE, false),
            marketGrowth25 = prefs.getBoolean(KEY_SC_MARKET_GROWTH, false),
            expenseOptimization15 = prefs.getBoolean(KEY_SC_EXPENSE_OPTIMIZATION, false),
            relocationLowerCost30 = prefs.getBoolean(KEY_SC_RELOCATION, false),
            inheritance1200 = prefs.getBoolean(KEY_SC_INHERITANCE, false),
            propertySale2000 = prefs.getBoolean(KEY_SC_PROPERTY_SALE, false),
            childrenSelfSufficient18 = prefs.getBoolean(KEY_SC_CHILDREN_SELF_SUFFICIENT, false),
            jobLoss = prefs.getBoolean(KEY_SC_JOB_LOSS, false),
            marketCrash30 = prefs.getBoolean(KEY_SC_MARKET_CRASH, false),
            mortgage20Years35Income = prefs.getBoolean(KEY_SC_MORTGAGE, false),
            carLoan5Years = prefs.getBoolean(KEY_SC_CAR_LOAN, false),
            largeOneOffExpense500 = prefs.getBoolean(KEY_SC_LARGE_EXPENSE, false),
            inflation5Percent = prefs.getBoolean(KEY_SC_INFLATION, false),
            childOrDependent18Years = prefs.getBoolean(KEY_SC_CHILD_DEPENDENT, false)
        )
        return FireSimulationState(
            extraMonthlySavings = prefs.getString(KEY_SIM_EXTRA_SAVINGS, "0.0")?.toDoubleOrNull() ?: 0.0,
            realReturnAdjustment = prefs.getString(KEY_SIM_RETURN_ADJUSTMENT, "0.0")?.toDoubleOrNull() ?: 0.0,
            expenseReduction = prefs.getString(KEY_SIM_EXPENSE_REDUCTION, "0.0")?.toDoubleOrNull() ?: 0.0,
            scenarios = scenarios
        )
    }

    fun saveSimulationState(state: FireSimulationState) {
        prefs.edit().apply {
            putString(KEY_SIM_EXTRA_SAVINGS, state.extraMonthlySavings.toString())
            putString(KEY_SIM_RETURN_ADJUSTMENT, state.realReturnAdjustment.toString())
            putString(KEY_SIM_EXPENSE_REDUCTION, state.expenseReduction.toString())

            val sc = state.scenarios
            putBoolean(KEY_SC_SALARY_INCREASE, sc.salaryIncrease20)
            putBoolean(KEY_SC_MARKET_GROWTH, sc.marketGrowth25)
            putBoolean(KEY_SC_EXPENSE_OPTIMIZATION, sc.expenseOptimization15)
            putBoolean(KEY_SC_RELOCATION, sc.relocationLowerCost30)
            putBoolean(KEY_SC_INHERITANCE, sc.inheritance1200)
            putBoolean(KEY_SC_PROPERTY_SALE, sc.propertySale2000)
            putBoolean(KEY_SC_CHILDREN_SELF_SUFFICIENT, sc.childrenSelfSufficient18)

            putBoolean(KEY_SC_JOB_LOSS, sc.jobLoss)
            putBoolean(KEY_SC_MARKET_CRASH, sc.marketCrash30)
            putBoolean(KEY_SC_MORTGAGE, sc.mortgage20Years35Income)
            putBoolean(KEY_SC_CAR_LOAN, sc.carLoan5Years)
            putBoolean(KEY_SC_LARGE_EXPENSE, sc.largeOneOffExpense500)
            putBoolean(KEY_SC_INFLATION, sc.inflation5Percent)
            putBoolean(KEY_SC_CHILD_DEPENDENT, sc.childOrDependent18Years)
            apply()
        }
    }

    fun clearData() {
        val empty = FinancialInputData(
            monthlyIncome = 0.0,
            monthlyExpenses = 0.0,
            liquidAssets = 0.0,
            financialAssets = 0.0,
            propertyAssets = 0.0,
            liabilities = 0.0,
            birthDate = "1990-01-01",
            lifeExpectancy = 82,
            nominalReturnPercent = 7.5,
            inflationPercent = 2.5,
            capitalGainsTaxPercent = 20.0,
            currencySymbol = "$",
            includeFinancialInEmergencyFund = true
        )
        saveData(empty)
        saveSimulationState(FireSimulationState())
    }
}
