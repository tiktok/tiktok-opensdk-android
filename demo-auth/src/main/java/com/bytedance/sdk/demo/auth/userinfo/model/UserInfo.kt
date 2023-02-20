package com.bytedance.sdk.demo.auth.userinfo.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("open_id")val openId: String,
    @SerializedName("union_id")val unionId: String,
    @SerializedName("display_name")val nickName: String,
    @SerializedName("avatar_url")val avatarUrl: String,
)
