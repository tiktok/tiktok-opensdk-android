package com.bytedance.sdk.demo.auth.userinfo.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import com.google.gson.annotations.SerializedName

data class AccessTokenInfo(
    @SerializedName("open_id")val openid: String,
    @SerializedName("scope")val scope: String,
    @SerializedName("access_token")val accessToken: String,
    @SerializedName("expires_in")val expiresIn: Long,
    @SerializedName("refresh_token")val refreshToken: String,
    @SerializedName("refresh_expires_in")val refreshExpiresIn: Long,
    @SerializedName("token_type") val tokenType: String,
)
