# Первая публикация в официальном F-Droid

Проверено 3 ноября 2026 года. Начальная версия приложения: `io.github.volmir.personalfinance`, versionName `1.0`, versionCode `1`, тег `v1.0`.

Перед этой инструкцией выполнить [сборку и GitHub Release](./GITHUB_RELEASE_SETUP.md). Для обычной сборки F-Droid опубликованный APK необязателен, но доступный релиз помогает тестированию и нужен для проверки воспроизводимости с подписью разработчика. Заявка в IzzyOnDroid независима.

## Что мешает считать приложение готовым

1. В исходном `metadata/io.github.volmir.personalfinance.yml` отсутствуют `RepoType` и `Repo`. Поле `SourceCode` их не заменяет: без `RepoType` автоматическая сборка отключена. Ниже дан полный рецепт для форка `fdroiddata`.
2. Тега `v1.0` ещё нет. В инструкции GitHub используется именно он, чтобы согласовать версию и тег.
3. Проект создан 10.10.2026. Текущая политика F-Droid обычно требует минимум три месяца активной разработки; исключения — наличие пользователей, сопровождение автором других приложений в F-Droid либо разработка для мероприятия. На 03.11.2026 основание для исключения не подтверждено. При отсутствии исключения ориентир по сроку — **10.01.2027**, при продолжающейся разработке.
4. Успех сборки через F-Droid CI и воспроизводимость ещё не проверены.

MIT, публичный код, отсутствие INTERNET в исходном манифесте и подготовленный Fastlane — положительные результаты проверки. Зависимости в Gradle относятся к AndroidX, Kotlin, Coroutines и desugaring; окончательная проверка дерева зависимостей и лицензий остаётся частью сборки и ревью каталога. Из отслеживаемых файлов JAR/AAR/SO/APK найден только стандартный `gradle-wrapper.jar`.

