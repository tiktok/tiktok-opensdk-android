package com.tiktok.open.sdk.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tiktok.open.sdk.core.appcheck.ITikTokAppCheck
import com.tiktok.open.sdk.core.appcheck.TikTokAppCheckUtil
import com.tiktok.open.sdk.share.constants.Keys
import com.tiktok.open.sdk.share.model.MediaContent
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
    private val packageName = "com.tiktok"
    private val resultActivityFullPath = "com.tiktok.share.resultActivity"

    private val appCheck = object : ITikTokAppCheck {
        override fun isAppInstalled(): Boolean = true
        override val appPackageName: String
            get() = "com.tiktok"
        override val signature: String
            get() = "adgfdsgsg"
    }

    private fun createTestSingleDefaultShareRequest(): ShareRequest {
        val mediaContent = MediaContent(MediaType.VIDEO, mediaSingleList)
        return ShareRequest(
            clientKey,
            mediaContent = mediaContent,
            shareFormat = Format.DEFAULT,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath,
        )
    }

    private fun createTestSingleGreenScreenShareRequest(): ShareRequest {
        val mediaContent = MediaContent(MediaType.VIDEO, mediaSingleList)
        return ShareRequest(
            clientKey,
            mediaContent = mediaContent,
            shareFormat = Format.GREEN_SCREEN,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath,
        )
    }

    private fun createTestMultiDefaultShareRequest(): ShareRequest {
        val mediaContent = MediaContent(MediaType.VIDEO, mediaMultiList)
        return ShareRequest(
            clientKey,
            mediaContent = mediaContent,
            shareFormat = Format.DEFAULT,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath,
        )
    }

    private fun createTestMultiGreenScreenShareRequest(): ShareRequest {
        val mediaContent = MediaContent(MediaType.VIDEO, mediaMultiList)
        return ShareRequest(
            clientKey,
            mediaContent = mediaContent,
            shareFormat = Format.GREEN_SCREEN,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath,
        )
    }

    @Test
    fun testShareSingleDefaultRequestToBundle() {
        val shareSingleDefaultRequest = createTestSingleDefaultShareRequest()

        val bundle = shareSingleDefaultRequest.toBundle()
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Format.DEFAULT.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaSingleList)
    }

    @Test
    fun testShareSingleGreenScreenRequestToBundle() {
        val shareSingleGreenScreenRequest = createTestSingleGreenScreenShareRequest()

        val bundle = shareSingleGreenScreenRequest.toBundle()
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaSingleList)
    }

    @Test
    fun testShareMultiDefaultRequestToBundle() {
        val shareShareMultiDefaultRequest = createTestMultiDefaultShareRequest()

        val bundle = shareShareMultiDefaultRequest.toBundle()
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Format.DEFAULT.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaMultiList)
    }

    @Test
    fun testShareMultiGreenScreenRequestToBundle() {
        val shareMultiGreenScreenRequest = createTestMultiGreenScreenShareRequest()

        val bundle = shareMultiGreenScreenRequest.toBundle()
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaMultiList)
    }

    @Test
    fun testSendingSingleDefaultShareRequest() {
        val mockContext = mockk<Activity>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareApi = ShareApi(
            mockContext
        )
        mockkObject(TikTokAppCheckUtil)
        every { TikTokAppCheckUtil.getInstalledTikTokApp(mockContext) }.returns(appCheck)

        shareApi.share(createTestSingleDefaultShareRequest())
        verify(exactly = 1) {
            mockContext.startActivityForResult(allAny(), 0)
        }
    }

    @Test
    fun testSendingSingleGreenScreenShareRequest() {
        val mockContext = mockk<Activity>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareApi = ShareApi(
            mockContext
        )
        mockkObject(TikTokAppCheckUtil)
        every { TikTokAppCheckUtil.getInstalledTikTokApp(mockContext) }.returns(appCheck)

        shareApi.share(createTestSingleGreenScreenShareRequest())
        verify(exactly = 1) {
            mockContext.startActivityForResult(allAny(), 0)
        }
    }

    @Test
    fun testSendingMultiDefaultShareRequest() {
        val mockContext = mockk<Activity>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareApi = ShareApi(
            mockContext
        )
        mockkObject(TikTokAppCheckUtil)
        every { TikTokAppCheckUtil.getInstalledTikTokApp(mockContext) }.returns(appCheck)

        shareApi.share(createTestMultiDefaultShareRequest())
        verify(exactly = 1) {
            mockContext.startActivityForResult(allAny(), 0)
        }
    }

    @Test
    fun testSendingMultiImageGreenScreenShareRequestAndTheRequestShouldFail() {
        val mockContext = mockk<Activity>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareApi = ShareApi(
            mockContext
        )
        mockkObject(TikTokAppCheckUtil)
        every { TikTokAppCheckUtil.getInstalledTikTokApp(mockContext) }.returns(appCheck)

        shareApi.share(createTestMultiGreenScreenShareRequest())
        verify(exactly = 0) {
            mockContext.startActivityForResult(allAny(), 0)
        }
    }
}
