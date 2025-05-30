package com.mlab.knockme.main_feature.presentation.chats

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.ProgramInfo
import com.mlab.knockme.core.util.*
import com.mlab.knockme.main_feature.domain.model.ChatListState
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.presentation.InnerScreens
import com.mlab.knockme.main_feature.presentation.MainScreens
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.profile.InfoBottomSheet
import com.mlab.knockme.main_feature.presentation.profile.TitleInfo
import com.mlab.knockme.main_feature.presentation.route
import com.mlab.knockme.ui.theme.*

@Composable
fun ChatPersonalScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    //val preferencesEditor = sharedPreferences.edit()
//    val myId = sharedPreferences.getString("studentId","")!!
    val user by viewModel.userFullProfileInfo.collectAsStateWithLifecycle()
    val myId = user.publicInfo.id
//    val showHadith by viewModel.showHadith.collectAsStateWithLifecycle()
    var toMain by remember { mutableStateOf(false) }
//    if (state.chatList.size == 1 && state.isSearchLoading) {
//        LocalFocusManager.current.clearFocus()
//    }
    LaunchedEffect(Unit) {
        if (!state.isSearchActive) {
            viewModel.getChatProfiles("personalMsg/$myId/profiles", true) {
                Log.d("TAG", "ChatPersonalScreen: $it")
                context.toast("Chat couldn't be loaded")
            }
        }else
            viewModel.setSearchActive(true)
//        if (showHadith) {
//            viewModel.getRandomHadith {
//                toMain = true
//                context.toast(it)
//            }
//            viewModel.setShowHadith(false)
//        }
    }
    if (toMain)
        navController.navigate(MainScreens.Profile.route)
//    BackHandler {
//        val activity= context as Activity
//        activity.finish()
//    }
    HadithDialog(viewModel)
    InfoBottomSheet(viewModel, context, myId, navController)
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()
    ) {
        AnimatedVisibility(!state.isSearchActive) {
            TitleInfo(title = "Personal", onClick = {
                viewModel.setInfoDialogVisibility(true)
            })
        }
        SearchBox(state, viewModel)
        if (state.isSearchLoading)
            ProgressBar()
        else
            Spacer(modifier = Modifier.size(17.dp))
        LoadChatList(state, navController, myId, viewModel)
    }
    CustomToast(state.isSearchLoading, state.loadingText)
}

