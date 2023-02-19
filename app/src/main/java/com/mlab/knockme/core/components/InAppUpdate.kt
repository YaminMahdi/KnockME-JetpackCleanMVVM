package com.mlab.knockme.core.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.ktx.AppUpdateResult
import se.warting.inappupdate.compose.rememberInAppUpdateState

private const val APP_UPDATE_REQUEST_CODE = 86500

@Composable
fun InAppUpdate() {
    val updateState = rememberInAppUpdateState()
    val context = LocalContext.current
    when (val result = updateState.appUpdateResult) {
        is AppUpdateResult.NotAvailable -> return
        is AppUpdateResult.Available ->  result.startImmediateUpdate(context as Activity, APP_UPDATE_REQUEST_CODE)
        is AppUpdateResult.InProgress -> return
        is AppUpdateResult.Downloaded -> return
    }
}