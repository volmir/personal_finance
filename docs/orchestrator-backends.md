# Бэкенды оркестратора

В `orchestrator/config.json` ключ `backend` выбирает бэкенд агента: `"codex"` (значение по умолчанию) или `"opencode"` (`orchestrator.py`, `load_config`).

Модели задаются в `models` во вложенном формате — секции `codex` и `opencode`, внутри каждой tier-ключи `light`, `standard`, `heavy`; плоский формат (`models.light` и т. п.) сохранён только для совместимости и допустим лишь при `backend="codex"`.

Для opencode вариант reasoning задаётся суффиксом `#variant` в строке модели (например, `opencode/glm-5.3-flash-free#max`); ключ `efforts` при этом бэкенде игнорируется — при его наличии `load_config` печатает предупреждение в stderr.

Платные наборы моделей opencode описываются в ключе `model_sets.opencode` (набор → `light`, `standard`, `heavy`) и выбираются флагом `--models <имя>`; флаг действует только при `backend="opencode"` и заменяет `models.opencode` на один запуск.

Права агента opencode ограничивает ключ `permissions` в `opencode.json` целевого проекта: оркестратор не передаёт ни `--auto`, ни песочницу (см. комментарий в `build_agent_command`).

Для codex по-прежнему применяются `codex_bin`, `sandbox` и `efforts` из `config.json` (команда `codex exec` не изменилась).
