package com.bytedance.sdk.open.tiktok.base;

import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.common.constants.Keys;

import java.util.ArrayList;

public class VideoObject implements IMediaObject{

    private static final String TAG = "VideoObject";
    public ArrayList<String> mVideoPaths;

    @Override
    public void serialize(Bundle bundle) {
        bundle.putStringArrayList(Keys.VIDEO_PATH,this.mVideoPaths);
    }

    @Override
    public void unserialize(Bundle bundle) {
        this.mVideoPaths = bundle.getStringArrayList(Keys.VIDEO_PATH);
    }

    @Override
    public int type() {
        return IMediaObject.TYPE_VIDEO;
    }

    @Override
    public boolean checkArgs() {
        return true;
    }
}
