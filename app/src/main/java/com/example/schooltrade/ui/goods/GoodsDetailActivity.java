package com.example.schooltrade.ui.goods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schooltrade.R;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.CollectDao;
import com.example.schooltrade.model.db.DBUtil;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.ui.message.ChatActivity;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

public class GoodsDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvUser, tvPrice, tvType, tvContent;
    private ImageView ivGoodsImage;
    private Button btnCollect, btnContact, btnBuy;
    private int goodsId, userId;
    private Goods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBUtil.init(this);
        setContentView(R.layout.activity_goods_detail);
        
        if (!UserSession.isLogin()) {
            ToastUtil.show(this, "请先登录");
            finish();
            return;
        }
        
        userId = UserSession.getCurrentUser().getUserId();
        goodsId = getIntent().getIntExtra("goodsId", 0);

        initView();
        loadGoodsDetail();
        checkCollectStatus();
    }

    private void initView() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        ivGoodsImage = findViewById(R.id.iv_goods_image);
        tvTitle = findViewById(R.id.tv_title);
        tvUser = findViewById(R.id.tv_user);
        tvPrice = findViewById(R.id.tv_price);
        tvType = findViewById(R.id.tv_type);
        tvContent = findViewById(R.id.tv_content);
        btnCollect = findViewById(R.id.btn_collect);
        btnContact = findViewById(R.id.btn_contact);
        btnBuy = findViewById(R.id.btn_buy);

        // 收藏按钮
        btnCollect.setOnClickListener(v -> collectGoods());
        
        // 联系卖家按钮
        btnContact.setOnClickListener(v -> contactSeller());
        
        // 购买商品按钮
        btnBuy.setOnClickListener(v -> buyGoods());
    }

    /**
     * 购买商品（暂未实现）
     */
    private void buyGoods() {
        ToastUtil.show(this, "软件尚未开发此功能");
    }

    /**
     * 联系卖家
     */
    private void contactSeller() {
        if (goods == null) {
            ToastUtil.show(this, "商品信息加载失败");
            return;
        }
        
        if (goods.getUserId() == userId) {
            ToastUtil.show(this, "不能与自己聊天");
            return;
        }
        
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("otherUserId", goods.getUserId());
        intent.putExtra("otherUserName", "用户" + goods.getUserId());
        intent.putExtra("goodsId", goodsId);
        intent.putExtra("goodsTitle", goods.getTitle());
        startActivity(intent);
    }

    // 加载商品详情
    private void loadGoodsDetail() {
        new Thread(() -> {
            goods = GoodsDao.getGoodsById(goodsId);
            runOnUiThread(() -> {
                if (goods != null) {
                    tvTitle.setText(goods.getTitle());
                    tvUser.setText("发布者ID：" + goods.getUserId());
                    tvContent.setText(goods.getContent());

                    if (goods.getPublishType() == 0) {
                        tvPrice.setText("￥" + String.format("%.2f", goods.getPrice()));
                        tvType.setText("类型：出售");
                    } else {
                        tvPrice.setText("置换");
                        tvType.setText("置换需求：" + goods.getWantGoods());
                    }

                    // 加载商品图片
                    loadImage(goods.getImgUrl());
                } else {
                    ToastUtil.show(this, "商品不存在");
                    finish();
                }
            });
        }).start();
    }

    /**
     * 加载图片
     */
    private void loadImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    ivGoodsImage.setImageBitmap(bitmap);
                } else {
                    ivGoodsImage.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ivGoodsImage.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            ivGoodsImage.setImageResource(R.mipmap.ic_launcher);
        }
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
                    } else {
                        ToastUtil.show(GoodsDetailActivity.this, "收藏失败");
                    }
                });
            } else {
                result = CollectDao.cancelCollect(userId, goodsId);
                runOnUiThread(() -> {
                    if (result) {
                        ToastUtil.show(GoodsDetailActivity.this, "取消收藏");
                        btnCollect.setText("收藏商品");
                    } else {
                        ToastUtil.show(GoodsDetailActivity.this, "取消失败");
                    }
                });
            }
        }).start();
    }
}