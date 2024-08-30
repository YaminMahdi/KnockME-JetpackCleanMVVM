@file:OptIn(ExperimentalFoundationApi::class)

package com.mlab.knockme.main_feature.presentation.messages

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
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
import com.mlab.knockme.core.util.isNotEmpty
import com.mlab.knockme.core.util.toDateTime
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.presentation.InnerScreens
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.main.BackBtn
import com.mlab.knockme.ui.theme.*

@Composable
fun MsgViewScreen(path: String, id: String,
                  navController: NavHostController,viewModel: MainViewModel = hiltViewModel()) {

    val context: Context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    //val preferencesEditor = sharedPreferences.edit()
    val myId = sharedPreferences.getString("studentId",null)


//    val paddingValue = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(key1 = "1") {
        viewModel.getMeg(path+"chats/$id"){
            Looper.prepare()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Looper.loop()
        }
        viewModel.getTarBasicInfo(id)
        viewModel.getMyBasicInfo(myId!!)
    }

    Scaffold(
        topBar = { MsgTopBar(navController, id, viewModel)},
    ){
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(DeepBlue)
        ) {
            LoadMsgList(
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                ,navController,myId!!
            )
            SendMsgBar(
                viewModel = viewModel,
                path = path,
                context = context,
                id,myId
            )
        }
    }


    
}

