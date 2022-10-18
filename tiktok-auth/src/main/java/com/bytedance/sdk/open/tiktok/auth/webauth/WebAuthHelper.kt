package com.bytedance.sdk.open.tiktok.auth.webauth

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
import android.net.Uri
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_ENDPOINT
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_HOST
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.core.utils.Md5Utils.hexDigest
import com.bytedance.sdk.open.tiktok.core.utils.SignatureUtils.getMd5Signs
import com.bytedance.sdk.open.tiktok.core.utils.SignatureUtils.packageSignature

internal object WebAuthHelper {
    fun composeLoadUrl(
        context: Context,
        redirectUrl: String,
        webAuthRequest: WebAuthRequest,
    ): String {
        val optionalScope = StringBuilder()
        fun concatenateScopes(scopeStr: String?, suffix: String) {
            if (!scopeStr.isNullOrEmpty()) {
                val scopes = scopeStr.split(",").toTypedArray()
                scopes.forEach {
                    optionalScope.append(",$it$suffix")
                }
            }
        }
        concatenateScopes(webAuthRequest.optionalScope1, ".1")
        concatenateScopes(webAuthRequest.optionalScope0, ".0")
        if (optionalScope.isNotEmpty()) { optionalScope.deleteAt(0) }

        val signs = getMd5Signs(context, webAuthRequest.resultActivityPackageName)
        val builder = Uri.Builder()
            .scheme(Keys.WebAuth.SCHEMA_HTTPS)
            .authority(WEB_AUTH_HOST)
            .path(WEB_AUTH_ENDPOINT)
            .appendQueryParameter(Keys.WebAuth.QUERY_RESPONSE_TYPE, Keys.WebAuth.VALUE_RESPONSE_TYPE_CODE)
            .appendQueryParameter(Keys.WebAuth.QUERY_FROM, Keys.WebAuth.VALUE_FROM_OPENSDK)
            .appendQueryParameter(Keys.WebAuth.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
            .appendQueryParameter(Keys.WebAuth.QUERY_PLATFORM, "android")

        packageSignature(signs)?.let { builder.appendQueryParameter(Keys.WebAuth.QUERY_SIGNATURE, it) }
        builder.appendQueryParameter(Keys.WebAuth.QUERY_REDIRECT_URI, redirectUrl)
        with(webAuthRequest) {
            builder.appendQueryParameter(Keys.WebAuth.QUERY_CLIENT_KEY, clientKey)
            state?.let {
                builder.appendQueryParameter(Keys.WebAuth.QUERY_STATE, it)
            }
            builder.appendQueryParameter(Keys.WebAuth.QUERY_SCOPE, scope)
            builder.appendQueryParameter(Keys.WebAuth.QUERY_ENCRYPTION_PACKAGE, hexDigest(resultActivityPackageName))
            language?.let {
                builder.appendQueryParameter(Keys.WebAuth.QUERY_ACCEPT_LANGUAGE, it)
            }
        }

        return builder.build().toString()
    }
}
