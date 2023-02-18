package com.mlab.knockme.main_feature.presentation.chats

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
import com.mlab.knockme.main_feature.presentation.profile.InfoDialog
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
    val myId = sharedPreferences.getString("studentId","")!!
    val showHadith by viewModel.showHadith.collectAsState()
    LaunchedEffect(key1 = state.isSearchActive){
        //pop backstack
        if(!state.isSearchActive){
            viewModel.getChatProfiles("personalMsg/$myId/profiles"){
                Log.d("TAG", "ChatPersonalScreen: $it")
                Looper.prepare()
                Toast.makeText(context, "Chat couldn't be loaded", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
        if(showHadith){
            viewModel.getRandomHadith {
                Looper.prepare()
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                Looper.loop()            }
            viewModel.setShowHadith(false)
        }

    }
//    BackHandler {
//        val activity= context as Activity
//        activity.finish()
//    }
    HadithDialog(viewModel)
    InfoDialog(viewModel, context, myId, navController)
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Personal"){
            viewModel.setInfoDialogVisibility(true)
        }
        SearchBox(state,viewModel)
        if(state.isSearchLoading)
            ProgressBar()
        else {
            Spacer(modifier = Modifier.size(17.dp))
        }
        LoadChatList(state,navController,myId)

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
    CustomToast(state.isSearchLoading, state.loadingText)
}

@Composable
fun CustomToast(isLoading: Boolean,loadingText: String) {
    if(isLoading) {
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Text(
                text = loadingText,
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

    Row{
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
                .weight(1f)
                .padding(start = 16.dp, end = if (state.isSearchActive) 0.dp else 16.dp),
            onValueChange = {
                data = it
                viewModel.searchUser(data)
            }
        )
        AnimatedVisibility(visible = state.isSearchActive) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .size(55.dp)
                    .padding(3.dp)
                    .bounceClick()
                    .clip(RoundedCornerShape(30.dp))
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(color = Color.White),
                        onClick = {
                            viewModel.setSearchActive(false)
                            data = ""
                        }
                    )
                    .padding(horizontal = 13.dp)
            )
        }
    }

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
    ){
        items(state.chatList) { proView ->
            ChatView(
                proView,
                modifier = Modifier
                    .animateItemPlacement(),
                state.isSearchActive
            ){ id ->
                if(!state.isSearchActive) {
                    when (navController.currentDestination?.route) {
                        MainScreens.CtPersonalScreen.route -> {
                            val path = "personalMsg/$myId/"
                            navController.navigate(ChatInnerScreens.MsgScreen.route+"path=$path&id=$id")
                        }
                        MainScreens.CtPlacewiseScreen.route ->
                            navController.navigate(ChatInnerScreens.MsgScreen.route+"path=groupMsg/placewise/&id=$id")
                        MainScreens.CtBusInfoScreen.route ->
                            navController.navigate(ChatInnerScreens.MsgScreen.route+"path=groupMsg/busInfo/&id=$id")
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
    isSearchActive: Boolean,
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
                if (!isSearchActive) proView.time?.toDateTime()!!
                else proView.time?.toDayPassed()!!,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
        
    }
}

@Composable
fun HadithDialog(viewModel: MainViewModel) {
    val dialogVisibility by viewModel.dialogVisibility.collectAsState()
    val hadith by viewModel.hadith.collectAsState()
    //val dialogVisibility by remember { mutableStateOf(true) }
//    val hadithDto = DailyHadithDto(
//        id = "0",
//        b = "আবদুল্লাহ ইবনু ’আমর (রাঃ) থেকে বর্ণিত,",
//        bn = "এক ব্যাক্তি রাসূল সাল্লাল্লাহু আলাইহি ওয়াসাল্লাম-কে জিজ্ঞাসা করল, ‘ইসলামের কোন্ কাজ সবচাইতে উত্তম?’ তিনি বললেনঃ তুমি লোকদের আহার করাবে এবং পরিচিত-অপরিচিত নির্বিশেষে সকলকে সালাম দিবে।",
//        e = "Narrated 'Abdullah bin 'Amr:",
//        en = "A person asked Allah's Messenger (sallallahu 'alaihi wa sallam). \"What (sort of) deeds in Islam are good?\" He replied, \"To feed (the poor) and greet those whom you know and those whom you don't know.\",",
//        ref = "Sahih al-Bukhari, 28",
//        src = "https://www.hadithbd.com/hadith/link/?id=30",
//        t = "h"
//    )
    var lang by remember { mutableStateOf("EN") }
    val uriHandler = LocalUriHandler.current

    if (dialogVisibility) {
        Dialog(
            onDismissRequest = { viewModel.setDialogVisibility(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueViolet0)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if(hadith.t=="h") "Read A Hadith" else "Read From Quran",
                        style = MaterialTheme.typography.headlineLarge,
                        color = ButtonBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 25.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if(lang=="EN") hadith.b else hadith.e,
                        fontFamily = if(lang=="EN") pappri else ubuntu,
                        color = Limerick3,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = if(lang=="EN") hadith.bn else hadith.en,
                        fontFamily = if(lang=="EN") pappri else ubuntu,
                        textAlign = TextAlign.Justify,
                        color = Neutral30,
                        fontSize = if(lang=="EN") 19.sp else 17.5.sp,
                        lineHeight = if(lang=="EN") 26.sp else 24.sp,
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )
                    Text(
                        text = hadith.ref,
                        color = Limerick3,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )
                    Text(
                        text = "(Open Source)",
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        color = Neutral50,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .offset(x = (-5).dp)
                            .clip(RoundedCornerShape(5.dp))
                            .clickable {
                                var link = hadith.src
                                if (link.contains("http")) {
                                    if (hadith.t == "q" && lang == "BN")
                                        link = link.replace("bn", "en")
                                    uriHandler.openUri(link)
                                }
                            }
                            .padding(5.dp)
                    )
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Button(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .bounceClick()
                            ,
                            onClick = {
                                viewModel.setDialogVisibility(false)
                            },
                            shape= RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor= ButtonBlue)
                        ) {
                            Text(text = "DONE", color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = ubuntu)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp, end = 7.dp)
                        .padding(10.dp)
                        .size(40.dp)
                        .aspectRatio(1f)
                        .align(Alignment.TopEnd)
                        .bounceClick()
                        .clip(CircleShape)
                        .background(ButtonBlue)
                        .clickable {
                            lang = if (lang == "EN") "BN" else "EN"
                        }
                ) {
                    Text(
                        text = lang,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ubuntu,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsProfile() {
    KnockMETheme {
        //ChatPersonalScreen(rememberNavController(),hiltViewModel())
        //HadithDialog()
    }
}