package com.bytedance.sdk.open.tiktok.helper

import android.content.Context
import com.bytedance.sdk.open.tiktok.base.AppCheckBase

class TikTokCheck(override val context: Context): AppCheckBase(context) {
    override val signature: String = "aea615ab910015038f73c47e45d21466"
    override val packageName: String = "com.ss.android.ugc.trill"
}