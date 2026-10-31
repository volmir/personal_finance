package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.data.model.TrajectoryPoint
import com.example.data.repository.LocalPreferencesManager
import com.example.domain.FinanceCalculator
import com.example.domain.SimulatorScaleCalculator
import com.example.domain.ScenarioConstants
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.util.localization.AppLanguage
import com.example.util.localization.AppStrings
import com.example.util.localization.getAppStrings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppTab {
    INPUT,
    METRICS,
    SCORES,
    FIRE;

    fun title(strings: AppStrings): String = when (this) {
        INPUT -> strings.tabInputTitle
        METRICS -> strings.tabMetricsTitle
        SCORES -> strings.tabScoresTitle
        FIRE -> strings.tabFireTitle
    }

    fun shortTitle(strings: AppStrings): String = when (this) {
        INPUT -> strings.tabInputShort
        METRICS -> strings.tabMetricsShort
        SCORES -> strings.tabScoresShort
        FIRE -> strings.tabFireShort
    }

    fun localizedTitle(strings: AppStrings): String = title(strings)
    fun localizedShortTitle(strings: AppStrings): String = shortTitle(strings)
}

data class FireScenarioToggles(
    // 2.1. Позитивные события
    val salaryIncrease20: Boolean = false,          // 1. Повышение зарплаты: доход +20% ежемесячно, постоянно
    val marketGrowth25: Boolean = false,            // 2. Рост рынка на 25%: ETF +25%
    val expenseOptimization15: Boolean = false,     // 3. Оптимизация расходов: -15% к расходам постоянно
    val relocationLowerCost30: Boolean = false,     // 4. Переезд в регион с более низкой стоимостью жизни: -30% к расходам постоянно
    val inheritance1200: Boolean = false,           // 5. Наследство или крупный подарок: +1200% месячного дохода
    val propertySale2000: Boolean = false,          // 6. Продажа имущества: +2000% месячного дохода
    val childrenSelfSufficient18: Boolean = false,  // 7. Выход детей на самообеспечение: расходы -25% ежемесячно после 18 лет

    // 2.2. Негативные события (стресс-тесты)
    val jobLoss: Boolean = false,                   // 1. Потеря работы: потеря 12 месяцев дохода из накоплений
    val marketCrash30: Boolean = false,             // 2. Обвал рынка на 30%: ETF -30%
    val mortgage20Years35Income: Boolean = false,   // 3. Ипотека на квартиру: 35% от ежемесячного дохода на период 20 лет
    val carLoan5Years: Boolean = false,             // 4. Автомобиль в кредит: ежемесячные расходы +20% на 5 лет
    val largeOneOffExpense500: Boolean = false,     // 5. Крупная разовая трата: расход 500% мес. дохода (покрытие из активов)
    val expensesPlus25: Boolean = false,           // 6. Рост расходов: +25% к расходам
    val childOrDependent18Years: Boolean = false    // 7. Рождение ребенка: +25% к расходам на период в 18 лет
) {
    val positiveActiveCount: Int
        get() = listOf(
            salaryIncrease20,
            marketGrowth25,
            expenseOptimization15,
            relocationLowerCost30,
            inheritance1200,
            propertySale2000,
            childrenSelfSufficient18
        ).count { it }

    val negativeActiveCount: Int
        get() = listOf(
            jobLoss,
            marketCrash30,
            mortgage20Years35Income,
            carLoan5Years,
            largeOneOffExpense500,
            expensesPlus25,
            childOrDependent18Years
        ).count { it }

    val activeCount: Int
        get() = positiveActiveCount + negativeActiveCount

    val hasActiveScenarios: Boolean
        get() = activeCount > 0
}

data class FireSimulationState(
    val extraMonthlySavings: Double = 0.0,
    val realReturnAdjustment: Double = 0.0,
    val expenseReduction: Double = 0.0,
    val scenarios: FireScenarioToggles = FireScenarioToggles()
) {
    val isSimulating: Boolean
        get() = extraMonthlySavings != 0.0 ||
                realReturnAdjustment != 0.0 ||
                expenseReduction != 0.0 ||
                scenarios.hasActiveScenarios
}

