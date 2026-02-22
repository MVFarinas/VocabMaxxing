package com.vocabmaxxing.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
val TextPrimary = Color(0xFFE4E4E4)
val TextSecondary = Color(0xFF9CA3AF)
val TextMuted = Color(0xFF6B7280)
val BorderSubtle = Color(0x0DFFFFFF) // white/5

// Tier colors
val TierAcademic = Color(0xFF3B82F6)
val TierElite = Color(0xFFA855F7)
val TierProfessional = Color(0xFF10B981)

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Surface950,
    secondary = AccentDim,
    tertiary = AccentBright,
    background = Surface950,
    surface = Surface900,
    surfaceVariant = Surface850,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = ScoreLow,
)

@Composable
fun VocabMaxxingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
