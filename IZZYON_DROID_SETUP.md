# Первая публикация в IzzyOnDroid

Проверено 3 ноября 2026 года. Выполнить после [первой сборки и GitHub Release](./GITHUB_RELEASE_SETUP.md). Ждать принятия в официальный F-Droid не требуется. IzzyOnDroid скачивает готовый APK разработчика; обычный F-Droid собирает приложение из исходников.

## Шаг 1. Проверить соответствие правилам до подачи заявки

Сначала прочитать актуальную [App Inclusion Policy](https://izzyondroid.org/docs/general/AppInclusionPolicy/), особенно **AI policy**. В ней отклоняются vibe-coded приложения и код, созданный LLM; использование LLM для документации и исследований допускается.

В этом проекте [docs/icon-source.md](./docs/icon-source.md) явно указывает на обработку иконки через `image_gen`. Это установленный факт об изображении, а не доказательство происхождения всего кода. Применимость правила к такой обработке ресурса нужно уточнить у мейнтейнеров. Если в приложении есть сгенерированный LLM код, считать соответствие правилам неподтверждённым и не заявлять обратное. Удаление упоминаний ИИ из документации не решает вопрос соответствия.

Технические предпосылки в проекте есть: публичный репозиторий, MIT, уникальный applicationId, Fastlane на шести языках. Подписанного опубликованного APK на дату проверки ещё нет. Существующий неподписанный файл около 1,77 MiB даёт лишь предварительный ориентир по размеру; проверить нужно итоговый APK.

## Шаг 2. Проверить опубликованный релиз v1.0

Команды для Bash/Ubuntu; GitHub CLI, Android SDK Build Tools 36.0.0 и platform-tools должны быть доступны, как в инструкции GitHub:

```bash
gh release view v1.0 --repo volmir/personal_finance
mkdir -p /tmp/personal-finance-v1.0
gh release download v1.0 --repo volmir/personal_finance \
  --pattern 'PersonalFinance-v1.0.apk' --dir /tmp/personal-finance-v1.0
export APK_FILE=/tmp/personal-finance-v1.0/PersonalFinance-v1.0.apk
apksigner verify --verbose --print-certs "$APK_FILE"
aapt2 dump badging "$APK_FILE"
aapt2 dump xmltree "$APK_FILE" --file AndroidManifest.xml
stat -c '%s bytes' "$APK_FILE"
sha256sum "$APK_FILE"
```

Если APK уже скачан по предыдущей инструкции, пропустить команду скачивания. Убедиться, что релиз опубликован, не draft/prerelease, а APK доступен без авторизации по адресу:

```text
https://github.com/volmir/personal_finance/releases/download/v1.0/PersonalFinance-v1.0.apk
```

Ожидаются `io.github.volmir.personalfinance`, versionName `1.0`, versionCode `1`, постоянный релизный сертификат; `debuggable` и `testOnly` должны отсутствовать либо быть `false`. Обычный лимит IzzyOnDroid — около 30 MB на APK/приложение; не рассчитывать на исключение. Не загружать debug APK или `app-release-unsigned.apk`.

Проверить именно этот файл на тестовом устройстве:

```bash
adb devices
adb install -r "$APK_FILE"
adb shell am start -n io.github.volmir.personalfinance/com.example.MainActivity
```

Проверить ввод данных, четыре вкладки, сохранение после перезапуска, симулятор и отправку отчёта. При конфликте подписи с уже установленной debug-версией использовать чистое тестовое устройство: удаление приложения уничтожит его локальные данные.

## Шаг 3. Проверить данные для карточки приложения

```bash
cd /var/www/personal_finance
for locale in en-US ru-RU de-DE fr-FR it-IT es-ES; do
  printf '%s\n' "$locale"
  wc -m "fastlane/metadata/android/$locale/title.txt" \
    "fastlane/metadata/android/$locale/short_description.txt" \
    "fastlane/metadata/android/$locale/full_description.txt" \
    "fastlane/metadata/android/$locale/changelogs/1.txt"
done
ls fastlane/metadata/android/en-US/images
ls fastlane/metadata/android/en-US/images/phoneScreenshots
```

Проверка 03.11.2026: короткие описания — 62–73 символа, полные — 1780–2630, changelog — 164–236 символов без завершающего перевода строки и 173–430 байт в файле; по четыре скриншота на язык, английская и русская иконки 512×512. Английские изображения служат базовым набором. Описания не нужно дублировать в README или рецепте сборки; актуальные поля и требования к изображениям сверять с [Fastlane](https://izzyondroid.org/docs/general/Fastlane/).

Скриншоты имеют размер 1080×2400, отношение длинной стороны к короткой — 2,22. Документация Fastlane предупреждает, что некоторые валидаторы требуют не больше 2:1. Перед подачей проверить их выбранным валидатором; если он отклоняет пропорции, добавить поля до 1200×2400, сохранив содержимое экрана. Сам по себе этот результат не подтверждает отказ каталога.

## Шаг 4. Подать заявку в актуальный трекер

Официальный FAQ направляет заявки в [Codeberg: IzzyOnDroid/repodata](https://codeberg.org/IzzyOnDroid/repodata/issues). Старый адрес `gitlab.com/IzzyOnDroid/repo` из предыдущей инструкции больше не использовать как основной путь подачи. См. [официальный FAQ](https://izzyondroid.org/faq/).

Войти в Codeberg, проверить отсутствие уже поданной заявки по `io.github.volmir.personalfinance`, нажать **New issue**, выбрать актуальный шаблон добавления приложения. Это действие в веб-интерфейсе; команды `gh` работают с GitHub и такую заявку не создают.

Данные для заполнения:

```text
App: Personal Finance & FIRE Audit
Application ID: io.github.volmir.personalfinance
Source: https://github.com/volmir/personal_finance
License: MIT
Release: https://github.com/volmir/personal_finance/releases/tag/v1.0
APK: https://github.com/volmir/personal_finance/releases/download/v1.0/PersonalFinance-v1.0.apk
Version: 1.0 (versionCode 1)
Fastlane: https://github.com/volmir/personal_finance/tree/v1.0/fastlane/metadata/android
Description: Offline personal finance calculator and FIRE retirement simulator.
```

Приложить фактический SHA-256 **сертификата подписи**, размер APK и результаты проверки на устройстве. Честно описать использование ИИ согласно шагу 1 и приложить ссылку на происхождение иконки. Не отправлять заявку с утверждением о соответствии, если известен конфликт с политикой каталога.

## Шаг 5. Пройти проверку и выпускать обновления

Следить за заявкой, отвечать на замечания, дождаться появления карточки `https://apt.izzysoft.de/fdroid/index/apk/io.github.volmir.personalfinance`. Одобрение заявки и загрузка в индекс занимают отдельное время; обещания срока первой публикации нет.

После включения каталог обычно проверяет релизы ежедневно. Для дальнейших версий сохранять ключ, повышать `versionCode`, менять `versionName`, добавлять changelog с новым кодом и публиковать новый тег с подписанным APK. Не подменять APK под существующим тегом. Воспроизводимые сборки — дополнительная проверка, она пока не настроена и не подтверждена.

GitHub и IzzyOnDroid распространяют APK с вашей подписью. Если официальный F-Droid использует отдельный ключ, обновление между источниками потребует переустановки; для общей подписи заранее пройти настройку воспроизводимости по [инструкции F-Droid](./FDROID_SETUP.md).
