package com.mlab.knockme.main_feature.presentation.profile

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.ClearanceInfo
import com.mlab.knockme.core.util.toShortSemester
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.ui.theme.*

@Composable
fun ClearanceViewScreen(navController: NavHostController, viewModel: MainViewModel = hiltViewModel()) {
    val context: Context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    val myId = sharedPreferences.getString("studentId",null)!!
    val myFullProfile by viewModel.userFullProfileInfo.collectAsState()

    LaunchedEffect(key1 = ""){
        viewModel.getUserFullProfileInfo(myId,{
            viewModel.updateClearanceInfo(
                id = myId,
                accessToken = it.token,
                clearanceInfoList = it.clearanceInfo
            ){msg ->
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
//        ClearanceInfo(
//            finalExam = false,
//            midTermExam = false,
//            registration = true,
//            semesterId = "55",
//            semesterName = "Summer, 2021"
//        ),
//        ClearanceInfo(
//            finalExam = false,
//            midTermExam = true,
//            registration = false,
//            semesterId = "55",
//            semesterName = "Fall, 2021"
//        ),
//        ClearanceInfo(
//            finalExam = true,
//            midTermExam = false,
//            registration = false,
//            semesterId = "55",
//            semesterName = "Summer, 2021"
//        ),
//        ClearanceInfo(
//            finalExam = false,
//            midTermExam = false,
//            registration = false,
//            semesterId = "55",
//            semesterName = "Spring, 2021"
//        )
//    )
    Scaffold(topBar = {
        TopBar(navController){
            viewModel.updateClearanceInfo(
                id = myId,
                accessToken = myFullProfile.token,
                clearanceInfoList = myFullProfile.clearanceInfo
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
                text = "Clearance",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
            Box(modifier = Modifier.weight(1f)){
                Column(modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BlueViolet3)
                            .padding(10.dp)
                            .padding(vertical = 10.dp)
                            .padding(start = 10.dp)
                    ) {
                        Text(
                            text = "Semester",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = ubuntu,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color.DarkGray,
                                    offset = Offset(.5f, .5f),
                                    blurRadius = 1f
                                )
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .fillMaxWidth(.55f)
                                .align(Alignment.CenterEnd)
                        ){
                            Text(
                                text = "Reg.",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = ubuntu,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.DarkGray,
                                        offset = Offset(.5f, .5f),
                                        blurRadius = 1f
                                    )
                                )
                            )
                            Text(
                                text = "Mid",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = ubuntu,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.DarkGray,
                                        offset = Offset(.5f, .5f),
                                        blurRadius = 1f
                                    )
                                )
                            )
                            Text(
                                text = "Final",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = ubuntu,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = Color.DarkGray,
                                        offset = Offset(.5f, .5f),
                                        blurRadius = 1f
                                    )
                                )
                            )
                        }

                    }
                    LazyColumn{
                        items(myFullProfile.clearanceInfo.size) { ind->
                            if(ind%2==0)
                                ClearanceItem(myFullProfile.clearanceInfo[ind],BlueViolet1)
                            else
                                ClearanceItem(myFullProfile.clearanceInfo[ind], BlueViolet0)
                        }
                    }
                }
            }
            LastUpdated(myFullProfile.lastUpdatedClearanceInfo)
        }
    }
}

@Composable
fun ClearanceItem(
    clearanceInfo: ClearanceInfo,
    color: Color=BlueViolet1,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .padding(start = 10.dp)
    ) {
        Text(
            text = clearanceInfo.semesterName.toShortSemester(),
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = ubuntu,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.DarkGray,
                    offset = Offset(.5f, .5f),
                    blurRadius = 1f
                )
            ),
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(end = 12.dp)
                .fillMaxWidth(.575f)
                .align(Alignment.CenterEnd)
        ){

            Icon(
                imageVector = if(clearanceInfo.registration) Icons.Rounded.Done else Icons.Rounded.Close,
                contentDescription = "",
                tint = if(clearanceInfo.registration) LessGreen else LessRed,
                modifier = Modifier.size(45.dp)
            )
            Icon(
                imageVector = if(clearanceInfo.midTermExam) Icons.Rounded.Done else Icons.Rounded.Close,
                contentDescription = "",
                tint = if(clearanceInfo.midTermExam) LessGreen else LessRed,
                modifier = Modifier.size(45.dp)
            )
            Icon(
                imageVector = if(clearanceInfo.finalExam) Icons.Rounded.Done else Icons.Rounded.Close,
                contentDescription = "",
                tint = if(clearanceInfo.finalExam) LessGreen else LessRed,
                modifier = Modifier.size(45.dp)
            )
        }

    }
}
@Preview(showBackground = true)
@Composable
fun Previews6() {
    KnockMETheme {
        ClearanceViewScreen(rememberNavController())
    }
}