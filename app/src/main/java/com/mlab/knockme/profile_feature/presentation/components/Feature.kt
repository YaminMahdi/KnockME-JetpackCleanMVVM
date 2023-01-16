package com.mlab.knockme.profile_feature.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class Feature(
    val title: String,
    val info: String,
    val lightColor: Color,
    val mediumColor: Color,
    val darkColor: Color
)
