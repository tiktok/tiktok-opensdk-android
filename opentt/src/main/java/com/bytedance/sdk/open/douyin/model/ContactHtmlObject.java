package com.bytedance.sdk.open.douyin.model;

import android.os.Bundle;

import com.bytedance.sdk.open.douyin.constants.ShareContactsMediaConstants;
import com.google.gson.Gson;


public class ContactHtmlObject {
    public String getHtml() {
        return mHtml;
    }

    public void setHtml(String mHtml) {
        this.mHtml = mHtml;
    }

    public String getDiscription() {
        return mDiscription;
    }

    public void setDiscription(String mDiscription) {
        this.mDiscription = mDiscription;
    }

    private String mHtml;
    private String mDiscription;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    private String mTitle;
    public void serialize(Bundle var1) {
        if (var1 == null) {
            return;
        }

        Gson gson = new Gson();
        String result = gson.toJson(this);
        var1.putSerializable(ShareContactsMediaConstants.ParamKey.SHARE_HTML_KEY, result);
    }


    public static ContactHtmlObject unserialize(Bundle var1) {
        String info = var1.getString(ShareContactsMediaConstants.ParamKey.SHARE_HTML_KEY, "");
        if (info == null) {
            return null;
        }
        Gson gson = new Gson();
        try {
            return gson.fromJson(info, ContactHtmlObject.class);
        } catch (Exception e) {
            return null;
        }
    }
}
