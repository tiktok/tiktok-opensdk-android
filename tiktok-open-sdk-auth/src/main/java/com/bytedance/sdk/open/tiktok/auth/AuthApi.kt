package com.bytedance.sdk.open.tiktok.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_RESPONSE
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.BROWSER_AUTH_REDIRECT_HOST
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.BROWSER_AUTH_REDIRECT_PATH
import com.bytedance.sdk.open.tiktok.auth.webauth.WebAuthActivity
import com.bytedance.sdk.open.tiktok.auth.webauth.WebAuthHelper.parseRedirectUriToAuthResponse
import com.bytedance.sdk.open.tiktok.core.appcheck.TikTokAppCheckFactory
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.constants.Constants.APIType
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base
import com.bytedance.sdk.open.tiktok.core.utils.AppUtils

/**
 * Provides an interface for requesting authorization from TikTok.
 * @param context your component context
 * @param clientKey your app client key
 * @param apiEventHandler the event handler class which will be used to handle authorization result
 */
class AuthApi(
    private val activity: Activity,
    private val clientKey: String,
    private val apiEventHandler: AuthApiEventHandler,
) {
    companion object {
        @JvmStatic
        fun isAuthorizedWithTikTokAppSupported(context: Context) =
            TikTokAppCheckFactory.getApiCheck(context, APIType.AUTH) != null
    }

    enum class AuthMethod {
        WebView,
        TikTokApp
    }

    fun handleResultIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return false
        }
        val data = intent.data
        val bundle = intent.extras
        if (data != null &&
            clientKey == data.scheme &&
            BROWSER_AUTH_REDIRECT_HOST == data.host &&
            BROWSER_AUTH_REDIRECT_PATH == data.path
        ) {
            val response = parseRedirectUriToAuthResponse(data)
            apiEventHandler.onResponse(response)
            return true
        } else if (bundle != null) {
            val type = bundle.getInt(Base.TYPE)
            if (type == AUTH_RESPONSE) {
                apiEventHandler.onResponse(bundle.toAuthResponse())
                return true
            }
        }
        return false
    }

    fun authorize(
        request: Auth.Request,
        authMethod: AuthMethod = AuthMethod.TikTokApp
    ): Boolean {
        val internalRequest = request.copy(
            scope = request.scope.replace(" ", ""),
        )
        apiEventHandler.onRequest(internalRequest)
        return when (authMethod) {
            AuthMethod.TikTokApp -> {
                TikTokAppCheckFactory.getApiCheck(
                    activity,
                    APIType.AUTH
                )?.let {
                    return authorizeNative(internalRequest, it.appPackageName)
                }
                authorizeWebView(internalRequest)
            }
            AuthMethod.WebView -> authorizeWebView(internalRequest)
        }
    }

    private fun authorizeNative(authRequest: Auth.Request, authorizeAppPackageName: String): Boolean {
        if (authorizeAppPackageName.isEmpty() || !authRequest.validate()) {
            return false
        }
        val bundle = authRequest.toBundle(
            clientKey = clientKey
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName(authorizeAppPackageName, AppUtils.componentClassName(authorizeAppPackageName, Constants.TIKTOK.AUTH_ACTIVITY_NAME))
            putExtras(bundle)
        }
        return try {
            activity.startActivityForResult(intent, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun authorizeWebView(authRequest: Auth.Request): Boolean {
        return if (!authRequest.validate()) {
            false
        } else {
            val bundle = Bundle().apply {
                putString(WebAuthActivity.CLIENT_KEY_IN_BUNDLE, clientKey)
                putParcelable(WebAuthActivity.AUTH_REQUEST_KEY_IN_BUNDLE, authRequest)
            }
            val intent = Intent(
                activity,
                WebAuthActivity::class.java
            ).apply {
                putExtras(bundle)
            }
            try {
                activity.startActivityForResult(intent, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
