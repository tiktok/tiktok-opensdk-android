package com.bytedance.sdk.demo.share.tiktokapi

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytedance.sdk.demo.share.ShareModel
import com.bytedance.sdk.demo.share.ShareUtils
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.Share
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

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

    private val _viewEffect: Channel<ViewEffect> = Channel()
    val viewEffectFlow: Flow<ViewEffect> = _viewEffect.receiveAsFlow()

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

    sealed class ViewEffect {
        data class ShowGeneralAlert(
            @StringRes
            val titleRes: Int,
            @StringRes
            val descriptionRes: Int,
        ) : ViewEffect()
    }

    private fun sendViewEffect(effect: ViewEffect) {
        viewModelScope.launch {
            _viewEffect.send(effect)
        }
    }

    fun publish(callerLocalEntry: String) {
        composeShareModel()
        val request = toShareRequest(callerLocalEntry)
        tikTokOpenApi.share(request)
    }

    private fun composeShareModel(): Boolean {
        val currentState = _shareViewState.value
        if (currentState != null) {
            shareModel.hashtags = ShareUtils.parseHashtags(currentState.hashtagContent)
        }
        if (currentState != null) {
            shareModel.disableMusicSelection = currentState.musicSelection
        }
        if (currentState != null) {
            shareModel.autoAttachAnchor = currentState.autoAttachAnchor
        } else {
            shareModel.autoAttachAnchor = false
        }
        val anchorSource = currentState?.anchorSourceType?.let { ShareUtils.parseAnchorSourceType(it) }
        if (shareModel.autoAttachAnchor) {
            shareModel.anchorSourceType = anchorSource
            val extra: Map<String, String>?
            if (currentState != null) {
                if (currentState.extraContent.isNotEmpty()) {
                    return try {
                        extra = ShareUtils.parseJSON(currentState.extraContent)
                        shareModel.shareExtra = extra
                        true
                    } catch (ex: Exception) {
                        // alert later
                        false
                    }
                } else {
                    shareModel.shareExtra = null
                }
            }
        }

        return true
    }

    fun updateHashtag(hashtags: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
    }

    fun updateMusicToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
    }

    fun updateGreenToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
    }

    fun updateAnchorToggle(isOn: Boolean) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
    }

    fun updateAnchorText(extra: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
    }

    fun updateExtraText(extra: String) {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
    }

    private fun toShareRequest(callerLocalEntry: String): Share.Request {
        val currentStateValue: ShareViewModelViewState = _shareViewState.value ?: ShareViewModelViewState()
        val mediaList = ArrayList<String>()
        for (m in currentStateValue.media) {
            mediaList.add(m)
        }
        val content = MediaContent(if (currentStateValue.isImage) Share.MediaType.IMAGE else Share.MediaType.VIDEO, mediaList)
        var request = Share.Request(mediaContent = content, callerLocalEntry = callerLocalEntry)
        val hashtags = ShareUtils.parseHashtags(currentStateValue.hashtagContent)
        hashtags?.let { validHashTags ->
            val mappedHashtags = ArrayList<String>()
            for (hashtag in validHashTags) {
                mappedHashtags.add(hashtag)
            }
            request = request.copy(hashTagList = mappedHashtags)
        }

        if (currentStateValue.musicSelection) {
            val options: HashMap<String, Any> = HashMap()
            options[Keys.Share.DISABLE_MUSIC_SELECTION] = 1
            request = request.copy(extraShareOptions = options)
        }

        if (currentStateValue.greenScreen) {
            request = request.copy(shareFormat = Share.Format.GREEN_SCREEN)
        }

        if (currentStateValue.autoAttachAnchor && !currentStateValue.anchorSourceType.isNullOrEmpty()) {
            val anchor = Anchor()
            anchor.sourceType = currentStateValue.anchorSourceType
            request = request.copy(anchor = anchor)
            currentStateValue.extraContent?.let {
                try {
                    request = request.copy(shareExtra = JSONObject(currentStateValue.extraContent).toString())
                } catch (_: Exception) { }
            }
        }

        return request
    }
}
