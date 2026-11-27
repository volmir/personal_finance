# AGENTS.md

Personal Finance & FIRE Audit — offline Android app (Kotlin, Jetpack Compose, MVVM). Read this file fully before touching code; it replaces exploring the repo from scratch.

## 0. Working rules

- Reply to the user in **Russian**, briefly. Code identifiers and commit messages stay in English.
- Current phase: **release hardening; `1.0.3` / `4` is in preparation, `1.0.2` / `3` remains the published release**. Fix defects and logical inconsistencies only. **No new features, no behavior changes beyond the fix, no refactors that are not needed for the fix.** If a fix would change a user-visible number, say so before applying it. Узкое исключение: согласованная очередь FA-01–FA-11 может менять правила оценки ровно в пределах утверждённой спецификации `docs/financial-assessment-methodology.md`; это отдельное функциональное изменение, а не выпуск версии.
- Minimal diffs: do not reformat, rename, or reorder untouched code. One logical change per commit.
- Never silently edit a test expectation to make a test pass. If an expectation must change, state which value changed, by how much, and why.
- Every bug fix ships with a regression test (boundary values for formulas).
- If you cannot run Gradle (no network / no SDK), say so explicitly. Do static review, and for math verify with a small throwaway script. Never claim tests passed when they were not run.
- If something is ambiguous or contradicts section 2, ask one short question instead of guessing.

- **One-off tool lifecycle.** One-off migration, repair and data transformation tools must not remain indefinitely in the main codebase. Once the operation is complete, move durable product decisions into the active configuration or code, keep regression tests for the resulting behavior, and remove the one-off tool and its dedicated tests if it is no longer needed for reproducibility. Before removal, verify dependencies, invocation paths and data/build reproducibility; preserve tests for active behavior, supported user-data migrations and historical evidence. If the tool's continued necessity is unclear, do not remove it without separate approval. Cleanup of existing tools is a separate task and must not expand the scope of the current work automatically.

- **Test lifecycle.** Tests must protect current product contracts and material risks. Remove proven duplicates, checks of removed functionality, and tests dedicated to completed one-off tools that are no longer needed for reproducibility. When merging tests, preserve every unique assertion. Prefer cheap pure unit tests for pure logic and representative UI integrations; do not weaken assertions or acceptance criteria for speed. Preserve supported migrations, regressions, accessibility, localization and ongoing release tooling. Every new bug fix still requires a regression test. Record evidence, the remaining equivalent protection and risks in `docs/test-suite-maintenance.md`; age, iteration count, a green run or lack of direct callers alone does not prove a test redundant.

- **UI test matrix budget.** This global rule applies to all existing and future app UI tests, including FA-*, FIRE-M* and FIRE-UI-* tasks. The owner explicitly approved reducing visual-test cost; the breadth of a test matrix is no longer itself a protected contract.
  - The baseline visual/layout run uses RU, a standard phone width of 360.dp, system fontScale 1.0 and the light theme. Cover every active main screen, including both FIRE subtabs. Use current standard viewport heights (for example 800.dp); do not create matrices of heights, densities or physical resolutions.
  - Additional visual checks may use at most two other languages, preferably DE for long strings and EN as the default language, and only on the riskiest cards/screens. Use a second typical width of 411.dp selectively for a named independent contract, never for every language/theme/screen. Do not require full UI rendering in all six languages.
  - Exclude extremely small viewports, particularly widths of 320.dp or less, from regular layout/UI matrices. Do not add fontScale 2.0 (200%) or higher checks; existing separate runs at those scales must also be removed during test maintenance. Exclude scales 1.3–1.6 from mass matrices. If an independent accessibility/text-wrapping check is needed, retain at most one representative increased scale (preferably 1.3), one problematic component and the standard viewport. Do not change system scaling, responsive layout, minSdk or actual device support: this is a testing policy, not a product limitation. Other extreme configurations require a separately and explicitly approved exception, never an agent-initiated expansion.
  - Check the dark theme selectively on RU for theme-dependent risks. Preserve separate cheap checks of contrast, status semantics, color tokens and accessibility; do not repeat the full layout run in every theme. Do not combine localization, status colors, scales and viewports into a Cartesian product.
  - All six languages remain supported and covered by cheap tests of localization completeness/meaning, strings, formatting, units, plurals, Android resources and exports. The RU plus up to two languages limit applies to expensive visual/layout rendering, not localization content or unique language rules. Where Android Context is needed, use a minimal test without rendering every screen.
  - Default to one standard parameter set per functional UI test; additional combinations require an explicitly named independent risk. Do not remove unique business regressions, IME, state, navigation, accessibility semantics or data-preservation assertions to reduce a visual matrix: move them to the standard configuration. Preserve release screenshot requirements (six locales, opt-in), functional tests, financial mathematics, supported migrations and mandatory gates.
  - This later owner decision supersedes earlier requirements for mass checks on small viewports, large fonts and all locales/themes in existing and pending tasks. Preserve their functional criteria while applying this reduced matrix. Agents must not edit protected TASKS.md/PROGRESS.md. If a targeted check needs an exception to this standard, explain the risk and request separate permission; never automatically restore a broad matrix.

