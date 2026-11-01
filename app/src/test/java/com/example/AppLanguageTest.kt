package com.example

import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun fromLocaleCodePreservesAllSupportedLanguagesAndEnglishFallback() {
        assertEquals(AppLanguage.RU, AppLanguage.fromLocaleCode("ru"))
        assertEquals(AppLanguage.RU, AppLanguage.fromLocaleCode("be"))
        assertEquals(AppLanguage.RU, AppLanguage.fromLocaleCode(" RU "))
        assertEquals(AppLanguage.EN, AppLanguage.fromLocaleCode("en"))
        assertEquals(AppLanguage.DE, AppLanguage.fromLocaleCode("de"))
        assertEquals(AppLanguage.DE, AppLanguage.fromLocaleCode("de-DE"))
        assertEquals(AppLanguage.FR, AppLanguage.fromLocaleCode("fr"))
        assertEquals(AppLanguage.FR, AppLanguage.fromLocaleCode("fr_FR"))
        assertEquals(AppLanguage.IT, AppLanguage.fromLocaleCode("it"))
        assertEquals(AppLanguage.IT, AppLanguage.fromLocaleCode("it-IT"))
        assertEquals(AppLanguage.ES, AppLanguage.fromLocaleCode("es"))
        assertEquals(AppLanguage.ES, AppLanguage.fromLocaleCode("es_ES"))
        // Unsupported languages fall back to default English
        assertEquals(AppLanguage.EN, AppLanguage.fromLocaleCode("ja"))
        assertEquals(AppLanguage.EN, AppLanguage.fromLocaleCode("zh"))
        assertEquals(AppLanguage.EN, AppLanguage.fromLocaleCode("pt"))
    }
}
