package com.mlab.knockme.main_feature.domain.model

import com.mlab.knockme.main_feature.domain.model.Msg

data class ChatListState(
    val chatList: List<Msg> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean=false,
    val loadingText: String="Loading.. "
)