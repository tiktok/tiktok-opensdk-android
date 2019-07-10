package com.bytedance.sdk.open.aweme.common.constants;

/**
 * 常量
 * Created by yangzhirong on 2018/10/8.
 */
public interface BDOpenConstants {

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
     * Bundle 数据类型
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
