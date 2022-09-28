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

class ShareService(val context: Context, val clientKey: String) {
    fun share(request: Share.Request, entryComponent: EntryComponent): Boolean {
        if (!request.validate()) {
            return false
        }
        val bundle = Bundle().apply {
            if (getPlatformSDKVersion(context, entryComponent.tiktokPackage, entryComponent.tiktokPlatformComponent)
                >= Keys.API.MIN_SDK_NEW_VERSION_API
            ) {
                putAll(request.toBundle())
            }
            putString(Keys.Share.CLIENT_KEY, clientKey)
//            val callerPackage = if (request.callerPackage.isNullOrEmpty()) context.packageName else request.callerPackage
//            putString(Keys.Share.CALLER_PKG, callerPackage)
//            putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION)
//            val callerLocalEntry = request.callerLocalEntry
//            putString(
//                Keys.Share.CALLER_LOCAL_ENTRY,
//                AppUtils.componentClassName(
//                    context.packageName,
//                    if (!callerLocalEntry.isNullOrEmpty()) callerLocalEntry else entryComponent.defaultComponent
//                )
//            )
            putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, BuildConfig.SDK_OVERSEA_NAME)
            putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
        }

        val intent = Intent().apply {
            component = ComponentName(
                entryComponent.tiktokPackage,
                AppUtils.componentClassName(BuildConfig.TIKTOK_COMPONENT_PATH, entryComponent.tiktokComponent)
            )
            putExtras(bundle)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
