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
       * @param scope the scopes required, scopes should be concatenated with ",", e.g. "scope1, scope2, scope3"
       * @param state state in request should match the state from the response received.
       * @param language the language.
       * @param packageName the package name of your app.
       * @param resultActivityFullPath the path of the activity being used to receive the authorization result information
     */
    @Parcelize
    data class Request(
        val scope: String,
        val state: String? = null,
        val language: String? = null,
        override val packageName: String,
        override val resultActivityFullPath: String
    ) : Base.Request() {
        @IgnoredOnParcel
        override val type: Int = AUTH_REQUEST

        override fun validate(): Boolean = scope.isNotEmpty()

        override fun toBundle(clientKey: String, sdkName: String, sdkVersion: String): Bundle {
            return super.toBundle(sdkName, sdkVersion).apply {
                putString(Keys.Auth.CLIENT_KEY, clientKey)
                putString(Keys.Auth.STATE, state)
                putString(Keys.Auth.SCOPE, scope)
                putString(Keys.Auth.LANGUAGE, language)
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
     */
    data class Response(
        val authCode: String,
        val state: String?,
        val grantedPermissions: String,
        override val errorCode: Int,
        override val errorMsg: String?,
        override val extras: Bundle? = null,
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
    return Auth.Response(
        authCode = authCode,
        state = state,
        grantedPermissions = grantedPermissions,
        errorCode = errorCode,
        errorMsg = errorMsg,
        extras = extras
    )
}
