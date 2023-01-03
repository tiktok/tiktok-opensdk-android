package com.bytedance.sdk.demo.auth.userinfo.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface GetUserInfoService {
    @POST("/oauth/access_token/")
    fun getAccessToken(
        @Query("code")code: String,
        @Query("client_key")clientKey: String,
        @Query("client_secret")clientSecret: String,
        @Query("grant_type")grantType: String
    ): Call<AccessTokenResponse>

    @POST("/oauth/userinfo/")
    fun getUserInfo(
        @Query("access_token")accessToken: String,
        @Query("open_id")openId: String
    ): Call<UserInfoResponse>
}
