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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareApi
import com.bytedance.sdk.open.tiktok.share.model.MediaContent
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

    fun publish(packageName: String, resultActivityFullPath: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val request = currentStateValue.toShareRequest(packageName, resultActivityFullPath)
        shareApi.share(request)
    }

    private fun ShareViewModelViewState.toShareRequest(packageName: String, resultActivityFullPath: String): Share.Request {
        return Share.Request(
            mediaContent = MediaContent(if (isSharingImage) Share.MediaType.IMAGE else Share.MediaType.VIDEO, mediaUrls),
            shareFormat = if (greenScreenEnabled) {
                Share.Format.GREEN_SCREEN
            } else {
                Share.Format.DEFAULT
            },
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath
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
