package com.bytedance.sdk.account.user.bean

import com.google.gson.annotations.SerializedName

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/3
 */
data class AccessTokenInfo(@SerializedName("access_token")var accessToken: String,
                           @SerializedName("expires_in")var expiresIn: Long,
                           @SerializedName("refresh_token")var refreshToken: String,
                           @SerializedName("open_id")var openid: String,
                           @SerializedName("scope")var scope: String,
                           @SerializedName("unionid")var unionid: String,
                           @SerializedName("captcha")var captcha: String,
                           @SerializedName("description")var description: String,
                           @SerializedName("error_code")var errorCode: Int)

data class AccessTokenInfo2(@SerializedName("open_id")var openid: String,
                            @SerializedName("scope")var scope: String,
                            @SerializedName("access_token")var accessToken: String,
                            @SerializedName("expires_in")var expiresIn: Long,
                            @SerializedName("refresh_token")var refreshToken: String,
                            @SerializedName("refresh_expires_in")var refreshExpiresIn: Long)