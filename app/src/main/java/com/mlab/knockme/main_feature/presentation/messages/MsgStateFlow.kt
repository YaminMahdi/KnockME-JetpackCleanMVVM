package com.mlab.knockme.main_feature.presentation.messages

import com.mlab.knockme.main_feature.domain.model.Msg

data class MsgStateFlow(
    val msgList: List<Msg> = emptyList(),
    val path: String ="",

)
