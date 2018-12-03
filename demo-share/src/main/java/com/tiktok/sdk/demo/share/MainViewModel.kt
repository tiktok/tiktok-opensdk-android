package com.tiktok.sdk.demo.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiktok.sdk.demo.share.constants.Constants.CLIENT_KEY

class MainViewModel : ViewModel() {

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
