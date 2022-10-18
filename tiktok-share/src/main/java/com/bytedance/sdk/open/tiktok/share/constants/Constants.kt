package com.bytedance.sdk.open.tiktok.share.constants

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
     * share
     */
    const val SHARE_REQUEST = 3

    /**
     * share response
     */
    const val SHARE_RESPONSE = 4

    object ShareError {
        /**
         * User pubilsh content Failed
         */
        const val SEND_FAIL = -3

        /**
         * Auth Denied
         */
        const val AUTH_DENIED = -4

        /**
         * Unsupported
         */
        const val UNSUPPORT = -5

        /********** subErrorCode ****************/
        /**
         * Not enough permissions to operation.
         */
        const val INVALID_GRANT = 20003

        /**
         * User not login
         */
        const val CANCEL_LOGIN = 20004

        /**
         * App has no album permissions
         */
        const val GALLERY_PERMISSION_ERROR = 20005

        /**
         * App Network error
         */
        const val GRANT_NETWORK_ERR = 20006

        /**
         * Video length doesn't meet requirements
         */
        const val INVALID_VIDEO_LENGTH = 20007

        /**
         * Photo doesn't meet requirements
         * a:both width and height over 360
         * b:1/2.2<=Aspect ratio<=2.2
         */
        const val INVALID_PHOTO = 20008

        /**
         * Timestamp check failed
         */
        const val INVALID_TIMESTAMP = 20009

        /**
         * Processing photo resources failed
         */
        const val PARSE_MEDIA_ERROR = 20010

        /**
         * Video resolution doesn't meet requirements
         * Largest edge is less than 1100
         * Max side / Min side < 4
         */
        const val INVALID_VIDEO_SIZE_RATIO = 20011

        /**
         * Video format is not supported(MP4)
         */
        const val INVALID_VIDEO_TYPE = 20012

        /**
         * Sharing canceled
         */
        const val CANCEL_SHARE = 20013

        /**
         * Another video is currently uploading
         */
        const val LAST_PUBLISH_UNFINISHED = 20014

        /**
         * Users store shared content to draft or user
         * accounts are not allowed to post videos
         */
        const val SAVE_TO_DRAFT = 20015

        /**
         * Post share content failed
         */
        const val PUBLISH_ERROR_UNKNOWN = 20016

        /**
         * Unsupport resolution
         */
        const val INVALID_VIDEO_RESOLUTION = 22001
    }
}
