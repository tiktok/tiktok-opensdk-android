package com.bytedance.sdk.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.utils.AppUtil;

import java.util.ArrayList;

public class MediaContent {

    private static final String TAG = "AWEME.SDK.MediaContent";
    public IMediaObject mMediaObject;

    public MediaContent() {

    }

    public MediaContent(IMediaObject obj) {
        this.mMediaObject = obj;
    }

    public final int getType() {
        return this.mMediaObject == null ? 0 : this.mMediaObject.type();
    }

    public final boolean checkArgs() {
        return this.mMediaObject.checkArgs();
    }

    public static class Builder {
        public static final String KEY_IDENTIFIER = "_dyobject_identifier_";

        public Builder() {
        }

        public static Bundle toBundle(MediaContent mediaContent) {
            Bundle bundle;
            bundle = new Bundle();
            if (mediaContent.mMediaObject != null) {
               String className = "";
                // 不打算支持7月14号之前的版本
                mediaContent.mMediaObject.serialize(bundle);

                // 支持旧版本抖音
                ArrayList<String> imagePath = bundle.getStringArrayList(ParamKeyConstants.AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH);
                ArrayList<String> videoPath = bundle.getStringArrayList(ParamKeyConstants.AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH);
                if (videoPath != null && videoPath.size() != 0) {
                    className = "com.ss.android.ugc.aweme.opensdk.share.base.TikTokVideoObject";
                }
                if (imagePath!= null && imagePath.size() != 0) {
                    className = "com.ss.android.ugc.aweme.opensdk.share.base.TikTokImageObject";
                }
                bundle.putString(KEY_IDENTIFIER, className);

            }
            return bundle;
        }

        @SuppressLint("LongLogTag")
        public static MediaContent fromBundle(Bundle bundle) {
            MediaContent mediaContent;
            mediaContent = new MediaContent();
            String mediaClassName;
            if (((mediaClassName = bundle.getString(KEY_IDENTIFIER))) != null && mediaClassName.length() > 0) {
                try {
                    // adapt to old douyin version
                    if (mediaClassName.contains("sdk")) {
                        mediaClassName = mediaClassName.replace("sdk", "sdk.account");
                    }

                    Class media = Class.forName(mediaClassName);
                    mediaContent.mMediaObject = (IMediaObject) media.newInstance();
                    mediaContent.mMediaObject.unserialize(bundle);
                    return mediaContent;
                } catch (Exception e) {
                    Log.e(TAG,
                            "get media object from bundle failed: unknown ident " + mediaClassName + ", ex = " + e.getMessage());
                    return mediaContent;
                }
            } else {
                return mediaContent;
            }
        }
    }
}