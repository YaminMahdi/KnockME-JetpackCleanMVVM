package com.mlab.knockme.auth_feature.presentation.login.components

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.login.widget.LoginButton
import com.mlab.knockme.R
import com.mlab.knockme.ui.theme.KnockMETheme

@SuppressLint("InflateParams")
@Composable
fun LoginBtn(update: (View) -> Unit)
{
    AndroidView(
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.login_btn, null, false)
            view.setOnClickListener {

            }
            // do whatever you want...
            view // return the view
        },
        update = update


    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    KnockMETheme {
        LoginBtn { view ->
            val btn = view.findViewById<LoginButton>(R.id.login_button)
            btn.setPermissions("id", "name", "link")

        }
    }
}