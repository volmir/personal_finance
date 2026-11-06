# Complete App Description

**Personal Finance & FIRE Audit** is an Android app for manually recording a monthly budget and balance sheet, calculating financial indicators, and modeling a FIRE (Financial Independence, Retire Early) scenario. It is a calculator and educational tool, with no connection to banks.

This document describes the current working code. The published APK is `v1.0`; the next release is planned as `v1.0.1` (`versionCode = 2`). The app version currently remains `1.0 / 1`, and newer fixes described here may not be in the published APK. Release checks are recorded in [the dated verification report](docs/release-readiness-1.0.1.md).

## App Screens

### 1. Input Data

The user enters:

- monthly income and expenses;
- liquid assets, financial assets, and the full estimated value of property;
- total liabilities;
- expected nominal return, inflation, and the capital-gains tax rate;
- birth date and the life-expectancy age through which the projection runs.

Expandable guidance with category examples is available for income, expenses, assets, liabilities, investments, and planning-horizon parameters. The emergency-fund toggle determines whether financial assets count toward the reserve: the reserve equals either liquid assets alone or liquid and financial assets combined. By default, the form contains demonstration values identified in the code as US averages; these are a starting example, not an assessment of the user or local statistics.

Users can choose among 13 currency symbols: `$`, `€`, `₽`, `₴`, `₸`, `Br`, `֏`, `£`, `¥`, `CHF`, `C$`, `A$`, `₹`. The selection changes the displayed symbol without converting amounts or using exchange rates.

Enter property at its full value and debts separately. Amounts range from 0 to 1,000,000,000,000. Money is displayed and entered to two decimal places, except yen, which uses whole units; accepted input is rounded HALF_UP to that precision. Percentage input uses two decimal places. Nominal return and inflation are limited to −50% through 100%; the entered capital-gains tax rate is limited to 0% through 100%.

Base monetary metrics are rounded to the selected currency precision before dependent ratios are calculated.

The life-expectancy input must be greater than the current age and no more than 120. While an empty or invalid draft is shown, a hint identifies the saved value used in calculations. If a birth-date change exhausts the saved horizon, the app reports that condition rather than automatically extending the horizon. Age is recalculated when the app resumes.

### 2. Financial Metrics

The app calculates nine metrics:

1. **Cash Flow:** monthly income minus monthly expenses.
2. **Savings Rate:** cash flow divided by income, expressed as a percentage.
3. **Emergency Fund:** the selected combination of liquid and financial assets.
4. **Safety Months:** the reserve divided by monthly expenses.
5. **Debt Ratio:** liabilities divided by total assets.
6. **Net Worth:** all assets minus liabilities.
7. **Fidelity Benchmark:** compares eligible capital against annual income multiplied by an age-based factor. Eligible capital is `liquid + financial − max(0, liabilities − property)` and can be negative. The app uses 0.5x up to age 25, then interpolates through 1x at 30, 3x at 40, 6x at 50, 8x at 60, and 10x at 67; the factor remains 10x thereafter. An invalid birth date or zero income-based target makes the benchmark unavailable.
8. **Financial Freedom Ratio (CFS):** net worth, including property, divided by annual expenses. It expresses net worth in years of expenses. Readiness to live on capital income is assessed on the FIRE tab, which uses investable capital.
9. **Real Return:** nominal return adjusted for inflation using the Fisher equation: `(1 + nominal return) / (1 + inflation) − 1`. The model applies a lower limit of −50% to the resulting real return.

Values are accompanied by color-coded assessments and brief explanations.

Cash flow and savings rate of zero are negative indicators. Net worth of zero is neutral; positive net worth is positive when it is at least 50% of total assets, and otherwise neutral. Fidelity is positive at 100% of its target or above, neutral from 70% up to but excluding 100%, and negative below 70%. Unavailable Fidelity values are shown as `—` and omitted from the audit.

Zero denominators have explicit boundary rules. With no income, savings rate is −100% for a deficit and 0% otherwise. With no assets, debt ratio is unavailable and negative when liabilities exist, and 0 otherwise. With no expenses, positive emergency reserves use 12 safety months; a zero reserve uses 0. With zero annual expenses, CFS is 30 for positive net worth, −1 for negative net worth, and 0 for zero net worth. These conventions do not establish readiness for retirement.

### 3. Score & Audit

The overall financial health score ranges from 0 to 10. Component weights are CFS 45%, savings rate 25%, emergency-fund runway 15%, and debt ratio 15%. The screen shows the overall score, component ratings, and an assessment generated from the metrics, with positive, neutral, and negative signals. The summary and full assessment can be shared through Android's system share menu.

CFS stages follow inclusive lower and exclusive upper bounds:

