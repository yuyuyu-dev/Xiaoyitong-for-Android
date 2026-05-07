package com.example.schooltrade.ui.goods;

import android.content.Intent;
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

public class EditGoodsActivity extends BaseActivity {
    private EditText etTitle, etContent, etPrice, etWant;
    private RadioGroup rgType;
    private int goodsId, type = 0;
    private Goods currentGoods;

    @Override
    protected int getLayoutId() { return R.layout.activity_edit_goods; }

    @Override
    protected void initView() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        etPrice = findViewById(R.id.et_price);
        etWant = findViewById(R.id.et_want);
        rgType = findViewById(R.id.rg_type);
        Button btnSave = findViewById(R.id.btn_save);
        goodsId = getIntent().getIntExtra("goodsId", 0);

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

        btnSave.setOnClickListener(v -> saveEdit());
    }

    @Override
    protected void initData() {
        loadGoodsData();
    }

    private void loadGoodsData() {
        showLoading();
        new Thread(() -> {
            currentGoods = GoodsDao.getGoodsById(goodsId);
            runOnUiThread(() -> {
                hideLoading();
                if (currentGoods == null) {
                    ToastUtil.show(this, "加载失败");
                    finish();
                    return;
                }
                etTitle.setText(currentGoods.getTitle());
                etContent.setText(currentGoods.getContent());
                if (currentGoods.getPublishType() == 0) {
                    rgType.check(R.id.rb_sell);
                    etPrice.setText(String.valueOf(currentGoods.getPrice()));
                } else {
                    rgType.check(R.id.rb_exchange);
                    etWant.setText(currentGoods.getWantGoods());
                }
            });
        }).start();
    }

    private void saveEdit() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        if (title.isEmpty() || content.isEmpty()) {
            ToastUtil.show(this, "不能为空");
            return;
        }

        double price = 0;
        if (type == 0 && !etPrice.getText().toString().trim().isEmpty()) {
            price = Double.parseDouble(etPrice.getText().toString().trim());
        }
        String want = type == 1 ? etWant.getText().toString().trim() : null;

        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        goods.setUserId(currentGoods.getUserId());
        goods.setTitle(title);
        goods.setContent(content);
        goods.setPrice(price);
        goods.setPublishType(type);
        goods.setWantGoods(want);

        showLoading();
        new Thread(() -> {
            boolean res = GoodsDao.updateGoods(goods);
            runOnUiThread(() -> {
                hideLoading();
                if (res) {
                    ToastUtil.show(this, "修改成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.show(this, "修改失败");
                }
            });
        }).start();
    }
}