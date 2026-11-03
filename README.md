# Personal Finance & FIRE Audit

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24%2B_%28Android_7.0%29-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2%2B-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Offline](https://img.shields.io/badge/Network-No_Internet_Permission-brightgreen.svg)](https://developer.android.com/guide/topics/manifest/uses-permission-element)

---

## 📲 Installation

- Download the signed APK from [GitHub Releases](https://github.com/volmir/personal_finance/releases).
- For update tracking with [Obtainium](https://github.com/ImranR98/Obtainium), add `https://github.com/volmir/personal_finance` as the app source. Obtainium checks GitHub Releases for updates; it does not require a store listing.
- Signing certificate SHA-256: `D6:7A:62:A2:33:C2:66:62:26:99:25:95:5C:65:34:64:82:0B:F1:21:E1:55:CA:82:73:59:C3:B9:9D:9B:66:09`. See [SIGNING_CERTIFICATE.txt](https://github.com/volmir/personal_finance/releases/latest/download/SIGNING_CERTIFICATE.txt) for certificate details. Each release also includes `SHA256SUMS.txt` for checking the downloaded APK.
- The app works offline and does not request the internet permission.

Direct link for devices with Obtainium installed:

[![Add to Obtainium](https://img.shields.io/badge/Obtainium-Add_app-536DFE.svg)](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22io.github.volmir.personalfinance%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fvolmir%2Fpersonal_finance%22%2C%22author%22%3A%22volmir%22%2C%22name%22%3A%22Personal%20Finance%20%26%20FIRE%20Audit%22%7D)

```text
obtainium://add/https://github.com/volmir/personal_finance
```

See the [Obtainium deep-link documentation](https://wiki.obtainium.imranr.dev/deep_links/) for supported link formats.

---

## 📖 App Description

A complete description of the screens, fields, FIRE formulas, and model limitations is available in [APP_DESCRIPTION.md](./APP_DESCRIPTION.md).

**Personal Finance & FIRE Audit** is an open-source, offline-first personal wealth management and early retirement modeling application built natively for Android using modern Jetpack Compose and Material 3.

The app helps users record a household budget and balance sheet, review calculated financial indicators, generate an explanatory audit, and explore a FIRE retirement projection with adjustable assumptions and hypothetical scenarios.

### 🌟 Core Philosophy
- **Works offline:** The app manifest does not request `android.permission.INTERNET`; calculations and saved financial data stay on the device. There is no account sign-in or bank connection.
- **Local persistence:** User inputs and simulator state are stored locally. The initial form is prefilled with sample US-average values; these are examples, not personalized or region-specific defaults.
- **Transparent assumptions:** Metrics use explicit formulas, including the Fisher equation for real return, age-based Fidelity milestones, and a FIRE projection through the selected life-expectancy horizon. The projection does not use the 4% rule or Trinity Study.

---

## ✨ Features

The application is structured into 4 cohesive functional tabs:

### 1. Input Data & Budgeting
- **Income & Expenses:** Monthly income and expenses with expandable category guidance.
- **Three Asset Types:**
  - *Liquid Assets:* Cash, high-yield checking, bank deposits.
  - *Financial Assets:* Stock portfolios, index funds/ETFs, bonds, retirement accounts.
  - *Property Assets:* Primary residence, vehicles, real estate equity.
- **Liabilities:** Enter a total for mortgages, car loans, consumer loans, credit balances, or other obligations.
- **Emergency Fund Setting:** Choose whether financial assets are included with liquid assets when calculating the emergency reserve.
- **Demographics & Investment Parameters:** Birth date, life expectancy, expected nominal return, inflation, and capital-gains tax assumption.
- **Currency Display:** Choose among USD, EUR, RUB, UAH, KZT, BYN, AMD, GBP, JPY, CHF, CAD, AUD, and INR symbols. This changes displayed units; it does not convert amounts or fetch exchange rates.
- **Dynamic Cash Flow & Savings Rate Preview:** Real-time feedback directly on the input cards.

### 2. 9 Core Financial Metrics
1. **Cash Flow:** `Monthly Income - Monthly Expenses` (net monthly surplus/deficit).
2. **Savings Rate:** `(Cash Flow / Income) * 100%` (budget surplus as a share of income).
3. **Emergency Fund:** Total crisis buffer volume with dynamic color-coding.
4. **Safety Months:** Runway duration in months of living expenses.
5. **Debt Ratio:** `Liabilities / Total Assets`.
6. **Net Worth:** `Total Assets - Total Liabilities`.
7. **Fidelity Benchmark:** Age-based milestone target (`Annual Income * Age Multiplier`), interpolated between age checkpoints.
8. **Financial Freedom Ratio (CFS):** `Net Worth / Annual Expenses`, shown as years of current expenses represented by net worth.
9. **Real Return:** Fisher equation `r = (1 + R) / (1 + i) - 1`, accounting for inflation drag.

### 3. Scores & Auditor Opinion
- **Financial Health Score:** Overall score from **0 to 10**: CFS 45%, savings rate 25%, emergency-fund runway and debt ratio 15% each.
- **Component Progress Indicators:** Individual ratings for each key pillar.
- **Automated Auditor Opinion:** Qualitative analysis classifying indicators into **Positive (🟢)**, **Neutral (🟡)**, and **Risk/Negative (🔴)** zones.
- **Shareable Reports:** Generate and share the financial summary and auditor opinion using Android's share menu.

### 4. FIRE & Pension Simulator
- **FIRE Method:** Projects capital accumulation and drawdown through the selected life-expectancy age at a constant real return. Target capital is calculated as the present value of future expenses; the 4% rule and Trinity Study are not used.
- **Starting FIRE Capital:** `max(0, liquid assets + financial assets + min(0, property - liabilities))`. Property is excluded from investable capital; liabilities are deducted only to the extent that they exceed property value. The "Include property and liabilities in FIRE calculations" toggle is absent from the current interface and has no effect on the calculation.
- **Target Capital:** The present value of future expenses through the selected life-expectancy age, adjusted for real return. Scenarios can change temporary and future expenses and add one-off income; an estimated capital-gains tax may apply when financial assets are present.
- **Time to FIRE:** The model finds the point when projected accumulated capital reaches the required amount. The result depends on current capital, cash flow, real return, expenses, and the planning horizon.
- **Simulation:** Sliders and toggles change a separate "What if?" calculation without overwriting the original inputs. Simulator state is saved locally and can be cleared with the reset button.
- **Three Sliders:** Adjust the monthly savings/income level, monthly expenses, and real investment return for the hypothetical projection.
- **14 Built-in Scenarios:**
  - *Positive:* income +20%; financial assets +25%; expenses −15%; cost of living −30%; a one-off inheritance of 12 monthly incomes; property sale proceeds capped at 20 monthly incomes and property value; expenses −25% after 18 years.
  - *Stress tests:* one-off reserve draw equal to 12 monthly incomes for job loss; financial assets −30%; mortgage payments of 35% of monthly income for 20 years; car payments of 20% of monthly income for 5 years; one-off expense of 5 monthly incomes; expenses +25% (scenario label says inflation); child/dependent costs +25% of expenses for 18 years.
- **Interactive Trajectory Chart:** Compare projected capital with required capital through the selected life-expectancy horizon.

---

## 🌐 Languages & Localization

- **Application Languages (6 supported languages):**
  - 🇬🇧 **English** — default language for all other system locales.
  - 🇷🇺 **Russian** — automatically selected for `ru` and `be` locales.
  - 🇩🇪 **German** — automatically selected for `de` locales.
  - 🇫🇷 **French** — automatically selected for `fr` locales.
  - 🇮🇹 **Italian** — automatically selected for `it` locales.
  - 🇪🇸 **Spanish** — automatically selected for `es` locales.
  - *Detection:* System locale is checked on first launch; the selected language is stored locally.
- **Currency symbols:** `$`, `€`, `₽`, `₴`, `₸`, `Br`, `֏`, `£`, `¥`, `CHF`, `C$`, `A$`, `₹`. Values are not converted between currencies.

---

## 📋 Requirements

| Parameter | Minimum Requirement | Recommended |
| :--- | :--- | :--- |
| **Android OS** | Android 7.0 (API level 24, Nougat) | Android 12+ (API level 31+) |
| **Target SDK** | Android 16 (API level 36) | API level 36 |
| **RAM** | 2 GB | 4 GB+ |
| **Storage** | ~25 MB free space | 50 MB |
| **Internet permission** | Not requested by the app | — |
| **Architecture** | `arm64-v8a`, `armeabi-v7a`, `x86_64` | `arm64-v8a` |
| **Build Tools** | JDK 17, Gradle 9.x, Kotlin 2.2+ | JDK 17, Gradle 9.3.1 (Gradle Wrapper) |

---

## 🏗 Technical Notes & Architecture

```
app/src/main/java/com/example/
├── data/
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
    └── localization/       # AppLanguage, AppStrings, StringsRu, StringsEn, StringsDe, StringsFr, StringsIt, StringsEs
```

### Architecture Highlights
- **Clean Architecture / MVVM:** Presentation layer strictly separated from financial domain calculations.
- **Reactive State Management:** Kotlin Coroutines and `MutableStateFlow` / `StateFlow` with `collectAsStateWithLifecycle`.
- **No app network permission:** The manifest does not request internet access. Android's share sheet can hand a report to an app selected by the user.
- **Adaptive UI:** Responsive layout scaling from compact smartphones to foldables and tablets via Jetpack Compose container-based constraints.

---

## 🧪 Tests & Quality Assurance

The project includes automated JVM unit and Robolectric integration test suites:
- **Formula Accuracy Tests:** Precision validation of Cash Flow, Savings Rate, Net Worth, Fisher Real Return, and Fidelity multiplier steps.
- **First-Launch & Persistence Tests:** Verification that default benchmarks are applied only on first launch, while user modifications (inputs, toggles, language, active tab) persist across restarts.
- **Stress-Test Recalculations:** Automated testing of dynamic simulator scale bounds and scenario stacking.
- **Localization & Disclaimer Assertions:** Unit tests guaranteeing accuracy of legal disclosures in all locales.

### Running Tests Locally

JDK 17 is used for builds, JVM tests, Robolectric tests, and screenshot generation. The shared Robolectric configuration in `app/src/test/resources/robolectric.properties` specifies Android 15 (API 35); the formatter test retains coverage on API 28. The tests do not emulate Android 16 (API 36), for which Robolectric requires JDK 21 ([Robolectric compatibility](https://robolectric.org/compatibility_table/)). The app's `compileSdk` and `targetSdk` values remain unchanged.

```bash
# Run all unit and Robolectric tests
bash ./gradlew testDebugUnitTest

# Run specific test suite
bash ./gradlew testDebugUnitTest --tests "com.example.MainActivityRobolectricTest"
```

---

## 📦 Building & Installation

### 1. Clone the repository
```bash
git clone https://github.com/volmir/personal_finance.git
cd personal_finance
```

### 2. Build Debug APK
```bash
bash ./gradlew assembleDebug
```
The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`.

### 3. Build Release APK
```bash
bash ./gradlew assembleRelease
```
With `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, and `KEY_ALIAS` set for your release keystore, the signed APK will be generated at `app/build/outputs/apk/release/app-release.apk`. Without signing configuration, Gradle produces `app-release-unsigned.apk`.

---

## 🏪 Distribution

Distribution uses signed GitHub Releases, with Obtainium available for update tracking:

- 📦 **[GitHub Releases](https://github.com/volmir/personal_finance/releases):** Signed APKs, APK checksums, and public signing certificate information.
- 🤖 **[F-Droid Inclusion Guide](./FDROID_SETUP.md):** Prepare and test the build recipe in advance. Submit to the official repository no earlier than **January 10, 2027**, after three months of active development since October 10, 2026, unless a documented policy exception applies. Inclusion remains subject to review.
- 🚀 **[GitHub Actions CI/CD Workflow](./.github/workflows/build-and-release.yml):** Builds the release APK on version tags, verifies its signature, and publishes it with APK checksums and public certificate information.
- 📁 **Fastlane Metadata:** The single source of store titles and descriptions is [fastlane/metadata/android](./fastlane/metadata/android): `title.txt`, `short_description.txt`, and `full_description.txt` for `ru-RU`, `en-US`, `de-DE`, `es-ES`, `fr-FR`, and `it-IT`. Release notes are in `changelogs/1.txt`, and screenshots are in `images/phoneScreenshots/` for each locale.
- 📄 **OSI-Approved License:** [MIT License](./LICENSE) in the project root.

For `v1.0`, a local rebuild in the official F-Droid buildserver container passed comparison with the published APK using `apksigcopier`. The [F-Droid guide](./FDROID_SETUP.md) records the verification and recipe for retaining the developer's signature. Official repository verification and inclusion are still pending.

F-Droid's [inclusion policy](https://f-droid.org/en/docs/Inclusion_Policy/) also allows distribution through a separate app repository. A self-hosted or third-party F-Droid repository is an optional future distribution channel.

Screenshots of the four tabs are generated from the app's actual Compose interface using Robolectric with the light theme. To regenerate them with JDK 17 and the Android SDK installed:

```bash
GENERATE_STORE_SCREENSHOTS=1 bash ./gradlew testDebugUnitTest --tests com.example.StoreScreenshotsTest --rerun-tasks
```

---

## ⚠️ Developer Disclaimer

> **Important:** This project is a personal development and is provided solely for informational purposes "as is". The author is not responsible for any malfunctions, data loss, or other damages arising from the use of this software. All calculations, formulas, metrics, and simulations are for informational purposes only and do not constitute individual financial, investment, or tax advice. You use this tool at your own risk.
> 
> *Note:* This application is not affiliated with or endorsed by Fidelity Investments and solely uses publicly available research milestones and educational benchmarks.

---

## 📄 License

This project is open source and available under the terms of the **[MIT License](./LICENSE)**.

The icon was adapted from the project's original image into a transparent PNG using image_gen; see the [source and adaptation details](./docs/icon-source.md). It is licensed under [MIT](./LICENSE).

