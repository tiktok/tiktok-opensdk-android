package com.bytedance.sdk.open.douyin;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.douyin.constants.ShareContactsMediaConstants;
import com.bytedance.sdk.open.douyin.model.ContactHtmlObject;

public class ShareToContact {
    public static class Request extends BaseReq {
        public String mClientKey;

        public MediaContent mMediaContent;

        public ContactHtmlObject htmlObject;
        public Request(Bundle bundle) {
            fromBundle(bundle);
        }
        public Request(){

        }


        public String mState;

        @Override
        public int getType() {
            return CommonConstants.ModeType.SHARE_TO_CONTACTS;
        }

        public void fromBundle(Bundle bundle) {
            this.callerPackage = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_CALLER_PACKAGE);
            this.extras = bundle.getBundle(ShareContactsMediaConstants.ParamKey.EXTRA);
            this.callerLocalEntry = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_CALLER_LOCAL_ENTRY);
            this.mClientKey = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_CLIENTKEY_KEY);
            mMediaContent = MediaContent.Builder.fromBundle(bundle);
            htmlObject = ContactHtmlObject.unserialize(bundle);
            mState = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_STATE_KEY, "");
        }

        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putInt(ShareContactsMediaConstants.ParamKey.SHARE_CONTACT_TYPE, getType());
            bundle.putBundle(ShareContactsMediaConstants.ParamKey.EXTRA, extras);
            bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_FROM_ENTRY, callerLocalEntry);
            bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_STATE_KEY, mState);
            bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_CLIENTKEY_KEY, mClientKey);
            if (mMediaContent != null) {
                bundle.putAll(MediaContent.Builder.toBundle(this.mMediaContent));
            }
            if (htmlObject != null) {
                htmlObject.serialize(bundle);
            }
        }

    }

    public static class Response extends BaseResp {
        public String mState;
        public Response(Bundle bundle) {
            fromBundle(bundle);
        }
        public Response() {

        }

        public void fromBundle(Bundle bundle) {
            this.errorCode = bundle.getInt(ShareContactsMediaConstants.ParamKey.ERROR_CODE);
            this.errorMsg = bundle.getString(ShareContactsMediaConstants.ParamKey.ERROR_MSG);
            this.extras = bundle.getBundle(ShareContactsMediaConstants.ParamKey.EXTRA);
            this.mState = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_STATE_KEY);
        }

        public void toBundle(Bundle bundle) {
            bundle.putInt(ShareContactsMediaConstants.ParamKey.ERROR_CODE, errorCode);
            bundle.putString(ShareContactsMediaConstants.ParamKey.ERROR_MSG, errorMsg);
            bundle.putInt(ShareContactsMediaConstants.ParamKey.SHARE_CONTACT_TYPE, getType());
            bundle.putBundle(ShareContactsMediaConstants.ParamKey.EXTRA, extras);
        }

        @Override
        public int getType() {
            return CommonConstants.ModeType.SHARE_TO_CONTACT_RESP;
        }
    }

    }
