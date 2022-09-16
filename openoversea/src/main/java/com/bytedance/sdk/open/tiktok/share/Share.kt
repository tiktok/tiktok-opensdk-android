package com.bytedance.sdk.open.tiktok.share

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.base.MicroAppInfo
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base

class Share {
    enum class Format(val format: Int) {
        DEFAULT(0),
        GREEN_SCREEN(1);
        companion object {
            fun from(value: Int): Format {
                if (value == 1) {
                    return GREEN_SCREEN
                }
                return DEFAULT
            }
        }
    }
    enum class MediaType(val type: Int) {
        VIDEO(0),
        IMAGE(1);

        companion object {
            fun from(value: Int): MediaType {
                return if (value == 1) {
                    IMAGE
                } else {
                    VIDEO
                }
            }
        }
    }
    class Request : Base.Request() {
        override var type: Int = Constants.TIKTOK.SHARE_REQUEST
        var targetSceneType: Int = 0 // TODO: chen.wu make it enum?
        var hashTagList: ArrayList<String>? = null
        var shareFormat: Format = Format.DEFAULT
        var mediaContent: MediaContent? = null
        var anchor: Anchor? = null
        var state: String? = null
        var microAppInfo: MicroAppInfo? = null
        var shareExtra: String? = null
        var extraShareOptions: HashMap<String, Any>? = null

        override fun validate(): Boolean {
            return mediaContent?.validate() ?: false
        }

        override fun toBundle(): Bundle {
            val bundle = super.toBundle()
            return bundle.apply {
                putString(Keys.Share.CALLER_LOCAL_ENTRY, callerLocalEntry)
                putString(Keys.Share.CALLER_PKG, callerPackage)
                putString(Keys.Share.STATE, state)
                mediaContent?.apply {
                    putAll(this.toBundle())
                }
                putInt(Keys.Share.SHARE_FORMAT, shareFormat.format)
                putInt(Keys.Share.SHARE_TARGET_SCENE, targetSceneType)
                putString(Keys.Share.OPENPLATFORM_EXTRA, shareExtra)
                putSerializable(Keys.Share.EXTRA_SHARE_OPTIONS, extraShareOptions)

                hashTagList?.let {
                    if (it.isNotEmpty()) {
                        putString(Keys.Share.SHARE_DEFAULT_HASHTAG, it[0])
                    }
                    putStringArrayList(Keys.Share.SHARE_HASHTAG_LIST, it)
                }
                // TODO: chen.wu check to remove micro app info
                microAppInfo?.let {
                    putAll(it.toBundle())
                }
                anchor?.apply {
                    if (this.anchorBusinessType == 10) { // TODO: chen.wu check this anchor business type
                        putAll(this.toBundle())
                    }
                    putString(Keys.Share.ANCHOR_SOURCE_TYPE, sourceType)
                }
            }
        }
        override fun fromBundle(bundle: Bundle) {
            super.fromBundle(bundle)
            callerPackage = bundle.getString(Keys.Share.CALLER_PKG)
            callerLocalEntry = bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY)
            state = bundle.getString(Keys.Share.STATE)
            bundle.getInt(Keys.Share.SHARE_FORMAT).let { shareFormat = Format.from(it) }
            targetSceneType = bundle.getInt(Keys.Share.SHARE_TARGET_SCENE)
            shareExtra = bundle.getString(Keys.Share.OPENPLATFORM_EXTRA)
            (bundle.getSerializable(Keys.Share.EXTRA_SHARE_OPTIONS) as HashMap<String, Any>?).let { extraShareOptions = it }
            hashTagList = bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST)
            mediaContent = MediaContent.Companion.fromBundle(bundle)
            microAppInfo = MicroAppInfo.fromBundle(bundle)
            anchor = Anchor.Companion.fromBundle(bundle)
        }
    }

    class Response : Base.Response() {
        override val type: Int = Constants.TIKTOK.SHARE_RESPONSE
        var state: String? = null
        var subErrorCode: Int? = null

        override fun validate(): Boolean {
            return true
        }

        override fun toBundle(): Bundle {
            val bundle = super.toBundle()
            subErrorCode?.let { bundle.putInt(Keys.Share.SHARE_SUB_ERROR_CODE, it) }
            state?.let { bundle.putString(Keys.Share.STATE, it) }
            return bundle
        }

        override fun fromBundle(bundle: Bundle) {
            super.fromBundle(bundle)
            this.errorCode = bundle.getInt(Keys.Share.ERROR_CODE)
            this.errorMsg = bundle.getString(Keys.Share.ERROR_MSG)
            state = bundle.getString(Keys.Share.STATE)
            subErrorCode = bundle.getInt(Keys.Share.SHARE_SUB_ERROR_CODE)
        }
    }
}
