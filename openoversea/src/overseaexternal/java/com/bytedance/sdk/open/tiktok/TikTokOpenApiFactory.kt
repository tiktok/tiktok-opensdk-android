package com.bytedance.sdk.open.tiktok

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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
                val auth = AuthService(context, it.clientKey)
                return TikTokOpenApiImpl(context, auth, share, handler)
            }
        }
    }
}
