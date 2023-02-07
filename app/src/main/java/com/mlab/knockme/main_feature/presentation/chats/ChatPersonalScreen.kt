package com.mlab.knockme.main_feature.presentation.chats

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.domain.model.ChatListState
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toDateTime
import com.mlab.knockme.core.util.toDayPassed
import com.mlab.knockme.main_feature.presentation.ChatInnerScreens
import com.mlab.knockme.main_feature.presentation.MainScreens
import com.mlab.knockme.main_feature.presentation.profile.TitleInfo
import com.mlab.knockme.ui.theme.*

@Composable
fun ChatPersonalScreen(
    navController: NavHostController,
    viewModel: MainViewModel= hiltViewModel()
) {
    val context: Context =LocalContext.current
    val state by viewModel.state.collectAsState()
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    //val preferencesEditor = sharedPreferences.edit()
    val myId = sharedPreferences.getString("studentId",null)
    LaunchedEffect(key1 = "1"){
        //pop backstack
        if(!state.isSearchActive&&state.searchText.length<2){
            viewModel.getChatProfiles("personalMsg/$myId/profiles"){
                Toast.makeText(context, "Chat couldn't be loaded- $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Personal")
        SearchBox(state,viewModel)
        if(state.isSearchActive)
            ProgressBar()
        else {
            Spacer(modifier = Modifier.size(17.dp))
        }
        LoadChatList(state,navController,myId!!)

//        LoadChatList(chatList =
//        listOf(
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "fgnfdjgnfjdnhgffgnhngnhfjknh nhfhnfghfghfgjhihi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi")
//        ))
    }
    if(state.isSearchActive) {
    Box(modifier = Modifier
        .fillMaxSize()

    ) {
        Text(
            text = state.loadingText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkerButtonBlue)
                .padding(16.dp)
        )
    }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(state: ChatListState, viewModel: MainViewModel) {
    var data by remember { mutableStateOf(state.searchText) }

    OutlinedTextField(
        value = data,
        placeholder = { Text("Type Student ID.") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "search",
                modifier = Modifier.padding(start=15.dp)
            )},
        shape = RoundedCornerShape(30.dp),
        colors= searchFieldColors(),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onValueChange = {
            data = it
            viewModel.searchUser(data,
                {loadingMsg ->

                },{errorMsg ->

                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchFieldColors() =
    TextFieldDefaults.textFieldColors(
        textColor = DeepBlueLess,
        containerColor = DeepBlueLess,
        cursorColor = AquaBlue,
        placeholderColor = DeepBlueLess,
        focusedIndicatorColor =  DarkerButtonBlue,
        unfocusedIndicatorColor = DarkerButtonBlue,
        focusedLeadingIconColor = TextWhite,
        unfocusedLeadingIconColor = LessWhite
    )

@Composable
private fun ProgressBar(){
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp)
                .height(7.dp)
            ,
            color = BlueViolet3,
            trackColor = DeepBlueLess,
            strokeCap= StrokeCap.Round
        )
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadChatList(state: ChatListState,navController: NavHostController,myId: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        items(state.chatList) { proView ->
            ChatView(
                proView,
                modifier = Modifier
                    .animateItemPlacement(),
                state.searchText
            ){ id ->
                if(state.searchText.length<2) {
                    when (navController.currentDestination?.route) {
                        MainScreens.CtPersonalScreen.route -> {
                            val path = "personalMsg/$myId/chats/$id"
                            navController.navigate(ChatInnerScreens.MsgScreen.route+"path=$path&id=$id")
                        }
                        MainScreens.CtPlacewiseScreen.route ->
                            navController.navigate(ChatInnerScreens.MsgScreen.route+"{placeMsg/$id/chats}/{$id}")
                        MainScreens.CtBusInfoScreen.route ->
                            navController.navigate(ChatInnerScreens.MsgScreen.route+"{busMsg/$id/chats}/{$id}")
                    }

                }
                else
                    navController.navigate(ChatInnerScreens.UserProfileScreen.route+id)
            }
        }
    }
}

@Composable
fun ChatView(
    proView: Msg,
    modifier: Modifier,
    searchText: String = "",
    onClick: (id: String) -> Unit,
) {
    Row(
        verticalAlignment=Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = Color.White),
                onClick = {
                    onClick.invoke(proView.id!!)
                }
            )
    ) {
        SubcomposeAsyncImage(
            model = proView.pic,
            contentDescription = proView.nm,
            modifier = Modifier
                .height(100.dp)
                .aspectRatio(1f)
                .padding(10.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(DarkerButtonBlue)
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        color = AquaBlue,
                        modifier = Modifier
                            .padding(25.dp)
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    SubcomposeAsyncImageContent(
                        painter = painterResource(id = R.drawable.ic_profile),
                        alpha = .7F,
                        modifier = Modifier
                            .padding(10.dp)
                    )}
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)

        ) {
            Text(
                text = proView.nm!!,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
            Text(
                text = proView.msg!!,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .padding(vertical = 5.dp)
            )
            Text(
                text =
                if (searchText.length<2) proView.time?.toDateTime()!!
                else proView.time?.toDayPassed()!!,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsProfile() {
    KnockMETheme {
        ChatPersonalScreen(rememberNavController(),hiltViewModel())
    }
}