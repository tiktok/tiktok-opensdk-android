package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.os.Bundle
import android.util.Log

class TikTokShareResponseActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("sharesdk", "------------ here")
        println(intent)
        finish()
    }
}