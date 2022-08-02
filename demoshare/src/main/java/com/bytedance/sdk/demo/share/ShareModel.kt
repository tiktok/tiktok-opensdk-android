package com.bytedance.sdk.demo.share

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShareModel(var bundleID: String = "",
                      var clientKey: String = "",
                      var clientSecret: String = "",
                      var media: List<String> = arrayListOf()): Parcelable {
}