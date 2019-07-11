package com.bytedance.sdk.open.aweme.authorize.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

import com.bytedance.sdk.open.aweme.authorize.WebViewHelper;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.aweme.utils.OpenUtils;

import java.lang.ref.WeakReference;


/**
 * 基础的 AuthorizeActivity
 * Created by yangzhirong on 2018/10/10.
 */
public abstract class BaseBDWebAuthorizeActivity extends Activity implements BDApiEventHandler {

    private static final String RES_ID = "id";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STRING = "string";
    protected static final String LOCAL_ENTRY_ACTIVITY = "bdopen.BdEntryActivity"; // 请求授权的结果回调Activity入口

    int OP_ERROR_NO_CONNECTION = -12;
    int OP_ERROR_CONNECT_TIMEOUT = -13;
    int OP_ERROR_NETWORK_ERROR = -15;

    protected WebView mContentWebView;

    protected Authorization.Request mAuthRequest;
    protected AlertDialog mBaseErrorDialog;
    MyHandler mHandler;

    /**
     * 网络是否通畅
     *
     * @return
     */
    protected abstract boolean isNetworkAvailable();

    /**
     * 处理API
     */
    protected abstract boolean handleIntent(Intent intent, BDApiEventHandler eventHandler);

    /**
     * 发送数据回调
     *
     * @param resp
     */
    protected abstract void sendInnerResponse(Authorization.Request req, BaseResp resp);

    /**
     * wap登录页域名
     *
     * @return
     */
    protected abstract String getHost();

    /**
     * wap登录页Path
     *
     * @return
     */
    protected abstract String getAuthPath();

    /**
     * 授权成功后跳转的redirectUrl的domain
     *
     * @return
     */
    protected abstract String getDomain();

