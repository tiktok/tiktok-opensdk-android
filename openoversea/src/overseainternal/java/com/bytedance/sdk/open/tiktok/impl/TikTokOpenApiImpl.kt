package com.bytedance.sdk.open.tiktok.impl

import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.SendAuthDataHandler
import com.bytedance.sdk.open.tiktok.authorize.Auth
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

class TikTokOpenApiImpl(val context: Context, private val authService: AuthService, private val shareService: ShareService, override val apiHandler: IApiEventHandler? = null): TikTokOpenApi {
    private val handlerMap: MutableMap<Constants.APIType, IDataHandler> = HashMap(2)
    init {
        handlerMap[Constants.APIType.AUTH] = SendAuthDataHandler()
        handlerMap[Constants.APIType.SHARE] = ShareDataHandler()
    }

    override val isAuthSupported: Boolean = (AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH) != null)
    override val isShareSupported: Boolean = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE) != null)
    override val isAppInstalled = (AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.isAppInstalled ?: false)
    override val getSdkVersion: String = BuildConfig.SDK_OVERSEA_VERSION
    override val isTikTokLiteAuthSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.isAppSupportAPI(Keys.API.AUTHORIZE_FOR_TIKTOK_LITE) ?: false)
    override val isShareFileProviderSupported = (AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.isShareFileProviderSupported ?: false)

    override fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean {
        if (eventHandler == null && apiHandler == null) {
            return false
        }
        if (intent == null) {
            eventHandler?.onErrorIntent(intent)
            return false
        }
        val bundle = intent.extras
        if (bundle == null) {
            eventHandler?.onErrorIntent(intent)
            return false
        }
        var type = bundle.getInt(Keys.Base.TYPE) //授权使用的
        if (type == 0) {
            type = bundle.getInt(Keys.Share.TYPE) //分享使用的
        }
        return when (type) {
            Constants.TIKTOK.AUTH_REQUEST, Constants.TIKTOK.AUTH_RESPONSE -> handlerMap[Constants.APIType.AUTH]!!.handle(type, bundle, eventHandler ?: apiHandler)
            Constants.TIKTOK.SHARE_REQUEST, Constants.TIKTOK.SHARE_RESPONSE -> handlerMap[Constants.APIType.SHARE]!!.handle(type, bundle, eventHandler ?: apiHandler)
            else -> handlerMap[Constants.APIType.AUTH]!!.handle(type, bundle, eventHandler) // TODO: chen.wu throw exception?
        }
    }

    override fun authorize(request: Auth.Request): Boolean {
        apiHandler?.onReq(request)
        AppCheckFactory.getApiCheck(context, Constants.APIType.AUTH)?.let {
            return authService.authorizeNative(request, it.packageName, BuildConfig.TIKTOK_AUTH_ACTIVITY, BuildConfig.DEFAULT_ENTRY_ACTIVITY)
        }
        return webAuth(request)
    }

    override fun share(request: Share.Request): Boolean {
        apiHandler?.onReq(request)
        request.let { req ->
            AppCheckFactory.getApiCheck(context, Constants.APIType.SHARE)?.let { appCheck ->
                val entryComponents = EntryComponent(BuildConfig.DEFAULT_ENTRY_ACTIVITY, appCheck.packageName,
                        BuildConfig.TIKTOK_SHARE_ACTIVITY, BuildConfig.TIKTOK_AUTH_ACTIVITY)
                return shareService.share(req, entryComponents)
            }
        }
        return false
    }

    private fun webAuth(request: Auth.Request): Boolean {
        return if (request == null) {
            false
        } else authService.authorizeWeb(WebAuthActivity::class.java, request)
    }
}