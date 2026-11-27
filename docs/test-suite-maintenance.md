# Жизненный цикл тестов — рабочий реестр

Аудит TEST-C01, 27.11.2026. Изменены только этот реестр и корневой AGENTS.md.
Ни одно удаление/слияние ниже ещё не выполнено; экономия времени не измерена.
Решения относятся к фактическому коду до FA-01–FA-11, FIRE-M01–M09 и FIRE-UI-01.
Будущие требования очереди не считаются реализованными. Квоты удаления нет.

## Исходный прогон

Предварительно запущено `bash ./gradlew testDebugUnitTest --offline --console=plain`:
BUILD SUCCESSFUL за 23 с, но testDebugUnitTest был UP-TO-DATE; этот результат
не включён в замер и не считается выполнением тестов.

Измеренный запуск: 27.11.2026, **11:45:43–11:49:16 UTC**, JDK Temurin 21.0.13,
Gradle wrapper 9.3.1, Linux, SDK и зависимости уже в кеше. Полный обычный набор,
без `--tests`, без screenshot opt-in; GENERATE_STORE_SCREENSHOTS удалён из окружения.
Команда: `bash ./gradlew testDebugUnitTest --offline --console=plain --no-configuration-cache --init-script /tmp/test-c01-d5n02gu4/force-test.gradle`.
Одноразовый init script вне репозитория применял только к `:app:testDebugUnitTest`
`outputs.upToDateWhen { false }` и `outputs.doNotCacheIf(...) { true }`, а doFirst/doLast
печатали System.nanoTime. Зависимые задачи не перезапускались; ни clean, ни
--rerun-tasks не использовались. Постоянная конфигурация Gradle не изменена.

| Показатель | Результат |
|---|---:|
| JUnit cases / XML classes | 333 / 43 |
| Выполнено и успешно | 332 |
| Skipped | 1 — StoreScreenshotsTest.captureLocalizedStoreScreenshots |
| Failures / errors | 0 / 0 |
| Wall time всего subprocess (time.monotonic) | **212,961 с** |
| Интервал doFirst–doLast задачи (System.nanoTime) | **209,998 с** |
| Gradle | BUILD SUCCESSFUL; 1 executed, 29 up-to-date |

Все 43 XML в `app/build/test-results/testDebugUnitTest` имеют mtime внутри
этого запуска; проверены tests/skipped/failures/errors и timestamp.
Старые XML от 24.11 не использованы. Длительности классов ниже — атрибут `time`
XML, **не wall time**; их сумма не используется как время сборки.

| Самые дорогие классы | Cases | XML time, с |
|---|---:|---:|
| FireSimulationStickySummaryRobolectricTest | 9 | 24,827 |
| UnifiedStatusColorsRobolectricTest | 5 | 24,760 |
| TypographyLayoutRobolectricTest | 1 | 23,254 |
| AuditScoreScreenRobolectricTest | 13 | 20,706 |
| FireRetirementScreenRobolectricTest | 17 | 18,690 |
| UiDisclosureStateRobolectricTest | 3 | 18,683 |
| InputDataScreenRobolectricTest | 10 | 15,534 |
| MetricsScreenRobolectricTest | 7 | 11,116 |
| FinanceAppRobolectricTest | 11 | 10,302 |
| FinancialConsistencyIntegrationTest | 6 | 6,897 |

StoreScreenshotsTest: **0,089 с**, skipped. В этом прогоне существенная стоимость
его пропуска не наблюдалась; затраты общего cold start Robolectric этим числом
не изолированы. Повторный замер после очистки выполнять в таком же окружении:
прогретые зависимости/daemon, только тестовая задача принудительно, без параллельной
сборки, без opt-in и с теми же флагами. Один запуск не задаёт статистическую норму.
Отдельный gate выполняет оркестратор, здесь он не запускался.

## Источники и границы аудита

Изучены корневой AGENTS.md, README.md, APP_DESCRIPTION.md, существующие docs,
app/build.gradle.kts, все 44 Kotlin-файла тестов и оставшаяся очередь
orchestrator/TASKS.md (только чтение). Отчёты о релизах — исторические свидетельства,
а не baseline текущего набора. Методические документы FA/FIRE ещё не созданы.

В `app/src/test` 43 класса: 13 pure JVM и 30 с RobolectricTestRunner;
в `app/src/androidTest` один instrumentation-класс. Единственный отдельный ресурс
тестов — `app/src/test/resources/robolectric.properties` (API 35).
LocalizationFormattersTest явно использует API 28; LaunchThemeRobolectricTest
проверяет Android resource/API-ветви 24/26/27/35. Это не эмуляторы.
ScenarioSimulationTest и TypographyTest, несмотря на имена, используют Robolectric.

Основные production-источники доказательств (далее P1–P7):

- **P1:** [FinanceCalculator.kt](../app/src/main/java/com/example/domain/FinanceCalculator.kt),
  [FinancialMetricEvaluator.kt](../app/src/main/java/com/example/domain/FinancialMetricEvaluator.kt):
  округление валют, формулы/статусы, Fidelity, четыре оценки, целочисленные сотые
  суммарного балла, cap при CFS score < 5. Новая оценка девяти показателей отсутствует.
