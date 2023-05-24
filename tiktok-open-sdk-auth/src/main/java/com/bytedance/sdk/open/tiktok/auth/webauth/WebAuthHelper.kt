package com.bytedance.sdk.open.tiktok.auth.webauth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.auth.Auth
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_ENDPOINT
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_HOST
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.auth.utils.PKCEUtils
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.utils.SignatureUtils
import java.security.MessageDigest

internal object WebAuthHelper {
    private const val DEVICE_ANDROID = "android"

    fun composeLoadUrl(
        context: Context,
        authRequest: Auth.Request,
        packageName: String,
    ): String {
        val builder = Uri.Builder()
            .scheme(Keys.WebAuth.SCHEMA_HTTPS)
            .authority(WEB_AUTH_HOST)
            .path(WEB_AUTH_ENDPOINT)
            .appendQueryParameter(Keys.WebAuth.QUERY_RESPONSE_TYPE, "code")
            .appendQueryParameter(Keys.WebAuth.QUERY_SDK_NAME, "tiktok_sdk_auth")
            .appendQueryParameter(Keys.WebAuth.QUERY_PLATFORM, DEVICE_ANDROID)
            .appendQueryParameter(
                Keys.WebAuth.QUERY_APP_IDENTITY,
                MessageDigest
                    .getInstance("SHA-256")
                    .digest(packageName.toByteArray())
                    .fold("") { str, it -> str + "%02x".format(it) }
            )
            .appendQueryParameter(
                Keys.WebAuth.QUERY_CERTIFICATE,
                SignatureUtils.getCallerSHA256Certificates(context, packageName)
            )

        with(authRequest) {
            builder.appendQueryParameter(Keys.WebAuth.QUERY_REDIRECT_URI, redirectUri)
            builder.appendQueryParameter(Keys.WebAuth.QUERY_CLIENT_KEY, clientKey)
            state?.let {
                builder.appendQueryParameter(Keys.WebAuth.QUERY_STATE, it)
            }
            builder.appendQueryParameter(Keys.WebAuth.QUERY_SCOPE, scope)
            builder.appendQueryParameter(Keys.WebAuth.QUERY_CODE_CHALLENGE, PKCEUtils.generateCodeChallenge(codeVerifier))
            language?.let {
                builder.appendQueryParameter(Keys.WebAuth.QUERY_LANGUAGE, it)
            }
        }

        return builder.build().toString()
    }

    fun parseRedirectUriToAuthResponse(uri: Uri, extras: Bundle? = null): Auth.Response {
        val authCode = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_CODE)
        return if (authCode != null) {
            Auth.Response(
                authCode = authCode,
                state = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_STATE),
                grantedPermissions = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_SCOPE)
                    ?: "",
                errorCode = Constants.BaseError.OK,
                errorMsg = null,
            )
        } else {
            val errorCodeStr: String? = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_ERROR_CODE)
            val authError: String? = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_ERROR)
            val authErrorDescription: String? = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_ERROR_DESCRIPTION)
            val errorCode = try {
                errorCodeStr?.toInt() ?: Constants.BaseError.ERROR_UNKNOWN
            } catch (e: Exception) {
                Constants.BaseError.ERROR_UNKNOWN
            }
            val errorMsgStr: String? = uri.getQueryParameter(Keys.WebAuth.REDIRECT_QUERY_ERROR_MESSAGE)
            Auth.Response(
                authCode = "",
                state = null,
                grantedPermissions = "",
                errorCode = errorCode,
                errorMsg = errorMsgStr,
                extras = extras,
                authError = authError,
                authErrorDescription = authErrorDescription
            )
        }
    }
}
