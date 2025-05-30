package com.mlab.knockme.main_feature.presentation.main

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.config.BarChartColorConfig
import com.himanshoe.charty.bar.config.BarChartConfig
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.ChartColor
import com.himanshoe.charty.common.LabelConfig
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.core.util.*
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.presentation.InnerScreens
import com.mlab.knockme.main_feature.presentation.MainScreens
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.chats.CustomToast
import com.mlab.knockme.main_feature.presentation.profile.Ic
import com.mlab.knockme.pref
import com.mlab.knockme.ui.theme.*


@Composable
fun ProfileViewScreen(
    id: String="",
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val userBasicInfo by viewModel.userBasicInfo.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasPrivateInfo by viewModel.hasPrivateInfo.collectAsStateWithLifecycle()
    val loading by viewModel.isResultLoading.collectAsStateWithLifecycle()
    val loadingTxt by viewModel.resultLoadingTxt.collectAsStateWithLifecycle()

    val myId = pref.getString("studentId","").orEmpty()
//    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
//    val hasPrivateInfo  by viewModel.hasPrivateInfo.collectAsStateWithLifecycle()
//    val userBasicInfo  by viewModel.userBasicInfo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        //if(isLoading)
        viewModel.getUserBasicInfo(id){
            if(!id.hasAlphabet()){
                viewModel.updateUserFullResultInfo(
                    publicInfo = it.publicInfo,
                    fullResultInfoList = it.fullResultInfo)
            }
        }
        viewModel.getMyBasicInfo(myId)
    }

    Scaffold(topBar = {
        TopBar(navController){
            if(!id.hasAlphabet())
                viewModel.updateUserFullResultInfo(
                    publicInfo = userBasicInfo.publicInfo,
                    fullResultInfoList = userBasicInfo.fullResultInfo)
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlue)
                .padding(it)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Profile(
                pic = userBasicInfo.privateInfo.pic,
                publicInfo = userBasicInfo.publicInfo,
                isLoading = isLoading,
                navController
            )
            SocialLink(viewModel,userBasicInfo, hasPrivateInfo, navController, id, myId)
            if(!id.hasAlphabet()){
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(weight = 1f, fill = false)

                ) {
                    if(!isLoading&&userBasicInfo.fullResultInfo.isNotEmpty())
                        CGBarChart(
                            barDataList = userBasicInfo.toBarData(),
                            pb = userBasicInfo.publicInfo,
                            navController = navController
                        )
                    //                listOf(
//                    CombinedBarData("F-19", 3.33F, 3.33F),
//                    CombinedBarData("S-20", 3.63F, 3.63F),
//                    CombinedBarData("S-20", 3.73F, 3.73F),
//                    CombinedBarData("F-20", 3.53F, 3.53F),
//                    CombinedBarData("S-21", 3.23F, 3.23F),
//                    CombinedBarData("S-21", 3.93F, 3.93F),
//                    CombinedBarData("F-20", 3.53F, 3.53F),
//                    CombinedBarData("S-21", 3.23F, 3.23F),
//                    CombinedBarData("S-21", 3.93F, 3.93F),
//                    CombinedBarData("F-20", 3.53F, 3.53F),
//            CombinedBarData("S-21", 3.23F,3.23F),
//            CombinedBarData("S-21", 3.93F,3.93F)
//                )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(.7f)
                            .padding(5.dp),
                        text = "Last Updated: ${userBasicInfo.lastUpdatedResultInfo.toDayPassed()}",
                        fontSize = 8.sp,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.End
                    )
                    Details(userBasicInfo, hasPrivateInfo)
                    if(hasPrivateInfo)
                        Address(userBasicInfo)
                }
            }


        }
        CustomToast(loading, loadingTxt)

    }

//    Box(modifier = Modifier
//        .fillMaxSize()
//        .padding(26.dp)
//        .alpha(.7f),
//        contentAlignment = Alignment.BottomEnd)
//    {
//        Box(modifier = Modifier
//            .clip(RoundedCornerShape(10.dp))
//            .background(BlueViolet1)
//            .padding(10.dp)
//        ){
//            Column(modifier = Modifier
//                .align(Alignment.Center)) {
//                Text(
//                    text = "Last Updated:",
//                    fontSize=10.sp,
//                    style=MaterialTheme.typography.headlineSmall,
//                )
//                Text(
//                    text = "5 Days Ago",
//                    style=MaterialTheme.typography.headlineMedium,
//                )
//            }
//        }
//
//    }

}
@Composable
fun TopBar(navController: NavHostController,onClick: (() -> Unit)? = null) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlue)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
    ) {
        BackBtn(navController)
        Ic(Icons.Rounded.Refresh,onClick)
    }
}