- **P2:** [ProjectionEngine.kt](../app/src/main/java/com/example/domain/ProjectionEngine.kt):
  FIRE start = max(0, Fidelity capital), taxInfo обнуляет налог при F=0,
  последняя точка + бисекция, накопление/пенсионная траектория с нижним ограничением 0.
- **P3:** [MainViewModel.kt](../app/src/main/java/com/example/ui/MainViewModel.kt),
  [LocalPreferencesManager.kt](../app/src/main/java/com/example/data/repository/LocalPreferencesManager.kt),
  [ScenarioConstants.kt](../app/src/main/java/com/example/domain/ScenarioConstants.kt),
  [SimulatorScaleCalculator.kt](../app/src/main/java/com/example/domain/SimulatorScaleCalculator.kt):
  раздельные base/simulation, 14 сценариев, jobLoss пока разовая потеря активов,
  clamp при загрузке/изменениях, debounce/flush, dateProvider, first-launch marker.
- **P4:** [FinanceApp.kt](../app/src/main/java/com/example/ui/FinanceApp.kt),
  [MainActivity.kt](../app/src/main/java/com/example/MainActivity.kt), ui/screens:
  четыре основные вкладки, rememberSaveableStateHolder, отдельный выбранный FIRE
  subtab, списки/раскрытия, ON_RESUME refresh, onStop flush. FIRE показывает base
  hero/график; Simulation — sticky comparison и управление. Старый бейдж hero уже
  отсутствует в коде; его отсутствие — действующий контракт, несмотря на старую
  запись о расположении бейджа в frozen decisions. В TEST-C01 это расхождение не меняется.
- **P5:** [StatusColors.kt](../app/src/main/java/com/example/ui/theme/StatusColors.kt),
  [ScoreColorEvaluator.kt](../app/src/main/java/com/example/ui/theme/ScoreColorEvaluator.kt),
  [Type.kt](../app/src/main/java/com/example/ui/theme/Type.kt): отдельные accent/text
  цвета, тема по luminance, общие sp-роли и lineHeight; UI должен применять токены.
- **P6:** [Formatters.kt](../app/src/main/java/com/example/util/Formatters.kt),
  util/localization, Android resources, [AuditorAnalysisGenerator.kt](../app/src/main/java/com/example/domain/AuditorAnalysisGenerator.kt):
  шесть языков, JPY, округление/множественные формы, статусы FIRE, группировка аудита.
  Три пути share: шаблон в AuditScoreScreen, toFormattedShareText,
  generateExportSummary/ExportResultsButton. Сейчас текстовый аудит ещё содержит общий балл.
- **P7:** [app/build.gradle.kts](../app/build.gradle.kts), StoreScreenshotsTest,
  docs/release-prep-1.0.3.md: ресурсы включены в unit tests, screenshot opt-in,
  текущие screenshot-материалы сохраняются, выпуск отложен.

В таблицах имена методов в обратных кавычках — точные имена текущих тестов.
Если строка решения не выделяет метод, KEEP относится ко всему классу с оговорками
из следующих разделов. KEEP не означает, что текущий набор исчерпывает все риски.

## Карта «контракт/риск → остающаяся защита»

