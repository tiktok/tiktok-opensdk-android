package com.bytedance.sdk.account;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bytedance.sdk.open.tiktok.TikTokOpenConfig;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;
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
    public static final String SHARE_PREFS = "SharePrefs" ;
    public static final String IS_ENVIRONMENT_BOE = "environment" ;

    TikTokOpenApi tiktokOpenApi;
    SharedPreferences sharedPreferences;

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    Button mShareToDouyin;
    Button mSystemShare;

    EditText mMediaPathList;

    Switch envSwitch;

    TextView envTitle;

    Button mClearMedia;

    EditText mSetDefaultHashTag;
    EditText mSetDefaultHashTag1;

    LinearLayout mAutoAttachAnchorToggleField;
    LinearLayout mExtraField;
    LinearLayout mAnchorSourceTypeField;

    EditText mSetExtra;
    EditText mSetAnchorSourceType;
    ToggleButton mAutoAttachAnchorToggle;

    TextView mExtraShareOptionText;
    TextView mVideoKitDisableMusicText;
    ToggleButton mVideoKitDisableMusicToggle;

    View greenScreenSetting;
    ToggleButton mGreenScreenToggle;

    static final int PHOTO_REQUEST_GALLERY = 10;
    static final int SET_SCOPE_REQUEST = 11;

    int currentShareType;

    private ArrayList<Uri> mUri = new ArrayList<>();
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
            // Set the status bar to be transparent
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        sharedPreferences = getSharedPreferences(SHARE_PREFS, Context.MODE_PRIVATE);
        tiktokOpenApi = TikTokOpenApiFactory.create(this);

        findViewById(R.id.go_to_selected_auth).setOnClickListener(v ->{

            if (tiktokOpenApi == null) {
                Toast.makeText(getApplication(), getString(R.string.tiktok_open_api_not_instantiated), Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            String scope = "";

            if (((ToggleButton)findViewById(R.id.music_collection_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.music_collection_scope));
            }

            if (((ToggleButton)findViewById(R.id.interests_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.interests_scope));
            }

            if (((ToggleButton)findViewById(R.id.sound_create_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.share_sound_create_scope));
            }

            if (((ToggleButton)findViewById(R.id.user_basic_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.user_info_basic_scope));
            }

            if (((ToggleButton)findViewById(R.id.user_username_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.user_info_username_scope));
            }

            if (((ToggleButton)findViewById(R.id.user_email_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.user_info_email_scope));
            }

            if (((ToggleButton)findViewById(R.id.user_email_toggle)).isChecked()) {
                scope = appendToScope(scope, getString(R.string.user_info_phone_scope));
            }

            if (scope.isEmpty()) {
                Toast.makeText(getApplication(), getString(R.string.tiktok_open_api_not_instantiated), Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            sendAuth(scope);
        });

        findViewById(R.id.go_to_system_picture).setOnClickListener(
                v -> ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100));

        mShareToDouyin = findViewById(R.id.share_to_tiktok);
        mSystemShare = findViewById(R.id.system_share);
        mSetDefaultHashTag = findViewById(R.id.set_default_hashtag);
        mSetDefaultHashTag1 = findViewById(R.id.set_default_hashtag1);
        mMediaPathList = findViewById(R.id.media_text);
        mClearMedia = findViewById(R.id.clear_media);
        mExtraField = findViewById(R.id.extra_field);
        mAnchorSourceTypeField = findViewById(R.id.anchor_source_field);
        mSetExtra = findViewById(R.id.extra_edit_text);
        mSetAnchorSourceType = findViewById(R.id.anchor_source_edit_text);
        mAutoAttachAnchorToggleField = findViewById(R.id.anchor_auto_attach_toggle_field);
        mAutoAttachAnchorToggle = findViewById(R.id.auto_attach_anchor_toggle);
        mExtraShareOptionText = findViewById(R.id.internal_share_options);
        mVideoKitDisableMusicText = findViewById(R.id.share_disable_music_option);
        mVideoKitDisableMusicToggle = findViewById(R.id.share_disable_music_option_toggle);
        mGreenScreenToggle = findViewById(R.id.greenscreen_toggle);
        greenScreenSetting = findViewById(R.id.greenscreen_setting);

        mClearMedia.setOnClickListener( v -> {
            mUri.clear();
            mMediaPathList.setText("");
        });

        mAutoAttachAnchorToggle.setOnCheckedChangeListener((v, checked) -> {
            if (checked) {
                mExtraField.setVisibility(View.VISIBLE);
                mAnchorSourceTypeField.setVisibility(View.VISIBLE);
            } else {
                mExtraField.setVisibility(View.GONE);
                mAnchorSourceTypeField.setVisibility(View.GONE);
            }
        });

        mShareToDouyin.setOnClickListener( v -> share(currentShareType));
        mSystemShare.setOnClickListener( v -> systemShare(currentShareType));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = (MenuItem) menu.findItem(R.id.envSwitchItem);
        envSwitch = (Switch) menuItem.getActionView().findViewById(R.id.switchAB);
        envTitle = (TextView) menuItem.getActionView().findViewById(R.id.envTitle);

        boolean isBOE = sharedPreferences.getBoolean(IS_ENVIRONMENT_BOE, false);
        envSwitch.setChecked(isBOE);
        if (isBOE) {
            envTitle.setText(getString(R.string.boe));
            initClientKey(false);
        } else {
            envTitle.setText(getString(R.string.prod));
            initClientKey(true);
        }

        envSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
               updatePreferences(true);
                initClientKey(false);
                envTitle.setText(getString(R.string.boe));
                Toast.makeText(getApplication(), getString(R.string.boe_env), Toast.LENGTH_SHORT)
                        .show();
            } else {
                updatePreferences(false);
                initClientKey(true);
                envTitle.setText(getString(R.string.prod));
                Toast.makeText(getApplication(), getString(R.string.prod_env), Toast.LENGTH_SHORT)
                        .show();
            }
        });
        return true;
    }

    private String appendToScope(String scope, String newScope) {
        if (scope.isEmpty()) {
            return newScope;
        }
        scope += "," + newScope;
        return scope;
    }

    private void initClientKey(boolean isProd) {
        TikTokOpenApiFactory.init(isProd ? prodTiktokOpenConfig : boeTiktokOpenConfig);
        tiktokOpenApi = TikTokOpenApiFactory.create(this);
    }

    private void updatePreferences(boolean isBOE) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_ENVIRONMENT_BOE, isBOE);
        editor.commit();
    }

    private  void createTikTokImplApi(int targetApp) {
        tiktokOpenApi = TikTokOpenApiFactory.create(this);
    }

    private boolean sendAuth(String scope) {
        Authorization.Request request = new Authorization.Request();
        request.scope = scope;                      // Permissions for user authorization
        request.state = "ww";                       // Used to maintain the status of the request and callback, and bring it back to the third party as it is after the authorization request.
        return tiktokOpenApi.authorize(request);    // Give priority to using the Tiktok app for authorization. If the Tiktok app cannot be authorized due to the version or other reasons, use the wap page authorization
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
                    mSetDefaultHashTag1.setVisibility(View.VISIBLE);
                    mShareToDouyin.setVisibility(View.VISIBLE);
                    mSystemShare.setVisibility(View.VISIBLE);
                    mClearMedia.setVisibility(View.VISIBLE);
                    mAutoAttachAnchorToggleField.setVisibility(View.VISIBLE);
                    mExtraShareOptionText.setVisibility(View.VISIBLE);
                    mVideoKitDisableMusicText.setVisibility(View.VISIBLE);
                    mVideoKitDisableMusicToggle.setVisibility(View.VISIBLE);
                    greenScreenSetting.setVisibility(View.VISIBLE);

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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        builder.setMessage(R.string.add_photo_video)
                .setNegativeButton(R.string.video, (dialog, which) -> {
                    currentShareType = Share.VIDEO;
                    intent.setType("video/*");
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
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

        if (tiktokOpenApi == null) {
            Toast.makeText(getApplication(), getString(R.string.tiktok_open_api_not_instantiated), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (!tiktokOpenApi.isShareSupportFileProvider() ||
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            Toast.makeText(MainActivity.this, "Version does not match", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> hashtags = new ArrayList<>();

        if (!TextUtils.isEmpty(mSetDefaultHashTag.getText())) {
            hashtags.add(mSetDefaultHashTag.getText().toString());
        }

        String openPlatformExtra = null;
        if (mAutoAttachAnchorToggle.isChecked() && !TextUtils.isEmpty(mSetExtra.getText())) {
            openPlatformExtra = mSetExtra.getText().toString();
        }

        String anchorSourceType = null;
        if (mAutoAttachAnchorToggle.isChecked() && !TextUtils.isEmpty(mSetAnchorSourceType.getText())) {
            anchorSourceType = mSetAnchorSourceType.getText().toString();
        }

        Handler handler = new Handler();
        String finalAnchorSourceType = anchorSourceType;
        String finalOpenPlatformExtra = openPlatformExtra;
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                ShareRequest.Builder requestBuilder = ShareRequest.builder()
                        .hashtags(hashtags)
                        .anchorSourceType(finalAnchorSourceType)
                        .extra(finalOpenPlatformExtra);
                if (mVideoKitDisableMusicToggle.isChecked()) {
                    requestBuilder.putExtraShareOptions(ParamKeyConstants.ShareOptions.TIKTOK_VIDEOKIT_DISABLE_MUSIC_SELECTION, 1);
                }
                if (mGreenScreenToggle.isChecked()) {
                    requestBuilder.shareFormat(Share.Format.GREEN_SCREEN);
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
                            String filePath = UriUtil.convertUriToPath(MainActivity.this,mUri.get(i));;
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