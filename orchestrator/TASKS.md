## UI-01: Перенести формулу «Активы» в существующую подсказку
tier: standard
gate: bash orchestrator/run.sh --check unit && bash orchestrator/run.sh --check android
retries: 1
````prompt
Следуй AGENTS.md. Только экран №1, показатель «Активы». Не переносить формулу доходности до UI-02. Не трогать другие экраны, расчёты, экспорт и оркестратор. Commit/push выполняет оркестратор.

Проверенные файлы:
- app/src/main/java/com/example/ui/screens/InputDataScreen.kt
- app/src/main/java/com/example/util/localization/AppStrings.kt
- app/src/main/java/com/example/util/localization/StringsRu.kt (и остальные локали только для сверки)
- app/src/test/java/com/example/InputDataScreenRobolectricTest.kt

Текущая структура InputDataScreen.kt:
- около строк 123–125: expandedHints, assetsFormulaExpanded, realReturnFormulaExpanded;
- около строк 343–354: CategoryBreakdownHint(metricName = strings.assetsBreakdownName, isExpanded = "assets" in expandedHints, hintCollapsed = strings.assetsHintCollapsed, hintExpanded = strings.assetsHintExpanded, categories = strings.assetsCategories, modifier = Modifier.padding(top = 6.dp));
- около строк 359–366: MetricIndicatorRow(title = strings.totalAssetsTitle, collapsibleFormula = true, formulaExpandedState = assetsFormulaExpanded, formula = strings.totalAssetsFormula, value = Formatters.formatMoney(metrics.totalAssets, inputData.currencySymbol, lang), valueColor = MaterialTheme.colorScheme.primary);
- около строк 675–795: MetricIndicatorRow, ветка collapsibleFormula с TextButton(strings.metricCardInfo), другая ветка с постоянно видимой формулой;
- около строк 998–1116: CategoryBreakdownHint, внутри if (isExpanded) инструкция и categories.forEachIndexed.

Выполнить последовательно:
1. В конец списка параметров CategoryBreakdownHint (после modifier, с сохранением совместимости остальных вызовов) добавить `formula: String? = null`. Внутри `if (isExpanded)`, СТРОГО после завершения categories.forEachIndexed, добавить только при ненулевой формуле:
   - Spacer(height = 8.dp);
   - Text(text = strings.metricCardFormulaPrefix.trimEnd(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface));
   - Text(text = переданная формула, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)).
   Использовать существующий локализованный `metricCardFormulaPrefix` (RU «Формула: »). Не менять исходные строки totalAssetsFormula и realReturnFormula, не добавлять дублирующие переводы. Не ограничивать формулу maxLines/ellipsis. Не изменять инструкцию и список категорий. Другие вызовы CategoryBreakdownHint по умолчанию не получают новую секцию.
2. Передать `formula = strings.totalAssetsFormula` ТОЛЬКО в вызове с ключом "assets". Добавить стабильный тег `input_assets_hint` на существующий modifier (не заменять padding). Формула отображается только при раскрытии этой подсказки. Состояние раскрытия остаётся в expandedHints.
3. В MetricIndicatorRow изменить тип `formula: String` на `formula: String?` (не вводить отдельный дублирующий компонент). В ветке обычного Row обернуть существующий Text(formula) в `formula?.let { ... }`, не добавляя Spacer при null. Сохранить прежний стиль и все параметры. На переходном этапе оставить ветку collapsibleFormula для доходности; сделать её условие `if (collapsibleFormula && formula != null)`, сохранив существующее поведение при ненулевой формуле. Это гарантирует, что Text не получает nullable строку. Остальные четыре вызова продолжают передавать исходную ненулевую формулу.
4. В вызове MetricIndicatorRow с title = strings.totalAssetsTitle удалить `collapsibleFormula = true` и `formulaExpandedState = assetsFormulaExpanded`, заменить аргумент на `formula = null`, добавить `modifier = Modifier.testTag("input_metric_total_assets")` (если уже есть тег, не дублировать). Удалить только неиспользуемое объявление assetsFormulaExpanded. Не удалять realReturnFormulaExpanded. Теперь обычная ветка Row даёт название слева и исходное значение справа, с вертикальным центрированием и padding 12.dp. Не добавлять пустой ряд или Info. Сохранить Formatters, цвет, Surface, значение, переключатель `toggle_emergency_fund_mode` и поля ввода. Не изменять промежуток 12.dp перед итоговым показателем или 8.dp перед переключателем без объективной необходимости.

