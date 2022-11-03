package com.bytedance.sdk.demo.auth

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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.auth.model.ConfigModel
import com.bytedance.sdk.demo.auth.model.HeaderModel
import com.bytedance.sdk.demo.auth.model.LogoModel
import com.bytedance.sdk.open.tiktok.auth.Auth
import com.bytedance.sdk.open.tiktok.auth.AuthApi
import com.bytedance.sdk.open.tiktok.auth.AuthApiEventHandler

class MainActivity : AppCompatActivity(), AuthApiEventHandler {
    private lateinit var viewModel: MainViewModel
    private lateinit var authApi: AuthApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: MainRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authApi = AuthApi(
            context = this,
            clientKey = BuildConfig.CLIENT_KEY,
            apiEventHandler = this
        )
        viewModel = ViewModelProvider(this, MainViewModel.Factory(authApi))[MainViewModel::class.java]

        recyclerView = findViewById(R.id.recycler_view)
        recyclerAdapter = MainRecyclerAdapter(
            onScopeToggle = viewModel::toggleScopeState
        )
        recyclerView.adapter = recyclerAdapter
        findViewById<TextView>(R.id.auth_button).setOnClickListener {
            this.authorize()
        }
        initRecyclerViewData()

        lifecycleScope.launchWhenCreated {
            viewModel.viewEffectFlow.collect {
                when (it) {
                    is MainViewModel.ViewEffect.ShowGeneralAlert -> showAlert(getString(it.titleRes), getString(it.descriptionRes))
                    is MainViewModel.ViewEffect.ShowAlertWithResponseError -> showAlert(getString(it.titleRes), it.description)
                    is MainViewModel.ViewEffect.GettingUserInfoSuccess -> {
                        showGettingUserInfoSuccessDialog(it.grantedPermissions, it.accessToken, it.displayName)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::authApi.isInitialized) {
            authApi.handleResultIntent(intent)
        }
    }

    private fun initRecyclerViewData() {
        viewModel.viewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                LogoModel(),
                HeaderModel(getString(R.string.config)),
                ConfigModel(
                    getString(R.string.always_in_web),
                    getString(R.string.always_in_web_description),
                    viewState.webAuthEnabled,
                    viewModel::toggleWebAuthEnabled
                ),
                HeaderModel(getString(R.string.scope_configuration)),
            )
            viewState.scopeStates.map {
                recyclerViewDataModel.add(it.value)
            }
            recyclerAdapter.updateModels(recyclerViewDataModel)
            if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout()) {
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun authorize() {
        viewModel.authorize(
            packageName = BuildConfig.APPLICATION_ID, // the package name of your app, must be same as what we have on developer portal
            resultActivityFullPath = "$packageName.${this::class.simpleName}" // com.bytedance.sdk.demo.auth.MainActivity, the full path of activity which will receive the sdk results
        )
    }

    private fun showGettingUserInfoSuccessDialog(grantedPermission: String, accessToken: String, displayName: String) {
        val descriptionBuilder = StringBuilder().apply {
            append(getString(R.string.user_info_description_granted_permission, grantedPermission))
            append("\n")
            append(getString(R.string.user_info_description_access_token, accessToken))
            append("\n")
            append(getString(R.string.user_info_description_display_name, displayName))
        }
        AlertDialog
            .Builder(this)
            .setTitle(getString(R.string.getting_user_info_succeeds))
            .setMessage(descriptionBuilder.toString())
            .setPositiveButton(getString(R.string.copy_access_token)) { dialog, _ ->
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("access token", accessToken)
                clipboard.setPrimaryClip(clip)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun showAlert(title: String, desc: String) {
        AlertDialog
            .Builder(this)
            .setTitle(title)
            .setMessage(desc)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    //  IApiEventHandler
    override fun onRequest(req: Auth.Request) = Unit

    override fun onResponse(resp: Auth.Response) {
        with(resp) {
            val authCode = authCode
            if (authCode.isNotEmpty()) {
                viewModel.updateGrantedScope(grantedPermissions)
                viewModel.getUserBasicInfo(authCode, grantedPermissions)
            } else if (errorCode != 0) {
                showAlert(
                    getString(R.string.error_dialog_title),
                    getString(
                        R.string.error_code_with_message,
                        errorCode,
                        errorMsg
                    )
                )
            }
        }
    }
}
