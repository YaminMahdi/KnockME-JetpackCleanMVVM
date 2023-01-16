package com.mlab.knockme.main_feature.domain.repo

import com.mlab.knockme.main_feature.domain.model.Msg
import kotlinx.coroutines.flow.StateFlow

interface MsgRepo {
    suspend fun getMessages(path: String): StateFlow<List<Msg>>

    suspend fun sendMessages(path: String, msg: Msg): Boolean


    suspend fun deleteMessage(path: String, id: String): Boolean
}