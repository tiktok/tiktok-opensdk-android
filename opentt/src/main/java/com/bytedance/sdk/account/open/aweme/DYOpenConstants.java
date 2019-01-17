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
    }

    public interface TargetSceneType {
        int SHARE_DEFAULT_TYPE = 0;
    }
}
