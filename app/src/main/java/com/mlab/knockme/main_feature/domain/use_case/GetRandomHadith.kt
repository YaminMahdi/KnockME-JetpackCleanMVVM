package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetRandomHadith @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        Success: (hadith: DailyHadithDto) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.getRandomHadith(Success,Failed)
}