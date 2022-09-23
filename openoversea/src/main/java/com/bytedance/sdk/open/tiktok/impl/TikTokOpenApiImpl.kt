package com.bytedance.sdk.open.tiktok.impl

import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.helper.AppCheckFactory
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareService

open class TikTokOpenApiImpl(
    private val context: Context,
    private val authService: AuthService,
    private val shareService: ShareService,
    private val apiEventHandler: IApiEventHandler,
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
        if (type == INVALID_TYPE_VALUE) {
            type = bundle.getInt(Keys.Share.TYPE)
        }
        val response = when (type) {
            Constants.TIKTOK.AUTH_RESPONSE -> Auth.Response()
            Constants.TIKTOK.SHARE_RESPONSE -> Share.Response()
            else -> null
        } ?: return false
        response.fromBundle(bundle)
        return if (response.validate()) {
            apiEventHandler.onResponse(response)
            true
        } else {
            false
        }
    }

    override fun authorize(request: Auth.Request, useWebAuth: Boolean): Boolean {
        request.scope = request.scope?.replace(" ", "")
        request.optionalScope1 = request.optionalScope1?.replace(" ", "")
        request.optionalScope0 = request.optionalScope0?.replace(" ", "")
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

    companion object {
        const val INVALID_TYPE_VALUE = 0
    }
}
