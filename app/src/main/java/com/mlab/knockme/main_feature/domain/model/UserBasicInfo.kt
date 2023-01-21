package com.mlab.knockme.main_feature.domain.model

import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.auth_feature.domain.model.PublicInfo

data class UserBasicInfo(
    val publicInfo: PublicInfo = PublicInfo(),
    val privateInfo: PrivateInfoExtended = PrivateInfoExtended(),
    val fullResultInfo: List<FullResultInfo> = emptyList()
)
