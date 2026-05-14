package com.example.schooltrade.ui.publish;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.utils.ToastUtil;

/**
 * 极简版PublishActivity - 用于排查闪退问题
 */
public class SimplePublishActivity extends BaseActivity {
    private static final String TAG = "SimplePublish";

    @Override
    protected int getLayoutId() {
        Log.d(TAG, "getLayoutId");
        return R.layout.activity_publish_simple;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate 开始");
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "onCreate super完成");
        } catch (Exception e) {
            Log.e(TAG, "onCreate异常: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initView 开始");
        try {
            EditText etTitle = findViewById(R.id.et_title);
            Log.d(TAG, "etTitle: " + (etTitle != null ? "OK" : "NULL"));
            
            Button btnPublish = findViewById(R.id.btn_publish);
            Log.d(TAG, "btnPublish: " + (btnPublish != null ? "OK" : "NULL"));
            
            if (btnPublish != null) {
                btnPublish.setOnClickListener(v -> {
                    Log.d(TAG, "点击发布");
                    ToastUtil.show(this, "测试成功！");
                });
            }
            
            Log.d(TAG, "initView 完成");
        } catch (Exception e) {
            Log.e(TAG, "initView异常: " + e.getMessage(), e);
            e.printStackTrace();
            ToastUtil.show(this, "初始化失败: " + e.getMessage());
            finish();
        }
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData");
    }
}
