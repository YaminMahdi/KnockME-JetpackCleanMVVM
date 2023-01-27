package com.mlab.knockme.main_feature.presentation.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toDayPassed
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.profile_feature.presentation.components.Ic
import com.mlab.knockme.ui.theme.*


@Composable
fun ProfileViewScreen(
    id: String="193-15-107",
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()

) {
    val state by viewModel.stateProfile.collectAsState()

//    val isLoading by viewModel.isLoading.collectAsState()
//    val hasPrivateInfo  by viewModel.hasPrivateInfo.collectAsState()
//    val userBasicInfo  by viewModel.userBasicInfo.collectAsState()
    viewModel.getUserBasicInfo(id)
//    LaunchedEffect(key1 = state){
//
//    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(navController)
        Profile(
            pic = state.userBasicInfo.privateInfo?.pic,
            publicInfo = state.userBasicInfo.publicInfo,
            isLoading = state.isLoading
        )
        SocialLink(state.userBasicInfo.privateInfo!!, state.hasPrivateInfo)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)

        ) {
            if(!state.isLoading&&!state.userBasicInfo.fullResultInfo.isNullOrEmpty())
                BarChart(
                    state.userBasicInfo.fullResultInfo!!.map { it.toCombinedBarData() },
                    state.userBasicInfo.publicInfo
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
                text = "Last Updated: ${state.userBasicInfo.publicInfo?.lastUpdated?.toDayPassed()}",
                fontSize = 8.sp,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.End
            )
            Details(state.userBasicInfo, state.hasPrivateInfo)
            if(state.hasPrivateInfo)
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
fun TopBar(navController: NavHostController) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        BackBtn(navController)
        Ic(Icons.Rounded.Refresh)
    }
}

@Composable
fun BackBtn(navController: NavHostController) {
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
                    navController.popBackStack()
                }
            )
            .padding(10.dp)
    )
}

@Composable
fun Profile(pic: String?, publicInfo: PublicInfo?, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .width(300.dp),
        contentAlignment = Alignment.Center
    ) {
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
                text = if(!publicInfo?.nm.isNullOrEmpty()) publicInfo?.nm!! else "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        CgpaToast(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 185.dp, top = 25.dp),
            pb = publicInfo,
            isLoading = isLoading
        )


    }
}

@Composable
fun CgpaToast(modifier: Modifier, pb: PublicInfo?, isLoading: Boolean) {
    //var isLoaded by remember { mutableStateOf(!isLoading) }

    val toastHeight by animateDpAsState(
        targetValue = if (!isLoading) 25.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        )
    )
    val toastWidth by animateDpAsState(
        targetValue = if (!isLoading) 90.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        )
    )
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = modifier
            .height(toastHeight)
            .width(toastWidth)
            .bounceClick()
            .clip(RoundedCornerShape(20.dp))
            .background(LightGreen2)
            .clickable {
                clipboardManager.setText(AnnotatedString(pb?.id.orEmpty()))
                var intent =
                    context.packageManager.getLaunchIntentForPackage("net.startbit.diucgpa")
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //intent.data = Uri.parse("market://details?id=" + "net.startbit.diucgpa")
                    intent.data =
                        Uri.parse("https://play.google.com/store/apps/details?id=net.startbit.diucgpa")
                    context.startActivity(intent)
                }
                Toast
                    .makeText(
                        context,
                        "Student ID copied, paste it in DIU CGPA App.",
                        Toast.LENGTH_LONG
                    )
                    .show()

            }
    ) {
        if (!isLoading) Text(
            text = "CGPA: ${pb?.cgpa}",
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
fun SocialLink(privateInfo: PrivateInfoExtended, hasPrivateInfo: Boolean) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)

    ) {
        val uriHandler = LocalUriHandler.current
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .bounceClick()
            ,
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
        if(hasPrivateInfo)
        {
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
                            if (!privateInfo.fbLink.isNullOrEmpty())
                                uriHandler.openUri(privateInfo.fbLink!!)
                            else
                                Toast
                                    .makeText(context, "Link Not Valid", Toast.LENGTH_SHORT)
                                    .show()
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
                            if (!privateInfo.email.isNullOrEmpty()) {
                                try {
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.data = Uri.parse("mailto:")
                                    intent.type = "text/plain"
                                    //intent.type = "vnd.android.cursor.item/email" // or "message/rfc822"
                                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(privateInfo.email))
                                    //context.startActivity(intent)
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Choose Email Client..."
                                        )
                                    )

                                } catch (e: Exception) {
                                    Toast
                                        .makeText(
                                            context,
                                            "No Mailing App Found",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            } else
                                Toast
                                    .makeText(context, "No Email Found", Toast.LENGTH_SHORT)
                                    .show()
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
fun BarChart(barDataList: List<CombinedBarData>, pb: PublicInfo?) {
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

                onClick = {},// returns CombinedBarData}
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
fun Details(userBasicInfo: UserBasicInfo, hasPrivateInfo: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Details:",
            style = MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(5.dp),
        ) {
            DetailsItems("ID: ${userBasicInfo.publicInfo!!.id}", LightGreen2)
            DetailsItems("Batch: ${userBasicInfo.publicInfo.batchNo}", BlueViolet1)
            if(hasPrivateInfo)
                DetailsItems("Blood: ${userBasicInfo.privateInfo!!.bloodGroup}", LightRed)
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
            DetailsItems("Current: ${userBasicInfo.privateInfo!!.loc}", Beige3)
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
fun ProfileViewScreenPr() {
    KnockMETheme {
        ProfileViewScreen(navController= rememberNavController())
    }

}