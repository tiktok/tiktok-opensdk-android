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

    /*
     * web auth
     */
    const val WEB_AUTH_HOST = "open-api.tiktok.com"
    const val WEB_AUTH_ENDPOINT = "/platform/oauth/connect/"
    const val WEB_AUTH_REDIRECT_URL_PATH = "/oauth/authorize/callback/"

    /*
     * browser auth
     */
    const val BROWSER_AUTH_REDIRECT_HOST = "response.bridge.bytedance.com"
    const val BROWSER_AUTH_REDIRECT_PATH = "/oauth"

    const val NETWORK_NO_CONNECTION = -12

    const val NETWORK_IO_ERROR = -15
}
