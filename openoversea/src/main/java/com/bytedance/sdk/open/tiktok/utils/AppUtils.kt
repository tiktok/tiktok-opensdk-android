package com.bytedance.sdk.open.tiktok.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.common.constants.Keys

object AppUtils {
    fun componentClassName(packageName: String, classPath: String): String {
        return "$packageName.$classPath"
    }
    fun getPlatformSDKVersion(context: Context?, platformPackageName: String?, remoteRequestEntry: String): Int {
        if (context == null || TextUtils.isEmpty(platformPackageName)) {
            return Keys.SDK_VERSION_ERROR
        }
        try {
            val componentName = ComponentName(platformPackageName!!, componentClassName(platformPackageName, remoteRequestEntry))
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
