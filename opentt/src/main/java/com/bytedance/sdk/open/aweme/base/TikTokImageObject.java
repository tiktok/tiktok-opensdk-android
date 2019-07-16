package com.bytedance.sdk.open.aweme.base;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

import java.util.ArrayList;

public class TikTokImageObject implements IMediaObject {

    private static final String TAG = "TikTokImageObject";
    public ArrayList<String> mImagePaths;

    public TikTokImageObject() {

    }

    public TikTokImageObject(ArrayList<String> images) {
        this.mImagePaths = new ArrayList<>();
        this.mImagePaths.addAll(images);
    }

    @Override
    public void serialize(Bundle bundle) {
        bundle.putStringArrayList(ParamKeyConstants.AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH, this.mImagePaths);
    }

    @Override
    public void unserialize(Bundle bundle) {
        this.mImagePaths = bundle.getStringArrayList(ParamKeyConstants.AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH);
    }

    @Override
    public int type() {
        return IMediaObject.TYPE_IMAGE;
    }

    @Override
    public boolean checkArgs() {
        return true;
    }
}
