package com.mlab.knockme.main_feature.presentation.chats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mlab.knockme.profile_feature.presentation.components.TitleInfo
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme

@Composable
fun ChatBusInfoScreen() {
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Bus Info")
        LoadChatList(chatList =
        listOf(
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "fgnfdjgnfjdnhgffgnhngnhfjknh nhfhnfghfghfgjhihi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi")
        ))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsChatB() {
    KnockMETheme {
        ChatBusInfoScreen()
    }
}