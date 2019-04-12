package com.bytedance.sdk.account.open.aweme.api;

import android.support.annotation.NonNull;

import com.bytedance.sdk.account.common.model.BaseReq;

/**
 * 主要功能：因为海外版的tiktok有两个产品，即Musically和Tiktok，这个用来判断使用哪个
 *
 * 策略是，当两个同时存在时，优先使用Musically
 *
 * author: changlei@bytedance.com
 * since: 2019/3/31
 */
public interface TiktokCheckApi {

    @NonNull
    String getSignature();

    @NonNull
    String getPackageName();

    @NonNull
    String getRemoteEntryActivity();

    boolean couldAppBeUsedForAuthorization();

    boolean isAppSupportAPI();

    boolean isAppInstalled();

    boolean sendRemoteRequest(String localEntryActivity, BaseReq req);
}
