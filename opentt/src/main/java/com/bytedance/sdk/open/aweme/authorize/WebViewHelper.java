package com.bytedance.sdk.open.aweme.authorize;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.bytedance.sdk.open.aweme.authorize.BDAuthConstants;
import com.bytedance.sdk.open.aweme.authorize.model.SendAuth;
import com.bytedance.sdk.open.aweme.utils.SignatureUtils;

import java.util.List;

/**
 * webview授权页
 * Created by jianghaiyang on 2018/12/24.
 */
public class WebViewHelper {
    private static WebView mWebView;

    public static void initWebView(Context context){
        mWebView = new WebView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(params);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓存模式
    }

    public static WebView getWebView(Context context){
        if (mWebView == null) {
            initWebView(context);
        }
        return mWebView;
    }

    /**
     * wap授权页预加载
     * @param context
     * @param request
     */
    public static void preload(Context context, SendAuth.Request request, String host, String path) {
        getWebView(context);
        mWebView.loadUrl(getLoadUrl(context, request, host, path));
    }

    /**
     * 根据request和host生成 WAP授权页url
     * @param context
     * @param request
     * @param host
     * @return
     */
    public static String getLoadUrl(Context context, SendAuth.Request request, String host, String path) {
        // 将optionalScope0和optionalScope1拼接成optionalScope = "messag,1,friend_relation,0 ";格式
        StringBuilder optionalScope = new StringBuilder();
        if (!TextUtils.isEmpty(request.optionalScope1)) {
            String[] optionalScope1s = request.optionalScope1.split(",");
            for (int i = 0; i < optionalScope1s.length; i++) {
                if (optionalScope.length() > 0) {
                    optionalScope.append(",");
                }
                optionalScope.append(optionalScope1s[i] + ",1");
            }
        }
        if (!TextUtils.isEmpty(request.optionalScope0)) {
            String[] optionalScope0s = request.optionalScope0.split(",");
            for (int i = 0; i < optionalScope0s.length; i++) {
                if (optionalScope.length() > 0) {
                    optionalScope.append(",");
                }
                optionalScope.append(optionalScope0s[i] + ",0");
            }
        }
        List<String> signs = SignatureUtils.getMd5Signs(context, request.getCallerPackage());
        Uri.Builder builder = new Uri.Builder()
                .scheme(BDAuthConstants.SCHEMA_HTTPS)
                .authority(host)
                .path(path)
                .appendQueryParameter(BDAuthConstants.QUERY_RESPONSE_TYPE, BDAuthConstants.VALUE_RESPONSE_TYPE_CODE)
                .appendQueryParameter(BDAuthConstants.QUERY_REDIRECT_URI, request.redirectUri)
                .appendQueryParameter(BDAuthConstants.QUERY_CLIENT_KEY, request.getClientKey())
                .appendQueryParameter(BDAuthConstants.QUERY_STATE, request.state)
                .appendQueryParameter(BDAuthConstants.QUERY_FROM, BDAuthConstants.VALUE_FROM_OPENSDK)
                .appendQueryParameter(BDAuthConstants.QUERY_SCOPE, request.scope)
                .appendQueryParameter(BDAuthConstants.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
                .appendQueryParameter(BDAuthConstants.QUERY_SIGNATURE, SignatureUtils.packageSignature(signs));
        return builder.build().toString();
    }
}