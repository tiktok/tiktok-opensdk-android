package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.text.TextUtils
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl
import com.bytedance.sdk.open.tiktok.share.ShareService

class TikTokOpenApiFactory {
    companion object {
        private var mConfig: TikTokOpenConfig? = null
        fun init(config: TikTokOpenConfig): Boolean {
            if (!TextUtils.isEmpty(config.clientKey)) {
                mConfig = config
                return true
            }
            return false
        }

        fun create(activity: Activity): TikTokOpenApi? {
            mConfig?.let {
                val share = ShareService(activity, it.clientKey)
                val auth = AuthService(activity, it.clientKey)
                return TikTokOpenApiImpl(activity, auth, share)
            }
            return null
        }
    }
}
