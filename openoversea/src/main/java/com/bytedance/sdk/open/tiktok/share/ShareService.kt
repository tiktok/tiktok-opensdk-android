package com.bytedance.sdk.open.tiktok.share

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.utils.AppUtils.Companion.getPlatformSDKVersion

class ShareService(val context: Context, val clientKey: String) {

    fun share(request: Share.Request?, entryComponent: EntryComponent): Boolean {
        return if (request == null || !request.validate()) {
            false
        } else {
            // packages
            val bundle = Bundle()
            if (getPlatformSDKVersion(context, entryComponent.tiktokPackage, entryComponent.tiktokPlatformComponent)
                    >= Keys.API.MIN_SDK_NEW_VERSION_API) {
                bundle.putAll(request.toBundle())
            }
            bundle.putString(Keys.Share.CLIENT_KEY, clientKey)
            bundle.putString(Keys.Share.CALLER_PKG, context.packageName)
            bundle.putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION)
            if (!TextUtils.isEmpty(request.callerLocalEntry)) {
                bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, request.callerLocalEntry)
//                bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, "com.bytedance.sdk.open.tiktok" + "." + "TikTokShareResponseActivity")
            } else {
                bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, context.packageName + "." + entryComponent.defaultComponent)
            }
            if (request.extras != null) {
                bundle.putBundle(Keys.Base.EXTRA, request.extras)
            }
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, BuildConfig.SDK_OVERSEA_NAME)
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
            bundle.putString(Keys.Share.OPENPLATFORM_EXTRA, request.shareExtra)
            if (request.anchor != null) {
                bundle.putString(Keys.Share.ANCHOR_SOURCE_TYPE, request.anchor!!.sourceType)
            }
            bundle.putSerializable(Keys.Share.EXTRA_SHARE_OPTIONS, request.extraShareOptions) // TOOD: chen.wu move this shareoptions wit this key in bundle
            bundle.putInt(Keys.Share.SHARE_FORMAT, request.shareFormat.format)
            val intent = Intent()
            val componentName = ComponentName(entryComponent.tiktokPackage, buildComponentClassName(entryComponent.tiktokPackage, entryComponent.tiktokComponent))
            intent.component = componentName
            intent.putExtras(bundle)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            try {
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun buildComponentClassName(packageName: String?, classPath: String): String {
        return "com.ss.android.ugc.aweme.$classPath"
    }
}