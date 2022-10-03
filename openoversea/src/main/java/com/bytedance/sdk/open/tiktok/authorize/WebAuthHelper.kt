package com.bytedance.sdk.open.tiktok.authorize

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
            builder.appendQueryParameter(Keys.Web.QUERY_ENCRIPTION_PACKAGE, hexDigest(resultActivityPackageName))
            language?.let {
                builder.appendQueryParameter(Keys.Web.QUERY_ACCEPT_LANGUAGE, it)
            }
        }

        return builder.build().toString()
    }
}
