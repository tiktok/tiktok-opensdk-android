package com.bytedance.sdk.account.open.aweme.impl;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.account.bdopen.impl.BDOpenAPiFactory;
import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.account.open.aweme.api.TiktokOpenApi;

/**
 * TTOpenApi
 *
 * @author changlei@bytedance.com
 */
public class TikTokOpenApiFactory {

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
    public static TiktokOpenApi create(Context context) {
        BDOpenApi bdOpenApi = BDOpenAPiFactory.create(context, sConfig);
        return new TikTokOpenApiImpl(context, bdOpenApi);
    }
}
