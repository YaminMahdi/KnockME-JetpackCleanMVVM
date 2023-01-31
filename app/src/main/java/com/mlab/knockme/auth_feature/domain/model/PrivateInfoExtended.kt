package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivateInfoExtended(
    var fbId: String? ="",
    var fbLink: String? ="",
    var pic: String? ="",
    var bloodGroup: String?="",
    var email: String?="",
    var permanentHouse: String?="",
    var ip: String? ="",
    var loc: String? ="",
) : Parcelable