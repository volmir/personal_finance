# Источник иконки

Исходный рисунок щита, монеты и стрелки взят из [ранее включённого в проект JPG](https://github.com/volmir/personal_finance/blob/d23a23c3d790b9abd7b37bbb0a7d5d9d6382cab7/app/src/main/res/drawable/ic_app_finance_logo_1791442290115.jpg).
Прозрачный foreground находится в `app/src/main/res/drawable/ic_app_finance_logo.png`.
Иконка распространяется на условиях [MIT](../LICENSE), как часть проекта PersonalFinance.

Автор подтвердил, что исходная иконка сгенерирована Google AI. Это согласуется с подписанными метаданными C2PA исходного JPG. Конкретный продукт Google, модель, исходный промпт и использование референсов пока не уточнены.

## Проверка происхождения 04.11.2026

- Исходный JPG: 1024×1024, SHA-256 `b98a89471c0b0cbf8c391cd925f07320b0ee02704ea0e03afe7d865dc125d630`.
- Генератор C2PA: `Google C2PA Core Generator Library`; подписант: `Google LLC / Google Media Processing Services`.
- Подписанные действия: `Created by Google Generative AI.` и `Applied imperceptible SynthID watermark.`; тип источника — `trainedAlgorithmicMedia` ([определение IPTC](https://cv.iptc.org/newscodes/digitalsourcetype/trainedAlgorithmicMedia)).
- Проверенная метка времени: `2026-10-08T06:51:29+00:00`.
- Проверка `c2patool 0.27.22` подтвердила подпись, хеш изображения и метку времени. С корневым сертификатом Google C2PA Root CA G3, загруженным с [официального сервера Google](https://pki.goog/c2pa/root-g3.crt), результат — `Trusted`, список ошибок пуст. SHA-256 отпечаток корневого сертификата: `E3:83:A9:18:25:FF:2A:09:44:85:7F:2E:0C:1B:EB:B3:BD:F8:4A:3E:43:0B:B5:05:FE:F8:E4:02:3E:D8:A3:C7`.
- Машиночитаемый результат сохранён в [icon-c2pa-report.json](./icon-c2pa-report.json). Исходный JPG доступен по ссылке на коммит выше; бинарный инструмент и его кеш в репозиторий не добавлены.

В [условиях Google](https://policies.google.com/terms?hl=en) указано, что Google не заявляет права собственности на оригинальный контент, сгенерированный некоторыми его сервисами. Это не является отдельной MIT-лицензией от Google и не гарантирует отсутствие прав третьих лиц. Конкретные дополнительные условия зависят от использованного сервиса.

Проверка подтверждает происхождение файла, но не даёт безусловного заключения о лицензионной чистоте. Визуальный осмотр и ограниченный поиск по тематическим изображениям не выявили точного стороннего оригинала; полноценный обратный поиск по изображению и проверка реестров товарных знаков не выполнялись. Для более полного описания стоит сохранить название сервиса и сведения о референсах. Заявленная MIT-лицензия проекта распространяется на права, которыми располагает автор проекта.

## Адаптация для приложения

Фон удалён встроенным инструментом `image_gen` (режим редактирования, `transparent_background=true`).
Промпт:

> Use case: background-extraction. Asset type: existing Android app adaptive-icon foreground, transparent PNG. Edit target: the supplied existing 1024x1024 app logo. Remove only the dark green background and background glow outside the logo, replacing them with actual transparent alpha. Preserve the original gold-rimmed green shield, gold dollar coin, diagonal rising green arrow, their shapes, colors, detailed shading, composition, size, position, original square canvas and transparent margins as faithfully as possible. Preserve the dollar sign on the coin. Keep the green inside the shield opaque. No redesign, no extra text, no new shapes, no checkerboard baked into the image, no cropping or enlarging the symbol. Return a transparent PNG with the same composition.

Для ресурсов Android изображение приведено к прозрачному холсту 1024×1024 с сохранением приблизительного размера исходного знака. PNG для старых launcher-ресурсов и Fastlane экспортированы с фоном `#0A3F33`, соответствующим adaptive background.
