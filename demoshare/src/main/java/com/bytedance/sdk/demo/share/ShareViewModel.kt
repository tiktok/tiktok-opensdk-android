package com.bytedance.sdk.demo.share.tiktokapi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.sdk.demo.share.ShareModel
import com.bytedance.sdk.demo.share.ShareUtils
import com.bytedance.sdk.demo.share.toShareRequest
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi

class ShareViewModel(
    private val tikTokOpenApi: TikTokOpenApi,
    private val shareModel: ShareModel
) : ViewModel() {

    @SuppressWarnings("unchecked")
    class Factory(val tikTokOpenApi: TikTokOpenApi, val shareModel: ShareModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShareViewModel(tikTokOpenApi, shareModel) as T
        }
    }

    private val _shareViewState: MutableLiveData<ShareViewModelViewState> = MutableLiveData(ShareViewModelViewState())
    val shareViewState: LiveData<ShareViewModelViewState> = _shareViewState

    data class ShareViewModelViewState(
        val musicSelection: Boolean = false,
        val greenScreen: Boolean = false,
        val isAnchor: Boolean = false,
        val anchorContent: String = "",
        val anchorExtraEnabled: Boolean = false,
        val hashtagContent: String = "",
        val extraContent: String = "",
        val anchorSourceTypeEnabled: Boolean = false,
        val autoAttachAnchor: Boolean = false,
        val anchorSourceType: String? = null,
        var isImage: Boolean = false,
        var media: List<String> = arrayListOf(),
    )

    fun publish(callerLocalEntry: String) {
        composeShareModel()
        val request = shareModel.toShareRequest(callerLocalEntry)
        if (request != null) {
            tikTokOpenApi.share(request)
        }
    }

    private fun composeShareModel() {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        shareModel.hashtags = ShareUtils.parseHashtags(currentStateValue.hashtagContent)
        shareModel.disableMusicSelection = currentStateValue.musicSelection
        shareModel.greenScreenFormat = currentStateValue.greenScreen
        shareModel.autoAttachAnchor = currentStateValue.autoAttachAnchor

        shareModel.anchorSourceType = currentStateValue.anchorSourceType?.let {
            ShareUtils.parseAnchorSourceType(
                it
            )
        }
        shareModel.shareExtra = ShareUtils.parseJSON(currentStateValue.extraContent)
    }

    fun updateHashtag(hashtags: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(hashtagContent = hashtags)
    }

    fun updateMusicToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(musicSelection = isOn)
    }

    fun updateGreenToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(greenScreen = isOn)
    }

    fun updateAnchorToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(autoAttachAnchor = isOn)
    }

    fun updateAnchorText(extra: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(anchorContent = extra)
    }

    fun updateExtraText(extra: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(extraContent = extra)
    }
}
