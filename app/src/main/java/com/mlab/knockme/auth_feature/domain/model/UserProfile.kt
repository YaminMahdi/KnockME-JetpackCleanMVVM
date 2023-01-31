package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    var token: String ="",
    val lastUpdatedPaymentInfo: Long=0,
    val lastUpdatedRegCourseInfo: Long=0,
    val lastUpdatedLiveResultInfo: Long=0,
    val lastUpdatedResultInfo: Long=0,
    val publicInfo: PublicInfo = PublicInfo(), //aida ase
    val privateInfo: PrivateInfoExtended = PrivateInfoExtended(),
    val paymentInfo: PaymentInfo = PaymentInfo(),
    val courseInfo: ArrayList<CourseInfo> = arrayListOf(),
    val liveResultInfo: ArrayList<LiveResultInfo> = arrayListOf(),
    val fullResultInfo: ArrayList<FullResultInfo> = arrayListOf()  //aida ase

) : Parcelable {
    fun toUserBasicInfo() =
        UserBasicInfo(
            lastUpdatedResultInfo = lastUpdatedResultInfo,
            publicInfo = publicInfo,
            privateInfo = privateInfo,
            fullResultInfo = fullResultInfo
        )
}