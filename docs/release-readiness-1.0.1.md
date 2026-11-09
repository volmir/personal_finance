# Выпуск v1.0.1 и проверка F-Droid — 09.11.2026

Версия приложения поднята до `versionName = "1.0.1"`, `versionCode = 2`.
После явного подтверждения автора тег `v1.0.1` создан и отправлен на релизный коммит `b94949e09844450a126a16a8f73f50edd87cd64a`.
Версия опубликована; рецепты сохраняют сборку `v1.0` и добавляют `v1.0.1` с полным SHA тега.

## Проверки итоговой версии перед тегом

Исходная база: `85f29cf70249e79c25d56b964a3e0ece195aabeb`. Сверх неё изменены только версия, документы и заметки выпуска; расчёты и статусы не менялись.

| Проверка | Результат |
| --- | --- |
| JVM и Robolectric | `bash ./gradlew testDebugUnitTest lintRelease --offline`: 205 успешно, 1 пропуск `StoreScreenshotsTest` (206 всего) |
| Ожидавшиеся ранее 197 тестов | Набор вырос после добавления регрессионных проверок; ожидания при подготовке релиза не менялись |
| Android Lint | 0 ошибок, 38 предупреждений |
| Локальное окружение | OpenJDK 21.0.12.1, Gradle 9.3.1, AGP 9.1.1, compileSdk 36.1, Build Tools 36.0.0 |
| Идентичность приложения | `io.github.volmir.personalfinance`, namespace `com.example`, minSdk 24, targetSdk 36 сохранены |
| Исторические заметки `changelogs/1.txt` | Все шесть файлов совпадают с тегом `v1.0` |
| `auditNetWorthBalanced` | Во всех шести языках обязательства описаны как превышающие половину активов |
| Возраст и горизонт | В APP_DESCRIPTION.md явно указаны полные годы |
| Скриншоты | Повторная генерация успешна: 24 PNG 1080×2400, по 4 на локаль; байты совпали с сохранёнными изображениями |
| Воспроизводимость unsigned APK | Две последовательные `clean assembleRelease --offline` на одном дереве дали байт-в-байт одинаковый APK |

Предпроверка до поднятия версии: `testDebugUnitTest --offline` завершён из актуального кеша Gradle; `lintRelease assembleRelease --offline` успешен, 0 ошибок Lint и 38 предупреждений. Затем для версии 1.0.1 выполнены повторная генерация скриншотов с `--rerun-tasks` и фактический полный тестовый прогон с lint.

Заметки `changelogs/2.txt` проверены: en-US — 362, ru-RU — 396, de-DE — 396, fr-FR — 435, it-IT — 424, es-ES — 425 символов с переводами строк. Они описывают округление, сценарии, загрузку повреждённых данных, обновление возраста при возврате, тексты налога на прирост капитала, КФС и денежного потока. Нулевой денежный поток остаётся негативным.

## Воспроизводимость unsigned APK

Два запуска `bash ./gradlew clean assembleRelease --offline` выполнены подряд без изменения дерева исходников. Gradle build cache был включён; во втором запуске 26 задач получены из кеша. Это проверка двух чистых сборок с обычными настройками проекта, а не двух компиляций с отключённым кешем.

- SHA-256 обеих копий: `d985a6c46e91c6883fbc5f29d471867887bf67755e256ad344494e2b816d427b`.
- Размер каждой: 1851643 байт.
- Через `aapt2` проверены пакет `io.github.volmir.personalfinance`, версия `1.0.1 / 2`, minSdk 24, targetSdk 36, отсутствие включённых debuggable/testOnly.
- Копии: `/tmp/personal-finance-release-1.0.1/unsigned-build-1.apk` и `unsigned-build-2.apk`.
- APK содержит `META-INF/version-control-info.textproto`; при сравнении с подписанной CI-сборкой отдельно проверяется и это происхождение сборки.

## Публикация и проверка артефактов — 09.11.2026

