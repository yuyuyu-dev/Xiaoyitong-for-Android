package com.example.schooltrade.ui.publish;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

/**
 * 发布商品Activity - 原始简单版本（无图片功能，无置换选项）
 */
public class PublishActivitySimple extends BaseActivity {
    private EditText etTitle, etContent, etPrice;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_clean;
    }

    @Override
    protected void initView() {
        try {
            // 返回按钮
            ImageView btnBack = findViewById(R.id.btn_back);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            etTitle = findViewById(R.id.et_title);
            etContent = findViewById(R.id.et_content);
            etPrice = findViewById(R.id.et_price);
            Button btnPublish = findViewById(R.id.btn_publish);

            if (btnPublish != null) {
                btnPublish.setOnClickListener(v -> publish());
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "页面加载失败");
            finish();
        }
    }

    private void publish() {
        try {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            
            if (title.isEmpty() || content.isEmpty()) {
                ToastUtil.show(this, "标题和描述不能为空");
                return;
            }

            double price = 0;
            String priceStr = etPrice.getText().toString().trim();
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    ToastUtil.show(this, "价格格式不正确");
                    return;
                }
            }

            if (UserSession.getCurrentUser() == null) {
                ToastUtil.show(this, "请先登录");
                finish();
                return;
            }

            Goods goods = new Goods();
            goods.setUserId(UserSession.getCurrentUser().getUserId());
            goods.setTitle(title);
            goods.setContent(content);
            goods.setPrice(price);
            goods.setPublishType(0); // 固定为出售类型
            goods.setWantGoods(null);
            goods.setImgUrl(null); // 无图片

            showLoading();
            new Thread(() -> {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        hideLoading();
                        ToastUtil.show(this, "发布异常：" + e.getMessage());
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "发布失败：" + e.getMessage());
        }
    }

    @Override
    protected void initData() {}
}
