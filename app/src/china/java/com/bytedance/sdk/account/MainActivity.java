package com.bytedance.sdk.account;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.DYOpenApi;
import com.bytedance.sdk.open.aweme.authorize.Authorization;
import com.bytedance.sdk.open.aweme.base.DYImageObject;
import com.bytedance.sdk.open.aweme.base.DYMediaContent;
import com.bytedance.sdk.open.aweme.base.DYVideoObject;
import com.bytedance.sdk.open.aweme.impl.DYOpenApiFactory;
import com.bytedance.sdk.open.aweme.share.Share;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static boolean IS_AUTH_BY_M = false;
    public static final String CODE_KEY = "code";

    DYOpenApi bdOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;

    EditText mSetDefaultHashTag;

    EditText mMediaPathList;

    Button mAddMedia;

    Button mClearMedia;

    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<String> mUri = new ArrayList<>();

    private String mScope = "user_info";
    private String mOptionalScope1 = "friend_relation";
    private String mOptionalScope2 = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bdOpenApi = DYOpenApiFactory.create(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 调用wap授权预加载
        Authorization.Request request = new Authorization.Request();
        request.scope = mScope;
        request.optionalScope1 = mOptionalScope2;
        request.optionalScope0 = mOptionalScope1;
        request.state = "ww";
        request.wapRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        // wap预加载接口，需要和sendAuthLogin或者sendInnerWebAuthRequest使用配置相同的SendAuth.Request，但不需要是同一实例
        bdOpenApi.preloadWebAuth(request);

        findViewById(R.id.go_to_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果本地未安装抖音或者抖音的版本过低，会直接自动调用 web页面 进行授权
                sendAuth(false);
            }
        });

        findViewById(R.id.go_to_auth_just_through_web).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAuth(true);
            }
        });

        findViewById(R.id.go_to_system_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
            }
        });

        findViewById(R.id.set_scope).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetScopeActivity.show(MainActivity.this, mScope, mOptionalScope1, mOptionalScope2, SET_SCOPE_REQUEST);
            }
        });

        mShareToDouyin = findViewById(R.id.share_to_douyin);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mMediaPathList = findViewById(R.id.media_text);
        mAddMedia = findViewById(R.id.add_photo_video);
        mClearMedia = findViewById(R.id.clear_media);

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

        mShareToDouyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(currentShareType);
            }
        });

    }

    private boolean sendAuth(boolean isWebAuth) {
        Authorization.Request request = new Authorization.Request();
        request.scope = mScope;                          // 用户授权时必选权限
        request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
        request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
        request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
//        request.wapRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;     // 指定wap授权页横竖屏展示，不指定时由系统控制
        if (isWebAuth) {
            return bdOpenApi.sendInnerWebAuthRequest(request);     // 打开wap授权页进行授权
        } else {
            return bdOpenApi.sendAuthLogin(request);               // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权
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
                    mMediaPathList.setText(mMediaPathList.getText().append("\n").append(uri.getPath()));
                    mShareToDouyin.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag.setVisibility(View.VISIBLE);
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
                DYImageObject imageObject = new DYImageObject();
                imageObject.mImagePaths = mUri;
                DYMediaContent mediaContent = new DYMediaContent();
                mediaContent.mMediaObject = imageObject;
                request.mMediaContent = mediaContent;
                request.mState = "ww";
                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    request.mHashTag = mSetDefaultHashTag.getText().toString();
                }
                break;
            case Share.VIDEO:
                DYVideoObject videoObject = new DYVideoObject();
                videoObject.mVideoPaths = mUri;
                DYMediaContent content = new DYMediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;

                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    request.mHashTag = mSetDefaultHashTag.getText().toString();
                }
                request.mState = "ss";
                break;
        }

        return bdOpenApi.share(request);
    }
}