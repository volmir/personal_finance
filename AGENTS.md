# AGENTS.md

Personal Finance & FIRE Audit — offline Android app (Kotlin, Jetpack Compose, MVVM). Read this file fully before touching code; it replaces exploring the repo from scratch.

## 0. Working rules

- Reply to the user in **Russian**, briefly. Code identifiers and commit messages stay in English.
- Current phase: **release hardening for v1.0**. Fix defects and logical inconsistencies only. **No new features, no behavior changes beyond the fix, no refactors that are not needed for the fix.** If a fix would change a user-visible number, say so before applying it.
- Minimal diffs: do not reformat, rename, or reorder untouched code. One logical change per commit.
- Never silently edit a test expectation to make a test pass. If an expectation must change, state which value changed, by how much, and why.
- Every bug fix ships with a regression test (boundary values for formulas).
- If you cannot run Gradle (no network / no SDK), say so explicitly. Do static review, and for math verify with a small throwaway script. Never claim tests passed when they were not run.
- If something is ambiguous or contradicts section 2, ask one short question instead of guessing.

## 1. Facts

| Item | Value |
|---|---|
| applicationId | `io.github.volmir.personalfinance` (**never change**: it is the app identity for updates and F-Droid) |
| namespace / package | `com.example` (keep until after 1.0; renaming is a repo-wide refactor with no user benefit) |
| Version | `versionName 1.0`, `versionCode 1` (`app/build.gradle.kts`) |
| SDK | minSdk 24, targetSdk 36, compileSdk 36.1 (`release(36) { minorApiLevel = 1 }`) |
| Toolchain | JDK 17, AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2024.09.00, core library desugaring 2.1.5 |
| Dependencies | all in `gradle/libs.versions.toml`; FLOSS only |
| Distribution | F-Droid only (IzzyOnDroid is declined; do not add anything for it). GitHub Releases via `v*` tags |
| Network | app has **no INTERNET permission** and works fully offline. Do not add one, no analytics, no ads, no proprietary SDKs |
| Languages | EN (default), RU, DE, FR, IT, ES. RU is selected only for `ru` and `be` system locales; `uk` is not RU |

## 2. Frozen product decisions (do not "fix" these)

- Capital-gains tax is applied only when `financialAssets > 0`; with `financialAssets == 0` the effective rate is 0.
- A zero savings rate is a **negative** indicator (score 0). Cash flow `<= 0` is negative too.
- CFS (Коэффициент финансовой свободы) is separate from Fidelity. CFS uses net worth **including property**; texts must say so and that readiness to live on capital income is judged on the FIRE tab.
- Fidelity benchmark compares liquid + financial assets minus debts not covered by property: `max(0, liabilities - property)`. Multiplier interpolation between benchmark ages stays; 0.5x up to age 25.
- FIRE start capital is investable assets adjusted for uncovered debts (same formula as the Fidelity capital).
- Net worth status rule (`>= 50% of assets` is positive) lives in `FinancialMetricEvaluator` and is used on Input, Metrics and Audit.
- Total score may be green with a neutral CFS. The cap (6.0) applies only when the CFS component score is below 5.
- Yen (¥) amounts are shown and entered as whole numbers.
- Simulation badge in the FIRE hero card sits to the right of "До пенсии осталось", only while simulating.
- The simulator never changes the saved base input; the Audit and export always use base metrics.

## 3. Architecture map

Source root: `app/src/main/java/com/example/`

