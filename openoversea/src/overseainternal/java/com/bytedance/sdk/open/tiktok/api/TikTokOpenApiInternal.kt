package com.bytedance.sdk.open.tiktok.api

interface TikTokOpenApiInternal : TikTokOpenApi, TikTokOpenApiBeta

interface TikTokOpenApiBeta {
    val isTikTokLiteAuthSupported: Boolean
}
