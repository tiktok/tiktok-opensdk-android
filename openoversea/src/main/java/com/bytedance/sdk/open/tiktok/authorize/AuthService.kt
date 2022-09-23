package com.bytedance.sdk.open.tiktok.authorize

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.AppUtils.componentClassName

class AuthService(val context: Context, val clientKey: String) {
    fun authorizeNative(req: Auth.Request, packageName: String, remoteRequestEntry: String, localEntry: String): Boolean {
        if (TextUtils.isEmpty(packageName) || !req.validate()) {
            return false
        }
        val bundle = req.toBundle().apply {
            putString(Keys.Auth.CLIENT_KEY, clientKey)
            putString(Keys.Base.CALLER_PKG, context.packageName)
            val callerLocalEntry = req.callerLocalEntry
            putString(Keys.Base.FROM_ENTRY, componentClassName(context.packageName, if (!callerLocalEntry.isNullOrEmpty()) callerLocalEntry else localEntry))
            putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, BuildConfig.SDK_OVERSEA_NAME)
            putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
        }
        val intent = Intent().apply {
            component = ComponentName(packageName, componentClassName(packageName, remoteRequestEntry))
            putExtras(bundle)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun authorizeWeb(req: Auth.Request): Boolean {
        return if (!req.validate()) {
            false
        } else {
            val bundle = req.toBundle().apply {
                putString(Keys.Auth.CLIENT_KEY, clientKey)
                putString(Keys.Base.CALLER_PKG, context.packageName)
            }
            val intent = Intent(context, WebAuthActivity::class.java).apply {
                putExtras(bundle)
            }
            try {
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
