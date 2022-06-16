package com.bytedance.sdk.open.tiktok.authorize;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.authorize.model.Auth;
import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.bytedance.sdk.open.tiktok.utils.Md5Utils;
import com.bytedance.sdk.open.tiktok.utils.SignatureUtils;

import java.util.List;


public class WebViewHelper {
    /**
     * generate url according to request and host
     * @param context
     * @param request
     * @param host
     * @return
     */
    public static String getLoadUrl(Context context, Auth.Request request, String host, String path) {
        StringBuilder optionalScope = new StringBuilder();
        if (!TextUtils.isEmpty(request.getOptionalScope1())) {
            String[] optionalScope1s = request.getOptionalScope1().split(",");
            for (int i = 0; i < optionalScope1s.length; i++) {
                if (optionalScope.length() > 0) {
                    optionalScope.append(",");
                }
                optionalScope.append(optionalScope1s[i] + ",1");
            }
        }
        if (!TextUtils.isEmpty(request.getOptionalScope0())) {
            String[] optionalScope0s = request.getOptionalScope0().split(",");
            for (int i = 0; i < optionalScope0s.length; i++) {
                if (optionalScope.length() > 0) {
                    optionalScope.append(",");
                }
                optionalScope.append(optionalScope0s[i] + ",0");
            }
        }
        List<String> signs = SignatureUtils.Companion.getMd5Signs(context, request.getCallerPackage());
        Uri.Builder builder = new Uri.Builder()
                .scheme(Keys.Web.SCHEMA_HTTPS)
                .authority(host)
                .path(path)
                .appendQueryParameter(Keys.Web.QUERY_RESPONSE_TYPE, Keys.Web.VALUE_RESPONSE_TYPE_CODE)
                .appendQueryParameter(Keys.Web.QUERY_REDIRECT_URI, request.getRedirectUri())
                .appendQueryParameter(Keys.Web.QUERY_CLIENT_KEY, request.getClientKey())
                .appendQueryParameter(Keys.Web.QUERY_STATE, request.getState())
                .appendQueryParameter(Keys.Web.QUERY_FROM, Keys.Web.VALUE_FROM_OPENSDK)
                .appendQueryParameter(Keys.Web.QUERY_SCOPE, request.getScope())
                .appendQueryParameter(Keys.Web.QUERY_OPTIONAL_SCOPE, optionalScope.toString())
                .appendQueryParameter(Keys.Web.QUERY_SIGNATURE, SignatureUtils.Companion.packageSignature(signs))
                .appendQueryParameter(Keys.Web.QUERY_ENCRIPTION_PACKAGE, Md5Utils.Companion.hexDigest(request.getCallerPackage()))
                .appendQueryParameter(Keys.Web.QUERY_PLATFORM, "android")
                .appendQueryParameter(Keys.Web.QUERY_ACCEPT_LANGUAGE, request.getLanguage());

        return builder.build().toString();
    }
}