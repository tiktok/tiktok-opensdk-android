package com.bytedance.sdk.open.tiktok.core.constants

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
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
