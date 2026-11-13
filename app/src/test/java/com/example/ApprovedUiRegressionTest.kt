package com.example

import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import com.example.ui.AppTab
import com.example.util.localization.AppLanguage
import com.example.util.localization.StringsRu
import com.example.util.localization.getAppStrings
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Итоговые guard-проверки утверждённого набора UI/UX-правок.
 * Фиксируют только пользовательские названия и состав основных вкладок,
 * чтобы последующие изменения не откатили согласованные формулировки.
 */
@RunWith(RobolectricTestRunner::class)
class ApprovedUiRegressionTest {

    @Test
    fun `main navigation keeps exactly four tabs in the approved order`() {
        assertEquals(
            listOf("INPUT", "METRICS", "SCORES", "FIRE"),
            AppTab.values().map { it.name }
        )
    }

    @Test
    fun `approved Russian navigation and section labels are unchanged`() {
        assertEquals("Посмотреть показатели", StringsRu.calculateButtonText)
        assertEquals("Итоги и Баллы", StringsRu.tabScoresShort)
        assertEquals("Финансовая оценка", StringsRu.scoresHeaderTitle)
        assertEquals("Капитал и риски", StringsRu.categoryCapitalRisks)
        assertEquals("FIRE и Пенсия", StringsRu.tabFireShort)
        assertEquals("Бенчмарки", StringsRu.categoryBenchmarks)
        assertEquals("FIRE", StringsRu.fireSubtabBase)
        assertEquals("Симуляция", StringsRu.fireSubtabSimulation)
        assertEquals("Финансы и FIRE", StringsRu.appName)
    }

    @Test
    fun `short app name and calculate action are defined in all six languages`() {
        val appNames = mapOf(
            AppLanguage.RU to "Финансы и FIRE",
            AppLanguage.EN to "Finance & FIRE",
            AppLanguage.DE to "Finanzen & FIRE",
            AppLanguage.FR to "Finances et FIRE",
            AppLanguage.IT to "Finanze e FIRE",
            AppLanguage.ES to "Finanzas y FIRE"
        )
        val calculateButtons = mapOf(
            AppLanguage.RU to "Посмотреть показатели",
            AppLanguage.EN to "View Metrics",
            AppLanguage.DE to "Kennzahlen ansehen",
            AppLanguage.FR to "Voir les indicateurs",
            AppLanguage.IT to "Visualizza indicatori",
            AppLanguage.ES to "Ver métricas"
        )

        for (lang in AppLanguage.values()) {
            val strings = getAppStrings(lang)
            assertEquals("$lang appName", appNames.getValue(lang), strings.appName)
            assertEquals("$lang calculateButtonText", calculateButtons.getValue(lang), strings.calculateButtonText)
        }
    }

    @Test
    fun `system app name matches the short title in all six locales`() {
        val expected = mapOf(
            "ru-RU" to "Финансы и FIRE",
            "en-US" to "Finance & FIRE",
            "de-DE" to "Finanzen & FIRE",
            "fr-FR" to "Finances et FIRE",
            "it-IT" to "Finanze e FIRE",
            "es-ES" to "Finanzas y FIRE"
        )
        val base = ApplicationProvider.getApplicationContext<Context>()

        for ((tag, appName) in expected) {
            val config = Configuration(base.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(tag))
            }
            val localized = base.createConfigurationContext(config)
            assertEquals("$tag app_name", appName, localized.getString(R.string.app_name))
        }
    }
}
