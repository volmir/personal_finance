# План: мультибэкенд оркестратора (Codex + OpenCode) и автономность модуля

Дата: 2026-11-12. Статус: согласован с владельцем, ожидает завершения DATA-01.
Вернуться к плану после выполнения задачи DATA-01 из очереди оркестратора.

## Контекст и текущее состояние

- В проекте живая контрольная точка `orchestrator/.runtime/checkpoint.json`:
  задача **DATA-01**, фаза `agent`, 6 попыток. Незакоммиченные правки в дереве
  (`docs/CAMPAIGN.md`, `tools/game_data_providers.py`, `tools/hint_text.py`,
  `tools/hint_vectors.json`, `DefinitionHint.kt`, `tools/definition_import_config.json`,
  `tools/tests/test_category_classification.py`, untracked: `tools/codex_data_prepare.sh`,
  `docs/snapshots/`, `core/.local/`, `.data.publish.lock`) — сохранённая работа
  прерванной DATA-01. Вручную не трогать, не коммитить, не стэшить.
- Остановка из-за лимита аккаунта Codex: сброс **16 ноября 2026, 09:43** локального времени.
- Первый проход DATA-01 остановился на неполной классификации категорий; кэши
  `.local/data01-cache/…` сохранены, отчёт агента — в diff `docs/CAMPAIGN.md`.
- Ветки: очередь — `auto/tasks` (push после каждой задачи включён).

## Этап 0 — завершить DATA-01 (16 ноября после 09:43)

1. `rm .data.publish.lock` (устаревший пустой lock от 2026-11-11 19:22, процессов нет).
2. Из корня проекта, на чистом от ручных правок дереве:
   ```sh
   CODEX_BIN="$PWD/tools/codex_data_prepare.sh" TASK_TIMEOUT=14400 \
     bash orchestrator/run.sh --only DATA-01
   ```
   Возобновление из контрольной точки; агент получит задание, предыдущий ответ/логи
   и указание сохранить полезные правки. После gate: коммит правок + журнала + обёртки,
   checkpoint удаляется.
3. Проверить после завершения: `checkpoint.json` отсутствует, `git status` чистый.

Пока checkpoint существует — НЕ менять ничего в `orchestrator/` (защищённые пути,
иначе возобновление заблокируется).

## Этап 1 — коммит 1: адаптер бэкендов + конфиг (ветка orchestrator/module)

От текущего `auto/tasks`: `git switch -c orchestrator/module auto/tasks`.

1. Ключ `backend` в `orchestrator/config.json`: `"codex"` (дефолт) | `"opencode"`;
   env-переопределение НЕ добавляем (слой env удаляется полностью).
2. `models`: вложенный формат `{codex: {...tiers}, opencode: {...tiers}}`;
   плоский формат валиден только при backend=codex (совместимость со старыми конфигами).
   Валидация секций при загрузке, до старта агента. Пустая строка = модель из настроек CLI.
   Для opencode вариант reasoning — суффикс `#variant` в строке модели
   (`provider/model#variant`); ключ `efforts` для opencode игнорируется (предупреждение).
   Для opencode не передаётся эквивалент песочницы: права задаются `permissions`
   в `opencode.json` целевого проекта (оркестратор не передаёт `--auto`, только
   документирует). Агент запускается как `--agent build`.
3. Удаление слоя окружения целиком (~30 строк в `load_config`): MODEL_*, EFFORT_*,
   CODEX_BIN, SANDBOX, TASKS_FILE, PROGRESS_FILE, BRANCH, REMOTE, TASK_TIMEOUT,
   GATE_TIMEOUT, GIT_TIMEOUT, RESTART_RETRIES, RETRY_DELAY, NO_PUSH, GATE_BASE.
   Приоритет: флаги CLI → config.json → значения по умолчанию.
4. Миграция DATA-01: `orchestrator/config.data-01.json` (agent_bin →
   `tools/codex_data_prepare.sh`, task_timeout: 14400); команда в `docs/CAMPAIGN.md`
   заменяется на `--only DATA-01 --config orchestrator/config.data-01.json`.
   Обёртка `tools/codex_data_prepare.sh` остаётся без изменений.
5. Адаптер запуска агента: для codex команда байт-в-байт текущая
   (`codex exec --model M -c model_reasoning_effort=E --sandbox S --output-last-message last -`,
   промпт через stdin); для opencode — `opencode run --agent build --model …`,
   захват stdout, извлечение финального ответа. Для opencode использовать
   `--format json` (см. решённый открытый вопрос). Проверить при реализации:
   читает ли `opencode run` промпт из stdin.
   Парсинг «try again at …» остаётся в Codex-ветке; для opencode — свой разбор лимитов.
6. Тесты: заглушка opencode, кейсы построения команды, валидации вложенного/плоского
   models, миграции DATA-01; переход тестов с env-заглушек на временные `--config`.
   Регрессионный инвариант: codex-команда побайтно равна текущей; плоский конфиг +
   backend=codex даёт те же tier-значения.

## Предложенные free-модели для opencode (проверить смоук-пробой перед очередью)

```jsonc
"models": {
  "codex": { "light": "gpt-5.6-luna", "standard": "gpt-6.1-sol", "heavy": "gpt-6-astra" },
  "opencode": {
    "light":    "opencode/glm-5.3-flash-free",
    "standard": "opencode/glm-5.3-flash-free#max",
    "heavy":    "opencode/beam-free#max"
  }
}
```

