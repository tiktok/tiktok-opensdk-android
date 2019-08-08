package com.bytedance.sdk.open.aweme.share;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.open.aweme.base.TikTokMediaContent;
import com.bytedance.sdk.open.aweme.base.TikTokMicroAppInfo;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.TikTokConstants;
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

        public TikTokMediaContent mMediaContent;  // 基础媒体数据
        public TikTokMicroAppInfo mMicroAppInfo;  // 小程序

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
            return TikTokConstants.ModeType.SHARE_CONTENT_TO_TT;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            this.mCallerPackage = bundle.getString(ParamKeyConstants.ShareParams.CALLER_PKG);
            this.mCallerSDKVersion = bundle.getString(ParamKeyConstants.ShareParams.CALLER_SDK_VERSION);
            this.extras = bundle.getBundle(ParamKeyConstants.ShareParams.EXTRA);
            this.callerLocalEntry = bundle.getString(ParamKeyConstants.ShareParams.CALLER_LOCAL_ENTRY);
            this.mState = bundle.getString(ParamKeyConstants.ShareParams.STATE);
            this.mClientKey = bundle.getString(ParamKeyConstants.ShareParams.CLIENT_KEY);
            this.mTargetSceneType =
                    bundle.getInt(ParamKeyConstants.ShareParams.SHARE_TARGET_SCENE, ParamKeyConstants.TargetSceneType.SHARE_DEFAULT_TYPE);
            this.mHashTag = bundle.getString(ParamKeyConstants.ShareParams.SHARE_DEFAULT_HASHTAG, "");
            this.mMediaContent = TikTokMediaContent.Builder.fromBundle(bundle);
            this.mMicroAppInfo = TikTokMicroAppInfo.unserialize(bundle);
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(ParamKeyConstants.ShareParams.TYPE, getType());
            bundle.putBundle(ParamKeyConstants.ShareParams.EXTRA, extras);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_LOCAL_ENTRY, callerLocalEntry);
            bundle.putString(ParamKeyConstants.ShareParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_SDK_VERSION, mCallerSDKVersion);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_PKG, mCallerPackage);
            bundle.putString(ParamKeyConstants.ShareParams.STATE, mState);
            bundle.putAll(TikTokMediaContent.Builder.toBundle(this.mMediaContent,false));
            bundle.putInt(ParamKeyConstants.ShareParams.SHARE_TARGET_SCENE, mTargetSceneType);
            bundle.putString(ParamKeyConstants.ShareParams.SHARE_DEFAULT_HASHTAG, mHashTag);

            // 670添加小程序
            if (mMicroAppInfo != null) {
                mMicroAppInfo.serialize(bundle);
            }
        }

        public void toBundleForOldVersion(Bundle bundle) {
            bundle.putInt(ParamKeyConstants.ShareParams.TYPE, getType());
            bundle.putBundle(ParamKeyConstants.ShareParams.EXTRA, extras);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_LOCAL_ENTRY, callerLocalEntry);
            bundle.putString(ParamKeyConstants.ShareParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_SDK_VERSION, mCallerSDKVersion);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_PKG, mCallerPackage);
            bundle.putString(ParamKeyConstants.ShareParams.STATE, mState);
            bundle.putAll(TikTokMediaContent.Builder.toBundle(this.mMediaContent,true));
            bundle.putInt(ParamKeyConstants.ShareParams.SHARE_TARGET_SCENE, mTargetSceneType);
            bundle.putString(ParamKeyConstants.ShareParams.SHARE_DEFAULT_HASHTAG, mHashTag);

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

        public Response() {
        }

        public Response(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return TikTokConstants.ModeType.SHARE_CONTENT_TO_TT_RESP;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            this.errorCode = bundle.getInt(ParamKeyConstants.ShareParams.ERROR_CODE);
            this.errorMsg = bundle.getString(ParamKeyConstants.ShareParams.ERROR_MSG);
            this.extras = bundle.getBundle(ParamKeyConstants.ShareParams.EXTRA);
            this.state = bundle.getString(ParamKeyConstants.ShareParams.STATE);

        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(ParamKeyConstants.ShareParams.ERROR_CODE, errorCode);
            bundle.putString(ParamKeyConstants.ShareParams.ERROR_MSG, errorMsg);
            bundle.putInt(ParamKeyConstants.ShareParams.TYPE, getType());
            bundle.putBundle(ParamKeyConstants.ShareParams.EXTRA, extras);
            bundle.putString(ParamKeyConstants.ShareParams.STATE, state);
        }
    }
}
