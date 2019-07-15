package com.bytedance.sdk.account.share

import com.google.gson.annotations.SerializedName

/**
 * @author yangjie
 * @date 2019-07-15
 */
data class ShareAccessTokenResp(@SerializedName("data") var data: AccessTokenData,
                                @SerializedName("message")var message: String)
