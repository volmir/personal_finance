# Первая сборка и первый релиз на GitHub

Проверено 3 ноября 2026 года для `volmir/personal_finance`, коммит приложения `d18261d`. Команды рассчитаны на Bash в Ubuntu x86_64. Первый релиз: **versionName `1.0`, versionCode `1`, тег `v1.0`**. Эти значения уже заданы в `app/build.gradle.kts` и рецепте F-Droid.

## Последовательность трёх инструкций

1. Выполнить эту инструкцию: подготовить окружение и постоянный ключ → собрать и проверить APK → проверить приложение на устройстве → добавить секреты GitHub → отправить тег → дождаться APK в GitHub Release.
2. Подать заявку в [F-Droid](./FDROID_SETUP.md), когда выполнены условия включения.
3. Подать заявку в [IzzyOnDroid](./IZZYON_DROID_SETUP.md), если проект соответствует его правилам.

После публикации GitHub Release заявки в два каталога независимы: их можно готовить параллельно, ждать одобрения одного перед другим не требуется. До подачи в F-Droid выбрать способ подписи: собственная подпись через воспроизводимую сборку либо отдельная подпись F-Droid.

## Результат проверки готовности

| Проверка | Результат на 03.11.2026 |
| --- | --- |
| Репозиторий | Публичный, `main`, лицензия MIT |
| Версия | `io.github.volmir.personalfinance`, `1.0` / `1`, minSdk 24, targetSdk 36 |
| Инструменты проекта | AGP 9.1.1, Gradle Wrapper 9.3.1, JDK 17, compileSdk 36.1 |
| Тесты текущего коммита в GitHub Actions | Успешны: [запуск 41610943612](https://github.com/volmir/personal_finance/actions/runs/41610943612) |
| Релизы и теги | Ещё не созданы |
| Секреты подписи GitHub | Все четыре отсутствуют; релизная задача без них завершится ошибкой |
| Локальная повторная сборка | Заблокирована окружением: `sdk.dir` указывает на отсутствующий `/tmp/personal-finance-build-tools/android-sdk`; JDK по умолчанию — 25 |
| Существующий APK | `app-release-unsigned.apk`, 1 851 647 байт, от 02.11.2026; подписи нет, публиковать и устанавливать его нельзя |
| Новая релизная сборка и Android Lint | Не подтверждены: локальная команда остановилась до запуска задач из-за отсутствия SDK |
| Fastlane | 6 языков, по 4 скриншота; иконки 512×512; длины текстов укладываются в лимиты. Пропорции скриншотов 1080×2400 могут потребовать полей для валидаторов с ограничением 2:1 |
| F-Droid | В рецепте отсутствуют `RepoType` и `Repo`; тег `v1.0` отсутствует. Дополнительно проверить условие возраста проекта и сборку в F-Droid CI |
| IzzyOnDroid | Требуется подписанный APK; отдельно проверить применимость AI policy |

Вывод: проект подготовлен к выпуску частично. Успешный запуск тестов на `main` ещё не подтверждает сборку и работоспособность подписанного release APK: в текущем workflow релизная задача запускается только по тегу.

## Шаг 1. Подготовить JDK и Android SDK

Установить JDK 17 и вспомогательные программы:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk git curl unzip python3 gh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/36.0.0:$PATH"
java -version
```

Если Android SDK Command-Line Tools уже установлены в этом SDK, перейти к установке пакетов ниже. Если нет, скачать пакет с [официальной страницы Android](https://developer.android.com/studio#command-line-tools-only). Для проверенного на дату инструкции пакета:

```bash
SDK_DOWNLOAD_DIR=$(mktemp -d)
curl -fL https://dl.google.com/android/repository/commandlinetools-linux-15859902_latest.zip \
  -o "$SDK_DOWNLOAD_DIR/tools.zip"
printf '%s  %s\n' \
  4e4c464f145a7512b57d088ac6c278c03c9eea610886b35a5e0804e74eedf583 \
  "$SDK_DOWNLOAD_DIR/tools.zip" | sha256sum -c -
unzip -q "$SDK_DOWNLOAD_DIR/tools.zip" -d "$SDK_DOWNLOAD_DIR"
mkdir -p "$ANDROID_HOME/cmdline-tools"
mv "$SDK_DOWNLOAD_DIR/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
```

Установить пакеты; лицензии прочитать и принять интерактивно:

```bash
sdkmanager --sdk_root="$ANDROID_HOME" --licenses
sdkmanager --sdk_root="$ANDROID_HOME" \
  "platform-tools" "platforms;android-36.1" "build-tools;36.0.0"
cd /var/www/personal_finance
```

Исправить только `sdk.dir`, сохранив остальные локальные настройки:

```bash
python3 - <<'PY'
from pathlib import Path
import os
p = Path('local.properties')
lines = p.read_text().splitlines() if p.exists() else []
lines = [line for line in lines if not line.lstrip().startswith('sdk.dir=')]
lines.append('sdk.dir=' + os.environ['ANDROID_HOME'])
p.write_text('\n'.join(lines) + '\n')
PY
bash ./gradlew --version
```

Ожидается Gradle 9.3.1 и JVM 17. `local.properties` игнорируется Git. `gradlew` хранится без бита исполнения, поэтому здесь используется `bash ./gradlew`; workflow сам выполняет `chmod +x`.

Совместимость инструментов подтверждена в [документации AGP 9.1.1](https://developer.android.com/build/releases/agp-9-1-0-release-notes). Установка пакетов описана в [документации sdkmanager](https://developer.android.com/tools/sdkmanager).

## Шаг 2. Создать постоянный ключ подписи

Выполнять генерацию только один раз. Если релизный ключ уже существует, использовать его вместо создания нового.

```bash
umask 077
mkdir -p "$HOME/.local/share/personal-finance-signing"
export KEYSTORE_PATH="$HOME/.local/share/personal-finance-signing/release.p12"
export KEY_ALIAS=upload
test ! -e "$KEYSTORE_PATH" && keytool -genkeypair -v \
  -storetype PKCS12 -keystore "$KEYSTORE_PATH" -alias "$KEY_ALIAS" \
  -keyalg RSA -keysize 3072 -validity 10000
```

Ввести пароль и данные сертификата. Для этого PKCS12 использовать одинаковый пароль хранилища и ключа. Сохранить ключ и пароль в отдельном защищённом резервном хранилище до публикации: последующие обновления должны сохранять совместимую подпись.

Загрузить пароли в переменные текущего терминала без вывода на экран:

```bash
read -r -s -p 'Keystore password: ' STORE_PASSWORD
printf '\n'
export STORE_PASSWORD
export KEY_PASSWORD="$STORE_PASSWORD"
keytool -list -v -keystore "$KEYSTORE_PATH" -alias "$KEY_ALIAS" \
  -storepass:env STORE_PASSWORD
```

Записать SHA-256 сертификата: это отпечаток подписи, отдельный от SHA-256 самого APK.

## Шаг 3. Собрать и проверить release APK до публикации

```bash
cd /var/www/personal_finance
bash ./gradlew --no-configuration-cache testDebugUnitTest lintRelease assembleRelease
export APK_FILE="$PWD/app/build/outputs/apk/release/app-release.apk"
test -f "$APK_FILE"
apksigner verify --verbose --print-certs "$APK_FILE"
aapt2 dump badging "$APK_FILE"
aapt2 dump xmltree "$APK_FILE" --file AndroidManifest.xml
sha256sum "$APK_FILE"
stat -c '%s bytes' "$APK_FILE"
```

Продолжать только после успешной сборки и проверки подписи. В APK должны быть пакет `io.github.volmir.personalfinance`, версия `1.0`, код `1`; `debuggable` и `testOnly` должны отсутствовать либо быть `false`. Сертификат должен совпадать с ключом из шага 2, а не с Android Debug.

Отчёты: `app/build/reports/tests/testDebugUnitTest/index.html` и `app/build/reports/lint-results-release.html`. Файл `app-release-unsigned.apk` появляется при сборке без переменных подписи; для GitHub/IzzyOnDroid он не подходит.

Подключить тестовое устройство с USB debugging:

```bash
adb devices
adb install -r "$APK_FILE"
adb shell am start -n io.github.volmir.personalfinance/com.example.MainActivity
```

Проверить четыре вкладки, изменение и сохранение данных после перезапуска, язык, валюту, симулятор и отправку отчёта. Желательно проверить Android 7/API 24 и актуальный Android. Robolectric в проекте использует API 35, отдельный тест форматтеров — API 28; это не замена проверке на устройстве. Если установлена debug-версия с другой подписью, использовать чистое тестовое устройство: удаление старой установки удалит её локальные данные.

## Шаг 4. Добавить четыре секрета GitHub Actions

Команды используют те же ключ и пароли, которыми проверена локальная сборка:

```bash
gh auth status
base64 -w 0 "$KEYSTORE_PATH" | gh secret set KEYSTORE_BASE64 --repo volmir/personal_finance
printf '%s' "$STORE_PASSWORD" | gh secret set STORE_PASSWORD --repo volmir/personal_finance
printf '%s' "$KEY_PASSWORD" | gh secret set KEY_PASSWORD --repo volmir/personal_finance
printf '%s' "$KEY_ALIAS" | gh secret set KEY_ALIAS --repo volmir/personal_finance
gh secret list --repo volmir/personal_finance
unset STORE_PASSWORD KEY_PASSWORD
```

В списке должны быть `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`. Base64 — представление файла, а не шифрование; промежуточный файл в репозитории не нужен. См. [gh secret set](https://cli.github.com/manual/gh_secret_set).

## Шаг 5. Зафиксировать подготовку и отправить тег

Сначала проверить diff и закоммитить только нужные изменения. Если документация уже закоммичена, команды `git add` и `git commit` пропустить:

```bash
cd /var/www/personal_finance
git status --short
git diff --check
git diff
git add -- GITHUB_RELEASE_SETUP.md FDROID_SETUP.md IZZYON_DROID_SETUP.md APP_DESCRIPTION.md
git diff --cached
git commit -m "docs: prepare first release guides and English app description"
git push origin main
```

Перед созданием тега рабочее дерево должно быть чистым, локальный HEAD должен соответствовать отправленной версии. Не добавлять ключи или пароли в Git.

```bash
git status --short
git fetch origin
git rev-parse HEAD origin/main
rg -n 'versionCode|versionName' app/build.gradle.kts
git tag -a v1.0 -m "Personal Finance & FIRE Audit 1.0"
git push origin refs/tags/v1.0
```

Именно **push тега** запускает релизную задачу текущего workflow. Предварительно создавать Release через интерфейс GitHub не требуется; workflow создаёт его сам. События `release` и `workflow_dispatch` в нём не настроены. См. [события GitHub Actions](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows).

## Шаг 6. Дождаться сборки и проверить опубликованный файл

Через несколько секунд после отправки тега:

```bash
RELEASE_RUN_ID=$(gh api \
  'repos/volmir/personal_finance/actions/workflows/build-and-release.yml/runs?event=push&branch=v1.0&per_page=1' \
  --jq '.workflow_runs[0].id // empty')
printf 'Release workflow run: %s\n' "$RELEASE_RUN_ID"
```

Если ID пустой, повторить этот блок чуть позже. Получив ID:

```bash
gh run watch "$RELEASE_RUN_ID" --repo volmir/personal_finance
gh run view "$RELEASE_RUN_ID" --repo volmir/personal_finance --json conclusion --jq '.conclusion'
gh release view v1.0 --repo volmir/personal_finance
mkdir -p /tmp/personal-finance-v1.0
gh release download v1.0 --repo volmir/personal_finance \
  --pattern 'PersonalFinance-v1.0.apk' --dir /tmp/personal-finance-v1.0
apksigner verify --verbose --print-certs /tmp/personal-finance-v1.0/PersonalFinance-v1.0.apk
sha256sum /tmp/personal-finance-v1.0/PersonalFinance-v1.0.apk
```

Ожидается `conclusion: success` и файл **`PersonalFinance-v1.0.apk`** на странице [релиза v1.0](https://github.com/volmir/personal_finance/releases/tag/v1.0). Проверить совпадение отпечатка сертификата с шагом 2 и повторить проверку на устройстве именно скачанным файлом. Совпадение хеша APK локальной и CI-сборки пока не подтверждено; это отдельная проверка воспроизводимости.

Если задача упала только из-за отсутствовавших секретов, после их добавления можно повторить запуск:

```bash
gh run view "$RELEASE_RUN_ID" --repo volmir/personal_finance --log-failed
gh run rerun "$RELEASE_RUN_ID" --repo volmir/personal_finance
```

Если нужно менять исходники, сначала исправить и проверить их, затем выпустить новую версию с увеличенным `versionCode` и новым тегом; повторный запуск старого workflow собирает старый коммит. Опубликованные теги не перемещать и готовые APK под той же версией не заменять.