Условия возраста и исключений, лицензий и ресурсов: [Inclusion Policy](https://f-droid.org/en/docs/Inclusion_Policy/). Значение полей рецепта: [Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/).

## Шаг 1. Выбрать подпись до первой публикации

| Вариант | Результат |
| --- | --- |
| Обычная сборка F-Droid | F-Droid собирает исходники и подписывает APK отдельным ключом приложения; подпись отличается от GitHub/IzzyOnDroid |
| Воспроизводимая сборка с подписью разработчика | F-Droid проверяет совпадение своей сборки с APK GitHub и распространяет APK с вашей подписью |

Для обновлений между каталогами без переустановки предпочтителен второй вариант. Его работоспособность в проекте пока не доказана. Одинаковый код и успешная компиляция сами по себе не гарантируют побайтового совпадения.

При разных ключах Android обычно не позволяет обновление поверх установленного приложения. Здесь `allowBackup=false`; удаление приложения для смены источника может привести к потере локальных финансовых данных. Подпись F-Droid — не единый «мастер-ключ» для всех приложений.

Описание механизма: [Reproducible Builds](https://f-droid.org/docs/Reproducible_Builds/).

## Шаг 2. Создать форк и ветку fdroiddata

Войти в GitLab, открыть [fdroid/fdroiddata](https://gitlab.com/fdroid/fdroiddata) и нажать **Fork**. Это отдельный репозиторий каталога, не репозиторий приложения. Заменить `YOUR_GITLAB_USERNAME` своим именем GitLab:

```bash
export GITLAB_USER=YOUR_GITLAB_USERNAME
mkdir -p "$HOME/src"
cd "$HOME/src"
git clone --depth 1 "https://gitlab.com/$GITLAB_USER/fdroiddata.git" fdroiddata-personal-finance
cd fdroiddata-personal-finance
git remote add upstream https://gitlab.com/fdroid/fdroiddata.git
git switch -c add-personal-finance
```

## Шаг 3. Создать полный рецепт для опубликованного коммита

Тег должен быть уже отправлен по инструкции GitHub. Выполнить в форке `fdroiddata`:

```bash
RELEASE_COMMIT=$(git -C /var/www/personal_finance rev-parse 'v1.0^{commit}')
printf 'Release commit: %s\n' "$RELEASE_COMMIT"
cat > metadata/io.github.volmir.personalfinance.yml <<EOF
Categories:
  - Money
License: MIT
AuthorName: PersonalFinance
SourceCode: https://github.com/volmir/personal_finance
IssueTracker: https://github.com/volmir/personal_finance/issues
Changelog: https://github.com/volmir/personal_finance/releases

RepoType: git
Repo: https://github.com/volmir/personal_finance.git

Builds:
  - versionName: '1.0'
    versionCode: 1
    commit: $RELEASE_COMMIT
    subdir: app
    gradle:
      - 'yes'

AutoUpdateMode: Version
UpdateCheckMode: Tags
CurrentVersion: '1.0'
CurrentVersionCode: 1
EOF
```

Используется полный SHA коммита, на который указывает тег. Пароли и ключ подписи в рецепт не включать. Описания и изображения берутся из `fastlane/metadata/android` репозитория приложения; копировать переводы в YAML не нужно. Подробности приложения: [APP_DESCRIPTION.md](./APP_DESCRIPTION.md).

Для варианта с подписью разработчика **дополнительно** добавить в этот YAML:

```yaml
Binaries: https://github.com/volmir/personal_finance/releases/download/v%v/PersonalFinance-v%v.apk
AllowedAPKSigningKeys:
  - YOUR_RELEASE_CERTIFICATE_SHA256
```

Заменить `YOUR_RELEASE_CERTIFICATE_SHA256` отпечатком сертификата из `apksigner verify --print-certs`, 64 шестнадцатеричных символа без двоеточий. Это не хеш файла из `sha256sum`. Не добавлять эти поля как обещание воспроизводимости без успешной проверки ниже.

## Шаг 4. Установить инструменты и проверить рецепт

Для локальной сборки подготовить JDK 17 и Android SDK по инструкции GitHub. Создать отдельное Python-окружение; если используемая версия Python не поддерживается актуальным `fdroidserver`, использовать поддерживаемую версию согласно сообщению установщика:

```bash
sudo apt-get install -y python3-venv
python3 -m venv "$HOME/.venvs/fdroidserver"
source "$HOME/.venvs/fdroidserver/bin/activate"
python -m pip install --upgrade pip
python -m pip install fdroidserver apksigcopier
fdroid --version
```

В каталоге форка создать минимальный локальный конфиг; файл не включать в коммит:

```bash
cd "$HOME/src/fdroiddata-personal-finance"
python3 - <<'PY'
from pathlib import Path
import os
import yaml
p = Path('config.yml')
config = yaml.safe_load(p.read_text()) if p.exists() else {}
config = config or {}
config['sdk_path'] = os.environ['ANDROID_HOME']
config['java_paths'] = {'17': os.environ['JAVA_HOME']}
p.write_text(yaml.safe_dump(config, sort_keys=False))
PY
fdroid rewritemeta io.github.volmir.personalfinance
fdroid lint io.github.volmir.personalfinance
unset KEYSTORE_PATH STORE_PASSWORD KEY_PASSWORD KEY_ALIAS
fdroid build --verbose io.github.volmir.personalfinance:1
```

Ожидается `unsigned/io.github.volmir.personalfinance_1.apk`. Локальная сборка зависит от установленных инструментов и не заменяет F-Droid CI. Если линтер или сканер указывает на проблему, исправить причину и повторить проверку; не отключать сканирование всего проекта.

Для проверки воспроизводимости сравнить с APK GitHub:

```bash
mkdir -p /tmp/personal-finance-v1.0
gh release download v1.0 --repo volmir/personal_finance \
  --pattern 'PersonalFinance-v1.0.apk' --dir /tmp/personal-finance-v1.0
apksigcopier compare --unsigned \
  /tmp/personal-finance-v1.0/PersonalFinance-v1.0.apk \
  unsigned/io.github.volmir.personalfinance_1.apk
```

Если файл уже скачан, повторно скачивать его не нужно. Успех сравнения позволяет продолжить проверку варианта с подписью разработчика; окончательное подтверждение — успешная независимая сборка F-Droid. При несовпадении проверить JDK, SDK/Build Tools, R8 и упаковку APK, согласовать рецепт с мейнтейнерами. Либо осознанно выбрать отдельную подпись F-Droid и убрать `Binaries`/`AllowedAPKSigningKeys`.

Официальные справочники: [установка fdroidserver](https://f-droid.org/en/docs/Installing_the_Server_and_Repo_Tools/), [fdroid build](https://f-droid.org/docs/Building_Applications/), [apksigcopier](https://github.com/obfusk/apksigcopier).

## Шаг 5. Отправить ветку и открыть Merge Request

Только когда выполнено условие возраста проекта или есть подтверждённое основание для исключения:

```bash
cd "$HOME/src/fdroiddata-personal-finance"
git diff --check
git add -- metadata/io.github.volmir.personalfinance.yml
git diff --cached
git commit -m "New App: io.github.volmir.personalfinance"
git push -u origin add-personal-finance
```

Открыть на GitLab **Create merge request**: исходная ветка — `add-personal-finance` вашего форка; целевой проект — `fdroid/fdroiddata`, ветка — `master`. Заполнить актуальный шаблон новой программы. Указать:

- исходники и релиз `https://github.com/volmir/personal_finance/releases/tag/v1.0`;
- MIT, приложение для ручного учёта финансов, без сетевого подключения;
- наличие Fastlane и результат тестирования на устройстве;
- выбранную схему подписи и результат проверки воспроизводимости;
- основание для исключения по возрасту, если оно требуется;
- происхождение ресурсов, включая обработку иконки, описанную в [docs/icon-source.md](./docs/icon-source.md).

Веб-форму и переписку с мейнтейнерами выполняет автор. Создание YAML в собственном GitHub-репозитории само по себе не является заявкой в каталог. Подготовка MR описана в [официальной инструкции](https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/).

## Шаг 6. Пройти CI и ревью, дождаться индекса

Проверить pipeline MR, ответить на замечания, при необходимости отправить исправленный рецепт в ту же ветку. Слияние MR и появление приложения в индексе — разные этапы; фиксированного срока включения нет.

После публикации проверить страницу `https://f-droid.org/packages/io.github.volmir.personalfinance/`, версию и подпись скачанного APK. Для следующих релизов повышать `versionCode`, менять `versionName`, добавлять `changelogs/<versionCode>.txt`, создавать соответствующий тег. Автообновление рецепта не гарантирует успешную сборку или немедленную публикацию каждой версии.
