package com.bytedance.sdk.open.aweme.common.constants;

/**
 * 常量
 * Created by yangzhirong on 2018/10/8.
 */
public interface BDOpenConstants {

    String AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH = "AWEME_EXTRA_IMAGE_MESSAGE_PATH";
    String AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH = "AWEME_EXTRA_VIDEO_MESSAGE_PATH";

    // 定义一些抖音内的事件，广播的形式通知给第三方app
    String ACTION_STAY_IN_DY = "com.aweme.opensdk.action.stay.in.dy";



    /**
     * oauth path
     */
    String OAUTH_PATH = "/oauth/authorize/";

    /**
     * redirect_url path
     */
    String REDIRECT_URL_PATH = "/oauth/authorize/callback/";

    /**
     * meta-info 上版本号错误
     */
    int META_PLATFORM_SDK_VERSION_ERROR = -1;

    /**
     * manifest 上 activity 的 meta-info 中的 platform 版本号
     */
    String META_PLATFORM_SDK_VERSION = "BD_PLATFORM_SDK_VERSION";

    /**
     * 请求参数 key
     * 由于最初BDOpenConstants里面定义的不全，所以新加了这些参数
     */
    interface NewVersionParams {
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


    /**
     * 请求参数 key
     * 旧的参数,后面会逐渐废弃掉
     */
    interface Params {
        /**
         * Bundle 数据类型
         */
        String TYPE = "_bytedance_params_type";
        /**
         * 授权 code
         */
        String AUTH_CODE = "_bytedance_params_authcode";
        /**
         * extra 内容
         */
        String EXTRA = "_bytedance_params_extra";
        /**
         * 注册的 client_key
         */
        String CLIENT_KEY = "_bytedance_params_client_key";
        String STATE = "_bytedance_params_state";

        /**
         * 授权的权限信息，包含必选和用户选择
         */
        String GRANTED_PERMISSION = "_bytedance_params_granted_permission";

        /**
         * 注册的 redirect_uri
         */
        String REDIRECT_URI = "_bytedance_params_redirect_uri";
        /**
         * 结果的 error_code
         */
        String ERROR_CODE = "_bytedance_params_error_code";
        /**
         * 结果的 error_message
         */
        String ERROR_MSG = "_bytedance_params_error_msg";
        /**
         * 请求 scope
         */
        String SCOPE = "_bytedance_params_scope";
        /**
         * 请求 optional_scope0
         */
        String OPTIONAL_SCOPE0 = "_bytedance_params_optional_scope0";
        /**
         * 请求 optional_scope1
         */
        String OPTIONAL_SCOPE1 = "_bytedance_params_optional_scope1";
        /**
         * 调用方 Entry activity
         */
        String FROM_ENTRY = "_bytedance_params_from_entry";
        /**
         * 调用方的 package
         */
        String CALLER_PKG = "_bytedance_params_type_caller_package";
        /**
         * 调用方的base open SDK版本
         */
        String CALLER_BASE_OPEN_VERSION = "__bytedance_base_caller_version";

        /**
         * 支持wap授权页面横竖屏方式
         */
        String WAP_REQUESETED_ORIENTATION = "wap_requested_orientation";
    }

    /**
     * 目标app，区分国内国外
     */
    interface TARGET_APP {

        int AWEME = 1; // 抖音

        int TIKTOK = 2; // TikTok
    }


    /**
     * 各功能要求的最低版本（抖音侧定义）
     */
    interface REQUIRED_API_VERSION {

        // 授权
        int AUTH_REQUIRE_API = 1; // 用于验证api版本是否支持

        // 抖音分享
        int SHARE_REQUIRED_MIN_VERSION = 2; //对应抖音5.2.0及以上 对应dt630及以上,m,11.3
    }

    interface TargetSceneType {
        int SHARE_DEFAULT_TYPE = 0;
    }



    /**
     * 关于webview的key
     */
    interface WebViewConstants {
        String QUERY_RESPONSE_TYPE = "response_type";
        String QUERY_REDIRECT_URI = "redirect_uri";
        String QUERY_CLIENT_KEY = "client_key";
        String QUERY_STATE = "state";
        String QUERY_FROM = "from";
        String QUERY_SCOPE = "scope";
        String QUERY_OPTIONAL_SCOPE = "optionalScope";
        String QUERY_SIGNATURE = "signature";
        String VALUE_FROM_OPENSDK = "opensdk";
        String VALUE_RESPONSE_TYPE_CODE = "code";
        String SCHEMA_HTTPS = "https";
        String REDIRECT_QUERY_CODE = "code";
        String REDIRECT_QUERY_STATE = "state";
        String REDIRECT_QUERY_ERROR_CODE = "errCode";
        String REDIRECT_QUERY_SCOPE = "scopes";
    }


    /**
     * sdk 版本
     */

    interface SdkVersion {
        /**
         * base open sdk 版本号
         */
        String VERSION = "1";
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
}
