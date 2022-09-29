package com.bytedance.sdk.open.tiktok.base

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.Share

data class MediaContent(val mediaType: Share.MediaType, val mediaPaths: ArrayList<String>) {
    object Companion {
        const val identifier = "_dyobject_identifier_" // TODO: chen.wu remove?
    }

    fun toBundle(): Bundle {
        val className = when (mediaType) {
            Share.MediaType.IMAGE -> "com.ss.android.ugc.aweme.opensdk.share.base.TikTokImageObject"
            Share.MediaType.VIDEO -> "com.ss.android.ugc.aweme.opensdk.share.base.TikTokVideoObject"
        }
        val pathKey = when (mediaType) {
            Share.MediaType.IMAGE -> Keys.IMAGE_PATH
            Share.MediaType.VIDEO -> Keys.VIDEO_PATH
        }
        return Bundle().apply {
            putString(Companion.identifier, className)
            putStringArrayList(pathKey, mediaPaths)
        }
    }

    fun validate(): Boolean {
        return mediaPaths.isNotEmpty()
    }
}
