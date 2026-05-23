  package com.example.schooltrade.ui.goods;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import java.util.List;
import retrofit2.Response;

public class MyCollectActivity extends AppCompatActivity {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recycler;
    private GoodsAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserSession.init(getApplicationContext());
        setContentView(R.layout.activity_my_collect);
        userId = UserSession.getCurrentUser().getUserId();
        initView();
        loadCollectData();
    }

    private void initView() {
        // 返回按钮
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        refreshLayout = findViewById(R.id.refreshLayout);
        recycler = findViewById(R.id.recycler_collect);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 下拉刷新
        refreshLayout.setOnRefreshListener(this::loadCollectData);
    }

    // 加载收藏商品
    private void loadCollectData() {
        new Thread(() -> {
            try {
                Response<Result<List<Goods>>> response = RetrofitClient.getInstance()
                    .getApiService().getMyCollect(userId).execute();
                runOnUiThread(() -> {
                    refreshLayout.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        List<Goods> list = response.body().getData();
                        adapter = new GoodsAdapter(MyCollectActivity.this, list);
                        recycler.setAdapter(adapter);

                        adapter.setOnItemClickListener(position -> {
                            Intent intent = new Intent(MyCollectActivity.this, GoodsDetailActivity.class);
                            intent.putExtra("goodsId", list.get(position).getGoodsId());
                            startActivity(intent);
                        });

                        if (list.isEmpty()) {
                            ToastUtil.show(MyCollectActivity.this, "暂无收藏商品");
                        }
                    } else {
                        ToastUtil.show(MyCollectActivity.this, "加载失败");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    refreshLayout.setRefreshing(false);
                    ToastUtil.show(MyCollectActivity.this, "网络连接失败");
                });
            }
        }).start();
    }
}