| Контракт и production-источник | Тесты и решение |
|---|---|
| Формулы, Fisher/clamp, денежная точность, даты, Fidelity/имущество, нули, отрицательные суммы (P1) | **KEEP JVM** FinanceCalculatorTest: формулы, даты с today, 50% net worth, currency cancellation, дополнительные cash flow. **KEEP JVM** FinancialMetricEvaluatorTest: точные границы, Infinity debt, нулевые расходы, CFS/Fidelity/описания. Ожидания старого total/cap — DEFER ниже. |
| FIRE PV, временные расходы/deferred reduction, налог, поиск, статусы и траектория (P2) | **KEEP JVM** ProjectionEngineTest: аналитические примеры, wrappers, deficit, 300 seeded profiles против независимого перебора времени, предельные r, траектория. **KEEP JVM** CapitalGainsTaxTest: связность engine→metrics→export и временные расходы. Старые методические ожидания — DEFER. |
| Согласованность между слоями и property/invariant (P1/P2/P6) | **KEEP JVM** CapitalAndAuditConsistencyTest: 1000 seeded capitals и 100 seeded audit profiles × 6 языков, unavailable Fidelity; **KEEP Robolectric** FinancialConsistencyIntegrationTest: input bounds, профиль→audit/share, точные export lines, реальные цвета Input/Metrics, tax fields, сценарии и horizon. Это разные уровни проверки; не удалять класс как «интеграционный дубль». |
| Чистые диапазоны/шаги, JPY, отсутствие мёртвых шагов (P3) | **KEEP JVM** SimulatorScaleCalculatorTest: 90% cap, нулевые расходы, inward rounding, границы nominal. Наличие VM-тестов не заменяет эти дешёвые границы. |
| Координация сценариев, clamp, restore/reset, date/flush, base isolation (P3) | **KEEP Robolectric без Compose** MainViewModelRobolectricTest, ScenarioSimulationTest, LocalPreferencesAndViewModelRobolectricTest. Последний проверяет raw NaN/Infinity/malformed/preferences, игнорирование legacy include_property_fire и реальную запись/чтение. Точечные слияния ниже. |
| Налоги/статусы и безопасное текстовое представление FIRE (P6) | **KEEP JVM** FireFormatterTest: date locale, unavailable target, REACHED_NOW/sub-tenth/invalid/horizon, HALF_EVEN. **KEEP JVM** FireSimulationComparisonFormatterTest: округление колонок перед delta, все пары nonnumeric statuses, отрицательный ноль, units/plurals шести языков, NaN/Infinity, no-op. |
| Численные точки графика (P2/P4) | **KEEP JVM** FireTrajectoryChartTest: интерполяция дробного возраста, endpoints, пустая/одна точка; это не screenshot/layout-тест. |
| Аудит и экспорты, наличие/порядок предположений и disclaimer, доступность Fidelity (P6) | **KEEP JVM** AuditorAnalysisTest: границы savings/CFS, все группы, фактические суммы, target availability, RU/EN и шесть языков. Merge одного повтора ниже; будущая семантика экспорта — DEFER. |
| Локализация содержимого/ресурсов всех шести языков (P6) | **KEEP JVM** AppLanguageTest (включая be→RU, uk→EN), DecimalInputParsingTest (оба разделителя/intermediate/cap). **KEEP минимальный Robolectric** LocalizationFormattersTest (API 28: ICU/plurals, валюты, знаки, rounding/nonfinite), LocalizationReleaseTextTest (смысл строк, двоеточия, CFS/tax/assumptions/export/year units). ApprovedUiRegressionTest — разделение ниже. Не превращать в шесть full-screen renders. |
| Ввод: редактор и системная клавиатура — разные риски (P4/P6) | **KEEP Robolectric** NumberInputFieldRobolectricTest (пасты/набор, focus, локали, JPY, HALF_UP/HALF_EVEN, промежуточный текст, cap), InputKeyboardRobolectricTest (IME Next/Done, cursor, decimal flags, dismiss без перехвата controls), InputImeInsetsRobolectricTest (реальные WindowInsets, нет двойного nav inset, последнее поле над IME). Pure parser не заменяет их. |
| Input hints/даты/горизонт/кнопка и Metrics фильтры/formula/export (P4) | **KEEP Robolectric** InputDataScreenRobolectricTest, MetricsScreenRobolectricTest: независимые hints, ISO edit/localized date, сохранённый horizon при невалидном вводе, 48dp action, категории, collapse/reexpand, share chooser/ACTION_SEND/text/plain. SIMPLIFY матриц ниже; отсутствие metric score badges ждёт FA-08. |
| Audit icon fallback, CFS/zero cash flow, share action и disclosure (P4/P6) | **KEEP Robolectric** AuditScoreScreenRobolectricTest: иконки по статусу без анализа текста/удвоения, действия/расположение export, share intent. Старый breakdown — DEFER; повторные визуальные комбинации — SIMPLIFY. |
| Реальная навигация и привязка UI к VM (P3/P4) | **KEEP Robolectric** FinanceAppRobolectricTest: notice, ON_RESUME birthday, четыре controls, base hero/chart против Simulation, реальные slider/switch actions и base isolation. Мелкие маршруты допускают MERGE ниже. |
| State restoration на разных уровнях (P4) | **KEEP Robolectric** FireSubtabRecreationRobolectricTest (MainActivity, subtab + scenarios/reset + независимые disclosure), FireScrollStateRobolectricTest (две list positions и SavedInstanceState на экране), TabScrollStateRobolectricTest (неактивные основные вкладки через disposal и Activity recreation), UiDisclosureStateRobolectricTest (filter row offset, item keys, reinsert, hints, base preservation). Не считать эти четыре класса одним дублем. |
| FIRE screen wiring/status/reset (P4/P6) | **KEEP Robolectric** FireRetirementScreenRobolectricTest: статусы/0.05–0.051, base/simulation separation, subtitle factors, reset target/semantics, независимые cards. **KEEP Robolectric** FireSimulationStickySummaryRobolectricTest: единственный sticky panel/reset, pin bounds, непрозрачность inset/rounded corners и отсутствие bleed-through при scroll, action count, neutral reset text. Матрицы — SIMPLIFY. |
| Accessibility semantics, а не только читаемость картинки (P4/P6) | **KEEP Robolectric** FireAccessibilityRobolectricTest: chart description, sliders min/max/step и SetProgress, 14 Switch roles/clicks/localized labels. Локализованные semantics шести языков — самостоятельный контракт, не общий layout sweep. **KEEP** CollapsibleInfoCardRobolectricTest: 64dp compact geometry и 48dp target; конфигурации упростить ниже. |
| Цветовые значения и реальное применение темы (P5) | **KEEP JVM** StatusTextContrastTest: WCAG 4.5, alpha fills, graphical contrast, golden neutral, accent palette, thresholds. **KEEP Robolectric** UnifiedStatusColorsRobolectricTest: текст + пиксели progress/gauge/switch, Material error отдельно от financial negative; StatusTextColorUsageRobolectricTest: hero/status/delta, slider sign, badges/counters. Matrix профилей FinancialConsistencyIntegrationTest проверяет domain→screen, не WCAG. Один точный цветовой повтор ниже. |
| Типографика и компоновка (P5) | **KEEP/SIMPLIFY Robolectric** TypographyTest (sp-роли, lineHeight, системный font, масштабируемость); TypographyLayoutRobolectricTest (полный обход lazy pages + expanded metric explanations на пяти экранах). Конкретный новый бюджет ниже. |
| Запуск/Android lifecycle/resources (P4/P7) | **KEEP Robolectric** MainActivityRobolectricTest: запуск и onStop flush; LaunchThemeRobolectricTest: launch theme до Compose на четырёх API и системные bars после recreation в двух режимах. Не удалять API-resource-ветви под видом визуальной матрицы. |
| Выпуск материалов (P7) | **KEEP opt-in Robolectric/NATIVE** StoreScreenshotsTest.captureLocalizedStoreScreenshots: 6 локалей × 5 экранов, 30 PNG в fastlane; текущие результаты не удалять. **DELETE-кандидат instrumentation** ExampleInstrumentedTest.useAppContext — обоснование ниже. |

