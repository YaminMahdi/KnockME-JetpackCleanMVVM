package com.mlab.knockme.auth_feature.data.data_source.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyHadithDto(
    val id: String = "",
    val b: String = "",
    val bn: String = "",
    val e: String = "",
    val en: String = "",
    val ref: String = "",
    val src: String = "",
    val t: String = ""
): Parcelable
