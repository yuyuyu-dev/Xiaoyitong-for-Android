package com.example.schooltrade.ui.goods;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.utils.UserSession;
import com.example.schooltrade.utils.ToastUtil;
import java.util.List;

public class MyPublishActivity extends BaseActivity {
    private RecyclerView recyclerMy;
    private GoodsAdapter adapter;
    private List<Goods> myGoodsList;
    private int userId;

    // ===================== 最新 Activity Result API =====================
    private final ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // 编辑成功 → 刷新列表
                    loadMyGoods();
                }
            }
    );

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_publish;
    }

    @Override
    protected void initView() {
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
            myGoodsList = GoodsDao.getMyGoods(userId);
            runOnUiThread(() -> {
                hideLoading();
                if (myGoodsList.isEmpty()) {
                    ToastUtil.show(this, "你还没有发布任何商品");
                }

                adapter = new GoodsAdapter(MyPublishActivity.this, myGoodsList);
                recyclerMy.setAdapter(adapter);

                adapter.setOnGoodsClickListener(new GoodsAdapter.OnGoodsClickListener() {
                    @Override
                    public void onEdit(int position) {
                        // 最新跳转方式
                        Intent intent = new Intent(MyPublishActivity.this, EditGoodsActivity.class);
                        intent.putExtra("goodsId", myGoodsList.get(position).getGoodsId());
                        editLauncher.launch(intent);
                    }

                    @Override
                    public void onDelete(int position) {
                        new AlertDialog.Builder(MyPublishActivity.this)
                                .setTitle("提示")
                                .setMessage("确定要删除该商品吗？")
                                .setPositiveButton("删除", (dialog, which) -> delGoods(position))
                                .setNegativeButton("取消", null)
                                .show();
                    }
                });
            });
        }).start();
    }

    private void delGoods(int position) {
        showLoading();
        new Thread(() -> {
            boolean res = GoodsDao.deleteGoods(myGoodsList.get(position).getGoodsId());
            runOnUiThread(() -> {
                hideLoading();
                if (res) {
                    ToastUtil.show(this, "删除成功");
                    myGoodsList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    ToastUtil.show(this, "删除失败");
                }
            });
        }).start();
    }
}