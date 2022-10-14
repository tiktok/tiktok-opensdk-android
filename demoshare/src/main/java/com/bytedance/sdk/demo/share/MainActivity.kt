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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.model.EditTextModel
import com.bytedance.sdk.demo.share.model.EditTextType
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.InfoModel
import com.bytedance.sdk.demo.share.model.LogoModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ToggleType
import com.bytedance.sdk.open.tiktok.BuildConfig.TIKTOK_AUTH_ACTIVITY
import com.bytedance.sdk.open.tiktok.BuildConfig.TIKTOK_SHARE_ACTIVITY
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.helper.TikTokCheck
import com.bytedance.sdk.open.tiktok.utils.AppUtils

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: MainActivityAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this, MainViewModel.Factory()).get(
            MainViewModel::class.java
        )

        findViewById<Button>(R.id.share_button).setOnClickListener { this.share() }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = MainActivityAdapter(
            onSaveEditTextValue = mainViewModel::updateCustomClientKey,
            onSaveToggleStatus = mainViewModel::updateCustomizedEnabled,
        )
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        initRecyclerViewData()
    }
    private fun share() {
        recyclerAdapter.saveTextInput()
        val intent = mainViewModel.getShareModelIntent(Intent(this, SelectMediaActivity::class.java))
        startActivity(intent)
    }

    override fun onDestroy() {
        recyclerAdapter.saveTextInput()
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerViewData() {
        mainViewModel.mainViewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                LogoModel(),
                HeaderModel(getString(R.string.demo_app_base_app_info)),
                ToggleModel(getString(R.string.demo_app_info_customize), getString(R.string.demo_app_desc_customize), isOn = viewState.customizeEnabled, toggleType = ToggleType.ENABLE_CUSTOMIZE_KEY),
                EditTextModel(EditTextType.CLIENT_KEY, R.string.demo_app_title_client_key, R.string.demo_app_title_client_secret_key, hint = BuildConfig.CLIENT_KEY, text = viewState.clientKey, enabled = viewState.customizeEnabled),
                initInfoText(),
            )
            recyclerAdapter.updateModels(recyclerViewDataModel)
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout) {
                recyclerAdapter.notifyDataSetChanged()
            }
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
                getString(R.string.installed)
            )
        } else {
            InfoModel(
                getString(R.string.target_app_installed), getString(R.string.check_if_app_installed, getString(R.string.tiktok_app_name)),
                getString(R.string.uninstalled)
            )
        }
    }
}
