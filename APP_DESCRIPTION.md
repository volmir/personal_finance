# Complete App Description

**Personal Finance & FIRE Audit** is an Android app for manually recording a monthly budget and balance sheet, calculating financial indicators, and modeling a FIRE (Financial Independence, Retire Early) scenario. It is a calculator and educational tool, with no connection to banks.

## App Screens

### 1. Input Data

The user enters:

- monthly income and expenses;
- liquid assets, financial assets, and property;
- total liabilities;
- expected nominal return, inflation, and the capital-gains tax rate;
- birth date and the life-expectancy age through which the projection runs.

Expandable guidance with category examples is available for income, expenses, assets, liabilities, investments, and planning-horizon parameters. The emergency-fund toggle determines whether financial assets count toward the reserve: the reserve equals either liquid assets alone or liquid and financial assets combined. By default, the form contains demonstration values identified in the code as US averages; these are a starting example, not an assessment of the user or local statistics.

Users can choose among 13 currency symbols: `$`, `€`, `₽`, `₴`, `₸`, `Br`, `֏`, `£`, `¥`, `CHF`, `C$`, `A$`, `₹`. The selection changes the displayed symbol without converting amounts or using exchange rates.

### 2. Financial Metrics

The app calculates nine metrics:

1. **Cash Flow:** monthly income minus monthly expenses.
2. **Savings Rate:** cash flow divided by income, expressed as a percentage.
3. **Emergency Fund:** the selected combination of liquid and financial assets.
4. **Safety Months:** the reserve divided by monthly expenses.
5. **Debt Ratio:** liabilities divided by total assets.
6. **Net Worth:** all assets minus liabilities.
7. **Fidelity Benchmark:** annual income multiplied by an age-based factor. Factors are defined at age checkpoints and interpolated between them.
8. **Financial Freedom Ratio (CFS):** net worth divided by annual expenses. This simple ratio expresses net worth in years of expenses; it is not a separate investment-return simulation.
9. **Real Return:** nominal return adjusted for inflation using the Fisher equation: `(1 + nominal return) / (1 + inflation) − 1`.

Values are accompanied by color-coded assessments and brief explanations.

### 3. Score & Audit

The overall financial health score ranges from 0 to 10. Component weights are CFS 45%, savings rate 25%, emergency-fund runway 15%, and debt ratio 15%. The screen shows the overall score, component ratings, and an assessment generated from the metrics, with positive, neutral, and negative signals. The summary and full assessment can be shared through Android's system share menu.

### 4. FIRE & "What if?"

The screen shows the projected time and age at which FIRE is reached, the required capital, and a chart comparing projected capital with required capital through the selected life-expectancy age. If the calculation finds no attainable FIRE point within the planning horizon, the screen displays a corresponding message.

**Starting FIRE capital** is calculated as:

```text
max(0, liquid assets + financial assets + min(0, property − liabilities))
```

Property is not added to investable assets. Liabilities reduce starting capital only by the amount exceeding property value.

**Required capital** is modeled as the present value of expenses for the remaining period from the assumed retirement date to the life-expectancy age, at a constant real return. Depending on the scenario, the calculation may account for temporary expenses, lower future expenses, one-off proceeds from simulated events, and an estimated tax on gains in financial assets. The 4% rule and Trinity Study are not used.

The retirement point is found by comparing projected capital (starting assets plus accumulated positive cash flow, accounting for real return) with target capital. Return and inflation come from the Input Data screen; the planning horizon is life-expectancy age minus current age.

**Three simulator sliders** adjust monthly savings/income, expenses, and the real-return adjustment. Changes apply to a separate "What if?" calculation without overwriting the original financial inputs.

**14 built-in scenarios:**

- income increases by 20%;
- financial assets increase by 25%;
- ongoing expenses decrease by 15%;
- expenses decrease by 30% after relocation;
- a one-off inheritance/gift equals 12 monthly incomes;
- property sale proceeds are capped at the lower of property value and 20 monthly incomes;
- expenses decrease by 25% after 18 years;
- job-loss stress: a one-off draw from assets equal to 12 monthly incomes (current income is not set to zero);
- financial assets decrease by 30%;
- mortgage payments equal 35% of monthly income for 20 years;
- car payments equal 20% of monthly income for 5 years;
- a large one-off expense equals 5 monthly incomes;
- expenses increase by 25% (the interface labels this scenario as rising inflation);
- child or dependent costs add 25% of original expenses for 18 years.

Sliders and scenarios can be reset. Simulator state is saved locally between app launches.

## Language, Storage & Limitations

- The interface supports 6 languages: English (default), Russian (for system locales `ru` and `be`), German (`de`), French (`fr`), Italian (`it`), and Spanish (`es`). The language is selected automatically from the Android system locale without additional interface controls.
- The app does not request the `INTERNET` permission; inputs and calculations are processed locally. Reports are shared only at the user's initiative through Android and an app of their choice.
- The app does not connect to banks, import transactions, or download market quotes or exchange rates.
- The projection assumes a constant real return and relies on user-entered amounts and simplified scenarios. It is not a promise of returns or personalized financial, investment, or tax advice.
- Fidelity is used as an age-based benchmark; the app is not affiliated with Fidelity Investments.
