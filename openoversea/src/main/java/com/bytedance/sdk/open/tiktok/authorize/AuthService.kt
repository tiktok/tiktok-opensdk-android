package com.bytedance.sdk.open.tiktok.authorize

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.utils.AppUtils.componentClassName

class AuthService(
    private val context: Context,
    private val clientKey: String,
    private val callerPackageName: String? = null,
    private val callerVersion: String? = null,
) {
    fun authorizeNative(authRequest: Auth.Request, authorizeAppPackageName: String, authorizeClassPath: String): Boolean {
        if (authorizeAppPackageName.isEmpty() || !authRequest.validate()) {
            return false
        }
        val bundle = authRequest.toBundle(
            clientKey = clientKey,
            callerPackageName = callerPackageName ?: context.packageName,
            callerVersion = callerVersion,
        )
        val intent = Intent().apply {
            component = ComponentName(authorizeAppPackageName, componentClassName(authorizeAppPackageName, authorizeClassPath))
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
            val bundle = req.toBundle(
                clientKey = clientKey,
                callerPackageName = callerPackageName ?: context.packageName,
                callerVersion = callerVersion,
            )
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
