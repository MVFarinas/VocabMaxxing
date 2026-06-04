package com.vocabmaxxing.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vocabmaxxing.app.R

val specialText = FontFamily(Font(R.font.raleway_regular, FontWeight.Normal),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_semibold, FontWeight.SemiBold),
    Font(R.font.raleway_bold, FontWeight.Bold))

val regularText =  FontFamily(Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_italic, FontWeight.Normal))

val Typography = Typography(
    // for words of the day and headings
    headlineLarge = TextStyle(
        fontFamily = specialText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp),
    // for side menu items
    bodyLarge = TextStyle(
        fontFamily = regularText,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    // for most text
    bodyMedium = TextStyle(
        fontFamily = regularText,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    // for lower emphasis text (like rarity)
    bodySmall = TextStyle(
        fontFamily = regularText,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    // for example sentence
    headlineMedium = TextStyle(
        fontFamily = regularText,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    // for word type classification labels (ACADEMIC/PROFESSIONAL/ELITE)
    labelSmall = TextStyle(
        fontFamily = regularText,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )

)