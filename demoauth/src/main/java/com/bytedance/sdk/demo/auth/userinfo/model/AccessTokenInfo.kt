package com.bytedance.sdk.demo.auth.userinfo.model

import com.google.gson.annotations.SerializedName

data class AccessTokenInfo(
    @SerializedName("open_id")var openid: String,
    @SerializedName("scope")var scope: String,
    @SerializedName("access_token")var accessToken: String,
    @SerializedName("expires_in")var expiresIn: Long,
    @SerializedName("refresh_token")var refreshToken: String,
    @SerializedName("refresh_expires_in")var refreshExpiresIn: Long
)
