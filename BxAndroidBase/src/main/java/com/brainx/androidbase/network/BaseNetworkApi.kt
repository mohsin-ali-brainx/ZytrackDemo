package com.brainx.androidbase.network

import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit

abstract class BaseNetworkApi {

    companion object {
        lateinit var baseUrl: String
    }

    fun <T> getApi(serviceClass: Class<T>, ): T {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
        return setRetrofitBuilder(retrofitBuilder).build().create(serviceClass)
    }

    abstract fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder
    abstract fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder

    private val okHttpClient: OkHttpClient
        get() {
            var builder = RetrofitUrlManager.getInstance().with(OkHttpClient.Builder())
            builder = setHttpClientBuilder(builder)
            return builder.build()
        }
}



