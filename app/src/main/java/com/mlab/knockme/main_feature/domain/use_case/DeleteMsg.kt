package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.repo.MsgRepo
import javax.inject.Inject

class DeleteMsg@Inject constructor(
    private val repo: MsgRepo
) {
    suspend operator fun invoke(path: String,id: String)= repo.deleteMessage(path, id)
}