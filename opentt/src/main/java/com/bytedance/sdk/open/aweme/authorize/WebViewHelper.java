package com.bytedance.sdk.open.aweme.authorize;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.utils.Md5Utils;
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
                .scheme(ParamKeyConstants.WebViewConstants.SCHEMA_HTTPS)
                .authority(host)
                .path(path)
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_RESPONSE_TYPE, ParamKeyConstants.WebViewConstants.VALUE_RESPONSE_TYPE_CODE)
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_REDIRECT_URI, request.redirectUri)
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_CLIENT_KEY, request.getClientKey())
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_STATE, request.state)
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_FROM, ParamKeyConstants.WebViewConstants.VALUE_FROM_OPENSDK)
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_SCOPE, request.scope)
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_SIGNATURE, SignatureUtils.packageSignature(signs))
                .appendQueryParameter(ParamKeyConstants.WebViewConstants.QUERY_ENCRIPTION_PACKAGE, Md5Utils.hexDigest(request.getCallerPackage()));
        return builder.build().toString();
    }
}