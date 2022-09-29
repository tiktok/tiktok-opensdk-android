package com.bytedance.sdk.open.tiktok.common.constants

internal object Constants {
    object BaseError {
        const val OK = 0
        const val ERROR_UNKNOWN = -1
        const val ERROR_CANCEL = -2
    }
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

    object TIKTOK {
        /**
         * auth Request
         */
        const val AUTH_REQUEST = 1

        /**
         * auth Response
         */
        const val AUTH_RESPONSE = 2

        /**
         * share
         */
        const val SHARE_REQUEST = 3

        /**
         * share response
         */
        const val SHARE_RESPONSE = 4
    }
    enum class APIType {
        AUTH, SHARE
    }
}
