package com.bytedance.sdk.demo.auth.userinfo.model

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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
