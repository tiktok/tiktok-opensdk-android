package com.bytedance.sdk.open.tiktok

data class TikTokOpenConfig(
    val clientKey: String,
    val callerPackageName: String? = null,
    val callerVersion: String? = null,
)
