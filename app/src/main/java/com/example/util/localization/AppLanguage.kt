package com.example.util.localization

import java.util.Locale

enum class AppLanguage(val code: String, val locale: Locale) {
    RU("ru", Locale("ru")),
    EN("en", Locale("en"));

    companion object {
        /**
         * Логика выбора языковой версии:
         * - если у пользователя установлена русская (ru) или белорусская (be) локаль — язык интерфейса русский.
         * - в противном случае — английский.
         */
        fun fromLocaleCode(languageCode: String): AppLanguage {
            return when (languageCode.lowercase().trim()) {
                "ru", "be" -> RU
                else -> EN
            }
        }

        fun detectSystemLanguage(): AppLanguage {
            return try {
                val systemLang = java.util.Locale.getDefault().language
                fromLocaleCode(systemLang)
            } catch (_: Exception) {
                EN
            }
        }
    }
}
