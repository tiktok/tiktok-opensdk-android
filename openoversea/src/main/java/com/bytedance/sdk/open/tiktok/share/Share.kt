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
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
import com.bytedance.sdk.open.tiktok.utils.AppUtils

class Share {
    enum class Format(val format: Int) {
        DEFAULT(0),
        GREEN_SCREEN(1);
    }

    enum class MediaType {
        VIDEO,
        IMAGE;
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
        override val resultActivityComponent: ResultActivityComponent,
    ) : Base.Request() {

        override var type: Int = Constants.TIKTOK.SHARE_REQUEST

        override fun validate(): Boolean {
            return mediaContent.validate()
        }

        override fun toBundle(clientKey: String): Bundle {
            return super.toBundle().apply {
                putString(Keys.Share.CLIENT_KEY, clientKey)
                putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION)
                putString(Keys.Share.STATE, state)
                putAll(mediaContent.toBundle())
                putInt(Keys.Share.SHARE_FORMAT, shareFormat.format)
                putInt(Keys.Share.SHARE_TARGET_SCENE, targetSceneType)
                putString(Keys.Share.OPEN_PLATFORM_EXTRA, shareExtra)
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

                putString(Keys.Share.CALLER_PKG, resultActivityComponent.packageName)
                putString(
                    Keys.Share.CALLER_LOCAL_ENTRY,
                    AppUtils.componentClassName(
                        packageName = resultActivityComponent.packageName,
                        classPath = resultActivityComponent.className
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
