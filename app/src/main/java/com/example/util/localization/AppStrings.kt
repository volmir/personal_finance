package com.example.util.localization

import androidx.compose.runtime.compositionLocalOf

data class BreakdownCategory(
    val title: String,
    val items: List<String>
)

interface AppStrings {
    val language: AppLanguage

    // App & Navigation
    val appName: String
    val tabInputTitle: String
    val tabInputShort: String
    val tabMetricsTitle: String
    val tabMetricsShort: String
    val tabScoresTitle: String
    val tabScoresShort: String
    val tabFireTitle: String
    val tabFireShort: String
    val quickScorePrefix: String

    // Input Screen
    val block1Title: String
    val block1Subtitle: String
    val monthlyIncomeLabel: String
    val monthlyExpensesLabel: String
    val incomeBreakdownName: String
    val incomeHintCollapsed: String
    val incomeHintExpanded: String
    val expensesBreakdownName: String
    val expensesHintCollapsed: String
    val expensesHintExpanded: String
    val breakdownInstruction: String
    val incomeCategories: List<BreakdownCategory>
    val expensesCategories: List<BreakdownCategory>
    val cashFlowTitle: String
    val cashFlowFormula: String
    val savingsRateTitle: String
    val savingsRateFormula: String

    val block2Title: String
    val block2Subtitle: String
    val liquidAssetsLabel: String
    val financialAssetsLabel: String
    val propertyAssetsLabel: String
    val assetsBreakdownName: String
    val assetsHintCollapsed: String
    val assetsHintExpanded: String
    val assetsCategories: List<BreakdownCategory>
    val totalAssetsTitle: String
    val totalAssetsFormula: String
    val toggleEmergencyFundTitle: String
    val toggleEmergencyFundSubOn: String
    val toggleEmergencyFundSubOff: String

    val block3Title: String
    val block3Subtitle: String
    val liabilitiesLabel: String
    val liabilitiesBreakdownName: String
    val liabilitiesHintCollapsed: String
    val liabilitiesHintExpanded: String
    val liabilitiesCategories: List<BreakdownCategory>
    val netWorthTitle: String
    val netWorthFormula: String

    val block4Title: String
    val block4Subtitle: String
    val nominalReturnLabel: String
    val inflationLabel: String
    val investmentsBreakdownName: String
    val investmentsHintCollapsed: String
    val investmentsHintExpanded: String
    val investmentsCategories: List<BreakdownCategory>
    val realReturnTitle: String
    val realReturnFormula: String

    val block5Title: String
    val block5Subtitle: String
    val birthDateLabel: String
    val lifeExpectancyLabel: String
    val fireHorizonBreakdownName: String
    val fireHorizonHintCollapsed: String
    val fireHorizonHintExpanded: String
    val fireHorizonCategories: List<BreakdownCategory>
    val horizonTitle: String
    val horizonFormula: String
    val horizonValueFormat: (horizon: Int, age: Int) -> String

    val calculateButtonText: String
    val exportButtonText: String
    val exportChooserTitle: String

    // Metrics Screen
    val metricsHeaderTitle: String
    val metricsHeaderSubtitle: String
    val categoryAll: String
    val categoryCashFlow: String
    val categoryCapitalRisks: String
    val categoryBenchmarks: String
    val metricCardInfo: String
    val metricCardHide: String
    val metricCardFormulaPrefix: String
    val metricCardExpand: String
    val metricCardCollapse: String

    val metricCashFlowTitle: String
    val metricCashFlowFormula: String
    val metricCashFlowDesc: String
    val metricCashFlowPositiveSub: String
    val metricCashFlowNegativeSub: String

    val metricSavingsRateTitle: String
    val metricSavingsRateFormula: String
    val metricSavingsRateDesc: String
    val metricSavingsRateSub: String

    val metricEmergencyFundTitle: String
    val metricEmergencyFundFormula: String
    val metricEmergencyFundDesc: String
    val metricEmergencyFundSubLiquidPlusFin: String
    val metricEmergencyFundSubOnlyLiquid: String

    val metricSafetyMonthsTitle: String
    val metricSafetyMonthsFormula: String
    val metricSafetyMonthsDesc: String
    val metricSafetyMonthsSub: String
    val monthsSuffix: String

    val metricDebtRatioTitle: String
    val metricDebtRatioFormula: String
    val metricDebtRatioDesc: String
    val metricDebtRatioLowSub: String
    val metricDebtRatioHighSub: String

    val metricNetWorthTitle: String
    val metricNetWorthFormula: String
    val metricNetWorthDesc: String
    val metricNetWorthSubPrefix: String
    val metricNetWorthNegativeSub: String

    val metricFidelityTitle: String
    val metricFidelityFormula: String
    val metricFidelityDesc: String
    val metricFidelitySubFormat: (age: Int, mult: String) -> String
    val metricFidelityProgress: String

    val metricCfsTitle: String
    val metricCfsFormula: String
    val metricCfsDesc: String

    val metricRealReturnTitle: String
    val metricRealReturnFormula: String
    val metricRealReturnDesc: String
    val metricRealReturnSubFormat: (nom: String, infl: String) -> String

    // Scores Screen
    val scoresHeaderTitle: String
    val scoresHeaderSubtitle: String
    val scoresShareReportChooser: String
    val scoresGaugeOutOf: (max: Int) -> String
    val scoresTotalResult: (score: String) -> String
    val scoresStatusExcellent: String
    val scoresStatusGood: String
    val scoresStatusModerate: String
    val scoresStatusAttention: String
    val scoresBreakdownTitle: String
    val scoreValuePrefix: String
    val scoreOutOfTen: (score: Int) -> String
    val savingsRateScoreComment: (rate: Double, sr: String) -> String
    val safetyMonthsScoreComment: (months: Double, monthsText: String) -> String
    val debtComment0: String
    val debtComment1: String
    val debtComment2: String
    val debtComment3: String

    val auditorOpinionTitle: String
    val auditorOpinionSubtitle: (count: Int) -> String
    val shareAuditorOpinionChooser: String
    val positiveBadgeText: (count: Int) -> String
    val neutralBadgeText: (count: Int) -> String
    val negativeBadgeText: (count: Int) -> String
    val positiveSectionTitle: (count: Int) -> String
    val neutralSectionTitle: (count: Int) -> String
    val negativeSectionTitle: (count: Int) -> String
    val positiveEmptyText: String
    val neutralEmptyText: String
    val negativeEmptyText: String

    // FIRE Screen
    val fireHeroTitle: String
    val fireHeroSubtitle: String
    val fireTimeToRetire: String
    val fireYearsSuffix: String
    val fireHeroAssumption: String
    val fireRetirementAgeLabel: String
    val fireRetirementAgeValue: (age: String) -> String
    val fireTargetCapitalLabel: String
    val fireParamsTitle: String
    val fireBirthDateLabel: String
    val fireCurrentAgeLabel: String
    val fireCurrentAgeValue: (age: Int) -> String
    val fireLifeExpectancyLabel: String
    val fireLifeExpectancyValue: (exp: Int) -> String
    val fireHorizonLabel: String
    val fireHorizonValue: (h: Int) -> String
    val fireRealReturnLabel: String
    val fireTrajectoryTitle: String
    val fireTrajectorySubtitle: String
    val fireChartAccumulated: String
    val fireChartRequired: String
    val fireChartYearsSuffix: String
    val fireChartFirePoint: (age: String) -> String
    val fireSimSummaryTitle: String
    val fireSimEarlier: (diff: String, target: String, age: String) -> String
    val fireSimLater: (diff: String, target: String, age: String) -> String
    val fireSimSame: (target: String) -> String
    val fireSimCashFlowRow: String
    val fireSimExpensesRow: String
    val fireSimNetWorthRow: String
    val fireSimPropertyRow: String
    val fireSimSliderTitle: String
    val fireSimReset: String
    val fireSimFineTuning: String
    val fireSliderSavings: String
    val fireSliderExpenses: String
    val fireSliderReturn: String
    val fireSliderBaseSuffix: String
    val fireSliderBaseReturnSuffix: String
    val fireSliderFrom: (v: String) -> String
    val fireSliderStep: (v: String) -> String
    val fireSliderTo: (v: String) -> String
    val fireSliderInc: String
    val fireSliderDec: String

