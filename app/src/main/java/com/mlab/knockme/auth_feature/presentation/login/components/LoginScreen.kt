package com.mlab.knockme.auth_feature.presentation.login.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.common.SignInButton
import com.mlab.knockme.R
import com.mlab.knockme.core.util.Constants
import com.mlab.knockme.main_feature.presentation.profile.ReportProblem
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme
import com.mlab.knockme.ui.theme.TextBlue

@SuppressLint("InflateParams")
@Composable
fun LoginScreen(fbUpdate: (View) -> Unit, googleUpdate: (View) -> Unit) {
    val context = LocalContext.current
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//    val screenWidth = configuration.screenWidthDp.dp
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
                    .padding(top = 50.dp)
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
                    val view = LayoutInflater.from(context).inflate(R.layout.fb_login_btn, null, false)
                    // do whatever you want...
                    view // return the view
                },
                update = fbUpdate
            )
            Text(text = "or", modifier = Modifier.padding(top=5.dp, bottom = 8.dp))
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(R.layout.google_login_btn, null, false)
                    val btn = view.findViewById<SignInButton>(R.id.sign_in_button)
                    btn.setSize(SignInButton.SIZE_WIDE)
                    view
                },
                update = googleUpdate
            )
            //Spacer(modifier = Modifier.padding(10.dp))
            //NbNote(text = "FB Login is needed to save Portal info in the backup server")
            NbNote(
                modifier = Modifier.padding(top = 70.dp),
                text = "The application is secure because it's open source on ",
                linkText = "GitHub",
                link = Constants.KNOCK_ME_GIT_URL
            )
            ReportProblem(
                context = context,
                myId = "xxx-xx-xxxx",
                modifier= Modifier
                    .padding(20.dp)
                    .padding(bottom = 40.dp),
                color = TextBlue.copy(.7f)
            )
        }
//        ReportProblem(
//            context = context,
//            myId = "xxx-xx-xxxx",
//            modifier= Modifier.wrapContentSize(Alignment.BottomCenter).padding(20.dp),
//            color = TextBlue.copy(.7f)
//        )

    }
}

@Preview(showBackground = true)
@Composable
fun Previews() {
    KnockMETheme {
        LoginScreen ({
//            val btn = view.findViewById<LoginButton>(R.id.login_button)
//            btn.setPermissions("id", "name", "link")
        },{})
    }
}