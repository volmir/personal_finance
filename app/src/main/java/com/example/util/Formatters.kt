package com.example.util

import com.example.util.localization.AppLanguage
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Formatters {
    private val symbolsRu = DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }

    private val symbolsEn = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    private val moneyFormatRu = DecimalFormat("#,##0.00", symbolsRu)
    private val moneyFormatEn = DecimalFormat("#,##0.00", symbolsEn)

    private val percentFormatRu = DecimalFormat("#,##0.00", symbolsRu)
    private val percentFormatEn = DecimalFormat("#,##0.00", symbolsEn)

    private val ratioFormatRu = DecimalFormat("#,##0.00", symbolsRu)
    private val ratioFormatEn = DecimalFormat("#,##0.00", symbolsEn)

    private val scoreFormatRu = DecimalFormat("#,##0.0", symbolsRu)
    private val scoreFormatEn = DecimalFormat("#,##0.0", symbolsEn)

    private val prefixCurrencies = setOf(
        "$", "€", "£", "¥", "₹", "C$", "A$",
        "USD", "EUR", "GBP", "JPY", "INR", "CAD", "AUD"
    )

    fun formatMoney(amount: Double, currency: String = "$", lang: AppLanguage = AppLanguage.RU): String {
        val formatter = if (lang == AppLanguage.EN) moneyFormatEn else moneyFormatRu
        return if (lang == AppLanguage.EN && currency in prefixCurrencies) {
            "$currency${formatter.format(amount)}"
        } else {
            "${formatter.format(amount)} $currency"
        }
    }

    fun formatNumber(amount: Double, lang: AppLanguage = AppLanguage.RU): String {
        return if (lang == AppLanguage.EN) moneyFormatEn.format(amount) else moneyFormatRu.format(amount)
    }

    fun formatPercent(value: Double, lang: AppLanguage = AppLanguage.RU): String {
        val formatter = if (lang == AppLanguage.EN) percentFormatEn else percentFormatRu
        return "${formatter.format(value)}%"
    }

    fun formatRatio(value: Double, lang: AppLanguage = AppLanguage.RU): String {
        val formatter = if (lang == AppLanguage.EN) ratioFormatEn else ratioFormatRu
        return formatter.format(value)
    }

    fun formatScore(value: Double, lang: AppLanguage = AppLanguage.RU): String {
        val formatter = if (lang == AppLanguage.EN) scoreFormatEn else scoreFormatRu
        return formatter.format(value)
    }

    fun parseDouble(input: String): Double {
        val cleaned = input.replace(" ", "")
            .replace(",", ".")
            .replace("\u00A0", "")
            .trim()
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    /**
     * Возвращает грамматически корректную форму с неразрывным пробелом:
     * RU: 1 чекбокс, 2 чекбокса, 5 чекбоксов
     * EN: 1 checkbox, 2 checkboxes
     */
    fun formatCheckboxesCount(count: Int, lang: AppLanguage = AppLanguage.RU): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        val word = strings.plural(count, strings.checkboxOne, strings.checkboxFew, strings.checkboxMany)
        return "$count\u00A0$word"
    }

    /**
     * Формирует бейдж активных параметров симуляции в одну строку с неразрывными пробелами.
     */
    fun formatSimulationBadge(activeCount: Int, hasSliders: Boolean, lang: AppLanguage = AppLanguage.RU): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return when {
            activeCount > 0 && hasSliders -> "${formatCheckboxesCount(activeCount, lang)}\u00A0+\u00A0${strings.simulationBadgeSlidersSuffix}"
            activeCount > 0 -> formatCheckboxesCount(activeCount, lang)
            else -> strings.simulationBadgeOnlySliders
        }
    }

    /**
     * Проверяет, недостижим ли FIRE при текущих параметрах:
     * retirementAge >= lifeExpectancy - 0.01
     */
    fun isFireUnreachable(retirementAge: Double, lifeExpectancy: Int): Boolean {
        return retirementAge >= lifeExpectancy - 0.01
    }

    fun isFireUnreachable(retirementAge: Double, lifeExpectancy: Double): Boolean {
        return retirementAge >= lifeExpectancy - 0.01
    }

    /**
     * Форматирует срок до пенсии в годах:
     * Если недостижимо -> «Недостижимо при текущих параметрах»
     * Иначе -> число лет с форматированием ratio
     */
    fun formatFireYearsToRetirement(
        yearsToRetirement: Double,
        retirementAge: Double,
        lifeExpectancy: Int,
        lang: AppLanguage = AppLanguage.RU
    ): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return if (isFireUnreachable(retirementAge, lifeExpectancy)) {
            strings.fireUnreachableText
        } else {
            formatRatio(yearsToRetirement, lang)
        }
    }

    /**
     * Форматирует возраст выхода на пенсию:
     * Если недостижимо -> «Недостижимо при текущих параметрах»
     * Иначе -> возраст с суффиксом лет/yrs
     */
    fun formatFireRetirementAge(
        retirementAge: Double,
        lifeExpectancy: Int,
        lang: AppLanguage = AppLanguage.RU
    ): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        return if (isFireUnreachable(retirementAge, lifeExpectancy)) {
            strings.fireUnreachableText
        } else {
            strings.fireRetirementAgeValue(formatRatio(retirementAge, lang))
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
        simulatedYearsToRetirement: Double,
        simulatedRetirementAge: Double,
        lifeExpectancy: Int,
        lang: AppLanguage = AppLanguage.RU
    ): String {
        val strings = com.example.util.localization.getAppStrings(lang)
        val isSimUnreachable = isFireUnreachable(simulatedRetirementAge, lifeExpectancy)
        val isBaseUnreachable = isFireUnreachable(baseRetirementAge, lifeExpectancy)

        return when {
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
        lifeExpectancy: Int,
        lang: AppLanguage = AppLanguage.RU
    ): String {
        return formatFireSimulationSummary(
            baseYearsToRetirement = baseMetrics.yearsToRetirement,
            baseRetirementAge = baseMetrics.retirementAge,
            simulatedYearsToRetirement = simulatedMetrics.yearsToRetirement,
            simulatedRetirementAge = simulatedMetrics.retirementAge,
            lifeExpectancy = lifeExpectancy,
            lang = lang
        )
    }
}
