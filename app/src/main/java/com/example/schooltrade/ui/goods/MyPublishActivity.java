package com.example.schooltrade.ui.goods;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.utils.UserSession;
import com.example.schooltrade.utils.ToastUtil;
import java.io.IOException;
import java.util.List;
import retrofit2.Response;

public class MyPublishActivity extends BaseActivity {
    private RecyclerView recyclerMy;
    private GoodsAdapter adapter;
    private List<Goods> myGoodsList;
    private int userId;

    // 最新Activity Result API
    private final ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) loadMyGoods();
            }
    );

    @Override
    protected int getLayoutId() { return R.layout.activity_my_publish; }

    @Override
    protected void initView() {
        // 返回按钮
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerMy = findViewById(R.id.recycler_my);
        recyclerMy.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        userId = UserSession.getCurrentUser().getUserId();
        loadMyGoods();
    }

    private void loadMyGoods() {
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<List<Goods>>> response = RetrofitClient.getInstance()
                    .getApiService().getMyGoods(userId).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        myGoodsList = response.body().getData();
                    } else {
                        myGoodsList = new java.util.ArrayList<>();
                    }
                    if (myGoodsList.isEmpty()) ToastUtil.show(this, "暂无发布商品");
                    adapter = new GoodsAdapter(this, myGoodsList, true);
                    recyclerMy.setAdapter(adapter);

                    adapter.setOnGoodsClickListener(new GoodsAdapter.OnGoodsClickListener() {
                        @Override
                        public void onEdit(int position) {
                            Intent intent = new Intent(MyPublishActivity.this, EditGoodsActivity.class);
                            intent.putExtra("goodsId", myGoodsList.get(position).getGoodsId());
                            editLauncher.launch(intent);
                        }

                        @Override
                        public void onDelete(int position) {
                            String title = myGoodsList.get(position).getTitle();
                            new AlertDialog.Builder(MyPublishActivity.this)
                                    .setTitle("确认删除")
                                    .setMessage("确定要删除「" + title + "」吗？\n删除后无法恢复。")
                                    .setPositiveButton("删除", (d, w) -> delGoods(position))
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    });
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(MyPublishActivity.this, "网络连接失败");
                });
            }
        }).start();
    }

    private void delGoods(int position) {
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<String>> response = RetrofitClient.getInstance()
                    .getApiService().deleteGoods(myGoodsList.get(position).getGoodsId())
                    .execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        ToastUtil.show(MyPublishActivity.this, "删除成功");
                        myGoodsList.remove(position);
                        adapter.notifyItemRemoved(position);
                    } else {
                        ToastUtil.show(MyPublishActivity.this, "删除失败");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(MyPublishActivity.this, "网络连接失败");
                });
            }
        }).start();
    }
}