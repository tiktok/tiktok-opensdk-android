package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl
import com.bytedance.sdk.open.tiktok.share.ShareService

class TikTokOpenApiFactory {
    companion object {
        private var sConfig: TikTokOpenConfig? = null
        fun init(config: TikTokOpenConfig): Boolean {
            if (!TextUtils.isEmpty(config.clientKey)) {
                sConfig = config
                return true
            }
            return false
        }

        fun create(activity: Activity, handler: IApiEventHandler? = null): TikTokOpenApi {
            if (sConfig == null) {
                throw Exception("Please init TikTokOpenApiFactory first before creating api")
            }
            checkNotNull(sConfig).let {
                val shareService = ShareService(activity, it.clientKey)
                val authService = AuthService(activity, it.clientKey)
                return TikTokOpenApiImpl(activity, authService, shareService, handler)
            }
        }
    }
}
