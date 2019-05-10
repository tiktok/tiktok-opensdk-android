package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.account.bdopen.impl.BDOpenAPiFactory;
import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.open.aweme.DYOpenApi;
import com.bytedance.sdk.open.aweme.share.ShareImpl;

/**
 * 创建 TTOpenApi
 * Created by yangzhirong on 2018/10/17.
 */
public class DYOpenApiFactory {

    private static BDOpenConfig sConfig;

    public static boolean init(BDOpenConfig config) {
        if (config != null && !TextUtils.isEmpty(config.clientKey)) {
            sConfig = config;
            return true;
        }
        return false;
    }

    /**
     * 创建 TTOpenApi
     *
     * @param context
     * @return
     */
    public static DYOpenApi create(Context context) {
        BDOpenApi bdOpenApi = BDOpenAPiFactory.create(context, sConfig);
        ShareImpl share = new ShareImpl(context, sConfig);
        return new DYOpenApiImpl(context, bdOpenApi, share);
    }
}