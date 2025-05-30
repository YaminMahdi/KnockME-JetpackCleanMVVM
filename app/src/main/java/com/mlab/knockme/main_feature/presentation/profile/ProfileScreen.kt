package com.mlab.knockme.main_feature.presentation.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.ClearanceInfo
import com.mlab.knockme.auth_feature.presentation.login.LoginActivity
import com.mlab.knockme.core.util.*
import com.mlab.knockme.main_feature.presentation.InnerScreens
import com.mlab.knockme.main_feature.presentation.MainScreens
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.profile.components.Feature
import com.mlab.knockme.main_feature.util.standardQuadFromTo
import com.mlab.knockme.pref
import com.mlab.knockme.ui.theme.*
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: MainViewModel= hiltViewModel()
) {
    val context: Context = LocalContext.current
    //val state by viewModel.state.collectAsStateWithLifecycle()
    //val preferencesEditor = sharedPreferences.edit()

    val myFullProfile by viewModel.userFullProfileInfo.collectAsStateWithLifecycle()
//    val myId = pref.getString("studentId","")!!
//    val myNm = pref.getString("nm","")!!
//    val myProShortName = pref.getString("proShortName","")!!
    val myId = myFullProfile.publicInfo.id
    val myNm = myFullProfile.publicInfo.nm
    val myProShortName = myFullProfile.publicInfo.progShortName
    val lastClearanceInfo =if(myFullProfile.clearanceInfo.isNotEmpty()) myFullProfile.clearanceInfo.last() else ClearanceInfo()
    val last1 =if(lastClearanceInfo.registration) "✔" else "✖"
    val last2 =if(lastClearanceInfo.midTermExam) "✔" else "✖"
    val last3 =if(lastClearanceInfo.finalExam) "✔" else "✖"
    val last = "$last1 $last2 $last3"

    if(myFullProfile.publicInfo.id.isNotEmpty()){
        pref.edit {
            putString("nm", myFullProfile.publicInfo.nm)
            putString("proShortName", myFullProfile.publicInfo.progShortName)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUserFullProfileInfo(myId,{
            GlobalScope.launch(Dispatchers.IO){
                viewModel.updateUserPaymentInfo(
                    id = myId,
                    accessToken = it.token,
                    paymentInfo = it.paymentInfo
                ){ }
//                delay(100)
//                viewModel.updateUserLiveResultInfo(
//                    id = myId,
//                    accessToken = it.token,
//                    liveResultInfoList = it.liveResultInfo
//                ){ }
//                delay(100)
//                viewModel.updateUserRegCourseInfo(
//                    id = myId,
//                    accessToken = it.token,
//                    regCourseList = it.regCourseInfo
//                ){ }
                delay(100)
                viewModel.updateClearanceInfo(
                    id = myId,
                    accessToken = it.token,
                    clearanceInfoList = it.clearanceInfo
                ){ }
//                delay(100)
//                viewModel.updateUserFullResultInfo(
//                    publicInfo = it.publicInfo,
//                    fullResultInfoList = it.fullResultInfo
//                )
            }
        },{
            context.toast(it)
        })
    }

    InfoBottomSheet(viewModel,context,myId,navController)
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo("Profile"){
            viewModel.setInfoDialogVisibility(true)
        }
        PersonInfo(
            myNm,
            myFullProfile.privateInfo.pic!!,
            myProShortName,
            myId,
            navController
            )
        FeatureSection(navController,myId,
            features = listOf(
            Feature(
                title = "CGPA",
                info = "%.2f".format(myFullProfile.publicInfo.cgpa),
                BlueViolet1,
                BlueViolet2,
                BlueViolet3
            ),
            Feature(
                title = "DUE",
                info = (myFullProfile.paymentInfo.totalDebit!!-myFullProfile.paymentInfo.totalCredit!!).toK(),
                Beige1,
                Beige2,
                Beige3

            ),
            Feature(
                title = "Course",
                info = myFullProfile.regCourseInfo.size.toWords(),
                Limerick1,
                Limerick2,
                Limerick3
            ),
            Feature(
                title = "Result",
                info = "LIVE" ,
                LightGreen1,
                LightGreen2,
                LightGreen3
            ),
            Feature(
                title = "Clearance",
                info = last,
                OrangeYellow1,
                OrangeYellow2,
                OrangeYellow3
            )
        ))
    }

}

