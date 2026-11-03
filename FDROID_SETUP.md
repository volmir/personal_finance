# Первая публикация в официальном F-Droid

Проверено 3 ноября 2026 года. Начальная версия приложения: `io.github.volmir.personalfinance`, versionName `1.0`, versionCode `1`, тег `v1.0`.

Перед этой инструкцией выполнить [сборку и GitHub Release](./GITHUB_RELEASE_SETUP.md). Подписанный APK распространяется через GitHub Releases; пользователи могут отслеживать обновления через Obtainium, добавив `https://github.com/volmir/personal_finance`. Это позволяет получить пользователей и обратную связь до подачи заявки в основной каталог.

## Когда подавать заявку: не раньше 10.01.2027

Разработка проекта началась **10.10.2026**. При отсутствии исключения заявку в официальный F-Droid следует подавать **не раньше 10.01.2027**, после трёх месяцев активной разработки. Самого истечения календарного срока недостаточно: разработка и сопровождение должны продолжаться.

Правило допускает ровно три исключения:

1. У приложения доказанно есть пользователи.
2. Автор уже поддерживает другие приложения в F-Droid.
3. Приложение разработано только для одного мероприятия.

Исключение нужно обосновать в заявке; наличие публичного APK само по себе не доказывает наличие пользователей. До этой даты можно подготовить рецепт в локальной ветке `fdroiddata`, выполнить `fdroid lint` и `fdroid build`, проверить воспроизводимость и собрать обратную связь. Публикация подписанного APK в GitHub Releases не требует ожидания этого срока.

