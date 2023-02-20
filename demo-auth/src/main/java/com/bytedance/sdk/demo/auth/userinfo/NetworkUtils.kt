package com.bytedance.sdk.demo.auth.userinfo

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkUtils {
    private lateinit var baseUrl: String

    fun <T> createApi(apiClass: Class<T>): T {
        baseUrl = "https:\\\\open.tiktokapis.com"

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