@Composable
fun BackBtn(navController: NavHostController) {
    val mutableInteractionSource by remember { mutableStateOf(MutableInteractionSource()) }

    Icon(
        Icons.AutoMirrored.Rounded.ArrowBack,
        contentDescription = "",
        tint = Color.White,
        modifier = Modifier
            .size(55.dp)
            .bounceClick()
            .clip(RoundedCornerShape(30.dp))
            .clickable(
                interactionSource = mutableInteractionSource,
                indication = ripple(color = Color.White),
                onClick = {
                    navController.navigateUp()
                }
            )
            .padding(10.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Profile(
    pic: String?,
    publicInfo: PublicInfo?,
    isLoading: Boolean,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .width(300.dp),
        contentAlignment = Alignment.Center
    ) {
        val haptic = LocalHapticFeedback.current
        val context = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SubcomposeAsyncImage(
                model = if(!pic.isNullOrEmpty()) pic else "",
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .aspectRatio(1f)
                    .padding(15.dp)
                    .clip(RoundedCornerShape(100.dp))
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
                            alpha = .7F
                        )
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            Text(
                text = if(!publicInfo?.nm.isNullOrEmpty()) publicInfo.nm else "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            context.setClipBoardData(publicInfo?.nm)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDoubleClick = {}
                    )
            )
        }
        if(!publicInfo?.id?.hasAlphabet()!!) {
            CgpaToast(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 185.dp, top = 25.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            val text =
                                if (!publicInfo.cgpa.isNaN()) "CGPA: ${publicInfo.cgpa}" else "CGPA: 0.0"
                            context.setClipBoardData(text)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDoubleClick = {}
                    ),
                pb = publicInfo,
                isLoading = isLoading,
            ) {
                navController.navigate(MainScreens.Profile.Cgpa(publicInfo.id))
            }
        }


    }
}

@Composable
fun CgpaToast(modifier: Modifier, pb: PublicInfo, isLoading: Boolean, onClick: (() -> Unit)) {
    //var isLoaded by remember { mutableStateOf(!isLoading) }

    val toastHeight by animateDpAsState(
        targetValue = if (!isLoading) 25.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        ), label = ""
    )
    val toastWidth by animateDpAsState(
        targetValue = if (!isLoading) 90.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        ), label = ""
    )
