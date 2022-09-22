package com.bytedance.sdk.open.tiktok.impl

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
