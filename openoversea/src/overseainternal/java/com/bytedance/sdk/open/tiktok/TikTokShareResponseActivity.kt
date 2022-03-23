package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.BaseReq
import com.bytedance.sdk.open.tiktok.common.model.BaseResp

class TikTokShareResponseActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("sharesdk", "------------ here")
        intent.extras?.let {
            val type = it.getInt(ParamKeyConstants.ShareParams.TYPE)
            println(type)
        }
        val api: TikTokOpenApi = TikTokOpenApiFactory.create(this)
        val handler: IApiEventHandler? = api.apiHandler
        api.handleIntent(intent, handler)
        finish()
        overridePendingTransition(0, 0)
    }
}