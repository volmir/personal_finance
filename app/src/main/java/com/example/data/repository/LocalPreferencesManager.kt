package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.FinancialInputData

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
        private const val KEY_CURRENCY = "currency"
        private const val KEY_INCLUDE_FINANCIAL_EMERGENCY = "include_financial_emergency"
    }

    fun loadData(): FinancialInputData {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            // First run: return Russian average data
            return FinancialInputData.russiaAverageData()
        }

        return FinancialInputData(
            monthlyIncome = prefs.getString(KEY_MONTHLY_INCOME, "85000.0")?.toDoubleOrNull() ?: 85000.0,
            monthlyExpenses = prefs.getString(KEY_MONTHLY_EXPENSES, "68000.0")?.toDoubleOrNull() ?: 68000.0,
            liquidAssets = prefs.getString(KEY_LIQUID_ASSETS, "200000.0")?.toDoubleOrNull() ?: 200000.0,
            financialAssets = prefs.getString(KEY_FINANCIAL_ASSETS, "450000.0")?.toDoubleOrNull() ?: 450000.0,
            propertyAssets = prefs.getString(KEY_PROPERTY_ASSETS, "3800000.0")?.toDoubleOrNull() ?: 3800000.0,
            liabilities = prefs.getString(KEY_LIABILITIES, "480000.0")?.toDoubleOrNull() ?: 480000.0,
            birthDate = prefs.getString(KEY_BIRTH_DATE, "1985-06-15") ?: "1985-06-15",
            lifeExpectancy = prefs.getInt(KEY_LIFE_EXPECTANCY, 78),
            nominalReturnPercent = prefs.getString(KEY_NOMINAL_RETURN, "4.0")?.toDoubleOrNull() ?: 4.0,
            inflationPercent = prefs.getString(KEY_INFLATION, "2.0")?.toDoubleOrNull() ?: 2.0,
            currencySymbol = prefs.getString(KEY_CURRENCY, "₽") ?: "₽",
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
            putString(KEY_CURRENCY, data.currencySymbol)
            putBoolean(KEY_INCLUDE_FINANCIAL_EMERGENCY, data.includeFinancialInEmergencyFund)
            apply()
        }
    }

    fun clearData() {
        prefs.edit().clear().apply()
    }
}