| CFS range | Component score | Indicator status |
| --- | ---: | --- |
| CFS < 0 | 0 | Negative |
| CFS >= 0 and < 0.5 | 2 | Negative |
| CFS >= 0.5 and < 2 | 4 | Negative |
| CFS >= 2 and < 5 | 6 | Neutral |
| CFS >= 5 and < 15 | 8 | Positive |
| CFS >= 15 and < 30 | 9 | Positive |
| CFS >= 30 | 10 | Positive |

The total is rounded to one decimal place before its color/status is selected: positive at 7.0 or above, neutral from 3.5 up to but excluding 7.0, and negative below 3.5. It is capped at 6.0 only when the CFS component score is below 5. A neutral CFS can therefore coexist with a green total; component scores of 10, 10, 10, and 6 produce a total of 8.2. Audit entries take their statuses from the shared evaluator and use icons consistent with their positive, neutral, or negative group.

### 4. FIRE & "What if?"

The screen shows the projected time and age at which FIRE is reached, the required capital, and a chart comparing projected capital with required capital through the selected life-expectancy age. Separate messages identify an invalid age, an exhausted horizon, and a FIRE point that is unattainable under the current assumptions; unavailable target values are displayed as `—`.

**Starting FIRE capital** is calculated as:

```text
max(0, liquid assets + financial assets − max(0, liabilities − property))
```

This uses the shared Fidelity-capital formula with a zero floor for FIRE. Property is not added to investable assets. Liabilities reduce starting capital only by the amount exceeding property value.

**Required capital** is modeled as the present value of expenses for the remaining period from the assumed retirement date to the life-expectancy age, at a constant real return. Depending on the scenario, the calculation may account for temporary expenses, lower future expenses, one-off proceeds from simulated events, and an estimated tax on gains in financial assets. The 4% rule and Trinity Study are not used.

Capital-gains tax is estimated in a simplified way for the financial-assets share and only when financial assets are present. The entered tax rate is distinct from the effective rate used by the projection, which is capped at 50%. Other taxes, market-event ordering, and income inflation are excluded.

The retirement point is found by comparing projected capital (starting assets and accumulated net cash flow, accounting for real return) with target capital. A deficit reduces projected capital. Return and inflation come from the Input Data screen; the planning horizon is life-expectancy age minus current age.

**Three simulator sliders** adjust monthly savings/income, expenses, and the real-return adjustment. Changes apply to a separate "What if?" calculation without overwriting the original financial inputs. Slider state is bounded after input changes, scenario changes, and restoration from local storage. Return adjustments respect the nominal-return limits at the selected inflation rate. Metrics, Audit, and exported reports use the base calculation; the FIRE tab shows the simulation while it is active.

**14 built-in scenarios:**

- income increases by 20%;
- financial assets increase by 25%;
- ongoing expenses decrease by 15%;
- expenses decrease by 30% after relocation;
- a one-off inheritance/gift equals 12 monthly incomes;
- property sale proceeds are capped at the lower of property value and 20 monthly incomes;
- expenses decrease by 25% after 18 years;
- job-loss stress: a one-off expense equal to 12 current monthly incomes (ongoing income is preserved);
- financial assets decrease by 30%;
- mortgage payments equal 35% of monthly income for 20 years;
- car payments equal 20% of monthly income for 5 years;
- a large one-off expense equals 5 monthly incomes;
- expenses increase by 25% (the interface labels this scenario as rising inflation);
- child or dependent costs add 25% of original expenses for 18 years.

Sliders and scenarios can be reset. Simulator state is saved locally between app launches.

Job-loss costs and mortgage/car payments use income after the salary-increase scenario. Inheritance, property-sale limits, and the large one-off expense use the original monthly income. A property sale transfers the capped amount from property to liquid assets. One-off expenses consume liquid assets first, then financial assets; an uncovered remainder becomes additional debt. Permanent expense multipliers combine multiplicatively, and the expense slider cannot reduce ongoing expenses below 10% of the original monthly expenses.

## Language, Storage & Limitations

- The interface supports 6 languages: English (default), Russian (for system locales `ru` and `be`), German (`de`), French (`fr`), Italian (`it`), and Spanish (`es`). The language is selected automatically from the Android system locale without additional interface controls.
- The app runs on Android 7.0 (API 24) and later. The current working snapshot was tested separately on AOSP x86_64 emulators at API 24 and 36; this is not a claim of testing every device or Android version.
- The app does not request the `INTERNET` permission; inputs and calculations are processed locally. Reports are shared only at the user's initiative through Android and an app of their choice.
- The app does not connect to banks, import transactions, or download market quotes or exchange rates.
- Inputs, the current tab, detected language, and simulator state are saved in local preferences. Invalid stored numeric values are restored to valid defaults or bounds. System-language changes are reflected when the app observes the updated locale configuration.
- The projection assumes a constant real return and relies on user-entered amounts and simplified scenarios. It is not a promise of returns or personalized financial, investment, or tax advice.
- Fidelity is used as an age-based benchmark; the app is not affiliated with Fidelity Investments.
