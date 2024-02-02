package com.mlab.knockme.main_feature.presentation.profile

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mlab.knockme.auth_feature.domain.model.SemesterInfo
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.ProfileInnerScreens
import com.mlab.knockme.main_feature.presentation.chats.CustomToast
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.main_feature.util.standardQuadFromTo
import com.mlab.knockme.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CgpaViewScreen(id: String, navController: NavHostController, viewModel: MainViewModel = hiltViewModel()) {
    val context: Context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val myFullProfile by viewModel.userFullProfileInfo.collectAsState()
    val loading by viewModel.isResultLoading.collectAsState()
    val loadingTxt by viewModel.resultLoadingTxt.collectAsState()

    val semesterInfoList = myFullProfile.fullResultInfo.map { it.semesterInfo }
    val semesterCount = semesterInfoList.count { it.sgpa != 0.0 }
    var isMsgNeeded: Boolean? by rememberSaveable { mutableStateOf(false) }
    if(isMsgNeeded != null)
        isMsgNeeded = semesterCount > 5

    LaunchedEffect(key1 = ""){
        viewModel.getUserFullProfileInfo(id,{
            viewModel.updateUserFullResultInfo(
                publicInfo = it.publicInfo,
                fullResultInfoList = it.fullResultInfo)
        },{
            Looper.prepare()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Looper.loop()
        })
    }

//    val lst = listOf(
//        SemesterInfo("","Summer",2023,3.45,15.0),
//        SemesterInfo("","Fall",2023,2.45,9.0),
//        SemesterInfo("","Spring",2022,3.55,22.0),
//        SemesterInfo("","Summer",2023,1.45,15.0),
//        SemesterInfo("","Fall",2019,3.45,15.0),
//        SemesterInfo("","Summer",2023,3.75,15.0),
//        SemesterInfo("","Summer",2023,3.45,15.0),
//
//        )

    Scaffold(
        topBar = {
        TopBar(navController){
            viewModel.updateUserFullResultInfo(
                publicInfo = myFullProfile.publicInfo,
                fullResultInfoList = myFullProfile.fullResultInfo)
        }
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlue)
                .padding(it)
                .padding(horizontal = 10.dp)
        ) {
            LastUpdated(myFullProfile.lastUpdatedResultInfo)
            Text(
                text = "Result",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                Modifier
                    .padding(10.dp)
                    .bounceClick()
                    .clip(
                        RoundedCornerShape(
                            topStart = 7.dp,
                            topEnd = 21.dp,
                            bottomStart = 21.dp,
                            bottomEnd = 7.dp
                        )
                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            val text = "CGPA: ${myFullProfile.publicInfo.cgpa}"
                            clipboardManager.setText(AnnotatedString(text))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(context, "Data Copied", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onDoubleClick = {}
                    )
                    .background(BlueViolet3)
                    .padding(27.dp)
                    .padding(horizontal = 5.dp)

            ) {
                Text(
                    text = "CGPA: ${myFullProfile.publicInfo.cgpa}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 30.sp
                )
            }
            Text(
                text = if(semesterInfoList.isNotEmpty()) "Congratulation," else "Oops!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 5.dp)
                    .padding(top = 10.dp),
                //textAlign = TextAlign.End
            )
            Text(
                text =
                if(semesterInfoList.size>1)
                    "You have completed $semesterCount semesters\nand ${myFullProfile.publicInfo.totalCompletedCredit.toInt()} credits."
                else if(semesterInfoList.size==1)
                    "You have completed $semesterCount semester\nand ${myFullProfile.publicInfo.totalCompletedCredit.toInt()} credits."
                else
                    "You haven't completed any semester.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhite,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                //textAlign = TextAlign.End
            )
            AnimatedVisibility(visible = isMsgNeeded ?: false) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "N.B : CGPA is calculated without the Final defense result",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AquaBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(10.dp),
                )
                CoroutineScope(Dispatchers.Main).launch {
                    // Simulate loading data
                    delay(7.seconds)
                    isMsgNeeded = null
                }
            }

            Text(
                text = if(semesterInfoList.isNotEmpty()) "All SGPAs" else "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(top = 20.dp, bottom = 10.dp),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 5.dp, end = 5.dp, bottom = 100.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(semesterInfoList.size) { ind->
                    if(semesterInfoList[ind].sgpa < 3.0)
                        SemesterInfoItem(semesterInfoList[ind], Beige1, Beige2, Beige3){
                            navController.navigate(ProfileInnerScreens.CgpaInnerScreen.route+id+"/"+ind)
                        }
                    else if(semesterInfoList[ind].sgpa >= 3.7)
                        SemesterInfoItem(semesterInfoList[ind], LightGreen1, LightGreen2, LightGreen3){
                            navController.navigate(ProfileInnerScreens.CgpaInnerScreen.route+id+"/"+ind)
                        }
                    else if(semesterInfoList[ind].sgpa >= 3.5)
                        SemesterInfoItem(semesterInfoList[ind], BlueViolet1, BlueViolet2, BlueViolet3){
                            navController.navigate(ProfileInnerScreens.CgpaInnerScreen.route+id+"/"+ind)
                        }
                    else
                        SemesterInfoItem(semesterInfoList[ind], Limerick1, Limerick2, Limerick3){
                            navController.navigate(ProfileInnerScreens.CgpaInnerScreen.route+id+"/"+ind)
                        }
                }
            }

        }
    }
    CustomToast(loading, loadingTxt)

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SemesterInfoItem(
    semesterInfo: SemesterInfo,
    lightColor: Color = Limerick1,
    mediumColor: Color = Limerick2,
    darkColor: Color = Limerick3,
    onClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = Modifier
            .padding(5.dp)
            .aspectRatio(1f)
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {
                    onClick.invoke()
                },
                onLongClick = {
                    val text =
                        "Semester: ${semesterInfo.toShortSemName()}\nSGPA: ${semesterInfo.sgpa}\nCredits: ${semesterInfo.creditTaken.toInt()}"
                    clipboardManager.setText(AnnotatedString(text))
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast
                        .makeText(context, "Data Copied", Toast.LENGTH_SHORT)
                        .show()
                },
                onDoubleClick = {}
            )
            .background(darkColor)
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight+50

        // Medium colored path
        val mediumColoredPoint1 = Offset(0f, height * 0.3f)
        val mediumColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val mediumColoredPoint3 = Offset(width * 0.4f, height * 0.15f)
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
        val lightPoint4 = Offset(width * 0.65f, height * 0.85f)
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
                color = mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = lightColor
            )
        }
        Text(
            text = semesterInfo.sgpa.toString(),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Text(
            text = semesterInfo.toShortSemName(),
            style = MaterialTheme.typography.bodySmall,
            color = TextWhite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
        Text(
            text = "${semesterInfo.creditTaken.toInt()} Credits",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            color = TextWhite,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Previews1() {
    KnockMETheme {
       // CgpaViewScreen(it.arguments?.getString("id")!!, rememberNavController())
    }
}