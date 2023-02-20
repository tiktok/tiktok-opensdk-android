package com.bytedance.sdk.demo.auth.userinfo.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GetUserInfoService {
    @FormUrlEncoded
    @POST("/v2/oauth/token/")
    fun getAccessToken(
        @Field("code") code: String,
        @Field("client_key") clientKey: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String
    ): Call<AccessTokenResponse>

    @GET("/v2/user/info/")
    fun getUserInfo(
        @Header("Authorization") accessToken: String,
        @Query("fields") fields: String = "open_id,union_id,avatar_url,display_name"
    ): Call<UserInfoResponse>
}
