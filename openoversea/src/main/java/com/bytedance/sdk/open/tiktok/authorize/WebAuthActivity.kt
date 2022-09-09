package com.bytedance.sdk.open.tiktok.authorize

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
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
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi
import com.bytedance.sdk.open.tiktok.authorize.WebAuthHelper.Companion.composeLoadUrl
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.utils.AppUtils.Companion.componentClassName
import com.bytedance.sdk.open.tiktok.utils.OpenUtils.Companion.setViewVisibility
import com.bytedance.sdk.open.tiktok.utils.ViewUtils

private const val USER_CANCEL_AUTH = "User cancelled the Authorization"
private const val BACKGROUND_COLOR = "#ffffff"

class WebAuthActivity: Activity(), IApiEventHandler {
    private lateinit var mContentWebView: WebView
    private lateinit var mContainer: RelativeLayout
    private lateinit var mCancelBtn: TextView

    private var mAuthRequest: Auth.Request? = null
    private lateinit var mBaseErrorDialog: AlertDialog
    private var mLoadingLayout: FrameLayout? = null
    private var mLastErrorCode = 0
    private var mIsExecutingRequest = false
    private var mStatusDestroyed = false
    private var isShowNetworkError = false
    private var ttOpenApi: TikTokOpenApi? = null
    private var isLoading: Boolean = false
        set(value) {
            field = value
            if (value) {
                setViewVisibility(mLoadingLayout, View.VISIBLE)
            } else {
                setViewVisibility(mLoadingLayout, View.GONE)
            }
        }

    // TODO: chen.wu change to connection monitoring https://developer.android.com/training/monitoring-device-state/connectivity-status-type when SDK >= LOLLIPOP
    fun isNetworkAvailable(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttOpenApi = TikTokOpenApiFactory.create(this)
        handleIntent(intent, this)
        setContentView(R.layout.layout_open_web_authorize)
        initView()
        handleRequestIntent()
        ViewUtils.setStatusBarColor(this, Color.TRANSPARENT)
    }

    override fun onReq(req: Base.Request?) {
        if (req is Auth.Request) {
            mAuthRequest = req
            mAuthRequest!!.redirectUri = "https://${BuildConfig.AUTH_HOST}${Keys.REDIRECT_URL_PATH}"
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onResp(resp: Base.Response?) {}
    override fun onErrorIntent(intent: Intent?) {}

    override fun onBackPressed() {
        if (mContentWebView.canGoBack()) {
            mContentWebView.goBack()
        } else {
            redirectToClientApp(Constants.BaseError.ERROR_CANCEL, errorMsg = USER_CANCEL_AUTH)
        }
    }

    private fun handleRequestIntent() {
        mAuthRequest?.let {
            if (!isNetworkAvailable()) {
                isShowNetworkError = true
                showNetworkErrorDialog(Constants.AuthError.NETWORK_NO_CONNECTION)
            } else {
                isLoading = true
                mContentWebView.webViewClient = AuthWebViewClient()
                // need auth request to be set in onReq
                mContentWebView.loadUrl(composeLoadUrl(this, it, BuildConfig.AUTH_HOST, BuildConfig.AUTH_ENDPOINT)!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mBaseErrorDialog.isInitialized && mBaseErrorDialog.isShowing) {
            mBaseErrorDialog.dismiss()
        }
    }

    private fun redirectToClientApp(errorCode: Int, code: String? = null, state: String? = null, permissions: String? = null, errorMsg: String? = null) {
        mAuthRequest?.let {
            val response = Auth.Response()
            response.authCode = code
            response.errorCode = errorCode
            response.state = state
            response.grantedPermissions = permissions
            response.errorMsg = errorMsg
            sendInnerResponse(it, response)
        }
        finish()
    }

    private fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean {
        return ttOpenApi?.handleIntent(intent, eventHandler) ?: false
    }

    private fun sendInnerResponse(req: Auth.Request, resp: Base.Response) {
        if (mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = Bundle()
            }
            resp.extras!!.putString("wap_authorize_url", mContentWebView.url)
        }
        sendInnerResponse(BuildConfig.DEFAULT_ENTRY_ACTIVITY, req, resp)
    }

    private fun sendInnerResponse(localEntry: String, req: Auth.Request, resp: Base.Response): Boolean {
        if (!resp.validate()) {
            return false
        }
        val bundle = resp.toBundle()
        val platformPackageName = packageName
        val localResponseEntry = if (TextUtils.isEmpty(req.callerLocalEntry)) componentClassName(platformPackageName, localEntry) else req.callerLocalEntry!!
        val intent = Intent()
        val componentName = ComponentName(platformPackageName, componentClassName(platformPackageName, localResponseEntry))
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
        (mContentWebView.parent as? ViewGroup)?.let { it.removeView(mContentWebView) }
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
        val errorCodeStr = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_ERROR_CODE)
        val errorMsgStr = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_ERROR_MESSAGE)
        var errorCode = Constants.BaseError.ERROR_UNKNOWN
        if (!TextUtils.isEmpty(errorCodeStr)) {
            try {
                 errorCodeStr?.apply { errorCode = this.toInt() }
            } catch (e: Exception) {
                e.printStackTrace() // TODO: chen.wu remove
            }
        }
        redirectToClientApp(errorCode, errorMsg = errorMsgStr)
    }

    private fun handleRedirect(url: String): Boolean {
        if (TextUtils.isEmpty(url)) {
            return false
        }
        val uri = Uri.parse(url)
        val argument = mAuthRequest
        if (argument?.redirectUri == null || !url.startsWith(argument.redirectUri!!)) {
            return false
        }
        val code = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_CODE)
        val state = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_STATE)
        val grantedPermissions = uri.getQueryParameter(Keys.Web.REDIRECT_QUERY_SCOPE)
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

    override fun isDestroyed(): Boolean { // TODO: chen.wu remove this?
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mStatusDestroyed
        } else {
            try {
                super.isDestroyed()
            } catch (ignore: Throwable) {
                mStatusDestroyed
            }
        }
    }

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
}