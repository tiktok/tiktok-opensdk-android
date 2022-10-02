package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.main.MainActivityAdapter
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.HintedTextModel
import com.bytedance.sdk.demo.share.model.InfoModel
import com.bytedance.sdk.demo.share.model.LogoModel
import com.bytedance.sdk.demo.share.model.MainToggleModel
import com.bytedance.sdk.demo.share.model.ViewType
import com.bytedance.sdk.open.tiktok.BuildConfig.TIKTOK_AUTH_ACTIVITY
import com.bytedance.sdk.open.tiktok.BuildConfig.TIKTOK_SHARE_ACTIVITY
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.helper.TikTokCheck
import com.bytedance.sdk.open.tiktok.utils.AppUtils

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var models: List<DataModel>
    private lateinit var shareButton: Button
    private val customEditable = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        initData()
        recyclerView.adapter = MainActivityAdapter(models)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    private fun share() {
        val intent = Intent(this, SelectMediaActivity::class.java)
        val shareModel = ShareModel("", BuildConfig.CLIENT_KEY, BuildConfig.CLIENT_SECRET)
        if (customEditable.value == true) {
            for (model in models) {
                if (model.viewType == ViewType.HINTED_TEXT) {
                    (model as HintedTextModel).let {
                        if (it.title == getString(R.string.demo_app_title_package)) {
                            shareModel.packageName = (it.text.value ?: "").trim()
                        } else if (it.title == getString(R.string.demo_app_title_client_key)) {
                            shareModel.clientKey = (it.text.value ?: "").trim()
                        } else if (it.title == getString(R.string.demo_app_title_client_secret_key)) {
                            shareModel.clientSecret = (it.text.value ?: "").trim()
                        }
                    }
                }
            }
        } else {
            shareModel.packageName = ""
            shareModel.clientKey = ""
            shareModel.clientSecret = ""
        }
        intent.putExtra("share_model", shareModel)
        startActivity(intent)
    }

    private fun initData() {
        shareButton = findViewById(R.id.share_button)
        shareButton.setOnClickListener {
            share()
        }
        models = mutableListOf<DataModel>().apply {
            add(initLogoModel())
            add(initHeaderModel())
            add(initCustomClientKeyModel())
            addAll(initHintedModels())
            add(initInfoText())
        }
    }

    private fun initInfoText(): InfoModel {
        val entryComponent = EntryComponent(
            TikTokCheck(this).packageName,
            TIKTOK_SHARE_ACTIVITY,
            TIKTOK_AUTH_ACTIVITY
        )
        return if (AppUtils.getPlatformSDKVersion(this, entryComponent.tiktokPackage, entryComponent.tiktokPlatformComponent) >= Keys.API.MIN_SDK_NEW_VERSION_API) {
            InfoModel(
                getString(R.string.target_app_installed), getString(R.string.check_if_app_installed, getString(R.string.tiktok_app_name)),
                MutableLiveData(getString(R.string.installed))
            )
        } else {
            InfoModel(
                getString(R.string.target_app_installed), getString(R.string.check_if_app_installed, getString(R.string.tiktok_app_name)),
                MutableLiveData(getString(R.string.uninstalled))
            )
        }
    }

    private fun initHintedModels(): List<HintedTextModel> {
        val hintedText = MutableLiveData("")
        val bundleIdEditable = MutableLiveData(false)
        val bundleId = HintedTextModel(getString(R.string.demo_app_title_package), getString(R.string.demo_app, getString(R.string.demo_app_title_package)), this.packageName, hintedText, bundleIdEditable)

        val clientKeyText = MutableLiveData("")
        val ckEditable = MutableLiveData(false)
        val clientKey = HintedTextModel(getString(R.string.demo_app_title_client_key), getString(R.string.demo_app, getString(R.string.demo_app_title_client_key)), getString(R.string.demo_app_placeholder_client_key), clientKeyText, ckEditable)

        val clientSecretText = MutableLiveData("")
        val csEditable = MutableLiveData(false)
        val clientSecretKey = HintedTextModel(getString(R.string.demo_app_title_client_secret_key), getString(R.string.demo_app, getString(R.string.demo_app_title_client_secret_key)), getString(R.string.demo_app_placeholder_client_secret_key), clientSecretText, csEditable)
        customEditable.observeForever { isEditable ->
            bundleIdEditable.postValue(isEditable)
            ckEditable.postValue(isEditable)
            csEditable.postValue(isEditable)
        }
        return arrayListOf(bundleId, clientKey, clientSecretKey)
    }

    private fun initCustomClientKeyModel(): MainToggleModel {
        val customization = MutableLiveData<Boolean>(false)
        customization.observeForever { customizable ->
            customEditable.postValue(customizable)
        }

        return MainToggleModel(getString(R.string.demo_app_info_customize), getString(R.string.demo_app_desc_customize), customization)
    }

    private fun initHeaderModel(): HeaderModel {
        return HeaderModel(getString(R.string.demo_app_base_app_info))
    }

    private fun initLogoModel(): LogoModel {
        return LogoModel()
    }
}
