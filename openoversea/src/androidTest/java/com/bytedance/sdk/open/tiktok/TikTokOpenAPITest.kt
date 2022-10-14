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
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.WebAuthActivity
import com.bytedance.sdk.open.tiktok.common.constants.Constants.TIKTOK.AUTH_RESPONSE
import com.bytedance.sdk.open.tiktok.common.constants.Constants.TIKTOK.SHARE_RESPONSE
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl.Companion.INVALID_TYPE_VALUE
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareService
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TikTokOpenAPITest {
    @MockK
    lateinit var intent: Intent
    @MockK
    lateinit var context: Context

    @MockK
    lateinit var authService: AuthService

    @MockK
    lateinit var shareService: ShareService

    private val eventHandler = spyk<IApiEventHandler>(object : IApiEventHandler {
        override fun onRequest(req: Base.Request) {}
        override fun onResponse(resp: Base.Response) {}
        override fun onErrorIntent(intent: Intent?) {}
    })

    private lateinit var tikTokOpenApiImpl: TikTokOpenApiImpl

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        intent = Intent(context, WebAuthActivity::class.java)
        authService = AuthService(context, "client_key")
        shareService = ShareService(context, "client_key")
        tikTokOpenApiImpl = TikTokOpenApiImpl(context, authService, shareService, eventHandler)
    }

    @Test
    fun testHandleIntentWhenIntentIsNull() {
        assertFalse(tikTokOpenApiImpl.handleIntent(null))
    }

    @Test
    fun testHandleIntentWhenBundleIsNull() {
        intent.replaceExtras(null)
        assertFalse(tikTokOpenApiImpl.handleIntent(intent))
    }

    @Test
    fun testHandleAuthResponseIntent() {
        intent.putExtra(Keys.Base.TYPE, AUTH_RESPONSE)
        assertTrue(tikTokOpenApiImpl.handleIntent(intent))
        verify(exactly = 1) {
            eventHandler.onResponse(any<Auth.Response>())
        }
    }

    @Test
    fun testHandleShareResponseIntent() {
        intent.putExtra(Keys.Base.TYPE, SHARE_RESPONSE)
        assertTrue(tikTokOpenApiImpl.handleIntent(intent))
        verify(exactly = 1) {
            eventHandler.onResponse(any<Share.Response>())
        }
    }

    @Test
    fun testHandleShareResponseFromShareTypeKeyIntent() {
        intent.putExtra(Keys.Base.TYPE, INVALID_TYPE_VALUE)
        intent.putExtra(Keys.Share.TYPE, SHARE_RESPONSE)
        assertTrue(tikTokOpenApiImpl.handleIntent(intent))
        verify(exactly = 1) {
            eventHandler.onResponse(any<Share.Response>())
        }
    }

    @Test
    fun testHandleInvalidResponseTypeFromBaseKeyIntent() {
        intent.putExtra(Keys.Base.TYPE, 100)
        assertFalse(tikTokOpenApiImpl.handleIntent(intent))
    }

    @Test
    fun testHandleInvalidResponseTypeFromShareKeyIntent() {
        intent.putExtra(Keys.Base.TYPE, INVALID_TYPE_VALUE)
        intent.putExtra(Keys.Share.TYPE, 100)
        assertFalse(tikTokOpenApiImpl.handleIntent(intent))
    }
}
