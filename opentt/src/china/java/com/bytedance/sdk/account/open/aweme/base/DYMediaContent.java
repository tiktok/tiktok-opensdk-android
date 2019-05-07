package com.bytedance.sdk.account.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

public class DYMediaContent {

    private static final String TAG = "AWEME.SDK.DYMediaContent";
    public IMediaObject mMediaObject;

    public DYMediaContent() {

    }

    public DYMediaContent(IMediaObject obj) {
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

        public static Bundle toBundle(DYMediaContent mediaContent) {
            Bundle bundle;
            bundle = new Bundle();
            if (mediaContent.mMediaObject != null) {
                bundle.putString("_dyobject_identifier_", mediaContent.mMediaObject.getClass().getName());
                mediaContent.mMediaObject.serialize(bundle);
            }
            return bundle;
        }

        @SuppressLint("LongLogTag")
        public static DYMediaContent fromBundle(Bundle bundle) {
            DYMediaContent mediaContent;
            mediaContent = new DYMediaContent();
            String mediaClassName;
            if (((mediaClassName = bundle.getString("_dyobject_identifier_"))) != null && mediaClassName.length() > 0) {
                try {
                    Class media = Class.forName(mediaClassName);
                    mediaContent.mMediaObject = (IMediaObject) media.newInstance();
                    mediaContent.mMediaObject.unserialize(bundle);
                    return mediaContent;
                } catch (Exception e) {
                    Log.e(TAG, "get media object from bundle failed: unknown ident " + mediaClassName + ", ex = " + e.getMessage());
                    return mediaContent;
                }
            } else {
                return mediaContent;
            }
        }
    }
}