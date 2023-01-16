package com.mlab.knockme.main_feature.presentation.chats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.profile_feature.presentation.components.TitleInfo
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme

@Composable
fun ChatPlacewiseScreen(viewModel: MainViewModel= hiltViewModel()) {
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Placewise")
        LoadChatList(chatList =
        listOf(
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", msg = "fgnfdjgnfjdnhgffgnhngnhfjknh nhfhnfghfghfgjhihi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120),
            Msg("Yamin Mahdi","", "hi, I'm mahdi","",120)

        ))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsChatP() {
    KnockMETheme {
        ChatPlacewiseScreen()
    }
}