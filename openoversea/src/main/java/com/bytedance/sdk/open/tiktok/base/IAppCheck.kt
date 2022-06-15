package com.bytedance.sdk.open.tiktok.base

interface IAppCheck {
    val remoteAuthEntryActivity: String

    val isAuthSupported: Boolean

    val isShareSupported: Boolean

    val isAppInstalled: Boolean

    val isShareFileProviderSupported: Boolean

    val packageName: String

    fun isAppSupportAPI(requiredApi: Int): Boolean
}