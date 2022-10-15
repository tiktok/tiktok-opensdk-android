package com.bytedance.sdk.open.tiktok.base

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
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.Share

data class MediaContent(val mediaType: Share.MediaType, val mediaPaths: ArrayList<String>) {
    fun toBundle(): Bundle {
        val pathKey = when (mediaType) {
            Share.MediaType.IMAGE -> Keys.IMAGE_PATH
            Share.MediaType.VIDEO -> Keys.VIDEO_PATH
        }
        return Bundle().apply {
            putStringArrayList(pathKey, mediaPaths)
        }
    }

    fun validate(): Boolean {
        return mediaPaths.isNotEmpty()
    }
}
