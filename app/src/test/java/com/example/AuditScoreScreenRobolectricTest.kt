package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import com.example.data.model.FinancialInputData
import com.example.domain.FinanceCalculator
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.screens.AuditOpinionIndicatorItem
import com.example.ui.screens.AuditScoreScreen
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import java.time.LocalDate
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
    fun `fallback icon depends only on status and ignores numbers debt words and embedded icons`() {
        val current = mutableStateOf(AuditIndicatorStatus.POSITIVE to "30")
        composeRule.setContent {
            AuditOpinionIndicatorItem(text = current.value.second, status = current.value.first, id = "fallback")
        }
        for ((status, icon) in listOf(
            AuditIndicatorStatus.POSITIVE to "✅", AuditIndicatorStatus.NEUTRAL to "ℹ️", AuditIndicatorStatus.NEGATIVE to "⚠️"
        )) {
            for (text in listOf("30", "< 0", "долг", "debt", "value 🎉")) {
                composeRule.runOnIdle { current.value = status to text }
                val node = composeRule.onNodeWithTag("audit_indicator_fallback").fetchSemanticsNode()
                val displayed = node.config[SemanticsProperties.Text].joinToString(" ") { it.text }
                assertEquals("$icon $text", displayed)
            }
        }
    }

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
                text = "🚨 КФС < 0 — Вы в долгах",
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

    @Test
    fun `auditor opinion export is a single secondary action at the bottom of the opinion card`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu
                ) {
                    AuditScoreScreen(inputData = input, metrics = metrics)
                }
            }
        }

        // Карточка финансовой оценки не меняется: её иконка экспорта на месте.
        composeRule.onNodeWithTag("audit_score_header_card").assertExists()
        composeRule.onNodeWithTag("share_audit_button").assertExists()

        composeRule.onNode(hasScrollAction())
            .performScrollToNode(hasTestTag("auditor_opinion_card"))

        // Иконка экспорта в заголовке заключения удалена.
        composeRule.onAllNodes(hasTestTag("share_auditor_opinion_button"), useUnmergedTree = true)
            .assertCountEquals(0)

        // Внизу карточки заключения ровно одна текстовая кнопка экспорта.
        composeRule.onAllNodes(hasTestTag("export_auditor_opinion_button"), useUnmergedTree = true)
            .assertCountEquals(1)
        composeRule.onNodeWithTag("export_auditor_opinion_button")
            .assertExists()
            .assert(hasClickAction())
            .assertHeightIsAtLeast(48.dp)
            .assert(hasAnyAncestor(hasTestTag("auditor_opinion_card")))
            .assertTextEquals("Экспорт заключения")
    }

    @Test
    fun `shared results export button is absent from the audit tab`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu
                ) {
                    AuditScoreScreen(inputData = input, metrics = metrics)
                }
            }
        }

        composeRule.onNode(hasScrollAction())
            .performScrollToNode(hasTestTag("developer_disclaimer_card"))

        composeRule.onAllNodes(hasTestTag("export_button"), useUnmergedTree = true)
            .assertCountEquals(0)
    }
}
