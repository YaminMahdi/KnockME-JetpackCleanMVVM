package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable

data class SemesterInfo(
    var semesterId: String? ="",
    var semesterName: String? ="",
    var semesterYear: Int=0,
    var sgpa: Double= 0.0,
    var creditTaken: Double=0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble()
    ){}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(semesterId)
        parcel.writeString(semesterName)
        parcel.writeInt(semesterYear)
        parcel.writeDouble(sgpa)
        parcel.writeDouble(creditTaken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SemesterInfo> {
        override fun createFromParcel(parcel: Parcel): SemesterInfo {
            return SemesterInfo(parcel)
        }

        override fun newArray(size: Int): Array<SemesterInfo?> {
            return arrayOfNulls(size)
        }
    }
}
