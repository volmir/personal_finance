package com.example.util.localization

val StringsEn: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.EN

    override val appName: String = "Personal Finance & FIRE Audit"
    override val tabInputTitle: String = "Input Data & Parameters"
    override val tabInputShort: String = "Inputs"
    override val tabMetricsTitle: String = "Calculated Metrics"
    override val tabMetricsShort: String = "Metrics"
    override val tabScoresTitle: String = "Score Breakdown & Financial Audit"
    override val tabScoresShort: String = "Scores & Audit"
    override val tabFireTitle: String = "Financial Independence, Retirement, FIRE"
    override val tabFireShort: String = "FIRE & Plan"
    override val quickScorePrefix: String = "Score"

    override val block1Title: String = "1. Income & Expenses"
    override val block1Subtitle: String = "Monthly cash inflows and outflows"
    override val monthlyIncomeLabel: String = "Income (monthly)"
    override val monthlyExpensesLabel: String = "Expenses (monthly)"
    override val incomeBreakdownName: String = "Income"
    override val incomeHintCollapsed: String = "What is included in \"Income\"? (hint)"
    override val incomeHintExpanded: String = "Hide structure of \"Income\""
    override val expensesBreakdownName: String = "Expenses"
    override val expensesHintCollapsed: String = "What is included in \"Expenses\"? (hint)"
    override val expensesHintExpanded: String = "Hide structure of \"Expenses\""
    override val breakdownInstruction: String = "Calculate and add up your items from the list below yourself, and enter the resulting total in the field above."
    override val breakdownInstructionIcon: String = "💡 "
    override val breakdownCategoryTitleFormat: (title: String) -> String = { title -> "• $title" }
    override val breakdownCategoryItemFormat: (item: String) -> String = { item -> "    – $item" }
    override val incomeCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Employment & Salary",
            items = listOf(
                "Primary employment (base salary, bonuses, overtime)",
                "Secondary job, moonlighting, part-time work"
            )
        ),
        BreakdownCategory(
            title = "Business & Self-Employment",
            items = listOf(
                "Net profit from owned business / sole proprietorship",
                "Freelancing, consulting, side gigs, one-off services"
            )
        ),
        BreakdownCategory(
            title = "Passive & Investment Income",
            items = listOf(
                "Interest on bank deposits and high-yield savings accounts",
                "Stock dividends and bond coupon payments",
                "Rental income from real estate or vehicles",
                "Royalties, intellectual property"
            )
        ),
        BreakdownCategory(
            title = "Social & Other Inflows",
            items = listOf(
                "Pensions, government benefits, subsidies",
                "Child support, regular family assistance",
                "Tax refunds, cashbacks"
            )
        )
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Essential Living Expenses",
            items = listOf(
                "Groceries and household goods",
                "Rent or primary mortgage interest/payments",
                "Utilities (electricity, gas, water, heating)",
                "Telecommunications, internet, mobile plans"
            )
        ),
        BreakdownCategory(
            title = "Transportation & Commute",
            items = listOf(
                "Public transit, commuter passes",
                "Fuel, parking, car wash",
                "Vehicle maintenance, insurance, repairs",
                "Taxis, ride-sharing, car sharing"
            )
        ),
        BreakdownCategory(
            title = "Health & Personal Care",
            items = listOf(
                "Medications, doctor visits, dental care, health insurance",
                "Clothing, footwear, personal grooming",
                "Sports, fitness memberships, athletic clubs"
            )
        ),
        BreakdownCategory(
            title = "Leisure, Education & Family",
            items = listOf(
                "Dining out, coffee shops, food delivery",
                "Entertainment, movies, travel, hobbies",
                "Education, courses, books, children's activities",
                "Gifts, charity, miscellaneous unexpected expenses"
            )
        ),
        BreakdownCategory(
            title = "Debt Service",
            items = listOf(
                "Monthly minimums and payments on personal loans and credit cards"
            )
        )
    )
    override val cashFlowTitle: String = "Cash Flow:"
    override val cashFlowFormula: String = "(Income - Expenses)"
    override val savingsRateTitle: String = "Savings Rate:"
    override val savingsRateFormula: String = "(Cash Flow / Income) * 100%"

    override val block2Title: String = "2. Assets (Σ)"
    override val block2Subtitle: String = "Liquid, financial and physical wealth accumulation"
    override val liquidAssetsLabel: String = "Liquid (cash, checking, demand deposits)"
    override val financialAssetsLabel: String = "Financial (stocks, bonds, ETFs, brokerage)"
    override val propertyAssetsLabel: String = "Physical / Property (real estate, vehicles, valuables)"
    override val assetsBreakdownName: String = "Assets"
    override val assetsHintCollapsed: String = "What is included in \"Assets\"? (hint)"
    override val assetsHintExpanded: String = "Hide structure of \"Assets\""
    override val assetsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Liquid Assets",
            items = listOf(
                "Cash holdings in any currency",
                "Balances on checking and debit accounts",
                "High-yield savings accounts and money market funds",
                "Short-term deposits (up to 1-3 months)"
            )
        ),
        BreakdownCategory(
            title = "Financial (Investment) Assets",
            items = listOf(
                "Equities, ETFs, mutual funds",
                "Bonds (government, municipal, corporate)",
                "Brokerage balances and tax-advantaged accounts",
                "Long-term fixed certificates of deposit",
                "Precious metals (bullion, allocated accounts, coins)",
                "Cryptocurrencies and digital assets",
                "Retirement plan balances (401k, IRA, pensions)"
            )
        ),
        BreakdownCategory(
            title = "Physical (Property) Assets",
            items = listOf(
                "Residential real estate (primary home, apartment, land)",
                "Commercial real estate, parking spaces, garages",
                "Vehicles (cars, motorcycles, boats)",
                "Machinery, equipment, antiques, fine jewelry",
                "Equity share in privately held operating businesses"
            )
        )
    )
    override val totalAssetsTitle: String = "Assets (Σ):"
    override val totalAssetsFormula: String = "(Liquid + Financial + Property)"
    override val toggleEmergencyFundTitle: String = "Include financial assets in emergency buffer"
    override val toggleEmergencyFundSubOn: String = "Buffer = Liquid + Financial"
    override val toggleEmergencyFundSubOff: String = "Buffer = Liquid Only"

    override val block3Title: String = "3. Liabilities"
    override val block3Subtitle: String = "Debt obligations, loans, credit cards and mortgages"
    override val liabilitiesLabel: String = "Liabilities (all debts and obligations)"
    override val liabilitiesBreakdownName: String = "Liabilities"
    override val liabilitiesHintCollapsed: String = "What is included in \"Liabilities\"? (hint)"
    override val liabilitiesHintExpanded: String = "Hide structure of \"Liabilities\""
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Secured & Long-Term Debt",
            items = listOf(
                "Remaining principal on residential mortgage",
                "Auto loans (remaining balance)",
                "Collateralized home equity loans"
            )
        ),
        BreakdownCategory(
            title = "Consumer & Short-Term Loans",
            items = listOf(
                "Unsecured personal bank loans",
                "Outstanding credit card balances and revolving lines",
                "Installment plans, BNPL services, microloans"
            )
        ),
        BreakdownCategory(
            title = "Personal & Private Debts",
            items = listOf(
                "Debts owed to relatives, friends or colleagues",
                "Private promissory notes and personal loans"
            )
        ),
        BreakdownCategory(
            title = "Other Financial Obligations",
            items = listOf(
                "Back taxes, unpaid legal claims, fines",
                "Delinquent utility bills"
            )
        )
    )
    override val netWorthTitle: String = "Net Worth:"
    override val netWorthFormula: String = "(Assets - Liabilities)"

    override val block4Title: String = "4. Investments, Inflation & Taxes"
    override val block4Subtitle: String = "Parameters of return, purchasing power, and capital gains tax"
    override val nominalReturnLabel: String = "Nominal return %"
    override val inflationLabel: String = "Inflation %"
    override val capitalGainsTaxLabel: String = "Capital gains tax (%)"
    override val capitalGainsTaxBreakdownName: String = "Capital Gains Tax"
    override val capitalGainsTaxHintCollapsed: String = "How is capital gains tax calculated? (hint)"
    override val capitalGainsTaxHintExpanded: String = "Hide capital gains tax details"
    override val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String = { tax, eff -> "Tax rate: $tax | Effective withdrawal tax: $eff" }
    override val capitalGainsTaxFormula: String = "Tax Rate * Individual Gain Share * Financial Assets Ratio"
    override val capitalGainsTaxCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Tax Principle & Taxable Base",
            items = listOf(
                "Tax is levied not on the total sale proceeds, but on the net gain between sale price and documented acquisition costs (purchase price, commissions).",
                "The rate applies to profits from selling stocks and bonds (difference between selling and purchase price)."
            )
        ),
        BreakdownCategory(
            title = "Realistic Blended Model",
            items = listOf(
                "When liquidating assets, part of the withdrawal is your original cost basis and part is accumulated capital gains. Only the gains are taxable.",
                "The individual shares of capital gain and return of principal are calculated dynamically for each user based on your investment horizon, compound returns, asset structure, and retirement living expenses.",
                "Example: If capital gains tax is 23% and the user's calculated gain share in withdrawal is 60%, effective withdrawal tax = 23% × 60% = 13.8%."
            )
        ),
        BreakdownCategory(
            title = "Impact on FIRE & Retirement Horizon",
            items = listOf(
                "Extends time to retirement and increases target capital if capital structure contains stocks and bonds that must be periodically liquidated for living expenses.",
                "Default is set to the benchmark average for developed North American & EU economies (20%)."
            )
        )
    )
    override val investmentsBreakdownName: String = "Investments & Inflation"
    override val investmentsHintCollapsed: String = "What to consider in \"Investments & Inflation\"? (hint)"
    override val investmentsHintExpanded: String = "Hide hint for investments and inflation"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Nominal Capital Return (% p.a.)",
            items = listOf(
                "Weighted average expected return of your total investment portfolio",
                "Interest yields from bank deposits and savings accounts",
                "Bond coupons and anticipated stock dividend yields",
                "Net rental yield from real estate or leased equipment",
                "Developed markets benchmark (US, EU, etc.): 6–9% in hard currency",
                "Emerging markets benchmark: 10–16% in local national currency"
            )
        ),
        BreakdownCategory(
            title = "Expected Annual Inflation (% p.a.)",
            items = listOf(
                "Central banks' official targets and economic forecasts",
                "Personal household inflation based on your living expenditure basket",
                "Historical average inflation rate over the past 5–10 years",
                "Developed markets benchmark: 2–3% annually (historically 2–4%)",
                "Emerging markets benchmark: typically 5–9% annually"
            )
        ),
        BreakdownCategory(
            title = "Real Capital Return (Fisher Equation)",
            items = listOf(
                "Formula: ((1 + Nominal Return) / (1 + Inflation) - 1) * 100%",
                "Inflation-adjusted growth of real purchasing power",
                "Typical real return benchmark: 3–5% p.a. for developed markets and 3–6% for emerging markets",
                "A positive rate (> 0%) protects wealth against depreciation and grows capital"
            )
        )
    )
    override val realReturnTitle: String = "Real Capital Return:"
    override val realReturnFormula: String = "(1 + Nominal Return) / (1 + Inflation) - 1"

    override val block5Title: String = "5. Age & Planning Horizon"
    override val block5Subtitle: String = "Parameters for retirement planning"
    override val birthDateLabel: String = "Birth date (YYYY-MM-DD)"
    override val invalidBirthDateError: String = "Please specify a valid birth date"
    override val lifeExpectancyLabel: String = "Life expectancy (yrs)"
    override val fireHorizonBreakdownName: String = "Age & FIRE Horizon"
    override val fireHorizonHintCollapsed: String = "How to determine age and life expectancy? (hint)"
    override val fireHorizonHintExpanded: String = "Hide hint for age and horizon"
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Birth Date and Current Age",
            items = listOf(
                "Used to calculate age-based capital benchmark milestones (Fidelity)",
                "Defines remaining accumulation timeline for compound wealth compounding",
                "Guides optimal asset allocation between growth and capital preservation"
            )
        ),
        BreakdownCategory(
            title = "Target Life Expectancy (Years)",
            items = listOf(
                "The maximum age target covered by your financial retirement model",
                "Statistical demographic baselines: 75–85 for men, 80–90 for women",
                "For conservative financial safety, setting 85–95 years is recommended",
                "A conservative longevity buffer ensures capital never runs out prematurely"
            )
        ),
        BreakdownCategory(
            title = "Planning Horizon (Years)",
            items = listOf(
                "Formula: Target life expectancy minus Current age",
                "The overall period during which capital must sustain your living expenses",
                "Core mathematical parameter driving required FIRE capital calculations"
            )
        )
    )
    override val horizonTitle: String = "Planning Horizon:"
    override val horizonFormula: String = "(Life expectancy - Current age)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon yrs (current age: $age yrs)" }
    override val includePropertyInFireLabel: String = "Include property and debts in FIRE calculation"
    override val includePropertyInFireSubOn: String = "Net worth is used, including property minus debts"
    override val includePropertyInFireSubOff: String = "Only investable assets are used (liquid + financial)"

    override val calculateButtonText: String = "Calculate Metrics"
    override val exportButtonText: String = "Export Results"
    override val exportChooserTitle: String = "Export Results"

    override val metricsHeaderTitle: String = "Calculated Metrics"
    override val metricsHeaderSubtitle: String = "All key financial indicators with formulas & evaluation"
    override val categoryAll: String = "All"
    override val categoryCashFlow: String = "Cash Flow"
    override val categoryCapitalRisks: String = "Capital & Risks"
    override val categoryBenchmarks: String = "Benchmarks"
    override val metricCardInfo: String = "Info"
    override val metricCardHide: String = "Hide"
    override val metricCardFormulaPrefix: String = "Formula: "
    override val metricCardExpand: String = "Expand"
    override val metricCardCollapse: String = "Collapse"

    override val metricCashFlowTitle: String = "Cash Flow"
    override val metricCashFlowFormula: String = "(Income - Expenses)"
    override val metricCashFlowDesc: String = "Cash Flow. Net movement of funds (inflows and outflows) per period.\n\n• Positive value: You earn more than you spend.\n• Negative value: You spend more than you earn."
    override val metricCashFlowPositiveSub: String = "Positive cash surplus"
    override val metricCashFlowNegativeSub: String = "Budget deficit"

    override val metricSavingsRateTitle: String = "Savings Rate"
    override val metricSavingsRateFormula: String = "(Cash Flow / Income) * 100%"
    override val metricSavingsRateDesc: String = "It is recommended to aim for a savings rate above 20% of gross household income.\n\n• Below 0%: Negative indicator (deficit)\n• 0% — 20%: Neutral indicator (moderate)\n• Above 20%: Positive indicator (excellent)"
    override val metricSavingsRateSub: String = "Target benchmark: > 20% (0–20% neutral)"

    override val metricEmergencyFundTitle: String = "Emergency Fund"
    override val metricEmergencyFundFormula: String = "(Liquid Assets)"
    override val metricEmergencyFundDesc: String = "A personal safety net created to cover unforeseen emergencies (illness, job loss, emergency repairs) and safeguard financial stability without incurring high-interest debt."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Liquid + Financial"
    override val metricEmergencyFundSubOnlyLiquid: String = "Liquid Only"

    override val metricSafetyMonthsTitle: String = "Emergency Buffer (Months)"
    override val metricSafetyMonthsFormula: String = "(Emergency Fund / Monthly Expenses)"
    override val metricSafetyMonthsDesc: String = "The number of months you can sustain baseline living expenses solely from your emergency reserves.\n\n• Minimum target: 3-6 months of core living costs (rent/mortgage, food, utilities, transport). Guarantees calm recovery during income gaps.\n• Ideal target: Up to 12 months for comprehensive crisis resilience, especially for freelancers or households with dependents."
    override val metricSafetyMonthsSub: String = "Norm: 3-6 mo, Ideal: 12+ mo"
    override val monthsSuffix: String = "mo."

    override val metricDebtRatioTitle: String = "Debt-to-Asset Ratio"
    override val metricDebtRatioFormula: String = "(Liabilities / Assets)"
    override val metricDebtRatioDesc: String = "Measures the proportion of assets financed through debt obligations, reflecting financial risk exposure and dependency on creditors.\n\n• 0.0 - 0.3: Strong financial solvency, most assets funded by equity\n• 0.3 - 0.6: Balanced leverage for most households\n• Above 0.6: High risk, excessive debt dependency\n• Above 1.0: Liabilities exceed assets (insolvency risk)"
    override val metricDebtRatioLowSub: String = "Low risk (excellent)"
    override val metricDebtRatioHighSub: String = "High debt risk"

    override val metricNetWorthTitle: String = "Net Worth"
    override val metricNetWorthFormula: String = "(Assets - Liabilities)"
    override val metricNetWorthDesc: String = "The total value of all assets minus all outstanding liabilities. This represents what you truly own outright after settling all obligations."
    override val metricNetWorthSubPrefix: String = "Total assets: "
    override val metricNetWorthNegativeSub: String = "Negative equity (deficit!)"
    override val metricNetWorthAssetsFormat: (prefix: String, amount: String) -> String = { prefix, amount -> "$prefix: $amount" }
    override val metricNetWorthNegativeAssetsFormat: (prefix: String, amount: String, negative: String) -> String = { prefix, amount, negative -> "$prefix: $amount ($negative)" }

    override val metricFidelityTitle: String = "Benchmark: Fidelity Capital Milestones"
    override val metricFidelityFormula: String = "Annual Income * Multiplier"
    override val metricFidelityDesc: String = "Fidelity's rule of thumb outlines how many times your current retirement portfolio should exceed your annual salary at specific life stages.\n\nCheckpoints: 1x by age 30, 3x by 40, 6x by 50, 8x by 60, and 10x by 67.\nExample: At age 40 with $100,000 annual income, Fidelity recommends having at least $300,000 saved.\n\nNote: This application is not affiliated with or endorsed by Fidelity Investments and solely uses publicly available research milestones and educational benchmarks."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Age: $age yrs | Multiplier: ${mult}x" }
    override val metricFidelityProgress: String = "Progress toward Fidelity goal:"

    override val metricCfsTitle: String = "Financial Freedom Ratio (CFS)"
    override val metricCfsFormula: String = "(Net Assets / Annual Expenses)"
    override val metricCfsDesc: String = "Measures how many years of baseline living expenses are covered by your net assets (net worth).\n\n• CFS < 0 — You are in net debt\n• CFS 0..0.5 — Minimal emergency cushion\n• CFS 0.5..2 — Initial capital accumulation stage\n• CFS 2..5 — Long-term investing stage\n• CFS 5..15 — Investment returns provide tangible income. Inflation impact grows\n• CFS 15..30 — Solid financial stability. How do you value your irreplaceable time?\n• CFS > 30 — Complete financial independence achieved"

    override val metricRealReturnTitle: String = "Real Return on Capital, %"
    override val metricRealReturnFormula: String = "(1 + Nominal Return) / (1 + Inflation) - 1"
    override val metricRealReturnDesc: String = "The net real expansion of purchasing power after deducting inflation from nominal investment yields. Shows real wealth creation."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Nominal: $nom | Inflation: $infl" }
    override val metricUnavailableValue: String = "—"

    override val scoresHeaderTitle: String = "Score Summary"
    override val scoresHeaderSubtitle: String = "Overall audit score & breakdown"
    override val scoresShareReportChooser: String = "Share Report"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "of $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Overall Score: $score of 10" }
    override val scoresStatusExcellent: String = "Outstanding financial resilience and readiness for long-term wealth compounding."
    override val scoresStatusGood: String = "Solid financial health with strong potential to increase savings rate."
    override val scoresStatusModerate: String = "Moderate stability. Recommended to strengthen your emergency cash buffer."
    override val scoresStatusAttention: String = "Budget optimization and aggressive debt reduction are urgently recommended."
    override val scoresBreakdownTitle: String = "Component Score Breakdown:"
    override val scoreValuePrefix: String = "Value: "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score of 10" }
    override val scoreSlashOutOfTen: (score: String) -> String = { score -> "$score / 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String) -> String = { rate, sr ->
        when {
            rate > 20.0 -> "Recommended above 20%. You maintain an excellent rate ($sr)."
            rate >= 0.0 -> "Recommended above 20%. You have a low rate ($sr)."
            else -> "Recommended above 20%. You have a negative rate ($sr), budget deficit."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String) -> String = { months, monthsText ->
        when {
            months >= 12.0 -> "Minimum 3-6 months, ideal 12+ months. Excellent result: buffer exceeds 12 months ($monthsText)."
            months >= 6.0 -> "Minimum 3-6 months, ideal 12+ months. Good buffer ($monthsText), 6-month benchmark achieved."
            months >= 3.0 -> "Minimum 3-6 months, ideal 12+ months. Basic buffer ($monthsText), expanding to 6-12 months recommended."
            else -> "Minimum 3-6 months, ideal 12+ months. You have an insufficient buffer ($monthsText), elevated financial risk."
        }
    }
    override val debtComment0: String = "0.0 - 0.3: Excellent stability, assets funded almost entirely by own equity."
    override val debtComment1: String = "0.3 - 0.6: Balanced leverage, manageable borrowing level."
    override val debtComment2: String = "0.6 - 1.0: Elevated risk, substantial dependence on borrowed money."
    override val debtComment3: String = "Above 1.0: Critical risk, total debts exceed the value of all assets."

    override val auditorOpinionTitle: String = "Auditor Opinion"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Analysis across all $count indicators from the Metrics screen" }
    override val shareAuditorOpinionChooser: String = "Share Auditor Opinion"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count positive" }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count neutral" }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count negative" }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Positive Indicators ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Neutral Indicators ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Negative Indicators ($count)" }
    override val positiveEmptyText: String = "No positive factors identified yet. Recommended to optimize income and expenses."
    override val neutralEmptyText: String = "No neutral indicators — all metrics fall into either positive or negative zones."
    override val negativeEmptyText: String = "🎉 No critical or negative risk factors found — all metrics are in safe territory!"

    override val fireHeroTitle: String = "Financial Independence, FIRE"
    override val fireHeroSubtitle: String = "Retirement timeline and wealth projection"
    override val fireTimeToRetire: String = "Time to retirement:"
    override val fireYearsSuffix: String = " years"
    override val fireHeroAssumption: String = "FIRE calculation considers liquid and financial assets; property is not included, and debts are deducted only to the extent they exceed property value"
    override val fireRetirementAgeLabel: String = "Retirement age:"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age yrs" }
    override val fireTargetCapitalLabel: String = "Target required capital:"
    override val fireParamsTitle: String = "Age & Planning Horizon Parameters"
    override val fireBirthDateLabel: String = "Date of birth:"
    override val fireCurrentAgeLabel: String = "Current age:"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age yrs" }
    override val fireLifeExpectancyLabel: String = "Life expectancy:"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp yrs" }
    override val fireHorizonLabel: String = "(Life expectancy - Current age):"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h yrs" }
    override val fireRealReturnLabel: String = "Real return on capital:"
    override val fireTrajectoryTitle: String = "Capital Accumulation Trajectory"
    override val fireTrajectorySubtitle: String = "Wealth growth toward the FIRE point and funding lifelong retirement"
    override val fireChartAccumulated: String = "Accumulated capital"
    override val fireChartRequired: String = "Required capital"
    override val fireChartYearsSuffix: String = " yrs"
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE: $age yrs" }
    override val fireSimSummaryTitle: String = "Simulation Summary (Combined Impact)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ Retirement achieved $diff yrs earlier (in $target yrs, at age $age)!" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Time to retirement increased by $diff yrs (in $target yrs, at age $age)." }
    override val fireSimSame: (target: String) -> String = { target -> "Retirement timeline remains unchanged (in $target yrs)." }
    override val fireSimWithoutBase: (target: String, age: String) -> String = { target, age -> "Retirement reachable in $target yrs (at age $age yrs)." }
    override val fireUnreachableText: String = "Unachievable with current parameters"
    override val fireSimCashFlowRow: String = "Cash flow (savings):"
    override val fireSimExpensesRow: String = "Monthly expenses:"
    override val fireSimNetWorthRow: String = "Net worth (assets − debts):"
    override val fireSimPropertyRow: String = "Property increase (at loan completion):"
    override val fireSimSliderTitle: String = "Simulator \"What If?\""
    override val fireSimReset: String = "Reset"
    override val fireSimFineTuning: String = "Fine-tune scales (sliders):"
    override val fireSliderSavings: String = "Monthly savings:"
    override val fireSliderExpenses: String = "Monthly expenses:"
    override val fireSliderReturn: String = "Real return on capital:"
    override val fireSliderBaseSuffix: String = " to base"
    override val fireSliderBaseReturnSuffix: String = "% to base"
    override val fireSliderFrom: (v: String) -> String = { v -> "from $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "step $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "up to +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "growth"
    override val fireSliderDec: String = "red."

    override val scenarioHeaderTitle: String = "Scenario Panel (Stress Tests)"
    override val scenarioHeaderSubtitle: String = "The collective impact of sliders and checkboxes accumulates and is calculated together. The final result reflects in numbers and on the trajectory chart:"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Total: $count" }
    override val scenarioPositiveTitle: String = "Positive Events"
    override val scenarioPositiveSectionNumber: String = "2.1"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Active: $count" }
    override val scenarioPositiveSubtitle: String = "Events that accelerate capital accumulation and retirement:"
    override val scenarioNegativeTitle: String = "Negative Events"
    override val scenarioNegativeSectionNumber: String = "2.2"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Active: $count" }
    override val scenarioNegativeSubtitle: String = "Stress tests, unforeseen expenses and crisis scenarios:"
    override val scenarioParamsPrefix: String = "Parameters: "

    override val scenarioSalaryIncreaseTitle: String = "Salary Increase"
    override val scenarioSalaryIncreaseParam: String = "income +20% monthly, permanent"
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Income increase: +$amt/mo." }

    override val scenarioMarketGrowthTitle: String = "Market Growth +25%"
    override val scenarioMarketGrowthParam: String = "ETF +25%"
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Financial portfolio gain: +$amt" }

    override val scenarioExpenseOptTitle: String = "Expense Optimization"
    override val scenarioExpenseOptParam: String = "−15% to monthly expenses, permanent"
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Ongoing savings: -$amt/mo." }

    override val scenarioRelocationTitle: String = "Relocation to Lower-Cost Region"
    override val scenarioRelocationParam: String = "−30% to monthly expenses, permanent"
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Living cost reduction: -$amt/mo." }

    override val scenarioInheritanceTitle: String = "Inheritance or Windfall Gift"
    override val scenarioInheritanceParam: String = "one-time windfall 1200% monthly income"
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Capital inflow: +$amt (into liquid assets)" }

    override val scenarioPropertySaleTitle: String = "Sale of Property (car, land, real estate)"
    override val scenarioPropertySaleParam: String = "one-time windfall 2000% monthly income"
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Sale proceeds: +$amt (into liquid assets)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Children Become Independent"
    override val scenarioChildrenSelfSufficientParam: String = "expenses −25% in 18 yrs (child costs already in your expenses)"
    override val scenarioChildrenSelfSufficientDesc: (amt: String) -> String = { amt -> "Family expense reduction: -$amt/mo. (in 18 yrs)" }

    override val scenarioJobLossTitle: String = "Job Loss"
    override val scenarioJobLossParam: String = "12 months of lost income"
    override val scenarioJobLossDesc: String = "12 months of income lost, covered from savings"

    override val scenarioMarketCrashTitle: String = "Market Crash −30%"
    override val scenarioMarketCrashParam: String = "ETF −30%"
    override val scenarioMarketCrashDesc: (amt: String) -> String = { amt -> "Portfolio drawdown of 30% ($amt)" }

    override val scenarioMortgageTitle: String = "Home Mortgage"
    override val scenarioMortgageParam: String = "35% income for 20 yrs"
    override val scenarioMortgageDesc: (pay: String) -> String = { pay -> "Payment: $pay/mo. (20 yrs)" }

    override val scenarioCarLoanTitle: String = "Car Loan"
    override val scenarioCarLoanParam: String = "20% income for 5 yrs"
    override val scenarioCarLoanDesc: (pay: String) -> String = { pay -> "Payment: $pay/mo. (5 yrs)" }

    override val scenarioLargeExpenseTitle: String = "Major One-Off Expense"
    override val scenarioLargeExpenseParam: String = "expense 500% monthly income"
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Outflow of $amt (covered from liquid and financial assets)" }

    override val scenarioInflationTitle: String = "Expense Increase (Inflation)"
    override val scenarioInflationParam: String = "+25% to monthly expenses"
    override val scenarioInflationDesc: (amt: String) -> String = { amt -> "Expenses increase: +$amt/mo." }

    override val scenarioChildTitle: String = "Child Birth"
    override val scenarioChildParam: String = "+25% to expenses for 18 yrs"
    override val scenarioChildDesc: (amt: String) -> String = { amt -> "Childcare costs: +$amt/mo. (for 18 years)" }

    override val disclaimerTitle: String = "Disclaimer"
    override val disclaimerText: String = "Important: This project is a personal development and is provided solely for informational purposes \"as is\". The author is not responsible for any malfunctions, data loss, or other damages arising from the use of this software. All calculations, formulas, metrics, and simulations are for informational purposes only and do not constitute individual financial, investment, or tax advice. You use this tool at your own risk."
    override val assumptionsDisclaimerTitle: String = "Disclaimer"
    override val assumptionsDisclaimerBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> "Calculations assume a constant return of $returnRate and inflation of $inflation, without taxes or sequence-of-returns effects. This is a model, not a forecast.\n\nThe model does not account for taxes, sequence-of-returns effects, or income inflation. This is a guide, not a guarantee." }

    override fun scenarioSavedSuccess(name: String): String = "Scenario \"$name\" saved successfully"
    override fun scenarioSaveError(error: String): String = "Failed to save scenario: $error"
    override val scenarioDatabaseError: String = "database error"
    override fun scenarioLoadedSuccess(name: String): String = "Scenario \"${name}\" loaded"
    override fun scenarioLoadError(error: String): String = "Failed to load scenario: $error"
    override val scenarioCorruptedDataError: String = "corrupted data"
    override fun scenarioDeletedSuccess(name: String): String = "Scenario \"${name}\" deleted"
    override fun scenarioDeleteError(error: String): String = "Failed to delete scenario: $error"

    // Pluralization
    override fun plural(n: Int, one: String, many: String): String = if (n == 1) one else many
    override fun plural(n: Int, one: String, few: String, many: String): String = if (n == 1) one else many
    override fun pluralYears(n: Int): String = "$n " + plural(n, "year", "years")
    override fun pluralMonths(n: Int): String = "$n " + plural(n, "month", "months")
    override fun pluralPoints(n: Int): String = "$n " + plural(n, "point", "points")
    override val checkboxOne: String = "checkbox"
    override val checkboxFew: String = "checkboxes"
    override val checkboxMany: String = "checkboxes"
    override val simulationBadgeSlidersSuffix: String = "sliders"
    override val simulationBadgeOnlySliders: String = "Adjustment sliders"

    // Score & Safety Months formats
    override val safetyMonthsValueFormat: String = "%1${'$'}s mo."
    override val shareScoreReportTemplate: String = "📊 Financial Report & Capital Audit:\n• Overall Score: %1${'$'}s of 10\n• Savings Rate: %2${'$'}s/10 (%3${'$'}s)\n• Expense Buffer: %4${'$'}s/10 (%5${'$'}s mo.)\n• Debt Ratio: %6${'$'}s/10 (%7${'$'}s)\n• Financial Freedom Ratio: %8${'$'}s/10 (%9${'$'}s)\n• Time to Retirement (FIRE): %10${'$'}s yrs (at age %11${'$'}s yrs)\n"

    // CFS Stage descriptions
    override val cfsStageDebt: String = "CFS < 0 — You are in debt"
    override val cfsStageBuffer: String = "CFS 0..0.5 — You have a minimal emergency buffer"
    override val cfsStageCapital: String = "CFS 0.5..2 — You have started building financial capital"
    override val cfsStageInvesting: String = "CFS 2..5 — You can begin engaging in long-term investing"
    override val cfsStageReturns: String = "CFS 5..15 — Investment returns provide significant income. Inflation affects capital more"
    override val cfsStageStability: String = "CFS 15..30 — Solid financial stability. How do you spend your irreplaceable time?"
    override val cfsStageIndependence: String = "CFS > 30 — You have achieved financial independence"

    // Auditor Opinion Share Text
    override val auditShareHeaderTitle: String = "📋 AUDITOR OPINION ACROSS ALL INDICATORS"
    override val auditShareScoreLine: String = "⭐️ Overall score: %1${'$'}s of 10 points"
    override val auditShareTotalCountLine: String = "Total indicators analyzed: %1${'$'}d"
    override val auditSharePositiveHeader: String = "🟢 POSITIVE INDICATORS (%1${'$'}d):"
    override val auditSharePositiveEmpty: String = "• No positive indicators identified"
    override val auditShareNeutralHeader: String = "🟡 NEUTRAL INDICATORS (%1${'$'}d):"
    override val auditShareNeutralEmpty: String = "• No neutral indicators"
    override val auditShareNegativeHeader: String = "🔴 NEGATIVE INDICATORS (%1${'$'}d):"
    override val auditShareNegativeEmpty: String = "• No critical or negative factors detected 🎉"
    override val auditShareGeneratedBy: String = "💡 Generated in \"Personal Finance\" app"
    override val auditShareDisclaimer: String = "⚠️ Important: This project is a personal development and is provided solely for informational purposes \"as is\". The author is not responsible for any malfunctions, data loss, or other damages arising from the use of this software. All calculations, formulas, metrics, and simulations are for informational purposes only and do not constitute individual financial, investment, or tax advice. You use this tool at your own risk."

    // Auditor Indicators
    override val auditCashFlowTitle: String = "Cash Flow"
    override val auditCashFlowPositive: String = "✅ Cash Flow (+%1${'$'}s/mo.): Budget surplus. Your income exceeds expenses, generating a regular investment resource for wealth accumulation."
    override val auditCashFlowNeutral: String = "ℹ️ Cash Flow (0 %1${'$'}s/mo.): Balanced budget (income equals expenses). You do not accumulate debt, but lack a surplus to build capital."
    override val auditCashFlowNegative: String = "🚨 Cash Flow (%1${'$'}s/mo.): Budget deficit! Current expenses exceed income, depleting reserves or raising debt."

    override val auditSavingsRateTitle: String = "Savings Rate"
    override val auditSavingsRatePositive: String = "✅ Savings Capacity (Savings Rate: %1${'$'}s): High savings rate (above 20%%). Provides steady asset accumulation and exponential wealth compounding."
    override val auditSavingsRateNeutral: String = "ℹ️ Savings Capacity (Savings Rate: %1${'$'}s): Neutral range (0–20%%). Capital accumulation proceeds steadily; expense optimization can help reach the >20%% target."
    override val auditSavingsRateNegative: String = "🚨 Savings Capacity (Savings Rate: %1${'$'}s): Negative cash flow (budget deficit)! Expenses exceed income, depleting assets and increasing debt."

    override val auditEmergencyFundTitle: String = "Emergency Fund"
    override val auditEmergencyFundPositive: String = "✅ Emergency Fund (Reserve Volume: %1${'$'}s): Liquid reserve is fully sufficient to cover unexpected emergencies and crises without borrowing."
    override val auditEmergencyFundNeutral: String = "ℹ️ Emergency Fund (Reserve Volume: %1${'$'}s): Baseline minimal reserve established. Recommended to gradually expand to 6 months of living expenses."
    override val auditEmergencyFundNegative: String = "⚠️ Emergency Fund (Reserve Volume: %1${'$'}s): Critically low reserve. High vulnerability to unforeseen costs and forced borrowing."

    override val auditSafetyMonthsTitle: String = "Emergency Buffer (Months)"
    override val auditSafetyMonthsHigh: String = "✅ Financial Cushion (Emergency Buffer: %1${'$'}s mo.): Fully formed with ample margin. Income loss risks are mitigated. Excess liquidity above 12 months should be deployed into productive investments."
    override val auditSafetyMonthsTarget: String = "✅ Financial Cushion (Emergency Buffer: %1${'$'}s mo.): Target 6-month norm achieved. Baseline family financial security is assured."
    override val auditSafetyMonthsNeutral: String = "ℹ️ Financial Cushion (Emergency Buffer: %1${'$'}s mo.): Minimal baseline reserve created; recommended to expand to 6 months for better crisis resilience."
    override val auditSafetyMonthsNegative: String = "⚠️ Financial Cushion (Emergency Buffer: %1${'$'}s mo.): Critically insufficient reserve (under 3 months). Cash crunch risks creating debt traps. Priority: build a 6-month reserve urgently."

    override val auditDebtRatioTitle: String = "Debt Ratio"
    override val auditDebtRatioZero: String = "✅ Debt Load (Debt Ratio: 0.00): Absolute financial autonomy — no debts or loans, 100% of assets are owned outright."
    override val auditDebtRatioConservative: String = "✅ Debt Load (Debt Ratio: %1${'$'}s, debt %2${'$'}s of assets): Conservative and safe leverage. Obligations are fully backed by assets; credit risk is minimal."
    override val auditDebtRatioModerate: String = "ℹ️ Debt Load (Debt Ratio: %1${'$'}s, debt %2${'$'}s of assets): Moderate borrowing level. Debt is controlled but requires discipline during major purchases."
    override val auditDebtRatioHigh: String = "⚠️ Warning: High Debt Load (Debt Ratio: %1${'$'}s, debt %2${'$'}s of assets)! Heavy reliance on creditors. Refinancing and debt repayment advised."
    override val auditDebtRatioCritical: String = "🚨 CRITICAL RISK: Negative Equity Margin (Debt Ratio: %1${'$'}s, debts exceed assets by %2${'$'}s)! Obligations exceed total asset value. Emergency restructuring required."

    override val auditNetWorthTitle: String = "Net Worth"
    override val auditNetWorthStrong: String = "✅ Net Worth (Own Capital: %1${'$'}s): Strong positive balance. Aggregate assets comfortably exceed financial liabilities, providing a solid foundation."
    override val auditNetWorthBalanced: String = "ℹ️ Net Worth (Own Capital: %1${'$'}s): Assets and liabilities are closely matched. Value is positive, but significant assets are encumbered by debts."
    override val auditNetWorthNegative: String = "🚨 Net Worth (Negative Balance: %1${'$'}s): Negative net worth (technical insolvency). Total liabilities exceed all possessed assets."

    override val auditFidelityTitle: String = "Fidelity Benchmark"
    override val auditFidelityPositive: String = "✅ Age Capital Milestone (Fidelity Benchmark: %1${'$'}s of goal): Capital exceeds target milestone for your age (%2${'$'}s). Excellent long-term trajectory."
    override val auditFidelityNeutral: String = "ℹ️ Age Capital Milestone (Fidelity Benchmark: %1${'$'}s of goal): Capital matches recommended age savings guidelines (%2${'$'}s)."
    override val auditFidelityNegativeInsolvent: String = "🚨 Age Capital Milestone (Fidelity Benchmark): Net capital is negative (%1${'$'}s). Outstanding liabilities prevent reaching recommended retirement milestone (%2${'$'}s)."
    override val auditFidelityNegativeLagging: String = "⚠️ Age Capital Milestone (Fidelity Benchmark: %1${'$'}s of goal): Lagging behind recommended age benchmark (%2${'$'}s). Accelerating savings pace advised."
    override val auditFidelityUnder30: String = "✅ Age Capital Milestone (Fidelity Benchmark): Under age 30, the primary focus is laying down early savings foundations. Fidelity multiplier milestones begin at age 30 (1x annual salary)."

    override val auditCfsTitle: String = "Financial Freedom Ratio"
    override val auditCfsIndependence: String = "🎉 Financial Freedom (CFS: %1${'$'}s): You have achieved complete financial independence (FIRE)! Accumulated net assets can support your current living standards indefinitely."
    override val auditCfsStability: String = "✅ Financial Freedom (CFS: %1${'$'}s): High degree of autonomy. Net assets yield substantial passive income covering most necessary living expenses."
    override val auditCfsGrowth: String = "✅ Financial Freedom (CFS: %1${'$'}s): Solid capital compounding phase (%2${'$'}s). Investment returns yield noticeable impact. Next target: CFS > 15."
    override val auditCfsInitial: String = "ℹ️ Financial Freedom (CFS: %1${'$'}s): Initial stage of investment capital (%2${'$'}s). Primary focus on disciplined regular savings."
    override val auditCfsNegative: String = "🚨 Financial Freedom (CFS: %1${'$'}s): Negative ratio (%2${'$'}s). Total obligations exceed assets, resulting in negative net worth."
    override val auditCfsBuffer: String = "📈 Financial Freedom (CFS: %1${'$'}s): Early stage of capital accumulation (%2${'$'}s). Current volume is insufficient for financial autonomy; disciplined saving required."
    override val auditCfsPathToIndependence: String = "You are on the path to independence, but still far from the goal."
    override val auditCfsCloseToIndependence: (percent: Int) -> String = { percent -> "You are close: capital covers $percent% of the target." }
    override val auditCfsFinanciallyIndependent: String = "You are financially independent under the 4% rule."

    override val auditRealReturnTitle: String = "Real Return on Capital"
    override val auditRealReturnPositive: String = "✅ Real Return on Capital (+%1${'$'}s): Asset yield outpaces inflation (%2${'$'}s vs %3${'$'}s). Capital is protected against erosion and expands purchasing power."
    override val auditRealReturnNeutral: String = "ℹ️ Real Return on Capital (0.0%): Yield precisely offsets inflation. Capital preserves nominal purchasing power, but produces no real wealth expansion."
    override val auditRealReturnNegative: String = "⚠️ Real Return on Capital (%1${'$'}s): Inflation (%2${'$'}s) exceeds capital returns (%3${'$'}s). Hidden depreciation of savings and loss of purchasing power occurring."

    // Export Summary
    override val exportHeaderTitle: String = "📊 FINANCIAL REPORT — PERSONAL FINANCE"
    override val exportSection1Title: String = "1. Input Data:"
    override val exportIncomeLine: String = "• Income: %1${'$'}s/mo."
    override val exportExpensesLine: String = "• Expenses: %1${'$'}s/mo."
    override val exportLiquidAssetsLine: String = "• Liquid assets: %1${'$'}s"
    override val exportFinancialAssetsLine: String = "• Financial assets: %1${'$'}s"
    override val exportPropertyAssetsLine: String = "• Real estate / property: %1${'$'}s"
    override val exportLiabilitiesLine: String = "• Liabilities (debts): %1${'$'}s"
    override val exportAgeHorizonLine: String = "• Age: %1${'$'}d yrs (life expectancy: %2${'$'}d yrs, horizon: %3${'$'}d yrs)"
    override val exportReturnsInflationLine: String = "• Nominal return: %1${'$'}s | Inflation: %2${'$'}s"
    override val exportTaxLine: String = "• Capital gains tax: %1${'$'}s"

    override val exportSection2Title: String = "2. Calculated Metrics:"
    override val exportCashFlowLine: String = "• Cash flow: %1${'$'}s/mo."
    override val exportSavingsRateLine: String = "• Savings rate: %1${'$'}s"
    override val exportEmergencyFundLine: String = "• Emergency fund: %1${'$'}s (%2${'$'}s mo.)"
    override val exportDebtRatioLine: String = "• Debt ratio: %1${'$'}s"
    override val exportNetWorthLine: String = "• Net worth: %1${'$'}s"
    override val exportRealReturnLine: String = "• Real return: %1${'$'}s"
    override val exportEffectiveTaxLine: String = "• Effective capital gains tax on withdrawal: %1${'$'}s"

    override val exportSection3Title: String = "3. Score Summary:"
    override val exportScoreTotalLine: String = "• Overall score: %1${'$'}s of 10"
    override val exportScoreSavingsRateLine: String = "• Savings rate: %1${'$'}d / 10"
    override val exportScoreSafetyMonthsLine: String = "• Emergency buffer: %1${'$'}d / 10"
    override val exportScoreDebtRatioLine: String = "• Debt load: %1${'$'}d / 10"
    override val exportScoreCfsLine: String = "• Financial freedom: %1${'$'}d / 10"

    override val exportSection4Title: String = "4. Financial Independence, FIRE:"
    override val exportCfsLine: String = "• CFS: %1${'$'}s (%2${'$'}s)"
    override val exportFireTargetCapitalLine: String = "• Target FIRE capital: %1${'$'}s"
    override val exportFireTimeToRetirementLine: String = "• Time to retirement: %1${'$'}s yrs (at age %2${'$'}s yrs)"
    override val exportFidelityLine: String = "• Fidelity benchmark: %1${'$'}s (%2${'$'}sx annual income) — %3${'$'}s of goal"
}

fun plural(n: Int, one: String, many: String): String = if (n == 1) one else many
