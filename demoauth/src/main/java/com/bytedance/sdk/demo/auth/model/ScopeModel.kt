package com.bytedance.sdk.demo.auth.model

import androidx.annotation.StringRes

data class ScopeModel(
    val type: ScopeType,
    @StringRes
    val descRes: Int,
    val isOn: Boolean,
    val isEnabled: Boolean,
    val isEditable: Boolean
) : DataModel {
    override val viewType = ViewType.SCOPE
}

enum class ScopeType(val value: String) {
    USER_INFO_BASIC("user.info.basic"),
    USER_INFO_USERNAME("user.info.username"),
    USER_INFO_PHONE("user.info.phone"),
    USER_INFO_EMAIL("user.info.email"),
    MUSIC_COLLECTION("music.collection"),
    VIDEO_UPLOAD("video.upload"),
    VIDEO_LIST("video.list"),
    USER_INTEREST("user.ue"),
}
