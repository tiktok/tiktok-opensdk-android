package com.bytedance.sdk.open.aweme.base;


import com.bytedance.sdk.open.aweme.common.model.BaseReq;


/**
 * 主要功能：因为海外版的tiktok有两个产品，即Musically和Tiktok，这个用来判断使用哪个
 * <p>
 * 策略是，当两个同时存在时，优先使用Musically
 * <p>
 * author: changlei@bytedance.com
 * since: 2019/3/31
 */
public interface IAPPCheckHelper {

    boolean isAppSupportAuthorization();

    boolean isAppSupportShare();

    boolean isAppInstalled();

    String getPackageName();

    String getRemoteAuthEntryActivity();

    boolean isSupportNewTiktokApi();

//    boolean sendRemoteRequest(String localEntryActivity, BaseReq req);
}