Основание — раздел **Quality Control** официальной [Inclusion Policy](https://f-droid.org/en/docs/Inclusion_Policy/). Эта же политика разрешает распространять приложение через собственный или сторонний репозиторий, даже если оно не включено в основной каталог. Такой репозиторий — отдельный канал, его создание не означает принятие в официальный F-Droid.

## Что мешает считать приложение готовым

1. В исходный `metadata/io.github.volmir.personalfinance.yml` добавлены `RepoType` и `Repo`. Для заявки нужно перенести рецепт в `fdroiddata`, указать полный SHA релизного коммита и проверить сборку.
2. Тег `v1.0` и подписанный APK опубликованы. При подготовке рецепта использовать полный SHA коммита этого тега; перед подачей заявки обновить рецепт до актуального проверенного релиза.
3. Соблюсти дату подачи и условия исключений из раздела выше; основание для досрочной подачи пока не подтверждено.
4. Локальные проверки рецепта, сборки и воспроизводимости успешны; проверка в инфраструктуре официального F-Droid и включение в каталог ещё предстоят.

MIT, публичный код, отсутствие INTERNET в исходном манифесте и подготовленный Fastlane — положительные результаты проверки. Зависимости в Gradle относятся к AndroidX, Kotlin, Coroutines и desugaring; окончательная проверка дерева зависимостей и лицензий остаётся частью сборки и ревью каталога. Из отслеживаемых файлов JAR/AAR/SO/APK найден только стандартный `gradle-wrapper.jar`.

Условия возраста и исключений, лицензий и ресурсов: [Inclusion Policy](https://f-droid.org/en/docs/Inclusion_Policy/). Значение полей рецепта: [Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/).

## Результаты проверки v1.0 от 03.11.2026

- Релизный коммит: `d2c01ccb94d4340e799735002b7a0e4f92f60a37`.
- Рецепт подготовлен и закоммичен в локальной ветке `add-personal-finance` checkout `/tmp/personal-finance-fdroiddata`; ветка в GitLab не отправлялась.
- `fdroid lint io.github.volmir.personalfinance` и `fdroid build --verbose io.github.volmir.personalfinance:1` завершились успешно; сканер исходников не обнаружил блокирующих проблем.
- Проверка выполнялась с `fdroidserver` коммита `cdfdce8` в официальном образе `registry.gitlab.com/fdroid/fdroidserver:buildserver`, digest `sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac`. Контейнер использовал OpenJDK 21, Gradle 9.3.1, SDK 36.1 и Build Tools 36.0.0; GitHub Release собран с JDK 17.
- `apksigcopier` коммита `3546ac6` успешно сравнил подписанный `PersonalFinance-v1.0.apk` из GitHub с `unsigned/io.github.volmir.personalfinance_1.apk`. Это подтверждает воспроизводимость между двумя проверенными окружениями для этой версии.
- SHA-256 сертификата: `d67a62a233c26662269925955c653464820bf121e155ca827359c3b99d9b6609`.

Результаты относятся к конкретной версии и окружениям, не означают принятие в каталог и не заменяют проверку на устройстве. Перед будущей заявкой проверить актуальный релиз повторно.

## Шаг 1. Выбрать подпись до первой публикации

| Вариант | Результат |
| --- | --- |
| Обычная сборка F-Droid | F-Droid собирает исходники и подписывает APK отдельным ключом приложения; подпись отличается от GitHub |
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
  - Finance Manager
License: MIT
AuthorName: PersonalFinance
SourceCode: https://github.com/volmir/personal_finance
IssueTracker: https://github.com/volmir/personal_finance/issues
Changelog: https://github.com/volmir/personal_finance/releases

RepoType: git
Repo: https://github.com/volmir/personal_finance.git
Binaries: https://github.com/volmir/personal_finance/releases/download/v%v/PersonalFinance-v%v.apk

AllowedAPKSigningKeys:
  - d67a62a233c26662269925955c653464820bf121e155ca827359c3b99d9b6609

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

Используется полный SHA коммита, на который указывает тег. Категория `Finance Manager` взята из актуального [списка категорий fdroiddata](https://gitlab.com/fdroid/fdroiddata/-/blob/master/config/categories.yml); прежняя `Money` больше не проходит `fdroid lint`. Пароли и ключ подписи в рецепт не включать. Описания и изображения берутся из `fastlane/metadata/android` репозитория приложения; копировать переводы в YAML не нужно. Подробности приложения: [APP_DESCRIPTION.md](./APP_DESCRIPTION.md).

В рецепт включены `Binaries` и `AllowedAPKSigningKeys` для сохранения подписи разработчика: для `v1.0` воспроизводимость уже проверена. Отпечаток задан 64 шестнадцатеричными символами без двоеточий; это SHA-256 сертификата из `apksigner verify --print-certs`, а не хеш файла из `sha256sum`. Для обычной сборки с отдельной подписью F-Droid удалить оба поля, учитывая последствия смены подписи. Для новых версий повторять проверку воспроизводимости ниже.

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
git add -- metadata/io.github.volmir.personalfinance.yml
git commit -m "New App: io.github.volmir.personalfinance"
unset KEYSTORE_PATH STORE_PASSWORD KEY_PASSWORD KEY_ALIAS
fdroid build --verbose io.github.volmir.personalfinance:1
```

Коммит на этом этапе остаётся локальным. Он нужен и для корректной даты `SOURCE_DATE_EPOCH`, которую F-Droid получает из истории рецепта; у нового незакоммиченного файла она может оказаться пустой и привести к ошибке запуска Gradle. Ветка и Merge Request до разрешённой даты не отправляются.

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

## Шаг 5. Обосновать пользу и отличие приложения

В заявке описать собственную ценность приложения: политика F-Droid требует полезности для конечных пользователей и отличий от уже существующих программ. Не заявлять уникальность без проверки похожих калькуляторов в каталоге. Подготовить конкретное сравнение возможностей и подтверждённые результаты проверки на устройстве.

Основа для описания заявки на английском:

> Personal Finance & FIRE Audit combines manual household budgeting and a balance sheet with nine financial metrics, an explanatory financial audit, and a FIRE projection in one offline Android app. The FIRE model uses the present value of expenses through a user-selected life-expectancy horizon, with explicit real-return and capital-gains-tax assumptions. Fourteen what-if scenarios let users explore changes without overwriting their original inputs. The app requires no account or bank connection and does not request internet access.

Сопоставление основных сценариев по опубликованным описаниям F-Droid на 03.11.2026:

| Приложение | Основной сценарий в описании каталога | Фокус Personal Finance & FIRE Audit |
| --- | --- | --- |
| [LoanPro](https://f-droid.org/en/packages/net.kaltner.LoanPro/) | Расчёт суммы кредита, платежа, срока и ставки, в том числе для ипотеки и автокредита | Оценка совокупного финансового положения и времени до FIRE по активам, обязательствам и денежному потоку |
| [My Expenses](https://f-droid.org/en/packages/org.totschnig.myexpenses/) | Учёт повседневных транзакций, бюджетов, счетов и инвестиционных портфелей | Ручной ввод агрегированного финансового снимка, объясняющий аудит и изменение прогноза через сценарии без перезаписи исходных данных |

Это сопоставление заявленных основных сценариев, а не исчерпывающая проверка всех функций аналогов. В заявку можно добавить:

> The app's focus is a compact financial snapshot that feeds both an explanatory audit and a retirement-timing projection. LoanPro's published description focuses on solving loan variables, while My Expenses describes daily transaction, budget, and portfolio management. Personal Finance & FIRE Audit lets users explore how changes to the same aggregate snapshot affect both financial indicators and a lifetime FIRE projection, without entering a transaction history.

Собственная ценность — такое сочетание задач и прозрачных допущений; решение о достаточности отличий принимает ревьюер F-Droid. Ссылки и сравнение нужно повторно проверить непосредственно перед подачей заявки.

## Шаг 6. Отправить ветку и открыть Merge Request

Только когда выполнено условие возраста проекта или есть подтверждённое основание для исключения:

```bash
cd "$HOME/src/fdroiddata-personal-finance"
git diff --check
git status --short
git push -u origin add-personal-finance
```

Рецепт уже закоммичен локально на этапе проверки. Если после этого он менялся, сначала проверить diff и закоммитить исправления, затем отправить ветку. Открыть на GitLab **Create merge request**: исходная ветка — `add-personal-finance` вашего форка; целевой проект — `fdroid/fdroiddata`, ветка — `master`. Заполнить актуальный шаблон новой программы. Указать:

- исходники и релиз `https://github.com/volmir/personal_finance/releases/tag/v1.0`;
- MIT, приложение для ручного учёта финансов, без сетевого подключения;
- наличие Fastlane и результат тестирования на устройстве;
- собственную ценность приложения и сравнение с похожими финансовыми калькуляторами в каталоге;
- выбранную схему подписи и результат проверки воспроизводимости;
- основание для исключения по возрасту, если оно требуется;
- происхождение ресурсов, включая обработку иконки, описанную в [docs/icon-source.md](./docs/icon-source.md).

Веб-форму и переписку с мейнтейнерами выполняет автор. Создание YAML в собственном GitHub-репозитории само по себе не является заявкой в каталог. Подготовка MR описана в [официальной инструкции](https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/).

## Шаг 7. Пройти CI и ревью, дождаться индекса

Проверить pipeline MR, ответить на замечания, при необходимости отправить исправленный рецепт в ту же ветку. Слияние MR и появление приложения в индексе — разные этапы; фиксированного срока включения нет.

После публикации проверить страницу `https://f-droid.org/packages/io.github.volmir.personalfinance/`, версию и подпись скачанного APK. Для следующих релизов повышать `versionCode`, менять `versionName`, добавлять `changelogs/<versionCode>.txt`, создавать соответствующий тег. Автообновление рецепта не гарантирует успешную сборку или немедленную публикацию каждой версии.
