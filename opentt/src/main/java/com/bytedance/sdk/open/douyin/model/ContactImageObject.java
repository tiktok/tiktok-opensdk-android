package com.bytedance.sdk.open.douyin.model;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.base.IMediaObject;
import com.bytedance.sdk.open.douyin.constants.ShareContactsMediaConstants;

public class ContactImageObject implements IMediaObject {
    public String mPath;
    @Override
    public void serialize(Bundle var1) {
        var1.putString(ShareContactsMediaConstants.ParamKey.SHARE_SINGLE_IMAGE_KEY, mPath);
    }

    @Override
    public void unserialize(Bundle var1) {
        mPath = var1.getString(ShareContactsMediaConstants.ParamKey.SHARE_SINGLE_IMAGE_KEY, "");
    }

    @Override
    public int type() {
        return IMediaObject.TYPE_CONTACT_IMAGE;
    }

    @Override
    public boolean checkArgs() {
        return true;
    }
}
