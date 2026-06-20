package com.vocabmaxxing.app.ui.components

import android.R.attr.onClick
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.ui.theme.*

// ─── Score Bar ───────────────────────────────────────────────────

@Composable
fun ScoreBar(
    label: String,
    score: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val pct = if (max > 0) score.toFloat() / max else 0f
    val animatedPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = tween(durationMillis = 800),
        label = "scoreBar"
    )
    val color = when {
        pct >= 0.7f -> ScoreHigh
        pct >= 0.4f -> ScoreMid
        else -> ScoreLow
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                fontSize = 10.sp
            )
            Text(
                text = "$score/$max",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedPct)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

// ─── Word Card ───────────────────────────────────────────────────

@Composable
fun WordCard(
    word: String,
    definition: String,
    exampleSentence: String,
    tier: String,
    rarityScore: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = when (tier) {
        "Academic" -> TierAcademic
        "Elite" -> TierElite
        "Professional" -> TierProfessional
        else -> TextMuted
    }
    // Normal word card
    val borderColor = MaterialTheme.colorScheme.primary
    if (!selected) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = tier.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = tierColor,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier
                        .border(1.dp, tierColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = definition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = exampleSentence,
                style = MaterialTheme.typography.bodySmall,
                color = midBrown,
                fontStyle = FontStyle.Italic,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                for (i in 0 until 10) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (i < rarityScore) mainGold.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.onBackground
                            )
                    )
                    if (i < 9) Spacer(Modifier.width(3.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "rarity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    // Enter sentence word card
    else {
        FlipCardTransition()
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = word,
//                style = MaterialTheme.typography.headlineLarge,
//                color = MaterialTheme.colorScheme.onBackground,
//            )
//
//
//    }
}}
// ─── Stat Card ───────────────────────────────────────────────────

@Composable
fun StatCard(
    label: String,
    value: String,
    subtext: String? = null,
    subtextColor: Color = TextMuted,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface900)
            .border(1.dp, Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = TextMuted,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        if (subtext != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = subtextColor
            )
        }
    }
}
@Composable
fun FlipCardTransition() {

}