Смена вкладки проверяет disposal/composition и SaveableStateHolder; recreation —
Android SavedInstanceState/ViewModel wiring; новый manager/VM — чтение durable
preferences. Последнее не является реальным убийством/перезапуском процесса.
Отдельного теста настоящего process death в текущем наборе нет; не заявлять такое
покрытие и не заменять проверки хранения простым `scenario.recreate()`.

## Независимые от будущей методики кандидаты

DELETE — доказанный повтор/шаблон; MERGE — сначала перенести уникальные assertions;
SIMPLIFY — сохранить именованный риск на меньшем наборе конфигураций.
Риск в таблице — что нужно перепроверить при реализации TEST-C02, не разрешение
ослабить контракт. Все прочие assertions перечисленных классов остаются KEEP.

| Решение, точное место | Доказательство → остающаяся проверка; риск |
|---|---|
| **DELETE** ExampleInstrumentedTest.useAppContext | Единственный assert сравнивает сгенерированный BuildConfig.APPLICATION_ID с targetContext.packageName, без независимого expected identity. Поиск имени класса/метода и androidTest/connected-test invocation в репозитории не нашёл отдельного потребителя/сценария. P7 фиксирует applicationId; MainActivityRobolectricTest.`main activity launches without crash` сохраняет локальный smoke. Это не эквивалент device smoke: после удаления не останется instrumentation-тестов; проверку APK identity и фактический запуск при выпуске сохранить по AGENTS/release docs. Зависимости/runner в TEST-C02 не чистить автоматически. |
| **DELETE** MetricsScreenRobolectricTest.`when cashFlow is zero MetricsScreen displays income equals expenses subtitle` | Метод проверяет только наличие RU neutral subtitle при income=expenses=50000. `zero cash flow subtitle says income equals expenses in every language` того же класса проверяет то же условие плюс отсутствие negative subtitle (RU включён). P4 MetricsScreen выбирает subtitle по знаку cashFlow, сумма/дата не меняют эту ветвь. При сокращении языков сохранить RU assertion. Риск низкий: не удалить обе проверки. |
| **MERGE** MainViewModelRobolectricTest.`nominal return at 100 and inflation at 2_5 percent ensures all slider positions change metrics` → `every return slider step changes metrics at maximum nominal return` | Одинаковые input nominal=100/inflation=2.5, полный обход шагов, строго растущая realReturn. Получатель дополнительно проверяет exact delta и base isolation. Перенести `steps > 0`; его `maxValue == 0` сильнее `<= 0`. Не сокращать набор шагов/границы. Риск: Float/Double представление delta оставить проверенным на краях (P3). |
| **MERGE** AuditorAnalysisTest.testCashFlowZeroPlacedInNegativeGroupWithWarningIcon → `zero cash flow stays negative with a warning and explanation in every language` | Проверки negative/not neutral/⚠️ и пояснения совпадают; остающийся тест ещё исключает positive и использует фиксированную дату. Перенести уникальную RU проверку «доходы равны расходам» (ignoreCase). P6 generate ветвится по cashFlow=0. Риск: без переноса потеряется смысловое утверждение. |
| **MERGE** NumberInputFieldRobolectricTest.`empty and sign-only values parse as zero` и `parseDouble correctly parses across locales without corrupting grouped numbers` → DecimalInputParsingTest (отдельные методы для parseDouble) | Оба метода вызывают только Formatters.parseDouble, не создают UI; сейчас оплачивают Compose rule/Robolectric. Перенести все assertions без изменения в JVM-класс: grouped numbers и fallback separator не равны parseDecimalInput, поэтому не заменять их одним существующим ungrouped-тестом. Pure путь подтверждён P6 и существующим DecimalInputParsingTest. Риск: не перенести заодно UI focus/cursor/input tests — они остаются Robolectric. |
| **MERGE** FinanceAppRobolectricTest.`selecting Simulation survives switching to Input and back to FIRE` и `selecting Simulation survives multiple main tab switches` | Свести в один маршрут пустой симуляции Input→FIRE→Simulation→Metrics→Scores→Input→FIRE. Сохранить initial BASE selected, INPUT selected, content simulation exists/base absent, trajectory absent и выбор Simulation. Источники: тела обоих методов и P4 rememberSaveable selectedFireSubtab. Не заменять этим `selecting FIRE subtab stays selected after visiting other main tabs` (обратное направление), активный slider/scenario маршрут, MainActivity recreation или two-list restoration. Риск: комбинированный маршрут хуже локализует сбой, поэтому assertions ставить на каждом переходе. |
| **DELETE** StatusTextColorUsageRobolectricTest.`fidelity progress value uses the same readable text color as the metric` | Проверяет один RU/light percent-color. UnifiedStatusColorsRobolectricTest.`fidelity value percent and progress bar share exact status color across statuses and themes` покрывает тот же percent selector, все три статуса/обе темы и пиксель fill. P5 getReadableTextColor(getColor(status)) эквивалентен getTextColor(status). Дополнительного default-profile business-контракта у метода нет. Риск низкий; получатель и его percent assertion должны остаться. |
| **SIMPLIFY** TypographyLayoutRobolectricTest.`RU and DE text fits at 320 360 411dp with font scales 1 1_3 1_6 in both themes` | 3×3×2×2×5=540 экранных обходов внутри одного @Test. Заменить точными десятью обходами из таблицы ниже, сохранив все страницы, expanded formulas, >=12sp, отсутствие ellipsis/overflow и завершение scroll. P4/P5 и новый budget заменяют широту матрицы, не её assertions. Риск редких сочетаний осознанно принят владельцем; не требовать прежнего wrapping-count/геометрии 320dp. |
| **SIMPLIFY** TypographyTest.`font scaling keeps every role readable including small text at twice the scale` | Исключить 2.0; оставить дешёвую проверку Density/sp на 1.0/1.3 без UI-матрицы, с ростом small text и lineHeight > fontSize. `every role has a system font and explicit scalable size and line height` и `dense screens retain body medium size and use readable supporting text` — KEEP. Regex guard `screens and components use typography roles without local size overrides` — KEEP как явное правило Type.kt, но не доказательство читаемости: он хрупок к синтаксису/комментариям. Не удалять его без равноценного guard. |
| **SIMPLIFY** FireRetirementScreenRobolectricTest.russianHeroTitleFitsAt360dp / russianHeroTitleFitsAt411dp / assertRussianHeroTitleFits | Оставить RU/360/1.0/light + единственный targeted RU/360/1.3/light для hero title; 411 и 1.6 убрать. Обязательны: реальная ширина, полный текст, отсутствие ellipsis/overflow, допустимый перенос и видимый subtitle. «Обязательно >1 строки» при новом масштабе и Int.MAX_VALUE как способ реализации не являются самостоятельным продуктовым контрактом. TXT/PNG сейчас пишутся при каждом проходе helper, не читаются ни одним тестом/сборочным потребителем (поиск fire-hero-title/ru-$widthDp); это диагностика, её убрать из обычного успешного прогона, оставить при падении либо по opt-in. Bitmap.compress assert проверяет запись диагностического файла, не layout. Риск: не потерять сами TextLayoutResult assertions и последующую FIRE-UI-01 проверку размещения срока. |

