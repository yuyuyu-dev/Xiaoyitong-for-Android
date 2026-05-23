package com.example.schooltrade.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schooltrade.R;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.entity.Goods;

import java.util.List;

public class HomeBannerAdapter extends RecyclerView.Adapter<HomeBannerAdapter.BannerHolder> {

    private final List<Goods> goodsList;
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(int goodsId);
    }

    public HomeBannerAdapter(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_banner, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        int realPos = position % goodsList.size(); // 无限循环
        Goods goods = goodsList.get(realPos);

        holder.tvTitle.setText(goods.getTitle());

        if (goods.getImgUrl() != null && !goods.getImgUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(RetrofitClient.fullUrl(goods.getImgUrl()))
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .into(holder.ivBanner);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBannerClick(goods.getGoodsId());
        });
    }

    @Override
    public int getItemCount() {
        return goodsList.isEmpty() ? 0 : Integer.MAX_VALUE; // 无限循环
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle;

        public BannerHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
        }
    }
}
