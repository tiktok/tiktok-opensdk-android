package com.bytedance.sdk.demo.share.tiktokapi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.share.Share

class TikTokEntryActivity : AppCompatActivity(), IApiEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TikTokOpenApiFactory.create(
            context = this,
            handler = this
        ).handleIntent(intent)
    }

    override fun onResponse(resp: Base.Response) {
        if (resp is Share.Response) {
            with(resp) {
                if (!isSuccess) {
                    Toast.makeText(
                        applicationContext,
                        "Sharing media failed: $errorMsg",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, "Media sharing was successful.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        finish()
    }

    override fun onErrorIntent(intent: Intent?) {
        Toast.makeText(this, "Intent error", Toast.LENGTH_SHORT).show()
    }
}
