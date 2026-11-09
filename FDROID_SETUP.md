# Публикация Personal Finance & FIRE Audit в F-Droid

Актуализировано 9 ноября 2026 года. Команды рассчитаны на Bash в Ubuntu. Приложение уже распространяется через [GitHub Releases](https://github.com/volmir/personal_finance/releases).

Опубликован [v1.0.1](https://github.com/volmir/personal_finance/releases/tag/v1.0.1): `versionName = "1.0.1"`, `versionCode = 2`, релизный коммит `b94949e09844450a126a16a8f73f50edd87cd64a`. Сумма, сертификат, версия и флаги APK проверены; опубликованные теги не перемещаются. Результаты: [проверки 1.0.1](docs/release-readiness-1.0.1.md).

Для текущего `versionCode = 2` заметки выпуска называются `fastlane/metadata/android/<locale>/changelogs/2.txt`, **не** `1.0.1.txt`. По правилам F-Droid changelog необязателен для сборки; если нужен один общий текст, достаточно `en-US/changelogs/2.txt` — английский служит запасной локалью. Для этого приложения предпочтительны все шесть локалей (`en-US`, `ru-RU`, `de-DE`, `fr-FR`, `it-IT`, `es-ES`), чтобы пользователи видели перевод. Каждый файл — не более **500 символов**. `1.txt` относится к `versionCode = 1` и не заменяет заметки версии 2. Метаданные и новые скриншоты должны войти в релизный коммит до создания тега. [Официальный формат и правила F-Droid](https://f-droid.org/en/docs/All_About_Descriptions_Graphics_and_Screenshots/).

Заметки `2.txt` опубликованы во всех шести локалях. Рецепт F-Droid сохраняет сборку `v1.0` и добавляет `v1.0.1` с полным SHA релизного коммита; CurrentVersion / CurrentVersionCode — `1.0.1 / 2`. Локальные lint, сборка и проверка воспроизводимости версии 2 успешны. По отдельному запросу автора 09.11.2026 досрочно подан [MR !56526](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/56526). Это ещё не публикация в каталоге: трёхмесячный срок не достигнут, исключение не подтверждено, требуется решение мейнтейнеров. После верификации аккаунта CI запустился; выявленное несоответствие каноническому формату YAML исправлено коммитом `dac96bf91` в ветке MR. [Pipeline 3045667325](https://gitlab.com/volmir561/fdroiddata/-/pipelines/3045667325) прошёл все 9 заданий, включая сборку, lint и анализ APK. Решение о включении и досрочной подаче остаётся за мейнтейнерами.

## Архив: подготовка на 06.11.2026

- Рабочий снимок с исправлениями успешно прошёл локальную сборку F-Droid с compileSdk **36.1**: сканирование исходников, `assembleRelease` и R8. Это проверка официального контейнерного образа, не запуск на производственных серверах каталога.
- `lintRelease` для проверенного снимка: **0 ошибок, 38 предупреждений**. Это не подтверждение отсутствия всех замечаний Lint.
- Release APK с временной тестовой подписью проверен на AOSP x86_64 эмуляторах **API 24 и 36**: установка, холодный запуск, четыре вкладки, возврат из фона; падений приложения не выявлено. Релизный ключ не использовался.
- Обновлены **24 скриншота 1080×2400** через Robolectric API 35 и подготовлены `changelogs/2.txt` на шести языках. Полный JVM/Robolectric-прогон перед этими проверками: **196 успешно, 1 пропуск**.
- Новый подписанный релиз `v1.0.1`, сравнение его APK на воспроизводимость и официальный pipeline F-Droid пока не выполнялись. Сведения о воспроизводимости `v1.0` ниже являются историческими и не распространяются автоматически на новую версию.

Точный снимок исходников, окружение, SHA-256 APK и пределы проверок приведены в [отчёте](docs/release-readiness-1.0.1.md). Последующие редакции документации и строк не превращают этот снимок в релизный коммит; перед публикацией проверяется окончательное содержимое тега.

## Архив: состояние, зафиксированное 04.11.2026

| Пункт | Результат |
| --- | --- |
| Публичный релиз | [v1.0](https://github.com/volmir/personal_finance/releases/tag/v1.0), опубликован 03.11.2026 |
| Приложение | `io.github.volmir.personalfinance`, versionName `1.0`, versionCode `1` |
| Релизный коммит | `d2c01ccb94d4340e799735002b7a0e4f92f60a37` |
| APK | `PersonalFinance-v1.0.apk`, 1 855 743 байта; подпись и контрольная сумма проверены после скачивания |
| Проверки GitHub Actions | JVM/Robolectric-тесты, Android Lint и проверка подписи [успешны](https://github.com/volmir/personal_finance/actions/runs/41631768887) |
| Проверки F-Droid | Для `v1.0` от 03.11.2026 `fdroid lint` и `fdroid build` успешны; сканер исходников не выявил блокирующих проблем. После восстановления окружения линтер повторно пройден |
| Воспроизводимость | Для `v1.0` от 03.11.2026 `apksigcopier` успешно сравнил локальную сборку F-Droid с подписанным APK GitHub; для будущего релиза проверку повторить |
| Рецепт | [metadata/io.github.volmir.personalfinance.yml](./metadata/io.github.volmir.personalfinance.yml), с полным SHA коммита, `RepoType`, `Repo`, `Binaries` и `AllowedAPKSigningKeys` |
| Подготовленная ветка | Восстановлена `add-personal-finance` в `$HOME/src/fdroiddata-personal-finance`; локальный коммит содержит только рецепт приложения, push не выполнялся |
| Проверка на устройстве | По сообщению автора, выявлены баги; нужны исправления и повторная проверка нового релиза |
| Текущий код | Синхронизирован с `origin/main`: `2f6b01d`, исправление склонения количества лет в FIRE. [CI этого коммита](https://github.com/volmir/personal_finance/actions/runs/41796061495) успешен; новый релиз ещё не выпущен |
| Происхождение иконки | Автор подтвердил генерацию Google AI; C2PA-подпись, хеш и цепочка к официальному корневому сертификату Google проверены. [Отчёт и пределы проверки](./docs/icon-source.md) сохранены |
| Заявка в официальный каталог | Не подана; ещё нужны исправленный релиз, основание по сроку разработки, CI и ревью F-Droid |

MIT, публичные исходники, Fastlane на шести языках и отсутствие разрешения `INTERNET` уже проверены. Факт успешной локальной сборки не означает принятие приложения в каталог.

### Оценка готовности на 04.11.2026

При первоначальной оценке шаги публикации не выполнялись. Затем по поручению автора восстановлено локальное окружение, как описано ниже; повторная сборка приложения, скачивание и сравнение APK, создание форка, push и Merge Request не выполнялись.

- Через GitHub API подтверждены существование релиза и трёх файлов (`APK`, `SHA256SUMS.txt`, `SIGNING_CERTIFICATE.txt`), а также успешный CI релизного коммита: тесты, `lintRelease`, сборка, проверка подписи и отсутствие флагов `debuggable`/`testOnly`. После первоначальной оценки получен коммит `2f6b01d` с исправлением склонения количества лет в FIRE; его CI успешен. Эти исправления ещё не входят в опубликованный APK `v1.0`.
- В сохранённых локальных отчётах — 144 теста, 0 ошибок и падений, 1 пропуск; Android Lint содержит предупреждения, но не ошибки. Это существующие результаты, а не новый прогон.
- В Fastlane есть описания и changelog на шести языках, по четыре скриншота 1080×2400 на язык и иконки 512×512 для английского и русского. Убраны конечные точки из кратких описаний согласно [Quick Start Guide](https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/). Эта правка не меняет содержимое уже опубликованного тега; её следует включить в следующий релиз, не перемещая `v1.0`.
- Docker работает, проверенный образ контейнера доступен локально; Git, GitHub CLI с авторизацией, Python 3 и `apksigner` доступны. Рабочие копии `fdroiddata`, `fdroidserver` и `apksigcopier` восстановлены в `$HOME/src`. `fdroid` запускается из контейнера; повторный `fdroid lint io.github.volmir.personalfinance` завершился успешно.
- Установлен GitLab CLI `glab 1.128.0` в `$HOME/.local/bin/glab` с проверкой SHA-256 архива официального релиза. Авторизация GitLab завершена для `@volmir561` через браузерный OAuth (`--web`); учётные данные хранятся в системной связке ключей. Подтверждены `glab auth status`, запрос API текущего пользователя и получение учётных данных Git через helper `glab` для HTTPS. Device flow завершался по таймауту, поэтому для повторного входа использовать `glab auth login --hostname gitlab.com --web --git-protocol https --container-registry-domains gitlab.com,gitlab.com:443,registry.gitlab.com --setup-git-credential-helper`. Авторизация GitHub и GitLab независима.
- Основание для исключения из трёхмесячного срока не подтверждено. Автор сообщил, что проверка на физическом устройстве выявила баги; сценарии, устройство и версия Android пока не описаны. После исправлений нужна повторная проверка APK нового релиза. Проверки Robolectric не заменяют испытание на устройстве.
- Автор подтвердил генерацию исходной иконки через Google AI. В исходном JPG проверены C2PA-подпись и хеш изображения, цепочка сертификатов к корню Google с официального `pki.goog` и метка времени. Происхождение установлено, результат `Trusted` сохранён в [отчёте](./docs/icon-c2pa-report.json). Конкретный сервис и референсы пока не уточнены; проверка происхождения не гарантирует отсутствие прав третьих лиц. Подробности — в [docs/icon-source.md](./docs/icon-source.md).

Техническая основа готова, но вся предварительная работа ещё не завершена. Инструменты восстановлены, авторизация GitLab настроена. Форк, push и Merge Request пока не создавались. Основание по сроку разработки, фактические сведения о правах на исходную иконку и результаты проверки на физическом устройстве нельзя заменить автоматически составленным текстом. Принятие заявки, официальную сборку и публикацию выполняет F-Droid.

### Оставшиеся действия перед публикацией

Проверить опубликованный APK `1.0.1` / `2` на доступных устройствах; релиз и его подпись уже проверены. Включить актуальные метаданные и changelog, сохранить прежний ключ подписи. Происхождение иконки уже описано; по возможности дополнить конкретный сервис и сведения о референсах. Перед подачей подтвердить три месяца активной разработки либо обоснованное исключение по политике F-Droid.

| Этап | Что осталось |
| --- | --- |
| 1. Форк | Создать `volmir561/fdroiddata`, добавить его как `origin` в восстановленной копии; официальный remote `upstream` сохранить |
| 2. Рецепт | Обновить `Builds`, SHA релизного коммита, `CurrentVersion` и `CurrentVersionCode` на новый релиз, перенести актуальный YAML в ветку `fdroiddata` |
| 3. Сборка | Для новой версии повторить форматирование, линтер, сканирование и сборку F-Droid |
| 4. Сравнение APK | Проверить сумму и сертификат нового GitHub APK, сравнить с APK сборки F-Droid; приложить результат повторных испытаний на устройстве |
| 5. Описание заявки | Добавить ссылки на актуальный релиз и проверки, сведения о происхождении иконки, сохранении подписи и основании для подачи |
| 6. MR | Отправить только рецепт в свой форк, получить успешный CI, открыть MR в `fdroid/fdroiddata` и устранить замечания ревьюеров |

После принятия MR официальная сборка, проверка воспроизводимости и включение в индекс выполняются F-Droid. Локальные проверки и отправленный MR не означают завершённую публикацию.

### Восстановленное локальное окружение

Постоянные рабочие каталоги:

- `fdroiddata`: `$HOME/src/fdroiddata-personal-finance`, ветка `add-personal-finance`. Remote `upstream` указывает на официальный репозиторий; remote своего форка пока не добавлен.
- `fdroidserver`: `$HOME/src/fdroidserver`, коммит `cdfdce8`.
- `apksigcopier`: `$HOME/src/apksigcopier`, коммит `3546ac6`.
- Кеш: `$HOME/.cache/personal-finance-fdroid`; постоянный Android SDK: `android-sdk` внутри него. Установлены платформа `android-36.1`, Build Tools `36.0.0` и Platform Tools с `adb`.
- Переменные путей и имя GitLab `GITLAB_USER=volmir561`: `source "$HOME/src/personal-finance-fdroid.env"`. Файл находится вне репозитория и не содержит токенов или паролей.

Локальный `config.yml` задаёт `/opt/android-sdk` и OpenJDK 21; исходный конфиг сохранён в `config.yml.before-local-build`. Изменённый конфиг, резервная копия и кеши не включены в коммит. Постоянный SDK при запуске контейнера монтируется в `/opt/android-sdk`; его нужно сохранять вместе с кешем. В новом контейнере проверены SDK, запуск `apksigner` и `adb`, импорт обоих Python-инструментов и успешный линтер рецепта. Прежние APK и результаты сравнения не восстановлены: для исправленной версии сборка и проверка будут выполнены заново.

## Когда подавать заявку

По разделу **Quality Control** [Inclusion Policy](https://f-droid.org/en/docs/Inclusion_Policy/) приложение должно активно разрабатываться не менее трёх месяцев, кроме трёх случаев:

1. У приложения доказанно есть пользователи.
2. Автор уже поддерживает другие приложения в F-Droid.
3. Приложение разработано только для одного мероприятия.

Правило относится к разработке приложения, а не к возрасту репозитория GitHub. Исходя из подтверждённой публичной истории текущей Android-реализации с **10.10.2026**, ориентир без обоснованного исключения — **не раньше 10.01.2027**, при продолжающейся активной разработке. Подтверждения более ранней работы над приложением также можно представить мейнтейнерам.

Со слов автора, финансовая модель развивалась в Google Таблицах несколько лет. В декабре 2025 года была неудачная попытка Android-реализации на React Native; её исходники не сохранились. Эти сведения можно честно привести как предысторию, но они сами по себе не подтверждают три месяца активной разработки нынешнего приложения. Политика не обещает автоматического зачёта работы над таблицами или такой попытки прототипирования.

Для заявки подходит следующая формулировка, если она соответствует фактической истории:

> The financial model behind this application was developed and refined in Google Sheets over several years. In December 2025, I attempted an earlier Android implementation using React Native. That attempt was unsuccessful, and its source code was not retained. The current native Android implementation has a public development history starting on October 10, 2026. The first signed Android release was published on November 3, 2026.

Для досрочной подачи наиболее прямое основание — реальные пользователи Android-приложения. Можно приложить публичные отзывы, сообщения об использовании и отчёты об ошибках от пользователей. Наличие APK и тестовые скачивания сами по себе не доказывают наличие пользователей. Применимость исключения оценивают мейнтейнеры.

Пока условия подачи не подтверждены, распространять APK через GitHub Releases, собирать обратную связь и поддерживать рецепт. Политика F-Droid также допускает собственный или сторонний репозиторий; это отдельный канал распространения, не включение в основной каталог.

## Подпись и результаты проверки воспроизводимости

В рецепте уже настроено сохранение подписи разработчика: `Binaries` указывает на APK GitHub, а `AllowedAPKSigningKeys` закрепляет SHA-256 сертификата:

```text
D6:7A:62:A2:33:C2:66:62:26:99:25:95:5C:65:34:64:82:0B:F1:21:E1:55:CA:82:73:59:C3:B9:9D:9B:66:09
```

В YAML он записан без двоеточий. Это отпечаток сертификата, а не контрольная сумма APK из `SHA256SUMS.txt`. Закрытый ключ и пароли для сборки F-Droid не передаются: используется публичный APK для сравнения. Существующий ключ подписи сохраняется для следующих релизов.

Проверка `v1.0` выполнена 03.11.2026:

- `fdroidserver`: коммит `cdfdce8`.
- Официальный контейнер: `registry.gitlab.com/fdroid/fdroidserver:buildserver`, digest `sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac`.
- Сборка F-Droid: OpenJDK 21, Gradle 9.3.1, Android SDK 36.1 и Build Tools 36.0.0. Сборка GitHub использовала JDK 17.
- `apksigcopier`: коммит `3546ac6`; сравнение подписанного GitHub APK с `unsigned/io.github.volmir.personalfinance_1.apk` успешно.

Результат относится к этой версии и двум проверенным окружениям. Официальный F-Droid должен повторить сборку и проверку у себя. Для следующих версий воспроизводимость проверяется заново. Описание механизма: [Reproducible Builds](https://f-droid.org/docs/Reproducible_Builds/).

Если F-Droid будет собирать с отдельной подписью, обновление поверх установленного GitHub APK обычно невозможно. У приложения `allowBackup=false`; удаление установки может привести к потере локальных данных. Текущий рецепт подготовлен для общей подписи.

## Шаг 1. Подготовить форк fdroiddata

Нужна учётная запись GitLab. Открыть [fdroid/fdroiddata](https://gitlab.com/fdroid/fdroiddata) и нажать **Fork**. Создание форка и локальная подготовка допустимы заранее; Merge Request открывается после выполнения условий подачи.

Прежняя временная копия `/tmp/personal-finance-fdroiddata` отсутствует. Вместо неё уже подготовлена постоянная копия `$HOME/src/fdroiddata-personal-finance` с локальной веткой и рецептом; повторять клонирование и создание ветки для неё не нужно. GitLab уже авторизован для `@volmir561`; при выполнении этого шага следует создать свой форк и добавить его как `origin`, сохранив `upstream`. Постоянным источником рецепта служит файл в репозитории приложения. Только для новой копии выполнить, заменив `YOUR_GITLAB_USERNAME` своим именем:

```bash
export GITLAB_USER=YOUR_GITLAB_USERNAME
export PF_FDROID_DATA="$HOME/src/fdroiddata-personal-finance"
mkdir -p "$HOME/src"
git clone "https://gitlab.com/$GITLAB_USER/fdroiddata.git" "$PF_FDROID_DATA"
cd "$PF_FDROID_DATA"
git remote add upstream https://gitlab.com/fdroid/fdroiddata.git
git fetch upstream
git switch -c add-personal-finance upstream/master
```

Если рабочая копия и ветка уже есть, использовать их; повторно клонировать и создавать одноимённую ветку не нужно. Переменную `PF_FDROID_DATA` установить в путь фактической копии. Команды далее выполнять в одном Bash-сеансе.

## Шаг 2. Перенести готовый рецепт

Полный актуальный YAML хранится в [metadata/io.github.volmir.personalfinance.yml](./metadata/io.github.volmir.personalfinance.yml). Копировать его целиком, сохраняя единый источник рецепта:

```bash
cd "$PF_FDROID_DATA"
mkdir -p metadata
cp /var/www/personal_finance/metadata/io.github.volmir.personalfinance.yml \
  metadata/io.github.volmir.personalfinance.yml
git diff -- metadata/io.github.volmir.personalfinance.yml
```

В текущем рецепте сохранена версия `1.0` / `1` и добавлена `1.0.1` / `2` с полным SHA коммита тега `v1.0.1`; сертификат разработчика сохранён. Категория `Finance Manager` проверена по актуальному [списку fdroiddata](https://gitlab.com/fdroid/fdroiddata/-/blob/master/config/categories.yml); прежняя `Money` не проходила линтер.

Описания, иконки, скриншоты и changelog берутся из `fastlane/metadata/android` репозитория приложения. Они уже включены в релизный коммит. Полное описание функций: [APP_DESCRIPTION.md](./APP_DESCRIPTION.md). Поля рецепта объяснены в [Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/).

Зафиксировать рецепт локально, добавляя только файл приложения:

```bash
git add -- metadata/io.github.volmir.personalfinance.yml
git diff --cached
git commit -m "New App: io.github.volmir.personalfinance"
```

Если рецепт уже закоммичен и не менялся, коммит пропустить. Запись в истории нужна и для корректной даты `SOURCE_DATE_EPOCH`: у нового незакоммиченного рецепта она может оказаться пустой и вызвать ошибку запуска Gradle. На этом этапе ветка остаётся локальной.

## Шаг 3. Проверить рецепт и собрать APK

Для повторения проверенного способа нужны работающий Docker и Git на хосте; локальный Android SDK не требуется. Инструменты и SDK находятся в контейнере. Для сравнения APK далее нужны также GitHub CLI, Python 3 и `apksigner`.

Подготовить исходники инструментов и отдельный кеш. Если каталог `PF_FDROID_SERVER` уже существует, использовать его вместо повторного клонирования:

```bash
export PF_FDROID_SERVER="$HOME/src/fdroidserver"
export PF_FDROID_CACHE="$HOME/.cache/personal-finance-fdroid"
export PF_FDROID_SDK="$PF_FDROID_CACHE/android-sdk"
git clone https://gitlab.com/fdroid/fdroidserver.git "$PF_FDROID_SERVER"
git -C "$PF_FDROID_SERVER" checkout cdfdce8
mkdir -p "$PF_FDROID_CACHE" "$PF_FDROID_SDK"
```

В отдельной рабочей копии `fdroiddata` сохранить текущий конфиг и задать локальные пути контейнера. Конфиг и кеши не включать в коммит:

```bash
cd "$PF_FDROID_DATA"
if [ -f config.yml ] && [ ! -f config.yml.before-local-build ]; then
  cp config.yml config.yml.before-local-build
fi
cat > config.yml <<'LOCAL_CONFIG'
sdk_path: /opt/android-sdk
java_paths:
  21: /usr/lib/jvm/java-21-openjdk-amd64
LOCAL_CONFIG
```

При проверке в отдельном каталоге нужен полный официальный каталог `config` из fdroiddata, включая `categories.yml` и указанные в нём иконки. Пустой список категорий вызывает ложную ошибку `Finance Manager is not valid`; копирование только YAML затем приводит к ошибке отсутствующей иконки. Категорию приложения ради такого окружения менять не нужно. Если `PF_FDROID_CHECK` указывает на изолированный каталог проверки:

```bash
mkdir -p "$PF_FDROID_CHECK"
cp -a "$PF_FDROID_DATA/config" "$PF_FDROID_CHECK/"
```

В проверке 09.11.2026 использован официальный каталог из ревизии fdroiddata `5938c5fbc6800dc13c0253190b9a14c8343d668c`; с ним `fdroid lint` прошёл.

Запустить форматирование, линтер и сборку в контейнере. Пользователь `vagrant` внутри контейнера должен иметь право записи в смонтированные рабочую копию и кеш:

```bash
unset KEYSTORE_PATH STORE_PASSWORD KEY_PASSWORD KEY_ALIAS
docker run --rm --user vagrant \
  --env GRADLE_USER_HOME=/build/.gradle-user-home \
  --entrypoint /bin/bash \
  -v "$PF_FDROID_DATA:/build" \
  -v "$PF_FDROID_SERVER:/home/vagrant/fdroidserver:ro" \
  -v "$PF_FDROID_CACHE:/home/vagrant/.cache" \
  -v "$PF_FDROID_SDK:/opt/android-sdk" \
  registry.gitlab.com/fdroid/fdroidserver@sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac \
  -lc 'export PATH="/home/vagrant/fdroidserver:$PATH" PYTHONPATH=/home/vagrant/fdroidserver
       cd /build
       fdroid rewritemeta io.github.volmir.personalfinance &&
       fdroid lint io.github.volmir.personalfinance &&
       fdroid build --verbose io.github.volmir.personalfinance:2'
```

Ожидается `unsigned/io.github.volmir.personalfinance_2.apk`. При первом запуске могут загружаться Gradle, SDK и зависимости. Если такой APK уже есть, F-Droid может пропустить повторную сборку; для проверки изменённой версии использовать новый `versionCode` и соответствующий рецепт. Локальный успех не заменяет CI каталога.

Официальные справочники: [серверные инструменты](https://f-droid.org/en/docs/Installing_the_Server_and_Repo_Tools/), [fdroid build](https://f-droid.org/docs/Building_Applications/).

## Шаг 4. Сравнить с подписанным APK GitHub

Использовать новый каталог скачивания, чтобы не перепутать версии:

```bash
export PF_APK_DOWNLOAD=$(mktemp -d)
gh release download v1.0.1 --repo volmir/personal_finance \
  --dir "$PF_APK_DOWNLOAD"
(cd "$PF_APK_DOWNLOAD" && sha256sum -c SHA256SUMS.txt)
apksigner verify --verbose --print-certs "$PF_APK_DOWNLOAD/PersonalFinance-v1.0.1.apk"
```

Отпечаток должен совпасть с указанным выше. Проверить пакет, версию и поведение приложения на тестовом Android-устройстве. По сообщению автора, испытание выявило баги; после исправлений этот шаг следует выполнять с новым релизом. Зафиксировать модель устройства, версию Android, версию приложения, проверенные сценарии и результат повторной проверки.

Для сравнения можно использовать исходники [apksigcopier](https://github.com/obfusk/apksigcopier); этот способ уже успешно выполнен в текущем окружении. Если каталог инструмента существует, пропустить его клонирование:

```bash
export PF_APKSIGCOPIER="$HOME/src/apksigcopier"
git clone https://github.com/obfusk/apksigcopier.git "$PF_APKSIGCOPIER"
git -C "$PF_APKSIGCOPIER" checkout 3546ac6
PYTHONPATH="$PF_APKSIGCOPIER" python3 - <<'PY'
import os
from pathlib import Path
import apksigcopier
signed = Path(os.environ['PF_APK_DOWNLOAD']) / 'PersonalFinance-v1.0.1.apk'
unsigned = Path(os.environ['PF_FDROID_DATA']) / 'unsigned/io.github.volmir.personalfinance_2.apk'
apksigcopier.do_compare(str(signed), str(unsigned), unsigned=True)
print('Reproducible APK comparison passed')
PY
```

Успешное завершение подтверждает сравнение двух конкретных APK. При несовпадении проверить версии JDK, Build Tools, Gradle, исходный коммит и упаковку; рецепт с `Binaries` должен проходить проверку воспроизводимости до публикации.

После форматирования проверить diff рецепта и при наличии изменений закоммитить только этот YAML. Локальные `config.yml`, резервную копию и кеши в коммит не добавлять.

## Шаг 5. Подготовить описание заявки

Полезность приложения и его отличия уже описаны ниже. Перед подачей повторно сверить функции и ссылки с актуальным релизом; не заявлять отсутствие аналогов во всём каталоге.

> Personal Finance & FIRE Audit combines a manual household budget and balance sheet with nine financial metrics, an explanatory audit, and a FIRE retirement projection. The projection uses the present value of expenses through a user-selected life-expectancy horizon, with explicit real-return and capital-gains-tax assumptions. Fourteen what-if scenarios let users explore changes without overwriting their original inputs. The app requires no account or bank connection and does not request internet access.

Сопоставление основных сценариев по опубликованным описаниям на 03.11.2026:

| Приложение | Основной сценарий | Фокус Personal Finance & FIRE Audit |
| --- | --- | --- |
| [LoanPro](https://f-droid.org/en/packages/net.kaltner.LoanPro/) | Расчёт суммы кредита, платежа, срока и ставки | Оценка совокупного финансового положения и времени до FIRE |
| [My Expenses](https://f-droid.org/en/packages/org.totschnig.myexpenses/) | Учёт транзакций, бюджетов, счетов и инвестиционных портфелей | Агрегированный финансовый снимок, объясняющий аудит и сценарный прогноз без ввода истории транзакций |

Это сопоставление основных сценариев из описаний, а не исчерпывающий аудит аналогов. Собственная ценность приложения — сочетание финансового снимка, объяснений и сценарного FIRE-прогноза с прозрачными допущениями; достаточность отличий оценивает ревьюер.

В заявку включить:

- исходники, лицензию MIT и ссылку на [релиз v1.0.1](https://github.com/volmir/personal_finance/releases/tag/v1.0.1);
- описание пользы и отличий приложения;
- результаты проверки на устройстве, сборки и воспроизводимости;
- сведения о сохранении подписи разработчика;
- подтверждение срока разработки либо конкретного исключения; предысторию таблиц и прототипа обозначить отдельно;
- происхождение ресурсов, включая обработку иконки из [docs/icon-source.md](./docs/icon-source.md).

## Шаг 6. Отправить ветку и открыть Merge Request

Выполнять после подтверждения условия срока разработки или исключения. В рабочей копии своего форка проверить, что коммиты содержат только нужный рецепт:

```bash
cd "$PF_FDROID_DATA"
git remote -v
git diff --check
git diff upstream/master...HEAD -- metadata/io.github.volmir.personalfinance.yml
git log --oneline upstream/master..HEAD
git push -u origin add-personal-finance
```

Команда предполагает новую копию своего форка из шага 1. В прежней временной копии `/tmp/personal-finance-fdroiddata` адрес `origin` указывал на официальный репозиторий. Если эта копия восстановлена с прежними remote, отправлять туда этой командой нельзя. Для использования такой ветки сначала добавить свой форк как отдельный remote и отправить ветку в него:

```bash
git -C /tmp/personal-finance-fdroiddata remote add myfork \
  "https://gitlab.com/$GITLAB_USER/fdroiddata.git"
git -C /tmp/personal-finance-fdroiddata push -u myfork add-personal-finance
```

Перед отправкой также убедиться, что изменения после последнего локального коммита включены в рецепт и повторно проверены. Отправка ветки в собственный форк сама по себе не подаёт заявку.

Перед открытием MR дождаться успешного pipeline ветки в своём форке; при ошибках исправить рецепт и повторно проверить CI. Такой порядок указан в [CONTRIBUTING.md fdroiddata](https://gitlab.com/fdroid/fdroiddata/-/blob/master/CONTRIBUTING.md). Если pipeline недоступен из-за настроек или ограничений учётной записи, сначала выяснить причину; локальный линтер не подтверждает успех CI.

После успешного pipeline в GitLab открыть **Create merge request**:

- исходная ветка: `add-personal-finance` вашего форка;
- целевой проект: `fdroid/fdroiddata`, ветка `master`;
- заголовок: `New App: io.github.volmir.personalfinance`;
- описание: актуальный шаблон новой программы и сведения из шага 5.

Проверить pipeline, отвечать на замечания мейнтейнеров и отправлять исправления в ту же ветку. Слияние MR, официальная сборка и появление в индексе — разные этапы; фиксированный срок включения не гарантирован. Порядок подачи описан в [официальном руководстве](https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/).

## Дальнейшие обновления

Опубликован `v1.0.1`: `versionName = "1.0.1"`, `versionCode = 2`. GitHub Actions подписал `PersonalFinance-v1.0.1.apk` прежним ключом; артефакты проверены. Рецепты сохраняют сборку `v1.0` и добавляют версию 2; основной рецепт закреплён на полный SHA тега `v1.0.1`. Результаты локальной проверки F-Droid приведены в отчёте. Следующие релизы требуют отдельного запроса автора. Опубликованные APK и теги не заменять.

Перед будущей заявкой или обновлением использовать актуальный проверенный релиз: увеличить `versionCode`, изменить `versionName`, добавить changelog с новым кодом, опубликовать соответствующий тег и подписанный APK. В рецепте проверить `Builds`, полный SHA коммита, `CurrentVersion` и `CurrentVersionCode`, затем повторить сборку и сравнение APK. Общий ключ подписи сохраняется.

Автообновление рецепта по тегам не гарантирует успешную сборку каждой версии. После включения проверять страницу `https://f-droid.org/packages/io.github.volmir.personalfinance/`, версию и сертификат распространяемого APK.
