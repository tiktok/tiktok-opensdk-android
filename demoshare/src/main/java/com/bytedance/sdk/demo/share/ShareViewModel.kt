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
import com.bytedance.sdk.demo.share.model.EditTextType
import com.bytedance.sdk.demo.share.model.ToggleType
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
import com.bytedance.sdk.open.tiktok.share.Share
import org.json.JSONObject

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
        val toggleStatus: Map<ToggleType, Boolean> = mapOf(
            ToggleType.DISABLE_MUSIC to false,
            ToggleType.GREEN_SCREEN to false,
            ToggleType.AUTO_ATTACH_ANCHOR to false
        ),
        val textStatus: Map<EditTextType, String> = mapOf(
            EditTextType.HASHTAG to "",
            EditTextType.ANCHOR to "",
            EditTextType.EXTRA to "",
        )
    )

    fun publish(resultActivityComponent: ResultActivityComponent) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val request = currentStateValue.toShareRequest(resultActivityComponent)
        tikTokOpenApi.share(request)
    }

    private fun ShareViewModelViewState.toShareRequest(resultActivityComponent: ResultActivityComponent): Share.Request {
        val content = MediaContent(if (isSharingImage) Share.MediaType.IMAGE else Share.MediaType.VIDEO, mediaUrls)
        val parsedHashTags = textStatus[EditTextType.HASHTAG]?.let {
            ShareUtils.parseHashtags(it)
        } ?: arrayListOf()
        val extraShareOptionsMap: HashMap<String, Any> = if (toggleStatus[ToggleType.DISABLE_MUSIC] == true) {
            hashMapOf(Keys.Share.DISABLE_MUSIC_SELECTION to 1)
        } else {
            hashMapOf()
        }
        val shareFormat = if (toggleStatus[ToggleType.GREEN_SCREEN] == true) {
            Share.Format.GREEN_SCREEN
        } else {
            Share.Format.DEFAULT
        }
        var anchor: Anchor? = null
        val anchorSourceType = textStatus[EditTextType.ANCHOR]
        if (toggleStatus[ToggleType.AUTO_ATTACH_ANCHOR] == true && !anchorSourceType.isNullOrEmpty()) {
            anchor = Anchor(sourceType = ShareUtils.parseAnchorSourceType(anchorSourceType))
        }
        var shareExtra: String? = null
        textStatus[EditTextType.EXTRA]?.let {
            try {
                shareExtra = JSONObject(ShareUtils.parseJSON(it)).toString()
            } catch (_: Exception) { }
        }

        return Share.Request(
            mediaContent = content,
            hashTagList = parsedHashTags,
            shareFormat = shareFormat,
            anchor = anchor,
            shareExtra = shareExtra,
            extraShareOptions = extraShareOptionsMap,
            resultActivityComponent = resultActivityComponent,
        )
    }

    fun updateText(type: EditTextType, text: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val textStatus = currentStateValue.textStatus.toMutableMap()
        textStatus[type]?.let {
            textStatus[type] = text
        }

        _shareViewState.value = currentStateValue.copy(textStatus = textStatus)
    }

    fun updateToggle(toggleType: ToggleType, isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val toggleStatus = currentStateValue.toggleStatus.toMutableMap()
        toggleStatus[toggleType]?.let {
            toggleStatus[toggleType] = isOn
        }

        _shareViewState.value = currentStateValue.copy(toggleStatus = toggleStatus)
    }
}
