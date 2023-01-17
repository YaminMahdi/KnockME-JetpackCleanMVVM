package com.mlab.knockme.main_feature.domain.repo

import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.model.Msg
import kotlinx.coroutines.flow.StateFlow

interface MainRepo {
    suspend fun getMessages(path: String): StateFlow<List<Msg>>
    suspend fun sendMessages(path: String, msg: Msg): Boolean
    suspend fun deleteMessage(path: String, id: String): Boolean
    fun getChatProfiles(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    )
    fun getChats(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    )
    fun getUserProfileInfo(
        id: String,
        Success: (userProfile: UserProfile) -> Unit,
        Failed: (msg:String) -> Unit
    )

    fun getUserBasicInfo(
        id: String,
        Success: (userBasicInfo: UserBasicInfo) -> Unit,
        Failed: (msg:String) -> Unit
    )

    fun getOrCreateUserProfileInfo(
        id: String,
        Success: (Msg) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg:String) -> Unit
    )

}