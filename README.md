# Personal finance and FIRE (Личные финансы & FIRE аудит)

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24%2B_%28Android_7.0%29-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2%2B-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Offline](https://img.shields.io/badge/Network-No_Internet_Permission-brightgreen.svg)](https://developer.android.com/guide/topics/manifest/uses-permission-element)

---

## 📖 App Description / Описание приложения

Полное описание экранов, полей, формул FIRE и ограничений модели находится в [APP_DESCRIPTION.md](./APP_DESCRIPTION.md).

**Personal Finance & FIRE Audit** is an open-source, offline-first personal wealth management and early retirement modeling application built natively for Android using modern Jetpack Compose and Material 3.

The app helps users record a household budget and balance sheet, review calculated financial indicators, generate an explanatory audit, and explore a FIRE retirement projection with adjustable assumptions and hypothetical scenarios.

### 🌟 Core Philosophy
- **Works offline:** The app manifest does not request `android.permission.INTERNET`; calculations and saved financial data stay on the device. There is no account sign-in or bank connection.
- **Local persistence:** User inputs and simulator state are stored locally. The initial form is prefilled with sample US-average values; these are examples, not personalized or region-specific defaults.
- **Transparent assumptions:** Metrics use explicit formulas, including the Fisher equation for real return, age-based Fidelity milestones, and a FIRE projection through the selected life-expectancy horizon. The projection does not use the 4% rule or Trinity Study.

---

## ✨ Features / Возможности

The application is structured into 4 cohesive functional tabs:

### 1. Данные для расчетов (Input Data & Budgeting)
- **Income & Expenses:** Monthly income and expenses with expandable category guidance.
- **Three Asset Types:**
  - *Liquid Assets (Подушка):* Cash, high-yield checking, bank deposits.
  - *Financial Assets (Инвестиции):* Stock portfolios, index funds/ETFs, bonds, retirement accounts.
  - *Property Assets (Недвижимость и имущество):* Primary residence, vehicles, real estate equity.
- **Liabilities:** Enter a total for mortgages, car loans, consumer loans, credit balances, or other obligations.
- **Emergency Fund Setting:** Choose whether financial assets are included with liquid assets when calculating the emergency reserve.
- **Demographics & Investment Parameters:** Birth date, life expectancy, expected nominal return, inflation, and capital-gains tax assumption.
- **Currency Display:** Choose among USD, EUR, RUB, UAH, KZT, BYN, AMD, GBP, JPY, CHF, CAD, AUD, and INR symbols. This changes displayed units; it does not convert amounts or fetch exchange rates.
- **Dynamic Cash Flow & Savings Rate Preview:** Real-time feedback directly on the input cards.

### 2. Расчет показателей (9 Core Financial Metrics)
1. **Денежный поток (Cash Flow):** `Monthly Income - Monthly Expenses` (net monthly surplus/deficit).
2. **Норма сбережений (Savings Rate):** `(Cash Flow / Income) * 100%` (budget surplus as a share of income).
3. **Подушка безопасности (Emergency Fund):** Total crisis buffer volume with dynamic color-coding.
4. **Запас месячных расходов (Safety Months):** Runway duration in months of living expenses.
5. **Коэффициент задолженности (Debt Ratio):** `Liabilities / Total Assets`.
6. **Чистый капитал (Net Worth):** `Total Assets - Total Liabilities`.
7. **Бенчмарк Fidelity:** Age-based milestone target (`Annual Income * Age Multiplier`), interpolated between age checkpoints.
8. **Коэффициент финансовой свободы (КФС / CFS):** `Net Worth / Annual Expenses`, shown as years of current expenses represented by net worth.
9. **Реальная доходность капитала (Real Return):** Fisher equation `r = (1 + R) / (1 + i) - 1`, accounting for inflation drag.

### 3. Набранные баллы и аудит (Scores & Auditor Opinion)
- **Financial Health Score:** Итоговый балл от **0 до 10**: КФС 45%, норма сбережений 25%, резерв и долговая нагрузка по 15%. / Overall score from **0 to 10**: CFS 45%, savings rate 25%, emergency-fund runway and debt ratio 15% each.
- **Component Progress Indicators:** Individual ratings for each key pillar.
- **Automated Auditor Opinion:** Qualitative analysis classifying indicators into **Positive (🟢)**, **Neutral (🟡)**, and **Risk/Negative (🔴)** zones.
- **Shareable Reports:** Generate and share the financial summary and auditor opinion using Android's share menu.

### 4. Финансовая свобода и симулятор «Что если?» (FIRE & Pension Simulator)
- **Метод FIRE:** Прогноз накопления и расходования капитала до указанного возраста дожития при постоянной реальной доходности. Целевой капитал рассчитывается по приведенной стоимости будущих расходов; правило 4% и Trinity Study не используются.
- **Стартовый капитал FIRE:** `max(0, ликвидные активы + финансовые активы + min(0, имущество - долги))`. Имущество не включается в инвестируемый капитал; долг вычитается только в части сверх стоимости имущества. Переключатель «Учитывать имущество и долги в расчёте FIRE» в текущем интерфейсе отсутствует и на расчёт не влияет.
- **Целевой капитал:** Приведённая стоимость будущих расходов до заданного возраста дожития с поправкой на реальную доходность. Сценарии могут менять временные и будущие расходы и добавлять разовые поступления; при наличии финансовых активов может применяться оценка налога на прирост капитала.
- **Срок до FIRE:** Модель ищет момент, когда прогноз накопленного капитала достигает требуемой суммы. Результат зависит от текущего капитала, денежного потока, реальной доходности, расходов и горизонта планирования.
- **Симуляция:** Ползунки и переключатели меняют отдельный расчёт «Что если?»; исходные введённые данные не переписываются. Состояние симуляции сохраняется локально и сбрасывается кнопкой сброса.
- **Three Sliders:** Adjust the monthly savings/income level, monthly expenses, and real investment return for the hypothetical projection.
- **14 Built-in Scenarios:**
  - *Positive:* income +20%; financial assets +25%; expenses −15%; cost of living −30%; a one-off inheritance of 12 monthly incomes; property sale proceeds capped at 20 monthly incomes and property value; expenses −25% after 18 years.
  - *Stress tests:* one-off reserve draw equal to 12 monthly incomes for job loss; financial assets −30%; mortgage payments of 35% of monthly income for 20 years; car payments of 20% of monthly income for 5 years; one-off expense of 5 monthly incomes; expenses +25% (scenario label says inflation); child/dependent costs +25% of expenses for 18 years.
- **Interactive Trajectory Chart:** Compare projected capital with required capital through the selected life-expectancy horizon.

---

## 🌐 Languages & Localization / Языки и локализация

- **Application Languages:**
  - 🇷🇺 **Russian (Русский)** — default for `ru`, `be`, `uk` locales.
  - 🇬🇧 **English** — default for all other system locales.
  - *Detection:* System locale is checked on first launch; the selected language is stored locally.
- **Currency symbols:** $, €, ₽, ₴, ₸, Br, ֏, £, ¥, CHF, C$, A$, ₹. Values are not converted between currencies.

---

## 📋 Requirements / Системные требования

| Parameter | Minimum Requirement | Recommended |
| :--- | :--- | :--- |
| **Android OS** | Android 7.0 (API level 24, Nougat) | Android 12+ (API level 31+) |
| **Target SDK** | Android 16 (API level 36) | API level 36 |
| **RAM** | 2 GB | 4 GB+ |
| **Storage** | ~25 MB free space | 50 MB |
| **Internet permission** | Not requested by the app | — |
| **Architecture** | `arm64-v8a`, `armeabi-v7a`, `x86_64` | `arm64-v8a` |
| **Build Tools** | JDK 11+, Gradle 8.x+, Kotlin 2.2+ | JDK 17, Gradle 9.x |

---

## 🏗 Technical Notes & Architecture / Техническая архитектура

```
app/src/main/java/com/example/
├── data/
│   ├── db/                 # Room SQLite Database & Scenario DAO
│   ├── model/              # FinancialInputData, CalculatedMetrics, TrajectoryPoint
│   └── repository/         # LocalPreferencesManager (SharedPreferences), FinanceRepository
├── domain/                 # FinanceCalculator, FinancialMetricEvaluator, AuditorAnalysisGenerator
├── ui/
│   ├── components/         # MetricCard, ScoreGauge, FireTrajectoryChart, ScenarioTogglesPanel
│   ├── screens/            # InputDataScreen, MetricsScreen, AuditScoreScreen, FireRetirementScreen
│   ├── theme/              # Color, Theme, Type (Material 3 palette)
│   ├── FinanceApp.kt       # Root Scaffold, TopAppBar & NavigationBar
│   └── MainViewModel.kt    # MVVM StateFlow Coordinator & Real-Time Calculation Engine
└── util/
    ├── Formatters.kt       # Currency, ratio, percentage & score number formatters
    └── localization/       # AppLanguage, AppStrings, StringsRu, StringsEn
```

### Architecture Highlights
- **Clean Architecture / MVVM:** Presentation layer strictly separated from financial domain calculations.
- **Reactive State Management:** Kotlin Coroutines and `MutableStateFlow` / `StateFlow` with `collectAsStateWithLifecycle`.
- **No app network permission:** The manifest does not request internet access. Android's share sheet can hand a report to an app selected by the user.
- **Adaptive UI:** Responsive layout scaling from compact smartphones to foldables and tablets via Jetpack Compose container-based constraints.

---

## 🧪 Tests & Quality Assurance / Тестирование

The project includes automated JVM unit and Robolectric integration test suites:
- **Formula Accuracy Tests:** Precision validation of Cash Flow, Savings Rate, Net Worth, Fisher Real Return, and Fidelity multiplier steps.
- **First-Launch & Persistence Tests:** Verification that default benchmarks are applied only on first launch, while user modifications (inputs, toggles, language, active tab) persist across restarts.
- **Stress-Test Recalculations:** Automated testing of dynamic simulator scale bounds and scenario stacking.
- **Localization & Disclaimer Assertions:** Unit tests guaranteeing accuracy of legal disclosures in all locales.

### Running Tests Locally
```bash
# Run all unit and Robolectric tests
./gradlew testDebugUnitTest

# Run specific test suite
./gradlew testDebugUnitTest --tests "com.example.ExampleRobolectricTest"
```

---

## 📦 Building & Installation / Сборка проекта

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
```

### 2. Build Debug APK
```bash
./gradlew assembleDebug
```
The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`.

### 3. Build Release APK
```bash
./gradlew assembleRelease
```
The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`.

---

## 🏪 Distribution / Публикация в каталогах

Подготовлены все необходимые файлы конфигураций и пошаговые инструкции для публикации:
- 🤖 **[F-Droid Inclusion Guide (Официальный каталог F-Droid)](./FDROID_SETUP.md):** Манифест сборки `metadata/com.aistudio.finhealthfire.kzqpv.yml`, полная инструкция по созданию Merge Request в `fdroiddata` на GitLab.
- 📦 **[IzzyOnDroid Distribution Guide](./IZZYON_DROID_SETUP.md):** Публикация готовых APK через GitHub Releases и авто-трекинг репозитория.
- 🚀 **[GitHub Actions CI/CD Workflow](./.github/workflows/build-and-release.yml):** Автоматическая сборка подписанного релизного APK при публикации Git-тегов.
- 📁 **Fastlane Metadata:** Локализованные метаданные `fastlane/metadata/android/` (`ru-RU`, `en-US`), иконки и описания для каталогов.
- 📄 **OSI-Approved License:** [MIT License](./LICENSE) в корне проекта.

---

## ⚠️ Отказ от отвествености / Developer Disclaimer

### Русский
> **Заголовок:** Отказ от отвествености  
> **Важно:** Проект является персональной разработкой и предоставляется исключительно в ознакомительных целях «как есть». Автор не несет ответственности за возможные сбои, потерю данных или иной ущерб, возникший в ходе использования данного ПО. Все расчеты, формулы, метрики и симуляции носят исключительно информационно-ознакомительный характер и не являются индивидуальной финансовой, инвестиционной или налоговой рекомендацией. Вы используете этот инструмент на свой собственный страх и риск.
> 
> *Примечание:* Данное приложение не связано с компанией Fidelity Investments и использует исключительно открытые исследовательские ориентиры и общедоступную финансовую методологию.

### English
> **Title:** Disclaimer  
> **Important:** This project is a personal development and is provided solely for informational purposes "as is". The author is not responsible for any malfunctions, data loss, or other damages arising from the use of this software. All calculations, formulas, metrics, and simulations are for informational purposes only and do not constitute individual financial, investment, or tax advice. You use this tool at your own risk.
> 
> *Note:* This application is not affiliated with or endorsed by Fidelity Investments and solely uses publicly available research milestones and educational benchmarks.

---

## 📄 License / Лицензия

This project is open source and available under the terms of the **[MIT License](./LICENSE)**.