@Composable
fun MsgTopBar(navController: NavHostController, id: String, viewModel: MainViewModel) {
    val mutableInteractionSource by remember { mutableStateOf(MutableInteractionSource()) }
    val tarBasicInfo by viewModel.tarBasicInfo.collectAsState()
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
                    interactionSource = mutableInteractionSource,
                    indication = ripple(color = Color.White),
                    onClick = {
                        tarBasicInfo.publicInfo.nm.isNotEmpty {
                            navController.navigate(InnerScreens.UserProfile(id))
                        }
                    }
                )
        ) {
            ImgView(
                img = tarBasicInfo.privateInfo.pic!!,
                modifier = Modifier
                    .height(70.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
            Text(
                text = tarBasicInfo.publicInfo.nm,
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

@Composable
fun LoadMsgList(
    viewModel: MainViewModel,
    modifier: Modifier,
    navController: NavHostController,
    myId: String
) {
    //var lst by remember { mutableStateOf(state.msgList) }
    val msgList by viewModel.msgList.collectAsState()
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 10.dp),
        reverseLayout = true
    ) {
        items(msgList) { msg ->
            if(msg.id==myId)
                MsgViewRight(
                    msg,
                    modifier = Modifier.animateItem()
                ) { id ->
                    if(id.isNotEmpty())
                        navController.navigate(InnerScreens.UserProfile(id))
                    else
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                }
            else
                MsgViewLeft(
                    msg,
                    modifier = Modifier.animateItem()
                ) { id ->
                    if(id.isNotEmpty())
                        navController.navigate(InnerScreens.UserProfile(id))
                    else
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

@Composable
fun MsgViewLeft(msg: Msg,modifier: Modifier,onClick:(id: String)->Unit) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    Row {
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
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.7f)
            ){
                Column(modifier = Modifier
                    .padding(start = 6.dp, top = 6.dp, bottom = 3.dp)
                    .bounceClick()
                    .clip(
                        RoundedCornerShape(
                            topStart = 7.dp,
                            topEnd = 14.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            clipboardManager.setText(AnnotatedString(msg.msg!!))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(context, "Text Copied", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onDoubleClick = {}
                    )
                    .background(DeepBlueMoreLess)
                    .padding(8.dp)
                ) {
                    Text(
                        text = msg.nm!!,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 10.sp,
                        modifier = Modifier.clip(RoundedCornerShape(2.dp))
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
                    .padding(start = 12.dp, bottom = 2.dp),
                text = msg.time?.toDateTime()!!,
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MsgViewRight(msg: Msg,modifier: Modifier,onClick:(id: String)->Unit) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
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
                    .padding(end = 6.dp, top = 6.dp, bottom = 3.dp)
                    .bounceClick()
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 7.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            clipboardManager.setText(AnnotatedString(msg.msg!!))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(context, "Text Copied", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onDoubleClick = {}
                    )
                    .background(BlueViolet3.copy(alpha = .7f))
                    .padding(8.dp)
                ) {
                    Text(
                        text = msg.nm!!,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 10.sp,
                        modifier = Modifier.clip(RoundedCornerShape(2.dp))
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
                    .padding(end = 12.dp, bottom = 2.dp),
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


@Composable
fun SendMsgBar(
    viewModel: MainViewModel,
    path: String,
    context: Context,
    id: String,
    myId: String
) {
    var data by remember { mutableStateOf("") }
    val myBasicInfo by viewModel.myBasicInfo.collectAsState()
    val tarBasicInfo by viewModel.tarBasicInfo.collectAsState()
    val mutableInteractionSource by remember { mutableStateOf(MutableInteractionSource()) }
    val myPath = path+"chats/$id"
    val myProfilePath = path+"profiles/$id"
    var tarPath : String?= null
    var tarProfilePath: String?= null

    if(!myPath.contains("groupMsg")) {
        tarPath = "personalMsg/$id/chats/$myId"
        tarProfilePath = "personalMsg/$id/profiles/$myId"
    }
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
            Icons.AutoMirrored.Rounded.Send,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(55.dp)
                .bounceClick()
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = mutableInteractionSource,
                    indication = ripple(color = Color.Transparent),
                    onClick = {
                        val time = System.currentTimeMillis()
                        val msg = Msg(
                            id = myBasicInfo.publicInfo.id,
                            nm = myBasicInfo.publicInfo.nm,
                            msg = data,
                            pic = myBasicInfo.privateInfo.pic,
                            time = time
                        )
                        val tarProfile = Msg(
                            id = tarBasicInfo.publicInfo.id,
                            nm = tarBasicInfo.publicInfo.nm,
                            msg = data,
                            pic = tarBasicInfo.privateInfo.pic,
                            time = time
                        )
                        viewModel.sendMsg(myPath, msg) {
                            Log.d("TAG", "ChatPersonalScreen: $it")
                            Looper.prepare()
                            Toast
                                .makeText(context, "Couldn't send message", Toast.LENGTH_SHORT)
                                .show()
                            Looper.loop()
                        }
                        viewModel.refreshProfileInChats(myProfilePath, tarProfile) {
                            Log.d("TAG", "ChatPersonalScreen: $it")
                            Looper.prepare()
                            Toast
                                .makeText(context, "Couldn't send message", Toast.LENGTH_SHORT)
                                .show()
                            Looper.loop()
                        }
                        if (tarPath != null && id != myId) {
                            viewModel.sendMsg(tarPath, msg) {
                                Log.d("TAG", "ChatPersonalScreen: $it")
                                Looper.prepare()
                                Toast
                                    .makeText(context, "Couldn't send message", Toast.LENGTH_SHORT)
                                    .show()
                                Looper.loop()
                            }
                            viewModel.refreshProfileInChats(tarProfilePath!!, msg) {
                                Log.d("TAG", "ChatPersonalScreen: $it")
                                Looper.prepare()
                                Toast
                                    .makeText(context, "Couldn't send message", Toast.LENGTH_SHORT)
                                    .show()
                                Looper.loop()
                            }
                        }
                        data = ""
                    }
                )
                .padding(10.dp)
        )
    }
}

@Composable
fun searchFieldColors() =
    TextFieldDefaults.colors().copy(
        focusedTextColor = TextWhite,
        focusedLabelColor = BlueViolet3,
        unfocusedLabelColor= BlueViolet3,
        focusedIndicatorColor = BlueViolet3,
        unfocusedIndicatorColor = BlueViolet3,
        focusedContainerColor = DeepBlueLess,
        unfocusedContainerColor = DeepBlueLess,
        cursorColor = AquaBlue,
        focusedPlaceholderColor = DeepBlueLess,
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