package com.mlab.knockme.auth_feature.domain.model

import com.mlab.knockme.main_feature.domain.model.UserBasicInfo

data class UserProfile(
    var token: String="",
    val publicInfo: PublicInfo=PublicInfo(), //aida ase
    val privateInfo: PrivateInfoExtended=PrivateInfoExtended(),
    val paymentInfo: PaymentInfo=PaymentInfo(),
    val courseInfo: List<CourseInfo> = emptyList(),
    val liveResultInfo: List<LiveResultInfo> = emptyList(),
    val fullResultInfo: List<FullResultInfo> = emptyList()  //aida ase

)
{
    fun toUserBasicInfo() =
        UserBasicInfo(publicInfo, privateInfo,fullResultInfo)
}