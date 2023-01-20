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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
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
        TopBar()
        Profile(",", "Ahmad Umar Mahdi")
        SocialLink()
        Details()
        Address()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(26.dp),
        contentAlignment = Alignment.BottomEnd)
    {
        Box(modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(BlueViolet1)
        .padding(10.dp)
        ){
            Column(modifier = Modifier
                .align(Alignment.Center)) {
                Text(
                    text = "Last Updated:",
                    fontSize=10.sp,
                    style=MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "5 Days Ago",
                    style=MaterialTheme.typography.headlineMedium,
                )
            }
        }

    }

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
fun SocialLink() {
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
            Row (Modifier.padding(vertical = 7.dp)){
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
                    fontSize=22.sp,
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

                    }
                )
                .background(DeepBlueLess)
                .padding(10.dp)
                .size(35.dp)
        ) {
            Text(
                text = "@",
                fontSize=24.sp,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = ubuntu,
                modifier = Modifier
                    .padding(bottom=3.dp)
            )
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Details() {
    Column(modifier= Modifier.fillMaxWidth()) {
        Text(
            text = "Details:",
            style=MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(5.dp),
        ) {
            DetailsItems("ID: 193-15-1071", LightGreen2)
            DetailsItems("Batch: 54", BlueViolet1)
            DetailsItems("Blood: O+", LightRed)
            DetailsItems("Prog: B.Sc. in CSE", LightBlue)
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Address() {
    Column(modifier= Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.size(25.dp))
        Text(
            text = "Address:",
            style=MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            modifier = Modifier.padding(5.dp),
        ) {
            DetailsItems("Current: Dhaka, Bangladesh", Beige3)
            DetailsItems("Home: Jessore, Jessore, Jessore, Khulna, Bangladesh", DarkerButtonBlue)
        }

    }
}

@Composable
fun DetailsItems(data: String,color: Color) {
    Box(modifier = Modifier
        .padding(top = 10.dp, end = 10.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(color)
        .padding(10.dp)
    ){
        Text(
            text = data,
            style=MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
    
}

@Preview(showBackground = true)
@Composable
fun ProfilelVIewScreenPre() {
    KnockMETheme() {
        ProfilelVIewScreen()
    }

}