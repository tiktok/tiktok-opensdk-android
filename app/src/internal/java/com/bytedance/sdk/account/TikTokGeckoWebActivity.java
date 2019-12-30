package com.bytedance.sdk.account;

import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public class TikTokGeckoWebActivity extends TikTokWebAuthorizeActivity {


    @Override
    public void configWebView() {
        mContentWebView.setWebViewClient(new AuthClient());

    }


    public class AuthClient extends AuthWebViewClient {
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.i("hello", "hello");
            return super.shouldInterceptRequest(view, request);
        }
    }
}
