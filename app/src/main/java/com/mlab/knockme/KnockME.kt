package com.mlab.knockme

import android.app.Application
import android.content.SharedPreferences
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KnockME : SingletonImageLoader.Factory, Application() {
    val pref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
    }

    override fun onCreate() {
        super.onCreate()
        AppEventsLogger.activateApp(this)
        instance = this
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(500)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.30)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .build()
    }

    companion object {
        lateinit var instance: KnockME
            private set
    }
}

val pref: SharedPreferences by lazy {
    KnockME.instance.pref
}