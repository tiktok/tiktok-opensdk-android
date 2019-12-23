package com.bytedance.sdk.account;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.TikTokConstants;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.TikTokImageObject;
import com.bytedance.sdk.open.aweme.base.TikTokMediaContent;
import com.bytedance.sdk.open.aweme.base.TikTokVideoObject;
import com.bytedance.sdk.open.aweme.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.share.Share;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int targetAppId = TikTokConstants.TARGET_APP.AWEME; // 默认抖音
    public static final String CODE_KEY = "code";

    TiktokOpenApi tiktokOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToTikTok;

    EditText mMediaPathList;

    Button mAddMedia;

    Button mClearMedia;

    EditText mSetDefaultHashTag;

    RadioGroup mTargetApp;

    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<String> mUri = new ArrayList<>();

    private String mScope = "user_info";
    private String mOptionalScope1 = "friend_relation";
    private String mOptionalScope2 = "message";

    public static boolean IS_AUTH_BY_M = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tiktokOpenApi = TikTokOpenApiFactory.create(this, targetAppId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        findViewById(R.id.go_to_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果本地未安装抖音或者抖音的版本过低，会直接自动调用 web页面 进行授权
                sendAuth();
            }
        });

        findViewById(R.id.go_to_system_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
            }
        });

        findViewById(R.id.auth_commonparam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authorization.Request request = new Authorization.Request();
                request.scope = mScope;                          // 用户授权时必选权限
                request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
                request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
                request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。

                Bundle bundle = new Bundle();
                CommonParamTest test = new CommonParamTest();
                test.setmAc("ac");
                test.setmAid("aid");
                test.setmAppName("douyin");
                test.setmChannel("channel");
                Gson gson = new Gson();

                bundle.putString("internal_secure_common_params", gson.toJson(test));
                request.extras = bundle;
                tiktokOpenApi.authorize(request);

            }
        });

        mShareToTikTok = findViewById(R.id.share_to_tiktok);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mMediaPathList = findViewById(R.id.media_text);
        mAddMedia = findViewById(R.id.add_photo_video);
        mClearMedia = findViewById(R.id.clear_media);
        mTargetApp = findViewById(R.id.target_app);

        mTargetApp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.app_aweme:
                        targetAppId = TikTokConstants.TARGET_APP.AWEME;
                        createTiktokApiImpl(TikTokConstants.TARGET_APP.AWEME);
                        break;
                    case R.id.app_tiktok:
                        targetAppId = TikTokConstants.TARGET_APP.TIKTOK;
                        IS_AUTH_BY_M = false;
                        createTiktokApiImpl(TikTokConstants.TARGET_APP.TIKTOK);
                        break;
                    case R.id.app_tiktok_m:
                        IS_AUTH_BY_M = true;
                        targetAppId = TikTokConstants.TARGET_APP.TIKTOK;
                        createTiktokApiImpl(TikTokConstants.TARGET_APP.TIKTOK);
                        break;
                }
            }
        });

        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemGallery();
            }
        });

        mClearMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUri.clear();
                mMediaPathList.setText("");
            }
        });

        mShareToTikTok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(currentShareType);
            }
        });

    }

    private void  createTiktokApiImpl(int targetApp) {
        tiktokOpenApi = TikTokOpenApiFactory.create(this,targetApp);
    }

    private boolean sendAuth() {
        Authorization.Request request = new Authorization.Request();
        request.scope = mScope;                          // 用户授权时必选权限
        request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
        request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
        request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
        if (tiktokOpenApi.isAppSupportAuthorization()) {
            return tiktokOpenApi.authorizeNative(request);
        } else {
            return tiktokOpenApi.authorizeWeb(request, TikTokGeckoWebActivity.class);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
                    openSystemGallery();
                } else {
                    Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_GALLERY:
                    Uri uri = data.getData();
                    mUri.add(UriUtil.convertUriToPath(this,uri));
                    mMediaPathList.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag.setVisibility(View.VISIBLE);
                    mMediaPathList.setText(mMediaPathList.getText().append("\n").append(uri.getPath()));
                    mShareToTikTok.setVisibility(View.VISIBLE);
                    mAddMedia.setVisibility(View.VISIBLE);
                    mClearMedia.setVisibility(View.VISIBLE);

                    break;

                case SET_SCOPE_REQUEST:
                    mScope = data.getStringExtra(SetScopeActivity.SCOPE_KEY);
                    mOptionalScope1 = data.getStringExtra(SetScopeActivity.OPTIONAL_1_SCOPE_KEY);
                    mOptionalScope2 = data.getStringExtra(SetScopeActivity.OPTIONAL_2_SCOPE_KEY);
                    break;
            }
        }
    }

    private void openSystemGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.add_photo_video)
                .setNegativeButton(R.string.video, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentShareType = Share.VIDEO;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("video/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                })
                .setPositiveButton(R.string.image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentShareType = Share.IMAGE;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean share(int shareType) {
        Share.Request request = new Share.Request();
        switch (shareType) {
            case Share.IMAGE:
                TikTokImageObject imageObject = new TikTokImageObject();
                imageObject.mImagePaths = mUri;
                TikTokMediaContent mediaContent = new TikTokMediaContent();
                mediaContent.mMediaObject = imageObject;
                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    request.mHashTag = mSetDefaultHashTag.getText().toString();
                }
                request.mMediaContent = mediaContent;
                request.mState = "ww";
                request.mTargetApp = targetAppId;
                break;
            case Share.VIDEO:
                TikTokVideoObject videoObject = new TikTokVideoObject();
                videoObject.mVideoPaths = mUri;
                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    request.mHashTag = mSetDefaultHashTag.getText().toString();
                }
                TikTokMediaContent content = new TikTokMediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;
                request.mState = "ss";
//                request.callerLocalEntry = "com.xxx.xxx...activity";

                // 0.0.1.1版本新增分享带入小程序功能，具体请看官网
//                TikTokMicroAppInfo mMicroInfo = new TikTokMicroAppInfo();
//                mMicroInfo.setAppTitle("小程序title");
//                mMicroInfo.setDescription("小程序描述");
//                mMicroInfo.setAppId("ttef9b992670b151ec");
//                mMicroInfo.setAppUrl("pages/movie/index?utm_source=share_wxapp&cityId=10&cityName=%E4%B8%8A%E6%B5%B7");
//                request.mMicroAppInfo = mMicroInfo;

                break;
        }

        return tiktokOpenApi.share(request);
    }
}