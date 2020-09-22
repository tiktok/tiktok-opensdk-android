package com.bytedance.sdk.open.tiktok.base;

import android.os.Bundle;

public interface IMediaObject {

    int TYPE_UNKNOWN = 0;
    int TYPE_TEXT = 1;
    int TYPE_IMAGE = 2;
    int TYPE_VIDEO = 3;
    int TYPE_CONTACT_IMAGE = 4;
    int TYPE_CONTACT_HTML = 5;

    void serialize(Bundle var1);

    void unserialize(Bundle var1);

    int type();

    boolean checkArgs();
}
