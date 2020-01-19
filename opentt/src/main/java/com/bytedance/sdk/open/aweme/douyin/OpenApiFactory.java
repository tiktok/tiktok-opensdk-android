package com.bytedance.sdk.open.aweme.douyin;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.douyin.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.douyin.impl.TikTokOpenApiImpl;
import com.bytedance.sdk.open.aweme.share.ShareImpl;

/**
 * TTOpenApi
 *
 * @author changlei@bytedance.com
 */
public class OpenApiFactory {

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
    public static TiktokOpenApi create(Context context) {
        AuthImpl auth = new AuthImpl(context, sConfig.clientKey);
        ShareImpl share = new ShareImpl(context, sConfig.clientKey);
        return new TikTokOpenApiImpl(context, auth, share);
    }


}
