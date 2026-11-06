package com.example

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.screens.AuditOpinionIndicatorItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuditScoreScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `CFS indicator in analytics block displays leading icon on positive status`() {
        composeRule.setContent {
            AuditOpinionIndicatorItem(
                text = "КФС 5..15 — Доходы от капитала могут приносить вам существенную прибавку. Инфляция все более значительно влияет на капитал",
                status = AuditIndicatorStatus.POSITIVE,
                id = "cfs"
            )
        }

        val node = composeRule.onNodeWithTag("audit_indicator_cfs").fetchSemanticsNode()
        val textList = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        val displayedText = textList.joinToString(" ") { it.text }

        assertTrue(
            "CFS indicator text '$displayedText' should start with an icon",
            displayedText.startsWith("✅")
        )
    }

    @Test
    fun `CFS indicator in analytics block displays alert icon when CFS is in debt`() {
        composeRule.setContent {
            AuditOpinionIndicatorItem(
                text = "КФС < 0 — Вы в долгах",
                status = AuditIndicatorStatus.NEGATIVE,
                id = "cfs"
            )
        }

        val node = composeRule.onNodeWithTag("audit_indicator_cfs").fetchSemanticsNode()
        val textList = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        val displayedText = textList.joinToString(" ") { it.text }

        assertTrue(
            "CFS indicator text '$displayedText' should start with an alert icon",
            displayedText.startsWith("🚨")
        )
    }

    @Test
    fun `CFS indicator in analytics block displays warning icon when CFS is low capital`() {
        composeRule.setContent {
            AuditOpinionIndicatorItem(
                text = "КФС 0.5..2 — Вы начали формировать свой финансовый капитал",
                status = AuditIndicatorStatus.NEGATIVE,
                id = "cfs"
            )
        }

        val node = composeRule.onNodeWithTag("audit_indicator_cfs").fetchSemanticsNode()
        val textList = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        val displayedText = textList.joinToString(" ") { it.text }

        assertTrue(
            "CFS indicator text '$displayedText' should start with a warning icon",
            displayedText.startsWith("⚠️")
        )
    }

    @Test
    fun `CFS indicator in analytics block displays info icon when CFS is neutral investing`() {
        composeRule.setContent {
            AuditOpinionIndicatorItem(
                text = "КФС 2..5 — Вы можете начать заниматься долгосрочными инвестициями",
                status = AuditIndicatorStatus.NEUTRAL,
                id = "cfs"
            )
        }

        val node = composeRule.onNodeWithTag("audit_indicator_cfs").fetchSemanticsNode()
        val textList = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        val displayedText = textList.joinToString(" ") { it.text }

        assertTrue(
            "CFS indicator text '$displayedText' should start with info icon",
            displayedText.startsWith("ℹ️")
        )
    }

    @Test
    fun `CFS indicator preserves existing leading icon without duplication`() {
        val originalText = "✅ Чистый капитал (Собственные средства: 187 200,00 ₽): Положительный чистый баланс"
        composeRule.setContent {
            AuditOpinionIndicatorItem(
                text = originalText,
                status = AuditIndicatorStatus.POSITIVE,
                id = "net_worth"
            )
        }

        val node = composeRule.onNodeWithTag("audit_indicator_net_worth").fetchSemanticsNode()
        val textList = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        val displayedText = textList.joinToString(" ") { it.text }

        assertEquals(originalText, displayedText)
    }

    @Test
    fun `cash flow indicator with zero cash flow displays warning icon and negative status`() {
        val input = com.example.data.model.FinancialInputData(
            monthlyIncome = 50_000.0,
            monthlyExpenses = 50_000.0
        )
        val metrics = com.example.domain.FinanceCalculator.calculate(input)
        val opinion = com.example.domain.AuditorAnalysisGenerator.generate(input, metrics, com.example.util.localization.AppLanguage.RU)
        val item = opinion.negative.first { it.id == "cash_flow" }

        composeRule.setContent {
            AuditOpinionIndicatorItem(
                text = item.text,
                status = item.status,
                id = item.id
            )
        }

        val node = composeRule.onNodeWithTag("audit_indicator_cash_flow").fetchSemanticsNode()
        val textList = node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        val displayedText = textList.joinToString(" ") { it.text }

        assertTrue(
            "Cash flow indicator text '$displayedText' should start with warning icon",
            displayedText.startsWith("⚠️")
        )
        assertTrue(
            "Cash flow indicator text should mention income equals expenses",
            displayedText.contains("доходы равны расходам")
        )
    }
}
