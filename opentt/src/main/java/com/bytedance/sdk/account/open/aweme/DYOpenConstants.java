package com.bytedance.sdk.account.open.aweme;

public class DYOpenConstants {

    public static final int OPEN_SDK_VERSION = 0;
    public static final int SUPPORT_DY_VERSION = 0;

    public static final String AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH = "AWEME_EXTRA_IMAGE_MESSAGE_PATH";
    public static final String AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH = "AWEME_EXTRA_VIDEO_MESSAGE_PATH";


    /**
     * Bundle 动作类型
     */
    public interface ModeType {
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
     * 请求参数 key
     */
    public interface Params {
        String STATE = "_aweme_open_sdk_params_state";
        String CLIENT_KEY = "_aweme_open_sdk_params_client_key";
        String CALLER_PKG = "_aweme_open_sdk_params_caller_package";
        String CALLER_SDK_VERSION = "_aweme_open_sdk_params_caller_sdk_version";
        String CALLER_LOCAL_ENTRY = "_aweme_open_sdk_params_caller_local_entry";
        String SHARE_TARGET_SCENE = "_aweme_open_sdk_params_target_scene";

        /**
         * extra 内容
         */
        String EXTRA = "_aweme_open_sdk_params_extra";
        String TYPE = "_aweme_open_sdk_params_type";

        String ERROR_CODE = "_aweme_open_sdk_params_error_code";
        String ERROR_MSG = "_aweme_open_sdk_params_error_msg";
    }

    public interface TargetSceneType {
        int SHARE_DEFAULT_TYPE = 0;
    }

    public interface ErrorCode {
        /**
         * 正常
         */
        int OK = 10000;
        /**
         * 未知错误
         */
        int UNKNOW = 10001;

        /**
         * 无效的请求参数
         */
        int INVALID_REQ = 10002;

        /**
         * 请求授权时网络出错
         */
        int GRANT_NETWORK_ERR = 10003;

        /**
         * 授权失败/无权限
         */
        int INVALID_GRANT = 10004;

        /**
         * 用户手动取消登录
         */
        int CANCEL_LOGIN = 10005;
        /**
         * 用户未授权使用相册
         */
        int GALLERY_PERMISSION_ERROR = 10006;

        /**
         * 分享图文不支持
         * 要求
         * a:宽高都大于360
         * b:1/2.2<=宽高比<=2.2
         */
        int INVALID_PHOTO = 10007;

        /**
         * 视频时长不支持
         */
        int INVALID_VIDEO_LENGTH = 10008;

        /**
         * 视频尺寸不支持
         * 最大边小于1100
         */
        int INVALID_VIDEO_SIZE = 10009;

        /**
         * 视频格式不支持（要求mp4）
         */
        int INVALID_VIDEO_TYPE = 10010;

        /**
         * 视频比例不支持
         * 最大边除以最小边 要小于4
         *
         */
        int INVALID_VIDEO_RATIO = 10011;

        /**
         *  解析媒体资源失败，包含图片，视频
         */
        int PARSE_MEDIA_FAIL = 10012;

        /**
         *  不支持的分辨率
         */
        int INVALID_VIDEO_RESOLUTION = 10013;

        /**
         *  视频整体时长超过1小时（多视频场景）
         */
        int OVER_MAX_VIDEO_LENGTH = 10014;

        /**
         * 用户手动取消发布
         */
        int CANCEL_PUBLISH = 10015;

        /**
         * 用户存为草稿
         */
        int SAVE_TO_DRAFT = 10016;
    }
}
