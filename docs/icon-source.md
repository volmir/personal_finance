# Источник иконки

Исходный рисунок щита, монеты и стрелки взят из [ранее включённого в проект JPG](https://github.com/volmir/personal_finance/blob/d23a23c3d790b9abd7b37bbb0a7d5d9d6382cab7/app/src/main/res/drawable/ic_app_finance_logo_1791442290115.jpg).
Прозрачный foreground находится в `app/src/main/res/drawable/ic_app_finance_logo.png`.
Иконка распространяется на условиях [MIT](../LICENSE), как часть проекта PersonalFinance.

Фон удалён встроенным инструментом `image_gen` (режим редактирования, `transparent_background=true`).
Промпт:

> Use case: background-extraction. Asset type: existing Android app adaptive-icon foreground, transparent PNG. Edit target: the supplied existing 1024x1024 app logo. Remove only the dark green background and background glow outside the logo, replacing them with actual transparent alpha. Preserve the original gold-rimmed green shield, gold dollar coin, diagonal rising green arrow, their shapes, colors, detailed shading, composition, size, position, original square canvas and transparent margins as faithfully as possible. Preserve the dollar sign on the coin. Keep the green inside the shield opaque. No redesign, no extra text, no new shapes, no checkerboard baked into the image, no cropping or enlarging the symbol. Return a transparent PNG with the same composition.

Для ресурсов Android изображение приведено к прозрачному холсту 1024×1024 с сохранением приблизительного размера исходного знака. PNG для старых launcher-ресурсов и Fastlane экспортированы с фоном `#0A3F33`, соответствующим adaptive background.
