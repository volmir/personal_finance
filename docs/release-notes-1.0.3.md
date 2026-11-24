# Personal Finance & FIRE Audit 1.0.3

Draft for future manual publication; minor UI edits are still planned. The owner approved reusing the current screenshots and deferred the final release build. Version: `1.0.3`, versionCode: `4`. Changes since `v1.0.2`.

## English

This update improves navigation, data entry and the presentation of financial results.

- Keyboard navigation supports moving between fields and completing entry. Lower input fields remain accessible while the keyboard is open; decimal input handling is improved.
- Scroll positions, audit filters and expanded hints are retained when switching tabs. FIRE and Simulation keep separate scroll positions.
- Metrics and Analysis are more compact and readable. Formulas, model assumptions and the disclaimer expand on tap; the assets and real-return formulas are available in the input hints.
- A pinned simulation panel compares time to FIRE and retirement age with the base calculation, shows the time change and counts active adjustments. Reset remains available in the panel.
- FIRE durations and projected ages use tenths of a year without unnecessary trailing zeros. Small positive durations that round to zero have a separate label. Dates use the interface language.
- Improved chart and slider accessibility descriptions, typography, button states and launch appearance in the dark theme.
- Corrected scenario wording and translations in all six languages. The score export includes a brief assessment, and exported reports use the localized app name and a model-assumptions icon.

The financial formulas, score thresholds and scenario amounts are unchanged. The calculation keeps its original precision; this update changes how FIRE durations are displayed. Simulation adjustments remain separate from the saved base inputs, audit and exported reports.

The app continues to work fully offline, without an account, ads, analytics or internet permission. Android 7.0 (API 24) or later is required.

## Русский

Обновление улучшает навигацию, ввод данных и представление финансовых результатов.

- С клавиатуры можно переходить к следующему полю и завершать ввод. Нижние поля доступны при открытой клавиатуре; улучшен ввод дробных сумм.
- При переходах между вкладками сохраняются прокрутка, фильтры аудита и открытые подсказки. У FIRE и «Симуляции» отдельные позиции прокрутки.
- Показатели и «Анализ» стали компактнее и удобнее для чтения. Формулы, допущения модели и отказ от ответственности раскрываются по нажатию; формулы активов и реальной доходности перенесены в подсказки ввода.
- Закреплённая панель симуляции сравнивает срок и возраст FIRE с базовым расчётом, показывает изменение срока и число активных поправок. Кнопка сброса остаётся в этой панели.
- Сроки и предполагаемый возраст FIRE показаны до десятых года без лишних нулей. Малый положительный срок, округляемый до нуля, имеет отдельную подпись. Даты отображаются на языке интерфейса.
- Улучшены описания графика и ползунков для средств доступности, типографика, состояния кнопок и запуск в тёмной теме.
- Исправлены формулировки сценариев и переводы на шести языках. В экспорт оценки добавлена краткая характеристика; в отчётах используются локализованное название приложения и пиктограмма допущений модели.

Финансовые формулы, пороги оценок и суммы сценариев не изменены. Расчёты сохраняют исходную точность; изменилось отображение сроков FIRE. Симуляция по-прежнему отделена от сохранённых исходных данных, аудита и экспортируемых отчётов.

Приложение работает полностью офлайн, без учётной записи, рекламы, аналитики и разрешения на интернет. Требуется Android 7.0 (API 24) или новее.

## Publication assets

Expected signed-release files, to be prepared from the final release commit with the existing release key:

- `PersonalFinance-v1.0.3.apk`
- `SHA256SUMS.txt`, calculated from that signed APK
- `SIGNING_CERTIFICATE.txt`, extracted from that signed APK

Store metadata is in `fastlane/metadata/android/<locale>/`: draft notes in `changelogs/4.txt`, titles, short and full descriptions, and screenshots in `images/phoneScreenshots/`. Locales: `en-US`, `ru-RU`, `de-DE`, `fr-FR`, `it-IT`, `es-ES`.

No tag or release is created as part of preparation. The owner intentionally removed the GitHub Actions workflow; publication is manual. Verification results and remaining publication steps are recorded in [the preparation checklist](release-prep-1.0.3.md).
