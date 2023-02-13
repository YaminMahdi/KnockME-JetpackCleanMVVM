package com.mlab.knockme.main_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Msg(
    var id: String?="",
    var nm: String?="",
    var msg: String?="",
    var pic: String?="",
    var time: Long?=0L
) : Parcelable