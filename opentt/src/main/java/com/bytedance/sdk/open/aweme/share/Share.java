package com.bytedance.sdk.open.aweme.share;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.base.DYMediaContent;
import com.bytedance.sdk.open.aweme.base.DYMicroAppInfo;
import com.bytedance.sdk.open.aweme.common.constants.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;

/**
 * Powered by WangJiaWei on 2019/1/15.
 */
public class Share {

    private static final String TAG = "Aweme.OpenSDK.Share";
    public static final int VIDEO = 0;
    public static final int IMAGE = 1;

    public static class Request extends BaseReq {

        public int mTargetSceneType = 0;

        public String mHashTag;
        @Deprecated
        public int mTargetApp = TikTokConstants.TARGET_APP.TIKTOK; //默认tiktok

        public DYMediaContent mMediaContent;  // 基础媒体数据
        public DYMicroAppInfo mMicroAppInfo;  // 小程序

        public String mCallerPackage;
        public String mCallerSDKVersion;

        public String mClientKey;

        public String mState;

        /**
         * 扩展信息
         */
        public Bundle extras;


        public Request() {
            super();
        }

        public Request(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return TikTokConstants.ModeType.SHARE_CONTENT_TO_DY;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            this.mCallerPackage = bundle.getString(BDOpenConstants.ShareParams.CALLER_PKG);
            this.mCallerSDKVersion = bundle.getString(BDOpenConstants.ShareParams.CALLER_SDK_VERSION);
            this.extras = bundle.getBundle(BDOpenConstants.ShareParams.EXTRA);
            this.callerLocalEntry = bundle.getString(BDOpenConstants.ShareParams.CALLER_LOCAL_ENTRY);
            this.mState = bundle.getString(BDOpenConstants.ShareParams.STATE);
            this.mClientKey = bundle.getString(BDOpenConstants.ShareParams.CLIENT_KEY);
            this.mTargetSceneType =
                    bundle.getInt(BDOpenConstants.ShareParams.SHARE_TARGET_SCENE, BDOpenConstants.TargetSceneType.SHARE_DEFAULT_TYPE);
            this.mHashTag = bundle.getString(BDOpenConstants.ShareParams.SHARE_DEFAULT_HASHTAG, "");
            this.mMediaContent = DYMediaContent.Builder.fromBundle(bundle);
            this.mMicroAppInfo = DYMicroAppInfo.unserialize(bundle);
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(BDOpenConstants.ShareParams.TYPE, getType());
            bundle.putBundle(BDOpenConstants.ShareParams.EXTRA, extras);
            bundle.putString(BDOpenConstants.ShareParams.CALLER_LOCAL_ENTRY, callerLocalEntry);
            bundle.putString(BDOpenConstants.ShareParams.CLIENT_KEY, mClientKey);
            bundle.putString(BDOpenConstants.ShareParams.CALLER_SDK_VERSION, mCallerSDKVersion);
            bundle.putString(BDOpenConstants.ShareParams.CALLER_PKG, mCallerPackage);
            bundle.putString(BDOpenConstants.ShareParams.STATE, mState);
            bundle.putAll(DYMediaContent.Builder.toBundle(this.mMediaContent));
            bundle.putInt(BDOpenConstants.ShareParams.SHARE_TARGET_SCENE, mTargetSceneType);
            bundle.putString(BDOpenConstants.ShareParams.SHARE_DEFAULT_HASHTAG, mHashTag);

            // 670添加小程序
            if (mMicroAppInfo != null) {
                mMicroAppInfo.serialize(bundle);
            }
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

    public static class Response extends BaseResp {
        public String state;

        /**
         * 错误码
         */
        public int errorCode;

        /**
         * 错误信息
         */
        public String errorMsg;

        /**
         * 扩展信息
         */
        public Bundle extras;
        public Response() {
        }

        public Response(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return TikTokConstants.ModeType.SHARE_CONTENT_TO_DY_RESP;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            this.errorCode = bundle.getInt(BDOpenConstants.ShareParams.ERROR_CODE);
            this.errorMsg = bundle.getString(BDOpenConstants.ShareParams.ERROR_MSG);
            this.extras = bundle.getBundle(BDOpenConstants.ShareParams.EXTRA);
            this.state = bundle.getString(BDOpenConstants.ShareParams.STATE);

        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(BDOpenConstants.ShareParams.ERROR_CODE, errorCode);
            bundle.putString(BDOpenConstants.ShareParams.ERROR_MSG, errorMsg);
            bundle.putInt(BDOpenConstants.ShareParams.TYPE, getType());
            bundle.putBundle(BDOpenConstants.ShareParams.EXTRA, extras);
            bundle.putString(BDOpenConstants.ShareParams.STATE, state);
        }
    }
}
