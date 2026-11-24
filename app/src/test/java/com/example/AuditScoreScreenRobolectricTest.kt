package com.example

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.domain.FinanceCalculator
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.screens.AuditOpinionIndicatorItem
import com.example.ui.screens.AuditScoreScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import com.example.util.localization.getAppStrings
import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class AuditScoreScreenRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `info cards collapse independently in all languages themes and large font`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))
        val language = mutableStateOf(AppLanguage.RU)
        val dark = mutableStateOf(false)
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalAppLanguage provides language.value,
                LocalAppStrings provides getAppStrings(language.value),
                LocalDensity provides Density(density.density, 1.3f)
            ) {
                MyApplicationTheme(darkTheme = dark.value) {
                    Box(Modifier.width(320.dp)) {
                        AuditScoreScreen(inputData = input, metrics = metrics)
                    }
                }
            }
        }
        for (lang in AppLanguage.values()) for (isDark in listOf(false, true)) {
            composeRule.runOnIdle {
                language.value = lang
                dark.value = isDark
            }
            val strings = getAppStrings(lang)
            val cards = listOf(
                Triple("audit_assumptions_card", strings.fireAssumptionsTitle,
                    strings.fireAssumptionsBody(
                        Formatters.formatPercent(input.nominalReturnPercent, lang),
                        Formatters.formatPercent(input.inflationPercent, lang)
                    )),
                Triple("developer_disclaimer_card", strings.disclaimerTitle, strings.disclaimerText)
            )
            for ((tag, title, body) in cards) {
                composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag(tag))
                composeRule.onNodeWithTag("${tag}_icon", useUnmergedTree = true).assertIsDisplayed()
                composeRule.onNode(hasText(title) and hasAnyAncestor(hasTestTag(tag)), useUnmergedTree = true)
                    .assertIsDisplayed()
                composeRule.onNodeWithTag("${tag}_body").assertDoesNotExist()
                composeRule.onNodeWithTag("${tag}_toggle")
                    .assert(hasClickAction()).assertHeightIsAtLeast(48.dp)
                    .assertContentDescriptionEquals(strings.metricCardExpand).performClick()
                composeRule.onNodeWithTag("${tag}_body").assertTextEquals(body)
                val otherTag = cards.first { it.first != tag }.first
                composeRule.onNodeWithTag("${otherTag}_body").assertDoesNotExist()
                composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("${tag}_toggle"))
                composeRule.onNodeWithTag("${tag}_toggle")
                    .assertContentDescriptionEquals(strings.metricCardCollapse).performClick()
                composeRule.onNodeWithTag("${tag}_body").assertDoesNotExist()
            }
        }
    }

    @Test
    fun `info card states survive tab disposal and saved instance restoration`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))
        val showAudit = mutableStateOf(true)
        val restoration = StateRestorationTester(composeRule)
        restoration.setContent {
            val tabs = rememberSaveableStateHolder()
            MaterialTheme {
                CompositionLocalProvider(LocalAppStrings provides StringsRu) {
                    if (showAudit.value) {
                        tabs.SaveableStateProvider("scores") {
                            AuditScoreScreen(inputData = input, metrics = metrics)
                        }
                    }
                }
            }
        }
        fun toggle(tag: String) {
            composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("${tag}_toggle"))
            composeRule.onNodeWithTag("${tag}_toggle").performClick()
        }
        fun assertState(assumptionsExpanded: Boolean, disclaimerExpanded: Boolean) {
            for ((tag, expanded) in listOf(
                "audit_assumptions_card" to assumptionsExpanded,
                "developer_disclaimer_card" to disclaimerExpanded
            )) {
                composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("${tag}_toggle"))
                composeRule.onNodeWithTag("${tag}_toggle").assertContentDescriptionEquals(
                    if (expanded) StringsRu.metricCardCollapse else StringsRu.metricCardExpand
                )
                if (expanded) composeRule.onNodeWithTag("${tag}_body").assertExists()
                else composeRule.onNodeWithTag("${tag}_body").assertDoesNotExist()
            }
        }
        toggle("audit_assumptions_card")
        composeRule.runOnIdle { showAudit.value = false }
        composeRule.runOnIdle { showAudit.value = true }
        assertState(true, false)
        restoration.emulateSavedInstanceStateRestore()
        assertState(true, false)
        toggle("developer_disclaimer_card")
        assertState(true, true)
        toggle("audit_assumptions_card")
        restoration.emulateSavedInstanceStateRestore()
        assertState(false, true)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `breakdown keeps four compact rows and full heading at 320dp in all languages and themes`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))
        val language = mutableStateOf(AppLanguage.RU)
        val dark = mutableStateOf(false)
        val scale = mutableStateOf(1f)
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalAppLanguage provides language.value,
                LocalAppStrings provides getAppStrings(language.value),
                LocalDensity provides Density(density.density, scale.value)
            ) {
                MyApplicationTheme(darkTheme = dark.value) {
                    Box(Modifier.width(320.dp)) {
                        AuditScoreScreen(inputData = input, metrics = metrics)
                    }
                }
            }
        }

        for (lang in AppLanguage.values()) for (isDark in listOf(false, true)) for (fontScale in listOf(1f, 1.3f)) {
            composeRule.runOnIdle {
                language.value = lang
                dark.value = isDark
                scale.value = fontScale
            }
            val strings = getAppStrings(lang)
            composeRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("scores_breakdown_card"))
            val heading = composeRule.onNodeWithTag("scores_breakdown_title", useUnmergedTree = true)
            heading.assertTextEquals(strings.scoresBreakdownTitle)
                .assert(hasAnyAncestor(hasTestTag("scores_breakdown_card")))
            val layouts = mutableListOf<TextLayoutResult>()
            heading.fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action!!.invoke(layouts)
            val layout = layouts.single()
            assertEquals("$lang heading lines", 1, layout.lineCount)
            // Semantics may recreate an intrinsic-width paragraph; compare actual line extents.
            assertTrue("$lang heading must fit at 320dp, scale $fontScale", !layout.didOverflowHeight &&
                layout.getLineRight(0) - layout.getLineLeft(0) <= heading.fetchSemanticsNode().size.width + 0.5f)
            assertTrue("$lang heading must not be ellipsized", !layout.isLineEllipsized(0))
            assertEquals(14.sp, layout.layoutInput.style.fontSize)
            assertEquals(20.sp, layout.layoutInput.style.lineHeight)
            assertEquals(FontWeight.Bold, layout.layoutInput.style.fontWeight)

            val rows = listOf(
                Triple("score_savings_rate", strings.metricSavingsRateTitle,
                    Formatters.formatPercent(metrics.savingsRate, lang) to metrics.scores.savingsRateScore),
                Triple("score_safety_months", strings.metricSafetyMonthsTitle,
                    String.format(Locale.ROOT, strings.safetyMonthsValueFormat, Formatters.formatRatio(metrics.safetyMonths, lang)) to metrics.scores.safetyMonthsScore),
                Triple("score_debt_ratio", strings.metricDebtRatioTitle,
                    Formatters.formatRatio(metrics.debtRatio, lang) to metrics.scores.debtRatioScore),
                Triple("score_cfs", strings.metricCfsTitle,
                    Formatters.formatRatio(metrics.financialFreedomRatio, lang) to metrics.scores.cfsScore)
            )
            var previousBottom = heading.getUnclippedBoundsInRoot().bottom
            for ((tag, title, valueAndScore) in rows) {
                val (value, score) = valueAndScore
                val row = composeRule.onNodeWithTag(tag, useUnmergedTree = true)
                row.assert(hasAnyAncestor(hasTestTag("scores_breakdown_card")))
                val bounds = row.getUnclippedBoundsInRoot()
                assertEquals("$tag has an internal 8dp gap", 8.dp, bounds.top - previousBottom)
                previousBottom = bounds.bottom
                // Exactly title, current value and badge: no explanatory paragraph.
                val descendants = hasAnyAncestor(hasTestTag(tag))
                composeRule.onAllNodes(descendants and SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
                    .assertCountEquals(3)
                for (text in listOf(title, "${strings.scoreValuePrefix} $value", strings.scoreOutOfTen(score))) {
                    composeRule.onNode(descendants and hasText(text), useUnmergedTree = true).assertExists()
                }
                val progress = composeRule.onNodeWithTag("${tag}_progress", useUnmergedTree = true)
                progress.assert(hasAnyAncestor(hasTestTag(tag)))
                assertEquals(score / 10f, progress.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current)
            }
        }
    }

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
        val opinion = AuditorAnalysisGenerator.generate(input, metrics, AppLanguage.RU)

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
        val exportButton = composeRule.onNodeWithTag("export_auditor_opinion_button")
        exportButton
            .assertExists()
            .assert(hasClickAction())
            .assertHeightIsAtLeast(48.dp)
            .assert(hasAnyAncestor(hasTestTag("auditor_opinion_card")))
            .assertTextEquals(StringsRu.exportAuditorOpinionButtonText)

        // Кнопка находится после всего текста заключения и закрывает карточку.
        val buttonBounds = exportButton.getUnclippedBoundsInRoot()
        val cardBounds = composeRule.onNodeWithTag("auditor_opinion_card").getUnclippedBoundsInRoot()
        (opinion.positive + opinion.neutral + opinion.negative).forEach { item ->
            val textBounds = composeRule.onNodeWithTag("audit_indicator_${item.id}").getUnclippedBoundsInRoot()
            assertTrue(
                "Audit item '${item.id}' must be above the export button",
                textBounds.bottom <= buttonBounds.top
            )
        }
        assertTrue(
            "The export button must be the last element inside the opinion card",
            cardBounds.bottom >= buttonBounds.bottom && cardBounds.bottom - buttonBounds.bottom <= 17.dp
        )
    }

    @Test
    fun `financial score header keeps its share icon and gains no score export`() {
        val input = FinancialInputData.defaultData()
        val metrics = FinanceCalculator.calculate(input, today = LocalDate.of(2025, 1, 1))
        val scores = metrics.scores
        val startedIntents = mutableListOf<Intent>()
        val recordingContext = object : ContextWrapper(ApplicationProvider.getApplicationContext<Context>()) {
            override fun startActivity(intent: Intent) {
                startedIntents += intent
            }
        }

        composeRule.setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalAppLanguage provides AppLanguage.RU,
                    LocalAppStrings provides StringsRu,
                    LocalContext provides recordingContext
                ) {
                    AuditScoreScreen(inputData = input, metrics = metrics)
                }
            }
        }

        // В карточке финансовой оценки осталась только прежняя иконка «Поделиться отчётом».
        composeRule.onNodeWithTag("audit_score_header_card").assertExists()
        composeRule.onAllNodes(
            hasClickAction() and hasAnyAncestor(hasTestTag("audit_score_header_card")),
            useUnmergedTree = true
        ).assertCountEquals(1)
        composeRule.onNodeWithTag("share_audit_button")
            .assertExists()
            .assert(hasClickAction())
            .assertContentDescriptionEquals(StringsRu.scoresShareReportChooser)

        // «Экспорт оценки» и общая кнопка экспорта в карточке оценки не появились.
        composeRule.onAllNodes(
            hasText("Экспорт оценки", substring = true),
            useUnmergedTree = true
        ).assertCountEquals(0)
        composeRule.onAllNodes(
            hasTestTag("export_button") and hasAnyAncestor(hasTestTag("audit_score_header_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)
        composeRule.onAllNodes(
            hasTestTag("export_auditor_opinion_button") and hasAnyAncestor(hasTestTag("audit_score_header_card")),
            useUnmergedTree = true
        ).assertCountEquals(0)

        // Действие иконки прежнее: текстовый отчёт об оценке через системный chooser.
        composeRule.onNodeWithTag("share_audit_button").performClick()
        val chooser = startedIntents.single()
        assertEquals(Intent.ACTION_CHOOSER, chooser.action)
        assertEquals(StringsRu.scoresShareReportChooser, chooser.getStringExtra(Intent.EXTRA_TITLE))
        val sendIntent = requireNotNull(chooser.getParcelableExtra<Intent>(Intent.EXTRA_INTENT))
        assertEquals(Intent.ACTION_SEND, sendIntent.action)
        assertEquals("text/plain", sendIntent.type)
        val expectedReport = String.format(
            Locale.ROOT,
            StringsRu.shareScoreReportTemplate,
            Formatters.formatScore(scores.totalScore, lang = AppLanguage.RU),
            scores.savingsRateScore,
            Formatters.formatPercent(metrics.savingsRate, AppLanguage.RU),
            scores.safetyMonthsScore,
            Formatters.formatRatio(metrics.safetyMonths, AppLanguage.RU),
            scores.debtRatioScore,
            Formatters.formatRatio(metrics.debtRatio, AppLanguage.RU),
            scores.cfsScore,
            Formatters.formatRatio(metrics.financialFreedomRatio, AppLanguage.RU),
            Formatters.formatFireYearsToRetirement(metrics.yearsToRetirement, metrics.fireStatus, AppLanguage.RU),
            Formatters.formatFireRetirementAge(metrics.retirementAge, metrics.fireStatus, AppLanguage.RU)
        )
        assertEquals(expectedReport, sendIntent.getStringExtra(Intent.EXTRA_TEXT))
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
