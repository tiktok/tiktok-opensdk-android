package com.bytedance.sdk.account

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object NetUtils {

    private lateinit var baseUrl: String;

    fun <T> createApi(apiClass: Class<T>, isBoe: Boolean?): T {
        baseUrl = "https:\\\\open-api.tiktok.com"
        isBoe?.let {
            if (isBoe) {
                baseUrl = "https:\\open-api-boei18n.bytedance.net"
            }
        }

        val retrofitBuilder = Retrofit.Builder()

        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        retrofitBuilder.baseUrl(baseUrl)
        val retrofit = retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        return retrofit.create(apiClass)
    }
}