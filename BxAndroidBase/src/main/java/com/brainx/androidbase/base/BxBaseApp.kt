package com.brainx.androidbase.base

import android.app.Application
import android.content.Context
import com.brainx.androidbase.network.NetworkApi

open class BxBaseApp : Application() {

    //region Properties
    companion object {
        lateinit var appInstance: BxBaseApp
        fun getApplication(): BxBaseApp = appInstance
        fun getTmContext(): Context = appInstance.applicationContext
    }
    //endregion

    //region LifeCycle
    override fun onCreate() {
        appInstance = this
        super.onCreate()
    }
    //endregion

    //region public method
    fun setNetworkBaseUrl(baseUrl: String) = NetworkApi.setBaseUrl(baseUrl)
    //endregion

}