| Path | Responsibility |
|---|---|
| `data/model/FinancialData.kt` | `FinancialInputData`, `CalculatedMetrics`, `FinancialScores`, `FireStatus`, `MetricStatus`, `TrajectoryPoint` |
| `data/repository/LocalPreferencesManager.kt` | SharedPreferences persistence (input, simulation state, tab, language) |
| `domain/FinanceCalculator.kt` | `FinancialInputLimits`, `FinanceCalculator.calculate(...)`: all base metrics, scores, Fidelity, FIRE call |
| `domain/FinancialMetricEvaluator.kt` | **Only place for thresholds** and status/score tiers of every metric |
| `domain/ProjectionEngine.kt` | FIRE math: accumulation, required capital, tax factor, bisection for years to retirement, trajectory |
| `domain/ScenarioConstants.kt` | Scenario percentages and multipliers |
| `domain/SimulatorScaleCalculator.kt` | Slider ranges and steps for the FIRE simulator |
| `domain/AuditorAnalysisGenerator.kt` | Audit texts grouped into positive / neutral / negative, export summary |
| `ui/MainViewModel.kt` | State (`inputData`, `metrics`, `fireSimulation`, `simulatedMetrics`, trajectories), debounced save, scenario logic (`recalculateSimulation`) |
| `ui/FinanceApp.kt` | Navigation, wiring of screens, language from system locale |
| `ui/screens/` | `InputDataScreen`, `MetricsScreen`, `AuditScoreScreen`, `FireRetirementScreen` |
| `ui/components/` | `MetricCard`, `ScoreGauge`, `ScenarioTogglesPanel`, `FireTrajectoryChart` |
| `ui/theme/` | `StatusColors`, `ScoreColorEvaluator`, theme |
| `util/Formatters.kt` | Money, percent, ratio, score, FIRE summary formatting (language-aware) |
| `util/localization/` | `AppStrings` interface + `StringsEn/Ru/De/Fr/It/Es.kt`, `AppLanguage` |

Data flow:

```
FinancialInputData --FinanceCalculator.calculate--> CalculatedMetrics (base)
   base + FireSimulationState --MainViewModel.recalculateSimulation--> simulatedMetrics
   metrics --ProjectionEngine--> yearsToRetirement / targetCapital / trajectory
```

Screens read base `metrics`; the FIRE tab shows `simulatedMetrics` while `isSimulating`.

## 4. Single sources of truth (never duplicate)

- Status and score thresholds: `FinancialMetricEvaluator`. `StatusColors`, `ScoreColorEvaluator`, `AuditorAnalysisGenerator`, screens and strings must call it, not copy numbers.
- Input limits: `FinancialInputLimits`. Scenario numbers: `ScenarioConstants`. Slider bounds: `SimulatorScaleCalculator`.
- Capital for Fidelity/FIRE: one formula (liquid + financial − `max(0, liabilities − property)`).
- Money display: `Formatters.formatMoney` and `Formatters.currencyFractionDigits`; do not format money by hand.
- When you change a threshold or formula, also update: the description strings in all 6 languages, the audit texts, the tests, and `README.md` if it states the rule.

## 5. Localization rules

- Adding or changing a user-visible string: add it to `AppStrings` and implement it in **all six** `Strings??.kt` in the same change.
- Strings that take a prefix or a number: check the final composed text in every language (no doubled punctuation such as `": :"`, correct Russian plural forms via `plural(...)` / `yearPluralForms`, French spaces before `:`).
- Texts that describe a rule (intervals, thresholds, assumptions) must match the code exactly, including whether bounds are inclusive. Intervals are written as `>= a and < b`.
- Assumptions text must not contradict the model (the model **does** estimate capital-gains tax on financial assets).
- Fastlane store texts live in `fastlane/metadata/android/<locale>/`; update them if user-visible behavior or wording changes. Short description limit is 80 characters.

## 6. Numbers and tests: pitfalls specific to this codebase

- Money is `Double`. Avoid `== 0.0` on sums of money; round to currency precision before comparing.
- Total score is a weighted sum; compute in integer hundredths, otherwise float error moves values across the 3.5 / 7.0 borders.
- Displayed rounding must agree with status: a value shown as "7,0" must not be colored as below 7.
- Slider state must always lie within the current `SimulatorScaleCalculator` ranges after any input, scenario or preference change.
- Persisted values are untrusted: clamp on load (no NaN, Infinity, out-of-range numbers).
- `calculateAge` uses `LocalDate.now()` through `dateProvider`; tests must inject the date.

## 7. Build and test commands

Use `bash ./gradlew` (wrapper is checked in). Iterate narrow, finish wide.

```bash
# fast loop: one suite or one method
bash ./gradlew testDebugUnitTest --tests com.example.ProjectionEngineTest
bash ./gradlew testDebugUnitTest --tests "com.example.FinanceCalculatorTest.*"

# before declaring done: everything (130 tests, JVM + Robolectric)
bash ./gradlew testDebugUnitTest

# release checks (needs signing env for a signed APK)
bash ./gradlew lintRelease assembleRelease
```

