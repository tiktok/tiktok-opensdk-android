package com.bytedance.sdk.open.tiktok.core.appcheck

import android.content.Context

internal class TikTokCheck(override val context: Context) : AppCheckBase(context) {
    override val signature: String = "aea615ab910015038f73c47e45d21466"
    override val appPackageName: String = "com.ss.android.ugc.trill"
}
