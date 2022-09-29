package com.bytedance.sdk.open.tiktok

import android.content.Context
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareService
import com.bytedance.sdk.open.tiktok.utils.AppUtils
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShareInstrumentedTest {
    private val clientKey = "clientKey"
    private val hashtag1 = "hashtag1"
    private val hashtag2 = "hashtag2"
    private val callerLocalEntry = "tiktok.MainActivity"
    private val callerPackage = "caller_package"
    private val state = "ready"
    private val shareExtra = "share_extra"
    private val extraShareOptions: HashMap<String, Any> = hashMapOf(Pair("option1", "value1"), Pair("option2", "value2"))
    private val mediaList = arrayListOf("media_url1", "media_url2")
    private val anchor = Anchor().apply {
        anchorBusinessType = 10
        anchorTitle = "title"
        anchorContent = "anchor_content"
        sourceType = "anchor_source_type"
    }

    private fun createTestShareRequest(localEntry: String? = null): Share.Request {
        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaList)
        return Share.Request(
            mediaContent = mediaContent,
            shareFormat = Share.Format.GREEN_SCREEN,
            hashTagList = arrayListOf(hashtag1, hashtag2),
            state = state,
            anchor = anchor,
            targetSceneType = Keys.Scene.LANDPAGE_SCENE_CUT,
            shareExtra = shareExtra,
            extraShareOptions = extraShareOptions,
            callerLocalEntry = localEntry
        )
    }

    @Test
    fun testShareRequestWithCustomEntry() {
        val shareRequest = createTestShareRequest(localEntry = callerLocalEntry)

        val bundle = shareRequest.toBundle(clientKey, callerPackage)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), AppUtils.componentClassName(callerPackage, callerLocalEntry))
        assertEquals(bundle.getString(Keys.Base.FROM_ENTRY), AppUtils.componentClassName(callerPackage, callerLocalEntry))
        verifyBundle(bundle)
    }

    @Test
    fun testShareRequestWithDefaultEntry() {
        val shareRequest = createTestShareRequest(localEntry = null)

        val bundle = shareRequest.toBundle(clientKey, callerPackage)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), AppUtils.componentClassName(callerPackage, BuildConfig.DEFAULT_ENTRY_ACTIVITY))
        assertEquals(bundle.getString(Keys.Base.FROM_ENTRY), AppUtils.componentClassName(callerPackage, BuildConfig.DEFAULT_ENTRY_ACTIVITY))
        verifyBundle(bundle)
    }

    private fun verifyBundle(bundle: Bundle) {
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST), arrayListOf(hashtag1, hashtag2))
        assertEquals(bundle.getString(Keys.Share.SHARE_DEFAULT_HASHTAG), hashtag1)
        assertEquals(bundle.getString(Keys.Share.CALLER_PKG), callerPackage)
        assertEquals(bundle.getString(Keys.Share.STATE), state)
        assertEquals(bundle.getString(Keys.Share.OPENPLATFORM_EXTRA), shareExtra)
        assertEquals(bundle.getSerializable(Keys.Share.EXTRA_SHARE_OPTIONS), extraShareOptions)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaList)
        val parsedAnchor = Gson().fromJson(bundle.getString(Keys.Share.SHARE_ANCHOR_INFO), Anchor::class.java)
        assertEquals(parsedAnchor, anchor)
        assertEquals(bundle.getInt(Keys.Share.SHARE_TARGET_SCENE), Keys.Scene.LANDPAGE_SCENE_CUT)
    }

    @Test
    fun testShareService() {
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareService = ShareService(mockContext, "client_key")
        val request = createTestShareRequest()
        val entryComponent = EntryComponent("defaultComponent", "tiktokPackage", "tiktokComponent", "tiktokPlatformComponent")
        shareService.share(request, entryComponent)
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }
}
