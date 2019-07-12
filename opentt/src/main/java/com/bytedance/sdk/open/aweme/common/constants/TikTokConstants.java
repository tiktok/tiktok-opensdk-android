package com.bytedance.sdk.open.aweme.common.constants;

public interface TikTokConstants {

    // 定义一些抖音内的事件，广播的形式通知给第三方app
    String ACTION_STAY_IN_DY = "com.aweme.opensdk.action.stay.in.dy";

    /**
     * 错误码
     */
    interface ErrorCode {
        /**
         * 正常
         */
        int ERROR_CODE_OK = 0;
        /**
         * 未知错误
         */
        int ERROR_CODE_UNKNOW = -1;
        /**
         * 用户手动取消
         */
        int ERROR_CODE_CANCEL = -2;

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
         * ticket过期
         */
        int ERROR_TICKET = 10009;
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
        int SHARE_CONTENT_TO_DY = 3;

        /**
         * share response
         */
        int SHARE_CONTENT_TO_DY_RESP = 4;
    }

    /**
     * 目标app，区分国内国外
     */
    interface TARGET_APP {

        int AWEME = 1; // 抖音

        int TIKTOK = 2; // TikTok
    }

}
