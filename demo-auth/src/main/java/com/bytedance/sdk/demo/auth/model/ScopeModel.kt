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

data class ScopeModel(
    val type: ScopeType,
    @StringRes
    val descRes: Int,
    val isOn: Boolean,
    val isEditable: Boolean
) : DataModel {
    override val viewType = ViewType.SCOPE
}

enum class ScopeType(val value: String) {
    USER_INFO_BASIC("user.info.basic"),
    VIDEO_UPLOAD("video.upload"),
    VIDEO_LIST("video.list");

    companion object {
        fun fromValue(value: String): ScopeType? {
            return values().find { it.value == value }
        }
    }
}
