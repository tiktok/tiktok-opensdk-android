package com.bytedance.sdk.open.tiktok.authorize

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base

class Auth {
    data class Request(
        val scope: String,
        val state: String,
        val optionalScope0: String? = null,
        val optionalScope1: String? = null,
        val language: String? = null,
        override val localEntry: String? = null,
    ) : Base.Request() {
        override val type: Int = Constants.TIKTOK.AUTH_REQUEST

        override fun validate(): Boolean = scope.isNotEmpty() && state.isNotEmpty()

        override fun toBundle(clientKey: String, callerPackageName: String, callerVersion: String?): Bundle {
            return super.toBundle(clientKey, callerPackageName, callerVersion).apply {
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
        val state: String,
        val grantedPermissions: String,
        override val errorCode: Int = Constants.BaseError.OK,
        override val errorMsg: String? = null,
        override val extras: Bundle? = null,
    ) : Base.Response() {
        override val type: Int = Constants.TIKTOK.AUTH_RESPONSE

        override fun fromBundle(bundle: Bundle) {
            super.fromBundle(bundle)
            authCode = bundle.getString(Keys.Auth.AUTH_CODE)
            state = bundle.getString(Keys.Auth.STATE)
            grantedPermissions = bundle.getString(Keys.Auth.GRANTED_PERMISSION)
        }

        override fun toBundle(): Bundle {
            return super.toBundle().apply {
                putString(Keys.Auth.AUTH_CODE, authCode)
                putString(Keys.Auth.STATE, state)
                putString(Keys.Auth.GRANTED_PERMISSION, grantedPermissions)
            }
        }

        fun Bundle.toAuthResponse() =
            Bundle
    }
}
