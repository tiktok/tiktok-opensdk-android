package com.bytedance.sdk.open.aweme.utils;

import android.text.TextUtils;
import android.view.View;


import com.bytedance.sdk.open.aweme.authorize.model.Authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gy on 2019/1/15.
 */
public class OpenUtils {
    /**
     *  去除数组中重复的字符串
     */
    public static String[] arrayUnique(String[] str) {
        try {
            if (str != null && str.length > 0) {
                List<String> list = new ArrayList<>(Arrays.asList(str));
                Set<String> set = new HashSet<>(list);
                return set.toArray(new String[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     *  针对可选权限，向下兼容版本，把可选权限添加到scope字段
     */
    public static void handleRequestScope(Authorization.Request request) {
        if (request == null) {
            return;
        }

        // 处理空格，否则服务端不认
        if (request.scope != null) {
            request.scope = request.scope.replace(" ", "");
        }
        if (request.optionalScope1 != null) {
            request.optionalScope1 = request.optionalScope1.replace(" ", "");
        }
        if (request.optionalScope0 != null) {
            request.optionalScope0 = request.optionalScope0.replace(" ", "");
        }
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(request.scope)) {
            String[] scopes = request.scope.split(",");
            if (scopes != null) {
                for (int i = 0; i < scopes.length; i++) {
                    if (!TextUtils.isEmpty(scopes[i])) {
                        if (builder.length() > 0) {
                            builder.append(",");
                        }
                        builder.append(scopes[i]);
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(request.optionalScope1)) {
            String[] optionalScope1s = request.optionalScope1.split(",");
            for (int i = 0; i < optionalScope1s.length; i++) {
                if (!TextUtils.isEmpty(optionalScope1s[i])) {
                    if (builder.length() > 0) {
                        builder.append(",");
                        builder.append(optionalScope1s[i]);
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(request.optionalScope0)) {
            String[] optionalScope0s = request.optionalScope0.split(",");
            for (int i = 0; i < optionalScope0s.length; i++) {
                if (!TextUtils.isEmpty(optionalScope0s[i])) {
                    if (builder.length() > 0) {
                        builder.append(",");
                    }
                    builder.append(optionalScope0s[i]);
                }
            }
        }
        String scope = builder.toString();
        if (!TextUtils.isEmpty(scope)) {
            String[] scopeArray = scope.split(",");
            scopeArray = arrayUnique(scopeArray);
            if (scopeArray != null && scopeArray.length > 0) {
                StringBuilder scopeBuilder = new StringBuilder();
                for (String string : scopeArray) {
                    if (!TextUtils.isEmpty(string)) {
                        if (scopeBuilder.length() > 0) {
                            scopeBuilder.append(",");
                        }
                        scopeBuilder.append(string);
                    }
                }
                request.scope = scopeBuilder.toString();
            }
        }
    }


    public static void setViewVisibility(View v, int visiable) {
        if (v == null || v.getVisibility() == visiable || !visibilityValid(visiable)) {
            return;
        }
        v.setVisibility(visiable);
    }

    private static boolean visibilityValid(int visiable) {
        return visiable == View.VISIBLE || visiable == View.GONE || visiable == View.INVISIBLE;
    }
}
