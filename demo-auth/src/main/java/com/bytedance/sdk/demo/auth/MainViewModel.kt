package com.bytedance.sdk.demo.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytedance.sdk.demo.auth.model.ScopeModel
import com.bytedance.sdk.demo.auth.model.ScopeType
import com.bytedance.sdk.demo.auth.userinfo.UserInfoQuery
import com.bytedance.sdk.open.tiktok.auth.Auth
import com.bytedance.sdk.open.tiktok.auth.AuthApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authApi: AuthApi
) : ViewModel() {

    @SuppressWarnings("unchecked")
    class Factory(val authApi: AuthApi) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(authApi) as T
        }
    }

    data class MainViewModelViewState(
        val browserAuthEnabled: Boolean,
        val scopeStates: LinkedHashMap<ScopeType, ScopeModel>
    )

    private val _viewEffect: Channel<ViewEffect> = Channel()
    val viewEffectFlow: Flow<ViewEffect> = _viewEffect.receiveAsFlow()

    private val _viewState: MutableLiveData<MainViewModelViewState>
    val viewState: LiveData<MainViewModelViewState>

    init {
        _viewState = MutableLiveData(
            getDefaultViewState()
        )
        viewState = _viewState
    }

    private fun getDefaultViewState() = MainViewModel.MainViewModelViewState(
        browserAuthEnabled = false,
        scopeStates = linkedMapOf(
            ScopeType.USER_INFO_BASIC to ScopeModel(
                ScopeType.USER_INFO_BASIC,
                R.string.basic_scope_description,
                true,
                false
            ),
            ScopeType.VIDEO_UPLOAD to ScopeModel(
                ScopeType.VIDEO_UPLOAD,
                R.string.video_upload_scope_description,
                false,
                true
            ),
            ScopeType.VIDEO_LIST to ScopeModel(
                ScopeType.VIDEO_LIST,
                R.string.video_list_scope_description,
                false,
                true
            ),
        )
    )

    sealed class ViewEffect {
        data class ShowGeneralAlert(
            @StringRes
            val titleRes: Int,
            @StringRes
            val descriptionRes: Int,
        ) : ViewEffect()
        data class ShowAlertWithResponseError(
            @StringRes
            val titleRes: Int,
            val description: String,
        ) : ViewEffect()
        data class GettingUserInfoSuccess(
            val grantedPermissions: String,
            val accessToken: String,
            val displayName: String,
        ) : ViewEffect()
    }

    fun toggleBrowserAuthEnabled(newBrowserAuthEnabled: Boolean) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: getDefaultViewState()
        _viewState.value = currentStateValue.copy(
            browserAuthEnabled = newBrowserAuthEnabled,
        )
    }

    fun toggleScopeState(scopeType: ScopeType, isOn: Boolean) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: getDefaultViewState()
        val scopeStates = currentStateValue.scopeStates
        scopeStates[scopeType]?.let {
            if (it.isEditable) {
                scopeStates[scopeType] = it.copy(isOn = isOn)
            } else {
                sendViewEffect(ViewEffect.ShowGeneralAlert(R.string.invalid_scope_auth, R.string.invalid_scope_auth_desc))
                scopeStates[scopeType] = it.copy(isOn = !isOn)
            }
            _viewState.value = currentStateValue.copy(
                scopeStates = scopeStates,
            )
        }
    }

    fun updateGrantedScope(grantedPermissions: String) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: getDefaultViewState()
        val scopeStates = currentStateValue.scopeStates
        val grantedScopesList = grantedPermissions.split(",").mapNotNull { ScopeType.fromValue(it) }
        val newScopeStates = linkedMapOf<ScopeType, ScopeModel>()
        scopeStates.entries.forEach {
            if (grantedScopesList.contains(it.key)) {
                newScopeStates[it.key] = it.value.copy(isOn = true)
            } else {
                newScopeStates[it.key] = it.value.copy(isOn = false)
            }
        }
        _viewState.value = currentStateValue.copy(
            scopeStates = newScopeStates,
        )
    }

    fun authorize(codeVerifier: String) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: getDefaultViewState()
        val currentScopeStates = currentStateValue.scopeStates
        val browserAuthEnabled = currentStateValue.browserAuthEnabled
        val enabledScopes: MutableList<String> = mutableListOf()
        currentScopeStates.forEach {
            if (it.value.isOn) {
                enabledScopes.add(it.key.value)
            }
        }

        if (enabledScopes.size == 0) {
            sendViewEffect(ViewEffect.ShowGeneralAlert(R.string.invalid_scope, R.string.invalid_scope_description))
            return
        }
        val request = Auth.Request(
            clientKey = BuildConfig.CLIENT_KEY,
            scope = enabledScopes.joinToString(),
            redirectUri = BuildConfig.REDIRECT_URL,
            codeVerifier = codeVerifier,
        )
        val authType = if (browserAuthEnabled) {
            AuthApi.AuthMethod.ChromeTab
        } else {
            AuthApi.AuthMethod.TikTokApp
        }
        authApi.authorize(request, authType)
    }

    fun getUserBasicInfo(authCode: String, grantedPermissions: String, codeVerifier: String) {
        // The following code is only for demo purpose, you should store client secret on your server and send this request to get access token on your server
        UserInfoQuery.getAccessToken(authCode, BuildConfig.REDIRECT_URL, codeVerifier) { response, errorMsg ->
            errorMsg?.let {
                sendViewEffect(ViewEffect.ShowAlertWithResponseError(R.string.access_token_error, errorMsg))
                return@getAccessToken
            }
            response?.let { accessTokenInfo ->
                UserInfoQuery.getUserInfo("${accessTokenInfo.tokenType} ${accessTokenInfo.accessToken}") { userInfo, errorMessage ->
                    errorMessage?.let {
                        sendViewEffect(ViewEffect.ShowAlertWithResponseError(R.string.user_info_error, errorMessage))
                        return@getUserInfo
                    }
                    userInfo?.let {
                        sendViewEffect(ViewEffect.GettingUserInfoSuccess(grantedPermissions, accessTokenInfo.accessToken, it.nickName))
                    }
                }
            }
        }
    }

    private fun sendViewEffect(effect: ViewEffect) {
        viewModelScope.launch {
            _viewEffect.send(effect)
        }
    }
}
