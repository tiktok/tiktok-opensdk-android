package com.bytedance.sdk.open.aweme.common.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bytedance.sdk.open.aweme.base.BaseCheckHelperImpl;


/**
 * 主要功能：检查Tiktok的相关
 * author: ChangLei
 * since: 2019/4/1
 */
public class TiktokCheckHelperImpl extends BaseCheckHelperImpl {

    public TiktokCheckHelperImpl(Context context) {
        super(context);
    }

    @Override
    protected int getAuthRequestApi() {
        // 因服务端未ready，Tiktok的授权禁用，只能M授权成功，T的话走wap授权
        return 999;
    }

    @NonNull
    @Override
    public String getSignature() {
        return "aea615ab910015038f73c47e45d21466";
    }

    @NonNull
    @Override
    public String getPackageName() {
        return "com.ss.android.ugc.trill";
    }
}
