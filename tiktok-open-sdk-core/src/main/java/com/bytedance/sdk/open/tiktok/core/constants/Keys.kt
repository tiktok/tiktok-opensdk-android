package com.bytedance.sdk.open.tiktok.core.constants

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
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
