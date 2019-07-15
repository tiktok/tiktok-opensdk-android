package com.bytedance.sdk.account.share

import com.google.gson.annotations.SerializedName

/**
 * @author yangjie
 * @date 2019-07-15
 */
data class ShareIdResp(@SerializedName("status_code") var status_code: Int,
                       @SerializedName("status_msg") var status_msg: String,
                       @SerializedName("share_id") var share_id:String)
