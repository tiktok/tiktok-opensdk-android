package com.bytedance.sdk.open.aweme;

public interface TikTokConstants {

    // send broadcast to your app
    String ACTION_STAY_IN_TT = "com.aweme.opensdk.action.stay.in.dy";

    interface BaseErrorCode {
        /**
         * success
         */
        int OK = 0;
        /**
         * unknown error
         */
        int ERROR_UNKNOW = -1;
        /**
         * User Canceled
         */
        int ERROR_CANCEL = -2;
    }



    interface ShareErrorCode {
        /**
         * User pubilsh content Failed
         */
        int ERROR_CODE_SEND_FAIL = -3;

        /**
         * Auth Denied
         */
        int ERROR_CODE_AUTH_DENIED = -4;

        /**
         * Unsupported
         */
        int ERROR_CODE_UNSUPPORT = -5;



        /********** subErrorCode ****************
         *

        /**
         * Not enough permissions to operation.
         */
        int INVALID_GRANT = 20003;

        /**
         * User not login
         */
        int CANCEL_LOGIN = 20004;

        /**
         * TikTok has no album permissions
         */
        int GALLERY_PERMISSION_ERROR = 20005;

        /**
         * TikTok Network error
         */
        int GRANT_NETWORK_ERR = 20006;

        /**
         * Video length doesn't meet requirements
         */
        int INVALID_VIDEO_LENGTH = 20007;

        /**
         * Photo doesn't meet requirements
         * 要求
         * a:both width and height over 360
         * b:1/2.2<=Aspect ratio<=2.2
         */
        int INVALID_PHOTO = 20008;

        /**
         * Timestamp check failed
         */
        int TIME_STAMP_INVALID = 20009;

        /**
         * Processing photo resources faild
         */
        int PARSE_MEDIA_FAIL = 20010;

        /**
         * Video resolution doesn't meet requirements
         * Largest edge is less than 1100
         * Max side / Min side < 4
         */
        int INVALID_VIDEO_SIZE_RATIO = 20011;

        /**
         * Video format is not supported(MP4)
         */
        int INVALID_VIDEO_TYPE = 20012;

        /**
         * Sharing canceled
         */
        int CANCEL_PUBLISH = 20013;

        /**
         * Another video is currently uploading
         */
        int LAST_PUBLISH_NOT_FINISH = 20014;

        /**
         * Users store shared content for draft or user
         * accounts are not allowed to post videos
         */
        int SAVE_TO_DRAFT = 20015;

        /**
         * Post share content failed
         */
        int PUBLISH_FAIL_UNKNOWN = 20016;

        /**
         * Unsupport resolution
         */
        int INVALID_VIDEO_RESOLUTION = 22001;

    }

    interface AuthErrorCode {
        /**
         * -12、-13、 -14、 -15、 -16、-21 newwork exception
         */
        int ERROR_NETWORK_NO_CONNECTION = -12;
        int ERROR_NETWORK_CONNECT_TIMEOUT = -13;
        int ERROR_NETWORK_TIMEOUT = -14;
        int ERROR_NETWORK_IO = -15;
        int ERROR_NETWORK_UNKNOWN_HOST_ERROR = -16;
        int ERROR_NETWORK_SSL = -21;

        /**
         * System error
         */
        int ERROR_SYSTEM = 10001;

        /**
         * param error
         */
        int ERROR_PARAM = 10002;

        /**
         * error config（partner_client）
         */
        int ERROR_CONFIG = 10003;

        /**
         * error scope
         */
        int ERROR_SCOPE = 10004;

        /**
         * Missing parameters
         */
        int ERROR_N_PARAMS = 10005;

        /**
         * error redirect url
         */
        int ERROR_REDIRECT_URL = 10006;
        /**
         * code expired
         */
        int ERROR_CODE_EXPIRED = 10007;

        /**
         * error token
         */
        int ERROR_TOKEN = 10008;

        /**
         * error ticked
         */
        int ERROR_TICKET = 10009;

        /**
         * refresh token expired
         */
        int ERROR_REFRESH_TOKEN = 10010;

        /**
         * authorization no permission
         */
        int ERROR_AUTHORIZATION_NO_PERMISSION = 10011;

    }
    /**
     * Bundle type
     */
    interface ModeType {
        /**
         * auth Request
         */
        int SEND_AUTH_REQUEST = 1;
        /**
         * auth Response
         */
        int SEND_AUTH_RESPONSE = 2;

        /**
         * share
         */
        int SHARE_CONTENT_TO_TT = 3;

        /**
         * share response
         */
        int SHARE_CONTENT_TO_TT_RESP = 4;
    }

    /**
     * target app
     */
    interface TARGET_APP {

        int AWEME = 1; // DOUYIN

        int TIKTOK = 2; // TikTok
    }

}
