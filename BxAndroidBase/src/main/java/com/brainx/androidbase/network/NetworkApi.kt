package com.brainx.androidbase.network


import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws


class NetworkApi : BaseNetworkApi() {

    companion object {
        var headerMap: HashMap<String, String> = hashMapOf()

        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }
        fun setBaseUrl(baseUrl: String) {
            BaseNetworkApi.baseUrl = baseUrl
        }
    }


    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val a = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val builder = chain.request().newBuilder()
                headerMap.forEach {
                    builder.addHeader(it.key, it.value).build()
                }
                return chain.proceed(builder.build())
            }
        }
        builder.apply {
            addInterceptor(logging)
            addInterceptor(a)
            connectTimeout(120, TimeUnit.SECONDS)
            readTimeout(120, TimeUnit.SECONDS)
            writeTimeout(120, TimeUnit.SECONDS)
        }
        return builder
    }

    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        }
    }


}



