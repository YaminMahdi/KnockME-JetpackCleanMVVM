package com.mlab.knockme

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KnockME : Application()
{
    override fun onCreate() {
        super.onCreate()
        AppEventsLogger.activateApp(this);
    }
}