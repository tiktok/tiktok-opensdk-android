package com.bytedance.sdk.open.tiktok.auth

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

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_REQUEST
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_RESPONSE
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.ERROR_CODE
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.ERROR_MSG
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base.EXTRA
import com.bytedance.sdk.open.tiktok.core.model.Base

class Auth {
    data class Request(
        val scope: String,
        val state: String? = null,
        val optionalScope0: String? = null,
        val optionalScope1: String? = null,
        val language: String? = null,
        override val packageName: String,
        override val resultActivityFullPath: String
    ) : Base.Request() {
        override val type: Int = AUTH_REQUEST

        override fun validate(): Boolean = scope.isNotEmpty()

        override fun toBundle(clientKey: String, sdkName: String, sdkVersion: String): Bundle {
            return super.toBundle(sdkName, sdkVersion).apply {
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
