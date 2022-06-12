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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
    public static final String SHARE_SOUND = "share_sound";
    public static final String SHARE_PREFS = "SharePrefs";
    public static final String IS_ENVIRONMENT_BOE = "environment";

    TikTokOpenApi tikTokOpenApi;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;

    EditText mMediaPathList;


    Button mClearMedia;

    EditText mSetDefaultHashTag;

    EditText mSetDefaultHashTag2;
    Button mSystemShare;

    TextView mExtraShareOptionText;
    TextView mVideoKitDisableMusicText;
    ToggleButton mVideoKitDisableMusicToggle;


    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<Uri> mUri = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oversea_external_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        tikTokOpenApi = TikTokOpenApiFactory.create(this);


        findViewById(R.id.go_to_selected_auth).setOnClickListener(v ->{

            if (tikTokOpenApi == null) {
                Toast.makeText(getApplication(), getString(R.string.tiktok_open_api_not_instantiated), Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            sendAuth(getString(R.string.user_info_basic_scope));
        });

        findViewById(R.id.go_to_system_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
            }
        });

        mShareToDouyin = findViewById(R.id.share_to_tiktok);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mSetDefaultHashTag2 = findViewById(R.id.set_default_hashtag1);
        mSystemShare = findViewById(R.id.system_share);
        mMediaPathList = findViewById(R.id.media_text);
        mClearMedia = findViewById(R.id.clear_media);
        mExtraShareOptionText = findViewById(R.id.share_options);
        mVideoKitDisableMusicText = findViewById(R.id.share_disable_music_option);
        mVideoKitDisableMusicToggle = findViewById(R.id.share_disable_music_option_toggle);

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

        mSystemShare.setOnClickListener( v -> systemShare(currentShareType));

    }

    private String appendToScope(String scope, String newScope) {
        if (scope.isEmpty()) {
            return newScope;
        }
        scope += "," + newScope;
        return scope;
    }

    private boolean sendAuth(String scope) {
        Authorization.Request request = new Authorization.Request();
        request.scope = scope;                      // Permissions for user authorization
        request.state = "ww";                       // Used to maintain the status of the request and callback, and bring it back to the third party as it is after the authorization request.
        return tikTokOpenApi.authorize(request);    // Give priority to using the Tiktok app for authorization. If the Tiktok app cannot be authorized due to the version or other reasons, use the wap page authorization
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mUri.clear();
            StringBuilder uriPaths = new StringBuilder();
            switch (requestCode) {
                case PHOTO_REQUEST_GALLERY:
                    if(data.getClipData() != null && data.getClipData().getItemCount() > 0) {
                        int count = data.getClipData().getItemCount();
                        for(int i = 0; i < count; i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            mUri.add(uri);
                            uriPaths.append(uri.getPath()).append("\n");
                        }
                    } else if(data.getData() != null) {
                        Uri uri = data.getData();
                        mUri.add(uri);
                        uriPaths.append(uri.getPath());
                    }

                    mMediaPathList.setText(uriPaths.toString());
                    mMediaPathList.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag.setVisibility(View.VISIBLE);
                    mSetDefaultHashTag2.setVisibility(View.VISIBLE);
                    mShareToDouyin.setVisibility(View.VISIBLE);
                    mClearMedia.setVisibility(View.VISIBLE);
                    mSystemShare.setVisibility(View.VISIBLE);
                    mExtraShareOptionText.setVisibility(View.VISIBLE);
                    mVideoKitDisableMusicText.setVisibility(View.VISIBLE);
                    mVideoKitDisableMusicToggle.setVisibility(View.VISIBLE);

                    break;
            }
        }
    }

    private void openSystemGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        builder.setMessage(R.string.add_photo_video)
                .setNegativeButton(R.string.video, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentShareType = Share.VIDEO;
                        intent.setType("video/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                })
                .setPositiveButton(R.string.image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentShareType = Share.IMAGE;
                        intent.setType("image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void share(int shareType) {

        if (tikTokOpenApi == null) {
            Toast.makeText(getApplication(), getString(R.string.tiktok_open_api_not_instantiated), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (!tikTokOpenApi.isShareSupportFileProvider() ||
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            Toast.makeText(MainActivity.this, "Version does not match", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> hashtags = new ArrayList<>();

        if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
            hashtags.add(mSetDefaultHashTag.getText().toString());
        }

        if (!TextUtils.isEmpty(mSetDefaultHashTag2.getText())) {
            hashtags.add(mSetDefaultHashTag2.getText().toString());
        }

        Handler handler = new Handler();
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                ShareRequest.Builder requestBuilder = ShareRequest.builder()
                        .hashtags(hashtags);
                if (mVideoKitDisableMusicToggle.isChecked()) {
                    requestBuilder.putExtraShareOptions(Keys.Share.DISABLE_MUSIC_SELECTION, 1);
                }
                switch (shareType) {
                    case Share.IMAGE:
                        ArrayList<String> images = new ArrayList<>();
                        for (int i=0; i<mUri.size(); i++) {
                            String filePath = UriUtil.convertUriToPath(MainActivity.this,mUri.get(i));
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                            File path = new File(getExternalFilesDir(null), "/imageData");
                            path.mkdirs();
                            File file = new File(path, i + ".png");

                            if(bitmap == null) {
                                try {
                                    InputStream ims = getContentResolver().openInputStream(mUri.get(i));
                                    BitmapFactory.Options opts = new BitmapFactory.Options();
                                    opts.inScaled = true;
                                    bitmap = BitmapFactory.decodeStream(ims);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

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
                            } catch (Exception e) {
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
                                (Runnable) () -> {
                                    requestBuilder.mediaType(ShareRequest.MediaType.IMAGE);
                                    requestBuilder.mediaPaths(images);

                                    tikTokOpenApi.share(requestBuilder.build());
                                });
                        break;
                    case Share.VIDEO:
                        ArrayList<String> videos = new ArrayList<>();
                        InputStream is = null;
                        ByteArrayOutputStream out = null;
                        for (int i=0; i<mUri.size(); i++) {
                            String filePath = UriUtil.convertUriToPath(MainActivity.this,mUri.get(i));
                            try {
                                if(filePath == null) {
                                    is = getContentResolver().openInputStream(mUri.get(i));
                                } else {
                                    is = new BufferedInputStream(new FileInputStream(filePath));
                                }

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

                                        tikTokOpenApi.share(requestBuilder.build());
                                    }
                                });
                        break;
                }

            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private void systemShare(int shareType) {

        if(mUri.size() <= 0)
            return;

        Intent shareIntent = new Intent();

        switch (shareType) {
            case Share.IMAGE:
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share images to.."));
                break;
            case Share.VIDEO:
                shareIntent.setType("video/*");
                if(mUri.size() == 1) {
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, mUri.get(0));
                    startActivity(Intent.createChooser(shareIntent, "Share video to.."));
                } else {
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mUri);
                    shareIntent.setType("video/mp4");
                    startActivity(Intent.createChooser(shareIntent, "Share videos to.."));
                }
                break;
        }
    }
}