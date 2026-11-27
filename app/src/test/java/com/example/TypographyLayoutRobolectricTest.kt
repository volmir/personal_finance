package com.example

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.ui.AppTab
import com.example.ui.FinanceApp
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Real app, including bars, every lazy-list page, both FIRE subtabs and expanded explanations. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "ru-w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TypographyLayoutRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `standard screens and selected localization risks fit completely`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app) { LocalDate.of(2026, 11, 6) }
        viewModel.dismissSampleDataNotice()
        val width = mutableStateOf(360)
        val scale = mutableStateOf(1f)
        val dark = mutableStateOf(false)
        val language = mutableStateOf("ru")
        val version = mutableStateOf(0)
        composeRule.setContent {
            val density = LocalDensity.current
            val configuration = Configuration(LocalConfiguration.current).apply {
                screenWidthDp = width.value
                fontScale = scale.value
                setLocale(Locale(language.value))
            }
            CompositionLocalProvider(
                LocalConfiguration provides configuration,
                LocalDensity provides Density(density.density, scale.value)
            ) {
                Box(Modifier.width(width.value.dp).fillMaxHeight()) {
                    key(version.value) {
                        MyApplicationTheme(darkTheme = dark.value, dynamicColor = false) {
                            FinanceApp(viewModel)
                        }
                    }
                }
            }
        }
        val failures = linkedSetOf<String>()
        val textMatcher = SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult)
        val listMatcher = SemanticsMatcher.keyIsDefined(SemanticsActions.ScrollToIndex)
        fun inspect(context: String) {
            for (node in composeRule.onAllNodes(textMatcher, useUnmergedTree = true).fetchSemanticsNodes()) {
                val layouts = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
                for (layout in layouts) {
                    val text = layout.layoutInput.text.text
                    if (layout.layoutInput.style.fontSize.value < 12f) failures += "$context below 12sp: $text"
                    // GetTextLayoutResult can recreate an intrinsic-width paragraph using
                    // the original maxWidth (even Infinity). Check actual line extents,
                    // not didOverflowWidth, which then compares that maxWidth with size.
                    val lineTooWide = (0 until layout.lineCount).any {
                        layout.getLineRight(it) - layout.getLineLeft(it) > layout.size.width + 0.5f
                    }
                    if (lineTooWide || layout.didOverflowHeight ||
                            (0 until layout.lineCount).any { layout.isLineEllipsized(it) }) {
                        val issue = "$context overflow: $text (size=${layout.size}, lines=${layout.lineCount}, " +
                            "width=${layout.didOverflowWidth}, height=${layout.didOverflowHeight}, " +
                            "paragraph=${layout.multiParagraph.width}x${layout.multiParagraph.height}, " +
                            "font=${layout.layoutInput.style.fontSize}, lineHeight=${layout.layoutInput.style.lineHeight}, " +
                            "constraints=${layout.layoutInput.constraints})"
                        failures.add(issue)
                    }
                }
            }
        }
        try {
            // Five baseline screens; each extra configuration protects one named risk.
            val scenarios = listOf(
                Triple("ru", false, listOf("INPUT", "METRICS", "SCORES", "FIRE", "SIMULATION")),
                Triple("de", false, listOf("METRICS")), // Long metric names and formulas.
                Triple("en", false, listOf("INPUT", "SCORES")) // Default-language fields and audit.
            )
            // Sticky summary DE and dark rendering are covered by its dedicated pixel/layout tests.
            for ((lang, isDark, screens) in scenarios) {
                val w = 360
                val s = 1f
                composeRule.runOnIdle {
                    width.value = w
                    scale.value = s
                    language.value = lang
                    dark.value = isDark
                    viewModel.updateLanguageFromSystem(lang)
                    viewModel.selectTab(AppTab.INPUT)
                    version.value++
                }
                val navWidth = composeRule.onNodeWithTag("bottom_nav_bar").fetchSemanticsNode().size.width
                assertTrue("Requested width=$w, actual navigation width=$navWidth", navWidth == w)
                for (screen in screens) {
                    if (screen == "SIMULATION") {
                        composeRule.onNodeWithTag("fire_subtab_simulation").performClick()
                    } else {
                        composeRule.runOnIdle { viewModel.selectTab(AppTab.valueOf(screen)) }
                    }
                    composeRule.mainClock.advanceTimeBy(1_000)
                    val context = "$w/$s/$lang/dark=$isDark/$screen"
                    val expanded = mutableSetOf<String>()
                    var pages = 0
                    while (true) {
                        inspect(context)
                        if (screen == "METRICS") {
                            val cards = composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick), useUnmergedTree = true)
                                .fetchSemanticsNodes().filter {
                                    val tag = it.config.getOrElse(SemanticsProperties.TestTag) { "" }
                                    tag.startsWith("metric_card_") && tag != "metric_card_formula" && tag !in expanded
                                }
                            for (card in cards) {
                                expanded += card.config[SemanticsProperties.TestTag]
                                composeRule.runOnIdle { card.config[SemanticsActions.OnClick].action?.invoke() }
                            }
                            composeRule.mainClock.advanceTimeBy(500)
                            inspect("$context/expanded")
                        }
                        val list = composeRule.onAllNodes(listMatcher).fetchSemanticsNodes().single()
                        val range = list.config[SemanticsProperties.VerticalScrollAxisRange]
                        if (range.value() >= range.maxValue()) break
                        composeRule.onAllNodes(listMatcher)[0].performSemanticsAction(SemanticsActions.ScrollBy) {
                            it(0f, 400f)
                        }
                        composeRule.waitForIdle()
                        // LazyColumn estimates the scroll range from average item height;
                        // it can decrease when an expanded card is composed.
                        check(++pages < 150) { "$context did not reach list end" }
                    }
                    // The selector must be back on screen before switching to Simulation.
                    if (screen == "FIRE") composeRule.onAllNodes(listMatcher)[0].performScrollToIndex(0)
                }
            }
        } finally {
            composeRule.runOnIdle { viewModel.flushPendingSave() }
        }
        assertTrue("Typography matrix failures (${failures.size}):\n${failures.joinToString("\n")}", failures.isEmpty())
    }
}
