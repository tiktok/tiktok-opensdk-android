package com.bytedance.sdk.demo.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bytedance.sdk.demo.share.constants.Constants.CLIENT_KEY
import com.bytedance.sdk.demo.share.constants.Constants.IS_SHARING_IMAGE
import com.bytedance.sdk.demo.share.constants.Constants.SELECTED_MEDIAS

class SelectMediaActivity : AppCompatActivity() {

    private var clientKey: String = ""
    private var isSharingImage: Boolean = true
    private var mediaUrls: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_media_activity)
        intent.getStringExtra(CLIENT_KEY)?.let {
            clientKey = it
        }
        findViewById<Button>(R.id.back_button).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.select_video).setOnClickListener { selectVideo() }
        findViewById<Button>(R.id.select_image).setOnClickListener { selectImage() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra(CLIENT_KEY)?.let {
            clientKey = it
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_GALLERY_REQUEST_CODE) {
            val uris = mutableListOf<String>()
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    uris.add(uri.toString())
                }
                mediaUrls = ArrayList(uris)
                goToShareActivity()
                return@onActivityResult
            }
            data?.dataString?.let {
                uris.add(it)
                mediaUrls = ArrayList(uris)
                goToShareActivity()
                return@onActivityResult
            }
        } else {
            Toast.makeText(this, "Media selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SYSTEM_ALBUM_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { true }) {
                    openSystemGallery()
                } else {
                    Toast.makeText(this, "Please grant necessary permissions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToShareActivity() {
        val intent = Intent(this, ShareActivity::class.java)
        intent.putExtra(CLIENT_KEY, clientKey)
        intent.putExtra(IS_SHARING_IMAGE, isSharingImage)
        intent.putStringArrayListExtra(SELECTED_MEDIAS, mediaUrls)
        startActivity(intent)
    }

    private fun openSystemGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        intent.type = if (isSharingImage) "image/*" else "video/*"
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE)
    }

    private fun selectVideo() {
        isSharingImage = false
        requestPermission()
    }

    private fun selectImage() {
        isSharingImage = true
        requestPermission()
    }

    private fun requestPermission() {
        val mPermissionList =
            if (Build.VERSION.SDK_INT < 33) {
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } else {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                )
            }

        ActivityCompat.requestPermissions(this, mPermissionList, SYSTEM_ALBUM_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val SYSTEM_ALBUM_PERMISSION_REQUEST_CODE = 101
        private const val OPEN_GALLERY_REQUEST_CODE = 102
    }
}
