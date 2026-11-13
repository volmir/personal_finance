#!/usr/bin/env bash
# Очередь запускайте из корня проекта; --check работает из любого каталога.
set -euo pipefail
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

check_usage() {
    cat <<'EOF'
Использование: bash orchestrator/run.sh --check [full|orchestrator|android]
  full          Гейт перед коммитом: тесты оркестратора, unit-тесты и lint приложения
  orchestrator  Тесты оркестратора с заглушкой агента
  android       Компиляция Kotlin и lint приложения
Для --check по умолчанию: full. Ошибка любой проверки останавливает выполнение.
Режим --check не запускает очередь, агента и Git-операции.
EOF
}

# Проверки выполняются до запуска Python: рабочее дерево может быть изменено,
# а блокировка очереди уже занята родительским оркестратором.
if [[ "${1:-}" == --check ]]; then
    if (( $# > 2 )); then
        check_usage >&2
        exit 2
    fi
    profile="${2-full}"
    case "$profile" in
        --help|-h) check_usage; exit 0 ;;
        full|orchestrator|android) ;;
        *) check_usage >&2; exit 2 ;;
    esac
    cd -- "$SCRIPT_DIR/.."
    # Вложенный gate не должен создавать __pycache__ в защищённом orchestrator/.
    # Переменная наследуется и дочерними Python-процессами проверок.
    export PYTHONDONTWRITEBYTECODE=1
    case "$profile" in
        orchestrator) python3 -m unittest discover -s orchestrator/tests ;;
        android)
            bash ./gradlew :app:compileDebugKotlin :app:lintDebug --console=plain
            ;;
        full)
            python3 -m unittest discover -s orchestrator/tests
            bash ./gradlew testDebugUnitTest lintDebug --console=plain
            ;;
    esac
    exit 0
fi

if (( $# == 1 )) && [[ "$1" == --help || "$1" == -h ]]; then
    python3 "$SCRIPT_DIR/orchestrator.py" "$@"
    check_usage
    exit 0
fi

exec python3 "$SCRIPT_DIR/orchestrator.py" "$@"
