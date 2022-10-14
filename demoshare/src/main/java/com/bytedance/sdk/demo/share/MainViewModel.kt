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

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.sdk.demo.share.constants.Constants.CLIENT_KEY

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
        val clientKey: String = BuildConfig.CLIENT_KEY
    )

    fun getShareModelIntent(intent: Intent): Intent {
        val currentStateValue: MainViewModelViewState = _mainViewState.value ?: MainViewModelViewState()
        intent.putExtra(CLIENT_KEY, currentStateValue.clientKey)
        return intent
    }

    fun updateCustomClientKey(text: String) {
        val currentStateValue: MainViewModelViewState = _mainViewState.value ?: MainViewModelViewState()
        val isOn = currentStateValue.customizeEnabled
        if (!isOn) {
            return
        }

        _mainViewState.value = currentStateValue.copy(clientKey = text, customizeEnabled = isOn)
    }

    fun updateCustomizedEnabled(isOn: Boolean) {
        val currentStateValue: MainViewModelViewState = _mainViewState.value ?: MainViewModelViewState()

        _mainViewState.value = currentStateValue.copy(
            customizeEnabled = isOn,
            clientKey = if (isOn) currentStateValue.clientKey else BuildConfig.CLIENT_KEY
        )
    }
}
