package com.bytedance.sdk.account;

import com.google.gson.annotations.SerializedName;

public class CommonParamTest {
    @SerializedName("ac")
    String mAc;
    @SerializedName("aid")
    String mAid;
    @SerializedName("app_name")
    String mAppName;

    public String getmAc() {
        return mAc;
    }

    public void setmAc(String mAc) {
        this.mAc = mAc;
    }

    public String getmAid() {
        return mAid;
    }

    public void setmAid(String mAid) {
        this.mAid = mAid;
    }

    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getmChannel() {
        return mChannel;
    }

    public void setmChannel(String mChannel) {
        this.mChannel = mChannel;
    }

    @SerializedName("channel")
    String mChannel;
}
