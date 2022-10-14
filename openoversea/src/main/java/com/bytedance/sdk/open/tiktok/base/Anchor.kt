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
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Anchor(
    @SerializedName("anchor_business_type")
    val anchorBusinessType: Int = 0,
    @SerializedName("anchor_title")
    val anchorTitle: String? = null,
    @SerializedName("anchor_content")
    val anchorContent: String? = null,
    @SerializedName("anchor_source_type")
    val sourceType: String? = null,
) {

    fun toBundle(): Bundle {
        return Bundle().apply {
            putString(Keys.Share.SHARE_ANCHOR_INFO, Gson().toJson(this@Anchor))
        }
    }
}
