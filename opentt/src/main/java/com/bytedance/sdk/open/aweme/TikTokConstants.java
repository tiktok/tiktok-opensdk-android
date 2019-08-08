package com.bytedance.sdk.open.aweme;

public interface TikTokConstants {

    // 定义一些抖音内的事件，广播的形式通知给第三方app
    String ACTION_STAY_IN_TT = "com.aweme.opensdk.action.stay.in.dy";

    interface BaseErrorCode {
        /**
         * 正常
         */
        int OK = 0;
        /**
         * 未知错误
         */
        int ERROR_UNKNOW = -1;
        /**
         * 用户手动取消
         */
        int ERROR_CANCEL = -2;
    }



    interface ShareErrorCode {
        /**
         * 发送失败，分享使用
         */
        int ERROR_CODE_SEND_FAIL = -3;

        /**
         * 权限错误, 第三方未获取相关分享权限或获取权限失败
         */
        int ERROR_CODE_AUTH_DENIED = -4;

        /**
         * 文件解析过程出错或命中其它的一些限制，分享使用
         */
        int ERROR_CODE_UNSUPPORT = -5;

    }

    interface AuthErrorCode {
        /**
         * -12、-13、 -14、 -15、 -16、-21 表示网络请求过程中的一些exception
         */
        int ERROR_NETWORK_NO_CONNECTION = -12;
        int ERROR_NETWORK_CONNECT_TIMEOUT = -13;
        int ERROR_NETWORK_TIMEOUT = -14;
        int ERROR_NETWORK_IO = -15;
        int ERROR_NETWORK_UNKNOWN_HOST_ERROR = -16;
        int ERROR_NETWORK_SSL = -21;

        /**
         * 系统异常
         */
        int ERROR_SYSTEM = 10001;

        /**
         * 参数错误
         */
        int ERROR_PARAM = 10002;

        /**
         * 非法的配置（partner_client）
         */
        int ERROR_CONFIG = 10003;

        /**
         * 非法的scope
         */
        int ERROR_SCOPE = 10004;

        /**
         * 缺少参数
         */
        int ERROR_N_PARAMS = 10005;

        /**
         * 非法重定向url
         */
        int ERROR_REDIRECT_URL = 10006;
        /**
         * code已失效
         */
        int ERROR_CODE_EXPIRED = 10007;

        /**
         * 无效token
         */
        int ERROR_TOKEN = 10008;

        /**
         * 非法参数
         */
        int ERROR_TICKET = 10009;

        /**
         * refresh token 过期
         */
        int ERROR_REFRESH_TOKEN = 10010;

    }
    /**
     * Bundle 动作类型
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
     * 目标app，区分国内国外
     */
    interface TARGET_APP {

        int AWEME = 1; // 抖音

        int TIKTOK = 2; // TikTok
    }

}
