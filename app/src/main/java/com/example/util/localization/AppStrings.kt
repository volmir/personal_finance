package com.example.util.localization

import androidx.compose.runtime.compositionLocalOf

data class BreakdownCategory(
    val title: String,
    val items: List<String>
)

data class AppStrings(
    val language: AppLanguage,

    // App & Navigation
    val appName: String,
    val tabInputTitle: String,
    val tabInputShort: String,
    val tabMetricsTitle: String,
    val tabMetricsShort: String,
    val tabScoresTitle: String,
    val tabScoresShort: String,
    val tabFireTitle: String,
    val tabFireShort: String,
    val quickScorePrefix: String,

    // Input Screen
    val block1Title: String,
    val block1Subtitle: String,
    val monthlyIncomeLabel: String,
    val monthlyExpensesLabel: String,
    val incomeBreakdownName: String,
    val incomeHintCollapsed: String,
    val incomeHintExpanded: String,
    val expensesBreakdownName: String,
    val expensesHintCollapsed: String,
    val expensesHintExpanded: String,
    val breakdownInstruction: String,
    val incomeCategories: List<BreakdownCategory>,
    val expensesCategories: List<BreakdownCategory>,
    val cashFlowTitle: String,
    val cashFlowFormula: String,
    val savingsRateTitle: String,
    val savingsRateFormula: String,

    val block2Title: String,
    val block2Subtitle: String,
    val liquidAssetsLabel: String,
    val financialAssetsLabel: String,
    val propertyAssetsLabel: String,
    val assetsBreakdownName: String,
    val assetsHintCollapsed: String,
    val assetsHintExpanded: String,
    val assetsCategories: List<BreakdownCategory>,
    val totalAssetsTitle: String,
    val totalAssetsFormula: String,
    val toggleEmergencyFundTitle: String,
    val toggleEmergencyFundSubOn: String,
    val toggleEmergencyFundSubOff: String,

    val block3Title: String,
    val block3Subtitle: String,
    val liabilitiesLabel: String,
    val liabilitiesBreakdownName: String,
    val liabilitiesHintCollapsed: String,
    val liabilitiesHintExpanded: String,
    val liabilitiesCategories: List<BreakdownCategory>,
    val netWorthTitle: String,
    val netWorthFormula: String,

    val block4Title: String,
    val block4Subtitle: String,
    val nominalReturnLabel: String,
    val inflationLabel: String,
    val realReturnTitle: String,
    val realReturnFormula: String,

    val block5Title: String,
    val block5Subtitle: String,
    val birthDateLabel: String,
    val lifeExpectancyLabel: String,
    val horizonTitle: String,
    val horizonFormula: String,
    val horizonValueFormat: (horizon: Int, age: Int) -> String,

    val calculateButtonText: String,
    val exportButtonText: String,
    val exportChooserTitle: String,

    // Metrics Screen
    val metricsHeaderTitle: String,
    val metricsHeaderSubtitle: String,
    val categoryAll: String,
    val categoryCashFlow: String,
    val categoryCapitalRisks: String,
    val categoryBenchmarks: String,
    val metricCardInfo: String,
    val metricCardHide: String,
    val metricCardFormulaPrefix: String,
    val metricCardExpand: String,
    val metricCardCollapse: String,

    val metricCashFlowTitle: String,
    val metricCashFlowFormula: String,
    val metricCashFlowDesc: String,
    val metricCashFlowPositiveSub: String,
    val metricCashFlowNegativeSub: String,

    val metricSavingsRateTitle: String,
    val metricSavingsRateFormula: String,
    val metricSavingsRateDesc: String,
    val metricSavingsRateSub: String,

    val metricEmergencyFundTitle: String,
    val metricEmergencyFundFormula: String,
    val metricEmergencyFundDesc: String,
    val metricEmergencyFundSubLiquidPlusFin: String,
    val metricEmergencyFundSubOnlyLiquid: String,

    val metricSafetyMonthsTitle: String,
    val metricSafetyMonthsFormula: String,
    val metricSafetyMonthsDesc: String,
    val metricSafetyMonthsSub: String,
    val monthsSuffix: String,

    val metricDebtRatioTitle: String,
    val metricDebtRatioFormula: String,
    val metricDebtRatioDesc: String,
    val metricDebtRatioLowSub: String,
    val metricDebtRatioHighSub: String,

    val metricNetWorthTitle: String,
    val metricNetWorthFormula: String,
    val metricNetWorthDesc: String,
    val metricNetWorthSubPrefix: String,
    val metricNetWorthNegativeSub: String,

    val metricFidelityTitle: String,
    val metricFidelityFormula: String,
    val metricFidelityDesc: String,
    val metricFidelitySubFormat: (age: Int, mult: String) -> String,
    val metricFidelityProgress: String,

    val metricCfsTitle: String,
    val metricCfsFormula: String,
    val metricCfsDesc: String,

    val metricRealReturnTitle: String,
    val metricRealReturnFormula: String,
    val metricRealReturnDesc: String,
    val metricRealReturnSubFormat: (nom: String, infl: String) -> String,

    // Scores Screen
    val scoresHeaderTitle: String,
    val scoresHeaderSubtitle: String,
    val scoresShareReportChooser: String,
    val scoresGaugeOutOf: (max: Int) -> String,
    val scoresTotalResult: (score: String) -> String,
    val scoresStatusExcellent: String,
    val scoresStatusGood: String,
    val scoresStatusModerate: String,
    val scoresStatusAttention: String,
    val scoresBreakdownTitle: String,
    val scoreValuePrefix: String,
    val scoreOutOfTen: (score: Int) -> String,
    val savingsRateScoreComment: (rate: Double, sr: String) -> String,
    val safetyMonthsScoreComment: (months: Double, monthsText: String) -> String,
    val debtComment0: String,
    val debtComment1: String,
    val debtComment2: String,
    val debtComment3: String,

    val auditorOpinionTitle: String,
    val auditorOpinionSubtitle: (count: Int) -> String,
    val shareAuditorOpinionChooser: String,
    val positiveBadgeText: (count: Int) -> String,
    val neutralBadgeText: (count: Int) -> String,
    val negativeBadgeText: (count: Int) -> String,
    val positiveSectionTitle: (count: Int) -> String,
    val neutralSectionTitle: (count: Int) -> String,
    val negativeSectionTitle: (count: Int) -> String,
    val positiveEmptyText: String,
    val neutralEmptyText: String,
    val negativeEmptyText: String,

    // FIRE Screen
    val fireHeroTitle: String,
    val fireHeroSubtitle: String,
    val fireTimeToRetire: String,
    val fireYearsSuffix: String,
    val fireHeroAssumption: String,
    val fireRetirementAgeLabel: String,
    val fireRetirementAgeValue: (age: String) -> String,
    val fireTargetCapitalLabel: String,
    val fireParamsTitle: String,
    val fireBirthDateLabel: String,
    val fireCurrentAgeLabel: String,
    val fireCurrentAgeValue: (age: Int) -> String,
    val fireLifeExpectancyLabel: String,
    val fireLifeExpectancyValue: (exp: Int) -> String,
    val fireHorizonLabel: String,
    val fireHorizonValue: (h: Int) -> String,
    val fireRealReturnLabel: String,
    val fireTrajectoryTitle: String,
    val fireTrajectorySubtitle: String,
    val fireChartAccumulated: String,
    val fireChartRequired: String,
    val fireChartYearsSuffix: String,
    val fireChartFirePoint: (age: String) -> String,
    val fireSimSummaryTitle: String,
    val fireSimEarlier: (diff: String, target: String, age: String) -> String,
    val fireSimLater: (diff: String, target: String, age: String) -> String,
    val fireSimSame: (target: String) -> String,
    val fireSimCashFlowRow: String,
    val fireSimExpensesRow: String,
    val fireSimNetWorthRow: String,
    val fireSimPropertyRow: String,
    val fireSimSliderTitle: String,
    val fireSimReset: String,
    val fireSimFineTuning: String,
    val fireSliderSavings: String,
    val fireSliderExpenses: String,
    val fireSliderReturn: String,
    val fireSliderBaseSuffix: String,
    val fireSliderBaseReturnSuffix: String,
    val fireSliderFrom: (v: String) -> String,
    val fireSliderStep: (v: String) -> String,
    val fireSliderTo: (v: String) -> String,
    val fireSliderInc: String,
    val fireSliderDec: String,

    // Scenario Panel
    val scenarioHeaderTitle: String,
    val scenarioHeaderSubtitle: String,
    val scenarioTotalBadge: (count: Int) -> String,
    val scenarioPositiveTitle: String,
    val scenarioPositiveActive: (count: Int) -> String,
    val scenarioPositiveSubtitle: String,
    val scenarioNegativeTitle: String,
    val scenarioNegativeActive: (count: Int) -> String,
    val scenarioNegativeSubtitle: String,
    val scenarioParamsPrefix: String,

    // Scenarios Items (titles, parameters, descriptions)
    val scenarioSalaryIncreaseTitle: String,
    val scenarioSalaryIncreaseParam: String,
    val scenarioSalaryIncreaseDesc: (amt: String) -> String,

    val scenarioMarketGrowthTitle: String,
    val scenarioMarketGrowthParam: String,
    val scenarioMarketGrowthDesc: (amt: String) -> String,

    val scenarioExpenseOptTitle: String,
    val scenarioExpenseOptParam: String,
    val scenarioExpenseOptDesc: (amt: String) -> String,

    val scenarioRelocationTitle: String,
    val scenarioRelocationParam: String,
    val scenarioRelocationDesc: (amt: String) -> String,

    val scenarioInheritanceTitle: String,
    val scenarioInheritanceParam: String,
    val scenarioInheritanceDesc: (amt: String) -> String,

    val scenarioPropertySaleTitle: String,
    val scenarioPropertySaleParam: String,
    val scenarioPropertySaleDesc: (amt: String) -> String,

    val scenarioChildrenSelfSufficientTitle: String,
    val scenarioChildrenSelfSufficientParam: String,
    val scenarioChildrenSelfSufficientDesc: (amt: String) -> String,

    val scenarioJobLossTitle: String,
    val scenarioJobLossParam: String,
    val scenarioJobLossDesc: String,

    val scenarioMarketCrashTitle: String,
    val scenarioMarketCrashParam: String,
    val scenarioMarketCrashDesc: (amt: String) -> String,

    val scenarioMortgageTitle: String,
    val scenarioMortgageParam: String,
    val scenarioMortgageDesc: (pay: String, asset: String) -> String,

    val scenarioCarLoanTitle: String,
    val scenarioCarLoanParam: String,
    val scenarioCarLoanDesc: (pay: String, asset: String) -> String,

    val scenarioLargeExpenseTitle: String,
    val scenarioLargeExpenseParam: String,
    val scenarioLargeExpenseDesc: (amt: String) -> String,

    val scenarioInflationTitle: String,
    val scenarioInflationParam: String,
    val scenarioInflationDesc: (amt: String) -> String,

    val scenarioChildTitle: String,
    val scenarioChildParam: String,
    val scenarioChildDesc: (amt: String) -> String
)