## Точный бюджет визуальных прогонов для TEST-C02

Это реестр будущего сокращения, а не уже изменённые тесты. Стандарт везде:
360.dp, текущая штатная высота 800.dp, fontScale=1.0, light, RU.
Не создавать общий цикл «язык × тема × scale × ширина».

Остающиеся сценарии **TypographyLayoutRobolectricTest** (10 экранных обходов):

| Язык/тема | Экраны | Независимый риск |
|---|---|---|
| RU/light | INPUT, METRICS, SCORES, FIRE BASE, FIRE SIMULATION | Базовая читаемость всех действующих экранов, lazy pages и раскрытых объяснений |
| DE/light | METRICS, FIRE SIMULATION | Длинные названия/формулы и тесные comparison columns |
| EN/light | INPUT, SCORES | Язык по умолчанию: поля/шапка и аудит |
| RU/dark | FIRE SIMULATION | Применение темы sticky panel; pixel/contrast assertions остаются в специализированных тестах |

В этой матрице нет 411dp или увеличенного шрифта. Единственная увеличенная UI
проверка — hero title RU/360/1.3/light выше. Дешёвые проверки sp-ролей и контрастности
не являются дополнительными экранными layout-прогонами.

Дополнительные существующие матрицы также подпадают под глобальное правило:

