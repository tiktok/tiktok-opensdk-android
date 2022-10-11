package com.bytedance.sdk.demo.auth.model

import androidx.annotation.StringRes
import com.bytedance.sdk.demo.auth.R

data class ScopeModel(
    val type: ScopeType,
    @StringRes
    val descRes: Int,
    val isOn: Boolean,
    val isEditable: Boolean
) : DataModel {
    override val viewType = ViewType.SCOPE
}

enum class ScopeType(val value: String, val descRes: Int) {
    USER_INFO_BASIC("user.info.basic", R.string.basic_scope_description),
    USER_INFO_EMAIL("user.info.email", R.string.email_scope_description),
    VIDEO_UPLOAD("video.upload", R.string.video_upload_scope_description),
    VIDEO_LIST("video.list", R.string.video_list_scope_description),
    USER_INFO_USERNAME("user.info.username", R.string.user_name_scope_description),
    USER_INFO_PHONE("user.info.phone", R.string.phone_scope_description),
    USER_INTEREST("user.ue", R.string.user_interest_scope_description),
    MUSIC_COLLECTION("music.collection", R.string.music_scope_description),
}

// remove in open source
val BETA_SCOPE = listOf<ScopeType>(
    ScopeType.USER_INFO_USERNAME,
    ScopeType.USER_INFO_PHONE,
    ScopeType.USER_INTEREST,
    ScopeType.MUSIC_COLLECTION,
)
