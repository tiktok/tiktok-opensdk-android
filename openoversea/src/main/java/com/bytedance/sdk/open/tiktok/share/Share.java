package com.bytedance.sdk.open.tiktok.share;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.open.tiktok.base.Anchor;
import com.bytedance.sdk.open.tiktok.base.MediaContent;
import com.bytedance.sdk.open.tiktok.common.constants.Constants;
import com.bytedance.sdk.open.tiktok.base.MicroAppInfo;
import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: chen.wu change it when auth response is migrated and base is replaced with Base in response
public class Share {

    private static final String TAG = "Aweme.OpenSDK.Share";
    public static final int VIDEO = 0;
    public static final int IMAGE = 1;
    public enum Format {
        DEFAULT(0),
        GREEN_SCREEN(1);
        private final int value;

        Format(int i) {
            this.value = i;
        }
        public int getValue() {
            return value;
        }
    }
    public static class Request extends BaseReq {

        public int mTargetSceneType = 0;

        public ArrayList<String> mHashTagList;
        public Format mShareFormat;
        public MediaContent mMediaContent;
        public MicroAppInfo mMicroAppInfo;

//        public AnchorObject mAnchorInfo;
        public Anchor mAnchorInfo;
        public String mCallerPackage;

        public String mClientKey;

        public String mState;

        public String mExtra;
        public String mAnchorSourceType;

        // Allow developers to include extra share options such as enable/disable certain actions
        // in the creation flow
        public HashMap<String, Integer> extraShareOptions;

        public Request() {
            super();
        }

        public Request(Bundle bundle) {
            fromBundle(bundle);
        }

        @Override
        public int getType() {
            return Constants.TIKTOK.SHARE_REQUEST;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            this.mCallerPackage = bundle.getString(Keys.Share.CALLER_PKG);
            this.callerLocalEntry = bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY);
            this.mState = bundle.getString(Keys.Share.STATE);
            this.mClientKey = bundle.getString(Keys.Share.CLIENT_KEY);
            this.mTargetSceneType =
                    bundle.getInt(Keys.Share.SHARE_TARGET_SCENE, Keys.Scene.LANDPAGE_SCENE_DEFAULT);
            this.mHashTagList = bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST);
            this.mMediaContent = MediaContent.Companion.INSTANCE.fromBundle(bundle); // MediaContent.Builder.fromBundle(bundle);
            this.mMicroAppInfo = MicroAppInfo.unserialize(bundle);
            this.mAnchorInfo = Anchor.Companion.INSTANCE.fromBundle(bundle);
            int shareFormatValue = bundle.getInt(Keys.Share.SHARE_FORMAT);
            switch (shareFormatValue) {
                case 1: {
                    this.mShareFormat = Format.GREEN_SCREEN;
                } break;
                default:
                    this.mShareFormat = Format.DEFAULT;
            }
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, callerLocalEntry);
            bundle.putString(Keys.Share.CLIENT_KEY, mClientKey);
            bundle.putString(Keys.Share.CALLER_PKG, mCallerPackage);
            bundle.putString(Keys.Share.STATE, mState);

            bundle.putAll(this.mMediaContent.toBundle());
            bundle.putInt(Keys.Share.SHARE_TARGET_SCENE, mTargetSceneType);
            if (mHashTagList != null && mHashTagList.size() > 0) {
                bundle.putString(Keys.Share.SHARE_DEFAULT_HASHTAG, mHashTagList.get(0)); // 兼容旧版本aweme
                bundle.putStringArrayList(Keys.Share.SHARE_HASHTAG_LIST, mHashTagList);
            }

            // 670 add micro app
            if (mMicroAppInfo != null) {
                mMicroAppInfo.serialize(bundle);
            }
            // 920 add anchor
            if (mAnchorInfo != null) {
                if (mAnchorInfo.getAnchorBusinessType() == 10){
                    bundle.putAll(mAnchorInfo.toBundle());
                }
            }
            bundle.putInt(Keys.Share.SHARE_FORMAT, mShareFormat.value);
        }


        @SuppressLint("MissingSuperCall")
        public boolean checkArgs() {
            if (this.mMediaContent == null) {
                Log.e(TAG, "checkArgs fail ,mediaContent is null");
                return false;
            } else {
//                return this.mMediaContent.checkArgs();
                return true;
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
            return Constants.TIKTOK.SHARE_RESPONSE;
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void fromBundle(Bundle bundle) {
            this.errorCode = bundle.getInt(Keys.Share.ERROR_CODE);
            this.errorMsg = bundle.getString(Keys.Base.ERROR_MSG);
            this.extras = bundle.getBundle(Keys.Base.EXTRA); // EXTRAS 复用老base
            this.state = bundle.getString(Keys.Share.STATE);
            this.subErrorCode = bundle.getInt(Keys.Share.SHARE_SUB_ERROR_CODE, -1000); // set default value

        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(Keys.Share.ERROR_CODE, errorCode);
            bundle.putString(Keys.Base.ERROR_MSG, errorMsg);
            bundle.putInt(Keys.Share.TYPE, getType());
            bundle.putBundle(Keys.Base.EXTRA, extras); // EXTRAS 复用老base
            bundle.putString(Keys.Share.STATE, state);
            bundle.putInt(Keys.Share.SHARE_SUB_ERROR_CODE, subErrorCode);

        }
    }
}
