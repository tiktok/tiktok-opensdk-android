package com.bytedance.sdk.demo.auth.userinfo.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
    @SerializedName("message")val message: String,
    @SerializedName("data")val data: AccessTokenInfo
)
