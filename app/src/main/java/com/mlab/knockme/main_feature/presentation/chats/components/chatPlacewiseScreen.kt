package com.mlab.knockme.main_feature.presentation.chats.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mlab.knockme.R
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.profile_feature.presentation.components.TitleInfo
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme

@Composable
fun ChatPlacewiseScreen(viewModel: MainViewModel= hiltViewModel()) {
    val context: Context = LocalContext.current
    val chatList by viewModel.chatListState.collectAsState()
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    //val preferencesEditor = sharedPreferences.edit()
    val id = sharedPreferences.getString("studentId",null)
    LaunchedEffect(key1 = "2"){
        //pop backstack
        viewModel.getChatProfiles("groupMsg/placewise/profiles"){
            Toast.makeText(context, "Chat couldn't be loaded- $it", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Placewise")
        LoadChatList(chatList = chatList)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsChatP() {
    KnockMETheme {
        ChatPlacewiseScreen()
    }
}