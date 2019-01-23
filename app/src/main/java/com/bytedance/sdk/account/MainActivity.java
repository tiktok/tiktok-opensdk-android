package com.bytedance.sdk.account;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.base.DYImageObject;
import com.bytedance.sdk.account.open.aweme.base.DYMediaContent;
import com.bytedance.sdk.account.open.aweme.base.DYVideoObject;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;
import com.bytedance.sdk.account.open.aweme.share.Share;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TTOpenApi bdOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;

    EditText mMediaPathList;

    Button mAddMedia;

    Button mClearMedia;

    static final int PHOTO_REQUEST_GALLERY = 10;

    int currentShareType;

    private ArrayList<String> mUri = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bdOpenApi = TTOpenApiFactory.create(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 调用wap授权预加载
        SendAuth.Request request = new SendAuth.Request();
        request.scope = "user_info,friend_relation,message";
        request.optionalScope1 = "friend_relation,message";
        request.state = "ww";
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

        mShareToDouyin = findViewById(R.id.share_to_douyin);
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
        SendAuth.Request request = new SendAuth.Request();
        request.scope = "user_info";                            // 用户授权时必选权限
        request.optionalScope1 = "friend_relation,message";     // 用户授权时可选权限（默认不选）
        request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
        if (isWebAuth) {
            return bdOpenApi.sendInnerWebAuthRequest(request);
        } else {
            return bdOpenApi.sendAuthLogin(request);
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
                    mAddMedia.setVisibility(View.VISIBLE);
                    mClearMedia.setVisibility(View.VISIBLE);

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
                break;
            case Share.VIDEO:
                DYVideoObject videoObject = new DYVideoObject();
                videoObject.mVideoPaths = mUri;
                DYMediaContent content = new DYMediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;
                request.mState = "ss";
                break;
        }

        return bdOpenApi.share(request);
    }
}