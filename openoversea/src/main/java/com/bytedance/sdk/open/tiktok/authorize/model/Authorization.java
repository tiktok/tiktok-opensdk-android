package com.bytedance.sdk.open.tiktok.authorize.model;
import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.CommonConstants;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;


public class Authorization {

    public static class Request extends BaseReq {

        public String state;
        public String redirectUri;
        public String clientKey;

        public String scope;

        public String optionalScope0;

        public String optionalScope1;

        public String getClientKey() {
            return clientKey;
        }

        public Request() {
            super();
        }

        public Request(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return CommonConstants.ModeType.SEND_AUTH_REQUEST;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.state = bundle.getString(ParamKeyConstants.AuthParams.STATE);
            this.clientKey = bundle.getString(ParamKeyConstants.AuthParams.CLIENT_KEY);
            this.redirectUri = bundle.getString(ParamKeyConstants.AuthParams.REDIRECT_URI);
            this.scope = bundle.getString(ParamKeyConstants.AuthParams.SCOPE);
            this.optionalScope0 = bundle.getString(ParamKeyConstants.AuthParams.OPTIONAL_SCOPE0);
            this.optionalScope1 = bundle.getString(ParamKeyConstants.AuthParams.OPTIONAL_SCOPE1);
        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(ParamKeyConstants.AuthParams.STATE, state);
            bundle.putString(ParamKeyConstants.AuthParams.CLIENT_KEY, clientKey);
            bundle.putString(ParamKeyConstants.AuthParams.REDIRECT_URI, redirectUri);
            bundle.putString(ParamKeyConstants.AuthParams.SCOPE, scope);
            bundle.putString(ParamKeyConstants.AuthParams.OPTIONAL_SCOPE0, optionalScope0);
            bundle.putString(ParamKeyConstants.AuthParams.OPTIONAL_SCOPE1, optionalScope1);
        }
    }

    public static class Response extends BaseResp {

        public String authCode;
        public String state;
        public String grantedPermissions;

        public Response() {
        }

        public Response(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return CommonConstants.ModeType.SEND_AUTH_RESPONSE;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.authCode = bundle.getString(ParamKeyConstants.AuthParams.AUTH_CODE);
            this.state = bundle.getString(ParamKeyConstants.AuthParams.STATE);
            this.grantedPermissions = bundle.getString(ParamKeyConstants.AuthParams.GRANTED_PERMISSION);

        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(ParamKeyConstants.AuthParams.AUTH_CODE, authCode);
            bundle.putString(ParamKeyConstants.AuthParams.STATE, state);
            bundle.putString(ParamKeyConstants.AuthParams.GRANTED_PERMISSION, grantedPermissions);
        }
    }
}
