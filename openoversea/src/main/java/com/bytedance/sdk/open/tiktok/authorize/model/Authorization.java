package com.bytedance.sdk.open.tiktok.authorize.model;
import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.common.constants.Constants;
import com.bytedance.sdk.open.tiktok.common.constants.Keys;
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

        public String language;

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
            return Constants.TIKTOK.AUTH_REQUEST;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.state = bundle.getString(Keys.Auth.STATE);
            this.clientKey = bundle.getString(Keys.Auth.CLIENT_KEY);
            this.redirectUri = bundle.getString(Keys.Auth.REDIRECT_URI);
            this.scope = bundle.getString(Keys.Auth.SCOPE);
            this.optionalScope0 = bundle.getString(Keys.Auth.OPTIONAL_SCOPE0);
            this.optionalScope1 = bundle.getString(Keys.Auth.OPTIONAL_SCOPE1);
            this.language = bundle.getString(Keys.Auth.LANGUAGE);
        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(Keys.Auth.STATE, state);
            bundle.putString(Keys.Auth.CLIENT_KEY, clientKey);
            bundle.putString(Keys.Auth.REDIRECT_URI, redirectUri);
            bundle.putString(Keys.Auth.SCOPE, scope);
            bundle.putString(Keys.Auth.OPTIONAL_SCOPE0, optionalScope0);
            bundle.putString(Keys.Auth.OPTIONAL_SCOPE1, optionalScope1);
            bundle.putString(Keys.Auth.LANGUAGE, language);
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
            return Constants.TIKTOK.AUTH_RESPONSE;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.authCode = bundle.getString(Keys.Auth.AUTH_CODE);
            this.state = bundle.getString(Keys.Auth.STATE);
            this.grantedPermissions = bundle.getString(Keys.Auth.GRANTED_PERMISSION);

        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(Keys.Auth.AUTH_CODE, authCode);
            bundle.putString(Keys.Auth.STATE, state);
            bundle.putString(Keys.Auth.GRANTED_PERMISSION, grantedPermissions);
        }
    }
}
