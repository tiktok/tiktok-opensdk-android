package com.bytedance.sdk.open.tiktok;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl;

public class TikTokOpenApiFactory {

    private static TikTokOpenConfig sConfig;

    public static boolean init(TikTokOpenConfig config) {
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
    public static TikTokOpenApi create(Context context) {
        AuthImpl auth = new AuthImpl(context, sConfig.clientKey);
        ShareImpl share = new ShareImpl(context, sConfig.clientKey);
        return new TikTokOpenApiImpl(context, auth, share);
    }


}
