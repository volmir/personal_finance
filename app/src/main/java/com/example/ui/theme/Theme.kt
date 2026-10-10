package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDarkPrimary,
    onPrimary = EmeraldDarkOnPrimary,
    primaryContainer = EmeraldDarkContainer,
    onPrimaryContainer = EmeraldDarkOnContainer,
    secondary = AmberSecondary,
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = AmberDarkContainer,
    onSecondaryContainer = AmberSecondary,
    tertiary = AccentTeal,
    background = SlateDarkSurface,
    onBackground = Color(0xFFE2EBE5),
    surface = SlateDarkSurface,
    onSurface = Color(0xFFE2EBE5),
    surfaceVariant = SlateDarkCard,
    onSurfaceVariant = Color(0xFFB9C8BF),
    outline = SlateDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldLightPrimary,
    onPrimary = EmeraldLightOnPrimary,
    primaryContainer = EmeraldLightContainer,
    onPrimaryContainer = EmeraldLightOnContainer,
    secondary = AccentGold,
    onSecondary = Color.White,
    secondaryContainer = AmberLightContainer,
    onSecondaryContainer = Color(0xFF261A00),
    tertiary = AccentTeal,
    background = SlateLightBackground,
    onBackground = Color(0xFF131D18),
    surface = SlateLightSurface,
    onSurface = Color(0xFF131D18),
    surfaceVariant = SlateLightCard,
    onSurfaceVariant = Color(0xFF4B5E55),
    outline = SlateLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted rich financial palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
