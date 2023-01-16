package com.mlab.knockme.main_feature.presentation.main.components

import androidx.annotation.DrawableRes

data class BottomNavContent(
    val title: String,
    //val icon : ImageVector,
    @DrawableRes val iconId: Int

)