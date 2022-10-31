package com.bytedance.sdk.open.tiktok.auth.constants

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

internal object Keys {

    object Auth {
        const val AUTH_CODE = "_bytedance_params_authcode"
        const val CLIENT_KEY = "_bytedance_params_client_key"
        const val STATE = "_bytedance_params_state"
        const val GRANTED_PERMISSION = "_bytedance_params_granted_permission"
        const val SCOPE = "_bytedance_params_scope"
        const val OPTIONAL_SCOPE0 = "_bytedance_params_optional_scope0"
        const val OPTIONAL_SCOPE1 = "_bytedance_params_optional_scope1"
        const val LANGUAGE = "language"
    }
    object WebAuth {
        const val QUERY_RESPONSE_TYPE = "response_type"
        const val QUERY_REDIRECT_URI = "redirect_uri"
        const val QUERY_CLIENT_KEY = "client_key"
        const val QUERY_STATE = "state"
        const val QUERY_FROM = "from"
        const val QUERY_SCOPE = "scope"
        const val QUERY_OPTIONAL_SCOPE = "optionalScope"
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
        const val QUERY_ACCEPT_LANGUAGE = "accept_language"
    }
}
