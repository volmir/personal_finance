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

    fun formatMoney(amount: Double, currency: String = "₽", lang: AppLanguage = AppLanguage.RU): String {
        val formatter = if (lang == AppLanguage.EN) moneyFormatEn else moneyFormatRu
        return if (lang == AppLanguage.EN && (currency == "$" || currency == "€" || currency == "£")) {
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
        return if (lang == AppLanguage.EN) {
            val word = if (count == 1) "checkbox" else "checkboxes"
            "$count\u00A0$word"
        } else {
            val mod10 = count % 10
            val mod100 = count % 100
            val word = when {
                mod100 in 11..14 -> "чекбоксов"
                mod10 == 1 -> "чекбокс"
                mod10 in 2..4 -> "чекбокса"
                else -> "чекбоксов"
            }
            "$count\u00A0$word"
        }
    }

    /**
     * Формирует бейдж активных параметров симуляции в одну строку с неразрывными пробелами.
     */
    fun formatSimulationBadge(activeCount: Int, hasSliders: Boolean, lang: AppLanguage = AppLanguage.RU): String {
        return if (lang == AppLanguage.EN) {
            when {
                activeCount > 0 && hasSliders -> "${formatCheckboxesCount(activeCount, lang)}\u00A0+\u00A0sliders"
                activeCount > 0 -> formatCheckboxesCount(activeCount, lang)
                else -> "Adjustment sliders"
            }
        } else {
            when {
                activeCount > 0 && hasSliders -> "${formatCheckboxesCount(activeCount, lang)}\u00A0+\u00A0ползунки"
                activeCount > 0 -> formatCheckboxesCount(activeCount, lang)
                else -> "Ползунки настройки"
            }
        }
    }
}
