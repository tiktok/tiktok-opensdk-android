package com.bytedance.sdk.open.tiktok.common.constants;

/**
 *
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
     * meta-info version error
     */
    int META_PLATFORM_SDK_VERSION_ERROR = -1;

    /**
     * platform version in meta-info of activity's manifest
     */
    String META_PLATFORM_SDK_VERSION = "BD_PLATFORM_SDK_VERSION";

    int AUTH_REQUEST_CODE = 100;
    int SHARE_TO_IM_CODE = 101;


    interface AuthParams {
        /**
         * auth code
         */
        String AUTH_CODE = "_bytedance_params_authcode";
        /**
         * client_key
         */
        String CLIENT_KEY = "_bytedance_params_client_key";
        String STATE = "_bytedance_params_state";
        /**
         * granted permission
         */
        String GRANTED_PERMISSION = "_bytedance_params_granted_permission";

        /**
         * redirect_uri
         */
        String REDIRECT_URI = "_bytedance_params_redirect_uri";

        /**
         * scope
         */
        String SCOPE = "_bytedance_params_scope";
        /**
         * optional_scope0
         */
        String OPTIONAL_SCOPE0 = "_bytedance_params_optional_scope0";
        /**
         * optional_scope1
         */
        String OPTIONAL_SCOPE1 = "_bytedance_params_optional_scope1";

        /**
         * wap orientation
         */
        String WAP_REQUESETED_ORIENTATION = "wap_requested_orientation";

    }

    interface ShareParams {
        String STATE = "_aweme_open_sdk_params_state";
        String CLIENT_KEY = "_aweme_open_sdk_params_client_key";
        String CALLER_PKG = "_aweme_open_sdk_params_caller_package";
        String CALLER_SDK_VERSION = "_aweme_open_sdk_params_caller_sdk_version";
        String CALLER_LOCAL_ENTRY = "_aweme_open_sdk_params_caller_local_entry";
        String SHARE_TARGET_SCENE = "_aweme_open_sdk_params_target_landpage_scene";

        String SHARE_DEFAULT_HASHTAG = "_aweme_open_sdk_params_target_scene";
        String SHARE_HASHTAG_LIST = "_aweme_open_sdk_params_hashtag_list";


        String SHARE_MICROAPP_INFO = "_aweme_open_sdk_params_micro_app_info";

        String TYPE = "_aweme_open_sdk_params_type";

        String ERROR_CODE = "_aweme_open_sdk_params_error_code";
        String ERROR_MSG = "_aweme_open_sdk_params_error_msg";
        String SHARE_SUB_ERROR_CODE = "_aweme_open_sdk_params_sub_error_code";


        String SHARE_ANCHOR_INFO = "_aweme_open_sdk_params_anchor_info";

    }

    interface BaseParams {
        /**
         * Bundle data type
         */
        String TYPE = "_bytedance_params_type";

        /**
         * extra
         */
        String EXTRA = "_bytedance_params_extra";

        /**
         * result error_code
         */
        String ERROR_CODE = "_bytedance_params_error_code";
        /**
         * result error_message
         */
        String ERROR_MSG = "_bytedance_params_error_msg";

        /**
         * your entry activity
         */
        String FROM_ENTRY = "_bytedance_params_from_entry";
        /**
         * your package
         */
        String CALLER_PKG = "_bytedance_params_type_caller_package";
        /**
         * your base open SDK version
         */
        String CALLER_BASE_OPEN_VERSION = "__bytedance_base_caller_version";


        String CALLER_BASE_OPEN_SDK_NAME = "_aweme_params_caller_open_sdk_name";
        String CALLER_BASE_OPEN_SDK_VERSION = "_aweme_params_caller_open_sdk_version";

        String CALLER_BASE_OPEN_SDK_COMMON_NAME = "_aweme_params_caller_open_sdk_common_name";
        String CALLER_BASE_OPEN_SDK_COMMON_VERSION = "_aweme_params_caller_open_sdk_common_version";


    }

    /**
     * required  version of platform
     */
    interface REQUIRED_API_VERSION {

        int AUTH_REQUIRE_API = 1;

        // sharing
        int SHARE_REQUIRED_MIN_VERSION = 2;

        //adapt to old version because of changing api name
        int MIN_SDK_NEW_VERSION_API = 3;

        //skipping 4 as Tiktok repo already was using version 4
        int SHARE_SUPPORT_FILEPROVIDER = 5;

        //Special for Lite Authorize
        int AUTHORIZE_FOR_TIKTOK_LITE = 6;

    }

    interface TargetSceneType {
        int LANDPAGE_SCENE_DEFAULT = 0;
        int LANDPAGE_SCENE_EDIT = 1;
        int LANDPAGE_SCENE_CUT = 2;
    }



    /**
     * key of webview
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
     * sdk version
     */

    interface SdkVersion {
        /**
         * base open sdk version
         */
        String VERSION = "1";
    }


}
