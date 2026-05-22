package com.example.schooltrade.ui.goods;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.schooltrade.R;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.api.TransactionRequest;
import com.example.schooltrade.entity.Transaction;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.ui.message.ChatActivity;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Response;

public class GoodsDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvUser, tvPrice, tvType, tvContent;
    private ImageView ivGoodsImage;
    private Button btnCollect, btnContact, btnBuy;
    private int goodsId, userId;
    private Goods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserSession.init(getApplicationContext());
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

        // 确保按钮文字显示
        if (btnCollect != null) {
            btnCollect.setText("收藏商品");
            btnCollect.setVisibility(android.view.View.VISIBLE);
        }
        if (btnContact != null) {
            btnContact.setText("联系卖家");
            btnContact.setVisibility(android.view.View.VISIBLE);
        }
        if (btnBuy != null) {
            btnBuy.setText("购买商品");
            btnBuy.setVisibility(android.view.View.VISIBLE);
        }

        // 收藏按钮
        if (btnCollect != null) {
            btnCollect.setOnClickListener(v -> collectGoods());
        }
        
        // 联系卖家按钮
        if (btnContact != null) {
            btnContact.setOnClickListener(v -> contactSeller());
        }
        
        // 购买商品按钮
        if (btnBuy != null) {
            btnBuy.setOnClickListener(v -> buyGoods());
        }
    }

    /**
     * 购买商品：弹出双选弹窗
     */
    private void buyGoods() {
        if (goods == null) {
            ToastUtil.show(this, "商品信息加载失败");
            return;
        }
        if (goods.getUserId() == userId) {
            ToastUtil.show(this, "不能购买自己的商品");
            return;
        }
        if (goods.getStatus() == 0) {
            ToastUtil.show(this, "该商品已售出");
            return;
        }
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("购买操作")
            .setMessage("请选择购买方式")
            .setPositiveButton("直接购买", (dialog, which) -> directBuy())
            .setNegativeButton("与卖家协商", (dialog, which) -> negotiateWithSeller())
            .show();
    }

    /** 直接购买：备案提示 → 模拟支付 → 成功弹窗 */
    private void directBuy() {
        // 第一步：醒目的备案提示弹窗
        String msg = "根据相关规定，在线支付功能需完成备案审核。\n" +
                     "所以我们小组通过模拟支付来演示功能\n"+
                     "点击「确认支付」后将直接完成交易。";
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("⚠️ 支付功能提示")
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("确认支付（模拟）", (d, w) -> doDirectPay())
            .setNegativeButton("取消", null)
            .show();
    }

    /** 执行模拟支付 */
    private void doDirectPay() {
        new Thread(() -> {
            try {
                Thread.sleep(600); // 模拟支付延迟
                TransactionRequest req = new TransactionRequest(
                    goods.getUserId(), goodsId, goods.getTitle(), goods.getPrice());
                Response<Result<Transaction>> response = RetrofitClient.getInstance()
                    .getApiService().createTransaction(req).execute();
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        String successMsg = "商品： " + goods.getTitle() + "\n" +
                            "金额： ¥" + String.format("%.2f", goods.getPrice()) + "\n\n" +
                            "交易已完成，可在「我的交易」中查看订单详情。";
                        new androidx.appcompat.app.AlertDialog.Builder(GoodsDetailActivity.this)
                            .setTitle("✅ 支付成功")
                            .setMessage(successMsg)
                            .setCancelable(false)
                            .setPositiveButton("好的", (d, w) -> finish())
                            .show();
                    } else {
                        String err = response.body() != null
                            ? response.body().getMessage() : "交易创建失败";
                        ToastUtil.show(GoodsDetailActivity.this, err);
                    }
                });
            } catch (IOException | InterruptedException e) {
                runOnUiThread(() ->
                    ToastUtil.show(GoodsDetailActivity.this, "网络连接失败"));
            }
        }).start();
    }

    /** 与卖家协商：跳转聊天页自动发消息 */
    private void negotiateWithSeller() {
        ToastUtil.show(this, "正在为您联系卖家...");
        openChatWithSeller("你好，我对你上架的商品很感兴趣，可以谈谈吗？");
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
        openChatWithSeller(null);
    }

    /** 查卖家头像后跳转聊天 */
    private void openChatWithSeller(String autoMessage) {
        new Thread(() -> {
            String avatarUrl = null;
            try {
                Response<Result<User>> userResp = RetrofitClient.getInstance()
                    .getApiService().getUserInfo(goods.getUserId()).execute();
                if (userResp.isSuccessful() && userResp.body() != null
                        && userResp.body().isSuccess() && userResp.body().getData() != null) {
                    avatarUrl = userResp.body().getData().getAvatarUrl();
                }
            } catch (IOException ignored) {}

            Intent intent = new Intent(GoodsDetailActivity.this, ChatActivity.class);
            intent.putExtra("otherUserId", goods.getUserId());
            intent.putExtra("otherUserName", "用户" + goods.getUserId());
            intent.putExtra("goodsId", goodsId);
            intent.putExtra("goodsTitle", goods.getTitle());
            if (avatarUrl != null) intent.putExtra("otherUserAvatar", avatarUrl);
            if (autoMessage != null) intent.putExtra("autoMessage", autoMessage);
            startActivity(intent);
        }).start();
    }

    // 加载商品详情
    private void loadGoodsDetail() {
        new Thread(() -> {
            try {
                Response<Result<Goods>> response = RetrofitClient.getInstance()
                    .getApiService().getGoodsById(goodsId).execute();
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        goods = response.body().getData();
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

                        loadImage(goods.getImgUrl());

                        // 已售出处理
                        if (goods.getStatus() == 0) {
                            // 不是卖家本人 → 提示已售出并返回刷新列表
                            if (goods.getUserId() != userId) {
                                ToastUtil.show(GoodsDetailActivity.this, "该商品已售出");
                                finish();
                                return;
                            }
                            // 卖家本人 → 显示已售出标记
                            tvType.setText(tvType.getText() + "（已售出）");
                            if (btnBuy != null) {
                                btnBuy.setText("已售出");
                                btnBuy.setEnabled(false);
                                btnBuy.setBackgroundResource(R.drawable.bg_button_danger);
                            }
                            if (btnCollect != null) btnCollect.setVisibility(android.view.View.GONE);
                        }
                    } else {
                        ToastUtil.show(GoodsDetailActivity.this, "商品不存在");
                        finish();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show(GoodsDetailActivity.this, "网络连接失败");
                    finish();
                });
            }
        }).start();
    }

    private void loadImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this)
                .load(RetrofitClient.fullUrl(imagePath))
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .into(ivGoodsImage);
        } else {
            ivGoodsImage.setImageResource(R.drawable.bg_image_placeholder);
        }
    }

    // 检查收藏状态
    private void checkCollectStatus() {
        if (btnCollect == null) {
            return;
        }

        new Thread(() -> {
            try {
                Response<Result<Boolean>> response = RetrofitClient.getInstance()
                    .getApiService().isCollect(userId, goodsId).execute();
                runOnUiThread(() -> {
                    if (btnCollect != null) {
                        boolean isCollect = response.isSuccessful() && response.body() != null
                            && response.body().isSuccess() && response.body().getData() != null
                            && response.body().getData();
                        btnCollect.setText(isCollect ? "取消收藏" : "收藏商品");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    if (btnCollect != null) {
                        btnCollect.setText("收藏商品");
                    }
                });
            }
        }).start();
    }

    // 收藏/取消收藏
    private void collectGoods() {
        new Thread(() -> {
            try {
                if (btnCollect.getText().toString().equals("收藏商品")) {
                    doAddCollect();
                } else {
                    doCancelCollect();
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show(GoodsDetailActivity.this, "网络连接失败");
                });
            }
        }).start();
    }

    private void doAddCollect() throws IOException {
        Map<String, Integer> body = new HashMap<>();
        body.put("goodsId", goodsId);
        Response<Result<String>> response = RetrofitClient.getInstance()
            .getApiService().addCollect(body).execute();
        runOnUiThread(() -> {
            if (response.isSuccessful() && response.body() != null
                    && response.body().isSuccess()) {
                ToastUtil.show(GoodsDetailActivity.this, "收藏成功");
                btnCollect.setText("取消收藏");
            } else {
                String msg = response.body() != null
                    ? response.body().getMessage() : "收藏失败";
                ToastUtil.show(GoodsDetailActivity.this, msg);
            }
        });
    }

    private void doCancelCollect() throws IOException {
        Map<String, Integer> body = new HashMap<>();
        body.put("goodsId", goodsId);
        Response<Result<String>> response = RetrofitClient.getInstance()
            .getApiService().cancelCollect(body).execute();
        runOnUiThread(() -> {
            if (response.isSuccessful() && response.body() != null
                    && response.body().isSuccess()) {
                ToastUtil.show(GoodsDetailActivity.this, "取消收藏");
                btnCollect.setText("收藏商品");
            } else {
                ToastUtil.show(GoodsDetailActivity.this, "取消失败");
            }
        });
    }
}