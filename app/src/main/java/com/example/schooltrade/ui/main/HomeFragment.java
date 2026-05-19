package com.example.schooltrade.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseFragment;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.ui.goods.GoodsDetailActivity;
import com.example.schooltrade.utils.ToastUtil;
import java.io.IOException;
import java.util.List;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {
    private RecyclerView recycler;
    private GoodsAdapter adapter;
    private List<Goods> goodsList;
    private EditText etSearch;
    private ImageView btnClearSearch;
    private String currentKeyword = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // 1. 初始化 Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        // 由于是在 Fragment 中，Toolbar 已经通过布局设置了标题

        // 2. 初始化搜索框
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        initSearch();

        // 3. 初始化列表
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));

        loadAllGoods();
        return view;
    }

    // 加载所有商品
    private void loadAllGoods() {
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<List<Goods>>> response = RetrofitClient.getInstance()
                    .getApiService().getAllGoods().execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        goodsList = response.body().getData();
                    } else {
                        goodsList = new java.util.ArrayList<>();
                        ToastUtil.show(mContext, "加载商品失败");
                    }
                    adapter = new GoodsAdapter(mContext, goodsList);
                    recycler.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> {
                        Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                        intent.putExtra("goodsId", goodsList.get(position).getGoodsId());
                        startActivity(intent);
                    });
                });
            } catch (IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(mContext, "网络连接失败");
                });
            }
        }).start();
    }

    // 初始化搜索功能
    private void initSearch() {
        // 监听输入框变化
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 显示/隐藏清空按钮
                if (s != null && s.length() > 0) {
                    btnClearSearch.setVisibility(View.VISIBLE);
                } else {
                    btnClearSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 实时搜索（可选，如果需要实时搜索可以取消注释）
                // performSearch(s.toString());
            }
        });

        // 监听搜索按钮（键盘上的搜索键）
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                performSearch(keyword);
            }
            return false;
        });

        // 清空按钮点击事件
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            currentKeyword = "";
            loadAllGoods();
            ToastUtil.show(mContext, "已清空搜索");
        });
    }

    // 执行搜索
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            ToastUtil.show(mContext, "请输入搜索关键词");
            return;
        }

        currentKeyword = keyword.trim();
        showLoading();

        new Thread(() -> {
            try {
                Response<Result<List<Goods>>> response = RetrofitClient.getInstance()
                    .getApiService().searchGoods(currentKeyword).execute();
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    hideLoading();

                    List<Goods> results;
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        results = response.body().getData();
                        if (results == null || results.isEmpty()) {
                            ToastUtil.show(mContext, "未找到相关商品");
                            results = new java.util.ArrayList<>();
                        } else {
                            ToastUtil.show(mContext, "找到 " + results.size() + " 个相关商品");
                        }
                    } else {
                        ToastUtil.show(mContext, "搜索失败");
                        results = new java.util.ArrayList<>();
                    }

                    final List<Goods> finalResults = results;
                    adapter = new GoodsAdapter(mContext, finalResults);
                    recycler.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> {
                        Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                        intent.putExtra("goodsId", finalResults.get(position).getGoodsId());
                        startActivity(intent);
                    });
                });
            } catch (IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(mContext, "网络连接失败");
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllGoods();
    }
}