//    val context = LocalContext.current
//    val clipboardManager = LocalClipboard.current

    Box(
        modifier = modifier
            .height(toastHeight)
            .width(toastWidth)
            .bounceClick()
            .clip(RoundedCornerShape(20.dp))
            .background(LightGreen2)
            .clickable {
                onClick.invoke()
//                clipboardManager.setText(AnnotatedString(pb.id.orEmpty()))
//                var intent =
//                    context.packageManager.getLaunchIntentForPackage("net.startbit.diucgpa")
//                if (intent != null) {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    context.startActivity(intent)
//                } else {
//                    intent = Intent(Intent.ACTION_VIEW)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    //intent.data = Uri.parse("market://details?id=" + "net.startbit.diucgpa")
//                    intent.data =
//                        Uri.parse("https://play.google.com/store/apps/details?id=net.startbit.diucgpa")
//                    context.startActivity(intent)
//                }
//                context.toast("Student ID copied, paste it in DIU CGPA App.")

            }
    ) {
        if (!isLoading) Text(
            text = if(!pb.cgpa.isNaN()) "CGPA: ${pb.cgpa}" else "CGPA: 0.0",
            fontSize = toastHeight.value.sp / 2,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
//    Column(modifier = Modifier.fillMaxSize()) {
//        Button(onClick = { isLoaded = !isLoaded }) { Text(text = "Toggle") }
//
//    }
}

@SuppressLint("IntentReset")
@Composable
fun SocialLink(
    viewModel: MainViewModel,
    userBasicInfo: UserBasicInfo,
    hasPrivateInfo: Boolean,
    navController: NavHostController,
    id: String,
    myId: String
) {
    val myBasicInfo by viewModel.myBasicInfo.collectAsStateWithLifecycle()

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)

    ) {
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .bounceClick()
            ,
            onClick = {
                if(!id.hasAlphabet()){
                    val path = "personalMsg/$myId/"
                    val myPath = "personalMsg/$myId/profiles/$id"
                    val tarPath = "personalMsg/$id/profiles/$myId"
                    val time = System.currentTimeMillis()
                    val tarProfile = Msg(
                        id = userBasicInfo.publicInfo.id,
                        nm = userBasicInfo.publicInfo.nm,
                        msg = "You Knocked.",
                        pic = userBasicInfo.privateInfo.pic,
                        time = time
                    )
                    viewModel.refreshProfileInChats(myPath, tarProfile) {
                        Log.d("TAG", "ChatPersonalScreen: $it")
                        context.toast("Couldn't send message")
                    }
                    val myProfile = Msg(
                        id = myBasicInfo.publicInfo.id,
                        nm = myBasicInfo.publicInfo.nm,
                        msg = "Knocked You.",
                        pic = myBasicInfo.privateInfo.pic,
                        time = time
                    )
                    viewModel.refreshProfileInChats(tarPath, myProfile) {
                        Log.d("TAG", "ChatPersonalScreen: $it")
                        context.toast("Couldn't send message")
                    }
                    navController.navigate(InnerScreens.Conversation(path, id))
                }
                else{
                    navController.navigateUp()
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepBlueLess)
        ) {
            Row(Modifier.padding(vertical = 7.dp)) {
                Text(
                    text = "Knock",
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontFamily = ubuntu,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ME",
                    color = LightBlue,
                    fontSize = 22.sp,
                    fontFamily = ubuntu,
                    fontWeight = FontWeight.Bold

                )
            }
            Spacer(modifier = Modifier.size(7.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_chat),
                contentDescription = "knockMe",
                tint = TextWhite
            )
        }
        if(hasPrivateInfo)
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .bounceClick()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = {
                            if (!userBasicInfo.privateInfo.fbLink.isNullOrEmpty())
                                context.showCustomTab(userBasicInfo.privateInfo.fbLink)
                            else if (!userBasicInfo.privateInfo.socialNetId.isNullOrEmpty())
                                context.showCustomTab(userBasicInfo.privateInfo.socialNetId)
                            else
                                context.toast("Invalid link")
                        }
                    )
                    .background(DeepBlueLess)
                    .padding(10.dp)
                    .size(35.dp)
            ) {
                Text(
                    text = "f",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = bakbakBold
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .bounceClick()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = {
                            if (!userBasicInfo.privateInfo.email.isNullOrEmpty()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    val body = "Hey, I found your email in KnockME.\n"
                                    val data =
                                        "mailto:${userBasicInfo.privateInfo.email}?subject=Wanna be friend!&body=$body".toUri()
                                    intent.data = data
                                    context.startActivity(intent)

                                } catch (_: Exception) {
                                    context.toast("No Mailing App Found")
                                }
                            } else
                                context.toast("No Email Found")
                        }
                    )
                    .background(DeepBlueLess)
                    .padding(10.dp)
                    .size(35.dp)
            ) {
                Text(
                    text = "@",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = ubuntu,
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                )
            }
        }
    }
}

