package com.bytedance.sdk.account.open.aweme.base;

import android.os.Bundle;

public interface IMediaObject {

    int TYPE_UNKNOWN = 0;
    int TYPE_TEXT = 1;
    int TYPE_IMAGE = 2;
    int TYPE_VIDEO = 3;

    void serialize(Bundle var1);

    void unserialize(Bundle var1);

    int type();

    boolean checkArgs();
}