@Composable
fun TitleInfo(
    title: String,
    onClick: (() -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontSize = 26.sp
        )
        Row{
            Ic(Icons.TwoTone.Info){ onClick.invoke() }
        }
    }
}

@Composable
fun Ic(
    iVec: ImageVector = Icons.Rounded.Settings,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(start = 12.dp)
            .bounceClick()
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onClick?.invoke()
            }
            .background(LessBlue.copy(.7f))
            .padding(8.dp)


    ) {
        Icon(
            iVec,
            contentDescription ="id",
            modifier= Modifier.size(26.dp),
            tint = AquaBlue
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    viewModel: MainViewModel,
    context: Context,
    myId: String,
    navController: NavHostController
) {
    val dialogVisibility by viewModel.infoDialogVisibility.collectAsStateWithLifecycle()
    val manager = ReviewManagerFactory.create(context)
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (dialogVisibility) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(context as Activity, reviewInfo)
                flow.addOnCompleteListener {
                    Log.d("TAG", "ReviewManagerFactory: ${it.result}")
                }
            } else {
                Log.d("TAG", "ReviewManagerFactory: ${task.exception}")
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                viewModel.setInfoDialogVisibility(false)
            },
            sheetState = bottomSheetState,
            containerColor = BlueViolet0,
            contentColor = Neutral30,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            color = Neutral30.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
            ) {
                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 20.dp, top = 10.dp)
                ) {
                    Text(
                        text = "Made With Love 💕",
                        style = MaterialTheme.typography.headlineMedium,
                        color = LightRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "By",
                        fontFamily = ubuntu,
                        color = Neutral30.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Developer Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.setInfoDialogVisibility(false)
                            navController.navigate(InnerScreens.UserProfile("193-15-1071"))
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = BlueViolet3.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Profile Avatar Placeholder
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(BlueViolet3, Limerick3)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AM",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Ahmad Umar Mahdi",
                            fontFamily = ubuntu,
                            fontWeight = FontWeight.Bold,
                            color = Neutral30,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "193-15-1071",
                            fontFamily = ubuntu,
                            color = Limerick3,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Link
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            context.showCustomTab(Constants.TAP_LINK_URL)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = BlueViolet3.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Link,
                            contentDescription = "Link",
                            tint = BlueViolet3,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "taplink.cc/yk_mahdi",
                            fontFamily = ubuntu,
                            color = BlueViolet4,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = BlueViolet3.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Settings",
                            fontFamily = ubuntu,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral30,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Show Hadith Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                                    contentDescription = "Hadith",
                                    tint = BlueViolet3,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Show Hadith",
                                        fontFamily = ubuntu,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Neutral30
                                    )
                                    Text(
                                        text = "Display daily Hadith content",
                                        fontFamily = ubuntu,
                                        fontSize = 12.sp,
                                        color = Neutral30.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            val showHadith by viewModel.showHadith.collectAsStateWithLifecycle()
                            Switch(
                                checked = showHadith,
                                onCheckedChange = { viewModel.setShowHadith(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BlueViolet3,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = BlueViolet3.copy(alpha = 0.2f),
                                    uncheckedBorderColor = BlueViolet3.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Report Problem Button

                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW)
                                val body = "My Student ID is $myId. The issue is.."
                                val data =
                                    "mailto:ahmad15-1071@diu.edu.bd?subject=Having Issue&body=$body".toUri()
                                intent.data = data
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                context.toast("No Mailing App Found")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .bounceClick(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = LessBlue.copy(alpha = 0.7f),
                        ),
                        border = BorderStroke(1.dp, Neutral30.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Report,
                            contentDescription = "Report",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Report",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = LessBlue.copy(alpha = 0.7f)
                        )
                    }

                    // Logout Button
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                pref.edit { clear() }
                                Firebase.auth.signOut()
                            }
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .bounceClick(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LessRed
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Logout",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportProblem(context: Context, myId: String, modifier: Modifier = Modifier, color: Color = Neutral50) {
    Text(
        text = "Report a problem",
        style = TextStyle(textDecoration = TextDecoration.Underline),
        fontFamily = ubuntu,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val body = "My Student ID is $myId. The issue is.."
                    val data =
                        "mailto:ahmad15-1071@diu.edu.bd?subject=Having Issue&body=$body".toUri()
                    intent.data = data
                    context.startActivity(intent)
                } catch (_: Exception) {
                    context.toast("No Mailing App Found")
                }
            }
            .padding(10.dp)
    )
}

