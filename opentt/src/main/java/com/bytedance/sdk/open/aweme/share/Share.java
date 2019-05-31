package com.bytedance.sdk.open.aweme.share;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.open.aweme.DYOpenConstants;
import com.bytedance.sdk.open.aweme.base.DYBaseRequest;
import com.bytedance.sdk.open.aweme.base.DYBaseResp;
import com.bytedance.sdk.open.aweme.base.DYMediaContent;

/**
 * Powered by WangJiaWei on 2019/1/15.
 */
public class Share {

    private static final String TAG = "Aweme.OpenSDK.Share";
    public static final int VIDEO = 0;
    public static final int IMAGE = 1;

    public static class Request extends DYBaseRequest {

        public int mTargetSceneType = 0;

        public String mHashTag;

        public int mTargetApp = DYOpenConstants.TARGET_APP.TIKTOK; //默认tiktok

        public DYMediaContent mMediaContent;

        public Request() {
            super();
        }

        public Request(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.mTargetSceneType = bundle.getInt(DYOpenConstants.Params.SHARE_TARGET_SCENE, DYOpenConstants.TargetSceneType.SHARE_DEFAULT_TYPE);
            this.mHashTag = bundle.getString(DYOpenConstants.Params.SHARE_DEFAULT_HASHTAG,"");
            this.mMediaContent = DYMediaContent.Builder.fromBundle(bundle);
        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putAll(DYMediaContent.Builder.toBundle(this.mMediaContent));
            bundle.putInt(DYOpenConstants.Params.SHARE_TARGET_SCENE, mTargetSceneType);
            bundle.putString(DYOpenConstants.Params.SHARE_DEFAULT_HASHTAG, mHashTag);
        }

        @SuppressLint("MissingSuperCall")
        public boolean checkArgs() {
            if (this.mMediaContent == null) {
                Log.e(TAG, "checkArgs fail ,mediaContent is null");
                return false;
            } else {
                return this.mMediaContent.checkArgs();
            }
        }
    }

    public static class Response extends DYBaseResp {
        public String state;

        public Response() {
        }

        public Response(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP;
        }

        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.state = bundle.getString(DYOpenConstants.Params.STATE);

        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(DYOpenConstants.Params.STATE, state);
        }
    }
}
