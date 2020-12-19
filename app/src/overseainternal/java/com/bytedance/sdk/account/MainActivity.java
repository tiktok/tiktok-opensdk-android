package com.bytedance.sdk.account;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bytedance.sdk.open.tiktok.TikTokOpenConfig;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.share.Share;
import com.bytedance.sdk.open.tiktok.share.ShareRequest;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String CODE_KEY = "code";

    TikTokOpenApi tiktokOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;

    EditText mMediaPathList;

    Switch envSwitch;

    TextView envTitle;

    Button mClearMedia;

    EditText mSetDefaultHashTag;
    EditText mSetDefaultHashTag1;


    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<String> mUri = new ArrayList<>();
    private TikTokOpenConfig prodTiktokOpenConfig;
    private TikTokOpenConfig boeTiktokOpenConfig;

    private String mScope = "user_info";
    private String mOptionalScope1 = "friend_relation";
    private String mOptionalScope2 = "message";

    public static boolean IS_AUTH_BY_M = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oversea_internal_main);
        prodTiktokOpenConfig = new TikTokOpenConfig(BuildConfig.CLIENT_KEY);
        boeTiktokOpenConfig = new TikTokOpenConfig(BuildConfig.CLIENT_KEY_BOE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        tiktokOpenApi = TikTokOpenApiFactory.create(this);

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

        mShareToDouyin = findViewById(R.id.share_to_tiktok);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mSetDefaultHashTag1 = findViewById(R.id.set_default_hashtag1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = (MenuItem) menu.findItem(R.id.envSwitchItem);
        envSwitch = (Switch) menuItem.getActionView().findViewById(R.id.switchAB);
        envTitle = (TextView) menuItem.getActionView().findViewById(R.id.envTitle);

        envSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    initClientKey(false);
                    envTitle.setText(getString(R.string.boe));
                    Toast.makeText(getApplication(), getString(R.string.boe_env), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    initClientKey(true);
                    envTitle.setText(getString(R.string.prod));
                    Toast.makeText(getApplication(), getString(R.string.prod_env), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return true;
    }

    private void initClientKey(boolean isProd) {
        TikTokOpenApiFactory.init(isProd ? prodTiktokOpenConfig : boeTiktokOpenConfig);
        tiktokOpenApi = TikTokOpenApiFactory.create(this);
    }

    private  void createTikTokImplApi(int targetApp) {
        tiktokOpenApi = TikTokOpenApiFactory.create(this);
    }

    private boolean sendAuth() {
        Authorization.Request request = new Authorization.Request();
        request.scope = "user_info,music.collection,user.ue";                          // 用户授权时必选权限
//        request.optionalScope1 = mOptionalScope2;     // 用户授权时可选权限（默认选择）
//        request.optionalScope0 = mOptionalScope1;    // 用户授权时可选权限（默认不选）
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
                    mMediaPathList.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag1.setVisibility(View.VISIBLE);
                    mMediaPathList.setText(mMediaPathList.getText().append("\n").append(uri.getPath()));
                    mShareToDouyin.setVisibility(View.VISIBLE);
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

    private void share(int shareType) {

        if (!tiktokOpenApi.isShareSupportFileProvider() ||
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            Toast.makeText(MainActivity.this, "Version does not match", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> hashtags = new ArrayList<>();

        if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
            hashtags.add(mSetDefaultHashTag.getText().toString());
        }

        Handler handler = new Handler();
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                ShareRequest.Builder requestBuilder = ShareRequest.builder()
                        .hashtags(hashtags);
                switch (shareType) {
                    case Share.IMAGE:
                        ArrayList<String> images = new ArrayList<>();
                        for (int i=0; i<mUri.size(); i++) {
                            String filePath = mUri.get(i);
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                            File path = new File(getExternalFilesDir(null), "/imageData");
                            path.mkdirs();
                            File file = new File(path, i + ".png");

                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

                                out.flush();
                                out.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName()+".fileprovider", file);
                            images.add(uri.toString());
                            grantUriPermission("com.ss.android.ugc.trill",
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            grantUriPermission("com.zhiliaoapp.musically",
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        handler.post(
                                new Runnable() {
                                    public void run()
                                    {
                                        requestBuilder.mediaType(ShareRequest.MediaType.IMAGE);
                                        requestBuilder.mediaPaths(images);

                                        tiktokOpenApi.share(requestBuilder.build());
                                    }
                                });
                        break;
                    case Share.VIDEO:
                        ArrayList<String> videos = new ArrayList<>();
                        InputStream is = null;
                        ByteArrayOutputStream out = null;
                        for (int i=0; i<mUri.size(); i++) {
                            String filePath = mUri.get(i);
                            try {
                                is = new BufferedInputStream(new FileInputStream(filePath));

                                File file = new File(getExternalFilesDir(null), "/videoData");
                                file.mkdirs();

                                OutputStream output = new FileOutputStream(getExternalFilesDir(null) + "/videoData/" + String.valueOf(i) + ".mp4");

                                byte data[] = new byte[1024];

                                int countin;
                                while ((countin = is.read(data)) != -1) {
                                    output.write(data, 0, countin);
                                }

                                output.flush();
                                output.close();
                                is.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            File dir = new File(getExternalFilesDir(null) + "/videoData/" + String.valueOf(i) + ".mp4");
                            Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName()+".fileprovider", dir);
                            videos.add(uri.toString());
                            grantUriPermission("com.ss.android.ugc.trill",
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            grantUriPermission("com.zhiliaoapp.musically",
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        handler.post(
                                new Runnable() {
                                    public void run()
                                    {
                                        requestBuilder.mediaType(ShareRequest.MediaType.VIDEO);
                                        requestBuilder.mediaPaths(videos);

                                        tiktokOpenApi.share(requestBuilder.build());
                                    }
                                });
                        break;
                }

            }
        };

        Thread t = new Thread(r);
        t.start();
    }
}