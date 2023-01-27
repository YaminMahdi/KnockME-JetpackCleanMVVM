package com.mlab.knockme.main_feature.presentation.messages

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toDateTime
import com.mlab.knockme.core.util.toDayPassed
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.presentation.ChatInnerScreens
import com.mlab.knockme.main_feature.presentation.main.BackBtn
import com.mlab.knockme.ui.theme.*

@Composable
fun MsgViewScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(DeepBlue)
    ) {
        MsgTopBar()
        LoadMsgList(
            msgList =
            listOf(
                Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
                Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
                Msg("193","Yamin Mahdi", "hi, I'm mahdi hi, I'm mahdi hi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdihi, I'm mahdi","",46238423),
                Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
                Msg("193","Yamin Mahdi", "hi, I'm mahdi","",46238423),
                Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423),
                Msg("13","Yamin Mahdi", "hi, I'm mahdi","",46238423)
            ), rememberNavController()
        )
    }
    
}

@Composable
fun MsgTopBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlueLess)
            .padding(5.dp)
    ) {
        BackBtn(rememberNavController())
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = Color.White),
                    onClick = {

                    }
                )
        ) {
            ImgView(
                img = "",
                modifier = Modifier
                    .height(70.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50.dp))


            )
            Text(
                text = "Ahmad Umar Mahdi",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth(.8f)
                ,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
                )
        }
    }
}

@Composable
fun ImgView(img: String,modifier: Modifier) {
    SubcomposeAsyncImage(
        model = img,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f)
            .background(DarkerButtonBlue)
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = AquaBlue,
                    modifier = Modifier
                        .padding(20.dp)
                )
            }
            is AsyncImagePainter.State.Error -> {
                SubcomposeAsyncImageContent(
                    painter = painterResource(id = R.drawable.ic_profile),
                    alpha = .7F,
                    modifier = Modifier
                        .padding(6.dp)
                )}
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }

}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadMsgList(msgList: List<Msg>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 10.dp)
    ) {
        items(msgList) { msg ->
            if(msg.id=="193")
                MsgViewRight(msg) { id ->
                    navController.navigate(ChatInnerScreens.UserProfileScreen.route+id)
                }
            else
                MsgViewLeft(msg) { id ->
                    navController.navigate(ChatInnerScreens.UserProfileScreen.route+id)
                }
        }
    }
}

@Composable
fun MsgViewLeft(msg: Msg,onClick:(id: String)->Unit) {
    Row() {
        ImgView(img = msg.pic!!, modifier = Modifier
            .height(40.dp)
            .padding(top = 5.dp)
            .bounceClick()
            .clip(
                RoundedCornerShape(
                    topStart = 50.dp,
                    topEnd = 20.dp,
                    bottomStart = 50.dp,
                    bottomEnd = 40.dp
                )
            )
            .clickable {
                onClick.invoke(msg.id!!)
            }
        )
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.7f)
            ){
                Column(modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(horizontal = 5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 7.dp,
                            topEnd = 14.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .background(DeepBlueLess)
                    .padding(8.dp)
                ) {
                    Text(
                        text = msg.nm!!,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .bounceClick()
                            .clip(RoundedCornerShape(2.dp))
                            .clickable {
                                onClick.invoke(msg.id!!)
                            }
                    )
                    Text(
                        text = msg.msg!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextBlue
                    )

                }
            }
            Text(
                modifier = Modifier
                    .alpha(.7f)
                    .padding(start = 6.dp)
                    .padding(2.dp),
                text = msg.time?.toDateTime()!!,
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@Composable
fun MsgViewRight(msg: Msg,onClick:(id: String)->Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth(.7f)
            ){
                Column(modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(horizontal = 5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 7.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .background(DeepBlueMoreLess)
                    .padding(8.dp)
                ) {
                    Text(
                        text = msg.nm!!,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .bounceClick()
                            .clip(RoundedCornerShape(2.dp))
                            .clickable {
                                onClick.invoke(msg.id!!)
                            }
                    )
                    Text(
                        text = msg.msg!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextBlue
                    )

                }
            }
            Text(
                modifier = Modifier
                    .alpha(.7f)
                    .padding(start = 6.dp)
                    .padding(2.dp),
                text = msg.time?.toDateTime()!!,
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }
        ImgView(img = msg.pic!!, modifier = Modifier
            .height(40.dp)
            .padding(top = 5.dp)
            .bounceClick()
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 50.dp,
                    bottomStart = 40.dp,
                    bottomEnd = 50.dp
                )
            )
            .clickable {
                onClick.invoke(msg.id!!)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsMsgViewScreen() {
    KnockMETheme {
        MsgViewScreen()
    }
}