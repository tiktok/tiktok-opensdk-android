package com.bytedance.sdk.open.aweme.common.constants;

/**
 * 常量
 * Created by yangzhirong on 2018/10/8.
 */
public interface ParamKeyConstants {

    String AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH = "AWEME_EXTRA_IMAGE_MESSAGE_PATH";
    String AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH = "AWEME_EXTRA_VIDEO_MESSAGE_PATH";

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


    interface AuthParams {
        /**
         * 授权 code
         */
        String AUTH_CODE = "_bytedance_params_authcode";
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
         * 支持wap授权页面横竖屏方式
         */
        String WAP_REQUESETED_ORIENTATION = "wap_requested_orientation";

    }

    interface ShareParams {
        String STATE = "_aweme_open_sdk_params_state";
        String CLIENT_KEY = "_aweme_open_sdk_params_client_key";
        String CALLER_PKG = "_aweme_open_sdk_params_caller_package";
        String CALLER_SDK_VERSION = "_aweme_open_sdk_params_caller_sdk_version";
        String CALLER_LOCAL_ENTRY = "_aweme_open_sdk_params_caller_local_entry";
        String SHARE_TARGET_SCENE = "_aweme_open_sdk_params_target_scene";

        // 因为工具线整体组件化下沉，引用不到DYOpenConstants，所以这里暂时也要写一下~ 555~ 要改！
        String SHARE_DEFAULT_HASHTAG = "_aweme_open_sdk_params_target_scene";

        String SHARE_MICROAPP_INFO = "_aweme_open_sdk_params_micro_app_info";

        String TYPE = "_aweme_open_sdk_params_type";

        String ERROR_CODE = "_aweme_open_sdk_params_error_code";
        String ERROR_MSG = "_aweme_open_sdk_params_error_msg";
    }

    interface BaseParams {
        /**
         * Bundle 数据类型
         */
        String TYPE = "_bytedance_params_type";

        /**
         * extra 内容
         */
        String EXTRA = "_bytedance_params_extra";

        /**
         * 结果的 error_code
         */
        String ERROR_CODE = "_bytedance_params_error_code";
        /**
         * 结果的 error_message
         */
        String ERROR_MSG = "_bytedance_params_error_msg";

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
    }

    /**
     * 各功能要求的最低版本（抖音侧定义）
     */
    interface REQUIRED_API_VERSION {

        // 授权
        int AUTH_REQUIRE_API = 1; // 用于验证api版本是否支持

        // 抖音分享
        int SHARE_REQUIRED_MIN_VERSION = 2; //对应抖音5.2.0及以上 对应dt630及以上,m,11.3

        // TODO: 2019-07-12 补充抖音及MT 对应版本，等待端上修改上车
        //修改API命名兼容老版本
        int MIN_SDK_NEW_TIKTOK_API = 3;//对应抖音xxx

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
        String QUERY_ENCRIPTION_PACKAGE = "app_identity";
        String QUERY_PLATFORM = "device_platform";
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


}
