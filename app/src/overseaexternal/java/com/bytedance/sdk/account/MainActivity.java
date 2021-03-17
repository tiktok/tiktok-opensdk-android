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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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

    private void share(int shareType) {

        if (tiktokOpenApi == null) {
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

                                        tikTokOpenApi.share(requestBuilder.build());
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
}