package com.bytedance.sdk.open.aweme.share;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.base.AnchorObject;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.MicroAppInfo;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;

import java.util.ArrayList;

public class Share {

    private static final String TAG = "Aweme.OpenSDK.Share";
    public static final int VIDEO = 0;
    public static final int IMAGE = 1;

    public static class Request extends BaseReq {

        public int mTargetSceneType = 0;

        public ArrayList<String> mHashTagList;

        public MediaContent mMediaContent;
        public MicroAppInfo mMicroAppInfo;

        public AnchorObject mAnchorInfo;

        public String mCallerPackage;

        public String mClientKey;

        public String mState;

        public Request() {
            super();
        }

        public Request(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return CommonConstants.ModeType.SHARE_CONTENT_TO_TT;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.mCallerPackage = bundle.getString(ParamKeyConstants.ShareParams.CALLER_PKG);
            this.callerLocalEntry = bundle.getString(ParamKeyConstants.ShareParams.CALLER_LOCAL_ENTRY);
            this.mState = bundle.getString(ParamKeyConstants.ShareParams.STATE);
            this.mClientKey = bundle.getString(ParamKeyConstants.ShareParams.CLIENT_KEY);
            this.mTargetSceneType =
                    bundle.getInt(ParamKeyConstants.ShareParams.SHARE_TARGET_SCENE, ParamKeyConstants.TargetSceneType.LANDPAGE_SCENE_DEFAULT);
            this.mHashTagList = bundle.getStringArrayList(ParamKeyConstants.ShareParams.SHARE_HASHTAG_LIST);
            this.mMediaContent = MediaContent.Builder.fromBundle(bundle);
            this.mMicroAppInfo = MicroAppInfo.unserialize(bundle);
            this.mAnchorInfo = AnchorObject.unserialize(bundle);
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_LOCAL_ENTRY, callerLocalEntry);
            bundle.putString(ParamKeyConstants.ShareParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_PKG, mCallerPackage);
            bundle.putString(ParamKeyConstants.ShareParams.STATE, mState);

            bundle.putAll(MediaContent.Builder.toBundle(this.mMediaContent));
            bundle.putInt(ParamKeyConstants.ShareParams.SHARE_TARGET_SCENE, mTargetSceneType);
            if (mHashTagList != null && mHashTagList.size() > 0) {
                bundle.putString(ParamKeyConstants.ShareParams.SHARE_DEFAULT_HASHTAG, mHashTagList.get(0)); // 兼容旧版本aweme
                bundle.putStringArrayList(ParamKeyConstants.ShareParams.SHARE_HASHTAG_LIST, mHashTagList);
            }

            // 670 add micro app
            if (mMicroAppInfo != null) {
                mMicroAppInfo.serialize(bundle);
            }
            // 920 add anchor
            if (mAnchorInfo != null) {
                mAnchorInfo.serialize(bundle);
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

        public int subErrorCode;


        public Response() {
        }

        public Response(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            this.errorCode = bundle.getInt(ParamKeyConstants.ShareParams.ERROR_CODE);
            this.errorMsg = bundle.getString(ParamKeyConstants.ShareParams.ERROR_MSG);
            this.extras = bundle.getBundle(ParamKeyConstants.BaseParams.EXTRA); // EXTRAS 复用老base
            this.state = bundle.getString(ParamKeyConstants.ShareParams.STATE);
            this.subErrorCode = bundle.getInt(ParamKeyConstants.ShareParams.SHARE_SUB_ERROR_CODE, -1000); // set default value

        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(ParamKeyConstants.ShareParams.ERROR_CODE, errorCode);
            bundle.putString(ParamKeyConstants.ShareParams.ERROR_MSG, errorMsg);
            bundle.putInt(ParamKeyConstants.ShareParams.TYPE, getType());
            bundle.putBundle(ParamKeyConstants.BaseParams.EXTRA, extras); // EXTRAS 复用老base
            bundle.putString(ParamKeyConstants.ShareParams.STATE, state);
            bundle.putInt(ParamKeyConstants.ShareParams.SHARE_SUB_ERROR_CODE, subErrorCode);

        }
    }
}
