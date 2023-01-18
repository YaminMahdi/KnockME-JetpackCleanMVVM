package com.mlab.knockme.auth_feature.presentation.login.components

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.facebook.login.widget.LoginButton
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.presentation.login.LoginViewModel
import com.mlab.knockme.ui.theme.*
import com.mlab.knockme.ui.theme.KnockMETheme
import com.skydoves.cloudy.Cloudy

@Composable
fun LoginScreen(update: (View) -> Unit) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
//    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
//    {
//        Cloudy(radius =5){
//            Image(
//                painter = painterResource(R.drawable.bg),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxSize(),
//                contentScale = ContentScale.Crop,
//                colorFilter = ColorFilter
//                    .tint(Color(63, 183, 235,120), blendMode = BlendMode.Darken)
//            )
//        }
//    }
//    else
//    {
//        Image(
//            painter = painterResource(R.drawable.bg),
//            contentDescription = null,
//            modifier = Modifier
//                .height(screenHeight)
//                .blur(radius = 2.dp),
//            contentScale = ContentScale.Crop,
//            colorFilter = ColorFilter
//                .tint(Color(63, 183, 235,120), blendMode = BlendMode.Darken)
//        )
//    }


    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = DeepBlue
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .wrapContentSize(align = Alignment.TopCenter)
                .width(screenWidth-80.dp)
                .padding(top = 100.dp)
        )
        Box(
            modifier = Modifier
                .wrapContentSize(align = Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
        {
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(R.layout.login_btn, null, false)
                    // do whatever you want...
                    view // return the view
                },
                update = update


            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun Previews() {
    KnockMETheme {
        LoginScreen { view ->
            val btn = view.findViewById<LoginButton>(R.id.login_button)
            btn.setPermissions("id", "name", "link")
        }
    }
}