Тесты (обязательная часть задачи):
- В InputDataScreenRobolectricTest заменить для активов старый сценарий клика по StringsRu.metricCardInfo на открытие существующей подсказки по тегу input_assets_hint. Тест `only assets and real return formulas collapse independently and restore after scrolling and recreation` сейчас проверяет два старых Info; обновить ТОЛЬКО часть активов, оставить доходность на прежнем Info. Проверить, что в свернутой подсказке формулы нет, при раскрытии есть ровно один полный текст totalAssetsFormula и заголовок metricCardFormulaPrefix.trimEnd(), при сворачивании исчезает; отдельный Info в строке input_metric_total_assets отсутствует. После скролла и StateRestorationTester состояние сохраняется. Скроллить реальный LazyColumn до нужного элемента, использовать unmerged tree для дочерних Text, если clickable Surface объединяет семантику.
- Тест `collapsible formulas preserve full text and style without value overlap at 320dp in all languages themes and font scales` сейчас напрямую рендерит две MetricIndicatorRow с collapsibleFormula=true. Заменить тестовую часть активов на Column с настоящими CategoryBreakdownHint и MetricIndicatorRow(formula = null) и управляемым состоянием подсказки. Для формулы оставить проверки точного текста, labelSmall/Monospace/primary и assertTextFits; для суммы оставить проверку полного текста и неперекрытия подписи/суммы (при крупном шрифте допустимо переносить подпись). Старую проверку доходности сохранить до UI-02. Использовать уже имеющиеся textLayoutResult/textStyle/assertTextFits и текущую матрицу 6 языков, обе темы, 320dp, fontScale 1/1.6. Не удалять покрытие четырёх постоянно видимых формул.
- При необходимости разделить два теста на отдельные сценарии вместо смешивания старого и нового поведения. Не менять ожидания других экранов. Проверить, что новое поведение не добавляет формулу в другие CategoryBreakdownHint.

Приёмка: формула активов видна ровно один раз и только в раскрытой основной подсказке, отдельного Info нет, итоговая сумма и переключатель прежние, реальная доходность и остальные формулы пока не изменены. Пройти unit + android gate. Итог: изменённые файлы, точные тесты и реальные результаты.
````

## UI-02: Перенести формулу «Реальная доходность капитала» в подсказку об инвестициях
tier: standard
gate: bash orchestrator/run.sh --check unit && bash orchestrator/run.sh --check android
retries: 1
````prompt
Следуй AGENTS.md. Выполнять после UI-01. Только экран №1, карточка «Инвестиции и инфляция». Не изменять финансовую формулу, налог, расчётные значения, другие экраны, экспорт и оркестратор. Commit/push делает оркестратор.

Файлы: app/src/main/java/com/example/ui/screens/InputDataScreen.kt; app/src/test/java/com/example/InputDataScreenRobolectricTest.kt. Строковые ресурсы уже существуют.

Проверенные точки в исходнике до UI-01:
- около строк 500–511: CategoryBreakdownHint(metricName = strings.investmentsBreakdownName, isExpanded = "investments" in expandedHints, hintCollapsed = strings.investmentsHintCollapsed, hintExpanded = strings.investmentsHintExpanded, categories = strings.investmentsCategories, modifier = Modifier.padding(top = 6.dp));
- около строк 513–524: ОТДЕЛЬНАЯ CategoryBreakdownHint с ключом "capitalGainsTax", categories = strings.capitalGainsTaxCategories; не изменять;
- около строк 529–537: MetricIndicatorRow(title = strings.realReturnTitle, collapsibleFormula = true, formulaExpandedState = realReturnFormulaExpanded, formula = strings.realReturnFormula, value = Formatters.formatPercent(metrics.realReturnPercent, lang), valueColor = StatusColors.getColor(FinancialMetricEvaluator.getRealReturnStatus(metrics.realReturnPercent)), modifier = Modifier.testTag("input_metric_real_return")).

