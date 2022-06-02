package com.bytedance.sdk.open.tiktok.authorize.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytedance.sdk.open.tiktok.CommonConstants;
import com.bytedance.sdk.open.tiktok.R;
import com.bytedance.sdk.open.tiktok.authorize.WebViewHelper;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;
import com.bytedance.sdk.open.tiktok.utils.AppUtil;
import com.bytedance.sdk.open.tiktok.utils.OpenUtils;


/**
 * Created by yangzhirong on 2018/10/10.
 */
public abstract class BaseWebAuthorizeActivity extends Activity implements IApiEventHandler {

    private static final String RES_ID = "id";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STRING = "string";
    protected static final String WAP_AUTHORIZE_URL = "wap_authorize_url";

    private static final String USER_CANCEL_AUTH = "User cancelled the Authorization";


    int OP_ERROR_NO_CONNECTION = -12;
    int OP_ERROR_CONNECT_TIMEOUT = -13;
    int OP_ERROR_NETWORK_ERROR = -15;

    protected WebView mContentWebView;

    protected Authorization.Request mAuthRequest;
    protected AlertDialog mBaseErrorDialog;

    /**
     * if network is available
     *
     * @return
     */
    protected abstract boolean isNetworkAvailable();

    /**
     * handle intent
     */
    protected abstract boolean handleIntent(Intent intent, IApiEventHandler eventHandler);

    /**
     * send response result
     *
     * @param resp
     */
    protected abstract void sendInnerResponse(Authorization.Request req, BaseResp resp);

    /**
     * web authorization host
     *
     * @return
     */
    protected abstract String getHost();

    /**
     * web authorization path
     *
     * @return
     */
    protected abstract String getAuthPath();

    /**
     * redirectUrl domain when authorization success
     * @return
     */
    protected abstract String getDomain();

    /**
     * errorCode to errorMsg
     *
     * @param errorCode
     * @return
     */
    protected abstract String errorCode2Message(int errorCode);


    protected RelativeLayout mHeaderView;

    protected RelativeLayout mContainer;

    protected FrameLayout mLoadingLayout;

    private int mLastErrorCode;

    protected boolean mHasExecutingRequest;

    protected boolean mStatusDestroyed = false;

    protected boolean isShowNetworkError = false;

    private static final int MSG_LOADING_TIME_OUT = 100;

