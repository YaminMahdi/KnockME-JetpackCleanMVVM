package com.mlab.knockme.main_feature.presentation.chats.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.presentation.MainViewModel
import com.mlab.knockme.profile_feature.presentation.components.TitleInfo
import com.mlab.knockme.ui.theme.*

@Composable
fun ChatPersonalScreen(
    viewModel: MainViewModel= hiltViewModel()
) {
    val context: Context =LocalContext.current
    val state by viewModel.state.collectAsState()
    LaunchedEffect(key1 = true){
        //pop backstack
        viewModel.getChatProfiles("personalMsg/193-15-1071/profiles"){
            Toast.makeText(context, "Chat couldn't be loaded- $it", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo(title = "Personal")
        SearchBox()

        LoadChatList(state.chatList)
//        LoadChatList(chatList =
//        listOf(
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "fgnfdjgnfjdnhgffgnhngnhfjknh nhfhnfghfghfgjhihi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi"),
//            UserChatInfo("Yamin Mahdi","", lastMsg = "hi, I'm mahdi")
//        ))
    }
//    Box(
//        modifier = Modifier
//            .background(DeepBlue)
//            .fillMaxSize())
//    {
//
//        //BottomNav(modifier  = Modifier.align(Alignment.BottomCenter))
//
//    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox() {
    var data by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = data,
        placeholder = { Text("Type Student ID.") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "search",
                modifier = Modifier.padding(start=15.dp)
            )},
        shape = RoundedCornerShape(30.dp),
        colors=searchFieldColors(),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onValueChange = {
            data = it
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchFieldColors() =
    TextFieldDefaults.textFieldColors(
        textColor = DeepBlueLess,
        containerColor = DeepBlueLess,
        cursorColor = AquaBlue,
        placeholderColor = DeepBlueLess,
        focusedIndicatorColor =  DarkerButtonBlue,
        unfocusedIndicatorColor = DarkerButtonBlue,
        focusedLeadingIconColor = TextWhite,
        unfocusedLeadingIconColor = LessWhite
    )

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadChatList(chatList: List<Msg>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        content = {
            items(chatList) { proView ->
                ChatView(
                    proView,
                    modifier = Modifier
                        .animateItemPlacement()
                )
            }
        }
    )
}

@Composable
fun ChatView(proView: Msg, modifier: Modifier) {
    Row(
        verticalAlignment=Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .clickable {

            }
    ) {
        SubcomposeAsyncImage(
            model = proView.pic,
            contentDescription = proView.nm,
            modifier = Modifier
                .height(100.dp)
                .aspectRatio(1f)
                .padding(5.dp)
                .clip(RoundedCornerShape(50.dp))
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
                        alpha = .7F,
                        modifier = Modifier
                            .padding(10.dp)
                    )}
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)

        ) {
            Text(
                text = proView.nm,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
            Text(
                text = proView.msg,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .padding(vertical = 5.dp)
            )
            Text(
                text = proView.time.toString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsProfile() {
    KnockMETheme {
//        ChatPersonalScreen()
    }
}