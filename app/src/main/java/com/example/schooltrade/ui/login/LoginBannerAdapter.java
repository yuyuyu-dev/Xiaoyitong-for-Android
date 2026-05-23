package com.example.schooltrade.ui.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltrade.R;

public class LoginBannerAdapter extends RecyclerView.Adapter<LoginBannerAdapter.BannerHolder> {

    private final int[] images = {
        R.drawable.bg_login_banner_1,
        R.drawable.bg_login_banner_2,
        R.drawable.bg_login_banner_3
    };

    private final String[] titles = {
        "闲置好物 · 低价淘好货",
        "安全交易 · 实名认证保障",
        "绿色校园 · 让旧物焕发新生"
    };

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_login_banner, parent, false);
        return new BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        holder.ivBanner.setImageResource(images[position]);
        holder.tvBanner.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // 无限循环
    }

    static class BannerHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvBanner;

        BannerHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner_img);
            tvBanner = itemView.findViewById(R.id.tv_banner_text);
        }
    }
}
