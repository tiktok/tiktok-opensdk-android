package com.bytedance.sdk.open.aweme.douyin.base;

/**
 * To check app if supports sharing and authorizing
 */
public interface IAPPCheckHelper {

    boolean isAppSupportAuthorization();

    boolean isAppSupportShare();

    boolean isAppInstalled();

    String getPackageName();

    String getRemoteAuthEntryActivity();
}
