package com.bytedance.sdk.account;

import android.app.Application;

import com.bytedance.sdk.open.tiktok.TikTokConfig;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;


/**
 * 主要功能：自定义{@link Application}
 * since: 2018/12/25
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TikTokConfig config = TikTokConfig.builder()
                .clientKey(BuildConfig.CLIENT_KEY)
                .build();

        TikTokOpenApiFactory.init(config);
    }
}
