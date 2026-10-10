package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Formatters {
    private val symbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }

    private val moneyFormat = DecimalFormat("#,##0.00", symbols)
    private val percentFormat = DecimalFormat("#,##0.00", symbols)
    private val ratioFormat = DecimalFormat("#,##0.00", symbols)
    private val scoreFormat = DecimalFormat("#,##0.0", symbols)

    fun formatMoney(amount: Double, currency: String = "₽"): String {
        return "${moneyFormat.format(amount)} $currency"
    }

    fun formatNumber(amount: Double): String {
        return moneyFormat.format(amount)
    }

    fun formatPercent(value: Double): String {
        return "${percentFormat.format(value)}%"
    }

    fun formatRatio(value: Double): String {
        return ratioFormat.format(value)
    }

    fun formatScore(value: Double): String {
        return scoreFormat.format(value)
    }

    fun parseDouble(input: String): Double {
        val cleaned = input.replace(" ", "")
            .replace(",", ".")
            .replace("\u00A0", "")
            .trim()
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    /**
     * Возвращает грамматически корректную форму в русском языке:
     * 1 чекбокс, 2 чекбокса, 5 чекбоксов с неразрывным пробелом,
     * исключающим перенос числа и текста на разные строки.
     */
    fun formatCheckboxesCount(count: Int): String {
        val mod10 = count % 10
        val mod100 = count % 100
        val word = when {
            mod100 in 11..14 -> "чекбоксов"
            mod10 == 1 -> "чекбокс"
            mod10 in 2..4 -> "чекбокса"
            else -> "чекбоксов"
        }
        return "$count\u00A0$word"
    }

    /**
     * Формирует бейдж активных параметров симуляции в одну строку с неразрывными пробелами.
     */
    fun formatSimulationBadge(activeCount: Int, hasSliders: Boolean): String {
        return when {
            activeCount > 0 && hasSliders -> "${formatCheckboxesCount(activeCount)}\u00A0+\u00A0ползунки"
            activeCount > 0 -> formatCheckboxesCount(activeCount)
            else -> "Ползунки настройки"
        }
    }
}
