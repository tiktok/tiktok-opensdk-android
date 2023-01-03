package com.bytedance.sdk.open.tiktok.auth.constants

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

internal object Keys {

    object Auth {
        const val AUTH_CODE = "_bytedance_params_authcode"
        const val CLIENT_KEY = "_bytedance_params_client_key"
        const val STATE = "_bytedance_params_state"
        const val GRANTED_PERMISSION = "_bytedance_params_granted_permission"
        const val SCOPE = "_bytedance_params_scope"
        const val LANGUAGE = "language"
    }
    object WebAuth {
        const val QUERY_RESPONSE_TYPE = "response_type"
        const val QUERY_REDIRECT_URI = "redirect_uri"
        const val QUERY_CLIENT_KEY = "client_key"
        const val QUERY_STATE = "state"
        const val QUERY_FROM = "from"
        const val QUERY_SCOPE = "scope"
        const val QUERY_SIGNATURE = "signature"
        const val VALUE_FROM_OPENSDK = "opensdk"
        const val VALUE_RESPONSE_TYPE_CODE = "code"
        const val SCHEMA_HTTPS = "https"
        const val REDIRECT_QUERY_CODE = "code"
        const val REDIRECT_QUERY_STATE = "state"
        const val REDIRECT_QUERY_ERROR_CODE = "errCode"
        const val REDIRECT_QUERY_ERROR_MESSAGE = "error_string"
        const val REDIRECT_QUERY_SCOPE = "scopes"
        const val QUERY_ENCRYPTION_PACKAGE = "app_identity"
        const val QUERY_PLATFORM = "device_platform"
        const val QUERY_OS_TYPE = "os_type"
        const val QUERY_OS_FROM = "os_from"
        const val QUERY_LANGUAGE = "lang"
    }
}
