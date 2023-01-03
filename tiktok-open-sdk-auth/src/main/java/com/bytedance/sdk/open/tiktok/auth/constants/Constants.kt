package com.bytedance.sdk.open.tiktok.auth.constants

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
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
