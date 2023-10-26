package com.tiktok.open.sdk.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.tiktok.open.sdk.auth.webauth.WebAuthHelper
import com.tiktok.open.sdk.core.appcheck.TikTokAppCheckUtil
import com.tiktok.open.sdk.core.constants.Constants
import com.tiktok.open.sdk.core.constants.Keys
import com.tiktok.open.sdk.core.utils.AppUtils

/**
 * Provides an interface for requesting authorization from TikTok.
 * @param activity your activity
 */
class AuthApi(private val activity: Activity) {

    enum class AuthMethod {
        ChromeTab,
        TikTokApp
    }

    fun authorize(
        request: AuthRequest,
        authMethod: AuthMethod = AuthMethod.TikTokApp
    ): Boolean {
        val internalRequest = request.copy(
            scope = request.scope.replace(" ", ""),
        )
        return when (authMethod) {
            AuthMethod.TikTokApp -> {
                TikTokAppCheckUtil.getInstalledTikTokApp(
                    activity
                )?.let {
                    return authorizeNative(internalRequest, it.appPackageName)
                }
                launchChromeTab(internalRequest)
            }
            AuthMethod.ChromeTab -> launchChromeTab(internalRequest)
        }
    }

    private fun authorizeNative(authRequest: AuthRequest, authorizeAppPackageName: String): Boolean {
        if (authorizeAppPackageName.isEmpty() || !authRequest.validate()) {
            return false
        }
        val bundle = authRequest.toBundle()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName(authorizeAppPackageName, AppUtils.concatPackageAndClassPath(authorizeAppPackageName, Constants.TIKTOK.AUTH_ACTIVITY_NAME))
            putExtras(bundle)
        }
        return try {
            activity.startActivityForResult(intent, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun launchChromeTab(authRequest: AuthRequest): Boolean {
        if (!authRequest.validate()) {
            return false
        }
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            activity,
            Uri.parse(
                WebAuthHelper.composeLoadUrl(
                    activity,
                    authRequest,
                    activity.packageName,
                )
            )
        )
        return true
    }

    fun getAuthResponseFromIntent(intent: Intent?, redirectUrl: String): AuthResponse? {
        if (intent == null) {
            return null
        }
        val data = intent.data
        val bundle = intent.extras
        if (bundle?.getInt(Keys.Base.TYPE) == com.tiktok.open.sdk.auth.constants.Constants.AUTH_RESPONSE) {
            return bundle.toAuthResponse()
        } else if (data?.toString()?.startsWith(redirectUrl) == true) {
            return WebAuthHelper.parseRedirectUriToAuthResponse(data)
        }
        return null
    }
}
