package com.bytedance.sdk.open.tiktok.share

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base

class ShareKt {
    enum class Format(val format: Int) {
        DEFAULT(0),
        GREEN_SCREEN(1),
    }
    enum class MediaType(val type: Int) {
        VIDEO(0),
        IMAGE(1),
    }
    class Request(): Base.Request() {
        override var type: Int = Constants.TIKTOK.SHARE_REQUEST
        var targetSceneType: Int = 0
        var hashTagList: List<String>? = null

        override fun validate(): Boolean {
            TODO("Not yet implemented")
        }

        override fun toBundle(): Bundle {
            val bundle = super.toBundle()
            return bundle.apply {
            }
        }
        override fun fromBundle(bundle: Bundle) {
            super.fromBundle(bundle)

//            this.extras = bundle.getBundle(Keys.Base.EXTRA)
        }
    }
}