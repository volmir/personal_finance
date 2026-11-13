# Пример задачи проекта (не входит в рабочую очередь)

## DOC-1: Уточнить инструкцию локальной сборки
tier: light
gate: bash orchestrator/run.sh --check full
retries: 1
```prompt
Проверь раздел «Сборка и запуск» в README.md по текущим Gradle-файлам и AGENTS.md.
Уточни команды только при обнаружении расхождений. Не устанавливай SDK и JDK,
не меняй зависимости, данные, конфигурацию сборки и правила проекта.
В отчёте перечисли исправления либо объясни, почему они не нужны.
Не выполняй коммит и push: ими управляет оркестратор.
```

## SMOKE-01: Смоук бэкенда opencode — раздел «Бэкенды» в README оркестратора
tier: light
gate: bash orchestrator/run.sh --check full
retries: 1
```prompt
Создай файл docs/orchestrator-backends.md с кратким описанием бэкендов оркестратора
(5–10 строк, по-русски): в orchestrator/config.json ключ backend выбирает бэкенд агента
("codex", по умолчанию, или "opencode"); models — вложенный формат с секциями codex
и opencode; для opencode вариант reasoning задаётся суффиксом #variant в строке модели,
ключ efforts игнорируется, права агента ограничивает permissions в opencode.json
целевого проекта (оркестратор не передаёт --auto). Не меняй код и другие файлы.
```
