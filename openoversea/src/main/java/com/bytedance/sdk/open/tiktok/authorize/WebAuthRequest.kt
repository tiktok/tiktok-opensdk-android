package com.bytedance.sdk.open.tiktok.authorize

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Keys

internal data class WebAuthRequest(
    val clientKey: String,
    val state: String?,
    val scope: String,
    val optionalScope0: String?,
    val optionalScope1: String?,
    val language: String?,
    val resultActivityPackageName: String,
    val resultActivityClassPath: String,
)

internal fun Bundle.toWebAuthRequest(): WebAuthRequest? {
    val clientKey = getString(Keys.Auth.CLIENT_KEY)
    val state = getString(Keys.Auth.STATE)
    val scope = getString(Keys.Auth.SCOPE)
    val optionalScope0 = getString(Keys.Auth.OPTIONAL_SCOPE0)
    val optionalScope1 = getString(Keys.Auth.OPTIONAL_SCOPE1)
    val language = getString(Keys.Auth.LANGUAGE)
    val resultReceiverPackageName = getString(Keys.Base.CALLER_PKG)
    val resultClassPath = getString(Keys.Base.FROM_ENTRY)

    if (clientKey.isNullOrEmpty() || scope.isNullOrEmpty() || resultReceiverPackageName.isNullOrEmpty() || resultClassPath.isNullOrEmpty()) {
        return null
    }
    return WebAuthRequest(
        clientKey,
        state,
        scope,
        optionalScope0,
        optionalScope1,
        language,
        resultReceiverPackageName,
        resultClassPath
    )
}
