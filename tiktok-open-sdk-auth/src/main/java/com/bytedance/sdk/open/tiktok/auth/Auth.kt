package com.bytedance.sdk.open.tiktok.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_REQUEST
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_RESPONSE
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.auth.constants.Keys.Auth.AUTH_ERROR
import com.bytedance.sdk.open.tiktok.auth.constants.Keys.Auth.AUTH_ERROR_DESCRIPTION
import com.bytedance.sdk.open.tiktok.auth.utils.PKCEUtils
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.ERROR_CODE
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.ERROR_MSG
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.EXTRA
import com.bytedance.sdk.open.tiktok.core.model.Base
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

class Auth {
    /*
       * Auth.Request
       *
       * @param clientKey your app's client key.
       * @param scope the scopes required, scopes should be concatenated with ",", e.g. "scope1, scope2, scope3"
       * @param redirectUri the specified endpoint URI which Auth server redirects after the users granted permissions for the client
       * @param codeVerifier randomly generated string on the application side for the authorization request
       * @param state state in request should match the state from the response received.
       * @param language the language.
     */
    @Parcelize
    data class Request(
        val clientKey: String,
        val scope: String,
        val redirectUri: String,
        val codeVerifier: String,
        val state: String? = null,
        val language: String? = null,
    ) : Base.Request() {
        @IgnoredOnParcel
        override val type: Int = AUTH_REQUEST

        override fun validate(): Boolean = scope.isNotEmpty()

        override fun toBundle(): Bundle {
            return super.toBundle(BuildConfig.AUTH_SDK_NAME, BuildConfig.AUTH_SDK_VERSION).apply {
                putString(Keys.Auth.CLIENT_KEY, clientKey)
                putString(Keys.Auth.STATE, state)
                putString(Keys.Auth.SCOPE, scope)
                putString(Keys.Auth.LANGUAGE, language)
                putString(Keys.Auth.REDIRECT_URI, redirectUri)
                putString(Keys.Auth.CODE_CHALLENGE, PKCEUtils.generateCodeChallenge(codeVerifier))
            }
        }
    }

    /*
       * Auth.Response
       *
       * @param authCode the authorization code received from TikTok.
       * @param state state from response should match the state in your request.
       * @param grantedPermissions the granted permission
       * @param errorCode the error code for authorization result .
       * @param errorMsg the error message
       * @param extras the extra information
       * @param authError auth error type
       * @param authErrorDescription auth error type description
     */
    data class Response(
        val authCode: String,
        val state: String?,
        val grantedPermissions: String,
        override val errorCode: Int, // Deprecated
        override val errorMsg: String?, // Deprecated
        override val extras: Bundle? = null,
        val authError: String? = null,
        val authErrorDescription: String? = null,
    ) : Base.Response() {
        override val type: Int = AUTH_RESPONSE

        override fun toBundle(): Bundle {
            return super.toBundle().apply {
                putString(Keys.Auth.AUTH_CODE, authCode)
                putString(Keys.Auth.STATE, state)
                putString(Keys.Auth.GRANTED_PERMISSION, grantedPermissions)
            }
        }
    }
}

internal fun Bundle.toAuthResponse(): Auth.Response {
    val authCode = getString(Keys.Auth.AUTH_CODE, "")
    val state = getString(Keys.Auth.STATE)
    val grantedPermissions = getString(Keys.Auth.GRANTED_PERMISSION, "")
    val errorCode = getInt(ERROR_CODE)
    val errorMsg = getString(ERROR_MSG)
    val extras = getBundle(EXTRA)
    val authError = getString(AUTH_ERROR)
    val authErrorDescription = getString(AUTH_ERROR_DESCRIPTION)
    return Auth.Response(
        authCode = authCode,
        state = state,
        grantedPermissions = grantedPermissions,
        errorCode = errorCode,
        errorMsg = errorMsg,
        extras = extras,
        authError = authError,
        authErrorDescription = authErrorDescription,
    )
}
