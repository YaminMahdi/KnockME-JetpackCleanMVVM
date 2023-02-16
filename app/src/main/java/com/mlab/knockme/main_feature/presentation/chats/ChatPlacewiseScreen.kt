package com.mlab.knockme.main_feature.presentation.chats

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mlab.knockme.R
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.profile.InfoDialog
import com.mlab.knockme.main_feature.presentation.profile.TitleInfo
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.DeepBlueMoreLess
import com.mlab.knockme.ui.theme.KnockMETheme

@Composable
fun ChatPlacewiseScreen(
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
    LaunchedEffect(key1 = "2"){
        //pop backstack
        viewModel.getChatProfiles("groupMsg/placewise/profiles"){
            Toast.makeText(context, "Chat couldn't be loaded- $it", Toast.LENGTH_SHORT).show()
        }
    }
    InfoDialog(viewModel, context, myId, navController)
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Placewise"){
            viewModel.setInfoDialogVisibility(true)
        }
        Separator()
        LoadChatList(state,navController,myId)
    }
}

@Composable
fun Separator() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top= 5.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DeepBlueMoreLess)
                .padding(1.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewsChatP() {
    KnockMETheme {
       // ChatPlacewiseScreen()
    }
}