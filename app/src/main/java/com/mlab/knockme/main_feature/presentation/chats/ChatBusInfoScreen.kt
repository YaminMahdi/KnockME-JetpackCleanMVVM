package com.mlab.knockme.main_feature.presentation.chats

import android.content.Context
import android.os.Looper
import android.util.Log
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
import androidx.navigation.NavHostController
import com.mlab.knockme.R
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.profile.InfoDialog
import com.mlab.knockme.main_feature.presentation.profile.TitleInfo
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme

@Composable
fun ChatBusInfoScreen(
    navController: NavHostController,
    viewModel: MainViewModel= hiltViewModel()
) {
    val context: Context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    //val preferencesEditor = sharedPreferences.edit()
    val myId = sharedPreferences.getString("studentId","0")!!
    LaunchedEffect(key1 = "3"){
        //pop backstack
        viewModel.getChatProfiles("groupMsg/busInfo/profiles"){
            Log.d("TAG", "ChatPersonalScreen: $it")
            Looper.prepare()
            Toast.makeText(context, "Chat couldn't be loaded", Toast.LENGTH_SHORT).show()
            Looper.loop()        }
    }
    InfoDialog(viewModel, context, myId, navController)
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Bus Info"){
            viewModel.setInfoDialogVisibility(true)
        }
        Separator()
        LoadChatList(state, navController, myId, viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsChatB() {
    KnockMETheme {
       // ChatBusInfoScreen()
    }
}