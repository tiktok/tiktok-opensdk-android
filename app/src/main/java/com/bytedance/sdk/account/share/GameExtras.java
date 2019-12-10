package com.bytedance.sdk.account.share;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GameExtras {

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    public String getGameDeviceId() {
        return mGameDeviceId;
    }

    public void setGameDeviceId(String mGameDeviceId) {
        this.mGameDeviceId = mGameDeviceId;
    }

    public int getShowCaseObjId() {
        return mShowCaseObjId;
    }

    public void setShowCaseObjId(int mShowCaseObjId) {
        this.mShowCaseObjId = mShowCaseObjId;
    }

    public int getEntranceId() {
        return mEntranceId;
    }

    public void setEntranceId(int mEntranceId) {
        this.mEntranceId = mEntranceId;
    }

    public String getAid() {
        return mAid;
    }

    public void setAid(String mAid) {
        this.mAid = mAid;
    }

    public String getGameExtra() {
        return mGameExtra;
    }

    public void setGameExtra(String mGameExtra) {
        this.mGameExtra = mGameExtra;
    }

    public String getClientKey() {
        return mClientKey;
    }

    public void setClientKey(String mClientKey) {
        this.mClientKey = mClientKey;
    }

    @SerializedName("game_name")
    String mGameName;
    @SerializedName("game_device_id")
    String mGameDeviceId;
    @SerializedName("ttgame_showcase_mgr_obj_id")
    int mShowCaseObjId;
    @SerializedName("entrance_id")
    int mEntranceId;
    @SerializedName("aid")
    String mAid;
    @SerializedName("game_extra")
    String mGameExtra;
    @SerializedName("client_key")
    String mClientKey;

}
