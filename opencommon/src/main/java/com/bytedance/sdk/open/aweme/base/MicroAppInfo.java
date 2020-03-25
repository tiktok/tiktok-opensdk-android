package com.bytedance.sdk.open.aweme.base;

import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class MicroAppInfo {

    @SerializedName("appId")
    private String appId;

    @SerializedName("appTitle")
    private String appTitle;

    @SerializedName("description")
    private String description;

    @SerializedName("appUrl")
    private String appUrl;

    public void serialize(Bundle sendBundle) {
        if (sendBundle == null) {
            return;
        }

        Gson gson = new Gson();
        String result = gson.toJson(this);
        sendBundle.putString(ParamKeyConstants.ShareParams.SHARE_MICROAPP_INFO, result);
    }

    public static MicroAppInfo unserialize(Bundle clientBundle) {
        if (clientBundle == null) {
            return null;
        }

        String info = clientBundle.getString(ParamKeyConstants.ShareParams.SHARE_MICROAPP_INFO);
        try {
            if (!TextUtils.isEmpty(info)) {
                Gson microGson = new Gson();
                MicroAppInfo microAppInfo = microGson.fromJson(info, MicroAppInfo.class);
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
