package com.example

import com.example.util.localization.AppLanguage
import com.example.util.localization.getAppStrings
import org.junit.Assert.assertEquals
import org.junit.Test

/** Text content for six locales; screen wiring and geometry use the standard configuration. */
class LocalizedUiTextTest {
    @Test
    fun `FIRE controls and ordered scenario factors keep complete localized text`() {
        val expected = mapOf(
            AppLanguage.RU to listOf(
                "Дата рождения:",
                "FIRE",
                "Симуляция",
                "Сбросить",
                "Симулятор «Что если?»",
                "Тонкая настройка шкал (ползунки):",
                "Панель сценариев (стресс-тесты)",
                "Результат влияния разных ползунков и чекбоксов суммируется (накапливается и рассчитывается совместно). Итоговый результат отражается в числах и на графике:",
                " к базовым",
                "% к базовой",
                "рост",
                "Повышение зарплаты",
                "Ипотека на квартиру",
                "Автомобиль в кредит",
                "Рождение ребенка",
                "Оптимизация расходов",
                "Переезд в регион с более низкой стоимостью жизни",
                "Рост расходов",
                "Изменений: 3"
            ),
            AppLanguage.EN to listOf(
                "Date of birth:",
                "FIRE",
                "Simulation",
                "Reset",
                "Simulator \"What If?\"",
                "Fine-tune scales (sliders):",
                "Scenario Panel (Stress Tests)",
                "The collective impact of sliders and checkboxes accumulates and is calculated together. The final result reflects in numbers and on the trajectory chart:",
                " to base",
                "% to base",
                "growth",
                "Salary Increase",
                "Home Mortgage",
                "Car Loan",
                "Child Birth",
                "Expense Optimization",
                "Relocation to Lower-Cost Region",
                "Expense Increase",
                "Changes: 3"
            ),
            AppLanguage.DE to listOf(
                "Geburtsdatum:",
                "FIRE",
                "Simulation",
                "Zurücksetzen",
                "Simulator \"Was wäre wenn?\"",
                "Feineinstellung (Schieberegler):",
                "Szenarien-Panel (Stresstests)",
                "Die Gesamtauswirkung der Regler und Optionen wird kumulativ berechnet und fließt in die Kennzahlen und die Verlaufskurve ein:",
                " zur Basis",
                "% zur Basis",
                "Zuwachs",
                "Gehaltserhöhung",
                "Immobilienkredit (Hypothek)",
                "Autokredit",
                "Geburt eines Kindes",
                "Kostenoptimierung",
                "Umzug in günstigere Region",
                "Kostensteigerung",
                "Änderungen: 3"
            ),
            AppLanguage.FR to listOf(
                "Date de naissance :",
                "FIRE",
                "Simulation",
                "Réinitialiser",
                "Simulateur \"Que se passerait-il si ?\"",
                "Réglage fin (curseurs) :",
                "Panneau de scénarios (Stress tests)",
                "L'impact cumulé des curseurs et des scénarios est calculé ensemble et se reflète sur les chiffres et la courbe de trajectoire :",
                " par rapport à la base",
                "% par rapport à la base",
                "hausse",
                "Augmentation de salaire",
                "Crédit immobilier (résidence)",
                "Crédit automobile",
                "Naissance d'un enfant",
                "Optimisation des dépenses",
                "Déménagement dans une région moins chère",
                "Hausse des coûts",
                "Changements : 3"
            ),
            AppLanguage.IT to listOf(
                "Data di nascita:",
                "FIRE",
                "Simulazione",
                "Reimposta",
                "Simulatore \"Cosa succederebbe se?\"",
                "Regolazione fine (cursori):",
                "Pannello Scenari (Stress Test)",
                "L'impatto complessivo di cursori e opzioni si accumula e viene calcolato congiuntamente, riflettendosi sui valori e sul grafico:",
                " rispetto alla base",
                "% rispetto alla base",
                "aumento",
                "Aumento di stipendio",
                "Mutuo casa",
                "Prestito auto",
                "Nascita di un figlio",
                "Ottimizzazione delle spese",
                "Trasferimento in una regione più economica",
                "Aumento dei costi",
                "Modifiche: 3"
            ),
            AppLanguage.ES to listOf(
                "Fecha de nacimiento:",
                "FIRE",
                "Simulación",
                "Restablecer",
                "Simulador \"¿Qué pasaría si...?\"",
                "Ajuste fino (deslizadores):",
                "Panel de Escenarios (Pruebas de estrés)",
                "El efecto conjunto de deslizadores y opciones se acumula y calcula conjuntamente, reflejándose en las cifras y en el gráfico:",
                " sobre la base",
                "% sobre la base",
                "aumento",
                "Aumento salarial",
                "Hipoteca de vivienda",
                "Préstamo para coche",
                "Nacimiento de un hijo",
                "Optimización de gastos",
                "Mudanza a una región más económica",
                "Subida de costes",
                "Cambios: 3"
            )
        )
        for ((lang, texts) in expected) {
            val strings = getAppStrings(lang)
            assertEquals("$lang FIRE labels and subtitle factors", texts, listOf(
                strings.fireBirthDateLabel,
                strings.fireSubtabBase,
                strings.fireSubtabSimulation,
                strings.fireSimReset,
                strings.fireSimSliderTitle,
                strings.fireSimFineTuning,
                strings.scenarioHeaderTitle,
                strings.scenarioHeaderSubtitle,
                strings.fireSliderBaseSuffix,
                strings.fireSliderBaseReturnSuffix,
                strings.fireSliderInc,
                strings.scenarioSalaryIncreaseTitle,
                strings.scenarioMortgageTitle,
                strings.scenarioCarLoanTitle,
                strings.scenarioChildTitle,
                strings.scenarioExpenseOptTitle,
                strings.scenarioRelocationTitle,
                strings.scenarioInflationTitle,
                strings.fireSimChangesCount(3)
            ))
        }
    }
}