val StringsRu = AppStrings(
    language = AppLanguage.RU,

    appName = "Личные финансы",
    tabInputTitle = "Данные для расчетов",
    tabInputShort = "Данные",
    tabMetricsTitle = "Расчет показателей",
    tabMetricsShort = "Показатели",
    tabScoresTitle = "Набранные баллы (итоги, суммирование результатов)",
    tabScoresShort = "Итоги & Баллы",
    tabFireTitle = "Финансовая свобода, пенсия, FIRE",
    tabFireShort = "FIRE & Пенсия",
    quickScorePrefix = "Итог",

    block1Title = "1. Доходы и Расходы",
    block1Subtitle = "Ежемесячный приток и отток денежных средств",
    monthlyIncomeLabel = "Доходы (в месяц)",
    monthlyExpensesLabel = "Расходы (в месяц)",
    incomeBreakdownName = "Доходы",
    incomeHintCollapsed = "Что входит в «Доходы»? (подсказка)",
    incomeHintExpanded = "Скрыть структуру показателя «Доходы»",
    expensesBreakdownName = "Расходы",
    expensesHintCollapsed = "Что входит в «Расходы»? (подсказка)",
    expensesHintExpanded = "Скрыть структуру показателя «Расходы»",
    breakdownInstruction = "Посчитайте и сложите ваши статьи из списка ниже самостоятельно, а полученную итоговую сумму укажите в поле выше.",
    incomeCategories = listOf(
        BreakdownCategory(
            title = "Заработная плата",
            items = listOf(
                "Основная работа (оклад, премия, бонусы)",
                "Вторая работа, совместительство"
            )
        ),
        BreakdownCategory(
            title = "Доход от бизнеса и самозанятости",
            items = listOf(
                "Прибыль от собственного бизнеса / ИП",
                "Фриланс, подработки, консалтинг, разовые услуги"
            )
        ),
        BreakdownCategory(
            title = "Пассивный и инвестиционный доход",
            items = listOf(
                "Проценты по банковским вкладам и накопительным счетам",
                "Дивиденды по акциям и купоны по облигациям",
                "Доход от сдачи недвижимости или транспорта в аренду",
                "Роялти, авторские права"
            )
        ),
        BreakdownCategory(
            title = "Социальные и прочие выплаты",
            items = listOf(
                "Пенсии, государственные пособия, субсидии",
                "Алименты, регулярная помощь родственников",
                "Налоговые вычеты, кэшбэк"
            )
        )
    ),
    expensesCategories = listOf(
        BreakdownCategory(
            title = "Базовые обязательные расходы",
            items = listOf(
                "Продукты питания и бытовая химия",
                "Аренда жилья или ипотечные платежи",
                "Коммунальные услуги (ЖКХ, электричество, вода)",
                "Связь, интернет, мобильные подписки"
            )
        ),
        BreakdownCategory(
            title = "Транспорт и логистика",
            items = listOf(
                "Общественный транспорт, проездные",
                "Топливо, парковка, мойка",
                "ТО, автостраховка (ОСАГО/КАСКО), ремонт",
                "Такси, каршеринг"
            )
        ),
        BreakdownCategory(
            title = "Здоровье и личные нужды",
            items = listOf(
                "Медикаменты, врачи, стоматология, ДМС",
                "Одежда, обувь, средства ухода",
                "Спорт, фитнес-абонементы, секции"
            )
        ),
        BreakdownCategory(
            title = "Досуг, образование и семья",
            items = listOf(
                "Кафе, рестораны, доставка еды",
                "Развлечения, кино, путешествия, хобби",
                "Обучение, курсы, книги, кружки детей",
                "Подарки, благотворительность, непредвиденные траты"
            )
        ),
        BreakdownCategory(
            title = "Обслуживание долгов",
            items = listOf(
                "Ежемесячные платежи по потребительским кредитам и кредитным картам"
            )
        )
    ),
    cashFlowTitle = "Денежный поток:",
    cashFlowFormula = "(Доходы - Расходы)",
    savingsRateTitle = "Норма сбережений:",
    savingsRateFormula = "(Денежный поток / Доходы) * 100%",

    block2Title = "2. Активы (Σ)",
    block2Subtitle = "Ликвидные, финансовые и имущественные накопления",
    liquidAssetsLabel = "Ликвидные (наличные, вклады до востребования)",
    financialAssetsLabel = "Финансовые (акции, облигации, фонды, брокерские счета)",
    propertyAssetsLabel = "Имущественные (недвижимость, автомобили, ценности)",
    assetsBreakdownName = "Активы",
    assetsHintCollapsed = "Что входит в «Активы»? (подсказка)",
    assetsHintExpanded = "Скрыть структуру показателя «Активы»",
    assetsCategories = listOf(
        BreakdownCategory(
            title = "Ликвидные активы",
            items = listOf(
                "Наличные деньги в любой валюте",
                "Остатки на текущих и дебетовых картах",
                "Накопительные счета и вклады до востребования",
                "Краткосрочные депозиты (до 1-3 месяцев)"
            )
        ),
        BreakdownCategory(
            title = "Финансовые (инвестиционные) активы",
            items = listOf(
                "Акции, ETF, паевые инвестиционные фонды (ПИФ)",
                "Облигации (ОФЗ, корпоративные, еврооблигации)",
                "Средства на брокерских счетах и ИИС",
                "Срочные долгосрочные банковские вклады",
                "Драгоценные металлы (ОМС, слитки, монеты)",
                "Криптовалюты и цифровые активы",
                "Пенсионные накопления в НПФ"
            )
        ),
        BreakdownCategory(
            title = "Имущественные (материальные) активы",
            items = listOf(
                "Жилая недвижимость (квартира, дом, дача)",
                "Коммерческая недвижимость, земельные участки, гаражи",
                "Транспортные средства (автомобиль, мотоцикл и др.)",
                "Оборудование, антиквариат, ювелирные ценности",
                "Доля в реальном бизнесе (оценочная стоимость)"
            )
        )
    ),
    totalAssetsTitle = "Активы (Σ):",
    totalAssetsFormula = "(Ликвидные + Финансовые + Имущественные)",
    toggleEmergencyFundTitle = "Включать финансовые активы в подушку",
    toggleEmergencyFundSubOn = "Подушка = Ликвидные + Финансовые",
    toggleEmergencyFundSubOff = "Подушка = Только Ликвидные",

    block3Title = "3. Пассивы",
    block3Subtitle = "Обязательства, кредиты, долги и ипотека",
    liabilitiesLabel = "Пассивы (все долги и обязательства)",
    liabilitiesBreakdownName = "Пассивы",
    liabilitiesHintCollapsed = "Что входит в «Пассивы»? (подсказка)",
    liabilitiesHintExpanded = "Скрыть структуру показателя «Пассивы»",
    liabilitiesCategories = listOf(
        BreakdownCategory(
            title = "Залоговые и долгосрочные обязательства",
            items = listOf(
                "Остаток основного долга по ипотеке (квартира, дом)",
                "Автокредит (остаток задолженности)",
                "Кредиты под залог имущества"
            )
        ),
        BreakdownCategory(
            title = "Потребительские и краткосрочные кредиты",
            items = listOf(
                "Потребительские кредиты наличными",
                "Задолженность по кредитным картам и картам рассрочки",
                "Рассрочки, BNPL-сервисы, займы в МФО"
            )
        ),
        BreakdownCategory(
            title = "Личные и частные долги",
            items = listOf(
                "Долги перед родственниками, друзьями или коллегами",
                "Частные займы под расписку"
            )
        ),
        BreakdownCategory(
            title = "Прочие финансовые обязательства",
            items = listOf(
                "Задолженность по налогам, штрафам, судебным искам",
                "Долги по коммунальным платежам (ЖКХ)"
            )
        )
    ),
    netWorthTitle = "Чистый капитал:",
    netWorthFormula = "(Активы - Пассивы)",

    block4Title = "4. Инвестиции & Инфляция",
    block4Subtitle = "Параметры доходности капитала и покупательской способности",
    nominalReturnLabel = "Номинальная %",
    inflationLabel = "Инфляция %",
    realReturnTitle = "Реальная доходность капитала:",
    realReturnFormula = "(1 + Номинальная доходность) / (1 + Инфляция) - 1",

    block5Title = "5. Возраст & Горизонт FIRE",
    block5Subtitle = "Параметры для расчета выхода на пенсию",
    birthDateLabel = "Дата рождения (ГГГГ-ММ-ДД)",
    lifeExpectancyLabel = "Дожитие (лет)",
    horizonTitle = "Горизонт дожития:",
    horizonFormula = "(Возраст дожития - Текущий возраст)",
    horizonValueFormat = { horizon, age -> "$horizon года (возраст $age лет)" },

    calculateButtonText = "Рассчитать показатели",
    exportButtonText = "Экспорт результатов",
    exportChooserTitle = "Экспорт результатов",

    metricsHeaderTitle = "Расчет показателей",
    metricsHeaderSubtitle = "Все ключевые метрики с формулами и оценкой",
    categoryAll = "Все",
    categoryCashFlow = "Денежный поток",
    categoryCapitalRisks = "Капитал & Риски",
    categoryBenchmarks = "Бенчмарки",
    metricCardInfo = "Инфо",
    metricCardHide = "Скрыть",
    metricCardFormulaPrefix = "Формула: ",
    metricCardExpand = "Развернуть",
    metricCardCollapse = "Свернуть",

    metricCashFlowTitle = "Денежный поток",
    metricCashFlowFormula = "(Доходы - Расходы)",
    metricCashFlowDesc = "Cash Flow. Движение денег (приток и отток) за период.\n\n• Положительное значение: Вы зарабатываете больше, чем тратите.\n• Отрицательное значение: Вы тратите больше, чем зарабатываете.",
    metricCashFlowPositiveSub = "Положительный приток денег",
    metricCashFlowNegativeSub = "Дефицит бюджета",

    metricSavingsRateTitle = "Норма сбережений",
    metricSavingsRateFormula = "(Денежный поток / Доходы) * 100%",
    metricSavingsRateDesc = "Рекомендуется стремиться к норме сбережений более 20% от общего дохода.\n\n• Меньше 0%: Негативный показатель (дефицит)\n• 0% — 20%: Нейтральный показатель (умеренно)\n• Более 20%: Позитивный показатель (отлично)",
    metricSavingsRateSub = "Целевой ориентир: > 20% (0–20% нейтрально)",

    metricEmergencyFundTitle = "Подушка безопасности",
    metricEmergencyFundFormula = "(Ликвидные активы)",
    metricEmergencyFundDesc = "Личная финансовая «подушка безопасности» или запас денег, созданный для покрытия непредвиденных расходов (болезни, потеря работы и т.п.) и обеспечения финансовой стабильности в экстренных ситуациях, чтобы не влезать в долги.",
    metricEmergencyFundSubLiquidPlusFin = "Ликвидные + финансовые",
    metricEmergencyFundSubOnlyLiquid = "Только ликвидные",

    metricSafetyMonthsTitle = "Запас месячных расходов",
    metricSafetyMonthsFormula = "(Подушка безопасности / Ежемесячные расходы)",
    metricSafetyMonthsDesc = "Количество месяцев, которые вы можете прожить на средства из \"подушки безопасности\".\n\n• Минимальный уровень: 3-6 месяцев основных расходов (аренда/ипотека, еда, транспорт, коммунальные услуги, связь). Это «спокойное» существование при нулевых доходах.\n• Идеальный уровень: Может включать больше, например, до 12 месяцев, для более уверенного прохождения длительных кризисов, особенно при нестабильной работе или наличии иждивенцев.",
    metricSafetyMonthsSub = "Норма: 3-6 мес, Идеал: 12+ мес",
    monthsSuffix = "мес.",

    metricDebtRatioTitle = "Коэффициент задолженности",
    metricDebtRatioFormula = "(Пассивы / Активы)",
    metricDebtRatioDesc = "Показывает, какая доля активов финансируется за счет долгов (обязательств), отражая степень финансового риска и зависимости от кредиторов.\n\n• 0,0 - 0,3: Хорошая финансовая устойчивость, большая часть активов финансируется собственными средствами\n• 0,3 - 0,6: Идеальный баланс для большинства, сбалансированное кредитное плечо\n• Больше 0,6: Высокий риск, сильная зависимость от долгов\n• Больше 1,0: Долгов больше, чем активов; очень высокий риск",
    metricDebtRatioLowSub = "Низкий риск (отлично)",
    metricDebtRatioHighSub = "Высокий риск долгов",

    metricNetWorthTitle = "Чистый капитал",
    metricNetWorthFormula = "(Активы - Пассивы)",
    metricNetWorthDesc = "Общая стоимость активов за вычетом всех обязательств. Это те средства, что останутся у собственника после продажи всех активов и погашения всех долгов.",
    metricNetWorthSubPrefix = "Активы: ",
    metricNetWorthNegativeSub = "Отрицательный баланс (дефицит!)",

    metricFidelityTitle = "Бенчмарк: целевые показатели по Fidelity",
    metricFidelityFormula = "Годовой доход * Множитель",
    metricFidelityDesc = "Методика Fidelity показывает, во сколько раз ваши текущие пенсионные накопления должны превышать ваш годовой заработок на определенном жизненном этапе.\n\nШкала Fidelity дискретна (задана конкретными точками для 30, 40, 50, 60 и 67 лет).\nПример: Если вам 40 лет и вы зарабатываете ($100,000) в год, то, согласно Fidelity, на ваших инвестиционных и пенсионных счетах должно быть накоплено не менее ($300,000).",
    metricFidelitySubFormat = { age, mult -> "Возраст: $age лет | Множитель: ${mult}x" },
    metricFidelityProgress = "Прогресс к ориентиру Fidelity:",

    metricCfsTitle = "Коэффициент финансовой свободы",
    metricCfsFormula = "(Чистые активы / Годовые расходы)",
    metricCfsDesc = "Показывает, сколько годовых расходов покрывают ваши чистые активы (чистый капитал).\n\n• КФС < 0 — Вы в долгах\n• КФС >= 0 и КФС <= 0,5 — У вас есть минимальная «подушка безопасности»\n• КФС > 0,5 и КФС <= 2 — Вы начали формировать свой финансовый капитал\n• КФС > 2 и КФС <= 5 — Вы можете начать заниматься долгосрочными инвестициями\n• КФС > 5 и КФС <= 15 — Доходы от капитала могут приносить вам существенную прибавку. Инфляция все более значительно влияет на капитал\n• КФС > 15 и КФС <= 30 — Крепкая финансовая стабильность. На что вы тратите свой невосполнимый капитал времени?\n• КФС > 30 — Вы достигли финансовой независимости",

    metricRealReturnTitle = "Реальная доходность капитала, %",
    metricRealReturnFormula = "(1 + Номинальная доходность) / (1 + Инфляция) - 1",
    metricRealReturnDesc = "Фактический прирост ваших сбережений после того, как из начисленной прибыли вычли уровень инфляции. Показывает, на сколько увеличилась ваша реальная покупательная способность.",
    metricRealReturnSubFormat = { nom, infl -> "Номинал: $nom | Инфляция: $infl" },

    scoresHeaderTitle = "Набранные баллы",
    scoresHeaderSubtitle = "Итоги и суммирование результатов",
    scoresShareReportChooser = "Поделиться отчетом",
    scoresGaugeOutOf = { max -> "из $max" },
    scoresTotalResult = { score -> "Итоговый результат: $score из 10" },
    scoresStatusExcellent = "Отличная финансовая устойчивость и готовность к долгосрочным инвестициям.",
    scoresStatusGood = "Хорошая финансовая форма с потенциалом роста нормы накопления.",
    scoresStatusModerate = "Умеренная стабильность. Рекомендуется нарастить подушку безопасности.",
    scoresStatusAttention = "Требуется оптимизация расходов и погашение долговых обязательств.",
    scoresBreakdownTitle = "Детализация баллов по компонентам:",
    scoreValuePrefix = "Значение: ",
    scoreOutOfTen = { score -> "$score из 10" },
    savingsRateScoreComment = { rate, sr ->
        when {
            rate > 20.0 -> "Ориентир более 20%. У вас отличное значение ($sr)."
            rate >= 0.0 -> "Ориентир более 20%. У вас низкое значение ($sr)."
            else -> "Ориентир более 20%. У вас отрицательное значение ($sr), дефицит бюджета."
        }
    },
    safetyMonthsScoreComment = { months, monthsText ->
        when {
            months >= 12.0 -> "Минимум 3-6 месяцев, идеал 12+ мес. Отличный результат: запас превышает 12 месяцев ($monthsText)."
            months >= 6.0 -> "Минимум 3-6 месяцев, идеал 12+ мес. Хороший запас ($monthsText), норматив в 6 месяцев достигнут."
            months >= 3.0 -> "Минимум 3-6 месяцев, идеал 12+ мес. Базовый запас ($monthsText), рекомендуется увеличить до 6-12 месяцев."
            else -> "Минимум 3-6 месяцев, идеал 12+ мес. У вас недостаточный запас ($monthsText), высокий финансовый риск."
        }
    },
    debtComment0 = "0,0 - 0,3: Отличная устойчивость, активы финансируются собственными средствами.",
    debtComment1 = "0,3 - 0,6: Сбалансированное кредитное плечо, умеренная нагрузка.",
    debtComment2 = "0,6 - 1,0: Высокий риск, значительная зависимость от заемных средств.",
    debtComment3 = "Более 1,0: Критический риск, долги превышают стоимость активов.",

    auditorOpinionTitle = "Аудиторское заключение",
    auditorOpinionSubtitle = { count -> "Анализ по всем $count показателям экрана «Показатели»" },
    shareAuditorOpinionChooser = "Поделиться аудиторским заключением",
    positiveBadgeText = { count -> "🟢 $count позит." },
    neutralBadgeText = { count -> "🟡 $count нейтр." },
    negativeBadgeText = { count -> "🔴 $count негатив." },
    positiveSectionTitle = { count -> "Позитивные показатели ($count)" },
    neutralSectionTitle = { count -> "Нейтральные показатели ($count)" },
    negativeSectionTitle = { count -> "Негативные показатели ($count)" },
    positiveEmptyText = "Позитивных показателей пока не выявлено. Рекомендуется оптимизировать доходы и расходы.",
    neutralEmptyText = "Нейтральных показателей нет — все метрики в позитивной или негативной зоне.",
    negativeEmptyText = "🎉 Критических и негативных факторов не выявлено — все показатели в безопасной зоне!",

    fireHeroTitle = "Финансовая свобода, пенсия, FIRE",
    fireHeroSubtitle = "Расчет срока до выхода на пенсию",
    fireTimeToRetire = "До пенсии осталось:",
    fireYearsSuffix = " года (лет)",
    fireHeroAssumption = "(с учетом будущих сбережений, инфляции и доходности капитала)",
    fireRetirementAgeLabel = "Возраст выхода на пенсию:",
    fireRetirementAgeValue = { age -> "$age лет" },
    fireTargetCapitalLabel = "Требуемый целевой капитал:",
    fireParamsTitle = "Параметры возраста и горизонта",
    fireBirthDateLabel = "Дата рождения:",
    fireCurrentAgeLabel = "Текущий возраст:",
    fireCurrentAgeValue = { age -> "$age лет" },
    fireLifeExpectancyLabel = "Возраст дожития:",
    fireLifeExpectancyValue = { exp -> "$exp лет" },
    fireHorizonLabel = "(Возраст дожития - Текущий возраст):",
    fireHorizonValue = { h -> "$h года" },
    fireRealReturnLabel = "Реальная доходность капитала:",
    fireTrajectoryTitle = "Траектория накопления капитала",
    fireTrajectorySubtitle = "Рост капитала до точки FIRE и обеспечение жизни на пенсии",
    fireChartAccumulated = "Накопленный капитал",
    fireChartRequired = "Необходимый капитал",
    fireChartYearsSuffix = " лет",
    fireChartFirePoint = { age -> "FIRE: $age лет" },
    fireSimSummaryTitle = "Итог симуляции (совместное влияние)",
    fireSimEarlier = { diff, target, age -> "⚡ Пенсия наступит на $diff года раньше (через $target лет, в возрасте $age лет)!" },
    fireSimLater = { diff, target, age -> "⏳ Срок до пенсии увеличится на $diff года (через $target лет, в возрасте $age лет)." },
    fireSimSame = { target -> "Срок выхода на пенсию остается прежним (через $target лет)." },
    fireSimCashFlowRow = "Денежный поток (сбережения):",
    fireSimExpensesRow = "Ежемесячные расходы:",
    fireSimNetWorthRow = "Чистый капитал (активы − долги):",
    fireSimPropertyRow = "Прирост имущества (по окончании выплат):",
    fireSimSliderTitle = "Симулятор «Что если?»",
    fireSimReset = "Сброс",
    fireSimFineTuning = "Тонкая настройка шкал (ползунки):",
    fireSliderSavings = "Ежемесячные сбережения:",
    fireSliderExpenses = "Ежемесячные расходы:",
    fireSliderReturn = "Реальная доходность капитала:",
    fireSliderBaseSuffix = " к базовым",
    fireSliderBaseReturnSuffix = "% к базовой",
    fireSliderFrom = { v -> "от $v" },
    fireSliderStep = { v -> "шаг $v" },
    fireSliderTo = { v -> "до +$v" },
    fireSliderInc = " (рост)",
    fireSliderDec = " (сниж.)",

    scenarioHeaderTitle = "Панель сценариев (стресс-тесты)",
    scenarioHeaderSubtitle = "Результат влияния разных ползунков и чекбоксов суммируется (накапливается и рассчитывается совместно). Итоговый результат отражается в числах и на графике:",
    scenarioTotalBadge = { count -> "Всего: $count" },
    scenarioPositiveTitle = "Позитивные события",
    scenarioPositiveActive = { count -> "Активно: $count" },
    scenarioPositiveSubtitle = "События, ускоряющие накопление капитала и выход на пенсию:",
    scenarioNegativeTitle = "Негативные события",
    scenarioNegativeActive = { count -> "Активно: $count" },
    scenarioNegativeSubtitle = "Стресс-тесты, непредвиденные расходы и кризисные ситуации:",
    scenarioParamsPrefix = "Параметры: ",

    scenarioSalaryIncreaseTitle = "Повышение зарплаты",
    scenarioSalaryIncreaseParam = "доход +20% ежемесячно, постоянно",
    scenarioSalaryIncreaseDesc = { amt -> "Увеличение дохода: +$amt/мес." },

    scenarioMarketGrowthTitle = "Рост рынка на 25%",
    scenarioMarketGrowthParam = "ETF +25%",
    scenarioMarketGrowthDesc = { amt -> "Прирост финансового портфеля: +$amt" },

    scenarioExpenseOptTitle = "Оптимизация расходов",
    scenarioExpenseOptParam = "−15% к ежемесячным расходам, постоянно",
    scenarioExpenseOptDesc = { amt -> "Постоянная экономия: -$amt/мес." },

    scenarioRelocationTitle = "Переезд в регион с более низкой стоимостью жизни",
    scenarioRelocationParam = "−30% к ежемесячным расходам, постоянно",
    scenarioRelocationDesc = { amt -> "Снижение стоимости жизни: -$amt/мес." },

    scenarioInheritanceTitle = "Наследство или крупный подарок",
    scenarioInheritanceParam = "разовое поступление 1200% мес. дохода",
    scenarioInheritanceDesc = { amt -> "Приток капитала: +$amt (в ликвидные активы)" },

    scenarioPropertySaleTitle = "Продажа имущества (авто, участок, недвижимость)",
    scenarioPropertySaleParam = "разовое поступление 2000% мес. дохода",
    scenarioPropertySaleDesc = { amt -> "Выручка от продажи: +$amt (в ликвидные активы)" },

    scenarioChildrenSelfSufficientTitle = "Выход детей на самообеспечение",
    scenarioChildrenSelfSufficientParam = "расходы −25% ежемесячно, после 18 лет",
    scenarioChildrenSelfSufficientDesc = { amt -> "Снижение семейных расходов: -$amt/мес. (после 18 лет)" },

    scenarioJobLossTitle = "Потеря работы",
    scenarioJobLossParam = "доход = 0",
    scenarioJobLossDesc = "Полная остановка притока дохода, покрытие всех расходов из резервов",

    scenarioMarketCrashTitle = "Обвал рынка на 30%",
    scenarioMarketCrashParam = "ETF −30%",
    scenarioMarketCrashDesc = { amt -> "Просадка финансового портфеля на 30% ($amt)" },

    scenarioMortgageTitle = "Ипотека на квартиру",
    scenarioMortgageParam = "35% дохода на 20 лет (+84 дохода в имущество)",
    scenarioMortgageDesc = { pay, asset -> "Платеж: $pay/мес. (20 лет). В конце срока: +$asset в имущество/активы" },

    scenarioCarLoanTitle = "Автомобиль в кредит",
    scenarioCarLoanParam = "20% дохода на 5 лет (+6 доходов в имущество)",
    scenarioCarLoanDesc = { pay, asset -> "Платеж: $pay/мес. (5 лет). В конце срока: +$asset в имущество (50% стоимости)" },

    scenarioLargeExpenseTitle = "Крупная разовая трата",
    scenarioLargeExpenseParam = "расход 500% мес. дохода",
    scenarioLargeExpenseDesc = { amt -> "Трата $amt (покрытие из ликвидных и фин. активов)" },

    scenarioInflationTitle = "Рост инфляции",
    scenarioInflationParam = "+20% к расходам",
    scenarioInflationDesc = { amt -> "Рост стоимости жизни: +$amt/мес." },

    scenarioChildTitle = "Рождение ребенка",
    scenarioChildParam = "+25% к расходам на 18 лет",
    scenarioChildDesc = { amt -> "Расходы на ребенка: +$amt/мес. (период 18 лет)" }
)

