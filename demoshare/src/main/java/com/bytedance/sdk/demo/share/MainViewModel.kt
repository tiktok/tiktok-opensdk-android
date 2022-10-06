package com.bytedance.sdk.demo.share

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.sdk.demo.share.common.constants.Constants

class MainViewModel : ViewModel() {
    @SuppressWarnings("unchecked")
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }

    private val _mainViewState: MutableLiveData<MainViewModelViewState> = MutableLiveData(
        MainViewModelViewState()
    )
    val mainViewState: LiveData<MainViewModelViewState> = _mainViewState

    data class MainViewModelViewState(
        val customizeEnabled: Boolean = false,
        val customizedClientKey: String = BuildConfig.CLIENT_KEY
    )

    fun getShareModelIntent(intent: Intent): Intent {
        val currentStateValue: MainViewModelViewState = _mainViewState.value ?: MainViewModelViewState()

        val shareModel: ShareModel = if (currentStateValue.customizeEnabled) {
            ShareModel("", currentStateValue.customizedClientKey, BuildConfig.CLIENT_SECRET)
        } else {
            ShareModel("", BuildConfig.CLIENT_KEY, BuildConfig.CLIENT_SECRET)
        }

        intent.putExtra(Constants.SHARE_MODEL, shareModel)
        return intent
    }

    fun updateCustomClientKey(text: String) {
        val currentStateValue: MainViewModelViewState = _mainViewState.value ?: MainViewModelViewState()
        val isOn = currentStateValue.customizeEnabled
        if (!isOn) {
            return
        }

        _mainViewState.value = currentStateValue.copy(customizedClientKey = text, customizeEnabled = isOn)
    }

    fun updateCustomizedEnabled(isOn: Boolean) {
        val currentStateValue: MainViewModelViewState = _mainViewState.value ?: MainViewModelViewState()

        _mainViewState.value = currentStateValue.copy(customizeEnabled = isOn)
    }
}
