package com.bytedance.sdk.open.tiktok.share

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.utils.AppUtils
import com.bytedance.sdk.open.tiktok.utils.AppUtils.getPlatformSDKVersion

const val kRefactorResponseHandling = false

class ShareService(val context: Context, val clientKey: String) {
    fun share(request: Share.Request?, entryComponent: EntryComponent): Boolean {
        if (request == null || !request.validate()) {
            return false
        }
        val bundle = Bundle()
        if (getPlatformSDKVersion(context, entryComponent.tiktokPackage, entryComponent.tiktokPlatformComponent)
            >= Keys.API.MIN_SDK_NEW_VERSION_API
        ) {
            bundle.putAll(request.toBundle())
        }
        bundle.putString(Keys.Share.CLIENT_KEY, clientKey)
        val callerPackage = if (request.callerPackage.isNullOrEmpty()) context.packageName else request.callerPackage
        bundle.putString(Keys.Share.CALLER_PKG, callerPackage)
        bundle.putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION)
        if (!request.callerLocalEntry.isNullOrEmpty()) {
            bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, AppUtils.componentClassName(context.packageName, request.callerLocalEntry!!))
        } else if (kRefactorResponseHandling) {
            // TODO: chen.wu TikTokApiResponseActivity to avoid EntryActivity or localEntry to handle the api response from TikTok
            bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, "com.bytedance.sdk.open.tiktok.TikTokApiResponseActivity")
        } else {
            bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, AppUtils.componentClassName(context.packageName, entryComponent.defaultComponent))
        }
        bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, BuildConfig.SDK_OVERSEA_NAME)
        bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, BuildConfig.SDK_OVERSEA_VERSION)

        val intent = Intent()
        val componentName = ComponentName(
            entryComponent.tiktokPackage,
            AppUtils.componentClassName(BuildConfig.TIKTOK_COMPONENT_PATH, entryComponent.tiktokComponent)
        )
        intent.component = componentName
        intent.putExtras(bundle)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