@Composable
fun PersonInfo(
    name: String,
    pic: String,
    program: String,
    id: String,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .clickable { navController.navigate(InnerScreens.UserProfile(id)) }
    ) {
        SubcomposeAsyncImage(
            model = pic,
            contentDescription = name,
            modifier = Modifier
                .fillMaxWidth(.4f)
                .aspectRatio(1f)
                .padding(15.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkerButtonBlue)
        ) {
            val state by painter.state.collectAsStateWithLifecycle()
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        color = AquaBlue,
                        modifier = Modifier
                            .padding(25.dp)
                    )}
                is AsyncImagePainter.State.Error -> {
                    SubcomposeAsyncImageContent(
                        painter = painterResource(id = R.drawable.ic_profile),
                        alpha = .7F
                    )}
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
//        Image(
//            painter = rememberAsyncImagePainter(pic),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth(.4f)
//                .aspectRatio(1f)
//                .clip(RoundedCornerShape(10.dp))
//        )
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(15.dp)

        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = program,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Text(
                text = "ID: $id",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }

}
@Composable
fun FeatureSection(navController: NavHostController,id: String, features: List<Feature>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top=15.dp, start = 15.dp, bottom = 5.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(features.size) {
                FeatureItem(feature = features[it]){
                    when (it) {
                        0 -> navController.navigate(MainScreens.Profile.Cgpa(id))
                        1 -> navController.navigate(MainScreens.Profile.Due)
                        2 -> navController.navigate(MainScreens.Profile.RegisteredCourse)
                        3 -> navController.navigate(MainScreens.Profile.LiveResult)
                        4 -> navController.navigate(MainScreens.Profile.Clearance)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeatureItem(
    feature: Feature,
    onClick: (() -> Unit)
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .aspectRatio(1f)
            .bounceClick()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = { onClick.invoke() },
                onLongClick = {
                    val text = "${feature.title}: ${feature.info}"
                    context.setClipBoardData(text)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onDoubleClick = {}
            )
            .background(feature.darkColor)
    ) {
        val width = this.constraints.maxWidth
        val height = this.constraints.maxHeight

        // Medium colored path
        val mediumColoredPoint1 = Offset(0f, height * 0.3f)
        val mediumColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val mediumColoredPoint3 = Offset(width * 0.4f, height * 0.05f)
        val mediumColoredPoint4 = Offset(width * 0.75f, height * 0.7f)
        val mediumColoredPoint5 = Offset(width * 1.4f, -height.toFloat())

        val mediumColoredPath = Path().apply {
            moveTo(mediumColoredPoint1.x, mediumColoredPoint1.y)
            standardQuadFromTo(mediumColoredPoint1, mediumColoredPoint2)
            standardQuadFromTo(mediumColoredPoint2, mediumColoredPoint3)
            standardQuadFromTo(mediumColoredPoint3, mediumColoredPoint4)
            standardQuadFromTo(mediumColoredPoint4, mediumColoredPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }

        // Light colored path
        val lightPoint1 = Offset(0f, height * 0.35f)
        val lightPoint2 = Offset(width * 0.1f, height * 0.4f)
        val lightPoint3 = Offset(width * 0.3f, height * 0.35f)
        val lightPoint4 = Offset(width * 0.65f, height.toFloat())
        val lightPoint5 = Offset(width * 1.4f, -height.toFloat() / 3f)

        val lightColoredPath = Path().apply {
            moveTo(lightPoint1.x, lightPoint1.y)
            standardQuadFromTo(lightPoint1, lightPoint2)
            standardQuadFromTo(lightPoint2, lightPoint3)
            standardQuadFromTo(lightPoint3, lightPoint4)
            standardQuadFromTo(lightPoint4, lightPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawPath(
                path = mediumColoredPath,
                color = feature.mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = feature.lightColor
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Text(
                text = feature.info,
                color = TextWhite,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Text(
            text = feature.title,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(15.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewsProfile() {
    KnockMETheme {
        //ProfileScreen()
    }
}