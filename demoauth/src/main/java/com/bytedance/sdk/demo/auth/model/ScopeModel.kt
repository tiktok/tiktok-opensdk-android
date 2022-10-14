package com.bytedance.sdk.demo.auth.model

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

import androidx.annotation.StringRes
import com.bytedance.sdk.demo.auth.R

data class ScopeModel(
    val type: ScopeType,
    @StringRes
    val descRes: Int,
    val isOn: Boolean,
    val isEditable: Boolean
) : DataModel {
    override val viewType = ViewType.SCOPE
}

enum class ScopeType(val value: String, val descRes: Int) {
    USER_INFO_BASIC("user.info.basic", R.string.basic_scope_description),
    USER_INFO_EMAIL("user.info.email", R.string.email_scope_description),
    VIDEO_UPLOAD("video.upload", R.string.video_upload_scope_description),
    VIDEO_LIST("video.list", R.string.video_list_scope_description),
    USER_INFO_USERNAME("user.info.username", R.string.user_name_scope_description),
    USER_INFO_PHONE("user.info.phone", R.string.phone_scope_description),
    USER_INTEREST("user.ue", R.string.user_interest_scope_description),
    MUSIC_COLLECTION("music.collection", R.string.music_scope_description),
}

// remove in open source
val BETA_SCOPE = listOf<ScopeType>(
    ScopeType.USER_INFO_USERNAME,
    ScopeType.USER_INFO_PHONE,
    ScopeType.USER_INTEREST,
    ScopeType.MUSIC_COLLECTION,
)
