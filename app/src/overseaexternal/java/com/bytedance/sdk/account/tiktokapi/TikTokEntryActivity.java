package com.bytedance.sdk.account.tiktokapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.bytedance.sdk.account.MainActivity;
import com.bytedance.sdk.account.UserInfoActivity;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.authorize.model.Auth;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.model.Base;
import com.bytedance.sdk.open.tiktok.share.Share;

/**
 * 主要功能：接受授权返回结果的activity
 * <p>
 * 注：该activity必须在程序包名下 bdopen包下定义
 * since: 2018/12/25
 */
public class TikTokEntryActivity extends Activity implements IApiEventHandler {

    TikTokOpenApi ttOpenApi;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi = TikTokOpenApiFactory.Companion.create(this);
        ttOpenApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(Base.Request req) {

    }

    @Override
    public void onResp(Base.Response resp) {
        // 授权成功可以获得authCode
        if (resp instanceof Auth.Response) {
            Auth.Response response = (Auth.Response) resp;
            Intent intent = null;
            if (resp.isSuccess() && response.getState().equals("ww")) {
                Toast.makeText(this, "Authorization is successful, granted permissions: " + response.getGrantedPermissions(),
                        Toast.LENGTH_LONG).show();

                intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra(MainActivity.CODE_KEY, response.getAuthCode());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Authorization Failed, errorCode: " + response.getErrorCode() + " Message: "+ response.getErrorMsg(),
                        Toast.LENGTH_LONG).show();

            }
            finish();
        } else if (resp instanceof Share.Response) {
            Share.Response response = (Share.Response) resp;
            if (response.isSuccess()) {
                Toast.makeText(this, "Sharing Successful", Toast.LENGTH_SHORT).show();
            } else if (response.isCancelled()) {
                Toast.makeText(this, "User Cancelled Manually", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sharing Failed, errorCode:" + response.getErrorCode() + "suberrorcode " + response.getSubErrorCode(), Toast.LENGTH_SHORT).show();
            }
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
