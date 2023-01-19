package com.mlab.knockme.main_feature.presentation.main.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.profile_feature.presentation.components.Ic
import com.mlab.knockme.ui.theme.*

@Composable
fun ProfilelVIewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Topbar()
        Profile(",","Ahmad Umar Mahdi")

    }

}

@Composable
fun Topbar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = "",
                tint = Color.White)
        }
        Ic(Icons.Rounded.Refresh)
    }
}

@Composable
fun Profile(pic: String,name:String) {
    Box(modifier = Modifier
        .width(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
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
            Text(
                text = name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        CgpaToast(modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = 185.dp,top = 25.dp)
            ,"3.65")


    }
}

@Composable
fun CgpaToast(modifier: Modifier,cgpa:String) {
    var isLoaded by remember { mutableStateOf(false) }

    val toastHeight by animateDpAsState(
        targetValue = if (isLoaded) 25.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        )
    )
    val toastwidth by animateDpAsState(
        targetValue = if (isLoaded) 90.dp else 15.dp, animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium
        )
    )
    Box(
        modifier = modifier
            .height(toastHeight)
            .width(toastwidth)
            .clip(RoundedCornerShape(20.dp))
            .background(LightGreen2)
            .clickable { isLoaded =!isLoaded }
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

@Preview(showBackground = true)
@Composable
fun ProfilelVIewScreenPre() {
    KnockMETheme() {
        ProfilelVIewScreen()
    }

}