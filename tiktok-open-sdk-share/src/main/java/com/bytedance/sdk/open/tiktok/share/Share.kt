package com.bytedance.sdk.open.tiktok.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.EXTRA
import com.bytedance.sdk.open.tiktok.core.model.Base
import com.bytedance.sdk.open.tiktok.share.constants.Constants
import com.bytedance.sdk.open.tiktok.share.constants.Keys
import com.bytedance.sdk.open.tiktok.share.model.MediaContent
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

class Share {
    /*
     * DEFAULT: Sharing video or image from your app to TikTok
     * GREEN_SCREEN: Sharing video or image as green-screen effect video. Green-screen video use the sharing video/image as the background and the creator's figure in the front.
     */
    enum class Format(val format: Int) {
        DEFAULT(0),
        GREEN_SCREEN(1);
    }

    enum class MediaType {
        VIDEO,
        IMAGE;
    }

    /*
       * Share.Request
       *
       * @param mediaContent the media content to share.
       * @param shareFormat the sharing format.
       * @param packageName the package name of your app.
       * @param resultActivityFullPath the path of the activity being used to receive the share result information
     */
    @Parcelize
    data class Request(
        val mediaContent: MediaContent,
        val shareFormat: Format = Format.DEFAULT,
        val packageName: String,
        val resultActivityFullPath: String,
    ) : Base.Request() {

        @IgnoredOnParcel
        override var type: Int = Constants.SHARE_REQUEST

        override fun validate(): Boolean {
            if (shareFormat == Format.GREEN_SCREEN) {
                return mediaContent.mediaPaths.size == 1
            }
            return mediaContent.validate()
        }

        override fun toBundle(clientKey: String): Bundle {
            return super.toBundle(BuildConfig.SHARE_SDK_NAME, BuildConfig.SHARE_SDK_VERSION).apply {
                putString(Keys.Share.CLIENT_KEY, clientKey)
                putAll(mediaContent.toBundle())
                putInt(Keys.Share.SHARE_FORMAT, shareFormat.format)
                putString(Keys.Share.CALLER_PKG, packageName)
                putString(
                    Keys.Share.CALLER_LOCAL_ENTRY,
                    resultActivityFullPath
                )
            }
        }
    }

    /*
       * Share.Response
       *
       * @param state the sharing state.
       * @param errorCode the error code for sharing result .
       * @param subErrorCode the code which provides more detail information.
       * @param errorMsg the error message
       * @param extras the extra information
     */
    data class Response(
        var state: String?,
        override val errorCode: Int,
        var subErrorCode: Int?,
        override val errorMsg: String?,
        override val extras: Bundle? = null,
    ) : Base.Response() {
        override val type: Int = Constants.SHARE_RESPONSE
    }
}

internal fun Bundle.toShareResponse(): Share.Response {
    val state = getString(Keys.Share.STATE)
    val subErrorCode = getInt(Keys.Share.SHARE_SUB_ERROR_CODE)
    val errorCode = getInt(Keys.Share.ERROR_CODE)
    val errorMsg = getString(Keys.Share.ERROR_MSG)
    val extras = getBundle(EXTRA)
    return Share.Response(
        state = state,
        subErrorCode = subErrorCode,
        errorCode = errorCode,
        errorMsg = errorMsg,
        extras = extras
    )
}
