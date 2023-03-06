package com.mlab.knockme.main_feature.presentation.profile

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mlab.knockme.auth_feature.domain.model.ResultInfo
import com.mlab.knockme.auth_feature.domain.model.SemesterInfo
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.main.BackBtn
import com.mlab.knockme.profile_feature.presentation.components.standardQuadFromTo
import com.mlab.knockme.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaDetailsScreen(id: String,index: Int, navController: NavHostController, viewModel: MainViewModel = hiltViewModel()) {
    val context: Context = LocalContext.current
    val fullProfile by viewModel.userFullProfileInfo.collectAsState()
    LaunchedEffect(key1 = ""){
        viewModel.getUserFullProfileInfo(id){
            Looper.prepare()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Looper.loop()
        }
    }
    val semInfo =
        if(fullProfile.fullResultInfo.size>0) fullProfile.fullResultInfo[index].semesterInfo else SemesterInfo()
//    val lst = listOf(
//        ResultInfo(
//            "", "Social and Professional Issues in Computing",
//            "CSE498","A+",2.65,3.0
//        ),
//        ResultInfo(
//            "", "Social and Professional",
//            "CSE498","A+",3.65,1.0
//        ),
//        ResultInfo(
//            "", "Social and Professional Issues",
//            "CSE498","D+",3.49,3.0
//        ),
//        ResultInfo(
//            "", "Social and Professional Issues in Computing",
//            "CSE498","A+",3.75,3.0
//        ),
//        ResultInfo(
//            "", "Social and Professional Issues in Computing",
//            "CSE498","A+",2.65,3.0
//        ),
//        ResultInfo(
//            "", "Social and Professional Issues in Computing",
//            "CSE498","A+",3.55,3.0
//        ),
//        ResultInfo(
//            "", "Social and Professional Issues in Computing",
//            "CSE498","A+",3.65,3.0
//        ),
//        ResultInfo(
//            "", "Social and Professional Issues in Computing",
//            "CSE498","A+",3.65,3.0
//        ),
//    )
    val lst=if(fullProfile.fullResultInfo.size>0) fullProfile.fullResultInfo[index].resultInfo else emptyList()
    Scaffold(topBar = { TopBarOnlyBack(navController) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlue)
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = semInfo.toShortSemName(),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp)
            ) {
                Text(
                    text = "Credits: ${semInfo.creditTaken.toInt()}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "SGPA: ${semInfo.sgpa}",
                    style = MaterialTheme.typography.headlineMedium
                )

            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(lst.size) { ind ->
                    if(lst[ind].pointEquivalent < 3.0)
                        ResultItemRGB(lst[ind], Beige1, Beige2, Beige3)
                    else if(lst[ind].pointEquivalent >= 3.7)
                        ResultItemRGB(lst[ind], LightGreen1, LightGreen2, LightGreen3)
                    else if(lst[ind].pointEquivalent >= 3.5)
                        ResultItemRGB(lst[ind], BlueViolet1, BlueViolet2, BlueViolet3)
                    else
                        ResultItemRGB(lst[ind], Limerick1, Limerick2, Limerick3)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResultItemRGB(
    resultInfo: ResultInfo,
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
                    val text = "${resultInfo.courseTitle} (${resultInfo.customCourseId})\nCredit: ${resultInfo.totalCredit.toInt()}\nCG: ${resultInfo.pointEquivalent}"
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
        val mediumColoredPoint4 = Offset(width * 0.7f, height * 0.6f)
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
        val lightPoint4 = Offset(width * 0.65f, height * .7f)
        val lightPoint5 = Offset(width * 1.4f, -height.toFloat() / 6f)

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Column{
                Text(
                    text = resultInfo.customCourseId!!,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text =
                    if(resultInfo.totalCredit>1)
                        "${resultInfo.totalCredit.toInt()} Credits"
                    else
                        "${resultInfo.totalCredit.toInt()} Credit",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.alpha(.8f)
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = resultInfo.courseTitle!!,
                    style = MaterialTheme.typography.headlineMedium,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 2.dp,
                            topEnd = 10.dp,
                            bottomStart = 10.dp,
                            bottomEnd = 2.dp
                        )
                    )
                    .background(DeepBlueMoreLess.copy(alpha = 0.2f))
                    .padding(7.dp)
                    .padding(horizontal = 5.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = resultInfo.pointEquivalent.toString(),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

    }
}

@Composable
fun ResultItem(resultInfo: ResultInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .background(DeepBlueLess)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = Color.White),
                onClick = { }
            )
            .padding(16.dp)
    ){
        Column{
            Text(
                text = resultInfo.customCourseId!!,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text =
                if(resultInfo.totalCredit>1)
                    "${resultInfo.totalCredit.toInt()} Credits"
                else
                    "${resultInfo.totalCredit.toInt()} Credit",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.alpha(.7f)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = resultInfo.courseTitle!!,
                style = MaterialTheme.typography.headlineMedium,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier =
            Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 2.dp,
                        topEnd = 10.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 2.dp
                    )
                )
                .background(DeepBlueMoreLess)
                .padding(7.dp)
                .padding(horizontal = 5.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(
                text = resultInfo.pointEquivalent.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}


@Composable
fun TopBarOnlyBack(navController: NavHostController) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun Previews5() {
    KnockMETheme {
       // CgpaDetailsScreen(rememberNavController(), navController)
    }
}
