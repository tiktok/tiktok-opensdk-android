package com.bytedance.sdk.open.tiktok.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.BaseWebAuthActivity
import com.bytedance.sdk.open.tiktok.utils.ViewUtils.Companion.setStatusBarColor

open class TikTokWebAuthActivity: BaseWebAuthActivity() {
    private var ttOpenApi: TikTokOpenApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ttOpenApi = TikTokOpenApiFactory.create(this) // TODO: chen.wu handle this, without TikTokOpenApiFactory.init first
        super.onCreate(savedInstanceState)
        setStatusBarColor(this, Color.TRANSPARENT)
    }
    override fun isNetworkAvailable(): Boolean {
        return true
    }

    override fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean {
        return ttOpenApi?.handleIntent(intent, eventHandler) ?: false
    }

    override fun sendInnerResponse(req: Auth.Request?, resp: Base.Response?) {
        if (resp != null && mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = Bundle()
            }
            resp.extras!!.putString("wap_authorize_url", mContentWebView.getUrl())
        }
        sendInnerResponse(BuildConfig.DEFAULT_ENTRY_ACTIVITY, req!!, resp)
    }
}