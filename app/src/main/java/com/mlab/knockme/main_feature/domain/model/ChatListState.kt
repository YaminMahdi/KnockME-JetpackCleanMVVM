package com.mlab.knockme.main_feature.domain.model

import com.mlab.knockme.auth_feature.domain.model.ProgramInfo

data class ChatListState(
    val chatList: List<Msg> = emptyList(),
    val programs: List<ProgramInfo> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean = false,
    val isSearchLoading: Boolean = false,
    val loadingText: String = "Loading.. "
)

data class MsgListState(
    val msgList: List<Msg> = emptyList(),
    val msgText: String = ""
)