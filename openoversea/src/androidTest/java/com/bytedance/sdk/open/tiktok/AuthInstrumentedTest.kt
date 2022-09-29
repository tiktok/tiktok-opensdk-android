package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.AppUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {
    private val state = "state"
    private val clientKey = "clientKey"
    private val scope = "scope1,scope2"
    private val optionalScope0 = "optionalScope0"
    private val optionalScope1 = "optionalScope1"
    private val language = "language"
    private val callerPackage = "callerPackage"
    private val callerLocalEntry = "callerLocalEntry"
    private val callerVersion = "callerVersion"

    private fun createTestAuthRequest(localEntry: String? = null): Auth.Request {
        return Auth.Request(
            scope = scope,
            state = state,
            optionalScope0 = optionalScope0,
            optionalScope1 = optionalScope1,
            language = language,
            callerLocalEntry = localEntry
        )
    }

    @Test
    fun testAuthWithCustomLocalEntry() {
        val request = createTestAuthRequest(localEntry = callerLocalEntry)
        val bundle = request.toBundle(clientKey, callerPackage, callerVersion)

        assertEquals(bundle.getString(Keys.Base.FROM_ENTRY), AppUtils.componentClassName(callerPackage, callerLocalEntry))
        verifyBundle(bundle)
    }

    @Test
    fun testAuthWithDefaultLocalEntry() {
        val request = createTestAuthRequest(localEntry = null)
        val bundle = request.toBundle(clientKey, callerPackage, callerVersion)

        assertEquals(bundle.getString(Keys.Base.FROM_ENTRY), AppUtils.componentClassName(callerPackage, BuildConfig.DEFAULT_ENTRY_ACTIVITY))
        verifyBundle(bundle)
    }

    private fun verifyBundle(bundle: Bundle) {
        assertEquals(bundle.getInt(Keys.Base.TYPE), Constants.TIKTOK.AUTH_REQUEST)
        assertEquals(bundle.getString(Keys.Auth.STATE), state)
        assertEquals(bundle.getString(Keys.Auth.CLIENT_KEY), clientKey)
        assertEquals(bundle.getString(Keys.Auth.SCOPE), scope)
        assertEquals(bundle.getString(Keys.Auth.OPTIONAL_SCOPE0), optionalScope0)
        assertEquals(bundle.getString(Keys.Auth.OPTIONAL_SCOPE1), optionalScope1)
        assertEquals(bundle.getString(Keys.Auth.LANGUAGE), language)
        assertEquals(bundle.getString(Keys.Base.CALLER_PKG), callerPackage)
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
        authService.authorizeNative(request, "packageName", "remoteRequestEntry")
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
