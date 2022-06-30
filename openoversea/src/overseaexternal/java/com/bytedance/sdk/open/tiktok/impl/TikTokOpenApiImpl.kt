package com.bytedance.sdk.open.tiktok.impl

import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.SendAuthDataHandler
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.base.IAppCheck
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler
import com.bytedance.sdk.open.tiktok.helper.MusicallyCheck
import com.bytedance.sdk.open.tiktok.helper.TikTokCheck
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler
import com.bytedance.sdk.open.tiktok.share.ShareService
import com.bytedance.sdk.open.tiktok.ui.TikTokWebAuthActivity

// TODO: chen.wu cleanup
class TikTokOpenApiImpl(val context: Context, val authService: AuthService, val shareService: ShareService?): TikTokOpenApi {
    private var mAuthcheckApis = arrayOf(MusicallyCheck(context), TikTokCheck(context))
    private var mSharecheckApis = arrayOf(MusicallyCheck(context), TikTokCheck(context))
    private val handlerMap: MutableMap<Int, IDataHandler> = HashMap(2)
    private val API_TYPE_LOGIN = 0
    private val API_TYPE_SHARE = 1
    private val LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"
    private val REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"


    private val TYPE_AUTH_HANDLER = 1
    private val TYPE_SHARE_HANDLER = 2
    init {
        handlerMap[TYPE_AUTH_HANDLER] = SendAuthDataHandler()
        handlerMap[TYPE_SHARE_HANDLER] = ShareDataHandler()
    }

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
            Constants.TIKTOK.AUTH_REQUEST, Constants.TIKTOK.AUTH_RESPONSE -> handlerMap[TYPE_AUTH_HANDLER]!!.handle(type, bundle, eventHandler)
            Constants.TIKTOK.SHARE_REQUEST, Constants.TIKTOK.SHARE_RESPONSE -> handlerMap[TYPE_SHARE_HANDLER]!!.handle(type, bundle, eventHandler)
            else -> handlerMap[TYPE_SHARE_HANDLER]!!.handle(type, bundle, eventHandler)
        }
    }

    override fun isAppInstalled(): Boolean {
        for (checkapi in mSharecheckApis) {
            if (checkapi.isAppInstalled) {
                return true
            }
        }
        return false
    }

    override fun isAppSupportShare(): Boolean {
        return getSupportApiAppInfo(API_TYPE_SHARE) != null
    }

    override fun isShareSupportFileProvider(): Boolean {
        for (checkapi in mSharecheckApis) {
            if (checkapi.isShareFileProviderSupported) {
                return true
            }
        }
        return false
    }

    override fun share(request: Share.Request): Boolean {
        if (request == null) {
            return false
        }
        if (isAppSupportShare()) {
            val remotePackage = getSupportApiAppInfo(API_TYPE_SHARE)!!.packageName
            return shareService!!.share(LOCAL_ENTRY_ACTIVITY, remotePackage, REMOTE_SHARE_ACTIVITY, request,
                    getSupportApiAppInfo(API_TYPE_SHARE)!!.remoteAuthEntryActivity, BuildConfig.SDK_OVERSEA_NAME, BuildConfig.SDK_OVERSEA_VERSION)
        }
        return false
    }

    override fun authorize(request: Auth.Request): Boolean {
        val appHasInstalled = getSupportApiAppInfo(API_TYPE_LOGIN)
        return if (appHasInstalled != null) {
            authService!!.authorizeNative(request, appHasInstalled.packageName, appHasInstalled.remoteAuthEntryActivity, LOCAL_ENTRY_ACTIVITY, BuildConfig.SDK_OVERSEA_NAME, BuildConfig.SDK_OVERSEA_VERSION)
        } else {
            sendWebAuthRequest(request)
        }
    }

    override fun getSdkVersion(): String? {
        return BuildConfig.SDK_OVERSEA_VERSION
    }

    override fun getApiHandler(): IApiEventHandler? {
        return null
    }

    override fun isAppSupportAuthorization(): Boolean {
        return getSupportApiAppInfo(API_TYPE_LOGIN) != null
    }

    private fun sendWebAuthRequest(request: Auth.Request): Boolean {
        return authService!!.authorizeWeb(TikTokWebAuthActivity::class.java, request)
    }

    private fun getSupportApiAppInfo(type: Int): IAppCheck? {
        when (type) {
            API_TYPE_LOGIN -> for (checkapi in mAuthcheckApis) {
                if (checkapi.isAuthSupported) {
                    return checkapi
                }
            }
            API_TYPE_SHARE -> for (checkapi in mSharecheckApis) {
                if (checkapi.isShareSupported) {
                    return checkapi
                }
            }
        }
        return null
    }
}