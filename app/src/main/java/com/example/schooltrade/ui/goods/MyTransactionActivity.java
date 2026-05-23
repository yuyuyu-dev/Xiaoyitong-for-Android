package com.example.schooltrade.ui.goods;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.TransactionAdapter;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Transaction;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

public class MyTransactionActivity extends BaseActivity {
    private TextView tabBuy, tabSell, tvEmpty;
    private View indicatorBuy, indicatorSell;
    private RecyclerView recycler;
    private TransactionAdapter adapter;
    private boolean isBuyMode = true;

    @Override
    protected int getLayoutId() { return R.layout.activity_my_transaction; }

    @Override
    protected void initView() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tabBuy = findViewById(R.id.tab_buy);
        tabSell = findViewById(R.id.tab_sell);
        indicatorBuy = findViewById(R.id.indicator_buy);
        indicatorSell = findViewById(R.id.indicator_sell);
        tvEmpty = findViewById(R.id.tv_empty);
        recycler = findViewById(R.id.recycler_transaction);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        tabBuy.setOnClickListener(v -> switchTab(true));
        tabSell.setOnClickListener(v -> switchTab(false));
    }

    @Override
    protected void initData() {
        loadTransactions();
    }

    private void switchTab(boolean buy) {
        isBuyMode = buy;
        tabBuy.setTextColor(buy ? 0xFF2196F3 : 0xFF757575);
        tabSell.setTextColor(buy ? 0xFF757575 : 0xFF2196F3);
        indicatorBuy.setVisibility(buy ? View.VISIBLE : View.GONE);
        indicatorSell.setVisibility(buy ? View.GONE : View.VISIBLE);
        loadTransactions();
    }

    private void loadTransactions() {
        int userId = UserSession.getCurrentUser().getUserId();
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<List<Transaction>>> response = isBuyMode
                    ? RetrofitClient.getInstance().getApiService().getMyBuy(userId).execute()
                    : RetrofitClient.getInstance().getApiService().getMySell(userId).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    List<Transaction> list = new ArrayList<>();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess() && response.body().getData() != null) {
                        list = response.body().getData();
                    }
                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);
                        adapter = new TransactionAdapter(MyTransactionActivity.this, list, isBuyMode);
                        recycler.setAdapter(adapter);
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(MyTransactionActivity.this, "网络连接失败");
                });
            }
        }).start();
    }
}
