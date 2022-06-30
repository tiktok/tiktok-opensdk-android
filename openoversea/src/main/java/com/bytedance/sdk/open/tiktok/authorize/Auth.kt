package com.bytedance.sdk.open.tiktok.authorize

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base

class Auth {
    class Request : Base.Request() {
        override val type: Int = Constants.TIKTOK.AUTH_REQUEST
        var state: String? = null
        var redirectUri: String? = null
        var clientKey: String? = null
        var scope: String? = null
        var optionalScope0: String? = null
        var optionalScope1: String? = null
        var language: String? = null

        override fun validate(): Boolean {
            return true // clientKey?.isNotEmpty() ?: false
        }

        override fun toBundle(): Bundle {
            return super.toBundle().apply {
                putString(Keys.Auth.STATE, state)
                putString(Keys.Auth.REDIRECT_URI, redirectUri)
                putString(Keys.Auth.CLIENT_KEY, clientKey)
                putString(Keys.Auth.SCOPE, scope)
                putString(Keys.Auth.OPTIONAL_SCOPE0, optionalScope0)
                putString(Keys.Auth.OPTIONAL_SCOPE1, optionalScope1)
                putString(Keys.Auth.LANGUAGE, language)
            }
        }

        override fun fromBundle(bundle: Bundle) {
            super.fromBundle(bundle)
            state = bundle.getString(Keys.Auth.STATE)
            redirectUri = bundle.getString(Keys.Auth.REDIRECT_URI)
            clientKey = bundle.getString(Keys.Auth.CLIENT_KEY)
            scope = bundle.getString(Keys.Auth.SCOPE)
            optionalScope0 = bundle.getString(Keys.Auth.OPTIONAL_SCOPE0)
            optionalScope1 = bundle.getString(Keys.Auth.OPTIONAL_SCOPE1)
            language = bundle.getString(Keys.Auth.LANGUAGE)
        }
    }
    class Response : Base.Response() {
        override val type: Int = Constants.TIKTOK.AUTH_RESPONSE
        var authCode: String? = null
        var state: String? = null
        var grantedPermissions: String? = null

        override fun validate(): Boolean {
            return true
        }

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
    }
}