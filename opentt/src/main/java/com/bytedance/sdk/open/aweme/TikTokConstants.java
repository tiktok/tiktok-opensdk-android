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



        /********** subErrorCode ****************
         *

        /**
         * 授权失败/无权限
         */
        int INVALID_GRANT = 20003;

        /**
         * 用户手动取消登录
         */
        int CANCEL_LOGIN = 20004;

        /**
         * 用户未授权使用相册
         */
        int GALLERY_PERMISSION_ERROR = 20005;

        /**
         * 请求授权时网络出错
         */
        int GRANT_NETWORK_ERR = 20006;

        /**
         * 视频时长不支持
         */
        int INVALID_VIDEO_LENGTH = 20007;

        /**
         * 分享图文不支持 ab开关关闭
         * 要求
         * a:宽高都大于360
         * b:1/2.2<=宽高比<=2.2
         */
        int INVALID_PHOTO = 20008;

        /**
         * 时间戳校验失败
         */
        int TIME_STAMP_INVALID = 20009;

        /**
         * 解析媒体资源失败，包含图片，视频
         */
        int PARSE_MEDIA_FAIL = 20010;

        /**
         * 视频尺寸或比例不支持
         * 最大边小于1100
         * 最大边除以最小边 要小于4
         */
        int INVALID_VIDEO_SIZE_RATIO = 20011;

        /**
         * 视频格式不支持（要求mp4）
         */
        int INVALID_VIDEO_TYPE = 20012;

        /**
         * 用户手动取消发布
         */
        int CANCEL_PUBLISH = 20013;

        /**
         * 有正在上传的作品，需要发布完成后再拍摄
         */
        int LAST_PUBLISH_NOT_FINISH = 20014;

        /**
         * 用户存为草稿
         */
        int SAVE_TO_DRAFT = 20015;

        /**
         * 用户点击发布视频按钮，但因为网络或其他原因，视频没有发布成功
         */
        int PUBLISH_FAIL_UNKNOWN = 20016;

        /**
         * 不支持的分辨率 android 独有
         */
        int INVALID_VIDEO_RESOLUTION = 22001;

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

        /**
         * 授权鉴权失败
         */
        int ERROR_AUTHORIZATION_NO_PERMISSION = 10011;

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
