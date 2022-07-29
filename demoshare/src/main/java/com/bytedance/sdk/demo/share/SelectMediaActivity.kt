package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectMediaActivity: AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var selectVideoButton: Button
    private lateinit var selectImageButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_media_activity)
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        selectVideoButton = findViewById(R.id.select_video)
        selectVideoButton.setOnClickListener { selectVideo() }
        selectImageButton = findViewById(R.id.select_image)
        selectImageButton.setOnClickListener { selectImage() }
    }

    private fun goToShareActivity() {
        val intent = Intent(this, ShareActivity::class.java)
        startActivity(intent)
    }

    private fun selectVideo() {
        goToShareActivity()
    }

    private fun selectImage() {

    }


}