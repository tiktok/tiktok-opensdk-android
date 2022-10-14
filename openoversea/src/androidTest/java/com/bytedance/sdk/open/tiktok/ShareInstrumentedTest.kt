package com.bytedance.sdk.open.tiktok

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.common.model.ResultActivityComponent
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
    private val resultActivityPackage = "com.bytedance"
    private val resultActivityClass = "resultActivity"
    private val state = "ready"
    private val shareExtra = "share_extra"
    private val extraShareOptions: HashMap<String, Any> = hashMapOf(Pair("option1", "value1"), Pair("option2", "value2"))
    private val mediaList = arrayListOf("media_url1", "media_url2")
    private val anchor = Anchor(anchorBusinessType = 10, anchorTitle = "title", anchorContent = "anchor_content", sourceType = "anchor_source_type")

    private fun createTestShareRequest(): Share.Request {
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
            resultActivityComponent = ResultActivityComponent(resultActivityPackage, resultActivityClass)
        )
    }

    @Test
    fun testShareRequestToBundle() {
        val shareRequest = createTestShareRequest()

        val bundle = shareRequest.toBundle(clientKey)
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST), arrayListOf(hashtag1, hashtag2))
        assertEquals(bundle.getString(Keys.Share.SHARE_DEFAULT_HASHTAG), hashtag1)
        assertEquals(bundle.getString(Keys.Share.CALLER_PKG), resultActivityPackage)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), AppUtils.componentClassName(resultActivityPackage, resultActivityClass))
        assertEquals(bundle.getString(Keys.Base.FROM_ENTRY), AppUtils.componentClassName(resultActivityPackage, resultActivityClass))
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
        val entryComponent = EntryComponent("tiktokPackage", "tiktokComponent", "tiktokPlatformComponent")
        shareService.share(request, entryComponent)
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }
}
