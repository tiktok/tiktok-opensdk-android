package com.bytedance.sdk.account

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetUtils {
    fun <T> createApi(apiClass: Class<T>): T {
        var retrofitBuilder = Retrofit.Builder()
        retrofitBuilder.baseUrl("https:\\open.douyin.com")
        var retrofit = retrofitBuilder
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(apiClass)
    }
}