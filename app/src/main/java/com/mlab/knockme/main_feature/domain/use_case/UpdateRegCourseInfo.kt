package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.CourseInfo
import com.mlab.knockme.auth_feature.domain.model.PaymentInfo
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class UpdateRegCourseInfo @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        id: String,
        accessToken: String,
        regCourseInfoList: List<CourseInfo>,
        Success: (regCourseInfoList: List<CourseInfo>) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.updateRegCourseInfo(id, accessToken, regCourseInfoList, Success, Failed)
}