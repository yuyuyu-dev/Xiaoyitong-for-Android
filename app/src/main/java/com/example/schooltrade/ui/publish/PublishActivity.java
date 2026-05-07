package com.example.schooltrade.ui.publish;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

public class PublishActivity extends BaseActivity {
    private EditText etTitle, etContent, etPrice, etWant;
    private RadioGroup rgType;
    private int type = 0;

    @Override
    protected int getLayoutId() { return R.layout.activity_publish; }

    @Override
    protected void initView() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        etPrice = findViewById(R.id.et_price);
        etWant = findViewById(R.id.et_want);
        rgType = findViewById(R.id.rg_type);
        Button btnPublish = findViewById(R.id.btn_publish);

        rgType.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.rb_sell) {
                type = 0;
                etPrice.setVisibility(View.VISIBLE);
                etWant.setVisibility(View.GONE);
            } else {
                type = 1;
                etWant.setVisibility(View.VISIBLE);
                etPrice.setVisibility(View.GONE);
            }
        });

        btnPublish.setOnClickListener(v -> publish());
    }

    private void publish() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        if (title.isEmpty() || content.isEmpty()) {
            ToastUtil.show(this, "标题和描述不能为空");
            return;
        }

        double price = 0;
        if (type == 0) {
            String priceStr = etPrice.getText().toString().trim();
            if (!priceStr.isEmpty()) price = Double.parseDouble(priceStr);
        }
        String want = type == 1 ? etWant.getText().toString().trim() : null;

        Goods goods = new Goods();
        goods.setUserId(UserSession.getCurrentUser().getUserId());
        goods.setTitle(title);
        goods.setContent(content);
        goods.setPrice(price);
        goods.setPublishType(type);
        goods.setWantGoods(want);

        showLoading();
        new Thread(() -> {
            boolean res = GoodsDao.publishGoods(goods);
            runOnUiThread(() -> {
                hideLoading();
                if (res) {
                    ToastUtil.show(this, "发布成功");
                    finish();
                } else {
                    ToastUtil.show(this, "发布失败");
                }
            });
        }).start();
    }

    @Override
    protected void initData() {}
}