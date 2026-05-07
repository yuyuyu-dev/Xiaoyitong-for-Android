package com.example.schooltrade.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.base.BaseFragment;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.ui.goods.GoodsDetailActivity;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private RecyclerView recycler;
    private EditText etSearch;
    private GoodsAdapter adapter;
    private List<Goods> goodsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recycler = view.findViewById(R.id.recycler);
        etSearch = view.findViewById(R.id.et_search);
        Button btnSearch = view.findViewById(R.id.btn_search);

        recycler.setLayoutManager(new LinearLayoutManager(mContext));

        // 搜索商品
        btnSearch.setOnClickListener(v -> searchGoods());

        loadAllGoods();
        return view;
    }

    // 加载所有商品（普通模式，无按钮）
    private void loadAllGoods() {
        new Thread(() -> {
            goodsList = GoodsDao.getAllGoods();
            getActivity().runOnUiThread(() -> {
                adapter = new GoodsAdapter(mContext, goodsList);
                recycler.setAdapter(adapter);
                adapter.setOnItemClickListener(position -> {
                    Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                    intent.putExtra("goodsId", goodsList.get(position).getGoodsId());
                    startActivity(intent);
                });
            });
        }).start();
    }

    // 搜索商品
    private void searchGoods() {
        String keyword = etSearch.getText().toString().trim();
        new Thread(() -> {
            goodsList = GoodsDao.searchGoods(keyword);
            getActivity().runOnUiThread(() -> {
                adapter = new GoodsAdapter(mContext, goodsList);
                recycler.setAdapter(adapter);
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllGoods();
    }
}