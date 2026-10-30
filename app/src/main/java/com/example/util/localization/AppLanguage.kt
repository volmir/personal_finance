package com.example.util.localization

enum class AppLanguage {
    RU,
    EN;

    companion object {
        /**
         * Логика выбора языковой версии:
         * - если у пользователя установлена русская (ru), украинская (uk) или белорусская (be) локаль — язык интерфейса русский.
         * - в противном случае — английский.
         */
        fun fromLocaleCode(languageCode: String): AppLanguage {
            return when (languageCode.lowercase().trim()) {
                "ru", "uk", "be" -> RU
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

val AppLanguage.isRussian: Boolean
    get() = this == AppLanguage.RU
