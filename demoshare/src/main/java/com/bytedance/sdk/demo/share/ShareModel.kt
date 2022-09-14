package com.bytedance.sdk.demo.share

import android.os.Parcelable
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.Share
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Suppress("ParcelCreator")
@Parcelize
data class ShareModel(var packageName: String = "",
                      var clientKey: String = "",
                      var clientSecret: String = "",
                      var isImage: Boolean = false,
                      var media: List<String> = arrayListOf(),
                      var hashtags: List<String>? = null,
                      var disableMusicSelection: Boolean = false,
                      var greenScreenFormat: Boolean = false,
                      var autoAttachAnchor: Boolean = false,
                      var anchorSourceType: String? = null,
                      var shareExtra: Map<String, String>? = null): Parcelable {
                      }

fun ShareModel.toShareRequest(): Share.Request {
    val request = Share.Request()
    this.packageName.takeUnless { it.isNullOrEmpty() }?.let {
        request.callerPackage = it
    }
    this.hashtags?.let { validHashTags ->
        val mappedHashtags =  ArrayList<String>()
        for (hashtag in validHashTags) {
            mappedHashtags.add(hashtag)
        }
        request.hashTagList = mappedHashtags
    }

    if (this.disableMusicSelection) {
        val options: HashMap<String, Any> = HashMap()
        options[Keys.Share.DISABLE_MUSIC_SELECTION] = 1
        request.extraShareOptions = options
    }
    if (this.greenScreenFormat) {
        request.shareFormat = Share.Format.GREEN_SCREEN
    }
    if (autoAttachAnchor && !anchorSourceType.isNullOrEmpty()) {
        val anchor = Anchor()
        anchor.sourceType = anchorSourceType
        request.anchor = anchor
        try {
            request.shareExtra = JSONObject(shareExtra).toString()
        } catch (_: Exception) {
            request.shareExtra = null
        }
    }
    val mediaList = ArrayList<String>()
    for (m in media) {
        mediaList.add(m)
    }
    if (isImage) {
        val content = MediaContent(Share.MediaType.IMAGE, mediaList)
        request.mediaContent = content
    } else {
        val content = MediaContent(Share.MediaType.VIDEO, mediaList)
        request.mediaContent = content
    }

    return request
}