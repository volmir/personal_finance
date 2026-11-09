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
- Signing certificate SHA-256: `D6:7A:62:A2:33:C2:66:62:26:99:25:95:5C:65:34:64:82:0B:F1:21:E1:55:CA:82:73:59:C3:B9:9D:9B:66:09`. See [SIGNING_CERTIFICATE.txt](https://github.com/volmir/personal_finance/releases/latest/download/SIGNING_CERTIFICATE.txt) for certificate details. Each release also includes `SHA256SUMS.txt` for checking the downloaded APK.
- The app works offline and does not request the internet permission.

The published release is **[v1.0.1 / versionCode 2](https://github.com/volmir/personal_finance/releases/tag/v1.0.1)**, built from commit `b94949e09844450a126a16a8f73f50edd87cd64a`. Its checksum, signing certificate, version and release flags have been verified. See the dated [release verification report](./docs/release-readiness-1.0.1.md).

---

## 📖 App Description

A complete description of the screens, fields, FIRE formulas, and model limitations is available in [APP_DESCRIPTION.md](./APP_DESCRIPTION.md).

**Personal Finance & FIRE Audit** is an open-source Android calculator for a monthly budget, assets, debts, and a possible path to financial independence and early retirement (FIRE).

Enter monthly totals and current asset and debt balances manually. The app calculates nine financial indicators, generates an explanatory assessment, and models retirement under adjustable assumptions and hypothetical scenarios.

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
  - *Property Assets:* Full estimated value of a primary residence, vehicles, and other property. Enter debts separately, so they are deducted once.
- **Liabilities:** Enter a total for mortgages, car loans, consumer loans, credit balances, or other obligations.
- **Emergency Fund Setting:** Choose whether financial assets are included with liquid assets when calculating the emergency reserve.
- **Demographics & Investment Parameters:** Birth date, life expectancy, expected nominal return, inflation, and capital-gains tax assumption.
- **Currency Display:** Choose among USD, EUR, RUB, UAH, KZT, BYN, AMD, GBP, JPY, CHF, CAD, AUD, and INR symbols. This changes displayed units; it does not convert amounts or fetch exchange rates.
- **Precision:** Money uses two decimal places; yen amounts are displayed and entered as whole numbers. Input rounding follows the selected currency precision.
- **Dynamic Cash Flow & Savings Rate Preview:** Real-time feedback directly on the input cards.

### 2. 9 Core Financial Metrics
1. **Cash Flow:** `Monthly Income - Monthly Expenses` (net monthly surplus/deficit).
2. **Savings Rate:** `(Cash Flow / Income) * 100%` (budget surplus as a share of income).
3. **Emergency Fund:** Total crisis buffer volume with dynamic color-coding.
4. **Safety Months:** Runway duration in months of living expenses.
5. **Debt Ratio:** `Liabilities / Total Assets`.
6. **Net Worth:** `Total Assets - Total Liabilities`.
7. **Fidelity Benchmark:** Compares liquid and financial assets minus debts not covered by property against `Annual Income * Age Multiplier`. The multiplier is interpolated between age checkpoints; the app uses 0.5x up to age 25.
8. **Financial Freedom Ratio (CFS):** `Net Worth / Annual Expenses`, including property in net worth. It expresses capital in years of current expenses; readiness to live on capital income is assessed separately on the FIRE tab.
9. **Real Return:** Fisher equation `r = (1 + R) / (1 + i) - 1`, with the model's lower limit of −50%.

Cash flow and savings rate equal to zero are negative indicators. A zero net worth is neutral; positive net worth is positive when it represents at least 50% of total assets. Fidelity is unavailable when the birth date is invalid or its income-based target is zero. Further boundary rules are documented in [APP_DESCRIPTION.md](./APP_DESCRIPTION.md).

### 3. Scores & Auditor Opinion
- **Financial Health Score:** Overall score from **0 to 10**: CFS 45%, savings rate 25%, emergency-fund runway and debt ratio 15% each.
- **Component Progress Indicators:** Individual ratings for each key pillar.
- **Automated Auditor Opinion:** Qualitative analysis classifying indicators into **Positive (🟢)**, **Neutral (🟡)**, and **Risk/Negative (🔴)** zones.
- **Shareable Reports:** Generate and share the financial summary and auditor opinion using Android's share menu.
- **Score Rules:** The total is rounded to one decimal place before its status is selected. A neutral CFS can coexist with a green total score; the 6.0 cap applies only when the CFS component score is below 5.

### 4. FIRE & Pension Simulator
- **FIRE Method:** Projects capital accumulation and drawdown through the selected life-expectancy age at a constant real return. Target capital is calculated as the present value of future expenses; the 4% rule and Trinity Study are not used.
- **Starting FIRE Capital:** `max(0, liquid assets + financial assets - max(0, liabilities - property))`. It uses the same capital formula as Fidelity, with a zero floor for FIRE. Property is excluded from investable capital; liabilities are deducted only to the extent that they exceed property value.
- **Target Capital:** The present value of future expenses through the selected life-expectancy age, adjusted for real return. Scenarios can change temporary and future expenses and add one-off income. Capital-gains tax is estimated in a simplified way only for the financial-assets share, and only when financial assets are present.
- **Time to FIRE:** The model finds the point when projected accumulated capital reaches the required amount. The result depends on current capital, cash flow, real return, expenses, and the planning horizon.
- **Simulation:** Sliders and toggles change a separate "What if?" calculation without overwriting the original inputs. Simulator state is saved locally and can be cleared with the reset button.
- **Three Sliders:** Adjust the monthly savings/income level, monthly expenses, and real investment return for the hypothetical projection.
- **14 Built-in Scenarios:**
  - *Positive:* income +20%; financial assets +25%; expenses −15%; cost of living −30%; a one-off inheritance of 12 monthly incomes; property sale proceeds capped at 20 monthly incomes and property value; expenses −25% after 18 years.
  - *Stress tests:* one-off reserve draw equal to 12 current monthly incomes for job loss; financial assets −30%; mortgage payments of 35% of monthly income for 20 years; car payments of 20% of monthly income for 5 years; one-off expense of 5 monthly incomes; expenses +25% (scenario label says inflation); child/dependent costs +25% of expenses for 18 years.
- **Scenario Income Bases:** Job-loss reserves and mortgage/car payments include the salary-increase scenario. Inheritance, property sales and large one-off expenses use the original monthly income.
- **Interactive Trajectory Chart:** Compare projected capital with required capital through the selected life-expectancy horizon.

The projection assumes constant return and inflation. Other taxes, market-event ordering, and income inflation are excluded. Results are estimates under the entered assumptions, not forecasts or guarantees. The Audit and exported reports always use the base inputs, while simulator changes affect the FIRE tab.

---

## 🌐 Languages & Localization

- **Application Languages (6 supported languages):**
  - 🇬🇧 **English** — default language for all other system locales.
  - 🇷🇺 **Russian** — automatically selected for `ru` and `be` locales.
  - 🇩🇪 **German** — automatically selected for `de` locales.
  - 🇫🇷 **French** — automatically selected for `fr` locales.
  - 🇮🇹 **Italian** — automatically selected for `it` locales.
  - 🇪🇸 **Spanish** — automatically selected for `es` locales.
  - *Detection:* The interface follows the current Android system language when opened and when the locale configuration changes. The detected language is saved locally.
- **Currency symbols:** `$`, `€`, `₽`, `₴`, `₸`, `Br`, `֏`, `£`, `¥`, `CHF`, `C$`, `A$`, `₹`. Values are not converted between currencies.

---

## 📋 Requirements

| Parameter | Current configuration / verification |
| :--- | :--- |
| **Minimum Android** | Android 7.0 (API 24) |
| **Target / compile SDK** | API 36 / SDK 36.1 |
| **Release APK native libraries** | `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64` |
| **Runtime checks, 2026-11-06** | AOSP x86_64 emulators on API 24 and 36: installation, cold start, four tabs, and resume |
| **Internet permission** | Not requested |
| **Build tools** | Gradle Wrapper 9.3.1, AGP 9.1.1, Kotlin 2.2.10; JDK 17 in CI, JDK 21 used for local verification |

Hardware RAM and installed-size requirements have not been benchmarked. Emulator checks and their exact scope are recorded in the [verification report](./docs/release-readiness-1.0.1.md).

---

## 🏗 Technical Notes & Architecture

```
app/src/main/java/com/example/
├── data/
│   ├── model/              # FinancialInputData, CalculatedMetrics, TrajectoryPoint
│   └── repository/         # LocalPreferencesManager (SharedPreferences)
├── domain/                 # Calculator, evaluator, projection engine, scales, scenarios, audit
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
- **MVVM:** Financial calculations live in `domain/`; screens read input, metrics, and simulation state from `MainViewModel`.
- **Reactive State Management:** Kotlin Coroutines and `MutableStateFlow` / `StateFlow` with `collectAsStateWithLifecycle`.
- **No app network permission:** The manifest does not request internet access. Android's share sheet can hand a report to an app selected by the user.
- **Compose UI:** Four scrollable screens, reusable metric and scenario components, and a capital trajectory chart.

---

## 🧪 Tests & Quality Assurance

The project includes automated JVM unit and Robolectric integration test suites:
- **Formula Accuracy Tests:** Precision validation of Cash Flow, Savings Rate, Net Worth, Fisher Real Return, and Fidelity multiplier steps.
- **First-Launch & Persistence Tests:** Demonstration defaults, restored input and simulator state, numeric validation, active-tab persistence, and recalculation after resume.
- **Stress-Test Recalculations:** Automated testing of dynamic simulator scale bounds and scenario stacking.
- **Localization & Text Assertions:** Formatting, plural forms, model assumptions, and audit icon/status consistency across all six languages.

### Running Tests Locally

CI is configured for JDK 17. The latest local JVM/Robolectric checks and screenshot generation used JDK 21. The shared Robolectric configuration in `app/src/test/resources/robolectric.properties` specifies API 35; localization/formatting suites also run on API 28. Separate emulator checks covered API 24 and 36. See the [dated results](./docs/release-readiness-1.0.1.md) for the tested snapshot and limits of those checks.

```bash
# Run all unit and Robolectric tests
bash ./gradlew testDebugUnitTest

# Run specific test suite
bash ./gradlew testDebugUnitTest --tests "com.example.MainActivityRobolectricTest"
```

Use `--offline` when dependencies are already cached. A fresh build needs the Android SDK and network access to download build dependencies; the installed app works offline.

---

## 📦 Building & Installation

Install Android SDK Platform `android-36.1` and Build Tools `36.0.0`, and select JDK 17 (CI) or JDK 21 (used for local verification). Set `ANDROID_HOME` or a local `sdk.dir` entry in `local.properties`; keep that file and signing keys out of Git. Use the checked-in Gradle Wrapper.

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
bash ./gradlew lintRelease assembleRelease
```
With `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, and `KEY_ALIAS` set for your release keystore, the signed APK will be generated at `app/build/outputs/apk/release/app-release.apk`. Without signing configuration, Gradle produces `app-release-unsigned.apk`.

---

## 🏪 Distribution

Distribution uses signed GitHub Releases:

- 📦 **[GitHub Releases](https://github.com/volmir/personal_finance/releases):** Signed APKs, APK checksums, and public signing certificate information.
- 🤖 **[F-Droid Inclusion Guide](./FDROID_SETUP.md):** Prepare and test the build recipe in advance. Submit to the official repository no earlier than **January 10, 2027**, after three months of active development since October 10, 2026, unless a documented policy exception applies. Inclusion remains subject to review.
- 🚀 **[GitHub Actions CI/CD Workflow](./.github/workflows/build-and-release.yml):** Builds the release APK on version tags, verifies its signature, and publishes it with APK checksums and public certificate information.
- 📁 **Fastlane Metadata:** The single source of store titles and descriptions is [fastlane/metadata/android](./fastlane/metadata/android): `title.txt`, `short_description.txt`, and `full_description.txt` for `ru-RU`, `en-US`, `de-DE`, `es-ES`, `fr-FR`, and `it-IT`. Historical `v1.0` release notes remain in `changelogs/1.txt`; release notes for `v1.0.1` (`versionCode = 2`) are in `changelogs/2.txt`. Screenshots are in `images/phoneScreenshots/` for each locale. The published app version is `1.0.1 / 2`. Future release tags require an explicit author request; published tags are never moved.
- 📄 **OSI-Approved License:** [MIT License](./LICENSE) in the project root.

For `v1.0.1`, `fdroid lint` and a local build in the official F-Droid buildserver container passed; the Binaries check verified reproducibility against the published signed APK. For `v1.0`, a local rebuild in the official F-Droid buildserver container passed comparison with the published APK using `apksigcopier`. The [F-Droid guide](./FDROID_SETUP.md) records the verification and recipe for retaining the developer's signature. Official repository verification and inclusion are still pending.

The development snapshot recorded on 2026-11-06 built with SDK 36.1 in the official F-Droid container. This is a local compatibility check, not a build on F-Droid's production infrastructure or proof that the next signed release is reproducible. `lintRelease` completed with 0 errors and 38 warnings; see the [verification report](./docs/release-readiness-1.0.1.md).

The store includes 24 screenshots: four tabs in each of the six languages, at 1080×2400. They are generated from the actual Compose interface using Robolectric and the light theme. The last regeneration used JDK 21 and the Android SDK:

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

Icon provenance, adaptation, and licensing notes are recorded in [docs/icon-source.md](./docs/icon-source.md).

