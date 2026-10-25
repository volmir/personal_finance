# Personal finance and FIRE (Личные финансы & FIRE аудит)

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24%2B_%28Android_7.0%29-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2%2B-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Privacy](https://img.shields.io/badge/Privacy-100%25_Offline_|_Zero_Tracking-brightgreen.svg)](https://exodus-privacy.eu.org/)

---

## 📖 App Description / Описание приложения

**Personal Finance & FIRE Audit** is an open-source, offline-first personal wealth management and early retirement modeling application built natively for Android using modern Jetpack Compose and Material 3.

The app serves as an objective, institutional-grade diagnostic tool for individuals who want to audit their current financial health, calculate key wealth metrics, stress-test their household budget against unexpected economic shocks, and model their path to financial independence and early retirement using the **FIRE (Financial Independence, Retire Early)** methodology.

### 🌟 Core Philosophy
- **100% Offline & Private:** No network permissions requested (`android.permission.INTERNET` is omitted). No servers, no telemetry, no tracking, and no bank credential requests. All calculations are executed strictly on your local device.
- **Data Persistence:** Default US benchmark values are assigned **only on the very first launch**. After initialization, all user inputs, settings, scenario toggles, and simulation slider positions are preserved across app restarts.
- **Transparent Economics:** Real math based on empirical finance (Fisher equation, Fidelity retirement milestones, Trinity study / 4% safe withdrawal rule).

---

## ✨ Features / Возможности

The application is structured into 4 cohesive functional tabs:

### 1. Данные для расчетов (Input Data & Budgeting)
- **Income & Expenses:** Gross monthly income and living expense tracking with expandable category breakdown hints.
- **Three-Tier Asset Allocation:**
  - *Liquid Assets (Подушка):* Cash, high-yield checking, bank deposits.
  - *Financial Assets (Инвестиции):* Stock portfolios, index funds/ETFs, bonds, retirement accounts.
  - *Property Assets (Недвижимость и имущество):* Primary residence, vehicles, real estate equity.
- **Comprehensive Liabilities:** Mortgages, car loans, consumer loans, and credit balances.
- **Emergency Buffer Config:** User-toggleable inclusion of financial assets into emergency reserve calculations.
- **Demographics & Investment Parameters:** Birth date, horizon planning, life expectancy, nominal expected yield, and inflation rate.
- **Dynamic Cash Flow & Savings Rate Preview:** Real-time feedback directly on the input cards.

### 2. Расчет показателей (9 Core Financial Metrics)
1. **Денежный поток (Cash Flow):** `Monthly Income - Monthly Expenses` (net monthly surplus/deficit).
2. **Норма сбережений (Savings Rate):** `(Cash Flow / Income) * 100%`.
3. **Подушка безопасности (Emergency Fund):** Total crisis buffer volume with dynamic color-coding.
4. **Запас месячных расходов (Safety Months):** Runway duration in months of living expenses.
5. **Коэффициент задолженности (Debt Ratio):** `Liabilities / Total Assets`.
6. **Чистый капитал (Net Worth):** `Total Assets - Total Liabilities`.
7. **Бенчмарк Fidelity:** Age-based milestone target (`Annual Income * Age Multiplier`, from 1x at 30 to 10x at 67).
8. **Коэффициент финансовой свободы (КФС / CFS):** `Net Assets / Annual Expenses` with life-stage interpretations.
9. **Реальная доходность капитала (Real Return):** Fisher equation `r = (1 + R) / (1 + i) - 1`, accounting for inflation drag.

### 3. Набранные баллы и аудит (Scores & Auditor Opinion)
- **Financial Health Score:** Weighted composite scoring from **0 to 10 points** across savings rate, emergency reserve, debt load, and capital autonomy.
- **Component Progress Indicators:** Individual ratings for each key pillar.
- **Automated Auditor Opinion:** Full qualitative analysis classifying indicators into **Positive (🟢)**, **Neutral (🟡)**, and **Risk/Negative (🔴)** zones.
- **One-Click Export:** Generate and share complete textual audit reports to clipboard, messaging apps, or email.

### 4. Финансовая свобода и симулятор «Что если?» (FIRE & Pension Simulator)
- **Dynamic Retirement Age Calculation:** Automatic calculation of target FIRE nest egg and exact age of financial independence based on the 4% safe withdrawal rate.
- **Interactive Tuning Sliders:**
  - Additional monthly savings slider.
  - Real portfolio yield adjustment slider.
  - Living expense reduction slider.
- **14 Stress-Test & Life Event Scenarios:**
  - *Positive:* Salary raise (+20%), market boom (+25%), expense optimization (-15%), geo-arbitrage relocation (-30%), inheritance (+1200% monthly income), property liquidation (+2000%), children reaching adulthood (-25% expenses).
  - *Negative:* Job loss (income drops to 0), market crash (-30% financial assets), 20-year mortgage (35% income), car loan (+20% expenses for 5 yrs), major one-off expense (500% monthly income), inflation surge (+5%), child birth (+25% expenses for 18 yrs).
- **Interactive Trajectory Chart:** Visual capital accumulation and decumulation curve projected year-by-year until life expectancy.

---

## 🌐 Languages & Localization / Языки и локализация

- **Application Languages:**
  - 🇷🇺 **Russian (Русский)** — default for `ru`, `be`, `uk` locales.
  - 🇬🇧 **English** — default for all other international system locales.
  - *Detection:* Fully automatic background system locale detection on first launch, persisted locally.
- **Supported Currency Symbols:**
  - **$** (US Dollar) — default benchmark currency
  - **€** (Euro)
  - **₽** (Russian Ruble)
  - **₴** (Ukrainian Hryvnia)
  - **₸** (Kazakhstani Tenge)
  - **Br** (Belarusian Ruble)
  - **֏** (Armenian Dram)

---

## 📋 Requirements / Системные требования

| Parameter | Minimum Requirement | Recommended |
| :--- | :--- | :--- |
| **Android OS** | Android 7.0 (API level 24, Nougat) | Android 12+ (API level 31+) |
| **Target SDK** | Android 16 (API level 36) | API level 36 |
| **RAM** | 2 GB | 4 GB+ |
| **Storage** | ~25 MB free space | 50 MB |
| **Internet** | **Not required** (100% offline) | — |
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
- **Zero Third-Party Tracking SDKs:** Verified clean with Exodus Privacy (0 trackers, 0 permissions).
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

## 🏪 F-Droid / IzzyOnDroid Compatibility

The repository includes pre-configured **Fastlane** metadata structure compatible with F-Droid and IzzyOnDroid:
- Metadata path: `fastlane/metadata/android/`
  - `en-US/`: `title.txt`, `short_description.txt`, `full_description.txt`, `changelogs/1.txt`
  - `ru-RU/`: `title.txt`, `short_description.txt`, `full_description.txt`, `changelogs/1.txt`
- OSI-Approved License: `LICENSE` (MIT) included in project root.

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

