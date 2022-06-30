package com.bytedance.sdk.open.tiktok.authorize

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.Md5Utils.Companion.hexDigest
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils.Companion.getMd5Signs
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils.Companion.packageSignature

class WebAuthHelper {
    companion object {
        fun composeLoadUrl(context: Context, request: Auth.Request, host: String, path: String): String? {
            val optionalScope = StringBuilder()
            fun concatenateScopes(scopeStr: String?, suffix: String) {
                if (!TextUtils.isEmpty(scopeStr)) {
                    val scopes = scopeStr!!.split(",").toTypedArray()
                    scopes.forEach {
                        optionalScope.append(",$it$suffix")
                    }
                }
            }
            concatenateScopes(request.optionalScope1, ".1")
            concatenateScopes(request.optionalScope0, ".0")
            if (optionalScope.isNotEmpty()) { optionalScope.deleteAt(0) }

            val signs = getMd5Signs(context, request.callerPackage)
            val builder = Uri.Builder()
                    .scheme(Keys.Web.SCHEMA_HTTPS)
                    .authority(host)
                    .path(path)
                    .appendQueryParameter(Keys.Web.QUERY_RESPONSE_TYPE, Keys.Web.VALUE_RESPONSE_TYPE_CODE)
                    .appendQueryParameter(Keys.Web.QUERY_FROM, Keys.Web.VALUE_FROM_OPENSDK)
                    .appendQueryParameter(Keys.Web.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
                    .appendQueryParameter(Keys.Web.QUERY_PLATFORM, "android")

            packageSignature(signs)?.let { builder.appendQueryParameter(Keys.Web.QUERY_SIGNATURE, it) }
            request.redirectUri?.let { builder.appendQueryParameter(Keys.Web.QUERY_REDIRECT_URI, it) }
            request.clientKey?.let { builder.appendQueryParameter(Keys.Web.QUERY_CLIENT_KEY, it)}
            request.state?.let { builder.appendQueryParameter(Keys.Web.QUERY_STATE, it) }
            request.scope?.let { builder.appendQueryParameter(Keys.Web.QUERY_SCOPE, it) }
            request.callerPackage?.let { builder.appendQueryParameter(Keys.Web.QUERY_ENCRIPTION_PACKAGE, hexDigest(it)) }
            request.language?.let { builder.appendQueryParameter(Keys.Web.QUERY_ACCEPT_LANGUAGE, it) }

            return builder.build().toString()
        }
    }
}