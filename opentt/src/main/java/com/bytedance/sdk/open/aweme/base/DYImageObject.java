package com.bytedance.sdk.open.aweme.base;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.DYOpenConstants;

import java.util.ArrayList;

public class DYImageObject implements IMediaObject {

    private static final String TAG = "DYImageObject";
    public ArrayList<String> mImagePaths;

    public DYImageObject() {

    }

    public DYImageObject(ArrayList<String> images) {
        this.mImagePaths = new ArrayList<>();
        this.mImagePaths.addAll(images);
    }

    @Override
    public void serialize(Bundle bundle) {
        bundle.putStringArrayList(DYOpenConstants.AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH, this.mImagePaths);
    }

    @Override
    public void unserialize(Bundle bundle) {
        this.mImagePaths = bundle.getStringArrayList(DYOpenConstants.AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH);
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
