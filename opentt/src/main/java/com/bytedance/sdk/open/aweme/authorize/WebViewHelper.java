package com.bytedance.sdk.open.aweme.authorize;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.utils.SignatureUtils;

import java.util.List;

/**
 * webview授权页
 * Created by jianghaiyang on 2018/12/24.
 */
public class WebViewHelper {
    /**
     * 根据request和host生成 WAP授权页url
     * @param context
     * @param request
     * @param host
     * @return
     */
    public static String getLoadUrl(Context context, Authorization.Request request, String host, String path) {
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
                .scheme(BDOpenConstants.WebViewConstants.SCHEMA_HTTPS)
                .authority(host)
                .path(path)
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_RESPONSE_TYPE, BDOpenConstants.WebViewConstants.VALUE_RESPONSE_TYPE_CODE)
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_REDIRECT_URI, request.redirectUri)
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_CLIENT_KEY, request.getClientKey())
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_STATE, request.state)
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_FROM, BDOpenConstants.WebViewConstants.VALUE_FROM_OPENSDK)
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_SCOPE, request.scope)
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
                .appendQueryParameter(BDOpenConstants.WebViewConstants.QUERY_SIGNATURE, SignatureUtils.packageSignature(signs));
        return builder.build().toString();
    }
}