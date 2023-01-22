package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable

data class ResultInfo(
    val courseId: String? = "",
    val courseTitle: String? = "",
    val customCourseId: String? = "",
    val gradeLetter: String? = "",
    val pointEquivalent: Double = 0.0,
    val totalCredit: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(courseId)
        parcel.writeString(courseTitle)
        parcel.writeString(customCourseId)
        parcel.writeString(gradeLetter)
        parcel.writeDouble(pointEquivalent)
        parcel.writeDouble(totalCredit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultInfo> {
        override fun createFromParcel(parcel: Parcel): ResultInfo {
            return ResultInfo(parcel)
        }

        override fun newArray(size: Int): Array<ResultInfo?> {
            return arrayOfNulls(size)
        }
    }
}