## 1. Facts

| Item | Value |
|---|---|
| applicationId | `io.github.volmir.personalfinance` (**never change**: it is the app identity for updates and F-Droid) |
| namespace / package | `com.example` (keep until after 1.0; renaming is a repo-wide refactor with no user benefit) |
| Version | `versionName 1.0.3`, `versionCode 4` (`app/build.gradle.kts`), in preparation; published release is `1.0.2` / `3` |
| Release tag | Published `v1.0.2`, commit `293849bcb33355ef68df9da68e09892fe2262529`; never delete or move published tags |
| SDK | minSdk 24, targetSdk 36, compileSdk 36.1 (`release(36) { minorApiLevel = 1 }`) |
| Toolchain | JDK 17 supported (former CI); JDK 21 verified locally. AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2024.09.00, core library desugaring 2.1.5 |
| Dependencies | all in `gradle/libs.versions.toml`; FLOSS only |
| Distribution | F-Droid only (IzzyOnDroid is declined; do not add anything for it). GitHub Releases via `v*` tags |
| Network | app has **no INTERNET permission** and works fully offline. Do not add one, no analytics, no ads, no proprietary SDKs |
| Languages | EN (default), RU, DE, FR, IT, ES. RU is selected only for `ru` and `be` system locales; unsupported locales use EN |

## 2. Frozen product decisions (do not "fix" these)

- Capital-gains tax is applied only when `financialAssets > 0`; with `financialAssets == 0` the effective rate is 0.
- A zero savings rate is a **negative** indicator (score 0). Cash flow `<= 0` is negative too.
- CFS (Коэффициент финансовой свободы) is separate from Fidelity. CFS uses net worth **including property**; texts must say so and that readiness to live on capital income is judged on the FIRE tab.
- Fidelity benchmark compares liquid + financial assets minus debts not covered by property: `max(0, liabilities - property)`. Multiplier interpolation between benchmark ages stays; 0.5x up to age 25.
- FIRE start capital is investable assets adjusted for uncovered debts (same formula as the Fidelity capital).
- Net worth status rule (`>= 50% of assets` is positive) lives in `FinancialMetricEvaluator` and is used on Input, Metrics and Audit.
- Согласованные правила комплексной оценки, включая девять показателей, веса, доступность, агрегацию, cap, статусы и экспорт, закреплены в [методике финансовой оценки](docs/financial-assessment-methodology.md). В FA-* не менять остальные frozen decisions из этого раздела.
- Yen (¥) amounts are shown and entered as whole numbers.
- Simulation badge in the FIRE hero card sits to the right of "До пенсии осталось", only while simulating.
- The simulator never changes the saved base input; the Audit and export always use base metrics.
- findYearsToRetirement использует бисекцию, обоснование в KDoc; не заменять сеточным поиском без нового контрпримера.

## 3. Architecture map

Source root: `app/src/main/java/com/example/`

