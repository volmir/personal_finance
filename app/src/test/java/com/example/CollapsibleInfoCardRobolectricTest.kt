package com.example

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.Density
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
                    Box(Modifier.width(320.dp)) {
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

        val card = composeRule.onNodeWithTag("geometry_card")
        assertEquals(64f * density, card.fetchSemanticsNode().size.height.toFloat(), density)
        composeRule.onNodeWithTag("geometry_card_toggle")
            .assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithTag("geometry_card_body").assertDoesNotExist()

        composeRule.onNodeWithTag("geometry_card_toggle").performClick()
        composeRule.onNodeWithTag("geometry_card_body").assertTextEquals("Текст пояснения")
        composeRule.onNodeWithTag("geometry_card_body").assertIsDisplayed()
    }

    @Test
    fun `large font does not clip the title`() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalAppStrings provides StringsRu,
                LocalDensity provides Density(density.density, 1.5f)
            ) {
                MaterialTheme {
                    Box(Modifier.width(180.dp)) {
                        CollapsibleInfoCard(
                            title = "Длинный заголовок карточки",
                            body = "Текст пояснения",
                            leadingIcon = Icons.Default.Info,
                            iconTint = MaterialTheme.colorScheme.primary,
                            testTag = "large_font_card"
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithTag("large_font_card").assertIsDisplayed()
        composeRule.onNodeWithTag("large_font_card_toggle").assertHeightIsAtLeast(48.dp)
        composeRule.onNodeWithTag("large_font_card_body").assertDoesNotExist()
    }
}