| SIMPLIFY — точные методы/источник | Оставляемый контракт и сценарии; риск |
|---|---|
| FireSimulationStickySummaryRobolectricTest.`comparison matrix at 320dp`, `comparison matrix at 360dp`, `comparison matrix at 411dp` → assertComparisonMatrix | Единственный запуск 360; все текущие status/years cases на RU/light, targeted DE/light для длинных cells и RU/dark для цвета delta, scale 1.0. Сохранить строки, baseline/две колонки, кнопки/count, отсутствие overflow и formatter agreement. Не удалять случаи бизнес-статусов, убирать только пересечение визуальных параметров. Риск потерянных редких layouts принят budget. |
| FireSimulationStickySummaryRobolectricTest.`opaque sticky panel at 320dp`, `opaque sticky panel at 360dp`, `opaque sticky panel at 411dp` → assertStickyPanel; весь класс сейчас имеет default w320dp | Оставить RU/360/1.0 в light/dark: pin к topbar, nav bottom, opaque pixels/insets/corners, no bleed-through после двух scroll, один reset, callback/resetCount и отсутствие leverCalls. Перенести остальные функциональные методы класса на стандарт. Pixel capture здесь — assertion в памяти, не диагностический мусор. 411 не защищает отдельную ветвь P4, исключить. |
| FireRetirementScreenRobolectricTest.`info cards collapse independently in all languages themes and large font`; AuditScoreScreenRobolectricTest.одноимённый метод | Оставить по одному RU/light/360/1.0 запуску каждого экрана: независимое раскрытие двух карточек, full body, 48dp target, contentDescription expand/collapse. Шесть локализованных title/body/labels проверять без полного рендера в LocalizationReleaseTextTest/минимальном semantics-тесте; это перенос assertions, не их удаление. Риск: легко потерять доступность при сокращении циклов. |
| FireRetirementScreenRobolectricTest.`base hero and simulation status texts wrap without overflow across all six languages`; `retirement age row shows short unreachable label in both subtabs across all six languages and themes`; `retirement age row does not clip or overlap on 320dp screens and large font scale across all six languages` | RU/360/light, плюс DE/light для длинных status/age cells и RU/dark только цвет unreachable. Сохранить обе подвкладки, REACHED_NOW/UNREACHABLE/sub-tenth, reachable age, full text, gap/no overlap и короткий unavailable label. Не делать повторов этих статусов по всем темам/масштабам; шесть языков остаются в FireFormatterTest/FireSimulationComparisonFormatterTest. Уникальные label fit/order assertions третьего метода перенести в оставленный age-row метод до удаления 320-обёртки. |
| InputDataScreenRobolectricTest.`hint formulas preserve full text and style without value overlap at 320dp in all languages themes and font scales`; `horizon row displays formula with unified style, main result, and numeric substitution across languages and themes`; `horizon row has no clipping or overlap on narrow screens in all languages` | RU/360/light + DE/light только для formula/horizon; актуальные суммы, formula/source, hint toggle, порядок numeric substitution и no overlap сохранить. Theme style проверить RU/dark точечно на одной formula, без обхода всех языков. Убрать 320/scale1.6. Unique raw invalid/empty/horizon assertions в других методах KEEP; localized strings вынести в дешёвый P6-тест. |
| MetricsScreenRobolectricTest.`metric card formulas are absent when collapsed and wrap completely when expanded` | RU/360/light + DE/light на длинных formulas; оставить collapse/reexpand полного текста, bounds/info target/style. Удалить конфигурационный критерий «минимум 3 формулы обязаны переноситься при 320», сохранив отсутствие clipping. `all category filters sit in one horizontally scrollable row and filter cards` и `results export button is the only one at the bottom and keeps the original summary and share action`: перенести с 411 на стандарт, функциональные assertions KEEP. |
| UiDisclosureStateRobolectricTest (class @Config ru-w320dp-h800dp); InputKeyboardRobolectricTest и InputImeInsetsRobolectricTest (w400dp) | Стандарт 360, все методы KEEP: offsets/restore, item identity, IME и сохранность данных. Для IME сохранить фактические insets 300px и сравнения bounds, не подменять тест статической картинкой. Сами 400/320 не независимые риски. |
| CollapsibleInfoCardRobolectricTest.`collapsed card keeps compact vertical padding and full toggle target`; `large font does not clip the title` | Обе проверки на стандартном viewport, без Box 180dp/scale1.5; сохранить 64dp card/48dp target/click/body assertions в одном сценарии. Второй метод сейчас проверяет isDisplayed/target/body, не реальное отсутствие clipping, поэтому не выдавать его за доказательство layout. Слияние этих assertions допустимо после проверки текста; повышенный scale уже выделен hero title. |
| ScenarioTogglesPanelRobolectricTest.`scenario section headers omit 2_1 and 2_2 badges and align cleanly across all languages` | Geometry RU/light/360, targeted DE/light; сохранить отсутствие 2.1/2.2, выравнивание и active counters. Локализованное содержимое всех шести языков (`scenario descriptions match raised income payments and capped property sale in every language`) — KEEP/перенос в дешёвый тест без полной отрисовки, без потери raised/base income и capped sale. |
| FireRetirementScreenRobolectricTest.`FIRE birth date follows selected app language`; `slider subtitles preserve scenario titles in all six languages`; `base parameters live only in the FIRE subtab and show the base real return in every language`; `active change count and scenario info appear only in Simulation subtab across all languages`; `reset button has accessible touch target, decorative icon, and triggers reset across languages` | Функциональные wiring/base isolation/reset/semantics на RU/360/1.0; собрать все уникальные subtitle/date/count/labels шести языков в дешёвые проверки formatter/strings или минимального компонента. Не сводить локализацию к одной RU строке. Риск: UI может применить simulated вместо base; оставить реальные screen assertions. |

