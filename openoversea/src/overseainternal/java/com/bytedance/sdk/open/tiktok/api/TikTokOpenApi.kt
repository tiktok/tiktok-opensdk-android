package com.bytedance.sdk.open.tiktok.api

import android.content.Intent
import com.bytedance.sdk.open.tiktok.authorize.model.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.share.Share

interface TikTokOpenApi {
    val apiHandler: IApiEventHandler?

    fun authorize(request: Auth.Request?): Boolean

    fun isAppSupportAuthorization(): Boolean

    fun share(request: Share.Request?): Boolean

    fun isAppSupportShare(): Boolean

    fun isShareSupportFileProvider(): Boolean

    fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean

    fun isAppInstalled(): Boolean

    fun getSdkVersion(): String?

    fun isSupportLiteAuthorize(): Boolean
}