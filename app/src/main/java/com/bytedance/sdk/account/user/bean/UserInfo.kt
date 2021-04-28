package com.bytedance.sdk.account.user.bean

import com.google.gson.annotations.SerializedName

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/3
 */
data class UserInfo(@SerializedName("open_id")var openId: String,
                    @SerializedName("union_id")var unionId: String,
                    @SerializedName("display_name")var nickName: String,
                    @SerializedName("avatar")var avatar:String,
                    @SerializedName("error_code")var errorCode: Int,
                    @SerializedName("description")var description: String)