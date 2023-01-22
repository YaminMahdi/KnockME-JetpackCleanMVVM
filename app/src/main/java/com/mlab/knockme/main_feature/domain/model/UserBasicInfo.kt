package com.mlab.knockme.main_feature.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.auth_feature.domain.model.SemesterInfo
import java.util.ArrayList

data class UserBasicInfo(
    val publicInfo: PublicInfo? = PublicInfo(),
    val privateInfo: PrivateInfoExtended? = PrivateInfoExtended(),
    val fullResultInfo: ArrayList<FullResultInfo>? = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        PublicInfo.createFromParcel(parcel),
        PrivateInfoExtended.createFromParcel(parcel),
        parcel.createTypedArrayList(FullResultInfo)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(publicInfo, flags)
        parcel.writeParcelable(privateInfo, flags)
        parcel.writeTypedList(fullResultInfo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserBasicInfo> {
        override fun createFromParcel(parcel: Parcel): UserBasicInfo {
            return UserBasicInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserBasicInfo?> {
            return arrayOfNulls(size)
        }
    }
}
