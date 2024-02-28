package com.tiktok.open.sdk.share.constants

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

internal object Constants {
    /**
     * share
     */
    const val SHARE_REQUEST = 3

    /**
     * share response
     */
    const val SHARE_RESPONSE = 4

    const val SHARE_UNSUPPORTED_ERROR = -3
}

internal object ShareErrorCodes {
    const val SUCCESS = 0
    const val UNKNOWN = -1

    /**
     * App certificate does not match configurations
     * */

    const val CONFIGURATION_ERR = 10011
    /**
     * Illegal authorization scope.
     * */
    const val SCOPE_ERR = 10004

    /**
     * Params parsing error.
     * */
    const val PARAMETER_PARSE_ERR = 20002

    /**
     * "TikTok has no album permissions."
     * */
    const val GALLERY_PERMISSION_ERR = 20005

    /**
     * TikTok Network error
     * */
    const val NETWORK_ERR = 20006

    /**
     * Photo doesn't meet requirements.
     * */
    const val INVALID_PHOTO_ERR = 20008

    /**
     * Processing photo resources failed.
     * */
    const val PHOTO_RESOURCE_ERR = 20010

    /**
     * Video format is not supported.
     * */
    const val INVALID_VIDEO_TYPE_ERR = 20012

    /**
     * Users store shared content for draft or user accounts are not allowed to post videos
     * */
    const val SAVE_TO_DRAFT_ERR = 20016
}
