package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable

data class PublicInfo(
    var id: String? ="",
    var nm: String? ="",
    var progShortName: String? ="",
    var batchNo: Int=0,
    var cgpa: Double=0.0,
    var lastUpdated: Long=0L
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nm)
        parcel.writeString(progShortName)
        parcel.writeInt(batchNo)
        parcel.writeDouble(cgpa)
        parcel.writeLong(lastUpdated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PublicInfo> {
        override fun createFromParcel(parcel: Parcel): PublicInfo {
            return PublicInfo(parcel)
        }

        override fun newArray(size: Int): Array<PublicInfo?> {
            return arrayOfNulls(size)
        }
    }
}
