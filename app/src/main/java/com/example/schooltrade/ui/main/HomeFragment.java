package com.example.schooltrade.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.adapter.GoodsAdapter;
import com.example.schooltrade.base.BaseFragment;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.ui.publish.PublishActivity;
import com.example.schooltrade.utils.ToastUtil;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private RecyclerView recycler;
    private Button btnPublish;
    private GoodsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recycler = view.findViewById(R.id.recycler);
        btnPublish = view.findViewById(R.id.btn_publish);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));

        btnPublish.setOnClickListener(v -> startActivity(new Intent(mContext, PublishActivity.class)));
        loadGoods();
        return view;
    }

    private void loadGoods() {
        showLoading();
        new Thread(() -> {
            List<Goods> list = GoodsDao.getAllGoods();
            getActivity().runOnUiThread(() -> {
                hideLoading();
                adapter = new GoodsAdapter(mContext, list);
                recycler.setAdapter(adapter);
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGoods();
    }
}