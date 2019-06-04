package com.bytedance.sdk.open.aweme.base;

import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.DYOpenConstants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by colinyu on 2019/5/22.
 *
 * @author colin.yu@bytedance.com
 */
public class DYMicroAppInfo {

    //小程序id
    private String appId;
    //小程序title
    private String appTitle;

    //小程序描述
    private String description;

    // 小程序入口页
    private String appUrl;

    // 创建两个方法，将整个对象序列化程json字符串，传递给bundle
    public void serialize(Bundle sendBundle) {
        if (sendBundle == null) {
            return;
        }

        Gson gson = new Gson();
        String result = gson.toJson(this);
        sendBundle.putString(DYOpenConstants.Params.SHARE_MICROAPP_INFO, result);
    }

    public static DYMicroAppInfo unserialize(Bundle clientBundle) {
        if (clientBundle == null) {
            return null;
        }

        String info = clientBundle.getString(DYOpenConstants.Params.SHARE_MICROAPP_INFO);
        try {
            if (!TextUtils.isEmpty(info)) {
                Gson microGson = new Gson();
                DYMicroAppInfo microAppInfo = microGson.fromJson(info, DYMicroAppInfo.class);
                return microAppInfo;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
