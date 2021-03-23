package com.bytedance.sdk.open.tiktok.base;

/**
 * To check app if supports sharing and authorizing
 */
public interface IAPPCheckHelper {

    boolean isAppSupportAuthorization();

    boolean isAppSupportShare();

    boolean isAppInstalled();

    boolean isShareSupportFileProvider();

    String getPackageName();

    String getRemoteAuthEntryActivity();

    boolean isAppSupportAPI(int requiredApi);
}
