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
import com.tiktok.open.sdk.auth.utils.PKCEUtils
import com.tiktok.open.sdk.core.model.Base
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/*
   * AuthRequest
   *
   * @param clientKey your app's client key.
   * @param scope the scopes required, scopes should be concatenated with ",", e.g. "scope1, scope2, scope3"
   * @param redirectUri the specified endpoint URI which Auth server redirects after the users granted permissions for the client
   * @param codeVerifier randomly generated string on the application side for the authorization request
   * @param state state in request should match the state from the response received.
   * @param language the language.
 */

@Parcelize
data class AuthRequest(
    val clientKey: String,
    val scope: String,
    val redirectUri: String,
    val codeVerifier: String,
    val state: String? = null,
    val language: String? = null,
) : Base.Request() {
    @IgnoredOnParcel
    override val type: Int = Constants.AUTH_REQUEST

    override fun validate(): Boolean = scope.isNotEmpty() && clientKey.isNotEmpty() && redirectUri.isNotEmpty() && codeVerifier.isNotEmpty()

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
