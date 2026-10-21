package com.example.recipes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CraftLightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = BakedMilk,
    onPrimaryContainer = InkDark,
    secondary = SageGreen,
    onSecondary = Color.White,
    secondaryContainer = PaperCream,
    onSecondaryContainer = InkDark,
    tertiary = CategoryDessert,
    onTertiary = Color.White,
    tertiaryContainer = StickerYellow,
    onTertiaryContainer = InkDark,
    background = PaperCream,
    onBackground = InkDark,
    surface = BakedMilk,
    onSurface = InkDark,
    surfaceVariant = PaperCream,
    onSurfaceVariant = InkWashed,
    outline = DottedBorderColor,
    outlineVariant = DottedBorderColor.copy(alpha = 0.5f)
)

private val CraftDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD97254),
    onPrimary = Color(0xFF1E1715),
    primaryContainer = Color(0xFF4A2A20),
    onPrimaryContainer = Color(0xFFF9F6EE),
    secondary = Color(0xFF96B89A),
    onSecondary = Color(0xFF1B261D),
    secondaryContainer = Color(0xFF2E3D31),
    onSecondaryContainer = Color(0xFFF0EAE1),
    tertiary = Color(0xFFDB8872),
    onTertiary = Color(0xFF281814),
    background = Color(0xFF1E1B18),
    onBackground = Color(0xFFF0EAE1),
    surface = Color(0xFF28231F),
    onSurface = Color(0xFFF0EAE1),
    surfaceVariant = Color(0xFF332E2A),
    onSurfaceVariant = Color(0xFFB3AAA4),
    outline = Color(0xFF5A524D),
    outlineVariant = Color(0xFF423B36)
)

@Composable
fun RecipesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is false by default to preserve the warm craft aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CraftDarkColorScheme
        else -> CraftLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
