package com.bytedance.sdk.open.tiktok.authorize.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
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
import com.bytedance.sdk.open.tiktok.R
import com.bytedance.sdk.open.tiktok.authorize.WebAuthHelper.Companion.composeLoadUrl
import com.bytedance.sdk.open.tiktok.authorize.model.Auth
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.model.Base
import com.bytedance.sdk.open.tiktok.utils.AppUtils.Companion.componentClassName
import com.bytedance.sdk.open.tiktok.utils.OpenUtils.Companion.setViewVisibility

open abstract class BaseWebAuthActivity: Activity(), IApiEventHandler {
    val WAP_AUTHORIZE_URL = "wap_authorize_url"
    val USER_CANCEL_AUTH = "User cancelled the Authorization"

    var OP_ERROR_NO_CONNECTION = -12
    var OP_ERROR_NETWORK_ERROR = -15

    open lateinit var mContentWebView: WebView
    open lateinit var mContainer: RelativeLayout

    var mAuthRequest: Auth.Request? = null
    var mBaseErrorDialog: AlertDialog? = null

    abstract fun isNetworkAvailable(): Boolean
    abstract fun handleIntent(intent: Intent?, eventHandler: IApiEventHandler?): Boolean

    abstract fun sendInnerResponse(req: Auth.Request?, resp: Base.Response?)

    protected abstract fun getHost(): String?

    protected abstract fun getAuthPath(): String?

    protected abstract fun getDomain(): String

    private var mHeaderView: RelativeLayout? = null



    private var mLoadingLayout: FrameLayout? = null

    private var mLastErrorCode = 0

    protected var mHasExecutingRequest = false

    private var mStatusDestroyed = false

    private var isShowNetworkError = false

