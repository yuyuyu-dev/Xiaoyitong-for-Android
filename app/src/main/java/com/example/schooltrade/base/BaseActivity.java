package com.example.schooltrade.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schooltrade.utils.UserSession;

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog loadingDialog;

    protected abstract int getLayoutId();
    protected abstract void initView();
    protected abstract void initData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserSession.init(getApplicationContext());
        setContentView(getLayoutId());
        initLoadingDialog();
        initView();
        initData();
    }

    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("加载中...");
        loadingDialog.setCancelable(false);
    }

    protected void showLoading() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    protected void hideLoading() {
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }
}