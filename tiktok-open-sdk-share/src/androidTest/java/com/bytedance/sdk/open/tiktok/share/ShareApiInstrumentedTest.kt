package com.bytedance.sdk.open.tiktok.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.core.appcheck.ITikTokAppCheck
import com.bytedance.sdk.open.tiktok.core.appcheck.TikTokAppCheckFactory
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base
import com.bytedance.sdk.open.tiktok.share.constants.Keys
import com.bytedance.sdk.open.tiktok.share.model.MediaContent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareApiInstrumentedTest {
    private val clientKey = "clientKey"
    private val packageName = "com.bytedance"
    private val resultActivityFullPath = "com.bytedance.share.resultActivity"
    private val mediaList = arrayListOf("media_url1", "media_url2")
    private val appCheck = object : ITikTokAppCheck {
        override val isAuthSupported: Boolean
            get() = true
        override val isShareSupported: Boolean
            get() = true
        override val isAppInstalled: Boolean
            get() = true
        override val isShareFileProviderSupported: Boolean
            get() = true
        override val appPackageName: String
            get() = "com.tiktok"
        override val sharePackageName: String
            get() = "SystemShareActivty"
        override val signature: String
            get() = "adgfdsgsg"
    }

    private fun createTestShareRequest(): Share.Request {
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaList)
        return Share.Request(
            mediaContent = mediaContent,
            shareFormat = Share.Format.GREEN_SCREEN,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath
        )
    }

    @Test
    fun testShareRequestToBundle() {
        val shareRequest = createTestShareRequest()

        val bundle = shareRequest.toBundle(clientKey, "", "")
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getString(Keys.Share.CALLER_PKG), packageName)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), resultActivityFullPath)
        assertEquals(bundle.getString(Base.FROM_ENTRY), resultActivityFullPath)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaList)
    }

    @Test
    fun testSendingShareRequest() {
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareApi = ShareApi(
            mockContext, "client_key",
            object : ShareApiEventHandler {
                override fun onRequest(req: Share.Request) = Unit
                override fun onResponse(resp: Share.Response) = Unit
            }
        )
        mockkObject(TikTokAppCheckFactory)
        every { TikTokAppCheckFactory.getApiCheck(mockContext, Constants.APIType.SHARE) }.returns(appCheck)
        val request = createTestShareRequest()
        shareApi.share(request)
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }
}
