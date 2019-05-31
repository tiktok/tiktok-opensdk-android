package com.bytedance.sdk.open.aweme;

public class DYOpenConstants {

    public static final int OPEN_SDK_VERSION = 0;
    public static final int SUPPORT_DY_VERSION = 0;

    public static final String AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH = "AWEME_EXTRA_IMAGE_MESSAGE_PATH";
    public static final String AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH = "AWEME_EXTRA_VIDEO_MESSAGE_PATH";

    // 定义一些抖音内的事件，广播的形式通知给第三方app
    public static final String ACTION_STAY_IN_DY = "com.aweme.opensdk.action.stay.in.dy";


    /**
     * 目标app，区分国内国外
     */
    public interface TARGET_APP {

        int AWEME = 1; // 抖音

        int TIKTOK = 2; // TikTok
    }

    /**
     * 各功能要求的最低版本（抖音侧定义）
     */
    public interface REQUIRED_API_VERSION {

        // 授权
        static final int AUTH_REQUIRE_API = 1; // 用于验证api版本是否支持

        // 抖音分享
        int SHARE_REQUIRED_MIN_VERSION = 2; //对应抖音5.2.0及以上 对应dt630及以上,m,11.3
    }

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

        // 因为工具线整体组件化下沉，引用不到DYOpenConstants，所以这里暂时也要写一下~ 555~ 要改！
        String SHARE_DEFAULT_HASHTAG = "_aweme_open_sdk_params_target_scene";

        String SHARE_MICROAPP_INFO = "_aweme_open_sdk_params_micro_app_info";
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
        int ERR_OK = 0;      // 成功
        int ERR_FAILED_COMMON = -1; // 通用错误类型
        int ERR_USER_CANCEL = -2; // 用户手动取消
        int ERR_SEND_FAIL = -3; // 发送失败
        int ERR_AUTH_DENIED = -4; // 权限错误
        int ERR_UNSUPPORT = -5; // 不支持
    }
}
