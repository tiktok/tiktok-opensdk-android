package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.publish.ShareActivityAdapter
import com.bytedance.sdk.demo.share.tiktokapi.ShareViewModel
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.share.Share

class ShareActivity : AppCompatActivity(), IApiEventHandler {
    private lateinit var shareModel: ShareModel
    private lateinit var backButton: Button
    private lateinit var publishButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ShareActivityAdapter
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var tiktokOpenAPI: TikTokOpenApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        shareModel = intent.getParcelableExtra("share_model")!!

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }
        publishButton = findViewById(R.id.share_button)
        publishButton.setOnClickListener { publish() }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = ShareActivityAdapter(
            onHashTagTextChange = shareViewModel:: updateHashtag,
            onMusicToggle = :: onMusicToggle,
            onGreenToggle = :: onGreenToggle,
            onAnchorToggle = :: onAnchorToggle,
            onAnchorTextChange = :: onAnchorTextChange,
            onExtraTextChange = :: onExtraTextChange,
            this,
        )
        recyclerView.adapter = recyclerAdapter

        initData()
        lifecycleScope.launchWhenCreated {
            shareViewModel.viewEffectFlow.collect {
                when (it) {

                    else -> {}
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (::tiktokOpenAPI.isInitialized) {
            tiktokOpenAPI.handleIntent(intent)
        }
    }

    private fun initData() {
        findViewById<TextView>(R.id.share_button).setOnClickListener {
            this.publish()
        }
        val tiktokOpenConfig = TikTokOpenConfig(BuildConfig.CLIENT_KEY)
        TikTokOpenApiFactory.init(tiktokOpenConfig)
        tiktokOpenAPI = TikTokOpenApiFactory.create(this, this)
        shareViewModel = ViewModelProvider(this, ShareViewModel.Factory(tiktokOpenAPI, shareModel)).get(ShareViewModel::class.java)

        if (shareModel == null) {
            finish()
        }
        shareViewModel.shareViewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                HeaderModel(getString(R.string.demo_app_header_info), getString(R.string.demo_app_header_desc)),
                EditModel(getString(R.string.demo_app_hashtag_info), getString(R.string.demo_app_hashtag_desc), onEditTextChange = shareViewModel::updateHashtag),
                ToggleModel(getString(R.string.demo_app_music_select_info), getString(R.string.demo_app_music_select_desc)),
                ToggleModel(getString(R.string.demo_app_green_screen_info), getString(R.string.demo_app_green_screen_desc)),
                ToggleModel(getString(R.string.demo_app_anchor_toggle_info), getString(R.string.demo_app_anchor_toggle_desc)),
                EditModel(getString(R.string.demo_app_anchor_info), getString(R.string.demo_app_anchor_desc), viewState.anchorContent, viewState.anchorExtraEnabled, onEditTextChange = shareViewModel::updateHashtag),
                EditModel(getString(R.string.demo_app_extra_info), getString(R.string.demo_app_extra_desc), viewState.extraContent, onEditTextChange = shareViewModel::updateHashtag)
            )
            recyclerAdapter.updateModels(recyclerViewDataModel)
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout()) {
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun publish() {
        shareViewModel.publish(this::class.simpleName.toString())
    }

    private fun onHashTagTextChange(hashtags: String) {
        shareViewModel.updateHashtag(hashtags)
    }

    private fun onMusicToggle(isOn: Boolean) {
        shareViewModel.updateMusicToggle(isOn)
    }

    private fun onGreenToggle(isOn: Boolean) {
        shareViewModel.updateGreenToggle(isOn)
    }

    private fun onAnchorToggle(isOn: Boolean) {
        shareViewModel.updateAnchorToggle(isOn)
    }

    private fun onAnchorTextChange(anchors: String) {
        shareViewModel.updateAnchorText(anchors)
    }

    private fun onExtraTextChange(extra: String) {
        shareViewModel.updateExtraText(extra)
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
