package com.bytedance.sdk.open.tiktok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler
import com.bytedance.sdk.open.tiktok.share.ShareService
import com.google.gson.Gson
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareInstrumentedTest {
    val hashtag1 = "hashtag1"
    val hashtag2 = "hashtag2"
    val callerLocalEntry = "com.tiktok.MainActivity"
    val callerPackage = "caller_package"
    val state = "ready"
    val shareExtra = "share_extra"
    val extraShareOption: HashMap<String, Any> = hashMapOf(Pair("option1", "value1"), Pair("option2", "value2"))
    val mediaList = arrayListOf("media_url1", "media_url2")

    private fun createTestAnchor(): Anchor {
        return Anchor().apply {
            anchorBusinessType = 10
            anchorTitle = "title"
            anchorContent = "anchor_content"
            sourceType = "anchor_source_type"
        }
    }

    private fun createTestShareRequest(): Share.Request {
        val anchor = createTestAnchor()
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaList)
        val shareRequest = Share.Request()
        shareRequest.callerLocalEntry = callerLocalEntry
        shareRequest.callerPackage = callerPackage
        shareRequest.shareFormat = Share.Format.GREEN_SCREEN
        shareRequest.hashTagList = arrayListOf(hashtag1, hashtag2)
        shareRequest.state = state
        shareRequest.anchor = anchor
        shareRequest.targetSceneType = Keys.Scene.LANDPAGE_SCENE_CUT
        shareRequest.shareExtra = shareExtra
        shareRequest.extraShareOptions = extraShareOption
        shareRequest.mediaContent = mediaContent
        return shareRequest
    }

    private fun createTestRequestBundle(): Bundle {
        return createTestShareRequest().toBundle()
    }

    private fun createTestResponse(): Share.Response {
        return Share.Response().apply {
            errorCode = 1002
            errorMsg = "mock error message"
            state = "error"
        }
    }

    @Test
    fun testShare() {
        val shareRequest = createTestShareRequest()

        val bundle = shareRequest.toBundle()
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST), arrayListOf(hashtag1, hashtag2))
        assertEquals(bundle.getString(Keys.Share.SHARE_DEFAULT_HASHTAG), hashtag1)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), callerLocalEntry)
        assertEquals(bundle.getString(Keys.Share.CALLER_PKG), callerPackage)
        assertEquals(bundle.getString(Keys.Share.STATE), state)
        assertEquals(bundle.getString(Keys.Share.OPENPLATFORM_EXTRA), shareExtra)
        assertEquals(bundle.getSerializable(Keys.Share.EXTRA_SHARE_OPTIONS), extraShareOption)
        assertEquals(bundle.get(Keys.VIDEO_PATH), mediaList)
        val parsedAnchor = Gson().fromJson(bundle.getString(Keys.Share.SHARE_ANCHOR_INFO), Anchor::class.java)
        assertEquals(parsedAnchor, shareRequest.anchor)
        assertEquals(bundle.getInt(Keys.Share.SHARE_TARGET_SCENE), Keys.Scene.LANDPAGE_SCENE_CUT)
    }

    @Test
    fun testShareDataHandler() {
        val handler = ShareDataHandler()
        val bundle = createTestRequestBundle()
        val eventHandler = spyk<IApiEventHandler>(object: IApiEventHandler {
            override fun onReq(req: Base.Request?) {}
            override fun onResp(resp: Base.Response?) {}
            override fun onErrorIntent(intent: Intent?) {}
        })
        handler.handle(Constants.TIKTOK.SHARE_REQUEST, bundle, eventHandler)
        verify(exactly = 1) {
            eventHandler.onReq(allAny())
        }
        handler.handle(Constants.TIKTOK.SHARE_RESPONSE, createTestResponse().toBundle(), eventHandler)
        verify(exactly = 1) {
            eventHandler.onResp(allAny())
        }
    }

    @Test
    fun testShareService() {
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareService = ShareService(mockContext, "client_key")
        val request = createTestShareRequest()
        val entryComponent = EntryComponent("defaultComponent", "tiktokPackage", "tiktokComponent",  "tiktokPlatformComponent")
        shareService.share(request, entryComponent)
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }
}