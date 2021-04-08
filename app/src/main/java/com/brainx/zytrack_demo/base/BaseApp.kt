package com.brainx.zytrack_demo.base

import androidx.appcompat.app.AppCompatDelegate
import com.brainx.androidbase.base.BxBaseApp
import com.brainx.zytrack_demo.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApp : BxBaseApp() {
    override fun onCreate() {
        super.onCreate()
        setNetworkBaseUrl(BuildConfig.BASE_URL)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
