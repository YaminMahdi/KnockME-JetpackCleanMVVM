package com.mlab.knockme.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mlab.knockme.R

val ubuntu = FontFamily(
    listOf(
        Font(R.font.ubuntu_regular, FontWeight.Normal),
        Font(R.font.ubuntu_medium, FontWeight.Medium),
        Font(R.font.ubuntu_bold, FontWeight.Bold),
        Font(R.font.ubuntu_light, FontWeight.Light),
    )
)
val bakbakBold = FontFamily(
    listOf(
        Font(R.font.bakbak_bold, FontWeight.Normal),
        Font(R.font.bakbak_bold, FontWeight.Bold)
    )
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyMedium = TextStyle(
        color = AquaBlue,
        fontFamily = ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp
    ),
    bodyLarge = TextStyle(
        color = AquaBlue,
        fontFamily = ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        color = AquaBlue,
        fontFamily = ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.2.sp
    ),
    headlineLarge = TextStyle(
        color = TextWhite,
        fontFamily = ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.7.sp
    ),
    headlineMedium = TextStyle(
        color = TextWhite,
        fontFamily = ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        color = TextWhite,
        fontFamily = ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    )


    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)