@Composable
fun CustomToast(isLoading: Boolean, loadingText: String) {
    if (isLoading) {
        Box(
            modifier = Modifier
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
    var data by rememberSaveable { mutableStateOf(state.searchText) }
    val fm = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    var selectedItem: ProgramInfo? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
    ) {
        OutlinedTextField(
            value = data,
            placeholder = { Text("Type Student ID.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                IconButton(
                    enabled= state.isSearchActive,
                    onClick = {
                        fm.clearFocus()
                        viewModel.setSearchActive(false)
                        data = ""
                    }) {
                        Icon(
                            imageVector = if(state.isSearchActive ) Icons.Rounded.ArrowBackIosNew else Icons.Rounded.Search,
                            contentDescription = "leadingIcon",
                        )
                    }
            },
            shape = RoundedCornerShape(16.dp),
            colors = searchFieldColors(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
//                .padding(start = 16.dp, end = if (state.isSearchActive) 0.dp else 16.dp)
                .onFocusChanged {
                    if(it.isFocused)
                        viewModel.setSearchActive(true)
                }
            ,
            onValueChange = {
                data = it
            }
        )
        AnimatedVisibility(state.isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(IntrinsicSize.Min)
            )
            {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                ) {
                    TextField(
                        value = selectedItem?.shortName.orEmpty(),
                        onValueChange = { },
                        placeholder = { Text("Select Program") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(16.dp),
                        colors = searchFieldColors(),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = LessBlue
                    ) {
                        state.programs.forEach { program ->
                            DropdownMenuItem(
                                text = { Text(program.shortName, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    selectedItem = program
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                colors = MenuDefaults.itemColors(textColor = LessWhite,),
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        fm.clearFocus()
                        viewModel.searchUser(data, selectedItem?.programId)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                        .bounceClick()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PersonSearch,
                        contentDescription = "search",
                        tint = TextWhite,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

//        AnimatedVisibility(visible = state.isSearchActive) {
//            Icon(
//                Icons.Rounded.Close,
//                contentDescription = "",
//                tint = Color.White,
//                modifier = Modifier
//                    .size(55.dp)
//                    .padding(3.dp)
//                    .bounceClick()
//                    .clip(RoundedCornerShape(30.dp))
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = ripple(color = Color.White),
//                        onClick = {
//                            fm.clearFocus()
//                            viewModel.setSearchActive(false)
//                            data = ""
//                        }
//                    )
//                    .padding(horizontal = 13.dp)
//            )
//        }
    }

}

@Composable
fun searchFieldColors() =
    TextFieldDefaults.colors(
        focusedTextColor = TextWhite,
        focusedLabelColor = BlueViolet3,
        unfocusedLabelColor = BlueViolet3,
        focusedIndicatorColor = DeepBlue,
        unfocusedIndicatorColor = DeepBlue,
        focusedContainerColor = LessBlue,
        unfocusedContainerColor = LessBlue,
        cursorColor = AquaBlue,
        focusedPlaceholderColor = DeepBlueLess,
        focusedLeadingIconColor = TextWhite,
        unfocusedLeadingIconColor = LessWhite
    )

@Composable
private fun ProgressBar() {
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp)
                .height(7.dp),
            color = BlueViolet3,
            trackColor = DeepBlueLess,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun LoadChatList(
    state: ChatListState,
    navController: NavHostController,
    myId: String,
    viewModel: MainViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        item {
            AnimatedVisibility(state.chatList.isEmpty() && state.isSearchActive){
                NoDataFound(modifier = Modifier.fillParentMaxSize())
            }
        }
        items(state.chatList) { proView ->
            ChatView(
                proView,
                modifier = Modifier
                    .animateItem(),
                state.isSearchActive
            ) { id ->
                viewModel.updateTarBasicInfo(UserBasicInfo())   //empty
                if (!state.isSearchActive) {
                    when (navController.currentDestination?.route) {
                        MainScreens.ChatPersonal.route ->
                            navController.navigate(InnerScreens.Conversation("personalMsg/$myId/", id))
                        MainScreens.ChatPlaceWise.route ->
                            navController.navigate(InnerScreens.Conversation("groupMsg/placewise/", id))
                        MainScreens.ChatBusInfo.route ->
                            navController.navigate(InnerScreens.Conversation("groupMsg/busInfo/", id))
                    }
                } else
                    navController.navigate(InnerScreens.UserProfile(id))
            }
        }
    }
}

@Composable
fun NoDataFound(
    modifier: Modifier = Modifier,
    title: String = "No Data Found",
    subtitle: String = "There's nothing to show here yet",
    icon: ImageVector = Icons.Rounded.SearchOff
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = BlueViolet2.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = BlueViolet2.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = BlueViolet2.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
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
        verticalAlignment = Alignment.CenterVertically,
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
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White),
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
            val state by painter.state.collectAsStateWithLifecycle()
            when (state) {
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
                    )
                }

                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
        Column(
            modifier = Modifier
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
                    .padding(vertical = 7.dp)
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
    val dialogVisibility by viewModel.dialogVisibility.collectAsStateWithLifecycle()
    val hadith by viewModel.hadith.collectAsStateWithLifecycle()
    var lang by remember { mutableStateOf("EN") }
    val context = LocalContext.current
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
                        text = if (hadith.t == "h") "Read A Hadith" else "Read From Quran",
                        style = MaterialTheme.typography.headlineLarge,
                        color = ButtonBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 25.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (lang == "EN") hadith.b else hadith.e,
                        fontFamily = if (lang == "EN") pappri else ubuntu,
                        color = Limerick3,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = if (lang == "EN") hadith.bn else hadith.en,
                        fontFamily = if (lang == "EN") pappri else ubuntu,
                        textAlign = TextAlign.Justify,
                        color = Neutral30,
                        fontSize = if (lang == "EN") 19.sp else 17.5.sp,
                        lineHeight = if (lang == "EN") 26.sp else 24.sp,
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
                                    context.showCustomTab(link)
                                }
                            }
                            .padding(5.dp)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .bounceClick(),
                            onClick = {
                                viewModel.setDialogVisibility(false)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                        ) {
                            Text(
                                text = "DONE",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontFamily = ubuntu
                            )
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