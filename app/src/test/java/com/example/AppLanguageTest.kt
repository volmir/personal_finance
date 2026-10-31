package com.example

import com.example.util.localization.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun fromLocaleCodePreservesRussianBelarusianAndEnglishFallback() {
        assertEquals(AppLanguage.RU, AppLanguage.fromLocaleCode("ru"))
        assertEquals(AppLanguage.RU, AppLanguage.fromLocaleCode("be"))
        assertEquals(AppLanguage.RU, AppLanguage.fromLocaleCode(" RU "))
        assertEquals(AppLanguage.EN, AppLanguage.fromLocaleCode("en"))
        assertEquals(AppLanguage.EN, AppLanguage.fromLocaleCode("fr"))
    }
}
