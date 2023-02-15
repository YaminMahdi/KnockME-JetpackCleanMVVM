package com.mlab.knockme.main_feature.domain.model

data class ChatListState(
    val chatList: List<Msg> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean = false,
    val isSearchLoading: Boolean = false,
    val loadingText: String = "Loading.. "
)

data class MsgListState(
    val msgList: List<Msg> = emptyList(),
    val msgText: String = ""
)