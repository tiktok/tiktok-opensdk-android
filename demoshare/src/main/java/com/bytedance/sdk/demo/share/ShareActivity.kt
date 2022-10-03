package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.common.constants.Constants
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.TextType
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.publish.ShareActivityAdapter
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
import com.bytedance.sdk.open.tiktok.share.Share

class ShareActivity : AppCompatActivity(), IApiEventHandler {
    private lateinit var backButton: Button
    private lateinit var shareButton: Button
    private lateinit var publishButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ShareActivityAdapter
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var tiktokOpenAPI: TikTokOpenApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }
        publishButton = findViewById(R.id.share_button)
        publishButton.setOnClickListener { publish() }
        shareButton = findViewById(R.id.share_button)
        shareButton.setOnClickListener { this.publish() }
        initData()
        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = ShareActivityAdapter(
            editTextChange = :: editTextChange
        )
        recyclerView.adapter = recyclerAdapter
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (::tiktokOpenAPI.isInitialized) {
            tiktokOpenAPI.handleIntent(intent)
        }
    }

    private fun initData() {
        val shareModel: ShareModel? = intent.getParcelableExtra(Constants.SHARE_MODEL)

        if (shareModel == null) {
            finish()
        } else {
            val tiktokOpenConfig = TikTokOpenConfig(BuildConfig.CLIENT_KEY)
            TikTokOpenApiFactory.init(tiktokOpenConfig)
            tiktokOpenAPI = TikTokOpenApiFactory.create(this, this)
            shareViewModel = ViewModelProvider(this, ShareViewModel.Factory(tiktokOpenAPI, shareModel)).get(
                ShareViewModel::class.java
            )
            shareViewModel.shareViewState.observe(this) { viewState ->
                val recyclerViewDataModel = mutableListOf(
                    HeaderModel(getString(R.string.demo_app_header_info), getString(R.string.demo_app_header_desc)),
                    EditModel(TextType.HASHTAG, R.string.demo_app_hashtag_info, R.string.demo_app_hashtag_desc),
                    ToggleModel(getString(R.string.demo_app_music_select_info), getString(R.string.demo_app_music_select_desc), isOn = viewState.isMusic, onToggleChange = shareViewModel::updateMusicToggle),
                    ToggleModel(getString(R.string.demo_app_green_screen_info), getString(R.string.demo_app_green_screen_desc), isOn = viewState.isGreenScreen, onToggleChange = shareViewModel::updateGreenToggle),
                    ToggleModel(getString(R.string.demo_app_anchor_toggle_info), getString(R.string.demo_app_anchor_toggle_desc), isOn = viewState.isAnchor, onToggleChange = shareViewModel::updateAnchorToggle),
                    EditModel(TextType.ANCHOR, R.string.demo_app_anchor_info, R.string.demo_app_anchor_desc),
                    EditModel(TextType.EXTRA, R.string.demo_app_extra_info, R.string.demo_app_extra_desc)
                )
                recyclerAdapter.updateModels(recyclerViewDataModel)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout()) {
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun editTextChange(type: TextType, text: String) {
        shareViewModel.updateText(type, text)
    }

    private fun publish() {
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
