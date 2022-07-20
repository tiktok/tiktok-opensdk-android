package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler

class TikTokApiResponseActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let {
            val type = it.getInt(Keys.Share.TYPE)
            println(type)
        }
        val api: TikTokOpenApi? = TikTokOpenApiFactory.create(this)
        api?.apiHandler?.let {
            api.handleIntent(intent, it)
        }
        finish()
        overridePendingTransition(0, 0)
    }
}