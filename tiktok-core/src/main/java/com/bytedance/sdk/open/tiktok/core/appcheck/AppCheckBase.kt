package com.bytedance.sdk.open.tiktok.core.appcheck

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

internal abstract class AppCheckBase(open val context: Context) : IAppCheck {

    override val isAuthSupported: Boolean
        get() = (isAppInstalled && validateSign(context, appPackageName, signature))

    override val isShareSupported: Boolean
        get() = isAppSupportAPI(
            sharePackageName, SHARE_ACTIVITY_NAME,
            Keys.API.MIN_SDK_NEW_VERSION_API
        )

    override val isAppInstalled: Boolean
        get() = isAppSupportAPI(
            appPackageName, AUTH_ACTIVITY_NAME,
            Keys.API.AUTH_REQUIRE_API
        )

    override val isShareFileProviderSupported: Boolean
        get() = isAppSupportAPI(
            sharePackageName, SHARE_ACTIVITY_NAME,
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
