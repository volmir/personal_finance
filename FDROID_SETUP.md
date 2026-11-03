# Публикация Personal Finance & FIRE Audit в F-Droid

Актуализировано 3 ноября 2026 года. Команды рассчитаны на Bash в Ubuntu. Приложение уже распространяется через [GitHub Releases](https://github.com/volmir/personal_finance/releases); для отслеживания обновлений в Obtainium пользователь добавляет `https://github.com/volmir/personal_finance`.

## Текущее состояние

| Пункт | Результат |
| --- | --- |
| Публичный релиз | [v1.0](https://github.com/volmir/personal_finance/releases/tag/v1.0), опубликован 03.11.2026 |
| Приложение | `io.github.volmir.personalfinance`, versionName `1.0`, versionCode `1` |
| Релизный коммит | `d2c01ccb94d4340e799735002b7a0e4f92f60a37` |
| APK | `PersonalFinance-v1.0.apk`, 1 855 743 байта; подпись и контрольная сумма проверены после скачивания |
| Проверки GitHub Actions | JVM/Robolectric-тесты, Android Lint и проверка подписи [успешны](https://github.com/volmir/personal_finance/actions/runs/41631768887) |
| Проверки F-Droid | `fdroid lint` и `fdroid build` успешны; сканер исходников не выявил блокирующих проблем |
| Воспроизводимость | `apksigcopier` успешно сравнил локальную сборку F-Droid с подписанным APK GitHub |
| Рецепт | [metadata/io.github.volmir.personalfinance.yml](./metadata/io.github.volmir.personalfinance.yml), с полным SHA коммита, `RepoType`, `Repo`, `Binaries` и `AllowedAPKSigningKeys` |
| Подготовленная ветка | `add-personal-finance` в `/tmp/personal-finance-fdroiddata`; в GitLab не отправлялась |
| Заявка в официальный каталог | Не подана; ещё нужны основание по сроку разработки, проверка на устройстве, CI и ревью F-Droid |

MIT, публичные исходники, Fastlane на шести языках и отсутствие разрешения `INTERNET` уже проверены. Факт успешной локальной сборки не означает принятие приложения в каталог.

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

Пока условия подачи не подтверждены, распространять APK через GitHub Releases и Obtainium, собирать обратную связь и поддерживать рецепт. Политика F-Droid также допускает собственный или сторонний репозиторий; это отдельный канал распространения, не включение в основной каталог.

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

Подготовленная временная копия уже есть в `/tmp/personal-finance-fdroiddata`. Каталог `/tmp` может быть очищен, поэтому постоянным источником рецепта служит файл в репозитории приложения. Для новой копии выполнить, заменив `YOUR_GITLAB_USERNAME` своим именем:

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

В текущем рецепте закреплены версия `1.0` / `1`, коммит тега `v1.0` и сертификат разработчика. Категория `Finance Manager` проверена по актуальному [списку fdroiddata](https://gitlab.com/fdroid/fdroiddata/-/blob/master/config/categories.yml); прежняя `Money` не проходила линтер.

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
git clone https://gitlab.com/fdroid/fdroidserver.git "$PF_FDROID_SERVER"
git -C "$PF_FDROID_SERVER" checkout cdfdce8
mkdir -p "$PF_FDROID_CACHE"
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

Запустить форматирование, линтер и сборку в контейнере. Пользователь `vagrant` внутри контейнера должен иметь право записи в смонтированные рабочую копию и кеш:

```bash
unset KEYSTORE_PATH STORE_PASSWORD KEY_PASSWORD KEY_ALIAS
docker run --rm --user vagrant \
  --env GRADLE_USER_HOME=/build/.gradle-user-home \
  --entrypoint /bin/bash \
  -v "$PF_FDROID_DATA:/build" \
  -v "$PF_FDROID_SERVER:/home/vagrant/fdroidserver:ro" \
  -v "$PF_FDROID_CACHE:/home/vagrant/.cache" \
  registry.gitlab.com/fdroid/fdroidserver@sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac \
  -lc 'export PATH="/home/vagrant/fdroidserver:$PATH" PYTHONPATH=/home/vagrant/fdroidserver
       cd /build
       fdroid rewritemeta io.github.volmir.personalfinance &&
       fdroid lint io.github.volmir.personalfinance &&
       fdroid build --verbose io.github.volmir.personalfinance:1'
```

Ожидается `unsigned/io.github.volmir.personalfinance_1.apk`. При первом запуске могут загружаться Gradle, SDK и зависимости. Если такой APK уже есть, F-Droid может пропустить повторную сборку; для проверки изменённой версии использовать новый `versionCode` и соответствующий рецепт. Локальный успех не заменяет CI каталога.

Официальные справочники: [серверные инструменты](https://f-droid.org/en/docs/Installing_the_Server_and_Repo_Tools/), [fdroid build](https://f-droid.org/docs/Building_Applications/).

## Шаг 4. Сравнить с подписанным APK GitHub

Использовать новый каталог скачивания, чтобы не перепутать версии:

```bash
export PF_APK_DOWNLOAD=$(mktemp -d)
gh release download v1.0 --repo volmir/personal_finance \
  --dir "$PF_APK_DOWNLOAD"
(cd "$PF_APK_DOWNLOAD" && sha256sum -c SHA256SUMS.txt)
apksigner verify --verbose --print-certs "$PF_APK_DOWNLOAD/PersonalFinance-v1.0.apk"
```

Отпечаток должен совпасть с указанным выше. Проверить пакет, версию и поведение приложения на тестовом Android-устройстве. Подтверждение проверки на физическом устройстве в текущем аудите не зафиксировано.

Для сравнения можно использовать исходники [apksigcopier](https://github.com/obfusk/apksigcopier); этот способ уже успешно выполнен в текущем окружении. Если каталог инструмента существует, пропустить его клонирование:

```bash
export PF_APKSIGCOPIER="$HOME/src/apksigcopier"
git clone https://github.com/obfusk/apksigcopier.git "$PF_APKSIGCOPIER"
git -C "$PF_APKSIGCOPIER" checkout 3546ac6
PYTHONPATH="$PF_APKSIGCOPIER" python3 - <<'PY'
import os
from pathlib import Path
import apksigcopier
signed = Path(os.environ['PF_APK_DOWNLOAD']) / 'PersonalFinance-v1.0.apk'
unsigned = Path(os.environ['PF_FDROID_DATA']) / 'unsigned/io.github.volmir.personalfinance_1.apk'
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

- исходники, лицензию MIT и ссылку на [релиз v1.0](https://github.com/volmir/personal_finance/releases/tag/v1.0);
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

Команда предполагает новую копию своего форка из шага 1. В подготовленном `/tmp/personal-finance-fdroiddata` адрес `origin` указывает на официальный репозиторий, поэтому отправлять туда этой командой нельзя. Для использования той ветки сначала добавить свой форк как отдельный remote и отправить ветку в него:

```bash
git -C /tmp/personal-finance-fdroiddata remote add myfork \
  "https://gitlab.com/$GITLAB_USER/fdroiddata.git"
git -C /tmp/personal-finance-fdroiddata push -u myfork add-personal-finance
```

Перед отправкой также убедиться, что изменения после последнего локального коммита включены в рецепт и повторно проверены. Отправка ветки в собственный форк сама по себе не подаёт заявку.

В GitLab открыть **Create merge request**:

- исходная ветка: `add-personal-finance` вашего форка;
- целевой проект: `fdroid/fdroiddata`, ветка `master`;
- заголовок: `New App: io.github.volmir.personalfinance`;
- описание: актуальный шаблон новой программы и сведения из шага 5.

Проверить pipeline, отвечать на замечания мейнтейнеров и отправлять исправления в ту же ветку. Слияние MR, официальная сборка и появление в индексе — разные этапы; фиксированный срок включения не гарантирован. Порядок подачи описан в [официальном руководстве](https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/).

## Дальнейшие обновления

Перед будущей заявкой или обновлением использовать актуальный проверенный релиз: увеличить `versionCode`, изменить `versionName`, добавить changelog с новым кодом, опубликовать соответствующий тег и подписанный APK. В рецепте проверить `Builds`, полный SHA коммита, `CurrentVersion` и `CurrentVersionCode`, затем повторить сборку и сравнение APK. Общий ключ подписи сохраняется.

Автообновление рецепта по тегам не гарантирует успешную сборку каждой версии. После включения проверять страницу `https://f-droid.org/packages/io.github.volmir.personalfinance/`, версию и сертификат распространяемого APK.
