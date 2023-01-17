package com.mlab.knockme.auth_feature.domain.model

import com.mlab.knockme.auth_feature.data.data_source.dto.CourseInfoDto
import com.mlab.knockme.auth_feature.data.data_source.dto.LiveResultInfoDto
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo

data class UserProfile(
    var token: String="",
    val publicInfo: PublicInfo=PublicInfo(),
    val privateInfo: PrivateInfoExtended=PrivateInfoExtended(),
    val paymentInfo: PaymentInfo=PaymentInfo(),
    val courseInfo: List<CourseInfo> = emptyList(),
    val liveResultInfo: List<LiveResultInfo> = emptyList()
)
{
    fun toUserBasicInfo() =
        UserBasicInfo(publicInfo, privateInfo)
}