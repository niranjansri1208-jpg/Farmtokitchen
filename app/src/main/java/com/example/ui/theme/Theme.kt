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

private val FarmTraceDarkColorScheme = darkColorScheme(
    primary = LimeGreen,
    onPrimary = ForestGreen,
    primaryContainer = DeepGreen,
    onPrimaryContainer = CreamWhite,
    secondary = FreshGreen,
    onSecondary = Color.White,
    secondaryContainer = DeepGreen,
    onSecondaryContainer = LimeGreen,
    tertiary = EarthBrown,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = CreamWhite,
    surface = DarkSurface,
    onSurface = CreamWhite,
    surfaceVariant = Color(0xFF1E382A),
    onSurfaceVariant = Color(0xFFCBDAD1),
    outline = DarkCardBorder
)

private val FarmTraceLightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F5C9),
    onPrimaryContainer = ForestGreen,
    secondary = DeepGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4F0E6),
    onSecondaryContainer = DeepGreen,
    tertiary = FreshGreen,
    onTertiary = Color.White,
    background = CreamWhite,
    onBackground = ForestGreen,
    surface = Color.White,
    onSurface = ForestGreen,
    surfaceVariant = SurfaceCream,
    onSurfaceVariant = MutedGray,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve FarmTrace branded colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FarmTraceDarkColorScheme
        else -> FarmTraceLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun FarmTraceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MyApplicationTheme(darkTheme = darkTheme, dynamicColor = false, content = content)
}
