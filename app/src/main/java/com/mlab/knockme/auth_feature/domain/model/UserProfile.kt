package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val token: String ="",
    val lastUpdatedPaymentInfo: Long=0,
    val lastUpdatedRegCourseInfo: Long=0,
    val lastUpdatedLiveResultInfo: Long=0,
    val lastUpdatedClearanceInfo: Long=0,
    val lastUpdatedResultInfo: Long=0,
    var publicInfo: PublicInfo = PublicInfo(), //aida ase
    var privateInfo: PrivateInfoExtended = PrivateInfoExtended(),
    val paymentInfo: PaymentInfo = PaymentInfo(),
    val regCourseInfo: List<CourseInfo> = emptyList(),
    val liveResultInfo: List<LiveResultInfo> = emptyList(),
    val fullResultInfo: List<FullResultInfo> = emptyList(),
    val clearanceInfo: List<ClearanceInfo> = emptyList()
) : Parcelable {
    fun toUserBasicInfo() =
        UserBasicInfo(
            lastUpdatedResultInfo = lastUpdatedResultInfo,
            publicInfo = publicInfo,
            privateInfo = privateInfo,
            fullResultInfo = fullResultInfo
        )
}