Выполнить:
1. В существующем вызове CategoryBreakdownHint с ключом "investments" передать `formula = strings.realReturnFormula`, используя новый опциональный параметр из UI-01. Добавить `input_investments_hint` к существующему modifier, сохранив padding. Локализованный заголовок и стиль формулы уже находятся в CategoryBreakdownHint: не дублировать их. Формула должна быть в конце раскрытой подсказки об инвестициях, не в налоговой подсказке. Исходную строку realReturnFormula не менять.
2. В вызове MetricIndicatorRow с title = strings.realReturnTitle заменить `formula = strings.realReturnFormula` на `formula = null`, удалить `collapsibleFormula = true` и `formulaExpandedState = realReturnFormulaExpanded`. Сохранить testTag, Formatters.formatPercent и StatusColors/FinancialMetricEvaluator без изменений. Удалить ставшее лишним объявление realReturnFormulaExpanded. Итоговая строка использует обычный Row (как активы) без Info и пустого пространства.
3. Удаление мёртвого кода только после проверки `git grep -n -e 'collapsibleFormula' -e 'formulaExpandedState' -- '*.kt'`: если иных реальных потребителей MetricIndicatorRow с этой веткой нет, удалить оба параметра и всю ветку if (collapsibleFormula...) этого компонента, оставив только существующий обычный Row с nullable formula из UI-01. Обновить относящийся к этому компоненту KDoc. Удалить лишь действительно неиспользуемые импорты. Не удалять общие строки metricCardInfo/metricCardExpand/metricCardCollapse и не изменять MetricCard.kt экрана №2. Если обнаружатся новые внешние потребители, сохранить ветку для них и указать это в отчёте.
4. Не менять CategoryBreakdownHint с ключом "capitalGainsTax" и другие подсказки. Состояния assets и investments должны оставаться независимыми через существующий expandedHints. Другие четыре MetricIndicatorRow (cashFlow, savingsRate, netWorth, horizon) сохраняют постоянно видимые формулы.

Тесты:
- В InputDataScreenRobolectricTest завершить обновление `only assets and real return formulas collapse independently and restore after scrolling and recreation`: обе формулы раскрываются через свои подсказки по тегам input_assets_hint / input_investments_hint. Проверить независимость, закрытие, восстановление после скролла и StateRestorationTester, отсутствие отдельного Info в обеих итоговых строках, отсутствие формулы доходности в налоговой подсказке. Проверить прежние четыре постоянно видимые формулы. Использовать unmerged tree для дочерних Text при объединённой семантике.
- Завершить переделку `collapsible formulas preserve full text and style without value overlap at 320dp in all languages themes and font scales`: теперь оба случая рендерят новый настоящий CategoryBreakdownHint и MetricIndicatorRow(formula = null), с управляемым состоянием. Проверить неизменённые числовые значения и стиль формул, отсутствие обрезания/перекрытия, показ заголовка и формулы только при раскрытии, отсутствие старых Info. Сохранить исходную матрицу 6 языков, обе темы, 320dp, fontScale 1/1.6. Удалить только устаревшие assertions о геометрии старой кнопки Info, заменить соответствующими проверками геометрии новой подсказки; не удалять тест целиком. При необходимости уточнить названия тестов по новому контракту.
- Проверить git grep на оставшиеся старые вызовы в тестах. Не менять тесты или код MetricsScreen: его Info остаётся.

Приёмка: обе формулы перемещены в свои основные подсказки, оба отдельные Info удалены, налоговая подсказка и четыре другие формулы сохранены. Расчёты и цвета не изменены. Unit + android gate проходят. В отчёте отметить, удалена ли неиспользуемая ветка MetricIndicatorRow, какие тесты действительно выполнены.
````

## UI-03: Увеличить расстояние между показателями «Детализация баллов»
tier: light
gate: bash orchestrator/run.sh --check unit && bash orchestrator/run.sh --check android
retries: 1
````prompt
Следуй AGENTS.md. Только экран №3 «Анализ», карточка «Детализация баллов». Commit/push делает оркестратор.

Файл: app/src/main/java/com/example/ui/screens/AuditScoreScreen.kt. Точное место около строк 195–251: Card с testTag("scores_breakdown_card"), Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)). В этой Column находятся заголовок с testTag("scores_breakdown_title") и четыре вызова private ScoreItemContent в порядке score_savings_rate, score_safety_months, score_debt_ratio, score_cfs.

Сделать только следующее: сохранить внешнюю Column с padding 16.dp и spacedBy(8.dp), заголовок оставить как есть. Обернуть четыре существующих вызова ScoreItemContent в одну вложенную Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)). Не менять аргументы, порядок или внутренности ScoreItemContent (около строки 513). Это даёт 8.dp между заголовком и первым показателем, ровно 12.dp между соседними показателями, без дополнительного отступа после последнего. Не добавлять Divider, Card, рамки, новые цвета. Не менять другие LazyColumn/items, внешние отступы, размеры шрифтов, баллы, прогресс-бары, расчёты и экспорт.

