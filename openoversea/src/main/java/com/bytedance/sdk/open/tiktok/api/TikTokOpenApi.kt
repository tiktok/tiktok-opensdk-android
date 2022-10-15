package com.bytedance.sdk.open.tiktok.api

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
import android.content.Intent
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.helper.AppCheckFactory
import com.bytedance.sdk.open.tiktok.share.Share

interface TikTokOpenApi {
    companion object {
        fun isShareSupported(context: Context) = AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE) != null
        fun isShareFileProviderSupported(context: Context) = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.isShareFileProviderSupported ?: false)
    }

    fun authorize(request: Auth.Request, useWebAuth: Boolean = false): Boolean

    fun share(request: Share.Request): Boolean

    fun handleIntent(intent: Intent?): Boolean
}
