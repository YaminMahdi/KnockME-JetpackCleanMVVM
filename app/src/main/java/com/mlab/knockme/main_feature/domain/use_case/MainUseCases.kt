package com.mlab.knockme.main_feature.domain.use_case

data class MainUseCases (
    val getChatProfiles: GetChatProfiles,
    val getChats: GetChats,
    val getMsg: GetMsg,
    val sendMsg: SendMsg,
    val deleteMsg: DeleteMsg
)