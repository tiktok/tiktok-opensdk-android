package com.bytedance.sdk.open.douyin;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.douyin.constants.ShareContactsMediaConstants;
import com.bytedance.sdk.open.douyin.model.ContactMediaContent;

public class ShareToContact {
    public static class Request extends BaseReq {

        public ContactMediaContent mMediaContent;


        public String mState;

        @Override
        public int getType() {
            return CommonConstants.ModeType.SHARE_TO_CONTACTS;
        }

        public void fromBundle(Bundle bundle) {
            this.callerPackage = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_CALLER_PACKAGE);
            this.extras = bundle.getBundle(ShareContactsMediaConstants.ParamKey.EXTRA);
            this.callerLocalEntry = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_CALLER_LOCAL_ENTRY);
            mMediaContent = new ContactMediaContent().fromBundle(bundle);
            mState = bundle.getString(ShareContactsMediaConstants.ParamKey.SHARE_STATE_KEY, "");
        }

        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putInt(ShareContactsMediaConstants.ParamKey.SHARE_CONTACT_TYPE, getType());
            bundle.putBundle(ShareContactsMediaConstants.ParamKey.EXTRA, extras);
            bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_FROM_ENTRY, callerLocalEntry);
            bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_STATE_KEY, mState);
            bundle.putAll(mMediaContent.toBundle());
        }

    }

    public static class Response extends BaseResp {
        public String mState;


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
