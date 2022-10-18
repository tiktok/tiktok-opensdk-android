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

object Keys {
    const val SDK_VERSION_ERROR = -1
    const val SDK_VERSION_KEY = "BD_PLATFORM_SDK_VERSION"

    object Base {
        const val TYPE = "_bytedance_params_type"
        const val EXTRA = "_bytedance_params_extra"
        const val ERROR_CODE = "_bytedance_params_error_code"
        const val ERROR_MSG = "_bytedance_params_error_msg"
        const val FROM_ENTRY = "_bytedance_params_from_entry"
        const val CALLER_PKG = "_bytedance_params_type_caller_package"
        const val CALLER_BASE_OPEN_SDK_NAME = "_aweme_params_caller_open_sdk_name"
        const val CALLER_BASE_OPEN_SDK_VERSION = "_aweme_params_caller_open_sdk_version"
        const val CALLER_BASE_OPEN_SDK_COMMON_NAME = "_aweme_params_caller_open_sdk_common_name"
        const val CALLER_BASE_OPEN_SDK_COMMON_VERSION = "_aweme_params_caller_open_sdk_common_version"
    }

    object API {
        const val AUTH_REQUIRE_API = 1
        const val MIN_SDK_NEW_VERSION_API = 3
        const val SHARE_SUPPORT_FILEPROVIDER = 5
    }
}
