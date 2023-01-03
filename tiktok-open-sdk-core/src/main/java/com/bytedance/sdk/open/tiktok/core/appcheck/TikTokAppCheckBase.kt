package com.bytedance.sdk.open.tiktok.core.appcheck

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
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.AUTH_ACTIVITY_NAME
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.SHARE_ACTIVITY_NAME
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.TIKTOK_SHARE_COMPONENT_PATH
import com.bytedance.sdk.open.tiktok.core.constants.Keys
import com.bytedance.sdk.open.tiktok.core.utils.AppUtils.componentClassName
import com.bytedance.sdk.open.tiktok.core.utils.SignatureUtils.validateSign

internal abstract class TikTokAppCheckBase(open val context: Context) : ITikTokAppCheck {

    override val isAuthSupported: Boolean
        get() = (isAppInstalled && validateSign(context, appPackageName, signature))

    override val isShareSupported: Boolean
        get() = isAppSupportAPI(
            sharePackageName,
            SHARE_ACTIVITY_NAME,
            Keys.API.MIN_SDK_NEW_VERSION_API
        )

    override val isAppInstalled: Boolean
        get() = isAppSupportAPI(
            appPackageName,
            AUTH_ACTIVITY_NAME,
            Keys.API.AUTH_REQUIRE_API
        )

    override val isShareFileProviderSupported: Boolean
        get() = isAppSupportAPI(
            sharePackageName,
            SHARE_ACTIVITY_NAME,
            Keys.API.SHARE_SUPPORT_FILEPROVIDER
        )

    private fun isAppSupportAPI(remoteRequestPackageName: String, remoteRequestClassName: String, requiredApiVersion: Int): Boolean {
        if (TextUtils.isEmpty(remoteRequestPackageName)) {
            return false
        }
        val intent = Intent()
        val componentName = ComponentName(appPackageName, componentClassName(remoteRequestPackageName, remoteRequestClassName))
        intent.component = componentName
        val activityInfo = intent.resolveActivityInfo(context.packageManager, PackageManager.MATCH_DEFAULT_ONLY)
        return activityInfo != null && activityInfo.exported && platformSdkVersion() >= requiredApiVersion
    }

    private fun platformSdkVersion(): Int {
        return try {
            val componentName = ComponentName(appPackageName, componentClassName(appPackageName, AUTH_ACTIVITY_NAME))
            context.packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).metaData?.getInt(
                Keys.SDK_VERSION_KEY,
                Keys.SDK_VERSION_ERROR
            ) ?: Keys.SDK_VERSION_ERROR
        } catch (e: PackageManager.NameNotFoundException) {
            Keys.SDK_VERSION_ERROR
        }
    }

    override val sharePackageName: String = TIKTOK_SHARE_COMPONENT_PATH
}