Содержательные локализационные/editor/semantics-тесты могут проверять шесть языков:
например, InputDataScreen invalid horizon, NumberInputField decimal parsing,
FireAccessibility labels. Они не требуют шести layout-обходов пяти экранов.
StatusTextContrastTest полностью KEEP; RU light/dark цветовые интеграции сохранять
как целевые проверки темы. Сокращение status-профилей FinancialConsistencyIntegrationTest
или общих цветов UnifiedStatusColorsRobolectricTest **DEFER**: точного эквивалента
для unavailable Fidelity, 50% net worth, Infinity debt и пиксельных проверок не доказано.

## DEFER: до фактической реализации соответствующего этапа

| Кандидат | Зависимость и обязательная замена |
|---|---|
| ApprovedUiRegressionTest.`score breakdown uses compact headings and savings titles without a colon in all languages` | FA-07/FA-10: сейчас scoresBreakdownTitle реально используется AuditScoreScreen. Убирать лишь obsolete heading map после удаления блока; metricSavingsRateTitle без двоеточия и RU savingsRateTitle с двоеточием сохранить. Остальные 4 метода класса KEEP: порядок enum, approved names/action и app_name Android resources шести локалей. Чистые строки/enum можно вынести в JVM, resource-проверку оставить минимальной Robolectric; это не повод удалить утверждённые названия. |
| AuditScoreScreenRobolectricTest.`breakdown keeps four compact rows and full heading at 320dp in all languages and themes` | FA-07: четыре строки/progress сейчас активны. До замены оставить assertions на RU/360/1.0/light (SIMPLIFY конфигураций по budget); затем заменить на отсутствие старого блока + gauge/share/9 IDs/N/A/new scores. Не сохранять старый layout как требование к новому UI. |
| FinanceCalculatorTest.`total score weights CFS and caps low CFS`, `total score combinatorial status matches formatted string with specific rounding edges`, `green total score is allowed with neutral cfs score six`; CapitalAndAuditConsistencyTest.`calculated total remains green with neutral CFS at both ends of the neutral interval`; total/cap части FinancialMetricEvaluatorTest.testSavingsRateThresholdsAndScores и zero-expenses теста | FA-06/FA-10: старый агрегатор/cap пока production. После переключения заменить только методические ожидания, сохранить независимые регрессии округления 6.95→7.0, 3.45→3.5, float edge, raw status thresholds и формулы CFS. Не чистить комбинации как «слишком много»: это дешёвая логическая проверка. |
| MetricsScreenRobolectricTest.`MetricsScreen does not display score out of ten badges on metric cards`; FinancialConsistencyIntegrationTest profile/audit/share/export и AuditorAnalysisTest/LocalizationReleaseTextTest ожидания старых экспортов | FA-08/FA-09: будущие N/A/forecast/new assessment и разделение трёх экспортов ещё отсутствуют. Сохранить raw values, locale/currency/plural/disclaimer/assumptions и ACTION_SEND; обновлять только реально изменённую семантику. |
| CapitalAndAuditConsistencyTest.`FIRE start capital equals nonnegative shared Fidelity capital for 1000 seeded inputs`; FinanceCalculatorTest.testFireCapitalInvestableAssetsAndUnsecuredDebts | FIRE-M03/M04: заменить FIRE zero floor/Fidelity equality на signed B0/U и переходы знака по новой спецификации, отдельно сохранить Fidelity/property/debt границы. До реализации НЕ удалять 1000 seeded inputs и не менять ожидания. |
| ProjectionEngineTest.testNoFinancialAssetsKeepTaxZeroEvenWithoutLiquidAssets, testLiquidAssetsWithoutFinancialAssetsKeepZeroCapitalGainsTax; FinanceCalculatorTest.testZeroFinancialAssetsCalculation; F=0 часть CapitalGainsTaxTest.testCapitalGainsTaxCalculationsAndImpact; tax UI integration/assumptions P6 | FIRE-M02/M08: пока F=0 реально выключает налог. После смены формулы V/W сохранить tax=0, r<=0, W=0, cap, invalid/horizon, temporary/deferred и связность engine→metrics→UI/export, заменить именно зависимость от текущего F/L. |
| ProjectionEngineTest.testRandomProfilesAgainstBruteForce, testDeficitBudgetMatchesBruteForce, testTrajectoryForDeficitAndBaseProfiles; ScenarioSimulationTest.testStressTestScenariosAccumulation, testJobLossUsesRaisedIncomeAcrossLiquidFinancialAndUncoveredDebtBoundaries; MainViewModelRobolectricTest.`job loss uses raised income while inheritance sale and large expense keep base income` | FIRE-M03–M07: поиск первого допустимого узла, signed trajectory и временный минус income вместо lump sum. Сохранить независимый oracle, stress monotonicity, все 14 сценариев/комбинации, base isolation и границы. Дороговизна/число итераций не доказательство дубликата; старые numeric expectations менять лишь с объяснением причины/разницы. |
| UnifiedStatusColorsRobolectricTest.`negative component progress bar in audit tab and checked scenario switch match unified red` | FA-07/FA-10: только old component bar станет obsolete. Gauge arc, checked negative Switch track, parameter text и обе темы остаются; нельзя удалять весь смешанный метод. Остальные cross-tab negative/neutral матрицы KEEP до доказанного переноса каждого уникального цвета. |
| FireRetirementScreenRobolectricTest hero/time размещение, отсутствие fire_hero_simulation_badge; FinanceAppRobolectricTest active scenario; sticky summary guards | FIRE-UI-01 после M09: текущее отсутствие старого бейджа/duplicate hero — KEEP уже сейчас. Не предполагать, что будущая горизонтальная строка срока реализована. Добавить RU/360/1.0/light bounds к внутреннему контейнеру; сохранить статусы/message layout, sticky и subtabs. Упрощение конфигураций независимо и допустимо раньше. |

