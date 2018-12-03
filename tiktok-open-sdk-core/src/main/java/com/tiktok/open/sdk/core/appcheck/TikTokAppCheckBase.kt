package com.tiktok.open.sdk.core.appcheck

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import com.tiktok.open.sdk.core.constants.Constants
import com.tiktok.open.sdk.core.utils.AppUtils
import com.tiktok.open.sdk.core.utils.SignatureUtils

internal abstract class TikTokAppCheckBase(open val context: Context) : ITikTokAppCheck {

    override fun isAppInstalled(): Boolean {
        if (TextUtils.isEmpty(appPackageName)) {
            return false
        }
        val intent = Intent()
        val componentName = ComponentName(appPackageName, AppUtils.concatPackageAndClassPath(appPackageName, Constants.TIKTOK.AUTH_ACTIVITY_NAME))
        intent.component = componentName
        val activityInfo = intent.resolveActivityInfo(context.packageManager, PackageManager.MATCH_DEFAULT_ONLY)
        return activityInfo != null && activityInfo.exported && SignatureUtils.validateSign(
            context,
            appPackageName,
            signature
        )
    }
}