    private var mContext: Context? = null
    private var mCancelBtn: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        handleIntent(getIntent(), this)
        setContentView(R.layout.layout_open_web_authorize)
        initView()
        handleRequestIntent()
    }

    override fun onReq(req: Base.Request?) {
        if (req is Auth.Request) {
            mAuthRequest = req
            mAuthRequest!!.redirectUri = "https://" + getDomain() + Keys.REDIRECT_URL_PATH
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        }
    }

    override fun onResp(resp: Base.Response?) {
    }

    override fun onErrorIntent(intent: Intent?) {
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        if (mContentWebView!!.canGoBack()) {
            mContentWebView!!.goBack()
        } else {
            redirectToClientApp(Constants.BaseError.ERROR_CANCEL, USER_CANCEL_AUTH)
        }
    }

    fun handleRequestIntent() {
        val argument = mAuthRequest
        if (argument == null) {
            finish()
            return
        }
        if (!isNetworkAvailable()) {
            isShowNetworkError = true
            showNetworkErrorDialog(OP_ERROR_NO_CONNECTION)
        } else {
            startLoading()
            configWebView()
            mContentWebView!!.loadUrl(composeLoadUrl(this, argument, getHost()!!, getAuthPath()!!)!!)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBaseErrorDialog != null && mBaseErrorDialog!!.isShowing) {
            mBaseErrorDialog!!.dismiss()
        }
    }

    private fun configWebView() {
        mContentWebView!!.webViewClient = AuthWebViewClient()
    }

    private fun redirectToClientApp(errorCode: Int, errorMsg: String?) {
        redirectToClientApp("", null, errorCode, errorMsg)
    }

    private fun redirectToClientApp(code: String, state: String?, errorCode: Int, errorMsg: String?) {
        val response = Auth.Response()
        response.authCode = code
        response.errorCode = errorCode
        response.state = state
        response.errorMsg = errorMsg
        sendInnerResponse(mAuthRequest, response)
        finish()
    }

    private fun redirectToClientApp(code: String?, state: String?, permissions: String?, errorCode: Int) {
        val response = Auth.Response()
        response.authCode = code
        response.errorCode = errorCode
        response.state = state
        response.grantedPermissions = permissions
        sendInnerResponse(mAuthRequest, response)
        finish()
    }

    fun sendInnerResponse(localEntry: String?, req: Auth.Request, resp: Base.Response?): Boolean {
        return if (resp == null || mContext == null) {
            false
        } else if (!resp.validate()) {
            false
        } else {
            val bundle = resp.toBundle()
            val platformPackageName = mContext!!.packageName
            val localResponseEntry = if (TextUtils.isEmpty(req.callerLocalEntry)) componentClassName(platformPackageName, localEntry!!) else req.callerLocalEntry!!
            val intent = Intent()
            val componentName = ComponentName(platformPackageName, localResponseEntry)
            intent.component = componentName
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            try {
                mContext!!.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun initView() {
        mContainer = findViewById(R.id.open_rl_container)
        // cancle button
        mHeaderView = findViewById(R.id.open_header_view)
        mCancelBtn = findViewById(R.id.cancel)
        mCancelBtn!!.setOnClickListener { onCancel(Constants.BaseError.ERROR_CANCEL) }
        setContainerViewBgColor()
        mLoadingLayout = findViewById<View>(R.id.open_loading_group) as FrameLayout?
        val loadingView = getLoadingView(mLoadingLayout)
        if (loadingView != null) {
            mLoadingLayout!!.removeAllViews()
            mLoadingLayout!!.addView(loadingView)
        }
        initWebView(this)
        if (mContentWebView!!.parent != null) {
            (mContentWebView!!.parent as ViewGroup).removeView(mContentWebView)
        }
        val params = mContentWebView!!.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.open_header_view)
        mContentWebView!!.layoutParams = params
        mContentWebView!!.visibility = View.INVISIBLE
        mContainer!!.addView(mContentWebView)
    }

    fun initWebView(context: Context?) {
        mContentWebView = WebView(context!!)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        mContentWebView!!.layoutParams = params
        val settings = mContentWebView!!.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
    }

    inner class AuthWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (isNetworkAvailable()) {
                if (handleRedirect(url)) {
                    return true
                }
                mContentWebView?.loadUrl(url)
            } else {
                showNetworkErrorDialog(OP_ERROR_NO_CONNECTION)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            mHasExecutingRequest = false
            if (mContentWebView != null && mContentWebView?.getProgress() == 100) {
                stopLoading()
                // loading  success
                if (mLastErrorCode == 0 && !isShowNetworkError) {
                    setViewVisibility(mContentWebView, View.VISIBLE)
                }
            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (mHasExecutingRequest) {
                return
            }
            mLastErrorCode = 0
            mHasExecutingRequest = true
            startLoading()
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            mLastErrorCode = errorCode
            // loading error
            showNetworkErrorDialog(OP_ERROR_NETWORK_ERROR)
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
                errorCode = errorCodeStr!!.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        redirectToClientApp(errorCode, errorMsgStr)
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
        redirectToClientApp(code, state, grantedPermissions, Constants.BaseError.OK)
        return true
    }

    protected open fun setContainerViewBgColor() {
        if (mContainer != null) {
            mContainer!!.setBackgroundColor(Color.parseColor("#ffffff"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mStatusDestroyed = true
        if (mContentWebView != null) {
            val parent = mContentWebView!!.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(mContentWebView)
            }
            mContentWebView!!.stopLoading()
//            mContentWebView?.bebViewClient = null
        }
    }


    protected fun startLoading() {
        setViewVisibility(mLoadingLayout, View.VISIBLE)
    }

    protected fun stopLoading() {
        setViewVisibility(mLoadingLayout, View.GONE)
    }

    private fun onCancel(errCode: Int) {
        redirectToClientApp(errCode, USER_CANCEL_AUTH)
    }

    override fun isDestroyed(): Boolean {
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

    private fun getLoadingView(root: ViewGroup?): View? {
        return LayoutInflater.from(this).inflate(R.layout.layout_open_loading_view, root, false)
    }

    protected fun showSslErrorDialog(handler: SslErrorHandler?, error: SslError) {
        try {
            val builder = AlertDialog.Builder(mContext)
            val ad = builder.create()
            var message = mContext!!.getString(R.string.aweme_open_ssl_error)
            val errorCode = error.primaryError
            when (errorCode) {
                SslError.SSL_UNTRUSTED -> message = mContext!!.getString(R.string.aweme_open_ssl_untrusted)
                SslError.SSL_EXPIRED -> message = mContext!!.getString(R.string.aweme_open_ssl_expired)
                SslError.SSL_IDMISMATCH -> message = mContext!!.getString(R.string.aweme_open_ssl_mismatched)
                SslError.SSL_NOTYETVALID -> message = mContext!!.getString(R.string.aweme_open_ssl_notyetvalid)
            }
            message += mContext!!.getString(R.string.aweme_open_ssl_continue)
            ad.setTitle(R.string.aweme_open_ssl_warning)
            ad.setTitle(message)
            ad.setButton(AlertDialog.BUTTON_POSITIVE, mContext!!.getString(R.string.aweme_open_ssl_ok)) { dialog, which -> cancelLoad(handler) }
            ad.setButton(AlertDialog.BUTTON_NEGATIVE, mContext!!.getString(R.string.aweme_open_ssl_cancel)) { dialog, which -> cancelLoad(handler) }
            ad.setCanceledOnTouchOutside(false)
            ad.show()
        } catch (e: Exception) {
            // ignore
            cancelLoad(handler)
        }
    }

    private fun cancelLoad(handler: SslErrorHandler?) {
        handler?.cancel()
        showNetworkErrorDialog(OP_ERROR_NETWORK_ERROR)
        isShowNetworkError = true
    }

    protected fun showNetworkErrorDialog(errCode: Int) {
        if (mBaseErrorDialog != null && mBaseErrorDialog!!.isShowing) {
            return
        }
        if (mBaseErrorDialog == null) {
            val mDialogView: View = LayoutInflater.from(this).inflate(R.layout.layout_open_network_error_dialog, null, false)
            mDialogView.findViewById<View>(R.id.tv_confirm).setOnClickListener { onCancel(errCode) }
            mBaseErrorDialog = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Holo))
                    .setView(mDialogView)
                    .setCancelable(false)
                    .create()
        }
        mBaseErrorDialog!!.show()
    }
}