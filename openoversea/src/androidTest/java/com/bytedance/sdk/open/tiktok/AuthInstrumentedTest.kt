package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.authorize.Auth
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.SendAuthDataHandler
import com.bytedance.sdk.open.tiktok.authorize.WebAuthActivity
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.share.Share
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler
import com.bytedance.sdk.open.tiktok.share.ShareService
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
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
    private fun createTestRequestBundle(): Bundle {
        return createTestAuthRequest().toBundle()
    }

    private fun createTestResponse(): Auth.Response {
        return Auth.Response().apply {
            errorCode = 1002
            errorMsg = "mock error message"
            state = "error"
        }
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
    fun testAuthDataHandler() {
        val handler = SendAuthDataHandler()
        val bundle = createTestRequestBundle()
        val eventHandler = spyk<IApiEventHandler>(object: IApiEventHandler {
            override fun onReq(req: Base.Request?) {}
            override fun onResp(resp: Base.Response?) {}
            override fun onErrorIntent(intent: Intent?) {}
        })
        handler.handle(Constants.TIKTOK.AUTH_REQUEST, bundle, eventHandler)
        verify(exactly = 1) {
            eventHandler.onReq(allAny())
        }
        handler.handle(Constants.TIKTOK.AUTH_RESPONSE, createTestResponse().toBundle(), eventHandler)
        verify(exactly = 1) {
            eventHandler.onResp(allAny())
        }
    }

    @Test
    fun testAuthService() {
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivityForResult(allAny(), Keys.AUTH_REQUEST_CODE)
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val request = createTestAuthRequest()
        authService.authorizeNative(request,  "packageName", "remoteRequestEntry", "localEntry")
        verify(exactly = 1) {
            mockActivity.startActivityForResult(allAny(), Keys.AUTH_REQUEST_CODE)
        }

        every {
            mockActivity.startActivity(allAny())
        } returns Unit
        authService.authorizeWeb(WebAuthActivity::class.java, request)
        verify(exactly = 1) {
            mockActivity.startActivity(allAny())
        }
    }
}