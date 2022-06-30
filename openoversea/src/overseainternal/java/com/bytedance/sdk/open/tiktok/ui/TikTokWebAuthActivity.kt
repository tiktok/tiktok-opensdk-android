package com.bytedance.sdk.open.tiktok.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.model.Auth
import com.bytedance.sdk.open.tiktok.authorize.ui.BaseWebAuthActivity
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.utils.ViewUtils.Companion.setStatusBarColor

open class TikTokWebAuthActivity: BaseWebAuthActivity() {
    val AUTH_HOST = "open-api.tiktok.com"
    val DOMAIN = "open-api.tiktok.com"
    val AUTH_PATH = "/platform/oauth/connect/"
    private val LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity" // 请求授权的结果回调Activity入口
    private lateinit var ttOpenApi: TikTokOpenApi

    override fun onCreate(savedInstanceState: Bundle?) {
        ttOpenApi = TikTokOpenApiFactory.create(this)
        super.onCreate(savedInstanceState)
        setStatusBarColor(this, Color.TRANSPARENT)
    }

    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean {
        return ttOpenApi.handleIntent(intent, eventHandler)
    }
    override fun sendInnerResponse(req: Auth.Request?, resp: Base.Response?) {
        if (resp != null && mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = Bundle()
            }
            resp.extras!!.putString("wap_authorize_url", mContentWebView.url)
        }
        sendInnerResponse(LOCAL_ENTRY_ACTIVITY, req!!, resp)
    }
    override val host = AUTH_HOST
    override val authPath = AUTH_PATH
    override val domain = DOMAIN

}