package com.tiktok.sdk.demo.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.tiktok.open.sdk.auth.AuthApi
import com.tiktok.open.sdk.auth.utils.PKCEUtils
import com.tiktok.sdk.demo.auth.model.ConfigModel
import com.tiktok.sdk.demo.auth.model.HeaderModel
import com.tiktok.sdk.demo.auth.model.LogoModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var authApi: AuthApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: MainRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authApi = AuthApi(activity = this)
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
        handleAuthResponse(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthResponse(intent)
    }

    private fun handleAuthResponse(intent: Intent) {
        val prefs = getSharedPreferences(KEY_CODE_VERIFIER, Context.MODE_PRIVATE)
        val codeVerifier = prefs.getString(KEY_CODE_VERIFIER, "").toString()
        authApi.getAuthResponseFromIntent(intent, BuildConfig.REDIRECT_URL)?.let {
            val authCode = it.authCode
            if (authCode.isNotEmpty()) {
                viewModel.updateGrantedScope(it.grantedPermissions)
                viewModel.getUserBasicInfo(authCode, it.grantedPermissions, codeVerifier)
            } else if (it.errorCode != 0) {
                val description = if (it.errorMsg != null) {
                    getString(
                        R.string.error_code_with_message,
                        it.errorCode,
                        it.errorMsg
                    )
                } else {
                    if (it.authErrorDescription != null) {
                        getString(
                            R.string.error_code_with_error_description,
                            it.authError,
                            it.authErrorDescription
                        )
                    } else {
                        getString(
                            R.string.error_code_with_error,
                            it.errorCode,
                            it.authError,
                        )
                    }
                }
                showAlert(
                    getString(R.string.error_dialog_title),
                    description
                )
            }
        }
    }

    private fun initRecyclerViewData() {
        viewModel.viewState.observe(this) { viewState ->
            val recyclerViewDataModel = mutableListOf(
                LogoModel(),
                HeaderModel(getString(R.string.config)),
                ConfigModel(
                    getString(R.string.always_in_browser),
                    getString(R.string.always_in_browser_description),
                    viewState.browserAuthEnabled,
                    viewModel::toggleBrowserAuthEnabled
                ),
                ConfigModel(
                    getString(R.string.disable_auto_auth),
                    getString(R.string.disable_auto_auth_description),
                    viewState.autoAuthDisabled,
                    viewModel::toggleAutoAuthEnabled
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
        val prefs = getSharedPreferences(KEY_CODE_VERIFIER, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_CODE_VERIFIER, PKCEUtils.generateCodeVerifier())
        editor.apply()

        val codeVerifier = prefs.getString(KEY_CODE_VERIFIER, "").toString()
        viewModel.authorize(codeVerifier)
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

    companion object {
        private const val KEY_CODE_VERIFIER = "key_code_verifier"
    }
}
