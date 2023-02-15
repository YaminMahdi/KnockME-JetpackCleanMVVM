package com.mlab.knockme.main_feature.presentation.profile

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.LiveResultInfo
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.profile_feature.presentation.components.standardQuadFromTo
import com.mlab.knockme.ui.theme.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun LiveResultViewScreen(navController: NavHostController, viewModel: MainViewModel = hiltViewModel()) {
    val context: Context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    val myId = sharedPreferences.getString("studentId",null)!!
    val myFullProfile by viewModel.userFullProfileInfo.collectAsState()

    LaunchedEffect(key1 = ""){
        viewModel.getUserFullProfileInfo(myId,{
            viewModel.updateUserLiveResultInfo(
                id = myId,
                accessToken = it.token,
                it.liveResultInfo
            ){
                Looper.prepare()
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        },{
            Looper.prepare()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Looper.loop()
        })
    }
//    val lst = listOf(
//        LiveResultInfo(
//            mid1 = 10.0, mid2 = 15.0,
//            q1 = 7.0, q2 = 8.0, q3 = 12.0, quiz = 9.0,
//            customCourseId = "CSE325", courseTitle = "Social and Professional Issues",
//            shortSemName = "Summer'23"
//        ),
//        LiveResultInfo(
//            mid1 = 10.0, mid2 = 15.0,
//            q1 = 7.0, q2 = 8.0, q3 = 12.0, quiz = 9.0,
//            customCourseId = "CSE325", courseTitle = "Social and Professional Issues in Computing",
//            shortSemName = "Summer'23"
//        ),
//        LiveResultInfo(
//            mid1 = 10.0, mid2 = 15.0,
//            q1 = 7.0, q2 = 8.0, q3 = 12.0, quiz = 9.0,
//            customCourseId = "CSE325", courseTitle = "Social and Professional Issues",
//            shortSemName = "Summer'23"
//        ),
//        LiveResultInfo(
//            mid1 = 10.0, mid2 = 15.0,
//            q1 = 7.0, q2 = 8.0, q3 = 12.0, quiz = 9.0,
//            customCourseId = "CSE325", courseTitle = "Social and Professional Issues",
//            shortSemName = "Summer'23"
//        ),
//    )
    Scaffold(topBar = {
        TopBar(navController){
            viewModel.updateUserLiveResultInfo(
                id = myId,
                accessToken = myFullProfile.token,
                myFullProfile.liveResultInfo
            ){
                Looper.prepare()
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }
    }) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(it)
            .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Live Result",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(vertical = 16.dp),
            )
            Text(
                text = if(myFullProfile.liveResultInfo.size>0) myFullProfile.liveResultInfo[0].shortSemName!! else "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                itemsIndexed(myFullProfile.liveResultInfo) { ind, courseResult ->
                    if(ind%2==0)
                        LiveRegCourseItem(courseResult)
                    else
                        LiveRegCourseItem(courseResult, Limerick1, Limerick2, Limerick3)
                }
            }
            LastUpdated(myFullProfile.lastUpdatedLiveResultInfo)

        }
    }
}

@Composable
fun LiveRegCourseItem(
    courseResultInfo: LiveResultInfo,
    lightColor: Color = BlueViolet1,
    mediumColor: Color = BlueViolet2,
    darkColor: Color = BlueViolet3
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(vertical = 7.5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .clickable {

            }
            .background(darkColor)
    ) {
        val width = constraints.maxWidth
        val height = 480

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
            Text(
                text = courseResultInfo.customCourseId!!,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                text = courseResultInfo.courseTitle!!,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .align(Alignment.End)
                    .alpha(.8f)
                ,
                textAlign = TextAlign.End

            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Quiz Average:  ${courseResultInfo.quiz}",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.size(3.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(.8f)
            ) {
                Text(
                    text = "Quiz 1:  ${courseResultInfo.q1}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical= 3.dp)
                )
                Text(
                    text = "Quiz 2:  ${courseResultInfo.q2}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
                Text(
                    text = "Quiz 3:  ${courseResultInfo.q3}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Midterm:  ${courseResultInfo.mid1}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(.5f)
            )
            Spacer(modifier = Modifier.size(3.dp))
            Text(
                text = "Midterm Improvement:  ${courseResultInfo.mid2}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.alpha(.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Previews3() {
    KnockMETheme {
        LiveResultViewScreen(rememberNavController())
    }
}