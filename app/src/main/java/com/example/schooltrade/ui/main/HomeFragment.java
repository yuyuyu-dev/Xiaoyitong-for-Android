package com.example.schooltrade.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class HomeFragment extends BaseFragment {

    private static final String[] CATEGORY_NAMES = {
        "全部分类", "教材书籍", "数码产品", "生活用品", "服饰鞋包", "运动器材", "其他"
    };

    private LinearLayout llCategories;
    private TextView tvResultCount;
    private TextView btnSortTime;
    private TextView btnSortPrice;
    private RecyclerView recycler;
    private GoodsAdapter adapter;
    private List<Goods> goodsList = new ArrayList<>();
    private EditText etSearch;
    private ImageView btnClearSearch;

    private int currentCategory = 0;
    private String currentSort = "time";
    private boolean priceAsc = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        llCategories = view.findViewById(R.id.ll_categories);
        tvResultCount = view.findViewById(R.id.tv_result_count);
        btnSortTime = view.findViewById(R.id.btn_sort_time);
        btnSortPrice = view.findViewById(R.id.btn_sort_price);
        recycler = view.findViewById(R.id.recycler);
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);

        recycler.setLayoutManager(new LinearLayoutManager(mContext));

        initSearch();
        initCategories();
        initSort();
        loadGoods();

        return view;
    }

    // ── 分类标签栏 ──
    private void initCategories() {
        for (int i = 0; i < CATEGORY_NAMES.length; i++) {
            TextView chip = new TextView(mContext);
            chip.setText(CATEGORY_NAMES[i]);
            chip.setId(View.generateViewId());
            chip.setTextSize(13);
            chip.setPadding(24, 10, 24, 10);
            chip.setClickable(true);
            chip.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(i == 0 ? 0 : 8, 0, 8, 0);
            chip.setLayoutParams(lp);
            chip.setBackgroundResource(R.drawable.bg_category_chip);

            final int idx = i;
            chip.setOnClickListener(v -> {
                currentCategory = idx;
                updateCategoryChips();
                loadGoods();
            });
            llCategories.addView(chip);
        }
        updateCategoryChips();
    }

    private void updateCategoryChips() {
        for (int i = 0; i < llCategories.getChildCount(); i++) {
            TextView chip = (TextView) llCategories.getChildAt(i);
            boolean selected = (i == currentCategory);
            chip.setSelected(selected);
            chip.setTextColor(selected ? Color.WHITE : Color.parseColor("#666666"));
        }
    }

    // ── 排序 ──
    private void initSort() {
        btnSortTime.setSelected(true);
        btnSortTime.setTextColor(Color.WHITE);

        btnSortTime.setOnClickListener(v -> {
            currentSort = "time";
            btnSortTime.setSelected(true);
            btnSortTime.setTextColor(Color.WHITE);
            btnSortPrice.setSelected(false);
            btnSortPrice.setTextColor(Color.parseColor("#757575"));
            btnSortPrice.setText("价格↑");
            priceAsc = true;
            loadGoods();
        });

        btnSortPrice.setOnClickListener(v -> {
            if (priceAsc) {
                currentSort = "price_asc";
                btnSortPrice.setText("价格↑");
            } else {
                currentSort = "price_desc";
                btnSortPrice.setText("价格↓");
            }
            priceAsc = !priceAsc;
            btnSortPrice.setSelected(true);
            btnSortPrice.setTextColor(Color.WHITE);
            btnSortTime.setSelected(false);
            btnSortTime.setTextColor(Color.parseColor("#757575"));
            loadGoods();
        });
    }

    // ── 搜索 ──
    private void initSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s != null && s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) performSearch(keyword);
            return false;
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            loadGoods();
        });
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) return;
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<List<Goods>>> response = RetrofitClient.getInstance()
                    .getApiService().searchGoods(keyword).execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        goodsList = response.body().getData();
                        if (goodsList == null) goodsList = new ArrayList<>();
                    } else {
                        ToastUtil.show(mContext, "搜索失败");
                    }
                    updateList();
                });
            } catch (IOException e) {
                safeUi(() -> { hideLoading(); ToastUtil.show(mContext, "网络连接失败"); });
            }
        }).start();
    }

    // ── 加载商品 ──
    private void loadGoods() {
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<List<Goods>>> response = RetrofitClient.getInstance()
                    .getApiService().getAllGoods(
                        currentCategory > 0 ? currentCategory : null,
                        currentSort
                    ).execute();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        goodsList = response.body().getData();
                        if (goodsList == null) goodsList = new ArrayList<>();
                    } else {
                        goodsList = new ArrayList<>();
                        ToastUtil.show(mContext, "加载失败");
                    }
                    updateList();
                });
            } catch (IOException e) {
                safeUi(() -> { hideLoading(); ToastUtil.show(mContext, "网络连接失败"); });
            }
        }).start();
    }

    private void updateList() {
        tvResultCount.setText(
            (currentCategory > 0 ? CATEGORY_NAMES[currentCategory] : "全部商品")
            + " · " + goodsList.size() + " 件"
        );
        adapter = new GoodsAdapter(mContext, goodsList);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(mContext, GoodsDetailActivity.class);
            intent.putExtra("goodsId", goodsList.get(position).getGoodsId());
            startActivity(intent);
        });
    }

    private void safeUi(Runnable action) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(action);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGoods();
    }
}
