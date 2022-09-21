package com.bytedance.sdk.open.tiktok.impl

import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.SendAuthDataHandler
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.helper.AppCheckFactory
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler
import com.bytedance.sdk.open.tiktok.share.ShareService

open class TikTokOpenApiImpl(
    private val context: Context,
    private val authService: AuthService,
    private val shareService: ShareService,
    private val apiEventHandler: IApiEventHandler,
    private val sendAuthDataHandler: SendAuthDataHandler = SendAuthDataHandler(),
    private val shareDataHandler: ShareDataHandler = ShareDataHandler(),
) : TikTokOpenApi {
    override val isAuthSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH) != null)
    override val isShareSupported = AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE) != null
    override val isAppInstalled = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.isAppInstalled ?: false) // TODO: chen.wu change to AUTH? to be consistent with internal?
    override val isShareFileProviderSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.isShareFileProviderSupported ?: false)
    override val sdkVersion = BuildConfig.SDK_OVERSEA_VERSION

    override fun handleIntent(intent: Intent?): Boolean {
        if (intent == null) {
            apiEventHandler.onErrorIntent(intent)
            return false
        }
        val bundle = intent.extras
        if (bundle == null) {
            apiEventHandler.onErrorIntent(intent)
            return false
        }
        var type = bundle.getInt(Keys.Base.TYPE)
        if (type == 0) {
            type = bundle.getInt(Keys.Share.TYPE)
        }
        return when (type) {
            Constants.TIKTOK.AUTH_REQUEST, Constants.TIKTOK.AUTH_RESPONSE -> sendAuthDataHandler.handle(type, bundle, apiEventHandler)
            Constants.TIKTOK.SHARE_REQUEST, Constants.TIKTOK.SHARE_RESPONSE -> shareDataHandler.handle(type, bundle, apiEventHandler)
            else -> false
        }
    }

    override fun authorize(request: Auth.Request, useWebAuth: Boolean): Boolean {
        apiEventHandler.onRequest(request)
        if (!useWebAuth) {
            AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.let {
                return authService.authorizeNative(request, it.packageName, BuildConfig.TIKTOK_AUTH_ACTIVITY, BuildConfig.DEFAULT_ENTRY_ACTIVITY)
            }
        }
        return webAuth(request)
    }

    override fun share(request: Share.Request): Boolean {
        apiEventHandler.onRequest(request)
        AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.let {
            val entryComponents = EntryComponent(
                BuildConfig.DEFAULT_ENTRY_ACTIVITY, it.packageName,
                BuildConfig.TIKTOK_SHARE_ACTIVITY, BuildConfig.TIKTOK_AUTH_ACTIVITY
            )
            return shareService.share(request, entryComponents)
        }
        return false
    }

    private fun webAuth(request: Auth.Request): Boolean {
        return authService.authorizeWeb(request)
    }
}
