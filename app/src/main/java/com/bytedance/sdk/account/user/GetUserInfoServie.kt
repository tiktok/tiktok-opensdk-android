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

    @GET("/oauth/access_token/")
    fun getAccessToken(@Query("client_key")clientKey: String, @Query("client_secret")clientSecret: String,
                       @Query("code")code: String, @Query("grant_type")grantType: String): Call<AccessTokenResponse>

    @GET("/oauth/userinfo/")
    fun getUserInfo(@Query("access_token")accessToken: String,
                    @Query("open_id")openId: String):Call<UserInfoResponse>
}