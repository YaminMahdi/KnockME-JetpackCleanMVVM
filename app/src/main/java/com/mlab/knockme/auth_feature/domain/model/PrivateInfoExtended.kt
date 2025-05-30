package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivateInfoExtended(
    val fbId: String? ="",
    val fbLink: String? ="",
    val pic: String? ="",
    val name: String?="",
    val bloodGroup: String?="",
    val email: String?="",
    var permanentHouse: String?="",
    val presentHouse: String?="",
    val sex: String?="",
    val mobile: String?="",
    val notes: String?="",
    val socialNetId: String?="",
    val birthDate: String?="",
    val placeOfBirth: String?="",
    val religion: String?="",
    val fatherMobile: String?="",
    val motherMobile: String?="",
    var ip: String? ="",
    var loc: String? ="",
) : Parcelable