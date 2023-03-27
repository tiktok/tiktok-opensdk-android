package com.bytedance.sdk.open.tiktok.auth

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_REQUEST
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.core.appcheck.ITikTokAppCheck
import com.bytedance.sdk.open.tiktok.core.appcheck.TikTokAppCheckFactory
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.constants.Keys.Base
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthApiInstrumentedTest {
    private val state = "state"
    private val clientKey = "clientKey"
    private val scope = "scope1,scope2"
    private val language = "language"
    private val redirectUri = "demoapp://oauth_response"
    private val apiEventHandler = object : AuthApiEventHandler {
        override fun onRequest(req: Auth.Request) = Unit
        override fun onResponse(resp: Auth.Response) = Unit
    }
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
            get() = "AuthActivity"
        override val signature: String
            get() = "adgfdsgsg"
    }

    private fun createTestAuthRequest(): Auth.Request {
        return Auth.Request(
            scope = scope,
            state = state,
            language = language,
            redirectUri = redirectUri,
        )
    }

    @Test
    fun testAuthToBundle() {
        val request = createTestAuthRequest()
        val bundle = request.toBundle(clientKey)

        verifyBundle(bundle)
    }

    private fun verifyBundle(bundle: Bundle) {
        assertEquals(bundle.getInt(Base.TYPE), AUTH_REQUEST)
        assertEquals(bundle.getString(Keys.Auth.STATE), state)
        assertEquals(bundle.getString(Keys.Auth.CLIENT_KEY), clientKey)
        assertEquals(bundle.getString(Keys.Auth.SCOPE), scope)
        assertEquals(bundle.getString(Keys.Auth.LANGUAGE), language)
        assertEquals(bundle.getString(Keys.Auth.REDIRECT_URI), redirectUri)
    }

    @Test
    fun testAuthWithNativeFlow() {
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivity(allAny())
        } returns Unit
        val authApi = AuthApi(mockActivity, clientKey, apiEventHandler)
        mockkObject(TikTokAppCheckFactory)
        every { TikTokAppCheckFactory.getApiCheck(mockActivity, Constants.APIType.AUTH) }.returns(appCheck)
        val request = createTestAuthRequest()
        authApi.authorize(
            request,
            authMethod = AuthApi.AuthMethod.TikTokApp
        )
        verify(exactly = 1) {
            mockActivity.startActivityForResult(allAny(), 0)
        }
    }
}