Тест: app/src/test/java/com/example/AuditScoreScreenRobolectricTest.kt, метод `breakdown keeps four compact rows and full heading at 320dp in all languages and themes` (около строки 174). В цикле по rows сейчас `assertEquals("$tag has an internal 8dp gap", 8.dp, bounds.top - previousBottom)`. Использовать rows.withIndex() либо эквивалент: expectedGap = if (index == 0) 8.dp else 12.dp. Сохранить проверки полного заголовка, четырёх тегов, текста, значения, баллов, ProgressBarRangeInfo, локалей, тем и fontScale. Не менять ApprovedUiRegressionTest, кроме объективно устаревшей проверки расстояния, если такая есть.

Приёмка: только три промежутка между показателями выросли с 8 до 12.dp; суммарный рост высоты на 12.dp при прочих равных. Все четыре показателя внутри одной карточки, заголовок и внутренности прежние. Unit + android gate проходят. Указать результат теста и изменённые файлы.
````

## UI-04: Уменьшить вертикальные отступы «Допущения модели» и «Отказ от ответственности»
tier: light
gate: bash orchestrator/run.sh --check unit && bash orchestrator/run.sh --check android
retries: 1
````prompt
Следуй AGENTS.md. Только раскрывающиеся информационные карточки экрана №3 и базовой подвкладки FIRE экрана №4. Commit/push выполняет оркестратор.

Проверенные файлы:
- app/src/main/java/com/example/ui/components/CollapsibleInfoCard.kt (97 строк на исходном HEAD);
- app/src/main/java/com/example/ui/screens/AuditScoreScreen.kt, вызовы с testTag = "audit_assumptions_card" и "developer_disclaimer_card";
- app/src/main/java/com/example/ui/screens/FireRetirementScreen.kt, вызовы с testTag = "fire_assumptions_card" и "fire_disclaimer_card" внутри if (!isSimulationSubtab).

Точная причина большой высоты: в CollapsibleInfoCard внешняя Card содержит Column(modifier = Modifier.padding(14.dp)), внутри Row с IconButton(modifier = Modifier.size(48.dp)). При однострочном заголовке получается 48 + 14 + 14 = 76.dp без учёта иных ограничений. Замена на 12.dp дала бы всего 4.dp экономии, поэтому для умеренного заметного уменьшения выбран 8.dp сверху и снизу (64.dp при однострочном заголовке и прочих равных).

Сделать: проверить `git grep -n 'CollapsibleInfoCard(' -- '*.kt'`. Если в production имеются только четыре указанных вызова, изменить одну строку в общем компоненте: `Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))`. Если имеются новые потребители за пределами этих четырёх карточек, добавить опциональный параметр verticalPadding: Dp = 14.dp в CollapsibleInfoCard и передать 8.dp только в четырёх указанных вызовах (не затрагивать новые места). Не менять горизонтальный padding 14.dp, IconButton 48.dp, ведущую иконку 20.dp, промежуток 10.dp до заголовка, стиль текста, Card shape/border/colors. Не менять Spacer(4.dp) между заголовком и body, body padding(start = 30.dp), сам текст, независимое состояние rememberSaveable. Не менять внешний интервал 4.dp между двумя карточками в обоих экранах и внешние Spacer. В «Симуляции» эти карточки по-прежнему отсутствуют.

Тесты:
- app/src/test/java/com/example/AuditScoreScreenRobolectricTest.kt: `info cards collapse independently in all languages themes and large font` и `info card states survive tab disposal and saved instance restoration`.
- app/src/test/java/com/example/FireRetirementScreenRobolectricTest.kt: `info cards collapse independently in all languages themes and large font`, `each internal subtab keeps only its own cards and FIRE explanations`.
- app/src/test/java/com/example/FireSubtabRecreationRobolectricTest.kt: сохранить проверку восстановления состояний.
- Добавить компактную проверку геометрии к существующим тестам или отдельный небольшой тест CollapsibleInfoCard в тестовом пакете com.example: при однострочном заголовке, fontScale 1, достаточно широкой карточке, высота свернутого блока ожидаемо 64.dp (допуск округления 1.dp), верхний/нижний зазор от Row/кнопки 8.dp, минимум 48.dp для тега `${testTag}_toggle`. При более крупном шрифте не ограничивать высоту Card сверху, заголовок переносится без обрезания. Сохранить проверки наличия/отсутствия `${testTag}_body`, иконок, локализованных описаний раскрытия и независимых состояний. Не утверждать, что при крупном шрифте высота всегда 64.dp.

Приёмка: у четырёх карточек умеренно уменьшена высота, целевая область нажатия не уменьшена, другие компоненты и интервалы не изменены, тексты раскрываются/сворачиваются как раньше, оба экрана и обе темы работают. Unit + android gate проходят. Указать измеренную высоту, изменения и тесты.
````
