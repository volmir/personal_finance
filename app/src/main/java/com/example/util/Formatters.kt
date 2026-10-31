package com.example.util

import com.example.util.localization.AppLanguage
import com.example.data.model.FireStatus
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object Formatters {
    private fun decimalFormat(lang: AppLanguage, fractionDigits: Int): DecimalFormat {
        val symbols = DecimalFormatSymbols.getInstance(lang.locale).apply {
            // Keep the existing Russian display separator while taking decimal/grouping
            // conventions from the language locale.
            if (groupingSeparator.isWhitespace() || groupingSeparator == '\u00A0' || groupingSeparator == '\u202F') {
                groupingSeparator = ' '
            }
        }
        return DecimalFormat("#,##0.${"0".repeat(fractionDigits)}", symbols).apply {
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
        }
    }

    fun formatMoney(amount: Double, currency: String = "$", lang: AppLanguage): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        val formattedAmount = decimalFormat(lang, 2).format(amount)
        return String.format(java.util.Locale.ROOT, strings.moneyFormat, currency, formattedAmount)
    }

    fun formatNumber(amount: Double, lang: AppLanguage): String {
        return decimalFormat(lang, 2).format(amount)
    }

    fun formatPercent(value: Double, lang: AppLanguage): String {
        return "${decimalFormat(lang, 2).format(value)}%"
    }

    fun formatRatio(value: Double, lang: AppLanguage): String {
        if (value.isInfinite()) return "—"
        return decimalFormat(lang, 2).format(value)
    }

    fun formatScore(value: Double, lang: AppLanguage): String {
        return decimalFormat(lang, 1).format(value)
    }

    fun parseDouble(input: String): Double {
        val cleaned = input.replace(" ", "")
            .replace(",", ".")
            .replace("\u00A0", "")
            .trim()
        if (cleaned.isEmpty() || cleaned == "-" || cleaned == "." || cleaned == "-.") return 0.0
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    /**
     * Возвращает грамматически корректную форму с неразрывным пробелом:
     * RU: 1 чекбокс, 2 чекбокса, 5 чекбоксов
     * EN: 1 checkbox, 2 checkboxes
     */
    fun formatCheckboxesCount(count: Int, lang: AppLanguage): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return strings.formatCount(count, strings.checkboxPluralForms)
    }

    /**
     * Формирует бейдж активных параметров симуляции в одну строку с неразрывными пробелами.
     */
    fun formatSimulationBadge(activeCount: Int, hasSliders: Boolean, lang: AppLanguage): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return when {
            activeCount > 0 && hasSliders -> "${formatCheckboxesCount(activeCount, lang)}\u00A0+\u00A0${strings.simulationBadgeSlidersSuffix}"
            activeCount > 0 -> formatCheckboxesCount(activeCount, lang)
            else -> strings.simulationBadgeOnlySliders
        }
    }

    fun isFireUnreachable(status: FireStatus): Boolean = status == FireStatus.UNREACHABLE

    /**
     * Форматирует срок до пенсии в годах:
     * Если недостижимо -> «Недостижимо при текущих параметрах»
     * Иначе -> число лет с форматированием ratio
     */
    fun formatFireYearsToRetirement(
        yearsToRetirement: Double,
        status: FireStatus,
        lang: AppLanguage
    ): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return when (status) {
            FireStatus.INVALID_AGE -> strings.invalidBirthDateError
            FireStatus.UNREACHABLE -> strings.fireUnreachableText
            FireStatus.HORIZON_EXHAUSTED -> strings.fireHorizonExhaustedText
            FireStatus.REACHED_NOW, FireStatus.REACHABLE -> formatRatio(yearsToRetirement, lang)
        }
    }

    /**
     * Форматирует возраст выхода на пенсию:
     * Если недостижимо -> «Недостижимо при текущих параметрах»
     * Иначе -> возраст с суффиксом лет/yrs
     */
    fun formatFireRetirementAge(
        retirementAge: Double,
        status: FireStatus,
        lang: AppLanguage
    ): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return when (status) {
            FireStatus.INVALID_AGE -> strings.invalidBirthDateError
            FireStatus.UNREACHABLE -> strings.fireUnreachableText
            FireStatus.HORIZON_EXHAUSTED -> strings.fireHorizonExhaustedText
            FireStatus.REACHED_NOW, FireStatus.REACHABLE -> strings.fireRetirementAgeValue(formatRatio(retirementAge, lang))
        }
    }

    /**
     * Форматирует сводный текст симуляции:
     * Если симуляция недостижима -> «Недостижимо при текущих параметрах» (скрывая разницу в годах с базой)
     * Если база недостижима, но симуляция достижима -> скрывает разницу в годах с базой
     * Иначе -> показывает стандартную разницу в годах с базой
     */
    fun formatFireSimulationSummary(
        baseYearsToRetirement: Double,
        baseRetirementAge: Double,
        baseStatus: FireStatus,
        simulatedYearsToRetirement: Double,
        simulatedRetirementAge: Double,
        simulatedStatus: FireStatus,
        lang: AppLanguage
    ): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        val isSimUnreachable = simulatedStatus == FireStatus.UNREACHABLE || simulatedStatus == FireStatus.HORIZON_EXHAUSTED
        val isBaseUnreachable = baseStatus == FireStatus.UNREACHABLE || baseStatus == FireStatus.HORIZON_EXHAUSTED

        return when {
            simulatedStatus == FireStatus.INVALID_AGE -> strings.invalidBirthDateError
            baseStatus == FireStatus.INVALID_AGE -> strings.invalidBirthDateError
            simulatedStatus == FireStatus.HORIZON_EXHAUSTED -> strings.fireHorizonExhaustedText
            isSimUnreachable -> strings.fireUnreachableText
            isBaseUnreachable -> strings.fireSimWithoutBase(
                formatRatio(simulatedYearsToRetirement, lang),
                formatRatio(simulatedRetirementAge, lang)
            )
            else -> {
                val yearsDiff = baseYearsToRetirement - simulatedYearsToRetirement
                when {
                    yearsDiff > 0 -> strings.fireSimEarlier(
                        formatRatio(yearsDiff, lang),
                        formatRatio(simulatedYearsToRetirement, lang),
                        formatRatio(simulatedRetirementAge, lang)
                    )
                    yearsDiff < 0 -> strings.fireSimLater(
                        formatRatio(-yearsDiff, lang),
                        formatRatio(simulatedYearsToRetirement, lang),
                        formatRatio(simulatedRetirementAge, lang)
                    )
                    else -> strings.fireSimSame(
                        formatRatio(simulatedYearsToRetirement, lang)
                    )
                }
            }
        }
    }

    fun formatFireSimulationSummary(
        baseMetrics: com.example.data.model.CalculatedMetrics,
        simulatedMetrics: com.example.data.model.CalculatedMetrics,
        lang: AppLanguage
    ): String {
        return formatFireSimulationSummary(
            baseYearsToRetirement = baseMetrics.yearsToRetirement,
            baseRetirementAge = baseMetrics.retirementAge,
            baseStatus = baseMetrics.fireStatus,
            simulatedYearsToRetirement = simulatedMetrics.yearsToRetirement,
            simulatedRetirementAge = simulatedMetrics.retirementAge,
            simulatedStatus = simulatedMetrics.fireStatus,
            lang = lang
        )
    }
}
