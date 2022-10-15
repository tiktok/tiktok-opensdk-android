package com.bytedance.sdk.open.tiktok.authorize

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
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.Md5Utils.hexDigest
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils.getMd5Signs
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils.packageSignature

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
            .scheme(Keys.Web.SCHEMA_HTTPS)
            .authority(BuildConfig.AUTH_HOST)
            .path(BuildConfig.AUTH_ENDPOINT)
            .appendQueryParameter(Keys.Web.QUERY_RESPONSE_TYPE, Keys.Web.VALUE_RESPONSE_TYPE_CODE)
            .appendQueryParameter(Keys.Web.QUERY_FROM, Keys.Web.VALUE_FROM_OPENSDK)
            .appendQueryParameter(Keys.Web.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
            .appendQueryParameter(Keys.Web.QUERY_PLATFORM, "android")

        packageSignature(signs)?.let { builder.appendQueryParameter(Keys.Web.QUERY_SIGNATURE, it) }
        builder.appendQueryParameter(Keys.Web.QUERY_REDIRECT_URI, redirectUrl)
        with(webAuthRequest) {
            builder.appendQueryParameter(Keys.Web.QUERY_CLIENT_KEY, clientKey)
            state?.let {
                builder.appendQueryParameter(Keys.Web.QUERY_STATE, it)
            }
            builder.appendQueryParameter(Keys.Web.QUERY_SCOPE, scope)
            builder.appendQueryParameter(Keys.Web.QUERY_ENCRYPTION_PACKAGE, hexDigest(resultActivityPackageName))
            language?.let {
                builder.appendQueryParameter(Keys.Web.QUERY_ACCEPT_LANGUAGE, it)
            }
        }

        return builder.build().toString()
    }
}
