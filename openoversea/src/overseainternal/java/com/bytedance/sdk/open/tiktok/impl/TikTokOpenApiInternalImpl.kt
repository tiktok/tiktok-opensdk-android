package com.bytedance.sdk.open.tiktok.impl

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
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApiInternal
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.helper.AppCheckFactory
import com.bytedance.sdk.open.tiktok.share.ShareService

class TikTokOpenApiInternalImpl(
    context: Context,
    authService: AuthService,
    shareService: ShareService,
    apiHandler: IApiEventHandler,
) : TikTokOpenApiImpl(
    context,
    authService,
    shareService,
    apiHandler,
),
    TikTokOpenApiInternal {
    override val isTikTokLiteAuthSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.isAppSupportAPI(Keys.API.AUTHORIZE_FOR_TIKTOK_LITE) ?: false)
}
