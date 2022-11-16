package com.bytedance.sdk.open.tiktok.core.constants

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

object Constants {
    object BaseError {
        const val OK = 0
        const val ERROR_UNKNOWN = -1
        const val ERROR_CANCEL = -2
    }

    object TIKTOK {
        const val SHARE_ACTIVITY_NAME = "share.SystemShareActivity"

        const val AUTH_ACTIVITY_NAME = "openauthorize.AwemeAuthorizedActivity"

        const val TIKTOK_SHARE_COMPONENT_PATH = "com.ss.android.ugc.aweme"
    }

    enum class APIType {
        AUTH, SHARE
    }
}
