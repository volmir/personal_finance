# Выпуск v1.0.2 и проверка артефактов — 18.11.2026

Версия приложения поднята до `versionName = "1.0.2"`, `versionCode = 3`.
После явного подтверждения автора тег `v1.0.2` создан с SSH-подписью и отправлен на релизный коммит `293849bcb33355ef68df9da68e09892fe2262529`.
Версия опубликована; рецепты сохраняют сборки `v1.0` и `v1.0.1` и добавляют `v1.0.2` с полным SHA тега.

## Проверки перед тегом

Исходная база: `46919a0` (замена значка). Сверх неё изменены только версия, заметки выпуска, рецепты F-Droid, документы и скриншоты; расчёты, пороги и тексты приложения не менялись.

| Проверка | Результат |
| --- | --- |
| JVM и Robolectric | `bash ./gradlew testDebugUnitTest :app:lintRelease :app:assembleRelease --offline`: 267 тестов, 0 падений, 1 пропуск (`StoreScreenshotsTest` в обычном прогоне) |
| Android Lint | `lintRelease`: 0 ошибок, 23 предупреждения (`GradleDependency` 11, `UnusedResources` 7 — шаблонные цвета, `UseKtx` 3, `ApplySharedPref` 1, `RedundantLabel` 1) |
| Локальное окружение | OpenJDK 21.0.13, Gradle 9.3.1, Kotlin 2.2.21, Build Tools 36.0.0 |
| Идентичность приложения | `io.github.volmir.personalfinance`, namespace `com.example`, minSdk 24, targetSdk 36 сохранены |
| Скриншоты | Повторная генерация успешна: 24 PNG 1080×2400, по 4 на локаль; все 24 файла изменились |
| Воспроизводимость unsigned APK | Два запуска `clean assembleRelease --offline` дали байт-в-байт одинаковый APK: SHA-256 `7dc15267dab6b255561dce6258397bbd192358478a26b61345f3048748bcdce2`, 1 560 018 байт |
| Заметки `changelogs/3.txt` | Шесть файлов, по 6 строк, длина: ru 487, en 453, de 488, es 488, fr 497, it 490 символов (≤ 500) |
| CI релизного коммита | [Run Unit Tests успешен](https://github.com/volmir/personal_finance/actions/runs/44270154409) на JDK 17 |

## Публикация и проверка артефактов — 18.11.2026

| Проверка | Результат |
| --- | --- |
| Тег | Аннотированный `v1.0.2` с SSH-подписью, объект тега `74f00d43666b02f04c4401bf7f0b3b753b5917c8` |
| Релизный коммит | `293849bcb33355ef68df9da68e09892fe2262529` |
| Проверка подписи тега локально | `git tag -v`: `Good "git" signature for volmir@ukr.net with RSA key SHA256:ixD/wpXxZkEW0RjYzsXlHmfIPdWAFgSTTS2fACvLWsI` (через временный `gpg.ssh.allowedSignersFile`, в репозиторий не добавлен) |
| Релизный CI тега | [Build, Test and Release успешен](https://github.com/volmir/personal_finance/actions/runs/44273637556): оба задания, включая проверку секретов, `lintRelease assembleRelease`, проверку подписи и публикацию |
| Релиз | [v1.0.2](https://github.com/volmir/personal_finance/releases/tag/v1.0.2), опубликован 18.11.2026 09:15 UTC |
| SHA-256 опубликованного APK | `d8a51625b02fa013939de561e54a7bad05f372eeb81e19a7b53f62b2b9cd7fe9` |
| Размер APK | 1 564 114 байт |
| SHA-256 сертификата | `d67a62a233c26662269925955c653464820bf121e155ca827359c3b99d9b6609`, прежние ключи v1.0 и v1.0.1 |
| Проверка скачанных файлов | `sha256sum -c SHA256SUMS.txt` успешна; `apksigner verify --print-certs` подтверждает сертификат из `SIGNING_CERTIFICATE.txt` |
| Пакет и версия | `io.github.volmir.personalfinance`, versionName 1.0.2, versionCode 3, minSdk 24, targetSdk 36 |
| Флаги манифеста | Включённых debuggable/testOnly нет; `INTERNET` отсутствует |
| Состав релиза | `PersonalFinance-v1.0.2.apk`, `SHA256SUMS.txt`, `SIGNING_CERTIFICATE.txt` |

## Сравнение с пересборкой из тега

Локальная пересборка того же коммита `293849b` (`clean assembleRelease --offline`, без подписи) дала SHA-256 `d65d928e8495e31c6e679b0e24b81c5095e4994b4d4a6d6637d31d1162ab16bc`, 1 560 018 байт.

- Все 88 записей ZIP совпадают с опубликованным APK побайтово; лишних или недостающих записей нет.
- Отличие только в блоке подписи APK (v2/v3), который добавляется при подписании, — это и объясняет разницу в 4 096 байт и другой SHA-256 всего файла.
- Запись `META-INF/version-control-info.textproto` в обеих сборках указывает `293849bcb33355ef68df9da68e09892fe2262529`. Предварительные unsigned-сборки до создания релизного коммита записали `46919a0` — это ожидаемое различие происхождения, а не расхождение кода.
- Локальная release-сборка не подписывалась; подпись выполнялась только в GitHub Actions, секреты и keystore не извлекались.

## Локальная проверка F-Droid — 18.11.2026

- Рецепт `metadata/io.github.volmir.personalfinance.yml` (в fdroiddata, ветка MR) содержит последнюю сборку `1.0.2` / `3` на полный SHA `293849bcb33355ef68df9da68e09892fe2262529`, `CurrentVersion` / `CurrentVersionCode` — `1.0.2` / `3`, `Binaries` и `AllowedAPKSigningKeys` сохранены, ключ подписи не менялся.
- `.fdroid.yml` в репозитории приложения ссылается на тег `v1.0.2`.
- `fdroid rewritemeta` и `fdroid lint io.github.volmir.personalfinance`: успешно.
- `fdroid build --verbose io.github.volmir.personalfinance:3` в официальном контейнере: **1 build succeeded**, исходники просканированы, 48 задач Gradle выполнены.
- `Binaries`: F-Droid сам сравнил собранный APK с опубликованным `PersonalFinance-v1.0.2.apk`, проверка прошла; `allowed signer` совпал с `d67a62a2…d9b6609`.
- Независимое сравнение `apksigcopier` (`3546ac6`, `do_compare(..., unsigned=True)`) опубликованного APK с `unsigned/io.github.volmir.personalfinance_3.apk`: успешно.
- Unsigned APK из контейнера: SHA-256 `d65d928e8495e31c6e679b0e24b81c5095e4994b4d4a6d6637d31d1162ab16bc`, 1 560 018 байт; он **побайтово совпал** с независимой пересборкой того же коммита на хосте.
- Окружение контейнера: `registry.gitlab.com/fdroid/fdroidserver@sha256:1ea8bef1820bff06c2974e31b4fc4c79c364cd24231b86e71fa4b926797e0dac`, fdroidserver `cdfdce8`, OpenJDK 21.0.12.1, Gradle 9.3.1, SDK 36.1, Build Tools 36.0.0.
- Локальный успех не заменяет сборку на производственной инфраструктуре каталога.

## Обновление MR F-Droid

- Ветка `submit-personal-finance-1.0.1` форка `volmir561/fdroiddata` обновлена коммитами `ee47cf92` (рецепт `1.0.2`) и `4a4470b4` (`AutoName` → `Finance & FIRE`); MR [!56526](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/56526) обновлён, описание и комментарий приведены к версии `1.0.2`.
- [Pipeline 3083348788](https://gitlab.com/volmir561/fdroiddata/-/pipelines/3083348788) на коммите `4a4470b4849f8982a503a10d9dccde72bc394b58`: **9 из 9 заданий успешны**, включая `fdroid build`, `check apk`, `checkupdates`, `fdroid lint`, `rewritemeta` и проверки схемы.
- Предыдущий прогон `3083300199` падал только в `checkupdates`: Android-label приложения изменился на «Finance & FIRE», и задание переписывало `AutoName`. Исправлено в `4a4470b4`; других изменений поведения нет.
- Решение о включении остаётся за мейнтейнерами: трёхмесячный срок не достигнут, исключение не подтверждено.

## Проверка на устройстве и эмуляторах

18.11.2026 автор установил опубликованный подписанный APK `PersonalFinance-v1.0.2.apk` на смартфон и сообщил, что приложение работает.

| Параметр | Значение |
| --- | --- |
| Устройство | Redmi Note 11S |
| Прошивка | Xiaomi HyperOS 1.0.11.0.TKEEUXM |
| Версия Android | 13 (сборка `13 TP1A.220624.014`) |
| Обновление безопасности | 2024-12-01 |

Перечень сценариев на смартфоне отдельно не зафиксирован: запись основана на сообщении автора об установке и работе приложения.

Прогон на эмуляторах выполнен 18.11.2026 для того же опубликованного APK (SHA-256 `d8a51625…7fe9`) на чистых AOSP-образах:

| Параметр | API 24 | API 36 |
| --- | --- | --- |
| Образ | `system-images;android-24;default;x86_64` | `system-images;android-36;default;x86_64` |
| Версия Android | 7.0 | 16 |
| AVD | Pixel 5, 1080×2340, headless, SwiftShader | то же |
| Установка `adb install -r` | Success | Success |
| Версия в `dumpsys package` | 1.0.2 / 3, minSdk 24, targetSdk 36 | 1.0.2 / 3, minSdk 24, targetSdk 36 |
| Холодный запуск | `Status: ok`, 1921 мс | `Status: ok`, 3317 мс |
| Одноразовое уведомление о демо-данных | показано, закрыто кнопкой «Got it» | показано, закрыто |
| Вкладки «Inputs → Metrics → Scores & Audit → FIRE & Plan → Inputs» | переключены нажатиями по UI-дереву (`uiautomator`), все экраны отрисованы | то же |
| Фон (HOME) и возврат | процесс жив (`pid` не изменился), `am start` → `Status: ok` | то же |
| `FATAL EXCEPTION` в logcat | 0 | 0 |
| Процесс после прогона | жив | жив |

Метод: headless-эмулятор, нажатия по координатам из `uiautomator dump` (без угадывания позиций), скриншоты каждого экрана сохранены вне репозитория (`/tmp/opencode/emu`). Не проверялись: экспорт файлов и «поделиться», ввод чисел с клавиатуры, поворот экрана, работа на планшете, длительная сессия, поведение при обновлении поверх 1.0.1. Это проверка запуска и навигации, а не полный регрессионный прогон: автоматические UI-тесты для этих сценариев в проекте отсутствуют.

## Что осталось

1. Дождаться решения мейнтейнеров по MR [!56526](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/56526) и, при необходимости, ответить на замечания.
2. При желании — расширить проверку на устройстве и эмуляторах: экспорт и «поделиться», ввод чисел, поворот экрана, обновление поверх 1.0.1.
