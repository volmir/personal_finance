package com.example.domain.model

/**
 * Четыре взвешенные группы комплексной оценки. Веса заданы в процентах
 * и не настраиваются пользователем.
 */
enum class FinancialAssessmentGroup(val weightPercent: Int) {
    BUDGET(25),
    RESERVE(25),
    DEBT(15),
    CAPITAL(35)
}

/**
 * Единственный каталог девяти стабильных идентификаторов оценки.
 * Здесь же хранятся группа и вес показателя, чтобы числа не дублировались
 * в калькуляторе, UI и экспортах.
 *
 * [REAL_RETURN] — параметр прогноза: вес 0, отдельной группы нет,
 * в агрегацию он не входит.
 */
enum class FinancialAssessmentId(
    val stableId: String,
    val group: FinancialAssessmentGroup?,
    val weightPercent: Int,
    val isForecastOnly: Boolean = false
) {
    CASH_FLOW("cash_flow", FinancialAssessmentGroup.BUDGET, 5),
    SAVINGS_RATE("savings_rate", FinancialAssessmentGroup.BUDGET, 20),
    EMERGENCY_FUND("emergency_fund", FinancialAssessmentGroup.RESERVE, 5),
    SAFETY_MONTHS("safety_months", FinancialAssessmentGroup.RESERVE, 20),
    DEBT_RATIO("debt_ratio", FinancialAssessmentGroup.DEBT, 10),
    NET_WORTH("net_worth", FinancialAssessmentGroup.DEBT, 5),
    FIDELITY("fidelity", FinancialAssessmentGroup.CAPITAL, 10),
    CFS("cfs", FinancialAssessmentGroup.CAPITAL, 25),
    REAL_RETURN("real_return", null, 0, isForecastOnly = true);

    init {
        require(stableId.isNotBlank()) { "Stable id must not be blank" }
        require(weightPercent in 0..100) { "Weight must be between 0 and 100" }
        require(isForecastOnly == (weightPercent == 0)) { "Only forecast parameters have zero weight" }
        require((group == null) == isForecastOnly) { "Only forecast parameters are outside weighted groups" }
    }
}

/**
 * Индивидуальный балл показателя. null означает недоступность и не равен нулю;
 * ноль — валидный критический балл. Признак прогноза берётся из каталога.
 */
data class FinancialAssessmentComponent(
    val id: FinancialAssessmentId,
    val score: Int?
) {
    init {
        require(score == null || score in 0..10) { "Component score must be null or between 0 and 10" }
    }

    val group: FinancialAssessmentGroup? get() = id.group
    val weightPercent: Int get() = id.weightPercent
    val isAvailable: Boolean get() = score != null
    val isForecastOnly: Boolean get() = id.isForecastOnly
}

/**
 * Набор из девяти компонентов в порядке каталога. Ровно одна запись на ID;
 * порядок детерминирован и проверяется при создании.
 */
data class FinancialAssessmentComponents(
    val components: List<FinancialAssessmentComponent>
) {
    init {
        require(components.map { it.id } == FinancialAssessmentId.values().toList()) {
            "Components must contain every assessment id exactly once in catalog order"
        }
    }

    private val byId: Map<FinancialAssessmentId, FinancialAssessmentComponent> =
        components.associateBy { it.id }

    operator fun get(id: FinancialAssessmentId): FinancialAssessmentComponent = byId.getValue(id)

    fun scoreOf(id: FinancialAssessmentId): Int? = get(id).score

    companion object {
        fun of(scores: Map<FinancialAssessmentId, Int?>): FinancialAssessmentComponents =
            FinancialAssessmentComponents(
                FinancialAssessmentId.values().map { id ->
                    FinancialAssessmentComponent(id, scores[id])
                }
            )
    }
}