| Path | Responsibility |
|---|---|
| `data/model/FinancialData.kt` | `FinancialInputData`, `CalculatedMetrics`, `FinancialScores`, `FireStatus`, `MetricStatus`, `TrajectoryPoint` |
| `data/repository/LocalPreferencesManager.kt` | SharedPreferences persistence (input, simulation state, tab, language) |
| `domain/FinanceCalculator.kt` | `FinancialInputLimits`, `FinanceCalculator.calculate(...)`: all base metrics, scores, Fidelity, FIRE call |
| `domain/FinancialMetricEvaluator.kt` | **Only place for thresholds** and status/score tiers of every metric |
| `domain/ProjectionEngine.kt` | FIRE math: accumulation, required capital, tax factor, bisection for years to retirement, trajectory |
| `domain/ScenarioConstants.kt` | Scenario constants and shared scenario amount calculations |
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

# before declaring done: all JVM + Robolectric tests
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

### Gate selection for orchestrator tasks

`--check` profiles are defined in `orchestrator/config.json`; change that file,
not the generic resolver. The built-in `orchestrator` profile always exists.

| Profile | Purpose | Commands |
|---|---|---|
| `android` | compilation + static analysis, **no unit tests** | `bash ./gradlew :app:compileDebugKotlin :app:lintDebug --console=plain` |
| `unit` | unit tests (JVM + Robolectric) | `bash ./gradlew :app:testDebugUnitTest --console=plain` |
| `full` | comprehensive check: orchestrator self-test + unit + lint + assembleDebug | `python3 -m unittest discover -s orchestrator/tests` and `bash ./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --console=plain` |
| `release` | release checks only: lintRelease + assembleRelease | `bash ./gradlew :app:lintRelease :app:assembleRelease --console=plain` |
| `orchestrator` | self-check of the automation (built-in) | `python3 -m unittest discover -s orchestrator/tests` |

`full` stays the default for `--check` without an argument and the composition
of the comprehensive check. It is **not** the default gate for small tasks.

| Change type | gate |
|---|---|
| Docs only, no runnable checks | `skip` |
| Docs with runnable commands/examples | `bash orchestrator/run.sh --check orchestrator` plus a targeted parse/check |
| Kotlin/Compose code, no logic or test changes | `bash orchestrator/run.sh --check android` |
| Financial math, scores, rounding, FIRE, formatters, persistence logic, ViewModel logic | `bash orchestrator/run.sh --check unit && bash orchestrator/run.sh --check android` |
| Screens/components with changed Robolectric tests | `bash orchestrator/run.sh --check unit && bash orchestrator/run.sh --check android` |
| Screens/components without test changes | `bash orchestrator/run.sh --check android` |
| Tests only | `bash orchestrator/run.sh --check unit` (or `--check orchestrator` for module tests) |
| Orchestrator code/tests | `bash orchestrator/run.sh --check orchestrator`; for `run.sh`/`checks.py` also `bash -n` and `--check --help` |
| Orchestrator config/queue/examples | `bash orchestrator/run.sh --check orchestrator` plus parser or affected-profile check |
| Version, release artifacts, store texts | `bash orchestrator/run.sh --check release`; add the changed area's profile if code/text changed |
| Several areas, final confidence | `bash orchestrator/run.sh --check full` |

Mandatory gate rules (full rationale in `orchestrator/README.md`, "Выбор gate"):

1. Every task in `TASKS.md` has a `gate`: a command or the exact value `skip`.
2. Pick the gate by change type from the matrix; `tier` and model do not affect it.
3. `skip` is allowed only when there is no executable check; it is logged as **ПРОПУЩЕНА**.
4. A missing SDK, reluctance to run tests, or a small task is not a reason for `skip` or a weaker gate.
5. Docs without runnable checks get no artificial gate; if commands are listed, check them narrowly.
6. Compilation/static analysis never replaces unit tests when logic changed.
7. Financial calculations and other business logic require `unit && android` together.
8. Test-only changes run the profile that executes those tests, not automatically `full`.
9. Orchestrator changes use the built-in `orchestrator` profile; `run.sh`/`checks.py` also need syntax and `--check --help` checks.
10. `full` is reserved for comprehensive/final verification, not for routine small tasks.
11. Combine gates with `&&`: left to right, stop on the first error, non-zero exit code of the first failing command.
12. Never use `||`, `;`, or suppressed exit codes in a gate; put `&&` in `gate`, not in `gate_base`.

