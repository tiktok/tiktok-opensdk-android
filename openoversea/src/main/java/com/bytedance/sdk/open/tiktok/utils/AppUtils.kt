package com.bytedance.sdk.open.tiktok.utils

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
import android.content.pm.PackageManager
import com.bytedance.sdk.open.tiktok.common.constants.Keys

object AppUtils {
    internal fun componentClassName(packageName: String, classPath: String): String {
        return "$packageName.$classPath"
    }

    fun getPlatformSDKVersion(context: Context?, platformPackageName: String?, remoteRequestEntry: String): Int {
        if (context == null || platformPackageName.isNullOrEmpty()) {
            return Keys.SDK_VERSION_ERROR
        }
        try {
            val componentName = ComponentName(platformPackageName, componentClassName(platformPackageName, remoteRequestEntry))
            val appInfo = context.packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            appInfo.metaData?.let {
                return it.getInt(Keys.SDK_VERSION_KEY, Keys.SDK_VERSION_ERROR)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace() // TODO: chen.wu remove?
        }
        return Keys.SDK_VERSION_ERROR
    }
}
