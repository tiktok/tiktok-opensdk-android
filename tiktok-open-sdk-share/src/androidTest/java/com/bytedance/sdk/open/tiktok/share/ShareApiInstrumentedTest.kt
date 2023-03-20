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
    private val mediaSingleList = arrayListOf("media_url1")
    private val mediaMultiList = arrayListOf("media_url1", "media_url2")

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

    private fun createTestSingleDefaultShareRequest(): Share.Request {
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaSingleList)
        return Share.Request(
            mediaContent = mediaContent,
            shareFormat = Share.Format.DEFAULT,
        )
    }

    private fun createTestSingleGreenScreenShareRequest(): Share.Request {
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaSingleList)
        return Share.Request(
            mediaContent = mediaContent,
            shareFormat = Share.Format.GREEN_SCREEN,
        )
    }

    private fun createTestMultiDefaultShareRequest(): Share.Request {
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaMultiList)
        return Share.Request(
            mediaContent = mediaContent,
            shareFormat = Share.Format.DEFAULT,
        )
    }

    private fun createTestMultiGreenScreenShareRequest(): Share.Request {
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaMultiList)
        return Share.Request(
            mediaContent = mediaContent,
            shareFormat = Share.Format.GREEN_SCREEN,
        )
    }

    @Test
    fun testShareSingleDefaultRequestToBundle() {
        val shareSingleDefaultRequest = createTestSingleDefaultShareRequest()

        val bundle = shareSingleDefaultRequest.toBundle(clientKey)
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.DEFAULT.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaSingleList)
    }

    @Test
    fun testShareSingleGreenScreenRequestToBundle() {
        val shareSingleGreenScreenRequest = createTestSingleGreenScreenShareRequest()

        val bundle = shareSingleGreenScreenRequest.toBundle(clientKey)
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaSingleList)
    }

    @Test
    fun testShareMultiDefaultRequestToBundle() {
        val shareShareMultiDefaultRequest = createTestMultiDefaultShareRequest()

        val bundle = shareShareMultiDefaultRequest.toBundle(clientKey)
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.DEFAULT.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaMultiList)
    }

    @Test
    fun testShareMultiGreenScreenRequestToBundle() {
        val shareMultiGreenScreenRequest = createTestMultiGreenScreenShareRequest()

        val bundle = shareMultiGreenScreenRequest.toBundle(clientKey)
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaMultiList)
    }

    @Test
    fun testSendingSingleDefaultShareRequest() {
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

        shareApi.share(createTestSingleDefaultShareRequest())
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }

    @Test
    fun testSendingSingleGreenScreenShareRequest() {
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

        shareApi.share(createTestSingleGreenScreenShareRequest())
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }

    @Test
    fun testSendingMultiDefaultShareRequest() {
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

        shareApi.share(createTestMultiDefaultShareRequest())
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }

    @Test
    fun testSendingMultiGreenScreenShareRequest() {
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

        shareApi.share(createTestMultiGreenScreenShareRequest())
        verify(exactly = 0) {
            mockContext.startActivity(allAny())
        }
    }
}
