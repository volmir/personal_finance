package com.example.util.localization

val StringsDe: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.DE

    override val appName: String = "Persönliche Finanzen & FIRE-Audit"
    override val tabInputTitle: String = "Eingabedaten & Parameter"
    override val tabInputShort: String = "Eingaben"
    override val tabMetricsTitle: String = "Berechnete Kennzahlen"
    override val tabMetricsShort: String = "Kennzahlen"
    override val tabScoresTitle: String = "Punktebewertung & Finanzprüfung"
    override val tabScoresShort: String = "Bewertung & Audit"
    override val tabFireTitle: String = "Finanzielle Freiheit, Ruhestand, FIRE"
    override val tabFireShort: String = "FIRE & Plan"
    override val quickScorePrefix: String = "Punktzahl"

    override val sampleDataNoticeMessage: String = "Beim ersten Start sind Beispielwerte eingetragen — prüfen und ersetzen Sie sie durch Ihre eigenen"
    override val sampleDataNoticeConfirm: String = "Verstanden"

    override val block1Title: String = "1. Einnahmen & Ausgaben"
    override val block1Subtitle: String = "Monatliche Geldzuflüsse und Ausgaben"
    override val monthlyIncomeLabel: String = "Einkommen (monatlich)"
    override val monthlyExpensesLabel: String = "Ausgaben (monatlich)"
    override val incomeBreakdownName: String = "Einkommen"
    override val incomeHintCollapsed: String = "Was zählt zum \"Einkommen\"? (Hinweis)"
    override val incomeHintExpanded: String = "Struktur von \"Einkommen\" ausblenden"
    override val expensesBreakdownName: String = "Ausgaben"
    override val expensesHintCollapsed: String = "Was zählt zu den \"Ausgaben\"? (Hinweis)"
    override val expensesHintExpanded: String = "Struktur von \"Ausgaben\" ausblenden"
    override val breakdownInstruction: String = "Berechnen und summieren Sie Ihre Posten aus der Liste unten selbst und tragen Sie die Gesamtsumme oben ein."
    override val breakdownInstructionIcon: String = "💡 "
    override val breakdownCategoryTitleFormat: (title: String) -> String = { title -> "• $title" }
    override val breakdownCategoryItemFormat: (item: String) -> String = { item -> "    – $item" }
    override val incomeCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Anstellung & Gehalt",
            items = listOf(
                "Hauptbeschäftigung (Grundgehalt, Boni, Überstunden)",
                "Nebentätigkeit, Nebenjob, Teilzeitarbeit"
            )
        ),
        BreakdownCategory(
            title = "Unternehmen & Selbstständigkeit",
            items = listOf(
                "Nettogewinn aus eigenem Gewerbe / Einzelunternehmen",
                "Freiberufliche Tätigkeit, Beratung, Einzelaufträge"
            )
        ),
        BreakdownCategory(
            title = "Passives Einkommen & Kapitalerträge",
            items = listOf(
                "Zinsen auf Tagesgeld, Festgeld und Sparkonten",
                "Aktien-Dividenden und Anleihekupons",
                "Mieteinnahmen aus Immobilien oder Fahrzeugen",
                "Lizenzgebühren, Urheberrechte"
            )
        ),
        BreakdownCategory(
            title = "Soziale & sonstige Zuflüsse",
            items = listOf(
                "Renten, staatliche Leistungen, Beihilfen",
                "Unterhalt, regelmäßige Unterstützung durch die Familie",
                "Steuerrückerstattungen, Cashbacks"
            )
        )
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Grundlegende Lebenshaltungskosten",
            items = listOf(
                "Lebensmittel und Haushaltswaren",
                "Miete oder Tilgung/Zinsen des Hauptwohnsitzes",
                "Nebenkosten (Strom, Gas, Wasser, Heizung)",
                "Telekommunikation, Internet, Mobilfunk"
            )
        ),
        BreakdownCategory(
            title = "Verkehr & Pendeln",
            items = listOf(
                "Öffentlicher Nahverkehr, Monatskarten",
                "Kraftstoff, Parkgebühren, Autowäsche",
                "Fahrzeugwartung, Kfz-Versicherung, Reparaturen",
                "Taxi, Fahrdienste, Carsharing"
            )
        ),
        BreakdownCategory(
            title = "Gesundheit & Körperpflege",
            items = listOf(
                "Medikamente, Arztbesuche, Zahnbehandlung, Krankenversicherung",
                "Kleidung, Schuhe, Körperpflege",
                "Sport, Fitnessstudio, Vereinsbeiträge"
            )
        ),
        BreakdownCategory(
            title = "Freizeit, Bildung & Familie",
            items = listOf(
                "Restaurants, Cafés, Essenslieferdienste",
                "Unterhaltung, Kino, Reisen, Hobbys",
                "Ausbildung, Weiterbildung, Kurse, Bücher, Aktivitäten für Kinder",
                "Geschenke, Spenden, unvorhergesehene Ausgaben"
            )
        ),
        BreakdownCategory(
            title = "Schuldendienst",
            items = listOf(
                "Monatliche Mindestraten für Ratenkredite und Kreditkarten"
            )
        )
    )
    override val cashFlowTitle: String = "Cashflow:"
    override val cashFlowFormula: String = "(Einkommen - Ausgaben)"
    override val savingsRateTitle: String = "Sparquote:"
    override val savingsRateFormula: String = "(Cashflow / Einkommen) * 100%"

    override val block2Title: String = "2. Vermögen (Σ)"
    override val block2Subtitle: String = "Liquides, finanzielles und materielles Vermögen"
    override val liquidAssetsLabel: String = "Liquide (Bargeld, Girokonto, Sichteinlagen)"
    override val financialAssetsLabel: String = "Finanziell (Aktien, Anleihen, ETFs, Depot)"
    override val propertyAssetsLabel: String = "Sachwerte / Immobilien (Immobilien, Fahrzeuge, Wertsachen)"
    override val assetsBreakdownName: String = "Vermögen"
    override val assetsHintCollapsed: String = "Was zählt zum \"Vermögen\"? (Hinweis)"
    override val assetsHintExpanded: String = "Struktur von \"Vermögen\" ausblenden"
    override val assetsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Liquide Mittel",
            items = listOf(
                "Bargeldbestände in beliebigen Währungen",
                "Guthaben auf Giro- und Debitkonten",
                "Tagesgeldkonten und Geldmarktfonds",
                "Kurzfristige Festgelder (bis zu 1–3 Monate)"
            )
        ),
        BreakdownCategory(
            title = "Finanzielle (Investierte) Anlagen",
            items = listOf(
                "Aktien, ETFs, Investmentfonds",
                "Anleihen (Staats-, Kommunal-, Unternehmensanleihen)",
                "Depotguthaben und steuerbegünstigte Konten",
                "Längerfristige Festgelder und Sparbriefe",
                "Edelmetalle (Barren, Münzen, Wertpapierbestände)",
                "Kryptowährungen und digitale Vermögenswerte",
                "Private Renten- und Pensionskonten"
            )
        ),
        BreakdownCategory(
            title = "Materielles Vermögen (Sachwerte)",
            items = listOf(
                "Wohnimmobilien (Eigenheim, Wohnung, Grundstück)",
                "Gewerbeimmobilien, Stellplätze, Garagen",
                "Fahrzeuge (PKW, Motorräder, Boote)",
                "Maschinen, Ausrüstung, Antiquitäten, Schmuck",
                "Beteiligungen an nicht-börsennotierten operativen Unternehmen"
            )
        )
    )
    override val totalAssetsTitle: String = "Gesamtvermögen (Σ):"
    override val totalAssetsFormula: String = "(Liquide + Finanziell + Sachwerte)"
    override val toggleEmergencyFundTitle: String = "Finanzanlagen in Notgroschen einbeziehen"
    override val toggleEmergencyFundSubOn: String = "Puffer = Liquide + Finanziell"
    override val toggleEmergencyFundSubOff: String = "Puffer = Nur liquide Mittel"

    override val block3Title: String = "3. Verbindlichkeiten"
    override val block3Subtitle: String = "Schulden, Kredite, Kreditkarten und Hypotheken"
    override val liabilitiesLabel: String = "Verbindlichkeiten (alle Schulden und Pflichten)"
    override val liabilitiesBreakdownName: String = "Verbindlichkeiten"
    override val liabilitiesHintCollapsed: String = "Was zählt zu den \"Verbindlichkeiten\"? (Hinweis)"
    override val liabilitiesHintExpanded: String = "Struktur von \"Verbindlichkeiten\" ausblenden"
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Besicherte & langfristige Schulden",
            items = listOf(
                "Restschuld der Baufinanzierung / Hypothek",
                "Autokredite (Restforderung)",
                "Besicherte Eigenheim- und Sanierungsdarlehen"
            )
        ),
        BreakdownCategory(
            title = "Konsum- & kurzfristige Kredite",
            items = listOf(
                "Ungesicherte Ratenkredite",
                "Kreditkartensalden und Dispositionskredite",
                "Ratenzahlungspläne, BNPL-Dienste, Mikrokredite"
            )
        ),
        BreakdownCategory(
            title = "Private Schulden",
            items = listOf(
                "Schulden gegenüber Verwandten, Freunden oder Kollegen",
                "Private Schuldscheine und Darlehen"
            )
        ),
        BreakdownCategory(
            title = "Sonstige finanzielle Verpflichtungen",
            items = listOf(
                "Steuerrückstände, offene Mahnungen, Bußgelder",
                "Rückständige Nebenkosten"
            )
        )
    )
    override val netWorthTitle: String = "Reinvermögen:"
    override val netWorthFormula: String = "(Vermögen - Verbindlichkeiten)"

    override val block4Title: String = "4. Investitionen, Inflation & Steuern"
    override val block4Subtitle: String = "Parameter für Rendite, Kaufkraft und Kapitalertragsteuer"
    override val nominalReturnLabel: String = "Nominale Rendite %"
    override val inflationLabel: String = "Inflation %"
    override val capitalGainsTaxLabel: String = "Kapitalertragsteuer (%)"
    override val capitalGainsTaxBreakdownName: String = "Kapitalertragsteuer"
    override val capitalGainsTaxHintCollapsed: String = "Wie wird die Kapitalertragsteuer berechnet? (Hinweis)"
    override val capitalGainsTaxHintExpanded: String = "Details zur Kapitalertragsteuer ausblenden"
    override val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String = { tax, eff -> "Steuersatz: $tax | Effektive Entnahmesteuer: $eff" }
    override val capitalGainsTaxFormula: String = "Steuersatz * Gewinnanteil * Finanzanlagenquote"
    override val capitalGainsTaxCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Steuerprinzip & Bemessungsgrundlage",
            items = listOf(
                "Die Steuer wird nicht auf den gesamten Verkaufserlös erhoben, sondern nur auf den realisierten Kursgewinn zwischen Verkaufspreis und Anschaffungskosten.",
                "Der Steuersatz gilt für Gewinne aus dem Verkauf von Wertpapieren (Differenz zwischen Verkaufs- und Kaufpreis)."
            )
        ),
        BreakdownCategory(
            title = "Realistisches Entnahmemodell",
            items = listOf(
                "Bei der Entnahme von Vermögen besteht ein Teil aus dem ursprünglichen Anlagekapital und ein Teil aus aufgelaufenen Kursgewinnen. Nur die Gewinne sind steuerpflichtig.",
                "Die Anteile von Kursgewinn und Kapitalrückzahlung werden individuell anhand des Anlagehorizonts, des Zinseszinseffekts, der Vermögensstruktur und der Entnahmerate ermittelt.",
                "Beispiel: Bei einem Steuersatz von 25% und einem Gewinnanteil an der Entnahme von 60% beträgt die effektive Steuer = 25% × 60% = 15%."
            )
        ),
        BreakdownCategory(
            title = "Auswirkung auf FIRE & Ruhestandshorizont",
            items = listOf(
                "Verlängert die Zeit bis zur Rente und erhöht das Zielkapital, wenn Aktien und Anleihen zur Deckung der Lebenshaltungskosten verkauft werden müssen.",
                "Der Standardwert entspricht dem Durchschnitt entwickelter Volkswirtschaften (20–25%)."
            )
        )
    )
    override val investmentsBreakdownName: String = "Investitionen & Inflation"
    override val investmentsHintCollapsed: String = "Was ist bei \"Investitionen & Inflation\" zu beachten? (Hinweis)"
    override val investmentsHintExpanded: String = "Hinweis zu Investitionen und Inflation ausblenden"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Nominale Gesamtrendite (% p. a.)",
            items = listOf(
                "Gewichtete erwartete Gesamtrendite des gesamten Portfolios",
                "Zinserträge aus Festgeld und Tagesgeld",
                "Anleihekupons und erwartete Dividendenerträge",
                "Nettomietrendite aus Immobilien oder Vermietung",
                "Benchmark entwickelter Märkte: 6–9% p. a.",
                "Benchmark Schwellenländer: 10–16% p. a. in lokaler Währung"
            )
        ),
        BreakdownCategory(
            title = "Erwartete jährliche Inflation (% p. a.)",
            items = listOf(
                "Offizielle Zielwerte der Zentralbanken und Wirtschaftsprognosen",
                "Persönliche Haushaltsinflation basierend auf dem eigenen Warenkorb",
                "Historische Durchschnittsinflation der letzten 5–10 Jahre",
                "Benchmark entwickelter Märkte: ca. 2–3% jährlich",
                "Benchmark Schwellenländer: meist 5–9% jährlich"
            )
        ),
        BreakdownCategory(
            title = "Reale Kapitalrendite (Fisher-Gleichung)",
            items = listOf(
                "Formel: ((1 + Nominale Rendite) / (1 + Inflation) - 1) * 100%",
                "Inflationsbereinigter Zuwachs der realen Kaufkraft",
                "Typische reale Rendite: 3–5% p. a. für entwickelte Märkte",
                "Ein positiver Wert (> 0%) schützt das Vermögen vor Entwertung und mehrt das Kapital"
            )
        )
    )
    override val realReturnTitle: String = "Reale Kapitalrendite:"
    override val realReturnFormula: String = "(1 + Nominale Rendite) / (1 + Inflation) - 1"

    override val block5Title: String = "5. Alter & Planungshorizont"
    override val block5Subtitle: String = "Parameter für die Ruhestandsplanung"
    override val birthDateLabel: String = "Geburtsdatum (JJJJ-MM-TT)"
    override val invalidBirthDateError: String = "Bitte geben Sie ein gültiges Geburtsdatum an"
    override val lifeExpectancyLabel: String = "Lebenserwartung (Jahre)"
    override val lifeExpectancyError: String = "Die Lebenserwartung muss über Ihrem aktuellen Alter liegen und darf höchstens 120 Jahre betragen"
    override val lifeExpectancyUsedValueFormat: (years: Int) -> String = { years -> "Berechnung: $years ${plural(years, yearPluralForms)}" }
    override val fireHorizonBreakdownName: String = "Alter & FIRE-Horizont"
    override val fireHorizonHintCollapsed: String = "Wie Alter und Lebenserwartung bestimmen? (Hinweis)"
    override val fireHorizonHintExpanded: String = "Hinweis zu Alter und Horizont ausblenden"
    override val fireHorizonInstruction: String = "Geben Sie Ihr Geburtsdatum und die erwartete Lebenserwartung anhand der folgenden Richtwerte und Empfehlungen ein."
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Geburtsdatum und aktuelles Alter",
            items = listOf(
                "Dient zur Berechnung altersbasierter Vermögensmeilensteine (Fidelity)",
                "Bestimmt den verbleibenden Anlagezeitraum für den Zinseszinseffekt",
                "Unterstützt die optimale Asset-Allokation zwischen Wachstum und Kapitalerhalt"
            )
        ),
        BreakdownCategory(
            title = "Angestrebte Lebenserwartung (Jahre)",
            items = listOf(
                "Das Höchstalter, das im Ruhestandsmodell abgedeckt werden soll",
                "Statistische Orientierungswerte: 80–85 für Männer, 83–88 für Frauen",
                "Für konservative Sicherheit wird eine Annahme von 85–95 Jahren empfohlen",
                "Ein defensiver Puffer stellt sicher, dass das Kapital nicht vorzeitig aufgebraucht wird"
            )
        ),
        BreakdownCategory(
            title = "Planungshorizont (Jahre)",
            items = listOf(
                "Formel: Lebenserwartung abzüglich aktuelles Alter",
                "Der Gesamtzeitraum, in dem das Kapital Ihre Lebenshaltungskosten decken muss",
                "Zentraler mathematischer Parameter für das erforderliche FIRE-Zielkapital"
            )
        )
    )
    override val horizonTitle: String = "Planungshorizont:"
    override val horizonFormula: String = "(Lebenserwartung - Aktuelles Alter)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon Jahre (aktuelles Alter: $age Jahre)" }
    override val includePropertyInFireLabel: String = "Immobilien und Schulden in FIRE-Berechnung einbeziehen"
    override val includePropertyInFireSubOn: String = "Es wird das Reinvermögen inklusive Immobilien abzüglich Schulden verwendet"
    override val includePropertyInFireSubOff: String = "Es wird nur investierbares Vermögen verwendet (liquide + finanziell)"

    override val calculateButtonText: String = "Kennzahlen berechnen"
    override val exportButtonText: String = "Ergebnisse exportieren"
    override val exportChooserTitle: String = "Ergebnisse exportieren"

    override val metricsHeaderTitle: String = "Berechnete Kennzahlen"
    override val metricsHeaderSubtitle: String = "Wichtige Finanzkennzahlen mit Formeln & Beurteilung"
    override val categoryAll: String = "Alle"
    override val categoryCashFlow: String = "Cashflow"
    override val categoryCapitalRisks: String = "Kapital & Risiken"
    override val categoryBenchmarks: String = "Benchmarks"
    override val metricCardInfo: String = "Info"
    override val metricCardHide: String = "Ausblenden"
    override val metricCardFormulaPrefix: String = "Formel: "
    override val metricCardExpand: String = "Ausklappen"
    override val metricCardCollapse: String = "Einklappen"

    override val metricCashFlowTitle: String = "Cashflow"
    override val metricCashFlowFormula: String = "(Einkommen - Ausgaben)"
    override val metricCashFlowDesc: String = "Cashflow. Nettosaldo der Mittelzuflüsse und -abflüsse pro Monat.\n\n• Positiver Wert (> 0): Sie nehmen mehr ein, als Sie ausgeben.\n• Null oder negativ (≤ 0): Einnahmen entsprechen den Ausgaben oder liegen darunter."
    override val metricCashFlowPositiveSub: String = "Positiver Budgetüberschuss"
    override val metricCashFlowNeutralSub: String = "Einnahmen decken Ausgaben genau"
    override val metricCashFlowNegativeSub: String = "Budgetdefizit"

    override val metricSavingsRateTitle: String = "Sparquote"
    override val metricSavingsRateFormula: String = "(Cashflow / Einkommen) * 100%"
    override val metricSavingsRateDesc: (String, String) -> String = { neutral, positive -> "Es wird eine Sparquote von über ${positive}% des Bruttohaushaltseinkommens empfohlen.\n\n• Bis einschließlich ${neutral}%: Negativ; über ${neutral}% bis ${positive}%: Neutral; über ${positive}%: Positiv." }
    override val metricSavingsRateSub: (String, String) -> String = { neutral, positive -> "Bis einschließlich ${neutral}%: Negativ; über ${neutral}% bis ${positive}%: Neutral; über ${positive}%: Positiv." }

    override val metricEmergencyFundTitle: String = "Notgroschen"
    override val metricEmergencyFundFormula: String = "(Liquide Mittel)"
    override val metricEmergencyFundDesc: String = "Ein finanzielles Sicherheitsnetz zur Deckung unvorhergesehener Ausgaben (Krankheit, Jobverlust, Notfallreparaturen), um teure Schulden zu vermeiden."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Liquide + Finanziell"
    override val metricEmergencyFundSubOnlyLiquid: String = "Nur liquide Mittel"

    override val metricSafetyMonthsTitle: String = "Notfallpuffer (Monate)"
    override val metricSafetyMonthsFormula: String = "(Notgroschen / Monatliche Ausgaben)"
    override val metricSafetyMonthsDesc: (String, String, String) -> String = { neutral, positive, excellent -> "Anzahl der Monate, die Sie Ihre grundlegenden Lebenshaltungskosten allein aus dem Notgroschen bestreiten können.\n\n• Mindestziel: ${neutral}–${positive} Monate Lebenshaltungskosten (Wohnen, Nahrung, Nebenkosten). Sichert Übergangsphasen ab.\n• Idealziel: Bis zu ${excellent} Monate für maximale Resilienz, besonders bei Freiberuflern oder Familien." }
    override val metricSafetyMonthsSub: (String, String, String) -> String = { neutral, positive, excellent -> "Norm: ${neutral}–${positive} Mon., Ideal: ${excellent}+ Mon." }
    override val monthsSuffix: String = "Mon."

    override val metricDebtRatioTitle: String = "Verschuldungsgrad (Debt-to-Asset)"
    override val metricDebtRatioFormula: String = "(Verbindlichkeiten / Vermögen)"
    override val metricDebtRatioDesc: (String, String, String, String) -> String = { zero, positive, neutral, critical -> "Gibt den Anteil des Vermögens an, der durch Schulden finanziert ist, und spiegelt das finanzielle Risiko wider.\n\n• ${zero} – ${positive}: Sehr solide, Vermögen fast vollständig durch Eigenkapital finanziert\n• ${positive} – ${neutral}: Ausgewogener Verschuldungsgrad für Privathaushalte\n• Über ${neutral}: Erhöhtes Risiko, starke Abhängigkeit von Gläubigern\n• Über ${critical}: Verbindlichkeiten übersteigen Vermögen (Überschuldungsrisiko)" }
    override val metricDebtRatioLowSub: String = "Geringes Risiko (ausgezeichnet)"
    override val metricDebtRatioHighSub: String = "Hohes Verschuldungsrisiko"

    override val metricNetWorthTitle: String = "Reinvermögen"
    override val metricNetWorthFormula: String = "(Vermögen - Verbindlichkeiten)"
    override val metricNetWorthDesc: String = "Gesamtwert aller Vermögenswerte abzüglich aller Verbindlichkeiten. Entspricht dem Betrag, der nach Begleichung aller Schulden tatsächlich verbleibt."
    override val metricNetWorthSubPrefix: String = "Gesamtvermögen: "
    override val metricNetWorthNegativeSub: String = "Negatives Reinvermögen (Defizit!)"
    override val metricNetWorthAssetsFormat: (prefix: String, amount: String) -> String = { prefix, amount -> "$prefix$amount" }
    override val metricNetWorthNegativeAssetsFormat: (prefix: String, amount: String, negative: String) -> String = { prefix, amount, negative -> "$prefix$amount ($negative)" }

    override val metricFidelityTitle: String = "Benchmark: Fidelity-Vermögensmeilensteine"
    override val metricFidelityFormula: String = "Jahreseinkommen * Multiplikator"
    override val metricFidelityDesc: String = "Fidelity-Richtwerte: Alter 30 — 1x, 40 — 3x, 50 — 6x, 60 — 8x und 67 — 10x. Die App interpoliert linear zwischen den Punkten und verwendet vor Alter 25 einen Multiplikator von 0,5x. Berücksichtigt werden liquide und finanzielle Mittel abzüglich ungedeckter Schulden; Sachwerte/Immobilien sind ausgenommen.\n\nBeispiel: Im Alter von 40 Jahren bei 100.000 Jahreseinkommen beträgt das Ziel 300.000.\n\nHinweis: Diese App steht in keiner Verbindung zu Fidelity Investments und nutzt ausschließlich öffentlich zugängliche Forschungs- und Bildungsrichtwerte."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Alter: $age J. | Multiplikator: ${mult}x" }
    override val metricFidelityProgress: String = "Fortschritt zum Fidelity-Ziel:"
    override val metricFidelityCapitalFormat: (capital: String) -> String = { capital -> "Berücksichtigtes Kapital: $capital" }
    override val fidelityProgressRatioFormat: (String, String) -> String = { capital, target -> "($capital von $target)" }

    override val metricCfsTitle: String = "Finanzielle Freiheitsquote (CFS)"
    override val metricCfsFormula: String = "(Reinvermögen inklusive Immobilien / Jahresausgaben)"
    override val metricCfsDesc: (String, String, String, String, String, String) -> String = { debt, buffer, neutral, positive, stability, independence -> "Reinvermögen = alle Vermögenswerte einschließlich Immobilien abzüglich aller Schulden. Diese Kennzahl bedeutet nicht, dass Sie von Kapitalerträgen leben können; die FIRE-Berechnung prüft dies gesondert ohne Immobilien.\n\nGibt an, wie viele Jahre der Lebenshaltungskosten durch das Reinvermögen gedeckt sind.\n\n• CFS < ${debt} — Sie haben Nettoschulden\n• CFS >= $debt und < $buffer — Minimaler Notfallpuffer\n• CFS >= $buffer und < $neutral — Erste Kapitalaufbauphase\n• CFS >= $neutral und < $positive — Langfristige Anlagephase\n• CFS >= $positive und < $stability — Kapitalerträge liefern spürbares Einkommen. Inflationseinfluss wächst\n• CFS >= ${stability} und < ${independence} — Solide finanzielle Stabilität. Wie nutzen Sie Ihre wertvolle Zeit?\n• CFS >= ${independence} — Vollständige finanzielle Unabhängigkeit erreicht" }

    override val metricRealReturnTitle: String = "Reale Kapitalrendite, %"
    override val metricRealReturnFormula: String = "(1 + Nominale Rendite) / (1 + Inflation) - 1"
    override val metricRealReturnDesc: String = "Der reale Kaufkraftzuwachs nach Abzug der Inflation von den nominalen Erträgen. Zeigt den tatsächlichen Vermögenszuwachs."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Nominal: $nom | Inflation: $infl" }
    override val metricUnavailableValue: String = "—"

    override val scoresHeaderTitle: String = "Punkteübersicht"
    override val scoresHeaderSubtitle: String = "Gesamtpunktzahl & Detailbewertung"
    override val scoresShareReportChooser: String = "Bericht teilen"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "von $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Gesamtergebnis: $score von 10" }
    override val scoresStatusExcellent: String = "Hervorragende finanzielle Stabilität und beste Voraussetzungen für langfristigen Vermögensaufbau."
    override val scoresStatusGood: String = "Gute finanzielle Verfassung mit Potenzial zur Steigerung der Sparquote."
    override val scoresStatusModerate: String = "Mäßige Stabilität. Es wird empfohlen, den Notgroschen auszubauen."
    override val scoresStatusAttention: String = "Kostenoptimierung und gezielter Schuldenabbau dringend empfohlen."
    override val scoresBreakdownTitle: String = "Punkteaufschlüsselung nach Komponenten:"
    override val scoreValuePrefix: String = "Wert: "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score von 10" }
    override val scoreSlashOutOfTen: (score: String) -> String = { score -> "$score / 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String, positiveThreshold: String) -> String = { rate, sr, positiveThreshold ->
        when {
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD -> "Richtwert über ${positiveThreshold}%. Sie halten einen hervorragenden Wert ($sr)."
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD -> "Richtwert über ${positiveThreshold}%. Bis einschließlich ${positiveThreshold}% liegt ein neutraler Wert vor ($sr)."
            else -> "Richtwert über ${positiveThreshold}%. Null oder negativ ($sr) ist ein negativer Wert: Einnahmen entsprechen Ausgaben oder liegen darunter."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String, neutral: String, positive: String, excellent: String) -> String = { months, monthsText, neutral, positive, excellent ->
        when {
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT -> "Minimum ${neutral}–${positive} Monate, Ideal ${excellent}+ Monate. Hervorragendes Ergebnis: Puffer übersteigt ${excellent} Monate ($monthsText)."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD -> "Minimum ${neutral}–${positive} Monate, Ideal ${excellent}+ Monate. Guter Puffer ($monthsText), Richtwert von ${positive} Monaten erreicht."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD -> "Minimum ${neutral}–${positive} Monate, Ideal ${excellent}+ Monate. Basispuffer ($monthsText), Ausbau auf ${positive}–${excellent} Monate empfohlen."
            else -> "Minimum ${neutral}–${positive} Monate, Ideal ${excellent}+ Monate. Unzureichender Puffer ($monthsText), erhöhtes finanzielles Risiko."
        }
    }
    override val debtComment0: (String, String) -> String = { zero, positive -> "$zero – $positive: Hervorragende Stabilität, Vermögen fast vollständig durch Eigenkapital finanziert." }
    override val debtComment1: (String, String) -> String = { positive, neutral -> "$positive – $neutral: Ausgewogener Verschuldungsgrad, beherrschbare Verbindlichkeiten." }
    override val debtComment2: (String, String) -> String = { neutral, critical -> "$neutral – $critical: Erhöhtes Risiko, erhebliche Abhängigkeit von Fremdkapital." }
    override val debtComment3: (String) -> String = { critical -> "Über $critical: Kritisches Risiko, Gesamtschulden übersteigen das Vermögen." }

    override val auditorOpinionTitle: String = "Auditoren-Gutachten"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Analyse aller $count Kennzahlen aus der Kennzahlenübersicht" }
    override val shareAuditorOpinionChooser: String = "Gutachten teilen"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count positiv" }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count neutral" }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count negativ" }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Positive Kennzahlen ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Neutrale Kennzahlen ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Negative Kennzahlen ($count)" }
    override val positiveEmptyText: String = "Bisher keine positiven Faktoren identifiziert. Einnahmen- und Ausgabenoptimierung empfohlen."
    override val neutralEmptyText: String = "Keine neutralen Kennzahlen — alle Werte liegen im positiven oder negativen Bereich."
    override val negativeEmptyText: String = "🎉 Keine kritischen Risikofaktoren festgestellt — alle Kennzahlen im sicheren Bereich!"

    override val fireHeroTitle: String = "Finanzielle Freiheit, FIRE"
    override val fireHeroSubtitle: String = "Ruhestandszeitplan und Vermögensprognose"
    override val fireSubtabBase: String = "FIRE"
    override val fireSubtabSimulation: String = "Simulation"
    override val fireTimeToRetire: String = "Zeit bis zum Ruhestand:"
    override val fireYearsSuffix: String = " Jahre"
    override val fireHeroAssumption: String = "FIRE-Berechnung berücksichtigt liquide und finanzielle Mittel; Sachwerte sind ausgeschlossen, Schulden werden nur verrechnet, soweit sie den Sachwert übersteigen"
    override val fireRetirementAgeLabel: String = "Ruhestandsalter:"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age J." }
    override val fireTargetCapitalLabel: String = "Erforderliches Zielkapital:"
    override val fireParamsTitle: String = "Parameter für Alter & Planungshorizont"
    override val fireBirthDateLabel: String = "Geburtsdatum:"
    override val fireCurrentAgeLabel: String = "Aktuelles Alter:"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age J." }
    override val fireLifeExpectancyLabel: String = "Lebenserwartung:"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp J." }
    override val fireHorizonLabel: String = "(Lebenserwartung - Aktuelles Alter):"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h J." }
    override val fireRealReturnLabel: String = "Reale Kapitalrendite:"
    override val fireTrajectoryTitle: String = "Verlauf des Kapitalaufbaus"
    override val fireTrajectorySubtitle: String = "Vermögenswachstum bis zum FIRE-Punkt und lebenslange Entnahme"
    override val fireChartAccumulated: String = "Aufgebautes Kapital"
    override val fireChartRequired: String = "Erforderliches Kapital"
    override val fireChartYearsSuffix: String = " J."
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE: $age J." }
    override val fireSimSummaryTitle: String = "Simulationsübersicht (Gesamteffekt)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ Ruhestand $diff Jahre früher erreicht (in $target Jahren, mit $age Jahren)!" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Zeit bis zum Ruhestand um $diff Jahre verlängert (in $target Jahren, mit $age Jahren)." }
    override val fireSimSame: (target: String) -> String = { target -> "Ruhestandszeitplan bleibt unverändert (in $target Jahren)." }
    override val fireSimWithoutBase: (target: String, age: String) -> String = { target, age -> "Ruhestand erreichbar in $target Jahren (mit $age Jahren)." }
    override val fireUnreachableText: String = "Nicht erreichbar bei aktuellen Parametern"
    override val fireHorizonExhaustedText: String = "Planungshorizont erschöpft"
    override val fireSimCashFlowRow: String = "Cashflow (Ersparnis):"
    override val fireSimExpensesRow: String = "Monatliche Ausgaben:"
    override val fireSimNetWorthRow: String = "Reinvermögen (Vermögen − Schulden):"
    override val fireSimPropertyRow: String = "Vermögenszuwachs (nach Tilgungsende):"
    override val fireSimSliderTitle: String = "Simulator \"Was wäre wenn?\""
    override val fireSimReset: String = "Zurücksetzen"
    override val fireSimFineTuning: String = "Feineinstellung (Schieberegler):"
    override val fireSliderSavings: String = "Monatliche Ersparnis:"
    override val fireSliderExpenses: String = "Monatliche Ausgaben:"
    override val fireSliderReturn: String = "Reale Kapitalrendite:"
    override val fireSliderBaseSuffix: String = " zur Basis"
    override val fireSliderBaseReturnSuffix: String = "% zur Basis"
    override val fireSliderFrom: (v: String) -> String = { v -> "von $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "Schritt $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "bis zu +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "Zuwachs"
    override val fireSliderDec: String = "Senkung"

    override val scenarioHeaderTitle: String = "Szenarien-Panel (Stresstests)"
    override val scenarioHeaderSubtitle: String = "Die Gesamtauswirkung der Regler und Optionen wird kumulativ berechnet und fließt in die Kennzahlen und die Verlaufskurve ein:"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Gesamt: $count" }
    override val scenarioPositiveTitle: String = "Positive Ereignisse"
    override val scenarioPositiveSectionNumber: String = "2.1"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Aktiv: $count" }
    override val scenarioPositiveSubtitle: String = "Ereignisse, die den Kapitalaufbau und Ruhestand beschleunigen:"
    override val scenarioNegativeTitle: String = "Negative Ereignisse"
    override val scenarioNegativeSectionNumber: String = "2.2"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Aktiv: $count" }
    override val scenarioNegativeSubtitle: String = "Stresstests, unerwartete Ausgaben und Krisenszenarien:"
    override val scenarioParamsPrefix: String = "Parameter: "

    override val scenarioSalaryIncreaseTitle: String = "Gehaltserhöhung"
    override val scenarioSalaryIncreaseParam: (String) -> String = { percent -> "Einkommen +${percent}% monatlich, dauerhaft" }
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Einkommensplus: +$amt/Mon." }

    override val scenarioMarketGrowthTitle: (String) -> String = { percent -> "Marktwachstum +${percent}%" }
    override val scenarioMarketGrowthParam: (String) -> String = { percent -> "ETF +${percent}%" }
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Depotzuwachs: +$amt" }

    override val scenarioExpenseOptTitle: String = "Kostenoptimierung"
    override val scenarioExpenseOptParam: (String) -> String = { percent -> "−${percent}% monatliche Ausgaben, dauerhaft" }
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Laufende Ersparnis: -$amt/Mon." }

    override val scenarioRelocationTitle: String = "Umzug in günstigere Region"
    override val scenarioRelocationParam: (String) -> String = { percent -> "−${percent}% monatliche Ausgaben, dauerhaft" }
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Kostenersparnis: -$amt/Mon." }

    override val scenarioInheritanceTitle: String = "Erbschaft oder Schenkung"
    override val scenarioInheritanceParam: (String) -> String = { percent -> "einmalig ${percent}% des Monatseinkommens" }
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Kapitalzufluss: +$amt (in liquide Mittel)" }

    override val scenarioPropertySaleTitle: String = "Verkauf von Sachwerten (Auto, Immobilie)"
    override val scenarioPropertySaleParam: (String) -> String = { percent -> "einmalig bis zu ${percent}% des Monatseinkommens" }
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Verkaufserlös: +$amt (in liquide Mittel)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Kinder auf eigenen Beinen"
    override val scenarioChildrenSelfSufficientParam: (String, String) -> String = { percent, years -> "Ausgaben −${percent}% in $years J. (Kinderkosten bereits in Ausgaben enthalten)" }
    override val scenarioChildrenSelfSufficientDesc: (amt: String, years: String) -> String = { amt, years -> "Ausgabenreduktion: -$amt/Mon. (in $years J.)" }

    override val scenarioJobLossTitle: String = "Jobverlust"
    override val scenarioJobLossParam: (String) -> String = { months -> "$months Monate Einkommensverlust" }
    override val scenarioJobLossDesc: (String) -> String = { months -> "$months Monate des aktuellen Einkommens als Ausfall, gedeckt aus Ersparnissen" }

    override val scenarioMarketCrashTitle: (String) -> String = { percent -> "Markteinbruch −${percent}%" }
    override val scenarioMarketCrashParam: (String) -> String = { percent -> "ETF −${percent}%" }
    override val scenarioMarketCrashDesc: (percent: String, amt: String) -> String = { percent, amt -> "Depotverlust von ${percent}% ($amt)" }

    override val scenarioMortgageTitle: String = "Immobilienkredit (Hypothek)"
    override val scenarioMortgageParam: (String, String) -> String = { percent, years -> "${percent}% Einkommen für $years Jahre" }
    override val scenarioMortgageDesc: (pay: String, years: String) -> String = { pay, years -> "Rate: $pay/Mon. ($years J.)" }

    override val scenarioCarLoanTitle: String = "Autokredit"
    override val scenarioCarLoanParam: (String, String) -> String = { percent, years -> "${percent}% Einkommen für $years Jahre" }
    override val scenarioCarLoanDesc: (pay: String, years: String) -> String = { pay, years -> "Rate: $pay/Mon. ($years J.)" }

    override val scenarioLargeExpenseTitle: String = "Größere Einmalausgabe"
    override val scenarioLargeExpenseParam: (String) -> String = { percent -> "Ausgabe ${percent}% des Monatseinkommens" }
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Mittelabfluss von $amt (gedeckt aus liquiden und Finanzanlagen)" }

    override val scenarioInflationTitle: String = "Kostensteigerung"
    override val scenarioInflationParam: (String) -> String = { percent -> "+${percent}% auf monatliche Ausgaben" }
    override val scenarioInflationDesc: (amt: String) -> String = { amt -> "Mehrausgaben: +$amt/Mon." }

    override val scenarioChildTitle: String = "Geburt eines Kindes"
    override val scenarioChildParam: (String, String) -> String = { percent, years -> "+${percent}% Ausgaben für $years Jahre" }
    override val scenarioChildDesc: (amt: String, years: String) -> String = { amt, years -> "Kinderbetreuungskosten: +$amt/Mon. (für $years Jahre)" }

    override val disclaimerTitle: String = "Haftungsausschluss"
    override val disclaimerText: String = "Wichtig: Dieses Projekt ist eine persönliche Entwicklung und wird ausschließlich zu Informationszwecken «wie besehen» bereitgestellt. Der Autor übernimmt keine Haftung für Fehlfunktionen, Datenverluste oder sonstige Schäden, die aus der Nutzung dieser Software entstehen. Alle Berechnungen, Formeln, Kennzahlen und Simulationen dienen reinen Informationszwecken und stellen keine individuelle Finanz-, Anlage- oder Steuerberatung dar. Die Nutzung erfolgt auf eigenes Risiko."
    override val assumptionsDisclaimerTitle: String = "Haftungsausschluss"
    override val assumptionsDisclaimerBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> fireAssumptionsBody(returnRate, inflation) }
    override val fireAssumptionsTitle: String = "Modellannahmen"
    override val fireAssumptionsBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> "Die Berechnungen gehen von einer konstanten Rendite von $returnRate und einer Inflation von $inflation aus. Die Steuer auf Kapitalerträge wird vereinfacht und nur für den Anteil der Finanzanlagen berücksichtigt; andere Steuern, die Reihenfolge der Marktereignisse und die Einkommensinflation werden nicht berücksichtigt. Dies ist eine Orientierungshilfe, keine Prognose und keine Garantie." }

    // Pluralization & Formats
    override val moneyFormat: String = "%3${'$'}s%1${'$'}s%2${'$'}s%4${'$'}s"
    override val countFormat: String = "%1${'$'}s\u00A0%2${'$'}s"
    override val checkboxPluralForms = PluralForms(one = "Checkbox", other = "Checkboxen")
    override val yearPluralForms = PluralForms(one = "Jahr", other = "Jahre")
    override val monthPluralForms = PluralForms(one = "Monat", other = "Monate")
    override val pointPluralForms = PluralForms(one = "Punkt", other = "Punkte")
    override val simulationBadgeSlidersSuffix: String = "Regler"
    override val simulationBadgeOnlySliders: String = "Anpassungsregler"

    // Score & Safety Months formats
    override val safetyMonthsValueFormat: String = "%1${'$'}s Mon."
    override val shareScoreReportTemplate: String = "📊 Finanzbericht & Kapitalprüfung:\n• Gesamtpunktzahl: %1${'$'}s von 10\n• Sparquote: %2${'$'}s/10 (%3${'$'}s)\n• Ausgabenpuffer: %4${'$'}s/10 (%5${'$'}s Mon.)\n• Verschuldungsgrad: %6${'$'}s/10 (%7${'$'}s)\n• Freiheitsquote (CFS): %8${'$'}s/10 (%9${'$'}s)\n• Zeit bis zum Ruhestand (FIRE): %10${'$'}s Jahre (mit %11${'$'}s)\n"

    // CFS Stage descriptions
    override val cfsStageDebt: (String) -> String = { debt -> "CFS < $debt — Sie haben Nettoschulden" }
    override val cfsStageBuffer: (String, String) -> String = { debt, buffer -> "CFS >= $debt und < $buffer — Sie verfügen über einen minimalen Notfallpuffer" }
    override val cfsStageCapital: (String, String) -> String = { buffer, neutral -> "CFS >= $buffer und < $neutral — Beginnender Aufbau von Finanzkapital" }
    override val cfsStageInvesting: (String, String) -> String = { neutral, positive -> "CFS >= $neutral und < $positive — Sie können mit langfristigen Investitionen starten" }
    override val cfsStageReturns: (String, String) -> String = { positive, stability -> "CFS >= $positive und < $stability — Kapitalerträge liefern spürbares Einkommen. Inflation beeinflusst das Kapital stärker" }
    override val cfsStageStability: (String, String) -> String = { stability, independence -> "CFS >= $stability und < $independence — Solide finanzielle Stabilität. Wie nutzen Sie Ihre unersetzliche Zeit?" }
    override val cfsStageIndependence: (String) -> String = { independence -> "CFS >= $independence — Reinvermögen inklusive Immobilien übersteigt $independence Jahresausgaben" }

    // Auditor Opinion Share Text
    override val auditShareHeaderTitle: String = "📋 AUDITOREN-GUTACHTEN ÜBER ALLE KENNZAHLEN"
    override val auditShareScoreLine: String = "⭐️ Gesamtpunktzahl: %1${'$'}s von 10 Punkten"
    override val auditShareTotalCountLine: String = "Analysierte Kennzahlen insgesamt: %1${'$'}d"
    override val auditSharePositiveHeader: String = "🟢 POSITIVE KENNZAHLEN (%1${'$'}d):"
    override val auditSharePositiveEmpty: String = "• Keine positiven Kennzahlen identifiziert"
    override val auditShareNeutralHeader: String = "🟡 NEUTRALE KENNZAHLEN (%1${'$'}d):"
    override val auditShareNeutralEmpty: String = "• Keine neutralen Kennzahlen"
    override val auditShareNegativeHeader: String = "🔴 NEGATIVE KENNZAHLEN (%1${'$'}d):"
    override val auditShareNegativeEmpty: String = "• Keine kritischen Risikofaktoren festgestellt 🎉"
    override val auditShareGeneratedBy: String = "💡 Erstellt in der App \"Persönliche Finanzen\""
    override val auditShareDisclaimer: String = "⚠️ Wichtig: Dieses Projekt ist eine persönliche Entwicklung und wird ausschließlich zu Informationszwecken «wie besehen» bereitgestellt. Der Autor übernimmt keine Haftung für Fehlfunktionen, Datenverluste oder sonstige Schäden. Alle Berechnungen und Simulationen dienen reinen Informationszwecken und stellen keine Finanz- oder Steuerberatung dar. Nutzung auf eigenes Risiko."

    // Auditor Indicators
    override val auditCashFlowTitle: String = "Cashflow"
    override val auditCashFlowPositive: String = "✅ Cashflow (+%1${'$'}s/Mon.): Haushaltsüberschuss. Ihre Einnahmen übersteigen die Ausgaben und schaffen regelmäßigen Raum für Vermögensaufbau."
    override val auditCashFlowNeutral: String = "⚠️ Cashflow (0 %1${'$'}s/Mon.): Ausgeglichenes Budget (Einnahmen decken Ausgaben). Keine Neuverschuldung. Es bleibt kein Überschuss zum Sparen, daher wird dieser Indikator negativ bewertet."
    override val auditCashFlowNegative: String = "🚨 Cashflow (%1${'$'}s/Mon.): Budgetdefizit! Die Ausgaben übersteigen die Einnahmen: Die Differenz muss aus Ersparnissen oder durch Schulden gedeckt werden. Wenn die Kapitalerträge das Defizit nicht ausgleichen, schrumpfen die Rücklagen."

    override val auditSavingsRateTitle: String = "Sparquote"
    override val auditSavingsRatePositive: (String, String) -> String = { value, threshold -> "✅ Sparvermögen (Sparquote: $value): Hohe Sparquote (über ${threshold}%). Ermöglicht kontinuierlichen Vermögensaufbau und Zinseszinseffekte." }
    override val auditSavingsRateNeutral: (String, String, String) -> String = { value, neutral, positive -> "ℹ️ Sparvermögen (Sparquote: $value): Neutraler Bereich (über $neutral bis ${positive}%). Vermögensaufbau schreitet voran; Kostenoptimierung kann helfen, die Marke von >${positive}% zu erreichen." }
    override val auditSavingsRateNegative: String = "🚨 Sparvermögen (Sparquote: %1${'$'}s): Cashflow gleich null oder negativ: Ihr laufendes Einkommen trägt nicht zum Kapitalaufbau bei."

    override val auditEmergencyFundTitle: String = "Notgroschen"
    override val auditEmergencyFundPositive: String = "✅ Notgroschen (Rücklagenvolumen: %1${'$'}s): Liquide Reserve reicht vollständig aus, um unvorhergesehene Krisen ohne Schulden zu überstehen."
    override val auditEmergencyFundNeutral: (String, String) -> String = { value, months -> "ℹ️ Notgroschen (Rücklagenvolumen: $value): Minimaler Basispuffer vorhanden. Es wird empfohlen, ihn schrittweise auf $months Monatsausgaben auszubauen." }
    override val auditEmergencyFundNegative: String = "⚠️ Notgroschen (Rücklagenvolumen: %1${'$'}s): Kritisch niedrige Reserve. Hohe Anfälligkeit für unvorhergesehene Kosten und Notkredite."

    override val auditSafetyMonthsTitle: String = "Notfallpuffer (Monate)"
    override val auditSafetyMonthsHigh: (String, String) -> String = { value, months -> "✅ Finanzpolster (Notfallpuffer: $value Mon.): Vollständig gebildet mit komfortabler Reserve. Risiken bei Einkommensausfall sind abgefedert. Überschüssige Liquidität über $months Monaten sollte investiert werden." }
    override val auditSafetyMonthsTarget: (String, String) -> String = { value, months -> "✅ Finanzpolster (Notfallpuffer: $value Mon.): Zielnorm von $months Monaten erreicht. Grundlegende finanzielle Sicherheit ist gewährleistet." }
    override val auditSafetyMonthsNeutral: (String, String) -> String = { value, months -> "ℹ️ Finanzpolster (Notfallpuffer: $value Mon.): Minimaler Basispuffer vorhanden; Ausbau auf $months Monate für Krisenfestigkeit empfohlen." }
    override val auditSafetyMonthsNegative: (String, String, String) -> String = { value, neutral, positive -> "⚠️ Finanzpolster (Notfallpuffer: $value Mon.): Kritisch unzureichend (unter $neutral Monaten). Gefahr von Schuldenfallen bei Engpässen. Priorität: Notgroschen auf ${positive} Monate aufbauen." }

    override val auditDebtRatioTitle: String = "Verschuldungsgrad"
    override val auditDebtRatioZero: String = "✅ Verschuldung (Debt Ratio: 0.00): Vollständige finanzielle Autonomie — keine Schulden oder Kredite, 100% des Vermögens sind unbelastet."
    override val auditDebtRatioConservative: String = "✅ Verschuldung (Debt Ratio: %1${'$'}s, Schulden %2${'$'}s des Vermögens): Konservativer und sicherer Verschuldungsgrad. Verbindlichkeiten sind voll gedeckt; Kreditrisiko ist minimal."
    override val auditDebtRatioModerate: String = "ℹ️ Verschuldung (Debt Ratio: %1${'$'}s, Schulden %2${'$'}s des Vermögens): Moderater Verschuldungsgrad. Schulden sind beherrschbar, erfordern aber Disziplin bei größeren Anschaffungen."
    override val auditDebtRatioHigh: String = "⚠️ Warnung: Hohe Verschuldung (Debt Ratio: %1${'$'}s, Schulden %2${'$'}s des Vermögens)! Starke Abhängigkeit von Gläubigern. Umschuldung und rasche Tilgung empfohlen."
    override val auditDebtRatioCritical: String = "🚨 KRITISCHES RISIKO: Überschuldung (Debt Ratio: %1${'$'}s, Schulden übersteigen Vermögen um %2${'$'}s)! Verbindlichkeiten übersteigen Gesamtwert des Vermögens. Dringende Sanierung erforderlich."

    override val auditNetWorthTitle: String = "Reinvermögen"
    override val auditNetWorthStrong: String = "✅ Reinvermögen (Eigenkapital: %1${'$'}s): Starker positiver Saldo. Gesamtvermögen übersteigt Verbindlichkeiten deutlich und bietet ein solides Fundament."
    override val auditNetWorthBalanced: String = "ℹ️ Reinvermögen (Eigenkapital: %1${'$'}s): Das Reinvermögen ist nicht negativ, aber die Verbindlichkeiten machen mehr als die Hälfte des Gesamtvermögens aus."
    override val auditNetWorthNegative: String = "🚨 Reinvermögen (Negativer Saldo: %1${'$'}s): Negatives Reinvermögen. Gesamtverbindlichkeiten übersteigen alle vorhandenen Vermögenswerte."

    override val auditFidelityTitle: String = "Fidelity-Benchmark"
    override val auditFidelityPositive: String = "✅ Alters-Meilenstein (Fidelity-Benchmark: %1${'$'}s des Ziels): Kapital übertrifft den Richtwert für Ihr Alter %2${'$'}s. Ausgezeichnete langfristige Entwicklung."
    override val auditFidelityNeutral: String = "ℹ️ Alters-Meilenstein (Fidelity-Benchmark: %1${'$'}s des Ziels): Kapital entspricht der Altersempfehlung %2${'$'}s."
    override val auditFidelityNegativeInsolvent: String = "🚨 Alters-Meilenstein (Fidelity-Benchmark): Liquides und finanzielles Kapital nach Schulden ist negativ (%1${'$'}s). Ausstehende Schulden verhindern das Erreichen des empfohlenen Ziels %2${'$'}s."
    override val auditFidelityNegativeLagging: String = "⚠️ Alters-Meilenstein (Fidelity-Benchmark: %1${'$'}s des Ziels): Rückstand gegenüber dem Altersrichtwert %2${'$'}s. Erhöhung der Sparquote empfohlen."

    override val auditCfsTitle: String = "Finanzielle Freiheitsquote"
    override val auditCfsIndependence: (String, String) -> String = { cfs, independence -> "🎉 Finanzielle Freiheit (CFS: $cfs): Reinvermögen inklusive Immobilien übersteigt $independence Jahresausgaben." }
    override val auditCfsStability: (String, String, String) -> String = { cfs, stability, independence -> "✅ Finanzielle Freiheit (CFS: $cfs): Reinvermögen inklusive Immobilien übersteigt $stability Jahresausgaben; nächste Stufe führt zur $independence-Jahres-Grenze." }
    override val auditCfsGrowth: (String, String) -> String = { cfs, stability -> "✅ Finanzielle Freiheit (CFS: $cfs): Reinvermögen deckt mehrere Jahresausgaben ab. Nächstes Ziel: CFS > $stability." }
    override val auditCfsInitial: String = "ℹ️ Finanzielle Freiheit (CFS: %1${'$'}s): Anfangsphase des Kapitalaufbaus (%2${'$'}s). Hauptaugenmerk liegt auf diszipliniertem Sparen."
    override val auditCfsNegative: String = "🚨 Finanzielle Freiheit (CFS: %1${'$'}s): Negatives Verhältnis (%2${'$'}s). Schulden übersteigen alle Vermögenswerte inklusive Immobilien."
    override val auditCfsBuffer: (String, String) -> String = { cfs, buffer -> "📈 Finanzielle Freiheit (CFS: $cfs): Reinvermögen deckt höchstens $buffer Jahresausgaben ab." }
    override val auditCfsPathToIndependence: String = "Sie sind auf dem Weg zur Unabhängigkeit, aber noch ein Stück vom Ziel entfernt."
    override val auditCfsCloseToIndependence: (percent: Int) -> String = { percent -> "Sie sind nah dran: Das Kapital deckt $percent% des Ziels. Das Reinvermögen umfasst Immobilien; der FIRE-Tab bewertet, ob Sie von Kapitalerträgen leben können." }
    override val auditCfsFinanciallyIndependent: String = "Sie haben finanzielle Unabhängigkeit erreicht: CFS beträgt mindestens 30. Das Reinvermögen umfasst Immobilien; der FIRE-Tab bewertet, ob Sie von Kapitalerträgen leben können."

    override val auditRealReturnTitle: String = "Reale Kapitalrendite"
    override val auditRealReturnPositive: String = "✅ Reale Kapitalrendite (+%1${'$'}s): Rendite übersteigt Inflation (%2${'$'}s vs. %3${'$'}s). Das Kapital ist vor Entwertung geschützt und steigert die Kaufkraft."
    override val auditRealReturnNeutral: String = "ℹ️ Reale Kapitalrendite (0.0%): Rendite gleicht Inflation exakt aus. Kapital erhält seine nominale Kaufkraft, wächst aber nicht real."
    override val auditRealReturnNegative: String = "⚠️ Reale Kapitalrendite (%1${'$'}s): Inflation (%2${'$'}s) übersteigt Rendite (%3${'$'}s). Es kommt zu schleichender Entwertung der Ersparnisse."

    // Export Summary
    override val exportHeaderTitle: String = "📊 FINANZBERICHT — PERSÖNLICHE FINANZEN"
    override val exportSection1Title: String = "1. Eingabedaten:"
    override val exportIncomeLine: String = "• Einkommen: %1${'$'}s/Mon."
    override val exportExpensesLine: String = "• Ausgaben: %1${'$'}s/Mon."
    override val exportLiquidAssetsLine: String = "• Liquide Mittel: %1${'$'}s"
    override val exportFinancialAssetsLine: String = "• Finanzanlagen: %1${'$'}s"
    override val exportPropertyAssetsLine: String = "• Sachwerte / Immobilien: %1${'$'}s"
    override val exportLiabilitiesLine: String = "• Verbindlichkeiten (Schulden): %1${'$'}s"
    override val exportAgeHorizonLine: (age: Int, lifeExpectancy: Int, horizon: Int) -> String = { age, lifeExpectancy, horizon -> "• Alter: $age J. (Lebenserwartung: $lifeExpectancy J., Horizont: $horizon J.)" }
    override val exportReturnsInflationLine: String = "• Nominale Rendite: %1${'$'}s | Inflation: %2${'$'}s"
    override val exportTaxLine: String = "• Kapitalertragsteuer: %1${'$'}s"

    override val exportSection2Title: String = "2. Berechnete Kennzahlen:"
    override val exportCashFlowLine: String = "• Cashflow: %1${'$'}s/Mon."
    override val exportSavingsRateLine: String = "• Sparquote: %1${'$'}s"
    override val exportEmergencyFundLine: String = "• Notgroschen: %1${'$'}s (%2${'$'}s Mon.)"
    override val exportDebtRatioLine: String = "• Verschuldungsgrad: %1${'$'}s"
    override val exportNetWorthLine: String = "• Reinvermögen: %1${'$'}s"
    override val exportRealReturnLine: String = "• Reale Rendite: %1${'$'}s"
    override val exportEffectiveTaxLine: String = "• Effektive Steuer bei Entnahme: %1${'$'}s"

    override val exportSection3Title: String = "3. Punkteübersicht:"
    override val exportScoreTotalLine: String = "• Gesamtpunktzahl: %1${'$'}s von 10"
    override val exportScoreSavingsRateLine: String = "• Sparquote: %1${'$'}d / 10"
    override val exportScoreSafetyMonthsLine: String = "• Notfallpuffer: %1${'$'}d / 10"
    override val exportScoreDebtRatioLine: String = "• Schuldenlast: %1${'$'}d / 10"
    override val exportScoreCfsLine: String = "• Finanzielle Freiheit: %1${'$'}d / 10"

    override val exportSection4Title: String = "4. Finanzielle Freiheit, FIRE:"
    override val exportCfsLine: String = "• CFS: %1${'$'}s (%2${'$'}s)"
    override val exportFireTargetCapitalLine: String = "• FIRE-Zielkapital: %1${'$'}s"
    override val exportFireTimeToRetirementLine: String = "• Zeit bis zum Ruhestand: %1${'$'}s J. (mit %2${'$'}s)"
    override val exportFidelityLine: String = "• Fidelity-Benchmark: %1${'$'}s (%2${'$'}sx Jahreseinkommen) — %3${'$'}s des Ziels"
}
