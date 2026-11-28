package com.example.domain.model

import com.example.domain.FinancialMetricEvaluator

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

/**
 * Причина ограничения итогового балла сверху. Набор пуст, если cap не изменил
 * значение: условия не выполнены либо исходный балл не превышает предел.
 */
enum class FinancialAssessmentCapReason {
    NEGATIVE_NET_WORTH,
    LIABILITIES_WITHOUT_ASSETS
}

/** Почему итоговый балл недоступен. */
enum class FinancialAssessmentUnavailableReason {
    ALL_ZERO_INPUT,
    NO_AVAILABLE_GROUPS
}

/**
 * Балл группы без предварительного округления. null означает, что вся группа
 * недоступна; ноль остаётся валидным критическим баллом.
 */
data class FinancialAssessmentGroupScore(
    val group: FinancialAssessmentGroup,
    val score: Double?
) {
    init {
        require(score == null || (score.isFinite() && score in 0.0..10.0)) {
            "Group score must be null or finite between 0 and 10"
        }
    }

    val isAvailable: Boolean get() = score != null
}

/**
 * Неизменяемый результат комплексной оценки: девять компонентов, четыре группы,
 * покрытие и итог. [rawScore] — неокруглённый балл до cap, [totalScore] — итог
 * после cap и округления HALF_UP до одного знака. При недоступности оба равны
 * null, а [unavailableReason] объясняет причину; подставной ноль не используется.
 */
data class FinancialAssessmentResult(
    val components: FinancialAssessmentComponents,
    val groups: List<FinancialAssessmentGroupScore>,
    val rawScore: Double?,
    val totalScore: Double?,
    val coveragePercent: Int,
    val capReasons: Set<FinancialAssessmentCapReason>,
    val unavailableReason: FinancialAssessmentUnavailableReason?
) {
    init {
        require(groups.map { it.group } == FinancialAssessmentGroup.values().toList()) {
            "Groups must contain every assessment group exactly once in catalog order"
        }
        require(coveragePercent in 0..100) { "Coverage must be between 0 and 100" }
        require((totalScore == null) == (rawScore == null)) {
            "Raw and total scores must be available together"
        }
        require((totalScore == null) == (unavailableReason != null)) {
            "Unavailable total must have exactly one reason"
        }
        require(totalScore == null || (totalScore.isFinite() && totalScore in 0.0..10.0)) {
            "Total score must be null or finite between 0 and 10"
        }
        require(rawScore == null || (rawScore.isFinite() && rawScore in 0.0..10.0)) {
            "Raw score must be null or finite between 0 and 10"
        }
        require(capReasons.isEmpty() || totalScore != null) {
            "Cap reason requires an available total"
        }
    }

    private val byGroup: Map<FinancialAssessmentGroup, FinancialAssessmentGroupScore> =
        groups.associateBy { it.group }

    operator fun get(group: FinancialAssessmentGroup): FinancialAssessmentGroupScore =
        byGroup.getValue(group)

    val isAvailable: Boolean get() = totalScore != null
    val isComplete: Boolean get() = coveragePercent == 100
    val isIncomplete: Boolean get() = !isComplete
    val isCapped: Boolean get() = capReasons.isNotEmpty()

    /** Статус считается только по округлённому итогу; null не передаётся в evaluator. */
    val overallStatus: AuditIndicatorStatus?
        get() = totalScore?.let { FinancialMetricEvaluator.getTotalScoreStatus(it) }

    /** Верхняя зелёная ступень [FinancialMetricEvaluator.TOTAL_SCORE_EXCELLENT_MIN]. */
    val isExcellent: Boolean
        get() = totalScore != null && totalScore >= FinancialMetricEvaluator.TOTAL_SCORE_EXCELLENT_MIN
}
