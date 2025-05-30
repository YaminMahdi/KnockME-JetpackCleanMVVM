package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class SyncPrograms @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke() = repo.syncPrograms()
}