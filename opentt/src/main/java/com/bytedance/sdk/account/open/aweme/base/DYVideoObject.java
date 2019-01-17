package com.bytedance.sdk.account.open.aweme.base;

import android.net.Uri;
import android.os.Bundle;

import com.bytedance.sdk.account.open.aweme.DYOpenConstants;

import java.util.ArrayList;

public class DYVideoObject implements IMediaObject{

    private static final String TAG = "DYVideoObject";
    public ArrayList<Uri> mVideoPaths;

    @Override
    public void serialize(Bundle bundle) {
        bundle.putParcelableArrayList(DYOpenConstants.AWEME_EXTRA_MEDIA_MESSAGE_VIDEO_PATH,this.mVideoPaths);
    }

    @Override
    public void unserialize(Bundle bundle) {
        this.mVideoPaths = bundle.getParcelableArrayList(DYOpenConstants.AWEME_EXTRA_MEDIA_MESSAGE_IMAGE_PATH);
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