@Composable
fun CGBarChart(barDataList: List<BarData>, pb: PublicInfo, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SGPA Graph:",
            style = MaterialTheme.typography.headlineSmall
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .padding(top = 10.dp)
                .bounceClick {
                    navController.navigate(MainScreens.Profile.Cgpa(pb.id))
                }
                .clip(RoundedCornerShape(10.dp))
                .background(DeepBlueLess)
        ) {
            Box(
                modifier = Modifier
                    .padding(end = when(barDataList.size){
                        in 1..2 -> 240.dp
                        in 3..4 -> 200.dp
                        in 5..6 -> 150.dp
                        in 7..9 -> 100.dp
                        else -> 0.dp
                    })
            ) {
                BarChart(
                    data = { barDataList },
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 40.dp)
                        .fillMaxWidth()
                        .height(100.dp),
                    barChartConfig = BarChartConfig(
                        showAxisLines = true,
                        showGridLines = false,
                        drawNegativeValueChart = false,
                        showCurvedBar = true,
                        minimumBarCount = barDataList.size.coerceAtLeast(1)
                    ),
                    barChartColorConfig = BarChartColorConfig(
                        fillGradientColors = ChartColor.Solid(Beige1),
                        negativeGradientBarColors = ChartColor.Solid(LightRed),
                        barBackgroundColor = ChartColor.Solid(Color.Transparent),
                        gridLineColor = ChartColor.Solid(Color.LightGray),
                        axisLineColor = ChartColor.Solid(Color.LightGray)
                    ),
                    labelConfig = LabelConfig(
                        textColor = ChartColor.Solid(Color.White),
                        showXLabel = false,
                        showYLabel = false
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    barDataList.forEach { barData ->
                        Text(
                            text = barData.yValue.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .offset(y = 12.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .graphicsLayer {
                                    rotationZ = -45f
                                }
                        )

                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    barDataList.forEach { barData ->
                        Text(
                            text = barData.xValue.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .offset(y = (-12).dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .graphicsLayer {
                                    rotationZ = -45f
                                }
                        )

                    }
                }
            }
        }
        Text(
            text = "Details CGPA View >",
            style = TextStyle(textDecoration = TextDecoration.Underline),
            color = TextWhite,
            fontSize = 13.sp,
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset(x = (-5).dp)
                .clip(RoundedCornerShape(5.dp))
                .clickable {
                    navController.navigate(MainScreens.Profile.Cgpa(pb.id))
                }
                .padding(5.dp),
            textAlign = TextAlign.Center
        )
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Details(userBasicInfo: UserBasicInfo, hasPrivateInfo: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Details:",
            style = MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(5.dp),
        ) {
            if (userBasicInfo.publicInfo.id.isNotNull())
                DetailsItems("ID: ${userBasicInfo.publicInfo.id}", LightGreen2)
            if(userBasicInfo.publicInfo.batchNo !=0)
                DetailsItems("Batch: ${userBasicInfo.publicInfo.batchNo}", BlueViolet1)
            if(hasPrivateInfo) {
                if(userBasicInfo.privateInfo.bloodGroup.isNotNull())
                    DetailsItems("Blood: ${userBasicInfo.privateInfo.bloodGroup}", LightRed)
                if(userBasicInfo.privateInfo.mobile.isNotNull())
                    DetailsItems("Tel: ${userBasicInfo.privateInfo.mobile}",  LightBlue)
                if(userBasicInfo.privateInfo.sex.isNotNull())
                    DetailsItems("Gender: ${userBasicInfo.privateInfo.sex}", BlueViolet1)
                if(userBasicInfo.privateInfo.religion.isNotNull())
                    DetailsItems("Religion: ${userBasicInfo.privateInfo.religion}", OrangeYellow2)
            }
            if(userBasicInfo.publicInfo.progShortName.isNotNull())
                DetailsItems("Prog: ${userBasicInfo.publicInfo.progShortName}", LightGreen2)
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Address(userBasicInfo: UserBasicInfo) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.size(25.dp))
        Text(
            text = "Address:",
            style = MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(5.dp),
        ) {
            if(userBasicInfo.privateInfo.presentHouse.isNotNull())
                DetailsItems("Current: ${userBasicInfo.privateInfo.presentHouse}", Beige3)
            else if(userBasicInfo.privateInfo.loc.isNotNull())
                DetailsItems("Current: ${userBasicInfo.privateInfo.loc}", Beige3)
            if(userBasicInfo.privateInfo.permanentHouse.isNotNull())
                DetailsItems("Home: ${userBasicInfo.privateInfo.permanentHouse}", DarkerButtonBlue)
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsItems(data: String, color: Color) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(top = 10.dp, end = 10.dp)
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    context.setClipBoardData(data.split(": ")[1])
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onDoubleClick = {}
            )
            .background(color)
            .padding(10.dp)
    ) {
        Text(
            text = data,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ProfileViewScreenPre() {
    KnockMETheme {
        ProfileViewScreen(navController= rememberNavController())
    }

}