## Инструменты, fixtures и стоимость пропуска

StoreScreenshotsTest — поддерживаемый инструмент выпуска, не завершённая разовая
миграция. Сохранить без изменения механизма opt-in и все существующие результаты:
`GENERATE_STORE_SCREENSHOTS=1 bash ./gradlew testDebugUnitTest --tests com.example.StoreScreenshotsTest --rerun-tasks`.
В TEST-C01 эта команда **не запускалась**. `createAndroidComposeRule<ComponentActivity>()`
создаётся в поле @Rule до входа в test method, `assumeTrue` находится первой строкой
метода: rule/Activity/Robolectric setup выполняется даже при skip; setContent,
ViewModel и генерация PNG — после assume, поэтому массовой генерации нет.
Время skipped test из baseline учитывает этот setup; оно не изолирует стоимость
отдельно rule. Оставить KEEP, если замер не доказывает существенных затрат.

Отдельных fixtures/helper-файлов или одноразовых migration/test tools в app/src/test
и app/src/androidTest не найдено. Вспомогательные private fun находятся в тестовых
классах; текстовый поиск по всем файлам не выявил private fun с единственным
упоминанием. Это эвристика, не доказательство отсутствия неиспользуемого кода.
JUnit @Test/@Before/@After/@Rule вызываются framework: отсутствие прямых callers
не основание удаления. `sampleProgressIndicatorFillPixel`, capturePanel и
assertOpaqueInsets реально питают assertions — KEEP. Диагностические файлы
TypographyLayout (TXT и 320/1.6 PNG) можно исключить вместе с obsolete конфигурациями;
сам inspect/scroll/expanded-formula guard сохранить. Не удалять production helpers,
зависимости, test runner или release-инструмент заодно с очисткой.

## Передача следующему этапу

1. **TEST-C02:** реализовать только независимые DELETE/MERGE/SIMPLIFY из реестра,
   сначала перенос уникальных assertions, затем удаление повторов. Применить
   глобальный budget к остальным функциональным UI-тестам без потери их контрактов.
   Не заявлять process-death coverage или прогноз экономии по сумме XML time.
2. **FA-01–FA-11:** новая спецификация→компоненты→агрегация→профили→summary→переключение→
   UI→методика/N/A→три экспорта→очистка→сквозная проверка. Использовать оставшийся
   эквивалентный тест вместо восстановления удалённых повторов. Новые финансовые
   границы pure JVM; всё содержание шести локалей сохранить.
3. **FIRE-M01–M09:** новая спецификация→налог→signed B0/динамика→границы→поиск→jobLoss→
   график→тексты→аудит. Пересмотреть DEFER только на реализованном коде. Затем
   **FIRE-UI-01** — размещение числового срока; не смешивать с текущей очисткой.
4. **TEST-C03:** после фактического завершения указанных этапов закрыть DEFER,
   обновить этот реестр точными удалениями/переносами и сопоставимым полным замером.
   Разделить эффект TEST-C02/03 и изменение числа тестов в FA/FIRE. Обычные gates
   AGENTS.md остаются прежними; gate TEST-C01 выполняет оркестратор.

В Git не включать XML, PNG, логи, init scripts и временные измерительные скрипты.
Никакие release APK, эмуляторы, screenshots opt-in или mutation testing для аудита
не запускались. Числа и поведение пользователя не изменены.
