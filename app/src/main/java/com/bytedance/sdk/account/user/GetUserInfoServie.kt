package com.bytedance.sdk.account.user

import com.bytedance.sdk.account.user.bean.AccessTokenResponse
import com.bytedance.sdk.account.user.bean.UserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 主要功能 use code to get user's info
 * author: ChangLei
 * since: 2019/4/3
 */
interface GetUserInfoServie {

    @GET("demoapp/callback/")
    fun getAccessToken(@Query("code")code: String, @Query("client_key")clientKey: String): Call<AccessTokenResponse>

    @GET("/oauth/userinfo/")
    fun getUserInfo(@Query("access_token")accessToken: String,
                    @Query("open_id")openId: String):Call<UserInfoResponse>
}