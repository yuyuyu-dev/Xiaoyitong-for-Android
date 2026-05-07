package com.example.schooltrade.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private ProgressDialog loadingDialog;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initLoadingDialog();
    }

    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(mContext);
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
    public void onDestroy() {
        super.onDestroy();
        hideLoading();
    }
}