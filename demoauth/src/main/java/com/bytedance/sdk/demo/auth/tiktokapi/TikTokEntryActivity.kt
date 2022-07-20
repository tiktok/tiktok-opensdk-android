package com.bytedance.sdk.demo.auth.tiktokapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base

class TikTokEntryActivity: Activity(), IApiEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TikTokOpenApiFactory.create(this)?.handleIntent(intent, this)
    }

    override fun onReq(req: Base.Request?) {
    }

    override fun onResp(resp: Base.Response?) {
        (resp as Auth.Response)?.let {
            if (it.isSuccess) {
                Toast.makeText(this, "Auth Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Auth error: ${it.errorMsg}", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    override fun onErrorIntent(intent: Intent?) {
        Toast.makeText(this, "Intent error", Toast.LENGTH_SHORT).show()
    }
}