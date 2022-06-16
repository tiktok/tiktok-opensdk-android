package com.bytedance.sdk.open.tiktok.base

import android.os.Bundle
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName

class MicroAppInfo {
    @SerializedName("appId")
    private val appId: String? = null
    @SerializedName("appTitle")
    private val appTitle: String? = null
    @SerializedName("description")
    private val description: String? = null
    @SerializedName("appUrl")
    private val appUrl: String? = null
    companion object {
        fun fromBundle(clientBundle: Bundle): MicroAppInfo? {
            val info = clientBundle.getString(Keys.Share.SHARE_MICROAPP_INFO)
            try {
                if (!TextUtils.isEmpty(info)) {
                    return Gson().fromJson(info, MicroAppInfo::class.java)
                }
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
            return null
        }
    }
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString(Keys.Share.SHARE_MICROAPP_INFO, Gson().toJson(this))
        }
    }
}