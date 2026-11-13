package com.example.util.localization

val StringsIt: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.IT

    override val appName: String = "Finanza Personale & Audit FIRE"
    override val tabInputTitle: String = "Dati & Parametri"
    override val tabInputShort: String = "Dati"
    override val tabMetricsTitle: String = "Indicatori Calcolati"
    override val tabMetricsShort: String = "Metriche"
    override val tabScoresTitle: String = "Punteggio & Audit Finanziario"
    override val tabScoresShort: String = "Punteggio"
    override val tabFireTitle: String = "Indipendenza Finanziaria, Pensione, FIRE"
    override val tabFireShort: String = "FIRE & Piano"
    override val quickScorePrefix: String = "Punteggio"

    override val block1Title: String = "1. Entrate & Uscite"
    override val block1Subtitle: String = "Flussi di cassa mensili in entrata e in uscita"
    override val monthlyIncomeLabel: String = "Entrate (mensili)"
    override val monthlyExpensesLabel: String = "Uscite (mensili)"
    override val incomeBreakdownName: String = "Entrate"
    override val incomeHintCollapsed: String = "Cosa include la voce \"Entrate\"? (suggerimento)"
    override val incomeHintExpanded: String = "Nascondi dettaglio delle \"Entrate\""
    override val expensesBreakdownName: String = "Uscite"
    override val expensesHintCollapsed: String = "Cosa include la voce \"Uscite\"? (suggerimento)"
    override val expensesHintExpanded: String = "Nascondi dettaglio delle \"Uscite\""
    override val breakdownInstruction: String = "Calcola e somma le tue voci dall'elenco sottostante, quindi inserisci il totale complessivo nel campo sopra."
    override val breakdownInstructionIcon: String = "💡 "
    override val breakdownCategoryTitleFormat: (title: String) -> String = { title -> "• $title" }
    override val breakdownCategoryItemFormat: (item: String) -> String = { item -> "    – $item" }
    override val incomeCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Lavoro dipendente & Stipendio",
            items = listOf(
                "Lavoro principale (stipendio base, premi, straordinari)",
                "Secondo lavoro, collaborazioni, lavoro part-time"
            )
        ),
        BreakdownCategory(
            title = "Impresa & Lavoro autonomo",
            items = listOf(
                "Utile netto da attività in proprio / partita IVA",
                "Freelance, consulenze, incarichi occasionali"
            )
        ),
        BreakdownCategory(
            title = "Entrate passive & Rendite",
            items = listOf(
                "Interessi su conti deposito e conti remunerati",
                "Dividendi azionari e cedole obbligazionarie",
                "Canoni di locazione da immobili o veicoli",
                "Royalties, diritti d'autore e brevetti"
            )
        ),
        BreakdownCategory(
            title = "Sussidi & Altre entrate",
            items = listOf(
                "Pensioni, sussidi statali, agevolazioni",
                "Assegni di mantenimento, aiuti familiari regolari",
                "Rimborsi fiscali, cashback"
            )
        )
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Spese primarie e alloggio",
            items = listOf(
                "Spesa alimentare e beni per la casa",
                "Affitto o rata mutuo della prima casa",
                "Utenze domestiche (luce, gas, acqua, riscaldamento)",
                "Telefonia, internet, abbonamenti mobili"
            )
        ),
        BreakdownCategory(
            title = "Trasporti & Spostamenti",
            items = listOf(
                "Trasporto pubblico, abbonamenti pendolari",
                "Carburante, parcheggi, pedaggi, lavaggio auto",
                "Manutenzione veicolo, assicurazione RCA, revisioni",
                "Taxi, noleggio, car sharing"
            )
        ),
        BreakdownCategory(
            title = "Salute & Cura della persona",
            items = listOf(
                "Farmaci, visite mediche, dentista, assicurazione sanitaria",
                "Abbigliamento, calzature, igiene personale",
                "Sport, palestra, corsi e attività fisica"
            )
        ),
        BreakdownCategory(
            title = "Tempo libero, Famiglia & Istruzione",
            items = listOf(
                "Ristoranti, bar, cibo a domicilio",
                "Intrattenimento, cinema, viaggi, vacanze, hobby",
                "Istruzione, corsi, libri, attività per i figli",
                "Regali, beneficenza, spese impreviste"
            )
        ),
        BreakdownCategory(
            title = "Servizio del debito",
            items = listOf(
                "Rate mensili di prestiti personali e carte revolving"
            )
        )
    )
    override val cashFlowTitle: String = "Flusso di cassa (Cash Flow):"
    override val cashFlowFormula: String = "(Entrate - Uscite)"
    override val savingsRateTitle: String = "Tasso di risparmio:"
    override val savingsRateFormula: String = "(Cash Flow / Entrate) * 100%"

    override val block2Title: String = "2. Patrimonio (Σ)"
    override val block2Subtitle: String = "Patrimonio liquido, finanziario e immobiliare"
    override val liquidAssetsLabel: String = "Liquido (contanti, conti correnti, depositi a vista)"
    override val financialAssetsLabel: String = "Finanziario (azioni, obbligazioni, ETF, fondi, dossier titoli)"
    override val propertyAssetsLabel: String = "Immobiliare / Beni fisici (immobili, veicoli, beni di valore)"
    override val assetsBreakdownName: String = "Patrimonio"
    override val assetsHintCollapsed: String = "Cosa include il \"Patrimonio\"? (suggerimento)"
    override val assetsHintExpanded: String = "Nascondi dettaglio del \"Patrimonio\""
    override val assetsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Attività liquide",
            items = listOf(
                "Disponibilità in contanti in qualsiasi valuta",
                "Saldi su conti correnti bancari e postali",
                "Conti deposito liberi e fondi monetari",
                "Depositi a breve termine (fino a 1–3 mesi)"
            )
        ),
        BreakdownCategory(
            title = "Attività finanziarie (investimenti)",
            items = listOf(
                "Azioni quotate, ETF, fondi comuni d'investimento",
                "Titoli di Stato (BTP, BOT), obbligazioni societarie",
                "Saldo dossier titoli e conti d'investimento",
                "Certificati di deposito e conti vincolati a lungo termine",
                "Metalli preziosi (lingotti, monete d'oro)",
                "Criptovalute e asset digitali",
                "Fondi pensione e piani previdenziali integrativi"
            )
        ),
        BreakdownCategory(
            title = "Beni fisici (patrimonio materiale)",
            items = listOf(
                "Immobili residenziali (prima casa, seconde case, terreni)",
                "Immobili commerciali, posti auto, box auto",
                "Veicoli (automobili, moto, imbarcazioni)",
                "Macchinari, attrezzature, opere d'arte, gioielli",
                "Partecipazioni in società operative non quotate"
            )
        )
    )
    override val totalAssetsTitle: String = "Patrimonio totale (Σ):"
    override val totalAssetsFormula: String = "(Liquido + Finanziario + Beni fisici)"
    override val toggleEmergencyFundTitle: String = "Includi attività finanziarie nel fondo di emergenza"
    override val toggleEmergencyFundSubOn: String = "Fondo = Liquido + Finanziario"
    override val toggleEmergencyFundSubOff: String = "Fondo = Solo liquidità"

    override val block3Title: String = "3. Debiti & Passività"
    override val block3Subtitle: String = "Mutui, prestiti, carte di credito e obbligazioni"
    override val liabilitiesLabel: String = "Passività (tutti i debiti e impegni finanziari)"
    override val liabilitiesBreakdownName: String = "Passività"
    override val liabilitiesHintCollapsed: String = "Cosa include la voce \"Passività\"? (suggerimento)"
    override val liabilitiesHintExpanded: String = "Nascondi dettaglio delle \"Passività\""
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Debiti garantiti & a lungo termine",
            items = listOf(
                "Debito residuo del mutuo prima casa o altri immobili",
                "Finanziamenti auto (debito residuo)",
                "Prestiti con garanzia ipotecaria"
            )
        ),
        BreakdownCategory(
            title = "Prestiti personali & a breve termine",
            items = listOf(
                "Prestiti personali non finalizzati",
                "Saldi carte di credito revolving e fidi bancari",
                "Piani rateali, servizi BNPL, microprestiti"
            )
        ),
        BreakdownCategory(
            title = "Debiti privati",
            items = listOf(
                "Somme dovute a familiari, parenti o amici",
                "Scritture private e prestiti personali non bancari"
            )
        ),
        BreakdownCategory(
            title = "Altri obblighi finanziari",
            items = listOf(
                "Debiti tributari, cartelle esattoriali, sanzioni",
                "Bollette o spese condominiali arretrate"
            )
        )
    )
    override val netWorthTitle: String = "Patrimonio netto:"
    override val netWorthFormula: String = "(Attività - Passività)"

    override val block4Title: String = "4. Investimenti, Inflazione & Tasse"
    override val block4Subtitle: String = "Parametri di rendimento, potere d'acquisto e tassazione delle plusvalenze"
    override val nominalReturnLabel: String = "Rendimento nominale %"
    override val inflationLabel: String = "Inflazione %"
    override val capitalGainsTaxLabel: String = "Tassazione plusvalenze (%)"
    override val capitalGainsTaxBreakdownName: String = "Imposta sulle plusvalenze"
    override val capitalGainsTaxHintCollapsed: String = "Come viene calcolata l'imposta sulle plusvalenze? (suggerimento)"
    override val capitalGainsTaxHintExpanded: String = "Nascondi dettagli sulle plusvalenze"
    override val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String = { tax, eff -> "Aliquota nominale: $tax | Aliquota effettiva al prelievo: $eff" }
    override val capitalGainsTaxFormula: String = "Aliquota * Quota di guadagno * Quota attività finanziarie"
    override val capitalGainsTaxCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Principio & Base imponibile",
            items = listOf(
                "L'imposta non viene applicata sull'intero importo del disinvestimento, ma solo sul guadagno netto realizzato tra prezzo di vendita e costo d'acquisto (prezzo, commissioni).",
                "L'aliquota colpisce i profitti derivanti dalla vendita di azioni e obbligazioni (differenza tra prezzo di vendita e prezzo di acquisto)."
            )
        ),
        BreakdownCategory(
            title = "Modello realistico di prelievo",
            items = listOf(
                "Quando si liquidano capitali, una parte è rimborso del capitale originariamente versato e una parte è guadagno maturato. Solo i guadagni sono tassabili.",
                "Le quote di capitale proprio e plusvalenza vengono calcolate dinamicamente in base all'orizzonte temporale, all'interesse composto, alla composizione del patrimonio e alle spese previste in pensione.",
                "Esempio: Con un'aliquota del 26% e una quota di guadagno nel prelievo del 60%, l'imposta effettiva = 26% × 60% = 15,6%."
            )
        ),
        BreakdownCategory(
            title = "Impatto sull'orizzonte FIRE e sulla pensione",
            items = listOf(
                "Allunga il tempo necessario al raggiungimento della pensione e aumenta il capitale obiettivo quando è necessario liquidare titoli per sostenere le spese.",
                "Il valore predefinito riflette la media delle economie avanzate (20–26%)."
            )
        )
    )
    override val investmentsBreakdownName: String = "Investimenti & Inflazione"
    override val investmentsHintCollapsed: String = "Cosa considerare in \"Investimenti & Inflazione\"? (suggerimento)"
    override val investmentsHintExpanded: String = "Nascondi suggerimento investimenti e inflazione"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Rendimento nominale del capitale (% annuo)",
            items = listOf(
                "Rendimento medio ponderato atteso del tuo portafoglio complessivo",
                "Interessi maturati su conti deposito e liquidità",
                "Cedole obbligazionarie e dividendi azionari stimati",
                "Rendimento netto da affitti di immobili o beni",
                "Benchmark mercati sviluppati: 6–9% annuo",
                "Benchmark mercati emergenti: 10–16% annuo in valuta locale"
            )
        ),
        BreakdownCategory(
            title = "Inflazione annua attesa (% annua)",
            items = listOf(
                "Obiettivi ufficiali delle banche centrali e stime economiche",
                "Inflazione personale calcolata sul proprio paniere di spesa",
                "Tasso medio storico registrato negli ultimi 5–10 anni",
                "Benchmark economie avanzate: circa 2–3% annuo",
                "Benchmark mercati emergenti: tipicamente 5–9% annuo"
            )
        ),
        BreakdownCategory(
            title = "Rendimento reale del capitale (Equazione di Fisher)",
            items = listOf(
                "Formula: ((1 + Rendimento nominale) / (1 + Inflazione) - 1) * 100%",
                "Crescita netta del potere d'acquisto al netto dell'inflazione",
                "Rendimento reale tipico: 3–5% annuo per mercati sviluppati",
                "Un tasso positivo (> 0%) protegge il patrimonio dalla svalutazione e genera crescita reale"
            )
        )
    )
    override val realReturnTitle: String = "Rendimento reale del capitale:"
    override val realReturnFormula: String = "(1 + Rendimento nominale) / (1 + Inflazione) - 1"

    override val block5Title: String = "5. Età & Orizzonte di pianificazione"
    override val block5Subtitle: String = "Parametri per la pianificazione pensionistica"
    override val birthDateLabel: String = "Data di nascita (AAAA-MM-GG)"
    override val invalidBirthDateError: String = "Inserisci una data di nascita valida"
    override val lifeExpectancyLabel: String = "Aspettativa di vita (anni)"
    override val lifeExpectancyError: String = "L'aspettativa di vita deve essere superiore all'età attuale e non superare i 120 anni"
    override val lifeExpectancyUsedValueFormat: (years: Int) -> String = { years -> "Calcolo: $years ${plural(years, yearPluralForms)}" }
    override val fireHorizonBreakdownName: String = "Età & Orizzonte FIRE"
    override val fireHorizonHintCollapsed: String = "Come determinare età e aspettativa di vita? (suggerimento)"
    override val fireHorizonHintExpanded: String = "Nascondi suggerimento età e orizzonte"
    override val fireHorizonInstruction: String = "Inserisci la data di nascita e l'aspettativa di vita prevista basandoti sui punti di riferimento e sui consigli sottostanti."
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Data di nascita ed età attuale",
            items = listOf(
                "Utilizzata per calcolare i traguardi di capitale per età (Fidelity)",
                "Definisce l'intervallo temporale residuo per sfruttare l'interesse composto",
                "Guida l'allocazione ottimale tra crescita ed efficienza difensiva"
            )
        ),
        BreakdownCategory(
            title = "Aspettativa di vita target (Anni)",
            items = listOf(
                "L'età massima coperta dal modello finanziario pensionistico",
                "Dati demografici di riferimento: 80–84 per gli uomini, 84–89 per le donne",
                "Per prudenza e sicurezza finanziaria, si consiglia di impostare 85–95 anni",
                "Un margine conservativo assicura che il capitale non si esaurisca precocemente"
            )
        ),
        BreakdownCategory(
            title = "Orizzonte di pianificazione (Anni)",
            items = listOf(
                "Formula: Aspettativa di vita meno Età attuale",
                "Il periodo totale in cui il capitale deve sostenere il tuo costo della vita",
                "Parametro matematico fondamentale che determina il capitale FIRE necessario"
            )
        )
    )
    override val horizonTitle: String = "Orizzonte di pianificazione:"
    override val horizonFormula: String = "(Aspettativa di vita - Età attuale)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon anni (età attuale: $age anni)" }
    override val includePropertyInFireLabel: String = "Includi immobili e debiti nel calcolo FIRE"
    override val includePropertyInFireSubOn: String = "Viene usato il patrimonio netto, compresi gli immobili al netto dei debiti"
    override val includePropertyInFireSubOff: String = "Vengono usate solo le attività investibili (liquide + finanziarie)"

    override val calculateButtonText: String = "Calcola indicatori"
    override val exportButtonText: String = "Esporta risultati"
    override val exportChooserTitle: String = "Esporta risultati"

    override val metricsHeaderTitle: String = "Indicatori Calcolati"
    override val metricsHeaderSubtitle: String = "Tutte le metriche chiave con formule & valutazione"
    override val categoryAll: String = "Tutti"
    override val categoryCashFlow: String = "Cash Flow"
    override val categoryCapitalRisks: String = "Patrimonio & Rischi"
    override val categoryBenchmarks: String = "Benchmark"
    override val metricCardInfo: String = "Info"
    override val metricCardHide: String = "Nascondi"
    override val metricCardFormulaPrefix: String = "Formula: "
    override val metricCardExpand: String = "Espandi"
    override val metricCardCollapse: String = "Riduci"

    override val metricCashFlowTitle: String = "Flusso di cassa (Cash Flow)"
    override val metricCashFlowFormula: String = "(Entrate - Uscite)"
    override val metricCashFlowDesc: String = "Cash Flow. Saldo netto tra entrate e uscite finanziarie per periodo.\n\n• Valore positivo (> 0): Guadagni più di quanto spendi.\n• Valore nullo o negativo (≤ 0): Le entrate coprono a stento o sono inferiori alle uscite."
    override val metricCashFlowPositiveSub: String = "Avanzo di bilancio positivo"
    override val metricCashFlowNeutralSub: String = "Entrate pari alle uscite"
    override val metricCashFlowNegativeSub: String = "Disavanzo di bilancio"

    override val metricSavingsRateTitle: String = "Tasso di risparmio"
    override val metricSavingsRateFormula: String = "(Cash Flow / Entrate) * 100%"
    override val metricSavingsRateDesc: (String, String) -> String = { neutral, positive -> "Si consiglia di puntare a un tasso di risparmio superiore al ${positive}% delle entrate totali.\n\n• Fino al ${neutral}%: Negativo; oltre ${neutral}% e fino al ${positive}%: Neutro; superiore al ${positive}%: Positivo." }
    override val metricSavingsRateSub: (String, String) -> String = { neutral, positive -> "Fino a ${neutral}%: Negativo; da ${neutral}% a ${positive}%: Neutro; oltre ${positive}%: Positivo." }

    override val metricEmergencyFundTitle: String = "Fondo di emergenza"
    override val metricEmergencyFundFormula: String = "(Attività liquide)"
    override val metricEmergencyFundDesc: String = "Riserva monetaria di sicurezza per affrontare imprevisti (spese mediche, perdita del lavoro, riparazioni urgenti) senza ricorrere a debiti onerosi."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Liquido + Finanziario"
    override val metricEmergencyFundSubOnlyLiquid: String = "Solo liquidità"

    override val metricSafetyMonthsTitle: String = "Cuscino di sicurezza (Mesi)"
    override val metricSafetyMonthsFormula: String = "(Fondo di emergenza / Spese mensili)"
    override val metricSafetyMonthsDesc: (String, String, String) -> String = { neutral, positive, excellent -> "Numero di mesi di spese correnti che puoi sostenere attingendo unicamente alla tua riserva di sicurezza.\n\n• Obiettivo minimo: ${neutral}–${positive} mesi di spese vive (casa, cibo, utenze). Protegge durante i periodi di transizione.\n• Obiettivo ideale: Fino a ${excellent} mesi per la massima resilienza, specie per autonomi o famiglie con figli." }
    override val metricSafetyMonthsSub: (String, String, String) -> String = { neutral, positive, excellent -> "Norma: ${neutral}–${positive} mesi, Ideale: ${excellent}+ mesi" }
    override val monthsSuffix: String = "mesi"

    override val metricDebtRatioTitle: String = "Rapporto debito/attività (Debt Ratio)"
    override val metricDebtRatioFormula: String = "(Passività / Attività)"
    override val metricDebtRatioDesc: (String, String, String, String) -> String = { zero, positive, neutral, critical -> "Indica la proporzione del patrimonio finanziata tramite debito, evidenziando il grado di rischio finanziario.\n\n• ${zero} – ${positive}: Ottima solidità, patrimonio posseduto quasi per intero su base netta\n• ${positive} – ${neutral}: Livello di debito equilibrato per una famiglia\n• Oltre ${neutral}: Rischio elevato, forte dipendenza dai creditori\n• Oltre ${critical}: I debiti superano il valore delle attività (rischio di insolvenza)" }
    override val metricDebtRatioLowSub: String = "Rischio basso (eccellente)"
    override val metricDebtRatioHighSub: String = "Rischio debito elevato"

    override val metricNetWorthTitle: String = "Patrimonio netto"
    override val metricNetWorthFormula: String = "(Attività - Passività)"
    override val metricNetWorthDesc: String = "Valore totale di tutti i beni posseduti al netto di tutte le passività. Rappresenta la ricchezza netta reale una volta estinti tutti gli impegni."
    override val metricNetWorthSubPrefix: String = "Patrimonio complessivo: "
    override val metricNetWorthNegativeSub: String = "Patrimonio netto negativo (disavanzo!)"
    override val metricNetWorthAssetsFormat: (prefix: String, amount: String) -> String = { prefix, amount -> "$prefix$amount" }
    override val metricNetWorthNegativeAssetsFormat: (prefix: String, amount: String, negative: String) -> String = { prefix, amount, negative -> "$prefix$amount ($negative)" }

    override val metricFidelityTitle: String = "Benchmark: Traguardi di capitale Fidelity"
    override val metricFidelityFormula: String = "Reddito annuo * Moltiplicatore"
    override val metricFidelityDesc: String = "Punti di riferimento Fidelity: età 30 — 1x, 40 — 3x, 50 — 6x, 60 — 8x e 67 — 10x il reddito annuo. L'app interpola linearmente e applica 0,5x prima dei 25 anni. Vengono conteggiati il patrimonio liquido e finanziario al netto dei debiti non coperti da immobili; i beni immobiliari sono esclusi.\n\nEsempio: a 40 anni con 100.000 di reddito annuo, il target è 300.000.\n\nNota: Questa applicazione non è affiliata a Fidelity Investments e adotta questi parametri esclusivamente a scopo divulgativo."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Età: $age anni | Moltiplicatore: ${mult}x" }
    override val metricFidelityProgress: String = "Progresso verso il target Fidelity:"
    override val metricFidelityCapitalFormat: (capital: String) -> String = { capital -> "Capitale considerato: $capital" }
    override val fidelityProgressRatioFormat: (String, String) -> String = { capital, target -> "($capital su $target)" }

    override val metricCfsTitle: String = "Tasso di libertà finanziaria (CFS)"
    override val metricCfsFormula: String = "(Patrimonio netto compresi immobili / Spese annue)"
    override val metricCfsDesc: (String, String, String, String, String, String) -> String = { debt, buffer, neutral, positive, stability, independence -> "Patrimonio netto = tutti i beni, immobili inclusi, meno tutti i debiti. Questo indicatore non significa che si possa vivere di rendita; la scheda FIRE valuta tale fattibilità escludendo gli immobili.\n\nMisura quanti anni di spese correnti sono coperti dal tuo patrimonio netto.\n\n• CFS < ${debt} — Situazione di debito netto\n• CFS >= $debt e < $buffer — Cuscino di emergenza minimo\n• CFS >= $buffer e < $neutral — Fase iniziale di accumulo del capitale\n• CFS >= $neutral e < $positive — Fase di investimento a lungo termine\n• CFS >= $positive e < $stability — I rendimenti generano entrate concrete\n• CFS >= ${stability} e < ${independence} — Solida stabilità finanziaria\n• CFS >= ${independence} — Piena indipendenza finanziaria raggiunta" }

    override val metricRealReturnTitle: String = "Rendimento reale del capitale, %"
    override val metricRealReturnFormula: String = "(1 + Rendimento nominale) / (1 + Inflazione) - 1"
    override val metricRealReturnDesc: String = "Crescita reale del potere d'acquisto al netto dell'inflazione rispetto ai rendimenti nominali. Misura l'arricchimento effettivo."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Nominale: $nom | Inflazione: $infl" }
    override val metricUnavailableValue: String = "—"

    override val scoresHeaderTitle: String = "Riepilogo Punteggi"
    override val scoresHeaderSubtitle: String = "Punteggio globale dell'audit & analisi di dettaglio"
    override val scoresShareReportChooser: String = "Condividi report"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "su $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Risultato complessivo: $score su 10" }
    override val scoresStatusExcellent: String = "Eccezionale solidità finanziaria e ottimi presupposti per la crescita patrimoniale a lungo termine."
    override val scoresStatusGood: String = "Buona forma finanziaria con un solido potenziale di incremento del tasso di risparmio."
    override val scoresStatusModerate: String = "Stabilità moderata. Si raccomanda di rafforzare il fondo di sicurezza."
    override val scoresStatusAttention: String = "Ottimizzazione delle uscite e riduzione attiva del debito vivamente raccomandate."
    override val scoresBreakdownTitle: String = "Dettaglio dei punteggi per componente:"
    override val scoreValuePrefix: String = "Valore: "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score su 10" }
    override val scoreSlashOutOfTen: (score: String) -> String = { score -> "$score / 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String, positiveThreshold: String) -> String = { rate, sr, positiveThreshold ->
        when {
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD -> "Valore di riferimento oltre ${positiveThreshold}%. Mantieni un livello eccellente ($sr)."
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD -> "Valore di riferimento oltre ${positiveThreshold}%. Fino a ${positiveThreshold}% compreso, il valore è neutro ($sr)."
            else -> "Valore di riferimento oltre ${positiveThreshold}%. Un valore nullo o negativo ($sr) è sfavorevole: le uscite eguagliano o superano le entrate."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String, neutral: String, positive: String, excellent: String) -> String = { months, monthsText, neutral, positive, excellent ->
        when {
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT -> "Minimo ${neutral}–${positive} mesi, ideale ${excellent}+ mesi. Risultato eccellente: riserva superiore a ${excellent} mesi ($monthsText)."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD -> "Minimo ${neutral}–${positive} mesi, ideale ${excellent}+ mesi. Buona riserva ($monthsText), obiettivo di ${positive} mesi raggiunto."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD -> "Minimo ${neutral}–${positive} mesi, ideale ${excellent}+ mesi. Riserva di base ($monthsText), ampliamento a ${positive}–${excellent} mesi consigliato."
            else -> "Minimo ${neutral}–${positive} mesi, ideale ${excellent}+ mesi. Riserva insufficiente ($monthsText), rischio finanziario elevato."
        }
    }
    override val debtComment0: (String, String) -> String = { zero, positive -> "$zero – $positive: Ottima stabilità, patrimonio posseduto quasi per intero su base netta." }
    override val debtComment1: (String, String) -> String = { positive, neutral -> "$positive – $neutral: Indebitamento equilibrato, impegni finanziari sotto controllo." }
    override val debtComment2: (String, String) -> String = { neutral, critical -> "$neutral – $critical: Rischio elevato, forte dipendenza dai debiti." }
    override val debtComment3: (String) -> String = { critical -> "Superiore a $critical: Rischio critico, i debiti totali superano il valore delle attività." }

    override val auditorOpinionTitle: String = "Parere dell'Auditor"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Analisi su tutti i $count indicatori della scheda Metriche" }
    override val shareAuditorOpinionChooser: String = "Condividi parere dell'audit"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count positivi" }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count neutri" }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count negativi" }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Indicatori positivi ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Indicatori neutri ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Indicatori negativi ($count)" }
    override val positiveEmptyText: String = "Nessun indicatore positivo individuato al momento. Si consiglia di ottimizzare entrate e uscite."
    override val neutralEmptyText: String = "Nessun indicatore neutro — tutti i parametri si collocano in zona positiva o critica."
    override val negativeEmptyText: String = "🎉 Nessun fattore critico o negativo rilevato — tutti gli indicatori sono in zona sicura!"

    override val fireHeroTitle: String = "Indipendenza Finanziaria, FIRE"
    override val fireHeroSubtitle: String = "Orizzonte pensionistico e proiezione del capitale"
    override val fireSubtabBase: String = "FIRE"
    override val fireSubtabSimulation: String = "Simulazione"
    override val fireTimeToRetire: String = "Tempo alla pensione:"
    override val fireYearsSuffix: String = " anni"
    override val fireHeroAssumption: String = "Il calcolo FIRE considera le attività liquide e finanziarie; gli immobili non sono inclusi e i debiti sono detratti solo per la parte che eccede il valore dei beni fisici"
    override val fireRetirementAgeLabel: String = "Età di pensionamento:"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age anni" }
    override val fireTargetCapitalLabel: String = "Capitale target richiesto:"
    override val fireParamsTitle: String = "Parametri di Età & Orizzonte"
    override val fireBirthDateLabel: String = "Data di nascita:"
    override val fireCurrentAgeLabel: String = "Età attuale:"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age anni" }
    override val fireLifeExpectancyLabel: String = "Aspettativa di vita:"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp anni" }
    override val fireHorizonLabel: String = "(Aspettativa di vita - Età attuale):"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h anni" }
    override val fireRealReturnLabel: String = "Rendimento reale del capitale:"
    override val fireTrajectoryTitle: String = "Traiettoria di accumulo del capitale"
    override val fireTrajectorySubtitle: String = "Crescita patrimoniale verso il punto FIRE ed erogazione della rendita vitalizia"
    override val fireChartAccumulated: String = "Capitale accumulato"
    override val fireChartRequired: String = "Capitale necessario"
    override val fireChartYearsSuffix: String = " anni"
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE: $age anni" }
    override val fireSimSummaryTitle: String = "Riepilogo Simulazione (Impatto complessivo)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ Pensione raggiunta $diff anni prima (tra $target anni, all'età di $age anni)!" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Tempo per la pensione aumentato di $diff anni (tra $target anni, all'età di $age anni)." }
    override val fireSimSame: (target: String) -> String = { target -> "L'orizzonte pensionistico resta invariato (tra $target anni)." }
    override val fireSimWithoutBase: (target: String, age: String) -> String = { target, age -> "Pensione raggiungibile tra $target anni (all'età di $age anni)." }
    override val fireUnreachableText: String = "Non raggiungibile con i parametri attuali"
    override val fireHorizonExhaustedText: String = "Orizzonte di pianificazione esaurito"
    override val fireSimCashFlowRow: String = "Flusso di cassa (risparmio):"
    override val fireSimExpensesRow: String = "Uscite mensili:"
    override val fireSimNetWorthRow: String = "Patrimonio netto (attività − debiti):"
    override val fireSimPropertyRow: String = "Aumento beni fisici (a fine finanziamento):"
    override val fireSimSliderTitle: String = "Simulatore \"Cosa succederebbe se?\""
    override val fireSimReset: String = "Reimposta"
    override val fireSimFineTuning: String = "Regolazione fine (cursori):"
    override val fireSliderSavings: String = "Risparmio mensile:"
    override val fireSliderExpenses: String = "Uscite mensili:"
    override val fireSliderReturn: String = "Rendimento reale del capitale:"
    override val fireSliderBaseSuffix: String = " rispetto alla base"
    override val fireSliderBaseReturnSuffix: String = "% rispetto alla base"
    override val fireSliderFrom: (v: String) -> String = { v -> "da $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "passo $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "fino a +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "aumento"
    override val fireSliderDec: String = "riduz."

    override val scenarioHeaderTitle: String = "Pannello Scenari (Stress Test)"
    override val scenarioHeaderSubtitle: String = "L'impatto complessivo di cursori e opzioni si accumula e viene calcolato congiuntamente, riflettendosi sui valori e sul grafico:"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Totale: $count" }
    override val scenarioPositiveTitle: String = "Eventi positivi"
    override val scenarioPositiveSectionNumber: String = "2.1"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Attivi: $count" }
    override val scenarioPositiveSubtitle: String = "Eventi che accelerano l'accumulo del capitale e la pensione:"
    override val scenarioNegativeTitle: String = "Eventi negativi"
    override val scenarioNegativeSectionNumber: String = "2.2"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Attivi: $count" }
    override val scenarioNegativeSubtitle: String = "Stress test, spese impreviste e scenari di crisi:"
    override val scenarioParamsPrefix: String = "Parametri: "

    override val scenarioSalaryIncreaseTitle: String = "Aumento di stipendio"
    override val scenarioSalaryIncreaseParam: (String) -> String = { percent -> "entrate +${percent}% al mese, permanente" }
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Incremento entrate: +$amt/mese" }

    override val scenarioMarketGrowthTitle: (String) -> String = { percent -> "Crescita dei mercati +${percent}%" }
    override val scenarioMarketGrowthParam: (String) -> String = { percent -> "ETF +${percent}%" }
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Guadagno portafoglio: +$amt" }

    override val scenarioExpenseOptTitle: String = "Ottimizzazione delle spese"
    override val scenarioExpenseOptParam: (String) -> String = { percent -> "−${percent}% sulle uscite mensili, permanente" }
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Risparmio continuativo: -$amt/mese" }

    override val scenarioRelocationTitle: String = "Trasferimento in una regione più economica"
    override val scenarioRelocationParam: (String) -> String = { percent -> "−${percent}% sulle uscite mensili, permanente" }
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Riduzione del costo della vita: -$amt/mese" }

    override val scenarioInheritanceTitle: String = "Eredità o donazione finanziaria"
    override val scenarioInheritanceParam: (String) -> String = { percent -> "entrata una tantum pari al ${percent}% delle entrate mensili" }
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Afflusso di capitale: +$amt (nelle attività liquide)" }

    override val scenarioPropertySaleTitle: String = "Vendita di beni (auto, immobile)"
    override val scenarioPropertySaleParam: (String) -> String = { percent -> "introito una tantum fino al ${percent}% delle entrate mensili" }
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Ricavato vendita: +$amt (nelle attività liquide)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Indipendenza economica dei figli"
    override val scenarioChildrenSelfSufficientParam: (String, String) -> String = { percent, years -> "uscite −${percent}% tra $years anni (spese figli già incluse nelle uscite)" }
    override val scenarioChildrenSelfSufficientDesc: (amt: String, years: String) -> String = { amt, years -> "Riduzione spese: -$amt/mese (tra $years anni)" }

    override val scenarioJobLossTitle: String = "Perdita del lavoro"
    override val scenarioJobLossParam: (String) -> String = { months -> "perdita di $months mesi di entrate" }
    override val scenarioJobLossDesc: (String) -> String = { months -> "$months mesi di entrate attuali persi, coperti con i risparmi" }

    override val scenarioMarketCrashTitle: (String) -> String = { percent -> "Crollo dei mercati −${percent}%" }
    override val scenarioMarketCrashParam: (String) -> String = { percent -> "ETF −${percent}%" }
    override val scenarioMarketCrashDesc: (percent: String, amt: String) -> String = { percent, amt -> "Calo del portafoglio del ${percent}% ($amt)" }

    override val scenarioMortgageTitle: String = "Mutuo casa"
    override val scenarioMortgageParam: (String, String) -> String = { percent, years -> "${percent}% delle entrate per $years anni" }
    override val scenarioMortgageDesc: (pay: String, years: String) -> String = { pay, years -> "Rata: $pay/mese ($years anni)" }

    override val scenarioCarLoanTitle: String = "Prestito auto"
    override val scenarioCarLoanParam: (String, String) -> String = { percent, years -> "${percent}% delle entrate per $years anni" }
    override val scenarioCarLoanDesc: (pay: String, years: String) -> String = { pay, years -> "Rata: $pay/mese ($years anni)" }

    override val scenarioLargeExpenseTitle: String = "Spesa straordinaria rilevante"
    override val scenarioLargeExpenseParam: (String) -> String = { percent -> "spesa pari al ${percent}% delle entrate mensili" }
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Uscita di $amt (coperta da liquidità e investimenti)" }

    override val scenarioInflationTitle: String = "Aumento dei costi"
    override val scenarioInflationParam: (String) -> String = { percent -> "+${percent}% sulle uscite mensili" }
    override val scenarioInflationDesc: (amt: String) -> String = { amt -> "Maggiori uscite: +$amt/mese" }

    override val scenarioChildTitle: String = "Nascita di un figlio"
    override val scenarioChildParam: (String, String) -> String = { percent, years -> "+${percent}% sulle uscite per $years anni" }
    override val scenarioChildDesc: (amt: String, years: String) -> String = { amt, years -> "Spese per il figlio: +$amt/mese (per $years anni)" }

    override val disclaimerTitle: String = "Dichiarazione di non responsabilità"
    override val disclaimerText: String = "Importante: Questo progetto è uno sviluppo personale fornito a titolo informativo «così com'è». L'autore non risponde di eventuali malfunzionamenti, perdite di dati o altri danni derivanti dall'utilizzo dell'applicazione. Tutti i calcoli, le formule, le metriche e le simulazioni hanno finalità puramente informative e non costituiscono consulenza finanziaria, fiscale o di investimento personalizzata. L'utilizzo avviene a proprio rischio."
    override val assumptionsDisclaimerTitle: String = "Dichiarazione di non responsabilità"
    override val assumptionsDisclaimerBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> fireAssumptionsBody(returnRate, inflation) }
    override val fireAssumptionsTitle: String = "Ipotesi del modello"
    override val fireAssumptionsBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> "I calcoli ipotizzano un rendimento costante di $returnRate e un'inflazione di $inflation. L'imposta sulle plusvalenze è stimata in modo semplificato e solo per la quota di attività finanziarie; altre imposte, l'ordine degli eventi di mercato e l'inflazione dei redditi non sono considerati. È un riferimento, non una previsione né una garanzia." }

    // Pluralization & Formats
    override val moneyFormat: String = "%3${'$'}s%1${'$'}s%2${'$'}s%4${'$'}s"
    override val countFormat: String = "%1${'$'}s\u00A0%2${'$'}s"
    override val checkboxPluralForms = PluralForms(one = "casella", other = "caselle")
    override val yearPluralForms = PluralForms(one = "anno", other = "anni")
    override val monthPluralForms = PluralForms(one = "mese", other = "mesi")
    override val pointPluralForms = PluralForms(one = "punto", other = "punti")
    override val simulationBadgeSlidersSuffix: String = "cursori"
    override val simulationBadgeOnlySliders: String = "Cursori di regolazione"

    // Score & Safety Months formats
    override val safetyMonthsValueFormat: String = "%1${'$'}s mesi"
    override val shareScoreReportTemplate: String = "📊 Report Finanziario & Audit del Capitale:\n• Punteggio complessivo: %1${'$'}s su 10\n• Tasso di risparmio: %2${'$'}s/10 (%3${'$'}s)\n• Cuscino di sicurezza: %4${'$'}s/10 (%5${'$'}s mesi)\n• Rapporto d'indebitamento: %6${'$'}s/10 (%7${'$'}s)\n• Libertà finanziaria (CFS): %8${'$'}s/10 (%9${'$'}s)\n• Tempo alla pensione (FIRE): %10${'$'}s anni (a %11${'$'}s)\n"

    // CFS Stage descriptions
    override val cfsStageDebt: (String) -> String = { debt -> "CFS < $debt — Sei in situazione di debito netto" }
    override val cfsStageBuffer: (String, String) -> String = { debt, buffer -> "CFS >= $debt e < $buffer — Disponi di un cuscino di emergenza minimo" }
    override val cfsStageCapital: (String, String) -> String = { buffer, neutral -> "CFS >= $buffer e < $neutral — Avvio della costituzione di capitale finanziario" }
    override val cfsStageInvesting: (String, String) -> String = { neutral, positive -> "CFS >= $neutral e < $positive — Puoi iniziare a investire a lungo termine" }
    override val cfsStageReturns: (String, String) -> String = { positive, stability -> "CFS >= $positive e < $stability — I rendimenti generano entrate significative. L'inflazione incide maggiormente sul capitale" }
    override val cfsStageStability: (String, String) -> String = { stability, independence -> "CFS >= $stability e < $independence — Solida stabilità finanziaria. Come impieghi il tuo tempo insostituibile?" }
    override val cfsStageIndependence: (String) -> String = { independence -> "CFS >= $independence — Il patrimonio netto compresi immobili supera $independence anni di spese" }

    // Auditor Opinion Share Text
    override val auditShareHeaderTitle: String = "📋 PARERE DELL'AUDITOR SU TUTTI GLI INDICATORI"
    override val auditShareScoreLine: String = "⭐️ Punteggio complessivo: %1${'$'}s su 10 punti"
    override val auditShareTotalCountLine: String = "Indicatori complessivi analizzati: %1${'$'}d"
    override val auditSharePositiveHeader: String = "🟢 INDICATORI POSITIVI (%1${'$'}d):"
    override val auditSharePositiveEmpty: String = "• Nessun indicatore positivo individuato"
    override val auditShareNeutralHeader: String = "🟡 INDICATORI NEUTRI (%1${'$'}d):"
    override val auditShareNeutralEmpty: String = "• Nessun indicatore neutro"
    override val auditShareNegativeHeader: String = "🔴 INDICATORI NEGATIVI (%1${'$'}d):"
    override val auditShareNegativeEmpty: String = "• Nessun fattore critico o negativo rilevato 🎉"
    override val auditShareGeneratedBy: String = "💡 Generato dall'app \"Finanza Personale\""
    override val auditShareDisclaimer: String = "⚠️ Importante: Questo progetto è uno sviluppo personale fornito a titolo informativo «così com'è». L'autore non risponde di eventuali danni o malfunzionamenti. Tutti i calcoli hanno finalità puramente informative e non costituiscono consulenza finanziaria. Utilizzo a proprio rischio."

    // Auditor Indicators
    override val auditCashFlowTitle: String = "Flusso di cassa (Cash Flow)"
    override val auditCashFlowPositive: String = "✅ Cash Flow (+%1${'$'}s/mese): Avanzo di bilancio. Le entrate superano le uscite, creando una capacità costante di investimento per far crescere il patrimonio."
    override val auditCashFlowNeutral: String = "⚠️ Cash Flow (0 %1${'$'}s/mese): Bilancio in pareggio (entrate pari alle uscite). Non crei debiti. Non ci sono risorse disponibili per il risparmio, quindi questo indicatore è negativo."
    override val auditCashFlowNegative: String = "🚨 Cash Flow (%1${'$'}s/mese): Disavanzo di bilancio! Le spese superano le entrate: la differenza va coperta con i risparmi o ricorrendo al debito. Se i rendimenti degli investimenti non compensano il disavanzo, le riserve diminuiscono."

    override val auditSavingsRateTitle: String = "Tasso di risparmio"
    override val auditSavingsRatePositive: (String, String) -> String = { value, threshold -> "✅ Capacità di risparmio (Tasso: $value): Tasso di risparmio elevato (superiore a ${threshold}%). Garantisce un accumulo costante di attivi e l'effetto dell'interesse composto." }
    override val auditSavingsRateNeutral: (String, String, String) -> String = { value, neutral, positive -> "ℹ️ Capacità di risparmio (Tasso: $value): Intervallo neutro (oltre $neutral e fino a ${positive}%). L'accumulo prosegue costantemente; l'ottimizzazione delle uscite può aiutare a superare il target di >${positive}%." }
    override val auditSavingsRateNegative: String = "🚨 Capacità di risparmio (Tasso: %1${'$'}s): Flusso di cassa nullo o negativo: il tuo reddito corrente non alimenta il capitale."

    override val auditEmergencyFundTitle: String = "Fondo di emergenza"
    override val auditEmergencyFundPositive: String = "✅ Fondo di emergenza (Volume riserva: %1${'$'}s): La riserva liquida è del tutto sufficiente per coprire emergenze impreviste senza ricorrere a prestiti."
    override val auditEmergencyFundNeutral: (String, String) -> String = { value, months -> "ℹ️ Fondo di emergenza (Volume riserva: $value): Riserva minima di base costituita. Si consiglia di portarla gradualmente a $months mesi di spese." }
    override val auditEmergencyFundNegative: String = "⚠️ Fondo di emergenza (Volume riserva: %1${'$'}s): Riserva criticamente bassa. Elevata vulnerabilità a imprevisti e prestiti forzati."

    override val auditSafetyMonthsTitle: String = "Cuscino di sicurezza (Mesi)"
    override val auditSafetyMonthsHigh: (String, String) -> String = { value, months -> "✅ Cuscino finanziario (Riserva: $value mesi): Pienamente formato con ampio margine. I rischi di perdita del reddito sono attenuati. La liquidità in eccesso oltre $months mesi dovrebbe essere investita." }
    override val auditSafetyMonthsTarget: (String, String) -> String = { value, months -> "✅ Cuscino finanziario (Riserva: $value mesi): Obiettivo di $months mesi raggiunto. La sicurezza finanziaria primaria del nucleo familiare è garantita." }
    override val auditSafetyMonthsNeutral: (String, String) -> String = { value, months -> "ℹ️ Cuscino finanziario (Riserva: $value mesi): Riserva minima di base presente; si raccomanda di estenderla a $months mesi per maggiore resilienza." }
    override val auditSafetyMonthsNegative: (String, String, String) -> String = { value, neutral, positive -> "⚠️ Cuscino finanziario (Riserva: $value mesi): Gravemente insufficiente (sotto $neutral mesi). Rischio di trappole del debito. Priorità: costituire una riserva di ${positive} mesi con urgenza." }

    override val auditDebtRatioTitle: String = "Rapporto di indebitamento"
    override val auditDebtRatioZero: String = "✅ Indebitamento (Debt Ratio: 0.00): Autonomia finanziaria assoluta — zero debiti o prestiti, il 100% degli attivi è posseduto su base netta."
    override val auditDebtRatioConservative: String = "✅ Indebitamento (Debt Ratio: %1${'$'}s, debiti %2${'$'}s degli attivi): Indebitamento prudente e sicuro. Gli impegni sono pienamente coperti dalle attività; il rischio di credito è minimo."
    override val auditDebtRatioModerate: String = "ℹ️ Indebitamento (Debt Ratio: %1${'$'}s, debiti %2${'$'}s degli attivi): Livello di debito moderato. Il debito è sotto controllo ma richiede disciplina negli acquisti importanti."
    override val auditDebtRatioHigh: String = "⚠️ Attenzione: Indebitamento elevato (Debt Ratio: %1${'$'}s, debiti %2${'$'}s degli attivi)! Forte dipendenza dai creditori. Si consiglia rinegoziazione e rimborso rapido."
    override val auditDebtRatioCritical: String = "🚨 RISCHIO CRITICO: Disavanzo patrimoniale (Debt Ratio: %1${'$'}s, debiti superiori agli attivi del %2${'$'}s)! Le passività superano il valore totale delle attività. Necessaria ristrutturazione urgente."

    override val auditNetWorthTitle: String = "Patrimonio netto"
    override val auditNetWorthStrong: String = "✅ Patrimonio netto (Capitale proprio: %1${'$'}s): Saldo fortemente positivo. Il totale delle attività supera ampiamente le passività, offrendo basi solide."
    override val auditNetWorthBalanced: String = "ℹ️ Patrimonio netto (Capitale proprio: %1${'$'}s): Il patrimonio netto non è negativo, ma le passività superano la metà delle attività totali."
    override val auditNetWorthNegative: String = "🚨 Patrimonio netto (Saldo negativo: %1${'$'}s): Patrimonio netto negativo (insolvenza tecnica). Le passività complessive superano tutte le attività possedute."

    override val auditFidelityTitle: String = "Benchmark Fidelity"
    override val auditFidelityPositive: String = "✅ Traguardo di capitale per età (Fidelity: %1${'$'}s del target): Il capitale supera la soglia raccomandata per la tua età %2${'$'}s. Traiettoria eccellente a lungo termine."
    override val auditFidelityNeutral: String = "ℹ️ Traguardo di capitale per età (Fidelity: %1${'$'}s del target): Il capitale corrisponde alle linee guida di risparmio per la tua età %2${'$'}s."
    override val auditFidelityNegativeInsolvent: String = "🚨 Traguardo di capitale per età (Fidelity): Il capitale liquido e finanziario al netto dei debiti è negativo (%1${'$'}s). Le passività impediscono il raggiungimento del traguardo %2${'$'}s."
    override val auditFidelityNegativeLagging: String = "⚠️ Traguardo di capitale per età (Fidelity: %1${'$'}s del target): In ritardo rispetto al benchmark consigliato per la tua età %2${'$'}s. Si raccomanda di accelerare il risparmio."

    override val auditCfsTitle: String = "Tasso di libertà finanziaria"
    override val auditCfsIndependence: (String, String) -> String = { cfs, independence -> "🎉 Libertà finanziaria (CFS: $cfs): Il patrimonio netto, compresi gli immobili, supera $independence anni di spese." }
    override val auditCfsStability: (String, String, String) -> String = { cfs, stability, independence -> "✅ Libertà finanziaria (CFS: $cfs): Il patrimonio netto supera $stability anni di spese; la fase successiva conduce alla soglia di $independence anni." }
    override val auditCfsGrowth: (String, String) -> String = { cfs, stability -> "✅ Libertà finanziaria (CFS: $cfs): Il patrimonio netto copre diversi anni di spese. Prossimo obiettivo: CFS > $stability." }
    override val auditCfsInitial: String = "ℹ️ Libertà finanziaria (CFS: %1${'$'}s): Fase iniziale di costituzione del capitale (%2${'$'}s). Focus primario sulla disciplina del risparmio."
    override val auditCfsNegative: String = "🚨 Liberté finanziaria (CFS: %1${'$'}s): Rapporto negativo (%2${'$'}s). I debiti superano tutti i beni posseduti compresi gli immobili."
    override val auditCfsBuffer: (String, String) -> String = { cfs, buffer -> "📈 Libertà finanziaria (CFS: $cfs): Il patrimonio netto copre al massimo $buffer anni di spese." }
    override val auditCfsPathToIndependence: String = "Sei sulla strada per l'indipendenza, ma c'è ancora strada per il traguardo."
    override val auditCfsCloseToIndependence: (percent: Int) -> String = { percent -> "Sei vicino: il capitale copre il $percent% dell'obiettivo. Il patrimonio netto include gli immobili; la scheda FIRE valuta la possibilità di vivere dei redditi del capitale." }
    override val auditCfsFinanciallyIndependent: String = "Hai raggiunto l'indipendenza finanziaria: il CFS è almeno pari a 30. Il patrimonio netto include gli immobili; la scheda FIRE valuta la possibilità di vivere dei redditi del capitale."

    override val auditRealReturnTitle: String = "Rendimento reale del capitale"
    override val auditRealReturnPositive: String = "✅ Rendimento reale (+%1${'$'}s): Il rendimento supera l'inflazione (%2${'$'}s contro %3${'$'}s). Il capitale è protetto dall'erosione e accresce il potere d'acquisto."
    override val auditRealReturnNeutral: String = "ℹ️ Rendimento reale (0.0%): Il rendimento compensa esattamente l'inflazione. Il capitale mantiene il valore nominale ma non cresce in termini reali."
    override val auditRealReturnNegative: String = "⚠️ Rendimento reale (%1${'$'}s): L'inflazione (%2${'$'}s) supera i rendimenti (%3${'$'}s). Erosione silenziosa dei risparmi e perdita di potere d'acquisto."

    // Export Summary
    override val exportHeaderTitle: String = "📊 REPORT FINANZIARIO — FINANZA PERSONALE"
    override val exportSection1Title: String = "1. Dati di input:"
    override val exportIncomeLine: String = "• Entrate: %1${'$'}s/mese"
    override val exportExpensesLine: String = "• Uscite: %1${'$'}s/mese"
    override val exportLiquidAssetsLine: String = "• Attività liquide: %1${'$'}s"
    override val exportFinancialAssetsLine: String = "• Attività finanziarie: %1${'$'}s"
    override val exportPropertyAssetsLine: String = "• Immobili / Beni fisici: %1${'$'}s"
    override val exportLiabilitiesLine: String = "• Passività (debiti): %1${'$'}s"
    override val exportAgeHorizonLine: (age: Int, lifeExpectancy: Int, horizon: Int) -> String = { age, lifeExpectancy, horizon -> "• Età: $age anni (aspettativa: $lifeExpectancy anni, orizzonte: $horizon anni)" }
    override val exportReturnsInflationLine: String = "• Rendimento nominale: %1${'$'}s | Inflazione: %2${'$'}s"
    override val exportTaxLine: String = "• Tassazione plusvalenze: %1${'$'}s"

    override val exportSection2Title: String = "2. Indicatori calcolati:"
    override val exportCashFlowLine: String = "• Cash Flow: %1${'$'}s/mese"
    override val exportSavingsRateLine: String = "• Tasso di risparmio: %1${'$'}s"
    override val exportEmergencyFundLine: String = "• Fondo di emergenza: %1${'$'}s (%2${'$'}s mesi)"
    override val exportDebtRatioLine: String = "• Rapporto d'indebitamento: %1${'$'}s"
    override val exportNetWorthLine: String = "• Patrimonio netto: %1${'$'}s"
    override val exportRealReturnLine: String = "• Rendimento reale: %1${'$'}s"
    override val exportEffectiveTaxLine: String = "• Aliquota effettiva al prelievo: %1${'$'}s"

    override val exportSection3Title: String = "3. Riepilogo punteggi:"
    override val exportScoreTotalLine: String = "• Punteggio complessivo: %1${'$'}s su 10"
    override val exportScoreSavingsRateLine: String = "• Tasso di risparmio: %1${'$'}d / 10"
    override val exportScoreSafetyMonthsLine: String = "• Cuscino di emergenza: %1${'$'}d / 10"
    override val exportScoreDebtRatioLine: String = "• Carico di debito: %1${'$'}d / 10"
    override val exportScoreCfsLine: String = "• Libertà finanziaria: %1${'$'}d / 10"

    override val exportSection4Title: String = "4. Indipendenza Finanziaria, FIRE:"
    override val exportCfsLine: String = "• CFS: %1${'$'}s (%2${'$'}s)"
    override val exportFireTargetCapitalLine: String = "• Capitale target FIRE: %1${'$'}s"
    override val exportFireTimeToRetirementLine: String = "• Tempo alla pensione: %1${'$'}s anni (a %2${'$'}s)"
    override val exportFidelityLine: String = "• Benchmark Fidelity: %1${'$'}s (%2${'$'}sx reddito annuo) — %3${'$'}s dell'obiettivo"
}
