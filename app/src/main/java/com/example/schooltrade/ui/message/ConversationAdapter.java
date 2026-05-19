package com.example.schooltrade.ui.message;

import android.content.Context;
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
import com.example.schooltrade.entity.Conversation;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.Holder> {
    private final Context context;
    private final List<Conversation> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ConversationAdapter(Context context, List<Conversation> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Conversation conv = list.get(position);

        holder.tvUsername.setText(conv.getOtherUserName() != null ? conv.getOtherUserName() : "用户" + conv.getOtherUserId());
        holder.tvGoodsTitle.setText(conv.getGoodsTitle() != null ? conv.getGoodsTitle() : "商品");
        holder.tvLastMessage.setText(conv.getLastMessage());
        holder.tvTime.setText(formatTime(conv.getLastMessageTime()));

        if (conv.getUnreadCount() > 0) {
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.tvUnreadCount.setText(String.valueOf(conv.getUnreadCount()));
        } else {
            holder.tvUnreadCount.setVisibility(View.GONE);
        }

        // 加载对方头像
        if (conv.getOtherUserAvatar() != null && !conv.getOtherUserAvatar().isEmpty()) {
            Glide.with(context).load(RetrofitClient.fullUrl(conv.getOtherUserAvatar()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(holder.ivAvatar);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    private String formatTime(String time) {
        if (time == null || time.isEmpty()) return "";
        if (time.length() > 16) {
            return time.substring(5, 16);
        }
        return time;
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername, tvGoodsTitle, tvLastMessage, tvTime, tvUnreadCount;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvGoodsTitle = itemView.findViewById(R.id.tv_goods_title);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);
        }
    }
}
