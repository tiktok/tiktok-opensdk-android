package com.bytedance.sdk.open.tiktok.core.appcheck

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

interface ITikTokAppCheck {
    val isAuthSupported: Boolean

    val isShareSupported: Boolean

    val isAppInstalled: Boolean

    val isShareFileProviderSupported: Boolean

    val appPackageName: String

    val sharePackageName: String

    val signature: String
}
