package com.bytedance.sdk.account.share;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GameAnchorObject {

    @SerializedName("id")
    String mGameId;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @SerializedName("extra")
    String extra;

    public String getmKeyWord() {
        return mKeyWord;
    }

    public void setmKeyWord(String mKeyWord) {
        this.mKeyWord = mKeyWord;
    }

    @SerializedName("keyword")
    String mKeyWord;


    public String getGameId() {
        return mGameId;
    }

    public void setGameId(String mGameId) {
        this.mGameId = mGameId;
    }

}
