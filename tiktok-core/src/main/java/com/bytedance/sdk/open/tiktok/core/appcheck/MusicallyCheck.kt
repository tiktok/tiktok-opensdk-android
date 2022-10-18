package com.bytedance.sdk.open.tiktok.core.appcheck

import android.content.Context

internal class MusicallyCheck(override val context: Context) : AppCheckBase(context) {
    override val signature: String = "194326e82c84a639a52e5c023116f12a"
    override val appPackageName: String = "com.zhiliaoapp.musically"
}
