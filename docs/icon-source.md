# Источник иконки

## Текущая иконка (в ветке после 1.0.1)

Знак — «кольцо роста»: разомкнутое кольцо с градиентом, три столбика разной высоты и четырёхлучевая звезда.
Палитра: фон `#0D1D36`, кольцо `#3085FA` → `#97CBFD`, столбики `#FFFFFF`, `#5AA0F5`, `#95CAFC`.

- Сгенерирована 18.11.2026 моделью `gemini-nano-banana-2.1` (Nano Banana 2.1) через инструмент генерации изображений OpenCode; мастер — квадрат 1024×1024, промпт ниже.
- В приложении растровый мастер не используется: знак перерисован векторно по измеренной геометрии макета — `drawable/ic_launcher_foreground.xml` (кольцо с `aapt:attr`-градиентом, столбики, звезда), `drawable/ic_launcher_background.xml`, `drawable/ic_launcher_monochrome.xml`. Легаси-PNG в `mipmap-*dpi` и иконки Fastlane 512×512 растрированы из тех же параметров, поэтому вектор и растры не расходятся.
- Геометрия adaptive-слоя: холст 108×108dp, знак 64dp по центру (безопасная зона — 66dp), разрыв кольца 16° с круглыми концами; легаси-иконки — знак 84% холста, квадрат со скруглением 22%, круглая версия — круг.
- C2PA-подписи у мастер-файлов нет, проверить происхождение по подписанным метаданным, как у предыдущей иконки, нельзя. Сведения об инструменте и промпте — единственное подтверждение происхождения.
- Иконка распространяется на условиях [MIT](../LICENSE), как часть проекта PersonalFinance. Генератор не заявляет прав на сгенерированный контент в своих условиях, но это не гарантирует отсутствие прав третьих лиц.

Промпт мастера:

> Flat vector app icon for a personal finance app, square 1024x1024, full-bleed background with no border, no frame, no shadow, no text, no letters, no numbers, no dollar sign. Background: deep navy indigo #0E1B33. Centered emblem occupying about 62% of the canvas with generous even margins, perfectly centered and symmetrical: a thick circular progress ring with a small gap at the lower left, colored with a smooth gradient from bright blue #3B82F6 at the top to light blue #93C5FD at the bottom; inside the ring three ascending rounded vertical bars side by side (left bar shortest in white, middle bar medium in #60A5FA, right bar tallest in #93C5FD); a small elegant four-pointed sparkle in white floating above the middle bar. Crisp clean geometry, consistent bar widths and equal gaps, modern minimal fintech style, high contrast, no gradients except the ring, no 3D, no gloss, no glow, no checkerboard.

## Предыдущая иконка (1.0–1.0.1)

Исходный рисунок щита, монеты и стрелки взят из [ранее включённого в проект JPG](https://github.com/volmir/personal_finance/blob/d23a23c3d790b9abd7b37bbb0a7d5d9d6382cab7/app/src/main/res/drawable/ic_app_finance_logo_1791442290115.jpg).
Прозрачный foreground находился в `app/src/main/res/drawable/ic_app_finance_logo.png`; с заменой иконки в ветке после 1.0.1 этот файл удалён.
Иконка распространяется на условиях [MIT](../LICENSE), как часть проекта PersonalFinance.

Автор подтвердил, что исходная иконка сгенерирована Google AI. Это согласуется с подписанными метаданными C2PA исходного JPG. Конкретный продукт Google, модель, исходный промпт и использование референсов пока не уточнены.

### Проверка происхождения 04.11.2026

- Исходный JPG: 1024×1024, SHA-256 `b98a89471c0b0cbf8c391cd925f07320b0ee02704ea0e03afe7d865dc125d630`.
- Генератор C2PA: `Google C2PA Core Generator Library`; подписант: `Google LLC / Google Media Processing Services`.
- Подписанные действия: `Created by Google Generative AI.` и `Applied imperceptible SynthID watermark.`; тип источника — `trainedAlgorithmicMedia` ([определение IPTC](https://cv.iptc.org/newscodes/digitalsourcetype/trainedAlgorithmicMedia)).
- Проверенная метка времени: `2026-10-08T06:51:29+00:00`.
- Проверка `c2patool 0.27.22` подтвердила подпись, хеш изображения и метку времени. С корневым сертификатом Google C2PA Root CA G3, загруженным с [официального сервера Google](https://pki.goog/c2pa/root-g3.crt), результат — `Trusted`, список ошибок пуст. SHA-256 отпечаток корневого сертификата: `E3:83:A9:18:25:FF:2A:09:44:85:7F:2E:0C:1B:EB:B3:BD:F8:4A:3E:43:0B:B5:05:FE:F8:E4:02:3E:D8:A3:C7`.
- Машиночитаемый результат сохранён в [icon-c2pa-report.json](./icon-c2pa-report.json). Исходный JPG доступен по ссылке на коммит выше; бинарный инструмент и его кеш в репозиторий не добавлены.

В [условиях Google](https://policies.google.com/terms?hl=en) указано, что Google не заявляет права собственности на оригинальный контент, сгенерированный некоторыми его сервисами. Это не является отдельной MIT-лицензией от Google и не гарантирует отсутствие прав третьих лиц. Конкретные дополнительные условия зависят от использованного сервиса.

Проверка подтверждает происхождение файла, но не даёт безусловного заключения о лицензионной чистоте. Визуальный осмотр и ограниченный поиск по тематическим изображениям не выявили точного стороннего оригинала; полноценный обратный поиск по изображению и проверка реестров товарных знаков не выполнялись. Для более полного описания стоит сохранить название сервиса и сведения о референсах. Заявленная MIT-лицензия проекта распространяется на права, которыми располагает автор проекта.

### Адаптация прежней иконки

Фон удалён встроенным инструментом `image_gen` (режим редактирования, `transparent_background=true`).
Промпт:

> Use case: background-extraction. Asset type: existing Android app adaptive-icon foreground, transparent PNG. Edit target: the supplied existing 1024x1024 app logo. Remove only the dark green background and background glow outside the logo, replacing them with actual transparent alpha. Preserve the original gold-rimmed green shield, gold dollar coin, diagonal rising green arrow, their shapes, colors, detailed shading, composition, size, position, original square canvas and transparent margins as faithfully as possible. Preserve the dollar sign on the coin. Keep the green inside the shield opaque. No redesign, no extra text, no new shapes, no checkerboard baked into the image, no cropping or enlarging the symbol. Return a transparent PNG with the same composition.

Для ресурсов Android изображение приводилось к прозрачному холсту 1024×1024 с сохранением приблизительного размера исходного знака. PNG для старых launcher-ресурсов и Fastlane экспортировались с фоном `#0A3F33`, соответствующим adaptive background.
