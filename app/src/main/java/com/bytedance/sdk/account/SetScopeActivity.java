package com.bytedance.sdk.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * 主要功能：设置授权权限
 * author: ChangLei
 * since: 2019/3/5
 */
public class SetScopeActivity extends Activity {

    public static final String SCOPE_KEY = "scope";
    public static final String OPTIONAL_1_SCOPE_KEY = "optional_1_scope";
    public static final String OPTIONAL_2_SCOPE_KEY = "optional_2_scope";

    public static void show(Activity activity, String scope, String optional1Scope, String optional2Scope, int requestCode) {
        Intent i = new Intent(activity, SetScopeActivity.class);
        if (!TextUtils.isEmpty(scope)) {
            i.putExtra(SCOPE_KEY, scope);
        }
        if (!TextUtils.isEmpty(optional1Scope)) {
            i.putExtra(OPTIONAL_1_SCOPE_KEY, optional1Scope);
        }
        if (!TextUtils.isEmpty(optional2Scope)) {
            i.putExtra(OPTIONAL_2_SCOPE_KEY, optional2Scope);
        }
        activity.startActivityForResult(i, requestCode);
    }

    private String mScope = "";
    private String mOptionalScope1 = "";
    private String mOptionalScope2 = "";

    private EditText mScopeView;
    private EditText mOptionalScope1View;
    private EditText mOptionalScope2View;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_scope);

        mScope = getIntent().getStringExtra(SCOPE_KEY);
        mOptionalScope1 = getIntent().getStringExtra(OPTIONAL_1_SCOPE_KEY);
        mOptionalScope2 = getIntent().getStringExtra(OPTIONAL_2_SCOPE_KEY);

        mScopeView = findViewById(R.id.scope_edit);
        mOptionalScope1View = findViewById(R.id.opentional1_edit);
        mOptionalScope2View = findViewById(R.id.opentional2_edit);

        if (!TextUtils.isEmpty(mScope)) {
            mScopeView.setText(mScope);
        }
        if (!TextUtils.isEmpty(mOptionalScope1)) {
            mOptionalScope1View.setText(mOptionalScope1);
        }
        if (!TextUtils.isEmpty(mOptionalScope2)) {
            mOptionalScope2View.setText(mOptionalScope2);
        }

        findViewById(R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mScope = mScopeView.getText().toString();
                mOptionalScope1 = mOptionalScope1View.getText().toString();
                mOptionalScope2 = mOptionalScope2View.getText().toString();

                Intent result = new Intent();
                result.putExtra(SCOPE_KEY, mScope);
                result.putExtra(OPTIONAL_1_SCOPE_KEY, mOptionalScope1);
                result.putExtra(OPTIONAL_2_SCOPE_KEY, mOptionalScope2);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }
}
