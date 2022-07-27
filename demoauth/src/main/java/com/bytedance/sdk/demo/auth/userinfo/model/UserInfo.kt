package com.bytedance.sdk.demo.auth.userinfo.model

import com.google.gson.annotations.SerializedName

data class UserInfo(@SerializedName("open_id")var openId: String,
                    @SerializedName("union_id")var unionId: String,
                    @SerializedName("display_name")var nickName: String,
                    @SerializedName("avatar")var avatar:String,
                    @SerializedName("error_code")var errorCode: Int,
                    @SerializedName("description")var description: String)