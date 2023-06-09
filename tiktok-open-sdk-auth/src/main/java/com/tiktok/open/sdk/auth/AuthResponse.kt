package com.tiktok.open.sdk.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import com.tiktok.open.sdk.auth.constants.Constants
import com.tiktok.open.sdk.auth.constants.Keys
import com.tiktok.open.sdk.core.constants.Keys.Base.ERROR_CODE
import com.tiktok.open.sdk.core.model.Base

/*
   * AuthResponse
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

data class AuthResponse(
    val authCode: String,
    val state: String?,
    val grantedPermissions: String,
    override val errorCode: Int, // Deprecated
    override val errorMsg: String?, // Deprecated
    override val extras: Bundle? = null,
    val authError: String? = null,
    val authErrorDescription: String? = null,
) : Base.Response() {
    override val type: Int = Constants.AUTH_RESPONSE

    override fun toBundle(): Bundle {
        return super.toBundle().apply {
            putString(Keys.Auth.AUTH_CODE, authCode)
            putString(Keys.Auth.STATE, state)
            putString(Keys.Auth.GRANTED_PERMISSION, grantedPermissions)
        }
    }
}

internal fun Bundle.toAuthResponse(): AuthResponse {
    val authCode = getString(Keys.Auth.AUTH_CODE, "")
    val state = getString(Keys.Auth.STATE)
    val grantedPermissions = getString(Keys.Auth.GRANTED_PERMISSION, "")
    val errorCode = getInt(ERROR_CODE)
    val errorMsg = getString(com.tiktok.open.sdk.core.constants.Keys.Base.ERROR_MSG)
    val extras = getBundle(com.tiktok.open.sdk.core.constants.Keys.Base.EXTRA)
    val authError = getString(Keys.Auth.AUTH_ERROR)
    val authErrorDescription = getString(Keys.Auth.AUTH_ERROR_DESCRIPTION)
    return AuthResponse(
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
