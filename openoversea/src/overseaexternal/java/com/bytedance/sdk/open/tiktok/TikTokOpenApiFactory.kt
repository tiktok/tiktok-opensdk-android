package com.bytedance.sdk.open.tiktok

import android.content.Context
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl
import com.bytedance.sdk.open.tiktok.share.ShareService

class TikTokOpenApiFactory {
    companion object {
        private var mConfig: TikTokOpenConfig? = null

        @JvmStatic
        fun init(config: TikTokOpenConfig): Boolean {
            if (!TextUtils.isEmpty(config.clientKey)) {
                mConfig = config
                return true
            }
            return false
        }

        @JvmStatic
        fun create(context: Context, handler: IApiEventHandler): TikTokOpenApi {
            if (mConfig == null) {
                throw Exception("Please init TikTokOpenApiFactory first before creating api")
            }
            checkNotNull(mConfig).let {
                val share = ShareService(context, it.clientKey)
                val auth = AuthService(context, it.clientKey, it.callerPackageName, it.callerVersion)
                return TikTokOpenApiImpl(context, auth, share, handler)
            }
        }
    }
}
