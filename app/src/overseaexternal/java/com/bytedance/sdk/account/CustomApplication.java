package com.bytedance.sdk.account;

import android.app.Application;

import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig;


/**
 * 主要功能：自定义{@link Application}
 * since: 2018/12/25
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String clientKey = BuildConfig.CLIENT_KEY;
        TikTokOpenApiFactory.Companion.init(new TikTokOpenConfig(clientKey));
    }
}
