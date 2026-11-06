# Проверки перед планируемым v1.0.1 — 06.11.2026

Следующий релиз запланирован как `v1.0.1`, `versionName = "1.0.1"`, `versionCode = 2`.
По решению автора сейчас сохранены `versionName = "1.0"` и `versionCode = 1`.
Тег не создавался; рецепт `metadata/io.github.volmir.personalfinance.yml` по-прежнему описывает опубликованный `v1.0`.

## Сборка F-Droid и compileSdk

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

## Запуск release APK на эмуляторах

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

## Скриншоты и заметки выпуска

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

Локальные журналы сборки, отчёты Lint, XML и результаты запуска обоих эмуляторов, контрольные суммы скриншотов и тестовые APK сохранены в `app/build/release-checks/` и не включаются в Git. Основные файлы: `fdroid-36.1-unsigned.log`, `lint-results-release.xml`, `api24/result.json`, `api36/result.json`, `store-metadata.json`.
