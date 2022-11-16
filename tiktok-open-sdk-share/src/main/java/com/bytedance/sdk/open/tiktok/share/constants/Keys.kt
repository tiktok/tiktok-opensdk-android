package com.bytedance.sdk.open.tiktok.share.constants

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

internal object Keys {
    const val IMAGE_PATH = "AWEME_EXTRA_IMAGE_MESSAGE_PATH"
    const val VIDEO_PATH = "AWEME_EXTRA_VIDEO_MESSAGE_PATH"
    const val VERSION = "1"

    object Share {
        const val STATE = "_aweme_open_sdk_params_state"
        const val CLIENT_KEY = "_aweme_open_sdk_params_client_key"
        const val CALLER_PKG = "_aweme_open_sdk_params_caller_package"
        const val CALLER_SDK_VERSION = "_aweme_open_sdk_params_caller_sdk_version"
        const val CALLER_LOCAL_ENTRY = "_aweme_open_sdk_params_caller_local_entry"
        const val SHARE_FORMAT = "_aweme_open_sdk_params_share_format"
        const val TYPE = "_aweme_open_sdk_params_type"
        const val ERROR_CODE = "_aweme_open_sdk_params_error_code"
        const val ERROR_MSG = "_aweme_open_sdk_params_error_msg"
        const val SHARE_SUB_ERROR_CODE = "_aweme_open_sdk_params_sub_error_code"
    }
}
