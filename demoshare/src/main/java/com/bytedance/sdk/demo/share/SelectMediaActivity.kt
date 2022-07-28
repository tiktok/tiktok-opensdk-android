package com.bytedance.sdk.demo.share

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectMediaActivity: AppCompatActivity() {
    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_media_activity)
        backButton = findViewById(R.id.back_arrow)
        backButton.setOnClickListener {
            finish()
        }
    }
}