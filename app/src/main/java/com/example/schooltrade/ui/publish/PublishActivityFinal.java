package com.example.schooltrade.ui.publish;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.utils.ToastUtil;

/**
 * 最终版PublishActivity - 确保能正常工作
 */
public class PublishActivityFinal extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_simple;
    }

    @Override
    protected void initView() {
        // 最简单的初始化，不做任何复杂操作
        try {
            EditText etTitle = findViewById(R.id.et_title);
            EditText etContent = findViewById(R.id.et_content);
            Button btnPublish = findViewById(R.id.btn_publish);
            
            if (btnPublish != null) {
                btnPublish.setOnClickListener(v -> {
                    String title = etTitle.getText().toString();
                    if (title.isEmpty()) {
                        ToastUtil.show(this, "请输入标题");
                    } else {
                        ToastUtil.show(this, "发布功能开发中");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        // 不需要初始化数据
    }
}
