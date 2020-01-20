package com.bytedance.sdk.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

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

        public static Bundle toBundle(MediaContent mediaContent, boolean supportOldVersion) {
            Bundle bundle;
            bundle = new Bundle();
            if (mediaContent.mMediaObject != null) {
                String className = mediaContent.mMediaObject.getClass().getName();
                // adapt to old douyin version
                if (className.contains("sdk")) {
                    className = className.replace("sdk", "sdk.account");
                }
                if (supportOldVersion) {
                    className = className.replace("TikTok","DY");
                }
                bundle.putString(KEY_IDENTIFIER, className);
                mediaContent.mMediaObject.serialize(bundle);
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