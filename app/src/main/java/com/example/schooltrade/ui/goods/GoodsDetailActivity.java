package com.example.schooltrade.ui.goods;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schooltrade.R;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.CollectDao;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

public class GoodsDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvUser, tvPrice, tvType, tvContent;
    private Button btnCollect;
    private int goodsId, userId;
    private Goods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        userId = UserSession.getCurrentUser().getUserId();
        goodsId = getIntent().getIntExtra("goodsId", 0);

        initView();
        loadGoodsDetail();
        checkCollectStatus();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvUser = findViewById(R.id.tv_user);
        tvPrice = findViewById(R.id.tv_price);
        tvType = findViewById(R.id.tv_type);
        tvContent = findViewById(R.id.tv_content);
        btnCollect = findViewById(R.id.btn_collect);

        // 收藏/取消收藏
        btnCollect.setOnClickListener(v -> collectGoods());
    }

    // 加载商品详情
    private void loadGoodsDetail() {
        new Thread(() -> {
            goods = GoodsDao.getGoodsById(goodsId);
            runOnUiThread(() -> {
                if (goods != null) {
                    tvTitle.setText(goods.getTitle());
                    tvUser.setText("发布者：" + goods.getUserId());
                    tvContent.setText("商品描述：" + goods.getContent());

                    if (goods.getPublishType() == 0) {
                        tvPrice.setText("价格：¥" + goods.getPrice());
                        tvType.setText("商品类型：出售");
                    } else {
                        tvPrice.setText("价格：置换");
                        tvType.setText("置换需求：" + goods.getWantGoods());
                    }
                }
            });
        }).start();
    }

    // 检查收藏状态
    private void checkCollectStatus() {
        new Thread(() -> {
            boolean isCollect = CollectDao.isCollect(userId, goodsId);
            runOnUiThread(() -> {
                btnCollect.setText(isCollect ? "取消收藏" : "收藏商品");
            });
        }).start();
    }

    // 收藏/取消收藏
    private void collectGoods() {
        new Thread(() -> {
            boolean result;
            if (btnCollect.getText().toString().equals("收藏商品")) {
                result = CollectDao.addCollect(userId, goodsId);
                runOnUiThread(() -> {
                    if (result) {
                        ToastUtil.show(GoodsDetailActivity.this, "收藏成功");
                        btnCollect.setText("取消收藏");
                    }
                });
            } else {
                result = CollectDao.cancelCollect(userId, goodsId);
                runOnUiThread(() -> {
                    if (result) {
                        ToastUtil.show(GoodsDetailActivity.this, "取消收藏");
                        btnCollect.setText("收藏商品");
                    }
                });
            }
        }).start();
    }
}