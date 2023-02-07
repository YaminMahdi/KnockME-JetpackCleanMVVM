package com.mlab.knockme.main_feature.presentation.messages

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toDateTime
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.MsgListState
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.presentation.ChatInnerScreens
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.main.BackBtn
import com.mlab.knockme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MsgViewScreen(path: String, id: String, navController: NavHostController) {

    val viewModel: MainViewModel = hiltViewModel()
    val context: Context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    //val preferencesEditor = sharedPreferences.edit()
    val myId = sharedPreferences.getString("studentId",null)
    val state by viewModel.msgState.collectAsState()
    val myBasicInfo by viewModel.userBasicInfo.collectAsState()

    viewModel.getMeg(path){
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }
    viewModel.getUserBasicInfo(myId!!)
//    val paddingValue = WindowInsets.ime.getBottom(LocalDensity.current)
//    LaunchedEffect(key1 = state.msgList) {
//        msgList = state.msgList
//    }
    Scaffold(
        topBar = { MsgTopBar(navController, id)},
    ){
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(DeepBlue)
        ) {
            LoadMsgList(
                state = state,
//                listOf(
//                    Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
//                    Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
//                    Msg("193","Yamin Mahdi", "hi, I'm mahdi hi, I'm mahdi hi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdi","",46238423),
//                    Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
//                    Msg("193","Yamin Mahdi", "hi, I'm mahdi","",46238423),
//                    Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
//                    Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423)
//                ),
                modifier = Modifier
                    .weight(1f)
                ,navController,myId
            )
            SendMsgBar(
                state = state,
                viewModel = viewModel,
                path = path,
                myBasicInfo = myBasicInfo,
                context = context
            )
        }
    }


    
}

@Composable
fun MsgTopBar(navController: NavHostController, id: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlueLess)
            .padding(5.dp)
    ) {
        BackBtn(navController)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .bounceClick()
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = Color.White),
                    onClick = {
                        navController.navigate(ChatInnerScreens.UserProfileScreen.route + id)
                    }
                )
        ) {
            ImgView(
                img = "",
                modifier = Modifier
                    .height(70.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50.dp))


            )
            Text(
                text = "Ahmad Umar Mahdi",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth(.8f)
                ,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
                )
        }
    }
}

@Composable
fun ImgView(img: String,modifier: Modifier) {
    SubcomposeAsyncImage(
        model = img,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f)
            .background(DarkerButtonBlue)
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = AquaBlue,
                    modifier = Modifier
                        .padding(20.dp)
                )
            }
            is AsyncImagePainter.State.Error -> {
                SubcomposeAsyncImageContent(
                    painter = painterResource(id = R.drawable.ic_profile),
                    alpha = .7F,
                    modifier = Modifier
                        .padding(6.dp)
                )}
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }

}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadMsgList(
    state: MsgListState,
    modifier: Modifier,
    navController: NavHostController,
    myId: String
) {
    //var lst by remember { mutableStateOf(msgList) }
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 10.dp),
        reverseLayout = true
    ) {
        items(state.msgList) { msg ->
            if(msg.id==myId)
                MsgViewRight(
                    msg,
                    modifier = Modifier.animateItemPlacement()
                ) { id ->
                    navController.navigate(ChatInnerScreens.UserProfileScreen.route+id)
                }
            else
                MsgViewLeft(
                    msg,
                    modifier = Modifier.animateItemPlacement()
                ) { id ->
                    navController.navigate(ChatInnerScreens.UserProfileScreen.route+id)
                }
        }
    }
}

@Composable
fun MsgViewLeft(msg: Msg,modifier: Modifier,onClick:(id: String)->Unit) {
    Row() {
        ImgView(img = msg.pic!!, modifier = modifier
            .height(40.dp)
            .padding(top = 5.dp)
            .bounceClick()
            .clip(
                RoundedCornerShape(
                    topStart = 50.dp,
                    topEnd = 20.dp,
                    bottomStart = 50.dp,
                    bottomEnd = 40.dp
                )
            )
            .clickable {
                onClick.invoke(msg.id!!)
            }
        )
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.7f)
            ){
                Column(modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(horizontal = 5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 7.dp,
                            topEnd = 14.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .background(DeepBlueLess)
                    .padding(8.dp)
                ) {
                    Text(
                        text = msg.nm!!,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .bounceClick()
                            .clip(RoundedCornerShape(2.dp))
                            .clickable {
                                onClick.invoke(msg.id!!)
                            }
                    )
                    Text(
                        text = msg.msg!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextBlue
                    )

                }
            }
            Text(
                modifier = Modifier
                    .alpha(.7f)
                    .padding(start = 6.dp)
                    .padding(2.dp),
                text = msg.time?.toDateTime()!!,
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@Composable
fun MsgViewRight(msg: Msg,modifier: Modifier,onClick:(id: String)->Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth(.7f)
            ){
                Column(modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(horizontal = 5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 7.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .background(DeepBlueMoreLess)
                    .padding(8.dp)
                ) {
                    Text(
                        text = msg.nm!!,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .bounceClick()
                            .clip(RoundedCornerShape(2.dp))
                            .clickable {
                                onClick.invoke(msg.id!!)
                            }
                    )
                    Text(
                        text = msg.msg!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextBlue
                    )

                }
            }
            Text(
                modifier = Modifier
                    .alpha(.7f)
                    .padding(start = 6.dp)
                    .padding(2.dp),
                text = msg.time?.toDateTime()!!,
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }
        ImgView(img = msg.pic!!, modifier = Modifier
            .height(40.dp)
            .padding(top = 5.dp)
            .bounceClick()
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 50.dp,
                    bottomStart = 40.dp,
                    bottomEnd = 50.dp
                )
            )
            .clickable {
                onClick.invoke(msg.id!!)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMsgBar(
    state: MsgListState,
    viewModel: MainViewModel,
    path: String,
    myBasicInfo: UserBasicInfo,
    context: Context
) {
    var data by remember { mutableStateOf(state.msgText) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(DeepBlueLess)
            .padding(5.dp)
            .imePadding()
    ){
        OutlinedTextField(
            value = data,
            placeholder = { Text("Type a message..",Modifier.padding(top=3.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(30.dp),
            textStyle = TextStyle(lineHeight=16.sp, fontFamily = ubuntu),
            colors= searchFieldColors(),
            modifier = Modifier
                .weight(1f)
                .padding(7.dp),
            onValueChange = { data = it },
            maxLines = 2
        )
        Icon(
            Icons.Rounded.Send,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(55.dp)
                .bounceClick()
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = Color.Transparent),
                    onClick = {
                        viewModel.sendMsg(
                            path,
                            Msg(
                                id = myBasicInfo.publicInfo.id,
                                nm = myBasicInfo.publicInfo.nm,
                                msg = data,
                                pic = myBasicInfo.privateInfo.pic,
                                time = System.currentTimeMillis()
                            )
                        ) {
                            Toast
                                .makeText(context, "Couldn't send message- $it", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
                .padding(10.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchFieldColors() =
    TextFieldDefaults.textFieldColors(
        textColor = TextBlue,
        containerColor = DeepBlueMoreLess,
        cursorColor = AquaBlue,
        placeholderColor = DeepBlueLess,
        focusedIndicatorColor =  Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = TextWhite,
        unfocusedLeadingIconColor = LessWhite
    )

@Preview(showBackground = true)
@Composable
fun PreviewsMsgViewScreen() {
    KnockMETheme {
        //MsgViewScreen(it.arguments?.getString("id"), navController)
    }
}