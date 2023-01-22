package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable

data class PrivateInfoExtended(
    var fbId: String? ="",
    var fbLink: String? ="",
    var pic: String? ="",
    var bloodGroup: String?="",
    var email: String?="",
    var permanentHouse: String?="",
    var ip: String? ="",
    var loc: String? ="",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fbId)
        parcel.writeString(fbLink)
        parcel.writeString(pic)
        parcel.writeString(bloodGroup)
        parcel.writeString(email)
        parcel.writeString(permanentHouse)
        parcel.writeString(ip)
        parcel.writeString(loc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrivateInfoExtended> {
        override fun createFromParcel(parcel: Parcel): PrivateInfoExtended {
            return PrivateInfoExtended(parcel)
        }

        override fun newArray(size: Int): Array<PrivateInfoExtended?> {
            return arrayOfNulls(size)
        }
    }
}
