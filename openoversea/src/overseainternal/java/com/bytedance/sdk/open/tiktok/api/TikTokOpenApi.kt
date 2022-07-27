package com.bytedance.sdk.open.tiktok.api

import android.content.Intent
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.share.Share

interface TikTokOpenApi {
    val apiHandler: IApiEventHandler?
    val isAuthSupported: Boolean
    val isShareSupported: Boolean
    val isAppInstalled: Boolean
    val getSdkVersion: String
    val isTikTokLiteAuthSupported: Boolean
    val isShareFileProviderSupported: Boolean

    fun authorize(request: Auth.Request, useWebAuth: Boolean = false): Boolean

    fun share(request: Share.Request): Boolean

    fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean
}