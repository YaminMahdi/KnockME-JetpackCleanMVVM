package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.LiveResultInfo

data class LiveResultInfoDto(
    val mid1: Double,
    val mid2: Double,
    val q1: Double,
    val q2: Double,
    val q3: Double,
    val quiz: Double
) {
    fun toLiveResultInfo(semesterId: String) =
        LiveResultInfo(
            mid1 = mid1,
            mid2 = mid2,
            q1 = q1,
            q2 = q2,
            q3 = q3,
            quiz = quiz,
            semesterId = semesterId
        )
}