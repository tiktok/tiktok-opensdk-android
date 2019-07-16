package com.bytedance.sdk.open.aweme.base;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

import java.util.ArrayList;

public class TikTokVideoObject implements IMediaObject{

    private static final String TAG = "TikTokVideoObject";
    public ArrayList<String> mVideoPaths;

    @Override
    public void serialize(Bundle bundle) {
        bundle.putStringArrayList(ParamKeyConstants.AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH,this.mVideoPaths);
    }

    @Override
    public void unserialize(Bundle bundle) {
        this.mVideoPaths = bundle.getStringArrayList(ParamKeyConstants.AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH);
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
