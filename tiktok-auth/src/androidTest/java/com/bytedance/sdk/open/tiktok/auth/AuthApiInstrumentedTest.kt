package com.bytedance.sdk.open.tiktok.auth

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
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.AUTH_REQUEST
import com.bytedance.sdk.open.tiktok.auth.constants.Keys
import com.bytedance.sdk.open.tiktok.core.appcheck.AppCheckFactory
import com.bytedance.sdk.open.tiktok.core.appcheck.IAppCheck
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
    private val optionalScope0 = "optionalScope0"
    private val optionalScope1 = "optionalScope1"
    private val language = "language"
    private val packageName = "com.bytedance"
    private val resultActivityFullPath = "com.bytedance.auth.resultActivity"
    private val apiEventHandler = object : AuthApiEventHandler {
        override fun onRequest(req: Auth.Request) = Unit
        override fun onResponse(resp: Auth.Response) = Unit
    }
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
            get() = "AuthActivity"
        override val signature: String
            get() = "adgfdsgsg"
    }

    private fun createTestAuthRequest(): Auth.Request {
        return Auth.Request(
            scope = scope,
            state = state,
            optionalScope0 = optionalScope0,
            optionalScope1 = optionalScope1,
            language = language,
            packageName = packageName,
            resultActivityFullPath = resultActivityFullPath
        )
    }

    @Test
    fun testAuthToBundle() {
        val request = createTestAuthRequest()
        val bundle = request.toBundle(clientKey, "", "")

        verifyBundle(bundle)
    }

    private fun verifyBundle(bundle: Bundle) {
        assertEquals(bundle.getInt(Base.TYPE), AUTH_REQUEST)
        assertEquals(bundle.getString(Keys.Auth.STATE), state)
        assertEquals(bundle.getString(Keys.Auth.CLIENT_KEY), clientKey)
        assertEquals(bundle.getString(Keys.Auth.SCOPE), scope)
        assertEquals(bundle.getString(Keys.Auth.OPTIONAL_SCOPE0), optionalScope0)
        assertEquals(bundle.getString(Keys.Auth.OPTIONAL_SCOPE1), optionalScope1)
        assertEquals(bundle.getString(Keys.Auth.LANGUAGE), language)
        assertEquals(bundle.getString(Base.CALLER_PKG), packageName)
        assertEquals(bundle.getString(Base.FROM_ENTRY), resultActivityFullPath)
    }

    @Test
    fun testAuthWithNativeFlow() {
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val authApi = AuthApi(mockContext, clientKey, apiEventHandler)
        mockkObject(AppCheckFactory)
        every { AppCheckFactory.getApiCheck(mockContext, Constants.APIType.SHARE) }.returns(appCheck)
        val request = createTestAuthRequest()
        authApi.authorize(
            request,
            useWebAuth = false
        )
        verify(exactly = 1) {
            mockContext.startActivity(allAny())
        }
    }
}
