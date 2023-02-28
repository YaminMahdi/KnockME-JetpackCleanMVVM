package com.mlab.knockme.auth_feature.presentation.login.components

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import com.mlab.knockme.R
import com.mlab.knockme.ui.theme.KnockMETheme

@SuppressLint("InflateParams")
@Composable
fun FacebookLoginBtn(update: (View) -> Unit)
{
    AndroidView(
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.fb_login_btn, null, false)
            view.setOnClickListener {

            }
            // do whatever you want...
            view // return the view
        },
        update = update


    )
}

@SuppressLint("InflateParams")
@Composable
fun GoogleLoginBtn(update: (View) -> Unit)
{
    AndroidView(
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.google_login_btn, null, false)
            view
                .findViewById<SignInButton>(R.id.sign_in_button)
                .setSize(SignInButton.SIZE_WIDE)
            view
        },
        update = update
    )
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    KnockMETheme {
        Column {

        }
        FacebookLoginBtn { view ->
            val btn = view.findViewById<LoginButton>(R.id.login_button)
            btn.setPermissions("id", "name", "link")

        }
        GoogleLoginBtn{ view ->


        }
    }
}