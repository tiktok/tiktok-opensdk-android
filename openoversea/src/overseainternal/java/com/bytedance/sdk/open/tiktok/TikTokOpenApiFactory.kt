package com.bytedance.sdk.open.tiktok

import android.content.Context
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApiInternal
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiInternalImpl
import com.bytedance.sdk.open.tiktok.share.ShareService

class TikTokOpenApiFactory {
    companion object {
        private var sConfig: TikTokOpenConfig? = null

        @JvmStatic
        fun init(config: TikTokOpenConfig): Boolean {
            if (!TextUtils.isEmpty(config.clientKey)) {
                sConfig = config
                return true
            }
            return false
        }

        @JvmStatic
        fun create(context: Context, handler: IApiEventHandler): TikTokOpenApiInternal {
            if (sConfig == null) {
                throw Exception("Please init TikTokOpenApiFactory first before creating api")
            }
            checkNotNull(sConfig).let {
                val shareService = ShareService(context, it.clientKey)
                val authService = AuthService(context, it.clientKey)
                return TikTokOpenApiInternalImpl(context, authService, shareService, handler)
            }
        }
    }
}
