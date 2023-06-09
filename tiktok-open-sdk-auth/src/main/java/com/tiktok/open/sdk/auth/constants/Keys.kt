package com.tiktok.open.sdk.auth.constants

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
        const val AUTH_ERROR = "_bytedance_params_auth_error"
        const val AUTH_ERROR_DESCRIPTION = "_bytedance_params_auth_error_description"
        const val REDIRECT_URI = "_bytedance_params_redirect_uri"
        const val CODE_CHALLENGE = "_bytedance_params_code_challenge"
    }

    object WebAuth {
        const val QUERY_RESPONSE_TYPE = "response_type"
        const val QUERY_REDIRECT_URI = "redirect_uri"
        const val QUERY_CLIENT_KEY = "client_key"
        const val QUERY_STATE = "state"
        const val QUERY_SDK_NAME = "sdk_name"
        const val QUERY_SCOPE = "scope"
        const val SCHEMA_HTTPS = "https"
        const val QUERY_APP_IDENTITY = "app_identity"
        const val QUERY_CERTIFICATE = "certificate"
        const val QUERY_PLATFORM = "device_platform"
        const val QUERY_LANGUAGE = "lang"
        const val QUERY_CODE_CHALLENGE = "code_challenge"

        const val REDIRECT_QUERY_CODE = "code"
        const val REDIRECT_QUERY_STATE = "state"
        const val REDIRECT_QUERY_ERROR_CODE = "errCode"
        const val REDIRECT_QUERY_ERROR = "error"
        const val REDIRECT_QUERY_ERROR_DESCRIPTION = "error_description"
        const val REDIRECT_QUERY_SCOPE = "scopes"
        const val REDIRECT_QUERY_ERROR_MESSAGE = "error"
    }
}
