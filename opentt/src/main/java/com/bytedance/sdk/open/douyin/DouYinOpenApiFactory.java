package com.bytedance.sdk.open.douyin;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.impl.DouYinOpenApiImpl;

/**
 * TTOpenApi
 *
 * @author changlei@bytedance.com
 */
public class DouYinOpenApiFactory {

    private static DouYinOpenConfig sConfig;

    public static boolean init(DouYinOpenConfig config) {
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
    public static DouYinOpenApi create(Context context) {
        AuthImpl auth = new AuthImpl(context, sConfig.clientKey);
        ShareImpl share = new ShareImpl(context, sConfig.clientKey);
        return new DouYinOpenApiImpl(context, auth, share);
    }


}
