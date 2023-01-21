package com.mlab.knockme.main_feature.presentation.main.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.himanshoe.charty.combined.CombinedBarChart
import com.himanshoe.charty.combined.config.CombinedBarConfig
import com.himanshoe.charty.combined.model.CombinedBarData
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.common.dimens.ChartDimens
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.PrivateInfo
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.profile_feature.presentation.components.Ic
import com.mlab.knockme.ui.theme.*

@Composable
fun ProfileViewScreen(
    id: String="193-15-1071",
    viewModel: MainViewModel = hiltViewModel()

) {
    val state by viewModel.stateProfile.collectAsState()

    LaunchedEffect(key1 = state){
        viewModel.getUserBasicInfo("193-15-1071")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar()
        Profile(state.userBasicInfo.privateInfo.pic, state.userBasicInfo.publicInfo.nm)
        SocialLink(state.userBasicInfo.privateInfo)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)

        ) {
            if(!state.isLoading)
            {
                BarChart(state.userBasicInfo.fullResultInfo.map { it.toCombinedBarData() })
            }
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
                text = "Last Updated: A Day Ago",
                fontSize = 8.sp,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.End
            )
            Details(state.userBasicInfo)
            Address(state.userBasicInfo)
        }

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
fun TopBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(55.dp)
                .bounceClick()
                .clip(RoundedCornerShape(30.dp))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = Color.White),
                    onClick = {

                    }
                )
                .padding(10.dp)
        )
        Ic(Icons.Rounded.Refresh)
    }
}

@Composable
fun Profile(pic: String, name: String) {
    Box(
        modifier = Modifier
            .width(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SubcomposeAsyncImage(
                model = pic,
                contentDescription = name,
                modifier = Modifier
                    .width(160.dp)
                    .aspectRatio(1f)
                    .padding(15.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(DarkerButtonBlue)
            ) {
                when (painter.state) {
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
                text = name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        CgpaToast(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 185.dp, top = 25.dp), "3.65"
        )


    }
}

@Composable
fun CgpaToast(modifier: Modifier, cgpa: String) {
    var isLoaded by remember { mutableStateOf(false) }

    val toastHeight by animateDpAsState(
        targetValue = if (isLoaded) 25.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        )
    )
    val toastWidth by animateDpAsState(
        targetValue = if (isLoaded) 90.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        )
    )
    Box(
        modifier = modifier
            .height(toastHeight)
            .width(toastWidth)
            .clip(RoundedCornerShape(20.dp))
            .background(LightGreen2)
            .clickable { isLoaded = !isLoaded }
    ) {
        if (isLoaded) Text(
            text = "CGPA: $cgpa",
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

@Composable
fun SocialLink(privateInfo: PrivateInfoExtended) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)

    ) {
        Button(
            modifier = Modifier
                .bounceClick(),
            onClick = {

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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 14.dp)
                .bounceClick()
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = Color.White),
                    onClick = {
                        privateInfo.fbLink
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
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = Color.White),
                    onClick = {
                        privateInfo.email
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

@Composable
fun BarChart(barDataList: List<CombinedBarData>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "SGPA Graph:",
            style = MaterialTheme.typography.headlineSmall
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .padding(top = 10.dp)
                .bounceClick()
                .clip(RoundedCornerShape(10.dp))
                .background(DeepBlueLess)
        ) {
            CombinedBarChart(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 40.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                onClick = {

                },// returns CombinedBarData}
                barColors = listOf(Beige1, LightRed, BlueViolet1),
                lineColors = listOf(Color.Transparent, Color.Transparent),
                combinedBarData = barDataList,
                combinedBarConfig = CombinedBarConfig(
                    hasRoundedCorner = true,
                    hasLineLabel = true,
                    lineLabelColor = Color.Transparent to Color.White
                ),
                axisConfig = AxisConfig(
                    xAxisColor = Color.LightGray,
                    showAxis = true,
                    isAxisDashed = false,
                    showUnitLabels = false,
                    showXLabels = true,
                    yAxisColor = Color.LightGray,
                    textColor = Color.White
                ),
                chartDimens = ChartDimens(
                    if (barDataList.size < 2)
                        150.dp
                    else if (barDataList.size < 3)
                        100.dp
                    else if (barDataList.size < 5)
                        50.dp
                    else if (barDataList.size < 7)
                        20.dp
                    else if (barDataList.size < 9)
                        10.dp
                    else
                        30.dp
                )
            )
        }

    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Details(userBasicInfo: UserBasicInfo) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Details:",
            style = MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(5.dp),
        ) {
            DetailsItems("ID: ${userBasicInfo.publicInfo.id}", LightGreen2)
            DetailsItems("Batch: ${userBasicInfo.publicInfo.batchNo}", BlueViolet1)
            DetailsItems("Blood: ${userBasicInfo.privateInfo.bloodGroup}", LightRed)
            DetailsItems("Prog: ${userBasicInfo.publicInfo.progShortName}", LightBlue)
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
            DetailsItems("Current: ${userBasicInfo.privateInfo.loc}", Beige3)
            DetailsItems("Home: ${userBasicInfo.privateInfo.permanentHouse}", DarkerButtonBlue)
        }

    }
}

@Composable
fun DetailsItems(data: String, color: Color) {
    Box(
        modifier = Modifier
            .padding(top = 10.dp, end = 10.dp)
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .clickable { }
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
    KnockMETheme() {
        ProfileViewScreen()
    }

}