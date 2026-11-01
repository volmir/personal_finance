package com.example.util.localization

import java.util.Locale

enum class AppLanguage(val code: String, val locale: Locale) {
    RU("ru", Locale.forLanguageTag("ru")),
    EN("en", Locale.forLanguageTag("en")),
    DE("de", Locale.forLanguageTag("de")),
    FR("fr", Locale.forLanguageTag("fr")),
    IT("it", Locale.forLanguageTag("it")),
    ES("es", Locale.forLanguageTag("es"));

    companion object {
        /**
         * Логика выбора языковой версии:
         * - ru, be -> RU
         * - de -> DE
         * - fr -> FR
         * - it -> IT
         * - es -> ES
         * - по умолчанию -> EN
         */
        fun fromLocaleCode(languageCode: String): AppLanguage {
            val normalized = languageCode.lowercase().trim()
            val baseCode = if (normalized.contains("-") || normalized.contains("_")) {
                normalized.split('-', '_')[0]
            } else {
                normalized
            }
            return when (baseCode) {
                "ru", "be" -> RU
                "de" -> DE
                "fr" -> FR
                "it" -> IT
                "es" -> ES
                else -> EN
            }
        }

        fun detectSystemLanguage(): AppLanguage {
            return try {
                val systemLang = Locale.getDefault().language
                fromLocaleCode(systemLang)
            } catch (_: Exception) {
                EN
            }
        }
    }
}
