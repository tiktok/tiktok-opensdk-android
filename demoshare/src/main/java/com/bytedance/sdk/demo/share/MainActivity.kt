package com.bytedance.sdk.demo.share

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.main.MainActivityAdapter
import com.bytedance.sdk.demo.share.model.*

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
        print("share")
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
        val bundleId = HintedTextModel("Bundle ID", "Demo app bundle ID", "com.tiktokopen.demonew", hintedText, bundleIdEditable)

        val clientKeyText = MutableLiveData("")
        val ckEditable = MutableLiveData(false)
        val clientKey = HintedTextModel("Client Key", "Demo app client key from dev portal", "client_key", clientKeyText, ckEditable)

        val clientSecretText = MutableLiveData("")
        val csEditable = MutableLiveData(false)
        val clientSecret = HintedTextModel("Client Secret", "Demo app client secret from dev portal", "client_secret", clientSecretText, csEditable)
        customEditable.observeForever { isEditable ->
            bundleIdEditable.postValue(isEditable)
            ckEditable.postValue(isEditable)
            csEditable.postValue(isEditable)
        }
        return arrayListOf(bundleId, clientKey, clientSecret)
    }

    private fun initCustomClientKeyModel(): ToggleModel {
        val customization = MutableLiveData<Boolean>(false)
        customization.observeForever { customizable ->
            customEditable.postValue(customizable)
        }
        return ToggleModel("Custom CS & CK", "Customize your client key and client secret", customization)
    }

    private fun initHeaderModel(): HeaderModel {
        return HeaderModel("Base App Info")
    }

    private fun initLogoModel(): LogoModel {
        return LogoModel()
    }

}