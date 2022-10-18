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

internal object Constants {
    /**
     * auth
     */
    const val AUTH_REQUEST = 1

    /**
     * auth response
     */
    const val AUTH_RESPONSE = 2

    const val WEB_AUTH_HOST = "open-api.tiktok.com"
    const val WEB_AUTH_ENDPOINT = "/platform/oauth/connect/"
    const val WEB_AUTH_REDIRECT_URL_PATH = "/oauth/authorize/callback/"

    object AuthError {
        /**
         * newwork no connection
         */
        const val NETWORK_NO_CONNECTION = -12
        /**
         * newwork connection timeout
         */
        const val NETWORK_CONNECT_TIMEOUT = -13
        /**
         * network timeout
         */
        const val NETWORK_TIMEOUT = -14
        /**
         * newwork IO error
         */
        const val NETWORK_IO_ERROR = -15
        /**
         * newwork error: uknown host
         */
        const val NETWORK_UNKNOWN_HOST_ERROR = -16
        /**
         * newwork error
         */
        const val NETWORK_SSL_ERROR = -21

        /**
         * System error
         */
        const val SYSTEM_ERROR = 10001

        /**
         * param error
         */
        const val PARAM_ERROR = 10002

        /**
         * error config（partner_client）
         */
        const val CONFIG_ERROR = 10003

        /**
         * error scope
         */
        const val SCOPE_ERROR = 10004

        /**
         * Missing parameters
         */
        const val MISSING_PARAMS = 10005

        /**
         * error redirect url
         */
        const val REDIRECT_URL_ERROR = 10006

        /**
         * code expired
         */
        const val CODE_EXPIRED = 10007

        /**
         * invalid token
         */
        const val TOKEN_ERROR = 10008

        /**
         * invalid ticket
         */
        const val TICKET_ERROR = 10009

        /**
         * refresh token expired
         */
        var REFRESH_TOKEN_ERROR = 10010

        /**
         * authorization no permission
         */
        var AUTHORIZATION_NO_PERMISSION = 10011
    }
}
