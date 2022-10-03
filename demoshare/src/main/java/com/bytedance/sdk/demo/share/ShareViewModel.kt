package com.bytedance.sdk.demo.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.TextType
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent

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

    private val _shareViewState: MutableLiveData<ShareViewModelViewState> = MutableLiveData(
        ShareViewModelViewState()
    )
    val shareViewState: LiveData<ShareViewModelViewState> = _shareViewState

    data class ShareViewModelViewState(
        val isAnchor: Boolean = false,
        val isMusic: Boolean = false,
        val isGreenScreen: Boolean = false,
        val anchorContent: String = "",
        val extraContent: String = "",
        var isImage: Boolean = false,
        var media: List<String> = arrayListOf(),
        val textStatus: LinkedHashMap<TextType, EditModel> = linkedMapOf(
            TextType.HASHTAG to EditModel(TextType.HASHTAG, R.string.demo_app_hashtag_info, R.string.demo_app_hashtag_desc),
            TextType.ANCHOR to EditModel(TextType.ANCHOR, R.string.demo_app_anchor_info, R.string.demo_app_anchor_desc),
            TextType.EXTRA to EditModel(TextType.EXTRA, R.string.demo_app_extra_info, R.string.demo_app_extra_desc),
        )
    )

    fun publish(resultActivityComponent: ResultActivityComponent) {
        composeShareModel()
        val request = shareModel.toShareRequest(resultActivityComponent)

        if (request != null) {
            tikTokOpenApi.share(request)
        }
    }

    private fun composeShareModel() {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        shareModel.hashtags = currentStateValue.textStatus[TextType.HASHTAG]?.let {
            ShareUtils.parseHashtags(it.text)
        }
        shareModel.disableMusicSelection = currentStateValue.isMusic
        shareModel.greenScreenFormat = currentStateValue.isGreenScreen
        shareModel.autoAttachAnchor = currentStateValue.isAnchor

        shareModel.anchorSourceType = currentStateValue.anchorContent?.let {
            ShareUtils.parseAnchorSourceType(it)
        }
        shareModel.shareExtra = ShareUtils.parseJSON(currentStateValue.extraContent)
    }

    fun updateText(type: TextType, text: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val textStatus = currentStateValue.textStatus
        textStatus[type]?.let {
            textStatus[type] = it.copy(text = text)
        }

        _shareViewState.value = currentStateValue.copy(textStatus = textStatus)
    }

    fun updateAnchorToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(isAnchor = isOn)
    }

    fun updateMusicToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(isMusic = isOn)
    }

    fun updateGreenToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        _shareViewState.value = currentStateValue.copy(isGreenScreen = isOn)
    }
}
