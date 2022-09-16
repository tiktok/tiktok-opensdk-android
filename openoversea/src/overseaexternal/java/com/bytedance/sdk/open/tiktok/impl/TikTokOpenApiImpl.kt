package com.bytedance.sdk.open.tiktok.impl

import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.SendAuthDataHandler
import com.bytedance.sdk.open.tiktok.authorize.WebAuthActivity
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.helper.AppCheckFactory
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler
import com.bytedance.sdk.open.tiktok.share.ShareService

class TikTokOpenApiImpl(val context: Context, private val authService: AuthService, private val shareService: ShareService) : TikTokOpenApi {
    private val handlerMap: MutableMap<Constants.APIType, IDataHandler> = HashMap(2)
    init {
        handlerMap[Constants.APIType.AUTH] = SendAuthDataHandler()
        handlerMap[Constants.APIType.SHARE] = ShareDataHandler()
    }
    override val isAuthSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH) != null)
    override val isShareSupported = AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE) != null
    override val isAppInstalled = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.isAppInstalled ?: false) // TODO: chen.wu change to AUTH? to be consistent with internal?
    override val isShareFileProviderSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.isShareFileProviderSupported ?: false)
    override val sdkVersion = BuildConfig.SDK_OVERSEA_VERSION

    override fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean {
        if (eventHandler == null) {
            return false
        }
        if (intent == null) {
            eventHandler.onErrorIntent(intent)
            return false
        }
        val bundle = intent.extras
        if (bundle == null) {
            eventHandler.onErrorIntent(intent)
            return false
        }
        var type = bundle.getInt(Keys.Base.TYPE)
        if (type == 0) {
            type = bundle.getInt(Keys.Share.TYPE)
        }
        return when (type) {
            Constants.TIKTOK.AUTH_REQUEST, Constants.TIKTOK.AUTH_RESPONSE -> handlerMap[Constants.APIType.AUTH]!!.handle(type, bundle, eventHandler)
            Constants.TIKTOK.SHARE_REQUEST, Constants.TIKTOK.SHARE_RESPONSE -> handlerMap[Constants.APIType.SHARE]!!.handle(type, bundle, eventHandler)
            else -> handlerMap[Constants.APIType.SHARE]!!.handle(type, bundle, eventHandler)
        }
    }

    override fun share(request: Share.Request): Boolean {
        if (request == null) {
            return false
        }
        AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.let {
            val remotePackage = it.packageName
            val entryComponents = EntryComponent(BuildConfig.DEFAULT_ENTRY_ACTIVITY, remotePackage, BuildConfig.TIKTOK_SHARE_ACTIVITY, BuildConfig.TIKTOK_AUTH_ACTIVITY)
            return shareService.share(request, entryComponents)
        }
        return false
    }

    override fun authorize(request: Auth.Request, useWebAuth: Boolean): Boolean {
        if (!useWebAuth) {
            AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.let {
                return authService.authorizeNative(request, it.packageName, BuildConfig.TIKTOK_AUTH_ACTIVITY, BuildConfig.DEFAULT_ENTRY_ACTIVITY)
            }
        }
        return webAuth(request)
    }

    private fun webAuth(request: Auth.Request): Boolean {
        return authService.authorizeWeb(WebAuthActivity::class.java, request)
    }
}