Запасные: light — `opencode/minimax-m3.1-flash-free`; standard —
`opencode/deepseek-v4.1-flash-free#max`; heavy — `opencode/flint-40b-free#max`.
Не взяты: inkling-2-small-free (самый маленький), nemotron-3.5-super-vl-free
(VL не нужен), iceman/longcat/mimo (без reasoning-вариантов).
Смоук-критерии перед боевым запуском: удержание формата `ИТОГ:`, соблюдение
защищённых путей, gate с первой попытки; на free-моделях оставить щедрые
restart_retries/retry_delay (квоты не проверены).

### Протокол смоук-проверки моделей opencode

Уровень 1 (обязательно, ~10–20 тыс. токенов суммарно на 3 модели): ручной
`opencode run --agent build --model <модель>` с инструкцией «прочитай
orchestrator/README.md, ничего не меняй, выдай строку ИТОГ: …». Проверяет
формат ответа, видимость файлов, извлечение ответа адаптером.

Уровень 2 (обязательно для light, ~30–100 тыс. токенов + время gate):
временная секция `SMOKE-01` в `orchestrator/TASKS.md` (новый ID), малая
документационная правка, gate `--check full`; запуск `bash orchestrator/run.sh
--only SMOKE-01 --config <конфиг opencode>`; после пробы секция удаляется из
очереди (запись журнала остаётся). Репетиция полного цикла: очередь → агент →
gate → коммит → журнал.

Уровень 2 для standard/heavy отдельными пробами НЕ проводится: проверка
«на месте» на первых реальных задачах соответствующих tier'ов новой пачки,
с контролем результата владельцем. Итог смоука фиксируется в отчёте
(критерии: ИТОГ, защищённые пути, gate с первой попытки).

### Результат смоука уровня 1 (2026-11-12): ПРОЙДЕН 3/3

- light `opencode/glm-5.3-flash-free`, standard `opencode/glm-5.3-flash-free#max`,
  heavy `opencode/beam-free#max` — каждая прочитала `orchestrator/README.md`
  с первой попытки, выдала ровно одну строку `ИТОГ:` с осмысленным содержанием,
  попыток записи 0 (git status после проб не изменился).
- Модели свободно запускаются через `opencode run --agent build --model …`
  без `--auto`; права записи корректно ограничены конфигом.

## Этап 2 — коммит 2: автономность модуля

1. Профили `--check` из кода → ключ `checks` в `orchestrator/config.json`
   (словарь «профиль → массив shell-команд»). Встроенным остаётся только
   `orchestrator` (самопроверка). Профили этого проекта (tools/core/data/android/full)
   переносятся в config.json с командами один в один (сверены с
   test_project_integration.py:46-66) — gates всех 36 секций TASKS.md продолжают работать.
   `run.sh --check --help` печатает профили динамически.
2. `README.md` переписать как документацию автономного модуля: установка в
   произвольный проект, формат очереди, конфиг (backend/models/checks), требования,
   границы, миграция с env-переменных. Без упоминаний «Составь слова»/Gradle/Android.
3. Новый `orchestrator/AGENTS.md` — правила работы над самим модулем; в корневом
   `AGENTS.md` раздел об оркестраторе сокращается до ссылки.
4. `examples/` — универсальные примеры (пара задач + секция checks); `VALIDATION.md`
   — в терминах модуля.

## Ветки и слияние

- Очередь работает в `auto/tasks` (авто-push), рефакторинг — в `orchestrator/module`.
  Пересечение по файлам нулевое. Не коммитить рефакторинг в `auto/tasks` (оркестратор
  сметёт его `git add -A` в коммит задачи) и не запускать очередь параллельно с правками.
- Финал: `git switch main && git merge auto/tasks && git merge orchestrator/module`,
  push вручную. Возможный конфликт — только config.json, приоритет версии рефакторинга.
- Новая пачка очереди после рефакторинга: сначала merge `orchestrator/module`
  в `auto/tasks`.

## Совместимость Codex-пути (проверено по коду 2026-11-12)

Без изменений: команда агента, stdin-промпт, `--output-last-message`, строка `ИТОГ:`,
продолжение после сбоя, парсинг лимитов, дефолты моделей/таймаутов, формат TASKS.md,
fingerprint (не включает backend/конфиг), PROGRESS.md v1, checkpoint v1, CLI-флаги,
gate_base, блокировка, Git-поведение.
Осознанные поломки: (1) слой env удалён — в проекте единственный потребитель
(DATA-01) мигрирует в том же коммите; (2) профили --check из кода — в этом репозитории
сохранены через config.json, внешние копии модуля требуют свой конфиг (задокументировать).
Перед коммитом 1 проверить: checkpoint отсутствует, дерево чистое.

## Проверки

- `python3 -m unittest discover -s orchestrator/tests`
- `bash -n orchestrator/run.sh && bash orchestrator/run.sh --check orchestrator`
- после коммита 2: `bash orchestrator/run.sh --check full` (профиль из config.json)

## Открытые проверки при реализации

1. `opencode run`: чтение промпта из stdin (или подача через argv/файл с учётом
   лимита 128 КБ на argv-аргумент Linux).
2. ~~Извлечение финального ответа opencode из stdout.~~ РЕШЕНО (2026-11-12):
   для адаптера использовать `opencode run --format json`; вывод — построчный
   поток событий (`step_start → tool_use → step_finish → …`), финальный ответ —
   последний event с `part.type == "text"`, поле `part.text` — чистое сообщение
   без ANSI-кодов и служебных префиксов. Plain stdout (формат `default`)
   замусорен escape-кодами и префиксами `> build · model`, `→ Read …` —
   вырезать ответ из него ненадёжно. Закрепить контракт регрессионным тестом
   (записать эталонный пример потока в фикстуры тестов).
3. Смоук выбранной free-модели heavy на реальной задаче.
