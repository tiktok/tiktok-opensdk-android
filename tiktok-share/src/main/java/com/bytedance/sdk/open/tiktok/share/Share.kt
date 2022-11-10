package com.bytedance.sdk.open.tiktok.share

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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
    enum class Format(val format: Int) {
        DEFAULT(0),
        GREEN_SCREEN(1);
    }

    enum class MediaType {
        VIDEO,
        IMAGE;
    }

    @Parcelize
    data class Request(
        val mediaContent: MediaContent,
        val shareFormat: Format = Format.DEFAULT,
        override val packageName: String,
        override val resultActivityFullPath: String,
    ) : Base.Request() {

        @IgnoredOnParcel
        override var type: Int = Constants.SHARE_REQUEST

        override fun validate(): Boolean {
            if (shareFormat == Format.GREEN_SCREEN) {
                return mediaContent.mediaPaths.size == 1
            }
            return mediaContent.validate()
        }

        override fun toBundle(clientKey: String, sdkName: String, sdkVersion: String): Bundle {
            return super.toBundle(sdkName, sdkVersion).apply {
                putString(Keys.Share.CLIENT_KEY, clientKey)
                putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION)
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

    data class Response(
        var state: String?,
        var subErrorCode: Int?,
        override val errorCode: Int,
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
