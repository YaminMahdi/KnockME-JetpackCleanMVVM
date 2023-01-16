package com.mlab.knockme.main_feature.data.data_source

import com.mlab.knockme.main_feature.domain.model.Msg
import kotlinx.coroutines.flow.Flow

interface MsgDao {

    fun getMessages(path: String): Flow<List<Msg>>

    suspend fun deleteMessage(path: String, id: String)

}