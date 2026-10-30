package com.example.util.localization

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

    override val block4Title: String = "4. Инвестиции, Инфляция & Налоги"
    override val block4Subtitle: String = "Параметры доходности, инфляции и налога на прирост капитала"
    override val nominalReturnLabel: String = "Номинальная доходность %"
    override val inflationLabel: String = "Инфляция %"
    override val capitalGainsTaxLabel: String = "Налог на прирост капитала (%)"
    override val capitalGainsTaxBreakdownName: String = "Налог на прирост капитала"
    override val capitalGainsTaxHintCollapsed: String = "Как устроен налог на прирост капитала и расчет? (подсказка)"
    override val capitalGainsTaxHintExpanded: String = "Скрыть информацию о налоге на прирост капитала"
    override val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String = { tax, eff -> "Ставка: $tax | Эффективный налог с вывода: $eff" }
    override val capitalGainsTaxFormula: String = "Ставка * Доля прибыли в выводе * Доля фин. активов"
    override val capitalGainsTaxCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Суть и база налогообложения",
            items = listOf(
                "Налогом облагается не вся сумма от продажи, а положительная разница между доходом от продажи и документально подтверждёнными расходами на приобретение (цена покупки, комиссии, связанные с покупкой).",
                "Ставка применяется к прибыли от продажи акций и облигаций (разница между ценой продажи и покупки)."
            )
        ),
        BreakdownCategory(
            title = "Реалистичный смешанный расчет (Blended Model)",
            items = listOf(
                "Обычно при продаже часть суммы — это ваш первоначальный вклад, а часть — накопленная прибыль. Налогом облагается только прибыль.",
                "В программе индивидуальные доли прибыли (прироста капитала) и возврата тела капитала рассчитываются динамически для каждого пользователя исходя из горизонта накопления, сложных процентов, структуры активов и планируемых расходов на пенсии.",
                "Пример: если налог на прирост капитала составляет 23%, а индивидуальная расчетная доля прибыли в выводе равна 60%, то эффективный налог с вывода = 23% × 60% = 13,8%."
            )
        ),
        BreakdownCategory(
            title = "Учет в расчетах и влияние на FIRE",
            items = listOf(
                "Влияет на необходимый капитал и срок выхода на пенсию, если в структуре капитала есть акции и облигации (финансовые активы), которые нужно регулярно выводить для покрытия расходов.",
                "Дефолтное значение: средняя ставка по развитым странам Северной Америки и ЕС (20%)."
            )
        )
    )
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
    override val invalidBirthDateError: String = "Укажите корректную дату рождения"
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
    override val includePropertyInFireLabel: String = "Учитывать имущество и долги в расчёте FIRE"
    override val includePropertyInFireSubOn: String = "Используется чистый капитал (net worth), включая недвижимость за вычетом долгов"
    override val includePropertyInFireSubOff: String = "Используются только инвестируемые активы (ликвидные + финансовые)"

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
    override val metricFidelityDesc: String = "Методика Fidelity показывает, во сколько раз ваши текущие пенсионные накопления должны превышать ваш годовой заработок на определенном жизненном этапе.\n\nШкала Fidelity дискретна (задана конкретными точками для 30, 40, 50, 60 и 67 лет).\nПример: Если вам 40 лет и вы зарабатываете ($100,000) в год, то, согласно Fidelity, на ваших инвестиционных и пенсионных счетах должно быть накоплено не менее ($300,000).\n\nПримечание: Данное приложение не связано с компанией Fidelity Investments и использует исключительно открытые исследовательские ориентиры и общедоступную финансовую методологию."
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
    override val fireHeroAssumption: String = "Расчёт FIRE учитывает ликвидные и финансовые активы; имущество не учитывается, а долги вычитаются только в части, превышающей стоимость имущества"
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
    override val fireSimWithoutBase: (target: String, age: String) -> String = { target, age -> "Пенсия достижима через $target лет (в возрасте $age лет)." }
    override val fireUnreachableText: String = "Недостижимо при текущих параметрах"
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

    override val disclaimerTitle: String = "Отказ от отвествености"
    override val disclaimerText: String = "Важно: Проект является персональной разработкой и предоставляется исключительно в ознакомительных целях «как есть». Автор не несет ответственности за возможные сбои, потерю данных или иной ущерб, возникший в ходе использования данного ПО. Все расчеты, формулы, метрики и симуляции носят исключительно информационно-ознакомительный характер и не являются индивидуальной финансовой, инвестиционной или налоговой рекомендацией. Вы используете этот инструмент на свой собственный страх и риск."

    override fun scenarioSavedSuccess(name: String): String = "Сценарий «$name» успешно сохранен"
    override fun scenarioSaveError(error: String): String = "Ошибка сохранения сценария: $error"
    override val scenarioDatabaseError: String = "ошибка базы данных"
    override fun scenarioLoadedSuccess(name: String): String = "Сценарий «${name}» загружен"
    override fun scenarioLoadError(error: String): String = "Ошибка загрузки сценария: $error"
    override val scenarioCorruptedDataError: String = "поврежденные данные"
    override fun scenarioDeletedSuccess(name: String): String = "Сценарий «${name}» удален"
    override fun scenarioDeleteError(error: String): String = "Ошибка удаления сценария: $error"

    // Pluralization
    override fun plural(n: Int, one: String, many: String): String = pluralRu(n, one, many, many)
    override fun plural(n: Int, one: String, few: String, many: String): String = pluralRu(n, one, few, many)
    override fun pluralYears(n: Int): String = "$n " + pluralRu(n, "год", "года", "лет")
    override fun pluralMonths(n: Int): String = "$n " + pluralRu(n, "месяц", "месяца", "месяцев")
    override fun pluralPoints(n: Int): String = "$n " + pluralRu(n, "пункт", "пункта", "пунктов")
    override val checkboxOne: String = "чекбокс"
    override val checkboxFew: String = "чекбокса"
    override val checkboxMany: String = "чекбоксов"
    override val simulationBadgeSlidersSuffix: String = "ползунки"
    override val simulationBadgeOnlySliders: String = "Ползунки настройки"

    // Score & Safety Months formats
    override val safetyMonthsValueFormat: String = "%1${'$'}s мес."
    override val shareScoreReportTemplate: String = "📊 Финансовый отчет и аудит капитала:\n• Итоговый результат: %1${'$'}s из 10\n• Норма сбережений: %2${'$'}s/10 (%3${'$'}s)\n• Запас расходов: %4${'$'}s/10 (%5${'$'}s мес.)\n• Коэффициент задолженности: %6${'$'}s/10 (%7${'$'}s)\n• Коэффициент фин. свободы: %8${'$'}s/10 (%9${'$'}s)\n• До пенсии (FIRE): %10${'$'}s лет (в %11${'$'}s лет)\n"

    // CFS Stage descriptions
    override val cfsStageDebt: String = "КФС < 0 — Вы в долгах"
    override val cfsStageBuffer: String = "КФС 0..0,5 — У вас есть минимальная «подушка безопасности»"
    override val cfsStageCapital: String = "КФС 0,5..2 — Вы начали формировать свой финансовый капитал"
    override val cfsStageInvesting: String = "КФС 2..5 — Вы можете начать заниматься долгосрочными инвестициями"
    override val cfsStageReturns: String = "КФС 5..15 — Доходы от капитала могут приносить вам существенную прибавку. Инфляция все более значительно влияет на капитал"
    override val cfsStageStability: String = "КФС 15..30 — Крепкая финансовая стабильность. На что вы тратите свой невосполнимый капитал времени?"
    override val cfsStageIndependence: String = "КФС > 30 — Вы достигли финансовой независимости"

    // Auditor Opinion Share Text
    override val auditShareHeaderTitle: String = "📋 АУДИТОРСКОЕ ЗАКЛЮЧЕНИЕ ПО ВСЕМ ПОКАЗАТЕЛЯМ"
    override val auditShareScoreLine: String = "⭐️ Итоговая оценка: %1${'$'}s из 10 баллов"
    override val auditShareTotalCountLine: String = "Всего показателей проанализировано: %1${'$'}d"
    override val auditSharePositiveHeader: String = "🟢 ПОЗИТИВНЫЕ ПОКАЗАТЕЛИ (%1${'$'}d):"
    override val auditSharePositiveEmpty: String = "• Нет позитивных показателей"
    override val auditShareNeutralHeader: String = "🟡 НЕЙТРАЛЬНЫЕ ПОКАЗАТЕЛИ (%1${'$'}d):"
    override val auditShareNeutralEmpty: String = "• Нет нейтральных показателей"
    override val auditShareNegativeHeader: String = "🔴 НЕГАТИВНЫЕ ПОКАЗАТЕЛИ (%1${'$'}d):"
    override val auditShareNegativeEmpty: String = "• Критических и негативных факторов не выявлено 🎉"
    override val auditShareGeneratedBy: String = "💡 Сформировано в приложении «Личные финансы»"
    override val auditShareDisclaimer: String = "⚠️ Важно: Проект является персональной разработкой и предоставляется исключительно в ознакомительных целях «как есть». Автор не несет ответственности за возможные сбои, потерю данных или иной ущерб, возникший в ходе использования данного ПО. Все расчеты, формулы, метрики и симуляции носят исключительно информационно-ознакомительный характер и не являются индивидуальной финансовой, инвестиционной или налоговой рекомендацией. Вы используете этот инструмент на свой собственный страх и риск."

    // Auditor Indicators
    override val auditCashFlowTitle: String = "Денежный поток"
    override val auditCashFlowPositive: String = "✅ Денежный поток (Cash Flow: +%1${'$'}s/мес.): Профицит бюджета. Ваши доходы превышают расходы, формируя регулярный инвестиционный ресурс для приумножения капитала."
    override val auditCashFlowNeutral: String = "ℹ️ Денежный поток (Cash Flow: 0 %1${'$'}s/мес.): Нулевой баланс бюджета (доходы равны расходам). Вы не накапливаете долги, однако свободный ресурс для создания и приумножения капитала отсутствует."
    override val auditCashFlowNegative: String = "🚨 Денежный поток (Cash Flow: %1${'$'}s/мес.): Дефицит бюджета! Текущие расходы превышают доходы, что ведет к быстрому истощению резервов или росту долговых обязательств."

    override val auditSavingsRateTitle: String = "Норма сбережений"
    override val auditSavingsRatePositive: String = "✅ Способность к накоплению (Норма сбережений: %1${'$'}s): Высокий уровень сбережений (более 20%%). Обеспечивает стабильное пополнение активов и долгосрочное благосостояние."
    override val auditSavingsRateNeutral: String = "ℹ️ Способность к накоплению (Норма сбережений: %1${'$'}s): Нейтральная зона (0–20%%). Формирование капитала идет умеренными темпами; рекомендуется оптимизировать расходы для выхода в зону >20%%."
    override val auditSavingsRateNegative: String = "🚨 Способность к накоплению (Норма сбережений: %1${'$'}s): Отрицательный денежный поток (дефицит бюджета)! Расходы превышают доходы, что ведет к истощению активов и росту долгов."

    override val auditEmergencyFundTitle: String = "Подушка безопасности"
    override val auditEmergencyFundPositive: String = "✅ Подушка безопасности (Объем резерва: %1${'$'}s): Ликвидный резерв полностью достаточен для покрытия непредвиденных кризисов и экстренных ситуаций без обращения к кредитам."
    override val auditEmergencyFundNeutral: String = "ℹ️ Подушка безопасности (Объем резерва: %1${'$'}s): Сформирован базовый минимальный резерв. Рекомендуется постепенно довести его до комфортных 6 месяцев расходов."
    override val auditEmergencyFundNegative: String = "⚠️ Подушка безопасности (Объем резерва: %1${'$'}s): Критически низкий объем резерва. При непредвиденных тратах высок риск кассового разрыва и необходимости брать кредиты."

    override val auditSafetyMonthsTitle: String = "Запас месячных расходов"
    override val auditSafetyMonthsHigh: String = "✅ Запас прочности (Подушка безопасности: %1${'$'}s мес.): Полностью сформирована с большим запасом. Риски потери дохода нивелированы. Избыточную ликвидность сверх годового резерва целесообразно направить в доходные инвестиционные инструменты."
    override val auditSafetyMonthsTarget: String = "✅ Запас прочности (Подушка безопасности: %1${'$'}s мес.): Целевой норматив в 6 месяцев расходов успешно достигнут. Базовая финансовая безопасность семьи обеспечена."
    override val auditSafetyMonthsNeutral: String = "ℹ️ Запас прочности (Подушка безопасности: %1${'$'}s мес.): Сформирован минимальный базовый резерв, однако рекомендуется постепенно нарастить его до комфортных 6 месяцев расходов для защиты от непредвиденных кризисов."
    override val auditSafetyMonthsNegative: String = "⚠️ Запас прочности (Подушка безопасности: %1${'$'}s мес.): Критически недостаточный резерв (менее 3 месяцев). Любой кассовый разрыв или непредвиденные траты создадут угрозу долговой ямы. Приоритетная задача — экстренно довести ликвидный резерв минимум до 6 месяцев расходов."

    override val auditDebtRatioTitle: String = "Коэффициент задолженности"
    override val auditDebtRatioZero: String = "✅ Долговая нагрузка (Коэффициент задолженности: 0,00): Абсолютная финансовая автономность — долги и кредиты отсутствуют, все активы принадлежат вам на 100%."
    override val auditDebtRatioConservative: String = "✅ Долговая нагрузка (Коэффициент задолженности: %1${'$'}s, долг %2${'$'}s от активов): Кредитное плечо консервативное и безопасное. Обязательства полностью обеспечены ликвидным имуществом, кредитный риск минимален."
    override val auditDebtRatioModerate: String = "ℹ️ Долговая нагрузка (Коэффициент задолженности: %1${'$'}s, долг %2${'$'}s от активов): Умеренный уровень заемного финансирования. Долговая нагрузка контролируема, но требует внимания при планировании крупных покупок и регулярных платежей."
    override val auditDebtRatioHigh: String = "⚠️ Внимание: Высокая закредитованность (Коэффициент задолженности: %1${'$'}s, долг %2${'$'}s от активов)! Значительная зависимость от внешних кредиторов. Рекомендуется рефинансировать займы, сократить долги и воздержаться от новых обязательств."
    override val auditDebtRatioCritical: String = "🚨 КРИТИЧЕСКИЙ РИСК: Дефицит капитала (Коэффициент задолженности: %1${'$'}s, долги превышают активы на %2${'$'}s)! Объем финансовых обязательств превышает стоимость всего вашего имущества. Необходима экстренная реструктуризация долгов и финансовое оздоровление."

    override val auditNetWorthTitle: String = "Чистый капитал"
    override val auditNetWorthStrong: String = "✅ Чистый капитал (Собственные средства: %1${'$'}s): Положительный чистый баланс. Ваши совокупные активы уверенно превышают финансовые обязательства, формируя прочный финансовый фундамент."
    override val auditNetWorthBalanced: String = "ℹ️ Чистый капитал (Собственные средства: %1${'$'}s): Активы и обязательства находятся в близком балансе. Ваша финансовая стоимость положительна, но существенная часть активов обременена долгами."
    override val auditNetWorthNegative: String = "🚨 Чистый капитал (Отрицательный баланс: %1${'$'}s): Отрицательный чистый капитал (техническая неплатежеспособность). Обязательства превышают стоимость всего имеющегося имущества."

    override val auditFidelityTitle: String = "Бенчмарк Fidelity"
    override val auditFidelityPositive: String = "✅ Возрастной ориентир капитала (Бенчмарк Fidelity: %1${'$'}s от цели): Капитал опережает целевой норматив для вашего возраста (%2${'$'}s). Отличный задел для долгосрочного благосостояния."
    override val auditFidelityNeutral: String = "ℹ️ Возрастной ориентир капитала (Бенчмарк Fidelity: %1${'$'}s от цели): Капитал соответствует рекомендуемым возрастным ориентирам накоплений (%2${'$'}s)."
    override val auditFidelityNegativeInsolvent: String = "🚨 Возрастной ориентир капитала (Бенчмарк Fidelity): Чистый капитал отрицателен (%1${'$'}s). Наличие долговой нагрузки отдаляет достижение пенсионного норматива (%2${'$'}s)."
    override val auditFidelityNegativeLagging: String = "⚠️ Возрастной ориентир капитала (Бенчмарк Fidelity: %1${'$'}s от цели): Отставание от рекомендуемой возрастной планки капитала (%2${'$'}s). Желательно ускорить темп капитализации."
    override val auditFidelityUnder30: String = "✅ Возрастной ориентир капитала (Бенчмарк Fidelity): В возрасте до 30 лет основной задачей является закладка фундамента накоплений. Целевые множители Fidelity начинаются с 30 лет (1x годовой доход)."

    override val auditCfsTitle: String = "Коэффициент финансовой свободы"
    override val auditCfsIndependence: String = "🎉 Финансовая свобода (КФС: %1${'$'}s): Вы достигли полной финансовой независимости (FIRE)! Накопленные чистые активы способны пожизненно обеспечивать текущий уровень жизни."
    override val auditCfsStability: String = "✅ Финансовая свобода (КФС: %1${'$'}s): Высокая степень автономии. Чистые активы генерируют существенный пассивный доход, закрывающий большую часть обязательных расходов."
    override val auditCfsGrowth: String = "✅ Финансовая свобода (КФС: %1${'$'}s): Уверенная фаза роста капитала (%2${'$'}s). Доходы от капитала начинают приносить ощутимый результат. Следующая стратегическая цель — рубеж КФС > 15."
    override val auditCfsInitial: String = "ℹ️ Финансовая свобода (КФС: %1${'$'}s): Начальный этап формирования инвестиционного капитала (%2${'$'}s). Основной упор на дисциплинированные регулярные сбережения."
    override val auditCfsNegative: String = "🚨 Финансовая свобода (КФС: %1${'$'}s): Отрицательный показатель (%2${'$'}s). Обязательства превышают стоимость активов, чистый капитал находится в дефиците."
    override val auditCfsBuffer: String = "📈 Финансовая свобода (КФС: %1${'$'}s): Начальный этап формирования капитала (%2${'$'}s). Накопленного объема недостаточно для финансовой автономии, требуется дисциплинированное сбережение."

    override val auditRealReturnTitle: String = "Реальная доходность капитала"
    override val auditRealReturnPositive: String = "✅ Реальная доходность капитала (+%1${'$'}s): Доходность активов опережает инфляцию (%2${'$'}s против %3${'$'}s). Капитал защищен от обесценивания и наращивает реальную покупательную способность."
    override val auditRealReturnNeutral: String = "ℹ️ Реальная доходность капитала (0,0%): Доходность в точности компенсирует инфляцию. Капитал сохраняет номинальную покупательную способность, но не создает реального прироста богатства."
    override val auditRealReturnNegative: String = "⚠️ Реальная доходность капитала (%1${'$'}s): Инфляция (%2${'$'}s) превышает доходность капитала (%3${'$'}s). Происходит скрытое обесценивание накоплений и потеря покупательной способности."

    // Export Summary
    override val exportHeaderTitle: String = "📊 ФИНАНСОВЫЙ ОТЧЕТ — ЛИЧНЫЕ ФИНАНСЫ"
    override val exportSection1Title: String = "1. Исходные данные:"
    override val exportIncomeLine: String = "• Доход: %1${'$'}s/мес."
    override val exportExpensesLine: String = "• Расходы: %1${'$'}s/мес."
    override val exportLiquidAssetsLine: String = "• Ликвидные активы: %1${'$'}s"
    override val exportFinancialAssetsLine: String = "• Финансовые активы: %1${'$'}s"
    override val exportPropertyAssetsLine: String = "• Недвижимость / имущество: %1${'$'}s"
    override val exportLiabilitiesLine: String = "• Пассивы (долги): %1${'$'}s"
    override val exportAgeHorizonLine: String = "• Возраст: %1${'$'}d лет (дожитие: %2${'$'}d лет, горизонт: %3${'$'}d г.)"
    override val exportReturnsInflationLine: String = "• Номинальная доходность: %1${'$'}s | Инфляция: %2${'$'}s"
    override val exportTaxLine: String = "• Налог на прирост капитала: %1${'$'}s"

    override val exportSection2Title: String = "2. Рассчитанные показатели:"
    override val exportCashFlowLine: String = "• Денежный поток: %1${'$'}s/мес."
    override val exportSavingsRateLine: String = "• Норма сбережений: %1${'$'}s"
    override val exportEmergencyFundLine: String = "• Подушка безопасности: %1${'$'}s (%2${'$'}s мес.)"
    override val exportDebtRatioLine: String = "• Коэффициент задолженности: %1${'$'}s"
    override val exportNetWorthLine: String = "• Чистый капитал: %1${'$'}s"
    override val exportRealReturnLine: String = "• Реальная доходность: %1${'$'}s"
    override val exportEffectiveTaxLine: String = "• Эффективная ставка налога с вывода капитала: %1${'$'}s"

    override val exportSection3Title: String = "3. Набранные баллы:"
    override val exportScoreTotalLine: String = "• Итоговая оценка: %1${'$'}s из 10"
    override val exportScoreSavingsRateLine: String = "• Норма сбережений: %1${'$'}d / 10"
    override val exportScoreSafetyMonthsLine: String = "• Подушка безопасности: %1${'$'}d / 10"
    override val exportScoreDebtRatioLine: String = "• Долговая нагрузка: %1${'$'}d / 10"
    override val exportScoreCfsLine: String = "• Финансовая свобода: %1${'$'}d / 10"

    override val exportSection4Title: String = "4. Финансовая свобода, FIRE:"
    override val exportCfsLine: String = "• КФС: %1${'$'}s (%2${'$'}s)"
    override val exportFireTargetCapitalLine: String = "• Капитал для FIRE: %1${'$'}s"
    override val exportFireTimeToRetirementLine: String = "• Срок до пенсии FIRE: %1${'$'}s лет (в возрасте %2${'$'}s лет)"
    override val exportFidelityLine: String = "• Бенчмарк Fidelity: %1${'$'}s (%2${'$'}sx годового дохода) — %3${'$'}s от цели"
}

// isRussian pluralization
fun pluralRu(n: Int, one: String, few: String, many: String): String {
    val mod10 = n % 10
    val mod100 = n % 100
    return when {
        mod10 == 1 && mod100 != 11 -> one
        mod10 in 2..4 && mod100 !in 12..14 -> few
        else -> many
    }
}
