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
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
import com.bytedance.sdk.open.tiktok.share.Share

class ShareViewModel(
    private val tikTokOpenApi: TikTokOpenApi,
    private val isSharingImage: Boolean,
    private val mediaUrls: ArrayList<String>
) : ViewModel() {

    @SuppressWarnings("unchecked")
    class Factory(val tikTokOpenApi: TikTokOpenApi, val isSharingImage: Boolean, val mediaUrls: ArrayList<String>) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShareViewModel(tikTokOpenApi, isSharingImage, mediaUrls) as T
        }
    }

    private val _shareViewState: MutableLiveData<ShareViewModelViewState> = MutableLiveData(
        ShareViewModelViewState()
    )
    val shareViewState: LiveData<ShareViewModelViewState> = _shareViewState

    data class ShareViewModelViewState(
        val greenScreenEnabled: Boolean = false
    )

    fun publish(resultActivityComponent: ResultActivityComponent) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val request = currentStateValue.toShareRequest(resultActivityComponent)
        tikTokOpenApi.share(request)
    }

    private fun ShareViewModelViewState.toShareRequest(resultActivityComponent: ResultActivityComponent): Share.Request {
        return Share.Request(
            mediaContent = MediaContent(if (isSharingImage) Share.MediaType.IMAGE else Share.MediaType.VIDEO, mediaUrls),
            shareFormat = if (greenScreenEnabled) {
                Share.Format.GREEN_SCREEN
            } else {
                Share.Format.DEFAULT
            },
            resultActivityComponent = resultActivityComponent
        )
    }

    fun updateGreenScreenStatus(enabled: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(greenScreenEnabled = enabled)
    }
}
