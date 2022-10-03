package com.bytedance.sdk.demo.auth

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytedance.sdk.demo.auth.model.ScopeModel
import com.bytedance.sdk.demo.auth.model.ScopeType
import com.bytedance.sdk.demo.auth.userinfo.UserInfoQuery
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val tikTokOpenApi: TikTokOpenApi
) : ViewModel() {

    @SuppressWarnings("unchecked")
    class Factory(val tikTokOpenApi: TikTokOpenApi) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(tikTokOpenApi) as T
        }
    }

    data class MainViewModelViewState(
        val webAuthEnabled: Boolean = false,
        val betaEnabled: Boolean = false,
        val scopeStates: LinkedHashMap<ScopeType, ScopeModel> = linkedMapOf(
            ScopeType.USER_INFO_BASIC to ScopeModel(ScopeType.USER_INFO_BASIC, R.string.basic_scope_description, true, true, false),
            ScopeType.USER_INFO_USERNAME to ScopeModel(ScopeType.USER_INFO_USERNAME, R.string.user_name_scope_description, false, false, true),
            ScopeType.USER_INFO_PHONE to ScopeModel(ScopeType.USER_INFO_PHONE, R.string.phone_scope_description, false, false, true),
            ScopeType.USER_INFO_EMAIL to ScopeModel(ScopeType.USER_INFO_EMAIL, R.string.email_scope_description, false, false, true),
            ScopeType.MUSIC_COLLECTION to ScopeModel(ScopeType.MUSIC_COLLECTION, R.string.music_scope_description, false, false, true),
            ScopeType.VIDEO_UPLOAD to ScopeModel(ScopeType.VIDEO_UPLOAD, R.string.video_upload_scope_description, false, false, true),
            ScopeType.VIDEO_LIST to ScopeModel(ScopeType.VIDEO_LIST, R.string.video_list_scope_description, false, false, true),
            ScopeType.USER_INTEREST to ScopeModel(ScopeType.USER_INTEREST, R.string.user_interest_scope_description, false, false, true),
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
            val displayName: String
        ) : ViewEffect()
    }

    private val _viewEffect: Channel<ViewEffect> = Channel()
    val viewEffectFlow: Flow<ViewEffect> = _viewEffect.receiveAsFlow()

    private val _viewState: MutableLiveData<MainViewModelViewState> = MutableLiveData(MainViewModelViewState())
    val viewState: LiveData<MainViewModelViewState> = _viewState

    fun toggleWebAuthEnabled(webAuthEnabled: Boolean) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: MainViewModelViewState()
        _viewState.value = currentStateValue.copy(
            webAuthEnabled = webAuthEnabled,
        )
    }

    fun toggleBetaEnabled(betaEnabled: Boolean) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: MainViewModelViewState()
        val currentScopeStates = currentStateValue.scopeStates
        val newScopeStates = linkedMapOf<ScopeType, ScopeModel>()
        currentScopeStates.forEach {
            if (it.key != ScopeType.USER_INFO_BASIC) {
                newScopeStates[it.key] = it.value.copy(
                    isOn = false,
                    isEnabled = betaEnabled,
                )
            } else {
                newScopeStates[it.key] = it.value
            }
        }
        _viewState.value = currentStateValue.copy(
            betaEnabled = betaEnabled,
            scopeStates = newScopeStates
        )
    }

    fun toggleScopeState(scopeType: ScopeType, isOn: Boolean) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: MainViewModelViewState()
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

    fun authorize(resultActivityComponent: ResultActivityComponent) {
        val currentStateValue: MainViewModelViewState = _viewState.value ?: MainViewModelViewState()
        val currentScopeStates = currentStateValue.scopeStates
        val webAuthEnabled = currentStateValue.webAuthEnabled
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
            scope = enabledScopes.joinBy(","),
            resultActivityComponent = resultActivityComponent,
        )
        tikTokOpenApi.authorize(request, webAuthEnabled)
    }

    fun getUserBasicInfo(authCode: String) {
        UserInfoQuery.getAccessToken(authCode) { response, errorMsg ->
            errorMsg?.let {
                sendViewEffect(ViewEffect.ShowAlertWithResponseError(R.string.access_token_error, errorMsg))
                return@getAccessToken
            }
            response?.let { accessTokenInfo ->
                UserInfoQuery.getUserInfo(accessTokenInfo.accessToken, accessTokenInfo.openid) { userInfo, errorMessage ->
                    errorMessage?.let {
                        sendViewEffect(ViewEffect.ShowAlertWithResponseError(R.string.user_info_error, errorMessage))
                        return@getUserInfo
                    }
                    userInfo?.let {
                        sendViewEffect(ViewEffect.GettingUserInfoSuccess(it.nickName))
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