| Проверка | Результат |
| --- | --- |
| Релиз | [v1.0.1](https://github.com/volmir/personal_finance/releases/tag/v1.0.1), опубликован 09.11.2026 |
| Релизный коммит | `b94949e09844450a126a16a8f73f50edd87cd64a` |
| Объект аннотированного тега | `4df86fc28dc95673c788bd359c44b6cf9adccdf4` |
| CI коммита до тега | [Run Unit Tests успешен](https://github.com/volmir/personal_finance/actions/runs/42615331336) |
| Релизный CI тега | [Build, Test and Release успешен](https://github.com/volmir/personal_finance/actions/runs/42616019065): тесты, валидация секретов, lintRelease assembleRelease, проверка подписи и флагов, публикация |
| SHA-256 опубликованного APK | `37e68af509530f630892eca3c38727340d147c45907cea56471397841503d86b` |
| Размер APK | 1855739 байт |
| SHA-256 сертификата | `d67a62a233c26662269925955c653464820bf121e155ca827359c3b99d9b6609`, прежний ключ v1.0 |
| Проверка скачанных файлов | `sha256sum -c SHA256SUMS.txt` и `apksigner verify --print-certs` успешны; запись SIGNING_CERTIFICATE.txt совпадает |
| Пакет и версия | `io.github.volmir.personalfinance`, versionName 1.0.1, versionCode 2, minSdk 24, targetSdk 36 |
| Флаги манифеста | Включённых debuggable/testOnly нет; проверены badging и XML манифеста |

При сравнении скачанного подписанного APK с unsigned APK до коммита (без записей подписи) отличается только `META-INF/version-control-info.textproto`: предварительная сборка содержит SHA `85f29cf70249e79c25d56b964a3e0ece195aabeb`, CI — релизный SHA `b94949e09844450a126a16a8f73f50edd87cd64a`. Остальные записи APK совпадают. Различие происхождения не скрывалось и не устранялось правкой бинарников.

Локальная release-сборка не подписывалась. Подпись выполнялась только GitHub Actions; секреты и keystore не извлекались.

## Локальная проверка F-Droid — 09.11.2026

- Основной рецепт сохраняет v1.0 и добавляет versionName 1.0.1 / versionCode 2 на полный SHA `b94949e09844450a126a16a8f73f50edd87cd64a`; `.fdroid.yml` ссылается на `v1.0.1`. CurrentVersion / CurrentVersionCode обновлены до 1.0.1 / 2. Binaries и AllowedAPKSigningKeys сохранены.
- Первая проверка в изолированном каталоге остановилась с `Categories 'Finance Manager' is not valid`: отсутствовал официальный список категорий. Копирования только YAML оказалось недостаточно из-за ссылок на иконки. После разрешения автора восстановлены `config/categories.yml` и 11 связанных PNG из fdroiddata `5938c5fbc6800dc13c0253190b9a14c8343d668c`; категория приложения не менялась.
- SHA-256 списка категорий: `edf68d8d70073fe52a52c6a33f490399ce9dc884acff3a761a3a1e34acf9d541`. Инструкция теперь требует полный официальный каталог config, включая иконки.
- `fdroid lint io.github.volmir.personalfinance`: успешно, exit code 0.
- `fdroid build --verbose io.github.volmir.personalfinance:2`: успешно, `1 build succeeded`. Сканирование исходников и все 48 задач Gradle выполнены, compileSdk 36.1 сохранён.
- Окружение: официальный образ `registry.gitlab.com/fdroid/fdroidserver@sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac`, fdroidserver `cdfdce835a8231996b02392bc79c1879e6bef501`, apksigcopier `3546ac613de67d8328eb8ee73d09d00636e71845`, OpenJDK 21.0.12.1, Gradle 9.3.1, Build Tools 36.0.0. Gradle-кеш изолирован; предварительно скопирован только кеш зависимостей и wrapper, задачи сборки из него не восстанавливались.
- SHA-256 F-Droid unsigned APK: `868cb53019b77ee3881eaf306ce39f9dca1d83c46b4d25a665a8745bd6577afc`.
- SHA-256 скачанного эталонного подписанного APK: `37e68af509530f630892eca3c38727340d147c45907cea56471397841503d86b`; совпадает с опубликованным артефактом.
- Проверка Binaries успешна: `compared built binary to supplied reference binary successfully`; перенос публичной подписи и её проверка подтвердили воспроизводимость относительно опубликованного APK. Проверка разрешённого сертификата также успешна.

## Ограничения и оставшиеся проверки

- Эмуляторы API 24 и 36 в текущем окружении не установлены; adb не обнаружил подключённых устройств. Текущая версия на них не проверена. Проверки от 06.11 ниже являются историческими.
- Успешная локальная сборка в официальном контейнере не подтверждает сборку на производственных серверах F-Droid и принятие приложения в каталог.
- MR в fdroiddata не открывался; подача — не ранее 10.01.2027.
- Текущие локальные логи, XML и APK: `/tmp/personal-finance-release-1.0.1/`; изолированная сборка F-Droid: `/tmp/personal-finance-fdroid-1.0.1/`. Эти временные файлы не включаются в Git. URL с временными параметрами скачивания удалены из сохранённого verbose-лога.

## Архив: проверки снимка рабочей версии 1.0 / 1 от 06.11.2026

Результаты ниже относятся только к историческому снимку `ca27b1f5722f36bb29028251265de5d74162541f`, а не к релизному коммиту 1.0.1. Они не заменяют проверки новых подписанных артефактов.

### Сборка F-Droid и compileSdk

**compileSdk 36.1 успешно проверен локально в официальном образе F-Droid.** Проверка на производственной инфраструктуре каталога F-Droid не выполнялась; её нельзя считать подтверждённой этим отчётом.

Окружение проверки:

- Образ: `registry.gitlab.com/fdroid/fdroidserver@sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac`.
- `fdroidserver`: `cdfdce835a8231996b02392bc79c1879e6bef501`.
- OpenJDK `21.0.12.1`, Gradle `9.3.1`, AGP `9.1.1`, SDK Platform `android-36.1`, Build Tools `36.0.0`.
- SDK manager F-Droid `20.0` распознаёт `platforms;android-36.1` и его официальный архив `platform-36.1_r01.zip`.
- Локальный снимок проверенного кода: `ca27b1f5722f36bb29028251265de5d74162541f`. Это временный проверочный SHA, а не коммит будущего релиза.

В отдельном каталоге выполнен `fdroid build --verbose io.github.volmir.personalfinance:1`: сканирование исходников, сборка `assembleRelease`, R8 и проверка итогового APK завершились успешно (`1 build succeeded`). В проверяемом `build.gradle.kts` осталось `release(36) { minorApiLevel = 1 }`; переход на compileSdk 36 не понадобился.

Временный рецепт использовал локальный снимок исходников. В нём отсутствует `Binaries`: текущий код закономерно отличается от опубликованного APK `v1.0`. Публичный рецепт не менялся. Проверка воспроизводимости относительно будущего подписанного APK `v1.0.1` этим запуском не выполнялась. Gradle-кеш контейнера был изолирован от кеша хоста, чтобы исключить конфликт межпроцессных блокировок.

`lintRelease --offline --no-daemon --max-workers=2 -Dorg.gradle.jvmargs=-Xmx2g` в том же образе: **0 ошибок, 38 предупреждений**. Предупреждения: `GradleDependency` 11, `IconLauncherShape` 10, `UnusedResources` 7, `IconDuplicates` 5, `UseKtx` 2, по одному `ApplySharedPref`, `RedundantLabel`, `IconLocation`.

### Запуск release APK на эмуляторах

Проверен один и тот же собранный APK после добавления временной тестовой подписи. Опубликованный ключ подписи не использовался и не менялся. Тестовый APK не предназначен для публикации.

| Проверка | API 24 | API 36 |
| --- | --- | --- |
| Android / образ | 7.0, AOSP `default/x86_64`, revision 8 | 16, AOSP `default/x86_64`, revision 2 |
| Установка APK | Успешна | Успешна |
| Холодный запуск `MainActivity` | `Status: ok` | `Status: ok` |
| Inputs, Metrics, Scores & Audit, FIRE & Plan | Все четыре вкладки отрисованы | Все четыре вкладки отрисованы |
| Возврат из фона | Успешен | Успешен |
| Падения приложения в crash log | Нет | Нет |

Android Emulator `37.2.13.0`, KVM, программный GPU SwiftShader. Эмуляторы после проверки остановлены.

Пакет: `io.github.volmir.personalfinance`, версия `1.0 / 1`, `minSdk = 24`, `targetSdk = 36`.

SHA-256 unsigned APK: `c27c43a75bb1aa3242d870f5c36771c95c2e91c38b50ab4fe3fa4d93166da879`.

SHA-256 тестового подписанного APK, проверенного на обоих API: `9e1af7f129d2137c82b0f8540100f7d872abd4cc1e57b9a2a277b10556dfa863`.

### Скриншоты и заметки выпуска

После завершения регрессионных проверок текущего кода (196 тестов успешно, 1 пропуск) выполнено:

```bash
GENERATE_STORE_SCREENSHOTS=1 bash ./gradlew testDebugUnitTest --tests com.example.StoreScreenshotsTest --rerun-tasks --offline
```

Генерация завершилась успешно на JDK 21 с Robolectric API 35. Обновлены **24 PNG 1080×2400**: по четыре `images/phoneScreenshots/` для `en-US`, `ru-RU`, `de-DE`, `fr-FR`, `it-IT`, `es-ES`. Это скриншоты Compose, полученные через Robolectric; проверки запуска на API 24 и 36 выполнены отдельно на настоящих эмуляторах.

Созданы черновики `fastlane/metadata/android/<locale>/changelogs/2.txt`:

| Локаль | Символов, включая переводы строк |
| --- | ---: |
| en-US | 341 |
| ru-RU | 369 |
| de-DE | 393 |
| fr-FR | 420 |
| it-IT | 403 |
| es-ES | 413 |

По [официальной документации F-Droid](https://f-droid.org/en/docs/All_About_Descriptions_Graphics_and_Screenshots/) changelog необязателен для сборки; `en-US` — запасная локаль. Для приложения подготовлены все шесть переводов. Имя `2.txt` соответствует будущему `versionCode = 2`, а не строке `versionName`; каждый файл укладывается в лимит 500 символов. Изображения и заметки должны войти в будущий релизный коммит до создания тега.

Во время исторической проверки журналы сборки, отчёты Lint, XML и результаты запуска обоих эмуляторов, контрольные суммы скриншотов и тестовые APK были сохранены в `app/build/release-checks/` и не включаются в Git. Основные файлы: `fdroid-36.1-unsigned.log`, `lint-results-release.xml`, `api24/result.json`, `api36/result.json`, `store-metadata.json`.
