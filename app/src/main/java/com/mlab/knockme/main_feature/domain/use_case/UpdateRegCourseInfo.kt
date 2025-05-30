package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.CourseInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class UpdateRegCourseInfo @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        UserProfile: UserProfile,
        success: (regCourseInfoList: List<CourseInfo>) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.updateRegCourseInfo(UserProfile, success, failed)
}