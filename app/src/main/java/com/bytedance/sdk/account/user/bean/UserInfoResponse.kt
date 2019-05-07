package com.bytedance.sdk.account.user.bean

import com.google.gson.annotations.SerializedName

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/3
 */
data class UserInfoResponse(@SerializedName("message")var message: String,
                            @SerializedName("data")var data: UserInfo)