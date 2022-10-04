package com.bytedance.sdk.demo.auth

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
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.TikTokOpenConfig
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent

class MainActivity : AppCompatActivity(), IApiEventHandler {
    private lateinit var viewModel: MainViewModel
    private lateinit var tiktokApi: TikTokOpenApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: MainRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tiktokOpenConfig = TikTokOpenConfig(BuildConfig.CLIENT_KEY)
        TikTokOpenApiFactory.init(tiktokOpenConfig)
        tiktokApi = TikTokOpenApiFactory.create(this, this)
        viewModel = ViewModelProvider(this, MainViewModel.Factory(tiktokApi)).get(MainViewModel::class.java)

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
                    is MainViewModel.ViewEffect.GettingUserInfoSuccess ->
                        showAlert(getString(R.string.getting_user_info_succeeds), getString(R.string.display_name, it.displayName))
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::tiktokApi.isInitialized) {
            tiktokApi.handleIntent(intent)
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
                ConfigModel(
                    getString(R.string.beta_mode),
                    getString(R.string.beta_mode_description),
                    viewState.betaEnabled,
                    viewModel::toggleBetaEnabled
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
            ResultActivityComponent(
                this.packageName,
                this.localClassName,
            )
        )
    }

    private fun showAlert(title: String, desc: String) {
        AlertDialog
            .Builder(this)
            .setTitle(title)
            .setMessage(desc)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.cancel() }
            .create().show()
    }

    //  IApiEventHandler
    override fun onResponse(resp: Base.Response) {
        if (resp is Auth.Response) {
            with(resp) {
                val authCode = authCode
                if (authCode.isNotEmpty()) {
                    viewModel.getUserBasicInfo(authCode)
                } else if (errorCode != 0) {
                    showAlert(
                        getString(R.string.error),
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
}
