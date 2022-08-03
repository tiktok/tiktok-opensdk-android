package com.bytedance.sdk.demo.share.tiktokapi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.share.Share

class TikTokEntryActivity: AppCompatActivity(), IApiEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TikTokOpenApiFactory.create(this)?.handleIntent(intent, this)
    }

    override fun onReq(req: Base.Request?) {
    }

    override fun onResp(resp: Base.Response?) {
        (resp as Share.Response)?.let { shareResponse ->
            if (!shareResponse.isSuccess) {
                Toast.makeText(this, "error: ${shareResponse.errorMsg}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "successfully shared media.", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    override fun onErrorIntent(intent: Intent?) {
        Toast.makeText(this, "Intent error", Toast.LENGTH_SHORT).show()
    }
}