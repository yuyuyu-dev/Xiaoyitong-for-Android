package com.example.schooltrade.ui.goods;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.schooltrade.R;
import com.example.schooltrade.api.GoodsRequest;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import retrofit2.Response;

public class EditGoodsActivity extends BaseActivity {
    private EditText etTitle, etContent, etPrice;
    private int goodsId;
    private Goods currentGoods;

    @Override
    protected int getLayoutId() { return R.layout.activity_edit_goods; }

    @Override
    protected void initView() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        etPrice = findViewById(R.id.et_price);
        Button btnSave = findViewById(R.id.btn_save);
        goodsId = getIntent().getIntExtra("goodsId", 0);

        btnSave.setOnClickListener(v -> saveEdit());
    }

    @Override
    protected void initData() {
        loadGoodsData();
    }

    private void loadGoodsData() {
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<Goods>> response = RetrofitClient.getInstance()
                    .getApiService().getGoodsById(goodsId).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (!response.isSuccessful() || response.body() == null
                            || !response.body().isSuccess()) {
                        ToastUtil.show(EditGoodsActivity.this, "加载失败");
                        finish();
                        return;
                    }
                    currentGoods = response.body().getData();

                    // 校验所有权
                    int myId = UserSession.getCurrentUser() != null
                        ? UserSession.getCurrentUser().getUserId() : 0;
                    if (currentGoods.getUserId() != myId) {
                        ToastUtil.show(EditGoodsActivity.this, "无权修改他人商品");
                        finish();
                        return;
                    }
                    // 已售出不可修改
                    if (currentGoods.getStatus() != 1) {
                        ToastUtil.show(EditGoodsActivity.this, "已售出商品不可修改");
                        finish();
                        return;
                    }

                    etTitle.setText(currentGoods.getTitle());
                    etContent.setText(currentGoods.getContent());
                    etPrice.setText(String.valueOf(currentGoods.getPrice()));
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(EditGoodsActivity.this, "网络连接失败");
                    finish();
                });
            }
        }).start();
    }

    private void saveEdit() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        if (title.isEmpty() || content.isEmpty()) {
            ToastUtil.show(this, "标题和描述不能为空");
            return;
        }

        String priceStr = etPrice.getText().toString().trim();
        double price = 0;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                ToastUtil.show(this, "价格格式不正确");
                return;
            }
        }

        GoodsRequest goodsReq = new GoodsRequest();
        goodsReq.setTitle(title);
        goodsReq.setContent(content);
        goodsReq.setPrice(price);
        goodsReq.setPublishType(0);
        goodsReq.setCategory(currentGoods.getCategory());

        showLoading();
        new Thread(() -> {
            try {
                Response<Result<String>> response = RetrofitClient.getInstance()
                    .getApiService().updateGoods(goodsId, goodsReq).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        ToastUtil.show(EditGoodsActivity.this, "修改成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String err = response.body() != null
                            ? response.body().getMessage() : "修改失败";
                        ToastUtil.show(EditGoodsActivity.this, err);
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(EditGoodsActivity.this, "网络连接失败");
                });
            }
        }).start();
    }
}
