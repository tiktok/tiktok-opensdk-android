package com.bytedance.sdk.open.tiktok.authorize

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
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.R
import com.bytedance.sdk.open.tiktok.authorize.WebAuthHelper.composeLoadUrl
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.utils.OpenUtils.setViewVisibility

internal class WebAuthActivity : Activity() {
    private lateinit var mContentWebView: WebView
    private lateinit var mContainer: RelativeLayout
    private lateinit var mCancelBtn: TextView

    private lateinit var webAuthRequest: WebAuthRequest
    private lateinit var mBaseErrorDialog: AlertDialog
    private var mLoadingLayout: FrameLayout? = null
    private var mLastErrorCode = 0
    private var mIsExecutingRequest = false
    private var mStatusDestroyed = false
    private var isShowNetworkError = false
    private var isLoading: Boolean = false
        set(value) {
            field = value
            if (value) {
                setViewVisibility(mLoadingLayout, View.VISIBLE)
            } else {
                setViewVisibility(mLoadingLayout, View.GONE)
            }
        }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info?.isConnected ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_open_web_authorize)
        initView()
        val bundle = intent.extras
        val webAuthRequestFromBundle = bundle?.toWebAuthRequest()
        // invalid request
        if (webAuthRequestFromBundle == null) {
            finish()
            return
        }
        webAuthRequest = webAuthRequestFromBundle
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        handleRequestIntent()
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun onBackPressed() {
        if (mContentWebView.canGoBack()) {
            mContentWebView.goBack()
        } else {
            redirectToClientApp(Constants.BaseError.ERROR_CANCEL, errorMsg = USER_CANCEL_AUTH)
        }
    }

    private fun handleRequestIntent() {
        webAuthRequest.let {
            if (!isNetworkAvailable()) {
                isShowNetworkError = true
                showNetworkErrorDialog(Constants.AuthError.NETWORK_NO_CONNECTION)
            } else {
                isLoading = true
                mContentWebView.webViewClient = AuthWebViewClient()
                mContentWebView.loadUrl(composeLoadUrl(this, REDIRECT_URL, webAuthRequest))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mBaseErrorDialog.isInitialized && mBaseErrorDialog.isShowing) {
            mBaseErrorDialog.dismiss()
        }
    }

    private fun redirectToClientApp(errorCode: Int, code: String = "", state: String? = null, permissions: String = "", errorMsg: String? = null) {
        val extras = Bundle()
        extras.putString(WEB_AUTHORIZE_URL, mContentWebView.url)
        val response = Auth.Response(
            authCode = code,
            state = state,
            grantedPermissions = permissions,
            errorCode = errorCode,
            errorMsg = errorMsg,
            extras = extras
        )
        sendInnerResponse(webAuthRequest, response)
        finish()
    }

    private fun sendInnerResponse(webAuthRequest: WebAuthRequest, resp: Base.Response): Boolean {
        val bundle = resp.toBundle()
        val intent = Intent()
        val componentName = ComponentName(webAuthRequest.resultActivityPackageName, webAuthRequest.resultActivityClassPath)
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
        mContainer = findViewById(R.id.open_rl_container)
        mContainer.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR))
        mCancelBtn = findViewById(R.id.cancel)
        mCancelBtn.setOnClickListener { onCancel(Constants.BaseError.ERROR_CANCEL) }

        mLoadingLayout = findViewById<View>(R.id.open_loading_group) as FrameLayout?
        getLoadingView(mLoadingLayout)?.let {
            mLoadingLayout?.removeAllViews()
            mLoadingLayout?.addView(it)
        }
        initWebView()
        (mContentWebView.parent as? ViewGroup)?.removeView(mContentWebView)
        (mContentWebView.layoutParams as RelativeLayout.LayoutParams).let {
            it.addRule(RelativeLayout.BELOW, R.id.open_header_view)
            mContentWebView.layoutParams = it
        }
        mContentWebView.visibility = View.INVISIBLE
        mContainer.addView(mContentWebView)
    }

    private fun initWebView() {
        mContentWebView = WebView(this)
        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT).let { mContentWebView.layoutParams = it }
        mContentWebView.settings.apply {
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
                mContentWebView.loadUrl(url)
            } else {
                showNetworkErrorDialog(Constants.AuthError.NETWORK_NO_CONNECTION)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            mIsExecutingRequest = false
            if (mContentWebView.progress == 100) {
                isLoading = false
                if (mLastErrorCode == 0 && !isShowNetworkError) {
                    setViewVisibility(mContentWebView, View.VISIBLE)
                }
            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (mIsExecutingRequest) {
                return
            }
            mLastErrorCode = 0
            mIsExecutingRequest = true
            isLoading = true
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            mLastErrorCode = errorCode
            showNetworkErrorDialog(Constants.AuthError.NETWORK_IO_ERROR)
            isShowNetworkError = true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            showSslErrorDialog(handler, error)
        }
    }

    private fun parseErrorAndRedirectToClient(uri: Uri) {
        val errorCodeStr: String? = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_ERROR_CODE)
        val errorCode = try {
            errorCodeStr?.toInt() ?: Constants.BaseError.ERROR_UNKNOWN
        } catch (e: Exception) {
            Constants.BaseError.ERROR_UNKNOWN
        }
        val errorMsgStr: String? = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_ERROR_MESSAGE)
        redirectToClientApp(errorCode, errorMsg = errorMsgStr)
    }

    private fun handleRedirect(url: String): Boolean {
        if (TextUtils.isEmpty(url)) {
            return false
        }
        val uri = Uri.parse(url)
        if (!url.startsWith(REDIRECT_URL)) {
            return false
        }
        val code = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_CODE) ?: ""
        val state = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_STATE)
        val grantedPermissions = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_SCOPE) ?: ""
        if (TextUtils.isEmpty(code)) {
            parseErrorAndRedirectToClient(uri)
            return false
        }
        redirectToClientApp(Constants.BaseError.OK, code, state, grantedPermissions)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mStatusDestroyed = true
        mContentWebView.apply {
            this.parent?.let {
                (it as ViewGroup).removeView(this)
            }
            isLoading = false
        }
    }

    private fun onCancel(errCode: Int) = redirectToClientApp(errCode, errorMsg = USER_CANCEL_AUTH)

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
        if (!::mBaseErrorDialog.isInitialized) {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.layout_open_network_error_dialog, null, false)
            mBaseErrorDialog = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Holo))
                .setView(mDialogView)
                .setCancelable(false)
                .create()
            mDialogView.findViewById<TextView>(R.id.tv_confirm)?.setOnClickListener {
                onCancel(errCode)
            }
        } else if (mBaseErrorDialog.isShowing) {
            return
        }
        mBaseErrorDialog.show()
    }

    companion object {
        private const val USER_CANCEL_AUTH = "User cancelled the Authorization"
        private const val BACKGROUND_COLOR = "#ffffff"
        private const val WEB_AUTHORIZE_URL = "wap_authorize_url"

        private const val REDIRECT_URL = "https://${BuildConfig.AUTH_HOST}${Keys.REDIRECT_URL_PATH}"
    }
}
