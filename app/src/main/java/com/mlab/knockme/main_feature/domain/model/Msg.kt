package com.mlab.knockme.main_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Msg(
    val id: String?="",
    val nm: String?="",
    val msg: String?="",
    val pic: String?="",
    val time: Long?=0L
) : Parcelable