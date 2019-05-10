package com.bytedance.sdk.open.aweme.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.account.bdopen.api.BDBaseOpenBuildConstants;
import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.api.BDDataHandler;
import com.bytedance.sdk.open.aweme.DYOpenConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by WangJiaWei on 2019/1/15.
 */
public class ShareImpl {

    private Context mContext;
    private BDOpenConfig openConfig;
    private List<BDDataHandler> handlers = new ArrayList<>();

    public ShareImpl(Context context, BDOpenConfig sConfig) {
        this.mContext = context;
        this.openConfig = sConfig;
        this.handlers.add(new ShareDataHandler());
    }

    /**
     * 发送一个 Request 请求到目标应用
     *
     * @param remotePackageName  目标应用包名
     * @param remoteRequestEntry 目标应用入口 Activity. 用于接收 Request
     * @param request            实际请求数据
     * @param localEntry         当前应用的入口 Activity.用于接收 Response
     * @return
     */
    public boolean share(String localEntry, String remotePackageName, String remoteRequestEntry, Share.Request request) {
        if (TextUtils.isEmpty(remotePackageName) || request == null || mContext == null) {
            return false;
        } else if (!request.checkArgs()) {
            return false;
        } else {
            // packages
            Bundle bundle = new Bundle();
            request.toBundle(bundle);
            bundle.putString(DYOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(DYOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
            bundle.putString(DYOpenConstants.Params.CALLER_SDK_VERSION, BDBaseOpenBuildConstants.VERSION);
            // 没有主动设置CallerLocalEntry
            if (TextUtils.isEmpty(request.callerLocalEntry)) {
                bundle.putString(DYOpenConstants.Params.CALLER_LOCAL_ENTRY, mContext.getPackageName() + "." + localEntry);
            }
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(remotePackageName, buildComponentClassName(remotePackageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);

            // flags
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            try {
                mContext.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private String buildComponentClassName(String packageName, String classPath) {
        return "com.ss.android.ugc.aweme" + "." + classPath;
    }

    public boolean handleShareIntent(Intent intent, BDApiEventHandler eventHandler) {
        if (eventHandler == null) {
            return false;
        }
        if (intent == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }
        int type = bundle.getInt(DYOpenConstants.Params.TYPE);
        for (BDDataHandler handler : handlers) {
            if (handler.handle(type, bundle, eventHandler)) {
                return true;
            }
        }
        eventHandler.onErrorIntent(intent);
        return false;
    }
}
