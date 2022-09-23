package com.bytedance.sdk.open.tiktok

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {
    private val state = "state"
    private val redirectUri = "redirectUri"
    private val clientKey = "clientKey"
    private val scope = "scope1,scope2"
    private val optionalScope0 = "optionalScope0"
    private val optionalScope1 = "optionalScope1"
    private val language = "language"
    private val callerPackage = "callerPackage"
    private val callerLocalEntry = "callerLocalEntry"
    private val callerVersion = "callerVersion"

    private fun createTestAuthRequest(): Auth.Request {
        val request = Auth.Request()
        request.state = state
        request.redirectUri = redirectUri
        request.clientKey = clientKey
        request.scope = scope
        request.optionalScope0 = optionalScope0
        request.optionalScope1 = optionalScope1
        request.language = language
        request.callerPackage = callerPackage
        request.callerLocalEntry = callerLocalEntry
        request.callerVersion = callerVersion

        return request
    }

    @Test
    fun testAuth() {
        val request = createTestAuthRequest()
        val bundle = request.toBundle()

        assertEquals(bundle.getInt(Keys.Base.TYPE), Constants.TIKTOK.AUTH_REQUEST)
        assertEquals(bundle.getString(Keys.Auth.STATE), state)
        assertEquals(bundle.getString(Keys.Auth.REDIRECT_URI), redirectUri)
        assertEquals(bundle.getString(Keys.Auth.CLIENT_KEY), clientKey)
        assertEquals(bundle.getString(Keys.Auth.SCOPE), scope)
        assertEquals(bundle.getString(Keys.Auth.OPTIONAL_SCOPE0), optionalScope0)
        assertEquals(bundle.getString(Keys.Auth.OPTIONAL_SCOPE1), optionalScope1)
        assertEquals(bundle.getString(Keys.Auth.LANGUAGE), language)
        assertEquals(bundle.getString(Keys.Base.CALLER_PKG), callerPackage)
        assertEquals(bundle.getString(Keys.Base.FROM_ENTRY), callerLocalEntry)
        assertEquals(bundle.getString(Keys.Base.CALLER_BASE_OPEN_VERSION), callerVersion)
        assertEquals(bundle.getString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_NAME), BuildConfig.SDK_OVERSEA_NAME)
        assertEquals(bundle.getString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_VERSION), BuildConfig.SDK_OVERSEA_VERSION)
    }

    @Test
    fun testAuthNative() {
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivity(allAny())
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val request = createTestAuthRequest()
        authService.authorizeNative(request, "packageName", "remoteRequestEntry", "localEntry")
        verify(exactly = 1) {
            mockActivity.startActivity(allAny())
        }
    }

    @Test
    fun testAuthWeb() {
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivity(allAny())
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val request = createTestAuthRequest()
        authService.authorizeWeb(request)
        verify(exactly = 1) {
            mockActivity.startActivity(allAny())
        }
    }
}
