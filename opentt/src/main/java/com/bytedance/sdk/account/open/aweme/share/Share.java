package com.bytedance.sdk.account.open.aweme.share;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;

import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Powered by WangJiaWei on 2019/1/15.
 */
public class Share {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIDEO, IMAGE})
    public @interface ShareType {}
    public static final int VIDEO = 0;
    public static final int IMAGE = 1;

    public static class Request extends BaseReq {

        public String state;
        public String clientKey;

        public @ShareType int shareType;
        public ArrayList<Uri> medias;

        /**
         * 必选权限，多个以逗号分割
         * 传入格式：scope = "user_info,friend_relation";
         */
        public String scope;
        /**
         * 可选权限(默认不勾选)，多个以逗号分割
         * 传入格式：optionalScope = "message,friend_relation";
         */
        public String optionalScope0;
        /**
         * 可选权限(默认勾选)，多个以逗号分割
         * 传入格式：optionalScope = "message,friend_relation";
         */
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
            return BDOpenConstants.ModeType.SEND_AUTH_REQUEST;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.state = bundle.getString(BDOpenConstants.Params.STATE);
            this.clientKey = bundle.getString(BDOpenConstants.Params.CLIENT_KEY);
            this.scope = bundle.getString(BDOpenConstants.Params.SCOPE);
            this.optionalScope0 = bundle.getString(BDOpenConstants.Params.OPTIONAL_SCOPE0);
            this.optionalScope1 = bundle.getString(BDOpenConstants.Params.OPTIONAL_SCOPE1);
            this.shareType = bundle.getInt("share_type");
            this.medias = bundle.getParcelableArrayList("medias");
        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(BDOpenConstants.Params.STATE, state);
            bundle.putString(BDOpenConstants.Params.CLIENT_KEY, clientKey);
            bundle.putString(BDOpenConstants.Params.SCOPE, scope);
            bundle.putString(BDOpenConstants.Params.OPTIONAL_SCOPE0, optionalScope0);
            bundle.putString(BDOpenConstants.Params.OPTIONAL_SCOPE1, optionalScope1);
            bundle.putInt("share_type", shareType);
            bundle.putParcelableArrayList("medias", medias);
        }
    }

    public static class Response extends BaseResp {

        public String state;

        public Response() {
        }

        public Response(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return BDOpenConstants.ModeType.SEND_AUTH_RESPONSE;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.state = bundle.getString(BDOpenConstants.Params.STATE);

        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(BDOpenConstants.Params.STATE, state);
        }
    }
}