## 8. Workflow for a fix

1. Reproduce with a failing test (or a minimal numeric example) before changing code.
2. Locate the single source of truth (section 4); fix it there, not in the UI.
3. Run the narrow suite, then the full suite.
4. Check all six languages if text or formatting is involved.
5. Report in Russian: what was wrong, which files changed, tests added, tests run and result, any number that changed for users.

Order for multi-step plans: numeric foundation (rounding, score) → status/text for edge cases → simulator state and bounds → persistence → input fields → texts in six languages → guard tests → release checklist. Later steps assume earlier ones are merged and green.

## 9. Known open defects (remove a line when fixed)

Verification of the published release is recorded in `docs/release-readiness-1.0.2.md`; preparation checks and remaining publication steps for the working version are in `docs/release-prep-1.0.3.md`. The former total-score rounding defect is covered by `FinanceCalculatorTest` boundary regressions and is no longer an open item.

## 10. Release and F-Droid

- Before tagging a future release: tests green, `lintRelease` without errors, two unsigned release builds byte-identical, store screenshots refreshed after text changes unless the owner explicitly approves reuse, release commit pushed, explicit author confirmation received. The owner intentionally removed the release workflow; do not restore it. Run checks and sign manually with the existing release key; verify the published signed APK and run available API 24 and 36 emulators after publication.
- For `1.0.3`, the owner plans further minor UI edits, deferred the final release build, and explicitly approved reusing the current screenshots. Do not rebuild release APKs or regenerate screenshots as part of this preparation; final publication checks belong to a later release task.
- `metadata/io.github.volmir.personalfinance.yml` preserves published `v1.0` and `v1.0.1` and adds `v1.0.2` with its full tag commit hash. Future builds are added separately after publication and artifact verification. compileSdk 36.1 is verified in the local official F-Droid container; production F-Droid verification remains pending.
- F-Droid normally requires 3 months of active development (threshold ~2027-01-10). At the author's explicit request, early MR [!56526](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/56526) was opened on 2026-11-09 with the unmet criterion disclosed; no policy exception has been approved. Continue that MR rather than opening a duplicate. Its recipe was updated to `1.0.2` on 2026-11-18 and pipeline 3083348788 is green.
- Release publication is manual; `.github/workflows/build-and-release.yml` was intentionally removed by the owner. Never create or push release tags unless the user asks. Never delete or move a published tag.
- Release signing uses `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`. Keep keystores, passwords, tokens and `local.properties` out of Git. Preserve the signing key.
- `FDROID_SETUP.md` has the reproducibility procedure.

## 11. Code style

- Official Kotlin style, four-space indentation; Gradle scripts use two spaces. `UpperCamelCase` classes/composables, `lowerCamelCase` members, `UPPER_SNAKE_CASE` constants. No standalone formatter; use Android Lint.
- Keep calculations in `domain/`, persistence in `data/`, presentation in `ui/`. Screens do not contain formulas.
- Comments and KDoc in `domain/` are mostly Russian: follow the language of the surrounding file. Keep comments about why, not what.
- Commit messages: `fix: handle yen amounts`, `refactor(ui): simplify metric cards`. PRs state the problem, resulting behavior, validation; add screenshots for visual changes.

## 12. Orchestrator

The `orchestrator/` directory is a self-contained task-queue module (Codex CLI / OpenCode → gate → auto-commit → journal). Its own rules, checks and docs live inside it: read `orchestrator/AGENTS.md` for rules and `orchestrator/README.md` for usage. Queue runs commit and push directly on `main` (configured via `branch` in `orchestrator/config.json`; no separate queue branch is used) and are managed by `bash orchestrator/run.sh`. Do not edit `orchestrator/` during a regular app task: its files are checksum-protected and any agent edits inside it stop the queue.

