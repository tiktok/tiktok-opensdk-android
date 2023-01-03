package com.bytedance.sdk.open.tiktok.core.appcheck

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.Context
import com.bytedance.sdk.open.tiktok.core.constants.Constants

object TikTokAppCheckFactory {

    fun getApiCheck(context: Context, apiType: Constants.APIType): ITikTokAppCheck? {
        when (apiType) {
            Constants.APIType.AUTH -> for (appCheck in getAppCheckArray(context)) {
                if (appCheck.isAuthSupported) {
                    return appCheck
                }
            }
            Constants.APIType.SHARE -> for (appCheck in getAppCheckArray(context)) {
                if (appCheck.isShareSupported) {
                    return appCheck
                }
            }
        }
        return null
    }

    private fun getAppCheckArray(context: Context) = arrayListOf<ITikTokAppCheck>(
        object : TikTokAppCheckBase(context) {
            override val signature: String = "194326e82c84a639a52e5c023116f12a"
            override val appPackageName: String = "com.zhiliaoapp.musically"
        },
        object : TikTokAppCheckBase(context) {
            override val signature: String = "aea615ab910015038f73c47e45d21466"
            override val appPackageName: String = "com.ss.android.ugc.trill"
        },
    )
}
