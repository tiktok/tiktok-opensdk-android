package com.bytedance.sdk.account.share

import com.google.gson.annotations.SerializedName

/**
 * @author yangjie
 * @date 2019-07-15
 */
data class AccessTokenData(@SerializedName("error_code") var status_code: Int,
                           @SerializedName("access_token") var access_token: String,
                           @SerializedName("expires_in") var expires_in: Long)
