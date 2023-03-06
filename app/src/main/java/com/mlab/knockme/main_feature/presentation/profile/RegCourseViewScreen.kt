package com.mlab.knockme.main_feature.presentation.profile

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.CourseInfo
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toTeacherInitial
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.profile_feature.presentation.components.standardQuadFromTo
import com.mlab.knockme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegCourseViewScreen(navController: NavHostController, viewModel: MainViewModel = hiltViewModel()) {
    val context: Context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    val myId = sharedPreferences.getString("studentId",null)!!
    val myFullProfile by viewModel.userFullProfileInfo.collectAsState()

    LaunchedEffect(key1 = ""){
        viewModel.getUserFullProfileInfo(myId,{
            viewModel.updateUserRegCourseInfo(
                id = myId,
                accessToken = it.token,
                it.regCourseInfo
            ){msg->
                Looper.prepare()
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        },{
            Looper.prepare()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Looper.loop()
        })
    }
//    val lst = listOf(
//        CourseInfo(
//            0,
//            "Social and Professional Issues in Computing",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0,
//            semesterName = "Summer",
//            semesterYear = 2023
//    ),
//        CourseInfo(
//            0,
//            "Social and Professional",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        ),
//        CourseInfo(
//            0,
//            "Social and Professional Issues in Computing",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        ),
//        CourseInfo(
//            0,
//            "Social and Professional",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        ),
//        CourseInfo(
//            0,
//            "Social and Professional Issues in Computing",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        ),
//        CourseInfo(
//            0,
//            "Social and Professional",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        ),
//        CourseInfo(
//            0,
//            "Social and Professional Issues in Computing",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        ),
//        CourseInfo(
//            0,
//            "Social and Professional",
//            "CSE498",
//            "Ms. Zerin Nasrin Tumpa",
//            "54_PC-A",
//            totalCredit = 3.0
//        )
//    )
    Scaffold(topBar = {
        TopBar(navController){
            viewModel.updateUserRegCourseInfo(
                id = myId,
                accessToken = myFullProfile.token,
                myFullProfile.regCourseInfo
            ){
                Looper.prepare()
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                Looper.loop()            }
        }
    }) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(it)
            .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Registered Course",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = if(myFullProfile.regCourseInfo.size>0) myFullProfile.regCourseInfo[0].toShortSemName() else "",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "${myFullProfile.regCourseInfo.sumOf { item -> item.totalCredit!! }.toInt()} Credits",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(myFullProfile.regCourseInfo.size) { ind->
                    if(ind%2==0)
                        RegCourseItem(myFullProfile.regCourseInfo[ind])
                    else
                        RegCourseItem(myFullProfile.regCourseInfo[ind],Limerick1, Limerick2, Limerick3)
                }
            }
            LastUpdated(myFullProfile.lastUpdatedRegCourseInfo)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegCourseItem(
    courseInfo: CourseInfo,
    lightColor: Color=BlueViolet1,
    mediumColor: Color=BlueViolet2,
    darkColor: Color=BlueViolet3
) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = Modifier
            .padding(vertical = 7.5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    val text = "${courseInfo.courseTitle} (${courseInfo.customCourseId})\nCredit: ${courseInfo.totalCredit?.toInt()}\nBy ${courseInfo.employeeName}"
                    clipboardManager.setText(AnnotatedString(text))
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "Data Copied", Toast.LENGTH_SHORT).show()
                },
                onDoubleClick = {}
            )
            .background(darkColor)
    ) {
        val width = constraints.maxWidth
        val height = 340

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
                .fillMaxWidth()
                .wrapContentHeight()
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = courseInfo.customCourseId!!,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "Credit: ${courseInfo.totalCredit?.toInt()}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = courseInfo.courseTitle!!,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(.8f)

            )
            Spacer(modifier = Modifier.size(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text =
                    if(!courseInfo.employeeName.isNullOrEmpty())
                        "${courseInfo.employeeName} (${courseInfo.employeeName.toTeacherInitial()})"
                    else
                        "Teacher: N/A",
                    color = TextWhite,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(.5f)
                )
                Text(
                    text = courseInfo.sectionName!!,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Previews4() {
    KnockMETheme {
        RegCourseViewScreen(rememberNavController())
    }
}