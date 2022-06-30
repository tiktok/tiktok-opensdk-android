package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler

class TikTokShareResponseActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let {
            val type = it.getInt(Keys.Share.TYPE)
            println(type)
        }
        val api: TikTokOpenApi? = TikTokOpenApiFactory.create(this)
        val handler: IApiEventHandler? = api?.apiHandler
        api?.handleIntent(intent, handler)
        finish()
        overridePendingTransition(0, 0)
    }
}