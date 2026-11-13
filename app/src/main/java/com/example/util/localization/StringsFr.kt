package com.example.util.localization

val StringsFr: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.FR

    override val appName: String = "Finances et FIRE"
    override val tabInputTitle: String = "Données & Paramètres"
    override val tabInputShort: String = "Entrées"
    override val tabMetricsTitle: String = "Indicateurs Calculés"
    override val tabMetricsShort: String = "Indicateurs"
    override val tabScoresTitle: String = "Score & Audit Financier"
    override val tabScoresShort: String = "Score & Audit"
    override val tabFireTitle: String = "Indépendance Financière, Retraite, FIRE"
    override val tabFireShort: String = "FIRE & Plan"
    override val quickScorePrefix: String = "Score"

    override val sampleDataNoticeMessage: String = "Lors du premier lancement, des valeurs d'exemple sont préremplies — vérifiez-les et remplacez-les par les vôtres"
    override val sampleDataNoticeConfirm: String = "Compris"

    override val block1Title: String = "1. Revenus & Dépenses"
    override val block1Subtitle: String = "Flux de trésorerie mensuels entrants et sortants"
    override val monthlyIncomeLabel: String = "Revenus (mensuels)"
    override val monthlyExpensesLabel: String = "Dépenses (mensuelles)"
    override val incomeBreakdownName: String = "Revenus"
    override val incomeHintCollapsed: String = "Que comprend la rubrique \"Revenus\" ? (aide)"
    override val incomeHintExpanded: String = "Masquer le détail des \"Revenus\""
    override val expensesBreakdownName: String = "Dépenses"
    override val expensesHintCollapsed: String = "Que comprend la rubrique \"Dépenses\" ? (aide)"
    override val expensesHintExpanded: String = "Masquer le détail des \"Dépenses\""
    override val breakdownInstruction: String = "Calculez et additionnez vous-même vos postes depuis la liste ci-dessous, puis saisissez le total ci-dessus."
    override val breakdownInstructionIcon: String = "💡 "
    override val breakdownCategoryTitleFormat: (title: String) -> String = { title -> "• $title" }
    override val breakdownCategoryItemFormat: (item: String) -> String = { item -> "    – $item" }
    override val incomeCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Emploi & Salaire",
            items = listOf(
                "Emploi principal (salaire net, primes, heures supplémentaires)",
                "Activité secondaire, travail à temps partiel"
            )
        ),
        BreakdownCategory(
            title = "Entreprise & Indépendant",
            items = listOf(
                "Bénéfice net d'entreprise / micro-entreprise / profession libérale",
                "Freelance, conseil, missions ponctuelles"
            )
        ),
        BreakdownCategory(
            title = "Revenus passifs & Placements",
            items = listOf(
                "Intérêts de livrets bancaires et comptes rémunérés",
                "Dividendes d'actions et coupons d'obligations",
                "Revenus locatifs immobiliers ou de véhicules",
                "Droits d'auteur, propriété intellectuelle"
            )
        ),
        BreakdownCategory(
            title = "Aides sociales & Autres rentrées",
            items = listOf(
                "Pensions, allocations familiales, aides publiques",
                "Pensions alimentaires, soutien familial régulier",
                "Remboursements d'impôt, cashbacks"
            )
        )
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Dépenses vitales du quotidien",
            items = listOf(
                "Alimentation et courses courantes",
                "Loyer ou mensualité de crédit de la résidence principale",
                "Charges du logement (électricité, gaz, eau, chauffage)",
                "Téléphonie, forfaits mobiles, internet"
            )
        ),
        BreakdownCategory(
            title = "Transports & Déplacements",
            items = listOf(
                "Transports en commun, abonnements",
                "Carburant, stationnement, péages, lavage",
                "Entretien du véhicule, assurance auto, réparations",
                "Taxis, VTC, autopartage"
            )
        ),
        BreakdownCategory(
            title = "Santé & Soins personnels",
            items = listOf(
                "Médicaments, consultations médicales, mutuelle, soins dentaires",
                "Habillement, chaussures, soins d'hygiène",
                "Activités sportives, abonnements salle de sport"
            )
        ),
        BreakdownCategory(
            title = "Loisirs, Éducation & Famille",
            items = listOf(
                "Restaurants, cafés, livraisons de repas",
                "Sorties, cinéma, vacances, loisirs créatifs",
                "Formation, livres, scolarité, activités des enfants",
                "Cadeaux, dons, dépenses imprévues"
            )
        ),
        BreakdownCategory(
            title = "Service de la dette",
            items = listOf(
                "Mensualités de crédits à la consommation et cartes de crédit"
            )
        )
    )
    override val cashFlowTitle: String = "Flux de trésorerie (Cash-flow) :"
    override val cashFlowFormula: String = "(Revenus - Dépenses)"
    override val savingsRateTitle: String = "Taux d'épargne :"
    override val savingsRateFormula: String = "(Cash-flow / Revenus) * 100%"

    override val block2Title: String = "2. Patrimoine (Σ)"
    override val block2Subtitle: String = "Actifs liquides, financiers et matériels"
    override val liquidAssetsLabel: String = "Liquide (espèces, comptes courants, livrets)"
    override val financialAssetsLabel: String = "Financier (actions, obligations, ETF, compte-titres/PEA)"
    override val propertyAssetsLabel: String = "Matériel / Immobilier (biens immobiliers, véhicules, objets de valeur)"
    override val assetsBreakdownName: String = "Patrimoine"
    override val assetsHintCollapsed: String = "Que comprend le \"Patrimoine\" ? (aide)"
    override val assetsHintExpanded: String = "Masquer le détail du \"Patrimoine\""
    override val assetsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Actifs liquides",
            items = listOf(
                "Espèces disponibles en toutes devises",
                "Soldes des comptes courants et de dépôt",
                "Livrets d'épargne et fonds monétaires",
                "Dépôts à court terme (jusqu'à 1–3 mois)"
            )
        ),
        BreakdownCategory(
            title = "Actifs financiers (investissements)",
            items = listOf(
                "Actions cotées, ETF, OPCVM / fonds communs",
                "Obligations (d'État, d'entreprises)",
                "Comptes-titres, PEA, assurance-vie",
                "Dépôts à terme de longue durée",
                "Métaux précieux (lingots, pièces d'or)",
                "Cryptomonnaies et actifs numériques",
                "Plans d'épargne retraite (PER, retraites supplémentaires)"
            )
        ),
        BreakdownCategory(
            title = "Actifs matériels (biens)",
            items = listOf(
                "Immobilier résidentiel (résidence principale, appartements, terrains)",
                "Immobilier commercial, parkings, garages",
                "Véhicules (voitures, motos, bateaux)",
                "Matériel professionnel, antiquités, bijoux de valeur",
                "Parts sociales dans des entreprises non cotées"
            )
        )
    )
    override val totalAssetsTitle: String = "Patrimoine total (Σ) :"
    override val totalAssetsFormula: String = "(Liquide + Financier + Matériel)"
    override val toggleEmergencyFundTitle: String = "Inclure les actifs financiers dans le coussin de sécurité"
    override val toggleEmergencyFundSubOn: String = "Coussin = Liquide + Financier"
    override val toggleEmergencyFundSubOff: String = "Coussin = Liquide uniquement"

    override val block3Title: String = "3. Passif & Dettes"
    override val block3Subtitle: String = "Obligations, emprunts, cartes et crédits immobiliers"
    override val liabilitiesLabel: String = "Passif (ensemble des dettes et obligations)"
    override val liabilitiesBreakdownName: String = "Passif"
    override val liabilitiesHintCollapsed: String = "Que comprend le \"Passif\" ? (aide)"
    override val liabilitiesHintExpanded: String = "Masquer le détail du \"Passif\""
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Dettes garanties & à long terme",
            items = listOf(
                "Capital restant dû du crédit immobilier",
                "Crédits automobiles en cours",
                "Prêts hypothécaires et prêts travaux garantis"
            )
        ),
        BreakdownCategory(
            title = "Crédits à la consommation & à court terme",
            items = listOf(
                "Prêts personnels non affectés",
                "Découverts bancaires et cartes à débit différé/revolving",
                "Paiements fractionnés (BNPL), microcrédits"
            )
        ),
        BreakdownCategory(
            title = "Dettes privées & familiales",
            items = listOf(
                "Sommes dues à des proches ou amis",
                "Reconnaissances de dette privées"
            )
        ),
        BreakdownCategory(
            title = "Autres engagements financiers",
            items = listOf(
                "Rappels d'impôts, amendes, créances judiciaires",
                "Factures ou charges en retard"
            )
        )
    )
    override val netWorthTitle: String = "Valeur nette :"
    override val netWorthFormula: String = "(Actifs - Passif)"

    override val block4Title: String = "4. Placements, Inflation & Fiscalité"
    override val block4Subtitle: String = "Paramètres de rendement, pouvoir d'achat et imposition des plus-values"
    override val nominalReturnLabel: String = "Rendement nominal %"
    override val inflationLabel: String = "Inflation %"
    override val capitalGainsTaxLabel: String = "Impôt sur les plus-values (%)"
    override val capitalGainsTaxBreakdownName: String = "Fiscalité des plus-values"
    override val capitalGainsTaxHintCollapsed: String = "Comment est calculé l'impôt sur les plus-values ? (aide)"
    override val capitalGainsTaxHintExpanded: String = "Masquer les détails de fiscalité"
    override val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String = { tax, eff -> "Taux nominal : $tax | Taux effectif au retrait : $eff" }
    override val capitalGainsTaxFormula: String = "Taux nominal * Part de plus-value * Quote-part actifs financiers"
    override val capitalGainsTaxCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Principe & Base imposable",
            items = listOf(
                "L'impôt ne porte pas sur le montant total cédé, mais uniquement sur le gain net réalisé entre le prix de vente et le prix de revient (achat, frais).",
                "Le taux s'applique aux profits tirés de la cession de titres (actions, obligations)."
            )
        ),
        BreakdownCategory(
            title = "Modèle réaliste de retrait",
            items = listOf(
                "Lors d'un retrait, une partie correspond au remboursement du capital investi et l'autre aux plus-values accumulées. Seules les plus-values sont imposées.",
                "Les parts de capital et de plus-value sont calculées dynamiquement selon votre horizon, les intérêts composés, la composition d'actifs et vos dépenses de retraite.",
                "Exemple : Avec un taux de 30% (flat tax) et une quote-part de gain de 60%, le taux effectif = 30% × 60% = 18%."
            )
        ),
        BreakdownCategory(
            title = "Impact sur l'horizon FIRE & la retraite",
            items = listOf(
                "Allonge la durée nécessaire et augmente le capital cible si des titres financiers doivent être vendus pour financer vos dépenses.",
                "La valeur par défaut correspond à la moyenne des économies développées (20–30%)."
            )
        )
    )
    override val investmentsBreakdownName: String = "Placements & Inflation"
    override val investmentsHintCollapsed: String = "Que considérer dans \"Placements & Inflation\" ? (aide)"
    override val investmentsHintExpanded: String = "Masquer l'aide placements et inflation"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Rendement nominal du capital (% par an)",
            items = listOf(
                "Rendement moyen pondéré attendu de votre portefeuille global",
                "Rémunération des livrets et dépôts bancaires",
                "Coupons d'obligations et dividendes d'actions attendus",
                "Rendement locatif net de l'immobilier",
                "Repère marchés développés : 6–9% par an",
                "Repère marchés émergents : 10–16% par an en devise locale"
            )
        ),
        BreakdownCategory(
            title = "Inflation annuelle attendue (% par an)",
            items = listOf(
                "Objectifs officiels des banques centrales et prévisions économiques",
                "Inflation personnelle calculée selon votre panier de consommation",
                "Moyenne historique observée sur les 5 à 10 dernières années",
                "Repère marchés développés : 2–3% par an",
                "Repère marchés émergents : généralement 5–9% par an"
            )
        ),
        BreakdownCategory(
            title = "Rendement réel du capital (Équation de Fisher)",
            items = listOf(
                "Formule : ((1 + Rendement nominal) / (1 + Inflation) - 1) * 100%",
                "Croissance nette du pouvoir d'achat corrigée de l'inflation",
                "Repère habituel : 3–5% net d'inflation pour les économies développées",
                "Un taux positif (> 0%) protège votre épargne de l'érosion monétaire"
            )
        )
    )
    override val realReturnTitle: String = "Rendement réel du capital :"
    override val realReturnFormula: String = "(1 + Rendement nominal) / (1 + Inflation) - 1"

    override val block5Title: String = "5. Âge & Horizon de planification"
    override val block5Subtitle: String = "Paramètres de projection de retraite"
    override val birthDateLabel: String = "Date de naissance (AAAA-MM-JJ)"
    override val invalidBirthDateError: String = "Veuillez indiquer une date de naissance valide"
    override val lifeExpectancyLabel: String = "Espérance de vie (ans)"
    override val lifeExpectancyError: String = "L'espérance de vie doit être supérieure à votre âge actuel et ne pas dépasser 120 ans"
    override val lifeExpectancyUsedValueFormat: (years: Int) -> String = { years -> "Calcul : $years ${plural(years, yearPluralForms)}" }
    override val fireHorizonBreakdownName: String = "Âge & Horizon FIRE"
    override val fireHorizonHintCollapsed: String = "Comment définir l'âge et l'espérance de vie ? (aide)"
    override val fireHorizonHintExpanded: String = "Masquer l'aide sur l'âge et l'horizon"
    override val fireHorizonInstruction: String = "Indiquez votre date de naissance et votre espérance de vie estimée en vous appuyant sur les repères et recommandations ci-dessous."
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Date de naissance et âge actuel",
            items = listOf(
                "Sert à calculer les jalons patrimoniaux par tranche d'âge (Fidelity)",
                "Définit la durée d'accumulation restante pour les intérêts composés",
                "Aide à l'arbitrage entre croissance et sécurisation du capital"
            )
        ),
        BreakdownCategory(
            title = "Espérance de vie visée (Années)",
            items = listOf(
                "L'âge maximal couvert par votre modèle financier de retraite",
                "Repères démographiques : 80–84 ans pour les hommes, 85–90 ans pour les femmes",
                "Par précaution financière, viser 85–95 ans est recommandé",
                "Une marge conservatrice garantit que le capital ne s'épuise pas prématurément"
            )
        ),
        BreakdownCategory(
            title = "Horizon de planification (Années)",
            items = listOf(
                "Formule : Espérance de vie moins Âge actuel",
                "Période totale durant laquelle vos actifs doivent couvrir vos dépenses",
                "Paramètre mathématique fondamental pour le calcul du capital FIRE requis"
            )
        )
    )
    override val horizonTitle: String = "Horizon de planification :"
    override val horizonFormula: String = "(Espérance de vie - Âge actuel)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon ans (âge actuel : $age ans)" }
    override val includePropertyInFireLabel: String = "Inclure biens matériels et dettes dans le calcul FIRE"
    override val includePropertyInFireSubOn: String = "Valeur nette utilisée, y compris immobilier déduction faite des dettes"
    override val includePropertyInFireSubOff: String = "Seuls les actifs investissables sont retenus (liquide + financier)"

    override val calculateButtonText: String = "Voir les indicateurs"
    override val exportButtonText: String = "Exporter les résultats"
    override val exportChooserTitle: String = "Exporter les résultats"

    override val metricsHeaderTitle: String = "Indicateurs Calculés"
    override val metricsHeaderSubtitle: String = "Toutes les métriques financières clés avec formules & analyse"
    override val categoryAll: String = "Tous"
    override val categoryCashFlow: String = "Cash-flow"
    override val categoryCapitalRisks: String = "Patrimoine & Risques"
    override val categoryBenchmarks: String = "Repères"
    override val metricCardInfo: String = "Infos"
    override val metricCardHide: String = "Masquer"
    override val metricCardFormulaPrefix: String = "Formule : "
    override val metricCardExpand: String = "Déplier"
    override val metricCardCollapse: String = "Replier"

    override val metricCashFlowTitle: String = "Flux de trésorerie (Cash-flow)"
    override val metricCashFlowFormula: String = "(Revenus - Dépenses)"
    override val metricCashFlowDesc: String = "Cash-flow. Solde net des flux financiers mensuels.\n\n• Valeur positive (> 0) : Vous gagnez plus que vous ne dépensez.\n• Valeur nulle ou négative (≤ 0) : Les revenus couvrent tout juste ou sont inférieurs aux dépenses."
    override val metricCashFlowPositiveSub: String = "Excédent budgétaire positif"
    override val metricCashFlowNeutralSub: String = "Revenus égaux aux dépenses"
    override val metricCashFlowNegativeSub: String = "Déficit budgétaire"

    override val metricSavingsRateTitle: String = "Taux d'épargne"
    override val metricSavingsRateFormula: String = "(Cash-flow / Revenus) * 100%"
    override val metricSavingsRateDesc: (String, String) -> String = { neutral, positive -> "Il est recommandé de viser un taux d'épargne supérieur à ${positive}% des revenus du foyer.\n\n• Inférieur ou égal à ${neutral}% : Négatif ; supérieur à ${neutral}% et jusqu'à ${positive}% : Neutre ; supérieur à ${positive}% : Positif." }
    override val metricSavingsRateSub: (String, String) -> String = { neutral, positive -> "Inférieur ou égal à ${neutral}% : Négatif ; supérieur à ${neutral}% jusqu'à ${positive}% : Neutre ; supérieur à ${positive}% : Positif." }

    override val metricEmergencyFundTitle: String = "Fonds d'urgence"
    override val metricEmergencyFundFormula: String = "(Actifs liquides)"
    override val metricEmergencyFundDesc: String = "Épargne de précaution destinée aux aléas (maladie, perte d'emploi, réparations urgentes) afin d'éviter tout endettement coûteux."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Liquide + Financier"
    override val metricEmergencyFundSubOnlyLiquid: String = "Liquide uniquement"

    override val metricSafetyMonthsTitle: String = "Coussin de sécurité (Mois)"
    override val metricSafetyMonthsFormula: String = "(Fonds d'urgence / Dépenses mensuelles)"
    override val metricSafetyMonthsDesc: (String, String, String) -> String = { neutral, positive, excellent -> "Nombre de mois de dépenses courantes couverts par votre réserve d'urgence.\n\n• Objectif minimal : ${neutral} à ${positive} mois de dépenses vitales. Protège les périodes de transition.\n• Objectif idéal : Jusqu'à ${excellent} mois pour une résilience maximale (indépendants, familles)." }
    override val metricSafetyMonthsSub: (String, String, String) -> String = { neutral, positive, excellent -> "Norme : ${neutral}–${positive} mois, Idéal : ${excellent}+ mois" }
    override val monthsSuffix: String = "mois"

    override val metricDebtRatioTitle: String = "Ratio d'endettement (Dette / Actifs)"
    override val metricDebtRatioFormula: String = "(Dettes / Actifs)"
    override val metricDebtRatioDesc: (String, String, String, String) -> String = { zero, positive, neutral, critical -> "Mesure la part du patrimoine financée par de la dette, reflétant le niveau de risque financier.\n\n• ${zero} – ${positive} : Excellente solidité, patrimoine détenu sur fonds propres\n• ${positive} – ${neutral} : Niveau d'endettement maîtrisé pour un foyer\n• Supérieur à ${neutral} : Risque accru, forte dépendance aux créanciers\n• Supérieur à ${critical} : Les dettes dépassent les actifs (risque d'insolvabilité)" }
    override val metricDebtRatioLowSub: String = "Faible risque (excellent)"
    override val metricDebtRatioHighSub: String = "Risque d'endettement élevé"

    override val metricNetWorthTitle: String = "Valeur nette"
    override val metricNetWorthFormula: String = "(Actifs - Passif)"
    override val metricNetWorthDesc: String = "Valeur totale des biens diminuée de toutes les dettes. Représente ce qui vous appartient réellement une fois tous les engagements soldés."
    override val metricNetWorthSubPrefix: String = "Patrimoine total : "
    override val metricNetWorthNegativeSub: String = "Valeur nette négative (déficit !)"
    override val metricNetWorthAssetsFormat: (prefix: String, amount: String) -> String = { prefix, amount -> "$prefix$amount" }
    override val metricNetWorthNegativeAssetsFormat: (prefix: String, amount: String, negative: String) -> String = { prefix, amount, negative -> "$prefix$amount ($negative)" }

    override val metricFidelityTitle: String = "Repère : Jalons de capital Fidelity"
    override val metricFidelityFormula: String = "Revenu annuel * Multiplicateur"
    override val metricFidelityDesc: String = "Repères Fidelity : 30 ans — 1x, 40 ans — 3x, 50 ans — 6x, 60 ans — 8x et 67 ans — 10x le revenu annuel. L'application interpole linéairement et applique 0,5x avant 25 ans. Sont comptabilisés les actifs liquides et financiers déduction faite des dettes non couvertes par l'immobilier.\n\nExemple : À 40 ans avec 100 000 de revenu annuel, la cible est de 300 000.\n\nNote : Cette application n'est pas affiliée à Fidelity Investments et utilise ces repères à titre purement pédagogique."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Âge : $age ans | Multiplicateur : ${mult}x" }
    override val metricFidelityProgress: String = "Progression vers l'objectif Fidelity :"
    override val metricFidelityCapitalFormat: (capital: String) -> String = { capital -> "Capital pris en compte : $capital" }
    override val fidelityProgressRatioFormat: (String, String) -> String = { capital, target -> "($capital sur $target)" }

    override val metricCfsTitle: String = "Taux de liberté financière (CFS)"
    override val metricCfsFormula: String = "(Patrimoine net, immobilier inclus / Dépenses annuelles)"
    override val metricCfsDesc: (String, String, String, String, String, String) -> String = { debt, buffer, neutral, positive, stability, independence -> "Patrimoine net = ensemble des actifs, immobilier inclus, déduction faite des dettes. Cet indicateur n'implique pas que vous puissiez vivre des rentes du capital ; l'onglet FIRE évalue cette autonomie sans l'immobilier.\n\nMesure le nombre d'années de dépenses courantes couvertes par votre patrimoine net.\n\n• CFS < ${debt} — Situation de dette nette\n• CFS >= $debt et < $buffer — Épargne de sécurité minimale\n• CFS >= $buffer et < $neutral — Phase initiale d'accumulation de capital\n• CFS >= $neutral et < $positive — Phase d'investissement à long terme\n• CFS >= $positive et < $stability — Les rendements génèrent des revenus tangibles\n• CFS >= ${stability} et < ${independence} — Stabilité financière solide\n• CFS >= ${independence} — Indépendance financière totale atteinte" }

    override val metricRealReturnTitle: String = "Rendement réel du capital, %"
    override val metricRealReturnFormula: String = "(1 + Rendement nominal) / (1 + Inflation) - 1"
    override val metricRealReturnDesc: String = "Gain réel de pouvoir d'achat après déduction de l'inflation du rendement nominal. Mesure l'enrichissement effectif."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Nominal : $nom | Inflation : $infl" }
    override val metricUnavailableValue: String = "—"

    override val scoresHeaderTitle: String = "Synthèse des scores"
    override val scoresHeaderSubtitle: String = "Score global d'audit & détail"
    override val scoresShareReportChooser: String = "Partager le rapport"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "sur $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Résultat global : $score sur 10" }
    override val scoresStatusExcellent: String = "Résilience financière remarquable et excellentes dispositions pour l'investissement à long terme."
    override val scoresStatusGood: String = "Bonne santé financière avec un beau potentiel d'accroissement du taux d'épargne."
    override val scoresStatusModerate: String = "Stabilité modérée. Il est conseillé de renforcer le fonds d'urgence."
    override val scoresStatusAttention: String = "Optimisation des dépenses et désendettement vivement recommandés."
    override val scoresBreakdownTitle: String = "Détail des scores par composante :"
    override val scoreValuePrefix: String = "Valeur : "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score sur 10" }
    override val scoreSlashOutOfTen: (score: String) -> String = { score -> "$score / 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String, positiveThreshold: String) -> String = { rate, sr, positiveThreshold ->
        when {
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD -> "Repère supérieur à ${positiveThreshold}%. Vous maintenez un excellent taux ($sr)."
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD -> "Repère supérieur à ${positiveThreshold}%. Jusqu'à ${positiveThreshold}% inclus, le niveau est neutre ($sr)."
            else -> "Repère supérieur à ${positiveThreshold}%. Un taux nul ou négatif ($sr) est défavorable : les dépenses égalent ou dépassent les revenus."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String, neutral: String, positive: String, excellent: String) -> String = { months, monthsText, neutral, positive, excellent ->
        when {
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT -> "Minimum ${neutral}–${positive} mois, idéal ${excellent}+ mois. Excellent résultat : réserve supérieure à ${excellent} mois ($monthsText)."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD -> "Minimum ${neutral}–${positive} mois, idéal ${excellent}+ mois. Bonne réserve ($monthsText), objectif de ${positive} mois atteint."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD -> "Minimum ${neutral}–${positive} mois, idéal ${excellent}+ mois. Réserve de base ($monthsText), extension à ${positive}–${excellent} mois conseillée."
            else -> "Minimum ${neutral}–${positive} mois, idéal ${excellent}+ mois. Réserve insuffisante ($monthsText), risque financier accru."
        }
    }
    override val debtComment0: (String, String) -> String = { zero, positive -> "$zero – $positive : Excellente stabilité, patrimoine financé quasi intégralement sur fonds propres." }
    override val debtComment1: (String, String) -> String = { positive, neutral -> "$positive – $neutral : Endettement équilibré, engagements sous contrôle." }
    override val debtComment2: (String, String) -> String = { neutral, critical -> "$neutral – $critical : Risque accru, forte dépendance à l'endettement." }
    override val debtComment3: (String) -> String = { critical -> "Supérieur à $critical : Risque critique, le passif dépasse la valeur du patrimoine." }

    override val auditorOpinionTitle: String = "Avis de l'Auditeur"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Analyse portant sur l'ensemble des $count indicateurs" }
    override val shareAuditorOpinionChooser: String = "Partager l'avis d'audit"
    override val exportAuditorOpinionButtonText: String = "Exporter l'avis"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count positif" }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count neutre" }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count négatif" }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Indicateurs positifs ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Indicateurs neutres ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Indicateurs négatifs ($count)" }
    override val positiveEmptyText: String = "Aucun facteur positif identifié pour le moment. Optimisation budgétaire recommandée."
    override val neutralEmptyText: String = "Aucun indicateur neutre — toutes les métriques sont en zone favorable ou critique."
    override val negativeEmptyText: String = "🎉 Aucun facteur de risque critique détecté — tous les indicateurs sont en zone sûre !"

    override val fireHeroTitle: String = "Indépendance Financière, FIRE"
    override val fireHeroSubtitle: String = "Horizon de retraite et projection du capital"
    override val fireSubtabBase: String = "FIRE"
    override val fireSubtabSimulation: String = "Simulation"
    override val fireTimeToRetire: String = "Temps jusqu'à la retraite :"
    override val fireYearsSuffix: String = " ans"
    override val fireHeroAssumption: String = "Le calcul FIRE retient les actifs liquides et financiers ; l'immobilier n'est pas inclus et les dettes ne sont déduites que pour leur part excédant les biens matériels"
    override val fireRetirementAgeLabel: String = "Âge de départ à la retraite :"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age ans" }
    override val fireTargetCapitalLabel: String = "Capital cible requis :"
    override val fireParamsTitle: String = "Paramètres d'Âge & d'Horizon"
    override val fireBirthDateLabel: String = "Date de naissance :"
    override val fireCurrentAgeLabel: String = "Âge actuel :"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age ans" }
    override val fireLifeExpectancyLabel: String = "Espérance de vie :"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp ans" }
    override val fireHorizonLabel: String = "(Espérance de vie - Âge actuel) :"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h ans" }
    override val fireRealReturnLabel: String = "Rendement réel du capital :"
    override val fireTrajectoryTitle: String = "Trajectoire d'accumulation du capital"
    override val fireTrajectorySubtitle: String = "Croissance du patrimoine jusqu'au point FIRE et financement de la retraite"
    override val fireChartAccumulated: String = "Capital accumulé"
    override val fireChartRequired: String = "Capital requis"
    override val fireChartYearsSuffix: String = " ans"
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE : $age ans" }
    override val fireSimSummaryTitle: String = "Synthèse de la simulation (Effet cumulé)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ Retraite atteinte $diff ans plus tôt (dans $target ans, à l'âge de $age ans) !" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Délai vers la retraite allongé de $diff ans (dans $target ans, à l'âge de $age ans)." }
    override val fireSimSame: (target: String) -> String = { target -> "L'horizon de retraite demeure inchangé (dans $target ans)." }
    override val fireSimWithoutBase: (target: String, age: String) -> String = { target, age -> "Retraite accessible dans $target ans (à l'âge de $age ans)." }
    override val fireUnreachableText: String = "Inaccessible avec les paramètres actuels"
    override val fireUnreachableSimulationText: String = "Inaccessible avec les paramètres de simulation"
    override val fireReachedBaseText: String = "Objectif FIRE atteint"
    override val fireReachedSimulationText: String = "Objectif FIRE atteint dans le scénario"
    override val fireLessThanHundredthYearText: String = "Moins de 0,01 an"
    override val fireHorizonExhaustedText: String = "Horizon de planification épuisé"
    override val fireSimCashFlowRow: String = "Cash-flow (épargne) :"
    override val fireSimExpensesRow: String = "Dépenses mensuelles :"
    override val fireSimNetWorthRow: String = "Valeur nette (actifs − dettes) :"
    override val fireSimPropertyRow: String = "Hausse de valeur matérielle (fin de crédit) :"
    override val fireSimSliderTitle: String = "Simulateur \"Que se passerait-il si ?\""
    override val fireSimReset: String = "Réinitialiser"
    override val fireSimFineTuning: String = "Réglage fin (curseurs) :"
    override val fireSliderSavings: String = "Épargne mensuelle :"
    override val fireSliderExpenses: String = "Dépenses mensuelles :"
    override val fireSliderReturn: String = "Rendement réel du capital :"
    override val fireSliderBaseSuffix: String = " par rapport à la base"
    override val fireSliderBaseReturnSuffix: String = "% par rapport à la base"
    override val fireSliderFrom: (v: String) -> String = { v -> "de $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "pas de $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "jusqu'à +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "hausse"
    override val fireSliderDec: String = "baisse"

    override val scenarioHeaderTitle: String = "Panneau de scénarios (Stress tests)"
    override val scenarioHeaderSubtitle: String = "L'impact cumulé des curseurs et des scénarios est calculé ensemble et se reflète sur les chiffres et la courbe de trajectoire :"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Total : $count" }
    override val scenarioPositiveTitle: String = "Événements favorables"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Actifs : $count" }
    override val scenarioPositiveSubtitle: String = "Événements accélérant l'accumulation du capital et la retraite :"
    override val scenarioNegativeTitle: String = "Événements défavorables"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Actifs : $count" }
    override val scenarioNegativeSubtitle: String = "Stress tests, dépenses imprévues et scénarios de crise :"
    override val scenarioParamsPrefix: String = "Paramètres : "

    override val scenarioSalaryIncreaseTitle: String = "Augmentation de salaire"
    override val scenarioSalaryIncreaseParam: (String) -> String = { percent -> "revenus +${percent}% par mois, permanent" }
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Hausse des revenus : +$amt/mois" }

    override val scenarioMarketGrowthTitle: (String) -> String = { percent -> "Hausse des marchés +${percent}%" }
    override val scenarioMarketGrowthParam: (String) -> String = { percent -> "ETF +${percent}%" }
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Gain du portefeuille : +$amt" }

    override val scenarioExpenseOptTitle: String = "Optimisation des dépenses"
    override val scenarioExpenseOptParam: (String) -> String = { percent -> "−${percent}% sur les dépenses mensuelles, permanent" }
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Économie récurrente : -$amt/mois" }

    override val scenarioRelocationTitle: String = "Déménagement dans une région moins chère"
    override val scenarioRelocationParam: (String) -> String = { percent -> "−${percent}% sur les dépenses mensuelles, permanent" }
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Baisse du coût de la vie : -$amt/mois" }

    override val scenarioInheritanceTitle: String = "Héritage ou don financier"
    override val scenarioInheritanceParam: (String) -> String = { percent -> "apport ponctuel de ${percent}% du revenu mensuel" }
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Entrée de capital : +$amt (dans les liquidités)" }

    override val scenarioPropertySaleTitle: String = "Vente d'un bien matériel (véhicule, immobilier)"
    override val scenarioPropertySaleParam: (String) -> String = { percent -> "cession ponctuelle jusqu'à ${percent}% du revenu mensuel" }
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Produit de cession : +$amt (dans les liquidités)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Indépendance financière des enfants"
    override val scenarioChildrenSelfSufficientParam: (String, String) -> String = { percent, years -> "dépenses −${percent}% dans $years ans (frais des enfants déjà inclus)" }
    override val scenarioChildrenSelfSufficientDesc: (amt: String, years: String) -> String = { amt, years -> "Baisse des dépenses : -$amt/mois (dans $years ans)" }

    override val scenarioJobLossTitle: String = "Perte d'emploi"
    override val scenarioJobLossParam: (String) -> String = { months -> "perte de $months mois de revenus" }
    override val scenarioJobLossDesc: (String) -> String = { months -> "$months mois de revenus actuels perdus, puisés sur l'épargne" }

    override val scenarioMarketCrashTitle: (String) -> String = { percent -> "Krach boursier −${percent}%" }
    override val scenarioMarketCrashParam: (String) -> String = { percent -> "ETF −${percent}%" }
    override val scenarioMarketCrashDesc: (percent: String, amt: String) -> String = { percent, amt -> "Baisse du portefeuille de ${percent}% ($amt)" }

    override val scenarioMortgageTitle: String = "Crédit immobilier (résidence)"
    override val scenarioMortgageParam: (String, String) -> String = { percent, years -> "${percent}% des revenus sur $years ans" }
    override val scenarioMortgageDesc: (pay: String, years: String) -> String = { pay, years -> "Mensualité : $pay/mois ($years ans)" }

    override val scenarioCarLoanTitle: String = "Crédit automobile"
    override val scenarioCarLoanParam: (String, String) -> String = { percent, years -> "${percent}% des revenus sur $years ans" }
    override val scenarioCarLoanDesc: (pay: String, years: String) -> String = { pay, years -> "Mensualité : $pay/mois ($years ans)" }

    override val scenarioLargeExpenseTitle: String = "Grosse dépense exceptionnelle"
    override val scenarioLargeExpenseParam: (String) -> String = { percent -> "dépense de ${percent}% du revenu mensuel" }
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Sortie de fonds de $amt (couverte par les liquidités et placements)" }

    override val scenarioInflationTitle: String = "Hausse des coûts"
    override val scenarioInflationParam: (String) -> String = { percent -> "+${percent}% sur les dépenses mensuelles" }
    override val scenarioInflationDesc: (amt: String) -> String = { amt -> "Dépenses accrues : +$amt/mois" }

    override val scenarioChildTitle: String = "Naissance d'un enfant"
    override val scenarioChildParam: (String, String) -> String = { percent, years -> "+${percent}% de dépenses sur $years ans" }
    override val scenarioChildDesc: (amt: String, years: String) -> String = { amt, years -> "Frais d'éducation/garde : +$amt/mois (pendant $years ans)" }

    override val disclaimerTitle: String = "Avertissement"
    override val disclaimerText: String = "Important : Ce projet est un développement personnel fourni à titre indicatif « en l'état ». L'auteur décline toute responsabilité quant à d'éventuels dysfonctionnements, pertes de données ou préjudices résultant de l'utilisation de ce logiciel. L'ensemble des calculs, formules, métriques et simulations a une vocation purement informative et ne saurait constituer un conseil financier, d'investissement ou fiscal personnalisé. Vous utilisez cet outil sous votre entière responsabilité."
    override val assumptionsDisclaimerTitle: String = "Avertissement"
    override val assumptionsDisclaimerBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> fireAssumptionsBody(returnRate, inflation) }
    override val fireAssumptionsTitle: String = "Hypothèses du modèle"
    override val fireAssumptionsBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> "Les calculs supposent un rendement constant de $returnRate et une inflation de $inflation. L'impôt sur les plus-values est estimé de manière simplifiée et uniquement pour la part des actifs financiers ; les autres impôts, l'ordre des événements de marché et l'inflation des revenus ne sont pas pris en compte. Il s'agit d'un repère, et non d'une prévision ou d'une garantie." }

    // Pluralization & Formats
    override val moneyFormat: String = "%3${'$'}s%1${'$'}s%2${'$'}s%4${'$'}s"
    override val countFormat: String = "%1${'$'}s\u00A0%2${'$'}s"
    override val checkboxPluralForms = PluralForms(one = "case à cocher", other = "cases à cocher")
    override val yearPluralForms = PluralForms(one = "an", other = "ans")
    override val monthPluralForms = PluralForms(one = "mois", other = "mois")
    override val pointPluralForms = PluralForms(one = "point", other = "points")
    override val simulationBadgeSlidersSuffix: String = "curseurs"
    override val simulationBadgeOnlySliders: String = "Curseurs d'ajustement"

    // Score & Safety Months formats
    override val safetyMonthsValueFormat: String = "%1${'$'}s mois"
    override val shareScoreReportTemplate: String = "📊 Rapport Financier & Audit du Capital :\n• Score global : %1${'$'}s sur 10\n• Taux d'épargne : %2${'$'}s/10 (%3${'$'}s)\n• Coussin de sécurité : %4${'$'}s/10 (%5${'$'}s mois)\n• Ratio d'endettement : %6${'$'}s/10 (%7${'$'}s)\n• Liberté financière (CFS) : %8${'$'}s/10 (%9${'$'}s)\n• Délai jusqu'à la retraite (FIRE) : %10${'$'}s ans (à %11${'$'}s)\n"

    // CFS Stage descriptions
    override val cfsStageDebt: (String) -> String = { debt -> "CFS < $debt — Vous êtes en dette nette" }
    override val cfsStageBuffer: (String, String) -> String = { debt, buffer -> "CFS >= $debt et < $buffer — Vous disposez d'un coussin d'urgence minimal" }
    override val cfsStageCapital: (String, String) -> String = { buffer, neutral -> "CFS >= $buffer et < $neutral — Début de constitution d'un capital financier" }
    override val cfsStageInvesting: (String, String) -> String = { neutral, positive -> "CFS >= $neutral et < $positive — Vous pouvez commencer à investir sur le long terme" }
    override val cfsStageReturns: (String, String) -> String = { positive, stability -> "CFS >= $positive et < $stability — Les rendements génèrent des revenus notables. L'inflation pèse davantage sur le capital" }
    override val cfsStageStability: (String, String) -> String = { stability, independence -> "CFS >= $stability et < $independence — Stabilité financière solide. Comment valorisez-vous votre temps irremplaçable ?" }
    override val cfsStageIndependence: (String) -> String = { independence -> "CFS >= $independence — Le patrimoine net, immobilier compris, dépasse $independence années de dépenses" }

    // Auditor Opinion Share Text
    override val auditShareHeaderTitle: String = "📋 AVIS DE L'AUDITEUR SUR L'ENSEMBLE DES INDICATEURS"
    override val auditShareScoreLine: String = "⭐️ Score global : %1${'$'}s sur 10 points"
    override val auditShareTotalCountLine: String = "Total des indicateurs analysés : %1${'$'}d"
    override val auditSharePositiveHeader: String = "🟢 INDICATEURS POSITIFS (%1${'$'}d) :"
    override val auditSharePositiveEmpty: String = "• Aucun indicateur positif identifié"
    override val auditShareNeutralHeader: String = "🟡 INDICATEURS NEUTRES (%1${'$'}d) :"
    override val auditShareNeutralEmpty: String = "• Aucun indicateur neutre"
    override val auditShareNegativeHeader: String = "🔴 INDICATEURS NÉGATIFS (%1${'$'}d) :"
    override val auditShareNegativeEmpty: String = "• Aucun facteur critique ou négatif détecté 🎉"
    override val auditShareGeneratedBy: String = "💡 Généré avec l'application \"Finances Personnelles\""
    override val auditShareDisclaimer: String = "⚠️ Important : Ce projet est un développement personnel fourni à titre indicatif « en l'état ». L'auteur décline toute responsabilité quant à d'éventuels dysfonctionnements ou préjudices. Tous les calculs ont une vocation informative et ne constituent pas un conseil financier ou fiscal. Utilisation sous votre entière responsabilité."

    // Auditor Indicators
    override val auditCashFlowTitle: String = "Flux de trésorerie (Cash-flow)"
    override val auditCashFlowPositive: String = "✅ Cash-flow (+%1${'$'}s/mois) : Excédent budgétaire. Vos revenus dépassent vos dépenses, dégageant une capacité régulière d'investissement et d'enrichissement."
    override val auditCashFlowNeutral: String = "⚠️ Cash-flow (0 %1${'$'}s/mois) : Budget à l'équilibre (revenus égaux aux dépenses). Aucun endettement supplémentaire. Aucun surplus n'est disponible pour épargner, cet indicateur est donc négatif."
    override val auditCashFlowNegative: String = "🚨 Cash-flow (%1${'$'}s/mois) : Déficit budgétaire ! Les dépenses dépassent les revenus : la différence doit être couverte par l'épargne ou l'endettement. Si les rendements des placements ne compensent pas le déficit, les réserves diminuent."

    override val auditSavingsRateTitle: String = "Taux d'épargne"
    override val auditSavingsRatePositive: (String, String) -> String = { value, threshold -> "✅ Capacité d'épargne (Taux : $value) : Taux d'épargne élevé (au-dessus de ${threshold}%). Assure une accumulation régulière d'actifs et l'effet des intérêts composés." }
    override val auditSavingsRateNeutral: (String, String, String) -> String = { value, neutral, positive -> "ℹ️ Capacité d'épargne (Taux : $value) : Zone neutre (entre $neutral et ${positive}%). La constitution de capital avance ; optimiser les dépenses peut aider à franchir le cap des >${positive}%." }
    override val auditSavingsRateNegative: String = "🚨 Capacité d'épargne (Taux : %1${'$'}s) : Cash-flow nul ou négatif : vos revenus courants n'alimentent pas votre capital."

    override val auditEmergencyFundTitle: String = "Fonds d'urgence"
    override val auditEmergencyFundPositive: String = "✅ Fonds d'urgence (Volume : %1${'$'}s) : Réserve liquide pleinement suffisante pour surmonter les aléas majeurs sans recours au crédit."
    override val auditEmergencyFundNeutral: (String, String) -> String = { value, months -> "ℹ️ Fonds d'urgence (Volume : $value) : Coussin minimal en place. Il est recommandé de le porter progressivement à $months mois de dépenses." }
    override val auditEmergencyFundNegative: String = "⚠️ Fonds d'urgence (Volume : %1${'$'}s) : Réserve critique trop faible. Forte vulnérabilité aux dépenses imprévues et aux découverts."

    override val auditSafetyMonthsTitle: String = "Coussin de sécurité (Mois)"
    override val auditSafetyMonthsHigh: (String, String) -> String = { value, months -> "✅ Matelas de sécurité (Coussin : $value mois) : Confortable réserve constituée. Les risques de baisse de revenus sont atténués. La liquidité au-delà de $months mois gagne à être investie." }
    override val auditSafetyMonthsTarget: (String, String) -> String = { value, months -> "✅ Matelas de sécurité (Coussin : $value mois) : Objectif de $months mois atteint. La sécurité financière de base du foyer est assurée." }
    override val auditSafetyMonthsNeutral: (String, String) -> String = { value, months -> "ℹ️ Matelas de sécurité (Coussin : $value mois) : Réserve minimale en place ; extension à $months mois conseillée pour une meilleure résilience." }
    override val auditSafetyMonthsNegative: (String, String, String) -> String = { value, neutral, positive -> "⚠️ Matelas de sécurité (Coussin : $value mois) : Nettement insuffisant (moins de $neutral mois). Risque d'engrenage de dettes. Priorité : bâtir un coussin de ${positive} mois." }

    override val auditDebtRatioTitle: String = "Ratio d'endettement"
    override val auditDebtRatioZero: String = "✅ Endettement (Ratio : 0.00) : Autonomie financière totale — aucune dette, 100% des actifs sont détenus sur fonds propres."
    override val auditDebtRatioConservative: String = "✅ Endettement (Ratio : %1${'$'}s, dette %2${'$'}s des actifs) : Niveau d'endettement prudent et sain. Les engagements sont largement couverts ; le risque de crédit est minime."
    override val auditDebtRatioModerate: String = "ℹ️ Endettement (Ratio : %1${'$'}s, dette %2${'$'}s des actifs) : Endettement modéré. Les dettes sont gérables mais exigent de la rigueur lors d'achats importants."
    override val auditDebtRatioHigh: String = "⚠️ Attention : Endettement élevé (Ratio : %1${'$'}s, dette %2${'$'}s des actifs) ! Forte dépendance aux créanciers. Un désendettement actif est préconisé."
    override val auditDebtRatioCritical: String = "🚨 RISQUE CRITIQUE : Surendettement (Ratio : %1${'$'}s, dettes dépassant les actifs de %2${'$'}s) ! Les engagements dépassent la valeur totale des actifs. Restructuration urgente requise."

    override val auditNetWorthTitle: String = "Valeur nette"
    override val auditNetWorthStrong: String = "✅ Valeur nette (Capitaux propres : %1${'$'}s) : Bilan très positif. Le patrimoine global excède nettement le passif, offrant des bases solides."
    override val auditNetWorthBalanced: String = "ℹ️ Valeur nette (Capitaux propres : %1${'$'}s) : La valeur nette n'est pas négative, mais les passifs représentent plus de la moitié du total des actifs."
    override val auditNetWorthNegative: String = "🚨 Valeur nette (Solde négatif : %1${'$'}s) : Valeur nette négative (situation de déficit). L'ensemble des dettes excède les biens possédés."

    override val auditFidelityTitle: String = "Repère Fidelity"
    override val auditFidelityPositive: String = "✅ Jalon de patrimoine (Fidelity : %1${'$'}s de la cible) : Votre capital dépasse le seuil recommandé pour votre âge %2${'$'}s. Excellente trajectoire."
    override val auditFidelityNeutral: String = "ℹ️ Jalon de patrimoine (Fidelity : %1${'$'}s de la cible) : Le capital correspond aux recommandations d'épargne pour votre âge %2${'$'}s."
    override val auditFidelityNegativeInsolvent: String = "🚨 Jalon de patrimoine (Fidelity) : Le capital net liquide et financier déduction faite des dettes est négatif (%1${'$'}s). Le passif bloque l'atteinte du jalon %2${'$'}s."
    override val auditFidelityNegativeLagging: String = "⚠️ Jalon de patrimoine (Fidelity : %1${'$'}s de la cible) : En retard sur le repère recommandé pour votre âge %2${'$'}s. Accélérer l'épargne est conseillé."

    override val auditCfsTitle: String = "Taux de liberté financière"
    override val auditCfsIndependence: (String, String) -> String = { cfs, independence -> "🎉 Liberté financière (CFS : $cfs) : Le patrimoine net, immobilier compris, dépasse $independence années de dépenses." }
    override val auditCfsStability: (String, String, String) -> String = { cfs, stability, independence -> "✅ Liberté financière (CFS : $cfs) : Le patrimoine net dépasse $stability années de dépenses ; prochaine étape vers le seuil de $independence ans." }
    override val auditCfsGrowth: (String, String) -> String = { cfs, stability -> "✅ Liberté financière (CFS : $cfs) : Le patrimoine couvre plusieurs années de dépenses. Prochain cap : CFS > $stability." }
    override val auditCfsInitial: String = "ℹ️ Liberté financière (CFS : %1${'$'}s) : Phase initiale de constitution du capital (%2${'$'}s). L'effort doit porter sur la régularité de l'épargne."
    override val auditCfsNegative: String = "🚨 Liberté financière (CFS : %1${'$'}s) : Ratio négatif (%2${'$'}s). Les dettes dépassent l'ensemble des actifs, y compris l'immobilier."
    override val auditCfsBuffer: (String, String) -> String = { cfs, buffer -> "📈 Liberté financière (CFS : $cfs) : Le patrimoine net couvre au maximum $buffer années de dépenses." }
    override val auditCfsPathToIndependence: String = "Vous êtes sur la voie de l'indépendance financière, mais encore à distance du but."
    override val auditCfsCloseToIndependence: (percent: Int) -> String = { percent -> "Vous êtes tout près : votre capital couvre $percent% de l'objectif. Le patrimoine net inclut l'immobilier ; l'onglet FIRE évalue la possibilité de vivre des revenus du capital." }
    override val auditCfsFinanciallyIndependent: String = "Vous avez atteint l'indépendance financière : le CFS est d'au moins 30. Le patrimoine net inclut l'immobilier ; l'onglet FIRE évalue la possibilité de vivre des revenus du capital."

    override val auditRealReturnTitle: String = "Rendement réel du capital"
    override val auditRealReturnPositive: String = "✅ Rendement réel du capital (+%1${'$'}s) : Le rendement devance l'inflation (%2${'$'}s contre %3${'$'}s). Le capital est protégé de l'érosion et gagne en pouvoir d'achat."
    override val auditRealReturnNeutral: String = "ℹ️ Rendement réel du capital (0.0%) : Le rendement compense exactement l'inflation. Le capital conserve sa valeur nominale mais ne progresse pas réellement."
    override val auditRealReturnNegative: String = "⚠️ Rendement réel du capital (%1${'$'}s) : L'inflation (%2${'$'}s) dépasse le rendement du capital (%3${'$'}s). Érosion invisible de votre épargne."

    // Export Summary
    override val exportHeaderTitle: String = "📊 RAPPORT FINANCIER — FINANCES PERSONNELLES"
    override val exportSection1Title: String = "1. Données d'entrée :"
    override val exportIncomeLine: String = "• Revenus : %1${'$'}s/mois"
    override val exportExpensesLine: String = "• Dépenses : %1${'$'}s/mois"
    override val exportLiquidAssetsLine: String = "• Actifs liquides : %1${'$'}s"
    override val exportFinancialAssetsLine: String = "• Actifs financiers : %1${'$'}s"
    override val exportPropertyAssetsLine: String = "• Immobilier / Matériel : %1${'$'}s"
    override val exportLiabilitiesLine: String = "• Passif (dettes) : %1${'$'}s"
    override val exportAgeHorizonLine: (age: Int, lifeExpectancy: Int, horizon: Int) -> String = { age, lifeExpectancy, horizon -> "• Âge : $age ans (espérance : $lifeExpectancy ans, horizon : $horizon ans)" }
    override val exportReturnsInflationLine: String = "• Rendement nominal : %1${'$'}s | Inflation : %2${'$'}s"
    override val exportTaxLine: String = "• Impôt sur les plus-values : %1${'$'}s"

    override val exportSection2Title: String = "2. Indicateurs calculés :"
    override val exportCashFlowLine: String = "• Cash-flow : %1${'$'}s/mois"
    override val exportSavingsRateLine: String = "• Taux d'épargne : %1${'$'}s"
    override val exportEmergencyFundLine: String = "• Fonds d'urgence : %1${'$'}s (%2${'$'}s mois)"
    override val exportDebtRatioLine: String = "• Ratio d'endettement : %1${'$'}s"
    override val exportNetWorthLine: String = "• Valeur nette : %1${'$'}s"
    override val exportRealReturnLine: String = "• Rendement réel : %1${'$'}s"
    override val exportEffectiveTaxLine: String = "• Taux effectif de taxation au retrait : %1${'$'}s"

    override val exportSection3Title: String = "3. Synthèse des scores :"
    override val exportScoreTotalLine: String = "• Score global : %1${'$'}s sur 10"
    override val exportScoreSavingsRateLine: String = "• Taux d'épargne : %1${'$'}d / 10"
    override val exportScoreSafetyMonthsLine: String = "• Coussin de sécurité : %1${'$'}d / 10"
    override val exportScoreDebtRatioLine: String = "• Poids de la dette : %1${'$'}d / 10"
    override val exportScoreCfsLine: String = "• Liberté financière : %1${'$'}d / 10"

    override val exportSection4Title: String = "4. Indépendance Financière, FIRE :"
    override val exportCfsLine: String = "• CFS : %1${'$'}s (%2${'$'}s)"
    override val exportFireTargetCapitalLine: String = "• Capital FIRE cible : %1${'$'}s"
    override val exportFireTimeToRetirementLine: String = "• Temps jusqu'à la retraite : %1${'$'}s ans (à %2${'$'}s)"
    override val exportFidelityLine: String = "• Jalon Fidelity : %1${'$'}s (%2${'$'}sx revenu annuel) — %3${'$'}s de la cible"
}
