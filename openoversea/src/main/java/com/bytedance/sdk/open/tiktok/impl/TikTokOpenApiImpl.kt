package com.bytedance.sdk.open.tiktok.impl

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

import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.toAuthResponse
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.helper.AppCheckFactory
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareService
import com.bytedance.sdk.open.tiktok.share.toShareResponse

internal open class TikTokOpenApiImpl(
    private val context: Context,
    private val authService: AuthService,
    private val shareService: ShareService,
    private val apiEventHandler: IApiEventHandler,
) : TikTokOpenApi {

    override fun handleIntent(intent: Intent?): Boolean {
        if (intent == null) {
            apiEventHandler.onErrorIntent(intent)
            return false
        }
        val bundle = intent.extras
        if (bundle == null) {
            apiEventHandler.onErrorIntent(intent)
            return false
        }
        var type = bundle.getInt(Keys.Base.TYPE)
        if (type == INVALID_TYPE_VALUE) {
            type = bundle.getInt(Keys.Share.TYPE)
        }
        val response = when (type) {
            Constants.TIKTOK.AUTH_RESPONSE -> bundle.toAuthResponse()
            Constants.TIKTOK.SHARE_RESPONSE -> bundle.toShareResponse()
            else -> null
        } ?: return false
        apiEventHandler.onResponse(response)
        return true
    }

    override fun authorize(request: Auth.Request, useWebAuth: Boolean): Boolean {
        val internalRequest = request.copy(
            scope = request.scope.replace(" ", ""),
            optionalScope0 = request.optionalScope0?.replace(" ", ""),
            optionalScope1 = request.optionalScope1?.replace(" ", ""),

        )
        apiEventHandler.onRequest(internalRequest)
        if (!useWebAuth) {
            AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.let {
                return authService.authorizeNative(internalRequest, it.appPackageName, BuildConfig.TIKTOK_AUTH_ACTIVITY)
            }
        }
        return webAuth(internalRequest)
    }

    override fun share(request: Share.Request): Boolean {
        apiEventHandler.onRequest(request)
        AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.let {
            return shareService.share(request, it.appPackageName)
        }
        return false
    }

    private fun webAuth(request: Auth.Request): Boolean {
        return authService.authorizeWeb(request)
    }

    companion object {
        const val INVALID_TYPE_VALUE = 0
    }
}
