# Руководство по публикации приложения в официальном каталоге F-Droid

Данный документ содержит полное описание и пошаговую инструкцию по добавлению приложения **«Личные финансы & FIRE аудит»** (`com.aistudio.finhealthfire.kzqpv`) в официальный каталог свободных приложений **F-Droid**.

---

## 1. Чем официальный F-Droid отличается от IzzyOnDroid?

| Параметр | IzzyOnDroid | Официальный F-Droid |
| :--- | :--- | :--- |
| **Способ поставки** | Скачивает ваши готовые APK из GitHub Releases | Собирает APK **напрямую из исходного кода** на изолированных сборочных серверах F-Droid |
| **Подпись APK** | Ваш личный ключ (Upload Keystore) | Официальный мастер-ключ F-Droid |
| **Требования к коду** | Достаточно открытого репозитория и лицензии | Строгий аудит: 100% FOSS, отсутствие проприетарных SDK, трекеров и бинарных блобов |
| **Аудитория** | Репозиторий для энтузиастов и бета-релизов | Миллионы пользователей открытого ПО по всему миру (встроен по умолчанию во многие кастомные прошивки: LineageOS, GrapheneOS, /e/OS, CalyxOS) |

---

## 2. Чек-лист готовности приложения

Приложение **полностью соответствует** всем критериям политики включения F-Droid (Inclusion Policy):

- [x] **Лицензия с открытым исходным кодом:** проект распространяется под лицензией **MIT** (файл `LICENSE` в корне репозитория).
- [x] **Чистота от трекеров и аналитики:** в приложении нет ни одного рекламного или отслеживающего SDK (0 трекеров по классификации Exodus Privacy).
- [x] **Полная автономность (100% Offline):** в `AndroidManifest.xml` **нет разрешения `android.permission.INTERNET`**.
- [x] **Отсутствие Anti-Features:** нет платного контента, навязчивых сетевых сервисов или зависимостей от закрытых Google Play Services.
- [x] **Наличие Gradle Wrapper:** в репозитории подготовлены скрипты `gradlew` и конфигурация `gradle-wrapper.properties` (Gradle 9.3.1).
- [x] **Метаданные Fastlane:** подготовлены каталоги `fastlane/metadata/android/` с полными текстами на русском (`ru-RU`) и английском (`en-US`), а также иконка `icon.png` и история изменений (`changelogs/1.txt`).
- [x] **Файл рецепта сборки для F-Droid:** сгенерирован `metadata/com.aistudio.finhealthfire.kzqpv.yml`.

---

## 3. Рецепт сборки для F-Droid (`fdroiddata`)

Для добавления приложения в каталог F-Droid используется следующий файл манифеста сборки. Он уже создан в вашем репозитории по пути:
`metadata/com.aistudio.finhealthfire.kzqpv.yml`

```yaml
Categories:
  - Money
License: MIT
AuthorName: Personal Finance & FIRE
SourceCode: https://github.com/ВАШ_ПОЛЬЗОВАТЕЛЬ/ВАШ_РЕПОЗИТОРИЙ
IssueTracker: https://github.com/ВАШ_ПОЛЬЗОВАТЕЛЬ/ВАШ_РЕПОЗИТОРИЙ/issues
Changelog: https://github.com/ВАШ_ПОЛЬЗОВАТЕЛЬ/ВАШ_РЕПОЗИТОРИЙ/releases

AutoUpdateMode: Version
UpdateCheckMode: Tags
CurrentVersion: '1.0'
CurrentVersionCode: 1

Builds:
  - versionName: '1.0'
    versionCode: 1
    commit: v1.0
    subdir: app
    gradle:
      - yes

AutoName: Personal finance and FIRE
```

> **Важно:** Замените `ВАШ_ПОЛЬЗОВАТЕЛЬ/ВАШ_РЕПОЗИТОРИЙ` на реальный путь к вашему репозиторию на GitHub или GitLab.

---

## 4. Пошаговая инструкция по подаче заявки в F-Droid

Добавление в F-Droid происходит через создание **Merge Request** в официальный репозиторий [`fdroiddata`](https://gitlab.com/fdroid/fdroiddata) на GitLab.

### Шаг 1. Опубликовать проект на GitHub и создать Git-тег
1. Отправьте проект в ваш публичный репозиторий GitHub:
   ```bash
   git add .
   git commit -m "feat: Initial release v1.0"
   git tag v1.0
   git push origin main --tags
   ```

### Шаг 2. Сделать Fork репозитория `fdroiddata`
1. Зайдите на GitLab: [https://gitlab.com](https://gitlab.com) (зарегистрируйтесь, если у вас нет учетной записи).
2. Перейдите в репозиторий каталога F-Droid: [https://gitlab.com/fdroid/fdroiddata](https://gitlab.com/fdroid/fdroiddata).
3. Нажмите кнопку **Fork** в правом верхнем углу.

### Шаг 3. Добавить файл метаданных в ваш Fork
1. В своем форке создайте новую ветку, например: `add-kzqpv-fire`:
   ```bash
   git checkout -b add-kzqpv-fire
   ```
2. Создайте файл `metadata/com.aistudio.finhealthfire.kzqpv.yml` (содержимое из раздела 3 выше).
3. Закоммитьте и отправьте ветку:
   ```bash
   git add metadata/com.aistudio.finhealthfire.kzqpv.yml
   git commit -m "New app: com.aistudio.finhealthfire.kzqpv"
   git push origin add-kzqpv-fire
   ```

### Шаг 4. Открыть Merge Request (MR)
1. Перейдите на страницу вашего форка на GitLab.
2. GitLab автоматически предложит **Create merge request**.
3. Заполните форму:
   - **Title:** `New app: com.aistudio.finhealthfire.kzqpv`
   - В описании откроется стандартный шаблон `New App Submission`. Отметьте нужные пункты:
     - [x] The app complies with the inclusion criteria
     - [x] The app is not a duplicate
     - [x] I am the author of the app
4. Нажмите **Create merge request**.

---

## 5. Что происходит после отправки MR?

1. **Автоматический CI бот F-Droid:**
   - Запустит проверку линтера `fdroid lint`.
   - Запустит тестовую сборку APK в изолированном Docker-контейнере.
   - Проверит соответствие лицензии и отсутствие закрытых библиотек.
2. **Ревью мейнтейнеров:**
   - Волонтеры F-Droid могут оставить комментарии или подтвердить сборку.
   - Так как приложение на 100% офлайн и использует чистый Compose + Room с лицензией MIT, прохождение проверки обычно проходит без нареканий.
3. **Слияние и публикация:**
   - После слияния MR сборочный сервер F-Droid скомпилирует релиз, подпишет его ключом F-Droid и добавит в официальный индекс каталога.
   - Приложение станет доступно для установки через клиент F-Droid, Aurora Droid, Neo Store и веб-сайт [f-droid.org](https://f-droid.org).

---

## 6. Как работают дальнейшие обновления версий?

Благодаря строкам в рецепте:
```yaml
AutoUpdateMode: Version
UpdateCheckMode: Tags
```
Вам **не нужно** будет каждый раз создавать новый Merge Request на GitLab!
Робот F-Droid `checkupdates` регулярно сканирует теги в вашем GitHub-репозитории. Когда вы выпускаете новую версию (например, тег `v1.1`), F-Droid автоматически:
1. Заметит новый тег.
2. Подтянет обновленный `versionCode` и `versionName`.
3. Запустит сборку новой версии и опубликует обновление в каталоге.
