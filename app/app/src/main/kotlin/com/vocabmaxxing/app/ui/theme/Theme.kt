package com.vocabmaxxing.app.ui.theme

import android.app.Activity

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.vocabmaxxing.app.R
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.MaterialTheme

val Inter = FontFamily(
    Font(resId = R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Brand colors
val Surface950 = Color(0xFF080808)
val Surface900 = Color(0xFF0E0E0E)
val Surface850 = Color(0xFF141414)
val Surface800 = Color(0xFF1A1A1A)
val Accent = Color(0xFFC8A45A)
val AccentDim = Color(0xFFA68940)
val AccentBright = Color(0xFFDBB96E)
val ScoreHigh = Color(0xFF4ADE80)
val ScoreMid = Color(0xFFFBBF24)
val ScoreLow = Color(0xFFEF4444)

private val DarkColorScheme = darkColorScheme(
    primary = mainGold,
    onPrimary = backgroundDarkMode,
    secondary = mainGreen,
    onSecondary = backgroundDarkMode,
    tertiary = mainBlue,
    onTertiary = backgroundDarkMode,
    background = backgroundDarkMode,
    onBackground = lightBeige,
    error = ScoreLow,
)

private val LightColorScheme = lightColorScheme(
    primary = mainGold,
    onPrimary = Color.White,
    secondary = mainGreen,
    onSecondary = darkBrown,
    tertiary = mainBlue,
    onTertiary = darkBrown,
    background = backgroundLightMode,
    onBackground = darkBrown,
    surface = lightBeige,
    onSurface = darkBrown,
    error = ScoreLow,
)

@Composable
fun VocabMaxxingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // Use dark status bar icons (black) on light background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
