package com.example.data.repository

import android.util.Log
import com.example.data.db.ScenarioDao
import com.example.data.db.ScenarioEntity
import com.example.data.model.FinancialInputData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class FinanceRepository(private val scenarioDao: ScenarioDao) {

    fun getAllScenarios(): Flow<List<ScenarioEntity>> = scenarioDao.getAllScenarios()
        .catch { e ->
            Log.e("FinanceRepository", "Error reading scenarios from database", e)
            emit(emptyList())
        }

    suspend fun saveScenario(name: String, data: FinancialInputData, id: Long = 0): Result<Long> {
        return runCatching {
            val entity = ScenarioEntity.fromInputData(name = name, data = data, id = id)
            scenarioDao.insertScenario(entity)
        }.onFailure { e ->
            Log.e("FinanceRepository", "Failed to save scenario '$name'", e)
        }
    }

    suspend fun deleteScenario(scenario: ScenarioEntity): Result<Unit> {
        return runCatching {
            scenarioDao.deleteScenario(scenario)
        }.onFailure { e ->
            Log.e("FinanceRepository", "Failed to delete scenario id=${scenario.id}", e)
        }
    }

    suspend fun getScenarioCount(): Result<Int> {
        return runCatching {
            scenarioDao.getCount()
        }.onFailure { e ->
            Log.e("FinanceRepository", "Failed to get scenario count", e)
        }
    }
}

