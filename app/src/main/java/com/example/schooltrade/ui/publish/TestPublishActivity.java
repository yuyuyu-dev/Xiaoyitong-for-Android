package com.example.schooltrade.ui.publish;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.utils.ToastUtil;

/**
 * 测试用发布Activity - 用于排查闪退问题
 */
public class TestPublishActivity extends BaseActivity {
    private static final String TAG = "TestPublishActivity";

    @Override
    protected int getLayoutId() {
        Log.d(TAG, "getLayoutId 被调用");
        return R.layout.activity_publish_test;
    }

    @Override
    protected void initView() {
        Log.d(TAG, "initView 开始");
        try {
            EditText etTestTitle = findViewById(R.id.et_test_title);
            Button btnTestBack = findViewById(R.id.btn_test_back);
            
            Log.d(TAG, "etTestTitle: " + (etTestTitle != null ? "找到" : "null"));
            Log.d(TAG, "btnTestBack: " + (btnTestBack != null ? "找到" : "null"));
            
            if (etTestTitle == null || btnTestBack == null) {
                Log.e(TAG, "控件未找到！");
                ToastUtil.show(this, "页面初始化失败");
                finish();
                return;
            }
            
            btnTestBack.setOnClickListener(v -> {
                Log.d(TAG, "点击返回按钮");
                finish();
            });
            
            Log.d(TAG, "initView 完成");
        } catch (Exception e) {
            Log.e(TAG, "initView 异常: " + e.getMessage(), e);
            e.printStackTrace();
            ToastUtil.show(this, "页面加载异常：" + e.getMessage());
            finish();
        }
    }

    @Override
    protected void initData() {
        Log.d(TAG, "initData 被调用");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate 开始");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate 完成");
    }
}
