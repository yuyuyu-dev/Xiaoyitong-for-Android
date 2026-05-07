package com.example.schooltrade.ui.goods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.model.db.CollectDao;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.util.List;

public class MyCollectActivity extends AppCompatActivity {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recycler;
    private GoodsAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        userId = UserSession.getCurrentUser().getUserId();
        initView();
        loadCollectData();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        recycler = findViewById(R.id.recycler_collect);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 下拉刷新
        refreshLayout.setOnRefreshListener(this::loadCollectData);
    }

    // 加载收藏商品
    private void loadCollectData() {
        new Thread(() -> {
            List<Goods> list = CollectDao.getMyCollect(userId);
            runOnUiThread(() -> {
                refreshLayout.setRefreshing(false);
                adapter = new GoodsAdapter(this, list);
                recycler.setAdapter(adapter);
                if (list.isEmpty()) {
                    ToastUtil.show(this, "暂无收藏商品");
                }
            });
        }).start();
    }
}