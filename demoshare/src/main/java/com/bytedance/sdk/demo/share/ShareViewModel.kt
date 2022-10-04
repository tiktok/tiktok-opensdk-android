package com.bytedance.sdk.demo.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytedance.sdk.demo.share.model.EditTextType
import com.bytedance.sdk.demo.share.model.ToggleType
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
        composeShareModel()
        val request = shareModel.toShareRequest(resultActivityComponent)
        tikTokOpenApi.share(request)
    }

    private fun composeShareModel() {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        shareModel.hashtags = currentStateValue.textStatus[EditTextType.HASHTAG]?.let {
            ShareUtils.parseHashtags(it)
        }
        shareModel.disableMusicSelection = currentStateValue.toggleStatus[ToggleType.DISABLE_MUSIC] ?: false
        shareModel.greenScreenFormat = currentStateValue.toggleStatus[ToggleType.GREEN_SCREEN] ?: false
        shareModel.autoAttachAnchor = currentStateValue.toggleStatus[ToggleType.AUTO_ATTACH_ANCHOR] ?: false

        shareModel.anchorSourceType = currentStateValue.textStatus[EditTextType.ANCHOR]?.let {
            ShareUtils.parseAnchorSourceType(it)
        }
        currentStateValue.textStatus[EditTextType.EXTRA]?.let {
            shareModel.shareExtra = ShareUtils.parseJSON(it)
        }
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
