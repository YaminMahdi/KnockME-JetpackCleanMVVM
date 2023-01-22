package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.himanshoe.charty.combined.model.CombinedBarData
import java.util.ArrayList

data class FullResultInfo(
    var semesterInfo: SemesterInfo? = SemesterInfo(),
    var resultInfo: ArrayList<ResultInfo>? = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        SemesterInfo.createFromParcel(parcel),
        parcel.createTypedArrayList(ResultInfo)
    )

    fun toCombinedBarData()=
        CombinedBarData(
            semesterInfo?.semesterName!![0]+"-${semesterInfo?.semesterYear!!%100}",
            semesterInfo?.sgpa!!.toFloat(),
            semesterInfo?.sgpa!!.toFloat()
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(semesterInfo, flags)
        parcel.writeTypedList(resultInfo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FullResultInfo> {
        override fun createFromParcel(parcel: Parcel): FullResultInfo {
            return FullResultInfo(parcel)
        }

        override fun newArray(size: Int): Array<FullResultInfo?> {
            return arrayOfNulls(size)
        }
    }
}
