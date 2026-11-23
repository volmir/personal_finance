package com.example

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Typography
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TypographyTest {
    private val styles = with(Typography) {
        listOf(
            displayLarge, displayMedium, displaySmall,
            headlineLarge, headlineMedium, headlineSmall,
            titleLarge, titleMedium, titleSmall,
            bodyLarge, bodyMedium, bodySmall,
            labelLarge, labelMedium, labelSmall
        )
    }

    @Test
    fun `dense screens retain body medium size and use readable supporting text`() {
        assertEquals(16.sp, Typography.bodyLarge.fontSize)
        assertEquals(14.sp, Typography.bodyMedium.fontSize)
        assertEquals(13.sp, Typography.bodySmall.fontSize)
        assertEquals(20.sp, Typography.titleMedium.fontSize)
        assertEquals(12.sp, Typography.labelSmall.fontSize)
    }

    @Test
    fun `every role has a system font and explicit scalable size and line height`() {
        for (style in styles) {
            assertEquals(FontFamily.Default, style.fontFamily)
            assertTrue(style.fontSize.isSp)
            assertTrue(style.lineHeight.isSp)
            assertTrue(style.fontSize.value >= 12f)
            assertTrue(style.lineHeight.value >= style.fontSize.value * 1.1f)
        }
    }

    @Test
    fun `font scaling keeps every role readable including small text at twice the scale`() {
        for (fontScale in listOf(1f, 1.3f, 2f)) {
            with(Density(density = 2f, fontScale = fontScale)) {
                for (style in styles) {
                    // Android увеличивает крупный текст слабее мелкого: линейная формула неверна.
                    val textPx = style.fontSize.toPx()
                    val linePx = style.lineHeight.toPx()
                    assertTrue(textPx >= style.fontSize.value * density)
                    assertTrue(linePx >= style.lineHeight.value * density)
                    assertTrue(linePx > textPx)
                    if (fontScale > 1f && style.fontSize.value <= 20f) {
                        assertTrue(textPx > style.fontSize.value * density)
                        assertTrue(linePx > style.lineHeight.value * density)
                    }
                }
            }
        }
    }
}
