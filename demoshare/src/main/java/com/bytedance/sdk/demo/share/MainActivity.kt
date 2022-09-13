package com.bytedance.sdk.demo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.main.MainActivityAdapter
import com.bytedance.sdk.demo.share.model.*

const val PackageNameTitle = "Package Name"
const val CLientKeyTitle = "Client Key"

class MainActivity: AppCompatActivity() {
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
        val shareModel = ShareModel("", BuildConfig.CLIENT_KEY , BuildConfig.CLIENT_SECRET)
        if (customEditable.value == true) {
            for (model in models) {
                if (model.viewType == ViewType.HINTED_TEXT) {
                    (model as HintedTextModel)?.let {
                        if (it.title == PackageNameTitle) {
                            shareModel.packageName = (it.text.value ?: "").trim()
                        } else if (it.title == CLientKeyTitle) {
                            shareModel.clientKey = (it.text.value ?: "").trim()
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
        val info = MutableLiveData("Yes")
        return InfoModel("Target app installed", "Check if TikTok app is installed", info)
    }

    private fun initHintedModels(): List<HintedTextModel> {
        val hintedText = MutableLiveData("")
        val bundleIdEditable = MutableLiveData(false)
        val bundleId = HintedTextModel(PackageNameTitle, "Demo app bundle ID", "com.tiktokopen.demonew", hintedText, bundleIdEditable)

        val clientKeyText = MutableLiveData("")
        val ckEditable = MutableLiveData(false)
        val clientKey = HintedTextModel(CLientKeyTitle, "Demo app client key from dev portal", "client_key", clientKeyText, ckEditable)

        val clientSecretText = MutableLiveData("")
        val csEditable = MutableLiveData(false)
        customEditable.observeForever { isEditable ->
            bundleIdEditable.postValue(isEditable)
            ckEditable.postValue(isEditable)
            csEditable.postValue(isEditable)
        }
        return arrayListOf(bundleId, clientKey)
    }

    private fun initCustomClientKeyModel(): ToggleModel {
        val customization = MutableLiveData<Boolean>(false)
        customization.observeForever { customizable ->
            customEditable.postValue(customizable)
        }
        return ToggleModel("Custom CS & CK", "Customize your client key and package name", customization)
    }

    private fun initHeaderModel(): HeaderModel {
        return HeaderModel("Base App Info")
    }

    private fun initLogoModel(): LogoModel {
        return LogoModel()
    }

}