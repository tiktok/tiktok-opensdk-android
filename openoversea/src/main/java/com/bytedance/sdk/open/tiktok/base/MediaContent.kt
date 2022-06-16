package com.bytedance.sdk.open.tiktok.base

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.ShareKt

class MediaContent(val mediaType: ShareKt.MediaType, val mediaPaths: ArrayList<String>) {
    object Companion {
        const val identifier = "_dyobject_identifier_" // TODO: chen.wu remove?

        fun fromBundle(bundle: Bundle): MediaContent? {
            bundle.getStringArrayList(Keys.IMAGE_PATH)?.let {
                return MediaContent(ShareKt.MediaType.IMAGE, it)
            }
            bundle.getStringArrayList(Keys.VIDEO_PATH)?.let {
                return MediaContent(ShareKt.MediaType.VIDEO, it)
            }
            return null
        }
    }
    fun toBundle(): Bundle {
        val className = when(mediaType) {
            ShareKt.MediaType.IMAGE -> "com.ss.android.ugc.aweme.opensdk.share.base.TikTokImageObject"
            ShareKt.MediaType.VIDEO -> "com.ss.android.ugc.aweme.opensdk.share.base.TikTokVideoObject"
        }
        val pathKey = when(mediaType) {
            ShareKt.MediaType.IMAGE -> Keys.IMAGE_PATH
            ShareKt.MediaType.VIDEO -> Keys.VIDEO_PATH
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