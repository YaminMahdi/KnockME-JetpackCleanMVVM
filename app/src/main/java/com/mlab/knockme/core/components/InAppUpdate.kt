package com.mlab.knockme.core.components

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.mlab.knockme.BuildConfig

/**
 * Must create an instance of this class in the activity root as val.
 * @param activity: provide ComponentActivity instance.
 */
class InAppUpdate(private val activity: ComponentActivity) {
    companion object{
        private var appUpdateManager: AppUpdateManager? = null
        private var isImmediateUpdateStarted = false
    }
    private val localVersionCode get() = BuildConfig.VERSION_CODE
    private var playVersionCode = 0

    private val updateFlowResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == FragmentActivity.RESULT_CANCELED) showImmediateUpdate()
        }

    /**
     * Checks that the update is available or not.
     * @param noUpdate: callback function that will be invoked when there is no update available.
     */
    fun checkForUpdate(noUpdate: () -> Unit = {}) {
        AppUpdateManagerFactory
            .create(activity)
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ||
                    appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED
                ) {
                    playVersionCode = appUpdateInfo.availableVersionCode()
                    if (playVersionCode > localVersionCode)
                        showImmediateUpdate()
                    Log.d("checkUpdate", "checkForUpdate: update available")
                } else {
                    Log.d("checkUpdate", "checkForUpdate: no update available")
                    noUpdate.invoke()
                }
            }
            .addOnFailureListener {
                Log.d("checkUpdate", "checkForUpdate: ${it.message}")
                noUpdate.invoke()
            }
    }

    /**
     * Show the update dialog to the user.
     * If the user cancels the update dialog then it will be shown again.
     */
    fun showImmediateUpdate() {
        if (isImmediateUpdateStarted) return
        isImmediateUpdateStarted = true
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        appUpdateManager?.let {manager ->
            manager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                manager.startUpdateFlowForResult(
                    appUpdateInfo, updateFlowResultLauncher,
                    AppUpdateOptions
                        .newBuilder(AppUpdateType.IMMEDIATE)
                        .build()
                )
            }.addOnFailureListener {
                isImmediateUpdateStarted = false
            }.addOnCanceledListener {
                isImmediateUpdateStarted = false
                showImmediateUpdate()
            }
        }

    }


    /**
     * Call this function in Activity's `onResume()` function.
     * This function handles the case when an immediate update was shown but the app was
     * closed before the update was completed. In this case, onResume should check if an update
     * is still pending and if so, resume the update.
     */
    fun onResume() {
        appUpdateManager
            ?: run { AppUpdateManagerFactory.create(activity).also { appUpdateManager = it } }
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    // However, you should execute this check at all entry points into the app.
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        showImmediateUpdate()
                    }
                }
    }
}

//implementation("com.google.android.play:app-update:2.1.0")
//implementation("com.google.android.play:app-update-ktx:2.1.0")