package com.bytedance.sdk.open.tiktok.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.AppUtils
import com.bytedance.sdk.open.tiktok.utils.AppUtils.Companion.componentClassName
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils.Companion.validateSign

abstract class AppCheckBase(open val context: Context): IAppCheck {
    override val isAuthSupported: Boolean
        get() = (isAppInstalled && validateSign(context, packageName, signature))

    override val isShareSupported: Boolean
        get() = isAppSupportAPI(Keys.API.MIN_SDK_NEW_VERSION_API)

    override val isAppInstalled: Boolean
        get() = isAppSupportAPI(Keys.API.AUTH_REQUIRE_API);

    override val isShareFileProviderSupported: Boolean
        get() = isAppSupportAPI(packageName, BuildConfig.TIKTOK_AUTH_ACTIVITY, Keys.API.SHARE_SUPPORT_FILEPROVIDER)

    override fun isAppSupportAPI(requiredApi: Int): Boolean {
        return isAppSupportAPI(packageName, BuildConfig.TIKTOK_AUTH_ACTIVITY, requiredApi)
    }

    open fun isAppSupportAPI(platformPackageName: String, remoteRequestEntry: String, requiredApi: Int): Boolean {
        if (context == null || TextUtils.isEmpty(platformPackageName)) {
            return false
        }
        val intent = Intent()
        val componentName = ComponentName(platformPackageName, componentClassName(platformPackageName, remoteRequestEntry))
        intent.component = componentName
        val activityInfo = intent.resolveActivityInfo(context.packageManager, PackageManager.MATCH_DEFAULT_ONLY)
        val platformSdkVersion: Int = AppUtils.getPlatformSDKVersion(context, platformPackageName, remoteRequestEntry)
        return activityInfo != null && activityInfo.exported && platformSdkVersion >= requiredApi
    }

    protected abstract val signature: String

    abstract override val packageName: String
}