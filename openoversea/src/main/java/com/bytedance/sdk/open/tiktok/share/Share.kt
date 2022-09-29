package com.bytedance.sdk.open.tiktok.share

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.utils.AppUtils

class Share {
    enum class Format(val format: Int) {
        DEFAULT(0),
        GREEN_SCREEN(1);
    }

    enum class MediaType(val type: Int) {
        VIDEO(0),
        IMAGE(1);
    }

    data class Request(
        val mediaContent: MediaContent,
        val targetSceneType: Int = 0,
        val hashTagList: ArrayList<String> = arrayListOf(),
        val shareFormat: Format = Format.DEFAULT,
        val anchor: Anchor? = null,
        val state: String? = null,
        val shareExtra: String? = null,
        val extraShareOptions: HashMap<String, Any>? = null,
        override val localEntry: String? = null,
    ) : Base.Request() {

        override var type: Int = Constants.TIKTOK.SHARE_REQUEST

        override fun validate(): Boolean {
            return mediaContent.validate()
        }

        override fun toBundle(clientKey: String, callerPackageName: String, callerVersion: String?): Bundle {
            return super.toBundle(callerPackageName = callerPackageName, callerVersion = callerVersion).apply {
                putString(Keys.Share.CLIENT_KEY, clientKey)
                putString(Keys.Share.CALLER_PKG, callerPackageName)
                putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION)
                putString(Keys.Share.STATE, state)
                putAll(mediaContent.toBundle())
                putInt(Keys.Share.SHARE_FORMAT, shareFormat.format)
                putInt(Keys.Share.SHARE_TARGET_SCENE, targetSceneType)
                putString(Keys.Share.OPENPLATFORM_EXTRA, shareExtra)
                putSerializable(Keys.Share.EXTRA_SHARE_OPTIONS, extraShareOptions)

                hashTagList.let {
                    if (it.isNotEmpty()) {
                        putString(Keys.Share.SHARE_DEFAULT_HASHTAG, it[0])
                    }
                    putStringArrayList(Keys.Share.SHARE_HASHTAG_LIST, it)
                }
                anchor?.apply {
                    if (this.anchorBusinessType == 10) { // TODO: chen.wu check this anchor business type
                        putAll(this.toBundle())
                    }
                    putString(Keys.Share.ANCHOR_SOURCE_TYPE, sourceType)
                }

                putString(
                    Keys.Share.CALLER_LOCAL_ENTRY,
                    AppUtils.componentClassName(
                        packageName = callerPackageName,
                        classPath = localEntry ?: BuildConfig.DEFAULT_ENTRY_ACTIVITY
                    )
                )
            }
        }
    }

    data class Response(
        var state: String?,
        var subErrorCode: Int?,
        override val errorCode: Int,
        override val errorMsg: String?,
        override val extras: Bundle? = null,
    ) : Base.Response() {
        override val type: Int = Constants.TIKTOK.SHARE_RESPONSE
    }
}

internal fun Bundle.toShareResponse(): Share.Response {
    val state = getString(Keys.Share.STATE)
    val subErrorCode = getInt(Keys.Share.SHARE_SUB_ERROR_CODE)
    val errorCode = getInt(Keys.Share.ERROR_CODE)
    val errorMsg = getString(Keys.Share.ERROR_MSG)
    val extras = getBundle(Keys.Base.EXTRA)
    return Share.Response(
        state = state,
        subErrorCode = subErrorCode,
        errorCode = errorCode,
        errorMsg = errorMsg,
        extras = extras
    )
}
