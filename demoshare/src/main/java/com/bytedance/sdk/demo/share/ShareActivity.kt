package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            hashtagText = :: hashtagText,
            onMusicToggle = :: onMusicToggle,
            onGreenToggle = :: onGreenToggle,
            onAnchorToggle = :: onAnchorToggle,
            anchorText = :: anchorText,
            extraText = :: extraText,
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
        shareViewModel = ViewModelProvider(this, ShareViewModel.Factory(tiktokOpenAPI)).get(ShareViewModel::class.java)
        shareViewModel.shareViewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                initHeader(),
                initHashtag(),
                initMusicToggle(),
                initGreenScreenToggle(),
                initAnchorToggle(),
                initAnchorText(viewState.anchorContent, viewState.anchorExtraEnabled),
                initExtraText(viewState.extraContent)
            )
            recyclerAdapter.updateModels(recyclerViewDataModel)
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout()) {
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initHashtag(): EditModel {
        return EditModel(getString(R.string.demo_app_hashtag_info), getString(R.string.demo_app_hashtag_desc))
    }

    private fun initExtraText(extraContent: String): EditModel {
        return EditModel(getString(R.string.demo_app_extra_info), getString(R.string.demo_app_extra_desc), extraContent)
    }

    private fun initAnchorText(anchorContent: String, anchorExtraEnabled: Boolean): EditModel {
        return EditModel(getString(R.string.demo_app_anchor_info), getString(R.string.demo_app_anchor_desc), anchorContent, anchorExtraEnabled)
    }

    private fun initMusicToggle(): ToggleModel {
        return ToggleModel(getString(R.string.demo_app_music_select_info), getString(R.string.demo_app_music_select_desc))
    }

    private fun initGreenScreenToggle(): ToggleModel {
        return ToggleModel(getString(R.string.demo_app_green_screen_info), getString(R.string.demo_app_green_screen_desc))
    }

    private fun initAnchorToggle(): ToggleModel {
        return ToggleModel(getString(R.string.demo_app_anchor_toggle_info), getString(R.string.demo_app_anchor_toggle_desc))
    }

    private fun initHeader(): HeaderModel {
        return HeaderModel(getString(R.string.demo_app_header_info), getString(R.string.demo_app_header_desc))
    }

    private fun publish() {
        composeShareModel()
        shareViewModel.publish(this::class.simpleName.toString())
    }

    private fun composeShareModel(): Boolean {
        val currentState = shareViewModel.shareViewState.value
        val hashtags = shareViewModel.shareViewState.value?.let { ShareUtils.parseHashtags(it.hashtagContent) }
        shareModel.hashtags = hashtags
        if (currentState != null) {
            shareModel.disableMusicSelection = currentState.musicSelection
        }
        if (currentState != null) {
            shareModel.autoAttachAnchor = currentState.autoAttachAnchor
        } else {
            shareModel.autoAttachAnchor = false
        }
        val anchorSource = currentState?.anchorSourceType?.let { ShareUtils.parseAnchorSourceType(it) }
        if (shareModel.autoAttachAnchor) {
            shareModel.anchorSourceType = anchorSource
            val extra: Map<String, String>?
            if (currentState != null) {
                if (currentState.extraContent.isNotEmpty()) {
                    return try {
                        extra = ShareUtils.parseJSON(currentState.extraContent)
                        shareModel.shareExtra = extra
                        true
                    } catch (ex: Exception) {
                        AlertDialog.Builder(this).setTitle(getString(R.string.demo_app_json_format_error_info))
                            .setMessage(getString(R.string.demo_app_json_format_error_desc)).setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
                            .create().show()
                        false
                    }
                } else {
                    shareModel.shareExtra = null
                }
            }
        }

        return true
    }

    private fun hashtagText(hashtags: String) {
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

    private fun anchorText(anchors: String) {
        shareViewModel.updateAnchorText(anchors)
    }

    private fun extraText(extra: String) {
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
