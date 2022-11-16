package com.bytedance.sdk.open.tiktok.auth

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_RESPONSE
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.BROWSER_AUTH_REDIRECT_HOST
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.BROWSER_AUTH_REDIRECT_PATH
import com.bytedance.sdk.open.tiktok.auth.webauth.WebAuthActivity
import com.bytedance.sdk.open.tiktok.auth.webauth.WebAuthHelper.parseRedirectUriToAuthResponse
import com.bytedance.sdk.open.tiktok.core.appcheck.AppCheckFactory
import com.bytedance.sdk.open.tiktok.core.constants.Constants.APIType
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.AUTH_ACTIVITY_NAME
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base
import com.bytedance.sdk.open.tiktok.core.utils.AppUtils.componentClassName

/**
 * Provides an interface for requesting authorization from TikTok.
 * @param context your component context
 * @param clientKey your app client key
 * @param apiEventHandler the event handler class which will be used to handle authorization result
 */
class AuthApi(
    private val context: Context,
    private val clientKey: String,
    private val apiEventHandler: AuthApiEventHandler,
) {
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
                AppCheckFactory.getApiCheck(
                    context,
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
            clientKey = clientKey,
            sdkName = BuildConfig.AUTH_SDK_NAME,
            sdkVersion = BuildConfig.AUTH_SDK_VERSION
        )
        val intent = Intent().apply {
            component = ComponentName(authorizeAppPackageName, componentClassName(authorizeAppPackageName, AUTH_ACTIVITY_NAME))
            putExtras(bundle)
        }
        return try {
            context.startActivity(intent)
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
                context,
                WebAuthActivity::class.java
            ).apply {
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
