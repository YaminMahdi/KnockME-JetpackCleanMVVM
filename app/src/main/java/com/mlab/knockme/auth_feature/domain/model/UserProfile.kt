package com.mlab.knockme.auth_feature.domain.model

import com.mlab.knockme.auth_feature.data.data_source.dto.CourseInfoDto
import com.mlab.knockme.auth_feature.data.data_source.dto.LiveResultInfoDto

data class UserProfile(
    var token: String="",
    val publicInfo: PublicInfo=PublicInfo(),
    val privateInfo: PrivateInfo=PrivateInfo(),
    val paymentInfo: PaymentInfo=PaymentInfo(),
    val courseInfo: List<CourseInfo> = emptyList(),
    val liveResultInfo: List<LiveResultInfo> = emptyList(),
)