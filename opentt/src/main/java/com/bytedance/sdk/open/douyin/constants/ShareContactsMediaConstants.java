package com.bytedance.sdk.open.douyin.constants;

public interface ShareContactsMediaConstants {
    interface ParamKey {
        String SHARE_SINGLE_IMAGE_KEY = "_aweme_open_sdk_share_contacts_single_image_key";
        String SHARE_HTML_KEY = "_aweme_open_sdk_share_contact_html_key";
        String SHARE_CLIENTKEY_KEY = "_aweme_open_sdk_share_contact_client_key";
        String SHARE_STATE_KEY = "_aweme_open_sdk_share_contact_state_key";
        String EXTRA = "_aweme_share_contact_params_extra";

        /**
         * result error_code
         */
        String ERROR_CODE = "_aweme_share_contact_params_error_code";
        /**
         * result error_message
         */
        String ERROR_MSG = "_aweme_share_contact_params_error_msg";

        String SHARE_CONTACT_TYPE = "_aweme_share_contact_params_type";

        String SHARE_FROM_ENTRY = "_aweme_share_contact_from_entry";

        String SHARE_CALLER_PACKAGE = "_aweme_share_contact_caller_package";

        String SHARE_CALLER_LOCAL_ENTRY = "_aweme_share_contact_caller_local_entry";

    }
}
