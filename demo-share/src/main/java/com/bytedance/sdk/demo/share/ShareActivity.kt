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
import androidx.appcompat.app.AlertDialog
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
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareApi
import com.bytedance.sdk.open.tiktok.share.ShareApiEventHandler

class ShareActivity : AppCompatActivity(), ShareApiEventHandler {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ShareActivityAdapter
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var shareApi: ShareApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)

        val isSharingImage = intent.getBooleanExtra(IS_SHARING_IMAGE, false)
        val clientKey = intent.getStringExtra(CLIENT_KEY)
        val mediaUrls = intent.getStringArrayListExtra(SELECTED_MEDIAS)
        if (clientKey == null || mediaUrls == null) {
            finish()
        } else {
            shareApi = ShareApi(
                context = this,
                clientKey = clientKey,
                apiEventHandler = this
            )
            shareViewModel =
                ViewModelProvider(this, ShareViewModel.Factory(shareApi, isSharingImage, mediaUrls))[ShareViewModel::class.java]
        }

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
        findViewById<Button>(R.id.share_button).setOnClickListener { this.publish() }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = ShareActivityAdapter(
            onSaveToggleStatus = shareViewModel::updateGreenScreenStatus,
        )
        recyclerView.adapter = recyclerAdapter

        initRecyclerViewData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (::shareApi.isInitialized) {
            shareApi.handleResultIntent(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerViewData() {
        shareViewModel.shareViewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                HeaderModel(getString(R.string.demo_app_header_info), getString(R.string.demo_app_header_desc)),
                ToggleModel(getString(R.string.demo_app_green_screen_info), getString(R.string.demo_app_green_screen_desc), isOn = viewState.greenScreenEnabled, toggleType = ToggleType.GREEN_SCREEN),
            )
            recyclerAdapter.updateModels(recyclerViewDataModel)
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout) {
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun publish() {
        shareViewModel.publish(
            packageName = BuildConfig.APPLICATION_ID, // the package name of your app, must be same as what we have on developer portal
            resultActivityFullPath = "$packageName.${this::class.simpleName}" // com.bytedance.sdk.demo.share.ShareActivity, the full path of activity which will receive the sdk results
        )
    }

    private fun showDialogAlert(title: String, desc: String) {
        AlertDialog
            .Builder(this)
            .setTitle(title)
            .setMessage(desc)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    // IApiEventHandler
    override fun onRequest(req: Share.Request) = Unit

    override fun onResponse(resp: Share.Response) {
        with(resp) {
            if (isSuccess) {
                Toast.makeText(applicationContext, R.string.share_success, Toast.LENGTH_SHORT)
                    .show()
            } else {
                showDialogAlert(
                    getString(R.string.error_dialog_title),
                    getString(
                        R.string.error_code_with_message,
                        subErrorCode,
                        errorMsg
                    )
                )
            }
        }
    }
}
