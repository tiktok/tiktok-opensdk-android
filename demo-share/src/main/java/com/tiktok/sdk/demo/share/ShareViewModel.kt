package com.tiktok.sdk.demo.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tiktok.open.sdk.share.Format
import com.tiktok.open.sdk.share.MediaType
import com.tiktok.open.sdk.share.ShareApi
import com.tiktok.open.sdk.share.ShareRequest
import com.tiktok.open.sdk.share.model.MediaContent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ShareViewModel(
    private val shareApi: ShareApi,
    private val isSharingImage: Boolean,
    private val mediaUrls: ArrayList<String>
) : ViewModel() {

    @SuppressWarnings("unchecked")
    class Factory(
        val shareApi: ShareApi,
        val isSharingImage: Boolean,
        val mediaUrls: ArrayList<String>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShareViewModel(shareApi, isSharingImage, mediaUrls) as T
        }
    }

    sealed class ViewEffect {
        object GREEN_SCREEN_UNSUPPORTED : ViewEffect()
    }

    private val _shareViewState: MutableLiveData<ShareViewModelViewState> = MutableLiveData(
        ShareViewModelViewState()
    )
    val shareViewState: LiveData<ShareViewModelViewState> = _shareViewState

    private val _viewEffect: Channel<ViewEffect> = Channel()
    val viewEffectFlow: Flow<ViewEffect> = _viewEffect.receiveAsFlow()

    data class ShareViewModelViewState(
        val greenScreenEnabled: Boolean = false
    )

    fun publish(
        clientKey: String,
        packageName: String,
        resultActivityFullPath: String
    ): Boolean {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val request = currentStateValue.toShareRequest(clientKey, packageName, resultActivityFullPath)
        return shareApi.share(request)
    }

    private fun ShareViewModelViewState.toShareRequest(
        clientKey: String,
        packageName: String,
        resultActivityFullPath: String
    ): ShareRequest {
        return ShareRequest(
            clientKey = clientKey,
            mediaContent = MediaContent(if (isSharingImage) MediaType.IMAGE else MediaType.VIDEO, mediaUrls),
            shareFormat = if (greenScreenEnabled) {
                Format.GREEN_SCREEN
            } else {
                Format.DEFAULT
            },
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath,
        )
    }

    fun updateGreenScreenStatus(enabled: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        if (enabled && mediaUrls.size > 1) {
            viewModelScope.launch {
                _viewEffect.send(ViewEffect.GREEN_SCREEN_UNSUPPORTED)
            }
            _shareViewState.value = currentStateValue.copy(greenScreenEnabled = false)
        } else {
            _shareViewState.value = currentStateValue.copy(greenScreenEnabled = enabled)
        }
    }
}
