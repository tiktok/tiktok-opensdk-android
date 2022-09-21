package com.bytedance.sdk.open.tiktok

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import com.bytedance.sdk.open.tiktok.authorize.AuthService
import com.bytedance.sdk.open.tiktok.authorize.SendAuthDataHandler
import com.bytedance.sdk.open.tiktok.authorize.WebAuthActivity
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl
import com.bytedance.sdk.open.tiktok.share.ShareService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TikTokOpenAPITest {
    private val testCases = arrayListOf(
        0, Constants.TIKTOK.AUTH_REQUEST, Constants.TIKTOK.AUTH_RESPONSE,
        Constants.TIKTOK.SHARE_REQUEST, Constants.TIKTOK.SHARE_RESPONSE, 5
    )
    @MockK
    lateinit var intent: Intent
    @MockK
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        intent = Intent(context, WebAuthActivity::class.java)
    }

    @Test
    fun testHandleIntentWhenIntentIsNull() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivityForResult(allAny(), Keys.AUTH_REQUEST_CODE)
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareService = ShareService(mockContext, "client_key")
        val eventHandler = spyk<IApiEventHandler>(object : IApiEventHandler {
            override fun onRequest(req: Base.Request?) {}
            override fun onResponse(resp: Base.Response?) {}
            override fun onErrorIntent(intent: Intent?) {}
        })

        val tikTokOpenApiImpl = TikTokOpenApiImpl(context, authService, shareService)

        assertFalse(tikTokOpenApiImpl.handleIntent(null, eventHandler))
    }

    @Test
    fun testHandleIntentWhenEventHandlerIsNull() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivityForResult(allAny(), Keys.AUTH_REQUEST_CODE)
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareService = ShareService(mockContext, "client_key")

        val tikTokOpenApiImpl = TikTokOpenApiImpl(context, authService, shareService)

        assertFalse(tikTokOpenApiImpl.handleIntent(intent, null))
    }

    @Test
    fun testHandleIntentWhenBundleIsNull() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivityForResult(allAny(), Keys.AUTH_REQUEST_CODE)
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareService = ShareService(mockContext, "client_key")

        val tikTokOpenApiImpl = TikTokOpenApiImpl(context, authService, shareService)
        intent.replaceExtras(null)
        assertFalse(tikTokOpenApiImpl.handleIntent(intent, null))
    }

    @Test
    fun testHandleIntent() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val mockActivity = mockk<Activity>(relaxed = true)
        every {
            mockActivity.startActivityForResult(allAny(), Keys.AUTH_REQUEST_CODE)
        } returns Unit
        val authService = AuthService(mockActivity, "client_key")
        val mockContext = mockk<Context>(relaxed = true)
        every {
            mockContext.startActivity(allAny())
        } returns Unit
        val shareService = ShareService(mockContext, "client_key")
        val tikTokOpenApiImpl = TikTokOpenApiImpl(context, authService, shareService)
        val bundle: Bundle? = intent.extras
        val eventHandler = spyk<IApiEventHandler>(object : IApiEventHandler {
            override fun onRequest(req: Base.Request?) {}
            override fun onResponse(resp: Base.Response?) {}
            override fun onErrorIntent(intent: Intent?) {}
        })

        val sendAuthDataHandler = SendAuthDataHandler()
        for (type in testCases) {
            if (type == Constants.TIKTOK.AUTH_REQUEST || type == Constants.TIKTOK.AUTH_RESPONSE) {
                assertEquals(
                    tikTokOpenApiImpl.handleIntent(intent, eventHandler),
                    sendAuthDataHandler.handle(type, bundle, eventHandler)
                )
            } else {
                assertEquals(
                    tikTokOpenApiImpl.handleIntent(intent, eventHandler),
                    sendAuthDataHandler.handle(type, bundle, eventHandler)
                )
            }
        }
    }
}
