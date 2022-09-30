package com.bytedance.sdk.demo.share.tiktokapi

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
    private val tikTokOpenApi: TikTokOpenApi
) : ViewModel() {

    @SuppressWarnings("unchecked")
    class Factory(val tikTokOpenApi: TikTokOpenApi) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShareViewModel(tikTokOpenApi) as T
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
        val request = toShareRequest(callerLocalEntry)
        tikTokOpenApi.share(request)
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