    private Context mContext;
    protected TextView mCancelBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        handleIntent(getIntent(), this);
        setContentView(R.layout.layout_open_web_authorize);
        initView();
        initActions();
        handleRequestIntent();
    }

    @Override
    public void onReq(BaseReq req) {
        if (req instanceof Authorization.Request) {
            mAuthRequest = (Authorization.Request) req;
            mAuthRequest.redirectUri = "https://" + getDomain() + ParamKeyConstants.REDIRECT_URL_PATH;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        //empty
    }

    @Override
    public void onErrorIntent(Intent intent) {
        //empty
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mContentWebView.canGoBack()) {
            mContentWebView.goBack();
        } else {
            redirectToClientApp(CommonConstants.BaseError.ERROR_CANCEL, USER_CANCEL_AUTH);
        }
    }

    public final void handleRequestIntent() {

        Authorization.Request argument = mAuthRequest;

        if (argument == null) {
            finish();
            return;
        }

        if (!isNetworkAvailable()) {
            isShowNetworkError = true;
            showNetworkErrorDialog(OP_ERROR_NO_CONNECTION);
        } else {
            startLoading();
            configWebView();
            mContentWebView.loadUrl(WebViewHelper.getLoadUrl(this, argument, getHost(), getAuthPath()));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBaseErrorDialog != null && mBaseErrorDialog.isShowing()) {
            mBaseErrorDialog.dismiss();
        }
    }

    protected void configWebView() {
        mContentWebView.setWebViewClient(new AuthWebViewClient());
    }




    private void redirectToClientApp(int errorCode, String errorMsg) {
        redirectToClientApp("", null, errorCode, errorMsg);
    }

    /**
     * return result to app
     *
     * @param code
     * @param state
     * @param errorCode
     */
    private void redirectToClientApp(String code, String state, int errorCode, String errorMsg) {
        Authorization.Response response = new Authorization.Response();
        response.authCode = code;
        response.errorCode = errorCode;
        response.state = state;
        response.errorMsg = errorMsg;
        sendInnerResponse(mAuthRequest, response);
        finish();
    }

    /**
     * return result to app
     *
     * @param code
     * @param state
     * @param errorCode
     */
    private void redirectToClientApp(String code, String state, String permissions, int errorCode) {
        Authorization.Response response = new Authorization.Response();
        response.authCode = code;
        response.errorCode = errorCode;
        response.state = state;
        response.grantedPermissions = permissions;
        sendInnerResponse(mAuthRequest, response);
        finish();
    }

    public boolean sendInnerResponse(String localEntry, Authorization.Request req, BaseResp resp) {
        if (resp == null || mContext == null) {
            return false;
        } else if (!resp.checkArgs()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            resp.toBundle(bundle);
            String platformPackageName = mContext.getPackageName();
            String localResponseEntry = TextUtils.isEmpty(req.callerLocalEntry) ? AppUtil.buildComponentClassName(platformPackageName, localEntry) : req.callerLocalEntry;
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(platformPackageName, localResponseEntry);
            intent.setComponent(componentName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            try {
                mContext.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }


    private void initView() {
        mContainer = findViewById(R.id.open_rl_container);
        // cancle button
        mHeaderView = findViewById(R.id.open_header_view);

        mCancelBtn = findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(CommonConstants.BaseError.ERROR_CANCEL);
            }
        });
        setContainerViewBgColor();

        mLoadingLayout = (FrameLayout) findViewById(R.id.open_loading_group);

        View loadingView = getLoadingView(mLoadingLayout);
        if (loadingView != null) {
            mLoadingLayout.removeAllViews();
            mLoadingLayout.addView(loadingView);
        }
        initWebView(this);

        if (mContentWebView.getParent() != null) {
            ((ViewGroup) mContentWebView.getParent()).removeView(mContentWebView);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentWebView.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.open_header_view);
        mContentWebView.setLayoutParams(params);
        mContentWebView.setVisibility(View.INVISIBLE);
        mContainer.addView(mContentWebView);
    }

    public void initWebView(Context context){
        mContentWebView = new WebView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContentWebView.setLayoutParams(params);
        WebSettings settings = mContentWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }


    protected void initActions() {

    }

    public class AuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isNetworkAvailable()) {
                if (handleRedirect(url)) {
                    return true;
                }
                mContentWebView.loadUrl(url);
            } else {
                showNetworkErrorDialog(OP_ERROR_NO_CONNECTION);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mHasExecutingRequest = false;
            if (mContentWebView != null && mContentWebView.getProgress() == 100) {
                stopLoading();
                // loading  success
                if (mLastErrorCode == 0 && !isShowNetworkError) {
                    OpenUtils.setViewVisibility(mContentWebView, View.VISIBLE);
                }


            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (mHasExecutingRequest) {
                return;
            }
            mLastErrorCode = 0;
            mHasExecutingRequest = true;
            startLoading();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mLastErrorCode = errorCode;
            // loading error
            showNetworkErrorDialog(OP_ERROR_NETWORK_ERROR);
            isShowNetworkError = true;
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            showSslErrorDialog(handler, error);
        }
    }

    private void parseErrorAndRedirectToClient(Uri uri) {
        String errorCodeStr = uri.getQueryParameter(ParamKeyConstants.WebViewConstants.REDIRECT_QUERY_ERROR_CODE);
        String errorMsgStr = uri.getQueryParameter(ParamKeyConstants.WebViewConstants.REDIRECT_QUERY_ERROR_MESSAGE);
        int errorCode = CommonConstants.BaseError.ERROR_UNKNOWN;
        if (!TextUtils.isEmpty(errorCodeStr)) {
            try {
                errorCode = Integer.parseInt(errorCodeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        redirectToClientApp(errorCode, errorMsgStr);
    }

    private boolean handleRedirect(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        Authorization.Request argument = mAuthRequest;
        if (argument == null || argument.redirectUri == null || !url.startsWith(argument.redirectUri)) {
            return false;
        }
        String code = uri.getQueryParameter(ParamKeyConstants.WebViewConstants.REDIRECT_QUERY_CODE);
        String state = uri.getQueryParameter(ParamKeyConstants.WebViewConstants.REDIRECT_QUERY_STATE);
        String grantedPermissions = uri.getQueryParameter(ParamKeyConstants.WebViewConstants.REDIRECT_QUERY_SCOPE);
        if (TextUtils.isEmpty(code)) {
            parseErrorAndRedirectToClient(uri);
            return false;
        }
        redirectToClientApp(code, state, grantedPermissions, CommonConstants.BaseError.OK);
        return true;
    }

    protected void setContainerViewBgColor() {
        if (mContainer != null) {
            mContainer.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStatusDestroyed = true;
        if (mContentWebView != null) {
            ViewParent parent = mContentWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentWebView);
            }
            mContentWebView.stopLoading();
            mContentWebView.setWebViewClient(null);
        }
    }


    protected void startLoading() {
        OpenUtils.setViewVisibility(mLoadingLayout, View.VISIBLE);
    }

    protected void stopLoading() {
        OpenUtils.setViewVisibility(mLoadingLayout, View.GONE);
    }

    protected void onCancel(int errCode) {
        redirectToClientApp(errCode, USER_CANCEL_AUTH);
    }


    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return mStatusDestroyed;
        } else {
            try {
                return super.isDestroyed();
            } catch (Throwable ignore) {
                return mStatusDestroyed;
            }
        }
    }

    protected View getLoadingView(ViewGroup root) {
        return LayoutInflater.from(this).inflate(R.layout.layout_open_loading_view, root, false);
    }


    /**
     * deal with ssl error
     */
    protected void showSslErrorDialog(final SslErrorHandler handler, SslError error) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            AlertDialog ad = builder.create();
            String message = mContext.getString(R.string.aweme_open_ssl_error);
            final int errorCode = error.getPrimaryError();
            switch (errorCode) {
                case SslError.SSL_UNTRUSTED:
                    message = mContext.getString(R.string.aweme_open_ssl_untrusted);
                    break;
                case SslError.SSL_EXPIRED:
                    message = mContext.getString(R.string.aweme_open_ssl_expired);
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = mContext.getString(R.string.aweme_open_ssl_mismatched);
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = mContext.getString(R.string.aweme_open_ssl_notyetvalid);
                    break;
            }
            message += mContext.getString(R.string.aweme_open_ssl_continue);
            ad.setTitle(R.string.aweme_open_ssl_warning);
            ad.setTitle(message);
            ad.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.aweme_open_ssl_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelLoad(handler);
                }
            });
            ad.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.aweme_open_ssl_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelLoad(handler);
                }
            });
            ad.setCanceledOnTouchOutside(false);
            ad.show();
        } catch (Exception e) {
            // ignore
            cancelLoad(handler);
        }
    }

    /**
     * cancel load when error
     */
    protected void cancelLoad(SslErrorHandler handler) {
        if (handler != null) {
            handler.cancel();
        }
        showNetworkErrorDialog(OP_ERROR_NETWORK_ERROR);
        isShowNetworkError = true;
    }

    protected void showNetworkErrorDialog(final int errCode) {
        if (mBaseErrorDialog != null && mBaseErrorDialog.isShowing()) {
            return;
        }
        if (mBaseErrorDialog == null) {
            View mDialogView = LayoutInflater.from(this).inflate(R.layout.layout_open_network_error_dialog, null, false);

            mDialogView.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel(errCode);
                }
            });

            mBaseErrorDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo))
                    .setView(mDialogView)
                    .setCancelable(false)
                    .create();

        }
        mBaseErrorDialog.show();
    }
}