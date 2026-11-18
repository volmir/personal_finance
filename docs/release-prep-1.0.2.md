# Подготовка релиза 1.0.2 — 18.11.2026

Статус: **подготовлено, тег не создан, подписанная сборка не выпущена.**
Опубликованным остаётся `v1.0.1` / `versionCode 2`; теги `v1.0` и `v1.0.1` не перемещаются.

## Подготовленные файлы

| Файл | Изменение |
|---|---|
| `app/build.gradle.kts` | `versionName = "1.0.2"`, `versionCode = 3` |
| `fastlane/metadata/android/<locale>/changelogs/3.txt` | заметки выпуска на шести языках, каждая ≤ 500 символов |
| `.fdroid.yml` | сборка `1.0.2` / `3` по тегу `v1.0.2`; `CurrentVersion` / `CurrentVersionCode` — `1.0.2` / `3` |
| `AGENTS.md` | фактическая версия и фаза проекта |
| `app/src/main/res/**`, `fastlane/.../images/icon.png` | новый значок приложения |
| `fastlane/.../images/phoneScreenshots/*.png` | скриншоты пересобраны под текущий интерфейс |

`metadata/io.github.volmir.personalfinance.yml` **намеренно не тронут**: запись сборки требует полного SHA коммита тега, которого ещё нет. Для `v1.0.1` такая запись тоже добавлялась отдельным коммитом после создания тега.

## Содержание заметок выпуска

Заметки описывают изменения относительно `1.0.1`: подвкладки «FIRE» и «Симуляция», перенос экспорта, новый значок, контрастные статусные цвета и крупные мелкие подписи, одноразовое уведомление о демо-данных, компактный статус «До пенсии осталось».
Проверено: все шесть файлов — по шесть строк, длина ≤ 500 символов, без удвоенной пунктуации.

## Проверки этого среза

Исходная база: `46919a0` (замена значка). Сверх неё изменены только версия, заметки выпуска, рецепты F-Droid, документы и скриншоты; расчёты, пороги и тексты приложения не менялись.

| Проверка | Результат |
| --- | --- |
| JVM и Robolectric | `bash ./gradlew testDebugUnitTest :app:lintRelease :app:assembleRelease --offline`: 267 тестов, 0 падений, 1 пропуск (`StoreScreenshotsTest` в обычном прогоне) |
| Скриншоты | `GENERATE_STORE_SCREENSHOTS=1 … --tests com.example.StoreScreenshotsTest --rerun-tasks`: успешно, 24 PNG 1080×2400 (4 × 6 локалей), все 24 файла изменились |
| Android Lint (`lintRelease`) | 0 ошибок, 23 предупреждения: `GradleDependency` 11, `UnusedResources` 7 (шаблонные цвета), `UseKtx` 3, `ApplySharedPref` 1, `RedundantLabel` 1 |
| Локальное окружение | OpenJDK 21.0.13, Gradle 9.3.1, Kotlin 2.2.21, Build Tools 36.0.0 |
| Идентичность приложения | `io.github.volmir.personalfinance`, namespace `com.example`, minSdk 24, targetSdk 36 сохранены |
| Версия в APK | `aapt2 dump badging`: versionName `1.0.2`, versionCode `3` |
| Флаги манифеста | Включённых debuggable/testOnly нет; `INTERNET` отсутствует, единственное разрешение — служебное `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` |
| Воспроизводимость unsigned APK | Два запуска `clean assembleRelease --offline`: SHA-256 `7dc15267dab6b255561dce6258397bbd192358478a26b61345f3048748bcdce2` у обоих, размер 1 560 018 байт. Во втором запуске 23 задачи взяты из build cache — это две чистые сборки с обычными настройками проекта, а не компиляция с отключённым кешем |
| Заметки выпуска | Шесть файлов `changelogs/3.txt`, по 6 строк, длина: ru 487, en 453, de 488, es 488, fr 497, it 490 символов (≤ 500) |
| `metadata/io.github.volmir.personalfinance.yml` | Не изменялся: запись `1.0.2` требует полного SHA тега, которого ещё нет |

Копии unsigned APK лежат в `/tmp/opencode/pf-1.0.2/unsigned-build-{1,2}.apk` (вне репозитория). Подпись локально не выполнялась и не проверялась: она делается только секретами GitHub Actions на релизном теге. На устройствах и эмуляторах эта сборка не запускалась.

## Осталось на день релиза (не выполнено)

1. Создать тег `v1.0.2` с SSH-подписью, как `v1.0.1`, на релизном коммите и отправить его: `git tag -s v1.0.2 -m "Release 1.0.2" && git push origin v1.0.2`. Пуш тега запускает `.github/workflows/build-and-release.yml` (тесты → подписанная сборка → GitHub Release).
2. Дождаться зелёного задания `Run Unit Tests` и релизного задания; скачать `PersonalFinance-v1.0.2.apk`, проверить `apksigner verify --print-certs` и `SHA256SUMS.txt`.
3. Добавить в `metadata/io.github.volmir.personalfinance.yml` запись `versionName: '1.0.2'` / `versionCode: 3` с полным SHA релизного коммита, поднять `CurrentVersion` / `CurrentVersionCode` до `1.0.2` / `3`, сохранив `v1.0` и `v1.0.1`.
4. Пересобрать `lintRelease` и `assembleRelease` с ключом, проверить две сборки на побайтовую идентичность, повторить проверку в официальном контейнере F-Droid и сравнить APK через `apksigcopier`.
5. Обновить MR F-Droid (`!56526`) веткой с рецептом `1.0.2` и дождаться pipeline.
6. Проверить подписанный APK на устройствах API 24 и 36.
7. Заполнить `docs/release-readiness-1.0.2.md` фактическими результатами и обновить `README.md`, `APP_DESCRIPTION.md`, `FDROID_SETUP.md` по факту публикации.

## Ограничения

- Тег не публикуется без явного подтверждения автора.
- Подпись выполняется только секретами GitHub Actions; локальные ключи и `local.properties` в Git не попадают.
- Заметки выпуска — не замена проверке на устройстве и не подтверждение воспроизводимости.
