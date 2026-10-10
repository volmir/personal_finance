package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.FinancialInputData

@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val liquidAssets: Double,
    val financialAssets: Double,
    val propertyAssets: Double,
    val liabilities: Double,
    val birthDate: String,
    val lifeExpectancy: Int,
    val nominalReturnPercent: Double,
    val inflationPercent: Double,
    val currencySymbol: String,
    val includeFinancialInEmergencyFund: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toInputData(): FinancialInputData = FinancialInputData(
        monthlyIncome = monthlyIncome,
        monthlyExpenses = monthlyExpenses,
        liquidAssets = liquidAssets,
        financialAssets = financialAssets,
        propertyAssets = propertyAssets,
        liabilities = liabilities,
        birthDate = birthDate,
        lifeExpectancy = lifeExpectancy,
        nominalReturnPercent = nominalReturnPercent,
        inflationPercent = inflationPercent,
        currencySymbol = currencySymbol,
        includeFinancialInEmergencyFund = includeFinancialInEmergencyFund
    )

    companion object {
        fun fromInputData(name: String, data: FinancialInputData, id: Long = 0): ScenarioEntity =
            ScenarioEntity(
                id = id,
                name = name,
                monthlyIncome = data.monthlyIncome,
                monthlyExpenses = data.monthlyExpenses,
                liquidAssets = data.liquidAssets,
                financialAssets = data.financialAssets,
                propertyAssets = data.propertyAssets,
                liabilities = data.liabilities,
                birthDate = data.birthDate,
                lifeExpectancy = data.lifeExpectancy,
                nominalReturnPercent = data.nominalReturnPercent,
                inflationPercent = data.inflationPercent,
                currencySymbol = data.currencySymbol,
                includeFinancialInEmergencyFund = data.includeFinancialInEmergencyFund
            )
    }
}
