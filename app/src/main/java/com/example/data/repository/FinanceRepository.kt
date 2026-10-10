package com.example.data.repository

import com.example.data.db.ScenarioDao
import com.example.data.db.ScenarioEntity
import com.example.data.model.FinancialInputData
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val scenarioDao: ScenarioDao) {

    fun getAllScenarios(): Flow<List<ScenarioEntity>> = scenarioDao.getAllScenarios()

    suspend fun saveScenario(name: String, data: FinancialInputData, id: Long = 0): Long {
        val entity = ScenarioEntity.fromInputData(name = name, data = data, id = id)
        return scenarioDao.insertScenario(entity)
    }

    suspend fun deleteScenario(scenario: ScenarioEntity) {
        scenarioDao.deleteScenario(scenario)
    }

    suspend fun getScenarioCount(): Int = scenarioDao.getCount()
}
