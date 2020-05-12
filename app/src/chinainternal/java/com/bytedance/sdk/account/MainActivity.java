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
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;

import com.bytedance.sdk.open.aweme.base.AnchorObject;
import com.bytedance.sdk.open.aweme.base.ImageObject;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.MicroAppInfo;
import com.bytedance.sdk.open.aweme.base.VideoObject;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.ShareToContact;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.model.ContactHtmlObject;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String CODE_KEY = "code";

    DouYinOpenApi tiktokOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;
    Button mGameAnchor;

    Button mMicroButton;
    EditText mMediaPathList;


    Button mClearMedia;

    Button shareToContact;

    EditText mSetDefaultHashTag;
    EditText mSetDefaultHashTag1;


    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<String> mUri = new ArrayList<>();

    private String shareContactPath = "";

    private String mScope = "user_info";
    private String mOptionalScope1 = "friend_relation";
    private String mOptionalScope2 = "message";

    public static boolean IS_AUTH_BY_M = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.china_internal_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        tiktokOpenApi = DouYinOpenApiFactory.create(this);

        findViewById(R.id.go_to_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果本地未安装抖音或者抖音的版本过低，会直接自动调用 web页面 进行授权
                sendAuth();
            }
        });

        findViewById(R.id.require_mobile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authorization.Request request = new Authorization.Request();
                request.scope = "user_info,mobile";                          // 用户授权时必选权限
                request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
                tiktokOpenApi.authorize(request);
            }
        });

        findViewById(R.id.option_mobile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authorization.Request request = new Authorization.Request();
                request.scope = "user_info";// 用户授权时必选权限
                request.optionalScope0 = "mobile";
                request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
                tiktokOpenApi.authorize(request);
            }
        });

        findViewById(R.id.id_mobile_alert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authorization.Request request = new Authorization.Request();
                request.scope = "user_info,mobile_alert";// 用户授权时必选权限
                request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
                tiktokOpenApi.authorize(request);
            }
        });


        findViewById(R.id.go_to_system_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
            }
        });

        findViewById(R.id.share_to_contact_html).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToContactHtml();
            }
        });

        mShareToDouyin = findViewById(R.id.share_to_tiktok);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mSetDefaultHashTag1 = findViewById(R.id.set_default_hashtag1);
        mMediaPathList = findViewById(R.id.media_text);
        mClearMedia = findViewById(R.id.clear_media);
        shareToContact = findViewById(R.id.share_to_contact);

        mGameAnchor = findViewById(R.id.game_anchor);
        mMicroButton = findViewById(R.id.microbutton);

        mClearMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUri.clear();
                mMediaPathList.setText("");
            }
        });

        mGameAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareGameAnchor(currentShareType);
            }
        });

        mMicroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMicroApp(currentShareType);
            }
        });

        mShareToDouyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(currentShareType);
            }
        });

        shareToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToContact();
            }
        });

    }

    private  void createTikTokImplApi(int targetApp) {
        tiktokOpenApi = DouYinOpenApiFactory.create(this);
    }

    private boolean sendAuth() {
        Authorization.Request request = new Authorization.Request();
        request.scope = mScope;                          // 用户授权时必选权限
        request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
        request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
        request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
        return tiktokOpenApi.authorize(request);               // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权

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
                    shareContactPath = UriUtil.convertUriToPath(this, uri);
                    mMediaPathList.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag1.setVisibility(View.VISIBLE);
                    mGameAnchor.setVisibility(View.VISIBLE);
                    mMicroButton.setVisibility(View.VISIBLE);
                    mMediaPathList.setText(mMediaPathList.getText().append("\n").append(uri.getPath()));
                    mShareToDouyin.setVisibility(View.VISIBLE);
                    shareToContact.setVisibility(View.VISIBLE);
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

    private void shareMicroApp(int shareType) {
        Share.Request request = new Share.Request();
        switch (shareType) {
            case Share.IMAGE:
                ImageObject imageObject = new ImageObject();
                imageObject.mImagePaths = mUri;
                MediaContent mediaContent = new MediaContent();
                mediaContent.mMediaObject = imageObject;
                request.mMediaContent = mediaContent;
                request.mState = "ww";
                break;
            case Share.VIDEO:
                VideoObject videoObject = new VideoObject();
                videoObject.mVideoPaths = mUri;
                MediaContent content = new MediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;
                request.mState = "ss";
//                request.callerLocalEntry = "com.xxx.xxx...activity";

                // 0.0.1.1版本新增分享带入小程序功能，具体请看官网
                MicroAppInfo mMicroInfo = new MicroAppInfo();
                mMicroInfo.setAppTitle("小程序title");
                mMicroInfo.setDescription("小程序描述");
                mMicroInfo.setAppId("ttef9b992670b151ec");
                mMicroInfo.setAppUrl("pages/movie/index?utm_source=share_wxapp&cityId=10&cityName=%E4%B8%8A%E6%B5%B7");
                request.mMicroAppInfo = mMicroInfo;

                break;
        }

        tiktokOpenApi.share(request);
    }

    private void shareGameAnchor(int shareType) {
        Share.Request request = new Share.Request();
        switch (shareType) {
            case Share.VIDEO:
                VideoObject videoObject = new VideoObject();
                videoObject.mVideoPaths = mUri;
                MediaContent content = new MediaContent();
                content.mMediaObject = videoObject;
                request.mMediaContent = content;
                request.mState = "ss";

                Gson gson = new Gson();
                GameAnchorObject gameAnchorObject = new GameAnchorObject();
                gameAnchorObject.setGameId("cge56412b084ae57d0");

                GameExtras gameExtras = new GameExtras();
                gameExtras.setGameDeviceId("8899");
                gameExtras.setShowCaseObjId(3000);
                gameExtras.setEntranceId(2);
                gameExtras.setClientKey("aw5k7vhtdbeqdoo8");
                String extraStr = gson.toJson(gameExtras);

                gameAnchorObject.setExtra(extraStr);
                gameAnchorObject.setmKeyWord("第五人格");

                AnchorObject anchorObject = new AnchorObject();
                anchorObject.setAnchorBusinessType(10);
                anchorObject.setAnchorTitle("第五人格");
                String str = gson.toJson(gameAnchorObject);
                anchorObject.setAnchorContent(str);
                request.mAnchorInfo = anchorObject;
                tiktokOpenApi.share(request);
                break;
        }
    }

    private void shareToContact() { // image
        ImageObject cImage = new ImageObject();
        cImage.mImagePaths = mUri;
        MediaContent mediaContent = new MediaContent();
        mediaContent.mMediaObject = cImage;
        ShareToContact.Request request = new ShareToContact.Request();
        request.mMediaContent = mediaContent;
        request.mState = "ww";
        tiktokOpenApi.shareToContacts(request);
    }

    private void shareToContactHtml() {
        ContactHtmlObject htmlObject = new ContactHtmlObject();
        htmlObject.setHtml("https://www.baidu.com");
        htmlObject.setDiscription("bbbbbbbb");
        htmlObject.setTitle("title");
        ShareToContact.Request request = new ShareToContact.Request();
        request.htmlObject = htmlObject;
        tiktokOpenApi.shareToContacts(request);

    }

    private boolean share(int shareType) {
        Share.Request request = new Share.Request();
        switch (shareType) {
            case Share.IMAGE:
                ImageObject imageObject = new ImageObject();
                imageObject.mImagePaths = mUri;
                MediaContent mediaContent = new MediaContent();
                mediaContent.mMediaObject = imageObject;
                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    ArrayList<String> hashtags = new ArrayList<>();
                    hashtags.add(mSetDefaultHashTag.getText().toString());
                    request.mHashTagList = hashtags;
                }
                request.mMediaContent = mediaContent;
                request.mState = "ww";
                break;
            case Share.VIDEO:
                VideoObject videoObject = new VideoObject();
                videoObject.mVideoPaths = mUri;
                if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
                    ArrayList<String> hashtags = new ArrayList<>();
                    hashtags.add(mSetDefaultHashTag.getText().toString());
                    request.mHashTagList = hashtags;
                }
                MediaContent content = new MediaContent();
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