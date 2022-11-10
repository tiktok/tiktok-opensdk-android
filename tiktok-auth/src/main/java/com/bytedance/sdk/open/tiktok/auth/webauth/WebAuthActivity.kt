package com.bytedance.sdk.open.tiktok.auth.webauth

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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bytedance.sdk.open.tiktok.auth.Auth
import com.bytedance.sdk.open.tiktok.auth.R
import com.bytedance.sdk.open.tiktok.auth.constants.Constants
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_HOST
import com.bytedance.sdk.open.tiktok.auth.constants.Constants.WEB_AUTH_REDIRECT_URL_PATH
import com.bytedance.sdk.open.tiktok.auth.utils.OpenUtils.setViewVisibility
import com.bytedance.sdk.open.tiktok.auth.webauth.WebAuthHelper.composeLoadUrl
import com.bytedance.sdk.open.tiktok.auth.webauth.WebAuthHelper.parseRedirectUriToAuthResponse
import com.bytedance.sdk.open.tiktok.core.constants.Constants.BaseError

internal class WebAuthActivity : Activity() {
    private lateinit var contentWebView: WebView
    private lateinit var container: RelativeLayout
    private lateinit var cancelBtn: TextView

    private lateinit var authRequest: Auth.Request
    private lateinit var clientKey: String
    private lateinit var baseErrorDialog: AlertDialog
    private var loadingLayout: FrameLayout? = null
    private var lastErrorCode = 0
    private var isExecutingRequest = false
    private var isStatusDestroyed = false
    private var isShowNetworkError = false
    private var isLoading: Boolean = false
        set(value) {
            field = value
            if (value) {
                setViewVisibility(loadingLayout, View.VISIBLE)
            } else {
                setViewVisibility(loadingLayout, View.GONE)
            }
        }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info?.isConnected ?: false
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_open_web_authorize)
        initView()
        val authRequestFromBundle = intent.getParcelableExtra<Auth.Request>(AUTH_REQUEST_KEY_IN_BUNDLE)
        val clientKeyFromBundle = intent.getStringExtra(CLIENT_KEY_IN_BUNDLE)
        // invalid request
        if (authRequestFromBundle == null || clientKeyFromBundle == null) {
            finish()
            return
        } else {
            authRequest = authRequestFromBundle
            clientKey = clientKeyFromBundle
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        handleRequestIntent()
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun onBackPressed() {
        if (contentWebView.canGoBack()) {
            contentWebView.goBack()
        } else {
            redirectToClientApp(getCancelResponse())
        }
    }

    private fun handleRequestIntent() {
        if (!isNetworkAvailable()) {
            isShowNetworkError = true
            showNetworkErrorDialog(Constants.AuthError.NETWORK_NO_CONNECTION)
        } else {
            isLoading = true
            contentWebView.webViewClient = AuthWebViewClient()
            contentWebView.loadUrl(composeLoadUrl(this, REDIRECT_URL, authRequest, clientKey))
        }
    }

    override fun onPause() {
        super.onPause()
        if (::baseErrorDialog.isInitialized && baseErrorDialog.isShowing) {
            baseErrorDialog.dismiss()
        }
    }

    private fun redirectToClientApp(response: Auth.Response) {
        sendInnerResponse(authRequest, response)
        finish()
    }

    private fun getCancelResponse() = Auth.Response(authCode = "", state = null, grantedPermissions = "", errorCode = BaseError.ERROR_CANCEL, errorMsg = USER_CANCEL_AUTH)

    private fun sendInnerResponse(authRequest: Auth.Request, resp: Auth.Response): Boolean {
        val bundle = resp.toBundle()
        val intent = Intent()
        val componentName = ComponentName(authRequest.packageName, authRequest.resultActivityFullPath)
        intent.component = componentName
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun initView() {
        container = findViewById(R.id.open_rl_container)
        container.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR))
        cancelBtn = findViewById(R.id.cancel)
        cancelBtn.setOnClickListener { onCancel(BaseError.ERROR_CANCEL) }

        loadingLayout = findViewById<View>(R.id.open_loading_group) as FrameLayout?
        getLoadingView(loadingLayout)?.let {
            loadingLayout?.removeAllViews()
            loadingLayout?.addView(it)
        }
        initWebView()
        (contentWebView.parent as? ViewGroup)?.removeView(contentWebView)
        (contentWebView.layoutParams as RelativeLayout.LayoutParams).let {
            it.addRule(RelativeLayout.BELOW, R.id.open_header_view)
            contentWebView.layoutParams = it
        }
        contentWebView.visibility = View.INVISIBLE
        container.addView(contentWebView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        contentWebView = WebView(this)
        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT).let { contentWebView.layoutParams = it }
        contentWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }
    }

    inner class AuthWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (isNetworkAvailable()) {
                if (handleRedirect(url)) {
                    return true
                }
                contentWebView.loadUrl(url)
            } else {
                showNetworkErrorDialog(Constants.AuthError.NETWORK_NO_CONNECTION)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            isExecutingRequest = false
            if (contentWebView.progress == 100) {
                isLoading = false
                if (lastErrorCode == 0 && !isShowNetworkError) {
                    setViewVisibility(contentWebView, View.VISIBLE)
                }
            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (isExecutingRequest) {
                return
            }
            lastErrorCode = 0
            isExecutingRequest = true
            isLoading = true
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            lastErrorCode = errorCode
            showNetworkErrorDialog(Constants.AuthError.NETWORK_IO_ERROR)
            isShowNetworkError = true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            showSslErrorDialog(handler, error)
        }
    }

    private fun handleRedirect(url: String): Boolean {
        if (TextUtils.isEmpty(url)) {
            return false
        }
        if (!url.startsWith(REDIRECT_URL)) {
            return false
        }
        val uri = Uri.parse(url)
        val extras = Bundle()
        extras.putString(WEB_AUTHORIZE_URL, contentWebView.url)
        redirectToClientApp(parseRedirectUriToAuthResponse(uri, extras))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        isStatusDestroyed = true
        contentWebView.apply {
            this.parent?.let {
                (it as ViewGroup).removeView(this)
            }
            isLoading = false
        }
    }

    private fun onCancel(errCode: Int) = redirectToClientApp(getCancelResponse())

    private fun getLoadingView(root: ViewGroup?): View? = LayoutInflater.from(this).inflate(R.layout.layout_open_loading_view, root, false)

    private fun showSslErrorDialog(handler: SslErrorHandler?, error: SslError) {
        try {
            val builder = AlertDialog.Builder(this)
            val dialog = builder.create()
            var message = getString(R.string.aweme_open_ssl_error)
            when (error.primaryError) {
                SslError.SSL_UNTRUSTED -> message = getString(R.string.aweme_open_ssl_untrusted)
                SslError.SSL_EXPIRED -> message = getString(R.string.aweme_open_ssl_expired)
                SslError.SSL_IDMISMATCH -> message = getString(R.string.aweme_open_ssl_mismatched)
                SslError.SSL_NOTYETVALID -> message = getString(R.string.aweme_open_ssl_notyetvalid)
            }
            message += getString(R.string.aweme_open_ssl_continue)
            dialog.setTitle(R.string.aweme_open_ssl_warning)
            dialog.setTitle(message)
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.aweme_open_ssl_ok)) { _, _ -> cancelLoad(handler) }
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.aweme_open_ssl_cancel)) { _, _ -> cancelLoad(handler) }
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } catch (e: Exception) {
            cancelLoad(handler)
        }
    }

    private fun cancelLoad(handler: SslErrorHandler?) {
        handler?.cancel()
        showNetworkErrorDialog(Constants.AuthError.NETWORK_IO_ERROR)
        isShowNetworkError = true
    }

    private fun showNetworkErrorDialog(errCode: Int) {
        if (!::baseErrorDialog.isInitialized) {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.layout_open_network_error_dialog, null, false)
            baseErrorDialog = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Holo))
                .setView(mDialogView)
                .setCancelable(false)
                .create()
            mDialogView.findViewById<TextView>(R.id.tv_confirm)?.setOnClickListener {
                onCancel(errCode)
            }
        } else if (baseErrorDialog.isShowing) {
            return
        }
        baseErrorDialog.show()
    }

    companion object {
        const val CLIENT_KEY_IN_BUNDLE = "client_key"
        const val AUTH_REQUEST_KEY_IN_BUNDLE = "auth_request_key"

        private const val USER_CANCEL_AUTH = "User cancelled the Authorization"
        private const val BACKGROUND_COLOR = "#ffffff"
        private const val WEB_AUTHORIZE_URL = "wap_authorize_url"

        const val REDIRECT_URL = "https://${WEB_AUTH_HOST}$WEB_AUTH_REDIRECT_URL_PATH"
    }
}
