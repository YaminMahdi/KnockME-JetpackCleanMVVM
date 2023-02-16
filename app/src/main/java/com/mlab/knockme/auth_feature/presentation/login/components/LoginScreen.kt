package com.mlab.knockme.auth_feature.presentation.login.components

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize(align = Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier
                    .width(335.dp)
                    .padding(top = 100.dp)
            )
            Text(
                text = "A DIU Student Portal Backup Server & Social App",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 80.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize(align = Alignment.BottomCenter)
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
            //Spacer(modifier = Modifier.padding(10.dp))
            NbNote(text = "FB Login is needed to save Portal info in the backup server")
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