    /**
     * errorCode 转 errorMsg
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


    private static class MyHandler extends Handler {
        private final WeakReference<BaseBDWebAuthorizeActivity> mActivty;
        public MyHandler(BaseBDWebAuthorizeActivity activity){
            mActivty =new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOADING_TIME_OUT:
                    BaseBDWebAuthorizeActivity authorizeActivity = mActivty.get();
                    if (authorizeActivity != null) {
                        authorizeActivity.handleLoadingTimeout();
                    }
                    break;
                default:
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mHandler = new MyHandler(this);
        handleIntent(getIntent(), this);
        int layoutId = getResources().getIdentifier("bd_open_base_web_authorize", RES_LAYOUT, getPackageName());
        setContentView(layoutId);
        initView();
        initActions();
        handleRequestIntent();
    }

    @Override
    public void onReq(BaseReq req) {
        if (req instanceof Authorization.Request) {
            mAuthRequest = (Authorization.Request) req;
            mAuthRequest.redirectUri = "https://" + getDomain() + BDOpenConstants.REDIRECT_URL_PATH;
            // 设置wap授权页横竖屏模式
            setRequestedOrientation(mAuthRequest.wapRequestedOrientation);
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        //empty
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        //empty
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        redirectToClientApp("", BDOpenConstants.ErrorCode.ERROR_CODE_CANCEL);
    }

    /**
     * 调用h5进行授权请求
     */
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
            // 开始加载, 等待8s
            mHandler.sendEmptyMessageDelayed(MSG_LOADING_TIME_OUT, 8000);
            startLoading();
            mContentWebView.setWebViewClient(new AuthWebViewClient());
            mContentWebView.loadUrl(WebViewHelper.getLoadUrl(this, argument, getHost(), getAuthPath()));
        }
    }

    private void redirectToClientApp(String code, int errorCode) {
        redirectToClientApp(code, null, errorCode);
    }

    /**
     * 结果返回
     *
     * @param code
     * @param state
     * @param errorCode
     */
    private void redirectToClientApp(String code, String state, int errorCode) {
        Authorization.Response response = new Authorization.Response();
        response.authCode = code;
        response.errorCode = errorCode;
        response.state = state;
        sendInnerResponse(mAuthRequest, response);
        finish();
    }

    /**
     * 结果返回
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            try {
                mContext.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }


    private void initView() {
        int containerId = getResources().getIdentifier("bd_open_rl_container", RES_ID, getPackageName());
        mContainer = (RelativeLayout) findViewById(containerId);
        // 添加取消按钮
        int headerId = getResources().getIdentifier("bd_open_header_view", RES_ID, getPackageName());
        mHeaderView = (RelativeLayout) findViewById(headerId);
        setContainerViewBgColor();

        View headerView = getHeaderView(mHeaderView);
        if (headerView != null) {
            mHeaderView.removeAllViews();
            mHeaderView.addView(headerView);
        }

        int loadingId = getResources().getIdentifier("bd_open_loading_group", RES_ID, getPackageName());
        mLoadingLayout = (FrameLayout) findViewById(loadingId);

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
        params.addRule(RelativeLayout.BELOW, headerId);
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
        settings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓存模式
    }


    @CallSuper
    protected void initActions() {

    }

    public final class AuthWebViewClient extends WebViewClient {
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
                // 加载没有出错
                if (mLastErrorCode == 0 && !isShowNetworkError) {
                    OpenUtils.setViewVisibility(mContentWebView, View.VISIBLE);
                }
                if (mHandler != null) {
                    mHandler.removeMessages(MSG_LOADING_TIME_OUT);
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
            // 加载出错
            showNetworkErrorDialog(OP_ERROR_NETWORK_ERROR);
            isShowNetworkError = true;
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            if (mHandler != null) {
                mHandler.removeMessages(MSG_LOADING_TIME_OUT);
            }
            showSslErrorDialog(handler, error);
        }
    }

    private boolean handleRedirect(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Authorization.Request argument = mAuthRequest;
        if (argument == null || argument.redirectUri == null || !url.startsWith(argument.redirectUri)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        String code = uri.getQueryParameter(BDOpenConstants.WebViewConstants.REDIRECT_QUERY_CODE);
        String state = uri.getQueryParameter(BDOpenConstants.WebViewConstants.REDIRECT_QUERY_STATE);
        String grantedPermissions = uri.getQueryParameter(BDOpenConstants.WebViewConstants.REDIRECT_QUERY_SCOPE);
        if (TextUtils.isEmpty(code)) {
            String errorCodeStr = uri.getQueryParameter(BDOpenConstants.WebViewConstants.REDIRECT_QUERY_ERROR_CODE);
            int errorCode = BDOpenConstants.ErrorCode.ERROR_CODE_UNKNOW;
            if (!TextUtils.isEmpty(errorCodeStr)) {
                try {
                    errorCode = Integer.parseInt(errorCodeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            redirectToClientApp("", errorCode);
            return false;
        }
        redirectToClientApp(code, state, grantedPermissions, BDOpenConstants.ErrorCode.ERROR_CODE_OK);
        return true;
    }

    /**
     * 默认设置背景颜色
     */
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
        if (mHandler != null) {
            mHandler.removeMessages(MSG_LOADING_TIME_OUT);
        }
    }


    protected void startLoading() {
        OpenUtils.setViewVisibility(mLoadingLayout, View.VISIBLE);
    }

    protected void stopLoading() {
        OpenUtils.setViewVisibility(mLoadingLayout, View.GONE);
    }

    /**
     * 取消操作
     * @param errCode
     */
    protected void onCancel(int errCode) {
        redirectToClientApp("", errCode);
    }

    protected void handleLoadingTimeout() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        stopLoading();
        if (mContentWebView != null && mContentWebView.getVisibility() == View.VISIBLE) {
            return;
        }
        isShowNetworkError = true;
        showNetworkErrorDialog(OP_ERROR_CONNECT_TIMEOUT);
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

    /**
     * 页面加载样式, 封装类实现loading样式
     */
    protected View getLoadingView(ViewGroup root) {
        return null;
    }


    /**
     * 添加header页面，封装类不需要返回null
     */
    protected View getHeaderView(ViewGroup root) {
        return null;
    }

    /**
     * 处理webview ssl错误
     */
    protected void showSslErrorDialog(final SslErrorHandler handler, SslError error) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            AlertDialog ad = builder.create();
            int sslError = getResources().getIdentifier("bd_open_ssl_error", RES_STRING, getPackageName());
            String message = mContext.getString(sslError);
            final int errorCode = error.getPrimaryError();
            switch (errorCode) {
                case SslError.SSL_UNTRUSTED:
                    int sslUntrusted = getResources().getIdentifier("bd_open_ssl_untrusted", RES_STRING, getPackageName());
                    message = mContext.getString(sslUntrusted);
                    break;
                case SslError.SSL_EXPIRED:
                    int sslExpired = getResources().getIdentifier("bd_open_ssl_expired", RES_STRING, getPackageName());
                    message = mContext.getString(sslExpired);
                    break;
                case SslError.SSL_IDMISMATCH:
                    int sslMismatched = getResources().getIdentifier("bd_open_ssl_mismatched", RES_STRING, getPackageName());
                    message = mContext.getString(sslMismatched);
                    break;
                case SslError.SSL_NOTYETVALID:
                    int sslNotyetvalid= getResources().getIdentifier("bd_open_ssl_notyetvalid", RES_STRING, getPackageName());
                    message = mContext.getString(sslNotyetvalid);
                    break;
            }
            int sslContinue = getResources().getIdentifier("bd_open_ssl_continue", RES_STRING, getPackageName());
            message += mContext.getString(sslContinue);
            int sslWarning = getResources().getIdentifier("bd_open_ssl_warning", RES_STRING, getPackageName());
            ad.setTitle(sslWarning);
            ad.setTitle(message);
            int sslOk = getResources().getIdentifier("bd_open_ssl_ok", RES_STRING, getPackageName());
            int sslCancel = getResources().getIdentifier("bd_open_ssl_cancel", RES_STRING, getPackageName());
            ad.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(sslOk), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    proceedLoad(handler);
                }
            });
            ad.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(sslCancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelLoad(handler);
                }
            });
            ad.setCanceledOnTouchOutside(false);
            ad.show();
        } catch (Exception e) {
            // ignore
            proceedLoad(handler);
        }
    }

    /**
     * webview 收到ssl错误，继续加载
     */
    protected void proceedLoad(SslErrorHandler handler) {
        if (handler != null) {
            // 开始加载, 等待8s
            mHandler.sendEmptyMessageDelayed(MSG_LOADING_TIME_OUT, 8000);
            handler.proceed();
        }
    }

    /**
     * webview 收到ssl错误 取消加载
     */
    protected void cancelLoad(SslErrorHandler handler) {
        if (handler != null) {
            handler.cancel();
        }
        // 加载出错
        showNetworkErrorDialog(OP_ERROR_NETWORK_ERROR);
        isShowNetworkError = true;
    }

    /**
     * 显示网络错误对话框, 封装类可实现自定义样式
     * @param errCode 网络错误码
     *
     */
    protected void showNetworkErrorDialog(final int errCode) {
        if (mBaseErrorDialog != null && mBaseErrorDialog.isShowing()) {
            return;
        }
        if (mBaseErrorDialog == null) {
            int layoutId = getResources().getIdentifier("bd_open_network_error_dialog", RES_LAYOUT, getPackageName());
            View mDialogView = LayoutInflater.from(this).inflate(layoutId, null, false);
            // 添加取消按钮
            int confirmId = getResources().getIdentifier("bd_open_tv_confirm", RES_ID, getPackageName());
            mDialogView.findViewById(confirmId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel(errCode);
                }
            });
            mBaseErrorDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(mDialogView)
                    .create();
        }
        mBaseErrorDialog.show();
    }
}