class MainViewModel @JvmOverloads constructor(
    application: Application,
    private val today: LocalDate = LocalDate.now()
) : AndroidViewModel(application) {

    private val localPrefs: LocalPreferencesManager = LocalPreferencesManager(application)
    private var inputDataSaveJob: Job? = null

    private val _currentTab = MutableStateFlow(localPrefs.loadCurrentTab() ?: AppTab.INPUT)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    // Языковая версия: восстанавливается из настроек, либо фоновый выбор при самом первом запуске
    private val _appLanguage = MutableStateFlow(
        localPrefs.loadLanguage() ?: AppLanguage.detectSystemLanguage().also {
            localPrefs.saveLanguage(it)
        }
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun updateLanguageFromSystem(languageCode: String) {
        val language = AppLanguage.fromLocaleCode(languageCode)
        if (_appLanguage.value != language) {
            _appLanguage.value = language
            localPrefs.saveLanguage(language)
        }
    }

    // Загрузка из локального хранилища (дефолтные значения только при первом запуске)
    private val _inputData = MutableStateFlow(localPrefs.loadData())
    val inputData: StateFlow<FinancialInputData> = _inputData.asStateFlow()

    private val _metrics = MutableStateFlow(FinanceCalculator.calculate(_inputData.value, today = today))
    val metrics: StateFlow<CalculatedMetrics> = _metrics.asStateFlow()

    private val _fireSimulation = MutableStateFlow(localPrefs.loadSimulationState())
    val fireSimulation: StateFlow<FireSimulationState> = _fireSimulation.asStateFlow()

    private val _simulatedMetrics = MutableStateFlow(_metrics.value)
    val simulatedMetrics: StateFlow<CalculatedMetrics> = _simulatedMetrics.asStateFlow()

    private val _trajectory = MutableStateFlow<List<TrajectoryPoint>>(emptyList())
    val trajectory: StateFlow<List<TrajectoryPoint>> = _trajectory.asStateFlow()

    private val _simulatedTrajectory = MutableStateFlow<List<TrajectoryPoint>>(emptyList())
    val simulatedTrajectory: StateFlow<List<TrajectoryPoint>> = _simulatedTrajectory.asStateFlow()

    init {
        recalculate()
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        localPrefs.saveCurrentTab(tab)
    }

    fun updateInputData(transform: (FinancialInputData) -> FinancialInputData) {
        val updated = transform(_inputData.value)
        _inputData.value = updated
        val updatedMetrics = FinanceCalculator.calculate(updated, today = today)
        val savingsConfig = SimulatorScaleCalculator.calculateSavingsConfig(
            currentSavings = updatedMetrics.cashFlow,
            monthlyIncome = updated.monthlyIncome,
            monthlyExpenses = updated.monthlyExpenses
        )
        val expensesConfig = SimulatorScaleCalculator.calculateExpensesConfig(
            monthlyExpenses = updated.monthlyExpenses,
            monthlyIncome = updated.monthlyIncome
        )
        val returnConfig = SimulatorScaleCalculator.calculateReturnConfig(updatedMetrics.realReturnPercent)
        val currentSimulation = _fireSimulation.value
        val boundedSimulation = currentSimulation.copy(
            extraMonthlySavings = currentSimulation.extraMonthlySavings.coerceIn(
                savingsConfig.minValue.toDouble(), savingsConfig.maxValue.toDouble()
            ),
            expenseReduction = currentSimulation.expenseReduction.coerceIn(
                expensesConfig.minValue.toDouble(), expensesConfig.maxValue.toDouble()
            ),
            realReturnAdjustment = currentSimulation.realReturnAdjustment.coerceIn(
                returnConfig.minValue.toDouble(), returnConfig.maxValue.toDouble()
            )
        )
        if (boundedSimulation != currentSimulation) {
            _fireSimulation.value = boundedSimulation
            localPrefs.saveSimulationState(boundedSimulation)
        }
        // Debounce disk writes while keeping model and calculations immediately up to date.
        inputDataSaveJob?.cancel()
        inputDataSaveJob = viewModelScope.launch {
            delay(400)
            localPrefs.saveData(_inputData.value)
        }
        // Dynamically recalculate all metrics in real-time
        recalculate()
    }

    fun updateSimulation(extraSavings: Double, realReturnDelta: Double, expReduction: Double) {
        _fireSimulation.value = _fireSimulation.value.copy(
            extraMonthlySavings = extraSavings,
            realReturnAdjustment = realReturnDelta,
            expenseReduction = expReduction
        )
        localPrefs.saveSimulationState(_fireSimulation.value)
        recalculateSimulation()
    }

    fun toggleScenario(scenarioId: String) {
        val current = _fireSimulation.value.scenarios
        val updated = when (scenarioId) {
            // 2.1. Позитивные события
            "pos_salary_increase" -> current.copy(salaryIncrease20 = !current.salaryIncrease20)
            "pos_market_growth" -> current.copy(marketGrowth25 = !current.marketGrowth25)
            "pos_expense_optimization" -> current.copy(expenseOptimization15 = !current.expenseOptimization15)
            "pos_relocation" -> current.copy(relocationLowerCost30 = !current.relocationLowerCost30)
            "pos_inheritance" -> current.copy(inheritance1200 = !current.inheritance1200)
            "pos_property_sale", "property_sale" -> current.copy(propertySale2000 = !current.propertySale2000)
            "pos_children_self_sufficient" -> current.copy(childrenSelfSufficient18 = !current.childrenSelfSufficient18)

            // 2.2. Негативные события
            "job_loss" -> current.copy(jobLoss = !current.jobLoss)
            "market_crash" -> current.copy(marketCrash30 = !current.marketCrash30)
            "mortgage" -> current.copy(mortgage20Years35Income = !current.mortgage20Years35Income)
            "car_loan" -> current.copy(carLoan5Years = !current.carLoan5Years)
            "large_expense" -> current.copy(largeOneOffExpense500 = !current.largeOneOffExpense500)
            "inflation" -> current.copy(expensesPlus25 = !current.expensesPlus25)
            "child_dependent" -> current.copy(childOrDependent18Years = !current.childOrDependent18Years)
            else -> current
        }
        _fireSimulation.value = _fireSimulation.value.copy(scenarios = updated)
        localPrefs.saveSimulationState(_fireSimulation.value)
        recalculateSimulation()
    }

    fun resetSimulation() {
        val resetState = FireSimulationState()
        _fireSimulation.value = resetState
        localPrefs.saveSimulationState(resetState)
        recalculateSimulation()
    }

    private fun recalculate() {
        val currentInput = _inputData.value
        val calc = FinanceCalculator.calculate(currentInput, today = today)
        _metrics.value = calc
        _trajectory.value = FinanceCalculator.generateTrajectory(calc, currentInput, today = today)
        recalculateSimulation()
    }

    private fun recalculateSimulation() {
        val baseInput = _inputData.value
        val sim = _fireSimulation.value
        val sc = sim.scenarios

        // 1. Доход:
        // Базовый доход с учетом "Повышение зарплаты" (+20% постоянно)
        val incomeMultiplier = 1.0 + (if (sc.salaryIncrease20) ScenarioConstants.SALARY_INCREASE_PERCENT / 100.0 else 0.0)
        // Потеря работы моделируется разовым изъятием накоплений, доход сохраняется.
        val baseIncome = baseInput.monthlyIncome * incomeMultiplier

        // 2. Рынок и финансовые активы:
        // Негативное: Обвал рынка на 30% (-30%)
        // Позитивное: Рост рынка на 25% (+25%)
        val marketMultiplier =
            (if (sc.marketCrash30) 1.0 - ScenarioConstants.MARKET_CRASH_PERCENT / 100.0 else 1.0) *
                (if (sc.marketGrowth25) 1.0 + ScenarioConstants.MARKET_GROWTH_PERCENT / 100.0 else 1.0)
        val afterMarketFinAssets = (baseInput.financialAssets * marketMultiplier.coerceAtLeast(0.0))

        // 3. Разовые поступления капитала (Позитивные события):
        // 5. Наследство или крупный подарок: +1200% месячного дохода (12 мес.)
        val inheritanceAmount = if (sc.inheritance1200) ScenarioConstants.INHERITANCE_PERCENT_OF_MONTHLY_INCOME / 100.0 * baseInput.monthlyIncome else 0.0
        // 6. Продажа имущества: min(2000% месячного дохода, имеющееся имущество)
        val propertySaleAmount = if (sc.propertySale2000) minOf(ScenarioConstants.PROPERTY_SALE_PERCENT_OF_MONTHLY_INCOME / 100.0 * baseInput.monthlyIncome, baseInput.propertyAssets) else 0.0
        val effectivePropertyAssets = baseInput.propertyAssets - propertySaleAmount
        val totalPositiveLumpSum = inheritanceAmount + propertySaleAmount

        // 4. Постоянные ежемесячные расходы:
        // Позитивное: Оптимизация расходов (-15%)
        // Позитивное: Переезд в регион с более низкой стоимостью (-30%)
        val ongoingExpenseMultiplier =
            (if (sc.expenseOptimization15) 1.0 - ScenarioConstants.EXPENSE_OPTIMIZATION_PERCENT / 100.0 else 1.0) *
                (if (sc.relocationLowerCost30) 1.0 - ScenarioConstants.RELOCATION_EXPENSE_REDUCTION_PERCENT / 100.0 else 1.0) *
                (if (sc.expensesPlus25) 1.0 + ScenarioConstants.EXPENSE_INFLATION_PERCENT / 100.0 else 1.0)
        val baseOngoingExpenses = baseInput.monthlyExpenses * ongoingExpenseMultiplier.coerceAtLeast(0.10)

        // Влияние ползунка сокращения/роста расходов (sim.expenseReduction)
        val minimumOngoingExpenses = baseInput.monthlyExpenses * 0.10
        val effectiveOngoingExpenses = (baseOngoingExpenses - sim.expenseReduction)
            .coerceAtLeast(minimumOngoingExpenses)

        // 5. Временные ежемесячные расходы:
        // Негативное 3. Ипотека на квартиру: 35% от ежемесячного дохода на период 20 лет
        val mortgageMonthlyExpense = if (sc.mortgage20Years35Income) ScenarioConstants.MORTGAGE_INCOME_PERCENT / 100.0 * baseIncome else 0.0

        // Негативное 4. Автомобиль в кредит: 20% от ежемесячного дохода на 5 лет
        val carLoanMonthlyExpense = if (sc.carLoan5Years) ScenarioConstants.CAR_LOAN_INCOME_PERCENT / 100.0 * baseIncome else 0.0

        // Негативное 7. Рождение ребенка: +25% к базовым расходам на 18 лет
        val childMonthlyExpense = if (sc.childOrDependent18Years) ScenarioConstants.CHILD_DEPENDENT_EXPENSE_PERCENT / 100.0 * baseInput.monthlyExpenses else 0.0

        // Текущие совокупные ежемесячные расходы на момент t = 0 (включая временные платежи)
        val currentEffectiveExpenses = effectiveOngoingExpenses + mortgageMonthlyExpense + carLoanMonthlyExpense + childMonthlyExpense

        // 6. Отложенное снижение расходов:
        // Позитивное 7. Выход детей на самообеспечение: расходы -25% ежемесячно после 18 лет
        val deferredReductionAnnual = if (sc.childrenSelfSufficient18) {
            minOf(ScenarioConstants.CHILDREN_EXPENSE_REDUCTION_PERCENT / 100.0 * baseOngoingExpenses * 12.0, effectiveOngoingExpenses * 12.0)
        } else 0.0

        val temporaryExpenses = mutableListOf<com.example.domain.TemporaryExpense>()
        if (sc.mortgage20Years35Income) {
            temporaryExpenses.add(
                com.example.domain.TemporaryExpense(
                    annualAmount = mortgageMonthlyExpense * 12.0,
                    durationYears = ScenarioConstants.MORTGAGE_DURATION_YEARS
                )
            )
        }
        if (sc.carLoan5Years) {
            temporaryExpenses.add(
                com.example.domain.TemporaryExpense(
                    annualAmount = carLoanMonthlyExpense * 12.0,
                    durationYears = ScenarioConstants.CAR_LOAN_DURATION_YEARS
                )
            )
        }
        if (sc.childOrDependent18Years) {
            temporaryExpenses.add(
                com.example.domain.TemporaryExpense(
                    annualAmount = childMonthlyExpense * 12.0,
                    durationYears = ScenarioConstants.CHILD_DEPENDENT_DURATION_YEARS
                )
            )
        }

        // 7. Крупная разовая трата: 500% месячного дохода, покрытие из ликвидных и финансовых активов
        // Стартовые ликвидные активы пополняются разовыми поступлениями (наследство, продажа имущества)
        var effLiquidAssets = baseInput.liquidAssets + totalPositiveLumpSum
        var effFinAssets = afterMarketFinAssets
        var uncoveredExpenseDebt = 0.0

        if (sc.largeOneOffExpense500 || sc.jobLoss) {
            val expenseTarget =
                (if (sc.largeOneOffExpense500) ScenarioConstants.LARGE_ONE_OFF_EXPENSE_PERCENT / 100.0 else 0.0) * baseInput.monthlyIncome +
                    (if (sc.jobLoss) ScenarioConstants.JOB_LOSS_MONTHS else 0.0) * baseInput.monthlyIncome
            var remainingToCover = expenseTarget

            val coveredFromLiquid = minOf(effLiquidAssets, remainingToCover)
            effLiquidAssets -= coveredFromLiquid
            remainingToCover -= coveredFromLiquid

            val coveredFromFin = minOf(effFinAssets, remainingToCover)
            effFinAssets -= coveredFromFin
            remainingToCover -= coveredFromFin

            uncoveredExpenseDebt = remainingToCover
        }

        // 8. Долги: непокрытая часть разовой траты (ипотека учитывается как аннуитетный платеж 35% дохода на 20 лет)
        val effectiveLiabilities = baseInput.liabilities + uncoveredExpenseDebt

        // 9. Реальная доходность капитала (3-й ползунок).
        val effectiveInflationPercent = baseInput.inflationPercent
        val effInfl = effectiveInflationPercent / 100.0
        val baseNominal = baseInput.nominalReturnPercent / 100.0

        // Базовая реальная доходность с учетом заданной пользователем инфляции.
        val baseRealReturn = if (effInfl != -1.0) {
            ((1.0 + baseNominal) / (1.0 + effInfl) - 1.0)
        } else 0.0

        // Целевая реальная доходность с учетом ползунка настройки realReturnAdjustment
        val targetRealReturn = baseRealReturn + (sim.realReturnAdjustment / 100.0)
        val effectiveNominal = if (effInfl != -1.0) {
            ((1.0 + targetRealReturn) * (1.0 + effInfl) - 1.0) * 100.0
        } else {
            baseInput.nominalReturnPercent + sim.realReturnAdjustment
        }

        val simulatedInput = baseInput.copy(
            monthlyIncome = baseIncome,
            monthlyExpenses = currentEffectiveExpenses,
            liquidAssets = effLiquidAssets,
            financialAssets = effFinAssets,
            propertyAssets = effectivePropertyAssets,
            liabilities = effectiveLiabilities,
            inflationPercent = effectiveInflationPercent,
            nominalReturnPercent = effectiveNominal
        )

        val simCalc = FinanceCalculator.calculate(
            input = simulatedInput,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredReductionAnnual,
            deferredStartYears = ScenarioConstants.CHILDREN_SELF_SUFFICIENT_YEARS,
            extraMonthlyCashFlow = sim.extraMonthlySavings,
            today = today
        )
        _simulatedMetrics.value = simCalc
        _simulatedTrajectory.value = FinanceCalculator.generateTrajectory(
            metrics = simCalc,
            input = simulatedInput,
            temporaryExpenses = temporaryExpenses,
            deferredAnnualExpenseReduction = deferredReductionAnnual,
            deferredStartYears = ScenarioConstants.CHILDREN_SELF_SUFFICIENT_YEARS,
            extraMonthlyCashFlow = sim.extraMonthlySavings,
            today = today
        )
    }

}
