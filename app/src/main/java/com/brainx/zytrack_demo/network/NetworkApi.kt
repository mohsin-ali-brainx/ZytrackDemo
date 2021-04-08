package com.brainx.zytrack_demo.network

import com.brainx.zytrack_demo.network.apiServices.ZytrackApiService
import com.brainx.androidbase.network.NetworkApi

val ZYTRACK_SERVICE: ZytrackApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ZytrackApiService::class.java)
}