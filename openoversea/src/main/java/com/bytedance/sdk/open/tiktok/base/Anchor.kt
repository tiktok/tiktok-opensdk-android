package com.bytedance.sdk.open.tiktok.base

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Anchor {
    @SerializedName("anchor_business_type")
    var anchorBusinessType: Int = 0
    @SerializedName("anchor_title")
    var anchorTitle: String? = null
    @SerializedName("anchor_content")
    var anchorContent: String? = null
    @SerializedName("anchor_source_type")
    var sourceType: String? = null

    object Companion {
        fun fromBundle(bundle: Bundle): Anchor? {
            val gsonString = bundle.getString(Keys.Share.SHARE_ANCHOR_INFO)
            return gsonString?.let {
                Gson().fromJson(it, Anchor::class.java)
            }
            return null
        }
    }

    fun toBundle(): Bundle {
        return Bundle().apply {
            putString(Keys.Share.SHARE_ANCHOR_INFO, Gson().toJson(this@Anchor))
        }
    }
}