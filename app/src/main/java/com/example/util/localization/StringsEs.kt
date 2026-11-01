package com.example.util.localization

val StringsEs: AppStrings = object : AppStrings {
    override val language: AppLanguage = AppLanguage.ES

    override val appName: String = "Finanzas Personales & Auditoría FIRE"
    override val tabInputTitle: String = "Datos & Parámetros"
    override val tabInputShort: String = "Datos"
    override val tabMetricsTitle: String = "Métricas Calculadas"
    override val tabMetricsShort: String = "Métricas"
    override val tabScoresTitle: String = "Puntuación & Auditoría Financiera"
    override val tabScoresShort: String = "Puntuación"
    override val tabFireTitle: String = "Independencia Financiera, Jubilación, FIRE"
    override val tabFireShort: String = "FIRE & Plan"
    override val quickScorePrefix: String = "Puntuación"

    override val block1Title: String = "1. Ingresos & Gastos"
    override val block1Subtitle: String = "Flujos de caja mensuales de entrada y salida"
    override val monthlyIncomeLabel: String = "Ingresos (mensuales)"
    override val monthlyExpensesLabel: String = "Gastos (mensuales)"
    override val incomeBreakdownName: String = "Ingresos"
    override val incomeHintCollapsed: String = "¿Qué incluye la sección \"Ingresos\"? (ayuda)"
    override val incomeHintExpanded: String = "Ocultar desglose de \"Ingresos\""
    override val expensesBreakdownName: String = "Gastos"
    override val expensesHintCollapsed: String = "¿Qué incluye la sección \"Gastos\"? (ayuda)"
    override val expensesHintExpanded: String = "Ocultar desglose de \"Gastos\""
    override val breakdownInstruction: String = "Calcula y suma tus partidas de la lista a continuación e introduce el total resultante en el campo superior."
    override val breakdownInstructionIcon: String = "💡 "
    override val breakdownCategoryTitleFormat: (title: String) -> String = { title -> "• $title" }
    override val breakdownCategoryItemFormat: (item: String) -> String = { item -> "    – $item" }
    override val incomeCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Empleo & Salario",
            items = listOf(
                "Empleo principal (sueldo base, primas, horas extras)",
                "Segundo empleo, colaboraciones, trabajo a tiempo parcial"
            )
        ),
        BreakdownCategory(
            title = "Negocios & Actividad autónoma",
            items = listOf(
                "Beneficio neto de actividad por cuenta propia / empresa",
                "Freelance, consultoría, encargos puntuales"
            )
        ),
        BreakdownCategory(
            title = "Ingresos pasivos & Rendimientos",
            items = listOf(
                "Intereses de depósitos bancarios y cuentas remuneradas",
                "Dividendos de acciones y cupones de bonos",
                "Ingresos por alquiler de inmuebles o vehículos",
                "Royalties, derechos de autor y patentes"
            )
        ),
        BreakdownCategory(
            title = "Prestaciones sociales & Otros ingresos",
            items = listOf(
                "Pensiones, ayudas estatales, subsidios",
                "Pensiones alimenticias, apoyo familiar recurrente",
                "Devoluciones de impuestos, reembolsos (cashback)"
            )
        )
    )
    override val expensesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Gastos básicos de subsistencia",
            items = listOf(
                "Alimentación y artículos para el hogar",
                "Alquiler o cuota hipotecaria de la vivienda habitual",
                "Suministros del hogar (electricidad, gas, agua, calefacción)",
                "Comunicaciones, internet, telefonía móvil"
            )
        ),
        BreakdownCategory(
            title = "Transporte & Movilidad",
            items = listOf(
                "Transporte público, abonos de cercanías",
                "Combustible, aparcamiento, peajes, lavado",
                "Mantenimiento del coche, seguro del automóvil, revisiones",
                "Taxis, vehículos de transporte con conductor (VTC), carsharing"
            )
        ),
        BreakdownCategory(
            title = "Salud & Cuidado personal",
            items = listOf(
                "Medicamentos, consultas médicas, dentista, seguro de salud",
                "Ropa, calzado, higiene y aseo personal",
                "Deporte, cuota de gimnasio, actividades deportivas"
            )
        ),
        BreakdownCategory(
            title = "Ocio, Familia & Educación",
            items = listOf(
                "Restaurantes, cafeterías, comida a domicilio",
                "Ocio, cine, viajes, vacaciones, aficiones",
                "Formación, cursos, libros, actividades para los hijos",
                "Regalos, donaciones, imprevistos"
            )
        ),
        BreakdownCategory(
            title = "Servicio de la deuda",
            items = listOf(
                "Cuotas mensuales de préstamos personales y tarjetas de crédito"
            )
        )
    )
    override val cashFlowTitle: String = "Flujo de caja (Cash Flow):"
    override val cashFlowFormula: String = "(Ingresos - Gastos)"
    override val savingsRateTitle: String = "Tasa de ahorro:"
    override val savingsRateFormula: String = "(Cash Flow / Ingresos) * 100%"

    override val block2Title: String = "2. Patrimonio (Σ)"
    override val block2Subtitle: String = "Patrimonio líquido, financiero e inmobiliario"
    override val liquidAssetsLabel: String = "Líquido (efectivo, cuentas corrientes, depósitos a la vista)"
    override val financialAssetsLabel: String = "Financiero (acciones, bonos, ETF, fondos, cuenta de valores)"
    override val propertyAssetsLabel: String = "Inmobiliario / Físico (inmuebles, vehículos, bienes de valor)"
    override val assetsBreakdownName: String = "Patrimonio"
    override val assetsHintCollapsed: String = "¿Qué incluye el \"Patrimonio\"? (ayuda)"
    override val assetsHintExpanded: String = "Ocultar desglose de \"Patrimonio\""
    override val assetsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Activos líquidos",
            items = listOf(
                "Efectivo disponible en cualquier moneda",
                "Saldos en cuentas corrientes y de débito",
                "Cuentas de ahorro de alta remuneración y fondos monetarios",
                "Depósitos a corto plazo (hasta 1–3 meses)"
            )
        ),
        BreakdownCategory(
            title = "Activos financieros (inversiones)",
            items = listOf(
                "Acciones cotizadas, ETF, fondos de inversión",
                "Bonos y deuda pública (letras, obligaciones del Estado)",
                "Cuentas de valores y planes de inversión",
                "Depósitos a plazo fijo de larga duración",
                "Metales preciosos (lingotes, monedas de inversión)",
                "Criptomonedas y activos digitales",
                "Planes de pensiones y seguros de ahorro"
            )
        ),
        BreakdownCategory(
            title = "Bienes físicos (patrimonio material)",
            items = listOf(
                "Inmuebles residenciales (vivienda habitual, segundas residencias, parcelas)",
                "Locales comerciales, plazas de garaje, trasteros",
                "Vehículos (coches, motos, embarcaciones)",
                "Maquinaria, equipamiento, antigüedades, joyas",
                "Participaciones en empresas operativas no cotizadas"
            )
        )
    )
    override val totalAssetsTitle: String = "Patrimonio total (Σ):"
    override val totalAssetsFormula: String = "(Líquido + Financiero + Inmobiliario)"
    override val toggleEmergencyFundTitle: String = "Incluir activos financieros en el fondo de emergencia"
    override val toggleEmergencyFundSubOn: String = "Fondo = Líquido + Financiero"
    override val toggleEmergencyFundSubOff: String = "Fondo = Solo líquido"

    override val block3Title: String = "3. Pasivo & Deudas"
    override val block3Subtitle: String = "Hipotecas, préstamos, tarjetas y obligaciones"
    override val liabilitiesLabel: String = "Pasivo (todas las deudas y obligaciones)"
    override val liabilitiesBreakdownName: String = "Pasivo"
    override val liabilitiesHintCollapsed: String = "¿Qué incluye el \"Pasivo\"? (ayuda)"
    override val liabilitiesHintExpanded: String = "Ocultar desglose de \"Pasivo\""
    override val liabilitiesCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Deudas garantizadas & a largo plazo",
            items = listOf(
                "Capital pendiente de la hipoteca de la vivienda",
                "Financiación del vehículo (saldo restante)",
                "Préstamos con garantía hipotecaria o préstamos para reformas"
            )
        ),
        BreakdownCategory(
            title = "Préstamos personales & a corto plazo",
            items = listOf(
                "Préstamos personales al consumo",
                "Saldos pendientes en tarjetas de crédito y líneas de crédito",
                "Financiaciones en cuotas (BNPL), microcréditos"
            )
        ),
        BreakdownCategory(
            title = "Deudas privadas",
            items = listOf(
                "Cantidades adeudadas a familiares, parientes o amigos",
                "Pagarés y contratos privados de préstamo"
            )
        ),
        BreakdownCategory(
            title = "Otras obligaciones financieras",
            items = listOf(
                "Deudas tributarias, sanciones, reclamaciones judiciales",
                "Recibos o gastos de comunidad pendientes"
            )
        )
    )
    override val netWorthTitle: String = "Patrimonio neto:"
    override val netWorthFormula: String = "(Activos - Pasivo)"

    override val block4Title: String = "4. Inversiones, Inflación & Impuestos"
    override val block4Subtitle: String = "Parámetros de rentabilidad, poder adquisitivo e impuestos sobre plusvalías"
    override val nominalReturnLabel: String = "Rentabilidad nominal %"
    override val inflationLabel: String = "Inflación %"
    override val capitalGainsTaxLabel: String = "Impuesto sobre plusvalías (%)"
    override val capitalGainsTaxBreakdownName: String = "Impuesto sobre plusvalías"
    override val capitalGainsTaxHintCollapsed: String = "¿Cómo se calcula el impuesto sobre plusvalías? (ayuda)"
    override val capitalGainsTaxHintExpanded: String = "Ocultar detalles de fiscalidad de plusvalías"
    override val capitalGainsTaxEffectiveFormat: (tax: String, eff: String) -> String = { tax, eff -> "Tipo nominal: $tax | Tipo efectivo en la retirada: $eff" }
    override val capitalGainsTaxFormula: String = "Tipo nominal * Proporción de ganancia * Cuota de activos financieros"
    override val capitalGainsTaxCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Principio & Base liquidable",
            items = listOf(
                "El impuesto no grava el importe total del reembolso, sino únicamente la ganancia patrimonial neta entre el precio de venta y el coste de adquisición (compra, comisiones).",
                "El tipo se aplica sobre los beneficios obtenidos de la venta de valores (diferencia entre valor de venta y de compra)."
            )
        ),
        BreakdownCategory(
            title = "Modelo realista de desinversión",
            items = listOf(
                "Al retirar capital, una parte corresponde a la devolución del capital invertido originalmente y otra parte a las plusvalías generadas. Solo las plusvalías tributan.",
                "Las proporciones de capital propio y ganancia se calculan dinámicamente según el horizonte de inversión, el interés compuesto, la distribución de activos y los gastos previstos en la jubilación.",
                "Ejemplo: Con un tipo nominal del 23% y una proporción de ganancia del 60% en la retirada, el tipo efectivo = 23% × 60% = 13,8%."
            )
        ),
        BreakdownCategory(
            title = "Impacto en el horizonte FIRE y la jubilación",
            items = listOf(
                "Amplía el plazo necesario y aumenta el capital objetivo si se deben reembolsar acciones y bonos para cubrir el coste de vida.",
                "El valor predeterminado coincide con el promedio de las economías desarrolladas (20–25%)."
            )
        )
    )
    override val investmentsBreakdownName: String = "Inversiones & Inflación"
    override val investmentsHintCollapsed: String = "¿Qué considerar en \"Inversiones & Inflación\"? (ayuda)"
    override val investmentsHintExpanded: String = "Ocultar ayuda de inversiones e inflación"
    override val investmentsCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Rentabilidad nominal anual (% anual)",
            items = listOf(
                "Rendimiento medio ponderado esperado de tu cartera global de inversión",
                "Intereses devengados por depósitos bancarios y cuentas remuneradas",
                "Cupones de bonos y dividendos estimados de acciones",
                "Rendimiento neto por alquiler de inmuebles o arrendamientos",
                "Referencia en mercados desarrollados: 6–9% anual",
                "Referencia en mercados emergentes: 10–16% anual en divisa local"
            )
        ),
        BreakdownCategory(
            title = "Inflación anual esperada (% anual)",
            items = listOf(
                "Objetivos oficiales de los bancos centrales y previsiones macroeconómicas",
                "Inflación personal calculada sobre tu propia cesta de consumo",
                "Promedio histórico registrado en los últimos 5–10 años",
                "Referencia en economías avanzadas: en torno al 2–3% anual",
                "Referencia en mercados emergentes: habitualmente 5–9% anual"
            )
        ),
        BreakdownCategory(
            title = "Rentabilidad real del capital (Ecuación de Fisher)",
            items = listOf(
                "Fórmula: ((1 + Rentabilidad nominal) / (1 + Inflación) - 1) * 100%",
                "Crecimiento neto del poder adquisitivo descontando el efecto de la inflación",
                "Rendimiento real típico: 3–5% anual para economías avanzadas",
                "Una tasa positiva (> 0%) protege el patrimonio de la devaluación y genera riqueza real"
            )
        )
    )
    override val realReturnTitle: String = "Rentabilidad real del capital:"
    override val realReturnFormula: String = "(1 + Rentabilidad nominal) / (1 + Inflación) - 1"

    override val block5Title: String = "5. Edad & Horizonte de planificación"
    override val block5Subtitle: String = "Parámetros para la planificación de la jubilación"
    override val birthDateLabel: String = "Fecha de nacimiento (AAAA-MM-DD)"
    override val invalidBirthDateError: String = "Introduce una fecha de nacimiento válida"
    override val lifeExpectancyLabel: String = "Esperanza de vida (años)"
    override val fireHorizonBreakdownName: String = "Edad & Horizonte FIRE"
    override val fireHorizonHintCollapsed: String = "¿Cómo definir la edad y la esperanza de vida? (ayuda)"
    override val fireHorizonHintExpanded: String = "Ocultar ayuda sobre edad y horizonte"
    override val fireHorizonCategories: List<BreakdownCategory> = listOf(
        BreakdownCategory(
            title = "Fecha de nacimiento y edad actual",
            items = listOf(
                "Sirve para calcular los hitos de capital acumulado por edad (Fidelity)",
                "Define el plazo temporal restante para el efecto del interés compuesto",
                "Orienta la asignación idónea de activos entre crecimiento y preservación"
            )
        ),
        BreakdownCategory(
            title = "Esperanza de vida objetivo (Años)",
            items = listOf(
                "La edad máxima contemplada por tu modelo financiero de jubilación",
                "Valores demográficos de referencia: 80–84 para hombres, 85–89 para mujeres",
                "Por prudencia y seguridad financiera, se recomienda fijar 85–95 años",
                "Un margen conservador asegura que el capital no se agote anticipadamente"
            )
        ),
        BreakdownCategory(
            title = "Horizonte de planificación (Años)",
            items = listOf(
                "Fórmula: Esperanza de vida menos Edad actual",
                "El periodo completo durante el cual tu patrimonio debe sostener tus gastos",
                "Parámetro matemático esencial para calcular el capital FIRE objetivo"
            )
        )
    )
    override val horizonTitle: String = "Horizonte de planificación:"
    override val horizonFormula: String = "(Esperanza de vida - Edad actual)"
    override val horizonValueFormat: (horizon: Int, age: Int) -> String = { horizon, age -> "$horizon años (edad actual: $age años)" }
    override val includePropertyInFireLabel: String = "Incluir inmuebles y deudas en el cálculo FIRE"
    override val includePropertyInFireSubOn: String = "Se utiliza el patrimonio neto, incluidos los inmuebles descontadas las deudas"
    override val includePropertyInFireSubOff: String = "Solo se utilizan los activos invertibles (líquido + financiero)"

    override val calculateButtonText: String = "Calcular métricas"
    override val exportButtonText: String = "Exportar resultados"
    override val exportChooserTitle: String = "Exportar resultados"

    override val metricsHeaderTitle: String = "Métricas Calculadas"
    override val metricsHeaderSubtitle: String = "Indicadores financieros clave con fórmulas & valoración"
    override val categoryAll: String = "Todas"
    override val categoryCashFlow: String = "Flujo de caja"
    override val categoryCapitalRisks: String = "Patrimonio & Riesgos"
    override val categoryBenchmarks: String = "Referencias"
    override val metricCardInfo: String = "Info"
    override val metricCardHide: String = "Ocultar"
    override val metricCardFormulaPrefix: String = "Fórmula: "
    override val metricCardExpand: String = "Desplegar"
    override val metricCardCollapse: String = "Plegar"

    override val metricCashFlowTitle: String = "Flujo de caja (Cash Flow)"
    override val metricCashFlowFormula: String = "(Ingresos - Gastos)"
    override val metricCashFlowDesc: String = "Cash Flow. Saldo neto de entradas y salidas de dinero por periodo.\n\n• Valor positivo (> 0): Ganas más de lo que gastas.\n• Cero o negativo (≤ 0): Los ingresos apenas cubren o son inferiores a los gastos."
    override val metricCashFlowPositiveSub: String = "Superávit presupuestario positivo"
    override val metricCashFlowNeutralSub: String = "Ingresos iguales a los gastos"
    override val metricCashFlowNegativeSub: String = "Déficit presupuestario"

    override val metricSavingsRateTitle: String = "Tasa de ahorro"
    override val metricSavingsRateFormula: String = "(Cash Flow / Ingresos) * 100%"
    override val metricSavingsRateDesc: (String, String) -> String = { neutral, positive -> "Se recomienda aspirar a una tasa de ahorro superior al ${positive}% de los ingresos del hogar.\n\n• Menor o igual a ${neutral}%: Negativo; de ${neutral}% a ${positive}%: Neutro; superior a ${positive}%: Positivo." }
    override val metricSavingsRateSub: (String, String) -> String = { neutral, positive -> "Menor o igual a ${neutral}%: Negativo; de ${neutral}% a ${positive}%: Neutro; superior a ${positive}%: Positivo." }

    override val metricEmergencyFundTitle: String = "Fondo de emergencia"
    override val metricEmergencyFundFormula: String = "(Activos líquidos)"
    override val metricEmergencyFundDesc: String = "Colchón de seguridad para afrontar imprevistos (enfermedad, desempleo, averías urgentes) sin recurrir a endeudamiento costoso."
    override val metricEmergencyFundSubLiquidPlusFin: String = "Líquido + Financiero"
    override val metricEmergencyFundSubOnlyLiquid: String = "Solo líquido"

    override val metricSafetyMonthsTitle: String = "Colchón de seguridad (Meses)"
    override val metricSafetyMonthsFormula: String = "(Fondo de emergencia / Gastos mensuales)"
    override val metricSafetyMonthsDesc: (String, String, String) -> String = { neutral, positive, excellent -> "Número de meses de gastos corrientes que puedes cubrir recurriendo únicamente a tu fondo de emergencia.\n\n• Objetivo mínimo: ${neutral}–${positive} meses de gastos básicos (vivienda, comida, suministros). Protege en periodos de cambio.\n• Objetivo ideal: Hasta ${excellent} meses para la máxima solvencia, en especial para autónomos o familias." }
    override val metricSafetyMonthsSub: (String, String, String) -> String = { neutral, positive, excellent -> "Norma: ${neutral}–${positive} m, Ideal: ${excellent}+ m" }
    override val monthsSuffix: String = "meses"

    override val metricDebtRatioTitle: String = "Ratio de endeudamiento (Debt-to-Asset)"
    override val metricDebtRatioFormula: String = "(Pasivo / Activos)"
    override val metricDebtRatioDesc: (String, String, String, String) -> String = { zero, positive, neutral, critical -> "Mide la proporción del patrimonio financiada con deuda, reflejando el nivel de riesgo financiero.\n\n• ${zero} – ${positive}: Excelente solvencia, patrimonio financiado casi en su totalidad con fondos propios\n• ${positive} – ${neutral}: Nivel de endeudamiento equilibrado para un hogar\n• Superior a ${neutral}: Riesgo elevado, gran dependencia de acreedores\n• Superior a ${critical}: Las deudas superan el valor del patrimonio (riesgo de insolvencia)" }
    override val metricDebtRatioLowSub: String = "Riesgo bajo (excelente)"
    override val metricDebtRatioHighSub: String = "Riesgo de deuda elevado"

    override val metricNetWorthTitle: String = "Patrimonio neto"
    override val metricNetWorthFormula: String = "(Activos - Pasivo)"
    override val metricNetWorthDesc: String = "Valor total de los bienes menos el total de las deudas. Representa lo que realmente te pertenece una vez canceladas todas las obligaciones."
    override val metricNetWorthSubPrefix: String = "Patrimonio total: "
    override val metricNetWorthNegativeSub: String = "Patrimonio neto negativo (¡déficit!)"
    override val metricNetWorthAssetsFormat: (prefix: String, amount: String) -> String = { prefix, amount -> "$prefix: $amount" }
    override val metricNetWorthNegativeAssetsFormat: (prefix: String, amount: String, negative: String) -> String = { prefix, amount, negative -> "$prefix: $amount ($negative)" }

    override val metricFidelityTitle: String = "Referencia: Hitos de capital Fidelity"
    override val metricFidelityFormula: String = "Ingresos anuales * Multiplicador"
    override val metricFidelityDesc: String = "Hitos Fidelity: a los 30 años — 1x, 40 — 3x, 50 — 6x, 60 — 8x y 67 — 10x los ingresos anuales. La app interpola linealmente y aplica 0,5x antes de los 25 años. Se contabilizan los activos líquidos y financieros deduciendo las deudas no cubiertas por inmuebles; los bienes inmobiliarios quedan excluidos.\n\nEjemplo: a los 40 años con 100.000 de ingresos anuales, el objetivo es 300.000.\n\nNota: Esta aplicación no tiene vinculación con Fidelity Investments y emplea estos hitos con fines puramente divulgativos."
    override val metricFidelitySubFormat: (age: Int, mult: String) -> String = { age, mult -> "Edad: $age años | Multiplicador: ${mult}x" }
    override val metricFidelityProgress: String = "Progreso hacia el objetivo Fidelity:"
    override val metricFidelityCapitalFormat: (capital: String) -> String = { capital -> "Capital considerado: $capital" }

    override val metricCfsTitle: String = "Tasa de libertad financiera (CFS)"
    override val metricCfsFormula: String = "(Patrimonio neto incluidos inmuebles / Gastos anuales)"
    override val metricCfsDesc: (String, String, String, String, String, String) -> String = { debt, buffer, neutral, positive, stability, independence -> "Patrimonio neto = todos los activos, incluidos inmuebles, menos todas las deudas. Esta métrica no significa que puedas vivir de las rentas; la pestaña FIRE analiza esa viabilidad excluyendo los inmuebles.\n\nMide cuántos años de gastos corrientes están cubiertos por tu patrimonio neto.\n\n• CFS < ${debt} — Estás en deuda neta\n• CFS ${debt}..${buffer} — Colchón de emergencia mínimo\n• CFS ${buffer}..${neutral} — Etapa inicial de acumulación de capital\n• CFS ${neutral}..${positive} — Etapa de inversión a largo plazo\n• CFS ${positive}..${stability} — Los rendimientos generan ingresos tangibles\n• CFS >= ${stability} y < ${independence} — Firme estabilidad financiera\n• CFS >= ${independence} — Independencia financiera completa alcanzada" }

    override val metricRealReturnTitle: String = "Rentabilidad real del capital, %"
    override val metricRealReturnFormula: String = "(1 + Rentabilidad nominal) / (1 + Inflación) - 1"
    override val metricRealReturnDesc: String = "Crecimiento real del poder adquisitivo tras deducir la inflación del rendimiento nominal. Refleja la creación efectiva de riqueza."
    override val metricRealReturnSubFormat: (nom: String, infl: String) -> String = { nom, infl -> "Nominal: $nom | Inflación: $infl" }
    override val metricUnavailableValue: String = "—"

    override val scoresHeaderTitle: String = "Resumen de Puntuación"
    override val scoresHeaderSubtitle: String = "Puntuación global de auditoría & detalle"
    override val scoresShareReportChooser: String = "Compartir informe"
    override val scoresGaugeOutOf: (max: Int) -> String = { max -> "de $max" }
    override val scoresTotalResult: (score: String) -> String = { score -> "Resultado global: $score de 10" }
    override val scoresStatusExcellent: String = "Excelente solidez financiera y óptimas condiciones para la acumulación de patrimonio a largo plazo."
    override val scoresStatusGood: String = "Buena salud financiera con margen prometedor para incrementar la tasa de ahorro."
    override val scoresStatusModerate: String = "Estabilidad moderada. Se aconseja consolidar el fondo de emergencia."
    override val scoresStatusAttention: String = "Optimización de gastos y amortización prioritaria de deudas muy recomendadas."
    override val scoresBreakdownTitle: String = "Desglose de puntuación por componentes:"
    override val scoreValuePrefix: String = "Valor: "
    override val scoreOutOfTen: (score: Int) -> String = { score -> "$score de 10" }
    override val scoreSlashOutOfTen: (score: String) -> String = { score -> "$score / 10" }
    override val savingsRateScoreComment: (rate: Double, sr: String, positiveThreshold: String) -> String = { rate, sr, positiveThreshold ->
        when {
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_POSITIVE_THRESHOLD -> "Referencia superior al ${positiveThreshold}%. Mantienes una tasa excelente ($sr)."
            rate > com.example.domain.FinancialMetricEvaluator.SAVINGS_RATE_NEUTRAL_THRESHOLD -> "Referencia superior al ${positiveThreshold}%. Hasta ${positiveThreshold}% inclusive, el valor es neutro ($sr)."
            else -> "Referencia superior al ${positiveThreshold}%. Un valor nulo o negativo ($sr) es desfavorable: los gastos igualan o superan a los ingresos."
        }
    }
    override val safetyMonthsScoreComment: (months: Double, monthsText: String, neutral: String, positive: String, excellent: String) -> String = { months, monthsText, neutral, positive, excellent ->
        when {
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_TIER_EXCELLENT -> "Mínimo ${neutral}–${positive} meses, ideal ${excellent}+ meses. Resultado excelente: el colchón supera ${excellent} meses ($monthsText)."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_POSITIVE_THRESHOLD -> "Mínimo ${neutral}–${positive} meses, ideal ${excellent}+ meses. Buen colchón ($monthsText), objetivo de ${positive} meses alcanzado."
            months >= com.example.domain.FinancialMetricEvaluator.SAFETY_MONTHS_NEUTRAL_THRESHOLD -> "Mínimo ${neutral}–${positive} meses, ideal ${excellent}+ meses. Colchón básico ($monthsText), se recomienda ampliar a ${positive}–${excellent} meses."
            else -> "Mínimo ${neutral}–${positive} meses, ideal ${excellent}+ meses. Colchón insuficiente ($monthsText), riesgo financiero elevado."
        }
    }
    override val debtComment0: (String, String) -> String = { zero, positive -> "$zero – $positive: Excelente solidez, patrimonio financiado casi en su totalidad con fondos propios." }
    override val debtComment1: (String, String) -> String = { positive, neutral -> "$positive – $neutral: Endeudamiento equilibrado, compromisos financieros controlados." }
    override val debtComment2: (String, String) -> String = { neutral, critical -> "$neutral – $critical: Riesgo elevado, dependencia importante de la deuda." }
    override val debtComment3: (String) -> String = { critical -> "Superior a $critical: Riesgo crítico, las deudas totales superan el valor del patrimonio." }

    override val auditorOpinionTitle: String = "Dictamen del Auditor"
    override val auditorOpinionSubtitle: (count: Int) -> String = { count -> "Análisis de los $count indicadores de la pantalla de Métricas" }
    override val shareAuditorOpinionChooser: String = "Compartir dictamen del auditor"
    override val positiveBadgeText: (count: Int) -> String = { count -> "🟢 $count positivos" }
    override val neutralBadgeText: (count: Int) -> String = { count -> "🟡 $count neutros" }
    override val negativeBadgeText: (count: Int) -> String = { count -> "🔴 $count negativos" }
    override val positiveSectionTitle: (count: Int) -> String = { count -> "Indicadores positivos ($count)" }
    override val neutralSectionTitle: (count: Int) -> String = { count -> "Indicadores neutros ($count)" }
    override val negativeSectionTitle: (count: Int) -> String = { count -> "Indicadores negativos ($count)" }
    override val positiveEmptyText: String = "No se identificaron factores positivos por ahora. Se recomienda optimizar ingresos y gastos."
    override val neutralEmptyText: String = "No hay indicadores neutros — todas las métricas están en zona positiva o crítica."
    override val negativeEmptyText: String = "🎉 ¡No se detectaron factores de riesgo críticos — todos los indicadores están en zona segura!"

    override val fireHeroTitle: String = "Independencia Financiera, FIRE"
    override val fireHeroSubtitle: String = "Horizonte de jubilación y proyección de capital"
    override val fireTimeToRetire: String = "Tiempo hasta la jubilación:"
    override val fireYearsSuffix: String = " años"
    override val fireHeroAssumption: String = "El cálculo FIRE considera activos líquidos y financieros; los inmuebles no se incluyen y las deudas solo se restan en la parte que supere el valor de los bienes físicos"
    override val fireRetirementAgeLabel: String = "Edad de jubilación:"
    override val fireRetirementAgeValue: (age: String) -> String = { age -> "$age años" }
    override val fireTargetCapitalLabel: String = "Capital objetivo necesario:"
    override val fireParamsTitle: String = "Parámetros de Edad & Horizonte"
    override val fireBirthDateLabel: String = "Fecha de nacimiento:"
    override val fireCurrentAgeLabel: String = "Edad actual:"
    override val fireCurrentAgeValue: (age: Int) -> String = { age -> "$age años" }
    override val fireLifeExpectancyLabel: String = "Esperanza de vida:"
    override val fireLifeExpectancyValue: (exp: Int) -> String = { exp -> "$exp años" }
    override val fireHorizonLabel: String = "(Esperanza de vida - Edad actual):"
    override val fireHorizonValue: (h: Int) -> String = { h -> "$h años" }
    override val fireRealReturnLabel: String = "Rentabilidad real del capital:"
    override val fireTrajectoryTitle: String = "Trayectoria de acumulación de capital"
    override val fireTrajectorySubtitle: String = "Crecimiento patrimonial hacia el punto FIRE y provisión de la renta vitalicia"
    override val fireChartAccumulated: String = "Capital acumulado"
    override val fireChartRequired: String = "Capital necesario"
    override val fireChartYearsSuffix: String = " a."
    override val fireChartFirePoint: (age: String) -> String = { age -> "FIRE: $age a." }
    override val fireSimSummaryTitle: String = "Resumen de Simulación (Efecto acumulado)"
    override val fireSimEarlier: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⚡ ¡Jubilación alcanzada $diff años antes (en $target años, a la edad de $age años)!" }
    override val fireSimLater: (diff: String, target: String, age: String) -> String = { diff, target, age -> "⏳ Plazo para la jubilación aumentado en $diff años (en $target años, a la edad de $age años)." }
    override val fireSimSame: (target: String) -> String = { target -> "El horizonte de jubilación permanece sin cambios (en $target años)." }
    override val fireSimWithoutBase: (target: String, age: String) -> String = { target, age -> "Jubilación alcanzable en $target años (a la edad de $age años)." }
    override val fireUnreachableText: String = "Inalcanzable con los parámetros actuales"
    override val fireHorizonExhaustedText: String = "Horizonte de planificación agotado"
    override val fireSimCashFlowRow: String = "Flujo de caja (ahorro):"
    override val fireSimExpensesRow: String = "Gastos mensuales:"
    override val fireSimNetWorthRow: String = "Patrimonio neto (activos − deudas):"
    override val fireSimPropertyRow: String = "Aumento de valor físico (al liquidar préstamo):"
    override val fireSimSliderTitle: String = "Simulador \"¿Qué pasaría si...?\""
    override val fireSimReset: String = "Restablecer"
    override val fireSimFineTuning: String = "Ajuste fino (deslizadores):"
    override val fireSliderSavings: String = "Ahorro mensual:"
    override val fireSliderExpenses: String = "Gastos mensuales:"
    override val fireSliderReturn: String = "Rentabilidad real del capital:"
    override val fireSliderBaseSuffix: String = " sobre la base"
    override val fireSliderBaseReturnSuffix: String = "% sobre la base"
    override val fireSliderFrom: (v: String) -> String = { v -> "desde $v" }
    override val fireSliderStep: (v: String) -> String = { v -> "paso $v" }
    override val fireSliderTo: (v: String) -> String = { v -> "hasta +${v.trim().trimStart('+')}" }
    override val fireSliderInc: String = "aumento"
    override val fireSliderDec: String = "reducc."

    override val scenarioHeaderTitle: String = "Panel de Escenarios (Pruebas de estrés)"
    override val scenarioHeaderSubtitle: String = "El efecto conjunto de deslizadores y opciones se acumula y calcula conjuntamente, reflejándose en las cifras y en el gráfico:"
    override val scenarioTotalBadge: (count: Int) -> String = { count -> "Total: $count" }
    override val scenarioPositiveTitle: String = "Eventos favorables"
    override val scenarioPositiveSectionNumber: String = "2.1"
    override val scenarioPositiveActive: (count: Int) -> String = { count -> "Activos: $count" }
    override val scenarioPositiveSubtitle: String = "Acontecimientos que aceleran la acumulación de capital y la jubilación:"
    override val scenarioNegativeTitle: String = "Eventos desfavorables"
    override val scenarioNegativeSectionNumber: String = "2.2"
    override val scenarioNegativeActive: (count: Int) -> String = { count -> "Activos: $count" }
    override val scenarioNegativeSubtitle: String = "Pruebas de estrés, imprevistos y situaciones de crisis:"
    override val scenarioParamsPrefix: String = "Parámetros: "

    override val scenarioSalaryIncreaseTitle: String = "Aumento salarial"
    override val scenarioSalaryIncreaseParam: (String) -> String = { percent -> "ingresos +${percent}% al mes, permanente" }
    override val scenarioSalaryIncreaseDesc: (amt: String) -> String = { amt -> "Subida de ingresos: +$amt/mes" }

    override val scenarioMarketGrowthTitle: (String) -> String = { percent -> "Crecimiento del mercado +${percent}%" }
    override val scenarioMarketGrowthParam: (String) -> String = { percent -> "ETF +${percent}%" }
    override val scenarioMarketGrowthDesc: (amt: String) -> String = { amt -> "Revalorización de cartera: +$amt" }

    override val scenarioExpenseOptTitle: String = "Optimización de gastos"
    override val scenarioExpenseOptParam: (String) -> String = { percent -> "−${percent}% en gastos mensuales, permanente" }
    override val scenarioExpenseOptDesc: (amt: String) -> String = { amt -> "Ahorro recurrente: -$amt/mes" }

    override val scenarioRelocationTitle: String = "Mudanza a una región más económica"
    override val scenarioRelocationParam: (String) -> String = { percent -> "−${percent}% en gastos mensuales, permanente" }
    override val scenarioRelocationDesc: (amt: String) -> String = { amt -> "Reducción del coste de vida: -$amt/mes" }

    override val scenarioInheritanceTitle: String = "Herencia o donación monetaria"
    override val scenarioInheritanceParam: (String) -> String = { percent -> "aportación puntual del ${percent}% del ingreso mensual" }
    override val scenarioInheritanceDesc: (amt: String) -> String = { amt -> "Entrada de capital: +$amt (en activos líquidos)" }

    override val scenarioPropertySaleTitle: String = "Venta de bienes (coche, inmueble)"
    override val scenarioPropertySaleParam: (String) -> String = { percent -> "ingreso puntual del ${percent}% del ingreso mensual" }
    override val scenarioPropertySaleDesc: (amt: String) -> String = { amt -> "Ingreso por venta: +$amt (en activos líquidos)" }

    override val scenarioChildrenSelfSufficientTitle: String = "Independencia económica de los hijos"
    override val scenarioChildrenSelfSufficientParam: (String, String) -> String = { percent, years -> "gastos −${percent}% en $years años (gastos de hijos ya incluidos)" }
    override val scenarioChildrenSelfSufficientDesc: (amt: String, years: String) -> String = { amt, years -> "Reducción de gastos: -$amt/mes (en $years años)" }

    override val scenarioJobLossTitle: String = "Pérdida de empleo"
    override val scenarioJobLossParam: (String) -> String = { months -> "pérdida de $months meses de ingresos" }
    override val scenarioJobLossDesc: (String) -> String = { months -> "$months meses de ingresos perdidos, cubiertos con ahorros" }

    override val scenarioMarketCrashTitle: (String) -> String = { percent -> "Desplome de mercado −${percent}%" }
    override val scenarioMarketCrashParam: (String) -> String = { percent -> "ETF −${percent}%" }
    override val scenarioMarketCrashDesc: (percent: String, amt: String) -> String = { percent, amt -> "Caída de cartera de ${percent}% ($amt)" }

    override val scenarioMortgageTitle: String = "Hipoteca de vivienda"
    override val scenarioMortgageParam: (String, String) -> String = { percent, years -> "${percent}% de ingresos durante $years años" }
    override val scenarioMortgageDesc: (pay: String, years: String) -> String = { pay, years -> "Cuota: $pay/mes ($years años)" }

    override val scenarioCarLoanTitle: String = "Préstamo para coche"
    override val scenarioCarLoanParam: (String, String) -> String = { percent, years -> "${percent}% de ingresos durante $years años" }
    override val scenarioCarLoanDesc: (pay: String, years: String) -> String = { pay, years -> "Cuota: $pay/mes ($years años)" }

    override val scenarioLargeExpenseTitle: String = "Gasto extraordinario elevado"
    override val scenarioLargeExpenseParam: (String) -> String = { percent -> "gasto del ${percent}% del ingreso mensual" }
    override val scenarioLargeExpenseDesc: (amt: String) -> String = { amt -> "Salida de $amt (cubierta con liquidez y fondos de inversión)" }

    override val scenarioInflationTitle: String = "Subida de costes (Inflación)"
    override val scenarioInflationParam: (String) -> String = { percent -> "+${percent}% en gastos mensuales" }
    override val scenarioInflationDesc: (amt: String) -> String = { amt -> "Mayor coste mensual: +$amt/mes" }

    override val scenarioChildTitle: String = "Nacimiento de un hijo"
    override val scenarioChildParam: (String, String) -> String = { percent, years -> "+${percent}% en gastos durante $years años" }
    override val scenarioChildDesc: (amt: String, years: String) -> String = { amt, years -> "Gastos de crianza: +$amt/mes (durante $years años)" }

    override val disclaimerTitle: String = "Descargo de responsabilidad"
    override val disclaimerText: String = "Importante: Este proyecto es un desarrollo personal facilitado a título informativo «tal cual». El autor no se hace responsable de posibles fallos de funcionamiento, pérdidas de datos u otros perjuicios derivados del uso de este software. Todos los cálculos, fórmulas, métricas y simulaciones tienen una finalidad exclusivamente informativa y no constituyen asesoramiento financiero, de inversión ni fiscal personalizado. El uso se realiza bajo tu propia responsabilidad."
    override val assumptionsDisclaimerTitle: String = "Descargo de responsabilidad"
    override val assumptionsDisclaimerBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> "Los cálculos asumen un rendimiento constante de $returnRate y una inflación de $inflation, sin impuestos ni efectos de la secuencia de rentabilidades. Es un modelo, no una previsión.\n\nEl modelo no contempla impuestos, el orden de rendimientos de mercado ni el crecimiento de los salarios. Es una guía orientativa, no una garantía." }
    override val fireAssumptionsTitle: String = "Supuestos del modelo"
    override val fireAssumptionsBody: (returnRate: String, inflation: String) -> String = { returnRate, inflation -> "Los cálculos asumen una rentabilidad constante de $returnRate y una inflación de $inflation, sin contemplar impuestos, secuencia temporal de rendimientos ni inflación de ingresos. Es una referencia estratégica, no una previsión o garantía." }

    // Pluralization & Formats
    override val moneyFormat: String = "%2${'$'}s\u00A0%1${'$'}s"
    override val countFormat: String = "%1${'$'}s\u00A0%2${'$'}s"
    override val checkboxPluralForms = PluralForms(one = "casilla", other = "casillas")
    override val yearPluralForms = PluralForms(one = "año", other = "años")
    override val monthPluralForms = PluralForms(one = "mes", other = "meses")
    override val pointPluralForms = PluralForms(one = "punto", other = "puntos")
    override val simulationBadgeSlidersSuffix: String = "deslizadores"
    override val simulationBadgeOnlySliders: String = "Deslizadores de ajuste"

    // Score & Safety Months formats
    override val safetyMonthsValueFormat: String = "%1${'$'}s meses"
    override val shareScoreReportTemplate: String = "📊 Informe Financiero & Auditoría de Capital:\n• Puntuación global: %1${'$'}s de 10\n• Tasa de ahorro: %2${'$'}s/10 (%3${'$'}s)\n• Colchón de seguridad: %4${'$'}s/10 (%5${'$'}s meses)\n• Ratio de endeudamiento: %6${'$'}s/10 (%7${'$'}s)\n• Libertad financiera (CFS): %8${'$'}s/10 (%9${'$'}s)\n• Plazo para la jubilación (FIRE): %10${'$'}s años (a los %11${'$'}s)\n"

    // CFS Stage descriptions
    override val cfsStageDebt: (String) -> String = { debt -> "CFS < $debt — Te encuentras en deuda neta" }
    override val cfsStageBuffer: (String, String) -> String = { debt, buffer -> "CFS $debt..$buffer — Cuentas con un colchón de emergencia mínimo" }
    override val cfsStageCapital: (String, String) -> String = { buffer, neutral -> "CFS $buffer..$neutral — Inicio de la creación de capital financiero" }
    override val cfsStageInvesting: (String, String) -> String = { neutral, positive -> "CFS $neutral..$positive — Puedes comenzar a invertir a largo plazo" }
    override val cfsStageReturns: (String, String) -> String = { positive, stability -> "CFS $positive..$stability — Los rendimientos generan ingresos tangibles. La inflación tiene mayor impacto en el capital" }
    override val cfsStageStability: (String, String) -> String = { stability, independence -> "CFS >= $stability y < $independence — Firme estabilidad financiera. ¿Cómo valoras tu tiempo insustituible?" }
    override val cfsStageIndependence: (String) -> String = { independence -> "CFS >= $independence — El patrimonio neto incluidos inmuebles supera $independence años de gastos" }

    // Auditor Opinion Share Text
    override val auditShareHeaderTitle: String = "📋 DICTAMEN DEL AUDITOR SOBRE TODOS LOS INDICADORES"
    override val auditShareScoreLine: String = "⭐️ Puntuación global: %1${'$'}s de 10 puntos"
    override val auditShareTotalCountLine: String = "Total de indicadores analizados: %1${'$'}d"
    override val auditSharePositiveHeader: String = "🟢 INDICADORES POSITIVOS (%1${'$'}d):"
    override val auditSharePositiveEmpty: String = "• No se identificaron indicadores positivos"
    override val auditShareNeutralHeader: String = "🟡 INDICADORES NEUTROS (%1${'$'}d):"
    override val auditShareNeutralEmpty: String = "• No hay indicadores neutros"
    override val auditShareNegativeHeader: String = "🔴 INDICADORES NEGATIVOS (%1${'$'}d):"
    override val auditShareNegativeEmpty: String = "• No se detectaron factores críticos o negativos 🎉"
    override val auditShareGeneratedBy: String = "💡 Generado en la app \"Finanzas Personales\""
    override val auditShareDisclaimer: String = "⚠️ Importante: Este proyecto es un desarrollo personal facilitado a título informativo «tal cual». El autor no responde de fallos ni pérdidas. Todos los cálculos tienen finalidad informativa y no constituyen asesoramiento financiero. Uso bajo tu propia responsabilidad."

    // Auditor Indicators
    override val auditCashFlowTitle: String = "Flujo de caja (Cash Flow)"
    override val auditCashFlowPositive: String = "✅ Cash Flow (+%1${'$'}s/mes): Superávit presupuestario. Tus ingresos superan a tus gastos, creando una capacidad periódica de inversión para hacer crecer el patrimonio."
    override val auditCashFlowNeutral: String = "ℹ️ Cash Flow (0 %1${'$'}s/mes): Presupuesto equilibrado (ingresos iguales a gastos). No generas deudas, pero careces de margen para acumular capital."
    override val auditCashFlowNegative: String = "🚨 Cash Flow (%1${'$'}s/mes): ¡Déficit presupuestario! Los gastos superan los ingresos, consumiendo las reservas o aumentando el endeudamiento."

    override val auditSavingsRateTitle: String = "Tasa de ahorro"
    override val auditSavingsRatePositive: (String, String) -> String = { value, threshold -> "✅ Capacidad de ahorro (Tasa: $value): Tasa de ahorro elevada (superior al ${threshold}%). Asegura una acumulación continua de activos y el efecto del interés compuesto." }
    override val auditSavingsRateNeutral: (String, String, String) -> String = { value, neutral, positive -> "ℹ️ Capacidad de ahorro (Tasa: $value): Rango neutro (entre $neutral y ${positive}%). El capital avanza de forma constante; optimizar gastos puede ayudar a superar el target de >${positive}%." }
    override val auditSavingsRateNegative: String = "🚨 Capacidad de ahorro (Tasa: %1${'$'}s): Cash flow nulo o negativo: los gastos igualan o superan los ingresos, el capital no crece."

    override val auditEmergencyFundTitle: String = "Fondo de emergencia"
    override val auditEmergencyFundPositive: String = "✅ Fondo de emergencia (Volumen: %1${'$'}s): La reserva líquida es plenamente suficiente para solventar contingencias sin endeudarse."
    override val auditEmergencyFundNeutral: (String, String) -> String = { value, months -> "ℹ️ Fondo de emergencia (Volumen: $value): Colchón mínimo de base formado. Se aconseja elevarlo progresivamente hasta $months meses de gastos." }
    override val auditEmergencyFundNegative: String = "⚠️ Fondo de emergencia (Volumen: %1${'$'}s): Reserva críticamente baja. Alta vulnerabilidad ante gastos imprevistos y necesidad de préstamos."

    override val auditSafetyMonthsTitle: String = "Colchón de seguridad (Meses)"
    override val auditSafetyMonthsHigh: (String, String) -> String = { value, months -> "✅ Cojín financiero (Reserva: $value meses): Plenamente consolidado con amplio margen. Los riesgos por pérdida de ingresos están amortiguados. La liquidez por encima de $months meses conviene invertirla." }
    override val auditSafetyMonthsTarget: (String, String) -> String = { value, months -> "✅ Cojín financiero (Reserva: $value meses): Objetivo de $months meses cumplido. La seguridad financiera básica del hogar está garantizada." }
    override val auditSafetyMonthsNeutral: (String, String) -> String = { value, months -> "ℹ️ Cojín financiero (Reserva: $value meses): Colchón básico disponible; se recomienda ampliarlo a $months meses para mayor tranquilidad." }
    override val auditSafetyMonthsNegative: (String, String, String) -> String = { value, neutral, positive -> "⚠️ Cojín financiero (Reserva: $value meses): Muy insuficiente (menos de $neutral meses). Riesgo de caer en espirales de deuda. Prioridad: crear un colchón de ${positive} meses con urgencia." }

    override val auditDebtRatioTitle: String = "Ratio de endeudamiento"
    override val auditDebtRatioZero: String = "✅ Nivel de deuda (Debt Ratio: 0.00): Autonomía financiera total — cero deudas o créditos, el 100% de los activos se posee en propiedad neta."
    override val auditDebtRatioConservative: String = "✅ Nivel de deuda (Debt Ratio: %1${'$'}s, deuda del %2${'$'}s de los activos): Endeudamiento prudente y seguro. Las obligaciones están totalmente respaldadas; el riesgo crediticio es mínimo."
    override val auditDebtRatioModerate: String = "ℹ️ Nivel de deuda (Debt Ratio: %1${'$'}s, deuda del %2${'$'}s de los activos): Nivel de deuda moderado. La deuda está bajo control pero exige disciplina ante gastos relevantes."
    override val auditDebtRatioHigh: String = "⚠️ Advertencia: Endeudamiento elevado (Debt Ratio: %1${'$'}s, deuda del %2${'$'}s de los activos)! Fuerte dependencia de acreedores. Se recomienda reestructurar y amortizar."
    override val auditDebtRatioCritical: String = "🚨 RIESGO CRÍTICO: Déficit patrimonial (Debt Ratio: %1${'$'}s, deudas que superan los activos en un %2${'$'}s)! Las obligaciones superan el valor del patrimonio. Reestructuración urgente necesaria."

    override val auditNetWorthTitle: String = "Patrimonio neto"
    override val auditNetWorthStrong: String = "✅ Patrimonio neto (Fondos propios: %1${'$'}s): Saldo fuertemente positivo. El conjunto de activos supera con holgura el pasivo, ofreciendo bases muy sólidas."
    override val auditNetWorthBalanced: String = "ℹ️ Patrimonio neto (Fondos propios: %1${'$'}s): Activos y pasivos están equilibrados. El saldo es positivo, pero una parte significativa está comprometida con deudas."
    override val auditNetWorthNegative: String = "🚨 Patrimonio neto (Saldo negativo: %1${'$'}s): Patrimonio neto negativo (insolvencia técnica). Las obligaciones totales superan el conjunto de bienes poseídos."

    override val auditFidelityTitle: String = "Referencia Fidelity"
    override val auditFidelityPositive: String = "✅ Hito patrimonial por edad (Fidelity: %1${'$'}s del objetivo): Tu capital supera la referencia aconsejada para tu edad (%2${'$'}s). Trayectoria excelente a largo plazo."
    override val auditFidelityNeutral: String = "ℹ️ Hito patrimonial por edad (Fidelity: %1${'$'}s del objetivo): El capital se alinea con la pauta de ahorro para tu edad (%2${'$'}s)."
    override val auditFidelityNegativeInsolvent: String = "🚨 Hito patrimonial por edad (Fidelity): El capital líquido y financiero tras deudas es negativo (%1${'$'}s). El pasivo impide alcanzar la referencia (%2${'$'}s)."
    override val auditFidelityNegativeLagging: String = "⚠️ Hito patrimonial por edad (Fidelity: %1${'$'}s del objetivo): Por debajo del objetivo de referencia para tu edad (%2${'$'}s). Se aconseja acelerar el ritmo de ahorro."

    override val auditCfsTitle: String = "Tasa de libertad financiera"
    override val auditCfsIndependence: (String, String) -> String = { cfs, independence -> "🎉 Libertad financiera (CFS: $cfs): El patrimonio neto incluidos inmuebles supera $independence años de gastos." }
    override val auditCfsStability: (String, String, String) -> String = { cfs, stability, independence -> "✅ Libertad financiera (CFS: $cfs): El patrimonio neto supera $stability años de gastos; el siguiente tramo conduce a la barrera de los $independence años." }
    override val auditCfsGrowth: (String, String) -> String = { cfs, stability -> "✅ Libertad financiera (CFS: $cfs): El patrimonio neto cubre varios años de gastos. Próxima meta: CFS > $stability." }
    override val auditCfsInitial: String = "ℹ️ Libertad financiera (CFS: %1${'$'}s): Fase inicial de creación de capital (%2${'$'}s). Enfoque principal en la constancia del ahorro."
    override val auditCfsNegative: String = "🚨 Libertad financiera (CFS: %1${'$'}s): Ratio negativo (%2${'$'}s). Las deudas superan todos los activos, incluidos los inmuebles."
    override val auditCfsBuffer: (String, String) -> String = { cfs, buffer -> "📈 Libertad financiera (CFS: $cfs): El patrimonio neto cubre como máximo $buffer años de gastos." }
    override val auditCfsPathToIndependence: String = "Vas camino de la independencia, pero aún queda recorrido hasta la meta."
    override val auditCfsCloseToIndependence: (percent: Int) -> String = { percent -> "Estás muy cerca: tu capital cubre el $percent% del objetivo." }
    override val auditCfsFinanciallyIndependent: String = "Has alcanzado la independencia financiera: el CFS es de al menos 30."

    override val auditRealReturnTitle: String = "Rentabilidad real del capital"
    override val auditRealReturnPositive: String = "✅ Rentabilidad real (+%1${'$'}s): La rentabilidad supera a la inflación (%2${'$'}s frente al %3${'$'}s). El capital queda protegido de la devaluación y gana poder adquisitivo."
    override val auditRealReturnNeutral: String = "ℹ️ Rentabilidad real (0.0%): El rendimiento compensa exactamente la inflación. El capital mantiene su valor nominal pero no crece en términos reales."
    override val auditRealReturnNegative: String = "⚠️ Rentabilidad real (%1${'$'}s): La inflación (%2${'$'}s) supera los rendimientos (%3${'$'}s). Devaluación silenciosa de los ahorros y pérdida de poder de compra."

    // Export Summary
    override val exportHeaderTitle: String = "📊 INFORME FINANCIERO — FINANZAS PERSONALES"
    override val exportSection1Title: String = "1. Datos introducidos:"
    override val exportIncomeLine: String = "• Ingresos: %1${'$'}s/mes"
    override val exportExpensesLine: String = "• Gastos: %1${'$'}s/mes"
    override val exportLiquidAssetsLine: String = "• Activos líquidos: %1${'$'}s"
    override val exportFinancialAssetsLine: String = "• Activos financieros: %1${'$'}s"
    override val exportPropertyAssetsLine: String = "• Bienes físicos / Inmuebles: %1${'$'}s"
    override val exportLiabilitiesLine: String = "• Pasivo (deudas): %1${'$'}s"
    override val exportAgeHorizonLine: String = "• Edad: %1${'$'}d años (esperanza: %2${'$'}d años, horizonte: %3${'$'}d años)"
    override val exportReturnsInflationLine: String = "• Rentabilidad nominal: %1${'$'}s | Inflación: %2${'$'}s"
    override val exportTaxLine: String = "• Impuesto sobre plusvalías: %1${'$'}s"

    override val exportSection2Title: String = "2. Métricas calculadas:"
    override val exportCashFlowLine: String = "• Cash Flow: %1${'$'}s/mes"
    override val exportSavingsRateLine: String = "• Tasa de ahorro: %1${'$'}s"
    override val exportEmergencyFundLine: String = "• Fondo de emergencia: %1${'$'}s (%2${'$'}s meses)"
    override val exportDebtRatioLine: String = "• Ratio de endeudamiento: %1${'$'}s"
    override val exportNetWorthLine: String = "• Patrimonio neto: %1${'$'}s"
    override val exportRealReturnLine: String = "• Rentabilidad real: %1${'$'}s"
    override val exportEffectiveTaxLine: String = "• Tipo impositivo efectivo al reembolsar: %1${'$'}s"

    override val exportSection3Title: String = "3. Resumen de puntuación:"
    override val exportScoreTotalLine: String = "• Puntuación global: %1${'$'}s de 10"
    override val exportScoreSavingsRateLine: String = "• Tasa de ahorro: %1${'$'}d / 10"
    override val exportScoreSafetyMonthsLine: String = "• Colchón de seguridad: %1${'$'}d / 10"
    override val exportScoreDebtRatioLine: String = "• Carga de deuda: %1${'$'}d / 10"
    override val exportScoreCfsLine: String = "• Libertad financiera: %1${'$'}d / 10"

    override val exportSection4Title: String = "4. Independencia Financiera, FIRE:"
    override val exportCfsLine: String = "• CFS: %1${'$'}s (%2${'$'}s)"
    override val exportFireTargetCapitalLine: String = "• Capital objetivo FIRE: %1${'$'}s"
    override val exportFireTimeToRetirementLine: String = "• Plazo hasta la jubilación: %1${'$'}s años (a los %2${'$'}s)"
    override val exportFidelityLine: String = "• Referencia Fidelity: %1${'$'}s (%2${'$'}sx ingresos anuales) — %3${'$'}s del objetivo"
}
