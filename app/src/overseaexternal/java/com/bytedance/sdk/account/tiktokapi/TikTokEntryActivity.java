package com.bytedance.sdk.account.tiktokapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.bytedance.sdk.account.MainActivity;
import com.bytedance.sdk.account.UserInfoActivity;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;
import com.bytedance.sdk.open.tiktok.share.Share;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;

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
            if (resp.isSuccess() && response.state.equals("ww")) {

                Toast.makeText(this, "Authorization is successful, granted permissions: " + response.grantedPermissions,
                        Toast.LENGTH_LONG).show();

                intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra(MainActivity.CODE_KEY, response.authCode);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Authorization Failed, errorCode: " + response.errorCode + " Message: "+ response.errorMsg,
                        Toast.LENGTH_LONG).show();

            }
            finish();
        } else if (resp instanceof Share.Response) {
            Share.Response response = (Share.Response) resp;
            if (response.isSuccess()) {
                Toast.makeText(this, "Sharing Successful", Toast.LENGTH_SHORT).show();
            } else if (response.isCancel()) {
                Toast.makeText(this, "User Cancelled Manually", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sharing Failed, errorCode:" + response.errorCode + "suberrorcode " + response.subErrorCode, Toast.LENGTH_SHORT).show();
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