    // Scenario Panel
    val scenarioHeaderTitle: String
    val scenarioHeaderSubtitle: String
    val scenarioTotalBadge: (count: Int) -> String
    val scenarioPositiveTitle: String
    val scenarioPositiveActive: (count: Int) -> String
    val scenarioPositiveSubtitle: String
    val scenarioNegativeTitle: String
    val scenarioNegativeActive: (count: Int) -> String
    val scenarioNegativeSubtitle: String
    val scenarioParamsPrefix: String

    // Scenarios Items (titles, parameters, descriptions)
    val scenarioSalaryIncreaseTitle: String
    val scenarioSalaryIncreaseParam: String
    val scenarioSalaryIncreaseDesc: (amt: String) -> String

    val scenarioMarketGrowthTitle: String
    val scenarioMarketGrowthParam: String
    val scenarioMarketGrowthDesc: (amt: String) -> String

    val scenarioExpenseOptTitle: String
    val scenarioExpenseOptParam: String
    val scenarioExpenseOptDesc: (amt: String) -> String

    val scenarioRelocationTitle: String
    val scenarioRelocationParam: String
    val scenarioRelocationDesc: (amt: String) -> String

    val scenarioInheritanceTitle: String
    val scenarioInheritanceParam: String
    val scenarioInheritanceDesc: (amt: String) -> String

    val scenarioPropertySaleTitle: String
    val scenarioPropertySaleParam: String
    val scenarioPropertySaleDesc: (amt: String) -> String

    val scenarioChildrenSelfSufficientTitle: String
    val scenarioChildrenSelfSufficientParam: String
    val scenarioChildrenSelfSufficientDesc: (amt: String) -> String

    val scenarioJobLossTitle: String
    val scenarioJobLossParam: String
    val scenarioJobLossDesc: String

    val scenarioMarketCrashTitle: String
    val scenarioMarketCrashParam: String
    val scenarioMarketCrashDesc: (amt: String) -> String

    val scenarioMortgageTitle: String
    val scenarioMortgageParam: String
    val scenarioMortgageDesc: (pay: String, asset: String) -> String

    val scenarioCarLoanTitle: String
    val scenarioCarLoanParam: String
    val scenarioCarLoanDesc: (pay: String, asset: String) -> String

    val scenarioLargeExpenseTitle: String
    val scenarioLargeExpenseParam: String
    val scenarioLargeExpenseDesc: (amt: String) -> String

    val scenarioInflationTitle: String
    val scenarioInflationParam: String
    val scenarioInflationDesc: (amt: String) -> String

    val scenarioChildTitle: String
    val scenarioChildParam: String
    val scenarioChildDesc: (amt: String) -> String
}

