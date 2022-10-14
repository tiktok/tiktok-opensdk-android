package com.bytedance.sdk.demo.share

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.constants.Constants.CLIENT_KEY
import com.bytedance.sdk.demo.share.constants.Constants.IS_SHARING_IMAGE
import com.bytedance.sdk.demo.share.constants.Constants.SELECTED_MEDIAS
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ToggleType
import com.bytedance.sdk.demo.share.publish.ShareActivityAdapter
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
import com.bytedance.sdk.open.tiktok.share.Share

class ShareActivity : AppCompatActivity(), IApiEventHandler {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ShareActivityAdapter
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var tiktokOpenAPI: TikTokOpenApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)

        val isSharingImage = intent.getBooleanExtra(IS_SHARING_IMAGE, false)
        val clientKey = intent.getStringExtra(CLIENT_KEY)
        val mediaUrls = intent.getStringArrayListExtra(SELECTED_MEDIAS)
        if (clientKey == null || mediaUrls == null) {
            finish()
        } else {
            val tiktokOpenConfig = TikTokOpenConfig(clientKey)
            TikTokOpenApiFactory.init(tiktokOpenConfig)
            tiktokOpenAPI = TikTokOpenApiFactory.create(this, this)
            shareViewModel =
                ViewModelProvider(this, ShareViewModel.Factory(tiktokOpenAPI, isSharingImage, mediaUrls))[ShareViewModel::class.java]
        }

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
        findViewById<Button>(R.id.share_button).setOnClickListener { this.publish() }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = ShareActivityAdapter(
            onSaveToggleStatus = shareViewModel::updateToggle,
            onSaveEditTextValue = shareViewModel::updateText,
        )
        recyclerView.adapter = recyclerAdapter

        initRecyclerViewData()
    }

    override fun onDestroy() {
        recyclerAdapter.saveTextInput()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (::tiktokOpenAPI.isInitialized) {
            tiktokOpenAPI.handleIntent(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerViewData() {
        shareViewModel.shareViewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                HeaderModel(getString(R.string.demo_app_header_info), getString(R.string.demo_app_header_desc)),
                ToggleModel(getString(R.string.demo_app_green_screen_info), getString(R.string.demo_app_green_screen_desc), isOn = viewState.toggleStatus[ToggleType.GREEN_SCREEN] ?: false, toggleType = ToggleType.GREEN_SCREEN),
            )
            recyclerAdapter.updateModels(recyclerViewDataModel)
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout) {
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun publish() {
        recyclerAdapter.saveTextInput()
        shareViewModel.publish(
            ResultActivityComponent(
                this.packageName,
                this.localClassName,
            )
        )
    }

    // IApiEventHandler
    override fun onResponse(resp: Base.Response) {
        if (resp is Share.Response) {
            with(resp) {
                if (isSuccess) {
                    Toast.makeText(applicationContext, "Media sharing was successful .", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Sharing media failed: $errorMsg",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
