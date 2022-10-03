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

class ShareService(
    private val context: Context,
    private val clientKey: String,
) {
    fun share(request: Share.Request, entryComponent: EntryComponent): Boolean {
        if (!request.validate()) {
            return false
        }
        val bundle = Bundle().apply {
            if (getPlatformSDKVersion(context, entryComponent.tiktokPackage, entryComponent.tiktokPlatformComponent)
                >= Keys.API.MIN_SDK_NEW_VERSION_API
            ) {
                putAll(request.toBundle(clientKey = clientKey))
            }
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
