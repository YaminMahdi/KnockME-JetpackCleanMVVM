package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgramInfo(
    val programId: String = "", // 17
    val id: Int = 0, // 8
    val name: String = "", // Master of Science in Management Information System
    val shortName: String = "", // M.S. in MIS
    val typeId: Int = 0, // 2
    val typeName: String = "" // MASTERS
): Parcelable

data class ProgramList(
    val list: List<ProgramInfo> = listOf()
)
