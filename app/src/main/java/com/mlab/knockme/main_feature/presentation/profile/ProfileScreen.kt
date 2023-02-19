package com.mlab.knockme.main_feature.presentation.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toK
import com.mlab.knockme.core.util.toWords
import com.mlab.knockme.main_feature.presentation.ChatInnerScreens
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.main_feature.presentation.ProfileInnerScreens
import com.mlab.knockme.main_feature.presentation.profile.components.Feature
import com.mlab.knockme.profile_feature.presentation.components.standardQuadFromTo
import com.mlab.knockme.ui.theme.*

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: MainViewModel= hiltViewModel()
) {
    val context: Context = LocalContext.current
    //val state by viewModel.state.collectAsState()
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    //val preferencesEditor = sharedPreferences.edit()
    val myId = sharedPreferences.getString("studentId","0")!!
    LaunchedEffect(key1 = "1")
    {
        viewModel.getUserFullProfileInfo(myId,{
            viewModel.updateUserFullResultInfo(
                publicInfo = it.publicInfo,
                fullResultInfoList = it.fullResultInfo)
        },{
            Looper.prepare()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Looper.loop()
        })
    }
    val myFullProfile by viewModel.userFullProfileInfo.collectAsState()

    InfoDialog(viewModel,context,myId,navController)
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo("Profile"){
            viewModel.setInfoDialogVisibility(true)
        }
        PersonInfo(
            myFullProfile.publicInfo.nm!!,
            myFullProfile.privateInfo.pic!!,
            myFullProfile.publicInfo.progShortName!!,
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
            .background(DarkerButtonBlue)
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
@Composable
fun InfoDialog(
    viewModel: MainViewModel,
    context: Context,
    myId: String,
    navController: NavHostController
) {
    val dialogVisibility by viewModel.infoDialogVisibility.collectAsState()
    val uriHandler = LocalUriHandler.current

    if (dialogVisibility) {
        Dialog(
            onDismissRequest = { viewModel.setInfoDialogVisibility(false) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueViolet0)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        text = "Made With Love \uD83D\uDC95",
                        style = MaterialTheme.typography.headlineLarge,
                        color = LightRed,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    )
                    Text(
                        text = "By",
                        fontFamily = ubuntu,
                        color = LightRed,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.setInfoDialogVisibility(false)
                                navController.navigate(ChatInnerScreens.UserProfileScreen.route + "193-15-1071")
                            }
                            .padding(15.dp)

                    ) {
                        Text(
                            text = "Ahmad Umar Mahdi",
                            fontFamily = ubuntu,
                            fontWeight = FontWeight.Bold,
                            color = Neutral30,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                        )
                        Text(
                            text = "193-15-1071",
                            fontFamily = ubuntu,
                            color = Limerick3,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.size(35.dp))
                    Text(
                        text = "knock-me.github.io",
                        fontFamily = ubuntu,
                        color = BlueViolet3,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .clickable {
                                uriHandler.openUri("https://knock-me.github.io")
                            }
                            .padding(5.dp)
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = "Report a problem",
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        fontFamily = ubuntu,
                        color = Neutral50,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    val body = "My Student ID is $myId. The issue is.."
                                    val data =
                                        Uri.parse("mailto:ahmad15-1071@diu.edu.bd?subject=Having Issue&body=$body")
                                    intent.data = data
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No Mailing App Found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(5.dp)
                    )
                }
            }

        }
    }
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
            .clickable { navController.navigate(ChatInnerScreens.UserProfileScreen.route + id) }
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
            when (painter.state) {
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
                fontWeight = FontWeight.Bold
            )
            Text(
                text = id,
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
            modifier = Modifier.padding(15.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(features.size) {
                FeatureItem(feature = features[it]){
                    when (it) {
                        0 -> navController.navigate(ProfileInnerScreens.CgpaScreen.route+id)
                        1 -> navController.navigate(ProfileInnerScreens.DueScreen.route)
                        2 -> navController.navigate(ProfileInnerScreens.RegCourseScreen.route)
                        3 -> navController.navigate(ProfileInnerScreens.LiveResultScreen.route)


                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    feature: Feature,
    onClick: (() -> Unit)
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .aspectRatio(1f)
            .bounceClick()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick.invoke() }
            .background(feature.darkColor)
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight

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
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(vertical = 6.dp, horizontal = 15.dp)
            )
        }
        Text(
            text = feature.title,
            style = MaterialTheme.typography.headlineMedium,
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