val StringsEn = AppStrings(
    language = AppLanguage.EN,

    appName = "Personal Finance",
    tabInputTitle = "Input Data & Parameters",
    tabInputShort = "Inputs",
    tabMetricsTitle = "Calculated Metrics",
    tabMetricsShort = "Metrics",
    tabScoresTitle = "Score Breakdown & Financial Audit",
    tabScoresShort = "Scores & Audit",
    tabFireTitle = "Financial Independence, Retirement, FIRE",
    tabFireShort = "FIRE & Plan",
    quickScorePrefix = "Score",

    block1Title = "1. Income & Expenses",
    block1Subtitle = "Monthly cash inflows and outflows",
    monthlyIncomeLabel = "Income (monthly)",
    monthlyExpensesLabel = "Expenses (monthly)",
    incomeBreakdownName = "Income",
    incomeHintCollapsed = "What is included in \"Income\"? (hint)",
    incomeHintExpanded = "Hide structure of \"Income\"",
    expensesBreakdownName = "Expenses",
    expensesHintCollapsed = "What is included in \"Expenses\"? (hint)",
    expensesHintExpanded = "Hide structure of \"Expenses\"",
    breakdownInstruction = "Calculate and add up your items from the list below yourself, and enter the resulting total in the field above.",
    incomeCategories = listOf(
        BreakdownCategory(
            title = "Employment & Salary",
            items = listOf(
                "Primary employment (base salary, bonuses, overtime)",
                "Secondary job, moonlighting, part-time work"
            )
        ),
        BreakdownCategory(
            title = "Business & Self-Employment",
            items = listOf(
                "Net profit from owned business / sole proprietorship",
                "Freelancing, consulting, side gigs, one-off services"
            )
        ),
        BreakdownCategory(
            title = "Passive & Investment Income",
            items = listOf(
                "Interest on bank deposits and high-yield savings accounts",
                "Stock dividends and bond coupon payments",
                "Rental income from real estate or vehicles",
                "Royalties, intellectual property"
            )
        ),
        BreakdownCategory(
            title = "Social & Other Inflows",
            items = listOf(
                "Pensions, government benefits, subsidies",
                "Child support, regular family assistance",
                "Tax refunds, cashbacks"
            )
        )
    ),
    expensesCategories = listOf(
        BreakdownCategory(
            title = "Essential Living Expenses",
            items = listOf(
                "Groceries and household goods",
                "Rent or primary mortgage interest/payments",
                "Utilities (electricity, gas, water, heating)",
                "Telecommunications, internet, mobile plans"
            )
        ),
        BreakdownCategory(
            title = "Transportation & Commute",
            items = listOf(
                "Public transit, commuter passes",
                "Fuel, parking, car wash",
                "Vehicle maintenance, insurance, repairs",
                "Taxis, ride-sharing, car sharing"
            )
        ),
        BreakdownCategory(
            title = "Health & Personal Care",
            items = listOf(
                "Medications, doctor visits, dental care, health insurance",
                "Clothing, footwear, personal grooming",
                "Sports, fitness memberships, athletic clubs"
            )
        ),
        BreakdownCategory(
            title = "Leisure, Education & Family",
            items = listOf(
                "Dining out, coffee shops, food delivery",
                "Entertainment, movies, travel, hobbies",
                "Education, courses, books, children's activities",
                "Gifts, charity, miscellaneous unexpected expenses"
            )
        ),
        BreakdownCategory(
            title = "Debt Service",
            items = listOf(
                "Monthly minimums and payments on personal loans and credit cards"
            )
        )
    ),
    cashFlowTitle = "Cash Flow:",
    cashFlowFormula = "(Income - Expenses)",
    savingsRateTitle = "Savings Rate:",
    savingsRateFormula = "(Cash Flow / Income) * 100%",

    block2Title = "2. Assets (Σ)",
    block2Subtitle = "Liquid, financial and physical wealth accumulation",
    liquidAssetsLabel = "Liquid (cash, checking, demand deposits)",
    financialAssetsLabel = "Financial (stocks, bonds, ETFs, brokerage)",
    propertyAssetsLabel = "Physical / Property (real estate, vehicles, valuables)",
    assetsBreakdownName = "Assets",
    assetsHintCollapsed = "What is included in \"Assets\"? (hint)",
    assetsHintExpanded = "Hide structure of \"Assets\"",
    assetsCategories = listOf(
        BreakdownCategory(
            title = "Liquid Assets",
            items = listOf(
                "Cash holdings in any currency",
                "Balances on checking and debit accounts",
                "High-yield savings accounts and money market funds",
                "Short-term deposits (up to 1-3 months)"
            )
        ),
        BreakdownCategory(
            title = "Financial (Investment) Assets",
            items = listOf(
                "Equities, ETFs, mutual funds",
                "Bonds (government, municipal, corporate)",
                "Brokerage balances and tax-advantaged accounts",
                "Long-term fixed certificates of deposit",
                "Precious metals (bullion, allocated accounts, coins)",
                "Cryptocurrencies and digital assets",
                "Retirement plan balances (401k, IRA, pensions)"
            )
        ),
        BreakdownCategory(
            title = "Physical (Property) Assets",
            items = listOf(
                "Residential real estate (primary home, apartment, land)",
                "Commercial real estate, parking spaces, garages",
                "Vehicles (cars, motorcycles, boats)",
                "Machinery, equipment, antiques, fine jewelry",
                "Equity share in privately held operating businesses"
            )
        )
    ),
    totalAssetsTitle = "Assets (Σ):",
    totalAssetsFormula = "(Liquid + Financial + Property)",
    toggleEmergencyFundTitle = "Include financial assets in emergency buffer",
    toggleEmergencyFundSubOn = "Buffer = Liquid + Financial",
    toggleEmergencyFundSubOff = "Buffer = Liquid Only",

    block3Title = "3. Liabilities",
    block3Subtitle = "Debt obligations, loans, credit cards and mortgages",
    liabilitiesLabel = "Liabilities (all debts and obligations)",
    liabilitiesBreakdownName = "Liabilities",
    liabilitiesHintCollapsed = "What is included in \"Liabilities\"? (hint)",
    liabilitiesHintExpanded = "Hide structure of \"Liabilities\"",
    liabilitiesCategories = listOf(
        BreakdownCategory(
            title = "Secured & Long-Term Debt",
            items = listOf(
                "Remaining principal on residential mortgage",
                "Auto loans (remaining balance)",
                "Collateralized home equity loans"
            )
        ),
        BreakdownCategory(
            title = "Consumer & Short-Term Loans",
            items = listOf(
                "Unsecured personal bank loans",
                "Outstanding credit card balances and revolving lines",
                "Installment plans, BNPL services, microloans"
            )
        ),
        BreakdownCategory(
            title = "Personal & Private Debts",
            items = listOf(
                "Debts owed to relatives, friends or colleagues",
                "Private promissory notes and personal loans"
            )
        ),
        BreakdownCategory(
            title = "Other Financial Obligations",
            items = listOf(
                "Back taxes, unpaid legal claims, fines",
                "Delinquent utility bills"
            )
        )
    ),
    netWorthTitle = "Net Worth:",
    netWorthFormula = "(Assets - Liabilities)",

    block4Title = "4. Investments & Inflation",
    block4Subtitle = "Parameters of capital return and purchasing power",
    nominalReturnLabel = "Nominal return %",
    inflationLabel = "Inflation %",
    realReturnTitle = "Real Capital Return:",
    realReturnFormula = "(1 + Nominal Return) / (1 + Inflation) - 1",

    block5Title = "5. Age & Planning Horizon",
    block5Subtitle = "Parameters for retirement planning",
    birthDateLabel = "Birth date (YYYY-MM-DD)",
    lifeExpectancyLabel = "Life expectancy (yrs)",
    horizonTitle = "Planning Horizon:",
    horizonFormula = "(Life expectancy - Current age)",
    horizonValueFormat = { horizon, age -> "$horizon yrs (current age: $age yrs)" },

    calculateButtonText = "Calculate Metrics",
    exportButtonText = "Export Results",
    exportChooserTitle = "Export Results",

    metricsHeaderTitle = "Calculated Metrics",
    metricsHeaderSubtitle = "All key financial indicators with formulas & evaluation",
    categoryAll = "All",
    categoryCashFlow = "Cash Flow",
    categoryCapitalRisks = "Capital & Risks",
    categoryBenchmarks = "Benchmarks",
    metricCardInfo = "Info",
    metricCardHide = "Hide",
    metricCardFormulaPrefix = "Formula: ",
    metricCardExpand = "Expand",
    metricCardCollapse = "Collapse",

    metricCashFlowTitle = "Cash Flow",
    metricCashFlowFormula = "(Income - Expenses)",
    metricCashFlowDesc = "Cash Flow. Net movement of funds (inflows and outflows) per period.\n\n• Positive value: You earn more than you spend.\n• Negative value: You spend more than you earn.",
    metricCashFlowPositiveSub = "Positive cash surplus",
    metricCashFlowNegativeSub = "Budget deficit",

    metricSavingsRateTitle = "Savings Rate",
    metricSavingsRateFormula = "(Cash Flow / Income) * 100%",
    metricSavingsRateDesc = "It is recommended to aim for a savings rate above 20% of gross household income.\n\n• Below 0%: Negative indicator (deficit)\n• 0% — 20%: Neutral indicator (moderate)\n• Above 20%: Positive indicator (excellent)",
    metricSavingsRateSub = "Target benchmark: > 20% (0–20% neutral)",

    metricEmergencyFundTitle = "Emergency Fund",
    metricEmergencyFundFormula = "(Liquid Assets)",
    metricEmergencyFundDesc = "A personal safety net created to cover unforeseen emergencies (illness, job loss, emergency repairs) and safeguard financial stability without incurring high-interest debt.",
    metricEmergencyFundSubLiquidPlusFin = "Liquid + Financial",
    metricEmergencyFundSubOnlyLiquid = "Liquid Only",

    metricSafetyMonthsTitle = "Emergency Buffer (Months)",
    metricSafetyMonthsFormula = "(Emergency Fund / Monthly Expenses)",
    metricSafetyMonthsDesc = "The number of months you can sustain baseline living expenses solely from your emergency reserves.\n\n• Minimum target: 3-6 months of core living costs (rent/mortgage, food, utilities, transport). Guarantees calm recovery during income gaps.\n• Ideal target: Up to 12 months for comprehensive crisis resilience, especially for freelancers or households with dependents.",
    metricSafetyMonthsSub = "Norm: 3-6 mo, Ideal: 12+ mo",
    monthsSuffix = "mo.",

    metricDebtRatioTitle = "Debt-to-Asset Ratio",
    metricDebtRatioFormula = "(Liabilities / Assets)",
    metricDebtRatioDesc = "Measures the proportion of assets financed through debt obligations, reflecting financial risk exposure and dependency on creditors.\n\n• 0.0 - 0.3: Strong financial solvency, most assets funded by equity\n• 0.3 - 0.6: Balanced leverage for most households\n• Above 0.6: High risk, excessive debt dependency\n• Above 1.0: Liabilities exceed assets (insolvency risk)",
    metricDebtRatioLowSub = "Low risk (excellent)",
    metricDebtRatioHighSub = "High debt risk",

    metricNetWorthTitle = "Net Worth",
    metricNetWorthFormula = "(Assets - Liabilities)",
    metricNetWorthDesc = "The total value of all assets minus all outstanding liabilities. This represents what you truly own outright after settling all obligations.",
    metricNetWorthSubPrefix = "Total assets: ",
    metricNetWorthNegativeSub = "Negative equity (deficit!)",

    metricFidelityTitle = "Benchmark: Fidelity Capital Milestones",
    metricFidelityFormula = "Annual Income * Multiplier",
    metricFidelityDesc = "Fidelity's rule of thumb outlines how many times your current retirement portfolio should exceed your annual salary at specific life stages.\n\nCheckpoints: 1x by age 30, 3x by 40, 6x by 50, 8x by 60, and 10x by 67.\nExample: At age 40 with $100,000 annual income, Fidelity recommends having at least $300,000 saved.",
    metricFidelitySubFormat = { age, mult -> "Age: $age yrs | Multiplier: ${mult}x" },
    metricFidelityProgress = "Progress toward Fidelity goal:",

    metricCfsTitle = "Financial Freedom Ratio (CFS)",
    metricCfsFormula = "(Net Assets / Annual Expenses)",
    metricCfsDesc = "Measures how many years of baseline living expenses are covered by your net assets (net worth).\n\n• CFS < 0 — You are in net debt\n• CFS 0..0.5 — Minimal emergency cushion\n• CFS 0.5..2 — Initial capital accumulation stage\n• CFS 2..5 — Long-term investing stage\n• CFS 5..15 — Investment returns provide tangible income. Inflation impact grows\n• CFS 15..30 — Solid financial stability. How do you value your irreplaceable time?\n• CFS > 30 — Complete financial independence achieved",

    metricRealReturnTitle = "Real Return on Capital, %",
    metricRealReturnFormula = "(1 + Nominal Return) / (1 + Inflation) - 1",
    metricRealReturnDesc = "The net real expansion of purchasing power after deducting inflation from nominal investment yields. Shows real wealth creation.",
    metricRealReturnSubFormat = { nom, infl -> "Nominal: $nom | Inflation: $infl" },

    scoresHeaderTitle = "Score Summary",
    scoresHeaderSubtitle = "Overall audit score & breakdown",
    scoresShareReportChooser = "Share Report",
    scoresGaugeOutOf = { max -> "of $max" },
    scoresTotalResult = { score -> "Overall Score: $score of 10" },
    scoresStatusExcellent = "Outstanding financial resilience and readiness for long-term wealth compounding.",
    scoresStatusGood = "Solid financial health with strong potential to increase savings rate.",
    scoresStatusModerate = "Moderate stability. Recommended to strengthen your emergency cash buffer.",
    scoresStatusAttention = "Budget optimization and aggressive debt reduction are urgently recommended.",
    scoresBreakdownTitle = "Component Score Breakdown:",
    scoreValuePrefix = "Value: ",
    scoreOutOfTen = { score -> "$score of 10" },
    savingsRateScoreComment = { rate, sr ->
        when {
            rate > 20.0 -> "Recommended above 20%. You maintain an excellent rate ($sr)."
            rate >= 0.0 -> "Recommended above 20%. You have a low rate ($sr)."
            else -> "Recommended above 20%. You have a negative rate ($sr), budget deficit."
        }
    },
    safetyMonthsScoreComment = { months, monthsText ->
        when {
            months >= 12.0 -> "Minimum 3-6 months, ideal 12+ months. Excellent result: buffer exceeds 12 months ($monthsText)."
            months >= 6.0 -> "Minimum 3-6 months, ideal 12+ months. Good buffer ($monthsText), 6-month benchmark achieved."
            months >= 3.0 -> "Minimum 3-6 months, ideal 12+ months. Basic buffer ($monthsText), expanding to 6-12 months recommended."
            else -> "Minimum 3-6 months, ideal 12+ months. You have an insufficient buffer ($monthsText), elevated financial risk."
        }
    },
    debtComment0 = "0.0 - 0.3: Excellent stability, assets funded almost entirely by own equity.",
    debtComment1 = "0.3 - 0.6: Balanced leverage, manageable borrowing level.",
    debtComment2 = "0.6 - 1.0: Elevated risk, substantial dependence on borrowed money.",
    debtComment3 = "Above 1.0: Critical risk, total debts exceed the value of all assets.",

    auditorOpinionTitle = "Auditor Opinion",
    auditorOpinionSubtitle = { count -> "Analysis across all $count indicators from the Metrics screen" },
    shareAuditorOpinionChooser = "Share Auditor Opinion",
    positiveBadgeText = { count -> "🟢 $count positive" },
    neutralBadgeText = { count -> "🟡 $count neutral" },
    negativeBadgeText = { count -> "🔴 $count negative" },
    positiveSectionTitle = { count -> "Positive Indicators ($count)" },
    neutralSectionTitle = { count -> "Neutral Indicators ($count)" },
    negativeSectionTitle = { count -> "Negative Indicators ($count)" },
    positiveEmptyText = "No positive factors identified yet. Recommended to optimize income and expenses.",
    neutralEmptyText = "No neutral indicators — all metrics fall into either positive or negative zones.",
    negativeEmptyText = "🎉 No critical or negative risk factors found — all metrics are in safe territory!",

    fireHeroTitle = "Financial Independence, FIRE",
    fireHeroSubtitle = "Retirement timeline and wealth projection",
    fireTimeToRetire = "Time to retirement:",
    fireYearsSuffix = " years",
    fireHeroAssumption = "(accounting for future savings, inflation and capital compounding)",
    fireRetirementAgeLabel = "Retirement age:",
    fireRetirementAgeValue = { age -> "$age yrs" },
    fireTargetCapitalLabel = "Target required capital:",
    fireParamsTitle = "Age & Planning Horizon Parameters",
    fireBirthDateLabel = "Date of birth:",
    fireCurrentAgeLabel = "Current age:",
    fireCurrentAgeValue = { age -> "$age yrs" },
    fireLifeExpectancyLabel = "Life expectancy:",
    fireLifeExpectancyValue = { exp -> "$exp yrs" },
    fireHorizonLabel = "(Life expectancy - Current age):",
    fireHorizonValue = { h -> "$h yrs" },
    fireRealReturnLabel = "Real return on capital:",
    fireTrajectoryTitle = "Capital Accumulation Trajectory",
    fireTrajectorySubtitle = "Wealth growth toward the FIRE point and funding lifelong retirement",
    fireChartAccumulated = "Accumulated capital",
    fireChartRequired = "Required capital",
    fireChartYearsSuffix = " yrs",
    fireChartFirePoint = { age -> "FIRE: $age yrs" },
    fireSimSummaryTitle = "Simulation Summary (Combined Impact)",
    fireSimEarlier = { diff, target, age -> "⚡ Retirement achieved $diff yrs earlier (in $target yrs, at age $age)!" },
    fireSimLater = { diff, target, age -> "⏳ Time to retirement increased by $diff yrs (in $target yrs, at age $age)." },
    fireSimSame = { target -> "Retirement timeline remains unchanged (in $target yrs)." },
    fireSimCashFlowRow = "Cash flow (savings):",
    fireSimExpensesRow = "Monthly expenses:",
    fireSimNetWorthRow = "Net worth (assets − debts):",
    fireSimPropertyRow = "Property increase (at loan completion):",
    fireSimSliderTitle = "Simulator \"What If?\"",
    fireSimReset = "Reset",
    fireSimFineTuning = "Fine-tune scales (sliders):",
    fireSliderSavings = "Monthly savings:",
    fireSliderExpenses = "Monthly expenses:",
    fireSliderReturn = "Real return on capital:",
    fireSliderBaseSuffix = " to base",
    fireSliderBaseReturnSuffix = "% to base",
    fireSliderFrom = { v -> "from $v" },
    fireSliderStep = { v -> "step $v" },
    fireSliderTo = { v -> "up to +$v" },
    fireSliderInc = " (growth)",
    fireSliderDec = " (red.)",

    scenarioHeaderTitle = "Scenario Panel (Stress Tests)",
    scenarioHeaderSubtitle = "The collective impact of sliders and checkboxes accumulates and is calculated together. The final result reflects in numbers and on the trajectory chart:",
    scenarioTotalBadge = { count -> "Total: $count" },
    scenarioPositiveTitle = "Positive Events",
    scenarioPositiveActive = { count -> "Active: $count" },
    scenarioPositiveSubtitle = "Events that accelerate capital accumulation and retirement:",
    scenarioNegativeTitle = "Negative Events",
    scenarioNegativeActive = { count -> "Active: $count" },
    scenarioNegativeSubtitle = "Stress tests, unforeseen expenses and crisis scenarios:",
    scenarioParamsPrefix = "Parameters: ",

    scenarioSalaryIncreaseTitle = "Salary Increase",
    scenarioSalaryIncreaseParam = "income +20% monthly, permanent",
    scenarioSalaryIncreaseDesc = { amt -> "Income increase: +$amt/mo." },

    scenarioMarketGrowthTitle = "Market Growth +25%",
    scenarioMarketGrowthParam = "ETF +25%",
    scenarioMarketGrowthDesc = { amt -> "Financial portfolio gain: +$amt" },

    scenarioExpenseOptTitle = "Expense Optimization",
    scenarioExpenseOptParam = "−15% to monthly expenses, permanent",
    scenarioExpenseOptDesc = { amt -> "Ongoing savings: -$amt/mo." },

    scenarioRelocationTitle = "Relocation to Lower-Cost Region",
    scenarioRelocationParam = "−30% to monthly expenses, permanent",
    scenarioRelocationDesc = { amt -> "Living cost reduction: -$amt/mo." },

    scenarioInheritanceTitle = "Inheritance or Windfall Gift",
    scenarioInheritanceParam = "one-time windfall 1200% monthly income",
    scenarioInheritanceDesc = { amt -> "Capital inflow: +$amt (into liquid assets)" },

    scenarioPropertySaleTitle = "Sale of Property (car, land, real estate)",
    scenarioPropertySaleParam = "one-time windfall 2000% monthly income",
    scenarioPropertySaleDesc = { amt -> "Sale proceeds: +$amt (into liquid assets)" },

    scenarioChildrenSelfSufficientTitle = "Children Independent",
    scenarioChildrenSelfSufficientParam = "expenses −25% monthly, after 18 yrs",
    scenarioChildrenSelfSufficientDesc = { amt -> "Family expense reduction: -$amt/mo. (after 18 yrs)" },

    scenarioJobLossTitle = "Job Loss",
    scenarioJobLossParam = "income = 0",
    scenarioJobLossDesc = "Complete income stoppage, covering all living expenses from emergency reserves",

    scenarioMarketCrashTitle = "Market Crash −30%",
    scenarioMarketCrashParam = "ETF −30%",
    scenarioMarketCrashDesc = { amt -> "Portfolio drawdown of 30% ($amt)" },

    scenarioMortgageTitle = "Home Mortgage",
    scenarioMortgageParam = "35% income for 20 yrs (+84 incomes into property)",
    scenarioMortgageDesc = { pay, asset -> "Payment: $pay/mo. (20 yrs). At completion: +$asset in property assets" },

    scenarioCarLoanTitle = "Car Loan",
    scenarioCarLoanParam = "20% income for 5 yrs (+6 incomes into property)",
    scenarioCarLoanDesc = { pay, asset -> "Payment: $pay/mo. (5 yrs). At completion: +$asset in property assets (50% value)" },

    scenarioLargeExpenseTitle = "Major One-Off Expense",
    scenarioLargeExpenseParam = "expense 500% monthly income",
    scenarioLargeExpenseDesc = { amt -> "Outflow of $amt (covered from liquid and financial assets)" },

    scenarioInflationTitle = "Inflation Surge",
    scenarioInflationParam = "+20% to expenses",
    scenarioInflationDesc = { amt -> "Cost of living increase: +$amt/mo." },

    scenarioChildTitle = "Child Birth",
    scenarioChildParam = "+25% to expenses for 18 yrs",
    scenarioChildDesc = { amt -> "Childcare costs: +$amt/mo. (for 18 years)" }
)

fun getAppStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.RU -> StringsRu
        AppLanguage.EN -> StringsEn
    }
}

val LocalAppStrings = compositionLocalOf { StringsRu }
val LocalAppLanguage = compositionLocalOf { AppLanguage.RU }
