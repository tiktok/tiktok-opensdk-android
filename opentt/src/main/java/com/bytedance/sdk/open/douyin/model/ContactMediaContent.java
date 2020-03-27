package com.bytedance.sdk.open.douyin.model;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.base.IMediaObject;

public class ContactMediaContent {
    IMediaObject object;

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (object != null) {
            object.serialize(bundle);
        }
        return bundle;
    }

    public ContactMediaContent fromBundle(Bundle bundle) {
        ContactMediaContent content = new ContactMediaContent();
        content.object.unserialize(bundle);
        return content;
    }
}
