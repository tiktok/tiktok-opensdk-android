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
        private var apiImpl: TikTokOpenApiImpl? = null
        fun init(config: TikTokOpenConfig): Boolean {
            if (!TextUtils.isEmpty(config.clientKey)) {
                sConfig = config
                return true
            }
            return false
        }
        fun create(activity: Activity): TikTokOpenApi? {
            apiImpl?.let {
                return it
            }
            return create(activity, null)
        }
        fun create(activity: Activity, handler: IApiEventHandler?): TikTokOpenApi? {
            sConfig?.let {
                if (apiImpl != null && apiImpl!!.apiHandler == handler) {
                    return apiImpl
                }
                val shareService = ShareService(activity, it.clientKey)
                val authService = AuthService(activity, it.clientKey)
                apiImpl = TikTokOpenApiImpl(activity, authService, shareService, handler)
                return apiImpl
            }
            return null
        }
    }
}