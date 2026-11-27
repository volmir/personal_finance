package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.example.data.model.FinancialInputData
import com.example.util.Formatters
import com.example.util.localization.AppLanguage
import com.example.util.localization.getAppStrings
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.ui.components.CollapsibleInfoCard
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.StringsRu
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(qualifiers = "ru-w360dp-h800dp")
class CollapsibleInfoCardRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `collapsed card keeps compact vertical padding and full toggle target`() {
        var density = 1f
        composeRule.setContent {
            density = LocalDensity.current.density
            CompositionLocalProvider(LocalAppStrings provides StringsRu) {
                MaterialTheme {
                    Box(Modifier.width(360.dp)) {
                        CollapsibleInfoCard(
                            title = "Короткий заголовок",
                            body = "Текст пояснения",
                            leadingIcon = Icons.Default.Info,
                            iconTint = MaterialTheme.colorScheme.primary,
                            testTag = "geometry_card"
                        )
                    }
                }
            }
        }

        val card = composeRule.onNodeWithTag("geometry_card").assertIsDisplayed()
        assertEquals(64f * density, card.fetchSemanticsNode().size.height.toFloat(), density)
        composeRule.onNodeWithTag("geometry_card_toggle")
            .assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithTag("geometry_card_body").assertDoesNotExist()

        composeRule.onNodeWithTag("geometry_card_toggle").performClick()
        composeRule.onNodeWithTag("geometry_card_body").assertTextEquals("Текст пояснения")
        composeRule.onNodeWithTag("geometry_card_body").assertIsDisplayed()
    }


    @Test
    fun `localized info cards expose full titles bodies and expand collapse labels`() {
        val input = FinancialInputData.defaultData()
        val language = mutableStateOf(AppLanguage.RU)
        composeRule.setContent {
            val strings = getAppStrings(language.value)
            CompositionLocalProvider(LocalAppStrings provides strings) {
                MaterialTheme {
                    Column(Modifier.width(360.dp).verticalScroll(rememberScrollState())) {
                        CollapsibleInfoCard(
                            title = strings.fireAssumptionsTitle,
                            body = strings.fireAssumptionsBody(
                                Formatters.formatPercent(input.nominalReturnPercent, language.value),
                                Formatters.formatPercent(input.inflationPercent, language.value)
                            ),
                            leadingIcon = Icons.Default.Info,
                            iconTint = MaterialTheme.colorScheme.primary,
                            testTag = "assumptions"
                        )
                        CollapsibleInfoCard(
                            title = strings.disclaimerTitle,
                            body = strings.disclaimerText,
                            leadingIcon = Icons.Default.Info,
                            iconTint = MaterialTheme.colorScheme.primary,
                            testTag = "disclaimer"
                        )
                    }
                }
            }
        }
        for (lang in AppLanguage.values()) {
            composeRule.runOnIdle { language.value = lang }
            val strings = getAppStrings(lang)
            val cards = listOf(
                Triple("assumptions", strings.fireAssumptionsTitle, strings.fireAssumptionsBody(
                    Formatters.formatPercent(input.nominalReturnPercent, lang),
                    Formatters.formatPercent(input.inflationPercent, lang)
                )),
                Triple("disclaimer", strings.disclaimerTitle, strings.disclaimerText)
            )
            for ((tag, title, body) in cards) {
                composeRule.onNodeWithTag("${tag}_toggle").performScrollTo()
                composeRule.onNodeWithTag("${tag}_icon", useUnmergedTree = true).assertIsDisplayed()
                composeRule.onNodeWithText(title, useUnmergedTree = true).assertIsDisplayed()
                composeRule.onNodeWithTag("${tag}_body").assertDoesNotExist()
                composeRule.onNodeWithTag("${tag}_toggle")
                    .assertContentDescriptionEquals(strings.metricCardExpand).performClick()
                composeRule.onNodeWithTag("${tag}_body").assertTextEquals(body)
                val otherTag = cards.first { it.first != tag }.first
                composeRule.onNodeWithTag("${otherTag}_body").assertDoesNotExist()
                composeRule.onNodeWithTag("${tag}_toggle").performScrollTo()
                    .assertContentDescriptionEquals(strings.metricCardCollapse).performClick()
                composeRule.onNodeWithTag("${tag}_body").assertDoesNotExist()
            }
        }
    }
}
