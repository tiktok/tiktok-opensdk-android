package com.bytedance.sdk.demo.auth.userinfo.model

import com.bytedance.sdk.demo.auth.userinfo.model.UserInfo
import com.google.gson.annotations.SerializedName

data class UserInfoResponse(@SerializedName("message")var message: String,
                            @SerializedName("data")var data: UserInfo)