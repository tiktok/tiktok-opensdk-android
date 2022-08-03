package com.bytedance.sdk.demo.share

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

const val SystemAlbumPermissionRequestCode = 101
const val OpenGalleryRequestCode = 102

class SelectMediaActivity: AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var selectVideoButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var shareModel: ShareModel
    private var shareImage: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_media_activity)
        shareModel = intent.getParcelableExtra("share_model")!!
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        selectVideoButton = findViewById(R.id.select_video)
        selectVideoButton.setOnClickListener { selectVideo() }
        selectImageButton = findViewById(R.id.select_image)
        selectImageButton.setOnClickListener { selectImage() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == OpenGalleryRequestCode) {
            var uris = mutableListOf<String>()
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    uris.add(clipData.getItemAt(i).uri.toString())
                }
                shareModel.media = uris
                goToShareActivity()
                return@onActivityResult
            }
            data?.dataString?.let {
                uris.add(it)
                shareModel.media = uris
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
            SystemAlbumPermissionRequestCode -> {
                val writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty() && writeExternalStorage && readExternalStorage) {
                    openSystemGallery()
                } else {
                    Toast.makeText(this, "Please grant necessary permissions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToShareActivity() {
        val intent = Intent(this, ShareActivity::class.java)
        intent.putExtra("share_model", shareModel)
        startActivity(intent)
    }

    private fun openSystemGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        intent.type = if (shareImage)  "image/*" else  "video/*"
        startActivityForResult(intent, OpenGalleryRequestCode)
    }

    private fun selectVideo() {
        shareImage = false
        shareModel.isImage = false
        requestPermission()
    }

    private fun selectImage() {
        shareImage = true
        shareModel.isImage = true
        requestPermission()
    }

    private fun requestPermission() {
        val mPermissionList = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, mPermissionList, SystemAlbumPermissionRequestCode)
    }
}