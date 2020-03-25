package com.bytedance.sdk.account.tiktokapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bytedance.sdk.account.MainActivity;
import com.bytedance.sdk.account.UserInfoActivity;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TiktokOpenApi;

/**
 * 主要功能：接受授权返回结果的activity
 * <p>
 * 注：该activity必须在程序包名下 bdopen包下定义
 * since: 2018/12/25
 */
public class TikTokEntryActivity extends Activity implements IApiEventHandler {

    TiktokOpenApi ttOpenApi;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi = TikTokOpenApiFactory.create(this);
        ttOpenApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        // 授权成功可以获得authCode
        if (resp instanceof Authorization.Response) {
            Authorization.Response response = (Authorization.Response) resp;
            Intent intent = null;
            if (resp.isSuccess()) {

                Toast.makeText(this, "授权成功，获得权限：" + response.grantedPermissions,
                        Toast.LENGTH_LONG).show();

                intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra(MainActivity.CODE_KEY, response.authCode);
                startActivity(intent);
            } else {

                Toast.makeText(this, "授权失败" + response.grantedPermissions,
                        Toast.LENGTH_LONG).show();

            }
            finish();
        } else if (resp instanceof Share.Response) {
            Share.Response response = (Share.Response) resp;
            if (response.isSuccess()) {
                Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
            } else if (response.isCancel()) {
                Toast.makeText(this, "用户手动取消", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "分享失败,errorCode:" + response.errorCode + "suberrorcode " + response.subErrorCode, Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
        Toast.makeText(this, "intent出错啦", Toast.LENGTH_LONG).show();
        finish();
    }
}
