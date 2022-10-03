package com.bytedance.sdk.open.tiktok.authorize

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent

class Auth {
    data class Request(
        val scope: String,
        val state: String? = null,
        val optionalScope0: String? = null,
        val optionalScope1: String? = null,
        val language: String? = null,
        override val resultActivityComponent: ResultActivityComponent,
    ) : Base.Request() {
        override val type: Int = Constants.TIKTOK.AUTH_REQUEST

        override fun validate(): Boolean = scope.isNotEmpty()

        override fun toBundle(clientKey: String): Bundle {
            return super.toBundle().apply {
                putString(Keys.Auth.CLIENT_KEY, clientKey)
                putString(Keys.Auth.STATE, state)
                putString(Keys.Auth.SCOPE, scope)
                putString(Keys.Auth.OPTIONAL_SCOPE0, optionalScope0)
                putString(Keys.Auth.OPTIONAL_SCOPE1, optionalScope1)
                putString(Keys.Auth.LANGUAGE, language)
            }
        }
    }

    data class Response(
        val authCode: String,
        val state: String?,
        val grantedPermissions: String,
        override val errorCode: Int,
        override val errorMsg: String?,
        override val extras: Bundle? = null,
    ) : Base.Response() {
        override val type: Int = Constants.TIKTOK.AUTH_RESPONSE

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
    val errorCode = getInt(Keys.Base.ERROR_CODE)
    val errorMsg = getString(Keys.Base.ERROR_MSG)
    val extras = getBundle(Keys.Base.EXTRA)
    return Auth.Response(
        authCode = authCode,
        state = state,
        grantedPermissions = grantedPermissions,
        errorCode = errorCode,
        errorMsg = errorMsg,
        extras = extras
    )
}
