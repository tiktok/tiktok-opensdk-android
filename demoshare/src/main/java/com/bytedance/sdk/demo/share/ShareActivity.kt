package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.publish.ShareActivityAdapter
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
    private lateinit var models: List<DataModel>
    private lateinit var tiktokOpenAPI: TikTokOpenApi

    private lateinit var autoAttachAnchor: MutableLiveData<Boolean>
    private lateinit var anchorSourceTypeEnabled: MutableLiveData<Boolean>
    private lateinit var anchorExtraEnabled: MutableLiveData<Boolean>

    private var hashtagString: String = ""
    private var anchorSourceType: String = ""
    private var shareExtra: String = ""

    private var disableMusicSelection = false
    private var greenScreenFormat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        shareModel = intent.getParcelableExtra("share_model")!!

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }
        publishButton = findViewById(R.id.share_button)
        publishButton.setOnClickListener { publish() }

        recyclerView = findViewById(R.id.recycler_view)
        initData()
        recyclerView.adapter = ShareActivityAdapter(this, models)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (::tiktokOpenAPI.isInitialized) {
            tiktokOpenAPI.handleIntent(intent)
        }
    }

    private fun publish() {
        if (!composeShareModel()) {
            return
        }
        // replace `this::class.simpleName` to `null` below to let the default `tiktokapi.TikTokEntryActivity` to handle the IApiEventHandler callbacks
        val request = shareModel.toShareRequest(this::class.simpleName)
        val tiktokOpenConfig = TikTokOpenConfig(shareModel.clientKey.ifEmpty { BuildConfig.CLIENT_KEY })
        TikTokOpenApiFactory.init(tiktokOpenConfig)
        TikTokOpenApiFactory.create(this, this).let {
            tiktokOpenAPI = it
            tiktokOpenAPI.share(request)
        }
    }

    private fun initData() {
        models = mutableListOf<DataModel>().apply {
            add(initHeader())
            add(initHashtag())
            addAll(initToggles())
            add(initAnchorEditModel())
            addAll(initExtraEditText())
        }
    }

    private fun initHashtag(): EditModel {
        val hashtagContent = MutableLiveData("")
        hashtagContent.observeForever {
            hashtagString = it
        }
        val hashtagModel = EditModel("Hashtag", "Hashtags attached to the video i.e. #abc #def", hashtagContent)
        return hashtagModel
    }

    private fun initExtraEditText(): List<EditModel> {
        val extraContent = MutableLiveData("")
        extraContent.observeForever {
            shareExtra = it
        }
        anchorSourceTypeEnabled = MutableLiveData(false)
        val extraEdit = EditModel(
            "Extra", "JSONObject string of information included in Share request",
            extraContent, anchorSourceTypeEnabled
        )
        return arrayListOf(extraEdit)
    }

    private fun initAnchorEditModel(): EditModel {
        val anchorContent = MutableLiveData("")
        anchorContent.observeForever {
            anchorSourceType = it
        }
        anchorExtraEnabled = MutableLiveData(false)
        val anchorEdit = EditModel(
            "Anchor source type", "The types of anchors that will be attached to the video",
            anchorContent, anchorExtraEnabled
        )
        return anchorEdit
    }

    private fun initToggles(): List<ToggleModel> {
        val disableMusicOn = MutableLiveData(false)
        disableMusicOn.observeForever {
            disableMusicSelection = it
        }
        val disableMusic = ToggleModel("Disable music selection", "Suppress automatically attaching music", disableMusicOn)

        val gsOn = MutableLiveData(false)
        gsOn.observeForever {
            greenScreenFormat = it
        }
        val greenScreen = ToggleModel("Use green-screen format", "Automatically apply green-screen effect", gsOn)

        autoAttachAnchor = MutableLiveData(false)
        autoAttachAnchor.observeForever { enabled ->
            if (::anchorSourceTypeEnabled.isInitialized) {
                anchorSourceTypeEnabled.postValue(enabled)
            }
            if (::anchorExtraEnabled.isInitialized) {
                anchorExtraEnabled.postValue(enabled)
            }
        }
        val anchor = ToggleModel("Auto attach anchor", "Automatically attach anchor to the video (Sharing images is not supported)", autoAttachAnchor)

        return arrayListOf(disableMusic, greenScreen, anchor)
    }

    private fun initHeader(): HeaderModel {
        return HeaderModel("Share meta info", "Description of the video kit and the features available in the SDK demo app")
    }

    private fun composeShareModel(): Boolean {
        val hashtags = ShareUtils.parseHashtags(hashtagString)
        shareModel.hashtags = hashtags
        shareModel.disableMusicSelection = disableMusicSelection
        shareModel.greenScreenFormat = greenScreenFormat
        shareModel.autoAttachAnchor = autoAttachAnchor.value ?: false
        val anchorSource = ShareUtils.parseAnchorSourceType(anchorSourceType)
        if (shareModel.autoAttachAnchor) {
            shareModel.anchorSourceType = anchorSource
            val extra: Map<String, String>?
            if (shareExtra.isNotEmpty()) {
                return try {
                    extra = ShareUtils.parseJSON(shareExtra)
                    shareModel.shareExtra = extra
                    true
                } catch (ex: Exception) {
                    val alertBuilder = AlertDialog.Builder(this)
                    alertBuilder.setTitle("Invalid Format")
                    alertBuilder.setMessage("Share extra is in invalid JSONObject format")
                    alertBuilder.setPositiveButton("OK") { dialog, _ -> dialog.cancel() }
                    alertBuilder.create().show()
                    false
                }
            } else {
                shareModel.shareExtra = null
            }
        }

        return true
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
