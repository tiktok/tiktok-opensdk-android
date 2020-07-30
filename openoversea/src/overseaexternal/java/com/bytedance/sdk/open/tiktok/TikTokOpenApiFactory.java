package com.bytedance.sdk.open.tiktok;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl;

public class TikTokOpenApiFactory {

    private static TikTokConfig sConfig;

    public static boolean init(TikTokOpenConfig config) {
        if (config != null && !TextUtils.isEmpty(config.clientKey)) {
            sConfig = TikTokConfig.builder().clientKey(config.clientKey).build();
            return true;
        }
        return false;
    }

    public static boolean init(TikTokConfig config) {
        if (config != null && !TextUtils.isEmpty(config.getClientKey())) {
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
    public static TikTokOpenApi create(Context context) {
        ShareImpl share = new ShareImpl(context, sConfig.getClientKey());
        return new TikTokOpenApiImpl(context, share);
    }
}