val StringsRu: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.RU

    override val appName: String = "Личные финансы & FIRE аудит"
    override val tabInputTitle: String = "Данные для расчетов"
    override val tabInputShort: String = "Данные"
    override val tabMetricsTitle: String = "Расчет показателей"
    override val tabMetricsShort: String = "Показатели"
    override val tabScoresTitle: String = "Набранные баллы (итоги, суммирование результатов)"
    override val tabScoresShort: String = "Итоги & Баллы"
    override val tabFireTitle: String = "Финансовая свобода, пенсия, FIRE"
    override val tabFireShort: String = "FIRE & Пенсия"
    override val quickScorePrefix: String = "Итог"

    override val block1Title: String = "1. Доходы и Расходы"
    override val block1Subtitle: String = "Ежемесячный приток и отток денежных средств"
    override val monthlyIncomeLabel: String = "Доходы (в месяц)"
    override val monthlyExpensesLabel: String = "Расходы (в месяц)"
    override val incomeBreakdownName: String = "Доходы"
    override val incomeHintCollapsed: String = "Что входит в «Доходы»? (подсказка)"
    override val incomeHintExpanded: String = "Скрыть структуру показателя «Доходы»"
    override val expensesBreakdownName: String = "Расходы"
    override val expensesHintCollapsed: String = "Что входит в «Расходы»? (подсказка)"
    override val expensesHintExpanded: String = "Скрыть структуру показателя «Расходы»"
    override val breakdownInstruction: String = "Посчитайте и сложите ваши статьи из списка ниже самостоятельно, а полученную итоговую сумму укажите в поле выше."
    override val incomeCategories: List<BreakdownCategory> = listOf(
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
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
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
    )
    override val cashFlowTitle: String = "Денежный поток:"
    override val cashFlowFormula: String = "(Доходы - Расходы)"
    override val savingsRateTitle: String = "Норма сбережений:"
    override val savingsRateFormula: String = "(Денежный поток / Доходы) * 100%"

    override val block2Title: String = "2. Активы (Σ)"
    override val block2Subtitle: String = "Ликвидные, финансовые и имущественные накопления"
    override val liquidAssetsLabel: String = "Ликвидные (наличные, вклады до востребования)"
    override val financialAssetsLabel: String = "Финансовые (акции, облигации, фонды, брокерские счета)"
    override val propertyAssetsLabel: String = "Имущественные (недвижимость, автомобили, ценности)"
    override val assetsBreakdownName: String = "Активы"
    override val assetsHintCollapsed: String = "Что входит в «Активы»? (подсказка)"
    override val assetsHintExpanded: String = "Скрыть структуру показателя «Активы»"
    override val assetsCategories: List<BreakdownCategory> = listOf(
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
    )
    override val totalAssetsTitle: String = "Активы (Σ):"
    override val totalAssetsFormula: String = "(Ликвидные + Финансовые + Имущественные)"
    override val toggleEmergencyFundTitle: String = "Включать финансовые активы в подушку"
    override val toggleEmergencyFundSubOn: String = "Подушка = Ликвидные + Финансовые"
    override val toggleEmergencyFundSubOff: String = "Подушка = Только Ликвидные"

    override val block3Title: String = "3. Пассивы"
    override val block3Subtitle: String = "Обязательства, кредиты, долги и ипотека"
    override val liabilitiesLabel: String = "Пассивы (все долги и обязательства)"
    override val liabilitiesBreakdownName: String = "Пассивы"
    override val liabilitiesHintCollapsed: String = "Что входит в «Пассивы»? (подсказка)"
    override val liabilitiesHintExpanded: String = "Скрыть структуру показателя «Пассивы»"
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
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
    )
    override val netWorthTitle: String = "Чистый капитал:"
    override val netWorthFormula: String = "(Активы - Пассивы)"

    override val block4Title: String = "4. Инвестиции & Инфляция"
    override val block4Subtitle: String = "Параметры доходности капитала и покупательской способности"
    override val nominalReturnLabel: String = "Номинальная %"
    override val inflationLabel: String = "Инфляция %"
    override val investmentsBreakdownName: String = "Инвестиции & Инфляция"
    override val investmentsHintCollapsed: String = "Что учитывать в «Инвестициях и инфляции»? (подсказка)"
    override val investmentsHintExpanded: String = "Скрыть подсказку по инвестициям и инфляции"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Номинальная доходность капитала (% годовых)",
            items = listOf(
                "Средневзвешенная доходность всего вашего инвестиционного портфеля",
                "Проценты по банковским вкладам и накопительным счетам",
                "Купоны по облигациям и ожидаемые дивиденды по акциям",
                "Чистый арендный доход от сдачи недвижимости или оборудования",
                "Ориентир для развитых стран (США, ЕС и др.): 6–9% в твердой валюте",
                "Ориентир для развивающихся рынков: 10–16% в национальной валюте"
            )
        ),
        BreakdownCategory(
            title = "Ожидаемая среднегодовая инфляция (% годовых)",
            items = listOf(
                "Официальные ориентиры центральных банков и финансовых регуляторов",
                "Личная потребительская инфляция по вашей структуре регулярных трат",
                "Историческая средняя инфляция за последние 5–10 лет",
                "Ориентир для развитых стран: 2–3% в год (исторически 2–4%)",
                "Ориентир для развивающихся рынков: в среднем 5–9% в год"
            )
        ),
        BreakdownCategory(
            title = "Реальная доходность капитала (формула Фишера)",
            items = listOf(
                "Формула: ((1 + Номинальная доходность) / (1 + Инфляция) - 1) * 100%",
                "Очищенный от инфляции прирост реальной покупательной способности",
                "Типовой ориентир реальной доходности: 3–5% для развитых стран и 3–6% для развивающихся рынков",
                "Положительное значение (> 0%) приумножает богатство и защищает от обесценивания"
            )
        )
    )
    override val realReturnTitle: String = "Реальная доходность капитала:"
    override val realReturnFormula: String = "(1 + Номинальная доходность) / (1 + Инфляция) - 1"

    override val block5Title: String = "5. Возраст & Горизонт FIRE"
    override val block5Subtitle: String = "Параметры для расчета выхода на пенсию"
    override val birthDateLabel: String = "Дата рождения (ГГГГ-ММ-ДД)"
    override val lifeExpectancyLabel: String = "Дожитие (лет)"
    override val fireHorizonBreakdownName: String = "Возраст & Горизонт FIRE"
    override val fireHorizonHintCollapsed: String = "Как определить возраст и горизонт дожития? (подсказка)"
    override val fireHorizonHintExpanded: String = "Скрыть подсказку по возрасту и горизонту"
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Дата рождения и текущий возраст",
            items = listOf(
                "Используется для расчета нормативного капитала по возрасту (Fidelity)",
                "Определяет оставшееся время для накопления капитала сложным процентом",
                "Влияет на баланс риска: доля защитных и доходных активов в портфеле"
            )
        ),
        BreakdownCategory(
            title = "Ожидаемый возраст дожития (лет)",
            items = listOf(
                "Возраст, до которого финансовая модель рассчитывает обеспечение расходов",
                "Статистический ориентир: для мужчин 75–85 лет, для женщин 80–90 лет",
                "Для консервативного и надежного расчета рекомендуется указывать 85–95 лет",
                "Запас по возрасту гарантирует, что капитал не иссякнет при долгожительстве"
            )
        ),
        BreakdownCategory(
            title = "Горизонт планирования (лет)",
            items = listOf(
                "Разница: Возраст дожития минус Текущий возраст",
                "Период, в течение которого накопления должны поддерживать уровень жизни",
                "Ключевой параметр математической модели достижения пенсионной свободы FIRE"
            )
        )
    )
    override val horizonTitle: String = "Горизонт дожития:"
    override val horizonFormula: String = "(Возраст дожития - Текущий возраст)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon года (возраст $age лет)" }

    override val calculateButtonText: String = "Рассчитать показатели"
    override val exportButtonText: String = "Экспорт результатов"
    override val exportChooserTitle: String = "Экспорт результатов"

    override val metricsHeaderTitle: String = "Расчет показателей"
    override val metricsHeaderSubtitle: String = "Все ключевые метрики с формулами и оценкой"
    override val categoryAll: String = "Все"
    override val categoryCashFlow: String = "Денежный поток"
    override val categoryCapitalRisks: String = "Капитал & Риски"
    override val categoryBenchmarks: String = "Бенчмарки"
    override val metricCardInfo: String = "Инфо"
    override val metricCardHide: String = "Скрыть"
    override val metricCardFormulaPrefix: String = "Формула: "
    override val metricCardExpand: String = "Развернуть"
    override val metricCardCollapse: String = "Свернуть"

    override val metricCashFlowTitle: String = "Денежный поток"
    override val metricCashFlowFormula: String = "(Доходы - Расходы)"
    override val metricCashFlowDesc: String = "Cash Flow. Движение денег (приток и отток) за период.\n\n• Положительное значение: Вы зарабатываете больше, чем тратите.\n• Отрицательное значение: Вы тратите больше, чем зарабатываете."
    override val metricCashFlowPositiveSub: String = "Положительный приток денег"
    override val metricCashFlowNegativeSub: String = "Дефицит бюджета"

    override val metricSavingsRateTitle: String = "Норма сбережений"
    override val metricSavingsRateFormula: String = "(Денежный поток / Доходы) * 100%"
    override val metricSavingsRateDesc: String = "Рекомендуется стремиться к норме сбережений более 20% от общего дохода.\n\n• Меньше 0%: Негативный показатель (дефицит)\n• 0% — 20%: Нейтральный показатель (умеренно)\n• Более 20%: Позитивный показатель (отлично)"
    override val metricSavingsRateSub: String = "Целевой ориентир: > 20% (0–20% нейтрально)"

    override val metricEmergencyFundTitle: String = "Подушка безопасности"
    override val metricEmergencyFundFormula: String = "(Ликвидные активы)"
    override val metricEmergencyFundDesc: String = "Личная финансовая «подушка безопасности» или запас денег, созданный для покрытия непредвиденных расходов (болезни, потеря работы и т.п.) и обеспечения финансовой стабильности в экстренных ситуациях, чтобы не влезать в долги."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Ликвидные + финансовые"
    override val metricEmergencyFundSubOnlyLiquid: String = "Только ликвидные"

    override val metricSafetyMonthsTitle: String = "Запас месячных расходов"
    override val metricSafetyMonthsFormula: String = "(Подушка безопасности / Ежемесячные расходы)"
    override val metricSafetyMonthsDesc: String = "Количество месяцев, которые вы можете прожить на средства из \"подушки безопасности\".\n\n• Минимальный уровень: 3-6 месяцев основных расходов (аренда/ипотека, еда, транспорт, коммунальные услуги, связь). Это «спокойное» существование при нулевых доходах.\n• Идеальный уровень: Может включать больше, например, до 12 месяцев, для более уверенного прохождения длительных кризисов, особенно при нестабильной работе или наличии иждивенцев."
    override val metricSafetyMonthsSub: String = "Норма: 3-6 мес, Идеал: 12+ мес"
    override val monthsSuffix: String = "мес."

    override val metricDebtRatioTitle: String = "Коэффициент задолженности"
    override val metricDebtRatioFormula: String = "(Пассивы / Активы)"
    override val metricDebtRatioDesc: String = "Показывает, какая доля активов финансируется за счет долгов (обязательств), отражая степень финансового риска и зависимости от кредиторов.\n\n• 0,0 - 0,3: Хорошая финансовая устойчивость, большая часть активов финансируется собственными средствами\n• 0,3 - 0,6: Идеальный баланс для большинства, сбалансированное кредитное плечо\n• Больше 0,6: Высокий риск, сильная зависимость от долгов\n• Больше 1,0: Долгов больше, чем активов; очень высокий риск"
    override val metricDebtRatioLowSub: String = "Низкий риск (отлично)"
    override val metricDebtRatioHighSub: String = "Высокий риск долгов"

    override val metricNetWorthTitle: String = "Чистый капитал"
    override val metricNetWorthFormula: String = "(Активы - Пассивы)"
    override val metricNetWorthDesc: String = "Общая стоимость активов за вычетом всех обязательств. Это те средства, что останутся у собственника после продажи всех активов и погашения всех долгов."
    override val metricNetWorthSubPrefix: String = "Активы: "
    override val metricNetWorthNegativeSub: String = "Отрицательный баланс (дефицит!)"

    override val metricFidelityTitle: String = "Бенчмарк: целевые показатели по Fidelity"
    override val metricFidelityFormula: String = "Годовой доход * Множитель"
    override val metricFidelityDesc: String = "Методика Fidelity показывает, во сколько раз ваши текущие пенсионные накопления должны превышать ваш годовой заработок на определенном жизненном этапе.\n\nШкала Fidelity дискретна (задана конкретными точками для 30, 40, 50, 60 и 67 лет).\nПример: Если вам 40 лет и вы зарабатываете ($100,000) в год, то, согласно Fidelity, на ваших инвестиционных и пенсионных счетах должно быть накоплено не менее ($300,000)."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Возраст: $age лет | Множитель: ${mult}x" }
    override val metricFidelityProgress: String = "Прогресс к ориентиру Fidelity:"

    override val metricCfsTitle: String = "Коэффициент финансовой свободы"
    override val metricCfsFormula: String = "(Чистые активы / Годовые расходы)"
    override val metricCfsDesc: String = "Показывает, сколько годовых расходов покрывают ваши чистые активы (чистый капитал).\n\n• КФС < 0 — Вы в долгах\n• КФС >= 0 и КФС <= 0,5 — У вас есть минимальная «подушка безопасности»\n• КФС > 0,5 и КФС <= 2 — Вы начали формировать свой финансовый капитал\n• КФС > 2 и КФС <= 5 — Вы можете начать заниматься долгосрочными инвестициями\n• КФС > 5 и КФС <= 15 — Доходы от капитала могут приносить вам существенную прибавку. Инфляция все более значительно влияет на капитал\n• КФС > 15 и КФС <= 30 — Крепкая финансовая стабильность. На что вы тратите свой невосполнимый капитал времени?\n• КФС > 30 — Вы достигли финансовой независимости"

    override val metricRealReturnTitle: String = "Реальная доходность капитала, %"
    override val metricRealReturnFormula: String = "(1 + Номинальная доходность) / (1 + Инфляция) - 1"
    override val metricRealReturnDesc: String = "Фактический прирост ваших сбережений после того, как из начисленной прибыли вычли уровень инфляции. Показывает, на сколько увеличилась ваша реальная покупательная способность."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Номинал: $nom | Инфляция: $infl" }

    override val scoresHeaderTitle: String = "Набранные баллы"
    override val scoresHeaderSubtitle: String = "Итоги и суммирование результатов"
    override val scoresShareReportChooser: String = "Поделиться отчетом"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "из $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Итоговый результат: $score из 10" }
    override val scoresStatusExcellent: String = "Отличная финансовая устойчивость и готовность к долгосрочным инвестициям."
    override val scoresStatusGood: String = "Хорошая финансовая форма с потенциалом роста нормы накопления."
    override val scoresStatusModerate: String = "Умеренная стабильность. Рекомендуется нарастить подушку безопасности."
    override val scoresStatusAttention: String = "Требуется оптимизация расходов и погашение долговых обязательств."
    override val scoresBreakdownTitle: String = "Детализация баллов по компонентам:"
    override val scoreValuePrefix: String = "Значение: "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score из 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String) -> String = { rate, sr ->
        when {
            rate > 20.0 -> "Ориентир более 20%. У вас отличное значение ($sr)."
            rate >= 0.0 -> "Ориентир более 20%. У вас низкое значение ($sr)."
            else -> "Ориентир более 20%. У вас отрицательное значение ($sr), дефицит бюджета."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String) -> String = { months, monthsText ->
        when {
            months >= 12.0 -> "Минимум 3-6 месяцев, идеал 12+ мес. Отличный результат: запас превышает 12 месяцев ($monthsText)."
            months >= 6.0 -> "Минимум 3-6 месяцев, идеал 12+ мес. Хороший запас ($monthsText), норматив в 6 месяцев достигнут."
            months >= 3.0 -> "Минимум 3-6 месяцев, идеал 12+ мес. Базовый запас ($monthsText), рекомендуется увеличить до 6-12 месяцев."
            else -> "Минимум 3-6 месяцев, идеал 12+ мес. У вас недостаточный запас ($monthsText), высокий финансовый риск."
        }
    }
    override val debtComment0: String = "0,0 - 0,3: Отличная устойчивость, активы финансируются собственными средствами."
    override val debtComment1: String = "0,3 - 0,6: Сбалансированное кредитное плечо, умеренная нагрузка."
    override val debtComment2: String = "0,6 - 1,0: Высокий риск, значительная зависимость от заемных средств."
    override val debtComment3: String = "Более 1,0: Критический риск, долги превышают стоимость активов."

    override val auditorOpinionTitle: String = "Аудиторское заключение"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Анализ по всем $count показателям экрана «Показатели»" }
    override val shareAuditorOpinionChooser: String = "Поделиться аудиторским заключением"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count позит." }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count нейтр." }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count негатив." }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Позитивные показатели ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Нейтральные показатели ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Негативные показатели ($count)" }
    override val positiveEmptyText: String = "Позитивных показателей пока не выявлено. Рекомендуется оптимизировать доходы и расходы."
    override val neutralEmptyText: String = "Нейтральных показателей нет — все метрики в позитивной или негативной зоне."
    override val negativeEmptyText: String = "🎉 Критических и негативных факторов не выявлено — все показатели в безопасной зоне!"

    override val fireHeroTitle: String = "Финансовая свобода, пенсия, FIRE"
    override val fireHeroSubtitle: String = "Расчет срока до выхода на пенсию"
    override val fireTimeToRetire: String = "До пенсии осталось:"
    override val fireYearsSuffix: String = " года (лет)"
    override val fireHeroAssumption: String = "(с учетом будущих сбережений, инфляции и доходности капитала)"
    override val fireRetirementAgeLabel: String = "Возраст выхода на пенсию:"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age лет" }
    override val fireTargetCapitalLabel: String = "Требуемый целевой капитал:"
    override val fireParamsTitle: String = "Параметры возраста и горизонта"
    override val fireBirthDateLabel: String = "Дата рождения:"
    override val fireCurrentAgeLabel: String = "Текущий возраст:"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age лет" }
    override val fireLifeExpectancyLabel: String = "Возраст дожития:"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp лет" }
    override val fireHorizonLabel: String = "(Возраст дожития - Текущий возраст):"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h года" }
    override val fireRealReturnLabel: String = "Реальная доходность капитала:"
    override val fireTrajectoryTitle: String = "Траектория накопления капитала"
    override val fireTrajectorySubtitle: String = "Рост капитала до точки FIRE и обеспечение жизни на пенсии"
    override val fireChartAccumulated: String = "Накопленный капитал"
    override val fireChartRequired: String = "Необходимый капитал"
    override val fireChartYearsSuffix: String = " лет"
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE: $age лет" }
    override val fireSimSummaryTitle: String = "Итог симуляции (совместное влияние)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ Пенсия наступит на $diff года раньше (через $target лет, в возрасте $age лет)!" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Срок до пенсии увеличится на $diff года (через $target лет, в возрасте $age лет)." }
    override val fireSimSame: (target: String) -> String = { target -> "Срок выхода на пенсию остается прежним (через $target лет)." }
    override val fireSimCashFlowRow: String = "Денежный поток (сбережения):"
    override val fireSimExpensesRow: String = "Ежемесячные расходы:"
    override val fireSimNetWorthRow: String = "Чистый капитал (активы − долги):"
    override val fireSimPropertyRow: String = "Прирост имущества (по окончании выплат):"
    override val fireSimSliderTitle: String = "Симулятор «Что если?»"
    override val fireSimReset: String = "Сброс"
    override val fireSimFineTuning: String = "Тонкая настройка шкал (ползунки):"
    override val fireSliderSavings: String = "Ежемесячные сбережения:"
    override val fireSliderExpenses: String = "Ежемесячные расходы:"
    override val fireSliderReturn: String = "Реальная доходность капитала:"
    override val fireSliderBaseSuffix: String = " к базовым"
    override val fireSliderBaseReturnSuffix: String = "% к базовой"
    override val fireSliderFrom: (v: String) -> String = { v -> "от $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "шаг $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "до +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "рост"
    override val fireSliderDec: String = "сниж."

    override val scenarioHeaderTitle: String = "Панель сценариев (стресс-тесты)"
    override val scenarioHeaderSubtitle: String = "Результат влияния разных ползунков и чекбоксов суммируется (накапливается и рассчитывается совместно). Итоговый результат отражается в числах и на графике:"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Всего: $count" }
    override val scenarioPositiveTitle: String = "Позитивные события"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Активно: $count" }
    override val scenarioPositiveSubtitle: String = "События, ускоряющие накопление капитала и выход на пенсию:"
    override val scenarioNegativeTitle: String = "Негативные события"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Активно: $count" }
    override val scenarioNegativeSubtitle: String = "Стресс-тесты, непредвиденные расходы и кризисные ситуации:"
    override val scenarioParamsPrefix: String = "Параметры: "

    override val scenarioSalaryIncreaseTitle: String = "Повышение зарплаты"
    override val scenarioSalaryIncreaseParam: String = "доход +20% ежемесячно, постоянно"
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Увеличение дохода: +$amt/мес." }

    override val scenarioMarketGrowthTitle: String = "Рост рынка на 25%"
    override val scenarioMarketGrowthParam: String = "ETF +25%"
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Прирост финансового портфеля: +$amt" }

    override val scenarioExpenseOptTitle: String = "Оптимизация расходов"
    override val scenarioExpenseOptParam: String = "−15% к ежемесячным расходам, постоянно"
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Постоянная экономия: -$amt/мес." }

    override val scenarioRelocationTitle: String = "Переезд в регион с более низкой стоимостью жизни"
    override val scenarioRelocationParam: String = "−30% к ежемесячным расходам, постоянно"
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Снижение стоимости жизни: -$amt/мес." }

    override val scenarioInheritanceTitle: String = "Наследство или крупный подарок"
    override val scenarioInheritanceParam: String = "разовое поступление 1200% мес. дохода"
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Приток капитала: +$amt (в ликвидные активы)" }

    override val scenarioPropertySaleTitle: String = "Продажа имущества (авто, участок, недвижимость)"
    override val scenarioPropertySaleParam: String = "разовое поступление 2000% мес. дохода"
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Выручка от продажи: +$amt (в ликвидные активы)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Выход детей на самообеспечение"
    override val scenarioChildrenSelfSufficientParam: String = "расходы −25% ежемесячно, после 18 лет"
    override val scenarioChildrenSelfSufficientDesc: (amt: String) -> String = { amt -> "Снижение семейных расходов: -$amt/мес. (после 18 лет)" }

    override val scenarioJobLossTitle: String = "Потеря работы"
    override val scenarioJobLossParam: String = "доход = 0"
    override val scenarioJobLossDesc: String = "Полная остановка притока дохода, покрытие всех расходов из резервов"

    override val scenarioMarketCrashTitle: String = "Обвал рынка на 30%"
    override val scenarioMarketCrashParam: String = "ETF −30%"
    override val scenarioMarketCrashDesc: (amt: String) -> String = { amt -> "Просадка финансового портфеля на 30% ($amt)" }

    override val scenarioMortgageTitle: String = "Ипотека на квартиру"
    override val scenarioMortgageParam: String = "35% дохода на 20 лет (+84 дохода в имущество)"
    override val scenarioMortgageDesc: (pay: String, asset: String) -> String = { pay, asset -> "Платеж: $pay/мес. (20 лет). В конце срока: +$asset в имущество/активы" }

    override val scenarioCarLoanTitle: String = "Автомобиль в кредит"
    override val scenarioCarLoanParam: String = "20% дохода на 5 лет (+6 доходов в имущество)"
    override val scenarioCarLoanDesc: (pay: String, asset: String) -> String = { pay, asset -> "Платеж: $pay/мес. (5 лет). В конце срока: +$asset в имущество (50% стоимости)" }

    override val scenarioLargeExpenseTitle: String = "Крупная разовая трата"
    override val scenarioLargeExpenseParam: String = "расход 500% мес. дохода"
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Трата $amt (покрытие из ликвидных и фин. активов)" }

    override val scenarioInflationTitle: String = "Рост инфляции"
    override val scenarioInflationParam: String = "+5% к инфляции"
    override val scenarioInflationDesc: (rate: String) -> String = { rate -> "Инфляция возрастает до $rate (снижает реальную доходность капитала)" }

    override val scenarioChildTitle: String = "Рождение ребенка"
    override val scenarioChildParam: String = "+25% к расходам на 18 лет"
    override val scenarioChildDesc: (amt: String) -> String = { amt -> "Расходы на ребенка: +$amt/мес. (период 18 лет)" }
}

