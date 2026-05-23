package com.example.schooltrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.entity.Transaction;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.Holder> {
    private final Context context;
    private final List<Transaction> list;
    private final boolean isBuyMode;

    public TransactionAdapter(Context context, List<Transaction> list, boolean isBuyMode) {
        this.context = context;
        this.list = list;
        this.isBuyMode = isBuyMode;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_transaction, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Transaction tx = list.get(position);
        holder.tvGoodsTitle.setText(tx.getGoodsTitle());
        holder.tvPrice.setText("¥" + String.format("%.2f", tx.getPrice()));
        holder.tvTime.setText(formatTime(tx.getCreateTime()));

        if (tx.getStatus() != null && tx.getStatus() == 1) {
            holder.tvStatus.setText("已完成");
            holder.tvStatus.setTextColor(0xFF4CAF50);
        }

        // 对手方信息
        if (isBuyMode) {
            holder.tvCounterparty.setText("卖家ID: " + tx.getSellerId());
        } else {
            holder.tvCounterparty.setText("买家ID: " + tx.getBuyerId());
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    private String formatTime(String time) {
        if (time == null) return "";
        return time.length() > 16 ? time.substring(0, 16) : time;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView tvGoodsTitle, tvPrice, tvStatus, tvCounterparty, tvTime;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvGoodsTitle = itemView.findViewById(R.id.tv_goods_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCounterparty = itemView.findViewById(R.id.tv_counterparty);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
