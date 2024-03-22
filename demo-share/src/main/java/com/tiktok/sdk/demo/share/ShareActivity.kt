package com.tiktok.sdk.demo.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.tiktok.open.sdk.core.appcheck.TikTokAppCheckUtil
import com.tiktok.open.sdk.share.ShareApi
import com.tiktok.sdk.demo.share.constants.Constants.CLIENT_KEY
import com.tiktok.sdk.demo.share.constants.Constants.IS_SHARING_IMAGE
import com.tiktok.sdk.demo.share.constants.Constants.SELECTED_MEDIAS
import com.tiktok.sdk.demo.share.constants.Constants.SUCCESS
import com.tiktok.sdk.demo.share.model.HeaderModel
import com.tiktok.sdk.demo.share.model.ToggleModel
import com.tiktok.sdk.demo.share.model.ToggleType
import com.tiktok.sdk.demo.share.publish.ShareActivityAdapter

class ShareActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ShareActivityAdapter
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var shareApi: ShareApi
    private lateinit var clientKey: String
    private var shouldCheckTikTokInstalled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)

        val isSharingImage = intent.getBooleanExtra(IS_SHARING_IMAGE, false)
        clientKey = intent.getStringExtra(CLIENT_KEY) ?: BuildConfig.CLIENT_KEY
        val mediaUrls = intent.getStringArrayListExtra(SELECTED_MEDIAS) ?: arrayListOf()
        shareApi = ShareApi(activity = this)
        shareViewModel =
            ViewModelProvider(
                this,
                ShareViewModel.Factory(shareApi, isSharingImage, mediaUrls)
            )[ShareViewModel::class.java]

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
        findViewById<Button>(R.id.share_button).setOnClickListener { this.publish() }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = ShareActivityAdapter(
            onSaveToggleStatus = shareViewModel::updateGreenScreenStatus,
        )
        recyclerView.adapter = recyclerAdapter

        initRecyclerViewData()

        lifecycleScope.launchWhenCreated {
            shareViewModel.viewEffectFlow.collect {
                when (it) {
                    is ShareViewModel.ViewEffect.GREEN_SCREEN_UNSUPPORTED -> showDialogAlert(
                        getString(R.string.unable_to_change_config),
                        getString(R.string.demo_app_green_screen_unsupported)
                    )
                }
            }
        }
        handleShareResponse(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareResponse(intent)
    }

    override fun onResume() {
        super.onResume()
        handleInstallationResult(intent)
    }

    private fun handleShareResponse(intent: Intent) {
        shareApi.getShareResponseFromIntent(intent)?.let {
            if (it.isSuccess) {
                Toast.makeText(applicationContext, R.string.share_success, Toast.LENGTH_SHORT)
                    .show()
            } else {
                showDialogAlert(
                    getString(R.string.error_dialog_title),
                    getString(
                        R.string.error_code_with_message,
                        it.subErrorCode,
                        it.errorMsg
                    )
                )
            }
        }
    }

    private fun handleInstallationResult(intent: Intent) {
        if (shouldCheckTikTokInstalled && !TikTokAppCheckUtil.isTikTokAppInstalled(this)) {
            showDialogAlert(getString(R.string.error_dialog_title), getString(R.string.sharing_fail_error))
        }
        shouldCheckTikTokInstalled = false
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
        val res = shareViewModel.publish(
            clientKey,
            packageName = packageName, // the package name of your activity
            resultActivityFullPath = "$packageName.${this::class.simpleName}" // com.tiktok.sdk.demo.share.ShareActivity, the full path of activity which will receive the sdk results
        )
        if (res.result != SUCCESS) {
            showDialogAlert(getString(R.string.error_dialog_title), getString(R.string.sharing_fail_error)) // add error code and message
        }
        shouldCheckTikTokInstalled = true
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
}
