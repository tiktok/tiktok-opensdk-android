package com.bytedance.sdk.account;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.base.ImageObject;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.VideoObject;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String CODE_KEY = "code";
    TikTokOpenApi tikTokOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;

    EditText mMediaPathList;


    Button mClearMedia;

    EditText mSetDefaultHashTag;

    EditText mSetDefaultHashTag2;



    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<String> mUri = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oversea_external_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        tikTokOpenApi = TikTokOpenApiFactory.create(this);


        findViewById(R.id.go_to_system_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
            }
        });

        mShareToDouyin = findViewById(R.id.share_to_tiktok);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mSetDefaultHashTag2 = findViewById(R.id.set_default_hashtag1);

        mMediaPathList = findViewById(R.id.media_text);
        mClearMedia = findViewById(R.id.clear_media);

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

    private  void createTikTokImplApi(int targetApp) {
        tikTokOpenApi = TikTokOpenApiFactory.create(this);
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
                    mSetDefaultHashTag2.setVisibility(View.VISIBLE);

                    mMediaPathList.setText(mMediaPathList.getText().append("\n").append(uri.getPath()));
                    mShareToDouyin.setVisibility(View.VISIBLE);
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
                ImageObject imageObject = new ImageObject();
                imageObject.mImagePaths = mUri;
                MediaContent mediaContent = new MediaContent();
                mediaContent.mMediaObject = imageObject;
                ArrayList<String> hashtags = new ArrayList<>();

                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    hashtags.add(mSetDefaultHashTag.getText().toString());
                }

                if (!TextUtils.isEmpty(mSetDefaultHashTag2.getText())) {
                    hashtags.add(mSetDefaultHashTag2.getText().toString());

                }
//                request.mHashTagList = hashtags;
                request.mMediaContent = mediaContent;
                request.mState = "ww";
                break;
            case Share.VIDEO:
                VideoObject videoObject = new VideoObject();
                videoObject.mVideoPaths = mUri;
                ArrayList<String> hashtagsVideo = new ArrayList<>();

                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    hashtagsVideo.add(mSetDefaultHashTag.getText().toString());
                }

                if (!TextUtils.isEmpty(mSetDefaultHashTag2.getText())) {
                    hashtagsVideo.add(mSetDefaultHashTag2.getText().toString());

                }
//                request.mHashTagList = hashtagsVideo;
                MediaContent content = new MediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;
                request.mState = "ss";
//                request.callerLocalEntry = "com.xxx.xxx...activity";

                // 0.0.1.1版本新增分享带入小程序功能，具体请看官网
//                MicroAppInfo mMicroInfo = new MicroAppInfo();
//                mMicroInfo.setAppTitle("小程序title");
//                mMicroInfo.setDescription("小程序描述");
//                mMicroInfo.setAppId("ttef9b992670b151ec");
//                mMicroInfo.setAppUrl("pages/movie/index?utm_source=share_wxapp&cityId=10&cityName=%E4%B8%8A%E6%B5%B7");
//                request.mMicroAppInfo = mMicroInfo;

                // 指定掉起抖音或者tiktok，不填默认tiktok
                break;
        }

        return tikTokOpenApi.share(request);
    }
}