- Do not use `--rerun-tasks` or `clean` in the iteration loop (Gradle build cache and configuration cache are on). Use `--offline` when dependencies are cached.
- The first Robolectric run downloads SDK jars from Maven Central (needs network). Robolectric runs on API 35 (JDK 17 limit); `LocalizationFormattersTest` pins API 28.
- Store screenshots are opt-in: `GENERATE_STORE_SCREENSHOTS=1 bash ./gradlew testDebugUnitTest --tests com.example.StoreScreenshotsTest --rerun-tasks`.

Which tests to run first:

| Area changed | Run first |
|---|---|
| Base metrics, scores, evaluator | `FinanceCalculatorTest`, `FinancialMetricEvaluatorTest`, `FinancialConsistencyIntegrationTest` |
| FIRE math, tax, trajectory | `ProjectionEngineTest`, `CapitalGainsTaxTest`, `ScenarioSimulationTest`, `FireTrajectoryChartTest`, `FireFormatterTest` |
| Simulator ranges, scenarios, ViewModel | `SimulatorScaleCalculatorTest`, `MainViewModelRobolectricTest` |
| Persistence | `LocalPreferencesAndViewModelRobolectricTest` |
| Strings, formatters, languages | `LocalizationFormattersTest`, `AppLanguageTest`, `AuditorAnalysisTest` |
| Screens and input fields | `MetricsScreenRobolectricTest`, `AuditScoreScreenRobolectricTest`, `NumberInputFieldRobolectricTest`, `MainActivityRobolectricTest` |

## 8. Workflow for a fix

1. Reproduce with a failing test (or a minimal numeric example) before changing code.
2. Locate the single source of truth (section 4); fix it there, not in the UI.
3. Run the narrow suite, then the full suite.
4. Check all six languages if text or formatting is involved.
5. Report in Russian: what was wrong, which files changed, tests added, tests run and result, any number that changed for users.

Order for multi-step plans: numeric foundation (rounding, score) → status/text for edge cases → simulator state and bounds → persistence → input fields → texts in six languages → guard tests → release checklist. Later steps assume earlier ones are merged and green.

## 9. Known open defects (remove a line when fixed)

- Total score: display vs status mismatch at 6.95 and 3.5 (float sum).
- Net worth subtitle prints a doubled colon (all six languages).
- FIRE "assumptions" text says calculations ignore taxes (contradicts the tax model), in all six languages and in the export.
- CFS description intervals use `<=` upper bounds; code uses `[a, b)`. Audit CFS texts do not mention that property is included.
- Russian age phrases use the wrong plural ("возраст 22 лет").
- "Job loss" scenario uses base income while mortgage/car use income after the salary raise; scenario descriptions can differ from computed amounts.

## 10. Release and F-Droid

- Before tagging `v1.0`: tests green, `lintRelease` clean, two release builds byte-identical, signed APK checked, run on emulators API 24 and 36, store screenshots refreshed after text changes, GitHub Actions secrets set (`KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`).
- `metadata/io.github.volmir.personalfinance.yml` pins a commit hash; it must equal the commit of the `v1.0` tag. Verify that the F-Droid build server supports compileSdk 36.1 (fallback: compileSdk 36).
- F-Droid requires about 3 months of active development: submit the request no earlier than ~2027-01-10.
- Pushing a `v*` tag triggers the signed release workflow (`.github/workflows/build-and-release.yml`). Never push tags unless the user asks. Never delete or move a published tag.
- Release signing uses `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`. Keep keystores, passwords, tokens and `local.properties` out of Git. Preserve the signing key.
- `FDROID_SETUP.md` has the reproducibility procedure.

## 11. Code style

- Official Kotlin style, four-space indentation; Gradle scripts use two spaces. `UpperCamelCase` classes/composables, `lowerCamelCase` members, `UPPER_SNAKE_CASE` constants. No standalone formatter; use Android Lint.
- Keep calculations in `domain/`, persistence in `data/`, presentation in `ui/`. Screens do not contain formulas.
- Comments and KDoc in `domain/` are mostly Russian: follow the language of the surrounding file. Keep comments about why, not what.
- Commit messages: `fix: handle yen amounts`, `refactor(ui): simplify metric cards`. PRs state the problem, resulting behavior, validation; add screenshots for visual changes.
