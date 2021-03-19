package com.bytedance.sdk.account

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetUtils {
    fun <T> createApi(apiClass: Class<T>, isBoe: Boolean?): T {
        return createApi(apiClass)
    }

    private fun <T> createApi(apiClass: Class<T>): T {
        val retrofitBuilder = Retrofit.Builder()

        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        retrofitBuilder.baseUrl("https:\\\\open-api.tiktok.com")
        val retrofit = retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        return retrofit.create(apiClass)
    }
}