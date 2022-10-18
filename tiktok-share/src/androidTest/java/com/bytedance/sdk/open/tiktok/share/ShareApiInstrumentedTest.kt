package com.bytedance.sdk.open.tiktok.share

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
import com.bytedance.sdk.open.tiktok.core.appcheck.AppCheckFactory
import com.bytedance.sdk.open.tiktok.core.appcheck.IAppCheck
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base
import com.bytedance.sdk.open.tiktok.share.constants.Keys
import com.bytedance.sdk.open.tiktok.share.model.Anchor
import com.bytedance.sdk.open.tiktok.share.model.MediaContent
import com.google.gson.Gson
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
    private val hashtag1 = "hashtag1"
    private val hashtag2 = "hashtag2"
    private val packageName = "com.bytedance"
    private val resultActivityFullPath = "com.bytedance.share.resultActivity"
    private val state = "ready"
    private val shareExtra = "share_extra"
    private val extraShareOptions: HashMap<String, Any> = hashMapOf(Pair("option1", "value1"), Pair("option2", "value2"))
    private val mediaList = arrayListOf("media_url1", "media_url2")
    private val anchor = Anchor(anchorBusinessType = 10, anchorTitle = "title", anchorContent = "anchor_content", sourceType = "anchor_source_type")
    private val appCheck = object : IAppCheck {
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
            hashTagList = arrayListOf(hashtag1, hashtag2),
            state = state,
            anchor = anchor,
            shareExtra = shareExtra,
            extraShareOptions = extraShareOptions,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath
        )
    }

    @Test
    fun testShareRequestToBundle() {
        val shareRequest = createTestShareRequest()

        val bundle = shareRequest.toBundle(clientKey, "", "")
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST), arrayListOf(hashtag1, hashtag2))
        assertEquals(bundle.getString(Keys.Share.SHARE_DEFAULT_HASHTAG), hashtag1)
        assertEquals(bundle.getString(Keys.Share.CALLER_PKG), packageName)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), resultActivityFullPath)
        assertEquals(bundle.getString(Base.FROM_ENTRY), resultActivityFullPath)
        assertEquals(bundle.getString(Keys.Share.STATE), state)
        assertEquals(bundle.getString(Keys.Share.OPEN_PLATFORM_EXTRA), shareExtra)
        assertEquals(bundle.getSerializable(Keys.Share.EXTRA_SHARE_OPTIONS), extraShareOptions)
        assertEquals(bundle.getStringArrayList(Keys.VIDEO_PATH), mediaList)
        val parsedAnchor = Gson().fromJson(bundle.getString(Keys.Share.SHARE_ANCHOR_INFO), Anchor::class.java)
        assertEquals(parsedAnchor, anchor)
        assertEquals(bundle.getInt(Keys.Share.SHARE_TARGET_SCENE), 0)
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
        mockkObject(AppCheckFactory)
        every { AppCheckFactory.getApiCheck(mockContext, Constants.APIType.SHARE) }.returns(appCheck)
        val request = createTestShareRequest()
        shareApi.share(request)
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }
}
