package com.bytedance.sdk.open.tiktok

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.Share
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareTest {

    @Test
    fun testShare() {
        val shareRequest = Share.Request()
        shareRequest.shareFormat = Share.Format.GREEN_SCREEN
        val bundle = shareRequest.toBundle()
        assert(bundle.getInt(Keys.Share.SHARE_FORMAT) == Share.Format.GREEN_SCREEN.format)
    }
}