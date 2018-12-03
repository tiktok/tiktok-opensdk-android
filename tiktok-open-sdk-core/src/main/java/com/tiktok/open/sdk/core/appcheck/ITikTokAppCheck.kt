package com.tiktok.open.sdk.core.appcheck

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

interface ITikTokAppCheck {
    fun isAppInstalled(): Boolean

    val appPackageName: String

    val signature: String
}