val StringsEn: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.EN

    override val appName: String = "Personal Finance & FIRE Audit"
    override val tabInputTitle: String = "Input Data & Parameters"
    override val tabInputShort: String = "Inputs"
    override val tabMetricsTitle: String = "Calculated Metrics"
    override val tabMetricsShort: String = "Metrics"
    override val tabScoresTitle: String = "Score Breakdown & Financial Audit"
    override val tabScoresShort: String = "Scores & Audit"
    override val tabFireTitle: String = "Financial Independence, Retirement, FIRE"
    override val tabFireShort: String = "FIRE & Plan"
    override val quickScorePrefix: String = "Score"

    override val block1Title: String = "1. Income & Expenses"
    override val block1Subtitle: String = "Monthly cash inflows and outflows"
    override val monthlyIncomeLabel: String = "Income (monthly)"
    override val monthlyExpensesLabel: String = "Expenses (monthly)"
    override val incomeBreakdownName: String = "Income"
    override val incomeHintCollapsed: String = "What is included in \"Income\"? (hint)"
    override val incomeHintExpanded: String = "Hide structure of \"Income\""
    override val expensesBreakdownName: String = "Expenses"
    override val expensesHintCollapsed: String = "What is included in \"Expenses\"? (hint)"
    override val expensesHintExpanded: String = "Hide structure of \"Expenses\""
    override val breakdownInstruction: String = "Calculate and add up your items from the list below yourself, and enter the resulting total in the field above."
    override val incomeCategories: List<BreakdownCategory> = listOf(
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
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
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
    )
    override val cashFlowTitle: String = "Cash Flow:"
    override val cashFlowFormula: String = "(Income - Expenses)"
    override val savingsRateTitle: String = "Savings Rate:"
    override val savingsRateFormula: String = "(Cash Flow / Income) * 100%"

    override val block2Title: String = "2. Assets (Σ)"
    override val block2Subtitle: String = "Liquid, financial and physical wealth accumulation"
    override val liquidAssetsLabel: String = "Liquid (cash, checking, demand deposits)"
    override val financialAssetsLabel: String = "Financial (stocks, bonds, ETFs, brokerage)"
    override val propertyAssetsLabel: String = "Physical / Property (real estate, vehicles, valuables)"
    override val assetsBreakdownName: String = "Assets"
    override val assetsHintCollapsed: String = "What is included in \"Assets\"? (hint)"
    override val assetsHintExpanded: String = "Hide structure of \"Assets\""
    override val assetsCategories: List<BreakdownCategory> = listOf(
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
    )
    override val totalAssetsTitle: String = "Assets (Σ):"
    override val totalAssetsFormula: String = "(Liquid + Financial + Property)"
    override val toggleEmergencyFundTitle: String = "Include financial assets in emergency buffer"
    override val toggleEmergencyFundSubOn: String = "Buffer = Liquid + Financial"
    override val toggleEmergencyFundSubOff: String = "Buffer = Liquid Only"

    override val block3Title: String = "3. Liabilities"
    override val block3Subtitle: String = "Debt obligations, loans, credit cards and mortgages"
    override val liabilitiesLabel: String = "Liabilities (all debts and obligations)"
    override val liabilitiesBreakdownName: String = "Liabilities"
    override val liabilitiesHintCollapsed: String = "What is included in \"Liabilities\"? (hint)"
    override val liabilitiesHintExpanded: String = "Hide structure of \"Liabilities\""
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
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
    )
    override val netWorthTitle: String = "Net Worth:"
    override val netWorthFormula: String = "(Assets - Liabilities)"

    override val block4Title: String = "4. Investments & Inflation"
    override val block4Subtitle: String = "Parameters of capital return and purchasing power"
    override val nominalReturnLabel: String = "Nominal return %"
    override val inflationLabel: String = "Inflation %"
    override val investmentsBreakdownName: String = "Investments & Inflation"
    override val investmentsHintCollapsed: String = "What to consider in \"Investments & Inflation\"? (hint)"
    override val investmentsHintExpanded: String = "Hide hint for investments and inflation"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Nominal Capital Return (% p.a.)",
            items = listOf(
                "Weighted average expected return of your total investment portfolio",
                "Interest yields from bank deposits and savings accounts",
                "Bond coupons and anticipated stock dividend yields",
                "Net rental yield from real estate or leased equipment",
                "Developed markets benchmark (US, EU, etc.): 6–9% in hard currency",
                "Emerging markets benchmark: 10–16% in local national currency"
            )
        ),
        BreakdownCategory(
            title = "Expected Annual Inflation (% p.a.)",
            items = listOf(
                "Central banks' official targets and economic forecasts",
                "Personal household inflation based on your living expenditure basket",
                "Historical average inflation rate over the past 5–10 years",
                "Developed markets benchmark: 2–3% annually (historically 2–4%)",
                "Emerging markets benchmark: typically 5–9% annually"
            )
        ),
        BreakdownCategory(
            title = "Real Capital Return (Fisher Equation)",
            items = listOf(
                "Formula: ((1 + Nominal Return) / (1 + Inflation) - 1) * 100%",
                "Inflation-adjusted growth of real purchasing power",
                "Typical real return benchmark: 3–5% p.a. for developed markets and 3–6% for emerging markets",
                "A positive rate (> 0%) protects wealth against depreciation and grows capital"
            )
        )
    )
    override val realReturnTitle: String = "Real Capital Return:"
    override val realReturnFormula: String = "(1 + Nominal Return) / (1 + Inflation) - 1"

    override val block5Title: String = "5. Age & Planning Horizon"
    override val block5Subtitle: String = "Parameters for retirement planning"
    override val birthDateLabel: String = "Birth date (YYYY-MM-DD)"
    override val lifeExpectancyLabel: String = "Life expectancy (yrs)"
    override val fireHorizonBreakdownName: String = "Age & FIRE Horizon"
    override val fireHorizonHintCollapsed: String = "How to determine age and life expectancy? (hint)"
    override val fireHorizonHintExpanded: String = "Hide hint for age and horizon"
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Birth Date and Current Age",
            items = listOf(
                "Used to calculate age-based capital benchmark milestones (Fidelity)",
                "Defines remaining accumulation timeline for compound wealth compounding",
                "Guides optimal asset allocation between growth and capital preservation"
            )
        ),
        BreakdownCategory(
            title = "Target Life Expectancy (Years)",
            items = listOf(
                "The maximum age target covered by your financial retirement model",
                "Statistical demographic baselines: 75–85 for men, 80–90 for women",
                "For conservative financial safety, setting 85–95 years is recommended",
                "A conservative longevity buffer ensures capital never runs out prematurely"
            )
        ),
        BreakdownCategory(
            title = "Planning Horizon (Years)",
            items = listOf(
                "Formula: Target life expectancy minus Current age",
                "The overall period during which capital must sustain your living expenses",
                "Core mathematical parameter driving required FIRE capital calculations"
            )
        )
    )
    override val horizonTitle: String = "Planning Horizon:"
    override val horizonFormula: String = "(Life expectancy - Current age)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon yrs (current age: $age yrs)" }

    override val calculateButtonText: String = "Calculate Metrics"
    override val exportButtonText: String = "Export Results"
    override val exportChooserTitle: String = "Export Results"

    override val metricsHeaderTitle: String = "Calculated Metrics"
    override val metricsHeaderSubtitle: String = "All key financial indicators with formulas & evaluation"
    override val categoryAll: String = "All"
    override val categoryCashFlow: String = "Cash Flow"
    override val categoryCapitalRisks: String = "Capital & Risks"
    override val categoryBenchmarks: String = "Benchmarks"
    override val metricCardInfo: String = "Info"
    override val metricCardHide: String = "Hide"
    override val metricCardFormulaPrefix: String = "Formula: "
    override val metricCardExpand: String = "Expand"
    override val metricCardCollapse: String = "Collapse"

    override val metricCashFlowTitle: String = "Cash Flow"
    override val metricCashFlowFormula: String = "(Income - Expenses)"
    override val metricCashFlowDesc: String = "Cash Flow. Net movement of funds (inflows and outflows) per period.\n\n• Positive value: You earn more than you spend.\n• Negative value: You spend more than you earn."
    override val metricCashFlowPositiveSub: String = "Positive cash surplus"
    override val metricCashFlowNegativeSub: String = "Budget deficit"

    override val metricSavingsRateTitle: String = "Savings Rate"
    override val metricSavingsRateFormula: String = "(Cash Flow / Income) * 100%"
    override val metricSavingsRateDesc: String = "It is recommended to aim for a savings rate above 20% of gross household income.\n\n• Below 0%: Negative indicator (deficit)\n• 0% — 20%: Neutral indicator (moderate)\n• Above 20%: Positive indicator (excellent)"
    override val metricSavingsRateSub: String = "Target benchmark: > 20% (0–20% neutral)"

    override val metricEmergencyFundTitle: String = "Emergency Fund"
    override val metricEmergencyFundFormula: String = "(Liquid Assets)"
    override val metricEmergencyFundDesc: String = "A personal safety net created to cover unforeseen emergencies (illness, job loss, emergency repairs) and safeguard financial stability without incurring high-interest debt."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Liquid + Financial"
    override val metricEmergencyFundSubOnlyLiquid: String = "Liquid Only"

    override val metricSafetyMonthsTitle: String = "Emergency Buffer (Months)"
    override val metricSafetyMonthsFormula: String = "(Emergency Fund / Monthly Expenses)"
    override val metricSafetyMonthsDesc: String = "The number of months you can sustain baseline living expenses solely from your emergency reserves.\n\n• Minimum target: 3-6 months of core living costs (rent/mortgage, food, utilities, transport). Guarantees calm recovery during income gaps.\n• Ideal target: Up to 12 months for comprehensive crisis resilience, especially for freelancers or households with dependents."
    override val metricSafetyMonthsSub: String = "Norm: 3-6 mo, Ideal: 12+ mo"
    override val monthsSuffix: String = "mo."

    override val metricDebtRatioTitle: String = "Debt-to-Asset Ratio"
    override val metricDebtRatioFormula: String = "(Liabilities / Assets)"
    override val metricDebtRatioDesc: String = "Measures the proportion of assets financed through debt obligations, reflecting financial risk exposure and dependency on creditors.\n\n• 0.0 - 0.3: Strong financial solvency, most assets funded by equity\n• 0.3 - 0.6: Balanced leverage for most households\n• Above 0.6: High risk, excessive debt dependency\n• Above 1.0: Liabilities exceed assets (insolvency risk)"
    override val metricDebtRatioLowSub: String = "Low risk (excellent)"
    override val metricDebtRatioHighSub: String = "High debt risk"

    override val metricNetWorthTitle: String = "Net Worth"
    override val metricNetWorthFormula: String = "(Assets - Liabilities)"
    override val metricNetWorthDesc: String = "The total value of all assets minus all outstanding liabilities. This represents what you truly own outright after settling all obligations."
    override val metricNetWorthSubPrefix: String = "Total assets: "
    override val metricNetWorthNegativeSub: String = "Negative equity (deficit!)"

    override val metricFidelityTitle: String = "Benchmark: Fidelity Capital Milestones"
    override val metricFidelityFormula: String = "Annual Income * Multiplier"
    override val metricFidelityDesc: String = "Fidelity's rule of thumb outlines how many times your current retirement portfolio should exceed your annual salary at specific life stages.\n\nCheckpoints: 1x by age 30, 3x by 40, 6x by 50, 8x by 60, and 10x by 67.\nExample: At age 40 with $100,000 annual income, Fidelity recommends having at least $300,000 saved."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Age: $age yrs | Multiplier: ${mult}x" }
    override val metricFidelityProgress: String = "Progress toward Fidelity goal:"

    override val metricCfsTitle: String = "Financial Freedom Ratio (CFS)"
    override val metricCfsFormula: String = "(Net Assets / Annual Expenses)"
    override val metricCfsDesc: String = "Measures how many years of baseline living expenses are covered by your net assets (net worth).\n\n• CFS < 0 — You are in net debt\n• CFS 0..0.5 — Minimal emergency cushion\n• CFS 0.5..2 — Initial capital accumulation stage\n• CFS 2..5 — Long-term investing stage\n• CFS 5..15 — Investment returns provide tangible income. Inflation impact grows\n• CFS 15..30 — Solid financial stability. How do you value your irreplaceable time?\n• CFS > 30 — Complete financial independence achieved"

    override val metricRealReturnTitle: String = "Real Return on Capital, %"
    override val metricRealReturnFormula: String = "(1 + Nominal Return) / (1 + Inflation) - 1"
    override val metricRealReturnDesc: String = "The net real expansion of purchasing power after deducting inflation from nominal investment yields. Shows real wealth creation."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Nominal: $nom | Inflation: $infl" }

    override val scoresHeaderTitle: String = "Score Summary"
    override val scoresHeaderSubtitle: String = "Overall audit score & breakdown"
    override val scoresShareReportChooser: String = "Share Report"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "of $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Overall Score: $score of 10" }
    override val scoresStatusExcellent: String = "Outstanding financial resilience and readiness for long-term wealth compounding."
    override val scoresStatusGood: String = "Solid financial health with strong potential to increase savings rate."
    override val scoresStatusModerate: String = "Moderate stability. Recommended to strengthen your emergency cash buffer."
    override val scoresStatusAttention: String = "Budget optimization and aggressive debt reduction are urgently recommended."
    override val scoresBreakdownTitle: String = "Component Score Breakdown:"
    override val scoreValuePrefix: String = "Value: "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score of 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String) -> String = { rate, sr ->
        when {
            rate > 20.0 -> "Recommended above 20%. You maintain an excellent rate ($sr)."
            rate >= 0.0 -> "Recommended above 20%. You have a low rate ($sr)."
            else -> "Recommended above 20%. You have a negative rate ($sr), budget deficit."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String) -> String = { months, monthsText ->
        when {
            months >= 12.0 -> "Minimum 3-6 months, ideal 12+ months. Excellent result: buffer exceeds 12 months ($monthsText)."
            months >= 6.0 -> "Minimum 3-6 months, ideal 12+ months. Good buffer ($monthsText), 6-month benchmark achieved."
            months >= 3.0 -> "Minimum 3-6 months, ideal 12+ months. Basic buffer ($monthsText), expanding to 6-12 months recommended."
            else -> "Minimum 3-6 months, ideal 12+ months. You have an insufficient buffer ($monthsText), elevated financial risk."
        }
    }
    override val debtComment0: String = "0.0 - 0.3: Excellent stability, assets funded almost entirely by own equity."
    override val debtComment1: String = "0.3 - 0.6: Balanced leverage, manageable borrowing level."
    override val debtComment2: String = "0.6 - 1.0: Elevated risk, substantial dependence on borrowed money."
    override val debtComment3: String = "Above 1.0: Critical risk, total debts exceed the value of all assets."

    override val auditorOpinionTitle: String = "Auditor Opinion"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Analysis across all $count indicators from the Metrics screen" }
    override val shareAuditorOpinionChooser: String = "Share Auditor Opinion"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count positive" }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count neutral" }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count negative" }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Positive Indicators ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Neutral Indicators ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Negative Indicators ($count)" }
    override val positiveEmptyText: String = "No positive factors identified yet. Recommended to optimize income and expenses."
    override val neutralEmptyText: String = "No neutral indicators — all metrics fall into either positive or negative zones."
    override val negativeEmptyText: String = "🎉 No critical or negative risk factors found — all metrics are in safe territory!"

    override val fireHeroTitle: String = "Financial Independence, FIRE"
    override val fireHeroSubtitle: String = "Retirement timeline and wealth projection"
    override val fireTimeToRetire: String = "Time to retirement:"
    override val fireYearsSuffix: String = " years"
    override val fireHeroAssumption: String = "(accounting for future savings, inflation and capital compounding)"
    override val fireRetirementAgeLabel: String = "Retirement age:"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age yrs" }
    override val fireTargetCapitalLabel: String = "Target required capital:"
    override val fireParamsTitle: String = "Age & Planning Horizon Parameters"
    override val fireBirthDateLabel: String = "Date of birth:"
    override val fireCurrentAgeLabel: String = "Current age:"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age yrs" }
    override val fireLifeExpectancyLabel: String = "Life expectancy:"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp yrs" }
    override val fireHorizonLabel: String = "(Life expectancy - Current age):"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h yrs" }
    override val fireRealReturnLabel: String = "Real return on capital:"
    override val fireTrajectoryTitle: String = "Capital Accumulation Trajectory"
    override val fireTrajectorySubtitle: String = "Wealth growth toward the FIRE point and funding lifelong retirement"
    override val fireChartAccumulated: String = "Accumulated capital"
    override val fireChartRequired: String = "Required capital"
    override val fireChartYearsSuffix: String = " yrs"
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE: $age yrs" }
    override val fireSimSummaryTitle: String = "Simulation Summary (Combined Impact)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ Retirement achieved $diff yrs earlier (in $target yrs, at age $age)!" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Time to retirement increased by $diff yrs (in $target yrs, at age $age)." }
    override val fireSimSame: (target: String) -> String = { target -> "Retirement timeline remains unchanged (in $target yrs)." }
    override val fireSimCashFlowRow: String = "Cash flow (savings):"
    override val fireSimExpensesRow: String = "Monthly expenses:"
    override val fireSimNetWorthRow: String = "Net worth (assets − debts):"
    override val fireSimPropertyRow: String = "Property increase (at loan completion):"
    override val fireSimSliderTitle: String = "Simulator \"What If?\""
    override val fireSimReset: String = "Reset"
    override val fireSimFineTuning: String = "Fine-tune scales (sliders):"
    override val fireSliderSavings: String = "Monthly savings:"
    override val fireSliderExpenses: String = "Monthly expenses:"
    override val fireSliderReturn: String = "Real return on capital:"
    override val fireSliderBaseSuffix: String = " to base"
    override val fireSliderBaseReturnSuffix: String = "% to base"
    override val fireSliderFrom: (v: String) -> String = { v -> "from $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "step $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "up to +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "growth"
    override val fireSliderDec: String = "red."

    override val scenarioHeaderTitle: String = "Scenario Panel (Stress Tests)"
    override val scenarioHeaderSubtitle: String = "The collective impact of sliders and checkboxes accumulates and is calculated together. The final result reflects in numbers and on the trajectory chart:"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Total: $count" }
    override val scenarioPositiveTitle: String = "Positive Events"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Active: $count" }
    override val scenarioPositiveSubtitle: String = "Events that accelerate capital accumulation and retirement:"
    override val scenarioNegativeTitle: String = "Negative Events"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Active: $count" }
    override val scenarioNegativeSubtitle: String = "Stress tests, unforeseen expenses and crisis scenarios:"
    override val scenarioParamsPrefix: String = "Parameters: "

    override val scenarioSalaryIncreaseTitle: String = "Salary Increase"
    override val scenarioSalaryIncreaseParam: String = "income +20% monthly, permanent"
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Income increase: +$amt/mo." }

    override val scenarioMarketGrowthTitle: String = "Market Growth +25%"
    override val scenarioMarketGrowthParam: String = "ETF +25%"
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Financial portfolio gain: +$amt" }

    override val scenarioExpenseOptTitle: String = "Expense Optimization"
    override val scenarioExpenseOptParam: String = "−15% to monthly expenses, permanent"
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Ongoing savings: -$amt/mo." }

    override val scenarioRelocationTitle: String = "Relocation to Lower-Cost Region"
    override val scenarioRelocationParam: String = "−30% to monthly expenses, permanent"
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Living cost reduction: -$amt/mo." }

    override val scenarioInheritanceTitle: String = "Inheritance or Windfall Gift"
    override val scenarioInheritanceParam: String = "one-time windfall 1200% monthly income"
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Capital inflow: +$amt (into liquid assets)" }

    override val scenarioPropertySaleTitle: String = "Sale of Property (car, land, real estate)"
    override val scenarioPropertySaleParam: String = "one-time windfall 2000% monthly income"
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Sale proceeds: +$amt (into liquid assets)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Children Independent"
    override val scenarioChildrenSelfSufficientParam: String = "expenses −25% monthly, after 18 yrs"
    override val scenarioChildrenSelfSufficientDesc: (amt: String) -> String = { amt -> "Family expense reduction: -$amt/mo. (after 18 yrs)" }

    override val scenarioJobLossTitle: String = "Job Loss"
    override val scenarioJobLossParam: String = "income = 0"
    override val scenarioJobLossDesc: String = "Complete income stoppage, covering all living expenses from emergency reserves"

    override val scenarioMarketCrashTitle: String = "Market Crash −30%"
    override val scenarioMarketCrashParam: String = "ETF −30%"
    override val scenarioMarketCrashDesc: (amt: String) -> String = { amt -> "Portfolio drawdown of 30% ($amt)" }

    override val scenarioMortgageTitle: String = "Home Mortgage"
    override val scenarioMortgageParam: String = "35% income for 20 yrs (+84 incomes into property)"
    override val scenarioMortgageDesc: (pay: String, asset: String) -> String = { pay, asset -> "Payment: $pay/mo. (20 yrs). At completion: +$asset in property assets" }

    override val scenarioCarLoanTitle: String = "Car Loan"
    override val scenarioCarLoanParam: String = "20% income for 5 yrs (+6 incomes into property)"
    override val scenarioCarLoanDesc: (pay: String, asset: String) -> String = { pay, asset -> "Payment: $pay/mo. (5 yrs). At completion: +$asset in property assets (50% value)" }

    override val scenarioLargeExpenseTitle: String = "Major One-Off Expense"
    override val scenarioLargeExpenseParam: String = "expense 500% monthly income"
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Outflow of $amt (covered from liquid and financial assets)" }

    override val scenarioInflationTitle: String = "Inflation Surge"
    override val scenarioInflationParam: String = "+5% to inflation"
    override val scenarioInflationDesc: (rate: String) -> String = { rate -> "Inflation increases to $rate (reduces real return on capital)" }

    override val scenarioChildTitle: String = "Child Birth"
    override val scenarioChildParam: String = "+25% to expenses for 18 yrs"
    override val scenarioChildDesc: (amt: String) -> String = { amt -> "Childcare costs: +$amt/mo. (for 18 years)" }
}


fun getAppStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.RU -> StringsRu
        AppLanguage.EN -> StringsEn
    }
}

val LocalAppStrings = compositionLocalOf { StringsRu }
val LocalAppLanguage = compositionLocalOf { AppLanguage.RU }
