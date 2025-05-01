package com.mlab.knockme.main_feature.domain.model

import android.os.Parcelable
import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import kotlinx.parcelize.Parcelize
import java.util.ArrayList
@Parcelize
data class UserBasicInfo(
    val lastUpdatedResultInfo: Long=0,
    val publicInfo: PublicInfo = PublicInfo(),
    val privateInfo: PrivateInfoExtended = PrivateInfoExtended(),
    var fullResultInfo: List<FullResultInfo> = emptyList()
) : Parcelable
