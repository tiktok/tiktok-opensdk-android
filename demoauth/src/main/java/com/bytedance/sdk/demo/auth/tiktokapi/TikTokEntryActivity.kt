package com.bytedance.sdk.demo.auth.tiktokapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.bytedance.sdk.demo.auth.R
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base

class TikTokEntryActivity : Activity(), IApiEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TikTokOpenApiFactory.create(
            context = this,
            handler = this
        ).handleIntent(intent)
    }

    override fun onResponse(resp: Base.Response) {
        if (resp is Auth.Response) {
            with(resp) {
                if (isSuccess) {
                    Toast.makeText(applicationContext, getString(R.string.auth_success), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.auth_error, errorMsg),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        finish()
    }

    override fun onErrorIntent(intent: Intent?) {
        Toast.makeText(applicationContext, getString(R.string.intent_error), Toast.LENGTH_SHORT).show()
    }
}
